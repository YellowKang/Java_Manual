# Mongo简介

​	MongoDB最明显的优势之一就是[文档](https://docs.mongodb.com/manual/core/document/?_ga=2.170372647.668462152.1555152647-1197570158.1502203710)数据模型。它在模式设计和开发周期中均提供了很大的灵活性。用MongoDB文档可以很容易地处理那些不知道之后会需要哪些字段的场景。然而，有些时候当结构是已知的，并且能够被填充或扩充时，会使设计简单得多。 

​	NoSQL指的是非关系型数据库

​	在Mongo中database和MySQL的database是类似的

​	collection等价于MySQL的表

​	document等于MySQl的每一行的数据row

# 数据库用户管理

user为用户名，pwd为用户密码，role为角色，db为数据库

```
db.createUser({user:"root",pwd:"bigkang",roles:[{role:'dbOwner',db:'test'}]})
```

```
数据库用户角色：read、readWrite;
数据库管理角色：dbAdmin、dbOwner、userAdmin；
集群管理角色：clusterAdmin、clusterManager、clusterMonitor、hostManager；
备份恢复角色：backup、restore；
所有数据库角色：readAnyDatabase、readWriteAnyDatabase、userAdminAnyDatabase、dbAdminAnyDatabase
超级用户角色：root  
这里还有几个角色间接或直接提供了系统超级用户的访问（dbOwner 、userAdmin、userAdminAnyDatabase）
内部角色：__system
```

```
Read：允许用户读取指定数据库
readWrite：允许用户读写指定数据库
dbAdmin：允许用户在指定数据库中执行管理函数，如索引创建、删除，查看统计或访问system.profile
userAdmin：允许用户向system.users集合写入，可以找指定数据库里创建、删除和管理用户
clusterAdmin：只在admin数据库中可用，赋予用户所有分片和复制集相关函数的管理权限。
readAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读权限
readWriteAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读写权限
userAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的userAdmin权限
dbAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的dbAdmin权限。
root：只在admin数据库中可用。超级账号，超级权限
```

```
docker run --name mongotest -d \
-p 27017:27017 \
--privileged=true \
-v /docker/mongo/conf:/data/configdb \
-v /docker/mongo/data:/data/db \
docker.io/mongo:3.4.9 mongod -f /data/configdb/mongo.conf
```







# 添加

### 添加单条数据

给testas集合添加一条数据

```
db.testas.save({
    "name":"bigkang",
    "like":"游戏，股票，吃喝玩乐，Java",
    "age":19,
    "email":"1360154205@qq.com"
})
```

这样就添加了一行数据

```
我们再来查看数据
db.testas.find()
```

### 添加多条数据

下面我们来批量添加多个，我们使用insertMany方法，添加一个name为test1的一个name为test2的

```
db.testas.insertMany([{"name":"test1"},{"name":"test2"}])
```

### 添加字段

给testas这个表，nice字段是123的数据，添加一个字段haode，值为PDF，multi:true表示修改所有nice为123的，如果不设置则默认修改一行

```
db.testas.update({"nice":"123"},{$set:{"haode":"PDF"}},{multi:true})

db.acc.update({},{$set:{"domestic":false}},{multi:true})
不存在isDomestic这个字段的数据添加isDomestic这个字段为true
db.acc.update({"domestic":{$exists:false}},{$set:{"domestic":true}},{multi:true})
```

多条件添加字段

db.acc.update({"province":"国外"},{$set:{"domestic":"true"}},{multi:true})

db.acc.update({},{$set:{"domestic":true}},{multi:true})

```
db.getCollection('accident').update({"hangye":{"$exists":false},"atype":"其他"},{$set:{"hangye":"其他"}},{multi:true})

db.getCollection('accident').find({"hangye":{"$exists":false}})
```



# 查询

### 简单查询

查询用户姓名等于

查询testas这个表中，nice这个字段存在的数据

```
db.testas.find({ "nice":{$exists:true}})
```

### 字段值查询

查询这个字段的所有的值类型，比如一共有2000条数据，他们的type有8个类型，admin，user，，，，等等我们把这些数据全部查询出来

```
db.getCollection('accident').distinct("type")
```



# 删除

### 删除单条数据

删除name为test2的数据只删除一条，第一个括号为条件，第二个为设置

```
db.testas.deleteOne(
{
    "name":"test2"
}
)
```

### 删除多行数据

```
db.testas.deleteMany(
{
    "name":"test2"
}
)
```

### 删除字段

也就是unset，然后不给条件为从上往下删除表中的haode字段，multi为全部删除

```
db.testas.update({},{$unset:{"haode":""}},{multi:true})
```



# 修改

### 修改字段名

修改testas表中的nice字段，为names

```
db.testas.update({}, {$rename:{"nice":"names"}}, false, true);
```

### 修改字段值

修改testas表中的phonename为小米的，修改phonename字段为华为

```
db.testas.update( { "phonename" : "小米" } , { $set : { "phonename" : "华为"} },false,true)
```





```
db.getCollection('acc').update({"deleted":true},{$set:{"deleted":false}},{multi:true})
修改删除为true的数据为false

db.acc.update({"domestic":{"$exists":false}},{$set:{"domestic":true}},{multi:true})
修改domestic不存在的字段插入domestic为true


```

# 聚合查询

和MySQL对比

| SQL 操作/函数 |    mongodb聚合操作     |
| :-----------: | :--------------------: |
|     where     |         $match         |
|   group by    |         $group         |
|    having     |         $match         |
|    select     |        $project        |
|   order by    |         $sort          |
|     limit     |         $limit         |
|     sum()     |          $sum          |
|    count()    |          $sum          |
|     join      | $lookup  （v3.2 新增） |

我们可以看到这些查询其实差别并不是很大只是换了个语法而已

下面我们就来实际使用一下吧



### group

这里的group 和mysql的group by一样，我们来看下使用吧

```
db.accident.aggregate([
   	{$group:{
   		_id: "统计条数",
   		count: {$sum:1}
     } 
    },
    {
     $sort:{deathnumber:1}
    }
])    
```

这里是查询所有的数量进行返回

然后我们来看一下根据所有的类型进行聚合吧，我们根据所有的大类进行统计，并且统计每个大类的和

```
db.accident.aggregate([
   	{$group:{
   		_id: "$atype",
   		count: {$sum:1}
     } 
    },
    {
     $sort:{deathnumber:1}
    }
])    
```

我们可以看到他将所有的类（atype）进行了统计，将所有类型查询出来并且查询数量

### match

match和mysql中的where条件相类似，下面我们来看下使用吧,我们查询汽车大类，并且按照他的二级分类进行聚合然后统计，并且根据汽车数量数排序

```
db.accident.aggregate([
    {$match:{
        "atype":"汽车"
     }
    },
    {$group:{
   		_id: "$atype2",
   		count: {$sum:1}
     } 
    },
    {
     $sort:{count:1}
    }
])
```

### project

project就是控制我们所查询的数据是否进行展示，例如我不想展示id了，这样既可取消展示id

```
    
db.accident.aggregate([
    {$match:{
        "atype":"煤矿"
     }
    },
    {$group:{
        _id: "$atype2",
        count: {$sum:1}
     } 
    },
    {
     $sort:{deathnumber:1}
    },
    {$project: {
        "count":1,"_id":0
     }
    }
])
```

### sort

sort是排序，我们可以根据字段进行排序，1为升序，-1为降序，我们再更具count进行排序

```
    
db.accident.aggregate([
    {$match:{
        "atype":"汽车"
     }
    },
    {$group:{
        _id: "$atype2",
        count: {$sum:1}
     } 
    },
    {
     $sort:{count:-1}
    },
    {$project: {
        "count":1,"_id":0
     }
    }
])
```

### limit

我们只想统计前几个部门的数据那么我们直接查询进行limit，我们排序后设置limit大小即可，还有配合skip就能坐到分页

```
    
db.accident.aggregate([
    {$match:{
        "atype":"汽车"
     }
    },
    {$group:{
        _id: "$atype2",
        count: {$sum:1}
     } 
    },
    {
     $sort:{count:-1}
    },
    {$project: {
        "count":1,"_id":0
     }
    },
    {$limit: 10}
])
```

### push

比如我们统计了这个部门的员工，但是我们需要把他的员工Id给拿出来。并且跟随数据一起返回。例如这个部门有30个人，我需要这个部门的30个人的员工id

```
db.accident.aggregate([
    {$group:{
        _id: "$atype",
        count: {$sum:1},
        获取所有id: {$push:"$deathnumber"}
     } 
    }
])
```

### first以及last

```
db.accident.aggregate([
    {$group:{
        _id: "$atype",
        count: {$sum:1},
        第一个: {$first:"$atype"},
        最后一个: {$last:"$atype"}
        
     } 
    }
])
```

### sum以及其他

下面我们来进行统计了，先把上面的去掉避免太长

简单的查询如下

```
db.accident.aggregate([
    {$group:{
        _id: "$atype",
        count: {$sum:1}
     } 
    }
])
```

每条数据都有汽车的汽车数量我们分别根据汽车数量统计每个大类的，汽车数总和，汽车数平均值，最大值，和最小值

```
db.accident.aggregate([
    {$group:{
        _id: "$atype",
        count: {$sum:1},
        汽车数总和: {$sum:"$atype2"},
        汽车数平均: {$avg:"$atype2"},
        汽车数最大: {$max:"$atype2"},
        汽车数最小: {$min:"$atype2"}
     } 
    }
])
```

### 查询出来所有的id

由于数据太大，这个id我们用汽车类，也就是聚合所有不同的价格将它做成列表

```
db.accident.aggregate([
    {$group:{
        _id: "$atype2",
        count: {$sum:1},
        汽车数总和: {$sum:"$price"},
        汽车数平均: {$avg:"$price"},
        汽车数最大: {$max:"$price"},
        汽车数最小: {$min:"$price"},
        汽车列表: {$addToSet:"$price"}
     } 
    }
])
```

Mongo相对应的复杂查询统计使用聚合

查询语句如下，我们根据accident这个集合然后进行聚合，聚合的查询条件为，atype为煤矿的，然后聚合的字段是atype2，然后我们定义一个count用来记录他的数量，然后统计总价，根据每个atype2统计汽车数，然后将所有的省全部都拼到一起，并且排序汽车数，1为升序，-1为降序

```sql
db.accident.aggregate([
	{$match: {"atype":"汽车"}},
    {$group: { 
    	_id: "$atype2",
    	count:{$sum:1}, 
    	price: { $sum: "$price" },
    	province:{$addToSet:"$province"}} 
    },
    {$sort:{count:1}}
])
```

### 按照时间聚合统计

```sql
db.minehn_fault_repair.aggregate([
    {
        $project: {
             time: { "$dateToString": { format: "%Y-%m-%d", date: "$planDate" } },
        }
    },
    { $group: { _id: "$time", count: { $sum: 1 } } },
    { $sort: { "_id": -1 } }
])
```

### 聚合时间以及类型多重统计

```
db.supervise_process.aggregate([
    {$project:{hosr:"$hosr",type:"$type"}},
    {$group:{_id:{hosr:"$hosr",type:"$type"},count:{$sum:1}}},
    {$sort:{"_id.hosr":-1}}
])
```



秒转时间，再统计死亡人数总和

```
db.accident.aggregate([
    {$match: {"atype":"建筑施工","content":/工地/,"atime":{"$gte":1451577600}}},
     {$project:{
        atime:{"$multiply":["$atime", 1000]},
        deathnumber : "$deathnumber"
    }},
     {$project:{
        atime:{"$add":[ new Date(0), "$atime"]},
        deathnumber : "$deathnumber"
    }},
    {
        $project: {
              yue: { "$dateToString": { format: "%Y年", date:"$atime"} },
             deathnumber : "$deathnumber"
        }
    },
    { $group: { _id: "$yue", count: { $sum: "$deathnumber" } } },
    { $sort: { "_id": -1 } }
])
```





将时间类型的毫秒+上8小时的时差，并且格式化成年

```
db.acc.aggregate([
    {$match: {"atype":"建筑施工","content":/工地/,"atime":{"$gte":1451577600}}},
    {
        $project: {
                 yue: { "$dateToString": { format: "%Y年", date:{ "$add": ["$adate",28800000]}} },
             deathnumber : "$deathnumber"
        }
    },
    { $group: { _id: "$yue", count: { $sum: "$deathnumber" } } },
    { $sort: { "_id": -1 } }
])
```

# Mongo脚本

统计后累加返回

```
 var c1 = db.acc.find({"atype":"建筑施工","content":/建筑工地/,"sgjb":{"$in":["重大事故","特大事故"]}}).count()
  var c2 = db.acc.find({"content":/地铁/,"sgjb":{"$in":["重大事故","特大事故"]}}).count()
  var c3 = db.acc.find({"content":/管道/,"sgjb":{"$in":["重大事故","特大事故"]}}).count()
 
 print(c1 + c2 + c3)
```



​		循环遍历修改

```javascript
# 首先查询数据保存到data中
var data = db.getCollection('dataSource').find({})
# forEach循环遍历
data.forEach(function(item){
		# 设置元素item的age为18
    item.age = 18
    # 将元素添加回去（保存操作）
    db.dataSource.save(item)
})
```

​		执行时删除注释否则报错

```
var data = db.getCollection('dataSource').find({})
data.forEach(function(item){
    item.age = 18
    db.dataSource.save(item)
})
```

