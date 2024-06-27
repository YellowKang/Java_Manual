# 环境框架描述

- ​				SpringBoot			->		3.1.5

- ​				MybatisPlus		  ->		3.5.4
- ​				Hutool				   ->		5.8.23
- ​				MySQL				  ->		5.7



​		使用如上配置完成SpringBoot3初始环境搭建快速构建业务系统

# Maven配置设置

```xml
    <properties>
        <java.version>17</java.version>
        <!-- 启动类 -->
        <start.class>com.sigreal.xp.external.XpExternalApplication</start.class>
        <!-- 打包默认环境 -->
        <app.profiles>dev</app.profiles>
        <!-- 指定jar包名 -->
        <finalName>app</finalName>

        <spring.cloud.version>NONE</spring.cloud.version>
        <spring.cloud.alibaba.version>NONE</spring.cloud.alibaba.version>

        <!-- MySQL、Mybatis Plus版本 -->
        <mysql.client.version>5.1.49</mysql.client.version>
        <mybatis.plus.version>3.5.4</mybatis.plus.version>

        <hutool.version>5.8.23</hutool.version>

    </properties>

    <!-- 镜像仓库 -->
    <repositories>
        <repository>
            <id>nexus-maven</id>
            <name>nexus-maven</name>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        </repository>
    </repositories>

    <!-- 版本依赖 -->
    <dependencyManagement>
        <dependencies>
            <!-- 接口文档版本定义 -->
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-dependencies</artifactId>
                <version>4.3.0-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- SpringBoot依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
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

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis.plus.version}</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.client.version}</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-core</artifactId>
            <version>${hutool.version}</version>
        </dependency>

    </dependencies>

    <build>
        <finalName>${finalName}</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <profiles>${app.profiles}</profiles>
                    <mainClass>${start.class}</mainClass>
                    <image>
                        <builder>paketobuildpacks/builder-jammy-base:latest</builder>
                        <!--执行构建任务的镜像，如果在当前环境不存在才会远程下载-->
                        <pullPolicy>IF_NOT_PRESENT</pullPolicy>
                    </image>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

# 配置文件初始化

## application.yaml

```properties
server:
  # 设置端口
  port: 8080
  servlet:
    context-path: /@artifactId@

# application工程配置
spring:
  application:
    # 设置工程名
    name: @artifactId@
    # 关联环境
    env: ${spring.profiles.active}
    # 关联版本
    version: @version@
    # 关联SpringCloud版本
    cloudVersion: @spring.cloud.version@
    # 关联SpringCloudAlibaba版本
    cloudAlibabaVersion: @spring.cloud.alibaba.version@
  profiles:
    # 引入环境变量，默认dev
    active: @app.profiles@

# 日志配置
log:
  info:
    path: logs/info # INFO日志路径
    history: 20 # 保留INFO日志天数
    maxsize: 10GB # INFO日志文件最大大小
    filesize: 70MB # 活动文件大小
    pattern: '%date [%thread] [%X{traceId}] %-5level [%logger{50}] %file:%line - %msg%n'
  error:
    path: logs/error # ERROR日志路径
    history: 20 # 保留ERROR日志天数
    maxsize: 10GB # ERROR日志文件最大大小
    filesize: 70MB # 活动文件大小
    pattern: '%date [%thread]  [%X{traceId}] %-5level [%logger{50}] %file:%line - %msg%n'
  pattern: '${CONSOLE_LOG_PATTERN:%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} [%X{traceId}] %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:%wEx}}'
  charset: UTF-8 # 编码格式

# knife4j文档增强
knife4j:
  enable: true
  setting:
    language: zh_cn
    swagger-model-name: EntityList
  basic:
    # 开启文档认证
    enable: true
    username: admin
    password: bigkang666
# API接口文档
springdoc:
  swagger-ui:
    path: /doc/swagger-ui.html
    tags-sorter: alpha
    operations-sorter: order
  api-docs:
    path: /v3/api-docs
    enabled: true
  group-configs:
    - group: 'defalt'
      paths-to-match: /api/**
      packages-to-scan:
        - com.sigreal.xp.external
  default-flat-param-object: true

# mybatis plus配置
mybatis-plus:
  global-config:
    db-config:
      # 逻辑删除字段
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  # mapper xml扫描路径
  mapper-locations:
    - "classpath*:/mapper/**/*.xml"
    - "classpath*:/**/mapper/xml/*.xml"
