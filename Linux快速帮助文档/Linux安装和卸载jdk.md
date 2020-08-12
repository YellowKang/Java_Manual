# 如何查看Linux是否存在jdk？

	rpm -qa | grep jdk
	
	然后就可以查看安装过的jdk了
	
	或者
	
	java  ，java --version都可以

# 如果发现有如何卸载？


	例如卸载java-1.7.0-openjdk-1.7.0.141-2.6.10.5.el7.x86_64
	
	rpm -e --nodeps 要卸载的程序	
	
	rpm -e --nodeps     java-1.7.0-openjdk-1.7.0.141-2.6.10.5.el7.x86_64


	也可以直接卸载所有jdk
	rpm -e --nodeps jdk

# 如何使用yum安装jdk

yum -y install java-1.8.0-openjdk-devel.x86_64
	使用

	yum list java*		查看所有的可安装的yum的jdk版本
	
	然后例如我们安装java01.8.0-openjdk-* 
   java-1.8.0-openjdk.x86_64
	yum install java01.8.0-openjdk-* -y

	然后安装完毕后使用java -version查看就可以了

yum install java-devel
安装javac 等等的环境，解决openjdk无法使用

# 如何使用压缩包安装jdk

创建jdk目录

```
mkdir -p /usr/jdk
```

将Jdk解压到相应目录

然后配置环境变量

```
export JAVA_HOME=/usr/jdk/jdk1.8.0_121
export JRE_HOME=${JAVA_HOME}/jre  
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib  
export PATH=${JAVA_HOME}/bin:$PATH
```

