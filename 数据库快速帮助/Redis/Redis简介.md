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

​			 msetnx <key1> <value1> <key2> <value2> .....      同时设置一个或多个 key-value 对，当且仅当所			

​													有给定key 都不存在

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

# 从根据某个Key，找到某个Value，并且在这个Key左边删除几个值
lrem <key> <n> <value>
# 在bigkang中，1开始向左边，删除一个（包括自己也算一个），n为1表示删除自己
lrem bigkang 1 1
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

# Key的定义的注意点

​		1、不要过长	

​		2、不要过短

​		3、统一的命名规范

# Jedis（JavaAPI）

## 环境

​		JDK+Maven

​		官网给我们推荐了很多种方式连接Redis，地址如下：[点击进入](https://redis.io/clients#java)

​		引入依赖

```xml
    <!-- https://mvnrepository.com/artifact/redis.clients/jedis -->
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.9.0</version>
    </dependency>
```

## 连接

​		连接单节点无密码Redis

```java
Jedis jedis = new Jedis("192.168.1.12",6379);
```

​		连接单节点带密码

```

```



## 常用API

```
	//key

	Set<String> keys = jedis.keys("*");

		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {

		String key = (String) iterator.next();

		System.out.println(key);

	}

	System.out.println("jedis.exists====>"+jedis.exists("k2"));

	System.out.println(jedis.ttl("k1"));

```



## String API

```
    System.out.println(jedis.get("k1"));
    jedis.set("k4","k4_Redis");
    System.out.println("----------------------------------------");
    jedis.mset("str1","v1","str2","v2","str3","v3");
    System.out.println(jedis.mget("str1","str2","str3"));
```



## List API

```
    List<String> list = jedis.lrange("mylist",0,-1);
    for (String element : list) {
    	
    	System.out.println(element);
    	
    }
```



## Set API



```
    jedis.sadd("orders","jd001");
    jedis.sadd("orders","jd002");
    jedis.sadd("orders","jd003");
    Set<String> set1 = jedis.smembers("orders");
    for (Iterator iterator = set1.iterator(); iterator.hasNext();) {
        String string = (String) iterator.next();
        System.out.println(string);
    }
    jedis.srem("orders","jd002");
```

## Hash API

```
    jedis.hset("hash1","userName","lisi");
    System.out.println(jedis.hget("hash1","userName"));
    Map<String,String> map = new HashMap<String,String>();
    map.put("telphone","13810169999");
    map.put("address","atguigu");
    map.put("email","abc@163.com");
    jedis.hmset("hash2",map);
    List<String> result = jedis.hmget("hash2", "telphone","email");
    for (String element : result) {
   		 System.out.println(element);
    }
```

## Zset API

```
    jedis.zadd("zset01",60d,"v1");
    jedis.zadd("zset01",70d,"v2");
    jedis.zadd("zset01",80d,"v3");
    jedis.zadd("zset01",90d,"v4");
    Set<String> s1 = jedis.zrange("zset01",0,-1);
    for (Iterator iterator = s1.iterator(); iterator.hasNext();) {
    String string = (String) iterator.next();
   	 	System.out.println(string);
    }
```



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



​			