

# Java基础相关

## 讲讲你所遇到过的异常

​		ConcurrentmodificationException		

​		这个异常是在多线程操作集合的时候产生的写并发写入的异常原因是因为操作没有加锁的对象例如ArrayList，HashSet，HashMap的时候，具体表现在高并发的写上

## String

### 讲一讲字符串的intern()方法

​		首先我们先解析intern()方法，我们点进源码中进行发现

```java
    // 我们发现他是一个Native方法，也就是本地方法
    public native String intern();
```

​		下面我们在来看一下关于这个方法的解释

```java
    /**
     * 返回字符串对象的规范表示形式。
     * 字符串常量池最初是空的，由类{@code String}单独维护。
     * 当intern方法被调用时，如果池中已经包含了一个字符串，该字符串等于这个{@code string}对象(由{@link #equals(object)}方法确定)，那么池中的字符串将被返回。否则，该{@code String}对象将被添加到池中，并返回对该{@code String}对象的引用。
     * 对于任意两个字符串{@code s}和{@code t}，当且仅当{@code s = (t)}为{@code true}时，{@code s == t.intern()}为{@code true}
     * 所有字符串字面量和字符串值常量表达式都是interned。字符串字面量定义在<cite>的第3.10.5节Java&trade;语言规范</引用>。
     * @return与此字符串具有相同内容的字符串，但保证来自惟一字符串池。
     */
```

​		通过翻译我们可以看出来，字符串常量池由String进行维护，我们调用方法的intern方法时，如果常量池中没有，那么则会在常量池中进行创建，创建完成之后则返回引用，如果已经存在那么则返回原来的已经创建的常量池中的引用。

​		示例如下：

​			我们创建一个常量，然后创建两个对象调用intern()方法		

```java
		// 第一次使用常量池初始化test属性，放入常量池
    private static final String test = "bigkang";
    public static void main(String[] args) {
        // 创建一个a变量
        String a = new StringBuilder("big").append("kang").toString();
        // 创建一个b变量
        String b = new StringBuilder("big").append("kang").toString();
        // a、b由不同对象new出来，内存地址不一致，无法相等，为false
        System.out.println(a == b);

        // 调用intern()方法，此时常量池已经存在返回内存地址
        String internA = a.intern();
        // 常量池内存地址与a初始化的对象内存地址不一致，false
        System.out.println(internA == a);

        // 调用intern()方法，此时常量池已经存在返回内存地址
        String internB = b.intern();
        // 常量池内存地址与b初始化的对象内存地址不一致，false
        System.out.println(internB == b);

        // a、b调用intern()方法返回常量池内存地址，返回两次都是从常量池中同一个地址，true
        System.out.println(internA == internB);
    }
```

​			我们再次修改代码去掉属性中的常量

```java
  public static void main(String[] args) {
        // 创建一个a变量
        String a = new StringBuilder("big").append("kang").toString();
        // 创建一个b变量
        String b = new StringBuilder("big").append("kang").toString();
        // a、b由不同对象new出来，内存地址不一致，无法相等，false
        System.out.println(a == b);

        // 调用intern()方法，此时常量池不存在bigkang，现在开始创建并且返回内存地址
        String internA = a.intern();
        // 常量池内存地址与a初始化的对象内存地址为同一个，true
        System.out.println(internA == a);

        // 调用intern()方法，此时常量池已经存在返回内存地址
        String internB = b.intern();
        // 常量池内存地址与b初始化的对象内存地址不一致，false
        System.out.println(internB == b);

        // a、b调用intern()方法返回常量池内存地址，返回两次都是从常量池中同一个地址，true
        System.out.println(internA == internB);
    }
```

​			我们再试一下java字符串，按道理来说第一次调用是为true，但是却是false

