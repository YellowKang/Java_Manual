## MySQL简介

​		

​		MySQL是一个关系型数据库管理系统，采用GPL开源协议，并且有标准SQL数据库语言形式

​		（Oracle，DB2等大型关系型数据库的SQL规范 ）



​		MySQL是可以定制的，采用了GPL协议，你可以修改源码来开发自己的Mysql系统。



​		MySQL支持大型的数据库。可以处理拥有上千万条记录的大型数据库。



​		MySQL可以允许于多个系统上，并且支持多种语言。这些编程语言包括C、C++、Python、			 

​		Java、Perl、PHP、Eiffel、Ruby和Tcl等。



​		MySQL支持大型数据库，支持5000万条记录的数据仓库，32位系统表文件最大可支持4GB，64

​		位系统支持最大的表文件为8TB。



## MySQL核心

​		数据库内部结构和原理

​		数据库建模优化

​		数据库索引建立

​		SQL语句优化

​		SQL编程(自定义函数、存储过程、触发器、定时任务)

​		MySQL服务器的安装配置

​		数据库的性能监控分析与系统优化

​		各种参数常量设定

​		主从复制

​		分布式架构搭建、垂直切割和水平切割

​		数据迁移

​		容灾备份和恢复

​		shell或python等脚本语言开发

​		对开源数据库进行二次开发

## （重点！）MySQL5.7日常坑

​		不让设置密码并且不让太简单：

​			注意：如果是5.7的版本可能会发不让设置密码，因为太过于简单，

​				并且引发异常：ERROR 1819 (HY000): Your password does not satisfy the current 

​								policy requirements

​							意思是你的密码太过于简单了；

​				我们需要更改密码的难易度

​				set global validate_password_policy=0; 

​				set global validate_password_mixed_case_count=0;

​				set global validate_password_number_count=3; 

​				set global validate_password_special_char_count=0;

​				set global validate_password_length=3;

​				将密码的所有什么包含字符和大写小写以及数字全部都关闭，然后长度可以为3

​				然后就能再设置了

​				



​		并且在5.7中的版本对group by的支持不好，他必须将所有的参数都加进去，所以我们找到他的/etc/my.cnf在他的mysql列加上

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION

​		然后保存然后重启就好了

​		然后在查看SELECT @@GLOBAL.sql_mode;这个函数，看他的值是不是等于上面设置的

## MySQL操作

### 用户操作

​	

#### 	创建用户

​				CREATE USER 'huangkang'@'%' IDENTIFIED BY 'bigkang'; 

​				表示创建名称为（huangkang）的用户，密码设为（bigkang）；

​				（%）表示让所有的ip访问



​				注意：如果是5.7的版本可能会发不让设置密码，因为太过于简单，

​				并且引发异常：ERROR 1819 (HY000): Your password does not satisfy the current 

​								policy requirements

​							意思是你的密码太过于简单了；

​				我们需要更改密码的难易度

​				set global validate_password_policy=0; 

​				set global validate_password_mixed_case_count=0;

​				set global validate_password_number_count=3; 

​				set global validate_password_special_char_count=0;

​				set global validate_password_length=3;

​				将密码的所有什么包含字符和大写小写以及数字全部都关闭，然后长度可以为3

​				然后就能再设置了



#### 	更改用户

​				修改用户的host让其他主机访问，修改host为host，user为huangkang的字段，

​												让他可以其他的ip访问

​				update user set host="%" where host="host" and user="huangkang";

​				

​				修改密码请注意使用password("密码");

​				因为password是一个加密函数，

​				（MySQL5.5一级以上）

​				update user set password= password("bigkang");

​				（MySQL5.7一级以上）

​				update user set authentication_string = password("bigkang");

​				版本上字段名字改了



#### 	删除用户

​				直接删除用户表中的huangkang这个用户

​				delete from user where user="huangkang";

#### 	用户授权

​				查看用户权限



​						show grants for huangkang;

​						查看用户bigkang的权限



​				注：mytest是数据库，*是表

​				授予用户权限



​						GRANT All ON * . * TO 'huangkang'@'%';

​						给用户bigkang并且host为%这个用户授权所有

​						

​						查询权限：

​								grant select on mytest.* to 'huangkang'@'%';

​						插入权限：

​								grant insert on mytest.* to 'huangkang'@'%';

​						修改权限：

​								grant update on mytest.* to 'huangkang'@'%';

​						删除权限：

​								grant delete on mytest.* to 'huangkang'@'%';

​						这只是基本的权限操作

​								REVOKE update on mytest.* to 'huangkang'@'%';

#### 		收回权限

​					注意！（删除的时候要和授予的权限一模一样）

​					先查询权限：

​						show grants for huangkang;

​					收回单个权限

​						REVOKE select ON mytest.* FROM huangkang@'%';

​					收回多个权限

​						REVOKE select,update,delete ON mytest.* FROM huangkang@'%';

​					收回所有权限

​						REVOKE ALL PRIVILEGES ON * . * FROM 'huangkang'@'%';



### 查看执行的SQL流程

