# SpringDataRedis方式

## 首先先引入依赖

```xml
        <!--整合Redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!--根据项目决定是否引入session共享，可选-->
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
        </dependency>
				<!--Lettuce连接池依赖-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
```

## 配置配置文件

​		SpringBoot在1.X的时候使用Jedis作为Redis客户端，但是在2.X版本默认引入了Lettuce客户端，Lettuce的连接是基于Netty的，连接实例（StatefulRedisConnection）可以在多个线程间并发访问，应为StatefulRedisConnection是线程安全的，所以一个连接实例（StatefulRedisConnection）就可以满足多线程环境下的并发访问，当然这个也是可伸缩的设计，一个连接实例不够的情况也可以按需增加连接实例。

### 单节点

#### Jedis连接池

​		配置文件配置，以及配置Jedis连接池，SpringBoot默认使用Lettuce客户端，我们需要修改依赖

​		Maven修改如下

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
          	<!-- 排除lettuce依赖 -->
            <exclusions>
                <exclusion>
                    <groupId>io.lettuce</groupId>
                    <artifactId>lettuce-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
				<!-- 引入Jedis -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>
```

​		然后配置

```properties
--------------------------------------------------------------------------------
Yaml版本
--------------------------------------------------------------------------------
spring:
  redis:
    # 数据库
    database: 0
    # RedisHost地址
    host: 192.168.1.12
    # 密码
    password: bigkang
    # 端口号
    port: 6379
    # Jedis连接池配置
    jedis:
      pool:
        # 在给定的池中可以分配的最大连接数时间。使用负值表示没有限制,默认8
        max-active: 8
        # 目标是在池中维护的最小空闲连接数。这个仅当设置和驱逐运行之间的时间均达到，肯定默认0
        min-idle: 0
        # 连接分配在抛出一个连接之前应阻塞的最长时间池耗尽时例外。使用负值阻止无限期，默认-1
        max-wait: -1ms
        # 池中“空闲”连接的最大数量。使用负值表示无限数量的空闲连接，默认8
        max-idle: 8
        # 空闲对象退出线程的运行之间的时间。当为正时，空闲对象驱逐线程启动，否则不执行空闲的对象驱逐，默认为空
        time-between-eviction-runs: 3000ms
--------------------------------------------------------------------------------
Properties版本
--------------------------------------------------------------------------------
# 数据库
spring.redis.database=0
# RedisHost地址
spring.redis.host=192.168.1.12
# 密码
spring.redis.password=bigkang
# 端口号
spring.redis.port=6379
# Jedis连接池配置,在给定的池中可以分配的最大连接数时间。使用负值表示没有限制,默认8
spring.redis.jedis.pool.max-active=8
# Jedis连接池配置,目标是在池中维护的最小空闲连接数。这个仅当设置和驱逐运行之间的时间均达到，肯定默认0
spring.redis.jedis.pool.min-idle=0
# Jedis连接池配置,连接分配在抛出一个连接之前应阻塞的最长时间池耗尽时例外。使用负值阻止无限期，默认-1
spring.redis.jedis.pool.max-wait=-1ms
# Jedis连接池配置,池中“空闲”连接的最大数量。使用负值表示无限数量的空闲连接，默认8
spring.redis.jedis.pool.max-idle=8
# Jedis连接池配置,空闲对象退出线程的运行之间的时间。当为正时，空闲对象驱逐线程启动，否则不执行空闲的对象驱逐，默认为空
spring.redis.jedis.pool.time-between-eviction-runs=3000ms
```

#### Lettuce连接池

​		配置文件配置，以及配置Lettuce连接池,并且使用URL方式进行连接

```properties
--------------------------------------------------------------------------------
Yaml版本
--------------------------------------------------------------------------------
spring:
  redis:
    # URL连接，格式为redis:// (用户名为空，必须加冒号):密码@Host地址:端口号/数据库
    url: redis://:bigkang@192.168.1.12:6379/0
    # Lettuce连接池配置
    lettuce:
      pool:
        # 在给定的池中可以分配的最大连接数时间。使用负值表示没有限制,默认8
        max-active: 8
        # 目标是在池中维护的最小空闲连接数。这个仅当设置和驱逐运行之间的时间均达到，肯定默认0
        min-idle: 0
        # 连接分配在抛出一个连接之前应阻塞的最长时间池耗尽时例外。使用负值阻止无限期，默认-1
        max-wait: -1ms
        # 池中“空闲”连接的最大数量。使用负值表示无限数量的空闲连接，默认8
        max-idle: 8
        # 空闲对象退出线程的运行之间的时间。当为正时，空闲对象驱逐线程启动，否则不执行空闲的对象驱逐，默认为空
        time-between-eviction-runs: 3000ms

