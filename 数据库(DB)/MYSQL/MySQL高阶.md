# MySQL连接问题

​		在mysql中如果出现这种异常，表示我们的连接异常被终止掉了，然后mysql随之自动关闭了

```
2019-08-14T11:18:30.558570Z 4 [Note] Aborted connection 4 to db: 'cattle' user: 'cattle' host: '10.18.16.99' (Got an error reading communication packets)
```

​		那么我们需要定位问题，就需要知道到底是什么原因引起的。所以我们看到连接想到会有两种可能，一是连接数量太多导致mysql崩溃，二是错误的连接太多，我们把他拒绝掉了，如果mysql崩溃了很显然就是连接太多了。

# 修改MySQL最大连接数

查看最大连接数

```mssql
show variables like '%max_connections%';
```

那么我们有两种方式。

​	一是临时修改，直接执行MySQL命令即可，二是修改配置文件。

​			命令：设置全局的最大连接数

```mssql
set global max_connections=2000;
```

​			配置文件：在/etc/my.conf文件加入，然后重启服务

```mssql
 max_connections=2000
```

# 查看当前mysql的连接客户端

即可查看当前所有连接的客户端

```mssql
show processlist;
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

```mssql
flush hosts
```

并且在配置文件中添加如下配置

```mssql
max_connect_errors=1000
```

# MySQL数据库被锁

如果我们使用Mysql发现数据库被锁了

如果他的值不是null那么表示数据库被锁了

```mssql
use 被锁的数据库
select * from DATABASECHANGELOGLOCK;
```

解锁，将他们都设置为null即可

```mssql
update DATABASECHANGELOGLOCK set LOCKED="", LOCKGRANTED=null, LOCKEDBY=null where ID=1;
```

# MySQL锁表

查看被锁的表

```mssql
SELECT * FROM information_schema.innodb_trx;
```

然后kill掉锁住的表

这里的id是查询的trx_mysql_thread_id,

```mssql
kill id
```

# MySQL设置

## 自增

```mysql
# 查看自增
show variables like '%increment%';

# 修改自增
set @@global.auto_increment_increment = 1; 
set @@auto_increment_increment =1;
set @@global.auto_increment_offset =1;
set @@auto_increment_offset =1;
```

## 编码

```mysql
# 查看编码
show variables like '%char%';
```

​		两种方式修改编码：

​			第一种配置文件（永久生效）：

```mysql
# mysql配置文件修改如下
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

​			第二种配置命令（临时生效）：

```mysql
# 修改配置
set character_set_client = utf8;
set character_set_server = utf8;
set character_set_connection = utf8;
set character_set_database = utf8;
set character_set_results = utf8;
set collation_connection = utf8_general_ci;
set collation_database = utf8_general_ci;
set collation_server = utf8_general_ci;
```



# MySQL性能分析

## EXPLAIN

我们使用MySQL语句分析的关键字EXPLAIN，例如我们分析一段sql语句，并且查看他是否使用索引

```sql
EXPLAIN SELECT * from t_user;
/* 使用show WARNINGS可以查询MySQL的可能优化的语句 */
show WARNINGS;
```

得到如下结果

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1571039035693.png)

下面是每个字段的解释



```properties
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



## TRACE工具

​		MySQL5.6版本后提供了对SQL的跟踪工具trace，通过使用trace阔以让我们明白optimizer如何选择执行计划的。

```sql
/* 开启TRACE工具 */
set optimizer_trace='enabled=on',end_markers_in_json=on;

/* 关闭TRACE工具 */
set optimizer_trace='enabled=off';
```

​		那么我们如何使用呢？

​		如下：

```sql
/* 开启TRACE工具 */
set optimizer_trace='enabled=on',end_markers_in_json=on;

/* 编写查询语句以及查看TRACE */
select * from t_user;
SELECT * FROM information_schema.OPTIMIZER_TRACE;


/* 查看TRACE并且格式化，shell窗口方式 */
SELECT * FROM information_schema.OPTIMIZER_TRACE\G;
```

​		查看返回结果：

```json

