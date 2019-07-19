# 引入依赖

 使用springboot 整合整合时请自带boot环境

redission依赖

```
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.11.0</version>
        </dependency>
```

# 编写配置

## 集群版

Yaml版本

```
spring:
  redis:
    cluster:
      nodes: 39.108.158.33:16371,39.108.158.33:16372,39.108.158.33:16373,140.143.0.227:16371,140.143.0.227:16372,140.143.0.227:16373 #集群地址
    jedis:
      pool:
        min-idle: 4 #最小空闲连接数，默认0
        max-idle: 8 #最大空闲连接数，默认8
        max-wait: -1ms #连接池最大阻塞等待时间（使用负值表示没有限制），默认-1
        max-active: 10 #连接池最大连接数（使用负值表示没有限制）,默认8
    timeout: 5000ms #连接超时时间
```

properties版本

```
spring.redis.cluster.nodes=39.108.158.33:16371,39.108.158.33:16372,39.108.158.33:16373,140.143.0.227:16371,140.143.0.227:16372,140.143.0.227:16373 #集群地址
spring.redis.jedis.pool.min-idle=4 #最小空闲连接数，默认0
spring.redis.jedis.pool.max-idle=8 #最大空闲连接数，默认8
spring.redis.jedis.pool.max-wait=-1ms #连接池最大阻塞等待时间（使用负值表示没有限制），默认-1
spring.redis.jedis.pool.max-active=10 #连接池最大连接数（使用负值表示没有限制）,默认8
spring.redis.timeout=5000ms #连接超时时间

```

## 单机版本

yaml版本

```
spring:
  redis:
    port: 20177 #端口号
    password: topcom123 #密码
    database: 12 #使用数据库
    timeout: 5000ms #超时时间
    host: 127.0.0.1 #redis地址
```

properties版本

```
spring.redis.port=20177 #端口号
spring.redis.password=topcom123 #密码
spring.redis.database=12 #使用数据库
spring.redis.timeout=5000ms #超时时间
spring.redis.host=127.0.0.1 #redis地址

```

# 代码实现

编写一个controller

TestLockController

```java
@RestController
@RequestMapping("redission")
public class TestLockController {

    @Autowired
    private RedissonClient redisson;

    @GetMapping("lock")
    public String lock(@RequestParam String lock){
        //获取锁对象
        RLock lock1 = redisson.getLock(lock);
        //上锁，并且设置强制解锁时间，防止死锁
        lock1.lock(3,TimeUnit.SECONDS);
        System.out.println("上锁成功！");
        try {
            //睡眠一秒
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //解锁
        lock1.unlock();
        System.out.println("解锁成功！");
        return "redission锁实现";
    }
}
```

