# 下载镜像

```java
docker pull docker.io/mongo:latest
```

# 运行容器	

```java
docker run --name mongo -p 27017:27017 -d docker.io/mongo:latest --auth
```

## 生产环境运行

```sh
#首先创建文件夹用于挂载目录

mkdir -p /docker/mongo/{conf,data}
#赋予权限
chmod 777 /docker/mongo/conf
chmod 777 /docker/mongo/data
#然后直接启动容器
docker run --name mongo -d \
-p 27018:27018 \
--privileged=true \
-v /docker/mongo/conf:/data/configdb \
-v /docker/mongo/data:/data/db \
docker.io/mongo:latest \
--auth
```

## 自定义配置文件运行

```sh
#首先创建文件夹用于挂载目录

mkdir -p /docker/mongo/{conf,data}
#赋予权限
chmod 777 /docker/mongo/conf
chmod 777 /docker/mongo/data

#新建配置文件
touch /docker/mongo/conf/mongo.conf

#然后放入如下配置
# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=27018
auth=true

#我们此处更换端口并且开启验证

#然后直接启动容器，注意此处后面执行mongod命令表示执行容器内部的配置文件，所以根据挂载文件再执行容器内部的路径
docker run --name mongo -d \
-p 27018:27018 \
--privileged=true \
-v /docker/mongo/conf:/data/configdb \
-v /docker/mongo/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf
```

# 创建用户

```shell
#-----进入容器
docker exec -it mongo bash
#-----进入mongo
mongo
#-----选中admin数据库
use admin
#-----创建用户，root用户
db.createUser({user:"root",pwd:"root",roles:[{role:'root',db:'admin'}]})
#-----退出mongo
exit
```

# 给数据库创建用户

首先创建数据库，如果有则选中如果没有则创建

```sql
use test
```

然后创建用户

user为用户名，pwd为用户密码，role为角色，db为数据库

```sql
db.createUser({user:"kang",pwd:"kang",roles:[{role:'dbOwner',db:'config'}]})
```

然后需要认证登录

```sql
use test
db.auth('kang','kang')

db.createUser({user:"graylog",pwd:"graylog",roles:[{role:'dbOwner',db:'graylog'}]})
```

## 角色权限

```java
1. 数据库用户角色：read、readWrite;  
2. 数据库管理角色：dbAdmin、dbOwner、userAdmin；       
3. 集群管理角色：clusterAdmin、clusterManager、clusterMonitor、hostManager；
4. 备份恢复角色：backup、restore；
5. 所有数据库角色：readAnyDatabase、readWriteAnyDatabase、userAdminAnyDatabase、dbAdminAnyDatabase
6. 超级用户角色：root  
// 这里还有几个角色间接或直接提供了系统超级用户的访问（dbOwner 、userAdmin、userAdminAnyDatabase）
7. 内部角色：__system
```

```json
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

```shell
db.createCollection("testas");
```

# Mongodb集群搭建

mongodb 集群搭建的方式有三种：

1. 主从备份（Master - Slave）模式，或者叫主从复制模式。
2. 副本集（Replica Set）模式。
3. 分片（Sharding）模式。

> 其中，第一种方式基本没什么意义，官方也不推荐这种方式搭建。另外两种分别就是副本集和分片的方式。今天介绍副本集的方式搭建mongodb高可用集群

​	

## Mongo分片集群(非高可用,不推荐)

### 搭建ConfigServer

首先我们搭建两个config-server



创建两个config-server的配置文件

```sh
#创建config-server-1
mkdir -p /docker/mongo-cluster/mongo-server1/{data,conf}

#创建config-server-2
mkdir -p /docker/mongo-cluster/mongo-server2/{data,conf}
```

然后配置文件中配置端口

```sh
#创建第一个配置文件
#写入配置信息，端口号

echo "# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=20011
auth=false" > /docker/mongo-cluster/mongo-server1/conf/mongo.conf

#创建第二个配置文件
#写入配置信息，端口号

echo "# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=20012
auth=false" > /docker/mongo-cluster/mongo-server2/conf/mongo.conf
```

然后启动容器

```sh
#启动Server1

docker run --name mongo-server1 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/mongo-server1/conf:/data/configdb \
-v /docker/mongo-cluster/mongo-server1/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf --configsvr --replSet "rs_config_server" --bind_ip_all 