{
  "steps": [
    {
      "join_preparation": {  -- 第一阶段：SQL准备阶段，格式化sql
        "select#": 1,
        "steps": [
          {
            "expanded_query": "/* select#1 */ select `t_student`.`id` AS `id`,`t_student`.`std_name` AS `std_name`,`t_student`.`age` AS `age`,`t_student`.`class_id` AS `class_id`,`t_student`.`gmt_create` AS `gmt_create` from `t_student` where (`t_student`.`std_name` > 'a') order by `t_student`.`age`"
          }
        ] /* 步骤 */
      } /* 准备阶段 */
    },
    {
      "join_optimization": {  -- 第二阶段：SQL优化阶段
        "select#": 1,
        "steps": [
          {
            "condition_processing": {  -- 条件处理
              "condition": "WHERE",
              "original_condition": "(`t_student`.`std_name` > 'a')",
              "steps": [
                {
                  "transformation": "equality_propagation",
                  "resulting_condition": "(`t_student`.`std_name` > 'a')"
                },
                {
                  "transformation": "constant_propagation",
                  "resulting_condition": "(`t_student`.`std_name` > 'a')"
                },
                {
                  "transformation": "trivial_condition_removal",
                  "resulting_condition": "(`t_student`.`std_name` > 'a')"
                }
              ] /* steps */
            } /* 条件处理，优化 */
          },
          {
            "substitute_generated_columns": {
            } /* 替代生成的列 */
          },
          {
            "table_dependencies": [  -- 表依赖详情
              {
                "table": "`t_student`",
                "row_may_be_null": false,
                "map_bit": 0,
                "depends_on_map_bits": [
                ] /* depends_on_map_bits */
              }
            ] /* table_dependencies */
          },
          {
            "ref_optimizer_key_uses": [
            ] /* ref_optimizer_key_uses */
          },
          {
            "rows_estimation": [  -- 预估表的访问成本
              {
                "table": "`t_student`",
                "range_analysis": {
                  "table_scan": {   -- 全表扫描
                    "rows": 100300,  -- 行数
                    "cost": 20351  -- 计算查询成本
                  } /* table_scan */,
                  "potential_range_indexes": [  -- 查询可能使用的索引
                    {
                      "index": "PRIMARY",  -- 主键索引
                      "usable": false,  -- 未使用
                      "cause": "not_applicable"  -- 原因：不适合
                    },
                    {
                      "index": "idx_std_age",  -- age索引
                      "usable": false,  -- 未使用
                      "cause": "not_applicable"  -- 原因：不适合
                    },
                    {
                      "index": "idx_std_name_age_class",  -- stdname,age,class的组合索引
                      "usable": true,  -- 使用
                      "key_parts": [
                        "std_name",
                        "age",
                        "class_id",
                        "id"
                      ] /* key_parts */
                    }
                  ] /* potential_range_indexes */,
                  "setup_range_conditions": [
                  ] /* setup_range_conditions */,
                  "group_index_range": {  -- group 用到的索引
                    "chosen": false,  -- 未使用
                    "cause": "not_group_by_or_distinct"  -- 原因：未使用group by 或者 distinct
                  } /* group_index_range */,
                  "analyzing_range_alternatives": {   -- 分析各个索引使用成本
                    "range_scan_alternatives": [
                      {
                        "index": "idx_std_name_age_class",
                        "ranges": [
                          "a < std_name"  -- 索引使用范围
                        ] /* ranges */,
                        "index_dives_for_eq_ranges": true,
                        "rowid_ordered": false,  -- 使用该索引获取的记录是否按照主键排序
                        "using_mrr": false,
                        "index_only": false,  -- 是否使用覆盖索引
                        "rows": 50150,  -- 索引扫描行数
                        "cost": 60181,   -- 索引使用成本
                        "chosen": false,  -- 是否选择该索引：否
                        "cause": "cost"  -- 原因：消耗，由于使用索引成本为60181不使用为20351，放弃使用
                      }
                    ] /* range_scan_alternatives */,
                    "analyzing_roworder_intersect": {  -- 分析使用索引合并的成本
                      "usable": false,
                      "cause": "too_few_roworder_scans"
                    } /* analyzing_roworder_intersect */
                  } /* analyzing_range_alternatives */
                } /* range_analysis */
              }
            ] /* rows_estimation */
          },
          {
            "considered_execution_plans": [  -- 分析出的执行计划
              {
                "plan_prefix": [
                ] /* plan_prefix */,
                "table": "`t_student`",
                "best_access_path": {  -- 最优访问路径
                  "considered_access_paths": [  --分析出的最终访问路径
                    {
                      "rows_to_scan": 100300,
                      "access_type": "scan",  -- 访问类型：为scan，全表扫描
                      "resulting_rows": 100300,
                      "cost": 20349,
                      "chosen": true,  -- 确定选择
                      "use_tmp_table": true
                    }
                  ] /* considered_access_paths */
                } /* best_access_path */,
                "condition_filtering_pct": 100,
                "rows_for_plan": 100300,
                "cost_for_plan": 20349,
                "sort_cost": 100300,
                "new_cost_for_plan": 120649,
                "chosen": true
              }
            ] /* considered_execution_plans */
          },
          {
            "attaching_conditions_to_tables": {   -- 为查询的表添加条件
              "original_condition": "(`t_student`.`std_name` > 'a')",
              "attached_conditions_computation": [
              ] /* attached_conditions_computation */,
              "attached_conditions_summary": [    -- 添加条件结果
                {
                  "table": "`t_student`",
                  "attached": "(`t_student`.`std_name` > 'a')"
                }
              ] /* attached_conditions_summary */
            } /* attaching_conditions_to_tables */
          },
          {
            "clause_processing": {   -- order by 处理
              "clause": "ORDER BY",
              "original_clause": "`t_student`.`age`",
              "items": [
                {
                  "item": "`t_student`.`age`"
                }
              ] /* items */,
              "resulting_clause_is_simple": true,
              "resulting_clause": "`t_student`.`age`"
            } /* clause_processing */
          },
          {
            "reconsidering_access_paths_for_index_ordering": {    -- 重构索引处理顺序
              "clause": "ORDER BY",
              "steps": [
              ] /* steps */,
              "index_order_summary": {
                "table": "`t_student`",
                "index_provides_order": false,
                "order_direction": "undefined",
                "index": "unknown",
                "plan_changed": false
              } /* index_order_summary */
            } /* reconsidering_access_paths_for_index_ordering */
          },
          {
            "refine_plan": [
              {
                "table": "`t_student`"
              }
            ] /* refine_plan */
          }
        ] /* steps */
      } /* join_optimization */
    },
    {
      "join_execution": {    -- 第三阶段：SQL执行阶段
        "select#": 1,
        "steps": [
          {
            "filesort_information": [
              {
                "direction": "asc",
                "table": "`t_student`",
                "field": "age"
              }
            ] /* filesort_information */,
            "filesort_priority_queue_optimization": {
              "usable": false,
              "cause": "not applicable (no LIMIT)"
            } /* filesort_priority_queue_optimization */,
            "filesort_execution": [
            ] /* filesort_execution */,
            "filesort_summary": {
              "rows": 100000,
              "examined_rows": 100000,
              "number_of_tmp_files": 14,
              "sort_buffer_size": 262016,
              "sort_mode": "<sort_key, packed_additional_fields>"
            } /* filesort_summary */
          }
        ] /* steps */
      } /* join_execution */
    }
  ] /* steps */
}
```

​		使用后关闭否则影响性能

```sql
set optimizer_trace='enabled=off';
```



# MySQL性能优化

​		数据库性能取决于数据库级别的几个因素，例如表，查询和配置设置。这些软件结构导致在硬件级别执行CPU和I / O操作，您必须将这些操作最小化并使其尽可能高效。在研究数据库性能时，首先要学习软件方面的高级规则和准则，并使用时钟时间来衡量性能。成为专家后，您将了解有关内部情况的更多信息，并开始测量诸如CPU周期和I / O操作之类的东西。

​		典型的用户旨在从其现有的软件和硬件配置中获得最佳的数据库性能。高级用户寻找机会改进MySQL软件本身，或开发自己的存储引擎和硬件设备以扩展MySQL生态系统。

​		我们将优化大概分为3个类型

- ​					在数据库级别进行优化

```sh
使数据库应用程序快速运行的最重要因素是其基本设计：

