# SpringBoot是如何实现自动装配的呢



# SpringBoot的自动装配过程



# SpringBoot自动装配实现方式

​			SpringBoot有两种方式来进行实现自动装配，分别是注解驱动方式，以及接口编程方式。

## 注解驱动方式

首先我们编写一个config配置，给他加上@Configuration，（注：建议不要放在SpringBoot启动类统计包或者子包下，因为放在同级包或者子包下会自动扫描并且装配，如果放在包外并且不自定义引入的话是不会生效的）

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 1:58 PM
 * @Summarize 自定义配置
 */
@Configuration
public class CustomConfig {

    @Bean
    public Date date(){
        System.out.println("enable spting boot start");
        return new Date();
    }

}
```

然后我们编写注解引入配置类

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 1:57 PM
 * @Summarize 自定义自动装配注解
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时生鲜
@Target(ElementType.TYPE) // 作用在类上
@Documented // 生成Java文档
@Import(CustomConfig.class) // 注入自定义Selector
public @interface EnableCustom {

}
```

然后我们启动类加上注解，然后启动项目即可装配

```java
/**
 * @Author BigKang
 * @Date 2019/12/10 3:31 PM
 * @Summarize 
 */
@SpringBootApplication
@EnableCustom
public class BootActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootActuatorApplication.class, args);
    }

}

```

## 接口编程方式（推荐使用）

​			更加灵活动态，SpringBoot的启动注解@SpringBootApplication引入@EnableAutoConfiguration，而@EnableAutoConfiguration引入了AutoConfigurationImportSelector，AutoConfigurationImportSelector实现了DeferredImportSelector，而DeferredImportSelector又继承了ImportSelector。（感兴趣的同学可以点进源码观看）

首先我们编写一个config配置，不给他加上@Configuration

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 1:58 PM
 * @Summarize 自定义配置
 */
public class CustomConfig {

    @Bean
    public Date date(){
        System.out.println("enable spting boot start");
        return new Date();
    }

}
```

然后我们使用接口方式编写一个Selector

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 2:01 PM
 * @Summarize 自动装配Selector
 */
public class ElableCustomSelector implements ImportSelector {

    /**
     * 实现接口方法
     * 将自动装配的Class类返回
     * @param annotationMetadata
     * @return
     */
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{CustomConfig.class.getName()};
    }
    
}
```

然后我们编写一个注解

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 1:57 PM
 * @Summarize 自定义自动装配注解
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时生鲜
@Target(ElementType.TYPE) // 作用在类上
@Documented // 生成Java文档
@Import(ElableCustomSelector.class) // 注入自定义Selector
public @interface EnableCustom {

}
```

然后在SpringBoot启动类或者能被扫描到的config类中加上@EnableCustom，然后启动即可

```java
/**
 * @Author BigKang
 * @Date 2019/12/10 3:31 PM
 * @Summarize 
 */
@SpringBootApplication
@EnableCustom
public class BootActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootActuatorApplication.class, args);
    }

}
```



# SpringBoot条件装配

SpringBoot有两种条件装配方式也是分别为注解驱动方式和接口编程模式。

## 注解驱动方式

@Profile，此注解作用于启动环境进行装配，如只在dev环境下使用自动装配。例如我们的动态定时任务，如果我们加上了@Profile那么就表示只有在dev环境下启动，才自动装配定时任务。

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 6:04 PM
 * @Summarize 动态定时任务
 */
@Component
@Slf4j
@Profile({"dev"})
public class DynamicTask implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // 需要执行的操作，定时任务操作 -》 线程
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("{}动态打印定时任务",taskCron.getCron());
            }
        };

        // 触发器对象，重写下次触发时间方法
        Trigger trigger = new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                CronTrigger trigger = new CronTrigger(“0/1 * * * * ?”);
                Date nextExecDate = trigger.nextExecutionTime(triggerContext);
                return nextExecDate;
            }
        };

        // 添加新任务
        scheduledTaskRegistrar.addTriggerTask(runnable, trigger);
    }
}
```

