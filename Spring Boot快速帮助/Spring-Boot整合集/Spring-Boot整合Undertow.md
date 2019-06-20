# 引入依赖

​		我们springboot添加了web依赖了，我们直接从web依赖排除掉tomcat并且使用undertow

```
<dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
       <!-- 依赖排除Tomcat -->
       <exclusions> 
            <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
       </exclusions> 
</dependency>
    <dependency> 

        <groupId>org.springframework.boot</groupId>

        <artifactId>spring-boot-starter-undertow</artifactId>

    </dependency> 
```

