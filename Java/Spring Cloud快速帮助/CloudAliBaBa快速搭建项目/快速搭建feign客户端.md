# 依赖

实际依赖这个是在dependencies中添加的实际依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
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
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

```

版本依赖，这个是放在dependencyManagement中的，定义cloud和alibaba的版本

```
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
```

版本定义，控制版本，添加在properties中

```
 	<spring-cloud.version>Greenwich.SR1</spring-cloud.version>
  	<spring-cloud-alibaba.version>0.9.0.RELEASE</spring-cloud-alibaba.version>
```

# 配置

编写bootstrap.yml，不需要application.properties

```
spring:
    application:
        name: test-feign
    cloud:
        nacos:
            config:
                file-extension: yml
                group: test
                namespace: 905ddece-e4ac-4564-b2aa-f4cf3d568318
                server-addr: 39.108.158.33:8848
            discovery:
                namespace: 905ddece-e4ac-4564-b2aa-f4cf3d568318
                server-addr: 39.108.158.33:8848
        sentinel:
            enabled: true
            transport:
                dashboard: localhost:9999

```

注释版

```
spring:
    application:
        name: test-feign #服务名，项目名，用于区分项目
    cloud:
        nacos:
            config:
                file-extension: yml #配置文件格式，nacos中的后缀
                group: test #配置文件分组
                namespace: 905ddece-e4ac-4564-b2aa-f4cf3d568318 #配置文件命名空间
                server-addr: 39.108.158.33:8848 #配置中心地址
            discovery:
                namespace: 905ddece-e4ac-4564-b2aa-f4cf3d568318 #服务发现命名空间
                server-addr: 39.108.158.33:8848 #服务发现地址
        sentinel:
            enabled: true #是否启用
            transport:
                dashboard: localhost:9999 #监控中心地址
```

# 代码

## 注解

```
服务发现
@EnableDiscoveryClient

启用feign客户端
@EnableFeignClients

配置刷新
@RefreshScope

服务调用
@FeignClient(name = "test",path = "/config")
```

