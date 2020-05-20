# yum安装docker

```sh
首先我们先更新下我们的yum，

yum -y update

然后安装

yum install -y docker
```

# Linux离线安装Docker

首先下载指定Docker或者直接wget

```
https://download.docker.com/linux/static/stable/x86_64/
```

wget

```
wget https://download.docker.com/linux/static/stable/x86_64/docker-18.06.1-ce.tgz
```

解压文件

```
tar -zxvf docker-18.06.1-ce.tgz 
```

运行命令

```
sudo cp docker/* /usr/bin/
```

添加启动服务

```
echo "[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target
 
[Service]
Type=notify
# the default is not to use systemd for cgroups because the delegate issues still
# exists and systemd currently does not support the cgroup feature set required
# for containers run by docker
ExecStart=/usr/bin/dockerd
ExecReload=/bin/kill -s HUP $MAINPID
# Having non-zero Limit*s causes performance problems due to accounting overhead
# in the kernel. We recommend using cgroups to do container-local accounting.
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
# Uncomment TasksMax if your systemd version supports it.
# Only systemd 226 and above support this version.
#TasksMax=infinity
TimeoutStartSec=0
# set delegate yes so that systemd does not reset the cgroups of docker containers
Delegate=yes
# kill only the docker process, not all processes in the cgroup
KillMode=process
# restart the docker process if it exits prematurely
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s
 
[Install]
WantedBy=multi-user.target" > /etc/systemd/system/docker.service
```

重新加载服务

```
systemctl daemon-reload
```

修改默认启动路径

```
mkdir -p /etc/docker
touch /etc/docker/daemon.json
echo '{
 "graph":"/data/docker"
}' > /etc/docker/daemon.json
```

启动docker

```
systemctl start docker
```

设置开机自启docker

```
systemctl enable docker
```

查看docker状态

```
systemctl status docker
```



# 指定yum源版本安装

参考下方更新

# Docker的启动关闭和查看以及操作

	启动docker
	
		systemctl start docker.service
	
		service docker start 
	
	重新启动


		systemctl restart docker.service
	
		service docker restart 
	
	关闭docker
	
		systemctl stop docker.service
	
		service docker stop
	
	查看docker状态
	
		systemctl status docker.service
	
		service docker status


	然后我们来配置下他的的加速镜像地址，加速地址根据自身情况挑选，建议使用aliyun
	
	根据自己的阿里云配置
	
	然后vi /etc/docker/daemon.json
	
	把后面的那个，的逗号去掉

# Docker版本升级

## !!!!!注意，请先将容器备份成文件然后进行更新

## 卸载旧版Docker

卸载

```
yum remove docker  docker-common docker-selinux docker-engine
```

## 安装需要的软件包以及Yum源

下载yum工具

```
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
```

更换yum源，这里我们使用阿里云的yum源

```
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo 
```

## 指定版本

指定版本首先我们需搜索版本，搜索docker显示版本并排序

```
yum list docker-ce --showduplicates | sort -r
```

然后查看里面有哪些版本，例如发现了18.06.1.ce  这个版本那么我们安装

```
sudo yum install docker-ce-18.06.1.ce  
```

## 安装最新

直接yum安装

```
sudo yum install docker-ce
```

# 设置开机启动

```
systemctl enable docker.service
systemctl enable docker

查看是否开机启动
systemctl status docker

然后查看版本，下面几个命令都可以
docker -v
docker version 
docker info
```

# 一键之骚操作重装最新Docker

```
yum -y remove docker  docker-common docker-selinux docker-engine
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
sudo yum -y install docker-ce
systemctl start docker.service
systemctl start docker
systemctl enable docker.service
systemctl enable docker
```

# 一键骚操作之安装指定版本Docker

```
yum -y remove docker  docker-common docker-selinux docker-engine
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
sudo yum install -y docker-ce-18.06.1.ce
systemctl enable docker
systemctl start docker
```

# 添加镜像加速

```
vim /etc/docker/daemon.json
# 填入一下内容,登录阿里云即可获取
{
 "registry-mirrors": ["https://ldlov75k.mirror.aliyuncs.com"]
}


systemctl restart docker.service
```

# 修改Docker文件存储路径

将docker文件存储的路径设置为data/docker，或者其他的挂载盘，可以有效地容灾

```
echo '{
 "registry-mirrors": ["https://ldlov75k.mirror.aliyuncs.com"],
 "graph":"/data/docker"
}' > /etc/docker/daemon.json

```