docker run --name mongo-server2 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/mongo-server2/conf:/data/configdb \
-v /docker/mongo-cluster/mongo-server2/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf --configsvr --replSet "rs_config_server" --bind_ip_all 
```

然后进入容器初始化



```sh
#进入容器
docker exec -it mongo-server1 bash

mongo -port 20011

#初始化
rs.initiate(
{
_id: "rs_config_server",
configsvr: true,
members: [
{ _id : 0, host : "114.67.80.169:20011" },
{ _id : 1, host : "114.67.80.169:20012" }
]
}
);
```

如果ok为1表示成功

### 创建分片集群

下面我们给每个server创建2个分片

创建挂载文件

```sh
#创建config-server-1的两个分片目录
mkdir -p /docker/mongo-cluster/mongo-server1-shard1/{data,conf}
mkdir -p /docker/mongo-cluster/mongo-server1-shard2/{data,conf}

#创建config-server-2的两个分片目录
mkdir -p /docker/mongo-cluster/mongo-server2-shard1/{data,conf}
mkdir -p /docker/mongo-cluster/mongo-server2-shard2/{data,conf}
```

创建配置文件

```sh
#创建config-server-1的两个分片配置文件

echo "# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=20021
auth=false" > /docker/mongo-cluster/mongo-server1-shard1/conf/mongo.conf

echo "# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=20022
auth=false" > /docker/mongo-cluster/mongo-server1-shard2/conf/mongo.conf
```

```sh
#创建config-server-2的两个分片配置文件

echo "# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=20023
auth=false" > /docker/mongo-cluster/mongo-server2-shard1/conf/mongo.conf

echo "# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=20024
auth=false" > /docker/mongo-cluster/mongo-server2-shard2/conf/mongo.conf
```

然后启动容器

```sh
#启动config-server-1的两个分片容器
docker run --name mongo-server1-shard1 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/mongo-server1-shard1/conf:/data/configdb \
-v /docker/mongo-cluster/mongo-server1-shard1/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf --shardsvr --replSet "rs_shard_server1" --bind_ip_all

docker run --name mongo-server1-shard2 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/mongo-server1-shard2/conf:/data/configdb \
-v /docker/mongo-cluster/mongo-server1-shard2/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf --shardsvr --replSet "rs_shard_server1" --bind_ip_all
```

```sh
#启动config-server-2的两个分片容器
docker run --name mongo-server2-shard1 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/mongo-server2-shard1/conf:/data/configdb \
-v /docker/mongo-cluster/mongo-server2-shard1/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf --shardsvr --replSet "rs_shard_server2" --bind_ip_all

docker run --name mongo-server2-shard2 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/mongo-server2-shard2/conf:/data/configdb \
-v /docker/mongo-cluster/mongo-server2-shard2/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf --shardsvr --replSet "rs_shard_server2" --bind_ip_all
```

进入第一个分片

```sh
 docker exec  -it mongo-server1-shard1 bash
 mongo -port 20021
 
#进行分片
 rs.initiate(
{
_id : "rs_shard_server1",
members: [
{ _id : 0, host : "114.67.80.169:20021" },
{ _id : 1, host : "114.67.80.169:20022" }
]
}
);
```

进入第二个分片

```sh
 docker exec  -it mongo-server2-shard1 bash
 mongo -port 20023
 
 #进行分片
 rs.initiate(
{
_id : "rs_shard_server2",
members: [
{ _id : 0, host : "182.61.2.16:20023" },
{ _id : 1, host : "182.61.2.16:20024" }
]
}
);
```

### 安装Mongos

创建挂载文件

```sh
mkdir -p /docker/mongo-cluster/mongos1/{data,conf}

echo "# mongodb.conf
logappend=true
# bind_ip=127.0.0.1
port=20099
auth=false" > /docker/mongo-cluster/mongos1/conf/mongo.conf
```

然后启动Mongo

```sh
docker run --name mongo-mongos1 -d \
--net=host \
--privileged=true \
-p 27017:27017 \
--entrypoint "mongos" \
-v /docker/mongo-cluster/mongos1/conf:/data/configdb \
-v /docker/mongo-cluster/mongos1/data:/data/db \
docker.io/mongo:latest \
--configdb rs_config_server/114.67.80.169:20011,114.67.80.169:20012 --bind_ip_all
```

mongo添加分片组

```
sh.addShard("rs_shard_server1/114.67.80.169:20021,114.67.80.169:20022")
sh.addShard("rs_shard_server2/182.61.2.16:20023,182.61.2.16:20024")
```

新建数据启用分片

```
sh.enableSharding("test")

