# 安装

首先我们只需要去apache官网下载jmeter的压缩包然后解压即可

下载地址：

​			<http://jmeter.apache.org/download_jmeter.cgi>

进入下载页面选择相应的jmeter下载即可

下载后解压，进入bin目录

```java
jmeter.bat					//windows运行脚本
jmeter.sh					//linux运行脚本
```

# 中文

我们运行后打开jmeter使用中文，我们找到导航栏上的Options然后找到Choose language然后选择Chinese(simplified)即可



也能修改jmeter.properties

```
修改
language=zh_CN
```

# 使用

首先我们先创建一个文件，点击文件新建

然后我们创建一个线程组，如下图示例：

![](img\jmeter.png)

创建之后创建一个http测试：

![](img\jmeter2.png)

下面我们来进行测试吧，例如我们现在需要请求一个地址：

<http://127.0.0.1:8084/testSendUdp>

我们将他拆分为，请求如下

​		协议：http

​    		ip：127.0.0.1

​	端口号：8084

​		方法：GET

​		路径：testSendUdp

​		参数：无

![](img\jmeter3.png)

然后我们配置结果监听：

![](img\jmeter4.png)

有很多种可以选择，这里我们选择表格查看结果，然后点击上方的启动按钮，等待执行完毕，然后查看结果即可

![](img\jmeter5.png)



然后查看监听表格即可

![](img\jmeter6.png)