```java
   public static void main(String[] args) {
        // 创建一个a变量
        String a = new StringBuilder("ja").append("va").toString();
        // 创建一个b变量
        String b = new StringBuilder("ja").append("va").toString();
        // a、b由不同对象new出来，内存地址不一致，无法相等，false
        System.out.println(a == b);

        // 调用intern()方法，此时按道理来说常量池应该没用java
        String internA = a.intern();
        // 常量池内存地址与a初始化的对象内存地址应该是为同一个，也应该为true
        System.out.println(internA == a);

        // 调用intern()方法，此时常量池已经存在返回内存地址
        String internB = b.intern();
        // 常量池内存地址与b初始化的对象内存地址不一致，false
        System.out.println(internB == b);

        // a、b调用intern()方法返回常量池内存地址，返回两次都是从常量池中同一个地址，true
        System.out.println(internA == internB);
    }
```

​			那么我们就知道了肯定是有哪个地方有初始化过java这个字符，我们定位到sun.misc.Version，就可以看到了

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1603691966468.png)

## HashMap

​		HashMap核心变量：

​			Node<K,V>：链表节点，包含了key、value、hash、next指针四个元素

​			table：Node<K,V>类型的数组，里面的元素是链表，用于存放HashMap元素的实体

​			loadFactor：负载因子

​			size：记录了放入HashMap的元素个数

​			threshold：阈值，决定了HashMap何时扩容，以及扩容后的大小，一般等于table大小乘以

​						loadFactor

### HashMap有几个构造方法？

​		答：4个

​		分别是

```java
// 此方法用于设置HashMap的初始化长度，还有负载因子
public HashMap(int initialCapacity, float loadFactor) {  
    ...  
    this.loadFactor = loadFactor;  
    this.threshold = tableSizeFor(initialCapacity);  
}  

// 此方法可以定义初始化容器长度
public HashMap(int initialCapacity) {  
    this(initialCapacity, DEFAULT_LOAD_FACTOR);  
}  
  
// 默认初始化值，默认初始化负载因子
public HashMap() {  
    this.loadFactor = DEFAULT_LOAD_FACTOR; 
}  
  
// 将一个HashMap通过构造方法放入另一个HashMap，负载因子使用默认
public HashMap(Map<? extends K, ? extends V> m) {  
    this.loadFactor = DEFAULT_LOAD_FACTOR;  
    putMapEntries(m, false); 
}  
```



### JDK8中对HashMap做了怎样的优化？

   在JDK1.6，JDK1.7中，HashMap采用数组+链表实现 ，而JDK1.8中，HashMap采用数组+链表+红黑树实现，当链表长度超过阈值（8）时，将链表转换为红黑树，这样大大减少了查找时间。

​	核心重点：1.6,1.7   	数组+链表

​			   		1.8 	数组+链表       长度超过8      ----->	转换为红黑树



### HaspMap是怎么进行扩容的？

​		为了能让 HashMap 存取高效，尽量较少碰撞，Hash 值的范围值-2147483648到2147483648，前后加起来大概40亿的映射空间。用之前还要先做对数组的长度取模运算，这个数组下标的计算方法是“ `(n - 1) & hash` ”。（n代表数组长度）。这也就解释了 HashMap 的长度为什么是2的幂次方。

​		但是通过源码后发现，HashMap的最大长度是10亿左右，并且它的每次扩容是因为计算机底层都是采用二进制存储，在扩容计算时，我们直接进行位移操作即可快速计算，只要每次扩容不停地向左位移一位即可乘2也就是2的N次幂，我感觉更多是考虑到了计算机底层的计算而进行优化，并且它的初始化长度如下：

```
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
```

采用位运算的方式进行计算，HashMap的源码中大量的使用了位运算，因为在乘除的计算中使用位运算能更快高效的计算出结果。

​		那么他到底是如何进行扩容的呢

### 为什么HashMap扩容的大小都是2的N次幂？

​	为了能让 HashMap 存取高效，尽量较少碰撞，Hash 值的范围值-2147483648到2147483648，前后加起来大概40亿的映射空间。用之前还要先做对数组的长度取模运算，这个数组下标的计算方法是“ `(n - 1) & hash` ”。（n代表数组长度）。这也就解释了 HashMap 的长度为什么是2的幂次方。

​	但是通过源码后发现，HashMap的最大长度是10亿左右，并且它的每次扩容是因为计算机底层都是采用二进制存储，在扩容计算时，我们直接进行位移操作即可快速计算，只要每次扩容不停地向左位移一位即可乘2也就是2的N次幂，我感觉更多是考虑到了计算机底层的计算而进行优化，并且它的初始化值是

