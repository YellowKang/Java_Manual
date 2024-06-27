基础环境

​		3台服务器，Kubernetes版本：1.22.0

​		11为master，12 ，13为node节点

| 主机名  |       IP       |             系统              |
| :-----: | :------------: | :---------------------------: |
| master1 | 192.168.100.11 | CentOS Linux release 8.3.2011 |
|  node1  | 192.168.100.12 | CentOS Linux release 8.3.2011 |
|  node2  | 192.168.100.13 | CentOS Linux release 8.3.2011 |

# 前置准备

​		修改主机名

​		服务器为华为云耀，三台主机名分别修改

```
hostnamectl set-hostname master1
hostnamectl set-hostname node1
hostnamectl set-hostname node2
```

​		关闭防火墙

```sh
systemctl stop firewalld && systemctl disable firewalld
```

​		关闭selinux

```sh
setenforce 0 && sed -i "s/^SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
```

​		关闭swap

```sh
swapoff -a && sed -i 's/.*swap.*/#&/' /etc/fstab
```

​		修改hosts文件

```sh
echo "192.168.100.11 master1
192.168.100.12 node1
192.168.100.13 node2
199.232.4.133 raw.githubusercontent.com" >> /etc/hosts
```

​		配置免密

```sh
# 生成秘钥，每台服务器都要执行，一直回车即可
ssh-keygen -t rsa
ssh-keygen -t rsa -N '' -f id_rsa -q

# 回到master
# 查看自己的公钥并且复制到/root/.ssh/authorized_keys
cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys

# 复制node1和2的公钥到自己服务器，输入密码复制过来
mkdir ~/ssh-pub/
scp node1:/root/.ssh/id_rsa.pub  ~/ssh-pub/node1.pub
scp node2:/root/.ssh/id_rsa.pub  ~/ssh-pub/node2.pub
# 将公钥添加到免密key中
cat ~/ssh-pub/node1.pub >> /root/.ssh/authorized_keys && cat ~/ssh-pub/node2.pub >> /root/.ssh/authorized_keys
# 查看免密
cat /root/.ssh/authorized_keys

# 将免密公钥文件复制到其他服务器
scp /root/.ssh/authorized_keys node1:/root/.ssh/authorized_keys
scp /root/.ssh/authorized_keys node2:/root/.ssh/authorized_keys

# 测试ssh是否免密
ssh node1
```

​		将桥接的IPv4流量传递到iptables的链,以及内核优化(所有节点)

```

```



```sh
modprobe br_netfilter
modprobe ip_conntrack
cat > /etc/sysctl.d/k8s.conf << EOF
br_netfilter
net.bridge.bridge-nf-call-iptables=1
net.bridge.bridge-nf-call-ip6tables=1
net.ipv4.ip_forward=1
net.ipv4.tcp_tw_recycle=0
vm.swappiness=0
vm.overcommit_memory=1
vm.panic_on_oom=0
net.ipv4.tcp_timestamps=0
fs.inotify.max_user_watches=89100
fs.file-max=52706963
fs.nr_open=52706963
net.ipv6.conf.all.disable_ipv6=1
net.netfilter.nf_conntrack_max=2310720
EOF
sysctl -p /etc/sysctl.d/k8s.conf
```

​		设置Docker配置文件（所有节点）

```sh
# 设置Docker加速
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://ldlov75k.mirror.aliyuncs.com"],
  "graph":"/data/docker",
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m",
    "max-file": "3"
  },
  "exec-opts": ["native.cgroupdriver=systemd"],
  "storage-driver": "overlay2",
  "insecure-registries" : ["hub.bigkang.k8s"]
}
EOF


# 注释如下
  "registry-mirrors": 镜像仓库加速
  "graph": docker数据存储路径
  "log-driver": 日志驱动
  "log-opts": {
    "max-size": 日志文件大小
    "max-file": 日志文件最大数量
  },
  "exec-opts": 执行配置
  "storage-driver": 存储驱动
  "storage-opts": 存储驱动设置
  "insecure-registries" : docker仓库设置（镜像仓库为共有可以pull，docker仓库可以自己push上传，这里域名一会搭建Harbor）
```

​		安装启动Docker(所有节点)

```sh
# wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo
# 替换Yum源
cd /etc/yum.repos.d/
mv CentOS-Linux-BaseOS.repo CentOS-Linux-BaseOS.repo.bak
wget -O CentOS-Linux-BaseOS.repo http://mirrors.aliyun.com/repo/Centos-8.repo

# 安装Docker指定版本
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo 

sudo yum install docker-ce-20.10.6  -y
docker --version
systemctl daemon-reload && systemctl start docker && systemctl restart docker  && systemctl enable docker
```

​		设置K8s阿里云镜像加速（所有节点）

```sh
cat > /etc/yum.repos.d/kubernetes.repo << EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
```

​		安装K8s命令补全

```sh
yum -y install bash-completion
source /usr/share/bash-completion/bash_completion
source <(kubectl completion bash)
echo "source <(kubectl completion bash)" >> ~/.bashrc
```

# 开始安装

## K8s安装

### kubeadm安装K8s

​		安装kubeadm，kubelet和kubectl，指定版本（所有节点）

```sh
# CentOs8 k8s yum加速
sudo curl -o /etc/yum.repos.d/CentOS-Base.repo curl -o /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-vault-8.5.2111.repo

export kubeletVersion="1.22.0"
yum install -y kubelet-$kubeletVersion kubeadm-$kubeletVersion kubectl-$kubeletVersion
```

​		设置开机启动

```sh
systemctl enable kubelet
```

​		然后初始化master，这里选择11作为master，只在11上执行

```sh
# 修改IP以及 K8s版本
cat > /var/lib/kubelet/config.yaml << EOF
apiVersion: kubeadm.k8s.io/v1beta2
kind: InitConfiguration
localAPIEndpoint:
  advertiseAddress: 192.168.100.11
  bindPort: 6443
nodeRegistration:
  taints:
  - effect: PreferNoSchedule
    key: node-role.kubernetes.io/master
---
apiVersion: kubeadm.k8s.io/v1beta2
imageRepository: registry.aliyuncs.com/google_containers
kind: ClusterConfiguration
kubernetesVersion: v1.22.0
networking:
  podSubnet: 10.244.0.0/16
EOF

# 写入环境变量
echo "export KUBECONFIG=/etc/kubernetes/admin.conf" >> /etc/profile
source  /etc/profile

# K8s Master 初始化
kubeadm init --config /var/lib/kubelet/config.yaml --ignore-preflight-errors=Swap 
```

​		然后会看到一堆日志，最后会看到如下日志

```sh
You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 192.168.100.11:6443 --token zlnr9w.dqfek2soaf2rdvuk \
    --discovery-token-ca-cert-hash sha256:b3fa2aad9cc73989117bb2215647f7c65b91540a21a380b6e5ab1fb4963f318b
```

​		HOME下创建配置文件,Master上执行

```sh
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

​		复制如下到Node1以及Node2执行（以实际为准）

```sh
kubeadm join 192.168.1.12:6443 --token zlnr9w.dqfek2soaf2rdvuk \
    --discovery-token-ca-cert-hash sha256:b3fa2aad9cc73989117bb2215647f7c65b91540a21a380b6e5ab1fb4963f318b
```

​		完成之后会出现如下日志

```sh
This node has joined the cluster:
* Certificate signing request was sent to apiserver and a response was received.
* The Kubelet was informed of the new secure connection details.

Run 'kubectl get nodes' on the control-plane to see this node join the cluster.
```

​		去Master执行命令即可看到Node节点

```sh
kubectl get nodes
```

### rancher2安装k8s

​		一键运行

```sh
docker run -d \
--name rancher2-server \
--restart=unless-stopped \
--privileged=true \
-p 80:80 \
-p 443:443 \
-v /data/rancher2/rancher:/var/lib/rancher \
-v /data/rancher2/log/auditlog:/var/log/auditlog \
-e CATTLE_SYSTEM_CATALOG=bundled \
-e AUDIT_LEVEL=3 \
rancher/rancher
```

​		初始化用户名密码，然后新增集群添加节点即可

```
docker stop rancher2-server
docker rm rancher2-server
```





```
kubectl create secret tls tomcat-ingress-secret --cert=root.crt --key=root.key
```





```properties
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress-tomcat
  namespace: default
  annotations: 
    kubernetes.io/ingress.class: "nginx"
spec:
  tls: 
  - hosts:
    - tomcat.bigkang.k8s
    secretName: tomcat-ingress-secret
  rules: 
  - host: tomcat.bigkang.k8s
    http: 
      paths: 
      - path: 
        backend: 
          serviceName: tomcat
          servicePort: 8080
```

### 二进制安装k8s（未完善）

#### 生成证书

​		更新yum源

```sh
yum -y install wget && wget -O /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-7.repo && yum -y install epel-release 
```

​		下载cfssl证书管理工具，生成证书使用

```sh
##获取证书管理工具
wget https://pkg.cfssl.org/R1.2/cfssl_linux-amd64
wget https://pkg.cfssl.org/R1.2/cfssljson_linux-amd64
wget https://pkg.cfssl.org/R1.2/cfssl-certinfo_linux-amd64
##添加看执行权限并放进可执行目录
chmod +x cfssl_linux-amd64 cfssljson_linux-amd64 cfssl-certinfo_linux-amd64
mv cfssl_linux-amd64 /usr/local/bin/cfssl
mv cfssljson_linux-amd64 /usr/local/bin/cfssljson
mv cfssl-certinfo_linux-amd64 /usr/bin/cfssl-certinfo
```

​		创建存放证书的目录

```sh
mkdir -p ~/tls/{etcd,k8s} 
```

##### 生成etcd证书

```sh
cd ~/tls/etcd
```

​		自签ca，csr配置文件

```sh
cat > ca-config.json << EOF
{
  "signing": {
    "default": {
      "expiry": "87600h"
    },
    "profiles": {
      "www": {
         "expiry": "87600h",
         "usages": [
            "signing",
            "key encipherment",
            "server auth",
            "client auth"
        ]
      }
    }
  }
}
EOF

cat > ca-csr.json << EOF
{
    "CN": "etcd CA",
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "Beijing",
            "ST": "Beijing"
        }
    ]
}
EOF
```

​		生成ca证书

```sh
cfssl gencert -initca ca-csr.json | cfssljson -bare ca -
```

​		新增server https证书

​		此处注意hosts尽量多写几个备用服务器防止通信问题

```properties
cat > server-csr.json << EOF
{
    "CN": "etcd",
    "hosts": [
        "127.0.0.1",
        "192.168.1.12",
        "192.168.1.28",
        "192.168.1.115"
    ],
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "BeiJing",
            "ST": "BeiJing"
        }
    ]
}
EOF
```

​		签发server证书

```sh
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=www server-csr.json | cfssljson -bare server
```

##### 生成k8s证书

```sh
cd ~/tls/k8s
```

​		创建ca,csr配置

```properties
cat > ca-config.json << EOF
{
  "signing": {
    "default": {
      "expiry": "87600h"
    },
    "profiles": {
      "kubernetes": {
         "expiry": "87600h",
         "usages": [
            "signing",
            "key encipherment",
            "server auth",
            "client auth"
        ]
      }
    }
  }
}
EOF
cat > ca-csr.json << EOF
{
    "CN": "kubernetes",
    "key": {
        "algo": "rsa",
        "size": 2048
    },
    "names": [
        {
            "C": "CN",
            "L": "Beijing",
            "ST": "Beijing",
            "O": "k8s",
            "OU": "System"
        }
    ]
}
EOF
```

​		生成Ca证书

```sh
cfssl gencert -initca ca-csr.json | cfssljson -bare ca -
```

​		使用Ca证书自签HTTPS证书，多放入几个IP方便扩容容灾等预留IP

```properties
cat > server-csr.json << EOF
{
    "CN":"kubernetes",
    "hosts":[
    		"10.0.0.1",
    		"127.0.0.1",
        "192.168.1.1",
        "192.168.1.12",
        "192.168.1.28",
        "192.168.1.115",
        "192.168.1.66",
        "192.168.1.67",
        "kubernetes",
        "kubernetes.default",
        "kubernetes.default.svc",
        "kubernetes.default.svc.cluster",
        "kubernetes.default.svc.cluster.local"
    ],
    "key":{
        "algo":"rsa",
        "size":2048
    },
    "names":[
        {
            "C":"CN",
            "L":"BeiJing",
            "ST":"BeiJing",
            "O":"k8s",
            "OU":"System"
        }
    ]
}
EOF
```

​		生成证书

```sh
cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes server-csr.json | cfssljson -bare server
```



#### 部署ETCD集群

​		创建挂载目录

```sh
# 每台都执行
cd 
mkdir -p /data/etcd/{bin,cfg,ssl,data} 
```

​		下载ETCD

​		此处采用加速地址

```sh
# 每台执行，或者单台执行后CP
wget https://github.91chifun.workers.dev//https://github.com/etcd-io/etcd/releases/download/v3.4.9/etcd-v3.4.9-linux-amd64.tar.gz 
tar xf etcd-v3.4.9-linux-amd64.tar.gz
mv etcd-v3.4.9-linux-amd64/etcd* /data/etcd/bin/
```

​		

| 主机名  |      IP       |
| :-----: | :-----------: |
| yunyao1 | 192.168.1.12  |
| yunyao2 | 192.168.1.28  |
| yunyao3 | 192.168.1.115 |

​		根据当前3台主机搭建ETCD集群，分别为

- ​				etcd-yunyao1
- ​				etcd-yunyao2
- ​				etcd-yunyao3

​		配置ETCD配置文件

```sh
# 每台单独执行
# yunyao1节点
cat > /data/etcd/cfg/etcd.conf << EOF
#[Member]
ETCD_NAME="etcd-1"
ETCD_DATA_DIR="/data/etcd/data/default.etcd"
ETCD_LISTEN_PEER_URLS="https://192.168.1.12:2380"
ETCD_LISTEN_CLIENT_URLS="https://192.168.1.12:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://192.168.1.12:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://192.168.1.12:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://192.168.1.12:2380,etcd-2=https://192.168.1.28:2380,etcd-3=https://192.168.1.115:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF

# yunyao2节点
cat > /data/etcd/cfg/etcd.conf << EOF
#[Member]
ETCD_NAME="etcd-2"
ETCD_DATA_DIR="/data/etcd/data/default.etcd"
ETCD_LISTEN_PEER_URLS="https://192.168.1.28:2380"
ETCD_LISTEN_CLIENT_URLS="https://192.168.1.28:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://192.168.1.28:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://192.168.1.28:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://192.168.1.12:2380,etcd-2=https://192.168.1.28:2380,etcd-3=https://192.168.1.115:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF

