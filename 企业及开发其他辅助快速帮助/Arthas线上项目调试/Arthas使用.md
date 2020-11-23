# 首先我们需要现在Arthas的jar包

```
wget https://alibaba.github.io/arthas/arthas-boot.jar
```

# 然后我们启动这个jar包

```
java -jar arthas-boot.jar
```

​		启动后就会进入到命令窗口，他会自动扫描当前正在运行的jar包项目，然后我们根据查询出的项目选择id，输入然后回车，即可进入Arthas。

我们使用命令进行查看。

# 命令



```sh
# 查询命令
help
```



```
dashboard
thread
jvm
sysprop
sysenv
getstatic
mc


```

Arthas    https://blog.csdn.net/lsy0903/article/details/94184008



# 查找class信息

## 查找某个具体的类的信息

```
以及表达式查询
 sc -d org.kang.cloud.test.web.controller.*Controller
```

```
 sc -d org.kang.cloud.test.web.controller.TestController
```

查找某个类的具体方法和参数

```
sm org.kang.cloud.test.web.controller.TestController 方法名（可以指定查询方法名）
```

查看方法以及参数类型相应返回类型

```
sm -d org.kang.cloud.test.web.controller.TestController
```

查看构造方法

```
sm org.kang.cloud.test.web.controller.TestController <init>
```

结果如下

```java
 class-info        org.kang.cloud.test.web.controller.TestController     # 类的全限定类名                                                                
 code-source       file:/app.jar!/BOOT-INF/classes!/              			 # class所属目录                                                                        
 name              org.kang.cloud.test.web.controller.TestController   	                                                                   
 isInterface       false          																			 # 是否属于接口                                                                                                        
 isAnnotation      false          																			 # 是否属于注解                                                                                                                   
 isEnum            false             																		 # 是否属于枚举                                                                                                                
 isAnonymousClass  false   																							 # 是否属于匿名类                                                                                                               
 isArray           false           																			 # 是否属于数组                                                                                                    
 isLocalClass      false                                                                                                                  
 isMemberClass     false                                                                                                                  
 isPrimitive       false                                                                                                                  
 isSynthetic       false                                                                                                                  
 simple-name       TestController                                                                                                         
 modifier          public                                                                                                                 
 annotation        org.springframework.web.bind.annotation.RestController,org.springframework.web.bind.annotation.RequestMapping          
 interfaces                                                                                                                               
 super-class       +-java.lang.Object  																	# 父类                                                                                                   
 class-loader      +-org.springframework.boot.loader.LaunchedURLClassLoader@254989ff                                                      
                     +-sun.misc.Launcher$AppClassLoader@70dea4e                                                                           
                       +-sun.misc.Launcher$ExtClassLoader@d129271       # 类加载器                                                                 
 classLoaderHash   254989ff      																				# 类加载哈希                                                                                                         

Affect(row-cnt:1) cost in 12 ms.

```

## 查找接口以及实现类

和查看类一样

```
 sc javax.servlet.Filter
```

查看详细信息

```
 sc -d javax.servlet.Filter
```



# 反编译类

```
jad org.kang.cloud.test.web.controller.TestController
```

# 执行方法

执行静态的System.out.println("hello ognl");

```
ognl '@java.lang.System@out.println("hello ognl")'
```



# 系统属性

查看系统属性

```
sysprop
```

查看服务名（SpringCloud）

```
sysprop @appId
```

以及grep筛选

```
sysprop | grep spring
```

以及设置系统属性

```
sysprop bigkang test
```

以及系统环境

```
sysenv
```

# JVM属性

查看JVM的启动时间

```
 jvm | grep JVM-START-TIME
```

查看当前JVM数量信息