--------------------------------------------------------------------------------
Properties版本
--------------------------------------------------------------------------------
# URL连接，格式为redis:// (用户名为空，必须加冒号):密码@Host地址:端口号/数据库
spring.redis.url=redis://:bigkang@192.168.1.12:6379/0
# Lettuce连接池配置,在给定的池中可以分配的最大连接数时间。使用负值表示没有限制,默认8
spring.redis.lettuce.pool.max-active=8
# Lettuce连接池配置,目标是在池中维护的最小空闲连接数。这个仅当设置和驱逐运行之间的时间均达到，肯定默认0
spring.redis.lettuce.pool.min-idle=0
# Lettuce连接池配置,连接分配在抛出一个连接之前应阻塞的最长时间池耗尽时例外。使用负值阻止无限期，默认-1
spring.redis.lettuce.pool.max-wait=-1ms
# Lettuce连接池配置,池中“空闲”连接的最大数量。使用负值表示无限数量的空闲连接，默认8
spring.redis.lettuce.pool.max-idle=8
# Lettuce连接池配置,空闲对象退出线程的运行之间的时间。当为正时，空闲对象驱逐线程启动，否则不执行空闲的对象驱逐，默认为空
spring.redis.lettuce.pool.time-between-eviction-runs=3000ms
```

### 集群版

​		连接池配置参考上方

```properties
--------------------------------------------------------------------------------
Yaml版本
--------------------------------------------------------------------------------
spring:
  redis:
    cluster:
      # 用逗号分隔的“host:port”对列表。这是一个群集节点的“初始”列表，并要求至少有一个条目。
      nodes: 192.168.1.12:6379,192.168.1.13:6379,192.168.1.14:6379
      # 在跨对象执行命令时要遵循的最大重定向数,集群
      max-redirects: 6
    # 密码
    password: bigkang
    # 客户端超时时间
    timeout: 2000ms
  
--------------------------------------------------------------------------------
Properties版本
--------------------------------------------------------------------------------
# 用逗号分隔的“host:port”对列表。这是一个群集节点的“初始”列表，并要求至少有一个条目。
spring.redis.cluster.nodes=192.168.1.12:6379,192.168.1.13:6379,192.168.1.14:6379
# 在跨对象执行命令时要遵循的最大重定向数,集群
spring.redis.cluster.max-redirects=6
# 密码
spring.redis.password=bigkang
# 客户端超时时间
spring.redis.timeout=2000ms
```

### 操作Redis

​		参考下方RedisTemplate进阶

# Redission方式

​		官网地址：[点击进入](https://github.com/redisson/redisson)

## 引入依赖

```xml
 				<dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.11.0</version>
        </dependency>
