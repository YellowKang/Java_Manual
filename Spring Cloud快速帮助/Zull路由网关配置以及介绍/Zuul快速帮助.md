# 什么是Zuul？

	Zuul是网关，那什么又是网关呢？因为在我们的微服务中我们的服务是不对外的，也就是内网访问，那么如果别人要访问我们的服务怎么办呢，
	那么我们就需要提供一个网关，别人只能通过网关来访问我们的服务，我们可以利用网关来做限制操作，这极大地提高了我们的服务的安全性



# 配置文件配置

```
server:
  port: 8888
spring:
  application:
    name: Test-Cloud-Zuul
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:8177/eureka/,http://127.0.0.1:8176/eureka/

zuul:
#  给网关添加前缀否则不能访问http://localhost:8888/bigkang/user/yo/hello
   prefix: /bigkang
   routes:
     users:
#     我们要映射的路径
       path: /user/**
#       我们需要映射的服务名
       serviceId: test-user-server-one
     feign:
       path: /feign/**
       serviceId: test-user-server-feign
#    忽略服务的名字，不能通过服务名直接访问，也就是禁止直接通过服访问，不能以
#    http://localhost:8888/test-user-server-one/yo/hello是不能访问的，
#    我们只能http://localhost:8888/user/yo/hello来访问他
   ignored-services: '*'
```

