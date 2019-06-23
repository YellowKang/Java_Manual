# 引入依赖

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```

# 编写配置

```
server:
  port: 8088
spring:
  security:
    user:
      name: bigkang
      password: bigkang
```

# 然后编写配置类

创建一个