1、表格的结构是否正确？特别是，这些列是否具有正确的数据类型，并且每个表都具有适合于该工作类型的列吗？

			例如，执行频繁更新的应用程序通常具有许多表而具有很少的列，而分析大量数据的应用程序通常具有较少的表而具有很多列。

2、是否安装了正确的 索引以提高查询效率？
	
			例如，一张数据库表数据上千万，查询特别慢，没有给数据库表添加索引。
			
3、您是否为每个表使用了适当的存储引擎，并利用了所使用的每个存储引擎的优势和功能？

			例如，特别地，对于InnoDB 诸如MyISAM 性能或可伸缩性之类的事务性存储引擎或诸如非 事务性存储引擎的选择 可能非常重要。
			
4、每个表都使用适当的行格式吗？

			例如，该选择还取决于表使用的存储引擎。特别是，压缩表使用较少的磁盘空间，因此需要较少的磁盘I / O来读取和写入数据。压缩适用于带InnoDB表的所有工作负载 以及只读 MyISAM表。
			
5、应用程序是否使用适当的 锁定策略？

			例如，通过在可能的情况下允许共享访问，以便数据库操作可以同时运行，并在适当的时候请求独占访问，以使关键操作获得最高优先级。同样，存储引擎的选择很重要。该InnoDB存储引擎处理大部分锁定问题，而不需要您的参与，允许在数据库更好的并发，减少试验和调整的金额，让您的代码。
			
6、用于缓存的 所有内存区域大小是否正确？

			例如，足够大以容纳经常访问的数据，但又不能太大以至于它们会使物理内存过载并导致分页。要配置的主要内存区域是InnoDB缓冲池，MyISAM键高速缓存和MySQL查询高速缓存。
```

- ​					在硬件级别进行优化

```
随着数据库变得越来越繁忙，任何数据库应用程序最终都会达到硬件极限。DBA必须评估是否有可能调整应用程序或重新配置服务器以避免这些 瓶颈，或者是否需要更多的硬件资源。系统瓶颈通常来自以下来源：

1、磁盘搜索？

		例如：磁盘查找数据需要花费时间。对于现代磁盘，此操作的平均时间通常小于10毫秒，因此理论上我们可以执行约100秒钟的搜索。这段时间随着新磁盘的使用而缓慢改善，并且很难为单个表进行优化。优化寻道时间的方法是将数据分发到多个磁盘上。
		
