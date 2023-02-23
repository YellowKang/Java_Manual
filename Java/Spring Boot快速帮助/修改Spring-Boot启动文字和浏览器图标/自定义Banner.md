# 自定义

​			我们只需要在resource目录下新增一个文件即可，文件名字叫做banner.txt，内容如下

```
 ____  _       _  __
| __ )(_) __ _| |/ /__ _ _ __   __ _
|  _ \| |/ _` | ' // _` | '_ \ / _` |
| |_) | | (_| | . \ (_| | | | | (_| |
|____/|_|\__, |_|\_\__,_|_| |_|\__, |
         |___/                 |___/
    　へ　　　　　／|
 　　/ ＼　　　 ∠＿/
 　 /　│　　 ／　／             康
 　│　 Z ＿＜　／　　　 /`ヽ     哥
 　│　　康　　ヽ　　 　/　　〉    专
 　Y　　　　　  :　  　/　　/    属
 　ｲ●　､　●　　⊂⊃　〈　　/       镇
 　()　 へ　　　　|　　＼〈       楼
 　　>ｰ ､_　 ィ　 │   ／／       神
 　 / へ　　 /　ﾉ＜|   ＼＼      宠
 　 ヽ_ﾉ　　(_／　 ＼＿／／
 　＜__r￣￣`＜＿＿r＿＿／
```

# 在线生成Banner网址

​				我们可以根据字母在线生成Banner文字。

​				地址如下：http://www.network-science.de/ascii/

# Banner开关

​		我们的Banner也可以开启以及关闭

```java
   public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TestbootApplication.class);
        // 关闭Banner
        application.setBannerMode(Banner.Mode.OFF);
        // 控制台输出banner
        application.setBannerMode(Banner.Mode.CONSOLE);
        // 日志输出banner
        application.setBannerMode(Banner.Mode.LOG);
        application.run(args);
    }
```

# 自定义新增Banner属性

## 新建事件监听器

​		并且我们的Banner也可以自定义属性，如下所示我们创建一个BannerPropertiesEventListener，启动SpringBoot时注入这个监听器

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
                        System.setProperty(properties,String.valueOf(propertiesObj));
                    }
                }
            }
        }

        // 设置JVM信息
        Runtime runtime = Runtime.getRuntime();
        System.setProperty("total.memory",String.valueOf(runtime.totalMemory()/1000L/1000L));
        System.setProperty("max.memory",String.valueOf(runtime.maxMemory()/1000L/1000L));
        System.setProperty("free.memory",String.valueOf(runtime.freeMemory()/1000L/1000L));
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

## 修改启动类

```java
/**
 * @Author BigKang
 * @Date 2020/10/20 9:42 上午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 测试
 */
@SpringBootApplication
public class TestApplication extends BaseApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication application = new SpringApplication(TestApplication.class);
        BannerPropertiesEventListener bannerListener = new BannerPropertiesEventListener();
        application.addListeners(bannerListener);
        application.run(args);
    }
}
```

## 设置Banner文件

​		resources文件夹下新建banner.txt

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

## 修改Maven

​		设置Maven版本以及cloud版本

```xml
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>1.8</java.version>
        <spring.cloud.version>Greenwich.SR3</spring.cloud.version>
        <spring.cloud.alibaba.version>2.1.1.RELEASE</spring.cloud.alibaba.version>
    </properties>
```

​		然后刷新Maven应用

​		最后启动SpringBoot项目即可得到如下

## 修改配置文件

```properties
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  application:
    name: sigreal-xp
    env: ${spring.profiles.active}
    version: @version@
    cloudVersion: @spring.cloud.version@
    cloudAlibabaVersion: @spring.cloud.alibaba.version@
```