```

## application-dev.yaml

```properties
spring:
  datasource:
    url: jdbc:mysql://172.17.127.39:3906/sigreal_xp?characterEncoding=UTF-8&useSSL=false
    username: root
    password: "%4xi?xA5KIi6"
    driver-class-name: com.mysql.jdbc.Driver

# 是否开启文档
knife4j:
  enable: true
springdoc:
  api-docs:
    enabled: true
# MybatisLog日志打印
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## application-prod.yaml

```properties
spring:
  datasource:
    url: jdbc:mysql://171.220.232.24:31306/boot3-prod?characterEncoding=UTF-8&useSSL=false
    username: root
    password: "bigkang"
    driver-class-name: com.mysql.jdbc.Driver

# 是否开启文档
knife4j:
  enable: false
springdoc:
  api-docs:
    enabled: false
```

## application-staging.yaml

```properties
spring:
  datasource:
    url: jdbc:mysql://172.17.127.39:3906/sigreal_xp?characterEncoding=UTF-8&useSSL=false
    username: root
    password: "%4xi?xA5KIi6"
    driver-class-name: com.mysql.jdbc.Driver

# 是否开启文档
knife4j:
  enable: false
springdoc:
  api-docs:
    enabled: false
```

## application-test.yaml

```properties
spring:
  datasource:
    url: jdbc:mysql://171.220.232.24:31306/boot3-test?characterEncoding=UTF-8&useSSL=false
    username: root
    password: "bigkang"
    driver-class-name: com.mysql.jdbc.Driver

# 是否开启文档
knife4j:
  enable: true
springdoc:
  api-docs:
    enabled: true
```

## banner.txt