## 接口编程方式（推荐使用）

@Conditional,下面我们使用Conditional的方式自己手写一个@Profile的注解

首先编写注解

```java

/**
 * @Author BigKang
 * @Date 2019/12/30 11:03 PM
 * @Summarize 自动装配条件注解
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时生鲜
@Target({ElementType.TYPE,ElementType.METHOD}) // 作用在类上
@Documented // 生成Java文档
@Conditional({CustomCondition.class}) // 条件为CustomCondition
public @interface CustomProfile {

    String[] value();

}
```

然后编写配置规则，这里的规则就是如果注解的值中包含dev，则进行自动装配。

```java

public class CustomCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Map<String, Object> annotationAttributes = annotatedTypeMetadata.getAnnotationAttributes(CustomProfile.class.getName());
        // 如果这个注解上带有dev，则进行装配
        String[] values = (String[]) annotationAttributes.get("value");
        for (String value : values) {
            if ("dev".equals(value)) {
                return true;
            }
        }
        return false;
    }
}
```

我们在方法上加入

```java
    @Bean
    @CustomProfile(value = {"dev"})
    public TaskCron taskCron(){
        TaskCron taskCron = new TaskCron();
        taskCron.setCron("0/1 * * * * ? ");
        return taskCron;
    }
```

如果我们参数中包含dev则自动装配不会生效。

# SpringBoot全自动装配

首先我们在resource目录下新建META-INF/spring.factories,然后创建自动装配类，自动装配注解实现参考：

​		一级标题第三个：SpringBoot自动装配实现方式（注解驱动方式），详情请参考。

```java

@Configuration
@EnableCustom
public class CustomEnableAutoConfiguration {
  
}
```

然后在META-INF/spring.factories中写入自动装配的类的全限定类名。

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.cloud.demo.annotation.CustomEnableAutoConfiguration
```

然后直接启动项目即可。

我们还可以获取配置文件中的配置

```java
@Data
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {

    private String name;

    private Integer age;

    private String email;

}
```

配置文件

```properties
custom:
  name: 黄康
  age: 19
  email: 1360154202@qq.com
```

然后我们在spring.factories中添加即可

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.cloud.demo.annotation.CustomEnableAutoConfiguration,\
com.cloud.demo.annotation.CustomProperties
```

# SpringBoot自动装配核心







# SpringBoot自定义Start



# SpringWebMVC配置自动装配坑

​			将参数解析器放入依赖包中，包中配置了一些WebMVC的自动装配，并且在spring.factories也指定了配置类，但是就是无法加载这个参数解析器，后面发现是由于继承的类的问题所导致的WebMvcConfigurationSupport当时我是继承了WebMvcConfigurationSupport这个类然后覆盖了addArgumentResolvers，但是发现将包引入后却没有自动装配上，那么到底是什么原因呢。

​			那是由于在WebMvcAutoConfiguration这个自动装配中，它添加了条件，如果我们自动装配了继承WebMvcConfigurationSupport的bean就给我们把这个自动装配的Bean给取消掉，那么如果解决这个问题呢，我们直接将WebMvcConfigurationSupport给替换为WebMvcConfigurer即可，并且实现WebMvcConfigurer的方法，因为WebMvcConfigurationSupport也是实现WebMvcConfigurer接口的方法的，我们直接实现即可，代码如下，

原代码：

```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 用户参数解析器
     */
    @Autowired
    private CurrentUserIdResolver currentUserIdResolver;

    /**
     * 新增用户参数解析器
     *
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserIdResolver);
    }

}
```

自动装配修改后：

```
package org.kang.cloud.common.web.mvc.config;

import org.kang.cloud.common.web.mvc.resolver.CurrentUserIdResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 用户参数解析器
     */
    @Autowired
    private CurrentUserIdResolver currentUserIdResolver;

    /**
     * 新增用户参数解析器
     *
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserIdResolver);
    }

}

```

