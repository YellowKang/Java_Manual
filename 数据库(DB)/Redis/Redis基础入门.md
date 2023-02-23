# Redis简介

​		什么是Redis？

​			Redis是一个NoSql（NoSQL = Not Only SQL）数据库（非关系型数据库），“不仅仅是SQL”，

​		NoSQL 不依赖业务逻辑方式存储，而以简单的key-value模式存储。因此大大的增加了数据库的扩展能力

​			没有ACID的特性，不支持事务，拥有远超于SQL的性能，

​		Redis的使用场景

​				对数据高并发的读写，海量数据的读写，还有数据的高拓展性

​		Redis的不适用场景

​				需要事务支持， 基于sql的结构化查询存储，处理复杂的关系,需要即席查询

​		Redis的重点核心

​				以Key-Value的形式存储，并且有很高的性能，但是不适合用来存储复杂的数据里，也就是关系	 

​			复杂并且有限制，而且需要支持事物的数据

​				支持持久化数据，也就是将数据存储到硬盘之中，相对于 Memcached，来说如果他断电了数

​			据也就没有了，不能像Redis一样支持数据的持久化，在这一点上Redis做的比他好，并且它包含了

​			Memcached的几乎所有的功能并且比他更加强大，作为Java来说Redis是一个不错的缓存数据库，

​			这只是Redis的一部分功能，还有更多的强大的功能

# Redis客户端

​		使用redis-cli连接Redis

```sh
# 单机版
redis-cli -h 127.0.0.1 -p 6379 -a bigkang

# 集群版
redis-cli -h 127.0.0.1 -p 6379 -a bigkang -c

-h 		# host地址，IP或者主机名
-p 		# port端口号
-a 		# password密码
-c 		# 集群方式连接
```

# Redis的Key的常用五大数据结构有哪些？

​		常见的五大数据类型（并非所有）

​		五种数据类型：

​						字符串（String）

​						哈希（hash）

​						字符串列表（list）

​						字符串集合（set）

​						有序字符串集合（sorted set）

### Redis的基本操作

​		使用help查询有哪些命令

```sh
help @string			# 查询string类型命令
help @hash				# 查询hash命令
help @list				# 查询list命令
help @set					# 查询set命令
help @zset				# 查询zset命令
help @cluster			# 查询集群命令
help @generic			# 查询通用命令

......等等其他命令，可以使用Table键提示
```

​		示例所有Key名都为bigkang为示例

```sh
# 查询所有Key
keys *	

# 判断某个Key是否存在
exists <key>
exists bigkang

# 查询某个Key的类型
type <key>
type bigkang

# 删除某个Key，返回一条影响行数，1表示true，删除成功
del <key>
del bigkang

# 给Key设置超时时间，单位秒，返回一表示true，设置成功
expire <key> <seconds>
expire bigkang 30

# 查询Key过期时间，返回秒数正数为超时时间，，-1表示永不过期，-2表示已过期
ttl <key>
ttl bigkang

# 查看当前数据库的key的数量（单个库）
dbsize

# 清空当前库
Flushdb

# 清空所有库
Flushall
```

### 字符串（String）的操作

```sh
help @string			# 查询string类型命令
```

```sh
# 查询Key对应键值
get  <key>
get bigkang

# 设置值，添加键值对，给bigkang赋值为123，没有则创建bigkang
set <key> <value>
set bigkang 123

# 追加，给bigkang追加456
append <key> <value>
append bigkang 456

# 查询Key长度，这个key的长度也就是length
strlen <key>
strlen bigkang

# 设置值，如果不存在则设置一个值，如果存在则设置失败
setnx <key> <value>
setnx bigkang 123

# 给Key值增加1，类似于i++操作，如果是字符串将无返回，必须为数据,返回值为修改后的Value
incr <key>
incr bigkang

# 给Key值减少1，类似于i--操作，如果是字符串将无返回，必须为数据,返回值为修改后的Value
decr <key>
dect bigkang

# 指定Key添加或者减少一定的数量，给bigkang这个key的Value添加100，然后再减少100,返回值为修改后的Value
incrby / decrby <key> <步长>
incrby bigkang 100
decrby bigkang 100

# 批量设置键值对
mset <key1> <value1> <key2> <value2>
mset bigkang1 1 bigkang2 2 bigkang3 3

# 同时设置一个或多个 key-value 对,如果不存在则设置,如果其中一个条件不满足则都失败
msetnx <key1> <value1> <key2> <value2> 
msetnx bigkang4 1 bigkang5 2 bigkang6 3

# 截取范围，获得值的范围，类似java中的substring,起始下标为0，只返回到结尾，超出正常返回
getrange <key> <起始位置> <结束位置>
getrange bigkang 0 10

# 插入值，从指定位置插入字符串,返回字符串长度
setrange <key> <起始位置> <value>
setrange bigkang 0 big

# 设置键值的同时，设置过期时间，单位秒
setex <key> <过期时间> <value>

# 获取以前的值写入新的值
getset <key> <value>
getset bigkang 123			
```

### 哈希（Hash）的操作

```sh
help @hash			# 查询hash类型命令
```