对test.order的_id进行哈希分片
sh.shardCollection("test.order", {"_id": "hashed" })
```

```
插入数据后查看分片数据
use test
for (i = 1; i <= 1000; i=i+1){db.order.insert({'price': 1})}
```



## Mongo分片集群高可用+权限（推荐）

### 简介以及概述

​		首先我们先来了解一下Mongo集群的概念，Mongo集群有3个主要组件

​				ConfigServer：在集群中扮演存储整个集群的配置信息，负责配置存储，如果需要高可用的ConfigServer那么需要3个节点。

​				Shard：分片，存储真实的数据，每一个Shard分片都负责存储集群中的数据，例如一个集群有3个分片，然后我们定义分片规则为哈希，那么整个集群的数据就会（分割）成3份分布在不同的分片中，那么分片是特别重要的，如果集群中的一个分片全部崩溃了那么集群将不可用，所以我们要保证集群的高可用，那么我们需要一个分片配置3个节点，2个副本集一个仲裁节点，仲裁节点类似于Redis的哨兵模式，如果发现主节点挂了那么让另一个副本集进行数据存储。

​				Mongos：Mongos我们可以理解为整个集群的入口，类似于Kafka的Broker代理，也就是客户端，我们通过客户端连接集群进行查询。

​		下面是MongoDB的官方集群架构图，我们看到Mongos是一个路由，他们的信息都存储在ConfigServer中，我们通过Mongos进行添加，然后根据条件将数据进行分片到分片的副本集中

![](https://docs.mongodb.com/manual/_images/sharded-cluster-production-architecture.bakedsvg.svg)



那么我们先来总结一下我们搭建一个集群需要多少个Mongo

mongos 				： 	3台

configserver		 ： 	3台

shard					 ：	 3片

每一片shard 分别 部署两个副本集和一个仲裁节点  ： 3台



那么就是 3 + 3 + 3 * 3 = 15  台，我这里演示采用3台服务器

​	114.67.80.169		4核16g			部署一个configserver，一个mongos，2个分片组

​	182.61.2.16			2核4g			   部署一个configserver，一个mongos，1个分片组

​	106.12.113.62		1核2g			   部署一个configserver，一个mongos，不搭建分片组

由于此处服务器原因所以不是均衡分布，请根据自身实际情况搭建

|      角色      |      ip       | 端口  |
| :------------: | :-----------: | :---: |
| config-server1 | 114.67.80.169 | 20011 |
| config-server2 |  182.61.2.16  | 20012 |
| config-server3 | 106.12.113.62 | 20013 |
|    mongos1     | 114.67.80.169 | 20021 |
|    mongos2     |  182.61.2.16  | 20022 |
|    mongos3     | 106.12.113.62 | 20023 |
| shard1-server1 | 114.67.80.169 | 20031 |
| shard1-server2 | 114.67.80.169 | 20032 |
| shard1-server3 | 114.67.80.169 | 20033 |
| shard2-server1 | 114.67.80.169 | 20034 |
| shard2-server2 | 114.67.80.169 | 20035 |
| shard2-server3 | 114.67.80.169 | 20036 |
| shard3-server1 |  182.61.2.16  | 20037 |
| shard3-server2 |  182.61.2.16  | 20038 |
| shard3-server3 |  182.61.2.16  | 20039 |

### 搭建ConfigServer

​		我们先来搭建ConfigServer，因为我们知道搭建的话一定要高可用而且一定要权限这里mongo之间通信采用秘钥文件，所以我们先进行生成

#### 搭建config-server1

创建挂载文件目录

```sh
mkdir -p /docker/mongo-cluster/config-server1/{data,conf}
```

写入配置文件

```sh
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20011  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: configsvr
sharding:
  clusterRole: configsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/config-server1/conf/mongo.conf
```

然后生成keyFile

```
openssl rand -base64 756  > /docker/mongo-cluster/config-server1/conf/mongo.key
```

文件如下，我们，之后我们所以key都采用这个（请采用自己生成的key）

```
tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU
```

写入key文件

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/config-server1/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/config-server1/conf/mongo.key
```

然后启动config-server1容器

```sh
docker run --name mongo-server1 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/config-server1/conf:/data/configdb \
-v /docker/mongo-cluster/config-server1/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf
```

#### 搭建config-server2

