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
#切换工作目录,工作目录为sh进入容器的默认路径
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

# 构建JRE基础镜像

## 打包Jre

​		先去官网下载JRE环境，然后删除多余的东西重新打包

​		下载Jre地址：[Jre地址](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

​		下载后解压,删除多余jre文件,然后重新压缩包

```sh
mkdir /tmp/jdk
tar -zxvf jre-8u231-linux-x64.tar.gz -C /tmp/jdk
cd /tmp/jdk
cd jre1.8.0_231
rm -rf COPYRIGHT LICENSE README* release THIRDPARTYLICENSEREADME-JAVAFX.txt THIRDPARTYLICENSEREADME.txt Welcome.html
rm -rf   lib/plugin.jar \
           lib/ext/jfxrt.jar \
           bin/javaws \
           lib/javaws.jar \
           lib/desktop \
           plugin \
           lib/deploy* \
           lib/*javafx* \
           lib/*jfx* \
           lib/amd64/libdecora_sse.so \
           lib/amd64/libprism_*.so \
           lib/amd64/libfxplugins.so \
           lib/amd64/libglass.so \
           lib/amd64/libgstreamer-lite.so \
           lib/amd64/libjavafx*.so \
           lib/amd64/libjfx*.so

cd ..
# 压缩
tar zcvf jre8.tar.gz jre1.8.0_231
```

​		

## alpine轻量级版本

​		然后新建Dockerfile文件

```sh
# 环境变量
export JRE_HOME=/usr/local/java/jre1.8.0_231
export JAVA_HOME=$JRE_HOME
echo "FROM docker.io/jeanblanchard/alpine-glibc
MAINTAINER bigkangsix@qq.com
# 设置apk源
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

RUN mkdir /usr/local/java
# 直接将JDK放入/usr/local/java
ADD jre8.tar.gz /usr/local/java
# 设置环境变量
ENV LANG zh_CN.uft8
ENV JRE_HOME=$JRE_HOME
ENV JAVA_HOME=$JRE_HOME
# 解决本地PATH导致容器异常，直接写死
ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$JRE_HOME/bin" > Dockerfile
rm -rf jre1.8.0_231
```

​		build镜像

```sh
docker build -t bigkang/jre8:alpine .
docker stop run
docker rm run
docker run --name run -di bigkang/jre8:alpine
docker exec -it run sh
进入容器输入java命令出现java提示即可
```

​		推送阿里云

```sh
# 登录
docker login registry.cn-shanghai.aliyuncs.com

# 标记版本号
docker tag bigkang/jre8:alpine registry.cn-shanghai.aliyuncs.com/bigkang/jre8:alpine

# 推送至阿里云
docker push registry.cn-shanghai.aliyuncs.com/bigkang/jre8:alpine
```



# 构建JDK基础镜像

## 打包Jdk

​		下载地址：[Jdk地址](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

​		首先解压jdk基础镜像

```sh
# 创建目录解压jdk
rm -rf /tmp/jdk
mkdir /tmp/jdk
tar -zxvf jdk-8u211-linux-x64.tar.gz -C /tmp/jdk
cd /tmp/jdk
cd jdk1.8.0_211

# 删除多余文件
rm -rf  *src.zip \
        "lib/missioncontrol" \
        "lib/visualvm" \
        "lib/"*javafx* \
        "jre/lib/plugin.jar" \
        "jre/lib/ext/jfxrt.jar" \
        "jre/bin/javaws" \
        "jre/lib/javaws.jar" \
        "jre/lib/desktop" \
        "jre/plugin" \
        "jre/lib/"deploy* \
        "jre/lib/"*javafx* \
        "jre/lib/"*jfx* \
        "jre/lib/amd64/libdecora_sse.so" \
        "jre/lib/amd64/"libprism_*.so \
        "jre/lib/amd64/libfxplugins.so" \
        "jre/lib/amd64/libglass.so" \
        "jre/lib/amd64/libgstreamer-lite.so" \
        "jre/lib/amd64/"libjavafx*.so \
        "jre/lib/amd64/"libjfx*.so \
        "jre/bin/jjs" \
        "jre/bin/keytool" \
        "jre/bin/orbd" \
        "jre/bin/pack200" \
        "jre/bin/policytool" \
        "jre/bin/rmid" \
        "jre/bin/rmiregistry" \
        "jre/bin/servertool" \
        "jre/bin/tnameserv" \
        "jre/bin/unpack200" \
        "jre/lib/ext/nashorn.jar" \
        "jre/lib/jfr.jar" \
        "jre/lib/jfr" \
        "jre/lib/oblique-fonts" \
        "jre/lib/security/README.txt"
rm -rf COPYRIGHT LICENSE README* release THIRDPARTYLICENSEREADME-JAVAFX.txt THIRDPARTYLICENSEREADME.txt Welcome.html
cd jre
rm -rf COPYRIGHT LICENSE README* release THIRDPARTYLICENSEREADME-JAVAFX.txt THIRDPARTYLICENSEREADME.txt Welcome.html


# 返回目录重新打包
cd ..
cd ..
tar zcvf jdk8.tar.gz jdk1.8.0_211 
```

## Alpine轻量级版本

```sh
# 环境变量
export JAVA_HOME=/usr/local/java/jdk1.8.0_211
export JRE_HOME=$JAVA_HOME/jre
echo "FROM docker.io/jeanblanchard/alpine-glibc
MAINTAINER bigkangsix@qq.com
# 设置apk源
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub
RUN wget -q -O /tmp/glibc-2.29-r0.apk  https://github.91chifun.workers.dev//https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-2.29-r0.apk
RUN apk add /tmp/glibc-2.29-r0.apk 
RUN apk add bash
# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

RUN mkdir /usr/local/java
# 直接将JDK放入/usr/local/java
ADD jdk8.tar.gz /usr/local/java
# 设置环境变量
ENV LANG zh_CN.uft8
ENV JAVA_HOME=$JAVA_HOME
ENV JRE_HOME=$JRE_HOME
# 解决本地PATH导致容器异常，直接写死
ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$JAVA_HOME/bin" > Dockerfile


# 删除停止以前容器
docker stop run
docker rm run
# 构建镜像
docker build -t bigkang/jdk8:alpine .

# 启动测试容器
docker run --name run -di bigkang/jdk8:alpine

# 进入容器
docker exec -it run sh

# 打印版本
java -version
```

​		安装字体工具库（选装，如果没有特殊要求则不需要安装其他字体）

```sh
apk add font-adobe-100dpi
```

​		推送镜像

```sh
# 登录
docker login registry.cn-shanghai.aliyuncs.com

# 标记版本号
docker tag bigkang/jdk8:alpine registry.cn-shanghai.aliyuncs.com/bigkang/jdk8:alpine

# 推送至阿里云
docker push registry.cn-shanghai.aliyuncs.com/bigkang/jdk8:alpine
```

## OpenJdk11+Alpine基础镜像





```bash
docker push bigkang/jdk:openjdk-11-alpine-font
docker tag bigkang/jdk:openjdk-11-alpine-font registry.gitlab.botpy.com/botpy/vosp/backend-common/openjdk:11.0.9

pxto8ZwFz4B_ha2t_Qkj
```





```bash
# 父镜像
FROM alpine:3.12
MAINTAINER bigkangsix@qq.com
# 设置apk源为清华
# RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN sed -i 's/dl-cdn.alpinelinux.org/mirror.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
# 安装bash curl 以及jdk11 还有字体
RUN apk add bash curl openjdk11  ttf-dejavu fontconfig
# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

# 设置环境变量
ENV LANG zh_CN.uft8
```

## CentOS版本

新建Dockerfile文件

```sh
# 环境变量
export JAVA_HOME=/usr/local/java/jdk1.8.0_211
export JRE_HOME=$JAVA_HOME/jre
# 下载阿里云加速
wget -O CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
echo "FROM centos:7
MAINTAINER bigkangsix@qq.com
# 设置编码
RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8
ENV LC_ALL "zh_CN.UTF-8"
# 设置时区
ENV TZ=Asia/Shanghai
RUN ls -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone

# 设置yum加速源
ADD CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo
# 直接将JDK放入/usr/local/java
RUN mkdir /usr/local/java
ADD jdk8.tar.gz /usr/local/java
# 设置环境变量
ENV JAVA_HOME=$JAVA_HOME
ENV JRE_HOME=$JRE_HOME
# 解决本地PATH导致容器异常，直接写死
ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$JAVA_HOME/bin" > Dockerfile

# 删除停止以前容器
docker stop run
docker rm run
# 构建镜像
docker build -t bigkang/jdk8:centos .

# 启动测试容器
docker run --name run -di bigkang/jdk8:centos

# 进入容器
docker exec -it run sh

# 打印版本
java -version
```



## 添加Arthas

​		如果需要将阿里的监控工具**Arthas**整和到jdk里面

```
wget https://arthas.aliyun.com/arthas-boot.jar
```

​		然后修改DockerFile,将arthas-boot添加进去即可

```sh
COPY arthas-boot.jar /arthas-boot.jar
```

​		然后重新打包		



# 构建带Ubantu的Jre镜像

先去官网下载JRE环境，然后删除多余的东西重新打包

下载Jre地址：[Jre地址](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

下载后解压

```sh
mkdir /tmp/jdk
tar -zxvf jre-8u231-linux-x64.tar.gz -C /tmp/jdk
cd /tmp/jdk
mv jre1.8.0_231 usr
cd usr
rm -rf COPYRIGHT LICENSE README release THIRDPARTYLICENSEREADME-JAVAFX.txt THIRDPARTYLICENSEREADME.txt Welcome.html
rm -rf   lib/plugin.jar \
           lib/ext/jfxrt.jar \
           bin/javaws \
           lib/javaws.jar \
           lib/desktop \
           plugin \
           lib/deploy* \
           lib/*javafx* \
           lib/*jfx* \
           lib/amd64/libdecora_sse.so \
           lib/amd64/libprism_*.so \
           lib/amd64/libfxplugins.so \
           lib/amd64/libglass.so \
           lib/amd64/libgstreamer-lite.so \
           lib/amd64/libjavafx*.so \
           lib/amd64/libjfx*.so

cd ..
tar zcvf jre8.tar.gz usr
```

然后创建Dockerfile，将jre8.tar.gz放入当前目录下，Dockerfile目录如下

```sh
echo "FROM boystar/ubantu
# 设置apt源
RUN touch /etc/apt/sources.list
RUN echo "deb http://mirrors.163.com/ubuntu precise main universe" > /etc/apt/sources.list
# 安装 vim ping ifconfig ip tcpdump nc curl iptables python 常用命令
RUN apt-get -y update
ADD jre8.tar.gz /" > Dockerfile
```

然后构建

```
docker build -t "xhbjdk1.8" .
```

# 关于镜像命令行无法使用中文问题

我们只需要指定环境，通常都是支持中文的

```
ENV LANG=C.UTF-8
```







我们在Docker file 中新增参数即可

测试添加jar包springboot项目以及arthas监控

```
echo "FROM kang/jdk1.8
COPY test-web.jar /app.jar
COPY arthas-boot.jar /arthas-boot.jar
ENV JAVA_OPTS=""
ENV APP_OPTS=""
CMD java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF8 -Duser.timezone=GMT+08 -jar /app.jar $APP_OPTS
EXPOSE 8081" > Dockerfile-app
```

```
docker build -t test-web -f Dockerfile-app . 
```

```
docker stop test-web
docker rm test-web 

docker run --name test-web -di -p 8081:8081 test-web
```

```
docker images|grep none|awk '{print $3 }'|xargs docker rmi
```

```
docker ps -a |grep /bin/sh |awk '{print $1 }'|xargs docker rm
```







```
docker run --name run -di bigkang/jdk8-arthas:latest
docker exec -it run sh

docker stop run
docker rm run
```



```sh
# 环境变量
export JAVA_HOME=/usr/local/java/jdk1.8.0_211
export JRE_HOME=$JAVA_HOME/jre
echo "FROM docker.io/jeanblanchard/alpine-glibc
MAINTAINER bigkangsix@qq.com
# 设置apk源
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub
RUN wget -q -O /tmp/glibc-2.29-r0.apk  https://github.91chifun.workers.dev//https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-2.29-r0.apk
RUN apk add /tmp/glibc-2.29-r0.apk 
# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone

RUN mkdir /usr/local/java
# 直接将JDK放入/usr/local/java
ADD jdk8.tar.gz /usr/local/java
# 设置环境变量
ENV LANG zh_CN.uft8
ENV JAVA_HOME=$JAVA_HOME
ENV JRE_HOME=$JRE_HOME
# 解决本地PATH导致容器异常，直接写死
ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$JAVA_HOME/bin" > Dockerfile

# 构建镜像
docker build -t bigkang/jdk8:alpine .
docker stop run
docker rm run
docker run --name run -di bigkang/jdk8:alpine
docker exec -it run sh
```

