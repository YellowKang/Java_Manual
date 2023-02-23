# 服务器配置信息

## 查看CPU

查看CPU型号信息

```sh
cat /proc/cpuinfo | grep name | cut -f2 -d: | uniq -c
```

查看物理CPU个数

```sh
cat /proc/cpuinfo| grep "physical id"| sort| uniq| wc -l
```

查看每个CPU的核心数量

```sh
cat /proc/cpuinfo| grep "cpu cores"| uniq
```

查看逻辑CPU的个数

查看总线程数=CPU个数 * CPU核数 * 线程数，一般的以两颗8核16线程CPU举例，则为2 * 8 * 2 = 32

```sh
cat /proc/cpuinfo| grep "processor"| wc -l
```

查看CPU主频

```shell
cat /proc/cpuinfo |grep MHz|uniq
```

查看CPU位数

```shell
getconf LONG_BIT
```



## 查看内存

查看运行内存，按逻辑展示

```sh
free -h
```

## 查看磁盘

```
df -h
```

## 查看网卡

查看网卡信息

```sh
lspci | grep Ethernet
```

# 服务器信息初始化

## 修改主机名

查看主机名

```
 hostname
```

查看主机名以及服务器信息

```
hostnamectl
```

查看主机名对应的ip

```
hostname -i
```

临时修改主机名（不推荐）

```
hostname bigkang
```

永久修改主机名（推荐）

```
方法一（CentOS7直接使用命令永久修改）：

		hostnamectl set-hostname bigkang
		
方法二（修改文件，重启生效）：

		echo "bigkang" > /etc/hostname
		
		再加上即可临时以及永久生效
		
				hostname bigkang
```

修改host映射，将host也修改为当前，如192.168.64.4 instance-abulrb7w instance-abulrb7w.novalocal

是hosts文件，但是我已经将主机名修改为bigkang，instance-abulrb7w是以前的主机名，我们还需要修改hosts

```
vim /etc/hosts
```

将instance-abulrb7w修改为bigkang即可

然后修改hosts

vim /etc/hosts

```
127.0.0.1       localhost      bigkang
```



## 设置静态IP

### Ubantu

​		

### CentOS

​		首先我们需要查看哪些网卡正在运行使用中，也就是插入网线的网卡

```shell
ifconfig | grep UP,BROADCAST,RUNNING,MULTICAST
```

​		然后就能看到如下，我们可以看到，那么eth0这个网卡已经插入网线了，我们来对eth0进行设置

```shell
eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
flannel.1: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1450
```

​		首先我们进入网卡目录

```shell
cd /etc/sysconfig/network-scripts
```

​		然后编辑这个相应的网卡文件

```shell
vim ifcfg-eth0 
```

​		我们修改如下：

```shell
# 使用静态IP地址，默认为dhcp 
BOOTPROTO=static
# 设置的静态IP地址
IPADDR=192.168.1.177
# 子网掩码 
NETMASK=255.255.255.0
# 网关地址 
GATEWAY=192.168.1.1
# DNS服务器（可选，可以不设置）
DNS1=8.8.8.8
DNS2=114.114.114.114


直接修改的配置如下：

BOOTPROTO=static
IPADDR=192.168.1.177
NETMASK=255.255.255.0
GATEWAY=192.168.1.1
DNS1=8.8.8.8
DNS2=114.114.114.114
```

​		然后我们修改/etc/sysconfig/network，修改如下两项

```shell
NETWORKING=yes
GATEWAY=192.168.1.1
```

​		最后重启网络即可

```shell
service network restart
```



## 生成SSH免密

生成公钥私钥，一直回车即可

```
ssh-keygen -t rsa
```

查看公钥（注意使用哪个用户生成公钥私钥，就在哪个用户的目录下，root为/root）

```
cat /root/.ssh/id_rsa.pub
```

例如我们生成的公钥如下，也就是cat后的返回值

```
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDmePO2xh4dZDVXlBZK7K2sxaUcPcfuXkUpMnnL1+HlWNGlX4J3wJlZ+jSXHrj5yqG2w8yXjvgShEeOjQCzd68bkGqrY7hD52/tVTbiEJ2hXeqZTE+1dH0DPUh3nC2Ssjwym0FtfSFsnib4Z5QiA1iLaINMadcFPqTk7OXGgDj17KXgeMOglOPCT++tQSrMP9eNtrW44PQ3AodaQntPuV2nq/VvIxi4wRQWIfceHbHuEg53aOZezf0w9uE8sVQEmb3Pf/CTjWLMxbtCiic6ItZqAyhYcuH3RmnMOiIE+zYsDYT3WQsPF/LQV7y2sZvvY9fRTlg/8FBqSTBN6w3qL6E3 root@bigkang
```

我们将这个数据添加到另一台需要免密的服务器上即可

