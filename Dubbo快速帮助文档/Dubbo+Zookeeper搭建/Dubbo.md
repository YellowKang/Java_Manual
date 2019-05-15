# 什么是Dubbo？

​		Dubbo是一套高性能的Java开源的RPC远程过程调用框架，他有非常优异的性能

​		RPC（重点），RPC是远程的过程调用，它基于底层的Socket通信，直接调用远程的接口进行实现，他没有通过http请求去操作，而是直接调用对方电脑中的接口，在对方电脑的接口中进行实现，这个我们称之为RPC远程过程的调用



​		HttpClient（相对应）那么和他相对应的还有微服务机构，他的实现就是通过Http请求进行两个服务的通信，那么在经过了一层的http请求中他的速度会稍微比RPC的远程调用慢一点，但是Dubbo的相对应的架构，Cloud他将一套完整的技术集成到了一起，并且提供了一整套成熟的微服务架构的解决方案技术，所以cloud也是很火的



# Dubbo的注册中心

​		他默认的注册中心有4种，默认推荐使用zookeeper



​				Multicast	注册中心

​				Zookeeper	注册中心

​				Redis		注册中心

​				Simple		注册中心



​		下面我们会使用Zookeeper来搭建环境



# 如何安装Dubbo环境

​		搭建准备：

​				确认Linux服务器装有jdk（最好是1.8）

​				CentOs7

​		在文件下有三个分别是Tomcat和Dubbo-Admin还有Zookeeper



​		那么首先我们先安装Zookepper

## 1）：首先我们需要先安装jdk，因为Dubbo需要java的支持

	
	
		首先我们
		查看Linux是否存在jdk
		rpm -qa | grep jdk
		然后就可以查看安装过的jdk了
		或者
		java  ，java --version都可以
	
		如果发现有如何卸载？
	
		例如卸载java-1.7.0-openjdk-1.7.0.141-2.6.10.5.el7.x86_64
		rpm -e --nodeps 要卸载的程序	
		rpm -e --nodeps     java-1.7.0-openjdk-1.7.0.141-2.6.10.5.el7.x86_64
		也可以直接卸载所有jdk
		rpm -e --nodeps jdk
	
		如何安装jdk？
	
		使用
		yum list java*		查看所有的可安装的yum的jdk版本
		然后例如我们安装java01.8.0-openjdk-* 
		yum install java01.8.0-openjdk-* -y
		然后安装完毕后使用java -version查看就可以了
​		

## 2）：然后我们来安装Zookeeper


		zookeeper版本     zookeeper-3.4.11.tar.gz
	
		拷贝zookeeper-3.4.11.tar.gz到/opt下，并解压缩
	
		然后进入/opt/zookeeper-3.4.11/conf/zoo_sample.cfg   到同一个目录下改个名字叫zoo.cfg
		cp zoo_sample.cfg zoo.cfg
		
		然后我们cd到Zookeeper的启动服务目录中
		cd /opt/zookeeper-3.4.11/bin/
	
		然后启动服务
		./zkServer.sh start 
	
		然后查看是否启动成功
		./zkServer.sh status
	    
		然后返回之后Zookeeper就启动成功了

## 3）：然后我们来安装我们的Dubbo-Admin

	
	
		首先Dubbo需要tomcat的支持，如果tomcat7的话要配置jdk7版本（请自行百度Dubbo和tomcat的版本的兼容问题）
		
		首先我们先在opt目录下下载好tomcat8.0的版本，然后解压他
	
		然后我们在当前的opt下面的tomcat的webapps把我们的Dubbo的war包房放进去
	
		然后启动tomcat
	
		cd .. 退回到上级目录
		cd bin 进入到tomcat的bin目录然后启动tomcat
	
		./startup.sh 
		这样就启动了，然后我们去tomcat的目录下发现多了个Dubbo-adminXXX.的东西
		然后我们访问tomcat，访问成功之后，然后我们去访问webapps下面的那个dubbo的文件名字，
	
		可以改的例如dubbo-admin-2.6.0我们给他改名字
	
		mv dubbo-admin-2.6.0 dubbo-admin
		就把他的名字改成dubbo-admin了
	
		然后我们来访问他
		例如我们的ip是192.168.44.177
		那么我们就访问
		
		http://192.168.44.177:8080/dubbo-admin
		
		记住要输入密码的。默认用户为root密码为root，可以去 tomcat的webapps下面的 /dubbo-admin/WEB-INF/dubbo.properties里面修改
		
		http://192.168.44.177:8080/dubbo-admin
		这样就成功的安装好了dubbo-admin监控



# 使用SpringBoot集成Dubbo环境



​	首先我们先来搭建我们的生产者

​	构建springboot生产者项目项目



​	

​	

​		