# yunyao3节点
cat > /data/etcd/cfg/etcd.conf << EOF
#[Member]
ETCD_NAME="etcd-3"
ETCD_DATA_DIR="/data/etcd/data/default.etcd"
ETCD_LISTEN_PEER_URLS="https://192.168.1.115:2380"
ETCD_LISTEN_CLIENT_URLS="https://192.168.1.115:2379"
#[Clustering]
ETCD_INITIAL_ADVERTISE_PEER_URLS="https://192.168.1.115:2380"
ETCD_ADVERTISE_CLIENT_URLS="https://192.168.1.115:2379"
ETCD_INITIAL_CLUSTER="etcd-1=https://192.168.1.12:2380,etcd-2=https://192.168.1.28:2380,etcd-3=https://192.168.1.115:2380"
ETCD_INITIAL_CLUSTER_TOKEN="etcd-cluster"
ETCD_INITIAL_CLUSTER_STATE="new"
EOF
```

​			配置文件信息如下：

```properties
ETCD_NAME																	etcd节点名称，每个节点不一样
ETCD_DATA_DIR															etcd数据目录（存储目录）
ETCD_LISTEN_PEER_URLS											etcd监听地址
ETCD_LISTEN_CLIENT_URLS 									etcd客户端地址
ETCD_INITIAL_ADVERTISE_PEER_URLS					etcd通信地址（与监听一致即可）
ETCD_ADVERTISE_CLIENT_URLS								etcd客户端通信地址（与客户端地址一致即可）
ETCD_INITIAL_CLUSTER											etcd集群连接地址（节点名=地址:端口号,逗号分割）
ETCD_INITIAL_CLUSTER_TOKEN								etcd集群token
ETCD_INITIAL_CLUSTER_STATE								etcd 集群状态
```

​		然后给每一台服务器ETCD编写启动管理文件

```sh
cat > /usr/lib/systemd/system/etcd.service << EOF
[Unit]
Description=Etcd Server
After=network.target
After=network-online.target
Wants=network-online.target
[Service]
Type=notify
EnvironmentFile=/data/etcd/cfg/etcd.conf
ExecStart=/data/etcd/bin/etcd \
--cert-file=/data/etcd/ssl/server.pem \
--key-file=/data/etcd/ssl/server-key.pem \
--peer-cert-file=/data/etcd/ssl/server.pem \
--peer-key-file=/data/etcd/ssl/server-key.pem \
--trusted-ca-file=/data/etcd/ssl/ca.pem \
--peer-trusted-ca-file=/data/etcd/ssl/ca.pem \
--logger=zap
Restart=on-failure
LimitNOFILE=65536
[Install]
WantedBy=multi-user.target
EOF
```

​		复制证书

```sh
cp -i ~/tls/etcd/*pem /data/etcd/ssl
# 将证书复制到其他节点
scp ~/tls/etcd/*pem yunyao2:/data/etcd/ssl
scp ~/tls/etcd/*pem yunyao3:/data/etcd/ssl
```

​		启动ETCD集群

```sh
systemctl daemon-reload && systemctl enable etcd && systemctl start etcd
```

​		验证etcd是否部署成功

```sh
ETCDCTL_API=3 /data/etcd/bin/etcdctl --cacert=/data/etcd/ssl/ca.pem --cert=/data/etcd/ssl/server.pem --key=/data/etcd/ssl/server-key.pem --endpoints="https://192.168.1.12:2379,https://192.168.1.28:2379,https://192.168.1.115:2379" endpoint health
```



#### 安装K8s

##### 		下载k8s

```sh
# 进入当前用户目录
cd ~

# 下载k8s包
wget  https://dl.k8s.io/v1.18.3/kubernetes-server-linux-amd64.tar.gz

# 创建数据目录
mkdir -p /data/kubernetes/{bin,cfg,ssl,logs} 

# 解压
tar zxvf kubernetes-server-linux-amd64.tar.gz

# 进入目录
cd kubernetes/server/bin

# 复制命令
cp kube-apiserver kube-scheduler kube-controller-manager /data/kubernetes/bin

# 复制kubectl
cp kubectl /usr/bin/

# 复制证书
cp ~/tls/k8s/* /data/kubernetes/ssl


# 简洁版一键执行
cd ~
wget  https://dl.k8s.io/v1.18.3/kubernetes-server-linux-amd64.tar.gz
mkdir -p /data/kubernetes/{bin,cfg,ssl,logs} 
tar zxvf kubernetes-server-linux-amd64.tar.gz
cd kubernetes/server/bin
cp kube-apiserver kube-scheduler kube-controller-manager /data/kubernetes/bin
cp kubectl /usr/bin/
cp ~/tls/k8s/* /data/kubernetes/ssl
```

##### 部署kube-apiserver

​		kube-apiserver部署在主控节点上，我们采用一主两节点方式

​		Master为192.168.1.12		

```sh
# 创建kube-apiserver配置文件
cat > /data/kubernetes/cfg/kube-apiserver.conf << EOF
KUBE_APISERVER_OPTS="--logtostderr=false \\
--v=4 \\
--log-dir=/data/kubernetes/logs \\
--etcd-servers=https://192.168.1.12:2379,https://192.168.1.28:2379,https://192.168.1.115:2379 \\
--bind-address=192.168.1.12 \\
--secure-port=6443 \\
--advertise-address=192.168.1.12 \\
--allow-privileged=true \\
--service-cluster-ip-range=10.0.0.0/24 \\
--enable-admission-plugins=NamespaceLifecycle,LimitRanger,ServiceAccount,ResourceQuota,NodeRestriction \\
--authorization-mode=RBAC,Node \\
--enable-bootstrap-token-auth=true \\
--token-auth-file=/data/kubernetes/cfg/token.csv \\
--service-node-port-range=30000-32767 \\
--kubelet-client-certificate=/data/kubernetes/ssl/server.pem \\
--kubelet-client-key=/data/kubernetes/ssl/server-key.pem \\
--tls-cert-file=/data/kubernetes/ssl/server.pem  \\
--tls-private-key-file=/data/kubernetes/ssl/server-key.pem \\
--client-ca-file=/data/kubernetes/ssl/ca.pem \\
--service-account-key-file=/data/kubernetes/ssl/ca-key.pem \\
--etcd-cafile=/data/etcd/ssl/ca.pem \\
--etcd-certfile=/data/etcd/ssl/server.pem \\
--etcd-keyfile=/data/etcd/ssl/server-key.pem \\
--audit-log-maxage=30 \\
--audit-log-maxbackup=3 \\
--audit-log-maxsize=100 \\
--audit-log-path=/data/kubernetes/logs/k8s-audit.log"
EOF

#参数详解:
#    –logtostderr：启用日志
#    —v：日志等级
#    –log-dir：日志目录
#    –etcd-servers：etcd集群地址
#    –bind-address：监听地址
#    –secure-port：https安全端口
#    –advertise-address：集群通告地址
#    –allow-privileged：启用授权
#    –service-cluster-ip-range：Service虚拟IP地址段
#    –enable-admission-plugins：准入控制模块
#    –authorization-mode：认证授权，启用RBAC授权和节点自管理
#    –enable-bootstrap-token-auth：启用TLS bootstrap机制
#    –token-auth-file：bootstrap token文件
#    –service-node-port-range：Service nodeport类型默认分配端口范围
#    –kubelet-client-xxx：apiserver访问kubelet客户端证书
#    –tls-xxx-file：apiserver https证书
#    –etcd-xxxfile：连接Etcd集群证书
#    –audit-log-xxx：审计日志


# 创建token
cat > /data/kubernetes/cfg/token.csv << EOF
b1dc586d69159ff4e3ef7efa9db60e48,10001,"system:node-bootstrapper"
EOF
# 自行生成token
# head -c 16 /dev/urandom | od -An -t x | tr -d ' '
# 格式：token，用户名，UID，用户组


# 创建api-server服务
cat > /usr/lib/systemd/system/kube-apiserver.service << EOF
[Unit]
Description=Kubernetes API Server
Documentation=https://github.com/kubernetes/kubernetes
[Service]
EnvironmentFile=/data/kubernetes/cfg/kube-apiserver.conf
ExecStart=/data/kubernetes/bin/kube-apiserver \$KUBE_APISERVER_OPTS
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemctl，启动服务，开机自启
systemctl daemon-reload && systemctl start kube-apiserver && systemctl enable kube-apiserver

# 授权kubelet-bootstrap用户允许请求证书
kubectl create clusterrolebinding kubelet-bootstrap \
--clusterrole=system:node-bootstrapper \
--user=kubelet-bootstrap
```

##### 部署kube-controller-manager

​		kube-controller-manager部署在主控节点上，我们采用一主两节点方式

​		Master为192.168.1.12		

```sh
# 创建kube-controller-manager配置文件
cat > /data/kubernetes/cfg/kube-controller-manager.conf << EOF
KUBE_CONTROLLER_MANAGER_OPTS="--logtostderr=false \\
--v=4 \\
--log-dir=/data/kubernetes/logs \\
--leader-elect=true \\
--master=127.0.0.1:8080 \\
--bind-address=127.0.0.1 \\
--allocate-node-cidrs=true \\
--cluster-cidr=10.244.0.0/16 \\
--service-cluster-ip-range=10.0.0.0/24 \\
--cluster-signing-cert-file=/data/kubernetes/ssl/ca.pem \\
--cluster-signing-key-file=/data/kubernetes/ssl/ca-key.pem  \\
--root-ca-file=/data/kubernetes/ssl/ca.pem \\
--service-account-private-key-file=/data/kubernetes/ssl/ca-key.pem \\
--experimental-cluster-signing-duration=87600h0m0s"
EOF

# –master：通过本地非安全本地端口8080连接apiserver。
# –leader-elect：当该组件启动多个时，自动选举（HA）
# –cluster-signing-cert-file/–cluster-signing-key-file：自动为kubelet颁发证书的CA，与apiserver保持一致

# 创建controller-manager服务
cat > /usr/lib/systemd/system/kube-controller-manager.service << EOF
[Unit]
Description=Kubernetes Controller Manager
Documentation=https://github.com/kubernetes/kubernetes
[Service]
EnvironmentFile=/data/kubernetes/cfg/kube-controller-manager.conf
ExecStart=/data/kubernetes/bin/kube-controller-manager \$KUBE_CONTROLLER_MANAGER_OPTS
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemctl，启动服务，开机自启
systemctl daemon-reload && systemctl start kube-controller-manager && systemctl enable kube-controller-manager
```

##### 部署kube-scheduler

​		kube-scheduler部署在主控节点上，我们采用一主两节点方式

​		Master为192.168.1.12		

```sh
# 创建kube-scheduler配置文件
cat > /data/kubernetes/cfg/kube-scheduler.conf << EOF
KUBE_SCHEDULER_OPTS="--logtostderr=false \
--v=2 \
--log-dir=/data/kubernetes/logs \
--leader-elect \
--master=127.0.0.1:8080 \
--bind-address=127.0.0.1"
EOF

# –master：通过本地非安全本地端口8080连接apiserver。
# –leader-elect：当该组件启动多个时，自动选举（HA）

# 创建kube-scheduler服务
cat > /usr/lib/systemd/system/kube-scheduler.service << EOF
[Unit]
Description=Kubernetes Scheduler
Documentation=https://github.com/kubernetes/kubernetes
[Service]
EnvironmentFile=/data/kubernetes/cfg/kube-scheduler.conf
ExecStart=/data/kubernetes/bin/kube-scheduler \$KUBE_SCHEDULER_OPTS
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemctl，启动服务，开机自启
systemctl daemon-reload && systemctl start kube-scheduler && systemctl enable kube-scheduler
```

​		获取状态

```sh
kubectl get cs
```

​		返回如下状态即可

```sh
controller-manager   Healthy   ok                  
scheduler            Healthy   ok                  
etcd-2               Healthy   {"health":"true"}   
etcd-0               Healthy   {"health":"true"}   
etcd-1               Healthy   {"health":"true"}   
```

##### 部署kubelet（未完成）

​		kubelet部署在工作节点上，我们采用一主两节点方式

​		Node为192.168.1.28 以及 192.168.1.115

​		创建目录

```sh
# 分别在两个节点上创建目录
mkdir -p /data/kubernetes/{bin,cfg,ssl,logs} 
```

```sh
# yunyao1
# 从Master复制可执行文件	
cd ~/kubernetes/server/bin
scp kubelet kube-proxy yunyao2:/data/kubernetes/bin
scp kubelet kube-proxy yunyao3:/data/kubernetes/bin  

# 从Master复制证书
scp /data/kubernetes/ssl/* yunyao2:/data/kubernetes/ssl
scp /data/kubernetes/ssl/* yunyao3:/data/kubernetes/ssl  
```

​		创建配置文件(Master节点中执行)

```sh
# Master创建配置文件
cat > /data/kubernetes/cfg/kubelet.conf << EOF
KUBELET_OPTS="--logtostderr=false \\
--v=2 \\
--log-dir=/data/kubernetes/logs \\
--hostname-override=${HOSTNAME} \\
--network-plugin=cni \\
--kubeconfig=/data/kubernetes/cfg/kubelet.kubeconfig \\
--experimental-bootstrap-kubeconfig=/data/kubernetes/cfg/bootstrap.kubeconfig \\
--config=/data/kubernetes/cfg/kubelet-config.yml \\
--cert-dir=/data/kubernetes/ssl \\
--image-pull-progress-deadline=15m \\
--pod-infra-container-image=registry.cn-hangzhou.aliyuncs.com/google_containers/pause-amd64:3.1"
EOF



# 配置如下
#    –hostname-override：显示名称，集群中唯一
#    –network-plugin：启用CNI
#    –kubeconfig：空路径，会自动生成，后面用于连接apiserver
#    –bootstrap-kubeconfig：首次启动向apiserver申请证书
#    –config：配置参数文件
#    –cert-dir：kubelet证书生成目录
#    –pod-infra-container-image：管理Pod网络容器的镜像

# 创建配置参数Yaml文件
cat > /data/kubernetes/cfg/kubelet-config.yml << EOF
kind: KubeletConfiguration
apiVersion: kubelet.config.k8s.io/v1beta1
address: 0.0.0.0
port: 10250
readOnlyPort: 10255
cgroupDriver: cgroupfs
clusterDNS:
- 10.0.0.2
clusterDomain: cluster.local 
failSwapOn: false
authentication:
  anonymous:
    enabled: false
  webhook:
    cacheTTL: 2m0s
    enabled: true
  x509:
    clientCAFile: /data/kubernetes/ssl/ca.pem 
authorization:
  mode: Webhook
  webhook:
    cacheAuthorizedTTL: 5m0s
    cacheUnauthorizedTTL: 30s
evictionHard:
  imagefs.available: 15%
  memory.available: 100Mi
  nodefs.available: 10%
  nodefs.inodesFree: 5%
maxOpenFiles: 1000000
maxPods: 110
EOF

# 生成bootstrap.kubeconfig
# 设置环境变量
# apiserver IP:PORT
KUBE_APISERVER="https://192.168.1.12:6443"
# 与token.csv里保持一致
TOKEN="b1dc586d69159ff4e3ef7efa9db60e48"
# 生成 kubelet bootstrap kubeconfig 配置文件
kubectl config set-cluster kubernetes \
  --certificate-authority=/data/kubernetes/ssl/ca.pem \
  --embed-certs=true \
  --server=${KUBE_APISERVER} \
  --kubeconfig=/data/kubernetes/cfg/bootstrap.kubeconfig

kubectl config set-credentials "kubelet-bootstrap" \
  --token=${TOKEN} \
  --kubeconfig=/data/kubernetes/cfg/bootstrap.kubeconfig
  
kubectl config set-context default \
  --cluster=kubernetes \
  --user="kubelet-bootstrap" \
  --kubeconfig=/data/kubernetes/cfg/bootstrap.kubeconfig
  
# 创建自动批准相关 CSR 请求的 ClusterRole
kubectl create clusterrolebinding  kubelet-bootstrap \
--clusterrole=system:node-bootstrapper \
--user=kubelet-bootstrap

# 创建目录存放文件
cat > /data/kubernetes/cfg/tls-instructs-csr.yaml << EOF
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: system:certificates.k8s.io:certificatesigningrequests:selfnodeserver
rules:
- apiGroups: ["certificates.k8s.io"]
  resources: ["certificatesigningrequests/selfnodeserver"]
  verbs: ["create"]
EOF
# 部署
kubectl apply -f /data/kubernetes/cfg/tls-instructs-csr.yaml

# 自动批准 kubelet-bootstrap 用户 TLS bootstrapping 首次申请证书的 CSR 请求
kubectl create clusterrolebinding node-client-auto-approve-csr \
--clusterrole=system:certificates.k8s.io:certificatesigningrequests:nodeclient \
--user=kubelet-bootstrap

# 自动批准 system:nodes 组用户更新 kubelet 自身与 apiserver 通讯证书的 CSR 请求
kubectl create clusterrolebinding node-client-auto-renew-crt \
--clusterrole=system:certificates.k8s.io:certificatesigningrequests:selfnodeclient \
--group=system:nodes

# 自动批准 system:nodes 组用户更新 kubelet 10250 api 端口证书的 CSR 请求
kubectl create clusterrolebinding node-server-auto-renew-crt \
--clusterrole=system:certificates.k8s.io:certificatesigningrequests:selfnodeserver \
--group=system:nodes

# 复制配置文件到Node节点中
scp kubelet.conf bootstrap.kubeconfig kubelet-config.yml kubelet.conf yunyao2:/data/kubernetes/cfg/
scp kubelet.conf bootstrap.kubeconfig kubelet-config.yml kubelet.conf yunyao3:/data/kubernetes/cfg/
```

​			两个节点创建服务

```sh
cat > /usr/lib/systemd/system/kubelet.service << EOF
[Unit]
Description=Kubernetes Kubelet
After=docker.service
[Service]
EnvironmentFile=/data/kubernetes/cfg/kubelet.conf
ExecStart=/data/kubernetes/bin/kubelet \$KUBELET_OPTS
Restart=on-failure
LimitNOFILE=65536
[Install]
WantedBy=multi-user.target
EOF

# 设置环境变量
# apiserver IP:PORT
KUBE_APISERVER="https://192.168.1.12:6443"
# 与token.csv里保持一致
TOKEN="b1dc586d69159ff4e3ef7efa9db60e48"
# 重新加载systemctl，启动服务，开机自启
systemctl daemon-reload && systemctl start kubelet && systemctl enable kubelet
```

​		





```
journalctl -xefu kubelet
```



## 安装CNI网络插件（选择其一即可）

​		在Kubernetes中有CNI插件，那么这个CNI插件到底是干什么的呢？我们知道Kubernetes是基于容器的，而容器又基于宿主机，也就是我们的Node节点，那么当我们部署一个一个Pod的时候，会将一个一个的Pod部署到不同的宿主机Node中（多Node节点情况），那么我们部署在不同节点下的Pod就无法互相通信了，例如，部署了3个Pod分别在3台Node中，他们之间是不能t通过ClusterIP访问的，所以为了让宿主机中的容器能够相互通讯，我们需要安装CNI网络插件。

### Flannel（推荐）

​		官网地址：[点击进入](https://github.com/coreos/flannel)

​			由CoreOS开发的项目Flannel，可能是最直接和最受欢迎的CNI插件。它是容器编排系统中最成熟的网络结构示例之一，旨在实现更好的容器间和主机间网络。随着CNI概念的兴起，Flannel CNI插件算是早期的入门。
与其他方案相比，Flannel相对容易安装和配置。它被打包为单个二进制文件FlannelD，许多常见的Kubernetes集群部署工具和许多Kubernetes发行版都可以默认安装Flannel。Flannel可以使用Kubernetes集群的现有etcd集群来使用API存储其状态信息，因此不需要专用的数据存储。

​		在线联网安装,Master节点执行

```sh
# 指定下载目录
export flannelPath="/root/flannel"
mkdir -p $flannelPath && cd $flannelPath

# 下载Flannel
wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml -O flannel.yml

# 然后应用
kubectl apply -f flannel.yml

# 查看是否安装成功
kubectl get pods -n kube-system | grep kube-flannel

# 如下都Running则成功并且都准备完成 1/1

# kube-flannel-ds-lzpgk             1/1     Running    0          64s
# kube-flannel-ds-t749p             1/1     Running    0          64s
# kube-flannel-ds-x9drq             1/1     Running    0          64s
```

### Calico（推荐）

​		官网地址：[点击进入](https://github.com/projectcalico/cni-plugin)

​			地址更新：https://github.com/projectcalico/calico

​			Calico是Kubernetes生态系统中另一种流行的网络选择。虽然Flannel被公认为是最简单的选择，但Calico以其性能、灵活性而闻名。Calico的功能更为全面，不仅提供主机和pod之间的网络连接，还涉及网络安全和管理。Calico CNI插件在CNI框架内封装了Calico的功能。

​			在满足系统要求的新配置的Kubernetes集群上，用户可以通过应用单个manifest文件快速部署Calico。如果您对Calico的可选网络策略功能感兴趣，可以向集群应用其他manifest，来启用这些功能。

​			尽管部署Calico所需的操作看起来相当简单，但它创建的网络环境同时具有简单和复杂的属性。与Flannel不同，Calico不使用overlay网络。相反，Calico配置第3层网络，该网络使用BGP路由协议在主机之间路由数据包。这意味着在主机之间移动时，不需要将数据包包装在额外的封装层中。BGP路由机制可以本地引导数据包，而无需额外在流量层中打包流量。

​			除了性能优势之外，在出现网络问题时，用户还可以用更常规的方法进行故障排除。虽然使用VXLAN等技术进行封装也是一个不错的解决方案，但该过程处理数据包的方式同场难以追踪。使用Calico，标准调试工具可以访问与简单环境中相同的信息，从而使更多开发人员和管理员更容易理解行为。

​			除了网络连接外，Calico还以其先进的网络功能而闻名。 网络策略是其最受追捧的功能之一。此外，Calico还可以与服务网格Istio集成，以便在服务网格层和网络基础架构层中解释和实施集群内工作负载的策略。这意味着用户可以配置强大的规则，描述Pod应如何发送和接受流量，提高安全性并控制网络环境。

​			如果对你的环境而言，支持网络策略是非常重要的一点，而且你对其他性能和功能也有需求，那么Calico会是一个理想的选择。此外，如果您现在或未来有可能希望得到技术支持，那么Calico是提供商业支持的。一般来说，当您希望能够长期控制网络，而不是仅仅配置一次并忘记它时，Calico是一个很好的选择。

​		在线联网安装：

```sh
# 指定版本
# curl https://docs.projectcalico.org/archive/v3.13/manifests/calico.yaml -O

# 指定下载目录
export calicoPath="/root/calico"
mkdir -p $calicoPath && cd $calicoPath
# 最新版本
wget https://docs.projectcalico.org/manifests/calico.yaml -O calico.yaml

# 查看Node 此时应该都为 NotReady
kubectl get nodes
# qingyun01   NotReady   control-plane,master   10m     v1.22.0
# qingyun02   NotReady   <none>                 4m57s   v1.22.0
# qingyun03   NotReady   <none>                 4m      v1.22.0
# 如果使用 192.168.0.0/16 作为Pod网络范围

# 使用calico.yaml
kubectl apply -f calico.yaml

# 查看是否安装成功
kubectl get pods -n kube-system | grep calico

# 如下都Running则成功并且都准备完成 1/1
# calico-kube-controllers-6b9fbfff44-qxk8b   1/1     Running   0             4h36m
# calico-node-n25rw                          1/1     Running   0             4h36m
# calico-node-rqqcg                          1/1     Running   0             4h36m
# calico-node-rrq5g                          1/1     Running   0             13m

# 查看Node 此时应该都为 Ready
kubectl get nodes
# qingyun01   Ready   control-plane,master   10m     v1.22.0
# qingyun02   Ready   <none>                 4m57s   v1.22.0
# qingyun03   Ready   <none>                 4m      v1.22.0
```

#### 问题汇总

​		问题：unable to connect to BIRDv4 socket: dial unix /var/run/bird/bird.ctl: connect

​		网卡问题导致，使用通配符匹配网卡

```sh
# 网卡问题导致重启或者外部情况导致，配置新增自定义网卡通配符					
            - name: IP_AUTODETECTION_METHOD
              value: "interface=eth*"
              
# 然后删除重新应用
kubectl delete -f calico.yaml
kubectl apply -f calico.yaml
```

### 安装Canal

​		官网地址：[点击进入](https://github.com/projectcalico/canal)

​			Canal也是一个有趣的选择，原因有很多。

​			首先，Canal 是一个项目的名称，它试图将Flannel提供的网络层与Calico的网络策略功能集成在一起。然而，当贡献者完成细节工作时却发现，很明显，如果Flannel和Calico这两个项目的标准化和灵活性都已各自确保了话，那集成也就没那么大必要了。结果，这个官方项目变得有些“烂尾”了，不过却实现了将两种技术部署在一起的预期能力。出于这个原因，即使这个项目不复存在，业界还是会习惯性地将Flannel和Calico的组成称为“Canal”。

​			由于Canal是Flannel和Calico的组合，因此它的优点也在于这两种技术的交叉。网络层用的是Flannel提供的简单Overlay，可以在许多不同的部署环境中运行且无需额外的配置。在网络策略方面，Calico强大的网络规则评估，为基础网络提供了更多补充，从而提供了更多的安全性和控制。

​			确保集群满足必要的系统要求后，用户需要应用两个manifest才能部署Canal，这使得其配置比单独的任何一个项目都困难。如果企业的IT团队计划改变他们的网络方案，且希望在实施改变之前能先对网络策略进行一些实验并获取一些经验，那么Canal是一个不错的选择。

​			一般来说，如果你喜欢Flannel提供的网络模型，但发现Calico的一些功能很诱人，那么不妨尝试一下Canal。从安全角度来看，定义网络策略规则的能力是一个巨大的优势，并且在许多方面是Calico的杀手级功能。能够将该技术应用到熟悉的网络层，意味着您可以获得更强大的环境，且可以省掉大部分的过渡过程。



### Weave

​		官网地址：[点击进入](https://www.weave.works/oss/net/)

​			Weave是由Weaveworks提供的一种Kubernetes CNI网络选项，它提供的模式和我们目前为止讨论的所有网络方案都不同。Weave在集群中的每个节点之间创建网状Overlay网络，参与者之间可以灵活路由。这一特性再结合其他一些独特的功能，在某些可能导致问题的情况下，Weave可以智能地路由。

​			为了创建网络，Weave依赖于网络中每台主机上安装的路由组件。然后，这些路由器交换拓扑信息，以维护可用网络环境的最新视图。当需要将流量发送到位于不同节点上的Pod时，Weave路由组件会自动决定是通过“快速数据路径”发送，还是回退到“sleeve”分组转发的方法。

​			快速数据路径依靠内核的本机Open vSwitch数据路径模块，将数据包转发到适当的Pod，而无需多次移入和移出用户空间。Weave路由器会更新Open vSwitch配置，以确保内核层具有有关如何路由传入数据包的准确信息。相反，当网络拓扑不适合快速数据路径路由时，sleeve模式可用作备份。它是一种较慢的封装模式，在快速数据路径缺少必要的路由信息或连接的情况下，它可以来路由数据包。当流量通过路由器时，它们会了解哪些对等体与哪些MAC地址相关联，从而允许它们以更少的跳数、更智能地路由后续流量。当网络更改导致可用路由改变时，这一相同的机制可以帮助每个节点进行自行更正。

​			与Calico一样，Weave也为Kubernetes集群提供网络策略功能。设置Weave时，网络策略会自动安装和配置，因此除了添加网络规则之外，用户无需进行其他配置。一个其他网络方案都没有、Weave独有的功能，是对整个网络的简单加密。虽然这会增加相当多的网络开销，但Weave可以使用NaCl加密来为sleeve流量自动加密所有路由流量，而对于快速数据路径流量，因为它需要加密内核中的VXLAN流量，Weave会使用IPsec ESP来加密快速数据路径流量。

​			对于那些寻求功能丰富的网络、同时希望不要增加大量复杂性或管理难度的人来说，Weave是一个很好的选择。它设置起来相对容易，提供了许多内置和自动配置的功能，并且可以在其他解决方案可能出现故障的场景下提供智能路由。网状拓扑结构确实会限制可以合理容纳的网络的大小，不过对于大多数用户来说，这也不是一个大问题。此外，Weave也提供收费的技术支持，可以为企业用户提供故障排除等等技术服务。

## 安装监控工具

​		官方地址：[点击进入](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/)

   GitHub地址：[点击进入](https://github.com/kubernetes/dashboard)

​		版本兼容：[点击进入](https://github.com/kubernetes/dashboard/releases)

​		我们在Master控制节点安装

​		这里1.22兼容的版本是（没有兼容取最新）  [v2.4.0](https://github.com/kubernetes/dashboard/releases/tag/v2.4.0)

​		执行如下命令

```sh
# 指定下载目录
export k8sDashboardPath="/root/k8s-dashboard"
mkdir -p $k8sDashboardPath && cd $k8sDashboardPath
# 下载部署文件
wget https://raw.githubusercontent.com/kubernetes/dashboard/v2.4.0/aio/deploy/recommended.yaml -O deploy.yaml

# 添加宿主机端口号30000
sed -i '/targetPort: 8443/a\      nodePort: 30000' deploy.yaml
# 设置新增类型为NodePort，全局搜/8443
vim deploy.yaml

--------------
metadata:
  labels:
    k8s-app: kubernetes-dashboard
  name: kubernetes-dashboard
  namespace: kubernetes-dashboard
spec:
	type: NodePort   # 新增这一行
  ports:
    - port: 443
      targetPort: 8443
--------------

# 启动dashboard
kubectl apply -f deploy.yaml

# 查看相关POD
kubectl -n kubernetes-dashboard get pods
kubectl -n kubernetes-dashboard get svc 

```

​		然后查看pod

```sh
kubectl -n kubernetes-dashboard get pods
kubectl -n kubernetes-dashboard get svc 

# 查看是否有http证书
kubectl get secret kubernetes-dashboard-certs -n kubernetes-dashboard
```

​		！！如果没有则自己生成，如果有则不需要,基本不需要

```sh
# 创建目录，2.0需要使用https进行访问，所以我们生成一个证书（假的）否则无法访问
mkdir -p ~/k8s-dashboard/tls && cd ~/k8s-dashboard/tls

# 生成证书
openssl genrsa -out ca.key 2048
openssl req -new -x509 -key ca.key -out ca.crt -days 3650 -subj "/C=CN/ST=shanghai/L=jingan/O=dev/OU=island/CN=*.onebean.net"
openssl genrsa -out dashboard.key 2048 &&\
openssl req -new -sha256 -key dashboard.key -out dashboard.csr -subj "/C=CN/ST=shanghai/L=jingan/O=dev/OU=island/CN=k8s.onebean.net" &&\
cat >  dashboard.cnf  <<EOF
extensions = san
[san]
keyUsage = digitalSignature
extendedKeyUsage = clientAuth,serverAuth
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid,issuer
subjectAltName = DNS:k8s.onebean.net
EOF
openssl x509 -req -sha256 -days 3650 -in dashboard.csr -out dashboard.crt -CA ca.crt -CAkey ca.key -CAcreateserial -extfile dashboard.cnf

# 如果有删除
kubectl delete secret kubernetes-dashboard-certs -n kubernetes-dashboard
kubectl get secret kubernetes-dashboard-certs -n kubernetes-dashboard
# 然后创建
kubectl create secret generic kubernetes-dashboard-certs --from-file="/root/ca/dashboard.crt,/root/ca/dashboard.key" -n kubernetes-dashboard
kubectl get secret kubernetes-dashboard-certs -n kubernetes-dashboard -o yaml

# 然后重启kubernetes-dashboard
# 查询出kubernetes-dashboard名字
kubectl  get pods -n kubernetes-dashboard
# 根据名字以及组重启
kubectl get pod kubernetes-dashboard-7f99b75bf4-kkfbk -n kubernetes-dashboard -o yaml |  kubectl replace --force -f -
```

​		我们访问浏览器

​		https://192.168.100.11:30000

​		如果谷歌浏览器提示这个证书无效非法，我们不要动在异常的谷歌界面上打字，切换英文

```
thisisunsafe
```

​		然后就能看到略过了证书不安全提示		

​		然后我们来创建Token进行访问

​		我们首先使用名称`admin-user`空间中的名称创建服务帐户`kubernetes-dashboard`。

```sh
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
EOF
```

​		在供应使用集束后大多数情况下`kops`，`kubeadm`或任何其他流行的工具，在`ClusterRole` `cluster-admin`已经存在的群集。我们可以使用它并仅为`ClusterRoleBinding`我们创建`ServiceAccount`。如果不存在，则需要首先创建此角色并手动授予所需的特权。

```sh
cat <<EOF | kubectl apply -f -
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
EOF
```

​		现在，我们需要找到可用于登录的令牌。执行以下命令：

```sh
# 对于Bash：
	
	kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}')

# 对于Powershell：
		
	kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | sls admin-user | ForEach-Object { $_ -Split '\s+' } | Select -First 1)
```

​		我们使用Bash

```sh
kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}')

```

​		返回如下日志：

```
Data
====
ca.crt:     1025 bytes
namespace:  20 bytes
token:      eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0d05VUWx1Vm5oU3ZtTHQ2ZENHZGtIa1FnRmhTZ244aVN1UU1lVl9kNEkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLTQ2ODhwIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI5M2QzMWVlZi00ZGJiLTQyM2EtYTI4ZS1jODVhYTUxMzE0YjkiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQ6YWRtaW4tdXNlciJ9.sFa68JLEirDHEkn9N6bKEZuAVMci1qJlnyHbjYd3wdz94zPtnJzaY6QUkyRKt9hQQYZoxKrRaH5Bzt7WE-X2GxeuHzbWWE1lO5DAsoIhDD9wSKpF-3z386JY5iUEWpe2oHDtetM34okmMxRcfe3HKwzGTr2YZ3S-fuwUOTlww64tdFgCcdeyDDa1Bg0TxwCGDbzZMoU_4cWlMX-nENyWHvpzJwyWi13HaJ1spEDbYR8rorBu-i6KIhyHVT4OYXIy93qoAbs7g9zHwMTdvneRW9mpCCfvU00FuKVdCP9kfj7xDbLg7tdVLmL1rqVJ2DbvDmtHE037-hnbGkzrasZuQg

```



```sh
# 将token写入文件,查询最后一行
kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}') | grep token | tail -n 1 | awk '{print $2}'  > /root/k8s/k8s-dashboard-token
```



​		复制token进入https://192.168.100.11:30000/ 输入Token登陆成功

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1605002186679.png)

## 安装Ingress

​		官网地址：[点击进入](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers/)

​		什么是Ingress，Ingress也叫做Ingress Controller。也称之为入口控制器，那么这个入口控制器肯定就是管理入口的。

​		在Kubernetes中，服务和Pod的IP地址仅可以在集群网络内部使用，对于集群外的应用是不可见的。为了使外部的应用能够访问集群内的服务，在Kubernetes 目前 提供了以下几种方案：

- ​			NodePort
- ​			LoadBalancer
- ​			Ingress

​		为了使Ingress资源正常工作，集群必须运行一个Ingress Controller。与作为`kube-controller-manager`二进制文件一部分运行的其他类型的控制器不同，Ingress控制器不会随群集自动启动。使用此页面选择最适合您的集群的入口控制器实现。

​		Kubernetes作为项目目前支持和维护[GCE](https://git.k8s.io/ingress-gce/README.md)和 [nginx的](https://git.k8s.io/ingress-nginx/README.md)控制器。

**附加控制器**

- [AKS Application Gateway入口控制器](https://github.com/Azure/application-gateway-kubernetes-ingress)是一个入口控制器，可使用[Azure Application Gateway](https://docs.microsoft.com/azure/application-gateway/overview)启用[AKS群集的](https://docs.microsoft.com/azure/aks/kubernetes-walkthrough-portal)入口。
- [大使馆](https://www.getambassador.io/)API网关是基于[Envoy](https://www.envoyproxy.io/)的入口控制器，具有[Datawire的](https://www.datawire.io/)[社区](https://www.getambassador.io/docs)或 [商业](https://www.getambassador.io/pro/)支持。
- [AppsCode Inc.](https://appscode.com/)为最广泛使用的基于[HAProxy](https://www.haproxy.org/)的入口控制器[Voyager](https://appscode.com/products/voyager)提供支持和维护。
- [AWS ALB Ingress Controller](https://github.com/kubernetes-sigs/aws-alb-ingress-controller)使用[AWS Application Load Balancer](https://aws.amazon.com/elasticloadbalancing/)启用入口。
- [Contour](https://projectcontour.io/)是VMware提供和支持的基于[Envoy](https://www.envoyproxy.io/)的入口控制器。
- Citrix为其硬件（MPX），虚拟化（VPX）和[免费容器化（CPX）ADC](https://www.citrix.com/products/citrix-adc/cpx-express.html)提供了一个[Ingress Controller](https://github.com/citrix/citrix-k8s-ingress-controller)，用于[裸机](https://github.com/citrix/citrix-k8s-ingress-controller/tree/master/deployment/baremetal)和[云](https://github.com/citrix/citrix-k8s-ingress-controller/tree/master/deployment)部署。
- F5 Networks[为Kubernetes](https://clouddocs.f5.com/containers/latest/userguide/kubernetes/) 的[F5 BIG-IP容器入口服务](https://clouddocs.f5.com/containers/latest/userguide/kubernetes/)提供[支持和维护](https://support.f5.com/csp/article/K86859508)。
- [Gloo](https://gloo.solo.io/)是基于[Envoy](https://www.envoyproxy.io/)的开源入口控制器，它提供API网关功能以及[solo.io的](https://www.solo.io/)企业支持。
- [HAProxy Ingress](https://haproxy-ingress.github.io/)是高度可定制的社区驱动的HAProxy入口控制器。
- [HAProxy Technologies](https://www.haproxy.com/)[为Kubernetes](https://github.com/haproxytech/kubernetes-ingress)的[HAProxy入口控制器](https://github.com/haproxytech/kubernetes-ingress)提供支持和维护。请参阅[官方文档](https://www.haproxy.com/documentation/hapee/1-9r1/traffic-management/kubernetes-ingress-controller/)。
- 基于[Istio](https://istio.io/)的入口控制器 [控制入口流量](https://istio.io/docs/tasks/traffic-management/ingress/)。
- [Kong](https://konghq.com/)[为Kubernetes](https://github.com/Kong/kubernetes-ingress-controller)的[Kong Ingress控制器](https://github.com/Kong/kubernetes-ingress-controller)提供[社区](https://discuss.konghq.com/c/kubernetes)或 [商业](https://konghq.com/kong-enterprise/)支持和维护 。
- [NGINX，Inc.](https://www.nginx.com/)为用于[Kubernetes的NGINX入口控制器](https://www.nginx.com/products/nginx/kubernetes-ingress-controller)提供支持和维护 。
- [Skipper](https://opensource.zalando.com/skipper/kubernetes/ingress-controller/) HTTP路由器和用于服务组合的反向代理，包括用例（如Kubernetes Ingress），被设计为用于构建自定义代理的库
- [Traefik](https://github.com/traefik/traefik)是功能齐全的入口控制器（[让我们加密](https://letsencrypt.org/)，秘密，http2，websocket），并且[Traefik Labs](https://traefik.io/)还提供了商业支持。

### ingress-nginx

​		官网地址：[点击进入](https://kubernetes.github.io/ingress-nginx/deploy/#quick-start)

​		安装部署：

```sh
# 指定Yaml下载目录
export ingressNginxPath="/root/ingress-nginx"
# 创建挂载目录
mkdir -p $ingressNginxPath && cd $ingressNginxPath
# 下载
# wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.0/deploy/static/provider/cloud/deploy.yaml -O deploy.yaml

wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.0/deploy/static/provider/baremetal/deploy.yaml -O deploy.yaml

# 修改镜像为阿里云加速镜像否则无法创建拉取
# 将 k8s.gcr.io/ingress-nginx/controller 替换为 registry.aliyuncs.com/google_containers/nginx-ingress-controller
sed -i "s#k8s.gcr.io/ingress-nginx/controller#registry.aliyuncs.com/google_containers/nginx-ingress-controller#g" deploy.yaml
sed -i "s#k8s.gcr.io/ingress-nginx/kube-webhook-certgen#registry.aliyuncs.com/google_containers/kube-webhook-certgen#g" deploy.yaml



# 启动部署
kubectl apply -f deploy.yaml


# 如下nginx-controller运行成功即可
kubectl get pod -n ingress-nginx
ingress-nginx-admission-create--1-mcdt8     0/1     Completed   0          3m2s
ingress-nginx-admission-patch--1-lrqsr      0/1     Completed   1          3m2s
ingress-nginx-controller-6747bcd76c-d597b   1/1     Running     0          3m2s


# ------暂时不用------暂时不用 ------暂时不用 ------暂时不用 ------暂时不用  
# 然后我们定义Ingress的宿主机端口,全局搜 type: NodePort
# 暂时不用
vim deploy.yaml

-------------
spec:
  type: NodePort
  externalTrafficPolicy: Local
  ipFamilyPolicy: SingleStack
  ipFamilies:
    - IPv4
  ports:
    - name: http
      port: 80
      protocol: TCP
      targetPort: http
      appProtocol: http
      nodePort: 30080  # 新增NodePort宿主机映射端口
    - name: https
      port: 443
      protocol: TCP
      targetPort: https
      appProtocol: https
      nodePort: 30443 # 新增NodePort宿主机映射端口
-------------
# 然后修改HostWork 全局搜：--election-id=ingress-controller-leader
-------------

    spec:
      dnsPolicy: ClusterFirst
      hostNetwork: true  # 新增网络hostNetwork
      containers:
        - name: controller
          image: registry.aliyuncs.com/google_containers/nginx-ingress-controller:v1.1.0@sha256:f766669fdcf3dc26347ed273a55e754b427eb4411ee075a53f30718b4499076a
          imagePullPolicy: IfNotPresent
          lifecycle:
            preStop:

-------------
```

​		修改端口（可以不修改）

```sh
# 如果想使用80 以及 443,那我们需要修改默认NodePort端口范围（不太推荐，不想使用443 以及 80可以略过）
kubectl get pod -A | grep apiserver

# 返回如下
kube-system            kube-apiserver-qingyun01                    1/1     Running   3 (77m ago)   5h34m

# 我们导出apiserver
# 指定Yaml下载目录
export customApiServerPath="/root/apiServer"
mkdir -p $customApiServerPath
kubectl get pod kube-apiserver-qingyun01 -n kube-system -o yaml > $customApiServerPath/apiserver.yaml

# 查看是否指定端口，如果有则修改没有则新增
cat $customApiServerPath/apiserver.yaml | grep service-node-port-rang
# 添加端口号范围
sed -i '/kubernetes.default.svc.cluster.local/a\    - --service-node-port-range=80-65535' /etc/kubernetes/manifests/kube-apiserver.yaml
# 修改完成后自动更新，不需要操作，重新导出一份yaml
kubectl get pod kube-apiserver-qingyun01 -n kube-system -o yaml > $customApiServerPath/apiserver.yaml
# 再次检查是否设置成功
cat $customApiServerPath/apiserver.yaml | grep service-node-port-rang
# 然后我们定义Ingress的宿主机端口,全局搜 ingress-nginx/templates/controller-service.yaml
vim deploy.yaml
-------------
  ports:
    - name: http
      port: 80
      protocol: TCP
      targetPort: http
      appProtocol: http
      nodePort: 80  # 新增NodePort宿主机映射端口
    - name: https
      port: 443
      protocol: TCP
      targetPort: https
      appProtocol: https
      nodePort: 443 # 新增NodePort宿主机映射端口
-------------
kubectl apply -f deploy.yaml
```

​		部署测试项目并且创建ingress

```sh
# 创建并且暴露demo
kubectl create deployment demo --image=httpd --port=80
kubectl expose deployment demo

# 查看demo是否启动
kubectl get all | grep demo

# 定义域名,域名证书地址，证书命名空间，以及Ingress服务
# 生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainName="demo.bigkang.club"
export domainPath="/root/k8s/tls"
export tlsNameSpace="default"
export ingressService="demo"
export ingressServicePort="80"

# 前置准备删除原来的证书以及Ingress
kubectl delete secret $domainName-tls-secret 
kubectl delete ingress $domainName-ingress
kubectl delete secret $domainName-tls-secret --namespace=$tlsNameSpace
kubectl delete ingress $domainName-ingress --namespace=$tlsNameSpace

# 定义域名,生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
mkdir -p $domainPath/$domainName && cd $domainPath/$domainName
# 生成私钥(KEY)
openssl genrsa -out $domainName.key 4096
openssl req -x509 -new -nodes -key $domainName.key -subj "/CN=$domainName" -days 36500 -out $domainName.crt
openssl req -new -sha256 \
    -key $domainName.key \
    -subj "/C=CN/ST=Beijing/L=Beijing/O=UnitedStack/OU=Devops/CN=$domainName" \
    -reqexts SAN \
    -config <(cat /etc/pki/tls/openssl.cnf \
        <(printf "[SAN]\nsubjectAltName=DNS:$domainName")) \
    -out $domainName.csr
openssl req -text -in $domainName.csr
openssl x509 -req -days 365000 \
    -in $domainName.csr -CA $domainName.crt -CAkey $domainName.key -CAcreateserial \
    -extfile <(printf "subjectAltName=DNS:$domainName") \
    -out $domainName.pem


# 创建tls证书
kubectl create secret tls $domainName-tls-secret --namespace=$tlsNameSpace --cert=$domainName.pem --key=$domainName.key --dry-run=client -o yaml > $domainName-secret.yaml
kubectl apply -f $domainName-secret.yaml



# 修改hosts 访问域名
echo "192.168.100.11 $domainName"

# Yaml方式启动
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $tlsNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/test.example.com.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/test.example.com.error.log;
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF


# 应用
kubectl apply -f  $domainName-ingress.yaml

# 查看ingress
kubectl get ing -n $tlsNameSpace
```

#### tomcat

​		然后我们部署一个tomcat测试转发功能，以及域名映射

```sh
# 定义域名,域名证书地址，证书命名空间，以及Ingress服务
# 生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainName="tomcat.bigkang.club"
export domainPath="/root/k8s/tls"
export tlsNameSpace="default"
export ingressService="tomcat"
export ingressServicePort="8080"

# 前置准备删除原来的证书以及Ingress
kubectl delete secret $domainName-tls-secret 
kubectl delete ingress $domainName-ingress
kubectl delete secret $domainName-tls-secret --namespace=$tlsNameSpace
kubectl delete ingress $domainName-ingress --namespace=$tlsNameSpace

# 创建目录
mkdir -p $domainPath/$domainName && cd $domainPath/$domainName

# 创建Tomcat部署的deployment
cat > $domainName-deployment.yaml << EOF 
apiVersion: v1
kind: Service
metadata:
  name: $ingressService
  namespace: $tlsNameSpace
spec:
  selector:
   app: $ingressService
  ports:
  - name: http
    targetPort: $ingressServicePort
    port: $ingressServicePort 
    
---
 
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $ingressService
  namespace: $tlsNameSpace
spec:
  replicas: 1
  selector:
   matchLabels:
     app: $ingressService
  template:
   metadata:
     labels:
       app: $ingressService
   spec:
     containers:
     - name: $ingressService
       image: tomcat
       ports:
       - name: http
         containerPort: $ingressServicePort
EOF

# 部署Tomcat
kubectl apply -f $domainName-deployment.yaml

# 初始化证书
# 生成私钥(KEY)
openssl genrsa -out $domainName.key 4096
openssl req -x509 -new -nodes -key $domainName.key -subj "/CN=$domainName" -days 36500 -out $domainName.crt
openssl req -new -sha256 \
    -key $domainName.key \
    -subj "/C=CN/ST=Beijing/L=Beijing/O=UnitedStack/OU=Devops/CN=$domainName" \
    -reqexts SAN \
    -config <(cat /etc/pki/tls/openssl.cnf \
        <(printf "[SAN]\nsubjectAltName=DNS:$domainName")) \
    -out $domainName.csr
openssl req -text -in $domainName.csr
openssl x509 -req -days 365000 \
    -in $domainName.csr -CA $domainName.crt -CAkey $domainName.key -CAcreateserial \
    -extfile <(printf "subjectAltName=DNS:$domainName") \
    -out $domainName.pem
    
# 创建tls证书
kubectl create secret tls $domainName-tls-secret --namespace=$tlsNameSpace --cert=$domainName.pem --key=$domainName.key --dry-run=client -o yaml > $domainName-secret.yaml
kubectl apply -f $domainName-secret.yaml

# 修改hosts 访问域名
echo "192.168.100.11 $domainName"

# 创建Ingress的yaml文件
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $tlsNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/test.example.com.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/test.example.com.error.log;
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF

# 启动Ingress
kubectl apply -f $domainName-ingress.yaml
```

#### ingress-k8s-dashboard

```sh
# 创建证书

# 定义域名,生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainName="dashboard.bigkang.club"
mkdir -p ~/k8s/tls/$domainName && cd ~/k8s/tls/$domainName
openssl genrsa -out $domainName.key
openssl req -new -sha256 -key $domainName.key -out $domainName.csr -subj "/C=CN/ST=sichuan/L=dazhou/O=bigkang/OU=kaifa/CN=$domainName"
openssl x509 -req -days 3650 -sha1 -extensions v3_ca -signkey $domainName.key -in $domainName.csr -out $domainName.crt
openssl x509 -in $domainName.crt -out $domainName.pem -outform PEM


# 创建tls证书
kubectl create secret tls $domainName-tls-secret --namespace=kubernetes-dashboard --cert=$domainName.pem --key=$domainName.key --dry-run=client -o yaml > $domainName-secret.yaml

kubectl apply -f $domainName-secret.yaml
```

```sh
# 两种方式启动(命令 Or Yaml)

# 命令直接创建ingress
kubectl create ingress ingress-k8s-dashboard --namespace=kubernetes-dashboard --class=nginx \
  --rule=$domainName/*=kubernetes-dashboard:443,tls=dashboard.bigkang.club-tls-secret
  
# Yaml方式启动
cat > kubernetes-dashboard-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ingress-k8s-dashboard
  namespace: kubernetes-dashboard
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/backend-protocol: "HTTPS"
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: kubernetes-dashboard
              port:
                number: 443
EOF
# 启动Yaml
kubectl apply -f kubernetes-dashboard-ingress.yaml
```

```sh
# 删除证书以及删除清空环境
export domainName="dashboard.bigkang.club"
rm -rf ~/k8s/tls/$domainName

# 删除ingress
kubectl delete ingress ingress-k8s-dashboard -n kubernetes-dashboard

# 删除tls证书
kubectl delete secret $domainName-tls-secret
```

#### 配置大全

​		我们使用ingress需要做很多配置我们可以通过注解annotation

​		官网地址：[注解 官网配置点击进入](https://github.com/kubernetes/ingress-nginx/blob/main/docs/user-guide/nginx-configuration/annotations.md)

```yaml
cat >  demo-ingress.yaml  <<EOF
kind: Ingress # 类型
apiVersion: networking.k8s.io/v1 # API版本
metadata:
  name: demo.bigkang.club-ingress # 名称
  namespace: default # 命名空间
  annotations:
  
  
  	###  重定向相关
  	nginx.ingress.kubernetes.io/ssl-redirect: "false" 					# 是否把重定向到https，访问http
  	nginx.ingress.kubernetes.io/force-ssl-redirect: "false"			# 强制重定向到HTTPS，即使入口不启用TLS
  	nginx.ingress.kubernetes.io/app-root: "/"										# 定义应用程序根目录，如果它在'/'上下文中，控制器必须重定向它
  	nginx.ingress.kubernetes.io/use-regex: "false" 							# 指示入口中定义的路径是否使用正则表达式
  	nginx.ingress.kubernetes.io/rewrite-target: "/$2"						
  	# 必须重定向流量的目标URI,在这个入口定义中，被捕获的任何字符(.*)都将分配给占位符$2，然后将其用作rewrite-target注释中的参数。
  	# 例如，上面的入口定义将导致以下重写：
    #    demo.bigkang.club/test 改写为 demo.bigkang.club/
    #    demo.bigkang.club/test/ 改写为 demo.bigkang.club/
    #    demo.bigkang.club/test/new 改写为 demo.bigkang.club/new
    
spec:
  ingressClassName: nginx # ingress 类型 这里使用nginx
  tls:
    - hosts:
        - demo.bigkang.club # https域名
      secretName: demo.bigkang.club-tls-secret # tls证书secret服务的名称（注意和证书同一命名空间）
  rules:
    - host: demo.bigkang.club # http的host域名
      http:
        paths:
          - path: / # 解析路径Path
            pathType: Prefix # 解析类型
            backend:
              service:
                name: demo # 后端的服务名 （同一命名空间的服务）
                port:
                  number: 80 # 端口
          - path: /test(/|$)(.*)  # 解析路径Path
            pathType: Prefix # 解析类型
            backend:
              service:
                name: demo # 后端的服务名 （同一命名空间的服务）
                port:
                  number: 80 # 端口
EOF
```

​		如下更多

```properties
# 粘性的Session会话

参考如下: https://github.com/kubernetes/ingress-nginx/blob/main/docs/examples/affinity/cookie/README.md

# 验证认证配置,配置Nginx用户名密码访问

参考如下: https://github.com/kubernetes/ingress-nginx/blob/main/docs/examples/auth/basic/README.md

# 一致性Hash

参考如下: https://github.com/kubernetes/ingress-nginx/blob/main/docs/examples/chashsubset/deployment.yaml

# Ingress-Nginx ConfigMap配置以及默认值配置
参考如下: https://github.com/kubernetes/ingress-nginx/blob/main/docs/user-guide/nginx-configuration/configmap.md#load-balance


```

#### ingress模板

​		不带证书http

```sh
# 部署路径，域名，命名空间，service名称，service端口
export dpPath="/root/k8s/tls"
export domainName="tomcat.bigkang.club"
export dpNameSpace="default"
export ingressService="tomcat"
export ingressServicePort="8080"

# 创建目录
mkdir -p $dpPath && cd $dpPath

# 创建Ingress的yaml文件
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $dpNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/$domainName.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/$domainName.error.log;
spec:
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF
```

​		带证书https

```sh
# 部署路径，域名，命名空间，service名称，service端口
export dpPath="/root/k8s/tls"
export domainName="tomcat.bigkang.club"
export dpNameSpace="default"
export ingressService="tomcat"
export ingressServicePort="8080"

# 创建目录
mkdir -p $dpPath && cd $dpPath

# 创建Ingress的yaml文件
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $dpNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/$domainName.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/$domainName.error.log;
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF
```





## 部署Nacos

### 单机版本

```sh
# 定义参数
export nacosPath="/root/k8s/deploy/nacos"
# 创建目录
mkdir -p $nacosPath && cd nacosPath

# 下载部署文件
git clone https://github.com/nacos-group/nacos-k8s.git

# 进入目录
cd nacos-k8s
# 启动Nacos
chmod +x quick-startup.sh
./quick-startup.sh
```

​		卸载单机版本Nacos

```sh
# ！！！ 卸载
# 定义参数
export nacosPath="/root/k8s/deploy/nacos"
# 创建目录
mkdir -p $nacosPath && cd nacosPath

# 卸载Nacos
kubectl delete -f ./deploy/nacos/nacos-quick-start.yaml

# 卸载MySQL
kubectl delete -f ./deploy/mysql/mysql-local.yaml
```

### 集群持久化

```sh
# 定义参数
export nacosPath="/root/k8s/deploy/nacos"
# 创建目录
mkdir -p $nacosPath && cd $nacosPath

# 下载部署文件
git clone https://github.com/nacos-group/nacos-k8s.git

# 进入目录
cd nacos-k8s

# 安装NFS
yum install -y nfs-utils
yum install -y rpcbind

# 开机自启
systemctl enable nfs-server.service
# 设置NFS
vim /etc/exports
# 写入如下
# /data/nfs/nacos *(insecure,rw,sync,no_root_squash) (不限制网段)
/data/nfs/nacos 192.168.100.0/24(insecure,rw,sync,no_root_squash)

# 参数详解
#   ro #只读共享
#   rw #读写共享
#   sync #同步写操作
#   async #异步写操作
#   wdelay #延迟写操作
#   root_squash #屏蔽远程root权限
#   no_root_squash #不屏蔽远程root权限
#   all_squash #屏蔽所有远程用户的权限
#   no_subtree_check #此选项可防止子树检查

# 创建目录
mkdir -p /data/nfs/nacos

# 启动NFS服务
systemctl start nfs-server.service
systemctl start rpcbind

# 查看共享的目录
showmount -e
# 返回如下即可
# Export list for qingyun01:
# /data/nfs/nacos 192.168.100.0/24

# 修改NFS的网络配置
sed -i "s#172.17.79.3#192.168.100.11#g"  ./deploy/nfs/deployment.yaml
sed -i "s#/data/nfs-share#/data/nfs/nacos#g"  ./deploy/nfs/deployment.yaml

# 子节点安装NFS
yum install -y nfs-utils

# 部署nfs

kubectl apply -f deploy/nfs/rbac.yaml
kubectl apply -f deploy/nfs/deployment.yaml
kubectl apply -f deploy/nfs/class.yaml

# 验证nfs是否部署成功
kubectl get pod -l app=nfs-client-provisioner

# 修改NFS
sed -i "s#172.17.79.3#192.168.100.11#g"  deploy/mysql/mysql-nfs.yaml
sed -i "s#/data/mysql#/data/nfs/nacos#g"  deploy/mysql/mysql-nfs.yaml

# 部署MySQL-NFS
kubectl apply -f deploy/mysql/mysql-nfs.yaml

# 部署Nacos
kubectl apply -f deploy/nacos/nacos-pvc-nfs.yaml
```

### 自定义持久化

​		mysql持久化pvc

```sh
cat >  nacos-mysql-pvc.yaml  <<EOF
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nacos-mysql-pv
spec:
  capacity:
    storage: 20Gi
  accessModes:
    - ReadWriteMany
  storageClassName: nfs
  nfs:
    server: 192.168.100.11
    path: /data/nfs/nacos-mysql
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nacos-mysql-pvc
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 10Gi
  storageClassName: nfs
EOF
kubectl apply -f nacos-mysql-pvc.yaml 
```

​		mysql部署

```sh
cat >  nacos-mysql.yaml  <<EOF
apiVersion: v1
kind: ReplicationController
metadata:
  name: nacos-mysql
  labels:
    name: nacos-mysql
spec:
  replicas: 1
  template:
    metadata:
      labels:
        name: nacos-mysql
    spec:
      containers:
      - name: nacos-mysql
        image: nacos/nacos-mysql:8.0.16
        ports:
        - containerPort: 3306
        volumeMounts:
        - name: mysql-data
          mountPath: /var/lib/mysql
        env:
        - name: MYSQL_ROOT_PASSWORD
          value: "bigkang"
        - name: MYSQL_DATABASE
          value: "nacos_devtest"
        - name: MYSQL_USER
          value: "nacos"
        - name: MYSQL_PASSWORD
          value: "nacos"
      volumes:
      - name: mysql-data
        persistentVolumeClaim:
          claimName: nacos-mysql-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: nacos-mysql
  labels:
    name: nacos-mysql
spec:
  type: NodePort
  ports:
  - port: 3306
    protocol: TCP
    nodePort: 63306
    targetPort: 3306
  selector:
    name: nacos-mysql
EOF


kubectl apply -f nacos-mysql.yaml
```

​		部署Nacos

```sh
# 定义参数
export mysqlHost="nacos-mysql"
export clusterIp="nacos-0.nacos-headless.default.svc.cluster.local:8848 nacos-1.nacos-headless.default.svc.cluster.local:8848 nacos-2.nacos-headless.default.svc.cluster.local:8848"

cat >  nacos-deploy.yaml  <<EOF
---
apiVersion: v1
kind: Service
metadata:
  name: nacos-headless
  labels:
    app: nacos
  annotations:
    service.alpha.kubernetes.io/tolerate-unready-endpoints: "true"
spec:
  ports:
    - port: 8848
      name: server
      targetPort: 8848
    - port: 9848
      name: client-rpc
      targetPort: 9848
    - port: 9849
      name: raft-rpc
      targetPort: 9849
    ## 兼容1.4.x版本的选举端口
    - port: 7848
      name: old-raft-rpc
      targetPort: 7848
  clusterIP: None
  selector:
    app: nacos
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: nacos-cm
data:
  mysql.service.host: "${mysqlHost}"
  mysql.db.name: "nacos_devtest"
  mysql.port: "3306"
  mysql.user: "nacos"
  mysql.password: "nacos"
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: nacos
spec:
  serviceName: nacos-headless
  replicas: 3
  template:
    metadata:
      labels:
        app: nacos
      annotations:
        pod.alpha.kubernetes.io/initialized: "true"
    spec:
      containers:
        - name: nacos
          imagePullPolicy: IfNotPresent
          image: nacos/nacos-server:v2.1.0
          resources:
            requests:
              memory: "2Gi"
              cpu: "500m"
          ports:
            - containerPort: 8848
              name: client-port
            - containerPort: 9848
              name: client-rpc
            - containerPort: 9849
              name: raft-rpc
            - containerPort: 7848
              name: old-raft-rpc
          env:
            - name: NACOS_REPLICAS
              value: "3"
            - name: SERVICE_NAME
              value: "nacos-headless"
            - name: DOMAIN_NAME
              value: "cluster.local"
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  apiVersion: v1
                  fieldPath: metadata.namespace
            - name: MYSQL_SERVICE_DB_NAME
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.db.name
            - name: MYSQL_SERVICE_HOST
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.service.host
            - name: MYSQL_SERVICE_PORT
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.port
            - name: MYSQL_SERVICE_USER
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.user
            - name: MYSQL_SERVICE_PASSWORD
              valueFrom:
                configMapKeyRef:
                  name: nacos-cm
                  key: mysql.password
            - name: NACOS_SERVER_PORT
              value: "8848"
            - name: NACOS_APPLICATION_PORT
              value: "8848"
            - name: PREFER_HOST_MODE
              value: "hostname"
            - name: NACOS_SERVERS
              value: "$clusterIp"
  selector:
    matchLabels:
      app: nacos
EOF

kubectl apply -f nacos-deploy.yaml
```

​		创建nacos-pvc(弃用)

```sh
# 定义参数
export nfsServer="192.168.100.11"
export nfsPath="/data/nfs/nacos"
export namespaceDp="default"

cat >  nacos-pvc.yaml  <<EOF
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nacos-pv
spec:
  capacity:
    storage: 20Gi
  accessModes:
    - ReadWriteMany
  storageClassName: nfs
  nfs:
    server: $nfsServer
    path: $nfsPath
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nfs-client-root
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 10Gi
  storageClassName: nfs
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-client-provisioner-runner
rules:
- apiGroups: [""]
  resources: ["persistentvolumes"]
  verbs: ["get", "list", "watch", "create", "delete"]
- apiGroups: [""]
  resources: ["persistentvolumeclaims"]
  verbs: ["get", "list", "watch", "update"]
- apiGroups: [""]
  resources: ["endpoints"]
  verbs: ["get", "list", "watch", "create", "update", "patch"]
- apiGroups: ["storage.k8s.io"]
  resources: ["storageclasses"]
  verbs: ["get", "list", "watch"]
- apiGroups: [""]
  resources: ["events"]
  verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-nfs-client-provisioner
subjects:
- kind: ServiceAccount
  name: nfs-client-provisioner
  namespace: $namespaceDp
roleRef:
  kind: ClusterRole
  name: nfs-client-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
rules:
- apiGroups: [""]
  resources: ["endpoints"]
  verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
subjects:
- kind: ServiceAccount
  name: nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: $namespaceDp
roleRef:
  kind: Role
  name: leader-locking-nfs-client-provisioner
  apiGroup: rbac.authorization.k8s.io
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nfs-client-provisioner
---
kind: Deployment
apiVersion: apps/v1
metadata:
  name: nfs-client-provisioner
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: nfs-client-provisioner
  template:
    metadata:
      labels:
        app: nfs-client-provisioner
    spec:
      serviceAccount: nfs-client-provisioner
      containers:
        - name: nfs-client-provisioner
          image: k8s.gcr.io/sig-storage/nfs-subdir-external-provisioner:v4.0.2
          volumeMounts:
            - name: nfs-client-root
              mountPath: /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: k8s-sigs.io/nfs-subdir-external-provisioner
            - name: NFS_SERVER
              value: $nfsServer
            - name: NFS_PATH
              value: $nfsPath
      volumes:
        - name: nfs-client-root
          nfs:
            server: $nfsServer
            path: $nfsPath
---
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: managed-nfs-storage
provisioner: k8s-sigs.io/nfs-subdir-external-provisioner
parameters:
  archiveOnDelete: "false"
EOF
kubectl apply -f nacos-pvc.yaml 
```

### Ingress

```sh
# 定义域名,域名证书地址，证书命名空间，以及Ingress服务
# 生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainName="nacos.bigkang.club"
export domainPath="/root/k8s/tls"
export tlsNameSpace="default"
export ingressService="nacos-headless"
export ingressServicePort="8848"

# 前置准备删除原来的证书以及Ingress
kubectl delete secret $domainName-tls-secret 
kubectl delete ingress $domainName-ingress
kubectl delete secret $domainName-tls-secret --namespace=$tlsNameSpace
kubectl delete ingress $domainName-ingress --namespace=$tlsNameSpace

# 创建目录
mkdir -p $domainPath/$domainName && cd $domainPath/$domainName


# 初始化证书
# 生成私钥(KEY)
openssl genrsa -out $domainName.key 4096
openssl req -x509 -new -nodes -key $domainName.key -subj "/CN=$domainName" -days 36500 -out $domainName.crt
openssl req -new -sha256 \
    -key $domainName.key \
    -subj "/C=CN/ST=Beijing/L=Beijing/O=UnitedStack/OU=Devops/CN=$domainName" \
    -reqexts SAN \
    -config <(cat /etc/pki/tls/openssl.cnf \
        <(printf "[SAN]\nsubjectAltName=DNS:$domainName")) \
    -out $domainName.csr
openssl req -text -in $domainName.csr
openssl x509 -req -days 365000 \
    -in $domainName.csr -CA $domainName.crt -CAkey $domainName.key -CAcreateserial \
    -extfile <(printf "subjectAltName=DNS:$domainName") \
    -out $domainName.pem
    
# 创建tls证书
kubectl create secret tls $domainName-tls-secret --namespace=$tlsNameSpace --cert=$domainName.pem --key=$domainName.key --dry-run=client -o yaml > $domainName-secret.yaml
kubectl apply -f $domainName-secret.yaml

# 修改hosts 访问域名
echo "192.168.100.11 $domainName"

# 创建Ingress的yaml文件
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $tlsNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/test.example.com.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/test.example.com.error.log;
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF

# 启动Ingress
kubectl apply -f $domainName-ingress.yaml


# 访问如下
echo "https://$domainName/nacos/"
```

## 部署MySQL

​		初始化目录

```sh
# 定义参数
export name="mysql"
export deployPath="~/k8s/deploy/mysql"
export deployNamespace="default"
# 创建目录
mkdir -p $deployPath && cd $deployPath
```

​		创建PVC以及配置文件

```sh
# 定义NFS信息
export nfsHost="192.168.100.12"
export nfsPath="/data/nfs/mysql"
export nfsSize="20Gi"

# NFS新增挂载(修改网段)
echo "${nfsPath} 192.168.100.0/24(insecure,rw,sync,no_root_squash)" >> /etc/exports
# 重新加载NFS
systemctl reload nfs-server.service
showmount -e

# 创建pvc
cat >  ${name}-pvc.yaml  <<EOF
apiVersion: v1
kind: PersistentVolume
metadata:
  name: ${name}-pv
spec:
  capacity:
    storage: $nfsSize
  accessModes:
    - ReadWriteMany
  storageClassName: $name
  nfs:
    server: $nfsHost
    path: $nfsPath
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: ${name}-pvc
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: $nfsSize
  storageClassName: $name
EOF

# 应用pvc
kubectl apply -f ${name}-pvc.yaml
```

​		部署MySQL

```sh
# 定义参数,root密码，初始化用户密码，以及镜像,暴露的端口
export rootPass="bigkang"
export initUser="bigkang"
export initPass="bigkang"
export podImage="mysql:8.0.28"
export nodePort="13306"


cat >  ${name}-deploy.yaml  <<EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name: ${name}-conf
  namespace: $deployNamespace
data:
  mysql.cnf: |
    [mysqld]

     pid-file        = /var/run/mysqld/mysqld.pid
     socket          = /var/run/mysqld/mysqld.sock
     datadir         = /var/lib/mysql
     
     symbolic-links=0
     sql-mode=ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
     character-set-server=utf8
    [client]
     default-character-set=utf8
    [mysql]
     default-character-set=utf8
     
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${name}
  namespace: $deployNamespace
  labels:
    app: ${name}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ${name}
  template:
    metadata:
      labels:
        app: ${name}
    spec:
      volumes:
        - name: mysql-data
          persistentVolumeClaim:
            claimName: ${name}-pvc
        - name: mysql-conf
          configMap:
            name: ${name}-conf
      containers:
        - env:
            - name: MYSQL_ROOT_PASSWORD
              value: $rootPass
            - name: MYSQL_USER
              value: $initUser
            - name: MYSQL_PASSWORD
              value: $initPass
          image: $podImage
          imagePullPolicy: IfNotPresent
          name: ${name}
          ports:
            - containerPort: 3306
              protocol: TCP
              name: http
          volumeMounts:
            - name: mysql-data
              mountPath: /var/lib/mysql
            - name: mysql-conf
              mountPath: /etc/mysql/mysql.conf.d
---
apiVersion: v1
kind: Service
metadata:
  name: ${name}-svc
  namespace: $deployNamespace
spec:
  type: NodePort
  selector:
    app: ${name}
  ports:
    - port: 3306
      targetPort: 3306
      nodePort: $nodePort
EOF
```

​		使用Ingress

```sh
# 定义域名,域名证书地址，证书命名空间，以及Ingress服务
# 生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainName="mysql.bigkang.club"
export domainPath="/root/k8s/tls"
export tlsNameSpace="default"
export ingressService="mysql-svc"
export ingressServicePort="3306"

# 前置准备删除原来的证书以及Ingress
kubectl delete secret $domainName-tls-secret 
kubectl delete ingress $domainName-ingress
kubectl delete secret $domainName-tls-secret --namespace=$tlsNameSpace
kubectl delete ingress $domainName-ingress --namespace=$tlsNameSpace

# 创建目录
mkdir -p $domainPath/$domainName && cd $domainPath/$domainName


# 初始化证书
# 生成私钥(KEY)
openssl genrsa -out $domainName.key 4096
openssl req -x509 -new -nodes -key $domainName.key -subj "/CN=$domainName" -days 36500 -out $domainName.crt
openssl req -new -sha256 \
    -key $domainName.key \
    -subj "/C=CN/ST=Beijing/L=Beijing/O=UnitedStack/OU=Devops/CN=$domainName" \
    -reqexts SAN \
    -config <(cat /etc/pki/tls/openssl.cnf \
        <(printf "[SAN]\nsubjectAltName=DNS:$domainName")) \
    -out $domainName.csr
openssl req -text -in $domainName.csr
openssl x509 -req -days 365000 \
    -in $domainName.csr -CA $domainName.crt -CAkey $domainName.key -CAcreateserial \
    -extfile <(printf "subjectAltName=DNS:$domainName") \
    -out $domainName.pem
    
# 创建tls证书
kubectl create secret tls $domainName-tls-secret --namespace=$tlsNameSpace --cert=$domainName.pem --key=$domainName.key --dry-run=client -o yaml > $domainName-secret.yaml
kubectl apply -f $domainName-secret.yaml

# 修改hosts 访问域名
echo "192.168.100.11 $domainName"

# 创建Ingress的yaml文件
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $tlsNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/test.example.com.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/test.example.com.error.log;
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF

# 启动Ingress
kubectl apply -f $domainName-ingress.yaml


# 访问如下
echo "https://$domainName/nacos/"
```



## 部署TiDB



## 部署ELK

### 部署Es-Master节点

```sh
# 定义参数
export deployPath=~/k8s/deploy/elk/es
# 创建目录
mkdir -p $deployPath && cd $deployPath

# 定义参数，部署的名称，以及命名空间
export deployName="es-master"
export deployNameSpace="default"

# 创建一个rbac有权限StorageClass动态操作pv以及pvc（用于动态创建磁盘目录）
cat > ${deployName}-rbac.yaml << EOF
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: ${deployName}-storage
  namespace: $deployNameSpace
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
   name: ${deployName}-storage
   namespace: $deployNameSpace
rules:
   -  apiGroups: [""]
      resources: ["persistentvolumes"]
      verbs: ["get", "list", "watch", "create", "delete"]
   -  apiGroups: [""]
      resources: ["persistentvolumeclaims"]
      verbs: ["get", "list", "watch", "update", "delete"]
   -  apiGroups: ["storage.k8s.io"]
      resources: ["storageclasses"]
      verbs: ["get", "list", "watch"]
   -  apiGroups: [""]
      resources: ["events"]
      verbs: ["watch", "create", "update", "patch"]
   -  apiGroups: [""]
      resources: ["services", "endpoints"]
      verbs: ["get","create","list", "watch","update"]
   -  apiGroups: ["extensions"]
      resources: ["podsecuritypolicies"]
      resourceNames: ["${deployName}-storage"]
      verbs: ["use"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: ${deployName}-storage-bind
subjects:
  - kind: ServiceAccount
    name: ${deployName}-storage
    namespace: $deployNameSpace
roleRef:
  kind: ClusterRole
  name: ${deployName}-storage
  apiGroup: rbac.authorization.k8s.io
EOF

# 应用
kubectl apply -f ${deployName}-rbac.yaml

# 定义nfs的地址以及Path，以及镜像（不同版本的k8s对应不同的镜像）
export nfsHost="192.168.100.13"
export nfsPath="/data/nfs/es"
export deployImage="easzlab/nfs-subdir-external-provisioner:v4.0.1"
# 创建一个nfs-provisioner动态操作pv以及pvc（用于动态创建磁盘目录）
cat > ${deployName}-storage.yaml << EOF
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: ${deployName}-storage
  namespace: $deployNameSpace
provisioner: ${deployName}/nfs
parameters:
  archiveOnDelete: "true"
reclaimPolicy: Retain
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${deployName}-provisioner
  namespace: $deployNameSpace
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ${deployName}-provisioner
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: ${deployName}-provisioner
    spec:
      serviceAccount: ${deployName}-storage
      containers:
        - name: ${deployName}-provisioner
          image: $deployImage
          imagePullPolicy: IfNotPresent
          volumeMounts:
            - name: nfs-client-root
              mountPath:  /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: ${deployName}/nfs
            - name: NFS_SERVER
              value: $nfsHost
            - name: NFS_PATH
              value: $nfsPath
      volumes:
        - name: nfs-client-root
          nfs:
            server: $nfsHost
            path: $nfsPath
EOF
# 应用
kubectl apply -f ${deployName}-storage.yaml

# 定义Service参数
export svcName="es-svc"
export app="es7"

# 创建Service(注意NodePort是否需要以及后期修改等等情况)
cat > $svcName.yaml << EOF
apiVersion: v1
kind: Service
metadata:
  name: $svcName
  namespace: $deployNameSpace
  labels:
    app: $app
spec:
  type: NodePort
  ports:
  - port: 9200
    targetPort: 9200
    name: http
    nodePort: 9200
  - port: 9300
    targetPort: 9300
    name: tcp
    nodePort: 9300
  selector:
    app: $app
EOF
# 然后应用
kubectl apply -f  $svcName.yaml



# 定义deploy参数
export deployImage="elasticsearch:7.17.0"
export app="es7"
export clusterName="${app}-cluster"
# 初始化节点信息，用于集群第一次启动的初始化
# 名称取 ${deployName}-[0-n]节点数.${svcName} 下面以三个master节点示例
# 示例  "es-master-0.es-master-svc,es-master-1.es-master-svc,es-master-2.es-master-svc"
export masterNodes="${deployName}-0.${svcName},${deployName}-1.${svcName},${deployName}-2.${svcName}"

# 集群节点发现，所有的节点都可以放进去，第一次就把所有的Master放进去后面可以自己修改
export seedHosts=$masterNodes

# 创建应用
# !!! 注意 ${HOSTNAME} 需要改回去，不需要引用，否则会变成主机名 ${HOSTNAME}.es-svc
# !!! affinity 如果机器不够6台则会启动失败亲和度问题，每台只能部署一个节点，删除掉该节点即可
cat > ${deployName}-deploy.yaml << EOF
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: ${deployName}
  namespace: $deployNameSpace
spec:
  serviceName: $svcName
  replicas: 3
  selector:
    matchLabels:
      app: $app
  template:
    metadata:
      labels:
        app: $app
    spec:
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            - labelSelector:
                matchExpressions:
                  - key: "app"
                    operator: In
                    values:
                      - $app
              topologyKey: "kubernetes.io/hostname"
      initContainers:
        - name: init-sysctl
          image: busybox
          command:
            - sysctl
            - '-w'
            - vm.max_map_count=262144
          imagePullPolicy: IfNotPresent
          securityContext:
            privileged: true
      containers:
        - name: ${deployName}
          image: ${deployImage}
          imagePullPolicy: IfNotPresent
          resources:
            requests:
              memory: 256Mi
              cpu: 128m
            limits:
              memory: 512Mi
              cpu: 256m
          env:
            - name: node.name
              value: "${HOSTNAME}.${svcName}"
            - name: cluster.name
              value: $clusterName
            - name: cluster.initial_master_nodes
              value: $masterNodes
            - name: discovery.seed_hosts
              value: $seedHosts
            - name: ES_JAVA_OPTS
              value: '-Xms128m -Xmx128m'
            - name: node.master
              value: 'true'
            - name: node.data
              value: 'true'
            - name: network.host
              value: '0.0.0.0'
          volumeMounts:
            - name: es-date
              mountPath: /usr/share/elasticsearch/data
  volumeClaimTemplates:
  - metadata:
      name: es-date
    spec:
      accessModes:
        - ReadWriteMany
      storageClassName: ${deployName}-storage
      resources:
        requests:
          storage: 10Gi
EOF

kubectl apply -f ${deployName}-deploy.yaml
```

### 部署Es-Data

```sh
# 定义参数
export deployPath=~/k8s/deploy/elk/es
# 创建目录
mkdir -p $deployPath && cd $deployPath

# 定义参数，部署的名称，以及命名空间
export deployName="es-data"
export deployNameSpace="default"

# 创建一个rbac有权限StorageClass动态操作pv以及pvc（用于动态创建磁盘目录）
cat > ${deployName}-rbac.yaml << EOF
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: ${deployName}-storage
  namespace: $deployNameSpace
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
   name: ${deployName}-storage
   namespace: $deployNameSpace
rules:
   -  apiGroups: [""]
      resources: ["persistentvolumes"]
      verbs: ["get", "list", "watch", "create", "delete"]
   -  apiGroups: [""]
      resources: ["persistentvolumeclaims"]
      verbs: ["get", "list", "watch", "update", "delete"]
   -  apiGroups: ["storage.k8s.io"]
      resources: ["storageclasses"]
      verbs: ["get", "list", "watch"]
   -  apiGroups: [""]
      resources: ["events"]
      verbs: ["watch", "create", "update", "patch"]
   -  apiGroups: [""]
      resources: ["services", "endpoints"]
      verbs: ["get","create","list", "watch","update"]
   -  apiGroups: ["extensions"]
      resources: ["podsecuritypolicies"]
      resourceNames: ["${deployName}-storage"]
      verbs: ["use"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: ${deployName}-storage-bind
subjects:
  - kind: ServiceAccount
    name: ${deployName}-storage
    namespace: $deployNameSpace
roleRef:
  kind: ClusterRole
  name: ${deployName}-storage
  apiGroup: rbac.authorization.k8s.io
EOF

# 应用
kubectl apply -f ${deployName}-rbac.yaml

# 定义nfs的地址以及Path，以及镜像（不同版本的k8s对应不同的镜像）
export nfsHost="192.168.100.12"
export nfsPath="/data/nfs/es"
export deployImage="easzlab/nfs-subdir-external-provisioner:v4.0.1"
# 创建一个nfs-provisioner动态操作pv以及pvc（用于动态创建磁盘目录）
cat > ${deployName}-storage.yaml << EOF
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: ${deployName}-storage
  namespace: $deployNameSpace
provisioner: ${deployName}/nfs
parameters:
  archiveOnDelete: "true"
reclaimPolicy: Retain
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${deployName}-provisioner
  namespace: $deployNameSpace
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ${deployName}-provisioner
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: ${deployName}-provisioner
    spec:
      serviceAccount: ${deployName}-storage
      containers:
        - name: ${deployName}-provisioner
          image: $deployImage
          imagePullPolicy: IfNotPresent
          volumeMounts:
            - name: nfs-client-root
              mountPath:  /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: ${deployName}/nfs
            - name: NFS_SERVER
              value: $nfsHost
            - name: NFS_PATH
              value: $nfsPath
      volumes:
        - name: nfs-client-root
          nfs:
            server: $nfsHost
            path: $nfsPath
EOF
# 应用
kubectl apply -f ${deployName}-storage.yaml

# 部署Es-Data,这里需要设置Master的部署名称
# 定义deploy参数
export deployImage="elasticsearch:7.17.0"
export app="es7"
export clusterName="${app}-cluster"
export masterDeployName="es-master"
# 初始化节点信息，用于集群第一次启动的初始化
# 名称取 ${deployName}-[0-n]节点数.${svcName} 下面以三个master节点示例
# 示例  "es-master-0.es-master-svc,es-master-1.es-master-svc,es-master-2.es-master-svc"
export masterNodes="${masterDeployName}-0.${svcName},${masterDeployName}-1.${svcName},${masterDeployName}-2.${svcName}"

# 集群节点发现，所有的节点都可以放进去，第一次就把所有的Master放进去后面可以自己修改
export seedHosts=$masterNodes

# 创建应用
# !!! 注意 ${HOSTNAME} 需要改回去，不需要引用，否则会变成主机名 ${HOSTNAME}.es-svc
# !!! affinity 如果机器不够6台则会启动失败亲和度问题，每台只能部署一个节点，删除掉该节点即可
cat > ${deployName}-deploy.yaml << EOF
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: ${deployName}
  namespace: $deployNameSpace
spec:
  serviceName: $svcName
  replicas: 3
  selector:
    matchLabels:
      app: $app
  template:
    metadata:
      labels:
        app: $app
    spec:
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            - labelSelector:
                matchExpressions:
                  - key: "app"
                    operator: In
                    values:
                      - $app
              topologyKey: "kubernetes.io/hostname"
      initContainers:
        - name: init-sysctl
          image: busybox
          command:
            - sysctl
            - '-w'
            - vm.max_map_count=262144
          imagePullPolicy: IfNotPresent
          securityContext:
            privileged: true
      containers:
        - name: ${deployName}
          image: ${deployImage}
          imagePullPolicy: IfNotPresent
          resources:
            requests:
              memory: 256Mi
              cpu: 128m
            limits:
              memory: 512Mi
              cpu: 256m
          env:
            - name: node.name
              value: "${HOSTNAME}.${svcName}"
            - name: cluster.name
              value: $clusterName
            - name: cluster.initial_master_nodes
              value: $masterNodes
            - name: discovery.seed_hosts
              value: $seedHosts
            - name: ES_JAVA_OPTS
              value: '-Xms128m -Xmx128m'
            - name: node.master
              value: 'true'
            - name: node.data
              value: 'true'
            - name: network.host
              value: '0.0.0.0'
          volumeMounts:
            - name: es-date
              mountPath: /usr/share/elasticsearch/data
  volumeClaimTemplates:
  - metadata:
      name: es-date
    spec:
      accessModes:
        - ReadWriteMany
      storageClassName: ${deployName}-storage
      resources:
        requests:
          storage: 10Gi
EOF

kubectl apply -f ${deployName}-deploy.yaml
```

### 部署Kibana

```sh
# 定义参数，部署的名称，以及命名空间
export deployName="kibana"
export deployNameSpace="default"
export deployImage="kibana:7.17.0"
# 可以直接指定es的Service地址
export esHost="http://es-svc:9200"


cat > ${deployName}-deploy.yaml << EOF
apiVersion: v1
kind: Service
metadata:
  name: $deployName
  namespace: $deployNameSpace
  labels:
    app: kibana
spec:
  type: NodePort
  ports:
  - port: 5601
    targetPort: 5601
    name: http
    nodePort: 5601
  type: NodePort
  selector:
    app: kibana
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $deployName
  namespace: $deployNameSpace
  labels:
    app: kibana
spec:
  selector:
    matchLabels:
      app: kibana
  template:
    metadata:
      labels:
        app: kibana
    spec:
      containers:
      - name: kibana
        image: $deployImage
        resources:
          limits:
            cpu: 256m
          requests:
            cpu: 256m
        env:
        - name: ELASTICSEARCH_HOSTS
          value: $esHost
        ports:
        - containerPort: 5601
EOF
```

### ELK-Ingress

```sh
# 配置参数
export domainName="elk.bigkang.club"
export domainPath=~/root/k8s/tls
export tlsNameSpace="default"
export ingressService="kibana"
export ingressServicePort="5601"


# 前置准备删除原来的证书以及Ingress
kubectl delete secret $domainName-tls-secret 
kubectl delete ingress $domainName-ingress
kubectl delete secret $domainName-tls-secret --namespace=$tlsNameSpace
kubectl delete ingress $domainName-ingress --namespace=$tlsNameSpace

# 使用什么方式生成SSL证书


# 创建tls证书
kubectl create secret tls $domainName-tls-secret --namespace=$tlsNameSpace --cert=$domainName.pem --key=$domainName.key --dry-run=client -o yaml > $domainName-secret.yaml

kubectl apply -f $domainName-secret.yaml


# 创建Ingress的yaml文件
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $tlsNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/test.example.com.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/test.example.com.error.log;
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF

# 启动Ingress
kubectl apply -f $domainName-ingress.yaml
```

## 安装Helm

​		官网地址：[点击进入](https://helm.sh/)

​		什么是Helm？

​		官网描述		**Helm The package manager for Kubernetes**

​								Helm 是Kubernetes的包管理器

​		Helm是找到、共享和使用为Kubernetes开发的软件的最佳途径。

​		

```sh
# 定义Helm参数
export helmPath="/root/helm"
export helmVersion="3.7.2"

# 创建安装目录
mkdir -p $helmPath && cd $helmPath

# 安装Helm
wget https://get.helm.sh/helm-v"$helmVersion"-linux-amd64.tar.gz -O $helmPath/helm-$helmVersion.tar.gz
# 解压
tar -zxvf helm-$helmVersion.tar.gz
# 复制到系统命令
cp linux-amd64/helm /usr/local/bin/


# 查看版本
helm version
```

## 安装Harbor镜像私服

```bash
# 定义Helm参数
export harborPath="/root/helm/deploy/harbor"
mkdir -p $harborPath && cd $harborPath



# helm安装Harbor
helm repo add harbor https://helm.goharbor.io
helm fetch harbor/harbor --untar


# 新建StorageClass RBAC
cat > $domainName-ingress.yaml << EOF
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-client-provisioner-runner
rules:
- apiGroups: [""]
  resources: ["persistentvolumes"]
  verbs: ["get", "list", "watch", "create", "delete"]
- apiGroups: [""]
  resources: ["persistentvolumeclaims"]
  verbs: ["get", "list", "watch", "update"]
- apiGroups: [""]
  resources: ["endpoints"]
  verbs: ["get", "list", "watch", "create", "update", "patch"]
- apiGroups: ["storage.k8s.io"]
  resources: ["storageclasses"]
  verbs: ["get", "list", "watch"]
- apiGroups: [""]
  resources: ["events"]
  verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-nfs-client-provisioner
subjects:
- kind: ServiceAccount
  name: nfs-client-provisioner
  namespace: default
roleRef:
  kind: ClusterRole
  name: nfs-client-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
rules:
- apiGroups: [""]
  resources: ["endpoints"]
  verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-client-provisioner
subjects:
- kind: ServiceAccount
  name: nfs-client-provisioner
  # replace with namespace where provisioner is deployed
  namespace: default
roleRef:
  kind: Role
  name: leader-locking-nfs-client-provisioner
  apiGroup: rbac.authorization.k8s.io
EOF
```

## 安装Harbor镜像私服

```sh
# 安装Docker-Compose(可以自行安装或者下载可能比较慢并且注意Docker版本)
curl -L https://github.91chifun.workers.dev//https://github.com/docker/compose/releases/download/1.27.4/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
docker-compose version

# 指定Harbor下载目录
export harborPath="/data/harbor"
mkdir -p $harborPath && cd $harborPath

# 下载解压Harbor
wget https://github.91chifun.workers.dev//https://github.com/goharbor/harbor/releases/download/v2.1.1/harbor-online-installer-v2.1.1.tgz -O harbor-online-installer.tgz
tar -zxvf harbor-online-installer.tgz

# 创建证书
# 定义域名,生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainNamePath="/root/k8s/tls"
export domainName="harbor.bigkang.club"
mkdir -p $domainNamePath/$domainName && cd $domainNamePath/$domainName
openssl genrsa -out $domainName.key
openssl req -new -sha256 -key $domainName.key -out $domainName.csr -subj "/C=CN/ST=sichuan/L=dazhou/O=bigkang/OU=kaifa/CN=$domainName"
openssl x509 -req -days 3650 -sha1 -extensions v3_ca -signkey $domainName.key -in $domainName.csr -out $domainName.crt
openssl x509 -in $domainName.crt -out $domainName.pem -outform PEM

# 复制配置文件
cd $harborPath/harbor
cp harbor.yml.tmpl harbor.yml

# 修改域名映射
sed -i "s#hostname: reg.mydomain.com#hostname: $domainName#g" harbor.yml

# 修改证书地址
sed -i "s#certificate: /your/certificate/path#certificate: $domainNamePath/$domainName/$domainName.crt#g" harbor.yml
sed -i "s#private_key: /your/private/key/path#private_key: $domainNamePath/$domainName/$domainName.key#g" harbor.yml

# 修改密码
export harborPassword="bigkang"
sed -i "s#Harbor12345#$harborPassword#g" harbor.yml

# 设置挂载的Harbor的Data盘
export harborDataPath="/data/harbor/data"
mkdir -p $harborDataPath
sed -i "s#data_volume: /data#data_volume: $harborDataPath#g" harbor.yml

# 设置日志盘
export harborLogPath="/data/harbor/log"
mkdir -p $harborLogPath
sed -i "s#/var/log/harbor#$harborLogPath#g" harbor.yml
```



​		修改配置文件

```sh
cp harbor.yml.tmpl harbor.yml
vim harbor.yml
```

​		修改如下：

```properties
# hostNmae ip或者域名
hostname: hub.bigkang.k8s

# http相关配置
http:
  # http的端口，默认为80。如果启用了https，则此端口将重定向到https端口
  port: 80
# http相关配置
https:
  # 港口的https端口，默认为443
  port: 443
  # Nginx的证书和密钥文件的路径
  certificate: /root/ca/crt/harbor.crt
  private_key: /root/ca/key/harbor.key
# 挂载磁盘路径
data_volume: /data/harbor/data
# 管理员用户的密码
harbor_admin_password: bigkang123
```

​		然后我们去创建证书

```sh
# 创建证书

# 定义域名,生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainNamePath="/root/k8s/tls"
export domainName="harbor.bigkang.club"
mkdir -p $domainNamePath/$domainName && cd $domainNamePath/$domainName
openssl genrsa -out $domainName.key
openssl req -new -sha256 -key $domainName.key -out $domainName.csr -subj "/C=CN/ST=sichuan/L=dazhou/O=bigkang/OU=kaifa/CN=$domainName"
openssl x509 -req -days 3650 -sha1 -extensions v3_ca -signkey $domainName.key -in $domainName.csr -out $domainName.crt
openssl x509 -in $domainName.crt -out $domainName.pem -outform PEM
```

​		然后启动

```sh
./install.sh
```

​		然后访问即可

​		再修改Host映射以及Docker配置地址即可

```properties
vim  /etc/docker/daemon.json 
{
		***,
		***,
		"insecure-registries" : ["hub.bigkang.k8s"]
}
```

​		修改host

```sh
echo "192.168.1.115 hub.bigkang.k8s" >> /etc/hosts
```

​		重启Docker

```sh
systemctl restart docker
```

​		然后测试登录

```
docker login https://hub.bigkang.k8s
```

​		输入用户名密码即可

​		重新标记版本

```
docker tag nginx hub.bigkang.club/library/nginx
docker push hub.bigkang.club/library/nginx
```

​		然后去harbor查看即可

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1605330589578.png)

​		





## 安装kubesphere

​		官网地址：[点击进入](https://kubesphere.io/docs/installing-on-kubernetes/on-prem-kubernetes/install-ks-on-linux-airgapped/)

​	

```sh
cd
mkdir kubesphere && cd kubesphere
curl -L -O https://github.com/kubesphere/ks-installer/releases/download/v3.0.0/images-list.txt
curl -L -O https://github.com/kubesphere/ks-installer/releases/download/v3.0.0/offline-installation-tool.sh

chmod +x offline-installation-tool.sh
./offline-installation-tool.sh -h
./offline-installation-tool.sh -s -l images-list.txt -d ./kubesphere-images
```



```

```



```sh
kubectl replace --force -f https://github.com/kubesphere/ks-installer/releases/download/v3.0.0/kubesphere-installer.yaml
```



```
kubectl replace --force -f https://github.com/kubesphere/ks-installer/releases/download/v3.0.0/cluster-configuration.yaml
```

## 安装Netdata



```sh
#  /root/deploy/netdata

mkdir -p ~/deploy/netdata && cd ~/deploy/netdata 

helm repo add netdata https://netdata.github.io/helmchart/

helm install netdata netdata/netdata

# 暴露端口
kubectl expose  deployment netdata-parent --type="NodePort" --port 19999



apiVersion: v1
kind: Pod
metadata:
  name: test-pd
spec:
  containers:
  - image: k8s.gcr.io/test-webserver
    name: test-container
    volumeMounts:
    - mountPath: /cache
      name: cache-volume
  volumes:
  - name: cache-volume
    emptyDir: {}
```



## 部署SpringBoot项目

### 代码

​		我们创建一个最简单的SpringBoot项目

​		application.yaml

```yaml

# 随机生成主机NodeId，确定每次访问的都不一样
node:
  id: ${random.int(0,800)}
server:
  port: 8080

```

​		controller打印nodeid以及主机名和Ip

```java
package com.kang.test.k8s.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author BigKang
 * @Date 2020/11/19 4:42 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 测试k8s控制器
 */

