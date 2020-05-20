# 什么是Mycat

​		Mycat 是一个开源的分布式数据库系统，但是由于真正的数据库需要存储引擎，而 Mycat 并没有存 储引擎，所以并不是完全意义的分布式数据库系统

 		Mycat是一个数据库中间件，也可以理解为是数据库代理。在架构体系中是位于数据库和应用层之间的一个组件，并且对于应用层是透明的，即数据库 感受不到Mycat的存在，认为是直接连接的mysql数据库（实际上是连接的mycat,mycat实现了mysql的原生协议）

 		Mycat的三大功能：

​				分表

​				读写分离

​				主从切换

# 为什么需要Mycat

​		 当我们的数据量日与俱增的时候，一旦达到了大百万级，甚至上千万级，那么我们的一个查询效率会变得非常低下，以及运维异常麻烦，优化也不太会有多大的效果，那么这个就是数据量达到了Mysql的一个瓶颈，所以我们需要对这些数据进行分库分表，以至于效率上的优化我们还需要进行读写分离。



# 准备环境

首先我们需要两个mysql环境的数据库用来做分库分表

我们的Ip为

```
192.168.0.3
```

我们需要两个数据库端口分别为13301以及13302，那么我们就来搭建这两个mysql库吧

创建挂载配置文件

```
mkdir -p /docker/mysql/conf

touch /docker/mysql/conf/my.cnf

echo "[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8" > /docker/mysql/conf/my.cnf
```

然后启动第一台mysql

```
docker run -p 13301:3306 \
--name mycat-mysql1 \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/mysql/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```

启动d第二台mysql

```
docker run -p 13302:3306 \
--name mycat-mysql2 \
-e MYSQL_ROOT_PASSWORD=bigkang \
--privileged=true \
-v /docker/mysql/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
-d docker.io/mysql:5.7
```

初始化建库建表语句

我们新建了一个用户信息表user_info

```
CREATE DATABASE test character set utf8;
use test;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
```



# 安装Mycat

​		Mycat的安装方式非常多种多样，这里我们选择非常好用的Docker的方式来安装Mycat。

​		Mycat下砸地址：

```
http://dl.mycat.io/
```



## 拉取镜像

```sh
docker pull longhronshens/mycat-docker:latest
```

## 自定义镜像

```java
FROM centos:7 
RUN echo "root:root" | chpasswd
RUN yum -y install net-tools

# install java
ADD http://mirrors.linuxeye.com/jdk/jdk-7u80-linux-x64.tar.gz /usr/local/
RUN cd /usr/local && tar -zxvf jdk-7u80-linux-x64.tar.gz && ls -lna

ENV JAVA_HOME /usr/local/jdk1.7.0_80
ENV CLASSPATH ${JAVA_HOME}/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $PATH:${JAVA_HOME}/bin

#install mycat
ADD http://dl.mycat.io/1.6-RELEASE/Mycat-server-1.6-RELEASE-20161028204710-linux.tar.gz /usr/local
RUN cd /usr/local && tar -zxvf Mycat-server-1.6-RELEASE-20161028204710-linux.tar.gz && ls -lna

#download mycat-ef-proxy
#RUN mkdir -p /usr/local/proxy
#ADD https://github.com/LonghronShen/mycat-docker/releases/download/1.6/MyCat-Entity-Framework-Core-Proxy.1.0.0-alpha2-netcore100.tar.gz /usr/local/proxy
#RUN cd /usr/local/proxy && tar -zxvf MyCat-Entity-Framework-Core-Proxy.1.0.0-alpha2-netcore100.tar.gz && ls -lna && sed -i -e 's#C:\\\\mycat#/usr/local/mycat#g' config.json

VOLUME /usr/local/mycat/conf

EXPOSE 8066 9066
#EXPOSE 7066

CMD /usr/local/mycat/bin/mycat console
```

## 启动容器

创建挂载文件夹

```
mkdir -p /docker/mycat/conf
```

### 创建server.xml文件

server文件是Mycat服务器参数调整和用户授权的配置文件。

我们创建一个用户，用户名为root，密码为bigkang，并且有一个逻辑库test库

