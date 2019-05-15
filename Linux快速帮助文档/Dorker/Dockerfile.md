# 什么是Dockerfile？

​		Dockerfile就是为了给我们的项目打包成一个镜像，Dockerfile实际上就是一串指令脚本，也就是将一个jdk或者mysql又或者是一个微服务打包成一个镜像，然后我们就可以通过打包好的镜像直接run容器，这样就可以快速部署，并且完全兼容不受环境的影响，

# Dockerfile的简单使用

​		首先我们先将jdk复制到一个目录下

​		然后再写一个Dockerfile文件

​		名字也叫Dockerfile

```
#依赖镜像名称和ID
FROM centos:7
#指定镜像创建者信息
MAINTAINER ITCAST
#切换工作目录
WORKDIR /usr
RUN mkdir /usr/local/java
#ADD 是相对路径jar,把java添加到容器中
ADD jdk-8u171-linux-x64.tar.gz /usr/local/java/
#配置java环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_171
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH

```

​		这里我们可以看到他将当前目录的jdk加了进去并且配置了环境变量，然后我们直接使用Docker来构建镜像

​	使用docker build  然后-t指定名字，然后加上一个点表示当前目录下构建

docker build -t='jdk1.8' .

然后我们就可以返回目录测试了

cd ~

然后

docker run 运行，-di后台 --name指定名字，最后空格运行jdk1.8这个镜像

docker run -di --name jdk1.8 jdk1.8



# 搭建Docker私有仓库

## 安装仓库

​		什么是Docker私有仓库呢？我们通常下载镜像是在docker上直接search然后再pull下来，但是如果我们自己将一个Docker镜像只做好了之后如何去下载呢？这个时候就需要使用我们的Docker私有仓库了

​		如何使用Docker私有仓库？首先我们先下载仓库



​			docker pull registry



​		首先我们现更改名字



​			docker tag docker.io/registry registry



​		然后删除原来的docker rmi docker.io/registry

​		这样就下载下来了一个仓库然后我们运行仓库



​		 	docker run -di --name registry --restart=always  -p 5000:5000 registry



​		然后我们docker ps查看一下

​		然后我们测试一下能不能连接上



​			 curl http://localhost:5000/v2/_catalog



​		如果返回{"repositories":[]}

​		就说明启动成功了

​		我们还能通过浏览器访问

​		将localhost换成ip

​		http://localhost:5000/v2/_catalog

```
"insecure-registries":["192.168.1.161:5000"]
```

http://140.143.0.227:5000/v2/_catalog

​		