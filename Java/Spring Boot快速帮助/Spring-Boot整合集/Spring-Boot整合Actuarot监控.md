# 如何去监控SpringBoot项目

```xml
首先我们先配置actuator监控的依赖

	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
```

# 启动项目

我们直接访问路径http://localhost:8080/actuator即可

我们会看到有一串Json,如下：

```
{
	_links: {
		self: {
			href: "http://localhost:8080/actuator",
			templated: false
		},
		health: {
			href: "http://localhost:8080/actuator/health",
			templated: false
		},
		health-path: {
			href: "http://localhost:8080/actuator/health/{*path}",
			templated: true
		},
		info: {
			href: "http://localhost:8080/actuator/info",
			templated: false
		}
	}
}
```

第一个self表示自己，href表示路径，templated表示是不是模板。

第二个health表示健康状态，项目启动并且运行中状态为UP

第三个health-path表示健康状态的路径，自定义的模板。

第四个info表示项目中配置的info的信息（属于自定义，可以在配置中添加）。

我们添加一些info测试下：

yaml配置文件中添加如下：

```properties
# 自定义Info信息
info:
  author: BigKang
  email: bigkangsix@qq.com
  qq: 1360154205
  blog: http://bigkang.club
```

properties配置文件中添加如下：

```properties
# 自定义Info信息
info.author=BigKang
info.email=bigkangsix@qq.com
info.qq=1360154205
info.blog=http://bigkang.club
```

然后我们访问http://localhost:8080/actuator/info

得到如下信息

```properties
{
	author: "BigKang",
	email: "bigkangsix@qq.com",
	qq: 1360154205,
	blog: "http://bigkang.club"
}
```

# 监控信息

我们可以看到这些都只是简单的一些信息，下面我们将更多更详细的信息进行监控。

我们在配置文件中添加：

yaml版本

```properties
management:
  endpoints:
    web:
      exposure:
        include: "*" #启用所有的监控
  endpoint:
    health:
      show-details: ALWAYS #显示细节 ： 总是，会将我们的磁盘信息进行监控
```

properties版本

```properties
management.endpoints.web.exposure.include=* #启用所有的监控
management.endpoint.health.show-details=ALWAYS #显示细节 ： 总是，会将我们的磁盘信息进行监控
```

然后我们再来进行访问http://localhost:8080/actuator/health就会出现如下数据：

### 磁盘监控

```properties
{
	status: "UP",
	components: {
		diskSpace: {
			status: "UP",
			details: {
				total: 121123069952,
				free: 37663899648,
				threshold: 10485760
			}
		},
		ping: {
			status: "UP"
		}
	}
}
```

status   		表示状态	UP为启动

diskSpace	表示磁盘	

​			total						总量（磁盘我这里mac为128G）

​			free						 剩余（剩余39G）

​			threshold   			阈值（如果free达到阈值则状态不正常）

### 监控Spring Bean容器

访问http://localhost:8080/actuator/beans即可查看所有的bean容器，数据示例如下

```

```



### 监控SpringBoot Task 定时任务

```
http://localhost:8080/actuator/scheduledtasks
```

我们直接访问：http://localhost:8080/actuator/scheduledtasks即可查看当前的定时任务有哪些

runnable表示运行的定时任务有哪些，哪个类的那个方法以及定时任务表达式

```
{
	cron: [
		{
			runnable: {
				target: "com.cloud.demo.actuator.task.TestPoolTask.testSynchronous"
			},
			expression: "0/1 * * * * ? "
		},
		{
			runnable: {
				target: "com.cloud.demo.actuator.task.TestPoolTask.testAsync"
			},
			expression: "0/1 * * * * ? "
		}
	],
	fixedDelay: [
	],
	fixedRate: [	
	],
	custom: [
	]
}
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