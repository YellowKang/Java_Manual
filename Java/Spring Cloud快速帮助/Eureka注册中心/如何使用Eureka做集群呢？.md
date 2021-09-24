## 1、如何使用Eureka做集群呢？

	我们只需要创建两个Eureka服务然后我们让下面的服务注册的时候相互注册就可以了，首先我们来创建另一个Eureka

server:
  port: 8177
spring:
  application:
    name: Eureka-Register

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://127.0.0.1:8176/eureka/


	这里我们可以看到我们创建了一个Eureka，但是他的注册的端口并不是自己的
	
	那么我们再创建一个集群的注册中心


server:
  port: 8176
spring:
  application:
    name: Eureka-Register-colony1

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://127.0.0.1:8177/eureka/



	这里我们就很清楚的就能看到，我们使用了8177这个端口的Eureka注册中心然后我们注册到8176上面
	
	然后我们又创建了一个Eureka使用了8176然后注册到了8177上面这样我们的注册中心就做到了集群了



	然后我们去服务里面我们去给它设置下注册的地址


eureka:
  client:
    service-url:
      defaultZone:  http://127.0.0.1:8177/eureka/,ttp://127.0.0.1:8176/eureka/



	我们这里就能很清楚的看得到我们注册了两个注册中心，这样我们就做了一个简单的乞丐版Eureka集群了



























