

# 首先确保Sentinel开启

​		参考上面的安装文档

# 然后引入依赖

```
		<!-- 引入Feign接口 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <exclusions>
            	<!-- 排除hystrix -->
                <exclusion>
                        <groupId>org.springframework.cloud</groupId>
                        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
		<!-- 服务连接Eureka或者Consul或者Nacos -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
		<!-- sentinel -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-alibaba-sentinel</artifactId>
            <version>0.2.1.RELEASE</version>
        </dependency>   
   
   <!-- 依赖版本cloud和alibabacloud的依赖管理 -->
   <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

# 修改配置

properties格式

开启组件，并且设置dashboard地址进行监控

```
spring.application.name=Test-User-Server-Feign
spring.cloud.sentinel.enabled=true
spring.cloud.sentinel.transport.dashboard=localhost:9999
```

yml格式

```
spring:
  application:
    name: Test-User-Server-Feign
  cloud:
    sentinel:
      enabled: true
      transport:
        dashboard: localhost:9999
```

- `-Dsentinel.dashboard.auth.username=sentinel` 用于指定控制台的登录用户名为 `sentinel`；
- `-Dsentinel.dashboard.auth.password=123456` 用于指定控制台的登录密码为 `123456`；如果省略这两个参数，默认用户和密码均为 `sentinel`；
- `-Dserver.servlet.session.timeout=7200` 用于指定 Spring Boot 服务端 session 的过期时间，如 `7200` 表示 7200 秒；`60m` 表示 60 分钟，默认为 30 分钟；



```
实现UrlBlock
```