@RestController
public class TestK8sController {
    
    @Value("${node.id}")
    public Integer nodeId;

    @RequestMapping("/")
    public Map index() throws UnknownHostException {
        Map<String, Object> map = new HashMap<>();
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        String hostAddress = localHost.getHostAddress();
        map.put("主机名",hostName);
        map.put("主机地址",hostAddress);
        map.put("NodeId",nodeId);
        return map;
    }

}

```

### 构建镜像(可以直接从Github下载)

​		进入115，yunyao3节点

​		拉取代码

```sh
cd ~ && mkdir boot-k8s && cd  boot-k8s
git clone https://github.com/YellowKang/boot-k8s.git
```

​		构建镜像

```sh
cd boot-k8s
docker build  -t hub.bigkang.k8s/library/boot-k8s .
```

​		上传私服

```sh
# 登录私服,输入用户名密码
docker login https://hub.bigkang.k8s
# push镜像
docker push hub.bigkang.k8s/library/boot-k8s
```

### 创建k8s启动文件（可以使用打包好的镜像）

​		如需使用公共镜像直接从hub拉取即可

```sh
# 修改image标签如下，已经上传hub公共仓库
   spec:
     containers:
     - name: boot-k8s
       image: registry.cn-shanghai.aliyuncs.com/bigkang/boot-k8s
