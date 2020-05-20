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

查看总线程数=CPU个数 * CPU核数 * 线程数，一般的以两颗8核CPU举例，则为2 * 8 * 2 = 32

```sh
cat /proc/cpuinfo| grep "processor"| wc -l
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
mkfs.ext3　　/dev/xvdc　
```

## 挂载磁盘



```
mount /dev/xvdc /data
```

# 用户



## 添加sudo权限

添加itsm用户的sudo权限

```
echo "itsm    ALL=(ALL:ALL) ALL" >> /etc/sudoers

adduser itsm
```