```sh
# 设置Hash值,Hash类似以Java中的Map,Hash的Key存在返回0，不存在返回1，如果是String则报错，设置name 为 bigkang，age为 21
hset <key> <field> <value>
hset bigkang name bigkang age 21

# 从Hash中取出某个属性的Value值
hget <key> <field>
hget bigkang name

# 批量设置Hash的值
hmset <key> <field1> <value1> <field2> <value2>
hmset bigkang name bigkang1 age 2000

# 批量获取值
hmset <key> <field1> <field2> 
hmget bigkang name age

# 判断field是否存在，返回0 OR 1，对应True，False
hexists <key> <field>
hexists bigkang name

# 列出某个key的所有field
hkeys <key>
hkeys bigkang

# 列出某个key的所有Value
hvals <key>
hvals bigkang

# 为哈希表 key 中的域 field 的值加上增量 increment
hincrby <key> <field> <increment>
hincrby bigkang age 1

# 将哈希表 key 中的域 field 的值设置为 value ，不存在时进行设置，存在不设置
hsetnx <key> <field> <value>
hsetnx bigkang name bigkang
			
```

### 字符串链表（list）的操作

```sh
help @list			# 查询list类型命令
```

```sh
# 从左边/右边插入一个或多个值
lpush/rpush <key> <value1> <value2> <value3>
# 左边插入
lpush bigkang 1 2 3
# 右边插入
rpush bigkang 4 5 6

#  从左边/右边吐出一个值
lpop/rpop key
# 从左边吐出
lpop bigkang
# 从右边吐出
rpop bigkang

# 从一个列表右边吐出一个值，插到另一个列表左边
rpoplpush <key1> <key2>
rpoplpush bigkang bigkang1

# 按照索引下标获得元素(从左到右),索引从0开始
lindex <key> <index>
lindex bigkang 2

# 按照起始位置结束位置范围获得元素(从左到右),索引从0开始
lrange <key> <start> <stop>
lrang bigkang 0 3

# 获得列表长度
llen <key>
llen bigkang

# 根据Key找到某个值，并且在他前面/后面插入一个值
linsert <key> AFTER/BEFORE <value> <newvalue>
# 在bigkang这个key的值为1的前面插入0.9
linsert bigkang AFTER 1 0.9
# 在bigkang这个key的值为1的后面插入1.1
linsert bigkang BEFORE 1 1.1

# 从根据某个Key，找到Value,删除这个Value几个个数
lrem <key> <n> <value>
# 在bigkang中，从左边开始查询，删除1这个元素，删除两个（可能元素List中有多个1）
lrem bigkang 2 1
```

### 字符串集合（set）的操作

```sh
help @set			# 查询set类型命令
```

```sh
# 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略,插入成功返回1，插入失败表示0已经存在
sadd <key> <value1> <value2> .....	
sadd bigkang 1 2 3
# 取出该集合的所有值
smembers <key>
smembers bigkang

# 返回该集合的元素个数
scard <key>
scard bigkang

# 判断集合<key>是否为含有该<value>值，有返回1，没有返回0
sismember <key> <value>
sismember bigkang 1

# 删除集合中的某个元素
srem <key> <value1> <value2> ....	
srem bigkang 1 2 3

# 随机从该集合中吐出一个值,会将原来的值删除
spop <key>
spop bigkang

# 随机从该集合中取出n个值。 不会从集合中删除
srandmember <key> <n>
srandmember bigkang 3

# 返回两个集合的交集元素,如bigkang有1，bigkang2也有1，则返回1，所有相同的都会返回
sinter <key1> <key2>
sinter bigkang bigkang2

# 返回两个集合的并集元素，例如bigkang为 1 2 3，bigkang2 为 3 4 5 ，则返回1 2 3 4 5
sunion <key1> <key2>
sunion bigkang bigkang2

# 返回两个集合的差集元素，例如bigkang为 1 2 3，bigkang2 为 3 4 5 ，则返回1 2 4 5
sdiff <key1> <key2>
sdiff bigkang bigkang2
```

### 有序字符串集合（zset）的操作

```sh
help @sorted_set			# 查询zset类型命令
```

```sh
# 将一个或多个 member 元素及其 score 值加入到有序集 key 当中，zset会根据score排序
zadd <key> <score1> <value1> <score2> <value2>...	
zadd bigkang 1 A 2 B 3 C

# 返回有序集 key 中，下标在<start> <stop>之间的元素,带WITHSCORES，可以让分数一起和值返回到结果集,索引从0开始，0 1 会包含0 和 1
zrange <key> <start> <stop> [WITHSCORES]
# 带分数返回0 到 2 三个值，并且返回分数，从小到大
zrange bigkang 0 2 WITHSCORES
# 不返回分数
zrange bigkang 0 2

# 同上，顺序相反，从大到小
zrevrange bigkang 0 2
# -1表示返回所有
zrevrange bigkang 0 -1

# 返回有序集 key 中，分数在min到max中的值（包含），有序集成员按 score 值递增(从小到大)次序排列
zrangebyscore key min max [withscores][limit offset count]
zrangebyscore bigkang 1 2.5 WITHSCORES

# 同上，改为从大到小排列,max min相反
zrevrangebyscore key max min [withscores][limit offset count]
zrevrangebyscore bigkang 2.5 1 WITHSCORES

# 为元素的score加上增量
zincrby <key> <increment> <value>
zincrby bigkang 3 A

# 删除该集合下，指定值的元素
zrem <key> <value>
zrem bigkang A

# 统计该集合，分数区间内的元素个数
zcount <key> <min> <max>
zcount bigkang 1 20

# 返回该值在集合中的排名，从0开始,返回索引下标，从小到大
zrank <key> <value>
zrank bigkang B			
```

# Bit操作

## 设置（Bit）

