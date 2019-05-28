# Redis简介

​		什么是Redis？

​			Redis是一个NoSql（NoSQL = Not Only SQL）数据库（非关系型数据库），“不仅仅是SQL”，

​		NoSQL 不依赖业务逻辑方式存储，而以简单的key-value模式存储。因此大大的增加了数据库的扩展能力

​		，没有ACID的特性，不支持事务，拥有远超于SQL的性能，

​		

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

# Redis的Key的五大数据结构有哪些？

​		五种数据类型：

​						字符串（String）

​						哈希（hash）

​						字符串列表（list）

​						字符串集合（set）

​						有序字符串集合（sorted set）

### Redis的基本操作

​		操作函数				参数				描述

​		 keys   				*					查询当前库的所有键

​				示例：		keys   *				

​				![../img/keys](img\keys.png)





​					

​		 exists 				<key>				判断某个键是否存在

​				示例		exists		

​				![](img\exists.png)





​		 type 				<key>				查看键的类型

​				示例		type   nice		<key>表示键的名字

​				![](img\type.png)





​		 del	 				<key>				删除某个键

​				示例		del    nice		删除哪一个key

​				![](img\del1.png)

​				返回一条影响行数，1表示true，删除成功

​				![](img\del0.png)

​		 expire 		       <key>      <seconds>		为键值设置过期时间，单位秒

​				

​			示例	expire	nice	  30		给主键nice设置过期时间为30秒

​				![](img\expire.png)

​				返回一表示true，设置成功



​		 ttl 					<key>				

​				示例：		ttl nice		（查询上面设置的过期时间）

​					![](img\ttl.png)

​					查询到了数据，查看还有多少秒过期，-1表示永不过期，-2表示已过期



​		dbsize									查看当前数据库的key的数量

​				示例：        dbsize

​					![](img\dbsize.png)



 		Flushdb									清空当前库

​				示例：		Flushdb									

​				![](img\flushdb.png)

​				(甚用！！！，和删库跑路差不多)

​		Flushall									通杀全部库
				示例：		Flushall		

​					![](img\flushall.png)





### 字符串（String）的操作

​			命令<参数>						描述

​			get  <key> 						查询对应键值

​					get  nice				查询nice的key的值

​			set  <key> <value>				添加键值对

​					set nice  qwe			给nice赋值为qwe，没有则创建nice

​			append  <key>  <value>			将给定的<value> 追加

​					append	 nice    nihao		给nice这个key中的value追加一个nihao的值

​			strlen  <key>						获得值的长度

​					strlen    nice			获取nice这个key的长度也就是length

​			setnx <key> <value>				只有在 key 不存在时设置 key 的值

​					setnx	nice   nihao		给nice设置你好，如果存在nice就不设置			

​			incr <key>						将 key 中储存的数字值增1只能对数字值操作，

​											如果为空，新增值为1

​					incr     nice    		注：如果是字符串将无反应，必须为数据

​			decr <key>						将 key 中储存的数字值减1 只能对数字值操作，

​											如果为空，新增值为-1

​					decr nice		给nice这个key中的值减一

​			incrby / decrby <key> <步长>	 	将 key 中储存的数字值增减。自定义步长

​			mset <key1> <value1> <key2> <value2>	 同时设置一个或多个 key-value对

​			 mget <key1> <key2> <key3> .....		 同时获取一个或多个 value

​			 msetnx <key1> <value1> <key2> <value2> .....      同时设置一个或多个 key-value 对，当且仅当所			

​													有给定key 都不存在

​			getrange <key> <起始位置> <结束位置>		获得值的范围，类似java中的substring

​			 setrange <key> <起始位置> <value>		用 value 覆写key>所储存的字符串值，从起始位置	

​													开始

​			 setex <key> <过期时间> <value>		设置键值的同时，设置过期时间，单位秒

​			getset <key> <value>					 以新换旧，设置了新值同时获得就值

### 哈希（Hash）的操作

​			 hset <key> <field> <value>			给<key>集合中的 <field>键赋值<value>

​			hget <key1> <field>					从<key1>集合<field> 取出 value

​			 hmset <key1> <field1> <value1> <field2> <value2>...	批量设置hash的值

​			hexists key <field>					查看哈希表 key 中，给定域 field 是否存在

​			 hkeys <key>							列出该hash集合的所有field

​			 hvals <key>							列出该hash集合的所有value

​			hincrby <key> <field> <increment>		为哈希表 key 中的域 field 的值加上增量 increment

​			 hsetnx <key> <field> <value>			将哈希表 key 中的域 field 的值设置为 value ，当且仅当			

​												域 field 不存在

​			

### 字符串链表（list）的操作

