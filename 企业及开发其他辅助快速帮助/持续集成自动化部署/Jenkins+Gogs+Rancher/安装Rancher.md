# 下载镜像

Rancher1使用server，Rancher2使用rancher

```
docker pull rancher/server
```

# 运行Rancher

直接运行不挂载文件，端口可以自行修改

```
docker run --name rancher -d -p 18080:8080 rancher/server
```

运行并且挂载数据文件

```
------创建挂载文件夹

mkdir -p /data/mysql/{datadir,conf.d,logs}
docker run -d --name rancher --link=mysqldb:db \
--restart=unless-stopped -p 8080:8080 -p 9345:9345 rancher/server:latest \
--db-host db --db-port 3306 --db-user cattle --db-pass cattle --db-name cattle \
--advertise-address mysql机器IP

```

这里记得修改mysql，的用户名密码，以及地址，还有数据库名字，和ip（也能不使用mysql，但是每次删除容器就会丢失数据）

<https://www.jianshu.com/p/b6cfd0fae18a> 博客地址



```
docker run -itd \
--name rancher-server \
--restart=always \
-p 8080:8080 \
rancher/server
```

