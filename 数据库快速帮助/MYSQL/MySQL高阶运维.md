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

# MySQL性能分析

我们使用MySQL语句分析的关键字EXPLAIN，例如我们分析一段sql语句，并且查看他是否使用索引

```sql
EXPLAIN SELECT * from t_user;
```

得到如下结果

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1571039035693.png)

下面是每个字段的解释



```
						id ： 分析查询的id
						
						
 	 select_type ： 查询的类型
 	 								（SIMPLE）：最简单的SELECT语句，不包含子查询等
 	 								（PRIMARY/UNION）：
 	 											PRIMARY查询中最外层的的SELECT
 	 											UNION查询中最里面的SELECT。
 	 								（DEPENDENT UNION/UNIOIN RESULT）
 	 											DEPENDENT UNION查询中处于内层的SELECT，
 	 													（内层的SELECT语句与外层的SELECT语句有依赖关系）
                     		UNION RESUL，TUNION操作的结果，id值通常为NULL
 	 								（SUBQUERY/DEPENDENT SUBQUERY）
 	 											SUBQUERY：子查询中首个SELECT（如果有多个子查询存在）
 	 											DEPENDENT SUBQUERY：子查询中首个SELECT，但依赖于外层的表（如果有多个子查询存在）
 	 								（DERIVED/MATERIALIZED）
 	 											DERIVED：被驱动的SELECT子查询（子查询位于FROM子句）
 	 											MATERIALIZED：被物化的子查询
 	 								（UNCACHEABLE SUBQUERY/UNCACHEABLE UNION）
 	 											UNCACHEABLE SUBQUERY：对于外层的主表，子查询不可被物化，每次都需要计算（耗时操作）
 	 											UNCACHEABLE UNION：UNION操作中，内层的不可被物化的子查询（类似于UNCACHEABLE SUBQUERY）
 	 											
 	 											
			 	 table ： 查询分析的表名
			 	 
			 	 
		partitions ： 匹配的分区
		
		
					type ： 常用的类型有： 
												ALL ：MySQL将遍历全表以找到匹配的行（全表扫描查询）
											index ：index与ALL区别为index类型只遍历索引树
											range ：只检索给定范围的行，使用一个索引来选择行
												ref ：表示上述表的连接匹配条件，即哪些列或常量被用于查找索引列上的值
										 eq_ref ：类似ref，区别就在使用的索引是唯一索引，对于每个索引键值，表中只有一条记录匹配，简单来说，就是多表连接中使用primary key或者 unique key作为关联条件
											const ：当MySQL对查询某部分进行优化，并转换为一个常量时，使用这些类型访问。如将主键置于where列表中，MySQL就能将该查询转换为一个常量。
										 system ：system是const类型的特例，当查询的表只有一行的情况下，使用system
										   NULL ：
											（从上到下，性能从差到好）
											
											
 possible_keys ： 该列完全独立于EXPLAIN输出所示的表的次序。这意味着在possible_keys中的某些键实际上不能按生成的表次序使用。如果该列是NULL，则没有相关的索引。在这种情况下，可以通过检查WHERE子句看是否它引用某些列或适合索引的列来提高你的查询性能。如果是这样，创造一个适当的索引并且再次用EXPLAIN检查查询
 
 
					 key ： 如果没有选择索引，键是NULL。要想强制MySQL使用或忽视possible_keys列中的索引，在查询中使用FORCE INDEX、USE INDEX或者IGNORE INDEX。
					 
					 
			 key_len ： 表示索引中使用的字节数，可通过该列计算查询中使用的索引的长度（key_len显示的值为索引字段的最大可能长度，并非实际使用长度，即key_len是根据表定义计算而得，不是通过表内检索出的）不损失精确性的情况下，长度越短越好。
			 
			 
					 ref ： 列与索引的比较，表示上述表的连接匹配条件，即哪些列或常量被用于查找索引列上的值。
					 
					 
					rows ： 估算出结果集行数，表示MySQL根据表统计信息及索引选用情况，估算的找到所需的记录所需要读取的行数。
					
					
			filtered ： 
			
			
				 Extra ： 
				 				Using where: 不用读取表中所有信息，仅通过索引就可以获取所需数据，这发生在对表的全部的请求列都是同一个索引的部分的时候，表示mysql服务器将在存储引擎检索行后再进行过滤
				 				Using temporary：表示MySQL需要使用临时表来存储结果集，常见于排序和分组查询，常见 group by ; order by
				 				Using filesort：当Query中包含 order by 操作，而且无法利用索引完成的排序操作称为“文件排序”
				 				Using join buffer：改值强调了在获取连接条件时没有使用索引，并且需要连接缓冲区来存储中间结果。如果出现了这个值，那应该注意，根据查询的具体情况可能需要添加索引来改进能。
				 				Impossible where：这个值强调了where语句会导致没有符合条件的行（通过收集统计信息不可能存在结果）。
				 				Select tables optimized away：这个值意味着仅通过使用索引，优化器可能仅从聚合函数结果中返回一行
				 				No tables used：Query语句中使用from dual 或不含任何from子句
				 			
```

# MySQL日志

### 错误日志

​		记录出错信息，也记录一些警告信息或者正确的信息

​		查看错误日志存放目录

```sql
show VARIABLES like "%log_error%"
```

​		查看警告日志级别

