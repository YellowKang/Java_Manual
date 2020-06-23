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
mongodump -h 192.168.1.11 --port 20168 -u minexhb -p minexhb123 -d minexhb-db -o  /Users/bigkang/Documents

mongodump --port 27018 -u minexhb -p minexhb123 -d minexhb-db -o  /download/xhb
```

## 导入数据

选择需要导入的数据库

```
mongorestore -h 39.108.168.33 --port 27017 -u minexhb -p minexhb123 -d minexhb-db --dir /Users/bigkang/Documents

mongorestore  -h 192.168.1.11 --port 20168 -u minexhb -p minexhb123 -d minexhb-db --dir /Users/bigkang/Documents/Data/xhb/minexhb-db
```



# MongoeExport带条件导出数据

## 导出



```
mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c acc -u anjian -p topcom123 -f content -q {"content":/溺水/,"atime":{"$gte":1514736000}} --type csv -o C:\Users\topcom\Desktop\mine_base.csv
```



```
导出json
mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c accident -u anjian -p topcom123 -f content -q {"content":/电/}  -o C:\Users\topcom\Desktop\accident-电.json



mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c company_business_info -u anjian -p topcom123  -o C:\Users\topcom\Desktop\company_business_info.json


导出csv
mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c accident -u anjian -p topcom123 -f content -q {"content":/电/} --type csv -o C:\Users\topcom\Desktop\mine_base.csv


mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c accident -u anjian -p topcom123 -q '{"province":{"$in":["湖南","河南","山东","河北","浙江"]},"atime":{"$gte":1451577600},"atype":{"$ne":"道路运输"}}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o /Users/bigkang/Documents/Data/utf8/直报数据.csv

mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c acc -u anjian -p topcom123 -q '{"province":{"$in":["湖南","河南","山东","河北","浙江"]},"atime":{"$gte":1451577600},"atype":{"$ne":"道路运输"}}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o /Users/bigkang/Documents/Data/utf8/5省除道路运输.csv




mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c acc -u anjian -p topcom123 -q '{"content":{"$regex":"地铁"},"domestic":true}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o /Users/bigkang/Documents/Data/utf8/地铁.csv

mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c acc -u anjian -p topcom123 -q '{"content":{"$regex":"管道"},"domestic":true}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o /Users/bigkang/Documents/Data/utf8/管道.csv

mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c acc -u anjian -p topcom123 -q '{"atype":"建筑施工","content":{"$regex":"建筑工地"},"domestic":true}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o /Users/bigkang/Documents/Data/utf8/房屋建筑.csv

mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c acc -u anjian -p topcom123 -q '{"$or":[{"atype":"建筑施工","content":{"$regex":"建筑工地"}},{"content":{"$regex":"地铁"}},{"content":{"$regex":"管道"}}],"sgjb":{"$in":["重大事故","特大事故"]}}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o  /Users/bigkang/Documents/Data/utf8/原因.csv


mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c acc -u anjian -p topcom123 -q '{"content":{"$regex":"溺水"},"atime":{"$gte":1514736000}}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o  /Users/bigkang/Documents/Data/utf8/acc溺水.csv


mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c accident -u anjian -p topcom123 -q '{"content":{"$regex":"溺水"},"atime":{"$gte":1514736000}}' -f originaltime,atype,cityinfo,deathnumber,sgjb,content --type csv -o  /Users/bigkang/Documents/Data/utf8/accident溺水.csv




iconv -f UTF8 -t gb18030 5省除道路运输.csv > 5省除道路运输_GBK.csv

iconv -f UTF8 -t gb18030 accident溺水.csv > accident溺水_GBK.csv
iconv -f UTF8 -t gb18030 acc溺水.csv > acc溺水_GBK.csv

iconv -f UTF8 -t gb18030 地铁.csv > 地铁_GBK.csv
iconv -f UTF8 -t gb18030 管道.csv > 管道_GBK.csv
iconv -f UTF8 -t gb18030 房屋建筑.csv > 房屋建筑_GBK.csv


-h ： 主机
--port ：端口号
-d ：数据库名
-c ：表名
-o ：输出的文件名
--type ： 输出的格式，默认为json
-f ：输出的字段，如果-type为csv，则需要加上-f "字段名"
-q ：输出查询条件
```

根据时间条件导出数据

```
mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c briefing -u anjian -p topcom123 -q  '{"dateCreated":{$gte:new Date(1570763405000)}}  -o E:/mongo/bf2.json
```

## 导入

```
mongoimport  -h 192.168.1.11 --port 20168 -d anjian-db -c coal_riskprobability -u anjian -p topcom123 --type csv --headerline --ignoreBlanks --file C:\Users\topcom\Documents\coalRiskProbability.csv

  -h 192.168.1.11 --port 20168 -d anjian-db -c mine_base -u anjian -p topcom123

mongoimport  -h 39.108.158.33 --port 27017 -d test -c coalRiskProbability -u bigkang -p bigkang --file C:\Users\topcom\Desktop\导出数据\风险预测\coal_riskprobability.json



