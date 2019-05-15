# 采用SpringBoot2.1.X+Cloud-Greenwich版本+Nacos1.0.0版本

​	如果需要使用其他版本请参考

​	https://github.com/spring-cloud-incubator/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E

# Nacos添加配置文件

​	我们在配置管理中添加配置文件

![](img\添加配置文件.png)



添加一个为test.properties的配置文件,并且选中配置文件格式为properties,文件内容为

```
test=黄康123
server.port=8888
```

![](img\配置文件配置.png)

# 添加依赖

我们添加springcloud的依赖和cloudalibaba的依赖

```
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

​		这里我们不是在application.properties中编写，而是在bootstrap.properties中编写

![](img\boostartp.properties.png)

这里端口我们采用8999，服务名我们叫做test,然后编写nacos的注册地址，这个如果有多个使用逗号隔开

注意这里的spring.application.name这个名字就是你的配置文件开始的名字，他会根据你的项目名字查询这个配置，以及环境，如果是test那么就新建，test.properties,如果是mynacos就是mynacos.properties

```
server.port=8999
spring.application.name=test
spring.cloud.nacos.config.server-addr=39.108.158.33:8848
```

# 代码编写

随便编写一个能被访问的controller，我们使用@RefreshScope注解来刷新，然后获取test这个配置，默认为13

```
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



![](img\serverport配置.png)



![](img\get接口测试.png)

这个时候我们再去修改配置文件中的test，然后再请求就会发现他的配置文件是动态获取的了

# 多项目区分

​	如果我们有很多个项目同时都用到了nacos的服务发现。那么我们如何将他们区分开呢？在nacos中我们可以通过他的命名空间，也就是namespace进行隔离，我们首先先来创建namespace，我们点击命名空间然后点击新建，我们新建一个test命名空间他会自动的生成一串id

![](img\namespace服务发现.png)



我们在nacos中找到配置列表，然后点击test空间新建配置，为test.properties，分组也为test

![](img\nacosnamespace环境config.png)

我们拿着这个id在配置文件中写入，我们这里又使用了namespace又使用了分组用来区分项目

```
spring.cloud.nacos.config.namespace=905ddece-e4ac-4564-b2aa-f4cf3d568318
spring.cloud.nacos.config.group=test
```

注意这个spring的项目名还是为test，

然后我们再启动项目就能使用test空间的test配置了

# 多环境区分

注：此处采用yml，在配置中心新建配置文件

项目名-prod.yml

```
spring:
    profiles:
      active: prod
```

就能访问了

# 使用Yml格式

将bootstrap.properties改为bootstrap.yml

然后在配置中更改

```
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



![](img\nacos配置yml.png)

这样就可以了