```

​		生成文件

```sh
mkdir ~/boot-k8s && cd ~/boot-k8s && touch boot-k8s.yaml
```

​		创建Pod配置文件写入

```sh
echo "apiVersion: apps/v1
kind: Deployment
metadata:
  name: boot-k8s-deploy
  namespace: default
spec:
  replicas: 3
  selector:
   matchLabels:
     app: boot-k8s
     release: canary
  template:
   metadata:
     labels:
       app: boot-k8s
       release: canary
   spec:
     containers:
     - name: boot-k8s
       image: hub.bigkang.k8s/library/boot-k8s
       ports:
       - name: http
         containerPort: 8080" > boot-k8s.yaml
```

​		追加Service

```sh
echo "
---
apiVersion: v1
kind: Service
metadata:
  name: boot-k8s-service
  namespace: default
spec:
  selector:
   app: boot-k8s
   release: canary
  ports:
  - name: http
    targetPort: 8080
    port: 8080" >> boot-k8s.yaml
```

​		追加Ingress

```sh
echo "
---
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress-boot-k8s
  annotations:
    kubernets.io/ingress.class: \"nginx\"
spec:
  tls:
  - hosts: 
    - boot.bigkang.k8s
    secretName: custom-tls-secret
  rules:
  - host: boot.bigkang.k8s
    http:
      paths:
      - path:
        backend:
          serviceName: boot-k8s-service
          servicePort: 8080" >> boot-k8s.yaml
