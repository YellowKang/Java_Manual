# 下载镜像

```sh
docker pull apache/zeppelin:0.8.0
```

# 运行镜像

根据实际使用情况进行参数配置

```sh
docker run -d \
-p 18089:8080 \
--name zeppelin \
-v  /docker/zeppelin/logs:/logs \
-v /docker/zeppelin/notebook:/notebook \
-e HOST_IP=0.0.0.0 \
-e ZEPPELIN_MEM:" -Xms256m -Xmx256m" \
-e ZEPPELIN_LOG_DIR="/logs" \
-e ZEPPELIN_NOTEBOOK_DIR="/notebook" \
--restart=always \
apache/zeppelin:0.8.0
```

# 添加权限

首先进入容器内部，然后进入conf文件夹下,将zeppelin-site.xml.template复制修改名字

```sh
cd conf
cp zeppelin-site.xml.template zeppelin-site.xml
```

然后修改，现在容器内部安装vim 编辑器

```xml
apt-get update
apt-get install vim

然后编辑
vim zeppelin-site.xml
然后修改，默认是匿名模式登录，需要修改为访问登录权限

将
<property>
  <name>zeppelin.anonymous.allowed</name>
  <value>false</value>
  <description>Anonymous user allowed by default</description>
</property>
修改为true，如下
<property>
  <name>zeppelin.anonymous.allowed</name>
  <value>true</value>
  <description>Anonymous user allowed by default</description>
</property>
```

然后复制shiro的初始化文件

```sh
cp shiro.ini.template shiro.ini
```

然后去里面的user上添加东西

```sh
例如添加bigkang用户密码为bigkang（注意放置位置）
bigkang=bigkang
```

