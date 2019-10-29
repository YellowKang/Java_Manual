# 整合MySQL

## 配置

首先我们需要配置jdbc的连接才能去使用我们找到配置的地方

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567481995837.png)

找到jdbc，并且编辑按钮，修改他的driver驱动，以及密码，然后修改url还有用户

切记加入mysql连接的依赖



![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567482024498.png)

![](img\jdbc-artifact.png)



```
com.mysql.jdbc.Driver
jdbc:mysql://xxx.xxx.xxx.xxx:3306/test
mysql:mysql-connector-java:5.1.39
```

然后我们就能来创建一个jdbc的数据可视化分析了

## 使用

创建一个NoteBook并且选择jdbc

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567482047973.png)

创建好之后我们随便展示一张表的数据吧,我们先编写一下查询的语句然后运行一下，然后选中展示方式即可

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567482060896.png)

# 整合Elasticsearch

## 配置

配置es的连接方式，然后集群名称，以及ip地址和端口号还有返回的大小

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567482076545.png)

## 使用

创建一个Es的NoteBook

然后查询语句如下，同样写好了查询语句之后，我们运行并且显示展示方式

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567482105567.png)



# settings的使用

我们可以看到这是我们查询出来的字段，我们按照id来进行展示，值就是id的总和统计，在使用饼图是分析比例，这里没有聚合字段，如果需要可以直接从上面拖入下面的框内即可

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567482165701.png)