### HashMap，HashTable，ConcurrentHashMap的区别？

​	**HashTable**

​	底层数组+链表实现，无论key还是value都不能为null，线程安全，实现线程安全的方式是在修改数据时锁住-整个HashTable，效率低，ConcurrentHashMap做了相关优化

​	初始size为11，扩容：newsize = olesize*2+1

​	计算index的方法：index = (hash & 0x7FFFFFFF) % tab.length

**HashMap**

底层数组+链表实现，可以存储null键和null值，线程不安全

初始size为16，扩容：newsize = oldsize*2，size一定为2的n次幂

扩容针对整个Map，每次扩容时，原来数组中的元素依次重新计算存放位置，并重新插入

插入元素后才判断该不该扩容，有可能无效扩容（插入后如果扩容，如果没有再次插入，就会产生无效扩容）

当Map中元素总数超过Entry数组的75%，触发扩容操作，为了减少链表长度，元素分配更均匀

计算index方法：index = hash & (tab.length – 1)

**ConcurrentHashMap**

1.6时底层采用分段的数组+链表实现，线程安全

通过把整个Map分为N个Segment，可以提供相同的线程安全，但是效率提升N倍，默认提升16倍。(读操作不加锁，由于HashEntry的value变量是 volatile的，也能保证读取到最新的值。)

1.8版本时采用数组+链表+红黑树

取消segments字段，直接采用`transient volatile HashEntry<K,V> table`保存数据，采用table数组元素作为锁，从而实现了对每一行数据进行加锁，进一步减少并发冲突的概率。 

Hashtable的synchronized是针对整张Hash表的，即每次锁住整张表让线程独占，ConcurrentHashMap允许多个修改操作并发进行，其关键在于使用了锁分离技术有些方法需要跨段，比如size()和containsValue()，它们可能需要锁定整个表而而不仅仅是某个段，这需要按顺序锁定所有段，操作完毕后，又按顺序释放所有段的锁

扩容：段内扩容（段内元素超过该段对应Entry数组长度的75%触发扩容，不会对整个Map进行扩容），插入前检测需不需要扩容，有效避免无效扩容

### 极高并发下HashTable和ConcurrentHashMap哪个性能更好，为什么，如何实现的

​	`ConcurrentHashMap` 的性能仍然保持上升趋势，而 `Hashtable` 的性能则随着争用锁的情况的出现而立即降了下来。 

​	核心：

​		Hashtable使用 ` synchronized` 作为锁，而在高并发情况下疯狂抢锁会损耗性能，ConcurrentHashMap使用锁桶（或段）。 ConcurrentHashMap将hash表分为16个桶（默认值），诸如get,put,remove等常用操作只锁当前需要用到的桶 ，所以他在高并发情况系会比HashTable快很多

### HashMap在高并发下如果没有处理线程安全会有怎样的安全隐患，具体表现是什么

Hashmap在并发环境下，可能出现的问题：

​	1、多线程put时可能会导致get无限循环，具体表现为CPU使用率100%；  

​			在HashMap每次put的时候都会检测，他的长度跟负载因子算出的阙值，如果在put的时候原来的Hash表放不下了，那么就会进行扩容， 扩容的长度为两倍，例如16的长度负载因子12，如果达到13那么就扩容到32，如果下次达到24那么就会再次扩容，他扩容的方式就是创建一个新的Hash表将原来的数据存储进去，这个过程就是一个数据的迁移过程，rehash ()的过程，多线程操作就有可能形成循环链表，如果使用get方法就会出现Infinite Loop的情况，在无限循环的过程中会造成cpu占满从而引起卡死，崩溃等等情况

​	2、多线程put时可能导致元素丢失 

​			当多个线程同时执行addEntry(hash,key ,value,i)时，如果产生哈希碰撞，导致两个线程得到同样的bucketIndex去存储，就可能会发生元素覆盖丢失的情况 

### HashMap检测到hash冲突后，将元素插入在链表的末尾还是开头

## ArrayList

### ArrayList的初始化以及扩容的实现过程