创建挂载文件目录

```sh
mkdir -p /docker/mongo-cluster/config-server2/{data,conf}
```

写入配置文件

写入配置文件

```sh
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20012  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: configsvr
sharding:
  clusterRole: configsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/config-server2/conf/mongo.conf
```

文件如下，我们，之后我们所以key都采用这个（请采用自己生成的key）

写入key文件

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/config-server2/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/config-server2/conf/mongo.key
```

然后启动config-server2容器

```sh
docker run --name mongo-server2 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/config-server2/conf:/data/configdb \
-v /docker/mongo-cluster/config-server2/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf
```

#### 搭建config-server3

创建挂载文件目录

```sh
mkdir -p /docker/mongo-cluster/config-server3/{data,conf}
```

写入配置文件

```sh
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20013  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: configsvr
sharding:
  clusterRole: configsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/config-server3/conf/mongo.conf
```

文件如下，我们，之后我们所以key都采用这个（请采用自己生成的key）

写入key文件

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/config-server3/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/config-server3/conf/mongo.key
```

然后启动config-server3容器

```sh
docker run --name mongo-server3 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/config-server3/conf:/data/configdb \
-v /docker/mongo-cluster/config-server3/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf
```

#### 初始化config-server

进入第一台容器

```
docker exec -it mongo-server1 bash
mongo -port 20011
```

输入

```
rs.initiate(
  {
    _id: "configsvr",
    members: [
      { _id : 1, host : "114.67.80.169:20011" },
      { _id : 2, host : "182.61.2.16:20012" },
      { _id : 3, host : "106.12.113.62:20013" }
    ]
  }
)
```

如果返回ok则成功

然后我们创建用户

```sh
use admin
db.createUser({user:"root",pwd:"root",roles:[{role:'root',db:'admin'}]})
```

### 搭建Shard分片组

​		由于mongos是客户端，所以我们先搭建好config以及shard之后再搭建mongos。

#### 搭建shard1分片组

在同一台服务器上初始化一组分片

创建挂载文件

```sh
mkdir -p /docker/mongo-cluster/shard1-server1/{data,conf}
mkdir -p /docker/mongo-cluster/shard1-server2/{data,conf}
mkdir -p /docker/mongo-cluster/shard1-server3/{data,conf}
```

配置配置文件

```sh
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20031  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard1
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard1-server1/conf/mongo.conf
------------------------------------------------------------------------------
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20032  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard1
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard1-server2/conf/mongo.conf
------------------------------------------------------------------------------
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20033  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard1
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard1-server3/conf/mongo.conf
```

创建keyfile

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/shard1-server1/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/shard1-server1/conf/mongo.key

#复制
cp /docker/mongo-cluster/shard1-server1/conf/mongo.key /docker/mongo-cluster/shard1-server2/conf/mongo.key

cp /docker/mongo-cluster/shard1-server1/conf/mongo.key /docker/mongo-cluster/shard1-server3/conf/mongo.key
```

运行shard1分片组

```sh
docker run --name shard1-server1 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard1-server1/conf:/data/configdb \
-v /docker/mongo-cluster/shard1-server1/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf

docker run --name shard1-server2 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard1-server2/conf:/data/configdb \
-v /docker/mongo-cluster/shard1-server2/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf

docker run --name shard1-server3 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard1-server3/conf:/data/configdb \
-v /docker/mongo-cluster/shard1-server3/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf
```

#### 初始化shard1分片组

并且制定第三个副本集为仲裁节点

```
 docker exec  -it shard1-server1 bash
 mongo -port 20031
 
#进行副本集配置
 rs.initiate(
{
_id : "shard1",
members: [
{ _id : 0, host : "114.67.80.169:20031" },
{ _id : 1, host : "114.67.80.169:20032" },
{ _id : 2, host : "114.67.80.169:20033",arbiterOnly:true }
]
}
);
```

返回ok后创建用户

```
use admin
db.createUser({user:"root",pwd:"root",roles:[{role:'root',db:'admin'}]})
```

然后退出，分片组1搭建完成

#### 搭建shard2分片组

在同一台服务器上初始化一组分片

创建挂载文件

```sh
mkdir -p /docker/mongo-cluster/shard2-server1/{data,conf}
mkdir -p /docker/mongo-cluster/shard2-server2/{data,conf}
mkdir -p /docker/mongo-cluster/shard2-server3/{data,conf}
```

配置配置文件

```sh
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20034  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard2
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard2-server1/conf/mongo.conf
------------------------------------------------------------------------------
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20035  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard2
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard2-server2/conf/mongo.conf
------------------------------------------------------------------------------
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20036  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard2
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard2-server3/conf/mongo.conf
```

创建keyfile

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/shard2-server1/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/shard2-server1/conf/mongo.key

#复制
cp /docker/mongo-cluster/shard2-server1/conf/mongo.key /docker/mongo-cluster/shard2-server2/conf/mongo.key

cp /docker/mongo-cluster/shard2-server1/conf/mongo.key /docker/mongo-cluster/shard2-server3/conf/mongo.key
```

