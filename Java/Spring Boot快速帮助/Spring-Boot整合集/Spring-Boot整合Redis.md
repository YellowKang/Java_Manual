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

​		官网Wiki：[点击进入](https://github.com/redisson/redisson/wiki/Table-of-Content)

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

## Redisson进阶

### 分布式锁和同步器

#### 分布式锁

​		用于Java的基于Redis的分布式可重入[Lock](https://static.javadoc.io/org.redisson/redisson/3.11.6/org/redisson/api/RLock.html)对象并实现`java.util.concurrent.locks.Lock`接口

​		如果获取锁的Redisson实例崩溃，则该锁可能会在获取状态下永久挂起。为避免此Redisson维护锁看门狗，它会在锁持有人Redisson实例处于活动状态时延长锁的到期时间。默认情况下，锁看门狗超时为30秒，可以通过[Config.lockWatchdogTimeout](https://github.com/redisson/redisson/wiki/2.-Configuration#lockwatchdogtimeout)设置进行更改。

​		Redisson还允许`leaseTime`在锁定获取期间指定参数。在指定的时间间隔后，锁定的锁将自动释放。

​		`RLock`对象的行为符合Java Lock规范。这意味着只有锁所有者线程才能解锁它，否则`IllegalMonitorStateException`将引发该锁。否则考虑使用[RSemaphore](https://github.com/mrniko/redisson/wiki/8.-distributed-locks-and-synchronizers/#86-semaphore)对象。

​		同步获取锁的方式

```java
				// 获取锁对象
				RLock lock = redisson.getLock("bigkang-lock");

					// 同步获取锁的几种方式

							// 传统的锁方法
							lock.lock();

							// 获取锁定，并在锁定10秒钟后自动将其解锁
							lock.lock(10, TimeUnit.SECONDS);
							
							// 等待最长100秒的锁获取，并在10秒后自动将其解锁
							boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
							// 如果成功获取在进行解锁
							if (res) {
                 try {
                   ...
                 } finally {
                     lock.unlock();
                 }
              }
```

​			异步获取锁的方式

```java
				// 获取锁对象
				RLock lock = redisson.getLock("bigkang-lock");

					// 异步获取锁的几种方式

							// 传统异步获取锁方法
							RFuture<Void> lockFuture = lock.lockAsync();

							// 获取锁定，并在锁定10秒钟后自动将其解锁
							RFuture<Void> lockFuture = lock.lockAsync(10, TimeUnit.SECONDS);
							
							// 等待最长100秒的锁获取，并在10秒后自动将其解锁
							RFuture<Boolean> lockFuture = lock.tryLockAsync(100, 10, TimeUnit.SECONDS);
							// 异步解锁
							lockFuture.whenComplete((res, exception) -> {
                  // ...
                  lock.unlockAsync();
              });
```

#### 公平锁

​		用于Java的基于Redis的分布式重入公平[锁定](https://static.javadoc.io/org.redisson/redisson/3.11.6/org/redisson/api/RLock.html)对象实现`java.util.concurrent.locks.Lock`接口。

​		公平锁保证线程将以与请求它相同的顺序来获取它。所有等待的线程都已排队，如果某个线程死亡，则Redisson等待其返回5秒钟。例如，如果5个线程由于某种原因而死亡，则延迟将为25秒。

​		如果获取锁的Redisson实例崩溃，则该锁可能会在获取状态下永久挂起。为避免此Redisson维护锁看门狗，它会在锁持有人Redisson实例处于活动状态时延长锁的到期时间。默认情况下，锁看门狗超时为30秒，可以通过[Config.lockWatchdogTimeout](https://github.com/redisson/redisson/wiki/2.-Configuration#lockwatchdogtimeout)设置进行更改。

​		Redisson还允许`leaseTime`在锁定获取期间指定参数。在指定的时间间隔后，锁定的锁将自动释放。

​		`RLock`对象的行为符合Java Lock规范。这意味着只有锁所有者线程才能解锁它，否则`IllegalMonitorStateException`将引发该锁。否则考虑使用[RSemaphore](https://github.com/mrniko/redisson/wiki/8.-distributed-locks-and-synchronizers/#86-semaphore)对象。

```java
        RLock lock = redisson.getFairLock("bigkang-lock");
```

#### 多重锁

​		基于Redis的分布式`MultiLock`对象允许对[Lock](https://static.javadoc.io/org.redisson/redisson/3.11.6/org/redisson/api/RLock.html)对象进行分组并将其作为单个锁进行处理。每个`RLock`对象可能属于不同的Redisson实例。

​		如果获得了`MultiLock`崩溃的Redisson实例崩溃，则该实例`MultiLock`可能会永远处于捕获状态。为避免此Redisson维护锁看门狗，它会在锁持有人Redisson实例处于活动状态时延长锁的到期时间。默认情况下，锁看门狗超时为30秒，可以通过[Config.lockWatchdogTimeout](https://github.com/redisson/redisson/wiki/2.-Configuration#lockwatchdogtimeout)设置进行更改。

​		其他都是上方锁一样

```java
        RLock lock1 = redisson1.getLock("lock1");
        RLock lock2 = redisson2.getLock("lock2");
        RLock lock3 = redisson3.getLock("lock3");
				RLock multiLock = redisson.getMultiLock(lock1, lock2, lock3);
```

#### 读写锁

​		用于Java的基于Redis的分布式重入[ReadWriteLock](http://static.javadoc.io/org.redisson/redisson/3.11.6/org/redisson/api/RReadWriteLock.html)对象实现`java.util.concurrent.locks.ReadWriteLock`接口。读锁和写锁均实现[RLock](https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers/#81-lock)接口。

​		允许多个ReadLock所有者和一个WriteLock所有者。

​		如果获取锁的Redisson实例崩溃，则该锁可能会在获取状态下永久挂起。为避免此Redisson维护锁看门狗，它会在锁持有人Redisson实例处于活动状态时延长锁的到期时间。默认情况下，锁看门狗超时为30秒，可以通过[Config.lockWatchdogTimeout](https://github.com/redisson/redisson/wiki/2.-Configuration#lockwatchdogtimeout)设置进行更改。

​		其他和上方一样

```java
        // 获取Redis锁对线
				RReadWriteLock rwlock = redisson.getReadWriteLock("myLock");

				// 获取读锁
        RLock lock = rwlock.readLock();

        // 获取写锁
        RLock lock = rwlock.writeLock();
```

#### 信号量

​		与Java类似的基于Redis的分布式[信号量](http://static.javadoc.io/org.redisson/redisson/3.11.6/org/redisson/api/RSemaphore.html)`java.util.concurrent.Semaphore`对象。

​		可以在使用前进行初始化，但这不是必需的，可以通过`trySetPermits(permits)`方法获得允许的数量。

```java
				// 获取Redis信号量
				RSemaphore semaphore = redisson.getSemaphore("mySemaphore");

				// 获得单信号量的锁
				semaphore.acquire();

				// 获得10个信号量的锁
				semaphore.acquire(10);

				// 尝试获取锁
				boolean res = semaphore.tryAcquire();
			
        // 尝试获取锁，或等待15秒
        boolean res = semaphore.tryAcquire(15, TimeUnit.SECONDS);

        // 尝试获取锁，10个信号量
        boolean res = semaphore.tryAcquire(10);

        // 尝试获取锁，10个信号量，15秒钟获取不到放弃获取
        boolean res = semaphore.tryAcquire(10, 15, TimeUnit.SECONDS);
        if (res) {
           try {
             ...
           } finally {
               semaphore.release();
           }
        }
```

### 布隆过滤器

```java
        // 获取Redis布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("bigkang11");
        // 布隆过滤器如果不存在则创建
        if (!bloomFilter.isExists()) {
            // 预计存储100000000个元素，百分之3的误差
            bloomFilter.tryInit(100000000L,0.03);
        }
        // 添加元素
        bloomFilter.add("1.1");
        bloomFilter.add("1.2");
        // 判断布隆过滤器是否存在
        System.out.println(bloomFilter.contains("1.2"));
```

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

				// 设置一个Key为bigkang，值为"123",一个小时后超时，没有返回值
        redisTemplate.opsForValue().set("bigkang","123",1L,TimeUnit.HOURS);

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
        
        // 如果不存在则设置，并且设置超时时间，类似于Setnx
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("bigkang", "lock", 1L, TimeUnit.HOURS);

        // 如果存在则设置，不存在返回false，并且设置超时时间，类似于Setex
        Boolean aBoolean = redisTemplate.opsForValue().setIfPresent("bigkang", "lock1", 1L, TimeUnit.HOURS);

				// 查询Key的索引起始下标到结束，从0开始到3表示查询最前面的4个字符
        String str = redisTemplate.opsForValue().get("bigkang", 0, 3);

        // 对索引为9的下标开始插入，会覆盖索引为9的下标然后进行插入
        redisTemplate.opsForValue().set("bigkang", "qweasdzxc",9);
```



## List操作

```java
        // List操作都是采用opsForList,表示对List进行操作，会根据redisTemplate的泛型序列化
        ListOperations<Object, Object> objectObjectListOperations = redisTemplate.opsForList();

        // 向Key的左边插入一个1，返回当前List个数
        Long count = redisTemplate.opsForList().leftPush("bigkang", 3);
				// 向Key的左边插入多个元素，返回当前List个数
        Long count = redisTemplate.opsForList().leftPushAll("bigkang", 1, 2, 3);
       
				// 向Key的右边插入一个元素，返回当前List个数
        Long count = redisTemplate.opsForList().rightPush("bigkang", 1);
				// 向Key的右边插入多个元素，返回当前List个数
        Long count = redisTemplate.opsForList().rightPushAll("bigkang", 1, 2, 3);

        // 设置0索引为1这个值，表示最左边的元素变成1，会覆盖原来的值
        redisTemplate.opsForList().set("bigkang",0,1);

				// 从List左边吐出一个值
        Object left = redisTemplate.opsForList().leftPop("bigkang");
        // 从List右边吐出一个值
        Object right = redisTemplate.opsForList().rightPop("bigkang");

				// 从第一个Key的最右边吐出一个值，并且Push到另一个Key的最左边，返回这个值
        Object value = redisTemplate.opsForList().rightPopAndLeftPush("bigkang", "bigkang1");
        
			  // 根据索引获取value，0表示最左边的元素
        Object value = redisTemplate.opsForList().index("bigkang", 0);

        // 根据Key的List的索引下标，位置范围返回相应的List元素，0-3表示第一个到第四个元素返回4个，如果List只有3个元素则只会返回3个
        List<Object> values = redisTemplate.opsForList().range("bigkang", 0, 3);

				// 返回某个List的元素个数
        Long count = redisTemplate.opsForList().size("bigkang");

				// 从左边查询，找到1这个元素，并且在1的右边插入1.1,返回插入后的元素个数,类
				// 似于linsert <key> AFTER/BEFORE <value> <newvalue>
        Long count = redisTemplate.opsForList().rightPush("bigkang", 1, 1.1);
				// 从左边查询，找到1这个元素，并且在1的左边插入0.9,返回插入后的元素个数
        Long count = redisTemplate.opsForList().leftPush("bigkang", 1, 0.9);

				// 根据Key，从左边开始查询，查询3这个元素，删除2个值为3的元素
        redisTemplate.opsForList().remove("bigkang",2,3);

        // 根据Key，从左边开始查询，查询3这个元素，删除2个值为3的元素，返回成功删除的个数
        Long count = redisTemplate.opsForList().remove("bigkang", 2, 3);				
```

## Set操作

```java
        // Set操作都是采用opsForSet,表示对Set进行操作，会根据redisTemplate的泛型序列化
        SetOperations<Object, Object> objectObjectSetOperations = redisTemplate.opsForSet();

				// 向某个Key中的Set，添加3个元素
        Long bigkang = redisTemplate.opsForSet().add("bigkang", 1, 2, 3);

        // 获取某个Set中的所有成员
        Set<Object> values = redisTemplate.opsForSet().members("bigkang");

				// 返回Set中的元素个数
        Long count = redisTemplate.opsForSet().size("bigkang");
			
        // 判断Set中是否包含这个元素
        Boolean exists = redisTemplate.opsForSet().isMember("bigkang", 1);

				// 删除Set中的一个或者多个元素，返回删除的个数
        Long delCount = redisTemplate.opsForSet().remove("bigkang", 1,2,3);

				// 从Set中随机吐出一个元素，并且删除元素，返回Null表示没有元素
        Object value = redisTemplate.opsForSet().pop("bigkang");
        // 从Set中随机吐出3个元素，并且删除元素
        List<Object> values = redisTemplate.opsForSet().pop("bigkang", 3);

				// 从Set中随机获取一个元素，不删除元素
        Object value = redisTemplate.opsForSet().randomMember("bigkang");
        // 从Set中随机获取3个元素，不删除元素
        List<Object> values = redisTemplate.opsForSet().randomMembers("bigkang", 3);

			  // 获取两个Set的交集,K1有的元素，并且K2也有的元素
        Set<Object> values = redisTemplate.opsForSet().intersect("bigkang", "bigkang1");

				// 获取两个Set的差集,K1有的元素，K2没有的元素，K2有但是K1没有的，也就是和交集取反
        Set<Object> values = redisTemplate.opsForSet().difference("bigkang", "bigkang1");

				// 获取两个Set的合集，也就是将两个Set合并到一起，去除重复值
        Set<Object> values = redisTemplate.opsForSet().union("bigkang", "bigkang1");
```

## Hash操作

```java
				// Hash操作都是采用opsForHash,表示对Hash进行操作，会根据redisTemplate的泛型序列化
				HashOperations<Object, Object, Object> objectObjectObjectHashOperations = redisTemplate.opsForHash();

        // 向Hash添加一个属性，name，值为黄康
        redisTemplate.opsForHash().put("bigkang","name","黄康");

        // 删除Hash中的一个或者多个属性，返回删除成功的属性数量
        Long count = redisTemplate.opsForHash().delete("bigkang", "age");
	
        //  向Hash，PUT多个元素
        Map<String,Object> map = new HashMap<>();
        map.put("name","蔡徐坤");
        map.put("like","唱跳RAP打篮球");
        map.put("descr","鸡哥");
        redisTemplate.opsForHash().putAll("bigkang",map);

				// 获取Hash中的某一个属性
        Object value = redisTemplate.opsForHash().get("bigkang", "name");

				// 获取Hash中的多个属性
        List<Object> values = redisTemplate.opsForHash().multiGet("bigkang", Arrays.asList("name", "like", "descr"));

				// 判断某个属性是否存在
        Boolean exists = redisTemplate.opsForHash().hasKey("bigkang", "name");

				// 返回Hash所有的Key的名字
        Set<Object> keys = redisTemplate.opsForHash().keys("bigkang");

       // 返回Hash所有的Value
        List<Object> values = redisTemplate.opsForHash().values("bigkang");

				// 将Hash中的某个属性+1，然后返回添加后的Value
        Long value = redisTemplate.opsForHash().increment("bigkang", "age", 1);

        // 返回整个Hash的数据，以Map方式,java.util.LinkedHashMap
        Map<Object, Object> map = redisTemplate.opsForHash().entries("bigkang");
```

## Zset操作

```java
				// ZSet操作都是采用opsForZSet,表示对ZSet进行操作，会根据redisTemplate的泛型序列化
				ZSetOperations<Object, Object> objectObjectZSetOperations = redisTemplate.opsForZSet();

				// 向Zset添加一个a,分值为1.1，返回添加成功或者失败，已经存在则返回失败修改分值
        Boolean add = redisTemplate.opsForZSet().add("bigkang", "a", 0.9);

				// 创建一个Set，添加对象，以及分值
        Set<ZSetOperations.TypedTuple<Object>> tupleSet = new HashSet<>();
        tupleSet.add(new DefaultTypedTuple("a",1.0));
        tupleSet.add(new DefaultTypedTuple("b",2.0));
        tupleSet.add(new DefaultTypedTuple("c",3.0));
        // 返回Add的数量
        Long count = redisTemplate.opsForZSet().add("bigkang", tupleSet);

       	// 查询分值从小到大的元素，按索引查询，0到3表示正序的从第一个到第四个元素
        Set<Object> values = redisTemplate.opsForZSet().range("bigkang", 0, 3);
			  // 查询分值从大到小的元素，按索引查询，0到3表示倒序的从第一个到第四个元素
        Set<Object> values = redisTemplate.opsForZSet().reverseRange("bigkang", 0, 3);
				// 0到-1表示查询所有,倒序，range表示正序
        Set<Object> values = redisTemplate.opsForZSet().reverseRange("bigkang", 0, -1);
				
				// 根据Zset分数范围查询，查询0.1分数到2.0分数区间的数据并且返回分数值，正序
				Set<ZSetOperations.TypedTuple<Object>> values = redisTemplate.opsForZSet().rangeByScoreWithScores("bigkang", 0.1, 2.0);
				// 根据分值查询，不返回分值，只返回Value，同理range正序，reverseRange倒序
				Set<Object> values = redisTemplate.opsForZSet().rangeByScore("bigkang", 0.1, 2.0);


				// 给ZSet的某一个元素d，添加分值，添加分值为1，并且返回添加后的分值
        Double score = redisTemplate.opsForZSet().incrementScore("bigkang", "d", 1);
				
        // 删除ZSet中的一个或者多个元素，返回成功删除的元素个数
        Long count = redisTemplate.opsForZSet().remove("bigkang", Arrays.asList("a"));

				// 返回改元素的倒序排名，分数越高，值越小，最小为0，0+1则为排名第一的分数
        Long reverseRank = redisTemplate.opsForZSet().reverseRank("bigkang", "b");
        // Rank表示相反，分数越小则值越小
        Long rank = redisTemplate.opsForZSet().rank("bigkang", "b");
```

## Bit操作

```

```



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

## Redisson序列化问题

​		Redisson序列化默认采用org.redisson.codec.MarshallingCodec

​		那么我们如何修改这个序列化呢？

​		我们直接从配置文件中修改即可

​		序列化问题官网地址：[点击进入](https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96)

```properties
---------------------------------------------------------
# 原配置
---------------------------------------------------------

				nettyThreads: 32
        codec: !<org.redisson.codec.MarshallingCodec> {}
        transportMode: "NIO"
        
---------------------------------------------------------
# 修改后，我们将其修改为JsonJacksonCodec
---------------------------------------------------------

				nettyThreads: 32
        codec: !<org.redisson.codec.JsonJacksonCodec> {}
        transportMode: "NIO"
```

​		Redisson提供了很多种序列化的方法，找到Redisson包下面的org.redisson.codec，提供了很多种序列化方式

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

## 不使用SpringBoot整合

​		有时候我们不需要跟SpringBoot进行整合，直接通过工具类或者其他方式进行整合，那么我们采用如下

​		引入依赖

```xml
			<!-- SpringData依赖 -->
			<dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis</artifactId>
        <version>2.2.10.RELEASE</version>
        <scope>compile</scope>
        <exclusions>
          <exclusion>
            <artifactId>jcl-over-slf4j</artifactId>
            <groupId>org.slf4j</groupId>
          </exclusion>
        </exclusions>
      </dependency>
			<!-- lettuce连接池 -->
      <dependency>
        <groupId>io.lettuce</groupId>
        <artifactId>lettuce-core</artifactId>
        <version>5.2.2.RELEASE</version>
        <scope>compile</scope>
      </dependency>
			<!-- 连接池配置 -->
			<dependency>
				<groupId>org.apache.commons</groupId>
				<artifactId>commons-pool2</artifactId>
				<version>2.7.0</version>
			</dependency>
```

​		初始化连接

```java
    public static void main(String[] args) {
        // Redis单节点配置文件，集群RedisClusterConfiguration，等等可以查找实现RedisConfiguration的接口
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        // 配置Redis单机版本，数据库，地址，端口，以及密码
        redisStandaloneConfiguration.setDatabase(0);
        redisStandaloneConfiguration.setHostName("124.71.9.101");
        redisStandaloneConfiguration.setPort(16371);
        redisStandaloneConfiguration.setPassword("bigkang");
        // 连接池配置
        GenericObjectPoolConfig genericObjectPoolConfig =
                new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(8);
        genericObjectPoolConfig.setMinIdle(2);
        genericObjectPoolConfig.setMaxTotal(8);
        genericObjectPoolConfig.setMaxWaitMillis(2000);
        // Lettuce连接池配置
        LettucePoolingClientConfiguration build = LettucePoolingClientConfiguration.builder().poolConfig(genericObjectPoolConfig).build();
        // 创建Redis连接工厂
        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration,build);
        // 初始化连接
        redisConnectionFactory.afterPropertiesSet();
        RedisTemplate redisTemplate = new RedisTemplate();
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
        // 重新初始化
        redisTemplate.afterPropertiesSet();

        System.out.println(redisTemplate.keys("*"));
        // 关闭连接
        redisConnectionFactory.getConnection().close();
    }
```

