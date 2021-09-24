# 什么是定时任务

​		在我们的项目中可能会出现一些需要不定期去进行操作的事情，比如清理垃圾数据，和过期数据，以及自动生成报表等等

# 如何使用定时任务？

​		在Springboot中已经默认的帮我们加入了定时任务，所以我们直接使用即可

​		首先我们需要标记为组件，然后启动定时任务组件

​		然后使用注解即可实现

​		

```java
//表示是一個Spring組件
@Component
//开启Task组件
@EnableScheduling
public class TestTask {

    //每隔多少毫秒进行一次定时任务  3000就是3秒
    @Scheduled(fixedRate = 3000)
    public void nicea(){
        System.out.println(new Date());
    }
}
```

还能使用另一种表达式来进行创建

```java
      //定时任务，每隔5秒钟进行一次定时任务如果直接写1 * * * * ？表示一分钟
      @Scheduled(cron = "*/5 * * * * ?")
      //开启异步
    	@Async
      public void nice(){
          System.out.println("1");
      }
```

下面是表达式的示例

```java
//    0 0 10，14，16 * * ？每天上午10点、下午两点、下午4点整触发
//    0 0/30 9-17 * * ? 每天朝九晚五内每隔半小时触发
//    0 15 10 ? * MON-FRI 周一至周五的上午10:15触发
//    0 0/5 * * * ?每5分钟触发
//    10 0/5 * * * ？每隔5分钟的第10秒触发(即10:00:10、10:05:10、10:10:10等)
//    30 * * * * ? 每半分钟触发
//    30 10 * * * ? 每小时的10分30秒触发
//    30 10 1 * * ? 每天1点10分30秒触发
//    30 10 1 20 * ? 每月20号1点10分30秒触发
//    30 10 1 20 10 ? * 每年10月20号1点10分30秒触发
//    30 10 1 20 10 ? 2011 2011年10月20号1点10分30秒触发
//    30 10 1 ? 10 * 2011 2011年10月每天1点10分30秒触发
//    30 10 1 ? 10 SUN 2011           2011年10月每周日1点10分30秒触发
//    15,30,45 * * * * ?  每15秒，30秒，45秒时触发
//    15-45 * * * * ? 15  到45秒内，每秒都触发
//    15/5 * * * * ?      每分钟的每15秒开始触发，每隔5秒触发一次
//    15-30/5 * * * * ?   每分钟的15秒到30秒之间开始触发，每隔5秒触发一次
//    0 0/3 * * * ?       每小时的第0分0秒开始，每三分钟触发一次
//    0 15 10 ? * MON-FRI 星期一到星期五的10点15分0秒触发
//    0 15 10 L * ?       每个月最后一天的10点15分0秒触发
//    0 15 10 LW * ?      每个月最后一个工作日的10点15分0秒触发
//    0 15 10 ? * 5L      每个月最后一个星期四的10点15分0秒触发
//    0 15 10 ? * 5#3     每个月第三周的星期四的10点15分0秒触发

MON  		星期一

TUE			星期二

WED			星期三

THU			星期四

FRI			星期五 

SAT			星期六
  
SUN 		星期日
  

```

# 定时任务线程池配置以及异步配置

引入Lombok依赖方便打印日志

```xml
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.0</version>
            <scope>provided</scope>
        </dependency>
```

### 创建配置文件

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author BigKang
 * @Date 2019/12/11 4:30 PM
 * @Summarize 定时任务配置
 */
@Configuration              // 配置类
@EnableAsync                // 开启异步
@EnableScheduling           // 开启定时任务
@Slf4j                      // 日志输出
public class SpringTaskConfig implements SchedulingConfigurer,AsyncConfigurer {

    // 定时任务线程数量
    @Value("${spring.task.pool.size:10}")
    private Integer size;

    // 定时任务线程名称前缀
    @Value("${spring.task.pool.prefix:TaskExecutor-}")
    private String prefix;

    // 等待超时秒数
    @Value("${spring.task.pool.seconds:600}")
    private Integer seconds;

    // 等待完成是否关闭
    @Value("${spring.task.pool.shutdown:true}")
    private Boolean shutdown;


    // 异步线程池核心线程数
    @Value("${spring.task.pool.async.core:10}")
    private Integer asyncCore;

    // 异步线程池最大线程数
    @Value("${spring.task.pool.async.max:20}")
    private Integer asyncMax;

    // 异步线程池阻塞队列长度
    @Value("${spring.task.pool.async.queue:1000}")
    private Integer asyncQueueSize;

    // 异步线程池活动秒数
    @Value("${spring.task.pool.async.seconds:600}")
    private Integer asyncSeconds;

    // 异步线程池线程名称前缀
    @Value("${spring.task.pool.async.prefix:AsyncTaskExecutor-}")
    private String asyncPrefix;