​		首先我们需要了解什么是Bit，其实所有的Bit操作都是在操作字符串类型，我们设置了bit以后可以发现他的类型其实是一个String

```sh
# 设置Bit语法 setbit ${Key名称} ${Bit位} ${Bit位的值 只能是 0 或者 1}
setbit newbit 1 1
```

​		然后我们来获取这个bit的类型

```sh
# 获取Key的类型
type newbit

# 我们发现返回了
"string"
```

​		那么我们就会发现其实存储的数据是一个字符串，那么字符串和bit有什么关系呢，我们知道Redis中的字符串底层采用的SDS，实际上它存储的一个char数组，那么这个char数组，那么C语言中一个char等于1个byte，一个byte等于8个bit，我们可以知道一个char能够存储8个bit，那么Redis的String能够存储512MB，那么我们再来看一下最大能够存储多少个bit位:

​		最大存储数量

​			512 * 1024 * 1024 * 8     =    42 9496 7296  （大约43亿）

​			MB       KB      Byte    BIit

​		我们就能够存储大约43亿bit，每个bit的值只能是0 或者 1

​		我们上访的操作 setbit newbit 1 1 就是将bit位为1，也就是第二个bit设置为1，bit为数组

​		大概的流程图如下：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1609391798951.png)

​		这样我们就可以知道bit位在 0 - 7的属于第一个字节，8 - 15属于第二个字节，那么我们现在来测试一下吧

```sh
# 删除原来的Key
del bitstr
# 新增一个bit位0的值为1
setbit bitstr 0 1
# 查看长度由于存入了第一个char，那么长度为1
strlen bitstr

# 再设置bit位为7的数据
setbit bitstr 7 1
# 查看长度由于存入了第一个char，那么长度还是为1
strlen bitstr


# 我们这次设置的话我们设置一下8，那么这个时候长度就会变成2了
setbit bitstr 8 1
# 查看长度由于存入了第二个char，那么长度就会扩容为2了
strlen bitstr 
```

​		例如我们想要获取某个bit位的值，我们使用getbit

```sh
# 获取第一个bit位的数据
getbit bitstr 0
```

## 统计（BitCount）

​		BitCount可以统计我们的Bit数组中的值为1的数据，例如我想要统计bit位的值的结果有多少。（！注意是根据一个char，也就是一个byte=8bit进行统计，每个值表示相应的8个bit）

​		如下

```sh
# 统计整个字节数组数据Bit位为1的数量
bitcount bitstr
```

​		或者根据范围进行统计

```sh
# 注意此范围不是统计bit范围，而是统计char中的bit，一个char = 8bit
# 表示统计第一个char也就是 0 - 8  到  0 - 8，那么就是1 到 1，第一个char，对应bit位 0 - 7
# 表示统计char[0] - char[0],bit位 0 - 7
bitcount bitstr 0 0
# 返回结果为2

# 表示统计char[0] - char[1],bit位 0 - 15
bitcount bitstr 0 1
# 返回结果为3		
```

## 函数（BitOp）

​		Bit主要用来帮助我们对不同Bit进行操作，和Set中的并集，并归等类似。

​		现在我们来初始化两个bit数据

```sh
# 初始化第一个bit，设置 0 和 4
setbit bit1 0 1
setbit bit1 4 1

# 初始化第二个bit，设置 3 和 4
setbit bit2 3 1
setbit bit2 4 1
```

​		目前两个Bit中的结构如下

```sh
#Bit位		0		1		2		3		4		5		6		7
	bit1	 1	 0	 0	 0	 1	 0	 0	 0
	bit2   0 	 0	 0	 1	 1	 0	 0	 0
```

​		那么我们知道既然是二进制，那么肯定是有运算的，例如与，或等等

​		Redis提供了如下几种

- ​				AND

- ​				OR

- ​				NOT

- ​				XOR

  语法如下：

```sh
bitop ${操作} ${新的Key} 。。。。（其他bit位）

# 返回结果为新的Bit的字节位数量，例如bit 1 -20，那么对应 char[0] - char[2],返回的数据为字符串长度，也就是strlen
```

​		AND

```sh
# 使用AND，将bit1 bit2 进行AND，然后将结果返回到newbit
bitop and newbit bit1 bit2

# and的操作如下
#Bit位		0		1		2		3		4		5		6		7
	bit1	 1	 0	 0	 0	 1	 0	 0	 0
				
	bit2   0 	 0	 0	 1	 1	 0	 0	 0
				
	newbit 0 	 0	 0	 0	 1	 0	 0	 0
	# 与操作，必须1 与 1 = 1，否则都为0		
  # 返回结果只有bit位为4的为1，所以使用bit统计出来则为1
```

​		OR

```sh
# 使用OR，将bit1 bit2 进行OR，然后将结果返回到newbit
bitop or newbit bit1 bit2

# or的操作如下
#Bit位		0		1		2		3		4		5		6		7
	bit1	 1	 0	 0	 0	 1	 0	 0	 0
				
	bit2   0 	 0	 0	 1	 1	 0	 0	 0
				
	newbit 1 	 0	 0	 1	 1	 0	 0	 0
	# 或操作，可以 1 或 0 , 0 或 1，只要有一个1，则返回1	
  # 返回结果有bit位为0，3，4的为1，所以使用bit统计出来则为3
```

​		NOT

