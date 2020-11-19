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

# Es数据迁移

## Elasticsearchdump

先安装node.js   官网： https://nodejs.org/en/download/  选择版本 下载 安装 
执行命令：  nmp install
			nmp install elasticdump -g

### 导入

```http
--山东
elasticdump --output=http://10.212.1.33:20269/yuqing_2019_2/ --input=E:\卓越讯通\煤矿项目\山东煤监局\sd_month2.json --type=data    
 --后面这个有的版本加了报错  --headers='{"content-type": "application/json"}'
--河北
elasticdump --output=http://10.224.0.86:19200/yuqing_2019_6/ --input=E:\卓越讯通\煤矿项目\河北煤矿\hb_month625.json --type=data 
--新疆
elasticdump --output=http://172.35.0.33:20369/yuqing_2019_6/ --input=E:\卓越讯通\煤矿项目\新疆\xj_month621.json --type=data





# 老版本多type类型指定type无效问题，直接索引后添加
# 导入警度data类型的索引
elasticdump --output=http://192.168.1.16:19200/pisearch/data/ --input=/Volumes/BIGKANG/警度部署/镜像和数据/es/pisearchEsData.json
# 导入警度data类型的索引
elasticdump --output=http://192.168.1.16:19200/pisearch/document/ --input=/Volumes/BIGKANG/警度部署/镜像和数据/es/pisearchEsDocment.json
```

### 导出

Linux

```
--山东
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_2*/ --output=sd_month2.json  --searchBody  '{"query":{"bool":{"filter":[{"range":{"pubTime":{"gte":1548950400000,"lt": 1551369600000}}},{"match_phrase":{"content":"山东"}}]}}}' &
--河北   
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=hb_month617.json  --searchBody  '{"query":{"bool":{"filter":[{"range":{"pubTime":{"gte":1559318400000,"lt": 1561910400000}}},{"match_phrase":{"content":"河北"}}]}}}' &
--新疆
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=hb_month624.json  --searchBody  '{"query":{"bool":{"filter":[{"range":{"pubTime":{"gte":1560614400000,"lt": 1561910400000}}},{"match_phrase":{"content":"新疆"}}]}}}' &



# 导出警度data类型的索引
elasticdump --input=http://192.168.1.12:19200/pisearch/data/ --output=/Volumes/黄康/警度部署/镜像和数据/es/pisearchEsData.json
# 导出警度document类型的索引
elasticdump --input=http://192.168.1.12:19200/pisearch/document/ --output=/Volumes/黄康/警度部署/镜像和数据/es/pisearchEsDocment.json

```

Windows

```
--山东
 elasticdump --input=http://192.168.1.14:20269/yuqing_2019_2*/ --output=E:\卓越讯通\煤矿项目\山东煤监局\sd_month2.json  --searchBody  {\"query\":{\"bool\":{\"filter\":[{\"range\":{\"pubTime\":{\"gte\":1548950400000,\"lt\":1551369600000}}},{\"match_phrase\":{\"content\":\"山东\"}}]}}} &
--新疆
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=E:\卓越讯通\煤矿项目\新疆\xj_month621.json      --searchBody  {\"query\":{\"bool\":{\"filter\":[{\"range\":{\"pubTime\":{\"gte\":1559318400000,\"lt\":1561910400000}}},{\"match_phrase\":{\"content\":\"新疆\"}}]}}} &
--河北
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=E:\卓越讯通\煤矿项目\河北煤矿\hb_month625.json    --searchBody  {\"query\":{\"bool\":{\"filter\":[{\"range\":{\"pubTime\":{\"gte\":1561305600000,\"lt\":1561910400000}}},{\"match_phrase\":{\"content\":\"河北\"}}]}}} &
```

# 查询所有数据总数

```
curl -s -XGET http://192.168.1.14:20269/_cat/count?v
```

# 索引

## 修改副本数

```
PUT index01/_settings  {"number_of_replicas": 2}
```

## 查询分片以及副本信息

我们通过cat

```
http://182.61.2.16:19201/_cat/shards
```

## 重建索引

也可以叫做索引复制，我们将test001复制到test002，但是我们需要注意，重新索引不会从源索引复制设置。映射，分片计数，副本等必须提前配置。

```
POST _reindex
{
  "source": {
    "index": "test001"
  },
  "dest": {
    "index": "test002"
  }
}
```



# 信息查看

## 查看索引别名

​		索引是可以建立别名的，我们可以直接新建一个别名，查询的时候直接查询这个别名就行了，下面这个_cat/aliases端点可以帮助我们查看已经建立的别名

```http
http://182.61.2.16:19201/_cat/aliases
```

## 查看分配的资源

​		我们可以查看我们的Es所分配使用的资源

```http
http://182.61.2.16:19201/_cat/allocation?v
```

​		例如返回

```properties
shards disk.indices disk.used disk.avail disk.total disk.percent host        ip          node
     7       41.2kb     7.6gb     31.5gb     39.2gb           19 172.16.16.5 172.16.16.5 es-node1-master
     6         21kb     7.6gb     31.5gb     39.2gb           19 172.16.16.5 172.16.16.5 es-node2
     7       21.7kb     7.4gb     31.7gb     39.2gb           18 172.16.16.4 172.16.16.4 es-node3-master
```