```

## 编写配置

​		Redisson的配置实在是恶心,引入了SpringDataRedis，但是配置方式却不同，类型分为如下：



- ​				ClusterServersConfig								集群配置

- ​				MasterSlaveServersConfig                       主从配置

- ​				SingleServerConfig                                    单机配置

- ​				SentinelServersConfig                               哨兵配置

- ​				ReplicatedServersConfig                           复制集配置

  ​	

  org.redisson.config.Config是一个总的配置类，下面有很多的配置

```java

		// 哨兵配置
    private SentinelServersConfig sentinelServersConfig;

		// 主从配置
    private MasterSlaveServersConfig masterSlaveServersConfig;

		// 单节点配置
    private SingleServerConfig singleServerConfig;

		// 集群配置
    private ClusterServersConfig clusterServersConfig;
		
		// 复制集配置
    private ReplicatedServersConfig replicatedServersConfig;

		// 连接管理器
    private ConnectionManager connectionManager;

    // 所有Redis节点客户端之间共享的线程数(最大线程数)
    private int threads = 16;

		// netty线程数
    private int nettyThreads = 32;

    // Redis键/值编解码器。默认情况下使用FST编解码器
    private Codec codec;

		// 执行器服务
    private ExecutorService executor;

    // 用于启用Redisson参考功能的配置选项,默认值为TRUE
    private boolean referenceEnabled = true;

		// 通信模式（NIO）
    private TransportMode transportMode = TransportMode.NIO;

		// 事件循环组
    private EventLoopGroup eventLoopGroup;

		// 看门狗超时配置,30秒
    private long lockWatchdogTimeout = 30 * 1000;

		// 保持Pub子订单
    private boolean keepPubSubOrder = true;

		......其他更多配置
```

​		所有的配置都在org.redisson.config包下，此处仅仅演示单机以及集群配置，其他请参考官网或者源码，下面我们来使用SpringBoot整合的Redsson吧。

### 单节点

```properties
--------------------------------------------------------------------------------
Yaml版本
--------------------------------------------------------------------------------
spring:
  redis:
    redisson:
      config: |
        singleServerConfig:
          address: redis://124.71.9.101:16371
          password: bigkang
          database: 0
          clientName: redisson
          idleConnectionTimeout: 10000
          sslEnableEndpointIdentification: false
          connectTimeout: 10000
          timeout: 3000
          retryAttempts: 3
          retryInterval: 1500
          subscriptionsPerConnection: 5
          pingConnectionInterval: 0
          keepAlive: false
          tcpNoDelay: false
        threads: 16
        nettyThreads: 32
        codec: !<org.redisson.codec.MarshallingCodec> {}
        transportMode: "NIO"
        
--------------------------------------------------------------------------------
Properties版本（极其恶心，建议使用file，指定redisson路径）
\u0020\u0020表示两个空格，\n\表示换行，然后\格式化
--------------------------------------------------------------------------------

spring.redis.redisson.config=singleServerConfig:\n\
\u0020\u0020address: redis://124.71.9.101:16371\n\
\u0020\u0020password: bigkang\n\
\u0020\u0020database: 0\n\
\u0020\u0020clientName: redisson\n\
\u0020\u0020idleConnectionTimeout: 10000\n\
\u0020\u0020sslEnableEndpointIdentification: false\n\
\u0020\u0020connectTimeout: 10000\n\
\u0020\u0020timeout: 3000\n\
\u0020\u0020retryAttempts: 3\n\
\u0020\u0020retryInterval: 1500\n\
\u0020\u0020subscriptionsPerConnection: 5\n\
\u0020\u0020pingConnectionInterval: 0\n\
\u0020\u0020keepAlive: false\n\
\u0020\u0020tcpNoDelay: false\n\
threads: 16\n\
nettyThreads: 32\n\
codec: !<org.redisson.codec.MarshallingCodec> {}\n\
transportMode: "NIO"
```

​		其中属于BaseConfig的有，所有的ServerConfig都可以配置如下属性,相当于我们只配置了address: 124.71.9.101:16371

```properties
          password: bigkang
          clientName: redisson
          idleConnectionTimeout: 10000
          connectTimeout: 10000
          timeout: 3000
          retryAttempts: 3
          retryInterval: 1500
          subscriptionsPerConnection: 5
          pingConnectionInterval: 0
          keepAlive: false
          tcpNoDelay: false
