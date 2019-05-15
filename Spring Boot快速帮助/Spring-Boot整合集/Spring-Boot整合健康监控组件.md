## 1、什么是健康监控？

```
就是检查服务器的状态，如果发信它宕机了我们立即切换备份服务器，然后对这个服务器进行重启，也就是容灾的机制
```

## 2、如何去监控项目的健康状态？

```
首先我们先配置actuator监控的依赖

	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
```



```
然后再去配置信息配置他的访问端口和配置信息，也可以不配置信息，然后在当前启动的localhost:当前服务器端口号/actuator/health

management:	
  server:
	port: 8177
  endpoints:
	web:
	  base-path: /
	  
这样就能配置他的端口号为8177，访问路径为根路径
查看状态为  localhost:8177/health

	UP为启动
	
	Down是崩溃了
```







management.server.add-application-context-header = false＃在每个响应中添加“X-Application-Context”HTTP标头。
management.server.address =               		＃管理端点应绑定到的网络地址。需要自定义management.server.port。
management.server.port =                                ＃管理端点HTTP端口（默认情况下使用与应用程序相同的端口）。配置其他端口以使用特定于管理的SSL。
management.server.servlet.context-path = 		#Management endpoint context-path（例如，`/ management`）。需要自定义management.server.port。
management.server.ssl.ciphers=                       	＃支持的SSL密码。
management.server.ssl.client-auth =                   	＃是否需要客户端身份验证（“想要”）或需要（“需要”）。需要信任存储。
management.server.ssl.enabled = true                  	＃是否启用SSL支持。
management.server.ssl.enabled-protocols =            	＃启用SSL协议。
management.server.ssl.key-alias =                    	＃标识密钥库中密钥的别名。
management.server.ssl.key-password =                 	＃用于访问密钥库中密钥的密码。
management.server.ssl.key-store =                    	＃保存SSL证书的密钥库的路径（通常是jks文件）。
management.server.ssl.key-store-password =          	＃用于访问密钥库的密码。
management.server.ssl.key-store-provider =         	＃密钥库的提供者。
management.server.ssl.key-store-type =            	＃密钥库的类型。
management.server.ssl.protocol = TLS                  	＃要使用的SSL协议。
management.server.ssl.trust-store =              	＃持有SSL证书的信任存储。
management.server.ssl.trust-store-password =      	＃用于访问信任库的密码。
management.server.ssl.trust-store-provider =         	＃信任存储的提供者。
management.server.ssl.trust-store-type =              	＃信任存储的类型。