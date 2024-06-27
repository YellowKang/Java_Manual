## 	MySQL简介

​		此处以MySQL5.7为示例进行演示

​		官网地址：[点击进入](https://dev.mysql.com/doc/refman/5.7/en/)

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

```
GRANT All ON * . * TO 'bigkang'@'%';
GRANT All ON * . * TO 'zsy'@'%';
```

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

## 数据库表

​		查询数据库中所有的表

```sql
SELECT * FROM INFORMATION_SCHEMA.TABLES;
```

​		根据数据库名表名统计

```sql
SELECT * FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = '数据库名' and TABLE_NAME = '表名';
```

​		字段介绍如下：

```java
TABLE_CATALOG																	// 数据库表目录
TABLE_SCHEMA																	// 所属数据库
TABLE_NAME																		// 表名称
TABLE_TYPE																		// 表类型，VIEW，BASE TABLE，SYSTEM VIEW，表示视图以及表
ENGINE																				// 使用的数据库引擎
VERSION																				// 版本
ROW_FORMAT																		// 行记录格式，跟使用的数据库引擎有关系
TABLE_ROWS																		// 表行数，数据库表中的数据行数也就是条数
AVG_ROW_LENGTH																// 平均每行的长度
DATA_LENGTH																		// 数据长度
MAX_DATA_LENGTH																// 最大数据长度
INDEX_LENGTH																	// 索引长度
DATA_FREE																			// 表碎片
AUTO_INCREMENT																// 数据主键自增记录值
CREATE_TIME																		// 表创建时间
UPDATE_TIME																		// 修改时间
CHECK_TIME																		// 检查时间
TABLE_COLLATION																// 表排序规则
CHECKSUM																			// 数据检查，用于校验主从或者备份时候数据是否一致
CREATE_OPTIONS																// 在CREATE TABLE语句中包括所有MySQL特性选项
TABLE_COMMENT																	// 表注释
```

### 查询表所有字段并拼接

​		查询case_quality表所有字段，并且使用逗号拼接

```sql
SELECT
	GROUP_CONCAT( tmpTab.COLUMN_NAME ) 
FROM
	( SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = 'case_quality' ) AS tmpTab;
```



## 数据库字段

​		如何查询数据库字段？

```sql
select * from information_schema.columns WHERE TABLE_SCHEMA="数据库名" and TABLE_NAME="表名称";
```

​		我们查询information_schema.columns即可，根据数据库名，以及表名查询字段

```java
TABLE_CATALOG																	// 数据库表目录
TABLE_SCHEMA																	// 所属数据库
TABLE_NAME																		// 表名称
COLUMN_NAME																		// 数据库字段名称
ORDINAL_POSITION															// 数据库字段序位，排序位置
COLUMN_DEFAULT																// 字段默认值
IS_NULLABLE																		// 是否允许为空
DATA_TYPE																			// 数据类型
CHARACTER_MAXIMUM_LENGTH											// 字符最大长度
CHARACTER_OCTET_LENGTH												// 字符八进制长度
NUMERIC_PRECISION															// 数值精度
NUMERIC_SCALE																	// 数值刻度表
DATETIME_PRECISION														// 日期时间精度
CHARACTER_SET_NAME														// 字符集编码名称
COLLATION_NAME																// 排序规则
COLUMN_TYPE																		// 字段类型
COLUMN_KEY																		// 字段Key，是否主键或者外键
EXTRA																					// 额外，自增等
PRIVILEGES																		// 权限
COLUMN_COMMENT																// 字段注释说明
GENERATION_EXPRESSION													// 建表信息
```

​		使用如下命令快速查询数据库表结构

```sql
SELECT
	column_name AS `列名`,
	data_type AS `数据类型`,
	column_comment AS `备注`,
	is_nullable AS `是否允许非空`,
CASE
		WHEN extra = 'auto_increment' THEN
		1 ELSE 0 
	END AS `是否自增`,
CASE
		WHEN column_key = 'PRI' THEN
		1 ELSE 0 
	END AS `是否主键`,
	character_maximum_length AS `字符长度`,
	numeric_precision AS `数字长度`,
	numeric_scale AS `小数位数`,
	column_default AS `默认值` 
FROM
	Information_schema.COLUMNS 
WHERE
	table_schema = '数据库名' 
	AND table_name = '表名';
```



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

​					

#### 		全连接

#### 		 全非连接（无关数据）

# 修改插入

## 字符串追加

将t_point_info表中的mine_name追加111在前面

```sql
UPDATE t_point_info SET mine_name=CONCAT('111', mine_name)
```

## 合并后插入清洗

​		将两张表数据合并到一张,将一张表数据迁移到另一张零时表中

```sql
INSERT INTO t_case_agg_tmp (caseClosedTime,totalPayAmount,accidentTime,productType,caseNo,typeId) SELECT
  oc.`caseClosedTime`  as `caseClosedTime`,
  oc.`totalPayAmount` AS `totalPayAmount`,
  oc.`accidentTime` as accidentTime,
  oc.`productType` as `productType`,
  oc.`caseNo` as caseNo,
1 as typeId
FROM
  `case_info_data_copy` as oc WHERE `caseClosedTime` != '没有信息' and `caseClosedTime`  != '';
```

​		合并两张表并且按id顺序进行过滤保留最早的一个ID

```sql
# 清洗重复数据，以美团案件号为维度
DELETE FROM
	 `t_case_agg_tmp`
WHERE
	id NOT in (
  SELECT
  t.minid 
  FROM
  ( SELECT `caseNo`, MIN(id)  AS minid FROM t_case_agg_tmp  GROUP BY `caseNo` ) t 
  );

```

## 按季度统计分析动态生成表

​		按保险信息统计季度趋势生成到新的表中，往前推11个季度按季度统计

```sql
set @aggCaseStartDate=STR_TO_DATE(CONCAT(YEAR(NOW()), '-', 
    CASE 
      WHEN MONTH(NOW()) BETWEEN 1 AND 3 THEN '01-01'
      WHEN MONTH(NOW()) BETWEEN 4 AND 6 THEN '04-01'
      WHEN MONTH(NOW()) BETWEEN 7 AND 9 THEN '07-01'
      WHEN MONTH(NOW()) BETWEEN 10 AND 12 THEN '10-01'
    END), '%Y-%m-%d');


set @aggCaseEndDate= DATE_ADD(@aggCaseStartDate,interval 3 month);


DROP TABLE IF EXISTS agg_case_quarter_tmp;

CREATE TABLE IF NOT EXISTS agg_case_quarter_tmp (
    出险季度 TEXT,
    aggCaseQuarter11 TEXT,
    aggCaseQuarter10 TEXT,
    aggCaseQuarter9 TEXT,
    aggCaseQuarter8 TEXT,
    aggCaseQuarter7 TEXT,
    aggCaseQuarter6 TEXT,
    aggCaseQuarter5 TEXT,
    aggCaseQuarter4 TEXT,
    aggCaseQuarter3 TEXT,
    aggCaseQuarter2 TEXT,
    aggCaseQuarter1 TEXT
);
INSERT INTO agg_case_quarter_tmp
SELECT 
    '出险季度' as 出险季度,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 31 month),'%Y年%m月') as aggCaseQuarter11,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 28 month),'%Y年%m月') as aggCaseQuarter10,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 25 month),'%Y年%m月') as aggCaseQuarter9,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 22 month),'%Y年%m月') as aggCaseQuarter8,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 19 month),'%Y年%m月') as aggCaseQuarter7,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 16 month),'%Y年%m月') as aggCaseQuarter6,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 13 month),'%Y年%m月') as aggCaseQuarter5,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 10 month),'%Y年%m月') as aggCaseQuarter4,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 7 month),'%Y年%m月') as aggCaseQuarter3,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 4 month),'%Y年%m月') as aggCaseQuarter2,
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 1 month),'%Y年%m月') as aggCaseQuarter1

union all

SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 1 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 0 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 0 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all


SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 4 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 3 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 3 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime
    

union all


SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 7 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 6 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 6 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all


SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 10 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 9 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 9 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all


SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 13 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 12 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 12 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all


SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 16 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 15 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 15 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all


SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 19 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 18 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 18 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all

SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 22 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 21 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 21 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all

SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 25 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 24 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 24 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all

SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 28 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 27 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 27 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime

union all

SELECT
    DATE_FORMAT(DATE_SUB(@aggCaseEndDate,interval 31 month),'%Y年%m月') as 出险季度,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 30 month),`totalPayAmount`,0)),2) as aggCaseQuarter11,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 27 month),`totalPayAmount`,0)),2) as aggCaseQuarter10,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 24 month),`totalPayAmount`,0)),2) as aggCaseQuarter9,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 21 month),`totalPayAmount`,0)),2) as aggCaseQuarter8,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 18 month),`totalPayAmount`,0)),2) as aggCaseQuarter7,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 15 month),`totalPayAmount`,0)),2) as aggCaseQuarter6,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 12 month),`totalPayAmount`,0)),2) as aggCaseQuarter5,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 9 month),`totalPayAmount`,0)),2) as aggCaseQuarter4,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 6 month),`totalPayAmount`,0)),2) as aggCaseQuarter3,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 3 month),`totalPayAmount`,0)),2) as aggCaseQuarter2,
    ROUND(SUM(if( `caseClosedTime` < DATE_SUB(@aggCaseEndDate,interval 0 month),`totalPayAmount`,0)),2) as aggCaseQuarter1
FROM
    `meituan_case_info`
WHERE
    accidentTime >=  DATE_SUB(@aggCaseStartDate,interval 30 month)
    and accidentTime < DATE_SUB(@aggCaseEndDate,interval 30 month)
    and `productType` LIKE "%雇主%"
    and accidentTime < caseClosedTime;
```



# 时间统计

## 按照天来查询统计

```mssql
SELECT DATE_FORMAT(create_time,'%Y-%m-%d'),count(*) FROM t_test_jpa GROUP BY  DATE_FORMAT(create_time,'%Y-%m-%d')
```

## 按照月份进行统计

```mssql
SELECT DATE_FORMAT(create_time,'%Y-%m'),count(*) FROM t_test_jpa GROUP BY  DATE_FORMAT(create_time,'%Y-%m')
```

## 按照年份进行统计

```mssql
SELECT DATE_FORMAT(create_time,'%Y'),count(*) FROM t_test_jpa GROUP BY  DATE_FORMAT(create_time,'%Y')
```

## 按照周进行统计

```mssql
SELECT DATE_FORMAT(create_time,'%Y-%u'),count(*) FROM t_test_jpa GROUP BY  DATE_FORMAT(create_time,'%Y-%u')
```

## 时间格式化参数

```
DATE_FORMAT(date,format) 
根据format字符串格式化date值。下列修饰符可以被用在format字符串中： 
%M 月名字(January……December) 
%W 星期名字(Sunday……Saturday) 
%D 有英语前缀的月份的日期(1st, 2nd, 3rd, 等等。） 
%Y 年, 数字, 4 位 
%y 年, 数字, 2 位 
%a 缩写的星期名字(Sun……Sat) 
%d 月份中的天数, 数字(00……31) 
%e 月份中的天数, 数字(0……31) 
%m 月, 数字(01……12) 
%c 月, 数字(1……12) 
%b 缩写的月份名字(Jan……Dec) 
%j 一年中的天数(001……366) 
%H 小时(00……23) 
%k 小时(0……23) 
%h 小时(01……12) 
%I 小时(01……12) 
%l 小时(1……12) 
%i 分钟, 数字(00……59) 
%r 时间,12 小时(hh:mm:ss [AP]M) 
%T 时间,24 小时(hh:mm:ss) 
%S 秒(00……59) 
%s 秒(00……59) 
%p AM或PM 
%w 一个星期中的天数(0=Sunday ……6=Saturday ） 
%U 星期(0……52), 这里星期天是星期的第一天 
%u 星期(0……52), 这里星期一是星期的第一天 
%% 一个文字“%”。
```



# MySQL系统函数大全

## 数值计算型



|         函数         |                             作用                             |
| :------------------: | :----------------------------------------------------------: |
|        ABS(X)        |                       返回X的绝对值。                        |
|       FLOOR(X)       |                   返回不大于X的最大整数。                    |
|    TRUNCATE(X,D)     |                   返回不小于X的最小整数。                    |
|    TRUNCATE(X,D)     |    返回数值X保留到小数点后D位的值，截断时不进行四舍五入。    |
|       ROUND(X)       |          返回离X最近的整数，截断时要进行四舍五入。           |
|      ROUND(X,D)      |         保留X小数点后D位的值，截断时要进行四舍五入。         |
|        RAND()        |                      返回0~1的随机数。                       |
|       SIGN(X)        |           返回X的符号(负数，零或正)对应-1，0或1。            |
|         PI()         |          返回圆周率的值。默认的显示小数位数是7位。           |
| POW(x,y)、POWER(x,y) |                     返回x的y次乘方的值。                     |
|       SQRT(x)        |                  返回非负数的x的二次方根。                   |
|        EXP(x)        |                     返回e的x乘方后的值。                     |
|       MOD(N,M)       |                    返回N除以M以后的余数。                    |
|        LOG(x)        |            返回x的自然对数，x相对于基数2的对数。             |
|       LOG10(x)       |                   返回x的基数为10的对数。                    |
|      RADIANS(x)      |                 返回x由角度转化为弧度的值。                  |
|      DEGREES(x)      |                 返回x由弧度转化为角度的值。                  |
|   SIN(x)、ASIN(x)    | 前者返回x的正弦，其中x为给定的弧度值；后者返回x的反正弦值，x为正弦。 |
|   COS(x)、ACOS(x)    | 前者返回x的余弦，其中x为给定的弧度值；后者返回x的反余弦值，x为余弦。 |
|   TAN(x)、ATAN(x)    | 前者返回x的正切，其中x为给定的弧度值；后者返回x的反正切值，x为正切。 |
|        COT(x)        |                   返回给定弧度值x的余切。                    |

## 字符型

|                           函数                           |                             作用                             |
| :------------------------------------------------------: | :----------------------------------------------------------: |
|                     CHAR_LENGTH(str)                     |                     计算字符串字符个数。                     |
|                       LENGTH(str)                        |            返回值为字符串str的长度，单位为字节。             |
|                    CONCAT(s1,s2，...)                    | 返回连接参数产生的字符串，一个或多个待拼接的内容，任意一个为NULL则返回值为NULL。 |
|                  CONCAT_WS(x,s1,s2,...)                  |   返回多个字符串拼接之后的字符串，每个字符串之间有一个x。    |
|                   INSERT(s1,x,len,s2)                    | 返回字符串s1，其子字符串起始于位置x，被字符串s2取代len个字符。 |
|                  LOWER(str)、LCASE(str)                  |                将str中的字母全部转换成小写。                 |
|                  UPPER(str)、UCASE(str)                  |               将字符串中的字母全部转换成大写。               |
|                  LEFT(s,n)、RIGHT(s,n)                   | 前者返回字符串s从最左边开始的n个字符，后者返回字符串s从最右边开始的n个字符。 |
|             LPAD(s1,len,s2)、RPAD(s1,len,s2)             | 前者返回s1，其左边由字符串s2填补到len字符长度，假如s1的长度大于len，则返回值被缩短至len字符；前者返回s1，其右边由字符串s2填补到len字符长度，假如s1的长度大于len，则返回值被缩短至len字符。 |
|                    LTRIM(s)、RTRIM(s)                    | 前者返回字符串s，其左边所有空格被删除；后者返回字符串s，其右边所有空格被删除。 |
|                         TRIM(s)                          |           返回字符串s删除了两边空格之后的字符串。            |
|                     TRIM(s1 FROM s)                      | 删除字符串s两端所有子字符串s1，未指定s1的情况下则默认删除空格。 |
|                       REPEAT(s,n)                        |   返回一个由重复字符串s组成的字符串，字符串s的数目等于n。    |
|                         SPACE(n)                         |               返回一个由n个空格组成的字符串。                |
|                     REPLACE(s,s1,s2)                     |   返回一个字符串，用字符串s2替代字符串s中所有的字符串s1。    |
|                      STRCMP(s1,s2)                       | 若s1和s2中所有的字符串都相同，则返回0；根据当前分类次序，第一个参数小于第二个则返回-1，其他情况返回1。 |
|             SUBSTRING(s,n,len)、MID(s,n,len)             | 两个函数作用相同，从字符串s中返回一个第n个字符开始、长度为len的字符串。 |
| LOCATE(str1,str)、POSITION(str1 IN str)、INSTR(str,str1) | 三个函数作用相同，返回子字符串str1在字符串str中的开始位置（从第几个字符开始）。 |
|                        REVERSE(s)                        |                       将字符串s反转。                        |
|              ELT(N,str1,str2,str3,str4,...)              |                      返回第N个字符串。                       |
|                    FIELD(s,s1,s2,...)                    |           返回第一个与字符串s匹配的字符串的位置。            |
|                    FIND_IN_SET(s1,s2)                    |           返回在字符串s2中与s1匹配的字符串的位置。           |
|                  MAKE_SET(x,s1,s2,...)                   |          按x的二进制数从s1，s2...，sn中选取字符串。          |

## 分支型

## 时间日期型

```
CURDATE()、CURRENT_DATE()			返回当前日期，格式：yyyy-MM-dd。
CURTIME()、CURRENT_TIME()			返回当前时间，格式：HH:mm:ss。
NOW()、CURRENT_TIMESTAMP()、
LOCALTIME()、SYSDATE()、
LOCALTIMESTAMP()							返回当前日期和时间，格式：yyyy-MM-dd HH:mm:ss。
UNIX_TIMESTAMP()							返回一个格林尼治标准时间1970-01-01 00:00:00到现在的秒数。
UNIX_TIMESTAMP(date)					返回一个格林尼治标准时间1970-01-01 00:00:00到指定时间的秒数。
FROM_UNIXTIME(date)						和UNIX_TIMESTAMP互为反函数，把UNIX时间戳转换为普通格式的时间。
UTC_DATE()										返回当前UTC（世界标准时间）日期值，其格式为"YYYY-MM-DD"或"YYYYMMDD"。
UTC_TIME()										返回当前UTC时间值，其格式为"YYYY-MM-DD"或"YYYYMMDD"。
															具体使用哪种取决于函数用在字符串还是数字语境中
MONTH(d)											返回日期d中的月份值，范围是1~12。
MONTHNAME(d)									返回日期d中的月份名称，如：January、February等。
DAYNAME(d)										返回日期d是星期几，如：Monday、Tuesday等。
DAYOFWEEK(d)									返回日期d是星期几，如：1表示星期日，2表示星期一等。
WEEKDAY(d)										返回日期d是星期几，如：0表示星期一，1表示星期二等。
WEEK(d)												计算日期d是本年的第几个星期，范围是0~53。
WEEKOFYEAR(d)									计算日期d是本年的第几个星期，范围是1~53。
DAYOFYEAR(d)									计算日期d是本年的第几天。
DAYOFMONTH(d)									计算日期d是本月的第几天。
YEAR(d)												返回日期d中的年份值。
QUARTER(d)										返回日期d是第几季度，范围是1~4。
HOUR(t)												返回时间t中的小时值。
MINUTE(t)											返回时间t中的分钟值。
SECOND(t)											返回时间t中的秒钟值。
EXTRACT(type FROM date)				从日期中提取一部分，type可以是YEAR、YEAR_MONTH、DAY_HOUR、DAY_MICROSECOND、
DAY_MINUTE、DAY_SECOND
TIME_TO_SEC(t)							将时间t转换为秒。
SEC_TO_TIME(s)							将以秒为单位的时间s转换为时分秒的格式。
TO_DAYS(d)									计算日期d至0000年1月1日的天数。
FROM_DAYS(n)								计算从0000年1月1日开始n天后的日期。
DATEDIFF(d1,d2)							计算日期d1与d2之间相隔的天数。
ADDDATE(d,n)								计算起始日期d加上n天的日期。
ADDDATE(d,type)							计算起始日期d加上一个时间段后的日期。
DATE_ADD(d,type)						同ADDDATE(d,INTERVAL expr type)
SUBDATE(d,n)								计算起始日期d减去n天的日期。
SUBDATE(d,type)							计算起始日期d减去一个时间段后的日期。
ADDTIME(t,n)								计算起始时间t加上n秒的时间。
SUBTIME(t,n)								计算起始时间t减去n秒的时间。
DATE_FORMAT(d,f)						按照表达式 f 的要求显示日期d。
TIME_FORMAT(t,f)						按照表达式 f 的要求显示时间t。
GET_FORMAT(type, s)					根据字符串s获取type类型数据的显示格式。
```



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

































