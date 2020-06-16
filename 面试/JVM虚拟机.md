# **JVM**

### JMM的内存模型

​	JMM内存模型简单的将他分为5大块分别为

​		方法区

​		堆

​		栈

​			当我们调用一个方法时，我们可以将这个调用的方法的线程看为一个栈，这个线程会随着栈的运行完成而执行完毕，然后进行销毁，栈是一个数据结构先进后出，后进先出，栈中有栈底和栈顶

​		本地方法栈

​			本地方法栈是Java调用本地方法是所使用的，它是与底层的C语言或者CPU，或者操作系统进行的交互

​		程序计数器（PC寄存器）

​			程序计数器是一块较小的内存空间，可以看作是当前线程所执行的字节码的行号指示器。分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖这个计数器来完成。 

### JVM的启动方式

JVM有两种运行模式Server与Client。两种模式的区别在于

​	HotSpot 虚拟机默认使用Server模式启动

​	Client模式启动速度较快

​	Server模式启动较慢



​		但是启动进入稳定期长期运行之后Server模式的程序运行速度比Client要快很多。这是因为Server模式启动的JVM采用的是重量级的虚拟机，对程序采用了更多的优化；而Client模式启动的JVM采用的是轻量级的虚拟机。所以Server启动慢，但稳定后速度比Client远远要快。 

### JVM启动流程

​			Java的底层是采用C++实现的所以他会调用C++的代码，那么下面我们来梳理一下JVM的启动流程吧。

```
1、执行java命令，调用底层jvm.dll  Or  jvm.so类库
2、创建一个引导类加载器实例（C++实现）
3、C++调用Java代码创建JVM启动器，实例（sun.misc.Launcher），该类由引导类加载器负责创建其他类加载器
4、获取运行类自己的ClassLoader，launcher.getClassLoader().loadClass("XXX.XXX.XXX.Application")如Application.java
5、加载完成后C++发起调用，JVM执行Application类的main方法
6、JVM程序运行结束，JVM销毁
```



### Java类加载的过程

加载：

​		通过一个类的全限定名来获取定义此类的二进制字节流 ，将这个字节流所代表的静态存储结构转化为方

​		法区域的运行时数据结构。 在Java堆中生成一个代表这个类的java.lang.Class对象，作为方法区域数据

​		的访问入口 

验证：

​		验证阶段作用是保证Class文件的字节流包含的信息符合JVM规范，不会给JVM造成危害。如果验证失

​		败，就会抛出一个java.lang.VerifyError异常或其子类异常。验证过程分为四个阶段 准备：



​		 1.文件格式验证：验证字节流文件是否符合Class文件格式的规范，并且能被当前虚拟机正确的处理。 

​		 2.元数据验证：是对字节码描述的信息进行语义分析，以保证其描述的信息符合Java语言的规范。 

​		 3.字节码验证：主要是进行数据流和控制流的分析，保证被校验类的方法在运行时不会危害虚拟机。 

​		 4.符号引用验证：符号引用验证发生在虚拟机将符号引用转化为直接引用的时候，这个转化动作将在解

​						析阶段中发生。 

准备 :

​		准备阶段为变量分配内存并设置类变量的初始化。在这个阶段分配的仅为类的变量（static修饰的变

​		量），而不包括类的实例变量。对已非final的变量，JVM会将其设置成“零值”，而不是其赋值语句的值

 

​		 pirvate static int size = 12; 

​		 那么在这个阶段，size的值为0，而不是12。 final修饰的类变量将会赋值成真实的值。 

解析：

​		解析过程是将常量池内的符号引用替换成直接引用。

​		主要包括四种类型引用的解析。

​			类或接口的解析、

​			字段解析、

​			方法解析、

​			接口方法解析 

初始化 ：

​		在准备阶段，类变量已经经过一次初始化了，在这个阶段，则是根据程序员通过程序制定的计划去初始

​		化类的变量和其他资源。这些资源有static{}块，构造函数，父类的初始化等。 



​		 至于使用和卸载阶段阶段，这里不再过多说明，使用过程就是根据程序定义的行为执行，卸载由GC完

​		成。 

使用 :

​		新线程---程序计数器----jvm栈执行（对象引用）-----堆内存（直接引用）----方法区 

卸载:

​		 GC垃圾回收 

### 类加载器双亲委派模型的过程以及优势

加载器分别分为：

