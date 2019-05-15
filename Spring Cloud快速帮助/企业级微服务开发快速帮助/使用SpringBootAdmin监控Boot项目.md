# SpringBoot环境下

## 搭建Admin-Server

### 引入依赖

新建一个SpringBoot项目

```
    <properties>
        <java.version>1.8</java.version>
        <spring-boot-admin.version>2.1.4</spring-boot-admin.version>
    </properties>
	<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-server</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>de.codecentric</groupId>
                <artifactId>spring-boot-admin-dependencies</artifactId>
                <version>${spring-boot-admin.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

### 编写配置

yml格式

```
server:
  port: 9999
spring:
  security:
    user:
      name: admin
      password: admin
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: ALWAYS
```

properties格式

```
server.port=9999
spring.security.user.name=admin
spring.security.user.password=admin
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=ALWAYS
```

### 编写代码

首先编写security拦截器

新建类SecurityConfig

然后写入

```
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String adminContextPath;

    public SecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");

        http.authorizeRequests()
                .antMatchers(adminContextPath + "/assets/**").permitAll()
                .antMatchers(adminContextPath + "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and()
                .logout().logoutUrl(adminContextPath + "/logout").and()
                .httpBasic().and()
                .csrf().disable();
    }
}
```

然后我们在启动类上或者配置类上加上注解@EnableAdminServer

然后我们启动项目，访问

<http://localhost:9999/login> 就好了

## 搭建Admin-Client

### 引入依赖

```
    <properties>
        <java.version>1.8</java.version>
        <spring-boot-admin.version>2.1.4</spring-boot-admin.version>
    </properties>
	<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-client</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>de.codecentric</groupId>
                <artifactId>spring-boot-admin-dependencies</artifactId>
                <version>${spring-boot-admin.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

### 编写配置

yml格式

```
server:
  port: 8082
spring:
  application:
    name: Spring Boot Client
  boot:
    admin:
      client:
        url: http://localhost:9999
        username: admin
        password: admin
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: ALWAYS
```

properties格式

```
server.port=8082
spring.application.name=Spring Boot Client
spring.boot.admin.client.url=http://localhost:9991
spring.boot.admin.client.username=admin
spring.boot.admin.client.password=admin
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=ALWAYS
```

# SpringCloud环境下