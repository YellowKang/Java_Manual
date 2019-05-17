# 下载镜像

```
docker pull docker.io/mongo:latest
```

# 运行容器

```
docker run --name mongo -p 27017:27017 -d docker.io/mongo:latest --auth
```

## 生产环境运行

```
首先创建文件夹用于挂载目录

mkdir -p /docker/mongo/conf
mkdir -p /docker/mongo/data
赋予权限
chmod 777 /docker/mongo/conf
chmod 777 /docker/mongo/data
然后直接启动容器
docker run --name mongo -d \
-p 27017:27017 \
--privileged=true \
-v /docker/mongo/conf:/data/configdb \
-v /docker/mongo/data:/data/db \
docker.io/mongo:latest \
--auth
```



# 创建用户

```
-----进入容器
docker exec -it mongo bash
-----进入mongo
mongo
-----选中admin数据库
use admin
-----创建用户，root用户
db.createUser({user:"root",pwd:"bigkang",roles:[{role:'root',db:'admin'}]})
-----退出mongo
exit
```

# 给数据库创建用户

首先创建数据库，如果有则选中如果没有则创建

```
use test
```

然后创建用户

user为用户名，pwd为用户密码，role为角色，db为数据库

```
db.createUser({user:"bigkang",pwd:"bigkang",roles:[{role:'dbOwner',db:'test'}]})
```

然后需要认证登录

```
use test
db.auth('admin','admin')
```

## 角色权限

```
1. 数据库用户角色：read、readWrite;  
2. 数据库管理角色：dbAdmin、dbOwner、userAdmin；       
3. 集群管理角色：clusterAdmin、clusterManager、clusterMonitor、hostManager；
4. 备份恢复角色：backup、restore；
5. 所有数据库角色：readAnyDatabase、readWriteAnyDatabase、userAdminAnyDatabase、dbAdminAnyDatabase
6. 超级用户角色：root  
// 这里还有几个角色间接或直接提供了系统超级用户的访问（dbOwner 、userAdmin、userAdminAnyDatabase）
7. 内部角色：__system
```

```
read:允许用户读取指定数据库 
readWrite:允许用户读写指定数据库 
dbAdmin：允许用户在指定数据库中执行管理函数，如索引创建、删除，查看统计或访问system.profile 
userAdmin：允许用户向system.users集合写入，可以找指定数据库里创建、删除和管理用户 
clusterAdmin：只在admin数据库中可用，赋予用户所有分片和复制集相关函数的管理权限。 
readAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读权限 
readWriteAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读写权限 
userAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的userAdmin权限 
dbAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的dbAdmin权限。 
root：只在admin数据库中可用。超级账号，超级权限
```

# 创建集合

```
db.createCollection("testas");
```