2、磁盘读写？

		例如：当磁盘位于正确的位置时，我们需要读取或写入数据。对于现代磁盘，一个磁盘可提供至少10–20MB/s的吞吐量。与查找相比，优化起来更容易，因为您可以从多个磁盘并行读取。
		
3、CPU周期？

		例如：当数据位于主存储器中时，我们必须对其进行处理以获得结果。与内存量相比，拥有较大的表是最常见的限制因素。但是对于小内存，速度通常不是问题。
		
4、内存带宽？

		例如：当CPU需要的数据超出CPU缓存的容量时，主内存带宽将成为瓶颈。对于大多数系统来说，这是一个不常见的瓶颈，但是要意识到这一点。
```

- ​					平衡便携性和性能

```
要在便携式MySQL程序中使用面向性能的SQL扩展，可以在/*! */注释定界符中的语句中包装特定于MySQL的关键字。其他SQL Server忽略注释的关键字。有关编写注释的信息，请参见第9.6节“注释”。

支持的注释格式：
		
			1、# 注释
			2、-- 注释
			3、/* 注释 */ 
```

​		数据库级别的优化有如下方案：

## 优化SQL

​		那么优化SQL我们首先优化查询语句：

```properties
where语句优化:
		
		1:	减少不必要的括号。
			
			示例: 	((a AND b) AND c OR (((a AND b) AND (c AND d))))
			
			优化:		(a AND b AND c) OR (a AND b AND c AND d)
			
			概述:		使用多个扩号时一定要注意逻辑，优化SQL语句可读性
			
		2:	条件折叠
			
			示例:   (a<b AND b=c) AND a=5
			
			优化:		b>5 AND b=c AND a=5
			
			概述:		首先优化掉括号，然后将b的条件不指向a，因为a已经会是5了
			
		3:	条件消除
			
			示例:		(b>=5 AND b=5) OR (b=6 AND 5=5) OR (b=7 AND 5=6)
			
			优化:		b=5 OR b=6
			
			概述:		我们可以看到整个查询条件是冗余的，那么针对于这种情况需要重新梳理逻辑减少条件

		4:	索引使用的常量表达式仅计算一次
		
		5:	COUNT(*)上没有一个单一的表WHERE是从该表信息直接检索MyISAM 和MEMORY表。NOT NULL当仅与一个表一起使用时，对于任何表达式也可以执行此操作。
		
		6: 早期检测无效的常量表达式。MySQL快速检测到某些 SELECT语句是不可能的，并且不返回任何行。
		
			示例:		EXPLAIN SELECT * FROM t_user WHERE 7=8
			
			概述:		对于这种不可能存在的条件MySQL是不会帮我们去进行执行的
			
		7: HAVINGWHERE如果您不使用GROUP BY或汇总功能（COUNT()， MIN()等），则与合并 。
		
			示例:		EXPLAIN SELECT * FROM t_user HAVING 1 =1 
			
			概述:		我们没有使用Group或者是COUNT()等方式，我们再去使用HAVING，MySQL会自动的帮我们合并
			
		8: 对于联接中的每个表，WHERE构造一个简单 WHERE的表以获得表的快速 评估，并尽快跳过行。
		
		等等等等的语句优化
		
总结:		针对与上面的这些情况MySQL都会去自动帮助我们进行优化，但是我们需要注意这些操作我们如果自己直接优化了SQL语句执行的话，那么MYSQL就不需要再去进行语句的检查优化了，所以会提升效率。

快速查询示例：
		# Count(*)效率也快
		SELECT COUNT(*) FROM tbl_name;
		
		# 查询同一个字段的最大和最小值也快
		SELECT MIN(key_part1),MAX(key_part1) FROM tbl_name;
		
		# 查询表数据排序分页（全部正序）
		SELECT ... FROM tbl_name
  ORDER BY key_part1,key_part2,... LIMIT 10;
  
  	# 查询表数据排序分页（全部倒序）
  	SELECT ... FROM tbl_name
  ORDER BY key_part1 DESC, key_part2 DESC, ... LIMIT 10;
```

​		我们使用多组多字段的查询时OR的优化,我们可以直接使用in来进行查询

```sql
原语句: SELECT ... FROM t1 WHERE ( col_1 = 'a' AND col_2 = 'b' ) OR ( col_1 = 'c' AND col_2 = 'd' );
优化后: SELECT ... FROM t1 WHERE ( col_1, col_2 ) IN (( 'a', 'b' ), ( 'c', 'd' ));

