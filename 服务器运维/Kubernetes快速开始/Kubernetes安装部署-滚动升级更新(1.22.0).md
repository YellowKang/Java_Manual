# 基础环境

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

```sh
modprobe br_netfilter
modprobe ip_conntrack
cat > /etc/sysctl.d/k8s.conf << EOF
net.bridge.bridge-nf-call-iptables=1
net.bridge.bridge-nf-call-ip6tables=1
net.ipv4.ip_forward=1
net.ipv4.tcp_tw_recycle=0
vm.swappiness=0
vm.overcommit_memory=1
vm.panic_on_oom=0
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

# 开始安装

## K8s安装

### kubeadm安装K8s

​		安装kubeadm，kubelet和kubectl，指定版本（所有节点）

```sh
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

​			在线联网安装,Master节点执行

```sh
# 创建目录存放命令
mkdir ~/flannel
cd ~/flannel
# 下载Flannel
wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
# 然后应用
kubectl apply -f kube-flannel.yml
```

​		查看是否安装成功

```sh
kubectl get pods -n kube-system | grep kube-flannel
```

​		返回类似如下

```sh
kube-flannel-ds-lzpgk             1/1     Running    0          64s
kube-flannel-ds-t749p             1/1     Running    0          64s
kube-flannel-ds-x9drq             0/1     Init:0/1   0          64s
```

​		等待所有状态都为Running表示安装成功

### Calico（推荐）