```xml
echo '<?xml version="1.0" encoding="UTF-8"?>
<!-- - - Licensed under the Apache License, Version 2.0 (the "License"); 
	- you may not use this file except in compliance with the License. - You 
	may obtain a copy of the License at - - http://www.apache.org/licenses/LICENSE-2.0 
	- - Unless required by applicable law or agreed to in writing, software - 
	distributed under the License is distributed on an "AS IS" BASIS, - WITHOUT 
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. - See the 
	License for the specific language governing permissions and - limitations 
	under the License. -->
<!DOCTYPE mycat:server SYSTEM "server.dtd">
<mycat:server xmlns:mycat="http://io.mycat/">
	<system>
	<property name="useSqlStat">0</property>  <!-- 1为开启实时统计、0为关闭 -->
	<property name="useGlobleTableCheck">0</property>  <!-- 1为开启全加班一致性检测、0为关闭 -->
		<property name="sequnceHandlerType">0</property>
		<property name="processorBufferPoolType">0</property>
		<!--默认是65535 64K 用于sql解析时最大文本长度 -->
		<!--<property name="maxStringLiteralLength">65535</property>-->
		<!--<property name="sequnceHandlerType">0</property>-->
		<!--<property name="backSocketNoDelay">1</property>-->
		<!--<property name="frontSocketNoDelay">1</property>-->
		<!--<property name="processorExecutor">16</property>-->
		<!--
			<property name="serverPort">8066</property> <property name="managerPort">9066</property> 
			<property name="idleTimeout">300000</property> <property name="bindIp">0.0.0.0</property> 
			<property name="frontWriteQueueSize">4096</property> <property name="processors">32</property> -->
		<!--分布式事务开关，0为不过滤分布式事务，1为过滤分布式事务（如果分布式事务内只涉及全局表，则不过滤），2为不过滤分布式事务,但是记录分布式事务日志-->
		<property name="handleDistributedTransactions">0</property>
			<!--off heap for merge/order/group/limit      1开启   0关闭-->
		<property name="useOffHeapForMerge">1</property>
		<!--内存页面大小，单位为m-->
		<property name="memoryPageSize">1m</property>
		<!--单位为k-->
		<property name="spillsFileBufferSize">1k</property>
		<property name="useStreamOutput">0</property>
		<!--系统存储内存大小，单位为m-->
		<property name="systemReserveMemorySize">384m</property>
		<!--是否采用zookeeper协调切换  -->
		<property name="useZKSwitch">true</property>
	</system>
	<user name="root">
		<property name="password">bigkang</property>
		<property name="schemas">test</property>
	</user>
</mycat:server>' > /docker/mycat/conf/server.xml
```

### 创建schema.xml文件

schema.xml是逻辑库定义和表以及分片定义的配置文件。

```xml
echo '<?xml version="1.0"?>
<!DOCTYPE mycat:schema SYSTEM "schema.dtd">
<mycat:schema xmlns:mycat="http://io.mycat/">
	<schema name="test" checkSQLschema="false" sqlMaxLimit="100">
		<table name="user_info"   primaryKey="id"  autoIncrement="false"  dataNode="dn1,dn2" rule="userinforule" />
	</schema>
	<dataNode name="dn1" dataHost="localhost1" database="test" />
	<dataNode name="dn2" dataHost="localhost2" database="test" />
	<dataHost name="localhost1" maxCon="1000" minCon="10" balance="1"
			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
		<heartbeat>select 1</heartbeat>
    <writeHost host="hostM1" url="192.168.0.3:13301" user="root"
				   password="bigkang">
		</writeHost>
	</dataHost>
  <dataHost name="localhost2" maxCon="1000" minCon="10" balance="1"
			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
		<heartbeat>select 1</heartbeat>
    <writeHost host="hostM2" url="192.168.0.3:13302" user="root"
				   password="bigkang">
		</writeHost>
	</dataHost>
</mycat:schema>' > /docker/mycat/conf/schema.xml
```

下面是对文件的注释

schema标签注释

```xml
<table 
	name="tb_user"   						// 表名称
	primaryKey="id"  						// 主键名称
	autoIncrement="true"  			// 是否自增
	dataNode="dn1,dn2" 					// 数据节点
		  rule="userinforule" 		// 分片规则
/>
```