以及如下单查询条件:
原语句: 
优化后: 
注意事项：仅使用IN()谓词，不使用NOT IN()，不要使用not in语句否则效率极其低下。
```

### where优化总结



### or优化总结



### in优化总结



### Order排序优化

​		使用EXPLAIN进行分析

```sql
EXPLAIN SELECT * from t_user where type='admin' ORDER BY age ;
```

​		注意查看排序是否使用到了索引

```yaml
# 返回的Extra列注意

	尽量出现Using index而不是Using filesort，不一定会是使用索引但是尽量表示不出现Using filesort
	Using filesort表示数据没有使用索引，将数据查询后进行文件排序，数据量大时会导致长时间文件排序过慢的问题
	也会出现前面使用到了索引条件，然后在进行文件排序，主要通过分析Extra值来拍断
```

​		使用in时排序不生效

```
使用 in 条件索引树查询数据不一定是排序的数据，导致无法使用索引，采用filesort
```

```sql
注意避免使用  

# 在查询的时候我们数据量过大的时候也会采用文件排序，我们查询指定的索引字段进行优化可以走索引排序

# 不会使用索引排序
EXPLAIN SELECT * from t_user where type='admin' ORDER BY age ;

# 会使用索引排序
EXPLAIN SELECT type,age from t_user where type='admin' ORDER BY age ;
```

​		filesort文件排序方式有两种：

- ​				单路排序：一次性取出满足条件所有字段然后sort buffer中进行排序，使用TRACE工具可以定位

- ​				双路排序：又叫做（回表排序模式），根据相应的条件取出相应的排序字段，然后定位到数据行ID，然后sort buffer中进行排序。

  通过系统变量，max_length_for_sort_data（默认1024字节），如果小于这个值进行单路排序，如果大于这个值则进行双路排序。

  

### Group分组优化

​		1、GroupBy使用时，会先进行排序然后进行Group，优化时注意OrderBy是否有使用索引。

​		2、尽量不要使用Having进行条件过滤，尽量在Where把条件写好。

​		3、以及Group使用时如果不需要排序，可以禁用掉排序优化SQL，ORDER BY NULL



### 注意事项

## 优化索引

​		索引用于快速查找具有特定列值的行。没有索引，MySQL必须从第一行开始，然后再通读整个表以找到相关的行。桌子越大，花费越多。如果表中有相关列的索引，MySQL可以快速确定要在数据文件中间查找的位置，而不必查看所有数据。这比顺序读取每一行要快得多。

​		大多数MySQL索引（`PRIMARY KEY`， `UNIQUE`，`INDEX`和 `FULLTEXT`）存储在 [B树](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_b_tree)。例外：空间数据类型的索引使用R树；`MEMORY` 表还支持[哈希索引](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_hash_index); `InnoDB`对`FULLTEXT`索引使用倒排列表。

​		通常，如以下讨论中所述使用索引。[第8.3.8节“ B树和哈希索引的比较”](https://dev.mysql.com/doc/refman/5.7/en/index-btree-hash.html)`MEMORY`中描述了哈希索引特有的特性（如表中所用 ） 。

​		主键索引：

```
		表的主键表示您在最重要的查询中使用的一列或一组列。它具有关联的索引，可提高查询性能。查询性能可从NOT NULL优化中受益，因为它不能包含任何NULL值。使用InnoDB存储引擎，可以对表数据进行物理组织，以根据一个或多个主键列进行超快速查找和排序。
		
		如果您的表又大又重要，但没有明显的列或一组列用作主键，则可以创建一个单独的列，并使用自动增量值作为主键。当您使用外键联接表时，这些唯一的ID可用作指向其他表中相应行的指针。
```



​		针对于文本类型的字段，我们建立索引时采用前缀索引，即前面的字符建立索引，但是需要注意根据名字排序时会导致排序失效不使用索引，禁止对这种数据进行排序。

```sql
ALTER TABLE t_user ADD INDEX user_index_username_and_age (username(20),age);
```

​		



## 优化数据库结构



## 优化InnoDB表



## 优化MyISAM表





## 优化内存表



## 执行计划





## 控制查询优化器



## 缓冲和缓存



## 优化锁定操作



## 优化MySQL服务器



## 基准测试



## 检查服务器进程线程信息



# MySQL日志

## 错误日志

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

## 查询日志

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

```mssql
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

## 慢查询日志

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

## 中继日志（bin log）

​		中继日志也是二进制日志，用来给slave 库恢复



​		记录对数据库执行更改的所有操作

​		查看二进制日志是否开启

```sql
show variables like 'log_bin';
```

​		如何开启二进制日志，修改my.cnf配置文件开启

```properties
## 同一局域网内注意要唯一
server-id=100  
## 开启二进制日志功能，可以随便取（关键），会在数据目录下生成一个一个mysql-bin.000000X文件
log-bin=mysql-bin
```

​		查看目前的二进制日志文件

```sql
/* 两条语句都可查看 */
show binary logs;
show master logs;
```

​			刷新日志文件，会从现在开始重新生成一个bin_log文件，重启时也会重新生成

```mssql
FLUSH LOGS;
```

​			删除某个二进制文件