```

### 集群版

```properties
--------------------------------------------------------------------------------
Yaml版本
--------------------------------------------------------------------------------
spring:
  redis:
    redisson: 
      config: |
        clusterServersConfig:
          idleConnectionTimeout: 10000
          connectTimeout: 10000
          timeout: 3000
          retryAttempts: 3
          retryInterval: 1500
          failedSlaveReconnectionInterval: 3000
          failedSlaveCheckInterval: 60000
          password: bigkang
          subscriptionsPerConnection: 5
          clientName: null
          loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}
          subscriptionConnectionMinimumIdleSize: 1
          subscriptionConnectionPoolSize: 50
          slaveConnectionMinimumIdleSize: 24
          slaveConnectionPoolSize: 64
          masterConnectionMinimumIdleSize: 24
          masterConnectionPoolSize: 64
          readMode: "SLAVE"
          subscriptionMode: "SLAVE"
          nodeAddresses:
          - "redis://127.0.0.1:7004"
          - "redis://127.0.0.1:7001"
          - "redis://127.0.0.1:7000"
          scanInterval: 1000
          pingConnectionInterval: 0
          keepAlive: false
          tcpNoDelay: false
        threads: 16
        nettyThreads: 32
        codec: !<org.redisson.codec.MarshallingCodec> {}
        transportMode: "NIO"
        
--------------------------------------------------------------------------------
Properties版本（极其恶心，建议使用file，指定redisson路径）
\u0020\u0020表示两个空格，\n\表示换行，然后\格式化
--------------------------------------------------------------------------------

spring.redis.redisson.config==clusterServersConfig:\n\
\u0020\u0020idleConnectionTimeout: 10000\n\
\u0020\u0020connectTimeout: 10000\n\
\u0020\u0020timeout: 3000\n\
\u0020\u0020retryAttempts: 3\n\
\u0020\u0020retryInterval: 1500\n\
\u0020\u0020failedSlaveReconnectionInterval: 3000\n\
\u0020\u0020failedSlaveCheckInterval: 60000\n\
\u0020\u0020password: bigkang\n\
\u0020\u0020subscriptionsPerConnection: 5\n\
\u0020\u0020clientName: null\n\
\u0020\u0020loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}\n\
\u0020\u0020subscriptionConnectionMinimumIdleSize: 1\n\
\u0020\u0020subscriptionConnectionPoolSize: 50\n\
\u0020\u0020slaveConnectionMinimumIdleSize: 24\n\
\u0020\u0020slaveConnectionPoolSize: 64\n\
\u0020\u0020masterConnectionMinimumIdleSize: 24\n\
\u0020\u0020masterConnectionPoolSize: 64\n\
\u0020\u0020readMode: "SLAVE"\n\
\u0020\u0020subscriptionMode: "SLAVE"\n\
\u0020\u0020nodeAddresses:\n\
\u0020\u0020- "redis://127.0.0.1:7004"\n\
\u0020\u0020- "redis://127.0.0.1:7001"\n\
\u0020\u0020- "redis://127.0.0.1:7000"\n\
\u0020\u0020scanInterval: 1000\n\
\u0020\u0020pingConnectionInterval: 0\n\
\u0020\u0020keepAlive: false\n\
\u0020\u0020tcpNoDelay: false\n\
threads: 16\n\
nettyThreads: 32\n\
codec: !<org.redisson.codec.MarshallingCodec> {}\n\
transportMode: "NIO"
```

### 操作Redis

​		参考下方RedisTemplate进阶

# RedisTemplate进阶

## 常用操作

```java
        // 删除单个Key,返回true删除成功，返回false失败，Key不存在
        Boolean delKey = redisTemplate.delete("bigkang");

        //删除多个Key，返回删除成功的条数，例如3个Key返回2，表示有一个Key不存在
        Long delKeys = redisTemplate.delete(Arrays.asList("bigkang1", "bigkang2"));
	
				// 判断单个Key是否存在,存在返回true，失败返回false，Key不存在
        Boolean exiKey = redisTemplate.hasKey("bigkang");

        // 判断多个Key是否存在,存在返回存在个数，都不存在返回0
        Long exiKeys = redisTemplate.countExistingKeys(Arrays.asList("bigkang1", "bigkang2"));

        // 查询所有匹配的Key，*为通配符可以匹配前缀后缀
        String pattern = "*";
        Set keys = redisTemplate.keys(pattern);

			  // 获取某个Key的类型，NONE，STRING，LIST，SET，ZSET，HASH，STREAM
        DataType type = redisTemplate.type("bigkang");

				// 设置超时时间，根据Key，超时时间，以及时间单位进行设置,返回是否设置成功
        Boolean setExpire = redisTemplate.expire("bigkang", 10, TimeUnit.SECONDS);

        // 指定时间过期，返回成功或者失败
        Boolean expireAt = redisTemplate.expireAt("bigkang", new Date());
        System.out.println(expireAt);

        // 获取Key的超时时间默认为秒
        Long time = redisTemplate.getExpire("bigkang");

				// 获取Key的超时时间,单位自定义
        Long time = redisTemplate.getExpire("bigkang",TimeUnit.SECONDS);			