```sh
# 使用NOT，将bit1进行NOT操作，然后将结果返回到newbit
bitop not newbit bit1

# NOT的操作如下
#Bit位		0		1		2		3		4		5		6		7
	bit1	 1	 0	 0	 0	 1	 0	 0	 0
				
	newbit 0 	 1	 1	 1	 0	 1	 1	 1
	# 取反操作，0变成1，1变成0
  # 统计返回结果则为6
```

​		XOR

```sh
# 使用XOR，将bit1,bit2进行XOR操作，然后将结果返回到newbit
bitop xor newbit bit1 bit2

# NOT的操作如下
#Bit位		0		1		2		3		4		5		6		7
	bit1	 1	 0	 0	 0	 1	 0	 0	 0
				
	bit2   0 	 0	 0	 1	 1	 0	 0	 0
				
	newbit 1 	 0	 0	 1	 0	 0	 0	 0
	# XOR操作  必须包含 0 和 1
  # 统计返回结果则为6
```

​		Bit操作可以帮助我们存储大量的数据，以及状态，我们可以在多个场景下使用,例如用户的连续登录，以及活跃用户统计。

​		例如如下操作，我们的Key采用   login-年-月-日  ，bit位 使用用户ID，状态为1

```sh
# 我们设置用户登录，1号有4个用户登录
setbit login-2020-12-1 19 1
setbit login-2020-12-1 20 1
setbit login-2020-12-1 21 1
setbit login-2020-12-1 22 1

# 2号有两个用户登录
setbit login-2020-12-2 19 1
setbit login-2020-12-2 21 1


# 功能操作
# 统计出最近两天登录过的用户
bitop or login-2020-12{1-2} login-2020-12-1 login-2020-12-2

# 统计最近的连续登录两天的用户
bitop and login-2020-12{1-2} login-2020-12-1 login-2020-12-2
```



# Key的定义的注意点

​		1、不要过长	

​		2、不要过短

​		3、统一的命名规范



# Redis的持久化？

​		Redis的持久化方案有两种

​			RDB （Redis DataBase）

​			AOF （Append Of File）



## RDB持久化

​		RDB的存储方式：在指定的时间间隔内将内存中的数据集快照写入磁盘，也
​						就是行话讲的Snapshot快照，它恢复时是将快照文件直接
​						读到内存里



​		Redis会单独创建（fork）一个子进程来进行持久化，会先将数据写入
​		到一个临时文件中，待持久化过程都结束了，再用这个临时文件替换
​		上次持久化好的文件。整个过程中，主进程是不进行任何IO操作的，
​		这就确保了极高的性能如果需要进行大规模数据的恢复，且对于数据
​		恢复的完整性不是非常敏感，那RDB方式要比AOF方式更加的高效。
​		RDB的缺点是最后一次持久化后的数据可能丢失



​		rdb的保存的文件： 在redis.conf中配置文件名称，默认为dump.rdb

​		![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567760178559.png)



​		下面就是他的持久化的文件存储的路径



​		RDB持久化的保存策略

​		这是他的保存策略，如果60秒内发生了10000次数据操作则进行一次存储，

​		如果300秒发生了10次则会存入一次。如果900秒内发生了一次操作那么900秒后

​		就会备份一次，

​		![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567760194255.png)

​		stop-writes-on-bgsave-error yes
​		当Redis无法写入磁盘的话，直接关掉Redis的写操作

​		rdbcompression yes
​		进行rdb保存时，将文件压缩

​		rdbchecksum yes
​		在存储快照后，还可以让Redis使用CRC64算法来进行数
​		据校验，但是这样做会增加大约10%的性能消耗，如果希
​		望获取到最大的性能提升，可以关闭此功能



### 		Redis持久化--RDB

​				rdb的备份

​					先通过config get dir 查询rdb文件的目录
​					将*.rdb的文件拷贝到别的地方

​			 	rdb的恢复

​			 		先把备份的文件拷贝到工作目录下
​		 			关闭Redis
​		 			启动Redis, 备份数据会直接加载

​				注：清先看清楚配置文件的RDB的文件名和路径

### 		RDB特性

​				优点：

​					 节省磁盘空间
 					 恢复速度快

​				rdb的缺点

​					虽然Redis在fork时使用了写时拷贝技术,

​					但是如果数据庞大时还是比较消耗性能



​					在备份周期在一定间隔时间做一次备份，所以如果
​					Redis意外down掉的话，就会丢失最后一次快照后的所有修改					

​		

## AOF持久化

​			 AOF默认不开启，需要手动在配置文件中配置

​			 可以在redis.conf中配置文件名称，默认为 appendonly.aof

​			![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567760207646.png)

​		这分别是是否开启。默认是关闭的而RDB默认是开启的，他的路径和RDB是一样的

​		下面这个就是文件名了

​		

​		那么如果RDB好AOF同时启动的话他会执行哪个呢？

​			AOF的备份机制和性能虽然和RDB不同, 但是备份和
​			恢复的操作同RDB一样，都是拷贝备份文件，需要
​			恢复时再拷贝到Redis工作目录下，启动系统即加载

​			**AOF和RDB同时开启，系统默认取AOF的数据**



### 持久化AOF

​			 AOF文件的保存路径，同RDB的路径一致

​			 如遇到AOF文件损坏，可通过
​				redis-check-aof --fix appendonly.aof 进行恢复

# Redis进阶（5.0）

## Redis源码

​		地址如下，可以自定义修改版本

```http
https://github.com/redis/redis/blob/5.0/src/sds.h
```

## Redis数据DB

​		Redis是一个一个的DB，那么这个DB到底是一个什么样结构的数据呢？

​		如下是Redis官方的源码（5.0）

