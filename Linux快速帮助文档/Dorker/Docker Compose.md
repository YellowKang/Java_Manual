# Docker Compose简介

​		什么是Docker-compose呢？前面我们使用 Docker 的时候，定义 Dockerfile 文件，然后使用 docker build、docker run 等命令操作容器。然而微服务架构的应用系统一般包含若干个微服务，每个微服务一般都会部署多个实例，如果每个微服务都要手动启停，那么效率之低，维护量之大可想而知，使用 Docker Compose 可以轻松、高效的管理容器，它是一个用于定义和运行多容器 Docker 的应用程序工具

 		简单的来说他可以帮助我们一键开启所有要部署的docker容器，也能一键关闭，是用来管理docker容器的

#  Docker Compose的安装

​		下面我们演示Linux下安装Docker Compose（联网环境）

我们首先直接去 Docker Compose的git地址查看下载命令

<https://github.com/docker/compose/releases> 

我们可以很清楚的看到现在的最新的是1.25.0

那么我们来安装它。我们看到下面有安装命令我们直接执行它的命令

```
curl -L https://github.com/docker/compose/releases/download/1.25.0-rc1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose

chmod +x /usr/local/bin/docker-compose
```

 执行后我们查看版本，如果出现版本则安装成功

```
docker-compose -v
```

如果太慢可以将它从一台安装好的机器scp到另一台机器

```
scp docker-compose root@114.67.80.169:/usr/local/bin/docker-compose
```

# Docker Compose离线安装

我们可以去Github上下载相应的，Docker Compose选择相应的系统以及类型下载，如Linux 64位，选择

[docker-compose-Linux-x86_64](https://github.com/docker/compose/releases/download/1.25.5/docker-compose-Linux-x86_64)

![](https://img02.sogoucdn.com/app/a/100520146/62bfd62dbf220aa368f85880e59f5f9f)

将相应的文件下载下来然后放入服务器上，并且复制到/usr/local/bin

```
cp /root/docker-compose /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

然后查看版本即可

```
docker-compose -V
```

