

# 什么是Jvm虚拟机

JVM结构官方文档地址：https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5.2

# JVM的运行时内存模型（JMM）

​		JDK1.8之前的运行时内存模型

​		![](https://img04.sogoucdn.com/app/a/100520146/2ec6d09cfc4d3880b91dd3ecce36c818)

​		JDK1.8以及以后的运行时内存模型

![](https://img01.sogoucdn.com/app/a/100520146/66c9d6bfa0434d8d8f71f067a424d05a)

我们可以看到在1.8之前一共分为

​			方法区

​			虚拟机栈

​			本地方法栈

​			堆内存

​			程序计数器

而1.8之后分为

​			本地方法栈

​			虚拟机栈

​			程序计数器

​			堆内存

​			元数据区

​							CodeCache为编译class的字节码缓存。

下面我们着重讲解1.8中的比较核心的一个模块

## 本地方法栈

​			官网上的解释是这样的：

```
			Java虚拟机的实现可以使用传统的堆栈（俗称“ C堆栈”）来支持native方法（以Java编程语言以外的语言编写的方法）。解释程序的实现也可以使用诸如C之类的语言来解释Java虚拟机的指令集，以使用native 本机方法栈。无法加载方法并且自身不依赖于常规堆栈的Java虚拟机实现无需提供本机方法栈。如果提供，通常在创建每个线程时为每个线程分配本机方法堆栈。

			该规范允许本机方法堆栈具有固定大小，或者根据计算要求动态扩展和收缩。如果本机方法堆栈的大小固定，则在创建每个本机方法堆栈的大小时可以独立选择。

			Java虚拟机实现可以为程序员或用户提供对本机方法堆栈的初始大小的控制，并且在本机方法堆栈大小变化的情况下，可以控制最大和最小方法堆栈大小。

			以下异常条件与本机方法堆栈相关联：

					如果线程中的计算需要比允许的更大的本机方法堆栈，则Java虚拟机将抛出StackOverflowError。

					如果可以动态扩展本机方法堆栈并尝试进行本机方法堆栈扩展，但可以提供足够的内存，或者可以提供足够的内存来为新线程创建初始本机方法堆栈，则Java虚拟机将抛出OutOfMemoryError。
```



​			个人理解：Java采用C语言进行编写，所以很多对计算机底层的一些操作其实还是使用JAVA来调用C语言来进行实现的，那么这个实现的过程就是我们使用Native方法来调用C语言所编写的DLL类库，而在我们调用方法时，则会创建虚拟机栈，这个线程中就会给我们分配本地方法栈堆让我们进行调用操作。

## 虚拟机栈

​			官网上的解释是这样的：

```
			每个Java虚拟机线程都有一个私有Java虚拟机堆栈，与该线程同时创建。Java虚拟机堆栈存储框架（第2.6节）。Java虚拟机堆栈类似于常规语言（例如C）的堆栈：它保存局部变量和部分结果，并在方法调用和返回中起作用。因为除了推送和弹出帧外，从不直接操纵Java虚拟机堆栈，所以可以为堆分配帧。Java虚拟机堆栈的内存不必是连续的。

			在第一版中的Java ®虚拟机规范，Java虚拟机堆被称为Java堆栈。

			该规范允许Java虚拟机堆栈具有固定大小，或者根据计算要求动态扩展和收缩。如果Java虚拟机堆栈的大小固定，则在创建每个Java虚拟机堆栈时可以独立选择其大小。

			Java虚拟机实现可以为程序员或用户提供对Java虚拟机堆栈初始大小的控制，并且在动态扩展或收缩Java虚拟机堆栈的情况下，可以控制最大和最小大小。

			以下异常条件与Java虚拟机堆栈相关：

				如果线程中的计算需要比允许的Java虚拟机更大的堆栈，则Java虚拟机将抛出StackOverflowError。

				如果可以动态扩展Java虚拟机堆栈，并尝试进行扩展，但是可以提供足够的内存来实现扩展，或者如果没有足够的内存来为新线程创建初始Java虚拟机堆栈，则Java虚拟机机器抛出一个OutOfMemoryError。
```

​			个人理解：我们可以看到官网中说他是保存局部变量和部分结果，并且在方法调用和返回中起作用，我们知道栈是一种数据结构他是先进后出的。

​						

​			那么我们来模拟一下我们平时调用方法，例如我们A方法中调用了B方法，那么我们肯定会从A方法进入B方法，在我们调用运行A方法的时候A方法就已经被压入了栈底，那么随后他调用了B方法，B方法也会随之压入栈中，我们肯定在B方法中会进行一系列的操作，操作完成之后我们是要将结果给返回A的，那么在我们执行完B方法之后，根据先进后，后进先出的规则，B在执行完了之后肯定是要被弹出栈的，那么B出栈了之后A拿到B的执行结果之后，A也继续执行代码，当A也执行完毕之后，A也会被弹出栈，当最后一个栈底被弹出的时候，这个方法调用过程就完毕了，随之这个栈也会被销毁。



​			我们可以看到在栈运行过程中有可能出现两种异常：

StackOverflowError				

```
			由于我们调用的方法太多（通常由于递归引起），循环嵌套层层调用，当这个栈所分配的空间满了的时候就会抛出异常：StackOverflowError。									
```

OutOfMemoryError	

```
			由于达到了JVM设置的最大的堆内存容量时，无法继续创建内存，则会抛出OutOfMemoryError，堆内存溢出的异常。
```

### 栈帧

​				JVM 执行 Java 程序时需要装载各种数据到内存中，不同的数据存放在不同的内存区中（逻辑上），这些数据内存区称作[运行时数据区（Run-Time Data Areas）](https://www.cnblogs.com/jhxxb/p/10896386.html)。

​				其中 JVM Stack（Stack 或虚拟机栈、线程栈、栈）中存放的就是 Stack Frame（Frame 或栈帧、方法栈）。



​				一个线程对应一个 JVM Stack。JVM Stack 中包含一组 Stack Frame。线程每调用一个方法就对应着 JVM Stack 中 Stack Frame 的入栈，方法执行完毕或者异常终止对应着出栈（销毁）。当 JVM 调用一个 Java 方法时，它从对应类的类型信息中得到此方法的局部变量区和操作数栈的大小，并据此分配栈帧内存，然后压入 JVM 栈中。在活动线程中，只有位于栈顶的栈帧才是有效的，称为当前栈帧，与这个栈帧相关联的方法称为当前方法。

​						

## 堆内存



## 程序计数器

​				官网上的解释是这样的：

```
			Java虚拟机可以一次支持多个执行线程（JLS§17）。每个Java虚拟机线程都有其自己的 pc（程序计数器）寄存器。在任何时候，每个Java虚拟机线程都在执行一个方法的代码，即该线程的当前方法（第2.6节）。如果不是 native，则该pc寄存器包含当前正在执行的Java虚拟机指令的地址。如果线程当前正在执行的方法是native，则Java虚拟机的pc 寄存器值未定义。Java虚拟机的pc寄存器足够宽，可以returnAddress在特定平台上保存或本机指针。
```

​				那么为什么需要这个程序计数器来记录我们的程序运行到了哪一步呢，答案就是当我们在进行多线程的时候我们知道我们需要执行操作指令是是通过争抢CPU的资源进行调度的，那么就会出现我们的两个栈争抢同一个CPU资源的情况，那么如果我们第一线程争抢到了资源，执行到了一半，然后第二个线程来争抢资源操作上下文切换，当第二个线程执行了之后，我们的第一个线程会被唤醒，那么他又如何知道我们执行到了哪一步呢，这个时候就需要我们的程序计数器来帮助我们记录，在进行上下文切换之后，我们快速的找到上次执行到的步骤继续执行。



​				程序计数器是用于存放下一条指令所在单元的地址的地方。

​				当执行一条指令时，首先需要根据PC中存放的指令地址，将指令由内存取到[指令寄存器](https://baike.baidu.com/item/指令寄存器/3219483)中，此过程称为“取指令”。与此同时，PC中的地址或自动加1或由转移指针给出下一条指令的地址。此后经过分析指令，执行指令。完成第一条指令的执行，而后根据PC取出第二条指令的地址，如此循环，执行每一条指令。

## 元数据区

# Jvm常见异常

## 堆内存异常

​	OutOfMemoryError

​	当我们在进行垃圾回收的时候出现的异常，这个是由于在垃圾回收老人区是使用FullGc回收不了从而引起堆内存对象无法存储，从而造成的堆内存的溢出，这个我们通常也成为OOM异常

## 栈异常

​	StackOverflowError

​	这个异常是栈内存溢出的异常，这个通常是由于方法循环的调用引起的，因为栈是先进后出的如果一直往里面进的话就会引发栈的内存溢出，是因为只进不出造成的，这个时候我们就需要找到无限循环调用的方法进行处理



# GC垃圾回收

​		GC是什么？

​			GC（分代收集算法），Young区（新生代），Old（老人代），Perm（永久代），

​		GC回收的是什么？

​			GC回收的是在Java的堆内存中产生的使用过的无效数据

​		有哪些GC回收？

​			GC    

​				是用于来回收新生代的回收机制，当新生代的内存要满的时候就会执行GC

​			FULL GC

​				是用于来回收老人代的回收机制，当老人代的内存要满的时候就会执行GC

## 回收的内存结构

​		在他回收的内存中既然是分代收集算法那么肯定分为上面的3代，我们先来简单的概述一下3种结构

​		

​			新生代

​				新生代是新创建的使用后的内存我们会将他进行垃圾回收，但是不一定能回收玩有可能没有回

​				收掉，那么在新生代中是分3个区的（伊甸园区，幸存0区，幸存1区），这三个分区和我们的

​				后面的GC的回收算法有很大的关联，首先当新生代的内存达到一定程度的时候我们就要进行垃

​				圾回收了，但是没有回收掉的数据怎么办呢？他就会进入到幸存0区，然后跟随下一次继续回收

​				，如果下一次还没有回收掉那么就会进入幸存1区然后再进行下一次的回收，如果还是回收不掉

​				怎么办呢？不用担心Java内置让我们在进行15次的垃圾回收的机制之后还是没有回收掉的数据

​				进入老人代



​				![](JVM\GC新生代.png)

​			老人代

​				在老人代中的数据是没有新生代那样频繁的清楚的

​			永久代（元空间）

## GC的四种垃圾回收算法

### 		复制算法(Copying)

### 		标记清除(Mark-Sweep)

### 		标记压缩(Mark-Compact)

### 		标记清除压缩(Mark-Sweep-Compact)



## JVM的垃圾回收器有哪些

### Serial

启用方式，来在新生代和老年代使用串行收集器。

```
-XX:+UseSerialGC
```



### ParNew

启用方式，指定使用 ParNew 作为新生代收集器。

```
-XX:+UseParNewGC
```





### Parallel

```
-XX:+UseParallelGC								新生代使用并行回收收集器，更加关注吞吐量
-XX:+UseParallelOldGC							老年代使用并行回收收集器
-XX:ParallelGCThreads							设置用于垃圾回收的线程数
```



### CMS(Concurrent Mark Sweep) 

启用CMS垃圾回收器。

```
-XX:+UseConcMarkSweepGC         新生代使用并行收集器，老年代使用CMS+串行收集器
```



### G1

启用G1

```
-XX:+UseG1GC
```

​			在HotSpot的实现中，整个堆被划分成2048左右个Region。每个Region的大小在1-32MB之间，具体多大取决于堆的大小，对于Region来说，它会有一个分代的类型，并且是唯一一个。即，每一个Region，它要么是young的，要么是old的。还有一类十分特殊的Humongous。所谓的Humongous，就是一个对象的大小超过了某一个阈值——HotSpot中是Region的1/2，那么它会被标记为Humongous。



### ZGC（JDK11使用）



## JVM参数手册大全

```

```



## JVM 的常用参数调优

​		JVM的调优调的到底是什么优？



-Xms1024m -Xmx1024m -XX:+PrintGCDetails

# 类加载器

​		通常来说类加载器有四种分别是：

​				1、自定义加载器（拓展）

​				2、应用类加载器

​				3、拓展类加载器

​				4、启动类加载器

​		而他们对应的级别是

​				启动类加载器  >  拓展类加载器  >  应用类加载器

## 		双亲委派机制：

​				首先找

​					应用类加载器    		如果没有找拓展类加载器

​									|

​									V

​					拓展类加载器			如果还是没有

​									|

​									V

​					启动类加载器			如果还是找不到的话就返回异常给拓展类加载器

​									|

​									V

​					拓展类加载器			将异常返回给应用类加载器

​									|

​									V

​								应用类加载器



​					双亲委派机制就类似于    儿子     父亲    爷爷三个角色

​					儿子找老爸要钱老爸没有，然后老爸找爷爷要钱爷爷也没有，然后爷爷给父亲说没钱了，

​					父亲又跟儿子说没钱了，这就是双亲委派机制

### 打破双亲委派机制

​			那么我们自己实现一个类加载器，并且拒绝掉双亲委派机制，而且重写掉我们的类。

​			首先编写一个类加载器。

```java
import java.io.FileInputStream;

/**
 * @Author BigKang
 * @Date 2020/6/15 3:23 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 自定义类加载器
 */
public class CustomClassLoader {

    /**
     * 测试自定义类加载器静态类
     */
    public static class TestCustomClassLoader extends ClassLoader {

        private String path;

        public TestCustomClassLoader(String path){
            this.path = path;
        }

        private byte[] loadByte(String name) throws Exception {
            // 根据路径加载字节，这里我们把加载路径改为自己的文件中的类
            name = name.replaceAll("\\.", "/");
            FileInputStream fis = new FileInputStream(path + "/" + name + ".class");
            int len = fis.available();
            byte[] data = new byte[len];
            fis.read(data);
            fis.close();
            return data;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] data = loadByte(name);
                return defineClass(name, data, 0, data.length);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ClassNotFoundException();
            }
        }

        /**
         * 修改双亲委派，直接从当前类加载器加载
         *
         * @param name
         * @return
         * @throws ClassNotFoundException
         */
        public Class<?> loadClass(String name,Boolean resolve)
                throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    long t0 = System.nanoTime();
                    try {
                        c = findClass(name);
                    } catch (ClassNotFoundException e) {
                    }

                    if (c == null) {
                        long t1 = System.nanoTime();
                        c = findClass(name);

                        sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                        sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                        sun.misc.PerfCounter.getFindClasses().increment();
                    }
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }

    }

}
```

首先我们编写一个类从外部进行加载

```java
package com.kang.java.jvm;

public class TestClass {
    public TestClass() {
    }

    public static void printMessage() {
        System.out.println("自定义类加载器加载");
    }
}

```

我们把它编译后放入指定的路径中

首先我们打印由自定义类加载器加载，然后编译他放到目录下，根据现在的包名那么路径就是：/Users/bigkang/Documents/test/com/kang/java/jvm/TestClass.class

"/Users/bigkang/Documents/test" + "/" + name + ".class"

```java
   public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        CustomClassLoader.TestCustomClassLoader classLoader = new CustomClassLoader.TestCustomClassLoader("/Users/bigkang/Documents/test");
        Class<?> aClass = classLoader.loadClass("com.kang.java.jvm.TestClass");
        Object obj = aClass.newInstance();
        Method method = aClass.getDeclaredMethod("printMessage", null);
        method.invoke(obj, null);
        System.out.println("类加载器是：" + aClass.getClassLoader().getClass().getName());
    }
```

这样我们就通过了自己集成ClassLoader并且进行一个修改，从而实现打破双亲委派机制，不从应用类加载器所加载。

注意：（由于我们的这个类本身就是由AppClassLoader所加载的，如果说AppClassLoader中还是有一个TestClass，那么这个类就会被AppClassLoader所加载，所以我们在idea里面将原来的类给他删除掉即可）

# Stack栈

​		什么是栈？

​		栈是一种数据结构，那么在java中的栈到底是一个什么样的结构呢？

​		栈是先进后出，后进先出的类似于如下

​		![](JVM\Stack.png)

​		那么栈是先进后出，后进先出，什么和他的特点相对立呢？

​		队列，队列是先进先出。后进后出的。

​		那么栈到底是用来干什么的呢？



# Heap堆

​		



# 类对象

​		我们有时候初始化了一个对象，但是这个对象可能很大，我们并不知道这个类对象的一个大小，所以我们需要怎么样去计算这个对象的大小呢？

首先我们引入依赖

```xml
<!-- https://mvnrepository.com/artifact/org.openjdk.jol/jol-core -->
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.10</version>
</dependency>

```

它可以帮助我们查看这个对象的大小

那么我们来测试一下吧

```java
        ClassLayout layout = ClassLayout.parseInstance(new Object());
        System.out.println(layout.toPrintable());
```

那么他就会打印信息如下

```
java.lang.Object object internals:
 OFFSET  SIZE   TYPE DESCRIPTION            VALUE
      0     4        (object header)        01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)        00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)        e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
     12     4        (loss due to the next object alignment)
     
Instance size: 16 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
```

那么从这里我们就能看出这分别做了4件事情，首先我们先来了解一下前两个

```
      0     4        (object header)        01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)        00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      
      
      这两个是我们的object header，也就是我们的对象头，用于标记我们的对象表示他是一个类，那么我们知道万物皆是Object的子类,首先这两个对象头占用了4 + 4 也就是八个比特,当然这个对象头并不是固定的01，
      001  -》 无锁

			101	 -》 偏向锁

			000  -》 轻量级锁

			010  -》 重量级锁
```

那么在我们的HotSpot虚拟机的Object Header中又把这个头对象分为了Mark Word（标记字段）和 Klass Pointer（类型指针）

那么这个就是我们的Object的类型指针，这个指针指向了JVM虚拟机初始化的类信息，也就是我们的元空间或者说永久代中的类信息。

```
      8     4        (object header)        e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
```

那么我们再往下看，这里为什么会出现一个下一个对象因为对其而丢失呢。

```
     12     4        (loss due to the next object alignment)
```

​		答案就是在我们的HotSpot虚拟机中，在64位的系统里面，我们需要根据内存空间的地址获取到内存，但是我们需要根据一段连续性的内存地址去进行查找计算，所以我们使用连续性的以8的倍数的比特去进行寻找效率会比较高，那么我们可以看到这一块就是由于缺少连续性的地址我们进行了一个填充，所以下面的信息也是比较容易看出来的。

​		# 我们实例化了16个比特，由3个对象头+一个填充空间所组成

​	   # Space losses表示我们损失的内存空间，0 bytes internal表示我们损失了0个内部比特，4 bytes external表示我们损失了4个外部比特，也就是我们所填充的一个空间，所以总计我们损失了 4 bytes total

```sh
Instance size: 16 bytes				
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
```

