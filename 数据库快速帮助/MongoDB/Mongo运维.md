# Mongo快速迁移所有数据（Docker版本）

## 挂载目录版本

​		针对有挂载目录的Docker或者没有挂载目录的Docker版本Mongo

挂载文件版本，首先docker  ps查询出docker 容器

docker ps | grep mongo

然后查询相应的容器的id

![](img\mongo-cp.png)

然后我们根据这个容器id查询它的详细信息找到挂载目录

docker inspect 容器id

![](img\mongo-cp2.png)

我们找到挂载目录将挂载目录复制到需要挂载的容器相应的目录即可

## 未挂载目录版本

如上所示查询mongo容器id

```
docker ps | grep mongo
```

然后使用docker cp 命令将容器中的data目录复制出来

```
docker cp  容器id:/data/db /root/test/data
```

将这个目录挂载到需要迁移的容器中或者挂载目录即可



# MongoDump迁移数据

## 导出数据

进入mongo的bin目录下，或者docker中的bin目录下

找到mongodump命令进行执行

输入用户名以及密码并且选择数据库然后选择导出路径即可

```
mongodump -h 127.0.0.1 -port 27017 -u root -p root123 -d my-db -o /tmp/mongo
```

## 导入数据

选择需要导入的数据库



```
mongorestore -h 127.0.0.1 -port 27017 -u root -p root123 -d my-db --dir /tmp/mongo
```



# MongoeExport带条件导出数据



## 导出

```
mongoexport -h 192.168.86.108 --port 27017 -d my-db -c my-collection -f id -q '{"id":{"$exists":true}}'  --csv -o 123


-h ： 主机
--port ：端口号
-d ：数据库名
-c ：表名
-o ：输出的文件名
--type ： 输出的格式，默认为json
-f ：输出的字段，如果-type为csv，则需要加上-f "字段名"
-q ：输出查询条件
```

## 导入

```
mongoimport -h 192.168.86.126 --port 27017 -d my-db -c my-collection --type csv --headerline -f _id --file 123
```

相关博客

<https://www.cnblogs.com/lingwang3/p/6567857.html>



# 数据操作

## 修改字段名

db.getCollection('synonymsList').update({}, {$rename : {"name_status" : "status"}}, false, true)