# 下载Python

​	首先进入官网

​	<https://www.python.org/downloads/windows/> 

​	下载windows版本，这里我们选择python3，自行选择

​	![](img\windows下载1.png)

进去之后找到下面的选项（往下滑动）

![](img\windows下载2.png)

我们这里选择windows64位的可执行文件，然后下载打开我们的执行文件

![](img\python安装1.png)

下一步点击上面的Customize installation

![](img\python安装2.png)

然后下一步，不用管

![](img\python安装3.png)

选择所有用户，然后安装，这一步可能有点慢等待一下，安装完成之后关闭页面，我们进入cmd命令窗口，输入python就能看到以下界面了，

![](img\python安装4.png)

如果出现了那么我们使用exit()命令就能退出了到此python安装完成

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