```c
/* Redis数据库表示。有多个数据库标识从0(默认数据库)到配置的最大值的整数数据库。数据库号是结构中的“id”字段*/
typedef struct redisDb {
    dict *dict;                 /* 这个数据库的键空间（字典类型） */
    dict *expires;              /* 设置超时的键的超时 */
    dict *blocking_keys;        /* 客户端等待数据的密钥(BLPOP) */
    dict *ready_keys;           /* 接收到推送的阻塞键 */
    dict *watched_keys;         /* EXEC CAS的监视键 */
    int id;                     /* 数据库ID */
    long long avg_ttl;          /* 平均TTL，仅用于统计 */
    list *defrag_later;         /* 要逐个进行磁盘整理的键名列表 */
} redisDb;
```

​		我们可以看到Redis的数据库主要的数据是存放在字典中的

## Redis数据Dict字典

​		官网源码地址：https://github.com/redis/redis/blob/5.0/src/dict.h

​		我们找到dict字典的定义：

```c
// 字典类型数据定义
typedef struct dict {
    dictType *type; /* 字典类型数组 */
    void *privdata; /* 私有数据 */
    dictht ht[2]; /* 字典Hash表数组 */
    long rehashidx; /* 如果 rehashidx == -1，表示没有进行Rehash,如果为正数表示有ReHash操作 */
    unsigned long iterators; /* 当前正在运行的迭代器数 */
} dict;
```

​		主要的数据是存放在我们的字典Hash表数组中的我们在来看一下这个dictht，字典Hash表

```c
// 字典Hash表类型数据定义
typedef struct dictht {
    dictEntry **table; /* Hash表，存放一个又一个的字典元素,实际上是一个数组 */
    unsigned long size; /* 哈希表大小，即哈希表数组大小 */
    unsigned long sizemask; /* 哈希表大小掩码，总是等于size-1，主要用于计算索引 */
    unsigned long used; /* 已使用节点数，即已使用键值对数 */
} dictht;
```

​		那么更加主要的就是我们的每一个字典的元素，表示我们存放的元素数据

```c
// 字典元素类型数据定义
typedef struct dictEntry {
  	// 无类型指针，Key指向Val值
    void *key;
    // 值，是一个公用体,他有可能是一个指针，或者一个64位正整数，或者64位int，浮点数
    union {
       	// 值指针
        void *val;
      	// 64位正整数
        uint64_t u64;
      	// 64位int
        int64_t s64;
      	// 浮点数
        double d;
    } v;
  	// next节点，每一个dictEntry都是一个链表，用于处理Hash冲突
    struct dictEntry *next;
} dictEntry;
```

### Redis字典扩容以及Hash冲突处理（ReHash）

​		我们知道上方的dict，有两个Hash表 ，那么为什么我们要放两个Hash表呢？

​		答案就是我们Redis的Hash表在进行扩容的时候需要用到的，那么下面我们来看一下源码中的解释吧。

​		int dictRehash(dict *d, int n);

​		源码位置：https://github.com/redis/redis/blob/5.0/src/dict.c

​		首先我们肯定需要知道我们是在哪一步进行扩容的，肯定是在我们发生Add操作的时候我们定位到Add的方法：

```c
/* 添加一个元素到目标哈希表 */
int dictAdd(dict *d, void *key, void *val)
{
  	// 向字典中添加key
    dictEntry *entry = dictAddRaw(d,key,NULL);

    if (!entry) return DICT_ERR;
  	// 然后设置节点的值
    dictSetVal(d, entry, val);
    return DICT_OK;
}
```

​		然后我们定位到dictAddRaw，这一步使用链表解决Hash冲突

```c
/* 低级添加或查找:
 * 此函数添加了元素，但不是设置值而是将dictEntry结构返回给用户，这将确保按需填写值字段.
 *
 * 此函数也直接公开给要调用的用户API主要是为了在哈希值内部存储非指针，例如:
 * entry = dictAddRaw(dict,mykey,NULL);
 * if (entry != NULL) dictSetSignedIntegerVal(entry,1000);
 * 
 * 返回值:
 *
 * 如果键已经存在，则返回NULL，如果不存在，则使用现有条目填充“ * existing”.
 * 如果添加了键，则哈希条目将返回以由调用方进行操作。
 */
dictEntry *dictAddRaw(dict *d, void *key, dictEntry **existing)
{
    long index;
    dictEntry *entry;
    dictht *ht;
		// 判断是否正在ReHash，如果需要则调用_dictRehashStep（后续ReHash中的步骤），每次ReHash一条数据，直到完成整个ReHash
    if (dictIsRehashing(d)) _dictRehashStep(d);

    /* 获取新元素的索引,根据Key计算索引，并且判断是否需要进行扩容ReHash(！！！重点)（第一次ReHash调用） */
    if ((index = _dictKeyIndex(d, key, dictHashKey(d,key), existing)) == -1)
        return NULL;

  	/* 解决Hash冲突，以及ReHash时效率问题 */
    /* 分配内存并存储新条目。假设在数据库系统中更有可能更频繁地访问最近添加的条目，则将元素插入顶部 */
  	// 判断是否需要ReHash，如果是那么当前的HashTable为字典下的第二个，如果不需要扩容则使用原来的的
    ht = dictIsRehashing(d) ? &d->ht[1] : &d->ht[0];
    // 创建元素,分配内存
    entry = zmalloc(sizeof(*entry));
  	// 进行元素链表操作，元素的下一个节点指向Hash表中的相应索引，如果以前这个下标有元素则链到当前元素后面
    entry->next = ht->table[index];
  	// Hash表节点索引设置为自己，替换原来的元素
    ht->table[index] = entry;
    ht->used++;

    /* 设置这个Hash元素的Key. */
    dictSetKey(d, entry, key);
    return entry;
}
```

