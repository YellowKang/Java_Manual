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

​		连接单节点带密码，使用Url方式

```java
        // redis:// (用户名为空，必须加冒号):密码@Host地址:端口号/数据库
				URI uri = new URI("redis://:bigkang@192.168.1.12:6379/0");
        Jedis jedis = new Jedis(uri);
```

​		集群链接

```java
				// 集群节点地址
        Set<HostAndPort> hosts = new HashSet<HostAndPort>();
        hosts.add(new HostAndPort("192.168.1.12", 6379));
        hosts.add(new HostAndPort("192.168.1.13", 6379));
        hosts.add(new HostAndPort("192.168.1.14", 6379));
        // 连接超时时间
        Integer connectionTimeout = 2000;
        // socket超时时间
        Integer soTimeout = 2000;
        // 最大尝试次数
        Integer maxAttempts = 5;
        // 密码
        String password = "bigkang";
        // 连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        JedisCluster jedisCluster = new 	JedisCluster(hosts,connectionTimeout,soTimeout,maxAttempts,password,poolConfig);
    
```



## 常用API

```java
        // 删除单个Key，返回个数
        Long del = jedis.del("bigkang");

        // 删除多个Key，返回个数
        Long del = jedis.del("bigkang", "bigkang2");

				// 判断某个Key是否存在,存在返回1，不存在返回0
				Long exists = jedis.exists("bigkang");

			  // 判断多个Key是否存在，返回存在的个数
        Long exists = jedis.exists("bigkang", "bigkang2");

				// 返回所有Key，以Set集合
        Set<String> keys = jedis.keys("*");

				// 获取Key的类型
        String type = jedis.type("bigkang");
```

​		

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