```
启动类(引导类)加载器 Bootstrap ClassLoader
			虚拟机的一部分，由c++实现。负责加载<JAVA_HOME>/lib下的类库	
扩展类加载器 Extension ClassLoader,
			sun.misc.Launcher$ExtClassLoader.负责加载<JAVA_HOME>/lib/ext下的类库
应用程序类加载器 Application ClassLoader 
			sun.misc.Launcher$AppClassLoader, 它是System.getClassLoader()的返回值，也称为系统类加载器。
自定义类加载器：
			负责加载用户类路径上所指定的类库。如果应用程序没有自定义过类加载器。
```

双亲委派流程：

```
	如果一个类加载器收到了类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委托给父类加载器去完成，每一个层次的类加载器都是如此，如果从应用层类加载器加载，那么他会将任务交给父类（扩展类加载器），扩展类加载器将任务交给启动类加载器，如果启动类加载器无法完成加载任务（找不到需要加载的类）那么他就会将任务递交给子类，如果启动类加载器加载不了则交给扩展类加载器，拓展类加载到了那么就直接返回结果，如果还是加载不到则继续往下，如果一直加载不了那么就会无法加载此类找不到类则会抛出异常java.lang.ClassNotFoundException。

	流程：
			例如查找类com.kang.Test
			
			应用类加载器（使我们启动的程序以及编写的代码所加载的）   -----》  交由拓展类加载器   -----》  交由启动类加载器
				
			-----》 如果启动类加载器有，则从启动类加载   -----》 如果没有拓展类加载器加载   -----》   如果还是没有交由应用类加载器加载 -----》 如果还是没有并且没有自定义类加载器则抛出异常java.lang.ClassNotFoundException。
```

如何测试哪些类是由哪个类加载器加载的呢，例如我们新建一个测试类

```java
public class TestJvm {

    public static void main(String[] args) {
        // String由启动类(引导类)加载器，由C++实现无法获取其类加载器
        System.out.println(String.class.getClassLoader());
        // 扩展类加载器负责加载<JAVA_HOME>/lib/ext下的类库所加载
        System.out.println(SunJCE.class.getClassLoader());
        // 应用程序类加载器所加载，也就是我们项目代码负责加载用户类路径上所指定的类库。如果应用程序没有自定义过类加载器
        System.out.println(TestJvm.class.getClassLoader());
    }

}
```

每一个类加载器也是有父加载器的存在的，例如拓展类加载器的父加载器是启动类(引导类)加载器，应用程序类加载器的父类是扩展类加载器

```java
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());
        System.out.println(SunJCE.class.getClassLoader().getParent());
        System.out.println(TestJvm.class.getClassLoader().getParent());
    }
```

那么这样做到底有什么好处呢？

```
1、沙箱安全机制，保护我们的基础类，如String等等JDK自带的类，防止篡改以及恶意篡改导致的系统Bug。
2、避免重复类加载，既保护安全，并且从父加载器进行加载，如果父类有直接从父类进行加载，如果没有则从现有的类加载器进行加载。
```



### 双亲委派机制可以不遵守么？

​			可以的，通过我们的自定义类加载器就能实现类加载器，我们通过自己的类加载器去进行加载，则可以打破双亲委派机制，例如非常常见Tomcat6的版本，它本身就采用了很多的自定义类加载器，并且打破了双亲委派机制，但是！！！（像java.lang.String这种类，在JVM中是受保护的，它具有沙箱安全机制，如果我们使用这个包名下相同的Class甚至引入了这样的包名，则会直接报错，并且我们通过AppClassLoader进行加载，AppClassLoader和自定义类加载器都去加载他的话，那么就会从AppClassLoader加载）

### G1

​	G1将新生代，老年代的物理空间划分取消了 ，取而代之的是，G1算法将堆划分为若干个区域（Region），它仍然属于分代收集器 。不过，这些区域的一部分包含新生代，新生代的垃圾收集依然采用暂停所有应用线程的方式，将存活对象拷贝到老年代或者Survivor空间。老年代也分成很多区域，G1收集器通过将对象从一个区域复制到另外一个区域，完成了清理工作。这就意味着，在正常的处理过程中，G1完成了堆的压缩（至少是部分堆的压缩），这样也就不会有cms内存碎片问题的存在了 

### Jvm启动的默认大小

​	Jvm启动的默认最小内存为当前系统内存的1/64，最大为1/4。

### JVM方法栈的工作过程，方法栈和本地方法栈有什么区别

​	方法栈：存储栈帧，支持Java方法的调用、执行和退出 

​	本地方法栈：作用是支撑Native方法的调用、执行和退出 

