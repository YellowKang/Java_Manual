## 1、为什么需要不同的配置环境呢？

```
在项目的开发中，我们通常分为很多个环节，例如：

	开发环节，测试环节，生产环节等等

	例如开发环节的端口号和数据库是一个，
	测试和生产的端口号和数据库又是另一个  

这个时候的配置都不一样所以我们需要去引用同的配置文件
```

## 2、如何使用不同环境下的配置文件呢？

```
1：）首先我们先创建一个配置文件

	application-XXX.properties

	例如创建一个：dev环境的配置文件
	并在其中配置端口号

	application-dev.properties
```

```
	server.port=8010
```

```
2：）然后我们在application中引入其他的环境
```

```
	注意：application-的名字一定要和dev一样

	加上spring.profiles.active=dev

	这样就成功的配置了dev环境的配置文件
```





```
还有另一种方式更为简单
这样使用配置块也能进行使用

注意：此方式只适配yml格式的配置文件
```

## 3、配置文件的加载顺序？

```
默认的如果不设置的话就是加载resources下的application.properties

一般来说顺序为：
```

```
	（文件路径指，当前项目的根路径）
	文件路径分别为根路径下的config目录，和/目录，会首先记载/config   然后加载/  

		文件路径的：   /config  			/
```

```
	（类路径指，src/main/java或者resoucres等等）
	类路径分别为根路径下的classpath:/config/，和classpath:/,先加载classpath:/config/然后加载classpath:/   

		类路径的：    classpath:/config/        	classpath:/
```

```
会首先加载文件路径，然后加载类路径，也就是

/config     ---->	/	---->	classpath:/config/  	----> classpath:/
```



```
高优先级的配置文件会覆盖低路径的配置文件（这里的代替指的是相同的配置，如果配置都不相同，则会将所有的配置文件都加载了）
```

```
外部命令行指定端口号：

	cmd中：

		java -jar sprning-oot-0.0.1Xxxx.jar --server.port=80
```

```
	这个的顺序是最高的

然后还可以在当前的jar包目录的地方新建一个配置文件application.properties，然后直接启动，他会加载当前路径的配置文件

（官方文档的方式更多），可以通过很多种手段进行配置加载时的修改
```



spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    username: root
    password: 123
    url: jdbc:mysql://localhost:3306/kang?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false
    driver-class-name: com.mysql.cj.jdbc.Driver
  profiles:
    active: dea

这里用三个 - 来隔开

------

server:
  port: 1254

spring:
  profiles: dea

------

server:
  port: 999

spring:
  profiles: dec

