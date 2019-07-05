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
docker exec -it mysql-master1 bash
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
show slave status \G

启动slave
start slave

再次查看状态
show slave status
如果发现
Slave_IO_Running和Slave_SQL_Running都为true的话那么主从搭建成功
```

然后我们去master新建数据库测试一下就可以了

首先我们去master新建一个数据库然后新建一张表，并且插入一点数据，然后去slave查看是否添加成功

```
CREATE DATABASE IF NOT EXISTS test DEFAULT CHARSET utf8 COLLATE utf8_general_ci; 
use test;
DROP TABLE IF EXISTS `t_test_jpa`;
CREATE TABLE `t_test_jpa`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `deleted` int(11) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` int(11) NULL DEFAULT NULL,
  `birthday` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


INSERT INTO `t_test_jpa` VALUES (3, '2019-05-12 11:23:25', 0, '2019-06-24 14:09:23', 'string', 0, '1', '1', '1', '1', '1');
INSERT INTO `t_test_jpa` VALUES (4, '2019-05-12 16:07:31', NULL, '2019-05-12 16:07:31', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (5, '2019-05-12 16:07:49', NULL, '2019-05-12 16:07:49', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (6, '2019-05-12 16:07:50', NULL, '2019-05-12 16:07:50', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (7, '2019-05-12 16:07:51', NULL, '2019-05-12 16:07:51', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (8, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (9, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (10, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (11, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (12, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (13, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');

select * from t_test_jpa;
```

# MySQL主主复制（双master）

​		例如A主机和B主机主主复制，那么首先搭建，A节点是B节点的主从，然后搭建B节点是A节点的主从，相互主从是为双master主主复制

## 创建环境

首先我们先创建两个mysql的配置文件目录和数据目录

```
mkdir -p /docker/mysql-master1/conf/
mkdir -p /docker/mysql-master2/conf/
mkdir -p /docker/mysql-master1/data/
mkdir -p /docker/mysql-master2/data/
```

然后我们先去创建两个数据库的配置文件

```
echo "[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8" > /docker/mysql-master1/conf/my.cnf

echo "[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8" > /docker/mysql-master2/conf/my.cnf
```

然后运行两个容器

```
-------------启动master1

docker run -p 13301:3306 \
--name mysql-master1 \
-e MYSQL_ROOT_PASSWORD=123456 \
--privileged=true \
-v /docker/mysql-master1/data:/var/lib/mysql \
-v /docker/mysql-master1/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d mysql:5.7

-------------启动master2

docker run -p 13302:3306 \
--name mysql-master2 \
-e MYSQL_ROOT_PASSWORD=123456 \
--privileged=true \
-v /docker/mysql-master2/data:/var/lib/mysql \
-v /docker/mysql-master2/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d mysql:5.7
```

## mysql操作

进入两个数据库然后创建两个用户

```
进入容器
docker exec -it mysql-master1 bash
然后
mysql -u root -p
输入
123456

创建用户
use mysql
CREATE USER 'test2master'@'%' IDENTIFIED BY 'test2master';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'test2master'@'%';


进入容器
docker exec -it mysql-master2 bash
然后
mysql -u root -p
输入
123456

创建用户
use mysql
CREATE USER 'test2master'@'%' IDENTIFIED BY 'test2master';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'test2master'@'%';
```

然后我们去外面进行主从的准备

修改两个配置文件

```
vim /docker/mysql-master1/conf/my.cnf

[mysqld]
character-set-server=utf8
server-id=100
log-bin=mysql-bin
[client]
default-character-set=utf8
[mysql]

vim /docker/mysql-master2/conf/my.cnf

[mysqld]
character-set-server=utf8
server-id=101
log-bin=mysql-bin
relay_log=edu-mysql-relay-bin
[client]
default-character-set=utf8
[mysql]

--------------------------------
此处为注释
server-id=100  #新增serverid，注意在同一局域网两个配置不能一样
log-bin=mysql-bin #开启器binlog日志，主从核心配置
--------------------------------
```

然后我们重启两个mysql

```
docker restart mysql-master1
docker restart mysql-master2
```

## 执行主从开始

### 第一台开始主从

​		我们进入master1，我们要把master1作为master2的丛机

​		我们需要先进入第二台去查询它的binlog文件

```
docker exec -it mysql-master2 bash

mysql -u root -p
123456


show master status;
我们就能看到如下的数据，有可能名字不一样
```

![](img\2master-1-1.png)

记住这两个值，我们现在去第一台执行从命令

```
进入第一台master1
docker exec -it mysql-master1 bash
```

