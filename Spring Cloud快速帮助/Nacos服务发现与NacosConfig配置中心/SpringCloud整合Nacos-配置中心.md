# 采用SpringBoot2.1.X+Cloud-Greenwich版本+Nacos1.0.0版本

​	如果需要使用其他版本请参考

​	https://github.com/spring-cloud-incubator/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E

# Nacos添加配置文件

​	我们在配置管理中添加配置文件

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566812795551.png)



添加一个为test.properties的配置文件,并且选中配置文件格式为properties,文件内容为

```properties
test=黄康123
server.port=8888
```

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566812810079.png)





# 注意！nacos已经毕业（新版依赖）

​		新版本依赖如下

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.2.RELEASE</version>
        <relativePath/>
    </parent>
    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Hoxton.SR8</spring-cloud.version>
        <spring-cloud-alibaba.version>2.2.5.RELEASE</spring-cloud-alibaba.version>
    </properties>
    <dependencies>
        <!-- SpringWeb依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!-- SpringBoot测试依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Nacos配置中心 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!-- 定义SpringCloud版本依赖 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- 定义SpringCloudAlibaba版本依赖 -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```



# 添加依赖（未毕业版本）

我们添加springcloud的依赖和cloudalibaba的依赖

```xml
    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
        <spring-cloud-alibaba.version>0.9.0.RELEASE</spring-cloud-alibaba.version>
    </properties>
        <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
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

# 配置文件

！！！！！！！！！！！！！！！！注意！！！！！！！！！

这里我们不是在application.properties中编写，而是在bootstrap.properties中编写

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566812883693.png)

这里端口我们采用8999，服务名我们叫做test,然后编写nacos的注册地址，这个如果有多个使用逗号隔开

注意这里的spring.application.name这个名字就是你的配置文件开始的名字，他会根据你的项目名字查询这个配置，以及环境，如果是test那么就新建，test.properties,如果是mynacos就是mynacos.properties

```properties
server.port=8999
spring.application.name=test
spring.cloud.nacos.config.server-addr=127.0.0.1:8848
```

# 代码编写

随便编写一个能被访问的controller，我们使用@RefreshScope注解来刷新，然后获取test这个配置，默认为13

```java
@RestController
@RefreshScope
public class TestClient {

    @Value("${test:13}")
    public String test;

    @GetMapping("get")
    public String get(){
        return test;
    }

}
```

然后启动项目，我们会发现他的端口变了，变成了配置文件的8888,然后我们在来请求一下这个get接口发现他不是默认的13了，而是配置文件中的      黄康123



![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566812923998.png)



![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566812954568.png)

这个时候我们再去修改配置文件中的test，然后再请求就会发现他的配置文件是动态获取的了

# 多项目区分

​	如果我们有很多个项目同时都用到了nacos的服务发现。那么我们如何将他们区分开呢？在nacos中我们可以通过他的命名空间，也就是namespace进行隔离，我们首先先来创建namespace，我们点击命名空间然后点击新建，我们新建一个test命名空间他会自动的生成一串id

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566812989577.png)



我们在nacos中找到配置列表，然后点击test空间新建配置，为test.properties，分组也为test

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566813003204.png)

我们拿着这个id在配置文件中写入，我们这里又使用了namespace又使用了分组用来区分项目

```properties
spring.cloud.nacos.config.namespace=905ddece-e4ac-4564-b2aa-f4cf3d568318
spring.cloud.nacos.config.group=test
```

注意这个spring的项目名还是为test，

然后我们再启动项目就能使用test空间的test配置了

# 多环境区分

注：此处采用yml，在配置中心新建配置文件

项目名-prod.yml

```properties
spring:
    profiles:
      active: prod
```

就能访问了

# 使用Yml格式

将bootstrap.properties改为bootstrap.yml

然后在配置中更改

```properties
spring:
    application:
        name: user-test-server #服务名，项目名，用于区分项目
    cloud:
        nacos:
            config:
                file-extension: yml #配置文件格式，nacos中的后缀
                group:  #配置文件分组
                namespace:  #配置文件命名空间
                server-addr:  #配置中心地址
```

重点就是这里的file-extension，写成yml，默认为properties然后我们去nacos中新建



![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566813060405.png)

这样就可以了

# 配置文件概览



```properties
spring:
  application:
    name: TestCloud
  cloud:
    nacos:
      server-addr: 124.71.9.101:8848
      config:
        # 配置文件DataID前缀，引用${spring.application.name}
        prefix: ${spring.application.name}
        # Server地址,默认引用${spring.cloud.nacos.server-addr}
        server-addr: ${spring.cloud.nacos.server-addr}
        # nacos的DataID后缀
        file-extension: yml
        # 刷新配置是否开启（默认开启）
        refresh-enabled: true
        # 扩展配置,List可以配置多个
        extension-configs:
          -
            # 配置文件名（DataID）
            dataId: TestCloud-dev.yaml
            # 分组
            group: DEFAULT_GROUP
            # 是否刷新
            refresh: true
          -
            dataId: TestCloud-dev2.yaml
            group: DEFAULT_GROUP
            refresh: true
        # 共享配置，List可以配置多个
        shared-configs:
          -
            dataId: TestCloud-dev.yaml
            group: DEFAULT_GROUP
            refresh: true
          -
            dataId: TestCloud-dev2.yaml
            group: DEFAULT_GROUP
            refresh: true
```

# 动态Properties

​		编写配置类

```java
@Data
@Component
@ConfigurationProperties(prefix = "test")
public class TestProperties {

    private String name;

    private Integer age;
}
```

​		nacos写入如下yml，即可动态刷新

```
test:
  name: bigkang212121
  age: 15
```

