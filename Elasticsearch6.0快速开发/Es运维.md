# Es的CPU暴涨

我们在前面输入es的url地址，后面加上_nodes/hot_threads?pretty，可以跟踪节点的线程日志，方便快速定位问题

http://192.168.1.14:20469/_nodes/hot_threads?pretty

# Es信息

我们可以访es的restful的接口查询信息

我们访问

```
http://ip地址:9200/_cat
```

就可以看到很多的命令

![](img\Es operations-1.png)