```
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDmePO2xh4dZDVXlBZK7K2sxaUcPcfuXkUpMnnL1+HlWNGlX4J3wJlZ+jSXHrj5yqG2w8yXjvgShEeOjQCzd68bkGqrY7hD52/tVTbiEJ2hXeqZTE+1dH0DPUh3nC2Ssjwym0FtfSFsnib4Z5QiA1iLaINMadcFPqTk7OXGgDj17KXgeMOglOPCT++tQSrMP9eNtrW44PQ3AodaQntPuV2nq/VvIxi4wRQWIfceHbHuEg53aOZezf0w9uE8sVQEmb3Pf/CTjWLMxbtCiic6ItZqAyhYcuH3RmnMOiIE+zYsDYT3WQsPF/LQV7y2sZvvY9fRTlg/8FBqSTBN6w3qL6E3 root@bigkang" >> /root/.ssh/authorized_keys
```

然后我们直接SSH到服务器上即可

```
ssh  免密登录服务器
```

# 磁盘设置

我们挂载磁盘首先需要分区，格式化，然后挂载

## 查看磁盘

```
fdisk -l
```

## 进行分区

```
fdisk /dev/xvdc
```

然后输入m就能看到命令提示了

输入n

出现

选择p

然后回车

选择1

然后两次回车

然后w保存



## 格式化磁盘

```
mkfs -t ext4 /dev/xvdc
```

## 挂载磁盘

```
mount /dev/xvdc /data
```

挂载完成后使用命令查看是否挂载完毕

```
mount | grep /dev/xvdc 
```

## 永久挂载（必须执行）

我们挂载了之后这只是临时挂载了上去如果我们想要重启后还有效果则需要修改配置文件	

首先我们查看fstab文件，这里面存储了我们永久挂载的信息

```
 cat /etc/fstab
```

一共有6个字段

设备（UUID或路径指定）  挂载点  文件系统类型   defaults  转储标志  fsck顺序  

如下

那么我们来获取自己的UUID

```
 blkid /dev/xvdc
 获取到了这个uuid
 d04f7568-91f4-496a-996e-e67cbb337300
```

然后我们在里面进行添加

这里中标麒麟是ext4

```
UUID=d04f7568-91f4-496a-996e-e67cbb337300 /data                       ext4     defaults        0 0

echo "UUID=d04f7568-91f4-496a-996e-e67cbb337300 /data                       ext4     defaults        0 0" >> /etc/fstab
```



# 用户



## 添加sudo权限

添加itsm用户的sudo权限

```
echo "itsm    ALL=(ALL:ALL) ALL" >> /etc/sudoers

adduser itsm
```





# 青云服务器重置

​		替换yum源

```sh
cd /etc/yum.repos.d/
mv CentOS-Linux-BaseOS.repo CentOS-Linux-BaseOS.repo.bak
wget -O CentOS-Linux-BaseOS.repo http://mirrors.aliyun.com/repo/Centos-8.repo

echo "192.168.100.11       qingyun01
192.168.100.12       qingyun02
192.168.100.13       qingyun03" >> /etc/hosts


# 生成ssh
ssh-keygen -t rsa

cat /root/.ssh/id_rsa.pub  >> /root/.ssh/authorized_keys


# 安装Docker
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo 
sudo yum install docker-ce-20.10.6  -y




# 上传OR下载Compose

cp docker-compose-linux-x86_64 /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose


# 磁盘挂载

mkdir /data
mkfs -t ext4 /dev/vdc
mount /dev/vdc /data
rm -rf /data/lost+found

 blkid /dev/vdc

# echo "UUID=31d82231-ebae-47ca-ad23-d0ee1ad92976 /data                       ext4     defaults        0 0" >>  /etc/fstab



sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://ldlov75k.mirror.aliyuncs.com"],
  "graph":"/data/docker",
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "1000m",
    "max-file": "3"
  },
  "exec-opts": ["native.cgroupdriver=systemd"],
  "insecure-registries" : ["hub.bigkang.k8s"]
}
EOF


systemctl enable docker.service
systemctl start docker.service


# 初始设置
systemctl stop firewalld && systemctl disable firewalld
setenforce 0 && sed -i "s/^SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
swapoff -a && sed -i 's/.*swap.*/#&/' /etc/fstab
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
cat > /etc/yum.repos.d/kubernetes.repo << EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

export kubeletVersion="1.22.0"
yum install -y kubelet-$kubeletVersion kubeadm-$kubeletVersion kubectl-$kubeletVersion


systemctl enable kubelet

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

kubeadm init --config /var/lib/kubelet/config.yaml --ignore-preflight-errors=Swap 


# 写入环境变量
echo "export KUBECONFIG=/etc/kubernetes/admin.conf" >> /etc/profile
source  /etc/profile

# 安装网络
# 创建目录存放命令
mkdir ~/flannel
cd ~/flannel
# 下载Flannel
wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

# 下载前ping 域名然后修改host
ping raw.githubusercontent.com
199.232.4.133 raw.githubusercontent.com

# 然后应用
kubectl apply -f kube-flannel.yml


# 然后检查集群是否健康
wget https://github.91chi.fun//https://github.com//vmware-tanzu/sonobuoy/releases/download/v0.55.1/sonobuoy_0.55.1_linux_386.tar.gz

tar -zxvf sonobuoy_0.55.1_linux_386.tar.gz
sonobuoy run --wait
```