配置两个节点dn1 以及 dn2 ，他们的dataHost是localhost1和localhost2，数据库名称是test

```xml
	<dataNode name="dn1" dataHost="localhost1" database="test" />
	<dataNode name="dn2" dataHost="localhost2" database="test" />
```

关联上方

```xml
	name   				host名称，关联dataNode的dataHost
	heartbeat			心跳查询语句
	writeHost     配置host名称，url地址，用户名，以及密码
	<dataHost name="localhost1" maxCon="1000" minCon="10" balance="1"
			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
		<heartbeat>select 1</heartbeat>
    <writeHost host="hostM1" url="192.168.0.3:13301" user="root"
				   password="bigkang">
		</writeHost>
	</dataHost>
  <dataHost name="localhost2" maxCon="1000" minCon="10" balance="1"
			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
		<heartbeat>select 1</heartbeat>
    <writeHost host="hostM2" url="192.168.0.3:13302" user="root"
				   password="bigkang">
		</writeHost>
	</dataHost>
```



### 创建rule.xml文件

这里关联上方

```xml
echo '<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mycat:rule SYSTEM "rule.dtd">
<mycat:rule xmlns:mycat="http://io.mycat/">
	<tableRule name="userinforule">
		<rule>
			<columns>id</columns>
			<algorithm>crc32slot</algorithm>
		</rule>
	</tableRule>

	<function name="murmur"
		class="io.mycat.route.function.PartitionByMurmurHash">
		<property name="seed">0</property><!-- 默认是0 -->
		<property name="count">2</property><!-- 要分片的数据库节点数量，必须指定，否则没法分片 -->
		<property name="virtualBucketTimes">160</property><!-- 一个实际的数据库节点被映射为这么多虚拟节点，默认是160倍，也就是虚拟节点数是物理节点数的160倍 -->
		<!-- <property name="weightMapFile">weightMapFile</property> 节点的权重，没有指定权重的节点默认是1。以properties文件的格式填写，以从0开始到count-1的整数值也就是节点索引为key，以节点权重值为值。所有权重值必须是正整数，否则以1代替 -->
		<!-- <property name="bucketMapPath">/etc/mycat/bucketMapPath</property> 
			用于测试时观察各物理节点与虚拟节点的分布情况，如果指定了这个属性，会把虚拟节点的murmur hash值与物理节点的映射按行输出到这个文件，没有默认值，如果不指定，就不会输出任何东西 -->
	</function>

	<function name="crc32slot"
			  class="io.mycat.route.function.PartitionByCRC32PreSlot">
		<property name="count">2</property><!-- 要分片的数据库节点数量，必须指定，否则没法分片 -->
	</function>
	<function name="hash-int"
		class="io.mycat.route.function.PartitionByFileMap">
		<property name="mapFile">partition-hash-int.txt</property>
	</function>
	<function name="rang-long"
		class="io.mycat.route.function.AutoPartitionByLong">
		<property name="mapFile">autopartition-long.txt</property>
	</function>
	<function name="mod-long" class="io.mycat.route.function.PartitionByMod">
		<!-- how many data nodes -->
		<property name="count">2</property>
	</function>

	<function name="func1" class="io.mycat.route.function.PartitionByLong">
		<property name="partitionCount">8</property>
		<property name="partitionLength">128</property>
	</function>
	<function name="latestMonth"
		class="io.mycat.route.function.LatestMonthPartion">
		<property name="splitOneDay">24</property>
	</function>
	<function name="partbymonth"
		class="io.mycat.route.function.PartitionByMonth">
		<property name="dateFormat">yyyy-MM-dd</property>
		<property name="sBeginDate">2015-01-01</property>
	</function>
	
	<function name="rang-mod" class="io.mycat.route.function.PartitionByRangeMod">
        	<property name="mapFile">partition-range-mod.txt</property>
	</function>
	
	<function name="jump-consistent-hash" class="io.mycat.route.function.PartitionByJumpConsistentHash">
		<property name="totalBuckets">4</property>
	</function>
</mycat:rule>' > /docker/mycat/conf/rule.xml
```

### 创建sequence_conf文件

sequence_conf是帮助我们使用自增主键的文件，定义一些自增主键的配置,如果不需要则保持为空即可

