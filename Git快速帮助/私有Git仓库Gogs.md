# 首先新建数据库

找一个能用的数据库用来存储信息

新建一个数据库叫做gogs

# 运行docker容器

首先创建数据挂载目录,然后授予权限

```
mkdir -p /docker/gogs/data
chmod 777 /docker/gogs/data

然后我们直接运行容器
	docker run -d -p 10022:22 -p 3000:3000 \
      --name=gogs \
      --privileged=true \
      -v /docker/gogs/data/:/data/ \
      gogs/gogs
```

# 配置Gogs

下面进入到配置

![](img\gogs-mysql配置.png)

数据库主机以及用户名密码配置，注意需要先新建数据库，然后就是下面配置

![](img\gogs-基础配置.png)

注意域名请修改成自己的域名以及端口号和ssh端口号示例如下，docker挂载时ssh端口号为10022

![](img\gogs-基础配置修改.png)

然后完成点击下面的立即安装

然后注册账号，第一个注册的账号为管理员，然后就能使用了

# 测试完一键删除环境

删除docker

```
docker stop gogs
docker rm gogs
```

删除本地环境

```
rm -rf /docker/gogs/data
```

数据库需要自己删除

# 备份容灾

备份挂载目录即可

需要恢复时复制挂载目录，然后安装时填写mysql地址即可