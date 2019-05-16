# 检查环境

所有服务器都需要检测环境，以及修改环境

这里我们采用4台服务器搭建，1台master，3台node

master主机

111.67.196.127	k8s-master

node节点主机

111.67.198.232 	k8s-node1
39.108.158.33	k8s-node2
140.143.0.227	k8s-node3

首先检测是否能联网

随便进入docker容器，如果没问题则能联网

```
docker exec -it 容器 bash
ping baidu.com
```

然后开机启动docker

```
systemctl enable docker
```

首先关闭防火墙

```
systemctl stop firewalld.service
systemctl disable firewalld
```

关闭selinux

```
sed -i 's/enforcing/disabled/' /etc/selinux/config 
setenforce 0
```

关闭swap分区

```
swapoff -a  # 临时
vim /etc/fstab  # 永久
```

添加主机名与IP对应关系

```
vim /etc/hosts
然后在下面写入
111.67.196.127	k8s-master
111.67.198.232 	k8s-node1
39.108.158.33	k8s-node2
140.143.0.227	k8s-node3
```

同步时间

```
yum install ntpdate -y
ntpdate  ntp.api.bz
```

开启ipv4转发

```
vim /etc/sysctl.conf
添加下面内容
net.ipv4.ip_forward = 1
net.ipv4.ip_forward_use_pmtu = 0
```

# 一键删除测试环境

批量停止rancher容器

```
docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh
```

批量删除rancher容器

```
docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh
```

批量删除rancher镜像

```
docker images| grep rancher | grep -v grep| awk '{print "docker rmi "$3}'|sh
```

docker ps -a| grep rancher | grep -v grep| awk '{print "docker stop "$1}'|sh

docker ps -a| grep k8s| grep -v grep| awk '{print "docker stop"$1}'|sh

docker ps -a| grep rancher | grep -v grep| awk '{print "docker rm "$1}'|sh

docker ps -a| grep k8s| grep -v grep| awk '{print "docker rm "$1}'|sh