​		看完了Hash冲突的解决方式我们再来看一下扩容，首先我们看一下dictIsRehashing，是如何判断需要进行ReHash的

```c
 // 如果字典的rehashidx不是-1，那就表示需要进行Hash扩容
 dictIsRehashing(d) ((d)->rehashidx != -1)
```

​		那么在什么地方修改了rehashidx呢，就是在我们计算Index的时候

```c
/* 返回可用插槽填充的索引,根据“Key”的哈希计算，如果Key已经存在，则返回-1
 * 请注意，如果我们正在重新哈希表，索引总是在第二个（新）哈希表的上下文中返回，也就是ht[1] */
static long _dictKeyIndex(dict *d, const void *key, uint64_t hash, dictEntry **existing)
{
    unsigned long idx, table;
    dictEntry *he;
    if (existing) *existing = NULL;

    /* 如果需要，扩容哈希表，如果失败返回-1（ReHash扩容机制） */
    if (_dictExpandIfNeeded(d) == DICT_ERR)
        return -1;
  	/* 从两个Hash表进行查询，可能这个Key放入了第二个哈希表 */
    for (table = 0; table <= 1; table++) {
  			/* 根据数组长度 - 1 然后取模计算卡槽 */
        idx = hash & d->ht[table].sizemask;
        /* 根据Hash表获取元素，并且判断这个Key有没有在Hash表里面，如果存在返回-1 */
        he = d->ht[table].table[idx];
        while(he) {
            if (key==he->key || dictCompareKeys(d, key, he->key)) {
                if (existing) *existing = he;
                return -1;
            }
            he = he->next;
        }
        // 如果不在ReHash，直接返回第一个Hash表的index卡槽，如果是ReHash那么就把idx放入第二个Hash表
        if (!dictIsRehashing(d)) break;
    }
    return idx;
}
```

​		此处开始判断是否需要进行扩容

```c
/* 如果需要，扩容Hash表 */
static int _dictExpandIfNeeded(dict *d)
{
    /* 如果已经在ReHash中了，直接返回 */
    if (dictIsRehashing(d)) return DICT_OK;

    /* 如果哈希表为空，将其展开到初始大小。初始大小4 */
    if (d->ht[0].size == 0) return dictExpand(d, DICT_HT_INITIAL_SIZE);

    /* 如果我们的已经使用的元素个数和Hash表数组长度达到 1：1 比率，那么就要进行扩容了 （全局设置）或者我们应该避免它， 但之间的比率元素/存储桶超过"安全"阈值，我们调整大小加倍存储桶的数量 */
  	/* 简单来说就是我们使用的元素等于数组的长度那么我们就扩容Hash表，容量扩容一倍 */
    if (d->ht[0].used >= d->ht[0].size &&
        (dict_can_resize ||
         d->ht[0].used/d->ht[0].size > dict_force_resize_ratio))
    {
        return dictExpand(d, d->ht[0].used*2);
    }
    return DICT_OK;
}
```

​		将第二张Hash表重新初始化，后续ReHash中的元素都会放入第二张Hash表

```c
/* 扩容或者创建Hash表 */
int dictExpand(dict *d, unsigned long size)
{
    /* 如果正在ReHash，或者使用数量大于原size * 2，返回-1 */
    if (dictIsRehashing(d) || d->ht[0].used > size)
        return DICT_ERR;

    dictht n; /* 新的哈希表 */
    unsigned long realsize = _dictNextPower(size);

    /* 重新大小重为相同的表大小没有用处，返回-1 */
    if (realsize == d->ht[0].size) return DICT_ERR;

    /* 分配新的哈希表并初始化所有指向 NULL 的指针 */
    n.size = realsize;
    n.sizemask = realsize-1;
  	/* 分配内存扩容空间 */
    n.table = zcalloc(realsize*sizeof(dictEntry*));
    n.used = 0;

    /* 这是第一次初始化吗？如果是这样，它不是真正的重述，我们只是设置第一个哈希表，以便它可以接受键。 */
    if (d->ht[0].table == NULL) {
        d->ht[0] = n;
        return DICT_OK;
    }

    /* 准备第二个哈希表以进行增量重哈希，将第二个临时存放的Hash表重新初始化，开始ReHash操作 */
    d->ht[1] = n;
    d->rehashidx = 0;
    return DICT_OK;
}
```

### ReHash过程

​		ReHash过程是指我们将状态设置为了ReHash，并且将新增的元素写入到了第二张Hash表，这个时候我们就需要将第二张Hash表和第一张Hash表

