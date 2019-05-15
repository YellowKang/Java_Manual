## 1、首先先引入依赖

```
	<!--整合Redis-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
    </dependency>
```

## 2、配置配置文件

```
  session:
	#设置你的Sessoin类型为Redis
	store-type: redis
  redis:
	#选择你的数据库索引为哪一个，默认0-15数据库，这里选择索引为1的数据库存入
	database: 1
	#ip地址，这里用的本机测试
	host: 127.0.0.1
	#Redis的密码
	password: 123
	#服务器的端口号，默认为6379
	port: 6379
	jedis:
	  pool:
		#最大连接数量
		max-idle: 8

		max-active: 8
		#最小连接数量
		min-idle: 0	
		#等待时间，无线等待
		max-wait: -1ms
	lettuce:
		#超时时间
	  shutdown-timeout: 100m
```

3、Session存入Redis
	然后在我们的Controller层存入Session
	

```
//Get请求
@GetMapping("/getAdmin")
public String getAdmin(Integer id, Map<String,List<Admin>> map, HttpSession httpSession) {

	//存入作用域
    map.put("admins",adminMapper.allAdmin());
    //存入Session，整合存入Redis
    httpSession.setAttribute("admin",adminMapper.allAdmin());
	
	//返回的视图
    return "/hello";
}

最后用我们的Redis连接工具查看是否有存入的Session就可以了
```





## &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Redis配置集合

SPRING SESSION REDIS（RedisSessionProperties）

spring.session.redis.cleanup-cron = 0 * * * * * 			#Cron 表达式用于过期的会话清理作业。
spring.session.redis.flush-mode = on-save 				#sessions flush mode。
spring.session.redis.namespace = spring：session     		＃用于存储会话的密钥的命名空间。



DATA REDIS

spring.data.redis.repositories.enabled = true      		＃是否启用Redis存储库。

REDIS（RedisProperties）

spring.redis.cluster.max -redirects =            			＃在群集中执行命令时要遵循的最大重定向数。
spring.redis.cluster.nodes =                             			＃逗号分隔的“host：port”对列表引导自。
spring.redis.database = 0                                			＃连接工厂使用的数据库索引。
spring.redis.url =                                					＃连接URL。覆盖主机，端口和密码。用户被忽略。		  

?								示例：redis：// user：password@example.com ：6379 
spring.redis.host = localhost                                			＃Redis服务器主机。
spring.redis.jedis.pool.max-active = 8                   		＃池在给定时间可以分配的最大连接数。使用负值无限制。
spring.redis.jedis.pool.max-idle = 8                       		＃池中“空闲”连接的最大数量。使用负值表示无限数量的空闲连接。
spring.redis.jedis.pool.max -wait = -1ms                    	＃在池耗尽时，在抛出异常之前连接分配应该阻塞的最长时间。使用负值无限期阻止。
spring.redis.jedis.pool.min-idle = 0                        		＃目标是池中维护的最小空闲连接数。此设置仅在其为正时才有效。
spring.redis.lettuce.pool.max-active = 8                    	＃池在给定时间可以分配的最大连接数。使用负值无限制。
spring.redis.lettuce.pool.max-idle = 8                        	＃池中“空闲”连接的最大数量。使用负值表示无限数量的空闲连接。
spring.redis.lettuce.pool.max -wait = -1ms                 	＃在池耗尽时，在抛出异常之前连接分配应阻塞的最长时间。使用负值无限期阻止。
spring.redis.lettuce.pool.min-idle = 0                     		＃目标是池中维护的最小空闲连接数。此设置仅在其为正时才有效。
spring.redis.lettuce.shutdown-timeout = 100ms       	＃关机超时。
spring.redis.password =                                			＃redis服务器的登录密码。
spring.redis.port = 6379                                			＃Redis服务器端口。
spring.redis.sentinel.master = 						#Redis服务器的名称。
spring.redis.sentinel.nodes =                                		＃逗号分隔的“host：port”对列表。
spring.redis.ssl = false                                				＃是否启用SSL支持。
spring.redis.timeout =                                				＃连接超时。