```properties
${AnsiColor.BRIGHT_CYAN}
______     _      __ _      _   __
| ___ \   (_)    / _` |    | | / /
| |_/ /    _    | (_| |    | |/ /       __ _     _ __       __ _
| ___ \   | |    \__, |    |    \      / _` |   | '_ \     / _` |
| |_/ /   | |     __/ |    | |\  \    | (_| |   | | | |   | (_| |
\____/    |_|    |___/     \_| \_/     \__,_|   |_| |_|    \__, |
                                                            __/ |
                                                           |___/
${AnsiColor.BRIGHT_YELLOW}
     /＼　　　 ／＼
 　　/ ＼　　　∠＿/
 　 /　 │　　／ ／                康
 　│　 Z ＿＜　／　　　  /`ヽ      哥
 　│　　　康　 ヽ　　 　/　 　〉    专
 　Y　　　　　   ヽ　  /　　/      属
 　ｲ ●　､　●　⊂⊃ |  〈　　/        镇
 　()　 へ　　　　|　　＼〈         楼
 　　>ｰ ､_　 ィ　 │   ／／         神
 　 / へ　　 /　ﾉ＜|  ＼＼         宠
 　 ヽ_ﾉ　　(_／　 ＼＿／／
 　＜__r￣￣`＜＿＿r＿＿／
${AnsiColor.BRIGHT_BLUE}
  Spring Boot版本: ${spring-boot.version}
 Spring Cloud版本: ${spring.application.cloudVersion}
Cloud AliBaBa版本: ${spring.application.cloudAlibabaVersion}
  Application名称: ${spring.application.name}
  Application环境: ${spring.application.env}
  Application版本: ${spring.application.version}
   初始最小化堆内存: ${total.memory}MB
   初始最大化堆内存: ${max.memory}MB
   剩余可用系统内存: ${free.memory}MB
          JDK版本: ${java.version}
           OS系统: ${os.name}${os.version}
```

## logback-spring.xml

```properties
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <!--从配置文件读取配置路径，并且添加默认值-->
    <!--日志全局设置-->
    <springProperty scope="context" name="log.charset" source="log.charset" defaultValue="UTF-8"/>


    <springProperty scope="context" name="log.pattern" source="log.pattern"
                    defaultValue="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

    <!--INFO日志设置-->
    <springProperty scope="context" name="info.path" source="log.info.path" defaultValue="logs/info"/>
    <springProperty scope="context" name="info.history" source="log.info.history" defaultValue="10"/>
    <springProperty scope="context" name="info.maxsize" source="log.info.maxsize" defaultValue="1GB"/>
    <springProperty scope="context" name="info.filesize" source="log.info.filesize" defaultValue="10MB"/>
    <springProperty scope="context" name="info.pattern" source="log.info.pattern"
                    defaultValue="%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n"/>


    <!--ERROR日志设置-->
    <springProperty scope="context" name="error.path" source="log.error.path" defaultValue="logs/error"/>
    <springProperty scope="context" name="error.history" source="log.error.history" defaultValue="10"/>
    <springProperty scope="context" name="error.maxsize" source="log.error.maxsize" defaultValue="1GB"/>
    <springProperty scope="context" name="error.filesize" source="log.error.filesize" defaultValue="10MB"/>
    <springProperty scope="context" name="error.pattern" source="log.error.pattern"
                    defaultValue="%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n"/>


    <!-- 彩色日志 -->
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter"/>
    <conversionRule conversionWord="wex"
                    converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"/>
    <conversionRule conversionWord="wEx"
                    converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"/>
    <!-- 定义彩色日志格式模板 -->


    <!--控制台日志打印格式-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
        </filter>
        <encoder>
            <pattern>${log.pattern}</pattern>
            <charset>${log.charset}</charset>
        </encoder>
    </appender>

    <!--INFO日志打印-->
    <appender name="FILE_INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--如果只是想要 Info 级别的日志，只是过滤 info 还是会输出 Error 日志，因为 Error 的级别高， 所以我们使用下面的策略，可以避免输出 Error 的日志-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!--过滤 Error-->
            <level>ERROR</level>
            <!--匹配到就禁止-->
            <onMatch>DENY</onMatch>
            <!--没有匹配到就允许-->
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <!--日志名称，如果没有File 属性，那么只会使用FileNamePattern的文件路径规则如果同时有<File>和<FileNamePattern>，那么当天日志是<File>，明天会自动把今天的日志改名为今天的日期。即，<File> 的日志都是当天的。-->
        <!--<File>logs/info.spring-boot-demo-logback.log</File>-->
        <!--滚动策略，按照时间滚动 TimeBasedRollingPolicy-->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--文件路径,定义了日志的切分方式——把每一天的日志归档到一个文件中,以防止日志填满整个磁盘空间-->
            <FileNamePattern>${info.path}/%d{yyyy-MM-dd}/info.%d{yyyy-MM-dd HH}.%i.log</FileNamePattern>
            <!--只保留最近10天的日志-->
            <maxHistory>${info.history}</maxHistory>
            <!--用来指定日志文件的上限大小，那么到了这个值，就会删除旧的日志-->
            <totalSizeCap>${info.maxsize}</totalSizeCap>
            <!-- maxFileSize:这是活动文件的大小，默认值是10MB,本篇设置为1KB，只是为了演示 -->
            <maxFileSize>${info.filesize}</maxFileSize>
        </rollingPolicy>
        <encoder>
            <pattern>${info.pattern}</pattern>
            <charset>${log.charset}</charset> <!-- 此处设置字符集 -->
        </encoder>
    </appender>
    <!--ERROR日志打印-->
    <appender name="FILE_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--如果只是想要 Error 级别的日志，那么需要过滤一下，默认是 info 级别的，ThresholdFilter-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>Error</level>
        </filter>
        <!--日志名称，如果没有File 属性，那么只会使用FileNamePattern的文件路径规则如果同时有<File>和<FileNamePattern>，那么当天日志是<File>，明天会自动把今天的日志改名为今天的日期。即，<File> 的日志都是当天的。-->
        <!--<File>logs/error.spring-boot-demo-logback.log</File>-->
        <!--滚动策略，按照时间滚动 TimeBasedRollingPolicy-->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--文件路径,定义了日志的切分方式——把每一天的日志归档到一个文件中,以防止日志填满整个磁盘空间-->
            <FileNamePattern>${error.path}/%d{yyyy-MM-dd}/error.%d{yyyy-MM-dd HH}.%i.log</FileNamePattern>
            <!--只保留最近90天的日志-->
            <maxHistory>${error.history}</maxHistory>
            <!--用来指定日志文件的上限大小，那么到了这个值，就会删除旧的日志-->
            <totalSizeCap>${error.maxsize}</totalSizeCap>
            <!-- maxFileSize:这是活动文件的大小，默认值是10MB,本篇设置为1KB，只是为了演示 -->
            <maxFileSize>${error.filesize}</maxFileSize>
        </rollingPolicy>
        <encoder>
            <pattern>${error.pattern}</pattern>
            <charset>${log.charset}</charset> <!-- 此处设置字符集 -->
        </encoder>
    </appender>


    <root level="info">
        <!--控制台输出-->
        <appender-ref ref="CONSOLE"/>

        <!--INFO日志输出-->
        <appender-ref ref="FILE_INFO"/>

        <!--ERROR日志输出-->
        <appender-ref ref="FILE_ERROR"/>
    </root>

</configuration>
```

# 代码初始化

## 配置文件（config）

### SpringUtil

```java

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HuangKang
 * @date 2022/10/27 9:42 AM
 * @describe Spring工具类
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (null == SpringUtil.applicationContext) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 获取应用上下文
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据Class获取Bean对象
     * @param classzz Class泛型
     * @return Bean对象
     * @param <T> 泛型类
     */
    public static <T> T getBean(Class<T> classzz) {
        return getApplicationContext().getBean(classzz);
    }

    /**
     * 根据Bean名称和Class获取Bean对象
     * @param beanName Bean名称
     * @param classzz classzz Class泛型
     * @return Bean对象
     * @param <T> 泛型类
     */
    public static <T> T getBean(String beanName, Class<T> classzz) {
        return getApplicationContext().getBean(beanName, classzz);
    }

    /**
     * 获取当前使用的环境
     * @return 定义使用的环境（dev，test，prod）
     */
    public static String getActiveEnv(){
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }


    /**
     * 是否包含环境
     * @param env 环境信息
     * @return 是否包含
     */
    public static Boolean hasActiveEnv(String env){
        return Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()).contains(env);
    }

    /**
     * 获取当前使用的环境
     * @return 定义使用的环境（dev，test，prod）等
     */
    public static List<String> getActiveEnvs(){
        return Arrays.stream(applicationContext.getEnvironment().getActiveProfiles()).collect(Collectors.toList());
    }
}

```

### UndertowFactoryCustomizer

```java

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * @author HuangKang
 * @date 2023/11/20 11:27:54
 * @describe Undertow工厂自定义设置
 */
@Configuration
public class UndertowFactoryCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });
    }
}
```

### MybatisPlusConfig

```java