```c
/* 字典ReHash操作，每次第一个参数表示字典，第二个参数表示每次ReHash的数量，例如100扩容至两百，如果没有Hash冲突，我们需要传入100才能完成ReHash */
int dictRehash(dict *d, int n) {
    int empty_visits = n*10; /* 可访问的最大空桶数 */
  	/* 不在ReHash过程直接返回 */
    if (!dictIsRehashing(d)) return 0;
		/* ReHash 第二张表时会先 */
    while(n-- && d->ht[0].used != 0) {
        dictEntry *de, *nextde;

        /* 请注意，rehashidx不会溢出，因为我们确信还有更多元素，因为ht [0] .used！= 0*/
        assert(d->ht[0].size > (unsigned long)d->rehashidx);
      	// 如果卡槽是空的那么从ReHashIndex开始自增，因为需要遍历，rehashidx从开始被默认置为0，如果需要将原来的Hash表完成ReHash，就需要从0遍历完整张Hash表
        while(d->ht[0].table[d->rehashidx] == NULL) {
            d->rehashidx++;
          	// 如果查找了n * 10个卡槽还是为空的话那么我们返回1，不执行操作
            if (--empty_visits == 0) return 1;
        }
      	// 获取原来的ht[0]的相应卡槽的Hash表
        de = d->ht[0].table[d->rehashidx];
        /* 然后将卡槽中的Key Value 都放入 ht[1] 表示将数据从ht[0] 移动到 ht[1]*/
        while(de) {
            uint64_t h;

            nextde = de->next;
            /* 获取新哈希表中的索引 */
            h = dictHashKey(d, de->key) & d->ht[1].sizemask;
            de->next = d->ht[1].table[h];
            d->ht[1].table[h] = de;
            d->ht[0].used--;
            d->ht[1].used++;
            de = nextde;
        }
      	// 如果为空那么继续+1，知道ht[0]的表变成空的
        d->ht[0].table[d->rehashidx] = NULL;
        d->rehashidx++;
    }

  	// 完成ReHash，表示将ht[0]所有数据已经移动到ht[1]，然后将ht[1] 赋值给ht[0]，然后清空ht[1]，一次ReHash操作完成
    /* 检查我们是否已经重新ReHash了第一张Hash表.. */
    if (d->ht[0].used == 0) {
        zfree(d->ht[0].table);
        d->ht[0] = d->ht[1];
        _dictReset(&d->ht[1]);
        d->rehashidx = -1;
        return 0;
    }

    /* 返回数据，这一步通常由于ReHash没有执行完，只ReHash了一部分（未完成ReHash） */
  	// 返回1表示给定时任务循环调度，while条件，表示没有ReHash完成
    return 1;
}
```

​		并且有任务调度ReHash

​		在Server中https://github.com/redis/redis/blob/5.0/src/server.c

```c
/* 数据库定时任务 */
void databasesCron(void) {
/* Rehash */
        if (server.activerehashing) {
            for (j = 0; j < dbs_per_call; j++) {
              	// 数据库ReHash
                int work_done = incrementallyRehash(rehash_db);
                if (work_done) {
                    break;
                } else {
                    /* If this db didn't need rehash, we'll try the next one. */
                    rehash_db++;
                    rehash_db %= server.dbnum;
                }
            }
        }


// 每个数据库每次执行1毫秒的ReHash
int incrementallyRehash(int dbid) {
    /* Keys dictionary */
    if (dictIsRehashing(server.db[dbid].dict)) {
        dictRehashMilliseconds(server.db[dbid].dict,1);
        return 1; /* 已经使用了毫秒作为循环周期。... */
    }
    /* Expires */
    if (dictIsRehashing(server.db[dbid].expires)) {
        dictRehashMilliseconds(server.db[dbid].expires,1);
        return 1; /* 已经使用了毫秒作为循环周期。... */
    }
    return 0;
}
  
/* Rehash在ms+"delta"毫秒。delta值较大,小于0，大多数情况下小于1。精确上界取决于dictRehash(d,100)的运行时间 */
int dictRehashMilliseconds(dict *d, int ms) {
    if (d->iterators > 0) return 0;
		// 记录开始同步
    long long start = timeInMilliseconds();
  	// 记录ReHash的数量
    int rehashes = 0;
		// 每次ReHash100条数据
    while(dictRehash(d,100)) {
        rehashes += 100;
      	// 如果执行到指定时间  例如 一毫秒，当前时间 - 开始时间 > 1毫秒，则直接Break
        if (timeInMilliseconds()-start > ms) break;
    }
    return rehashes;
}
```

## SDS动态字符串

​	动态字符串（simple dynamic string）

​		首先我们需要了解什么是sds动态字符串

​		我们知道Redis是采用C语言进行编写的，而所有的Key键都是字符串String类型，以及我们的很多的Value也会存储字符串，那么我们就要首先了解C语言的字符串了。

​		C语言中是没有String这个字符串类型的，而是采用的一个char数组，然后以\0作为一个结束符

```java
		// C语言中的字符串
		char *str;
    str = "redis";
		printf("%s",str);

		// 但是实际上这个str在转成String字符串的时候底层的char数组被转了,后面会多出一个/0的字符串结束符
		char str[5] = {'r','e','d','i','s','\0'};
```

​		那么我们在获取字符串的长度的时候，我们就会发现一个问题，我们需要遍历这个char数组，获取长度的时间复杂度是O(N)。

​		并且我们还会发现一个问题，我们存储二进制的时候，如果说二进制流中出现\0的时候，就会出现问题。

​		使用C字符串数组有以下问题

```properties
			1: 字符串数组的长度都是固定的，并且我们追加或者修改字符串数组相当于都是在重新创建内存空间，损耗内存
			2: 获取字符串长度时需要遍历字符串数组，时间复杂度较高，大量查询长度，会引起性能问题
			3: 存储二进制数据时，例如文件等等我们使用\0判断是否结尾，会导致二进制数据存储、查询长度、获取数据时引发的一系列问题
```

​		总体上来说则使用C语言转换后的String并不适合Redis用来存储，那么针对字符串的Key我们怎么去解决呢？

