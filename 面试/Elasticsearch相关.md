# 什么是倒排索引？

​		倒排索引源于实际应用中需要根据属性的值来查找记录。这种索引表中的每一项都包括一个属性值和具有该属性值的各记录的地址。由于不是由记录来确定属性值，而是由属性值来确定记录的位置，因而称为倒排索引(inverted index)。带有倒排索引的文件我们称为倒排[索引文件](https://baike.baidu.com/item/索引文件)，简称[倒排文件](https://baike.baidu.com/item/倒排文件/4137688)(inverted file)。

​		**倒排索引**（英语：Inverted index），也常被称为反向索引、置入档案或反向档案，是一种索引方法，被用来存储在全文搜索下某个单词在一个文档或者一组文档中的存储位置的映射。它是文档检索系统中最常用的数据结构。通过倒排索引，可以根据单词快速获取包含这个单词的文档列表。倒排索引主要由两个部分组成：“单词词典”和“倒排文件”。

​		正排索引是通过DOC（文档）找WORD（关键词），例如我们想要通过ID为1的数据去查询，通过他的KEY去进行查询出VALUE。

​		倒排索引是通过WORD（关键字）找DOC（文档），例如我们想要查询带有小米的关键词的数据有哪些。通过他的VALUE去进行查询出KEY。

​		简单的来说

​				正排索引就是通过KEY找VALUE

​				倒排索引就是通过VALUE找KEY

​		

# Elasticsearch的主分片和副本集有设么区别以及作用？

​		我们知道Elasticsearch在建立Index的时候，会默认帮助我们建立主分片（shards）以及副本集（replicas），那么这个主分片（shards）以及副本集（replicas）到底是用来干什么的呢？

​		主分片（shards），我们知道Elasticsearch是一个天然的分布式搜索引擎，那么他是如何分布数据的呢？答案就是我们的shard分片,打个比方来说，我们有8条数据存储到Elasticsearch中，那么这8条数据会根据shard进行分片，例如我们有5个shard，那么这10条数据肯定会在这5个shard中分布，并且这5个shard中的数据都是不重复的。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1613985047061.png)

​		副本集（replicas），副本集很明显的可以看出来他是作为副本使用的，那么他的副本是什么副本呢？答案就是shard的副本，也就是前面的分片，比如说我们有5个shard，那么我们把他的副本集设置为1，那么一共就会有5 + 5 * 1 = 10个shard（5shard + 5副本），如果我们把副本设置为2那么则会有5 + 5 * 2 = 15个shard（5shard + 10副本），副本的数量我们可以把它理解为shard的数据备份，1就是备份一遍，2就是备份两遍，所以数据也会成倍的存储在Elasticsearch中，那么为什么需要使用到副本呢？我们打个比方我们的某一台节点存储了2个shard的数据，这个时候这个节点宕机了，那么我们就无法查询了，这个时候我们是不是可以找到其他机器上的这两个shard的副本进行使用。

​		Elasticsearch同一个shard的分片和副本不会存放在同一台服务器上，Elasticsearch会根据自己的算法进行分配，当单节点我们给他设置1个副本集，那么集群状态则会变黄，因为他的副本集无法分配到其他节点中，那么这几个副本则会一直无法分配导致出现无法分配的副本分片，所以集群状态会变成黄色。

​		示意图如下：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1613985103929.png)

# Elasticsearch数据写入流程？

​		例如我们像Elasticsearch写入（索引）一条数据，那么Elasticsearch是如何处理这个数据的呢？

​		首先我们写入一条数据

​					1、客户端选择一个node发送请求过去，这个node就是coordinating node（协调节点）

​					2、协调节点会将数据进行路由，然后将请求转发给对应的Node节点（primary shard主分片）

```
ES会根据传入的_routing参数（或mapping中设置的_routing, 如果参数和设置中都没有则默认使用_id), 按照公式shard_num = hash(\routing) % num_primary_shards,计算出文档要分配到的分片
```

​					3、Node的主分片，会将数据同时写入Buffer缓存，以及Translog日志中

```
Buffer缓存								快速的数据缓存，用于刷新写入数据
Translog日志							防止突然宕机，导致Buffer数据丢失
简单的来说Buffer缓存帮助我们写入数据，Translog帮助我们对数据进行容灾防止丢失，Es会先将数据写入Translog然后再写入Buffer缓存
数据写入  -》 写入Translog  -》 写入Buffer
```

​					4、Buffer缓存中的数据，每间隔1秒钟会进行一个reflush（刷新）操作数据写入到新的segment file，Lucene执行Open操作，将segment file打开，然后写入到OS Cache（系统缓存中），这个时候就已经可以查询到数据了

​					5、OS Cache调用fsync操作，将数据刷新到磁盘，至此数据写入缓存，并且落入磁盘，写入完成

```
Translog恢复的时候fsync操作我们可以修改成异步刷盘但是可能会导致数据丢失，详情参考https://www.elastic.co/guide/en/elasticsearch/reference/7.9/index-modules-translog.html
```

​		如下是Elasticsearch数据写入的流程图例：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1614049285231.png)

# Elasticsearch数据读取流程？

​		例如我们简单的根据ID进行获取数据，那么Elasticsearch是如何把这个ID的数据给我们查询出来的呢？

​		首先我们对某一个ID进行查询

​						1、客户端选择一个Node发送请求过去，这个Node就是Coordinating Node（协调节点）

​						2、协调节点会将数据进行路由，然后找到相应的shard的主分片和副本集，然后调用rund-robin随机轮询算法进行负载均衡

​						3、查询到Document后将数据返回给协调节点,协调节点再返回给客户端

