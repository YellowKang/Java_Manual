# 采用SpringBoot2.1.X+Cloud-Greenwich版本+Nacos1.0.0版本

​	如果需要使用其他版本请参考

​	https://github.com/spring-cloud-incubator/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E

## Nacos安装搭建

[Nacos安装](http://bigkang.club/articles/2019/08/26/1566809544493.html)

## 注意！nacos已经毕业新版依赖为

这里采用nacos服务端1.1.0   + Springboot 2.1.6 + Springcloud Greenwich.SR2

依赖也变为com.alibaba.cloud

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-alibaba-dependencies -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>2.1.0.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

## 配置文件

这里端口我们采用8999，服务名我们叫做test,然后编写nacos的注册地址，这个如果有多个使用逗号隔开

```properties
server.port=8999
spring.application.name=test
spring.cloud.nacos.discovery.server-addr=127.0.0.1：8848
```

# 代码编写

我们只需要在启动类上加上一个注解就可以了，他就是Cloud的原生服务发现注解@EnableDiscoveryClient

```java
@EnableDiscoveryClient
@SpringBootApplication
public class NacosClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosClientApplication.class, args);
    }

}
```

然后启动项目，打开控制台如果发现有下面的示例那么就完成服务发现了

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566811842538.png)

# 多项目区分

​		如果我们有很多个项目同时都用到了nacos的服务发现。那么我们如何将他们区分开呢？在nacos中我们可以通过他的命名空间，也就是namespace进行隔离，我们首先先来创建namespace，我们点击命名空间然后点击新建，我们新建一个test命名空间他会自动的生成一串id

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566811860320.png)



​	我们拿着这个id在配置文件中写入

```properties
spring.cloud.nacos.discovery.namespace=905ddece-e4ac-4564-b2aa-f4cf3d568318
```

这里表示nacos的服务发现的namespace的id为905ddece-e4ac-4564-b2aa-f4cf3d568318,也就是将他注册到test命名空间中，我们启动项目再来查看，我们点击服务发现，会发现旁边多了一个test，我们点击test就能发现我们的服务了

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566811887709.png)

# 元数据

​				我们可以在服务注册时带上我们的元数据，metadata后面可以输入要附带的元数据,我们在获取服务的实例的时候就能获取到元数据的信息了，下面讲解如何获取服务实例以及信息。

properties版本

```
spring.cloud.nacos.discovery.metadata.name=BigKang
spring.cloud.nacos.discovery.metadata.email=bigkangsix@qq.com
spring.cloud.nacos.discovery.metadata.blog=www.bigkang.club
```

yaml版本

```
spring:
    cloud:
        nacos:
            discovery:
                metadata:
                  name: BigKang
                  email: bigkangsix@qq.com
                  blog: www.bigkang.club
```



# 获取服务实例

```
@Autowired
private DiscoverClient discoveryClient;

publiuc void main(){
		// 获取用户服务的信息
		discoveryClient.getInstances("user-server");
}
```

# Nacos核心功能

官网Java-API地址：https://nacos.io/zh-cn/docs/sdk.html

## 服务注册

### 服务注册流程以及源码解析

首先我们服务发现需要引入Pom文件

```xml
    		<dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```

而这个Pom文件又依赖了一个

```xml
     		<dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-nacos-discovery</artifactId>
        </dependency>
```

那么这个spring-cloud-alibaba-nacos-discovery就是我们的Nacos的源码了，我们知道SpringCloud是基于SpringBoot的，那么SpringBoot是有自动装配的所以我们直接查看自动装配的文件，在META-INF/spring.factories

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.alibaba.cloud.nacos.NacosDiscoveryAutoConfiguration,\
  com.alibaba.cloud.nacos.ribbon.RibbonNacosAutoConfiguration,\
  com.alibaba.cloud.nacos.endpoint.NacosDiscoveryEndpointAutoConfiguration,\
  com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientAutoConfiguration,\
  com.alibaba.cloud.nacos.discovery.configclient.NacosConfigServerAutoConfiguration
org.springframework.cloud.bootstrap.BootstrapConfiguration=\
  com.alibaba.cloud.nacos.discovery.configclient.NacosDiscoveryClientConfigServiceBootstrapConfiguration
```

我们可以看到自动装配中有这个几个类

```properties
NacosDiscoveryAutoConfiguration														
RibbonNacosAutoConfiguration															
NacosDiscoveryEndpointAutoConfiguration										
NacosDiscoveryClientAutoConfiguration											
NacosConfigServerAutoConfiguration
```

首先我们定位到NacosDiscoveryAutoConfiguration中，发现他继承了一个抽象方法，AbstractAutoServiceRegistration，这个抽象方法实现了ApplicationListener这个监听器，那么这个监听器就是帮助我们在Web服务器启动之后调用的一个事件，当触发这个事件会触发一个方法onApplicationEvent(E var1);

​		这个方法调用的自己的bind方法。

```java
   	public void onApplicationEvent(WebServerInitializedEvent event) {
        this.bind(event);
    }
```

​		然后调用了start方法启动这个线程

```java
    /** @deprecated */
    @Deprecated
    public void bind(WebServerInitializedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        if (!(context instanceof ConfigurableWebServerApplicationContext) || !"management".equals(((ConfigurableWebServerApplicationContext)context).getServerNamespace())) {
            this.port.compareAndSet(0, event.getWebServer().getPort());
            this.start();
        }
    }
```

在这个方法中有一个register方法，这个就是注册我们的服务的。

```java
  public void start() {
        if (!this.isEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Discovery Lifecycle disabled. Not starting");
            }

        } else {
            if (!this.running.get()) {
                this.context.publishEvent(new InstancePreRegisteredEvent(this, this.getRegistration()));
                this.register();
                if (this.shouldRegisterManagement()) {
                    this.registerManagement();
                }

                this.context.publishEvent(new InstanceRegisteredEvent(this, this.getConfiguration()));
                this.running.compareAndSet(false, true);
            }

        }
    }
```

然后我们查看这个register方法

```java
	@Override
	public void register(Registration registration) {

		if (StringUtils.isEmpty(registration.getServiceId())) {
			log.warn("No service to register for nacos client...");
			return;
		}

		String serviceId = registration.getServiceId();
		String group = nacosDiscoveryProperties.getGroup();

    // 获取实例
		Instance instance = getNacosInstanceFromRegistration(registration);

		try {
			namingService.registerInstance(serviceId, group, instance);
			log.info("nacos registry, {} {} {}:{} register finished", group, serviceId,
					instance.getIp(), instance.getPort());
		}
		catch (Exception e) {
			log.error("nacos registry, {} register failed...{},", serviceId,
					registration.toString(), e);
		}
	}
```



### 				

## 服务心跳

​				

## 服务同步

## Nacos CP  以及  AP





## 服务健康检查

​				