```

​		应用

```sh
kubectl apply -f  boot-k8s.yaml
```

​		查看pod，检查是否Running

```sh
kubectl get pods -o wide | grep boot-k8s
```

​		查看service

```sh
kubectl get service -o wide | grep boot-k8s
```

​		查看Ingress

```sh
kubectl get ingress -o wide | grep boot-k8s
```

​		启动后添加host映射然后访问即可

```sh
192.168.1.12 boot.bigkang.k8s
```

​		https://boot.bigkang.k8s

​		我们一直访问会出现多个不同的信息，默认采用轮询

​		然后我们查看pod

```sh
# 查看pod
kubectl get pods -o wide | grep boot-k8s
# 返回如下3个Pod
boot-k8s-deploy-f86cd775f-dp8cb   1/1     Running   0          15h   10.244.1.13   yunyao2   <none>           <none>
boot-k8s-deploy-f86cd775f-hkzl9   1/1     Running   0          15h   10.244.2.5    yunyao3   <none>           <none>
boot-k8s-deploy-f86cd775f-rlrlz   1/1     Running   0          15h   10.244.1.12   yunyao2   <none>           <none>

# 查看deployment
kubectl  get deployment 
# 返回如下
NAME              READY   UP-TO-DATE   AVAILABLE   AGE
boot-k8s-deploy   3/3     3            3           15h
tomcat-deploy     1/1     1            1           19h