--type csv --headerline --ignoreBlanks --file 
```

相关博客

<https://www.cnblogs.com/lingwang3/p/6567857.html>



# 数据操作

## 修改字段名

db.getCollection('synonymsList').update({}, {$rename : {"name_status" : "status"}}, false, true)

# Mongo索引优化



## 语句分析

​				我们使用mongo的性能分析器explian，他和我们mysql中的explian关键字是一样的，他们的效果也类似，都是分析此次sql执行的效率以及性能分析等等。

​				我们随便拿一个集合来进行测试。

```

```



## 创建索引



## 查询索引

我们使用getIndexes方法即可查询相应集合的索引

```
db.{集合名称}.getIndexes()
```

返回的数据如下

```
[
    {
        "v" : 2,
        "key" : {
            "_id" : 1
        },
        "name" : "_id_",
        "ns" : "minexhb-db.sys_data_history"
    },
    {
        "v" : 2,
        "key" : {
            "dateModified" : -1.0,
            "sysType" : 1.0,
            "did" : 1.0
        },
        "name" : "dateModified_-1_sysType_1_did_1",
        "ns" : "minexhb-db.sys_data_history"
    }
]
```

集合的_id默认是添加了索引的，我们自己添加了一个索引就是第二个，下面我们来解释下他们的意思吧

```
v 									表示索引的版本，默认为1，我添加了一个所以版本变成了2，每次修改新增版本

key									key表示添加的索引，如第二个，我们添加了组合索引，有3个字段
														dateModified（修改时间）			-1     -1表示倒排
														sysType（系统类型）			  		 1		 1表示顺排
                            did（设备id)									 1		 1表示顺排
                            
name								name表示索引的名称，如果创建时不指定则按照  字段名[0]_排序规则_字段名[2]...

ns									表示所在的哪个库的哪个集合的索引当前为minexhb-db库的sys_data_history集合
```







```
 mongodump -h 192.168.1.11 --port 20168 -u topcom -p topcom123 -d minehn-db -o /Users/bigkang/Documents
```





```
mongoexport -h 192.168.1.11 --port 20168 -d anjian-db -c accident -u anjian -p topcom123  --type csv -f _id,adate,atype,atype2,province,sgjb,deathnumber -o /Users/bigkang/Documents/工具/accident.csv
```



```
echo "[Unit]

Description=Docker Application Container Engine

Documentation=https://docs.docker.com

After=network-online.target firewalld.service

Wants=network-online.target

[Service]

Type=notify

# the default is not to use systemd for cgroups because the delegate issues still

# exists and systemd currently does not support the cgroup feature set required

# for containers run by docker

ExecStart=/usr/bin/dockerd

ExecReload=/bin/kill -s HUP $MAINPID

# Having non-zero Limit*s causes performance problems due to accounting overhead

# in the kernel. We recommend using cgroups to do container-local accounting.

LimitNOFILE=infinity

LimitNPROC=infinity

LimitCORE=infinity

# Uncomment TasksMax if your systemd version supports it.

# Only systemd 226 and above support this version.

#TasksMax=infinity

TimeoutStartSec=0

# set delegate yes so that systemd does not reset the cgroups of docker containers

Delegate=yes

# kill only the docker process, not all processes in the cgroup

KillMode=process

# restart the docker process if it exits prematurely

Restart=on-failure

StartLimitBurst=3

StartLimitInterval=60s

 

[Install]

WantedBy=multi-user.target" > /etc/systemd/system/docker.service
chmod +x /etc/systemd/system/docker.service
systemctl daemon-reload
echo '{
	"graph":"/data/docker",
	"disable-legacy-registry": true
}' > /etc/docker/daemon.json
systemctl start docker
```





# 脚本清洗修改

```
db.sys_data_history.find({"did":"5eb64ba13610300007d9f690","atime":{"$exists":false}}).forEach(function(item){
              var time =item.dateModified;
              var atime = NumberInt(time.valueOf() / 1000);
              db.sys_data_history.update({"_id":item._id} , { $set : { "atime":atime} },false,true)
       
    });
```





```
        // 判断父类是否查询map
        if (q.getClass().getSuperclass().equals(ScaffoldBaseMap.class)) {
            // 判断是否需要指定字段查询
            for (Field field : fields) {
                field.setAccessible(true);
                MongoSearch annotation = field.getAnnotation(MongoSearch.class);
                if (annotation != null && annotation.type() == MongoSearchType.FIELD) {
                    try {
                        Object o = field.get(q);
                        if (o != null) {
                            ArrayList list = (ArrayList) o;
                            if (list.size() > 0) {
                                queryBuilder = new QueryBuilder();
                                BasicDBObject basicDBObject = new BasicDBObject();
                                list.forEach(v -> {
                                    basicDBObject.put((String) v, 1);
                                });
                                //初始化query对象
                                query = new BasicQuery(queryBuilder.get().toString(), basicDBObject.toJson());

                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
```