```sql
/* 指定文件名删除 */
purge binary logs to 'mysql-bin.000001';

/* 按照时间点进行清理 */
purge binary logs before '2017-03-10 10:10:00';

/* 删除7天前文件 */
purge master logs before date_sub( now( ), interval 7 day);
```

​		查询指定中继日志数据

```sql
show binlog events in 'mysql-bin.000001';
```

​		查询binlog日志类型

```sql
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
binlog_format = row

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

```sql
set binlog_format='row';	
```

## 重做日志（redo log）

​		作用是确保事务的持久性，防止在发生故障的时间点，尚有脏页未写入磁盘。

​		在重启 MySQL 服务的时候，根据 redo log 进行重做，

​		从而达到事务的持久性这一特性。

​		首先我们需要了解为什么需要这个redo log？

​		MySQL作为一个存储系统，为了保证数据的可靠性，最终得落盘。但是，又为了数据写入的速度，需要引入基于内存的"缓冲池"。其实不止MySQL，这种引入缓冲来解决速度问题的思想无处不在。既然数据是先缓存在缓冲池中，然后再以某种方式刷新到磁盘，那么就存在因宕机导致的缓冲池中的数据丢失，为了解决这种情况下的数据丢失问题，引入了redo log。在其他存储系统，比如Elasticsearch中，也有类似的机制，叫translog。

​		也就是说，MySQL为了提升数据写入的速度会将数据缓存buffer中，然后再从buffer中写入到磁盘，但是我们在使用事务的时候会出现某一种情况，我们刚刚操作了一个事务，这个事务记录写入到了buffer缓存中，这个时候MySQL突然宕机，导致我们这个事务虽然已经提交或者操作但是还没有来得及写入磁盘，然后重启后发现找不到这个事务日志了，这样就会导致我们已经提交或者操作的事务丢失掉，所以为了解决这样的情况MySQL引入了redo log。

​		redo log日志写入方式如下如下图所示：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606373119771.png)

​		

​		MySQL有3种从buffer中同步到redo log的策略，如下：

```mssql
-- 通过innodb_flush_log_at_trx_commit，定义写入策略，分别有3种
-- 三种策略的表示分别是0、1、2，默认为1

--		0 : 事务操作的时候不会马上写入os buffer，然后每隔一秒钟从redo log buffer中同步到os buffer，紧接着调用fsync存储到redo log磁盘中，
--		1 : 事务操作的时候直接将redo log buffer写入os buffer，然后马上调用fsync存储到redo log磁盘中，过程没有间隔。
--		2 : 事务操作的时候直接将redo log buffer写入os buffer，然后每隔一秒钟调用一次fsync存储到redo log磁盘中，过程间隔一秒。

-- 查询当前的日志刷新策略
SHOW VARIABLES like "%innodb_flush_log_at_trx_commit%"

-- 相关参数
innodb_log_buffer_size					-- log buffer的大小，默认8M
innodb_log_file_size						-- 事务日志的大小，默认5M
innodb_log_files_group					-- 事务日志组中的事务日志文件个数，默认2个
innodb_log_group_home_dir				-- 事务日志组路径，当前目录表示数据目录
innodb_mirrored_log_groups 			-- 指定事务日志组的镜像组个数
```

​		过程如下：

```yaml
0 : 事务操作 --> 写入log buffer --> 间隔一秒 --> 写入os buffer --> 调用fsync --> 写入磁盘

1 : 事务操作 --> 写入log buffer --> 写入os buffer --> 调用fsync --> 写入磁盘

2 : 事务操作 --> 写入log buffer --> 写入os buffer --> 间隔一秒 --> 调用fsync --> 写入磁盘

