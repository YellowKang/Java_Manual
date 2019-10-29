

# IDEA安装Go插件

我们打开Idea设置然后查看插件，然后输入go，我这里已经安装如果没显示，点击下方install搜索，然后重启idea

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567154965973.png)

# 第一个Go项目

我们新建一个项目

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567155163740.png)

然后右键新建项目，选择go

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567155277704.png)

新建后新建文件，选择Go   Fiel

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567155341362.png)

然后我们在里面写入代码,我们可以看到我们的包名是HelloGo，然后引入了fmt包，并且打印了Hello World		

![](/Users/bigkang/Library/Application Support/typora-user-images/image-20190830165919178.png)

但是这样是启动不了的，因为只有在main包下才能运行

我们修改为main包，然后我们再启动，我们可以通过命令

```
go run HelloGo.go 
```

或者直接右键运行项目

```
package main

import "fmt"

func main()  {
	fmt.Print("Hello World")
}
```

然后我们可以看到打印出如下内容，我们的Hello World已经执行成功了

![](/Users/bigkang/Library/Application Support/typora-user-images/image-20190830170429301.png)