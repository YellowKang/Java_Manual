# 修改

## 字符串追加

将t_point_info表中的mine_name追加111在前面

```
UPDATE t_point_info SET mine_name=CONCAT('111', mine_name)
```



# 时间统计

## 按照天来查询统计

```
SELECT DATE_FORMAT(create_time,'%Y-%m-%d'),count(*) FROM t_test_jpa GROUP BY  DATE_FORMAT(create_time,'%Y-%m-%d')
```

## 按照月份进行统计

```
SELECT DATE_FORMAT(create_time,'%Y-%m'),count(*) FROM t_test_jpa GROUP BY  DATE_FORMAT(create_time,'%Y-%m')
```

## 按照年份进行统计

```
SELECT DATE_FORMAT(create_time,'%Y'),count(*) FROM t_test_jpa GROUP BY  DATE_FORMAT(create_time,'%Y')
```

## 按照周进行统计

```
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