import org.mybatis.spring.annotation.MapperScan;

/**
 * @author HuangKang
 * @date 2023/11/20 14:54:51
 * @describe MybatisPlus配置
 */
@MapperScan(basePackages = "com.sigreal.xp.external")
public class MybatisPlusConfig {

}
```

## 监听器（listener）

### BannerPropertiesEventListener

```java

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.List;

/**
 * @author HuangKang
 * @date 2022/10/17 11:33 AM
 * @describe Banner配置属性事件监听器（SpringBoot启动的时候初始化项目信息）
 */
public class BannerPropertiesEventListener implements GenericApplicationListener {

    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

    private static Class<?>[] EVENT_TYPES = {ApplicationStartingEvent.class, ApplicationEnvironmentPreparedEvent.class,
            ApplicationPreparedEvent.class, ContextClosedEvent.class, ApplicationFailedEvent.class};

    private static Class<?>[] SOURCE_TYPES = {SpringApplication.class, ApplicationContext.class};

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment envi = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            MutablePropertySources mps = envi.getPropertySources();
            // 获取配置文件配置源,注意版本不同SpringBoot可能配置源名称不一样，可以DEBUG查看名称
            PropertySource<?> ps = mps.get("configurationProperties");
            // 需要填充的属性
            List<String> propertiesList = Arrays.asList(
                    "spring.application.name",
                    "spring.application.env",
                    "spring.application.version",
                    "spring.application.cloudVersion",
                    "spring.application.cloudAlibabaVersion");
            for (String properties : propertiesList) {
                if (ps != null && ps.containsProperty(properties)) {
                    Object propertiesObj = ps.getProperty(properties);
                    if (propertiesObj != null && !propertiesObj.toString().trim().isEmpty()) {
                        System.setProperty(properties, String.valueOf(propertiesObj));
                    }
                }
            }
        }

        // 设置JVM信息
        Runtime runtime = Runtime.getRuntime();
        System.setProperty("total.memory", String.valueOf(runtime.totalMemory() / 1000L / 1000L));
        System.setProperty("max.memory", String.valueOf(runtime.maxMemory() / 1000L / 1000L));
        System.setProperty("free.memory", String.valueOf(runtime.freeMemory() / 1000L / 1000L));
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}
```

## 启动类

```java