#### 		查看执行过的sql

​					先查询我们的profiling是否开启

​						show variables  like '%profiling%';

​													如果profiling是OFF的模式我们就开启他

​													底层使用C编写我们设置为1就能开启

​						set profiling=1;

​													然后我们执行一条查询

​						select * from user;

​													然后再来查看就能看到我们执行过的SQL了

​						show profiles;



​						![](img\Profiles.png)

​						这样就能查看最近运行的SQL了

​					然后使用

​						show profile cpu,block io for query id;

​						例如：

​								show profile cpu,block io for query 10;就能查看他这个语句执行了的内容

​								![](img\profileone.png)



​	

### 存储引擎操作

#### 			查看存储引擎类型

​					show engines;

#### 			查看数据库默认使用引擎

​				 	show variables like '%storage_engine%';![](img\引擎1.png)

## 数据库引擎

### 两大主流引擎解析

​		对比项					MyISAM						InnoDB
​		外键					不支持						支持
​		事务					不支持						支持
​		行表锁			表锁，即使操作一条记录也会锁住		行锁,操作时只锁某一行，不对其它行有影		

​						整个表，不适合高并发的操作			响，适合高并发的操作

​		缓存				只缓存索引，不缓存真实数据		不仅缓存索引还要缓存真实数据，对内存

​															要求较高，而且内存大小对性能有决定性		

​															的影响	

​		关注点				节省资源、消耗少、简单业务		并发写、事务、更大资源
​		默认安装						Y						Y
​		默认使用						N 						Y
​		自带系统表使用				Y						N



## Join的使用方式

​		![](img\join.png)

​				

### 内连接

​	SELECT * FROM t_emp INNER JOIN t_dept ON t_emp.deptId=t_dept.id

​	内连接

​	INNER JOIN

​			他会查询两张表中交集的数据，如果A表中有一个用户没有对应的B表的外键那么不会查出B表的数据，如果B表中有一条数据没有用户那么相对应的也不会查询出来，这样查询出来的数据一定是符合条件的

​			![](img\innerjoin (2).png)

​			![](img\innerjoins.png)

​			

### 外连接

#### 		左外连接	

​				SELECT * FROM t_emp LEFT JOIN t_dept ON t_emp.deptId=t_dept.id

​				左外连接

​				LEFT JOIN

​				![](img\leftjoin1.png)

​				![](img\leftjoin2.png)

​				这里我们可以很清楚的看得到左外连接连接了左表他将左表的所有的数据都查了出来，哪怕是没有门派的，这就是左外连接，以A表为主，然后查询和B表交集的数据



#### 		右外连接

​				SELECT * FROM t_emp RIGHT JOIN t_dept ON t_emp.deptId=t_dept.id

​				左外连接

​				RIGHT JOIN

​				![](img\rightjoin.png)

​				![](img\rightjoin2.png)

​				这里我们可以看到他将所有的门派都查了出来，还有和A表的交集的数据，这个就是以右边的B表为主表，然后A表的交集数据

#### 		左连接

​			SELECT * FROM t_emp LEFT JOIN t_dept ON t_emp.deptId=t_dept.id where t_dept.id is NULL

​				左连接

​				 LEFT JOIN     					t_dept.id is NULL

​				左连外接之后查询B表没有的数据，这样取到的数据就是A表中的所有的和B表无关的数据

​				![](img\leftnull1.png)

​				![](img\leftnull2.png)

​			这里我们可以很清楚的看得到查出来的数据，查询左边的用户表的所有信息，然后以左表为主，然后查询B表为空的，这样我们就查询出了A表独有的数据，而且和B表无关的

#### 		右连接

​			SELECT * FROM t_emp RIGHT JOIN t_dept ON t_emp.deptId=t_dept.id where t_emp.id is NULL

​				右连接

​					RIGHT JOIN					 t_emp.id is NULL

​				右外连接之后查询没有交集的数据，以右边的B表为主，查询和A表交集的数据，并且A表id为空的，这样就能查询出哪些门派	

​					![]()

​					![]()

#### 		全连接

#### 		 全非连接（无关数据）

​					

##  为什么尽量不要使用一主多从MySQL？



​	在MySQL的集群中我们为什么尽量不要使用一主多从的MySQL集群方案？

​	因为如果我们使用一个主服务器然后很多个从服务器的话，如果在我们的项目中出现了问题，我们需要切换一个从服务器，在众多的从服务器中挑选一个数据最新的，然后我们将他设置成主服务，再去把其他的从服务器以新设置的主服务器为主，这样的话工作量繁忙，而且效率及低，因为我们需要去把所有的服务都改一遍，对DBA来说也不好维护，而且这样过多重复的操作我们是不会允许他存在的



## QPS和TPS的描述

   	QPS（Queries Per Second）：意思是“每秒查询率”，是一台服务器每秒能够相应的查询次数，是对一个特定的查询服务器在规定时间内所处理流量多少的衡量标准。 例如我们开启事务，操作，回滚，这个就是3个QPS，如果每秒十万个QPS表示，这样简单的操作进行了3.3万个