​			 lpush/rpush <key> <value1> <value2> <value3>	从左边/右边插入一个或多个值

​			 lpop/rpop <key>						从左边/右边吐出一个值。值在键在，值光键亡

​			 rpoplpush <key1> <key2>			 从<key1>列表右边吐出一个值，插到<key2>列表左边

​			 lrange <key> <start> <stop>		按照索引下标获得元素(从左到右)

​			 lindex <key> <index>				按照索引下标获得元素(从左到右)

​			 llen <key>						获得列表长度

​			linsert <key> before <value > <newvalue>		在 <value>的后面插入> <newvalue> 插入值

​			lrem <key> <n> <value>			从左边删除n n个 value(从左到右)

​			

### 字符串集合（set）的操作

​			sadd <key> <value1> <value2> .....	将一个或多个 member 元素加入到集合 key 当中，已
											经存在于集合的 member 元素将被忽略。

​			smembers <key>					取出该集合的所有值

​			 sismember <key> <value>			判断集合<key>是否为含有该<value>值，有返回1，
											没有返回0

​			scard <key>						返回该集合的元素个数

​			srem <key> <value1> <value2> ....	删除集合中的某个元素

​			 spop <key>						随机从该集合中吐出一个值

​			 srandmember <key> <n>			随机从该集合中取出n个值。 不会从集合中删除

​			 sinter <key1> <key2>				返回两个集合的交集元素

​			sunion <key1> <key2>				返回两个集合的并集元素

​			 sdiff <key1> <key2>				返回两个集合的差集元素。

​			

### 有序字符串集合（zset）的操作



​			zadd <key> <score1> <value1> <score2> <value2>...	

​			将一个或多个 member 元素及其 score 值		加入到有序集 key 当中‘’

​	

​			 zrange <key> <start> <stop> [WITHSCORES]

​			• 返回有序集 key 中，下标在<start> <stop>之间的元素
			• 带WITHSCORES，可以让分数一起和值返回到结果集。



​			 zrangebyscore key min max [withscores][limit offset count]

​			返回有序集 key 中，所有 score 值介于 min 和 max 之间
			(包括等于 min 或 max )的成员。有序集成员按 score 值递
			增(从小到大)次序排列



​			 zrevrangebyscore key max  【  minwithscores]   [limit offset count]

​			同上，改为从大到小排列

​		

​			 zincrby <key> <increment> <value>

​			为元素的score加上增量



​			zrem <key> <value>

​			删除该集合下，指定值的元素



​			zcount <key> <min> <max>

​			统计该集合，分数区间内的元素个数



​			zrank <key> <value>

​			返回该值在集合中的排名，从0开始



​			

# Key的定义的注意点

​		1、不要过长	

​		2、不要过短

​		3、统一的命名规范

# 存储String

​		如果存入的话，是采用二进制的方式，是安全的，存入和获取的数据相同，并且Value最多可以容纳的数据库长度是512M 

### 存储String的常用命令

​		1、赋值

​			set  key的名字   值

​				例如：

​					set kang 黄康

​					

​					![d](img\a.png)



​		2、取值

​     			get      key的名字

​				例如：

​					get  kang

​					

​		3、扩展命令

​		4、删除

​		5、数值增减





# Redis相关配置

​	redis.conf 配置项说明如下：

1. Redis默认不是以守护进程的方式运行，可以通过该配置项修改，使用yes启用守护进程

​    **daemonize no**

2. 当Redis以守护进程方式运行时，Redis默认会把pid写入/var/run/redis.pid文件，可以通过pidfile指定

​    **pidfile /var/run/redis.pid**

3. 指定Redis监听端口，默认端口为6379，作者在自己的一篇博文中解释了为什么选用6379作为默认端口，因为6379在手机按键上MERZ对应的号码，而MERZ取自意大利歌女Alessia Merz的名字

​    **port 6379**

4. 绑定的主机地址

​    **bind 127.0.0.1**

 5.当 客户端闲置多长时间后关闭连接，如果指定为0，表示关闭该功能

​    **timeout 300**

6. 指定日志记录级别，Redis总共支持四个级别：debug、verbose、notice、warning，默认为verbose

​    **loglevel verbose**

7. 日志记录方式，默认为标准输出，如果配置Redis为守护进程方式运行，而这里又配置为日志记录方式为标准输出，则日志将会发送给/dev/null

​    **logfile stdout**

8. 设置数据库的数量，默认数据库为0，可以使用SELECT <dbid>命令在连接上指定数据库id

​    **databases 16**

9. 指定在多长时间内，有多少次更新操作，就将数据同步到数据文件，可以多个条件配合

