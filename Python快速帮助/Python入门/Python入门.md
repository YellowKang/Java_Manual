# Python数据类型

## Number（数字）

Number下面又分为4中基本类型

整型(Int)-

​	通常被称为是整型或整数，是正或负整数，不带小数点。

​	创建一个整数，我们可以看到输出的类型是一个整数

```
var1 = 1;
print(var1.__class__)
```

浮点型(floating point real values) -

​	浮点型由整数部分与小数部分组成，浮点型也可以使用科学计数法表示（2.5e2 = 2.5 x 102 = 250）

复数(complex numbers)

​	 复数由实数部分和虚数部分构成，可以用a + bj,或者complex(a,b)表示， 复数的实部a和虚部b都是浮点型

## String（字符串）

字符串我们可以声明一个变量

单引号字符串

```
str = 'asda'
print(str)
```

双引号字符串

```
str = "asda"
print(str)
```

三引号多行字符串

```
str = '''line 1
line 2'''
print(str)
```

## Boolean（布尔值）

Boolean值只有两种分别是True，和False

那么我们来看一下吧

```
flag1 = True;
flag2 = False;
print("flag1的值是：" + flag1.__str__() + "\tflag2的值是" + flag2.__str__())
```

## None（空值）

## List（列表）

## Tuple（元组）

## Dict（字典）

## Set（集合）

# Python基础语法

## Hello World

入门程序第一个就是Hello World那么我们下面来写一个吧

```
print("Hello World")
```

然后右键运行

![](img\hello world.png)





## 数组使用

遍历他的值

```
lis = [1,2,3,5,7,4]
for i in lis:
    print(i)
```

遍历他的索引

```
lis = [1,2,3,5,7,4]
for i in range(len(lis)):
    print(i)
```

遍历并且遍历index索引，i为index，v为索引

```
lis = [1,2,3,5,7,4]
for i,v in enumerate(lis):
    print(i,"-----",v)
```

获取数组长度

```
lis = [1,2,3,5,7,4]
print(len(lis))
```

增加元素，我们可以看到他的长度多了一位

```
lis = [1,2,3,5,7,4]
lis.append(7)
print(len(lis))
```

删除元素值，我们将1的值删除掉了！！（注意这里不是索引，是值）

```
lis = [1,2,3,5,7,4]
lis.remove(1)
for i in lis:
    print(i)
```

根据索引删除数组的值，在python中他叫做pop，也就是吐出

```
lis = [1,2,3,5,7,4]
lis.pop(2)
for i in lis:
    print(i)
```

清空数组，将这个数组中的所有元素清空

```
lis = [1,2,3,5,7,4]
lis.clear()
print(len(lis))
```