```
 LOADED-CLASS-COUNT                     15980				# 加载的类的计数器
 TOTAL-LOADED-CLASS-COUNT               15981				# 总加载的类的数量
 UNLOADED-CLASS-COUNT                   1						# null加载类数量
 PENDING-FINALIZE-COUNT                 0						# 等待释放的类的数量
 PROCESSORS-COUNT                       4						# 处理器核数
 COUNT                                  43					# 线程运行数量	
 DAEMON-COUNT                           30					# 守护线程数量
 PEAK-COUNT                             43					# 峰值（同时存活运行线程数）
 STARTED-COUNT                          372					# 启动线程总数
 DEADLOCK-COUNT                         0						# 死锁线程数	
 MAX-FILE-DESCRIPTOR-COUNT              65536				# 最大允许打开的文件数（和linux设置有关尤其是es安装时会出现这种问题）
 OPEN-FILE-DESCRIPTOR-COUNT             112					# 最大打开文件描述符数量
```

# 监控

执行后挂起，监控此方法执行的所有params以及抛出的异常信息

```
watch org.kang.cloud.test.web.controller.TestController * '{params, throwExp}'
```



```
   watch -b org.apache.commons.lang.StringUtils isBlank params     监控此方法调用前的所有参数                                                                       
   watch -f org.apache.commons.lang.StringUtils isBlank returnObj  监控执行后的返回结果                                                                       
   watch org.apache.commons.lang.StringUtils isBlank '{params, target, returnObj}' -x 2  
   																																	监控参数目标以及返回
   watch -bf *StringUtils isBlank params          								监控这个方法执行前后的参数                                                                                        
   watch *StringUtils isBlank params[0]                                                                                                   
   watch *StringUtils isBlank params[0] params[0].length==1                                                                               
   watch *StringUtils isBlank params '#cost>100'                                                                                          
   watch -E -b org\.apache\.commons\.lang\.StringUtils isBlank params[0]    
```

监控点示例

    params,params[0],'params[0]+params[1]','{params[0], target, returnObj}',returnObj,throwExp                                                                                 
                                                 target                                                                                   
                                                 clazz                                                                                    
                                                 method   
以及条件过滤

```
watch com.example.demo.arthas.user.UserController * returnObj 'params[0] > 100'
```

以及执行时间，只查看执行200毫秒以上的

```
watch com.example.demo.arthas.user.UserController * '{params, returnObj}' '#cost>200'
```



```
watch org.kang.cloud.test.web.controller.TestController primeFactors returnObj
```

# 动态修改Class

​	首先反编译，并且将文件写入系统

```
jad --source-only org.kang.cloud.test.web.controller.TestController > /tmp/TestController.java
```

然后退出Arthas

exit

然后修改class

```
vim /tmp/TestController.java
```

然后进入Arthas

```
java -jar arthas-boot.jar
```

然后查看这个classloader的哈希

```
sc -d org.kang.cloud.test.web.controller.TestController | grep classLoaderHash
```

重新通过内存编译这个classloader哈希

```
mc -c 254989ff /tmp/TestController.java -d /tmp
```

然后重新装载这个类

```
redefine /tmp/org/kang/cloud/test/web/controller/TestController.class
```

如果返回

```
redefine success, size: 1
```

则成功,然后执行修改过的代码即可发现修改完成

# 获取属性

## 获取静态属性

```
ognl -c 254989ff '@org.kang.cloud.test.web.controller.TestController@jwtUtil'
```



```
ognl -c 254989ff 'org.kang.cloud.test.web.controller.TestController.a()'

```



```
tt -t org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter invokeHandlerMethod
```



```
tt -i 1000 -w 'target.getApplicationContext().getBean("pathConfig").addPublicPath(@String[testasda])'
```

# 查看类加载器



```
 classloader -l
```



树状结构

```
classloader -t
```

# 线程

即可查看当前所有的线程，线程名字，所属GROUP，优先级，以及CPU使用情况等等，是否中断。以及是否守护线程

```
thread
```

查看具体线程信息

```
thread 34
```

查看统计CPU使用TOP的线程

查看前3

```
thread -n 3
```

查看是否有线程阻塞

```
thread -b
```

# Arthas

默认开启WEB端口http://127.0.0.1:8563/ 

可以通过端口直接访问web端