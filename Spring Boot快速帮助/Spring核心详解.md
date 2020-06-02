# spring-framework 5 版本

# Spring的核心有哪些

​			Spring的Core核心有：IOC容器，AOP切面，Events事件，Resources资源，i18n国际化，Validation校验，Data Binding数据绑定，Type Conversion类型转换，SpEL表达式。

# IOC容器

## IOC是什么？

​			控制反转（IOC）原理的Spring框架实现。IOC也称为依赖注入（DI），在此过程中，对象仅通过构造函数参数，工厂方法的参数或在构造或从工厂方法返回后在对象实例上设置的属性来定义其依赖项（即，与它们一起使用的其他对象） 。然后，容器在创建bean时注入那些依赖项。

​			控制反转：此过程从根本上讲是通过使用类的直接构造或诸如服务定位器模式之类的方法来控制其依赖项的实例化或位置的bean本身的逆过程（因此称为Control Inversion）控制反转。

​								核心：通过使用类的直接构造或者服务定位器初始化对象的实例化位置。

​								理解：好比容器为一个盒子，盒子里面放了许多的玩具，我们往里面放了一个变形金刚，这个变形金刚放在了盒子的某一个位置，我下一次需要拿这个变形金刚直接从盒子中指定的位置拿出来即可。例如我们初始化了一个Java对象，他一定存在于堆内存的某一个地方，我们将这个堆内存地址获取到，下次调用这个对象的时候直接从相应的堆内存地址中取出来即可，不需要自己重新初始化一遍，有效利用内存资源，并且帮助我们能够快速精准得地获取对象。

## IOC能干什么？

​			IOC的作用就是帮助我们管理Bean对象，我们在Java项目中会new各种不同的对象，类似于以前的Dao层对象，我们不需要每次一个请求都去new一次dao，相同的Dao我们只需要初始化一次，然后将它作为Bean对象放入IOC容器中，每次需要用到这个Bean对象的时候我们直接从里面拿，好比现在我们1000个请求，每次请求都去实例化一次Dao那么相当于多初始化了999个Dao，并且是会在内存中占用的，对我们的内存来说也是一个非常大的损耗。提高程序的灵活性、可扩展性和可维护性。

## 如何使用IOC？

### 使用XML进行加载

​			首先我们创建一个实体类用于构建bean对象，这里我们采用了Lombok的@Data注解快速生成Getter和Setter方法，感兴趣的朋友可以了解下，不过不想使用则自己添加Getter以及Setter方法。

```java
package com.cloud.demo.actuator.test.domain;

import lombok.Data;

@Data
public class BigKang {

    private String name;

    private Integer age;

    private String email;
}

```

首先我们在（此处演示采用SpringBoot，所以将xml放入resource即可）resource或者classpath下面创建一个applicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--我么初始化了一个bean对象，这个bean的名字叫做bigkang，他的类型是com.cloud.demo.actuator.test.domain.BigKang，并且他有三个属性，name、age、email，又分别初始化了值-->
    <bean id="bigkang" class="com.cloud.demo.actuator.test.domain.BigKang">
        <property name="name" value="黄康"/>
        <property name="age" value="19" />
        <property name="email" value="1360154205@qq.com"/>
    </bean>

</beans>
```

然后我们使用ClassPathXmlApplicationContext进行读取

```java
    @Test
    void contextLoads() {
      	// 解析Xml文件，将Xml中的bean对象加载到容器中
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      
      	// 从容器中的id获取对象，并且转换类型
        BigKang bigkang = (BigKang) context.getBean("bigkang");
      
      	// 打印对象
        System.out.println(bigkang);
    }
```

执行结果为：

```
BigKang(name=黄康, age=19, email=1360154205@qq.com)
```

我们在早期的SSM项目中大多数都采用XML进行对象的配置，但是XML的可维护性不强，后面在3.0开始Spring就支持使用Java代码来进行初始化了。

那么如果有多个xml文件则配置多个即可

```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml","customContext.xml");
```

### XML加载流程

我们知道xml所加载的应用上下文是ClassPathXmlApplicationContext

首先继承了AbstractXmlApplicationContext这个抽象的Xml应用上下文



### 使用Java代码进行加载

在Spring3.0的时候我们加载Bean的时候就方便多了我们可以通过类+注解的方式进行加载，我们可以通过@Configuration， @Bean，以及@Import和@DependsOn

那么我们先讲一下这些注解的作用吧

```java
@Configuration						// 用于标注这个类是一个配置类，并且他也会注册到容器中

@Bean											// 用于方法或者注解上，表示这是一个Bean对象

@Import										// 引入一个配置类，将引入的配置类的配置也加入到容器中

@DependsOn								// 初始化Bean的时候指定顺序，这里如果填入bean的名称，则填入的bean名称先加载完之后才会加载当前Bean
```

#### @Configuration

首先我们来测试下@Configuration注解

首先我们新建一个类叫做CustomConfig

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 1:58 PM
 * @Summarize 自定义配置
 */
@Configuration
public class CustomConfig {

    private String name = "bigkang";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

```

我们可以看到我们初始化了一个name属性为bigkang，并且提供了get和set方法访问，那么下面我们来读取吧。

```java
    public static void main(String[] args) {
        AnnotationConfigApplicationContext actx = new AnnotationConfigApplicationContext(CustomConfig.class);
        CustomConfig bigkang = (CustomConfig) actx.getBean("bigkang");
        System.out.println(bigkang.getName());
    }

```

