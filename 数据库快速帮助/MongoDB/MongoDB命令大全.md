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

# 添加

## 添加数据

### 添加单条数据

给testas集合添加一条数据

```
db.testas.save({
    "name":"bigkang",
    "like":"游戏，股票，吃喝玩乐，Java",
    "age":18,
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

## 添加字段

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

## 简单查询

查询用户姓名等于

查询testas这个表中，nice这个字段存在的数据

```
db.testas.find({ "nice":{$exists:true}})
```

## 字段查询

查询这个字段的所有的值类型，比如一共有2000条数据，他们的type有8个类型，admin，user，，，，等等我们把这些数据全部查询出来

```
db.getCollection('accident').distinct("type")
```



# 删除

## 删除单条数据

删除name为test2的数据只删除一条，第一个括号为条件，第二个为设置

```
db.testas.deleteOne(
{
    "name":"test2"
}
)
```

## 删除多行数据

```
db.testas.deleteMany(
{
    "name":"test2"
}
)
```

## 删除字段

也就是unset，然后不给条件为从上往下删除表中的haode字段，multi为全部删除

```
db.testas.update({},{$unset:{"haode":""}},{multi:true})
```



# 修改

修改字段名

修改testas表中的nice字段，为names

```
db.testas.update({}, {$rename:{"nice":"names"}}, false, true);
```

修改字段值

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