前面第一个属性为表名称，需要全部大写否则报错，

表名称指定值为数据节点

HISIDS 为当前Id默认不填

MINID 为最小的ID

MAXID 为自增ID最大值

CURID 为每次自增的数量

现在为最小为1，最大为500000，每次增加1个

```xml
echo 'USER_INFO=dn1
USER_INFO.HISIDS=
USER_INFO.MINID=1
USER_INFO.MAXID=500000
USER_INFO.CURID=1
' > /docker/mycat/conf/sequence_conf.properties
```

后期需要新增则添加即可

### 运行容器

```
docker run --name mycat \
-v /docker/mycat/conf/schema.xml:/usr/local/mycat/conf/schema.xml \
-v /docker/mycat/conf/rule.xml:/usr/local/mycat/conf/rule.xml \
-v /docker/mycat/conf/server.xml:/usr/local/mycat/conf/server.xml \
-v /docker/mycat/conf/sequence_conf.properties:/usr/local/mycat/conf/sequence_conf.properties \
--privileged=true \
-p 8066:8066 \
-p 9066:9066 \
-e MYSQL_ROOT_PASSWORD=bigkang \
-d longhronshens/mycat-docker
```

访问服务端口8066，用户名root，密码bigkang，修改配置文件即可修改密码

```
INSERT INTO user_info(id,name,age,email) VALUES(1,"bigkang19",19,"bigkang19@qq.com");
INSERT INTO user_info(id,name,age,email) VALUES(2,"bigkang20",20,"bigkang20@qq.com");
INSERT INTO user_info(id,name,age,email) VALUES(3,"bigkang21",21,"bigkang21@qq.com");
INSERT INTO user_info(id,name,age,email) VALUES(4,"bigkang22",22,"bigkang22@qq.com");
INSERT INTO user_info(id,name,age,email) VALUES(5,"bigkang23",23,"bigkang23@qq.com");
```





# Mycat密码加密

找到Jar包执行以下命令

- `0`：代表的是mycat用户登录密码加密（`1` 则是`dataHost`加密）
- `root`：用户名
- `123456`：明文密码

```
java -cp lib/Mycat-server-1.6.7.4-release.jar io.mycat.util.DecryptUtil 0:root:123456
```



```xml
<user name="root" defaultAccount="true">
        <!-- 需要声明使用的是加密后的密码 -->
        <property name="usingDecrypt">1</property>
        <property name="password">GO0bnFVWrAuFgr1JMuMZkvfDNyTpoiGU7n/Wlsa151CirHQnANVk3NzE3FErx8v6pAcO0ctX3xFecmSr+976QA==</property>
        ...
</user>
```

# Mycat自增主键

如果需要主键自增我们需要定义一张自增的表，首先添加一个dn3,或者直接使用原先的dn1也行

```
	<dataNode name="dn3" dataHost="localhost3" database="test" />
	
	<dataHost name="localhost3" maxCon="1000" minCon="10" balance="1"
			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
		<heartbeat>select 1</heartbeat>
    <writeHost host="hostM3" url="39.108.158.33:3306" user="root"
				   password="bigkang">
		</writeHost>
	</dataHost>
```

然后设置属性为自增为true

```xml
	<schema name="test" checkSQLschema="false" sqlMaxLimit="100">
		<table name="user_info"   primaryKey="id"  autoIncrement="true"  dataNode="dn1,dn2" rule="userinforule" />
	</schema>
```

然后修改配置文件

```properties
echo 'USER_INFO=dn3
USER_INFO.HISIDS=
USER_INFO.MINID=1
USER_INFO.MAXID=500000
USER_INFO.CURID=1
' >> /docker/mycat/conf/sequence_conf.properties
```

然后在dn3数据源中插入USER_INFO数据

建表语句

