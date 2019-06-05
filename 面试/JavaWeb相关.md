# Web相关

## Cookie和Session的区别

Cookie以文本文件格式存储在浏览器中，而session存储在服务端它存储了限制数据量。它只允许4kb它没有在cookie中保存多个变量。

cookie的存储限制了数据量，只允许4KB，而session是无限量的

我们可以轻松访问cookie值但是我们无法轻松访问会话值，因此它更安全

设置cookie时间可以使cookie过期。但是使用session-destory（），我们将会销毁会话。

## Serlvet的生命周期

初始化	---》	运行	---》 	销毁

初始化	init()



​		init 方法被设计成只调用一次。它在第一次创建 Servlet 时被调用，在后续每次用户请求时不再调用，当用户调用一个 Servlet 时，但是您也可以指定 Servlet 在服务器第一次启动时被加载 ，就会创建一个 Servlet 实例，每一个用户请求都会产生一个新的线程，适当的时候移交给 doGet 或 doPost 方法 



运行	service()



​		service() 方法是执行实际任务的主要方法。Servlet 容器（即 Web 服务器）调用 service() 方法来处理来自客户端（浏览器）的请求，并把格式化的响应写回给客户端。 每次服务器接收到一个 Servlet 请求时，服务器会产生一个新的线程并调用服务。service() 方法检查 HTTP 请求类型（GET、POST、PUT、DELETE 等），并在适当的时候调用 doGet、doPost、doPut，doDelete 等方法。 



销毁	destroy()

​	

​		destroy() 方法只会被调用一次，在 Servlet 生命周期结束时被调用。destroy() 方法可以让您的 Servlet 关闭数据库连接、停止后台线程、把 Cookie 列表或点击计数器写入到磁盘，并执行其他类似的清理活动。在调用 destroy() 方法之后，servlet 对象被标记为垃圾回收。



## Jsp和Servlet的区别

​	jsp经编译后就变成了Servlet，jsp更擅长表现于页面显示,servlet更擅长于逻辑控制，Jsp是Servlet的一种简化，使用Jsp只需要完成程序员需要输出到客户端的内容，Jsp中的Java脚本如何镶嵌到一个类中，由Jsp容器完成，Servlet更多的是类似于一个Controller，用来做控制



​	区别：

​			Servlet在Java代码中通过HttpServletResponse对象动态输出HTML内容

​			JSP在静态HTML内容中嵌入Java代码，Java代码被动态执行后生成HTML内容

## Tomcat是什么

​	Tomcat是一个应用端的Web服务器，属于轻量级应用服务器，tomcat除了实现了http协议，用来接收和响应Http请求

## Tomcat和Servlet的区别

​	，Tomcat实际上也是一个Servlet容器，但是这个端是在Web服务层的，而Java还需要经过动态的响应以及处理需要和Web服务端进行交互，那么这个交互的容器就是Servlet，而Servlet就会进行一系列的处理响应给Tomcat

## Request和Response的区别是什么

​		Request对象是服务器对浏览器所发送过来的请求的封装，而Response是是服务器对浏览器的响应的对象，里面所封装了请求头和响应头，封装了请求的请求信息，以及响应的响应信息

## 四大域和九大内置对象

​		Request域

​			request请求对象 

​		Page域

​			response响应对象 

​			pageContext页面上下文对象 

​			out输出对象 

​			config配置对象 

​			page页面对象

​			exception例外对象 

​		Application域

​			application应用程序对象

​		Session域

​			session会话对象 