​    **save <seconds> <changes>**

​    Redis默认配置文件中提供了三个条件：

​    **save 900 1**

​    **save 300 10**

​    **save 60 10000**

​    分别表示900秒（15分钟）内有1个更改，300秒（5分钟）内有10个更改以及60秒内有10000个更改。

 

10. 指定存储至本地数据库时是否压缩数据，默认为yes，Redis采用LZF压缩，如果为了节省CPU时间，可以关闭该选项，但会导致数据库文件变的巨大

​    **rdbcompression yes**

11. 指定本地数据库文件名，默认值为dump.rdb

​    **dbfilename dump.rdb**

12. 指定本地数据库存放目录

​    **dir ./**

13. 设置当本机为slav服务时，设置master服务的IP地址及端口，在Redis启动时，它会自动从master进行数据同步

​    **slaveof <masterip> <masterport>**

14. 当master服务设置了密码保护时，slav服务连接master的密码

​    **masterauth <master-password>**

15. 设置Redis连接密码，如果配置了连接密码，客户端在连接Redis时需要通过AUTH <password>命令提供密码，默认关闭

​    **requirepass foobared**

16. 设置同一时间最大客户端连接数，默认无限制，Redis可以同时打开的客户端连接数为Redis进程可以打开的最大文件描述符数，如果设置 maxclients 0，表示不作限制。当客户端连接数到达限制时，Redis会关闭新的连接并向客户端返回max number of clients reached错误信息

​    **maxclients 128**

17. 指定Redis最大内存限制，Redis在启动时会把数据加载到内存中，达到最大内存后，Redis会先尝试清除已到期或即将到期的Key，当此方法处理 后，仍然到达最大内存设置，将无法再进行写入操作，但仍然可以进行读取操作。Redis新的vm机制，会把Key存放内存，Value会存放在swap区

​    **maxmemory <bytes>**

18. 指定是否在每次更新操作后进行日志记录，Redis在默认情况下是异步的把数据写入磁盘，如果不开启，可能会在断电时导致一段时间内的数据丢失。因为 redis本身同步数据文件是按上面save条件来同步的，所以有的数据会在一段时间内只存在于内存中。默认为no

​    **appendonly no**

19. 指定更新日志文件名，默认为appendonly.aof

​     **appendfilename appendonly.aof**

20. 指定更新日志条件，共有3个可选值： 

​    **no**：表示等操作系统进行数据缓存同步到磁盘（快） 
    **always**：表示每次更新操作后手动调用fsync()将数据写到磁盘（慢，安全） 
    **everysec**：表示每秒同步一次（折中，默认值）

​    **appendfsync everysec**

 

21. 指定是否启用虚拟内存机制，默认值为no，简单的介绍一下，VM机制将数据分页存放，由Redis将访问量较少的页即冷数据swap到磁盘上，访问多的页面由磁盘自动换出到内存中（在后面的文章我会仔细分析Redis的VM机制）

​     **vm-enabled no**

22. 虚拟内存文件路径，默认值为/tmp/redis.swap，不可多个Redis实例共享

​     **vm-swap-file /tmp/redis.swap**

23. 将所有大于vm-max-memory的数据存入虚拟内存,无论vm-max-memory设置多小,所有索引数据都是内存存储的(Redis的索引数据 就是keys),也就是说,当vm-max-memory设置为0的时候,其实是所有value都存在于磁盘。默认值为0

​     **vm-max-memory 0**

24. Redis swap文件分成了很多的page，一个对象可以保存在多个page上面，但一个page上不能被多个对象共享，vm-page-size是要根据存储的 数据大小来设定的，作者建议如果存储很多小对象，page大小最好设置为32或者64bytes；如果存储很大大对象，则可以使用更大的page，如果不 确定，就使用默认值

​     **vm-page-size 32**

25. 设置swap文件中的page数量，由于页表（一种表示页面空闲或使用的bitmap）是在放在内存中的，，在磁盘上每8个pages将消耗1byte的内存。

​     **vm-pages 134217728**

26. 设置访问swap文件的线程数,最好不要超过机器的核数,如果设置为0,那么所有对swap文件的操作都是串行的，可能会造成比较长时间的延迟。默认值为4

​     **vm-max-threads 4**

27. 设置在向客户端应答时，是否把较小的包合并为一个包发送，默认为开启

​    **glueoutputbuf yes**

\28. 指定在超过一定的数量或者最大的元素超过某一临界值时，采用一种特殊的哈希算法

​    **hash-max-zipmap-entries 64**

​    **hash-max-zipmap-value 512**

29. 指定是否激活重置哈希，默认为开启（后面在介绍Redis的哈希算法时具体介绍）

​    **activerehashing yes**

30. 指定包含其它的配置文件，可以在同一主机上多个Redis实例之间使用同一份配置文件，而同时各个实例又拥有自己的特定配置文件

​    **include /path/to/local.conf**



# Jedis操作

​	

​		环境

​				Commons-pool-1.6.jar
 				Jedis-2.1.0.jar

​		

​	public class Demo01 {
		public static void main(String[] args) {
			//连接本地的 Redis 服务
			Jedis jedis = new Jedis("127.0.0.1",6379);
			//查看服务是否运行，打出pong表示OK
			System.out.println("connection is OK==========>:
			"+jedis.ping());
		}
	}







### Key的获取

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



### String操作

```
    System.out.println(jedis.get("k1"));
    jedis.set("k4","k4_Redis");
    System.out.println("----------------------------------------");
    jedis.mset("str1","v1","str2","v2","str3","v3");
    System.out.println(jedis.mget("str1","str2","str3"));
```



### List操作

```
    List<String> list = jedis.lrange("mylist",0,-1);
    for (String element : list) {
    	
    	System.out.println(element);
    	
    }
```



### Set操作



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

### Hash操作

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

### Zset操作

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



### 详细更多api请参照语法以及Api方法调用



# Redis的持久化？

​		Redis的持久化方案有两种

​			RDB （Redis DataBase）

​			AOF （Append Of File）



## RDB持久化

​		RDB的存储方式：在指定的时间间隔内将内存中的数据集快照写入磁盘，也
						就是行话讲的Snapshot快照，它恢复时是将快照文件直接
						读到内存里



​		Redis会单独创建（fork）一个子进程来进行持久化，会先将数据写入
		到一个临时文件中，待持久化过程都结束了，再用这个临时文件替换
		上次持久化好的文件。整个过程中，主进程是不进行任何IO操作的，
		这就确保了极高的性能如果需要进行大规模数据的恢复，且对于数据
		恢复的完整性不是非常敏感，那RDB方式要比AOF方式更加的高效。
		RDB的缺点是最后一次持久化后的数据可能丢失



​		rdb的保存的文件： 在redis.conf中配置文件名称，默认为dump.rdb

​		![](img\drb.png)



​		下面就是他的持久化的文件存储的路径



​		RDB持久化的保存策略

​		这是他的保存策略，如果60秒内发生了10000次数据操作则进行一次存储，

​		如果300秒发生了10次则会存入一次。如果900秒内发生了一次操作那么900秒后

​		就会备份一次，

​		![](img\保存策略rdb.png)

​		stop-writes-on-bgsave-error yes
		当Redis无法写入磁盘的话，直接关掉Redis的写操作

​		rdbcompression yes
		进行rdb保存时，将文件压缩

​		rdbchecksum yes
		在存储快照后，还可以让Redis使用CRC64算法来进行数
		据校验，但是这样做会增加大约10%的性能消耗，如果希
		望获取到最大的性能提升，可以关闭此功能



### 		Redis持久化--RDB

​				rdb的备份

​					先通过config get dir 查询rdb文件的目录
					将*.rdb的文件拷贝到别的地方

​			 	rdb的恢复

​			 		先把备份的文件拷贝到工作目录下
		 			关闭Redis
		 			启动Redis, 备份数据会直接加载

​				注：清先看清楚配置文件的RDB的文件名和路径

### 		RDB特性

​				优点：

​					 节省磁盘空间
 					 恢复速度快

​				rdb的缺点

​					虽然Redis在fork时使用了写时拷贝技术,

​					但是如果数据庞大时还是比较消耗性能



​					在备份周期在一定间隔时间做一次备份，所以如果
					Redis意外down掉的话，就会丢失最后一次快照后的所有修改					

​		

## AOF持久化

​			 AOF默认不开启，需要手动在配置文件中配置

​			 可以在redis.conf中配置文件名称，默认为 appendonly.aof

​			![](img\AOF.png)

​		这分别是是否开启。默认是关闭的而RDB默认是开启的，他的路径和RDB是一样的

​		下面这个就是文件名了

​		

​		那么如果RDB好AOF同时启动的话他会执行哪个呢？

​			AOF的备份机制和性能虽然和RDB不同, 但是备份和
			恢复的操作同RDB一样，都是拷贝备份文件，需要
			恢复时再拷贝到Redis工作目录下，启动系统即加载

​			**AOF和RDB同时开启，系统默认取AOF的数据**



### 持久化AOF

​			 AOF文件的保存路径，同RDB的路径一致

​			 如遇到AOF文件损坏，可通过
				redis-check-aof --fix appendonly.aof 进行恢复



​			