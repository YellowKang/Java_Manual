# Linux搭建

## 修改主机名

修改master主机

```
hostnamectl set-hostname master
```

修改node1主机

```
hostnamectl set-hostname node1
```

## 修改Host

修改master    Host文件

```
echo "172.16.16.5 master
172.16.16.4 node1" >> /etc/hosts
```

修改node1    Host文件

```
echo "172.16.16.5 master
172.16.16.4 node1" >> /etc/hosts
```

## 关闭防火墙、selinux还有swap分区

关闭防火墙

```
systemctl stop firewalld
systemctl disable firewalld
```

关闭selinux

```
setenforce 0
sed -i "s/^SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
```

关闭swap分区

```
swapoff -a
sed -i 's/.*swap.*/#&/' /etc/fstab
```

## 配置内核参数

```
cat > /etc/sysctl.d/k8s.conf << EOF
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system
```

## 配置国内yum源

```
yum install -y wget

wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.cloud.tencent.com/repo/centos7_base.repo

wget -O /etc/yum.repos.d/epel.repo http://mirrors.cloud.tencent.com/repo/epel-7.repo

yum clean all && yum makecache

cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF


wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo

yum install kubelet-1.14.2 kubeadm-1.14.2 kubectl-1.14.2 -y

systemctl enable kubelet
```

## 初始化

初始化master

```
kubeadm init --kubernetes-version=1.14.2 \
--apiserver-advertise-address=172.16.16.5  \
--image-repository registry.aliyuncs.com/google_containers \
--service-cidr=10.1.0.0/16 \
--pod-network-cidr=10.244.0.0/16
```

初始化node1

```
kubeadm init --kubernetes-version=1.14.2 \
--apiserver-advertise-address=172.16.16.4 \
--image-repository registry.aliyuncs.com/google_containers \
--service-cidr=10.1.0.0/16 \
--pod-network-cidr=10.244.0.0/16
```

## 记录

```
kubeadm join 172.16.16.5:6443 --token 6hycoj.mmxc3wwvckfip1k5 --discovery-token-ca-cert-hash sha256:26a1987f705f6a1c12147ec2949c1ded70774c7833a379d24e77abcc54ea86cc 
```

