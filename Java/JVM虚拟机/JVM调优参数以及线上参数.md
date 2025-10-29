# 引言

此处采用1.8.0_221版本的OracleJdk，虚拟机采用HotSpot

```
java version "1.8.0_221"
Java(TM) SE Runtime Environment (build 1.8.0_221-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.221-b11, mixed mode)
```

如果Jdk类型不一致或者版本老旧请使用如下命令查阅JDK参数

```properties
java -XX:+PrintFlagsInitial							 // 获取虚拟机的所有参数
java -XX:+PrintFlagsFinal -version			 // 获取所有参数并且打印版本
java -XX:+PrintCommandLineFlags -version // 查看JVM启动时默认内存以及默认垃圾收集器等等（重点）
```

# JDK调优诊断工具类

## jps

​		JVM进程状态工具(JVM Process Status Tool)，在目标系统上列出HotSpot Java虚拟机进程的描述信息

​		使用：

```shell
# 列举出当前系统运行的Java进程
jps
```

## jstat

​		JVM统计监控工具(JVM Statistics Monitoring Tool)，根据参数指定的方式收集和记录指定的jvm进程的性能统计信息。

​		查询jstat支持的命令

```shell
# 查询帮助命令
jstat -h

# 查询支持哪些类型指令
jstat -options
```

​		查看当前进程加载的类：

```shell
# 18662为查询出的Java进程Id
jstat -class 18662

# 返回结果
加载的数量  字节数					卸载的数量		 字节数     耗时（）
Loaded  	Bytes  				 Unloaded  		Bytes     Time
15130 		28495.5        0     				0.0       7.30
```

​		查看当前进程编译的类：

```shell
# 18662为查询出的Java进程Id
jstat -compiler 18662

#返回结果
已编译数量		 失败数量		无效数量		 耗时					失败类型			 	 失败方法
Compiled 		Failed 		Invalid   	Time   			FailedType 			FailedMethod
11188       0       	0     			2.97        0
```

​		查看当前进程gc情况

```shell
# 18662为查询出的Java进程Id
jstat -gc 18662

# 返回结果
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT    CGC    CGCT     GCT   
 0.0   1024.0  0.0   142.9  483328.0 252928.0  564224.0   322508.5  99888.0 93473.7 14464.0 11708.4   2084   13.860   0      0.000 1126    10.439   24.298


S0C：			第一个幸存区的大小
S1C：			第二个幸存区的大小
S0U：			第一个幸存区的使用大小
S1U：			第二个幸存区的使用大小
EC：				伊甸园区的大小
EU：				伊甸园区的使用大小
OC：				老年代大小
OU：				老年代使用大小
MC：				方法区大小
MU：				方法区使用大小
CCSC:			 压缩类空间大小
CCSU:			 压缩类空间使用大小
YGC：			年轻代垃圾回收次数
YGCT：			年轻代垃圾回收消耗时间
FGC：			老年代垃圾回收次数
FGCT：			老年代垃圾回收消耗时间
GCT：			垃圾回收消耗总时间
```

​		等等等等其他命令

## jmap

​		

```
jmap -histo 18662 > /test.log
```



# JVM信息定位

​		首先我们如果要对Java程序进行一个调优首先我们肯定要定位或者说找到这个Java程序才对，那么我们如何定位呢：

​		第一种方法，Jps定位：

```properties
# 查询当前所有的Java进程
jps   
```

​		第二种，进程查询：

```shell
# 查询当前和Java有关的进程
ps -ef | grep -v grep | grep java
```

## 定位对象

​		那么我们就能查询到当前的Java进程的Pid，然后使用jmap命令写入文件

```shell
jmap -histo 18662 > /test.log
```

​		然后我们查看前50行内容

```
head -n 100 test.log
```

​		大概就能看到如下内容了，下面就能查看到我们占用内存空间最多的对象了

```shell
	 序号			 实例数量			 实例字节		 实例的类名称
   num      #instances   #bytes     class name
----------------------------------------------
   1:       1575773      464835560  [B
   2:       1429847      207927808  [C
   3:         13298       29851904  [I
   4:       1021384       24513216  java.lang.String
   5:        117758       10362704  java.lang.reflect.Method
   6:        238262        9530480  java.util.LinkedHashMap$Entry
   7:         52902        6611848  [Ljava.util.HashMap$Node;
   
   例如[B表示byte字节数组，[C表示char数组
```

## 定位堆内存

​			然后我们再来定位我们的堆内存情况

