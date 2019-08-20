# MySQL连接问题

​		在mysql中如果出现这种异常，表示我们的连接异常被终止掉了，然后mysql随之自动关闭了

```
2019-08-14T11:18:30.558570Z 4 [Note] Aborted connection 4 to db: 'cattle' user: 'cattle' host: '10.18.16.99' (Got an error reading communication packets)
```

​		那么我们需要定位问题，就需要知道到底是什么原因引起的。所以我们看到连接想到会有两种可能，一是连接数量太多导致mysql崩溃，二是错误的连接太多，我们把他拒绝掉了，如果mysql崩溃了很显然就是连接太多了。

# 修改MySQL最大连接数

查看最大连接数

```
show variables like '%max_connections%';
```

那么我们有两种方式。

​	一是临时修改，直接执行MySQL命令即可，二是修改配置文件。

​			命令：设置全局的最大连接数

```
set global max_connections=2000;
```

​			配置文件：在/etc/my.conf文件加入，然后重启服务

```
 max_connections=2000
```

# 查看当前mysql的连接客户端

即可查看当前所有连接的客户端

```
show processlist 
```

然后就会出现如下信息

```
User	Host					id		db		Command Time State		info
-----------------------------------------------------------------------------------------
root	172.18.0.1:54986		35807	nacos	Sleep	5		
root	123.113.96.136:55697	36135	test	Query	0	 starting	show processlist
root	123.113.96.136:57555	36138	test	Sleep	163		
root	123.113.96.136:57559	36139	test	Sleep	161		
root	123.113.96.136:57570	36140	test	Sleep	156		
root	123.113.96.136:57604	36141	test	Sleep	135		
root	123.113.96.136:57608	36142	test	Sleep	133		
root	123.113.96.136:57610	36143	test	Sleep	132		
root	123.113.96.136:57611	36144	test	Sleep	132		
root	123.113.96.136:57616	36145	test	Sleep	130		
root	123.113.96.136:57618	36146	test	Sleep	129		
root	123.113.96.136:57620	36147	test	Sleep	128		
```

我们就能看到如下这些数据，那么这些数据代表什么意思呢

```
User					当前连接的用户
Host					连接的ip地址
id						生成的id
db						连接的数据库
Command					执行的命令			Sleep代表等待，Query代表查询
Time					当前状态持续时间（秒）
State					状态
info					执行的命令详情
```

# MySQL连接被拒接锁ip

同样当我们遇到最大连接数以后，很大几率会导致另一个问题，那么就是异常连接太多，导致mysql将我们的ip给锁了，那么解决这个问题我们可以使用如下命令刷新

```
flush hosts
```

并且在配置文件中添加如下配置

```
max_connect_errors=1000
```

# MySQL数据库被锁

如果我们使用Mysql发现数据库被锁了

如果他的值不是null那么表示数据库被锁了

```
use 被锁的数据库
select * from DATABASECHANGELOGLOCK;
```

解锁，将他们都设置为null即可

```
update DATABASECHANGELOGLOCK set LOCKED="", LOCKGRANTED=null, LOCKEDBY=null where ID=1;
```

# MySQL锁表

查看被锁的表

```
SELECT * FROM information_schema.innodb_trx 
```

然后kill掉锁住的表

这里的id是查询的trx_mysql_thread_id,

```
kill id
```

# 慢查询日志开启

查询是否开启慢查询日志以及路径

```
show variables like '%slow_query_log%';
```

OFF为关闭ON为开启

手动开启

```
set global slow_query_log=1;
```

查看当前查询多少秒判断为慢查询

```
SHOW VARIABLES  LIKE '%long_query_time%';
```

设置查询多少秒为慢日志，设置为3秒

```
set global long_query_time=3;
```

然后退出连接再次查询，我们直接查询的话还是默认值看不到修改值，再次查看

```
SHOW VARIABLES  LIKE '%long_query_time%';
```

