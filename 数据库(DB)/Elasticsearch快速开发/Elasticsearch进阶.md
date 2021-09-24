# 集群

## 添加集群节点

​		我们在使用Elasticsearch的时候，例如我们以前有一个集群，叫做log-cluster，现在我们需要向这个集群中添加两台机器。

​		那么我们添加节点的时候首先需要修改配置文件

​		首先我们先修改集群名称，以及节点名称，我们这里准备添加一个data节点，一个master节点

​		首先是第一个data节点如下，我们需要修改的项有：

```
# 1、修改集群名称
cluster.name: log-cluster
# 2、修改节点名称
node.name: node-data6

# 3、设置节点角色为data
node.master: false 
node.data: true 
node.ingest: false 

# 4、修改集群服务发现的其他Es节点地址，Transport端口
discovery.zen.ping.unicast.hosts: ["192.168.1.12:9301","192.168.1.13:9302","192.168.1.14:9303"]
```

​		再修改第二个master节点的配置

```
# 1、修改集群名称
cluster.name: log-cluster
# 2、修改节点名称
node.name: node-master4

# 3、设置节点角色为data
node.master: true 
node.data: false 
node.ingest: false 

# 4、修改集群服务发现的其他Es节点地址，Transport端口
discovery.zen.ping.unicast.hosts: ["192.168.1.12:9301","192.168.1.13:9302","192.168.1.14:9303"]
```

​		添加节点最主要的设置就是如下四个：

​					集群名称

​					节点名称

​					集群角色

​					其他节点地址

​		然后我们启动Es，他就会根据配置的服务发现的地址，集群名称，进行同步，我们就向log-cluster这个集群添加了一个data一个master节点，启动后data节点会和集群进行服务发现和数据同步的工作，这部分由Es可以自动完成。

## Es版本升级

​		Es官方给我们提供了非常多的升级策略如6.8的版本升级策略地址为：

​		我们这里写入想要升级的版本然后进入官网即可

```
https://www.elastic.co/guide/en/elasticsearch/reference/6.8/setup-upgrade.html
```

​		升级的类型大概有如下几种

|  原版本   | 升级版本 |                        支持的升级类型                        |
| :-------: | :------: | :----------------------------------------------------------: |
|   `5.x`   |  `5.y`   | [滚动升级](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/rolling-upgrades.html)（其中`y > x`） |
|   `5.6`   |  `6.x`   | [滚动升级](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/rolling-upgrades.html) [ [1](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/setup-upgrade.html#_footnotedef_1) ] |
| `5.0-5.5` |  `6.x`   | [完全集群重新启动](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/restart-upgrade.html) [ [1](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/setup-upgrade.html#_footnotedef_1) ] |
|  `<5.x`   |  `6.x`   | [重新索引升级](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/reindex-upgrade.html) |
|   `6.x`   |  `6.y`   | [滚动升级](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/rolling-upgrades.html)（其中`y > x`） |

​		除了小于5的版本的Es，其他的升级大部分都可以滚动升级不重启

​		那么下面给大家介绍一下这几种方式吧：

​				1、滚动升级

```
		滚动升级允许Elasticsearch集群一次升级一个节点，因此升级不会中断服务。不支持在升级持续时间内在同一集群中运行多个版本的Elasticsearch，因为无法将碎片从升级的节点复制到运行旧版本的节点。
		注意事项：
			1、如果在5.x集群上启用了Elasticsearch安全功能，则在进行滚动升级之前，必须使用SSL / TLS加密节点间的通信，这需要重新启动整个集群。有关此要求和关联的引导检查的更多信息，请参阅SSL / TLS检查。
			2、Kibana和X-Pack使用的内部索引使用的格式在6.x中已更改。从5.6升级到6.x时，必须先升级这些内部索引， 然后才能开始滚动升级过程。否则，升级后的节点将拒绝加入群集。
			3、如果要从6.3之前的版本升级并使用X-Pack，则必须先删除X-Pack插件，然后再使用进行升级 bin/elasticsearch-plugin remove x-pack。从6.3开始，X-Pack包含在默认发行版中，因此请确保升级到该版本。
			4、升级版本必须升级其插件版本
		概述：可以从集群中一次一个节点一个节点的升级，这样不需要重启服务
```

​				2、完全集群重启升级

```
		完整的集群重新启动升级要求您关闭集群中的所有节点，升级它们，然后重新启动集群。升级到6.x之前的主要版本时，需要完全重启群集。Elasticsearch 6.x支持 从Elasticsearch 5.6进行滚动升级。从早期版本升级到6.x需要完全重启群集。请参阅 升级路径表以验证您需要执行的升级类型。
		注意事项：
				除了上方的SSL/TLS忽略外其他也需要注意
		概述：先关闭整个集群，关闭完成后升级，然后重启整个集群
```

​				3、重新索引升级

```
		Elasticsearch可以读取在以前的主要版本中创建的索引。旧索引必须重新索引或删除。Elasticsearch 6.x可以使用在Elasticsearch 5.x中创建的索引，但不能使用在Elasticsearch 2.x或更早版本中创建的索引。Elasticsearch 5.x可以使用在Elasticsearch 2.x中创建的索引，但不能使用在1.x或更早版本中创建的索引。
		如果存在不兼容的索引，Elasticsearch节点将无法启动。
		
		注意事项：
				除了上方的SSL/TLS忽略外其他也需要注意
		概述：重新索引升级，需要删除或者重建索引，需要重建或者导入后删除，最为麻烦，主要是针对版本差异较大的Es
```



# 索引