    /**
     * 定时任务使用的线程池
     * @return
     */
    @Bean(destroyMethod = "shutdown", name = "taskScheduler}")
    public ThreadPoolTaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 线程数量
        scheduler.setPoolSize(size);
        // 线程前缀
        scheduler.setThreadNamePrefix(prefix);
        // 活动秒数
        scheduler.setAwaitTerminationSeconds(seconds);
        // 执行完毕关闭
        scheduler.setWaitForTasksToCompleteOnShutdown(shutdown);
        return scheduler;
    }

    /**
     * 异步任务执行线程池
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor asyncExecutor() {
        // 线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(asyncCore);
        // 最大线程数
        executor.setMaxPoolSize(asyncMax);
        // 阻塞队列容量
        executor.setQueueCapacity(asyncQueueSize);
        // KeepAlive活动秒数
        executor.setKeepAliveSeconds(asyncSeconds);
        // 执行线程名称前缀
        executor.setThreadNamePrefix(asyncPrefix);
        // 拒绝策略（当最大线程池和阻塞队列饱满，多余的线程如何处理，当前设置为调用线程处理）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * 将定时任务线程池注册
     * @param scheduledTaskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler());
    }

    /**
     * 返回异步线程池
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

    /**
     * 配置异步异常处理器
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (message, method, params) -> {
            log.error("异步任务执行出现异常, 消息： {}, 方法： {}, 参数： {}", message, method, params);
        };
    }
}

```

### 使用配置文件

properties版本：

```
# 同步定时任务线程池配置
spring.task.pool.size=10																#定时任务线程数
spring.task.pool.prefix=TaskExecutor-										#定时任务线程前缀
spring.task.pool.seconds=600														#定时任务等待时间（秒）
spring.task.pool.shutdown=true													#执行后是否关闭

# 异步定时任务线程池配置
spring.task.pool.async.core=10													#异步核心线程数
spring.task.pool.async.max=20														#异步最大线程数
spring.task.pool.async.queue=1000												#异步最大阻塞队列数量
spring.task.pool.async.seconds=600											#活动秒数
spring.task.pool.async.prefix=AsyncTaskExecutor-				#异步线程名称前缀
```

yaml版本

```
spring:
    task:
        pool:
            async:
                core: 10 #异步核心线程数
                max: 20 #异步最大线程数
                prefix: AsyncTaskExecutor- #异步线程名称前缀
                queue: 1000 #异步最大阻塞队列数量
                seconds: 600 #活动秒数
            prefix: TaskExecutor- #定时任务线程前缀
            seconds: 600 #定时任务等待时间（秒）
            shutdown: true #执行后是否关闭
            size: 10 #定时任务线程数
```



### 测试

```java
@Component
@Slf4j
public class TestPoolTask {

    /**
     * 测试同步定时任务
     */
    @Scheduled(cron = "0/1 * * * * ? ") //每1秒执行一次
    public void testSynchronous() {
        log.info("1");
    }

    /**
     * 测试异步定时任务
     */
    @Scheduled(cron = "0/1 * * * * ? ") //每1秒执行一次
    @Async
    public void testAsync() {
        log.info("2");
    }
}
```

# 动态定时任务

## 创建定时任务对象

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 6:07 PM
 * @Summarize 定时任务表达式对象
 */
@Data
public class TaskCron {

    // 定时任务表达式
    private String cron;

}
```

## 创建配置类

初始化bean对象，并且将时间设置为1秒钟触发一次

```java
/**
 * @Author BigKang
 * @Date 2019/12/11 4:30 PM
 * @Summarize 定时任务配置
 */
@Configuration              // 配置类
@Slf4j                      // 日志输出
public class SpringTaskConfig {
    @Bean
    public TaskCron taskCron(){
        TaskCron taskCron = new TaskCron();
        taskCron.setCron("0/1 * * * * ? ");
        return taskCron;
    }
}
```

## 创建控制器修改定时任务时间间隔

我们通过controller控制器的请求，直接修改定时任务的cron表达式。

```java
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private TaskCron taskCron;

    @GetMapping("setCron")
    public String str(String cron){
        taskCron.setCron(cron);
        return taskCron.getCron();
    }
}
```

我们可以看到，我们通过rest接口修改了bean对象中的表达式，最后我们就来编写定时任务吧。

## 动态定时任务任务调度

```java
/**
 * @Author BigKang
 * @Date 2019/12/30 6:04 PM
 * @Summarize 动态定时任务
 */
@Component
@Slf4j
public class DynamicTask implements SchedulingConfigurer {

    @Autowired
    private TaskCron taskCron;

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
                CronTrigger trigger = new CronTrigger(taskCron.getCron());
                Date nextExecDate = trigger.nextExecutionTime(triggerContext);
                return nextExecDate;
            }
        };

        // 添加新任务
        scheduledTaskRegistrar.addTriggerTask(runnable, trigger);
    }


}

```

然后我们启动就能看到日志了

```java
2019-12-30 18:18:16.005  INFO 17572 --- [ TaskExecutor-2] c.cloud.demo.actuator.task.DynamicTask   : 0/1 * * * * ? 动态打印定时任务
2019-12-30 18:18:17.003  INFO 17572 --- [ TaskExecutor-1] c.cloud.demo.actuator.task.DynamicTask   : 0/1 * * * * ? 动态打印定时任务
2019-12-30 18:18:18.004  INFO 17572 --- [ TaskExecutor-3] c.cloud.demo.actuator.task.DynamicTask   : 0/1 * * * * ? 动态打印定时任务
```

然后我们请求rest接口，修改定时任务的时间间隔

例如将间隔修改为2秒

```
http://localhost:8080/test/setCron?cron=0/1 * * ? MON
```

请求以下以下接口即可动态执行定时任务

```

```