# 我们动态进行扩容,扩容至5个pod
kubectl scale deployment boot-k8s-deploy --replicas 5

# 再次查看Pod
kubectl get pods -o wide | grep boot-k8s

# 返回如下，发现扩容至5个pod实例
boot-k8s-deploy-f86cd775f-dp8cb   1/1     Running   0          15h   10.244.1.13   yunyao2   <none>           <none>
boot-k8s-deploy-f86cd775f-hkzl9   1/1     Running   0          15h   10.244.2.5    yunyao3   <none>           <none>
boot-k8s-deploy-f86cd775f-k4psx   1/1     Running   0          25s   10.244.2.6    yunyao3   <none>           <none>
boot-k8s-deploy-f86cd775f-nhv6j   1/1     Running   0          25s   10.244.1.14   yunyao2   <none>           <none>
boot-k8s-deploy-f86cd775f-rlrlz   1/1     Running   0          15h   10.244.1.12   yunyao2   <none>           <none>

# 我们再进行访问	发现可以负载到5个Pod中
# 缩容回1个实例
kubectl scale deployment boot-k8s-deploy --replicas 1

# 再次查看
kubectl get pods -o wide | grep boot-k8s
# 返回如下，发现其他容器都被终止只有一个运行
boot-k8s-deploy-f86cd775f-dp8cb   1/1     Terminating   0          15h     10.244.1.13   yunyao2   <none>           <none>
boot-k8s-deploy-f86cd775f-hkzl9   1/1     Running       0          15h     10.244.2.5    yunyao3   <none>           <none>
boot-k8s-deploy-f86cd775f-k4psx   1/1     Terminating   0          2m35s   10.244.2.6    yunyao3   <none>           <none>
boot-k8s-deploy-f86cd775f-nhv6j   1/1     Terminating   0          2m35s   10.244.1.14   yunyao2   <none>           <none>
boot-k8s-deploy-f86cd775f-rlrlz   1/1     Terminating   0          15h     10.244.1.12   yunyao2   <none>           <none>
# 稍等一会后发现只有一个pod了
```

# 辅助

## 快速部署网站

​		https://www.kubebiz.com/KubeBiz

## 创建TLS证书

​		使用openssl创建

```sh
# 定义域名,域名证书地址，证书命名空间，以及Ingress服务
# 生成证书 -subj 【ST（城市）L（地区）O（组织名）OU（组织单位）CN（域名）】
export domainName="tomcat.bigkang.club"
export domainPath="/root/k8s/tls"
export tlsNameSpace="default"
export ingressService="tomcat"
export ingressServicePort="8080"