# 我们可以看到，0和2都间隔了一秒钟，而1没有任何间隔，那么他的数据的一致性肯定是最高的，但是性能没有其他两种方案高
```

​		示例图：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606373068583.png)

## 回滚日志（undo log）

​		保存了事务发生之前的数据的一个版本，可以用于回滚，同时可以提供多版本并发控制下的读（MVCC），也即非锁定读。

​		查询undo log信息。

```mssql
show variables like "%undo%";
```

​		返回如下

```mssql
innodb_max_undo_log_size							-- 当超过这个阀值（默认是1G），会触发truncate回收（收缩）动作，truncate后空间缩小到10M。
innodb_undo_directory									-- undo日志文件路径
innodb_undo_log_truncate							-- 即开启在线回收（收缩）undo log日志文件，支持动态设置，默认关闭
innodb_undo_logs											-- undo log文件数量
innodb_undo_tablespaces								-- 表空间，默认回滚段是在系统表空间中，至少有一个回滚段，如果要设置单独的undo表空间，innodb_undo_tablespaces至少2个，当一个表空间进行日志截断的时候，此表空间会临时offline，另外一个表空间必须在线并且可用；表空间的数量是通过参数innodb_undo_tablespaces 来定义，默认值是0，
```

​		undo log的日志存储过程也如redo log一样。

​		undo log 存放在MySQL的数据目录下，默认为ibdata1文件

​		如果开启innodb_file_per_table，则会存放在每个表数据相同的.ibd结尾的文件

```mssql
-- 查询是否开启innodb_file_per_table，OFF关闭，ON开启，（默认开启）
show variables like "%innodb_file_per_table%";
```

​		当事务提交的时候，innodb不会立即删除undo log，因为后续还可能会用到undo log，如隔离级别为repeatable read时，事务读取的都是开启事务时的最新提交行版本，只要该事务不结束，该行版本就不能删除，即undo log不能删除。

​		但是在事务提交的时候，会将该事务对应的undo log放入到删除列表中，未来通过purge来删除。并且提交事务时，还会判断undo log分配的页是否可以重用，如果可以重用，则会分配给后面来的事务，避免为每个独立的事务分配独立的undo log页而浪费存储空间和性能。

​		delete操作实际上不会直接删除，而是将delete对象打上delete flag，标记为删除，最终的删除操作是purge线程完成的。

​		update分为两种情况：update的列是否是主键列。



- ​				如果不是主键列，在undo log中直接反向记录是如何update的。即update是直接进行的。

- ​				如果是主键列，update分两部执行：先删除该行，再插入一行目标行。		

  

  undo log主要分为两种：

- ​                insert undo log

  ​		代表事务在insert新记录时产生的undo log, 只在事务回滚时需要，并且在事务提交后可以被立即丢弃。

- ​	            update undo log

  ​		事务在进行update或delete时产生的undo log; 不仅在事务回滚时需要，在快照读时也需要；所以不能随便删除，只有在快速读或事务回滚不涉及该日志时，对应的日志才会被purge线程统一清除。







### MVCC（多版本并发控制）

​		MVCC，全称Multi-Version Concurrency Control，即多版本并发控制。MVCC是一种并发控制的方法，一般在数据库管理系统中，实现对数据库的并发访问，在编程语言中实现事务内存。

​		我们知道MySQL中的事务分别有各个等级的事务隔离级别，那么MySQL是如何实现这些隔离级别的呢？尤其是我们常用的可重复读，答案就是：MVCC。

​		那么MVCC是如何实现多版本并发控制的呢？

​		首先他是通过如下3个点，我们反向来介绍：

​			Read View（读视图，控制读写可见性）

​			undo日志（记录事务操作，存储操作日志，以及回滚数据）

​			隐式字段（记录事务数据，隐式主键，事务ID，回滚指针等等）

​		下面我们以如下的一张表来进行示范，建表语句如下：

```sql
-- 很简单的一张测试mvcc表
CREATE TABLE `test_mvcc`  (
  `id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```

​		然后我们进行操作。

​		思路如下，首先我们开启事务，不去进行操作的话这个时候事务是没有生效，举例：

```sql
-- 首先我们开启事务
BEGIN;

-- 然后打开另一个窗口查询我们的事务
SELECT * FROM information_schema.INNODB_TRX;
```

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606375704037.png)

​		我们发现并没有任何的事务记录，我们在原来的事务窗口查询一下数据

```mssql
-- 查询test_mvcc
select * from test_mvcc;

-- 然后打开另一个窗口查询我们的事务
SELECT * FROM information_schema.INNODB_TRX;
```

​		我们发现这个时候已经有事务记录了![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606375804446.png)

​		那么我们可以判断出来这个事务记录，肯定是我们在开启事务操作的过程，使用了查询或者修改语句才会给我们创建事务ID。

​		首先我们了解一下事务ID，事务ID是一个记录事务的ID，他是随着操作次数进行上升的一个ID，也就是说事务ID是自动增长的，那么这个自动增长的特性会在后面的Read View中体现出来。

#### 隐式字段

​		每行记录除了我们自定义的字段外，还有数据库隐式定义的，也就是除了我们开启的事务的修改的一些数据，还会给我们记录一些隐式字段，有如下：

- ​			DB_TRX_ID

​				最近修改(修改/插入)事务ID，记录创建这条记录/最后一次修改该记录的事务ID，操作ID

- ​			DB_ROW_ID

​				隐含的自增ID（隐藏主键），如果数据表没有主键，InnoDB会自动以DB_ROW_ID产生一个聚簇索引

- ​			DB_ROLL_PTR

​				回滚指针，指向这条记录的上一个版本（存储于rollback segment里）

- ​			删除标记

​				记录被更新或删除并不代表真的删除，而是删除标记变了

​		例如我们修改了一个name，那么对应的隐式字段如下：

​				类似于如下

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606381533774.png)				这只是单条的隐式字段后续我们会写入到redo log中，并且生成相应的回滚语句。



#### undo log版本链

​		在操作的时候会将相应的隐式字段记录到undo log中，并且生成为版本链，数据结构类似如下，左侧为执行的语句：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606382271757.png)

#### ReadView