#### @Bean

首先我们新建一个类，上面放入一个@Bean注解的方法

```java
import java.util.HashMap;
import java.util.Map;

public class CustomBean {

    @Bean(name = "map")
    public Map<String,Object> map(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","Bigkang");
        map.put("specialty","Play Games");
        return map;
    }
}
```

然后我们使用注解上下文加载，我们需要像上下文注册Bean然后进行刷新（如果不刷新则会抛出异常）

```java
    @Test
    public void testSpring(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(CustomBean.class);
        context.refresh();
        Map<String,Object> map = (Map) context.getBean("map");
        System.out.println(map);
    }
```

#### @Import

我们还是使用CustomBean但是我们不去引入他，我们使用一个CustomImport进行引入

```java
@Import({CustomBean.class})
public class CustomImport {

    public String a;

    public CustomImport(){
        a = "q";
    }

}
```

然后再次获取，我们会发现map以及import自己都被初始化并且管理到了容器中了

```java
   @Test
    public void testSpring(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(CustomImport.class);
        context.refresh();
        Map<String,Object> map = (Map) context.getBean("map");
        System.out.println(map);
        CustomImport bean = context.getBean(CustomImport.class);
        System.out.println(bean.a);
    }
```

## @DependsOn

DependsOn注解是用来加在初始化方法上的，下面我们初始化两个容器，分别为CustomA，以及CustomB

```java
public class CustomA {

    public CustomA(){
        System.out.println("初始化A");
    }

}

public class CustomB {

    public CustomB(){
        System.out.println("初始化B");
    }

}
```

然后我们使用一个CusotmBean来初始化他们

```java

public class CustomBean {

    @Bean
    public CustomA customA(){
        return new CustomA();
    }

    @Bean
    public CustomB customB(){
        return new CustomB();
    }

}
```

然后我们执行代码

![](https://img04.sogoucdn.com/app/a/100520146/b7d5fd8bfbbfc66a5612f50b98d9db8e)

我们发现A先打印然后再打印B那么我们想初始化的时候B先初始化，那么我们在B上加上注解DependsOn

我们将在A加载的时候等待customB先加载完，customB为Bean容器名称；

```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

public class CustomBean {



    @Bean
    @DependsOn(value = {"customB"})
    public CustomA customA(){
        return new CustomA();
    }

    @Bean(name = "customB")
    public CustomB customB(){
        return new CustomB();
    }

}
```

然后我们再次启动

![](http://yanxuan.nosdn.127.net/7d83106c22f49bafc2d1d20de1d31937.png)

这样就是A等待B先加载完再加载自己。



# AOP切面



# Event事件

## 什么是Event事件？

 官网解释：`ApplicationContext`通过`ApplicationEvent` 类和`ApplicationListener`接口提供中的事件处理。如果将实现`ApplicationListener`接口的Bean 部署到上下文中，则每次 将Bean `ApplicationEvent`发布到时`ApplicationContext`，都会通知该Bean。本质上，这是标准的Observer设计模式。

 概述：就是我们自己编写一个事件，并且将这个事件注册到Spring的Bean工厂中，然后我们通过发布这个事件然后来进行处理，

## 如何使用Event事件

 首先我们需要创建一个实体类，然后继承ApplicationEvent，如下示例：

```java
/**
 * @Author BigKang
 * @Date 2020/1/6 2:42 PM
 * @Summarize 自定义事件
 */
@Data
public class CustomEvent extends ApplicationEvent {

    /**
     * 消息，自定义字段，作为事件的参数
     */
    private String message;

    public CustomEvent(Object source,String message) {
        super(source);
        this.message = message;
    }
}
```

 然后我们需要编写一个处理监听器，我们可以看到我们将message拿出并且进行处理了。(推荐注解方式)

```java
/**
 * @Author BigKang
 * @Date 2020/1/6 9:45 AM
 * @Summarize 事件监听处理器
 */
@Slf4j
@Component
public class CustomEventListener implements ApplicationListener<CustomEvent> {

    @Override
    public void onApplicationEvent(CustomEvent customEvent) {
        log.info("收到来自事件驱动消息：{}", customEvent.getMessage());
    }

}

或者以注解方式进行监听
-------------------------------------------
/**
 * @Author BigKang
 * @Date 2020/1/6 9:45 AM
 * @Summarize 事件监听处理器
 */
@Slf4j
@Component
public class CustomEventListener {

    @EventListener
    public void customListener(CustomEvent customEvent){
        log.info("收到来自事件驱动消息：{}", customEvent.getMessage());
    }

}
```

 然后我们编写一个controller用于测试

```java
/**
 * @Author BigKang
 * @Date 2020/1/6 2:48 PM
 * @Summarize 测试Event事件驱动控制器
 */
@RestController
@RequestMapping("events")
public class EventTestController {

    /**
     * 注入ApplicationEvent事件发布器
     */
    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping("sendMessage")
    public String sendMessage(String message){
        // 发布事件驱动
        publisher.publishEvent(new CustomEvent(this,message));
        return "发送事件驱动成功！！！";
    }

}
```

然后我们用浏览器访问地址即可。如果需要使用异步，则使用[@Async](http://git.bigkang.club/Async)即可

```
http://localhost:8080/events/sendMessage?message=%E6%B5%8B%E8%AF%95%E5%8F%91%E9%80%81
```

# Resources资源

# i18n国际化

# Validation数据校验

# Data Binding数据绑定

# Type Conversion类型转换

# SpEL表达式