```shell
jmap -heap 18662

# 返回结果
Attaching to process ID 5932, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.91-b15

using thread-local object allocation.
Parallel GC with 4 thread(s)

# 堆内存配置
Heap Configuration:
   MinHeapFreeRatio         = 0
   MaxHeapFreeRatio         = 100
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 42991616 (41.0MB)
   MaxNewSize               = 357564416 (341.0MB)
   OldSize                  = 87031808 (83.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

# 使用情况
Heap Usage:
# 年轻代（新生代）
PS Young Generation
# 伊甸园区
Eden Space:
   capacity = 60293120 (57.5MB)
   used     = 44166744 (42.120689392089844MB)
   free     = 16126376 (15.379310607910156MB)
   73.25337285580842% used
# 幸存0区
From Space:
   capacity = 5242880 (5.0MB)
   used     = 0 (0.0MB)
   free     = 5242880 (5.0MB)
   0.0% used
# 幸存1区
To Space:
   capacity = 14680064 (14.0MB)
   used     = 0 (0.0MB)
   free     = 14680064 (14.0MB)
   0.0% used
# 老人代（老年代）
PS Old Generation
   capacity = 120061952 (114.5MB)
   used     = 19805592 (18.888084411621094MB)
   free     = 100256360 (95.6119155883789MB)
   16.496143590935453% used

20342 interned Strings occupying 1863208 bytes.
```



# 堆

```properties
# 最小堆
-Xms<size> 						设置堆的最小值 		
​	例如		-Xms256m	将堆内存最大设置为256mb
​	注：		生产环境下，尽量设置-Xms和-Xmx是一样大的

# 最大堆
-Xmx<size> 						设置堆的最大值 
​	例如		-Xms256m	将堆内存最大值设置为256mb
​	注：		生产环境下，尽量设置-Xms和-Xmx是一样大的

# 新生代（年轻代）
-XX:NewSize=<size> 				设置新生代大小
​	例如		-XX:NewSize=1g  	将新生代大小设置为1个g
-XX:MaxNewSize=<size> 
```



# 常用调优参数

## 溢出打印dump

```properties
-XX:+HeapDumpOnOutOfMemoryError 																// 堆内存溢出时输出异常文件
-XX:HeapDumpPath=/Users/bigkang/Documents/test/image/jvm.dump		// 指定异常dump文件位置

# 一条龙参数
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./dump/jvm.dump
```

## GC日志存储

```sh
-Xloggc:./log/gc-%t.log																							# 打印GC日志到指定目录
-XX:+PrintGCDetails																									# 打印GC过程
-XX:+PrintGCDateStamps																							# 打印GC日期
-XX:+PrintGCTimeStamps																							# 打印GC时间
-XX:+PrintGCCause																										# 打印Gc原因										
-XX:+UseGCLogFileRotation																						# 使用Gc日志滚动
-XX:NumberOfGCLogFiles=10																						# Gc日志数量					
-XX:GCLogFileSize=100M																							# Gc日志大小（10 * 100） = 1000M = 1G


# 一条龙指定Gc参数
-Xloggc:./logs/gc-%t.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:+PrintGCCause -XX:GCLogFileSize=10M  -XX:NumberOfGCLogFiles=10


# 特别参数，打印堆内存GC详细信息
 -XX:+PrintHeapAtGC				
# 如下
Heap after GC invocations=5 (full 1):
 PSYoungGen      total 111616K, used 6669K [0x000000076ab00000, 0x0000000773c80000, 0x00000007c0000000)
  eden space 104448K, 0% used [0x000000076ab00000,0x000000076ab00000,0x0000000771100000)
  from space 7168K, 93% used [0x0000000773580000,0x0000000773c03620,0x0000000773c80000)
  to   space 10752K, 0% used [0x0000000772780000,0x0000000772780000,0x0000000773200000)
 ParOldGen       total 97280K, used 7370K [0x00000006c0000000, 0x00000006c5f00000, 0x000000076ab00000)
  object space 97280K, 7% used [0x00000006c0000000,0x00000006c0732a50,0x00000006c5f00000)
 Metaspace       used 22322K, capacity 23200K, committed 23296K, reserved 1069056K
  class space    used 3047K, capacity 3268K, committed 3328K, reserved 1048576K
}
```