​	本地方法栈Native方法是基于系统还有底层的实现，它并不是调用的Java代码

### JVM的栈中引用如何和堆中的对象产生关联

​	栈中的栈帧用来存储内存地址，通过PC计数器进行引用，它类似于C中的指针，可以帮助栈引用到堆中的堆内存对象



类加载时加载了哪些信息到

### 对象里面存储了哪些数据？

​			在 Java 程序的运行过程中，会被创建出许许多多的对象，这些对象可能是我们自己写的一个类，也可能是 JDK 自带的一些类，那么这些被我们创建的对象他们都存储了一些什么呢？

​			那么我们知道的，我们可以通过这个对象获取他的类，并且获取他的属性，那么这一块他是如何存储的呢？下面就是Java对象所存储的东西：

```
1、Object Header（对象头）
2、Object Alignment Padding（对象对齐填充）
3、Object Properties（对象属性）
```



### 什么是Object Header对象头？

​			对象由对象头，对象对齐填充，对象属性所组成，对象头在整个对象的最前面，也就是头部，那么对象头其实也是由几部分进行组成的，分别是：

```
1、Mark Word（标记指令）
2、Klass Word（类）
3、数组信息（如果是数组的话则会有）
```

**Mark Word**

​			这部分主要用来存储对象自身的运行时数据，如Hashcode、GC分代年龄等。Mark Word的位长度为JVM的一个Word大小，也就是说32位JVM的Mark word为32位，64位JVM为64位，为了压缩这一块标记指令，Mark Word采用二进制存储，也就是01的机器码，我们使用工具jol-core进行查看发现如下：

```
(object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
(object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
```

这8个字节就是我们的Mark Word

**Klass Word**

​			用于

### JVM指针压缩



# GC垃圾回收



### 说一下逃逸分析技术

​		逃逸是指在某个方法之内创建的对象除了在方法体之内被引用之外，还在方法体之外被其它变量引用到；这样带来的后果是在该方法执行完毕之后，该方法中创建的对象将无法被GC回收。由于其被其它变量引用，由于无法回收，即称为逃逸。

​		逃逸分析技术可以分析出某个对象是否永远只在某个方法、线程的范围内，并没有“逃逸”出这个范围，逃逸分析的一个结果就是对于某些未逃逸对象可以直接在栈上分配提高对象分配回收效率，对象占用的空间会随栈帧的出栈而销毁。

​		总结：虽然他被引用之后导致了无法回收，但是它是随着栈的结束而结束掉的，而一个栈对应一个线程，这个线程一结束就会被销毁了

### GC的常见算法，CMS以及G1的垃圾回收过程，CMS的各个阶段哪两个是Stop the world的，CMS会不会产生碎片，G1的优势

​		

### 标记清除和标记整理算法的理解以及优缺点



​		标记清除：就是将要被回收的内存对象进行标记，然后统一的去清除，但是在这个过程中会产生大量的不连续的内存碎片，并且标记然后清除的效率不高

​		

​		标记整理：它是将标记的 内存进行先标记然后整理然后再清除，所以他不会产生内存碎片，但是他所耗费的资源更多

​		

### JVM如何判断一个对象是否该被GC，可以视为root的都有哪几种类型

​	是根据他的引用强度进行垃圾回收的，引用分为四种

​	强引用(Strong)

​			 就是我们平时使用的方式 A a = new A();强引用的对象是不会被回收的

​	软引用(Soft) 

​			在jvm要内存溢出(OOM)时，会回收软引用的对象，释放更多内存

​			是用来描述一些还有用但并非必需的对象。对于软引用关联着的对象，在系统将要发生内存溢出异

​			常之前，将会把这些对象列进回收范围之中进行第二次回收。 

​	弱引用(Weak) 

​			在下次GC时，弱引用的对象是一定会被回收的

​			 也是用来描述非必需对象的，但是它的强度比软引用更弱一些，被弱引用关联的对象只能生存到下

​			一次垃圾收集发生之前。当垃圾收集器工作时，无论当前内存是否足够，都会回收掉只被弱引用关

​			联的对象。 

​	虚引用(Phantom)

​			 对对象的存在时间没有任何影响，也无法引用对象实力，唯一的作用就是在该对象被回收时收到一

​			个系统通知

​	视为root的有

​			虚拟机栈(JVM stack)中引用的对象(准确的说是虚拟机栈中的栈帧(frames))  

​			方法区中类静态属性引用的对象  