# 前置准备删除原来的证书以及Ingress
kubectl delete secret $domainName-tls-secret 
kubectl delete ingress $domainName-ingress
kubectl delete secret $domainName-tls-secret --namespace=$tlsNameSpace
kubectl delete ingress $domainName-ingress --namespace=$tlsNameSpace

# 创建目录
mkdir -p $domainPath/$domainName && cd $domainPath/$domainName

# 初始化证书
# 生成私钥(KEY)
openssl genrsa -out $domainName.key 4096
openssl req -x509 -new -nodes -key $domainName.key -subj "/CN=$domainName" -days 36500 -out $domainName.crt
openssl req -new -sha256 \
    -key $domainName.key \
    -subj "/C=CN/ST=Beijing/L=Beijing/O=UnitedStack/OU=Devops/CN=$domainName" \
    -reqexts SAN \
    -config <(cat /etc/pki/tls/openssl.cnf \
        <(printf "[SAN]\nsubjectAltName=DNS:$domainName")) \
    -out $domainName.csr
openssl req -text -in $domainName.csr
openssl x509 -req -days 365000 \
    -in $domainName.csr -CA $domainName.crt -CAkey $domainName.key -CAcreateserial \
    -extfile <(printf "subjectAltName=DNS:$domainName") \
    -out $domainName.pem
    