​		在线分析Gc日志网站：[点击进入](https://gceasy.io/)



# 系统相关

## CPU相关

修改CPU使用数量ActiveProcessorCount

```
uintx   intx ActiveProcessorCount                      = -1   
```

默认值   -1  表示获取当前服务器的逻辑CPU个数，如2核4线程，则为 2 * 2 = 4 ，依此类推

设置CPU为5个逻辑CPU

```
-XX:ActiveProcessorCount=5
```

调用测试代码

```java
    public static void main(String[] args) {
        // 打印逻辑CPU个数
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
```

注意：就算设置为0也可以继续运行，此处指表示我们Java程序获取到的逻辑CPU个数，由于许多默认根据CPU个数来进行设置的参数。实际运行并不影响。



## GC相关

### 内存

​		当内存不够的时候并行收集器可以动态调整内存，默认4%，当我们的内存不够使用的时候则会将并行收集器的内存进行减少。

```
    uintx AdaptiveSizeDecrementScaleFactor          = 4     
```

设置

```
-XX:AdaptiveSizeDecrementScaleFactor=20%
```

------------------------------------------------------------------------------------------------

自适应的JVM  GC的时间衰减尺度，默认是10%

```
    uintx AdaptiveSizeMajorGCDecayTimeScale         = 10       
```

# JVM一条龙

## 内存

```
 `-Xms1g`                       | 初始堆大小，建议设置为与 `-Xmx` 相同以避免运行期扩容带来的抖动
 `-Xmx1.5g`                     | 最大堆大小，控制最大内存使用。留出 0.5G 给 Metaspace、线程栈、本地库等
 `-Xmn512m`                     | 年轻代大小（仅适用于 Parallel/Serial GC；G1GC 会自动调整，但仍可手动设置）
 `-Xss256k`                     | 每个线程的栈大小，适用于高并发时节省内存（默认一般为 1M，可调小）               
 `-XX:MaxDirectMemorySize=256m` | 限制 NIO/Netty 的直接内存使用上限（默认与 `-Xmx` 相同）


-Xms1g -Xmx1.5g -Xmn512m -Xss256k -XX:MaxDirectMemorySize=256m
```

## GC 

g1

```
 `-XX:+UseG1GC`                          | 启用 G1 垃圾回收器（推荐用于大于 512MB 的堆内存）  
 `-XX:MaxGCPauseMillis=200`              | 期望最大 GC 停顿时间，G1 会尽量满足（但不保证）
 `-XX:InitiatingHeapOccupancyPercent=45` | G1 GC 的混合回收触发阈值，默认 45%，可调整
 `-XX:ParallelGCThreads=2`               | GC 并行线程数，可根据 CPU 数量进行设置   
 `-XX:ConcGCThreads=2`                   | G1 GC 的并发标记线程数，推荐设置与上面相同   
 `-XX:+ExplicitGCInvokesConcurrent`      | `System.gc()` 变为 G1 的并发回收，避免 Full GC 

-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -XX:ParallelGCThreads=2 -XX:ConcGCThreads=2 -XX:+ExplicitGCInvokesConcurrent
```

cms

```
 `-XX:+UseConcMarkSweepGC`               | 启用 CMS 回收器（低延迟，适合老系统） 
 `-XX:+CMSClassUnloadingEnabled`         | CMS 回收器在回收时同时回收 Perm 区 
 `-XX:+UseCMSInitiatingOccupancyOnly`    | 启用自定义的触发阈值    
 `-XX:CMSInitiatingOccupancyFraction=70` | 老年代使用到 70% 时触发 CMS


-XX:+UseConcMarkSweepGC
-XX:+CMSClassUnloadingEnabled
-XX:+UseCMSInitiatingOccupancyOnly
-XX:CMSInitiatingOccupancyFraction=70
```

## JVM日志

```
-Xloggc:./log/gc-%t.log																							# 打印GC日志到指定目录
-XX:+PrintGCDetails																									# 打印GC过程
-XX:+PrintGCDateStamps																							# 打印GC日期
-XX:+PrintGCTimeStamps																							# 打印GC时间
-XX:+PrintGCCause																										# 打印Gc原因										
-XX:+UseGCLogFileRotation																						# 使用Gc日志滚动
-XX:NumberOfGCLogFiles=10																						# Gc日志数量					
-XX:GCLogFileSize=100M																							# Gc日志大小（10 * 100） = 1000M = 1G
-XX:+PrintHeapAtGC																									# 特别参数，打印堆内存GC详细信息				

-Xloggc:./logs/gc-%t.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:+PrintGCCause -XX:GCLogFileSize=10M  -XX:NumberOfGCLogFiles=10  -XX:+PrintHeapAtGC
```

## 性能监控与错误诊断

```
 `-XX:+HeapDumpOnOutOfMemoryError`      | OOM 时生成堆快照         
 `-XX:HeapDumpPath=/path/to/dump.hprof` | 堆快照保存路径               
 `-XX:+ExitOnOutOfMemoryError`          | OOM 发生时强制退出 JVM       
 `-XX:+CrashOnOutOfMemoryError`         | 触发 JVM 崩溃 dump，便于诊断   
 `-XX:+UnlockDiagnosticVMOptions`       | 解锁诊断类参数（如 CodeCache 使用） 
 `-XX:+PrintCodeCache`                  | 打印代码缓存区域使用情况      


-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/path/to/dump.hprof -XX:+ExitOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:+UnlockDiagnosticVMOptions -XX:+PrintCodeCache
```

## 容器环境相关

```
 `-XX:+UseContainerSupport`      | 使 JVM 感知容器内的资源限制（Java 8u191+ 默认开启）
 `-XX:MaxRAMPercentage=75.0`     | 堆大小为最大可用内存的百分比（替代 `-Xmx`，更灵活）  
 `-XX:InitialRAMPercentage=50.0` | 初始堆大小百分比                   
 `-XX:MinRAMPercentage=25.0`     | 最小堆大小百分比                     


-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:MinRAMPercentage=25.0
```

## 其他常用参数

```
 `-Dfile.encoding=UTF-8`         | 设置默认文件编码为 UTF-8   
 `-Duser.timezone=Asia/Shanghai` | 设置默认时区  
 `-Djava.awt.headless=true`      | 无头模式运行（用于服务器） 
 `-XX:+DisableExplicitGC`        | 忽略代码中调用的 `System.gc()` 

-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Djava.awt.headless=true -XX:+DisableExplicitGC
```