​			本地方法栈(Native Stack)引用的对象 

### 什么情况下类会被回收掉

​		堆中不存在该类的任何实例

​		加载该类的classloader已经被回收

​		该类的java.lang.Class对象没有在任何地方被引用，也就是说无法通过反射再带访问该类的信息	

### 如何判断一个对象需要被回收

​		1、引用计数法

​			引用计数法思路是这样的，给对象添加一个引用计数器，有地方引用时，计数器就加1；当引用失效时就减1；当计数为0的时候就判定对象需要被回收

引用计数法有一个难以解决的问题就是相互循环引用问题。这样就会造成无法回收，并且JVM用的是可达性分析

​		2、可达性分析算法

​			这个算法的基本思路是通过一些列称为“GC Roots”的对象作为起始点，从这些点开始向下搜索，搜索走过的路径称为引用链，当一个对象到GC Roots没有任何引用链相连时，则证明对象需要被回收. 

​		被视为Roots的对象有

​			虚拟机栈中引用的对象

​			方法区中类静态属性引用的对象

​			方法区中常量引用的对象

​			本地方法栈中JNI引用的对象

### 强软弱虚引用的区别以及GC对他们执行怎样的操作

​		强引用一般在GC的时候都不会去回收他，所以强引用过多JVM就算抛出OOM异常也不会去回收他



​		对象是虚可达到对象 ，虚引用对象在那时或者在以后的某一时间，它会将该引用加入队列， 所以他其实只是会发出一个通知，但是他在某一个时间段还是会加入队列，所以在每次GC他都会被清理但是每次都又会回归队列

### System.gc()可以进行垃圾回收么？

​		System.gc()是一个Java提供的函数，看似他是进行Gc操作的，实际上它并不能进行Gc，因为Java并没有提供手动gc的任何方法，那么他的作用是什么呢？

​		System.gc()他只是帮助我们提醒一下虚拟机需要回收垃圾了，但是他并不会一定去执行垃圾回收的操作，只是起到了一个提醒的作用

下面这几个方法，上面两个都是提醒系统需要gc了，而下面两个方法则是进行gc这种方法本质上是不安全的。它可能导致在活动对象上调用终结器，而其他线程同时操作这些对象，从而导致不稳定的行为或死锁。并且这两个方法已经过时

```java
        System.gc();
        System.runFinalization();


        System.runFinalizersOnExit(true);
        Runtime.runFinalizersOnExit(true);
```



# JVM参数调优

### 常用的JVM调优参数

```shell
-Xms 					//初始大小内存，默认为物理内存1/64,等价于-XX：initialHeapSize
						示例：-Xms512m    			设置为512m
-Xmx 					//分配最大内存，默认为物理内存1/64,等价于-XX：MaxHeapSize
						示例：-Xmx512m    			设置为512m
-Xss					//设置单个线程的栈的大小，默认为512k-1024k，等价于-XX:ThreadStackSize
						根据操作系统区分具体可查看Oracle官方文档
						示例：-Xss1024k    			设置为1024k
-Xmn					//设置年轻代大小（很少调试）
-XX:MetaspaceSize		 //设置元空间的大小，那么元空间和永久代的区别是什么呢，答案就是元空间并不在虚拟机中，而在本地内存中，因此默认情况下元空间的大小仅受限本地内存，并且无论内存大小元空间初始化如果未被修改那么就是在20M左右，因此为了防止元空间OOM溢出，所以会进行调试
```

非调优参数

```shell
-XX:+PrintGCDetails 	  //打印GC回收的过程
```



### 如何查看JVM运行时的运行参数

```shell
首先使用jps查询出来正在运行的java程序
jps
然后找到他的pid然后通过jinfo查看，注意会出现一个jps的pid请不要选择jps的ip，我们选中一个进行查看
jinfo -flags 533
然后我们就会查看到如下的画面了
我们还能使用，查看具体一个参数，比如我查看最大堆内存
jinfo -flag MaxHeapSize  533
我们可以看到他是
2147483648计算后为2G，而我的内存为16g，为1/8，默认最大为1/4,我们再看下最小是不是1/64,我们会发现我查询的jenkins的最小也是2g，这里做了jvm的内存调优，默认正常是1/64，我们可以看到开始时有一个Non-default我们就能发现我们使用的jvm参数是被修改过的而不是默认的
我们还能在项目启动的时候打印他，只需要添加一个JVM参数,就能打印初始化的信息了
java -XX:+PrintCommandLineFlags -jar XXX.jar 
```

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570954838.png)

