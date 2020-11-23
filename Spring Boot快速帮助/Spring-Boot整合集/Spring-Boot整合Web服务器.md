

# 整合Tomcat

## 引入依赖

​		引入依赖，默认的web引入的就是Tomcat服务器

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

## 配置



# 整合Undertow

## 引入依赖

​		我们springboot添加了web依赖了，我们直接从web依赖排除掉tomcat并且使用undertow

```xml
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
<!-- 引入undertow依赖 -->
<dependency> 
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
</dependency> 
```

## 配置