```
master_host ：Master的地址，指的是容器的独立ip,可以通过docker inspect --
master_port：Master的端口号，指的是容器的端口号
master_user：用于数据同步的用户
master_password：用于同步的用户的密码
master_log_file：指定 Slave 从哪个日志文件开始复制数据，即上文中提到的 File 字段的值
master_log_pos：从哪个 Position 开始读，即上文中提到的 Position 字段的值
master_connect_retry：如果连接失败，重试的时间间隔，单位是秒，默认是60秒
```

上面是下面这写命令的意思

```
change master to master_host='192.168.0.100', master_user='test2master', master_password='test2master', master_port=13302, master_log_file='mysql-bin.000001', master_log_pos=154, master_connect_retry=30;
```

如果此处配置错了可以

```
reset slave;
```

然后启动slave命令

```
start slave;
```

然后查看状态即可

```
show slave status \G;
```

如果我们看到就表示成功了

![](C:/Users/topcom/Documents/Java%E4%BF%AE%E4%BB%99%E6%89%8B%E5%86%8C/Java_Manual/%E6%95%B0%E6%8D%AE%E5%BA%93%E5%BF%AB%E9%80%9F%E5%B8%AE%E5%8A%A9/MYSQL/img/2master-test.png)

### 第二台相互主从

​	我们进入master2，我们要把master2作为master1的从机

​		我们需要先进入第一台去查询它的binlog文件

```
docker exec -it mysql-master1 bash

mysql -u root -p
123456


show master status;
我们就能看到如下的数据，有可能名字不一样
```

![](C:/Users/topcom/Documents/Java%E4%BF%AE%E4%BB%99%E6%89%8B%E5%86%8C/Java_Manual/%E6%95%B0%E6%8D%AE%E5%BA%93%E5%BF%AB%E9%80%9F%E5%B8%AE%E5%8A%A9/MYSQL/img/2master-1-1.png)

记住这两个值，我们现在去第二台执行从命令

```
进入第二台master2
docker exec -it mysql-master2 bash
```

```
master_host ：Master的地址，指的是容器的独立ip,可以通过docker inspect --
master_port：Master的端口号，指的是容器的端口号
master_user：用于数据同步的用户
master_password：用于同步的用户的密码
master_log_file：指定 Slave 从哪个日志文件开始复制数据，即上文中提到的 File 字段的值
master_log_pos：从哪个 Position 开始读，即上文中提到的 Position 字段的值
master_connect_retry：如果连接失败，重试的时间间隔，单位是秒，默认是60秒
```

上面是下面这写命令的意思

```
change master to master_host='192.168.0.100', master_user='test2master', master_password='test2master', master_port=13301, master_log_file='mysql-bin.000001', master_log_pos=154, master_connect_retry=30;
```

如果此处配置错了可以

```
reset slave;
```

然后启动slave命令

```
start slave;
```

然后查看状态即可

```
show slave status \G;
```

如果我们看到就表示成功了

![](img\2master-test.png)

## 测试双主功能

我们先进入master1新建一个表然后插入数据并且查询

```
CREATE DATABASE IF NOT EXISTS test DEFAULT CHARSET utf8 COLLATE utf8_general_ci; 
use test;
DROP TABLE IF EXISTS `t_test_jpa`;
CREATE TABLE `t_test_jpa`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `deleted` int(11) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` int(11) NULL DEFAULT NULL,
  `birthday` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


INSERT INTO `t_test_jpa` VALUES (3, '2019-05-12 11:23:25', 0, '2019-06-24 14:09:23', 'string', 0, '1', '1', '1', '1', '1');
INSERT INTO `t_test_jpa` VALUES (4, '2019-05-12 16:07:31', NULL, '2019-05-12 16:07:31', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (5, '2019-05-12 16:07:49', NULL, '2019-05-12 16:07:49', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (6, '2019-05-12 16:07:50', NULL, '2019-05-12 16:07:50', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (7, '2019-05-12 16:07:51', NULL, '2019-05-12 16:07:51', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (8, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (9, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (10, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (11, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (12, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
INSERT INTO `t_test_jpa` VALUES (13, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');

select * from t_test_jpa;
```

随后进入master2查看是否有数据然后我们在master2机器中也进行写入

```
use test;
INSERT INTO `t_test_jpa` VALUES (14, '2019-05-12 16:07:52', NULL, '2019-05-12 16:07:52', 'string', 0, 'string', 'string', 'string', 'string', 'string');
```

写入了之后我们再去master1查看14的id是否进来了

```
select * from t_test_jpa;
```

数据进入双主完成

# 后续

## 搭建失败提示



​					1、请先重新查询binlog日志。因为重启之后他的日志会变，可能这一点导致不成功

​					2、同一局域网内，server-id不能重复否则搭建失败



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