​		官网地址：[点击进入](https://github.com/projectcalico/cni-plugin)

​			Calico是Kubernetes生态系统中另一种流行的网络选择。虽然Flannel被公认为是最简单的选择，但Calico以其性能、灵活性而闻名。Calico的功能更为全面，不仅提供主机和pod之间的网络连接，还涉及网络安全和管理。Calico CNI插件在CNI框架内封装了Calico的功能。

​			在满足系统要求的新配置的Kubernetes集群上，用户可以通过应用单个manifest文件快速部署Calico。如果您对Calico的可选网络策略功能感兴趣，可以向集群应用其他manifest，来启用这些功能。

​			尽管部署Calico所需的操作看起来相当简单，但它创建的网络环境同时具有简单和复杂的属性。与Flannel不同，Calico不使用overlay网络。相反，Calico配置第3层网络，该网络使用BGP路由协议在主机之间路由数据包。这意味着在主机之间移动时，不需要将数据包包装在额外的封装层中。BGP路由机制可以本地引导数据包，而无需额外在流量层中打包流量。

​			除了性能优势之外，在出现网络问题时，用户还可以用更常规的方法进行故障排除。虽然使用VXLAN等技术进行封装也是一个不错的解决方案，但该过程处理数据包的方式同场难以追踪。使用Calico，标准调试工具可以访问与简单环境中相同的信息，从而使更多开发人员和管理员更容易理解行为。

​			除了网络连接外，Calico还以其先进的网络功能而闻名。 网络策略是其最受追捧的功能之一。此外，Calico还可以与服务网格Istio集成，以便在服务网格层和网络基础架构层中解释和实施集群内工作负载的策略。这意味着用户可以配置强大的规则，描述Pod应如何发送和接受流量，提高安全性并控制网络环境。

​			如果对你的环境而言，支持网络策略是非常重要的一点，而且你对其他性能和功能也有需求，那么Calico会是一个理想的选择。此外，如果您现在或未来有可能希望得到技术支持，那么Calico是提供商业支持的。一般来说，当您希望能够长期控制网络，而不是仅仅配置一次并忘记它时，Calico是一个很好的选择。

​		在线联网安装：

```

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
mkdir ~/k8s-dashboard
cd ~/k8s-dashboard
wget https://raw.githubusercontent.com/kubernetes/dashboard/v2.4.0/aio/deploy/recommended.yaml
```

​		然后修改

​		修改类型为type: NodePort，然后新增nodePort: 30000暴露端口

```sh
vim ~/k8s-dashboard/recommended.yaml
```

```sh
spec:
  type: NodePort
  ports:
    - port: 443
      targetPort: 8443
      nodePort: 30000
```

​		然后应用

```sh
kubectl apply -f ~/k8s-dashboard/recommended.yaml
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

```sh
# 创建挂载目录
mkdir -p ~/ingress-nginx/ && cd ~/ingress-nginx/
# 下载
wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.0/deploy/static/provider/cloud/deploy.yaml
```

​		部署ingress-nginx

```sh
# 修改镜像为阿里云加速镜像否则无法创建拉取

# 将 k8s.gcr.io/ingress-nginx/controller 替换为 registry.aliyuncs.com/google_containers/nginx-ingress-controller
sed -i "s#k8s.gcr.io/ingress-nginx/controller#registry.aliyuncs.com/google_containers/nginx-ingress-controller#g" deploy.yaml

sed -i "s#k8s.gcr.io/ingress-nginx/kube-webhook-certgen#registry.aliyuncs.com/google_containers/kube-webhook-certgen#g" deploy.yaml

          
# 启动部署
kubectl apply -f deploy.yaml
```

​			然后查看信息直到全部启动

```
 kubectl get all -n ingress-nginx
```

​			查看是否启动成功

```sh
kubectl get pod -n ingress-nginx 

# ingress准备好了
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
```

​			然后我们新建一个测试Demo用于ingress转发

```sh
# 创建并且暴露demo
kubectl create deployment demo --image=httpd --port=80
kubectl expose deployment demo

# 查看demo是否启动
kubectl get all

# 然后使用nginx进行映射,使用demo.localdev.me域名即可访问
kubectl create ingress demo-localhost --class=nginx \
  --rule=demo.localdev.me/*=demo:80
  
 

# 修改host 访问域名
192.168.100.11 demo.localdev.me
```

​		启动成功查看是否有服务

```sh
kubectl  get service -n ingress-nginx -o wide
```

​		然后我们部署一个tomcat测试转发功能

```yaml
cd ~
echo "apiVersion: v1
kind: Service
metadata:
  name: tomcat
  namespace: default
spec:
  selector:
   app: tomcat
   release: canary
  ports:
  - name: http
    targetPort: 8080
    port: 8080
  - name: ajp
    targetPort: 8009
    port: 8009
 
---
 
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tomcat-deploy
  namespace: default
spec:
  replicas: 1
  selector:
   matchLabels:
     app: tomcat
     release: canary
  template:
   metadata:
     labels:
       app: tomcat
       release: canary
   spec:
     containers:
     - name: tomcat
       image: tomcat
       ports:
       - name: http
         containerPort: 8080" > tomcat.yaml
```

​		创建完成yaml后启动

```sh
kubectl apply -f tomcat.yaml
```

​		查看是否启动成功

```sh
kubectl get pods | grep tomcat
```

​		启动成功后我们创建一个访问控制策略

​		然后启动控制策略

```sh
kubectl apply -f tomcat-ingress.yaml
```

​		然后查看是否启动成功

```
kubectl get Ingress
```

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1605078645402.png)

​		然后我们修改自己电脑上的hosts（如果有真实的域名+公网IP则使用即可），如果没有则修改Host否则无法访问

```
192.168.1.12 tomcat.bigkang.k8s
```

​		然后访问域名

```
http://tomcat.bigkang.k8s:30080
```

​		我们采用四层负载代理TCP

​		我们发现使用30080端口不好，那么我们在最外面再部署一个nginx，用于转发我们使用Docker部署,切记nginx下放入ca证书

```sh
mkdir -p /data/nginx/{conf,logs,data}
touch  /data/nginx/nginx.conf
chmod 777 /data/nginx/


# 写入如下内容
vim /data/nginx/nginx.conf

user  nginx;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;
    sendfile        on;
    keepalive_timeout  65;
    include /etc/nginx/conf.d/*.conf;
}
stream {
     server {
       listen 80;
       proxy_pass 192.168.1.12:30080;
     }
     server {
       ssl_certificate /data/ca/root.crt;
       ssl_certificate_key /data/ca/root.key;
       ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
       listen 443;
       proxy_pass 192.168.1.12:30443;
     }
}
```

​		创建http证书

```sh
mkdir -p /data/nginx/data/ca &&  cd /data/nginx/data/ca

openssl genrsa -out root.key 2048
openssl req -new -x509 -key root.key -out root.crt -days 3650 -subj "/C=CN/ST=shanghai/L=jingan/O=dev/OU=island/CN=*.onebean.net"
```

​		然后启动即可

```sh
docker run -d \
--name nginx-server \
--restart=always \
-p 80:80 \
-p 443:443 \
-v /data/nginx/nginx.conf:/etc/nginx/nginx.conf \
-v /data/nginx/conf:/etc/nginx/conf.d \
-v /data/nginx/data:/data \
-v /data/nginx/logs:/var/log/nginx nginx:1.17.8
```

​		我们访问80即可

​		然后我们再将tomcat改造成https方式请求

​		生成tls证书

```sh
cd /data/nginx/data/ca
# crt转pem
openssl x509 -in root.crt -out root.pem -outform PE
# K8s添加tls证书
kubectl create secret tls custom-tls-secret --cert=root.pem --key=root.key

# 查看证书
kubectl get secret  custom-tls-secret



```

​		我们修改tomcat-ingress.yaml

```sh
cd ~
vim tomcat-ingress.yaml

# 修改如下，新增tls证书以及域名secretName为刚添加的证书
spec:
  tls:
  - hosts: 
    - tomcat.bigkang.k8s
    secretName: custom-tls-secret
  rules:
  - host: tomcat.bigkang.k8s
    http:
      paths:
      - path:
        backend:
          serviceName: tomcat
          servicePort: 8080



# 重新应用
kubectl replace --force -f tomcat-ingress.yaml
```

​		然后访问http也会直接跳转到https，也可直接访问https

​		然后我们将dashboard也改造为https,注意kubernetes-dashboard有自己的tls证书，还有命名空间,以及修改注解

​		这里注意注解需要修改，不再是nginx，而是HTTPS以及ssl等

```sh
cd /root/k8s-dashboard

cat >  dashboard-ingress.yaml  <<EOF
apiVersion: extensions/v1beta1
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
    - dashboard.bigkang.k8s
    secretName: kubernetes-dashboard-certs
  rules:
  - host: dashboard.bigkang.k8s
    http:
      paths:
      - path:
        backend:
          serviceName: kubernetes-dashboard
          servicePort: 443
EOF

# 然后应用
kubectl apply -f dashboard-ingress.yaml

replace

# 然后查看
kubectl get ingress -n kubernetes-dashboard
```

​		再次修改hosts

```
192.168.1.12 dashboard.bigkang.k8s
```

​		此时访问https://dashboard.bigkang.k8s 已经可以，但是我们需要关闭掉以前的30000端口

​		然后我们关闭3000端口

```sh
vim recommended.yaml 


# 删除node port以及type
# 修改完成后如下
spec:
  ports:
    - port: 443
      targetPort: 8443
  selector:
    k8s-app: kubernetes-dashboard

# 然后需要重新加载，有两种方式，重新应用后创建用户，或者将创建用户写入资源文件一起执行
# 有两种方式，选择一种即可推荐第二种
```

​		第一种重新应用后创建用户

```sh
kubectl replace --force -f recommended.yaml 
# 重新加载后需要重新创建用户否则无法查询集群信息
# 执行如下两步即可
# 新建用户
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
EOF
# 设置Rbac权限
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

​		第二种直接写入资源文件（推荐）

```sh
# 我们可以直接执行命令或者把资源创建放在recommended.yaml中
# 写入文件，写入后需要重新加载
echo "---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
---
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
" >> recommended.yaml 
# 重新应用
kubectl replace --force -f recommended.yaml 
```

​		查看是否启动完成

```
kubectl get all -n kubernetes-dashboard
```

​		然后重新获取token

```sh
# 获取token
kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}') | grep token | tail -n 1 | awk '{print $2}' 

# 将token写入文件,查询最后一行
kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}') | grep token | tail -n 1 | awk '{print $2}'  > /root/k8s/k8s-dashboard-token
```

​		然后重启dashboard-ingress

```sh
kubectl replace --force -f dashboard-ingress.yaml
```

这样我们就可以只访问ingress-nginx转发的dashboard了，而不是暴露nodeport



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
kubectl create secret tls $domainName-tls-secret --cert=$domainName.pem --key=$domainName.key
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
sed -i "s#certificate: /your/certificate/path#certificate: $domainNamePath/$domainName.crt#g" harbor.yml
sed -i "s#private_key: /your/private/key/path#private_key: $domainNamePath/$domainName.key#g" harbor.yml

# 修改密码
export harborPassword="bigkang"
sed -i "s#Harbor12345#$harborPassword#g" harbor.yml

# 设置挂载的Harbor的Data盘
export harborDataPath="/data/harbor/data"
mkdir -p $harborDataPath
sed -i "s#data_volume: /data#data_volume: $harborDataPath#g" harbor.yml

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

## 安装CoreDns

​		官网地址：[点击进入](https://github.com/coredns/coredns)

​		CoreDNS是用Go编写的DNS服务器/转发器，它链接[插件](https://coredns.io/plugins)。每个插件执行一个（DNS）功能。

​		CoreDNS可以代替Kubernetes中的标准Kube-DNS运行。使用*kubernetes* 插件，CoreDNS将从Kubernetes集群读取区域数据。它实现了为基于Kubernetes DNS的服务发现定义的规范：[DNS规范](https://github.com/kubernetes/dns/blob/master/docs/specification.md)。

​		首先我们来安装使用CoreDns

```sh
# 创建目录
mkdir ~/coreDns && cd ~/coreDns
# 下载部署脚本
wget https://raw.githubusercontent.com/coredns/deployment/master/kubernetes/deploy.sh
wget https://raw.githubusercontent.com/coredns/deployment/master/kubernetes/coredns.yaml.sed

# 启动脚本并且应用
chmod 7 deploy.sh
./deploy.sh | kubectl apply -f -
# 删除原来的kube-dns
kubectl delete --namespace=kube-system deployment kube-dns
```

​		如果需要回滚到kube-dns，使用如下(一般不需要)

```sh
# 下载回滚脚本
wget https://github.com/coredns/deployment/blob/master/kubernetes/rollback.sh

# 回滚应用
./rollback.sh | kubectl apply -f -

# 删除CoreDns
kubectl delete --namespace=kube-system deployment coredns
```

​		验证是否能够使用CoreDNS

```sh
# 运行容器
kubectl run cirros-$RANDOM --rm -it --image=cirros -- sh
# 进入脚本后我们先查看hosts
cat /etc/resolv.conf 
# 返回如下，我们可以看到search的域
nameserver 10.1.0.10
search default.svc.cluster.local svc.cluster.local cluster.local openstacklocal
options ndots:5
# 测试访问外网
ping baidu.com
# 测试我们直接使用service名称访问
ping boot-k8s-service
# 返回如下，因为Master执行所以无法ping通，但是我们可以看到DNS解析成功了，成功解析到service的ip
PING boot-k8s-service (10.1.85.177): 56 data bytes
```

## kube-proxy使用ipvs（pod无法pingservice问题）

​		我们发现pod中无法ping通service

​		**原因：kube-proxy使用了iptable模式，修改为ipvs模式则可以在pod内ping通clusterIP或servicename**

​		我们查看

```sh
# 查看kube-proxy
kubectl get pods -A  | grep kube-proxy

# 返回如下
kube-system            kube-proxy-2clfd                             1/1     Running   0          24h
kube-system            kube-proxy-mn9j4                             1/1     Running   0          24h
kube-system            kube-proxy-mprrf                             1/1     Running   0          24h

# 查看日志
kubectl logs -n kube-system kube-proxy-mn9j4

# 返回如下
W1119 03:07:18.587672       1 server_others.go:559] Unknown proxy mode "", assuming iptables proxy
I1119 03:07:18.593516       1 node.go:136] Successfully retrieved node IP: 192.168.1.115
I1119 03:07:18.593540       1 server_others.go:186] Using iptables Proxier.
I1119 03:07:18.593713       1 server.go:583] Version: v1.18.12
I1119 03:07:18.593978       1 conntrack.go:100] Set sysctl 'net/netfilter/nf_conntrack_max' to 131072
I1119 03:07:18.593995       1 conntrack.go:52] Setting nf_conntrack_max to 131072
I1119 03:07:18.594041       1 conntrack.go:100] Set sysctl 'net/netfilter/nf_conntrack_tcp_timeout_established' to 86400
I1119 03:07:18.594061       1 conntrack.go:100] Set sysctl 'net/netfilter/nf_conntrack_tcp_timeout_close_wait' to 3600
I1119 03:07:18.594444       1 config.go:315] Starting service config controller
I1119 03:07:18.594459       1 shared_informer.go:223] Waiting for caches to sync for service config
I1119 03:07:18.594475       1 config.go:133] Starting endpoints config controller
I1119 03:07:18.594484       1 shared_informer.go:223] Waiting for caches to sync for endpoints config
I1119 03:07:18.694579       1 shared_informer.go:230] Caches are synced for service config 
I1119 03:07:18.694609       1 shared_informer.go:230] Caches are synced for endpoints config 

# 我们可以看到
I1119 03:07:18.593540       1 server_others.go:186] Using iptables Proxier.
# 使用的iptables
```

​		修改为**ipvs模式**

```sh
# 便捷configMap，cm为简写
kubectl edit cm kube-proxy -n kube-system

# 找到mod
    kind: KubeProxyConfiguration
    metricsBindAddress: ""
    mode: ""
    nodePortAddresses: null
    oomScoreAdj: null
    portRange: ""
    showHiddenMetricsForVersion: ""

# 修改为ipvs
    kind: KubeProxyConfiguration
    metricsBindAddress: ""
    mode: "ipvs"
    nodePortAddresses: null
    oomScoreAdj: null
    portRange: ""
    showHiddenMetricsForVersion: ""
```

​		然后服务器中设置ipvs配置

```sh
cat > /etc/sysconfig/modules/ipvs.modules <<EOF
#!/bin/bash 
modprobe -- ip_vs 
modprobe -- ip_vs_rr 
modprobe -- ip_vs_wrr 
modprobe -- ip_vs_sh 
modprobe -- nf_conntrack_ipv4 
EOF
```

​		设置权限

```sh
sudo chmod 755 /etc/sysconfig/modules/ipvs.modules && bash /etc/sysconfig/modules/ipvs.modules && lsmod | grep -e ip_vs -e nf_conntrack_ipv4
```

​		Master中重启pod

```sh
kubectl get pod -n kube-system | grep kube-proxy |awk '{system("kubectl delete pod "$1" -n kube-system")}'
```

​		查看日志

```sh
kubectl get pods -A  | grep kube-proxy

kubectl logs -n kube-system  kube-proxy-jfl44 

# 返回如下
I1120 03:55:42.062279       1 node.go:136] Successfully retrieved node IP: 192.168.1.12
I1120 03:55:42.062316       1 server_others.go:259] Using ipvs Proxier.
W1120 03:55:42.062496       1 proxier.go:429] IPVS scheduler not specified, use rr by default
```

​		发现修改为Using ipvs Proxier即可

# 辅助

## 创建TLS证书

​		使用openssl创建

```sh
# 生成.key
# openssl genrsa -out root.key
# 生成.csr，
# C=国家代号  			 		 CN表示中国
# ST=省（拼音）  		 		shanghai（上海）
# L=市（拼音）  					jingan（静安）
# O=组织名（公司名）  		bigkang（bigkang公司）
# OU=组织单位名（公司名）  kaifa（开发单位）
# CN=域名								bigkang
# openssl req -new -sha256 -key root.key -out root.csr -subj "/C=CN/ST=shanghai/L=jingan/O=bigkang/OU=kaifa/CN=bigkang"
# 生成.crt，-days为天数，
# openssl x509 -req -days 3650 -sha1 -extensions v3_ca -signkey root.key -in root.csr -out root.crt
# crt装pem
# openssl x509 -in root.crt -out root.pem -outform PE


# 生成秘钥
openssl genrsa -out root.key 1024
# 生成证书请求文件
openssl req -new -sha256 -key root.key -out root.csr -subj "/C=CN/ST=shanghai/L=sichuan/O=bigkang/OU=kaifa/CN=bigkang"
# 生成CA根证书 (公钥证书)
openssl x509 -req -days 3650 -sha1 -extensions v3_ca -signkey root.key -in root.csr -out root.crt

# 根据服务器私钥生成公钥文件
openssl x509 -in root.crt -out root.pem -outform PEM

# K8s添加tls证书
kubectl create secret tls custom-tls-secret --cert=root.pem --key=root.key
```



# kubeadmin重新初始化

​		删除旧文件

```sh
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
  --pod-network-cidr=10.244.0.0/16
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

