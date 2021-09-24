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

​		并且我们的Banner也可以自定义属性，如下所示我们创建一个BaseApplication，启动类继承这个类，然后静态代码块初始化设置属性以及JVM信息。

```java
/**
 * @Author BigKang
 * @Date 2020/10/20 11:49 上午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize 父类Application应用
 */
public class BaseApplication {
    static {
        Runtime runtime = Runtime.getRuntime();
        System.setProperty("total.memory",String.valueOf(runtime.totalMemory()/1000L/1000L));
        System.setProperty("max.memory",String.valueOf(runtime.maxMemory()/1000L/1000L));
        System.setProperty("free.memory",String.valueOf(runtime.freeMemory()/1000L/1000L));
        System.setProperty("spring-cloud.version","Greenwich.SR3");
        System.setProperty("cloud-alibaba.version","2.1.1.RELEASE");
    }
}
```

​		启动类

```java
/**
 * @Author BigKang
 * @Date 2020/10/20 9:42 上午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 测试
 */
@SpringBootApplication
public class TestApplication extends BaseApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TestApplication.class);
        application.run(args);
    }
}
```

​		然后就是我们的Banner文件了

```properties
${AnsiColor.RED} ____  _       _  __
| __ )(_) __ _| |/ /__ _ _ __   __ _
|  _ \| |/ _` | ' // _` | '_ \ / _` |
| |_) | | (_| | . \ (_| | | | | (_| |
|____/|_|\__, |_|\_\__,_|_| |_|\__, |
         |___/                 |___/
${AnsiColor.BRIGHT_YELLOW}
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

${AnsiColor.BRIGHT_MAGENTA}
  Application版本: ${application.version}
  Application标题: ${application.title}
  Spring Boot版本: ${spring-boot.version}
 Spring Cloud版本: ${spring-cloud.version}
Cloud AliBaBa版本: ${cloud-alibaba.version}
   初始最小化堆内存: ${total.memory}MB
   初始最大化堆内存: ${max.memory}MB
   剩余可用系统内存: ${free.memory}MB
          JDK版本: ${java.version}
           OS系统: ${os.name}${os.version}
```