```
shards													当前节点所分配的shard(包括primary shard和replica shard)
disk.indices										磁盘的使用大小（Es的索引以及数据大小）
disk.used												当前服务器所使用的磁盘大小
disk.avail											剩余的磁盘资源大小
disk.total											服务器上总共的磁盘大小
disk.percent										磁盘使用的占比率（百分比）
host														host地址
ip															ip地址
node														节点名称
```

​		



# 节点

## 查询节点信息

我们访问/_nodes端点

```http
http://182.61.2.16:19201/_nodes
```

可以查询出所有的ES的节点信息，以及节点的状态

我们也能过滤指定的节点信息，例如查询es-node1-master节点信息

```http
http://182.61.2.16:19201/_nodes/es-node1-master/
```

查看节点状态

```http
http://182.61.2.16:19201/_nodes/es-node1-master/nodes
```

返回如下信息

```json
{
  	# 节点信息
    "_nodes": {
  			# 节点数
        "total": 1,
  			# 正常节点
        "successful": 1,
  			# 失败异常节点
        "failed": 0
    },
		# 集群名称
    "cluster_name": "souti-cluster",
    "nodes": {
        "E02OO1rOQH2-_JbI2DVKaw": {
          	# 节点名称
            "name": "es-node1-master",
          	# IP信息
            "transport_address": "172.16.16.5:19301",
            "host": "172.16.16.5",
            "ip": "172.16.16.5",
          	# ES版本
            "version": "6.8.10",
            "build_flavor": "default",
          	# 构件类型
            "build_type": "docker",
            "build_hash": "537cb22",
          	# 节点角色
            "roles": [
                "master",
                "data",
                "ingest"
            ],
            "attributes": {
      					# 机器内存
                "ml.machine_memory": "4142190592",
      					# xpack是否安装
                "xpack.installed": "true",
      					# 最大打开任务数量
                "ml.max_open_jobs": "20",
      					# 是否启用
                "ml.enabled": "true"
            }
        }
    }
}
```

还有其他的查询类型

```http
http://182.61.2.16:19201/_nodes/es-node1-master/jvm					#查询jvm信息
http://182.61.2.16:19201/_nodes/es-node1-master/settings		#查询设置信息
http://182.61.2.16:19201/_nodes/es-node1-master/os					#查询系统信息等等
http://182.61.2.16:19201/_nodes/es-node1-master/stats				#统计节点信息
```



## 节点线程信息查询

我们可以使用/_nodes/hot_threads端点来查询我们的节点的线程信息

```http
http://182.61.2.16:19201/_nodes/hot_threads
```

我们还能通过节点名称进行过滤，如我们现在查询的es-node1-master这个节点

```http
http://182.61.2.16:19201/_nodes/es-node1-master/hot_threads
```



像线程信息我们可以指定参数进行查询

```http
threads												返回的线程数					
			
			这里我们可以指定返回几条线程的信息如返回5条（默认值3条），示例如下
					http://182.61.2.16:19201/_nodes/es-node1-master/hot_threads?threads=5
					
interval											线程信息采集间隔

			这里表示我们采集线程的时间间隔，多少毫秒搜集一次（默认500ms）,我们这里两秒钟搜集一次，示例如下
					http://182.61.2.16:19201/_nodes/es-node1-master/hot_threads?interval=2s
					
type													线程类型

			这里表示我们线程的采集类型有cpu，wait，block（默认为cpu）,我们这里采集阻塞的线程
					http://182.61.2.16:19201/_nodes/es-node1-master/hot_threads?type=block

ignore_idle_threads						是否过滤空闲线程
		
			这里表示我们是否过滤掉闲置的线程，也就是运行未工作的线程（默认true），我们这里选择不过滤
					http://182.61.2.16:19201/_nodes/es-node1-master/hot_threads?ignore_idle_threads=false
			
```

# 集群

## 集群健康查询

查询集群健康情况

```
http://182.61.2.16:19201/_cluster/health
```

返回如下信息

```json
{
  	# 集群名称
    "cluster_name": "souti-cluster",
  	# 集群状态
    "status": "green",
  	# 是否超时
    "timed_out": false,
  	# 节点数量
    "number_of_nodes": 3,
  	# 数据节点数量
    "number_of_data_nodes": 3,
  	# 活动的主分片数量
    "active_primary_shards": 10,
  	# 活动的分片数量
    "active_shards": 20,
  	# 重新定位的分片
    "relocating_shards": 0,
  	# 初始化的分片
    "initializing_shards": 0,
  	# 未分配的分片
    "unassigned_shards": 0,
  	# 延迟未分配的分片
    "delayed_unassigned_shards": 0,
  	# 挂起的任务数量
    "number_of_pending_tasks": 0,
  	# 飞行的数量
    "number_of_in_flight_fetch": 0,
  	# 在队列millis中等待的任务最大值
    "task_max_waiting_in_queue_millis": 0,
  	# 活动的shard百分比
    "active_shards_percent_as_number": 100
}
```

## 集群状态查询

此处信息过多未做概述

```http
http://182.61.2.16:19201/_cluster/state
```

## 集群统计查询

集群统计信息

```http
http://182.61.2.16:19201/_cluster/stats
```

## 集群挂起任务

查询集群挂起的任务

```http
http://182.61.2.16:19201/_cluster/pending_tasks
```

## 集群重新设置路由

```http
http://182.61.2.16:19201/_cluster/reroute
```

## 集群更新设置

```http
http://182.61.2.16:19201/_cluster/settings
```