```sql
DROP TABLE IF EXISTS MYCAT_SEQUENCE;
CREATE TABLE MYCAT_SEQUENCE (
NAME VARCHAR (50) NOT NULL,
current_value INT NOT NULL,
increment INT NOT NULL DEFAULT 100,
PRIMARY KEY (NAME)
) ENGINE = INNODB ;


INSERT INTO MYCAT_SEQUENCE(NAME,current_value,increment) VALUES ('GLOBAL', 100000, 100);

DROP FUNCTION IF EXISTS `mycat_seq_currval`;
DELIMITER ;;
CREATE FUNCTION `mycat_seq_currval`(seq_name VARCHAR(50)) 
RETURNS VARCHAR(64) CHARSET utf8
    DETERMINISTIC
BEGIN DECLARE retval VARCHAR(64);
        SET retval="-999999999,null";  
        SELECT CONCAT(CAST(current_value AS CHAR),",",CAST(increment AS CHAR) ) INTO retval 
          FROM MYCAT_SEQUENCE WHERE NAME = seq_name;  
        RETURN retval ; 
END
;;
DELIMITER ;

DROP FUNCTION IF EXISTS `mycat_seq_nextval`;
DELIMITER ;;
CREATE FUNCTION `mycat_seq_nextval`(seq_name VARCHAR(50)) RETURNS VARCHAR(64)
 CHARSET utf8
    DETERMINISTIC
BEGIN UPDATE MYCAT_SEQUENCE  
                 SET current_value = current_value + increment 
                  WHERE NAME = seq_name;  
         RETURN mycat_seq_currval(seq_name);  
END
;;
DELIMITER ;


DROP FUNCTION IF EXISTS `mycat_seq_setval`;
DELIMITER ;;
CREATE FUNCTION `mycat_seq_setval`(seq_name VARCHAR(50), VALUE INTEGER) 
RETURNS VARCHAR(64) CHARSET utf8
    DETERMINISTIC
BEGIN UPDATE MYCAT_SEQUENCE  
                   SET current_value = VALUE  
                   WHERE NAME = seq_name;  
         RETURN mycat_seq_currval(seq_name);  
END
;;
DELIMITER ;
```

然后测试是否能返回数据

```
SELECT MYCAT_SEQ_Nextval('test')
```

如果响应则正确,新建USER_INFO表的基础自增数据

```
insert into MYCAT_SEQUENCE (name,current_value,increment) values ('USER_INFO',0,1);
```

然后重启Mycat即可

```
docker restart mycat
```

# Mycat配置读写分离

修改balance为3

```
0：不开启读写分离机制，所有读操作都发送到当前可用的 writeHost 上
1：全部的readHost与stand by writeHost参与select语句的负载均衡
2：所有读操作都随机在writeHost、readhost 上分发
3：所有读请求随机分发到 wiriterHost 对应的readhost 执行。即 writerHost 不负担读压力，全部读请求由 readhost 执行。注意该取值只在1.4及其以后版本有，1.3没有
```

修改writeType为0

```
-1：表示不自动切换
0：所有写操作发送到配置的第一个writeHost，第一个挂了切到还生存的第二个writeHost。重新启动后以切换后的为准，切换记录在配置文件中：dnindex.properties
1：所有写操作都随机的发送到配置的writeHost，1.5 以后废弃不推荐使用
2：基于MySQL主从同步的状态决定是否切换（1.4 新增）
```

修改switchType 为1
	-1：表示不自动切换
	1：默认值，自动切换
	2：基于MySQL主从同步的状态决定是否切换，心跳检测语句为：show slave status
	3：基于MySQL galary cluster的切换机制（适合集群，1.4.1新增），心跳检测语句为show status like 'wsrep%'


writeHost为写的服务器，readHost为读的服务器，配置地址，用户名，密码即可。

```
 <dataHost name="localhost2" maxCon="1000" minCon="10" balance="3"
			  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
		<heartbeat>select 1</heartbeat>
    <writeHost host="hostM2" url="192.168.0.3:13302" user="root"
				   password="bigkang">
		</writeHost>
		<readHost host="hostS2" url="192.168.0.3:13303" user="root" password="apple" />
	</dataHost>
```







# 配置文件详情

## server.xml

