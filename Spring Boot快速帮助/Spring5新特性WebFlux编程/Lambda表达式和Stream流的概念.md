## 什么是Lambda表达式？

​	Lambda 表达式”(lambda expression)是一个[匿名函数](https://baike.baidu.com/item/%E5%8C%BF%E5%90%8D%E5%87%BD%E6%95%B0/4337265)，Lambda表达式基于数学中的[λ演算](https://baike.baidu.com/item/%CE%BB%E6%BC%94%E7%AE%97)得名，直接对应于其中的lambda抽象(lambda abstraction)，是一个匿名函数，即没有函数名的函数。Lambda表达式可以表示[闭包](https://baike.baidu.com/item/%E9%97%AD%E5%8C%85/10908873)（注意和数学传统意义上的不同）。 

​	他是在Jdk8的新特性中出现的，可以用来简化我们的代码量，属于函数式编程。



## Lambda表达式的简单用法



​	//表示声明这是一个函数式接口

​	@FunctionalInterface

​	interface Opne{   

​		//注意！在Lambda中只能有一个方法

​		 int save(int i);



​		//但是可以有另外多个的默认方法,并且默认方法可以不用在继承接口时必须去实现它也就是说，如果我			  

​		//们继承了Opne那么我们可以不重写jian

​		default int jian(int i,int o){

​			return i - o;

​		};

​	}

​	//这里我们可以很清楚的就看到语法上面发生了改变

​	Opne opne = i -> i*9;

​	//然后我们来调用这个接口中的方法

​	System.out.println(opne.save(1));





## Lambda表达式的默认接口

​	接口					输入参数				返回类型				说明		

​	Predicate<T>				T					boolean				断言（true或false）

​	Consumer<T>			T					/					消费一个数据

​	Function<T,R>			T					R					输入T输出R的函数

​	Supplier<T>				/					T					提供一个数据

​	UnaryOperator<T>		T					T					一元函数（输出输入类型相同）

​	BiFunction<T,U,R>		(T,U)				R					两个输入的函数

​	BinaryOperator<T>		(T,T)					T					二元函数(输出输入类型相同)



### 	断言函数使用

```
//定义一个断言函数
Predicate<Integer> integerPredicate = i -> i == 9;
//进行断言
System.out.println(integerPredicate.test(pre));
```

### 	消费函数使用

```
//定义一个消费函数
Consumer<Integer> consumer = con -> System.out.println(con);
//进行消费
consumer.accept(i);
```

### 输入输出函数使用

```
//定义一个输入输出函数
Function<Integer, String> integerPredicate = i -> "输出了" + i;
//进行输入输出
System.out.println(integerPredicate.apply(f));
```

### 数据提供函数使用

```
//定义一个提供函数
Supplier<Integer> supplier = () -> ge;
//进行提供
System.out.println("提供了" + supplier.get());
```

### 一元函数使用

```
//定义一个一元函数
UnaryOperator<Integer> integerPredicate = i -> i + 9;
//进行输出一元函数
System.out.println(integerPredicate.apply(ins));
```

### 二元函数使用

```
//定义一个二元函数
BinaryOperator<Integer> integerPredicate = (i, z) -> i + z;
//进行二元函数消费
System.out.println(integerPredicate.apply(m, n));
```

### 多参数函数使用

```
//定义一个多参数函数
BiFunction<Integer, Double, String> integerStringBiFunction =
        (a, s) -> "" + a + s;
//进行多参数函数消费
System.out.println(integerStringBiFunction.apply(q, w));
```



## 函数的柯里化

​	什么是柯里化，在计算机科学中，柯里化（Currying）是把接受多个参数的函数变换成接受一个单一参数(最初函数的第一个参数)的函数，并且返回接受余下的参数且返回结果的新函数的技术。这个技术由 Christopher Strachey 以逻辑学家 Haskell Curry 命名的，尽管它是 Moses Schnfinkel 和 Gottlob Frege 发明的。 

​	如何使用柯里化函数？

```
	//柯里化函数，函数返回函数
	Function<String, Consumer<String>> functionFunction = x -> y -> System.out.println(x + y);
	functionFunction.apply("dage\t").accept("niubi");
	//打印结果       ——    dage         niubi
```

​	



## 什么是Stream流？

流是字节序列的抽象概念。

文件是数据的静态存储形式，而流是指数据传输时的形态。

流类分为两个大类：节点流类和过滤流类（也叫处理流类）。

程序用于直接操作目标设备所对应的类叫节点流类，程序也可以通过一个间接流类去调用节点流类，以达到更加灵活方便地读取各种类型的数据，这个间接流类就是过滤流类（也叫处理流类），或者称为包装类。

## 流的创建

​	集合			

​			Collection.stream / parallelStream

​	数组

​			Arrays.stream

​	数字Stream

​			IntStream / LongStream.range / rangeClosed

​			/ Random.ints / longs/doubles

​	自己创建

​			Stream.generate/iterate



## 流的中间操作

​	无状态操作

​			map/mapToXxx

​			flatMap/flatMapToXxx

​			filter

​			peek

​			unordered

​	有状态操作

​			distinct

​			sorted

​			limit/skip