​		Read View我们听名字就可以知道他是读取一个读视图，那么这个读视图是干啥的呢？这个读视图其实就是主要用于记录我们的事务ID，通过不同区分的事务ID进行可见性判断。

​		Read View遵循一个可见性算法，主要是将要被修改的数据的最新记录中的DB_TRX_ID（即当前事务ID）取出来，与系统当前其他活跃事务的ID去对比（由Read View维护），如果DB_TRX_ID跟Read View的属性做了某些比较，不符合可见性，那就通过DB_ROLL_PTR回滚指针去取出Undo Log中的DB_TRX_ID再比较，即遍历链表的DB_TRX_ID（从链首到链尾，即从最近的一次修改查起），直到找到满足特定条件的DB_TRX_ID, 那么这个DB_TRX_ID所在的旧记录就是当前事务能看见的最新老版本。

​		那么这句话什么意思呢？

​		Read View的读取视图，我们拿事务ID进行判断，来判断我们从undo日志中读取数据，下面我们使用可重复度级别简单概述：

​				可重复读：

​						两个不相同的事务，针对事务开启时操作，例如事务A和事务B。

​										事务A查询id1，第一次查询为bigkang，事务B修改了id1的数据为kang123，并且提交了事务。

​										那么事务A现在查询出来的还是bigkang，那么MVCC是如何控制的呢？

​		通过我们的Read View

​				那么他是如何实现的呢？

​				下面我们进行假设，数据原来就是bigkang，最后一次提交的ID为60，现在创建两个事务。

​		简单的来说控制我们的可重复读，通过如下逻辑即可判断，只是示例大概流程：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606378593630.png)

​		实际的Read View主要包含如下：		

​				trx_ids

​						记录了当前活跃的事务列表，事务开启的时候, 其它未提交的活跃的事务ID。

​				up_limit_id

​						记录trx_list列表中事务ID最小的ID。

​				low_limit_id

​						ReadView生成时刻系统尚未分配的下一个事务ID，也就是目前已出现过的事务ID的最大值+1。

​				creator_trx_id

​						当前自己的事务ID，生成该readview的事务的事务id

​		MySQL的源码判断流程

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606387035392.png)

​		下面是针对代码解析

```c++
# id < m_up_limit_id 判断当前的版本链ID是不是小于活跃的最小ID，如果小于最小活跃事务ID表示事务已经提交不是活跃事务
# id == m_creator_trx_id 表示当前版本链ID是不是等于当前事务的ID如果是返回true表示可见
if (id < m_up_limit_id || id == m_creator_trx id){
	return(true)
}

# 检查id完整性
check_trx_id_sanity(id, name);

# 判断如果当前版本链ID大于最大ID则直接返回，不可见
if(id >= m_low_limit_id){
	return(false);
# 判断如果没有活跃ID，表示没有其他事务，表示直接可见
}else if (m_ids.empty()){
	return(true);
}
```

​		对于小于等于RC的隔离级别，每次SQL语句结束后都会调用read_view_close_for_mysql将read view从事务中删除，这样在下一个SQL语句启动时，会判断trx->read_view为NULL，从而重新申请。对于RR隔离级别，则SQL语句结束后不会删除read_view，从而下一个SQL语句时，使用上次申请的，这样保证事务中的read view都一样，从而实现可重复读的隔离级别。

​		根据Read View对上面的图例做出更新：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1606389797054.png)

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
ALTER TABLE t_user ADD INDEX idx_user_index_username_and_age (username,age);
```

# 事务

​		查看事务隔离级别

```sql
show variables like 'transaction_isolation';
```

​		设置事务隔离级别

```sql
set transaction_isolation='read-uncommitted';						// 读未提交
set transaction_isolation='read-committed';							// 读已提交
set transaction_isolation='repeatable-read';						// 可重复度
set transaction_isolation='serializable';								// 串行化
```

​		查看InnoDB引擎锁行锁

```sql
show STATUS LIKE "%innodb_row_lock%"
```

​		返回如下

```yaml
Innodb_row_lock_current_waits					# InnoDB行锁当前锁定的数量
Innodb_row_lock_time									# InnoDB行锁等待时间
Innodb_row_lock_time_avg							#	InnoDB行锁平均每次的等待时间
Innodb_row_lock_time_max							# InnoDB行锁从系统启动到现在等待最长的时间
Innodb_row_lock_waits									# InnoDB行锁启动到现在等待的数量
```

​		SQL命令查看以及释放锁

```sql
-- 查看InnoDB当前事务
SELECT * FROM information_schema.INNODB_TRX;

-- 查看InnoDB当前的锁
SELECT * FROM information_schema.INNODB_LOCKS;

-- 查看InnoDB当前的锁等待
SELECT * FROM information_schema.INNODB_LOCK_WAITS;

-- 释放锁
KILL trx_mysql_thread_id;

-- 显示InnoDB最近状态
SHOW ENGINE INNODB STATUS;
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