```xml
<system>
    <!-- mycat 服务连接端口 -->
    <property name="serverPort">8066</property>
    <!-- mycat 服务管理端口 -->
    <property name="managerPort">9066</property>
    <!-- mycat 服务监听的ip -->
    <property name="bindIp">0.0.0.0</property>
    <!-- 0为需要密码登陆、1为不需要密码登陆；默认为0，设置为1则需要指定默认账户-->
    <property name="nonePasswordLogin">0</property>
    <!-- 前端连接的写队列大小 -->
    <property name="frontWriteQueueSize">2048</property>
    <!-- 设置字符集编码 -->
    <property name="charset">utf8</property>
    <!-- mycat 的进程数量 -->
    <property name="processors">8</property>
    <!-- 闲置连接超时时间，单位：毫秒 -->
    <property name="idleTimeout">1800000</property>
    <!-- 默认最大返回的数据集大小 -->
    <property name="defaultMaxLimit">100</property>
    <!-- 允许的最大包大小 -->
    <property name="maxPacketSize">104857600</property>
    <!-- 0遇上没有实现的报文(Unknown command:),就会报错、1为忽略该报文，返回ok报文。
在某些mysql客户端存在客户端已经登录的时候还会继续发送登录报文,mycat会报错,该设置可以绕过这个错误-->
    <property name="ignoreUnknownCommand">0</property>
    <property name="useHandshakeV10">1</property>
    <property name="removeGraveAccent">1</property>
    <!-- 1为开启实时统计、0为关闭 -->
    <property name="useSqlStat">0</property>
    <!-- 1为开启全加班一致性检测、0为关闭 -->
    <property name="useGlobleTableCheck">0</property>
    <!-- SQL 执行超时 单位:秒-->
    <property name="sqlExecuteTimeout">300</property>
    <property name="sequnceHandlerType">1</property>
    <!--必须带有MYCATSEQ_或者 mycatseq_进入序列匹配流程 注意MYCATSEQ_有空格的情况-->
    <property name="sequnceHandlerPattern">(?:(\s*next\s+value\s+for\s*MYCATSEQ_(\w+))(,|\)|\s)*)+</property>
    <!-- 子查询中存在关联查询的情况下,检查关联字段中是否有分片字段 .默认 false -->
    <property name="subqueryRelationshipCheck">false</property>
    <property name="sequenceHanlderClass">io.mycat.route.sequence.handler.HttpIncrSequenceHandler</property>
    <!--默认为type 0: DirectByteBufferPool | type 1 ByteBufferArena | type 2 NettyBufferPool -->
    <property name="processorBufferPoolType">0</property>
    <!--分布式事务开关，0为不过滤分布式事务，1为过滤分布式事务（如果分布式事务内只涉及全局表，则不过滤），2为不过滤分布式事务,但是记录分布式事务日志-->
    <property name="handleDistributedTransactions">0</property>
    <!-- off heap for merge/order/group/limit  1开启；0关闭 -->
    <property name="useOffHeapForMerge">0</property>
    <!--是否采用zookeeper协调切换  -->
    <property name="useZKSwitch">false</property>
    <!--如果为 true的话 严格遵守隔离级别,不会在仅仅只有select语句的时候在事务中切换连接-->
    <property name="strictTxIsolation">false</property>
    <!-- Mycat连接数据库时使用的隔离级别
         1 - 读未提交
         2 - 读已提交
         3 - 可重复读
         4 - 串行化
     -->
    <property name="txIsolation">2</property>
    <property name="useZKSwitch">true</property>
    <!--如果为0的话,涉及多个DataNode的catlet任务不会跨线程执行-->
    <property name="parallExecute">0</property>
</system>
<!-- 用户名 -->
<user name="mall">
    <!-- 密码 -->
    <property name="password">123456</property>
    <!-- 允许该用户访问的逻辑库 -->
    <property name="schemas">mall_db,db1,db2</property>
    
    <!-- 表级 DML 权限配置，check属性表示是否开启该配置 -->
    <privileges check="true">
        <!-- 特别权限应用的逻辑库 -->
        <schema name="mall_db" dml="0110">
            <!-- 
                配置用户对该表的访问权限，dml属性用于指定权限位，
                如果table标签没有配置该属性的话，默认取schema标签的dml属性值，
                剩余没有配置的其他表默认也是取schema标签的dml属性值
								dml属性配置的数字是权限位，
								分别对应着insert,update,select,delete四种权限。
            -->
            <table name="user_table" dml="0000"></table>
            <table name="order_table" dml="1111"></table>
        </schema>
    </privileges>
</user>
```

## schema.xml

#### schema标签

