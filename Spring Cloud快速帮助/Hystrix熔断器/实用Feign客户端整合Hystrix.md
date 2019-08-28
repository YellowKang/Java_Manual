# 如何使用Feign来整合Hystrix呢？

	其实在Feign中已经默认的集成了Hystrix和Ribbon，我们只需要给他设置访问失败响应的熔断处理即可
	
	首先我们需要一个注册中心和一个服务，然后通过客户端去调用他（这里我们就不详细的演示了），默认采用轮询访问，我们轮询两个服务，挡轮训到第二个时我们将第二个停止即可演示熔断

# 我们先引入依赖


​	
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```


# 然后我们在配置信息里面配置


```properties
feign.hystrix.enabled=true意思是开启feign的hystrix组件
```
```properties
server:
  port: 80
spring:
  application:
    name: Test-User-Server-Feign
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:8177/eureka/,http://127.0.0.1:8176/eureka/
      management:
        endpoints:
          web:
            exposure:
              include:  '*'
feign:
  hystrix:
    enabled: true
```

# 然后我们来使用客户端调用服务,并且实现熔断


​	
​	
```java
我们这里调用Test-User-Server-One这个服务，然后访问/yo/hello，后面的fallback就是我们熔断器的关键
//Feign客户端连接，路径yo，映射服务的路径，发生错误后熔断器处理的类

@FeignClient(value = "Test-User-Server-One",path = "/yo",fallback = FallbackHystrixError.class)
public interface UserServerClient {
    //访问Test-User-Server-One，服务下面的/yo/hello，然后给客户端的controller调用
    @GetMapping("hello")
    String hello();
}
```

这里我们可以看到我们制定了一个访问失败的类这就是实现熔断器的关键，


```java
@Component
public class FallbackHystrixError implements UserServerClient {
    @Override
    public String hello() {
    System.out.pringln("Client Server Eroor");
        return "报错了啊康哥";
    }
}
```
首先我们先标注为组件，然后我们实现了刚刚调用服务的接口，然后方法不用改剩下的返回值和中间就是我们进行的操作，这样我们就实现了通过客户端去进行服务的熔断

还有启动类上面别忘了加注解哟

@EnableFeignClients
@EnableHystrix
还有其他的更详细的整合请参看官网