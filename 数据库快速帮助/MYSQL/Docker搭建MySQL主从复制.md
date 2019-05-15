# 下载镜像

首先下载mysql的镜像，这里mysql的版本我们选择5.7

```
docker pull docker.io/mysql:5.7
```

# 运行容器

## 编写master配置文件

首先我们创建master的配置文件

```
mkdir -p /docker/mysql-master/conf
mkdir -p /docker/mysql-master/data
vim /docker/mysql-master/conf/my.cnf
```

复制下面内容

```
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

## 启动master

运行master容器，端口为3301，名字为mysql-master，然后提升它的权限，挂载数据目录，后台启动容器

```
docker run -p 3301:3306 \
--name mysql-master \
-e MYSQL_ROOT_PASSWORD=123456 \
--privileged=true \
-v /docker/mysql-master/data:/var/lib/mysql \
-v /docker/mysql-master/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```

## 编写slave配置文件

首先我们创建slave的配置文件

```
mkdir -p /docker/mysql-slave/conf
mkdir -p /docker/mysql-slave/data
vim /docker/mysql-slave/conf/my.cnf
```

复制下面内容

```
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

## 启动slave

运行slave容器，端口为3302，名字为mysql-slave，然后提升它的权限，挂载数据目录，挂载配置目录，后台启动容器

```
docker run -p 3302:3306 \
--name mysql-slave \
-e MYSQL_ROOT_PASSWORD=123456 \
--privileged=true \
-v /docker/mysql-slave/data:/var/lib/mysql \
-v /docker/mysql-slave/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```

# 进行主从操作

## 开启master的binlog日志

编辑配置文件

```
vim /docker/mysql-master/conf/my.cnf
```

将下面的添加到[mysqld]中

```
server-id=100  
log-bin=mysql-bin
--------------------------------------------------------------------
[mysqld]
## 同一局域网内注意要唯一
server-id=100  
## 开启二进制日志功能，可以随便取（关键）
log-bin=mysql-bin
```

保存后重启master

```
docker restart mysql-master
```

进入mysql中

```
docker exec -it mysql-master bash

然后
mysql -u root -p
输入
123456
```

创建用户

```
use mysql

CREATE USER 'slave'@'%' IDENTIFIED BY '123456';

GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'slave'@'%';
```

## 修改slave的配置

在外部找到刚才挂载的mysql配置文件

```
vim /docker/mysql-slave/conf/my.cnf
```

添加下面内容到mysqld中

```
server-id=101  
log-bin=mysql-slave-bin   
relay_log=edu-mysql-relay-bin  
-------------------------------------------------------------------
[mysqld]
## 设置server_id,注意要唯一
server-id=101  
## 开启二进制日志功能，以备Slave作为其它Slave的Master时使用
log-bin=mysql-slave-bin   
## relay_log配置中继日志
relay_log=edu-mysql-relay-bin  
```

重启docker

```
docker restart mysql-slave
```

## 执行slave命令

我们先去主机mysql中查询binlog日志的信息

```
show master status
```

这个信息在slave中需要用到

```
change master to master_host='39.108.158.31', master_user='slave', master_password='123456', master_port=3301, master_log_file='mysql-bin.000001', master_log_pos= 154, master_connect_retry=30;
```

创建slave配置详情

```
master_host ：Master的地址，指的是容器的独立ip,可以通过docker inspect --
master_port：Master的端口号，指的是容器的端口号
master_user：用于数据同步的用户
master_password：用于同步的用户的密码
master_log_file：指定 Slave 从哪个日志文件开始复制数据，即上文中提到的 File 字段的值
master_log_pos：从哪个 Position 开始读，即上文中提到的 Position 字段的值
master_connect_retry：如果连接失败，重试的时间间隔，单位是秒，默认是60秒
```

然后查看状态

```
show slave status

启动slave
start slave

再次查看状态
show slave status
如果发现
Slave_IO_Running和Slave_SQL_Running都为true的话那么主从搭建成功
```

然后我们去master新建数据库测试一下就可以了

# 后续

## 测试完一键删除环境

### 删除Docker环境

```
docker stop mysql-master
docker stop mysql-slave

docker rm mysql-master
docker rm mysql-slave
```

镜像删除

```
docker rmi docker.io/mysql:5.7
```

### 删除Linux环境

删除本地挂载文件目录

```
rm -rf  /docker/mysql-master
rm -rf  /docker/mysql-slave
```