# 创建tls证书
kubectl create secret tls $domainName-tls-secret --namespace=$tlsNameSpace --cert=$domainName.pem --key=$domainName.key --dry-run=client -o yaml > $domainName-secret.yaml
kubectl apply -f $domainName-secret.yaml

# 修改hosts 访问域名
echo "192.168.100.11 $domainName"

# 创建Ingress的yaml文件
cat > $domainName-ingress.yaml << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: $domainName-ingress
  namespace: $tlsNameSpace
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/secure-backends: "true"
    nginx.ingress.kubernetes.io/enable-access-log: "true"
    nginx.ingress.kubernetes.io/configuration-snippet: |
       access_log /var/log/nginx/test.example.com.access.log upstreaminfo if=$loggable;
       error_log  /var/log/nginx/test.example.com.error.log;
spec:
  tls:
    - hosts:
      - $domainName
      secretName: $domainName-tls-secret
  ingressClassName: nginx
  rules:
    - host: $domainName
      http:
        paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: $ingressService
              port:
                number: $ingressServicePort
EOF

# 启动Ingress
kubectl apply -f $domainName-ingress.yaml
```

## 删除加入节点

​		找到Master服务器在上面执行

```sh
# 删除老节点
kubectl delete node1

# 创建永久token
kubeadm token create --ttl 0 --print-join-command
`kubeadm join 192.168.100.11:6443 --token rpi151.qx3660ytx2ixq8jk     --discovery-token-ca-cert-hash sha256:5cf4e801c903257b50523af245f2af16a88e78dc00be3f2acc154491ad4f32a4`

# 新节点加入
kubeadm join 192.168.100.11:6443 --token rpi151.qx3660ytx2ixq8jk     --discovery-token-ca-cert-hash sha256:5cf4e801c903257b50523af245f2af16a88e78dc00be3f2acc154491ad4f32a4
```

## NFS安装部署

```sh
# 安装NFS
yum install -y nfs-utils
yum install -y rpcbind

# 开机自启
systemctl enable nfs-server.service
# 设置NFS
vim /etc/exports
# 写入如下
# /data/nfs/nacos *(insecure,rw,sync,no_root_squash) (不限制网段)
/data/nfs/nacos 192.168.100.0/24(insecure,rw,sync,no_root_squash)

# 参数详解
#   ro #只读共享
#   rw #读写共享
#   sync #同步写操作
#   async #异步写操作
#   wdelay #延迟写操作
#   root_squash #屏蔽远程root权限
#   no_root_squash #不屏蔽远程root权限
#   all_squash #屏蔽所有远程用户的权限
#   no_subtree_check #此选项可防止子树检查

# 创建目录
mkdir -p /data/nfs/nacos

# 启动NFS服务
systemctl start nfs-server.service
systemctl start rpcbind

# 查看共享的目录
showmount -e
# 返回如下即可
# Export list for qingyun01:
# /data/nfs/nacos 192.168.100.0/24

# 修改NFS的网络配置
sed -i "s#172.17.79.3#192.168.100.11#g"  ./deploy/nfs/deployment.yaml
sed -i "s#/data/nfs-share#/data/nfs/nacos#g"  ./deploy/nfs/deployment.yaml

# 子节点安装NFS
yum install -y nfs-utils
```

## 内部服务转发端口

​		将K8s的服务转发到本地端口

```bash
kubectl port-forward -n test service/upms-test 8083:80
```

# 问题排查

## 访问端口超时卡死

​		本地部署k8s后访问nodePort使用curl等一直连接超时，无论什么原因都无法访问，telnet端口无限卡死

```sh
# 查看所有Pod
kubectl get pods -A

NAMESPACE              NAME                                        READY   STATUS    RESTARTS      AGE
default                cirros-28920                                1/1     Running   0             36m
default                demo-654c477f6d-x79bb                       1/1     Running   0             33m
kube-system            calico-kube-controllers-6b9fbfff44-qxk8b    1/1     Running   0             4h13m
kube-system            calico-node-fppj8                           0/1     Running   1 (21m ago)   39m
kube-system            calico-node-n25rw                           1/1     Running   0             4h13m

# 发现POD calico-kube 重启并且没有就绪
# 查看容器日志
kubectl logs -f --tail 100 calico-node-fppj8 -n kube-system

# 发现如下
2021-12-16 13:52:59.237 [INFO][64] monitor-addresses/startup.go 713: Using autodetected IPv4 address on interface br-20b81ecbf803: 172.18.0.1/16
2021-12-16 13:53:57.952 [INFO][67] felix/summary.go 100: Summarising 11 dataplane reconciliation loops over 1m2.6s: avg=4ms longest=6ms ()
2021-12-16 13:53:59.239 [INFO][64] monitor-addresses/startup.go 713: Using autodetected IPv4 address on interface br-20b81ecbf803: 172.18.0.1/16

# 使用接口br-20b81ecbf803上的自动检测IPv4地址:172.18.0.1/16
# 定位到是网络ipv4的问题,发现是个网桥应该是某个容器的网桥
ifconfig | grep br-20b81ecbf803

# 查询Docker网络
docker network ls

# 发现如下（harbor占用了）
NETWORK ID     NAME            DRIVER    SCOPE
43a365c1a427   bridge          bridge    local
20b81ecbf803   harbor_harbor   bridge    local
83d930364997   host            host      local
e78673506809   none            null      local

# 我们找到Harbor的启动目录
cd XXX/harbor
# 停止Harbor容器，并且删除网络
docker-compose stop
docker network rm 20b81ecbf803


# 删除Pod重新启动
kubectl delete pod calico-node-fppj8 -n kube-system


# 重新安装启动Harbor
./install.sh
```

## 集群健康检查



```
kubectl get cs

scheduler Unhealthy Get “http://127.0.0.1:10251/healthz“: dial tcp 127.0.0.1:10251: con

解决方法：
cd /etc/kubernetes/manifests
然后将你的scheduler以及controll manager .yaml中都port=0注释掉

 containers:
  - command:
    - kube-scheduler
    - --authentication-kubeconfig=/etc/kubernetes/scheduler.conf
    - --authorization-kubeconfig=/etc/kubernetes/scheduler.conf
    - --bind-address=127.0.0.1
    - --kubeconfig=/etc/kubernetes/scheduler.conf
    - --leader-elect=true
#    - --port=0
    image: k8s.gcr.io/kube-sc

kubectl cluster-info


```



# kubeadmin重新初始化

​		删除旧文件

```sh
kubeadm reset
rm -rf /etc/kubernetes/manifests
systemctl stop kubelet 
rm -rf /var/lib/etcd/*
```

​		重新初始化

```sh
 kubeadm init \
  --apiserver-advertise-address=192.168.1.12 \
  --image-repository registry.aliyuncs.com/google_containers \
  --kubernetes-version v1.18.0 \
  --service-cidr=10.1.0.0/16 \
  --pod-network-cidr=10.244.0.0/1
```

# K8s卸载

​		卸载后重装

```sh
kubeadm reset -f
rpm -qa|grep kube*|xargs rpm --nodeps -e
modprobe -r ipip
lsmod
rm -rf ~/.kube/
rm -rf /etc/kubernetes/
rm -rf /etc/systemd/system/kubelet.service.d
rm -rf /etc/systemd/system/kubelet.service
rm -rf /usr/bin/kube*
rm -rf /etc/cni
rm -rf /opt/cni
rm -rf /var/lib/etcd
rm -rf /var/etcd

docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh

docker ps -a| grep google_containers | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep google_containers | grep -v grep| awk '{print "docker rm "$1}'|sh
docker ps -a| grep k8s_ | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep k8s_ | grep -v grep| awk '{print "docker rm "$1}'|sh

docker images | grep google_containers |xargs docker rmi -f


docker images | grep pause  |xargs docker rmi -f
docker images | grep coredns |xargs docker rmi -f
docker images | grep etcd |xargs docker rmi -f
```

```
# 不删除镜像
kubeadm reset -f
modprobe -r ipip
lsmod
systemctl stop kubelet 
rm -rf ~/.kube/
rm -rf /etc/kubernetes/
rm -rf /etc/cni
rm -rf /opt/cni
rm -rf /var/lib/etcd
rm -rf /var/etcd

docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep google_containers | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep k8s_ | grep -v grep| awk '{print "docker stop "$1}'|sh

docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh
docker ps -a| grep google_containers | grep -v grep| awk '{print "docker rm "$1}'|sh
docker ps -a| grep k8s_ | grep -v grep| awk '{print "docker rm "$1}'|sh
```