运行shard2分片组

```sh
docker run --name shard2-server1 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard2-server1/conf:/data/configdb \
-v /docker/mongo-cluster/shard2-server1/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf

docker run --name shard2-server2 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard2-server2/conf:/data/configdb \
-v /docker/mongo-cluster/shard2-server2/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf

docker run --name shard2-server3 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard2-server3/conf:/data/configdb \
-v /docker/mongo-cluster/shard2-server3/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf
```

#### 初始化shard2分片组

并且制定第三个副本集为仲裁节点

```
 docker exec  -it shard2-server1 bash
 mongo -port 20034
 
#进行副本集配置
 rs.initiate(
{
_id : "shard2",
members: [
{ _id : 0, host : "114.67.80.169:20034" },
{ _id : 1, host : "114.67.80.169:20035" },
{ _id : 2, host : "114.67.80.169:20036",arbiterOnly:true }
]
}
);
```

返回ok后创建用户

```
use admin
db.createUser({user:"root",pwd:"root",roles:[{role:'root',db:'admin'}]})
```

然后退出，分片组2搭建完成

#### 搭建shard3分片组

在同一台服务器上初始化一组分片

创建挂载文件

```sh
mkdir -p /docker/mongo-cluster/shard3-server1/{data,conf}
mkdir -p /docker/mongo-cluster/shard3-server2/{data,conf}
mkdir -p /docker/mongo-cluster/shard3-server3/{data,conf}
```

配置配置文件

```sh
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20037  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard3
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard3-server1/conf/mongo.conf
------------------------------------------------------------------------------
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20038  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard3
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard3-server2/conf/mongo.conf
------------------------------------------------------------------------------
echo "
# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 20039  #端口号
#  bindIp: 127.0.0.1	#绑定ip
replication:
  replSetName: shard3
sharding:
  clusterRole: shardsvr
security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径 "  > /docker/mongo-cluster/shard3-server3/conf/mongo.conf
```

创建keyfile

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/shard3-server1/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/shard3-server1/conf/mongo.key

#复制
cp /docker/mongo-cluster/shard3-server1/conf/mongo.key /docker/mongo-cluster/shard3-server2/conf/mongo.key

cp /docker/mongo-cluster/shard3-server1/conf/mongo.key /docker/mongo-cluster/shard3-server3/conf/mongo.key
```

运行shard3分片组

```sh
docker run --name shard3-server1 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard3-server1/conf:/data/configdb \
-v /docker/mongo-cluster/shard3-server1/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf

docker run --name shard3-server2 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard3-server2/conf:/data/configdb \
-v /docker/mongo-cluster/shard3-server2/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf

docker run --name shard3-server3 -d \
--net=host \
--privileged=true \
-v /docker/mongo-cluster/shard3-server3/conf:/data/configdb \
-v /docker/mongo-cluster/shard3-server3/data:/data/db \
docker.io/mongo:latest mongod -f /data/configdb/mongo.conf
```

#### 初始化shard3分片组

并且制定第三个副本集为仲裁节点

```
 docker exec  -it shard3-server1 bash
 mongo -port 20037
 
#进行副本集配置
 rs.initiate(
{
_id : "shard3",
members: [
{ _id : 0, host : "182.61.2.16:20037" },
{ _id : 1, host : "182.61.2.16:20038" },
{ _id : 2, host : "182.61.2.16:20039",arbiterOnly:true }
]
}
);
```

返回ok后创建用户

```sh
use admin
# 创建用户
db.createUser({user:"root",pwd:"root",roles:[{role:'root',db:'admin'}]})
```

然后退出，分片组3搭建完成

### 搭建Mongos

#### 搭建Mongos1

创建配置文件

```
mkdir -p /docker/mongo-cluster/mongos1/{data,conf}
```

填入配置文件,这里我们删除了认证的信息，因为mongos是不能设置认证的，他也是用的前面使用的密码即可，如configserver的密码

```sh
echo "net:
  port: 20021  #端口号