```sql
show VARIABLES like "%log_warnings%"

如果log_warnings值等于0，表示不记录警告信息
如果log_warnings值等于1，表示警告信息一并记录到错误日志中
如果log_warnings值大于1，表示“失败的连接”和信息和连接时“拒绝访问”的错误也会记录到错误日志中
```

### 查询日志

​	记录所有对数据库请求的信息，不论这些请求是否得到了正确的执行

​	查看是否开启查询日志

```sql
show variables like '%general_log%';

general_log如果为OFF表示关闭，ON为开启
general_log_file表示通用查询日志的文件路径
```

​	查询当前通用查询日志存储类型

```sql
show variables like '%log_output%';

查看当前慢查询日志输出的格式，
		可以是FILE（存储在数数据库的数据文件中的hostname.log）
		也可以是TABLE（存储在数据库中的mysql.general_log）
```

开启通用查询日志以及设置日志存储格式

```
开启通用日志查询： set global general_log=on;
关闭通用日志查询： set globalgeneral_log=off;
设置通用日志输出为表方式： set globallog_output=’TABLE’;
设置通用日志输出为文件方式： set globallog_output=’FILE’;
设置通用日志输出为表和文件方式：set global log_output=’FILE,TABLE’;
```

此设置重启后失效永久设置配置文件

```sql
general_log=1  #为1表示开启通用日志查询，值为0表示关闭通用日志查询
log_output=FILE,TABLE#设置通用日志的输出格式为文件和表
```

### 慢查询日志

设置一个阈值，将运行时间超过该值的所有SQL语句都记录到慢查询的日志文件中

查看是否开启慢查询日志

```sql
show variables like '%slow_query_log%';

slow_query_log为OFF表示关闭，ON为开启
slow_query_log_file表示慢查询日志文件路径
```

查询慢日志时间阈值

```sql
show variables like 'long_query_time';

单位为秒

修改全局慢查询日志时间
set global long_query_time=4;
```

查询当前通用查询日志存储类型

```sql
show variables like '%log_output%';

查看当前慢查询日志输出的格式，
		可以是FILE（存储在数数据库的数据文件中的hostname.log）
		也可以是TABLE（存储在数据库中的mysql.general_log）
```

查询没有使用索引的查询是否记录到慢查询中

```sql
设置未使用索引的查询是否记录到慢查询日志中
show variables like 'log_queries_not_using_indexes';

log_queries_not_using_indexes为OFF表示关闭，ON为开启

开启未使用索引慢查询日志
set global log_queries_not_using_indexes=1;
```

### 二级制日志

​								记录对数据库执行更改的所有操作

### 中继日志（bin-log）

​					 			中继日志也是二进制日志，用来给slave 库恢复

查询所有binlog

```
show binary logs;
```

查询指定中继日志数据

```
show binlog events in 'mysql-bin.000001';
```

查询binlog日志类型

```
show variables like '%binlog_format%';
```

日志类型分为三种

|   类型    |                             功能                             |
| :-------: | :----------------------------------------------------------: |
| STATEMENT | 基于SQL语句的复制(statement-based replication, SBR)，每一条会修改数据的sql语句会记录到binlog中。 |
|    ROW    | 基于行的复制(row-based replication, RBR)：不记录每一条SQL语句的上下文信息，仅需记录哪条数据被修改了，修改成了什么样子了。 |
|   MIXED   | 混合模式复制(mixed-based replication, MBR)：以上两种模式的混合使用，一般的复制使用STATEMENT模式保存binlog，对于STATEMENT模式无法复制的操作使用ROW模式保存binlog，MySQL会根据执行的SQL语句选择日志保存方式。 |

修改

配置文件：

```properties
#设置日志格式
binlog_format = mixed

#设置日志路径，注意路经需要mysql用户有权限写
log-bin = /data/mysql/logs/mysql-bin.log

#设置binlog清理时间
expire_logs_days = 7

#binlog每个日志文件大小
max_binlog_size = 100m

#binlog缓存大小
binlog_cache_size = 4m

#最大binlog缓存大小
max_binlog_cache_size = 512m
```

命令修改：

等等

```
set binlog_format='row';	
```

### 事务日志(重做日志（redo log）)

​								作用是确保事务的持久性，防止在发生故障的时间点，尚有脏页未写入磁盘。

​								在重启 MySQL 服务的时候，根据 redo log 进行重做，

​								从而达到事务的持久性这一特性。		

### 回滚日志

​								保存了事务发生之前的数据的一个版本，可以用于回滚，

​								同时可以提供多版本并发控制下的读（MVCC），也即非锁定读。

# 

# 数据库表

根据表名称获取建表语句

```sql
show create table t_user;
```







# 索引相关

## 表索引

查询某个表的索引

查看t_user表的索引

```sql
show index from t_user;
```

添加索引

```sql
ALTER TABLE t_user ADD INDEX user_index_username_and_age (username,age);
```







# 事务

查看事务隔离级别

```sql
show variables like 'transaction_isolation';
```

设置事务隔离级别

```sql
set transaction_isolation='read-uncommitted';						// 读未提交
set transaction_isolation='read-committed';							// 读已提交
set transaction_isolation='repeatable-read';						// 可重复度
set transaction_isolation='serializable';								// 串行化
```



# MySQL系统

查看MySQL系统变量参数

```
show variables;
```

查看某个具体参数

```
show variables like 'wait_timeout';				// 连接超时时间
```