​		ArrayList的初始化他的容器大小时0，因为ArrayList在初始化的时候不会进行容器的初始化

​		他会在第一次添加的时候给他的容器进行赋值，他的大小是10

​		并且在他的扩容的时候是在add的时候容器大小不够触发，扩容为原来容器的1.5倍

### 什么情况下你会使用ArrayList？什么时候你会选LinkedList？

​		在ArrayList中使用的是数组，那么他的查询效率就会高，因为他的查询时间复杂度为0（1），而LinkedList的采用的是双向链表，所以他的查询的时间复杂度为O（N），所以在查询比较多的情况下我们使用ArrayList，那么又来看一下使用LinkedList的场景，我们知道ArrayList的删的时候他会去进行一个位移的操作，在中间添加的时候他会查询并且位移，那么他的时间复杂度为O（n），而LinkedList采用双向链表，那么他可以从节点的上一个节点指向节点的下一个直接就进行了删除时间复杂度为O（1），所以在频繁的操作元素的时候使用LinkedList

## 类

### java中四种修饰符的限制范围。

​		public

​			不同包，非子类，只要项目内都可以访问

​		protected

​			只有当前类和子类还有本包能够使用，

​		default

​			只有当前类和本包能够使用

​		private

​			只有本类能够使用

### Object类中的方法。

```
 1 registerNatives()   //私有方法
 
 2 getClass()    //返回此 Object 的运行类。
 
 3 hashCode()    //用于获取对象的哈希值。
 
 4 equals(Object obj)     //用于确认两个对象是否“相同”。
 
 5 clone()    //创建并返回此对象的一个副本。 
 
 6 toString()   //返回该对象的字符串表示。   
 
 7 notify()    //唤醒在此对象监视器上等待的单个线程。   
 
 8 notifyAll()     //唤醒在此对象监视器上等待的所有线程。   
 
 9 wait(long timeout)    //在其他线程调用此对象的 notify() 方法或 notifyAll() 方法，或        者超过指定的时间量前，导致当前线程等待。 
 
10 wait(long timeout, int nanos)    //在其他线程调用此对象的 notify() 方法或 notifyAll() 方法，或者其他某个线程中断当前线程，或者已超过某个实际时间量前，导致当前线程等待。

11 wait()    //用于让当前线程失去操作权限，当前线程进入等待序列

12 finalize()    //当垃圾回收器确定不存在对该对象的更多引用时，由对象的垃圾回收器调用此方法。
```

## 动态代理

### 动态代理的两种方式，以及区别。

​		**一般而言，动态代理分为两种，一种是JDK反射机制提供的代理，另一种是CGLIB代理。在JDK代理，必须提供接口，而CGLIB则不需要提供接口，在Mybatis里两种动态代理技术都已经使用了，在Mybatis中通常在延迟加载的时候才会用到CGLIB动态代理。** 



​		通过实现接口InvocationHandler，然后在他的invoke方法上面进行动态代理

​		CGLIB的类似于jdk的动态代理，但是不用提供接口

## JAVA库相关

### Java序列化的方式。

​		1、实现Serializable接口

​		2、实现Externalizable接口

### 传值和传引用的区别，Java是怎么样的，有没有传值引用

- 传值：传递的是值的副本。方法中对副本的修改，不会影响到调用方。
- 引用：传递的是引用的副本，共用一个内存，会影响到调用方。此时，形参和实参指向同一个内存地址。对引
- 用副本本身（对象地址）的修改 



### 一些较新的东西JDK8的新特性，流的概念及优势，为什么有这种优势

​		个人感觉，一项技术需要有生命周期地进行更新，所以会出现一些新式的东西，可能好用也有可能不好用，但是他的存在肯定是有意义的，像Lambda表达式和Stream流这是JDK8的新特性中比较火的两个特性，那么他们分别又做了什么呢？先来说说Lambda它体现了函数式编程这个概念，其实在很多地方已经有这个概念了，例如前端ES6的规范中，而且他不单单是一个概念，也是一个革新，他还能帮助我们简化代码，尤其是在编写匿名内部类的时候，这就是他为什么会产生（因为这个概念是已经有的，并且8的版本开发出来了，而且也能简洁代码，还能体现函数式编程），那么再来说一下Stream流的概念，

