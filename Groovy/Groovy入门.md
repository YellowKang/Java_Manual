

# Groovy简介

​		Groovy是一种基于[JVM](https://baike.baidu.com/item/JVM)（[Java虚拟机](https://baike.baidu.com/item/Java虚拟机)）的敏捷开发语言，它结合了[Python](https://baike.baidu.com/item/Python)、[Ruby](https://baike.baidu.com/item/Ruby/11419)和[Smalltalk](https://baike.baidu.com/item/Smalltalk)的许多强大的特性，Groovy 代码能够与 Java 代码很好地结合，也能用于扩展现有代码。由于其运行在 JVM 上的特性，Groovy也可以使用其他非Java语言编写的库。

​		Groovy 是 用于Java[虚拟机](https://baike.baidu.com/item/虚拟机)的一种敏捷的[动态语言](https://baike.baidu.com/item/动态语言)，它是一种成熟的[面向对象](https://baike.baidu.com/item/面向对象)编程语言，既可以用于面向对象编程，又可以用作纯粹的[脚本语言](https://baike.baidu.com/item/脚本语言)。使用该种语言不必编写过多的代码，同时又具有[闭包](https://baike.baidu.com/item/闭包)和动态语言中的其他特性。

​		Groovy是[JVM](https://baike.baidu.com/item/JVM)的一个替代语言（替代是指可以用 Groovy 在Java平台上进行 Java 编程），使用方式基本与使用 Java代码的方式相同，该语言特别适合与[Spring](https://baike.baidu.com/item/Spring)的动态语言支持一起使用，设计时充分考虑了Java集成，这使 Groovy 与 Java 代码的互操作很容易。（注意：不是指Groovy替代java，而是指Groovy和java很好的结合编程。

​		简单的来说Groovy是基于JVM开发的语言，可以理解为Java拓展，以及Spring集成等等。

# Groovy安装

​		下载官网：[点击进入](https://groovy.apache.org/download.html)

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1631156083381.png)

## MacOS

​		下载最新的包（点击即可）

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1631156139418.png)

​		下载后解压到目录，然后配置环境变量即可

​		vim ~/.bash_profile

​		最后新增如下

```sh
export GROOVY_HOME=/Users/bigkang/Documents/groovy-4.0
export PATH=$PATH:$GROOVY_HOME/bin
```

​		然后刷新环境变量即可

```sh
source ~/.bash_profile
```

​		然后再查看Groovy版本即可

```sh
groovy -v
```

## Windows（暂无）



# IDEA初始化Groovy项目

​		首先创建项目

​		IDEA -》 New Project

​		选择 Groovy 然后选择项目的SDK的JVM版本，以及Groovy的版本

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1631156908438.png)

​		然后设置项目名，然后点击Finsh即可

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1631157111204.png)

​		src下新建Groovy Class

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1631157151627.png)

​		写入如下代码,启动即可

```groovy
class Test {
    static void main(args){
        println("Hello World")
    }
}

```

# Groovy语法

## 注释

​		Groovy中注释分单行注释和多行注释

```groovy
    static void main(String[] args) {
        /*
            多行注释
        */
        // 单行注释
        println("Hello World")
    }
```

## 变量

Groovy中的变量可以通过两种方式定义

- **使用数据类型的本地语法**

- **使用def关键字**

下面是如下两种方式定义变量

```groovy
    static void main(String[] args) {
        // 数据类型的本地语法
        String name1 = "BigKang1"
        // 使用def关键字
        def name2 = "BigKang2"
        println "Name1:" + name1 + ",Name2:" + name2
    }
```

Groovy中有以下基本类型的变量，如上一章所述 -

- **byte** - 这用于表示字节值。例如2。
- **short** - 用于表示一个短数。例如10。
- **int** - 这用于表示整数。 例如1234。
- **long** - 这用于表示一个长数。例如10000090。
- **float** - 用于表示32位浮点数。例如12.34。
- **double** - 这用于表示64位浮点数。例如12.3456565。
- **char** - 这定义了单个字符文字。例如'a'。
- **Boolean** - 这表示一个布尔值，可以是true或false。
- **String** - 这是以字符串形式表示的文本。 例如“Hello World”。

Groovy还允许其他类型的变量，如数组，结构和类，我们将在后续章节中看到。

## 数组&集合

​		数组集合定义方式,默认创建的数组会被包装成集合，我们可以通过as方式指定集合的实现类型

```groovy
        // 定义一个Integer数组
        def integerArray = [1, 1, 2, 4, 5, 6] as Integer[]
        
        // 定义一个ArrayList集合
        def arrayList = [1, 1, 2, 4, 5, 6]

        // 定义一个LinkedList集合
        def linkedList = [1, 1, 2, 4, 5, 6] as LinkedList

        // 定义一个copyOnWriteArrayList集合
        def copyOnWriteArrayList = [1, 1, 2, 4, 5, 6] as CopyOnWriteArrayList
```

​		操作

```groovy
        // 定义一个ArrayList集合
        def arrayList = [1, 2, 3, 4, 5, 6]

        // 插入7
        arrayList.add(7)
        // 插入8
        arrayList.leftShift(8)
        // 插入9
        arrayList << 9
        // 结果[1, 2, 3, 4, 5, 6, 7, 8, 9]
        println arrayList

        // 7 OR 100 是否存在arrayList
        def has7 = 7 in arrayList
        def has100 = 100 in arrayList
        // true false
        println "$has7 $has100"

        // 删除索引下标为8的数据
        arrayList.remove(8)

        // 删除值为1的数据
        arrayList.remove((Object) 1)

        // 删除最后的一个元素
        arrayList.removeLast()
        // [2, 3, 4, 5, 6, 7]
        println arrayList

        // 遍历元素
        arrayList.each { println it }

        // 查询元素中大于4的第一个元素
        def find = arrayList.find(it -> it > 4)
        println find

        // 查询元素中大于4的所有元素
        def findAll = arrayList.findAll(it -> it > 4)
        println findAll

        // 只要元素中包含7那么就返回true，不包含返回false
        def any = arrayList.any(it -> it == 7)
        println any

        // 如果所有元素都大于3则返回true，如果有一个不大于则返回false
        def every = arrayList.every(it -> it > 3)
        println every

        // 统计元素中的偶数的数量
        def count = arrayList.count { it % 2 == 0 }
        println "% 2 Count is: $count"

        // 返回元素中的最大值和最小值
        def max = arrayList.max()
        def min = arrayList.min()
        println "max: $max , min: $min"


```



# Groovy简化写法

## 比较返回比较值

​		当我们用到某一些比较的时候，比如大于

```

```



## 时间格式化

```groovy
// 直接.format 即可将Date类型格式化
carInfo.transferDate.format("yyyy-MM-dd")
```

