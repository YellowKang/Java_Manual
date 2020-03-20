# 首先下载jar包

https://github.com/alibaba/Sentinel/releases

如果不能联网则使用，在github上面下载相应的较为新的jar包，然后复制到linux主机

如果能联网

linux下载命令

```
wget https://github.com/alibaba/Sentinel/releases/download/1.6.0/sentinel-dashboard-1.6.0.jar
```

如果不能使用wget

```
yum install wget
```



# 然后运行

进入控制台输入

这里可以修改端口号

```
java -Dserver.port=9999 -Dcsp.sentinel.dashboard.server=localhost:9999 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.6.0.jar
```

然后我们直接访问localhost:9999就能访问了

可以使用Linux后台启动,注意修改此处的ip地址端口号，以及用户名密码

```
nohup java -Dserver.port=18858 -Dauth.username=bigkang -Dauth.password=bigkang -Dcsp.sentinel.dashboard.server=192.168.1.11:18858 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.6.3.jar > sentinel.log &

```

如下图所示

![](img\Sentinel-Dashboard.png)

# 新版Docker安装

拉取镜像

```sh
docker pull bladex/sentinel-dashboard:1.6.3
```

运行镜像

```
docker run -d \
--name sentinel \
-p 18858:8858 \
bladex/sentinel-dashboard:1.6.3
```

访问18858端口

```
默认用户名：sentinel
默认密  码：sentinel
```

# 自定义使用DockerFile构建（推荐）

创建DockerFile,复制一键执行

```sh
echo 'FROM java:alpine
MAINTAINER zhoutaoo "bigkangsix@qq.com"

# set environment
ENV BASE_DIR="/home/sentinel" \
    SERVER_PORT="9999" \
    DASHBOARD_SERVER="localhost:9999" \
    USERNAME="sentinel" \
    PASSWORD="sentinel" \
    PROJECT_NAME="sentinel-dashboard" \
    SENTINEL_DASHBOARD_VERSION="1.6.3" \
    JAVA_OPTS="" \
    TIME_ZONE="Asia/Shanghai"

ARG SENTINEL_DASHBOARD_VERSION=1.6.3

WORKDIR /$BASE_DIR

RUN set -x \
    && apk --no-cache add ca-certificates wget \
    && update-ca-certificates \
    && wget https://github.com/alibaba/Sentinel/releases/download/${SENTINEL_DASHBOARD_VERSION}/sentinel-dashboard-${SENTINEL_DASHBOARD_VERSION}.jar -P $BASE_DIR \
    && ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo '$TIME_ZONE' > /etc/timezone

ADD docker-entrypoint.sh bin/docker-entrypoint.sh

# set startup log dir
RUN mkdir -p logs \
	&& cd logs \
	&& touch start.out \
	&& ln -sf /dev/stdout start.out \
	&& ln -sf /dev/stderr start.out
RUN chmod +x bin/docker-entrypoint.sh

EXPOSE 9999
ENTRYPOINT ["bin/docker-entrypoint.sh"]' > /root/sentinel/Dockerfile
```

创建启动脚本

```sh
echo '#!/bin/sh
#startup Server
RUN_CMD="java"

# 应用参数
RUN_CMD="$RUN_CMD -Dserver.port:\"$SERVER_PORT\""
RUN_CMD="$RUN_CMD -Dcsp.sentinel.dashboard.server=\"$DASHBOARD_SERVER\""
RUN_CMD="$RUN_CMD -Dproject.name=\"$PROJECT_NAME\""
RUN_CMD="$RUN_CMD -Dauth.username=\"$USERNAME\""
RUN_CMD="$RUN_CMD -Dauth.password=\"$PASSWORD\""

RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD -jar"
RUN_CMD="$RUN_CMD sentinel-dashboard-\"SENTINEL_DASHBOARD_VERSION\".jar"

echo $RUN_CMD
eval $RUN_CMD' > /root/sentinel/docker-entrypoint.sh
```

构建Docker镜像

```sh
 docker build . -t sentinel:1.6.3
```

运行Docker容器

```java
docker run -d \
--name sentinel-dashboard \
-p 9999:9999 \
-e SERVER_PORT=9999 \
-e USERNAME=topcom \
-e PASSWORD=topcom123 \
sentinel:1.6.3
```