​		流的概念数据流（data stream）最初是通信领域使用的概念，代表传输中所使用的信息的数字编码信号序列。然而，我们所提到的数据流概念与此不同。这个概念最初在1998年由Henzinger在文献87中提出，他将数据流定义为“只能以事先规定好的顺序被读取一次的数据的一个序列”。 

​		那么Stream流有什么优势呢？

（1）速度更快

（2）代码更少（增加了新的语法Lambda表达式）

（3）强大的Stream API

（4）便于并行

（5）最大化减少了空指针异常Optional

其中最为核心的为 Lambda 表达式与Stream API 



​		流注重的不是不是数据，而是计算，Stream他并不会自己存储元素，只能通过其他获取，而且他也不会修改源对象，只会产生新的数据，他的流式计算方便快捷，效率非常高，但是不适合作为存储容器，他更适合计算数据，数据筛选之类



### @transactional注解在什么情况下会失效，为什么

1，检查方法是不是public 

2，检查异常是不是unchecked异常 

3，如果是checked异常也想回滚的话，注解上写明异常类型即可 @Transactional(rollbackFor=Exception.class) 

## Java反射原理？

## Java注解原理？

## Integer

### 为什么两个Integer==为false，有的为true？

​		首先我们先来看一看这一个代码，我们给a和b进行赋值，为127，然后c和d赋值为128

```java
        Integer a = 127, b = 127;
        Integer c = 128, d = 128;

        System.out.println(a == b);
        System.out.println(c == d);
```

​		那么他的结果为什么会是如下的情况呢？

```java
true
false
```

​		我们发现非常神奇，为什么一模一样的代码，只是两个变量的值不一样就会变成这样呢，究竟是a和b的扭曲，还是c和d的沦丧，导致了这么变态的结果，那么下面我们来看一下究竟是为什么。

​		首先我们先来反编译一下这个代码究竟有什么操作：

```java
   // 第一步
	 L0
    // 行号，第一步编号
    LINENUMBER 3 L0
    // 执行push操作，将常量127赋值
    BIPUSH 127
    // 注意这一步，调用静态方法Integer.valueOf
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
		// 将引用类型或returnAddress类型值存入局部变量
    ASTORE 1
   L1
    BIPUSH 127
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    ASTORE 2
   L2
    LINENUMBER 4 L2
    SIPUSH 128
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    ASTORE 3
   L3
    SIPUSH 128
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    ASTORE 4
```

​		那么我们就发现了一个问题

​		在我们平时的赋值中，我们都是直接进行BIPUSH，为什么在Integer的时候我们需要，调用Integer的ValueOf呢？原来这是Java在我们对代码进行编译的时候默认给我们进行了优化，在装箱的时候如果发现了Integer就会在class指令中加入装箱程序，同样的Long，Short等其他的包装类也有可能会有一些优化。

```java

   int a = 10;
	 // 反编译后如下
	 L0
    LINENUMBER 3 L0
    BIPUSH 10
    ISTORE 1
```

​		那么为什么我们调用了之后他会出现==的时候这个问题呢，答案就是在Integer.valueOf(int i)的源码中：		

```java
    public static Integer valueOf(int i) {
      	// 首先我们看到这里有一个判断操作，这里一看到Cache就知道肯定是缓存的操作，那么这一步就是判断我们的i这个值有没有超过缓存的大小这个值为-128到127，如果有的话从缓存数组中取出，如果说没有的话则new 一个Integer
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }
```

​		我们就知道了这个前面127和128的两组对象为什么不等于了，其实我们从127进行取出的时候那么他会从缓存中直接取出，这个缓存是一个Integer对象数组，那么我们两次从127取出，就相当于把这个缓存中的内存地址，给到了两个变量进行引用，所以他们两个的内存地址其实是一个对象，所以使用==他们就会为true，但是我们使用128进行获取的时候超过了这个范围，那么就会重新new一个对象，导致初始化了两个内存空间，他们的内存地址不一致所以为false。

​		同样有缓存的包装类还有Long,Short,Byte,整形的基础包装类都会有一个缓存并且值都为-128到127