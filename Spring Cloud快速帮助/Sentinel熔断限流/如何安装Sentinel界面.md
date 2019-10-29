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