这些就是jvm的运行时参数了

### Jvm的参数类型有哪些呢？

​	分为三类：

​		标配参数

```shell
-help
-server
-client
-showversion
-cp
-classpath
```

​		X参数

```shell
X参数是非标准的参数
-Xint：解释执行
-Xcomp：第一次使用就编译成本地代码
-Xmixed：混合模式，jvm自己来决定是否编译成本地代码。
```

​		XX参数

```shell
XX参数是一种非标准化的参数，相对不是很稳定，用户可以自己设置，jvm调优和debug都是用这些参数，主要分为以下两大类：
1、Boolean类型
	格式：-XX:[+-]<NAME>表示启用或者禁用name属性
	比如：-XX:+UseConcMarkSweepGC
	-XX:+UseG1GC
2、非Boolean类型
	格式：-XX:<NAME>=<VALUE>表示name属性的值是value
	比如：XX:MaxGCPauseMillis=500
	XX:GCTimeRatio=19
3、特例
	不是X参数，而是XX参数
	-Xms等价于-XX:InitialHeapSize
	-Xmx等价于-XX:MaxHeapSize
```

我们可以通过命令查询JVM的所有参数

```shell
java -XX:+PrintFlagsInitial
java -XX:+PrintFlagsFinal -version

查看JVM启动时默认参数以及默认垃圾收集器等等（重点）
java -XX:+PrintCommandLineFlags -version
```

### **jvisualvm.exe**的使用

​		产生OOM异常时生成Dump文件		

​			-XX:+HeapDumpOnOutOfMemoryError

​			然后找到	

​			java的、bin目录下的**jvisualvm.exe** 

​			选中pid运行的线程，右键打印dump文件，然后进行分析

​		-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false

​		使用jvisualvm监控9090这个端口的java信息

​		**Trace跟踪参数** 

​				打开GC跟踪日志（每次执行GC的信息都能打印，获得执行时间，空间大小）：

​				-verbose:gc 或 -XX:+printGC 或 -XX:+printGCDetails

​				类加载监控：（监控类加载的顺序）

​				-XX:+TraceClassLoading

​		**堆的分频参数** 

​				-Xmx10M 指定最大堆，JVM最多能够使用的堆空间 （超过该空间引发OOM）

​				-Xms5M 指定最小堆，JVM至少会有的堆空间（尽可能维持在最小堆）

​				-Xmn 11M（new） 设置新生代大小

​		**栈的分配参数**

​				-Xss 每个线程都有独立的栈空间（几百k，比较小）

​				需要大量线程时，需要尽可能减小栈空间

​				栈空间太小-----StackOverFlow栈溢出（一般递归时产生大量局部变量导致）

### JVM自带工具类

jconsole的使用 		

javap反编译的使用

jdb是一个断点工具 

jps查询Java进程

jstat查询运行数据

jstatd同样用于监控JVM实例 

jinfo打印特定JVM实例的配置信息

jmap用于查看JVM的so对象内存占用情况或指定的JVM实例堆内存情况 

jstack用于打印指定进程的调用堆栈信息 



# 测试

### 模拟堆内存溢出

我们编写一个大对象，为50M，然后我们使用IDEA设置JVM的启动参数



![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570906702.png)



启动参数如下

```shell
 -Xmx10m -Xms10m -XX:+PrintCommandLineFlags -XX:+PrintGCDateStamps -XX:+PrintGCDetails 
```

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570889103.png)

我们只有10m的堆内存，但是我们new了一个50m的大对象所以一定会oom Java heap space

如下图所示

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570873505.png)

我们可以看到我们已经oom了，并且我们通过参数可以看到打印了非常多的信息，下面我们来分析打印的这些数据，

我们可以看到打印了时间已经GC类型，GC代表新生代的垃圾回收，Full GC表示老年代的回收下面是具体的参数的解释，下面如图就是参数的信息

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570861296.png)



### 模拟栈溢出

还是使用jvm参数如下，修改栈为108k

```shell
-Xmx10m -Xms10m -Xss108k -XX:+PrintCommandLineFlags -XX:+PrintGCDateStamps -XX:+PrintGCDetails 
```

然后使用如下代码运行

```java
    static Integer i = 1;
    public static void main(String[] args) {
        stackOOM();
    }

    public static void stackOOM(){
        i += 1;
        if(i <=  10000){
            byte[] a = new byte[10];
            stackOOM();
        }
    }
```

发现栈溢出