​	TPS（TransactionsPerSecond）：也就是事务数/秒。它是软件测试结果的测量单位。一个事务是指一个客户机向服务器发送请求然后服务器做出反应的过程。客户机在发送请求时开始计时，收到服务器响应后结束计时，以此来[计算](https://www.aliyun.com/)使用的时间和完成的事务个数， 



​	这样我们就通过结论了解到如果一个简单的场景，我们执行个增加，表示一个事务，一个TPS这一个TPS又产生了三次操作（没报错的情况下），那么就是开启事务，执行增加，提交事务，这就对应了3个QPS，那么如果我们每秒钟产生了1000个增加操作，那么他的TPS就是1000，QPS就是3000



​	那么超高的QPS和TPS会给我们带来什么样的影响呢？



​		效率低下的SQL



## 大表的劣势?

​		什么样的表称为大表？

​			1、记录行数巨大，单表超过千万行

​			2、表数据文件巨大，表数据文件超过10G

​		大表的影响？

​			1、查询慢，当数据量过大的时候，我们的尤其是带条件的查询（这个基本都会带查询），很难快速				  	   

​			 的将数据给我们查询出来 

​			2、建立索引需要很长的时间

​				风险：MySQL版本小于5.5建立索引会锁表

​					    MySQL版本大于等于5.5虽然不会锁表，但是会引起主从复制的延迟



## 如何解决大表的问题？

​		分库分表：

​				难点：

​						分表主键的选择

​						分表后跨分区数据的查询和统计

​		大表的历史数据归档：

​				难点：

​						归档时间点的选择

​						如何进行归档操作

## 数据库操作别名

| DDL   英文全称 (Data  Definition  Language)  | 数据定义语言 | 库、表、列         | 创建、删除、修改、库或表结构，对数据库或表的结构操作 |
| -------------------------------------------- | ------------ | ------------------ | ---------------------------------------------------- |
| DML   英文全称(Data  Manipulation  Language) | 数据操作语言 | 数据库记录（数据） | 增、删、改，对表记录进行更新(增、删、改)             |
| DQL   英文全称(Data  Query  Language)        | 数据查询语言 | 数据库记录（数据） | 查、用来查询数据，对表记录的查询                     |
| DCL   英文全称(Data  Control  Language)      | 数据控制语言 | 数据库用户         | 用来定义访问的权限和安全级别，对用户的创建，及授权   |

## 什么是大事务？

​		大事务就是在你进行一个事务的时候执行了多次操作，例如我们增加了一个数据，并且给他分配什么权限，并且计算他的其他表需要初始化的东西，然后这条数据在执行到某一个部分的时候报错了，那么就会引起回滚以前的所有操作，那么在我们的操作较多的时候会对数据库的性能产生很大的影响

​		

​		风险：

​				锁定太多的数据，造成大量的阻塞和超时

​				回滚的时间长

​				时间一长，会引起主从的延迟时间



​		如何处理大事务？



​			1、避免一次处理太多的数据

​					例如处理100万条数据，我们分10次处理

​			2、移出不必要在事务中的SELECT操作

## 影响性能的方面？

​		1、CPU

​				重点：

​					老版本MySQL不支持多CPU对同一SQL并发的处理

​				

​		2、运行内存

​				内存虽然越大越好但是到了一定的大小时（例如100G的数据我们使用240G的内存），MySQL					

​				对内存就没那么大的提升了

​		3、磁盘读写速度

​				1、使用传统机械硬盘

​						是最常见的存储磁盘

​						特点：

​							价格低，存储的数据大

​						机械硬盘的读取数据过程

​							1、移动磁头到磁盘表面上的正确位置

​							2、等待磁盘旋转，使得所需的数据在磁头之下

​							3、等待磁盘旋转过去，所有所需的数据都被磁头读出

​						如何选择传统机器硬盘

​							1、存储容量

​							2、传输速度

​							3、访问时间

​							4、主轴转速

​							5、物理尺寸

​				2、使用RAID增强传统机器硬盘的性能

​						什么是RAID？

​							RAID是英文Redundant Array of Independent Disks的缩写，中文简称为独立冗		

​							余磁盘阵列。简单的说，RAID是一种把多块独立的硬盘（物理硬盘）按不同的方		

​							式组合起来形成一个硬盘组（逻辑硬盘），从而提供比单个硬盘更高的存储性能

​							和提供数据备份技术。 

​				3、使用固态存储SSD和PCIe卡

​				4、使用网络存储NAS和SAN

​		4、服务器系统

​				选择什么样的系统？Linux或者Windows？Linux更加轻量安全，没有Windows那么臃肿

​		5、数据库存储引擎的选择

​				MyISAM：不支持事务，表级锁

​					索引通过内存存储缓存

​					数据通过操作系统缓存

​				InnoDB：事务级存储引擎，完美支持行级锁，事务ACID特性

​					他会使用内存，存储索引和数据，提升了性能

​		6、数据库参数配置

​				设计时的数据库的表结构的设计

​				SQL语句的编写和优化



​	

































