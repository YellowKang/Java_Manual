# 什么是Akka？

​		Akka 是一个工具包和运行时，用于在 JVM 上构建高度并发、分布式和容错的事件驱动应用程序。Akka 可以与 Java 和 Scala 一起使用。

# Akka能做什么？

Akka 的以下特性可以让您以直观的方式解决困难的并发和可扩展性挑战：

- 事件驱动模型——Actor 执行工作以响应消息。Actors 之间的通信是异步的，允许 Actors 发送消息并继续自己的工作，而不会阻塞等待回复。
- 强隔离原则——与 Java 中的常规对象不同，Actor 没有可调用的公共 API。相反，它的公共 API 是通过参与者处理的消息定义的。这可以防止 Actors 之间的任何状态共享；观察另一个actor状态的唯一方法是向它发送一条请求它的消息。
- 位置透明度 - 系统从工厂构造 Actor 并返回对实例的引用。由于位置无关紧要，Actor 实例可以启动、停止、移动和重新启动以放大和缩小以及从意外故障中恢复。
- 轻量级——每个实例只消耗几百字节，这实际上允许在单个应用程序中存在数百万个并发 Actor。

# Akka的核心组件？

## Actor

​		**Actor**即角色，前面已经说过，**Akka Actors模型**将actors当做通用的并行计算原语，所以Actor是必不可少的。

​						Akka Actor的组织结构是一种树形结。

​						因为Actor是树形组织，所以Actor的路径类似于文件的路径。

​						每个Actor都有父级，有可能有子级当然也可能没有。

​						父级Actor给其子级Actor分配资源，任务，并管理其的生命状态（监管和监控）。

​						如果我们知道一个远程Actor的具体位置，那么我们就可以向他发送消息。

​		一个本地Actor的路径：akka://search-system/user/master

​		一个远程Actor的路径：akka.tcp://search-system@host.example.com:5678/user/master

## ActorSystem

​		**ActorSystem**即角色系统，为了统一的调度和管理系统中的众多**actors**，我们需要首先定义一个**ActorSystem**。

​		ActorSystem的主要功能有三个：

​						统一管理和调度actors，如：任务的拆分、处理等等。

​						配置Actor系统参数，如：拦截器、优先、路由策略等等。

​						日志功能：为了保证Akka的高容错机制，编程中尽量需要完善的日志记录，以便出错处理。



## ActorRef

​		**ActorRef**即角色引用，每个**Actor**有唯一的**ActorRef**，**Actor**引用可以看成是**Actor**的代理，与**Actor**打交道都需要通过**Actor**引用。



# Akka快速测试使用

​		下载包：[点击下载](https://example.lightbend.com/v1/download/akka-quickstart-java?name=akka-quickstart-java)

​		下载后解压，使用IDEA打开选择Maven工具或者Gradle

​		然后使用命令进行编译启动

```sh
Maven	:	mvn compile exec:exec
Gradle: gradle run
```

​		然后可以看到打印如下日志

```sh
[2021-09-13 16:23:29,889] [INFO] [akka.event.slf4j.Slf4jLogger] [helloakka-akka.actor.default-dispatcher-3] [] - Slf4jLogger started
>>> Press ENTER to exit <<<
[2021-09-13 16:23:29,922] [INFO] [com.example.Greeter] [helloakka-akka.actor.default-dispatcher-6] [akka://helloakka/user/greeter] - Hello Charles!
[2021-09-13 16:23:29,924] [INFO] [com.example.GreeterBot] [helloakka-akka.actor.default-dispatcher-3] [akka://helloakka/user/Charles] - Greeting 1 for Charles
[2021-09-13 16:23:29,924] [INFO] [com.example.Greeter] [helloakka-akka.actor.default-dispatcher-3] [akka://helloakka/user/greeter] - Hello Charles!
[2021-09-13 16:23:29,924] [INFO] [com.example.GreeterBot] [helloakka-akka.actor.default-dispatcher-6] [akka://helloakka/user/Charles] - Greeting 2 for Charles
[2021-09-13 16:23:29,925] [INFO] [com.example.Greeter] [helloakka-akka.actor.default-dispatcher-3] [akka://helloakka/user/greeter] - Hello Charles!
[2021-09-13 16:23:29,925] [INFO] [com.example.GreeterBot] [helloakka-akka.actor.default-dispatcher-6] [akka://helloakka/user/Charles] - Greeting 3 for Charles
```

​		再按一下回车即可退出

​		该示例由三个参与者组成：

- 问候语：接收对`Greet`某人的命令并`Greeted`以 确认问候已发生
- GreeterBot：接收来自 Greeter 的回复并发送一些额外的问候消息并收集回复，直到达到给定的最大消息数。
- GreeterMain：引导一切的守护演员