```

## String操作

```java
        // String操作都是采用opsForValue,表示对String进行操作，会根据redisTemplate的泛型序列化
        ValueOperations<Object, Object> objectObjectValueOperations = redisTemplate.opsForValue();

        // 设置一个Key为bigkang，值为"123"，没有返回值
        redisTemplate.opsForValue().set("bigkang","123");
	
				// 获取bigkang这个Key的Value值
        Object get = redisTemplate.opsForValue().get("bigkang");

				// 追加某个Key，返回追加后的长度，如果Key不存在，则创建Key赋值空，然后追加
        Integer length = redisTemplate.opsForValue().append("bigkang", "456");
				
				// 获取某个Key的长度，不存在返回0
        Long length = redisTemplate.opsForValue().size("bigkang7");

			  // 将某个Key的Value进行+1操作，然后返回值，必须Number数字类型
        Long value = redisTemplate.opsForValue().increment("bigkang");

				// 将某个Key的Value进行+指定的值，然后返回值，必须Number数字类型
        Long value = redisTemplate.opsForValue().increment("bigkang",2L);

				// 将某个Key的Value进行-1操作，然后返回值，必须Number数字类型
        Long value = redisTemplate.opsForValue().decrement("bigkang");

				// 将某个Key的Value进行-指定的值，然后返回值，必须Number数字类型
        Long value = redisTemplate.opsForValue().decrement("bigkang",2L);

				// 批量设置键值对，采用Map
        Map<String,Object> map = new HashMap<>();
        map.put("bigkang1",1);
        map.put("bigkang2",2);
        map.put("bigkang3",3);
        redisTemplate.opsForValue().multiSet(map);

				// 批量获取键值对，返回List,不存在返回Null
        List<Object> objects = redisTemplate.opsForValue().multiGet(Arrays.asList("bigkang1", "bigkang2", "bigkang3"));
        
```



## List操作



## Set操作



## Hash操作



## Zset操作



# 通用进阶

## RedisTemplate序列化问题

​		RedisTemplate默认采用JDK序列化骂我们如果想要修改，我们可以通过配置类修改他的序列化方式。

​		使用配置类注入进去，然后使用RedisTemplate即可发现Key以及Value正常

```java
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置Hash的Key以及Value的序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
```

## 连接池测试

​		使用如下测试类即可测试Spring采用哪种连接池进行连接

```java
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Test
    public void test(){
        if(redisConnectionFactory instanceof JedisConnectionFactory){
            System.out.println("Jedis连接工厂");
        }else if(redisConnectionFactory instanceof LettuceConnectionFactory){
            System.out.println("Lettuce连接工厂");
        }else if(redisConnectionFactory instanceof RedissonConnectionFactory){
            System.out.println("Redisson连接工厂");
        }
    }
```

