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

 docker
