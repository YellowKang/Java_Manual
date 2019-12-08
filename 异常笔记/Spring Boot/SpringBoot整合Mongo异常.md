# 聚合异常

## 格式化时间引起SpEL异常

SpEl聚合project字符引起，我们如下，在Mongo我们直接执行这样的语句是没有问题的

```properties
db.sys_data_history.aggregate(
   [
     {
       $project: {
           "_id":1,
           "did":1,
           "dateModified":1,
           "sysType":1,
           "value":1,
           time: { $dateToString: { format: '%Y-%m-%d',date: {$add: ['$dateModified',28800000]}}}
       }
     },
     {$group:{_id:{date:"$time",sysType:"$sysType"},value:{"$sum":1}}},
     {$sort:{"_id":-1}}
   ]
)
```

如果使用MongoJpa

```java
pjOperation = 
  Aggregation.project("time")
  .andExpression("{ $dateToString: { format: '%H时',date: {$add: ['$dateModified',28800000]}}}")
 .as("time")
 .and("dateModified").as("dateModified").and("value").as("value").and("sysType").as("sysType").and("sid").as("sid");
```

如下使用格式化时间时添加8小时，则直接异常了

```
@65: EL1043E: Unexpected token. Expected 'rsquare(])' but was 'comma(,)'
```

这里是因为我们使用[]字符，在SpEL中[]表示参数占位符，如[0]+[1]表示第一个占位符拼接+然后第二个占位符，我们需要写成,我们把[]写成{}

```java
pjOperation = 
  Aggregation.project("time")
  .andExpression("{ $dateToString: { format: '%H时',date: {$add: {'$dateModified',28800000}}}}")
 .as("time")
 .and("dateModified").as("dateModified").and("value").as("value").and("sysType").as("sysType").and("sid").as("sid");
```

我们可以看到他经过了编译后还是[]所以对执行过程没有影响《真坑啊》！！！

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1574158961009.png)