import com.sigreal.xp.external.listener.BannerPropertiesEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XpExternalApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(XpExternalApplication.class);
        // 注册启动属性监听器
        BannerPropertiesEventListener bannerListener = new BannerPropertiesEventListener();
        application.addListeners(bannerListener);
        application.run(args);
    }

}
```

# 打包方式

## Jar

```bash
# maven打包并且指定环境
mvn clean -DskipTests=true -Dapp.profiles=prod package
```

## Native(打包Mybatis报错)

​		可能是集成的问题参考官方Mybatis + Native方式：https://github.com/mybatis/spring-native

```bash
# 设置环境变量，打包时用到了GRAALVM_HOME环境变量进行编译
export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-community-openjdk-17.0.9+9.1/Contents/Home

# 编译打包原生
mvn -Dapp.profiles=prod -DskipTests=true -Pnative native:compile
```

## Docker镜像

### Dockerfile

```bash
cat << EOF > Dockerfile
FROM openjdk:17-jdk
ENV APP_OPTS=""
ENV JAVA_OPTS="-Xms256m -Xmx1024m"
ARG JAR_FILE=target/app.jar
COPY ${JAR_FILE} app.jar
RUN echo "Asia/Shanghai" > /etc/timezone
EXPOSE 8080
ENTRYPOINT ["sh", "-c","java $JAVA_OPTS -jar app.jar $APP_OPTS"]
EOF
```

### 打包

```bash
# maven打包并且指定环境,打包Jar包,指定Jar包名
mvn clean -DfinalName=app -DskipTests=true -Dapp.profiles=prod package

# 指定镜像名以及版本
export javaAppImageName=boot-test:v1.0.0
# 构建镜像
docker build -t $javaAppImageName .
```





# 代码生成-EasyCode

## 宏定义

### init

```velocity
##初始化区域

##去掉表的t_和template_前缀
##去掉表的t_和template_前缀
#if($tableInfo.obj.name.startsWith("t_"))
  $!tableInfo.setName($tool.getClassName($tableInfo.obj.name.replaceFirst("t_","")))
#end
#if($tableInfo.obj.name.startsWith("template_"))
  $!tableInfo.setName($tool.getClassName($tableInfo.obj.name.replaceFirst("template_","")))
#end

##参考阿里巴巴开发手册，POJO 类中布尔类型的变量，都不要加 is 前缀，否则部分框架解析会引起序列化错误
#foreach($column in $tableInfo.fullColumn)
#if($column.name.startsWith("is") && $column.type.equals("java.lang.Boolean"))
    $!column.setName($tool.firstLowerCase($column.name.substring(2)))
#end
#end

##实现动态排除列
#set($temp = $tool.newHashSet("testCreateTime", "otherColumn"))
#foreach($item in $temp)
    #set($newList = $tool.newArrayList())
    #foreach($column in $tableInfo.fullColumn)
        #if($column.name!=$item)
            ##带有反回值的方法调用时使用$tool.call来消除返回值
            $tool.call($newList.add($column))
        #end
    #end
    ##重新保存
    $tableInfo.setFullColumn($newList)