​		答案就是：动态字符串（simple dynamic string）SDS

​				那么SDS能帮助我们解决什么问题呢？，如下 : 

```properties
			1: SDS在字符串发生扩容的时候直接使用空闲的空间进行扩容，不需要重新分配数组对象，从而解决扩容问题
			2: 在SDS的内部定义了字符串的长度，使用时可以直接获取,将时间复杂度从O(n)变成了O(1)提高了长度查询效率
			3: SDS的空间预分配是惰性释放内存的，从而减少分配内存的次数
			4: SDS中存储了字符串的长度信息，我们可以直接根据起始位置，找到长度，获取数据，从而避免了二进制所导致问题
```

​		下面是SDS所存储的数据（老版本SDS >= 3.0）：

```c
struct sdshr{
  int len;  // 用于记录已使用数组长度，存储的字符串数据在buffer数组中的长度
  int free; // 用于记录数组剩余空间，用于追加时扩容是否需要扩容buffer
  char buf[];// 用于创建内存空间，以及存储的数据字符串buff数组
}
```

​		但其实这个buffer数组也是采用的\0进行存储的，那么为什么还要加上这个\0呢，答案就是为了兼容某些C的类库，所以还是需要\0进行结尾。

​		SDS空间分配策略：

- ​		**预留空间**

```java
		// 预留空间，是如何预留的呢？
					我们举例示范，例如 我们新建了一个字符串"redis",
          
					现在Buffer的长度是20
          char buf[20] = {'r','e','d','i','s','\0',.....空};

          那么此时的SDS如下
					struct sdshr{
            int len = 5;
            int free = 14;
            char buf[] = {'r','e','d','i','s','\0',.....空};
          }

					我们现在需要给他追加5.0.3这个字符串
          append("5.0.3");
					如果是采用来的字符串数组那么,则是
          char str[] = {'r','e','d','i','s','\0'};
					我们还需要将两个字符串的长度进行计算，然后创建一个新的字符串数组，再把值给添加进去
					而使用SDS我们就可以直接根据len找到数组的位置然后进行插入，也不需要创建新的数组对象
            															len
          																 |
            															 v
          char buf[] = {'r','e','d','i','s','\0',.....空};
```

- ​		**惰性空间释放**

```java
		// 惰性空间释放，是如何惰性空间释放的呢？
					还是以上面的示例
          
          我们将redis修改为key
          那么这个时候
          struct sdshr{
            int len = 3;
            int free = 16;
            char buf[] = {'k','e','y','\0',.....空};
          }

					我们可以看到buffer数组的长度还是没有变，我们下一次再插入一个redis5.0.3的时候是不会再创建内存空间的。
          这个时候我们数组长度还是20，那么再次修改的话我们的buffer数组不需要重新创建内存空间了。
          		缺点：
            			如果字符串占用较小的话只会修改free，占用内存空间，不立即释放
            			但是Redis作为一个内存缓存中间件来说的话，只要性能高，是可以牺牲一部分内存的
```

​		新版本的SDS,在SDS >= 4.0的版本源码如下：[点击进入](https://github.com/redis/redis/blob/unstable/src/sds.h)

```c
/* 注意： sdshdr5 从未使用过， 我们只是直接访问标志字节.
 * 但是，这里记录类型 5 SDS 字符串的布局. */
struct __attribute__ ((__packed__)) sdshdr5 {
    unsigned char flags; /*3 lsb 的类型，和 5 msb 的字符串长度 */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr8 {
    uint8_t len; /* 已经使用的长度 */
    uint8_t alloc; /* 排除掉Header以及null之后的可分配空间 */
    unsigned char flags; /* 3 lsb类型，5个未使用位 */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr16 {
    uint16_t len; /* used */
    uint16_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr32 {
    uint32_t len; /* used */
    uint32_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr64 {
    uint64_t len; /* used */
    uint64_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
```

## Redis分析工具

​		参考博客

```
https://blog.csdn.net/weixin_48380416/article/details/123995573


rdb -c memory /mnt/data/redis/dump.rdb >  /mnt/data/redis/memory.csv   
```



## RedisObject

​		我们知道了字典的值实际上是一个虚类型

```c
    union {
       	// 值指针
        void *val;
      	// 64位正整数
        uint64_t u64;
      	// 64位int
        int64_t s64;
      	// 浮点数
        double d;
    } v;
```

​		那么除了Numer外，其他的类型的值是怎么存储的呢？

​		我们在Server中有一个redisObject，也就是Redis对象

​		源码地址：https://github.com/redis/redis/blob/unstable/src/server.h

```c
typedef struct redisObject {
  	// Redis对象类型，占4个字节
    unsigned type:4;
  	// 编码，占用4个字节
    unsigned encoding:4;
  	// 用于内存淘汰的lru时间
    unsigned lru:LRU_BITS; /* LRU时间(相对于全局lru_clock)或LFU数据(最低有效8位频率和最有效的16位访问时间)。*/
  	// 引用计数（类似于Java的GC中引用计数，记录引用数）
    int refcount;
  	// 此处就是Redis对象的值实际所指向的内存空间的数据，是一个指针
    void *ptr;
} robj;
```

## Redis5大数据类型底层

### String

​		String数据类型底层存储的编码类型有3种

​				embstr

​				int

​				raw

```
IOS-8859-1：1字节

GBK：2字节

UTF-8：3字节
```



### Hash

### List

### Set

### ZSet