```xml
<schema name="TESTDB" checkSQLschema="true" sqlMaxLimit="100" randomDataNode="dn1">
    ...
</schema>
```

```
checkSQLschema
						
						属性判断是否检查发给Mycat的SQL是否含有库名，为true时会将SQL中的库名删除掉
						
name
				
						属性定义逻辑库的名字，必须唯一不能重复
						
sqlMaxLimit
				
						属性用于限制返回结果集的行数，值为-1时表示关闭该限制。如果没有开启限制则默认取server.xml里配置的限制
						
randomDataNode
		
						属性定义将一些随机语句发送到该数据节点中
```

#### table标签

```xml
<schema name="TESTDB" checkSQLschema="true" sqlMaxLimit="100" randomDataNode="dn1">
    <!-- 多表定义 -->
    <table name="travelrecord,address" dataNode="dn1,dn2,dn3" rule="auto-sharding-long" splitTableNames ="true"/>
    <!-- 单表定义 -->
    <table name="oc_call" primaryKey="id" dataNode="dn1$0-743" rule="latest-month-calldate"/>
</schema>
```

```
name 

			属性定义逻辑表的名字，必须唯一不能重复且需要与数据库中的物理表名一致。使用逗号分割配置多个表，即多个表使用这个配置
			
primaryKey 
		
			属性指定逻辑表中的主键，也是需要与物理表的主键一致
dataNode 

			属性指定物理表所在数据节点的名称，配置多个数据节点时需按索引顺序并使用逗号分隔，或指定一个索引范围：dn1$0-743。注意数据节点定义之后，顺序不能再发生改变，否则会导致数据混乱
			
rule 

			属性用于指定分片规则名称，对应rule.xml中的<tableRule>标签的name属性，如无需分片可以不指定
			
splitTableNames 

			属性定义是否允许多个表的定义
```

#### dataNode 标签

```xml
<dataNode name="dn1" dataHost="localhost1" database="db1" />
<dataNode name="dn2" dataHost="localhost1" database="db2" />
<dataNode name="dn3" dataHost="localhost1" database="db3" />
<!-- 可以配置一个范围 -->
<dataNode name="dn1$0-743" dataHost="localhost1" database="db$0-743"/>
```

```
name 

			属性定义数据节点的名称，必须唯一
			
dataHost 

			属性指定分片所在的物理主机
			
database 

			属性指定物理数据库的名称
```

#### dataHost 标签

`			dataHost` 标签用于定义后端物理数据库主机信息，该标签内有两个子标签，可以定义一组数据库主机信息。例如，定义一组主从集群结构的数据库主机信息。

```xml
<dataHost name="localhost1" maxCon="1000" minCon="10" balance="0"
          writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
    <heartbeat>select user()</heartbeat>
    <!-- 可以配置多个写实例 -->
    <writeHost host="localhost" url="localhost:3306" user="root"
               password="123456">
        <readHost host="localhost" url="localhost:3306" 
                  user="root" password="123456"></readHost>
    </writeHost>
</dataHost>
```