#end

##对importList进行篡改
#set($temp = $tool.newHashSet())
#foreach($column in $tableInfo.fullColumn)
    #if(!$column.type.startsWith("java.lang."))
        ##带有反回值的方法调用时使用$tool.call来消除返回值
        $tool.call($temp.add($column.type))
    #end
#end
##覆盖
#set($importList = $temp)
```

### define

```velocity
##（Velocity宏定义）

##定义设置表名后缀的宏定义，调用方式：#setTableSuffix("Test")
#macro(setTableSuffix $suffix)
    #set($tableName = $!tool.append($tableInfo.name, $suffix))
#end

##定义设置包名后缀的宏定义，调用方式：#setPackageSuffix("Test")
#macro(setPackageSuffix $suffix)
#if($suffix!="")package #end#if($tableInfo.savePackageName!="")$!{tableInfo.savePackageName}.#{end}$!suffix;
#end

##定义直接保存路径与文件名简化的宏定义，调用方式：#save("/entity", ".java")
#macro(save $path $fileName)
    $!callback.setSavePath($tool.append($tableInfo.savePath, $path))
    $!callback.setFileName($tool.append($tableInfo.name, $fileName))
#end

##定义表注释的宏定义，调用方式：#tableComment("注释信息")
#macro(tableComment $desc)
/**
 * $!{tableInfo.comment}($!{tableInfo.name})
 * @Author $!author
 * @Time $!time.currTime()
 * @Summarize $tableInfo.obj.name $desc
 */
#end

##定义GET，SET方法的宏定义，调用方式：#getSetMethod($column)
#macro(getSetMethod $column)

    public $!{tool.getClsNameByFullName($column.type)} get$!{tool.firstUpperCase($column.name)}() {
        return $!{column.name};
    }

    public void set$!{tool.firstUpperCase($column.name)}($!{tool.getClsNameByFullName($column.type)} $!{column.name}) {
        this.$!{column.name} = $!{column.name};
    }
#end
```

### autoimport

```velocity
##自动导入包（仅导入实体属性需要的包，通常用于实体类）
#foreach($import in $importList)
import $!import;
#end
```

### mybatisSupport

```velocity
##针对Mybatis 进行支持，主要用于生成xml文件
#foreach($column in $tableInfo.fullColumn)
    ##储存列类型
    $tool.call($column.ext.put("sqlType", $tool.getField($column.obj.dataType, "typeName")))
    #if($tool.newHashSet("java.lang.String").contains($column.type))
        #set($jdbcType="VARCHAR")
    #elseif($tool.newHashSet("java.lang.Boolean", "boolean").contains($column.type))
        #set($jdbcType="BOOLEAN")
    #elseif($tool.newHashSet("java.lang.Byte", "byte").contains($column.type))
        #set($jdbcType="BYTE")
    #elseif($tool.newHashSet("java.lang.Integer", "int", "java.lang.Short", "short").contains($column.type))
        #set($jdbcType="INTEGER")
    #elseif($tool.newHashSet("java.lang.Long", "long").contains($column.type))
        #set($jdbcType="INTEGER")
    #elseif($tool.newHashSet("java.lang.Float", "float", "java.lang.Double", "double").contains($column.type))
        #set($jdbcType="NUMERIC")
    #elseif($tool.newHashSet("java.util.Date", "java.sql.Timestamp", "java.time.Instant", "java.time.LocalDateTime", "java.time.OffsetDateTime", "	java.time.ZonedDateTime").contains($column.type))
        #set($jdbcType="TIMESTAMP")
    #elseif($tool.newHashSet("java.sql.Date", "java.time.LocalDate").contains($column.type))
        #set($jdbcType="TIMESTAMP")
    #else
        ##其他类型
        #set($jdbcType="OTHER")
    #end
    $tool.call($column.ext.put("jdbcType", $jdbcType))
#end