sharding:
  configDB: configsvr/114.67.80.169:20011,182.61.2.16:20012,106.12.113.62:20013
security:
  keyFile: /data/configdb/mongo.key #keyFile路径
"  > /docker/mongo-cluster/mongos1/conf/mongo.conf
```

创建keyfile

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/mongos1/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/mongos1/conf/mongo.key
```

运行mongos1

```
docker run --name mongos1 -d \
--net=host \
--privileged=true \
--entrypoint "mongos" \
-v /docker/mongo-cluster/mongos1/conf:/data/configdb \
-v /docker/mongo-cluster/mongos1/data:/data/db \
docker.io/mongo:latest -f /data/configdb/mongo.conf --bind_ip_all
```

#### 搭建Mongos2

创建配置文件

```
mkdir -p /docker/mongo-cluster/mongos2/{data,conf}
```

填入配置文件,这里我们删除了认证的信息，因为mongos是不能设置认证的，他也是用的前面使用的密码即可，如configserver的密码

```sh
echo "net:
  port: 20022  #端口号
sharding:
  configDB: configsvr/114.67.80.169:20011,182.61.2.16:20012,106.12.113.62:20013
security:
  keyFile: /data/configdb/mongo.key #keyFile路径
"  > /docker/mongo-cluster/mongos2/conf/mongo.conf
```

创建keyfile

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/mongos2/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/mongos2/conf/mongo.key
```

运行mongos2

```
docker run --name mongos2 -d \
--net=host \
--privileged=true \
--entrypoint "mongos" \
-v /docker/mongo-cluster/mongos2/conf:/data/configdb \
-v /docker/mongo-cluster/mongos2/data:/data/db \
docker.io/mongo:latest -f /data/configdb/mongo.conf  --bind_ip_all
```

#### 搭建Mongos3

创建配置文件

```
mkdir -p /docker/mongo-cluster/mongos3/{data,conf}
```

填入配置文件,这里我们删除了认证的信息，因为mongos是不能设置认证的，他也是用的前面使用的密码即可，如configserver的密码

```sh
echo "net:
  port: 20023  #端口号
sharding:
  configDB: configsvr/114.67.80.169:20011,182.61.2.16:20012,106.12.113.62:20013
security:
  keyFile: /data/configdb/mongo.key #keyFile路径
"  > /docker/mongo-cluster/mongos3/conf/mongo.conf
```

创建keyfile

```sh
echo "tsUtJb3TeueNR8Mehr7ZLmZx82qfuCQ7LfLjUvQA7hNfWSomyNDISXDiSTJQEVym
OhXXzwB+0iv+czxi4qe9tAP8fMDuXpieZreysg4gxZ1VoFC1q39IrUDAEpCikSKS
abGl8RTEOM/GzVM8BATjaGHuBIi2osBAPg2Hzi+/u9ORbb4I4jzvgStcPcozRgOZ
5kPvXBybanV8MhLA6MfG1rcUiTkGoKb65YuWIfPuuF7PTWZe4VcF+iU6jgw73juZ
pbcZR5oTKvOWz89KCRTmQqHRexmJyn+NJcIGHFS/sZSJXE8LFPBZ+XLGYrtmDqo0
9tA1x8R+u32OJ7iOAU1mFkCHe2Uoph6aeVx/jZx1FgFjW0afT4ou2w7QHsdF0WRn
nskJ1FCA8NKzhYYgv/YrpyAChhTgd//gbWr028qz1W1POpBkj4muKUk7OTHRV6bs
qr2C73bqcZ1n2s60k6WbRUd6LP6POHR93wvi5EaXyorSMBIGiSD1Kyr/iqO7gD4C
GN8iA3MqF+fW5nKn1yBNEfPGoFk+p0EaxIAhfLEpzSRb3Wt5XLOWP7CBGuTo7KST
Y5HAcblqN7TByQhLdH5MZJ4FhfTZ0yNKTOVQdZUYRb5GGgS0GZfUk4bndLTkHrJd
tcR4WreHpz7ccncE5Vt8TGglrEx0noFVBqLqTdrqFUFpvWoukw/eViacLlBHKOxB
QVgfo4491znNMmthqGimVI7TFV706AvVJGqoIyuiFZRE5qx5MsOlIXiFwA3ue1Lo
kiFq5c6ImvS0R9LGu1Xcr0REYN53/bBVgGzJovEn7IIrHChYow7TkTLf/LsnjL3m
rmkDRgzA0C5i6fXgKkJdBhvvA521Yf75YP9n+819NUTZbtGIxRnP07pMS9RP4TjS
ZSd9an5yc7IpnL0gE4Pmnvf8LM86WTt9hZWKrE2LeQPEFgFl/Eq5NH60Zd4utxfi
qM2FH7aNsEukoAvA2v3All1wsM2kn4fMa89Hwui9h4xMy5tU"  > /docker/mongo-cluster/mongos3/conf/mongo.key