```
name

				属性用于定义主机名称，必须唯一
				
maxCon 

				属性指定每个读/写实例连接池的最大连接数。也就是说，标签内嵌套的writeHost、readHost 标签都会使用这个属性的值来实例化出连接池的最大连接数
				
minCon 

				属性指定每个读写实例连接池的最小连接数，即初始化连接池的大小
				
dbType 

				属性指定后端连接的数据库类型，目前支持二进制的mysql协议，还有其他使用JDBC连接的数据库
				
dbDriver 

				属性指定连接后端数据库使用的驱动，目前可选的值有native和JDBC 

slaveThreshold 

				属性用于定义主从复制延时阈值，当Seconds_Behind_Master > slaveThreshold时，读写分离筛选器会过滤掉此Slave机器，防止读到很久之前的旧数据
				
balance 

				属性指定读写分离的负载均衡类型，目前的取值有4 种：
						0：不开启读写分离机制，所有读操作都发送到当前可用的 writeHost 上
						1：全部的readHost与stand by writeHost参与select语句的负载均衡
						2：所有读操作都随机在writeHost、readhost 上分发
						3：所有读请求随机分发到 wiriterHost 对应的readhost 执行。即 writerHost 不负担读压力，全部读请求由 readhost 执行。注意该取值只在1.4及其以后版本有，1.3没有
						
writeType 

				属性指定写实例的负载均衡类型，目前的取值有4 种：
						-1：表示不自动切换
						0：所有写操作发送到配置的第一个writeHost，第一个挂了切到还生存的第二个writeHost。重新启动后以切换后的为准，切换记录在配置文件中：dnindex.properties
						1：所有写操作都随机的发送到配置的writeHost，1.5 以后废弃不推荐使用
						2：基于MySQL主从同步的状态决定是否切换（1.4 新增）
						
switchType 
				
				属性用于指定主从切换的方式：
						-1：表示不自动切换
						1：默认值，自动切换
						2：基于MySQL主从同步的状态决定是否切换，心跳检测语句为：show slave status
						3：基于MySQL galary cluster的切换机制（适合集群，1.4.1新增），心跳检测语句为show status like 'wsrep%'
								
heartbeat 

				标签内指明用于和后端数据库进行心跳检查的语句。例如，MySQL可以使用select user()，Oracle可以使用select 1 from dual 等。这个标签还有一个connectionInitSql属性，主要是当使用Oracla数据库时，需要执行的初始化SQL语句就这个放到这里面来。例如：alter session set nls_date_format='yyyy-mm-dd hh24:mi:ss'注：如果是配置主从切换的语句在1.4之后必须是：show slave status

writeHost
	
			标签配置写实例，即主从中的master节点
			
						host 
									属性用于标识不同实例名称，一般writeHost名称使用M1作为后缀，readHost则使用S1作为后缀
						url 
									属性用于配置数据库的连接地址，如果是使用native的dbDriver，则一般为address:port这种形式。用JDBC或其他的dbDriver，则需要特殊指定。例如，当使用JDBC 时则可以这么写：jdbc:mysql://localhost:3306/
									
						user 
									属性配置数据库用户名
						password 
									属性配置数据库密码
						weight 
									属性配置某个数据库在 readhost 中作为读节点的权重
						usingDecrypt 
									属性指定是否对密码加密，默认为0， 若需要开启则配置为1
			
readHost 

			标签配置读实例，即主从中的salve节点
			
readHost

			是writeHost的子标签，与writeHost有绑定关系
```





 `name`				   属性定义逻辑库的名字，必须唯一不能重复

 `sqlMaxLimit `     属性用于限制返回结果集的行数，值为`-1`时表示关闭该限制。如果没有开启限制则默认取`server.xml`里配置的限制

 `randomDataNode`属性定义将一些随机语句发送到该数据节点中

## sequence_conf.properties



## rule.xml

```xml
<!-- name属性指定分片规则的名称，必须在 rule.xml 文件中是唯一的 -->
<tableRule name="hash-mod-4_id">
    <rule>
        <!-- 指定使用表中的哪个列进行分片 -->
        <columns>id</columns>
        <!-- 指定表的分片算法，取值为<function>标签的name属性 -->
        <algorithm>hash-mod-4</algorithm>
    </rule>
</tableRule>
```



## log4j2.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
      	
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d [%-5p][%t] %m %throwable{full} (%C:%F:%L) %n"/>
        </Console>

        <RollingFile name="RollingFile" fileName="${sys:MYCAT_HOME}/logs/mycat.log"
                     filePattern="${sys:MYCAT_HOME}/logs/$${date:yyyy-MM}/mycat-%d{MM-dd}-%i.log.gz">
        <PatternLayout>
                <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] (%l) - %m%n</Pattern>
            </PatternLayout>
            <Policies>
                <OnStartupTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="250 MB"/>
                <TimeBasedTriggeringPolicy/>
            </Policies>
        </RollingFile>
    </Appenders>
    <Loggers>
        <!--<AsyncLogger name="io.mycat" level="info" includeLocation="true" additivity="false">-->
            <!--<AppenderRef ref="Console"/>-->
            <!--<AppenderRef ref="RollingFile"/>-->
        <!--</AsyncLogger>-->
        <asyncRoot level="info" includeLocation="true">

            <AppenderRef ref="Console" />
            <AppenderRef ref="RollingFile"/>

        </asyncRoot>
    </Loggers>
</Configuration>

```