##定义宏，查询所有列
#macro(allSqlColumn)#foreach($column in $tableInfo.fullColumn)$column.obj.name#if($velocityHasNext), #end#end#end
```

## 模板-Mybaits plus

### entity

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 保存文件（宏定义）
## 设置根据生成的包下，创建entity包
#save("/entity", ".java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为entity
#setPackageSuffix("entity")

## 注解标注表名
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
## 自动导入包（全局变量）
$!autoImport

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("实体类")

@Getter
@Setter
#if($tableInfo.comment)
@Schema(description = "$tableInfo.comment")
#end
## 使用注解，保存表名称
@TableName("`$tableInfo.obj.name`")
public class $!{tableInfo.name} extends Model<$!{tableInfo.name}>{

    #foreach($column in $tableInfo.pkColumn)
@TableId(type = IdType.AUTO)
    @Schema(description = "${column.comment}")
    private $!{tool.getClsNameByFullName($column.type)} $!{column.name};
    #end
    
## 循环遍历生成字段
#foreach($column in $tableInfo.otherColumn)
#if($column.name == "deleted")
    @TableLogic
#end
#if(${column.comment})
    @Schema(description = "${column.comment}")
#end
    @TableField("${column.obj.name}")
    private $!{tool.getClsNameByFullName($column.type)} $!{column.name};
    
#end
}
```

### mapper

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+Mapper，用于定义类名
#setTableSuffix("Mapper")

##保存文件（宏定义）
## 设置根据生成的包下，创建dao包，并且生成实体名+Dao.java文件
#save("/mapper", "Mapper.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为mapper
## 获取主键类型$!{tool.getClsNameByFullName($!tableInfo.pkColumn[0].type)}
#setPackageSuffix("mapper")

## 引入实体类
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import $!{tableInfo.savePackageName}.entity.$!tableInfo.name;

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("表数据库访问层")
## 类创建信息
public interface $!{tableName} extends BaseMapper<$!tableInfo.name> {

}
```

### mapper-xml

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init
## 保存文件（宏定义）
## 设置根据生成的包下，创建entity包
#save("/mapper/xml", "Mapper.xml")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为entity
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="$!{tableInfo.savePackageName}.mapper.$!{tableInfo.name}Mapper">


</mapper>
```

### service

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+Service，用于定义类名
#setTableSuffix("Service")

## 保存文件（宏定义）
## 设置根据生成的包下，创建dao包，并且生成实体名+Dao.java文件
#save("/service", "Service.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为service
#setPackageSuffix("service")

## ## 引入自定义Service层继承
## 引入实体类
import com.baomidou.mybatisplus.extension.service.IService;
import $!{tableInfo.savePackageName}.entity.$!tableInfo.name;

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("表服务接口")
## 类创建信息
public interface $!{tableName} extends IService<$!tableInfo.name>{

}
```

### serviceImpl

```velocity
## 导入宏定义
## 导入define以及init宏，导入方法定义的方法，以及表前缀去除等等
$!define
$!init

## 设置表后缀（宏定义）
## 设置$!{tableName}变量为实体类+ServiceImpl，用于定义类名
#setTableSuffix("ServiceImpl")

## 保存文件（宏定义）
## 设置根据生成的包下，创建service包，并且生成实体名+ServiceImpl.java文件
#save("/service/impl", "ServiceImpl.java")

## 包路径（宏定义）
## 设置包路径，为当前包结尾为service.impl
#setPackageSuffix("service.impl")

## 引入Dao层，以及实体类，以及Service层
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import $!{tableInfo.savePackageName}.mapper.$!{tableInfo.name}Mapper;
import $!{tableInfo.savePackageName}.entity.$!{tableInfo.name};
import $!{tableInfo.savePackageName}.service.$!{tableInfo.name}Service;
## 引入自定义ServiceImpl继承
## 引入Spring注解
import org.springframework.stereotype.Service;

## 表注释（宏定义）
## 调用define中的宏生成注释
#tableComment("表服务实现类")
## 类创建信息
@Service("$!tool.firstLowerCase($tableInfo.name)Service")
public class $!{tableName} extends ServiceImpl<$!{tableInfo.name}Mapper,$!{tableInfo.name}> implements $!{tableInfo.name}Service {

}
```



### 拓展

#### tool-service

#### tool-serviceimpl

#### dto

#### controller