#处理权限为400

chmod 400 /docker/mongo-cluster/mongos3/conf/mongo.key
```

运行mongos3

```
docker run --name mongos3 -d \
--net=host \
--privileged=true \
--entrypoint "mongos" \
-v /docker/mongo-cluster/mongos3/conf:/data/configdb \
-v /docker/mongo-cluster/mongos3/data:/data/db \
docker.io/mongo:latest -f /data/configdb/mongo.conf --bind_ip_all
```

#### 配置所有mongos

进入第一台mongos

```
docker exec -it mongos1 bash
mongo -port 20021
```

先登录（使用前面设置的root用户密码）

```
use admin;
db.auth("root","root");
```

进行配置分片信息

```
sh.addShard("shard1/114.67.80.169:20031,114.67.80.169:20032,114.67.80.169:20033")
sh.addShard("shard2/114.67.80.169:20034,114.67.80.169:20035,114.67.80.169:20036")
sh.addShard("shard3/182.61.2.16:20037,182.61.2.16:20038,182.61.2.16:20039")
```

全部返回ok则成功

去其他两台mongos执行

mongos2

```
docker exec -it mongos2 bash
mongo -port 20022

use admin;
db.auth("root","root");

sh.addShard("shard1/114.67.80.169:20031,114.67.80.169:20032,114.67.80.169:20033")
sh.addShard("shard2/114.67.80.169:20034,114.67.80.169:20035,114.67.80.169:20036")
sh.addShard("shard3/182.61.2.16:20037,182.61.2.16:20038,182.61.2.16:20039")
```

mongos3

```
docker exec -it mongos3 bash
mongo -port 20023

use admin;
db.auth("root","root");

sh.addShard("shard1/114.67.80.169:20031,114.67.80.169:20032,114.67.80.169:20033")
sh.addShard("shard2/114.67.80.169:20034,114.67.80.169:20035,114.67.80.169:20036")
sh.addShard("shard3/182.61.2.16:20037,182.61.2.16:20038,182.61.2.16:20039")
```

### 功能测试

#### 数据库分片

```
sh.enableSharding("test")

对test库的test集合的_id进行哈希分片
sh.shardCollection("test.test", {"_id": "hashed" })
```

创建用户

```
use admin;
db.auth("root","root");
use test;
db.createUser({user:"kang",pwd:"kang",roles:[{role:'dbOwner',db:'test'}]})
```

插入数据

```
use test
for (i = 1; i <= 300; i=i+1){db.test.insert({'name': "bigkang"})}
```



# 配置文件

## ConfigServer配置

openssl rand -base64 756  > mongo.key

```properties

# 日志文件
#systemLog:
#  destination: file
#  logAppend: true
#  path: /var/log/mongodb/data0802.log

#  网络设置
net:
  port: 27018  #端口号
#  bindIp: 127.0.0.1	#绑定ip

security:
  authorization: enabled #是否开启认证
  keyFile: /data/configdb/mongo.key #keyFile路径
```



# 环境清理

清空server1两个分片数据

```
docker stop mongo-server1-shard{1,2}
docker rm mongo-server1-shard{1,2}

rm -rf /docker/mongo-cluster/mongo-server1-shard{1,2}
```

清空server2两个分片数据

```
docker stop mongo-server2-shard{1,2}
docker rm mongo-server2-shard{1,2}

rm -rf /docker/mongo-cluster/mongo-server2-shard{1,2}
```





```properties
docker ps -a| grep mongo | grep -v grep| awk '{print "docker stop "$1}'|sh
docker ps -a| grep mongo | grep -v grep| awk '{print "docker rm "$1}'|sh
```

