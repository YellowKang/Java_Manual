# 什么是Elasticsearch？

​		ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTfulweb接口，ElasticSearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。构建在全文检索开源软件Lucene之上的Elasticsearch，不仅能对海量规模的数据完成分布式索引与检索，还能提供数据聚合分析。据国际权威的数据库产品评测机构DBEngines的统计，在2016年1月，Elasticsearch已超过Solr等，成为排名第一的搜索引擎类应用

​		概括：基于Restful标准的高扩展高可用的实时数据分析的全文搜索工具	

# Elasticsearch能做什么？

​		Elasticsearch我们知道他是作为一个基于Lucene的一个搜索服务器，那么他肯定平时比较适用的场景就是用来搜索了，可以很好地存储和查询文档，可以应用于各个应用程序的搜索功能，例如电商的商品搜索，大数据量的日志系统，或者说一个海量数据的查询查找，以及我们能非常灵活的动态进行扩容，还能使用与地址数据的存储和分析，以及时间序列等等，只要是用于查询的功能，Elasticsearch大部分都能够给我们提供服务，所以Elasticsearch是一个非常强大的搜索引擎。

​		能够用于但不限于一下场景：

​					1、各种应用程序的搜索，如电商，医药，物流等等

​					2、大数据量的日志分析，我们可以用搭建ELK日志系统，对日志进行分析

​					3、使用机器学习实时自动建模数据行为

​					4、使用Elasticsearch作为地理信息系统（GIS）管理，集成和分析空间信息

​					5、使用Elasticsearch作为生物信息学研究工具来存储和处理遗传数据

# Elasticsearch官方文档地址？

​		Elasticsearch的版本随着更新也会有不同的变化下面我们将所有版本的文档都放在了官网的这个地址中：  [点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/index.html)

​		Elasticsearch提供了一个简单，一致的REST API，用于管理您的集群以及索引和搜索数据。为了进行测试，您可以轻松地直接从命令行或通过Kibana中的开发者控制台提交请求。在您的应用程序中，您可以使用 [Elasticsearch客户端](https://www.elastic.co/guide/en/elasticsearch/client/index.html) 作为您选择的语言：Java，JavaScript，Go，.NET，PHP，Perl，Python或Ruby。

# Elasticsearch的核心概念？		

## Index（索引）

```
　　Index（索引）就是相当于我们查询的时候指定从哪一个地方进行查询，我们知道Elasticsearch对于存储的数据都回去进行创建索引，提高查询效率。
　　
　　这里的Index有一点类似于我们的库，例如我们把一条数据存储到一个索引中，把另一条数据存储到另一个索引中，但是他和库的概念是相差非常地大。
　　
　　例如我们可以同时查询多个索引或者所有索引的数据，但是数据库不行，Index（索引）相当于帮我们定制了一个路径我们需要把相应的文件放入相应的路径中。
```

## Type（类型）

```
　　关于Type（类型），如果我们将Index比喻为库，那么我们可以简单的将它理解成数据库中的表，但是他和表的性质也是相差非常大的，这里的类型也相当于一个帮助我们精确定位数据集的一个类型，在早期的版本中一个Index（索引）可以创建多个Type但在之后的版本中只能设置一个，之后的Elasticsearch会逐渐的将Type（类型移除掉）。
　　
　　一种type就像一类表。如用户表、充值表等。

	 注意：

    	- ES 5.x中一个index可以有多种type。

    	- ES 6.x中一个index只能有一种type。

    	- ES 7.x以后，将移除type这个概念。
```

## Document（文档）

```
　　关于Document（文档）就是我们存储的一条条数据，我们可以把它叫做一个个文档，我们也能将这个Document理解为数据库中的一行表数据库。
```

## Field（字段）

```
　	如果说我们的Document是一条行数据的话，那么我们的Field就是行数据中的属性字段，我们知道在Java中每个对象都会有不同的字段属性也就是我们的Field，每个Field都是有对应的类型的，比如我们name这个Field通常莱索就是字符串那么他是一个text文本的类型，Field就是我们的字段，可以理解为数据库中的字段。
```

## Mapping（映射）

```
　　那么我们知道既然我们有库有表有字段，那么我们对应这张表的数据结构到底是什么呢，我们总不可能往里面乱放数据吧，这个Mapping（映射）如果我们把它和数据库进行对比的话，我们可以吧这个Mapping（映射）理解为一个表结构。
　　
　　我们数据库的表中都规定了字段以及类型，我们的Mapping（映射）也是类似的功能，但是如果说我们存入数据的时候没有这个Mapping（映射）那么Elasticsearch就会自己进行动态的创建。
```

## Cluster（集群）

```
		我们在官网可以很明显的看到Elasticsearch是一个分布式文档存储，那么从这个分布式中我们就能很明白的看出他肯定是会有集群的。
		
		那么我们的Cluster（集群）就相当于把很多台Elasticsearch节点组成了一整个集群，这个集群中的各个节点协调工作，并且可以互相访问以及同步数据，和我们的主从复制或者哨兵机制不一样，Elasticsearch的集群采用的一个分片的方式进行集群。
		
		集群由一个或多个节点组成。一个集群有一个默认名称"Elasticsearch"。 
			
		注意：不同集群，集群名称应唯一。 
```

## Node（节点）

```
		我们知道Elasticsearch是分布式的集群，那么每一个集群中肯定是有很多个Node（节点）的，他们部署在各个不同的服务器中，并且为我们的整个集群提供服务，我们可以把每一个Node（节点）看作是一个Elasticsearch服务，相当于一个班级（集群）下有很多个学生（节点）。
		
		节点是集群的一部分。ES 6.x中，有多种类型的节点：

		Master节点：存元数据，管理集群节点。

		Data节点：存数据。

		Ingest节点：可在数据真正进入index前,通过配置pipline拦截器对数据ETL。

		Coordinate节点：协调节点。如接收搜索请求，并将请求转发到数据节点，每个数据节点在本地执行请求并将结果返回给协调节点。

		协调节点将每个数据节点的结果汇总并返回给客户端。每个节点默认都是一个协调节点。当将node.master，node.data和node.ingest设置为false时，该节点仅用作协调节点。

注意：Coordinate Tribe 是一种特殊类型的协调节点，可连接到多个集群并在所有连接的集群中执行搜索和其他操作。

```

## Shard（分片）

```
		那么我们知道我们Elasticsearch是一个分布式的文档存储引擎，那么我们的数据是怎么样存储到各个服务器上的呢？
		
		答案就是我们的Shard分片了，好比说我们的100条数据需要分开存储到4台服务器中，那么我们就需要有4个Shard（分片），每一个节点一个Shard（分片），我们在存储数据的时候根据Elasticsearch的算法将数据分配到4台服务器中，也就是相当于我们的100条数据分成4分就是4个Shard（分片）。
		分成5份就是5个Shard（分片），如果说我们只有3个Node（节点），然后我们分配了4份，那么就会有一个Node（节点）会分配到两个Shard（分片）。
```

## Replica（副本）

```
		Replica（副本）这个其实就是我们的Shard（分片）的一个拓印版，相当于我们把这个Shard（分片）复制了，那么我们为什么需要这个Replica（副本）呢？
		那么我们知道Elasticsearch是分布式存储的，如果说我们的某一个节点宕机了，那么我们的数据是已经分片了的，宕机的那个Node（节点）的Shard（分片）我们就无法查询到了。
		所以Elasticsearch为了容灾会创建Replica（副本），我们的Shard（分片）对应的Replica（副本）是不会放在同一个节点上的，当一个节点宕机后我们可以找到这个Shard（分片）在其他机器上的副本进行查询。
```

## 分片(Shard)和副本(Replica)

```
		副本是分片的副本。分片有主分片(primary Shard)和副本分片(replica Shard)之分。

		一个Index数据在物理上被分布在多个主分片中，每个主分片只存放部分数据。

		每个主分片可以有多个副本，叫副本分片，是主分片的复制。

		注意：

    		1. 一个document只存在于某个primary shard以及其对应的replica shard上，不会在多个primary shard上。

    		2. 默认情况下，一个index有5个主分片,每个主分片都有一个副本。这样，整个index就有10个分片，要保证整个集群健康，就需要至少两个节点。因为主分片和副本分片不能在同一台机器上。

    		3. 主分片的数量在创建索引后不能再被修改，副本分片的数量可以改变。

    		4. 每个分片shard都是一个完整的lucene实例，有完整创建索引和处理请求的能力。

    		5. 分片有助于es水平扩展。副本一般用来容错，相当于主分片的HA。除此之外，分片和副本还有助于提高并行搜索的性能，因为可以在所有副本上并行执行搜索。
```

## Query DSL（DSL查询语句）

```
		类似于MySQL的SQL语句用来编写查询数据的语句以及查询条件和处理等等
```

## Sgment（分段）

```
		一个shard包含多个segment，每个segment都是倒排索引。

		查询时，每个shard会把所有segment结果汇总作为shard的结果返回。

			1.写入ES时，一方面会把数据写入到内存Buffer缓冲，为防止Buffer中数据丢失，另一方面会同时把数据写入到translog日志文件。

			2.每隔1秒钟，数据从Buffer被写入到segment file，直接写入os cache。

			3.os cache segment file被打开供search使用，然后内存Buffer被清空。

			4.随着时间推移，os cache中segment file越来越多，translog日志文件也越来越大，当translog日志文件大到一定程度的时候就会触发flush操作。

			5.flush操作将os cache中segment file持久化到磁盘并删除translog日志文件。

			6.当segment增多到一定程度，会触发ES合并segment,将许多小的segment合并成大segment并删除小segment，提高查询性能。
```



# RestFull风格API

​		他的RestFull风格的API和我们的后台的RestFull风格API一样，可以直接使用GET，POST，PUT，DELETE来进行操作的

​		GET（查询）

​		POST（新增）

​		PUT（修改）

​		DELETE（删除）

## 全局参数

​		Elasticsearch给我们提供了全局参数帮助我们在使用接口时可以设置很多参数，下面就是各个参数

```properties
# 过滤数据
?filter_path=***,***											# 表示只获取哪些属性，例如我只想获取设置中的主shard和副本信息其他的字段我不想要查询
											原路径为：GET /索引名/_settings
											过滤后为：GET /test_file/_settings？filter_path=test_file.settings.index.number*
											这样我们就可以将多余的信息过滤掉，使用逗号隔开可以获取多个,filter_path可以和_source一起使用过滤多余数据
						
```



```properties
# 返回数据参数设置
?pretty=true															# 将返回的数据json进行格式化，更加方便阅读
?format=yaml															# 将返回的数据以yaml的方式进行格式化返回
```



## Index（索引）

### 索引创建

​		使用Kibana创建一个索引为docment1，然后设置中3个分片，1个副本备份（注：创建主分片以后不能更改，但是副本集是可以修改数量的）

```properties
PUT /docment1
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  }
}
```

​		如果不设置，默认分片5片，副本一份

### 索引约定

​		大多数引用index参数的API都支持使用简单的test1,test2,test3符号（或_all所有索引）跨多个索引执行。它还支持通配符，例如：test*或*test或te*t或*test*，以及“排除”（-）的功能，例如：test*,-test3。

```
		也就是说我们的索引，可以使用简单的索引名，然后跟上id  1，2，3，我们可以通过通配符进行查询，那么这个方法在我们分索引时是特别有用的，例如我们根据天分索引，以log为例子，log_2020_05_06,log_2020_05_07,那么我们通过log*就可以查询所有了
```

​		如果我们使用多索引查询的时候还能指定3个参数：

```
ignore_unavailable
		控制是否忽略是否有任何指定的索引（包括不存在的索引或关闭的索引）不可用。任一true或false 可以被指定。
allow_no_indices
		控制在通配符索引表达式不产生任何具体索引的情况下是否失败。任一true或false可以被指定。例如，如果foo*指定了通配符表达式，并且没有以开头的索引可用foo，则根据此设置，请求将失败。当_all，，*或未指定索引时，此设置也适用。如果别名指向封闭索引，此设置也适用于别名。
expand_wildcards
		控制通配符索引表达式可以扩展到的具体索引类型。如果open指定，则通配符表达式将扩展为仅开放索引。如果closed指定，则通配符表达式仅扩展为封闭索引。也open,closed可以指定两个值（）以扩展到所有索引。如果none指定，则通配符扩展将被禁用。如果all 指定，通配符表达式将扩展到所有索引（等同于指定open,closed）。
```

​		时间索引

```
		日期数学索引名称解析使您可以搜索一系列时间序列索引，而不必搜索所有时间序列索引并过滤结果或维护别名。限制搜索索引的数量可以减少群集上的负载并提高执行性能。例如，如果您要在日常日志中搜索错误，则可以使用日期数学名称模板将搜索范围限制为过去两天。

		几乎所有具有index参数的API都在index参数值中支持日期数学。
		日期数学索引名称采用以下形式：
		<static_name{date_math_expr{date_format|time_zone}}>
		<静态名称{动态日志表达式{时间格式化|时区}}>
```



### 查看索引

​		查询所有索引

```properties
GET _cat/indices
```

​		我们先来查看docment1这个索引的设置信息

```properties
GET /docment1/_settings															# 查看索引设置信息
			
GET /docment1/_settings?flat_settings=true					# 查看索引设置信息，并且将设置以Key的properties形式返回而不是json，设置为false则返回json，默认不设置的话则为false
```

​		查看所有的索引信息

```
GET _all/_settings
```

#### 查看索引分片信息

​		例如我们查询index1这个索引的分片信息

```properties
GET /index1/_search_shards
```

​		会给我们返回如下信息

```properties
{
    "nodes": {
    		# 节点Id
        "3BNmLEOFQbeneOzUgALLQw": {
        		# 节点名称
            "name": "es-node2",
            "ephemeral_id": "5jKaQQCQRhCghc_B9VKhrg",
            "transport_address": "172.16.16.5:19302",
            "attributes": {
                "ml.machine_memory": "4142190592",
                "ml.max_open_jobs": "20",
                "xpack.installed": "true",
                "ml.enabled": "true"
            }
        }
    },
    "indices": {
        "index1": {}
    },
```



### 删除索引

​		删除docment2这个索引

```
DELETE docment2
```

​		删除之后所有这个索引相关数据都没了

### Reindex重新索引

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-reindex.html)

​		重新索引表示我们根据某一个索引重新建立索引一份新的索引并且同步数据，也可以称为复制。

```properties
POST _reindex
{
  "source":{
    "index":"test_boot"
  },
  "dest": {
    "index": "test_boot_news"
  }
}
```

​		我们还可以设置是否保留版本值分别有两种方式，在reindex的时候所有的Version默认都是internal，所以重新索引后版本号标记都会变成1。

```properties
# 将索引文档的Version重置为1
POST _reindex
{
  "source":{
    "index":"test_boot"
  },
  "dest": {
    "index": "test_boot_news",
    "version_type": "internal"
  }
}

# 保留原来的Version版本以及历史版本信息
POST _reindex
{
  "source":{
    "index":"test_boot"
  },
  "dest": {
    "index": "test_boot_news",
    "version_type": "external"
  }
}
```

​		并且我们可以添加查询语句进行查询，如下，我们只查询age为41的数据，并且把它添加到test_boot_news中

```properties
POST _reindex
{
  "source":{
    "index":"test_boot",
    "query": {
      "term": {
        "age": 41
      }
    }
  },
  "dest": {
    "index": "test_boot_news"
  }
}
```

​		指定我们重新索引的类型，op_type设置为create，如果我们从以前的index索引过来有相同id的数据那么则会抛出异常，不进行同步

```properties
POST _reindex
{
  "source":{
    "index":"test_boot",
    "query": {
      "term": {
        "age": 41
      }
    }
  },
  "dest": {
    "index": "test_boot_news",
    "op_type": "create"
  }
}
```

​		我们还能从多个类型的文档进行重新索引，并且我们索引到指定的那个type

```properties
POST _reindex
{
    "source": {
        "index": [
            "index1",
            "index2"
        ],
        "type": [
            "type1",
            "type2"
        ]
    },
    "dest": {
        "index": "index_new",
        "type": "type_new"
    }
}
```

​		我们也能指定进行索引的类型，以及根据哪个字段进行排序，并且索引age和name字段,然后使用脚本修改将age+4

```properties
POST _reindex
{
    "size": 10000,
    "source": {
        "index": "index1",
        "sort": {
            "age": "desc"
        },
        "_source":["age","name"]
    },
    "dest": {
        "index": "index_new"
    },
    "script": {
        "source": "ctx._source.age += params.count",
        "lang": "painless",
        "params" : {
            "count" : 4
        }
    }
}
```

### 索引分片

```json
#动态设置es索引副本数量
# 对一个Index设置副本
curl -XPUT 'http://168.7.1.67:9200/index1/_settings' -d '{
   "number_of_replicas" : 2
}'
 
#设置es不自动分配分片
curl -XPUT 'http://168.7.1.67:9200/log4j-emobilelog/_settings' -d '{
   "cluster.routing.allocation.disable_allocation" : true
}'
 
#手动移动分片
curl -XPOST "http://168.7.1.67:9200/_cluster/reroute' -d  '{
   "commands" : [{
		"move" : {
			"index" : "log4j-emobilelog",
			"shard" : 0,
			"from_node" : "es-0",
			"to_node" : "es-3"
		}
	}]
}'
#手动分配分片
curl -XPOST "http://168.7.1.67:9200/_cluster/reroute' -d  '{
   "commands" : [{
		"allocate" : {
			"index" : ".kibana",
			"shard" : 0,
			"node" : "es-2",
		}
	}]
}'

```



## Mapping(映射)

### 新建Mapping

​		我们索引已经建立好了，现在我们需要向里面新建Mapping，使用以下方式，假设我们的索引为myindex，类型为books，现在我们对name，以及发布时间publish_date不进行索引

```json
PUT /myindex/_mapping/books
{
    "properties": {
        "title": {
            "type": "text"
        },
        "name": {
            "type": "text",
            "index": false
        },
        "publish_date": {
            "type": "date",
            "index": false
        },
        "price": {
            "type": "double"
        }
    }
}
```

### 新建索引+Mapping

​		新建一个Mapping，设置分片3片副本0个，然后类型是books，然后配置它的字段title，类型为text，我们可以在创建的时候就设置mapping，index表示我们是否进行索引

```json
PUT /myindex
{
    "settings": {
        "number_of_shards": 3,
        "number_of_replicas": 0
    },
    "mappings": {
        "books": {
            "properties": {
                "title": {
                    "type": "text"
                },
                "name": {
                    "type": "text",
                    "index": false
                },
                "publish_date": {
                    "type": "date",
                    "index": false
                },
                "price": {
                    "type": "double"
                }
            }
        }
    }
}
```

### 查看Mapping

​		如何查看mapping的属性设置呢？我们直接使用Get方式+_mapping即可查询模板映射

```
GET /索引名/_mapping
```

### Mapping属性定制

#### 定制类型

定制dynamic，定制自己的字段，这个mapping类型为integer，它的dynamic为true

```properties
"number":{
		"type":"integer",
		"dynamic":true
}
```

```properties
"dynamic":true					遇到陌生字段，就进行dynamic mapping
"dynamic":false					遇到陌生字段，就忽略
"dynamic":strict				遇到陌生字段，就报错
```

这个属性可以直接加在类型上"dynamic": "strict",例如，那么这个类型就是不可变的了，如果我们新建了mapping中没有的字段那么就会报错

```properties
PUT /myindex/_mapping/books
{
    "dynamic": "strict",
    "properties": {
        "number": {
            "type": "integer"
        }
    }
}
```



#### 属性配置

​		//  是否在_source之外存储一份，例如用于stored_fields属性查询出来，相当于我们将这数据额外存储了一份

```properties
"title":{
	"type":"text",
	"store":false     
}
```

​		// 分词索引，不分词是：false,设置成false，字段将不会被索引

```properties
"title":{
	"type":"text",
	"index": true   
}
```

​		// 指定分词器,默认分词器为standard analyzer

```properties
"title":{
	"type":"text",
	"analyzer":"ik_max_word"
}

可以设置两个属性
ik_max_word				最细粒度拆分		例如：我今天在北京{我，今天，北京，在，我今天，等等}
ik_smart				最粗粒度拆分		例如：我今天在北京{我，今天，在北京，我今天在北京}
```

​		// 字段级别的分数加权，默认值是1.0

```properties
"title":{
	"type":"text",
	"boost":1.23
}
```

​		// 对not_analyzed字段，默认都是开启，分词字段不能使用，对排序和聚合能提升较大性能，节约内存

```properties
"title":{
	"type":"text",
	"doc_values":false
}
```

​		// 针对分词字段，参与排序或聚合时能提高性能，不分词字段统一建议使用doc_value

```properties
"title":{
	"type":"text",
	"fielddata":{"format":"disabled"}
}
```

​		// 可以对一个字段提供多种索引模式，同一个字段的值，一个分词，一个不分词

```properties
"title":{
	"type":"text",	
	"fields":{
		"raw":{
			"type":"string",
			"index":"not_analyzed"
		}
	} 
}
```

​		// 超过100个字符的文本，将会被忽略，不被索引

```properties
"title":{
	"type":"text",
	"ignore_above":100
}
```

​		// 设置是否此字段包含在_all字段中，默认是true，除非index设置成no选项

```properties
"title":{
	"type":"text",
	"include_in_all":ture
}
```

​		// 4个可选参数docs（索引文档号） ,freqs（文档号+词频），positions（文档号+词频+位置，通常用来距离查询），offsets（文档号+词频+位置+偏移量，通常被使用在高亮字段）分词字段默认是position，其他的默认是docs

```properties
"title":{
	"type":"text",
	"index_options":"docs"
}
```

​		// 分词字段默认配置，不分词字段：默认{"enable":false}，存储长度因子和索引时boost，建议对需要参与评分字段使用 ，会额外增加内存消耗量

```properties
"title":{
	"type":"text",
	"norms":{"enable":true,"loading":"lazy"}
}
```

​		// 设置一些缺失字段的初始化值，只有string可以使用，分词字段的null值也会被分词

```properties
"title":{
	"type":"text",
	"null_value":"NULL"
}
```

​		// 影响距离查询或近似查询，可以设置在多值字段的数据上火分词字段上，查询时可指定slop间隔，默认值是100

```properties
"title":{
	"type":"text",
	"position_increament_gap":0
}
```

​		// 设置搜索时的分词器，默认跟ananlyzer是一致的，比如index时用standard+ngram，搜索时用standard用来完成自动提示功能

```properties
"title":{
	"type":"text",
	"search_analyzer":"ik"
}
```

​		// 默认是TF/IDF算法，指定一个字段评分策略，仅仅对字符串型和分词类型有效

```properties
"title":{
	"type":"text",
	"similarity":"BM25"
}
```

​		// 默认不存储向量信息，支持参数yes（term存储），with_positions（term+位置）,with_offsets（term+偏移量），with_positions_offsets(term+位置+偏移量) 对快速高亮fast vector highlighter能提升性能，但开启又会加大索引体积，不适合大数据量用

```properties
"title":{
	"type":"text",
	"term_vector":"no"
}
```

#### Mapping类型

##### text（文本类型）

​		用于索引全文值的字段，例如电子邮件的正文或产品的描述。这些字段是`analyzed`，也就是说，它们通过[分析器](https://www.elastic.co/guide/en/elasticsearch/reference/6.7/analysis.html)传递，以便 在被索引之前将字符串转换为单个术语的列表。通过分析过程，Elasticsearch可以*在* 每个全文本字段中搜索单个单词。文本字段不用于排序，很少用于聚合（尽管 [重要的文本聚合](https://www.elastic.co/guide/en/elasticsearch/reference/6.7/search-aggregations-bucket-significanttext-aggregation.html) 是一个明显的例外）。

```properties
# 将title设置为文本并且指定分词器
{
  "mappings": {
    "_doc": {
      "properties": {
        "title": {
          "type":"text",
          "analyzer":"ik_max_word"
        }
      }
    }
  }

```



##### numeric（数值类型）

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.7/number.html)

​		支持以下数字类型：

```
long
integer
short
byte
double
float
half_float
scaled_float
```

##### keyword（关键词类型）

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.7/keyword.html)

```properties
# 它们通常用于过滤（找到我的所有博客文章，其中 status为published），排序，和聚合。关键字字段只能按其确切值进行搜索。
# 简单的来说就是不分词

# Mapping
{"mappings":{"_doc":{"properties":{"tags":{"type":"keyword"}}}}}

```

##### date（时间类型）

```properties
# JSON没有日期数据类型，因此Elasticsearch中的日期可以是包含格式化日期的字符串，例如"2015-01-01"或"2015/01/01 12:10:30"，从纪元以来 代表毫秒的长整数，代表秒后的整数。在内部，日期会转换为UTC（如果指定了时区），并存储为一个整数，表示自纪元以来的毫秒数。
# 日期格式可以自定义，但是如果未format指定，则使用默认格式

"type":“strict_date_optional_time || epoch_millis”
		1、{"date": "2015-01-01" }
		2、{"date": "2015-01-01T12:10:30Z" }
		3、{"date": 1420070400001 }
# 以及自定义
PUT test
{
  "mappings": {
    "_doc": {
      "properties": {
        "date": {
          "type":   "date",
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        }
      }
    }
  }
}
```

##### boolean（布尔类型）

```properties
# 布尔字段接受JSON，true和false值（true，false），但也可以接受解释为true或false的字符串（"true","false"）
"bool_field": {
	"type": "boolean"
}
```

##### object（对象类型）

```properties

# 数据格式
PUT my_index/_doc/1
{ 
  "region": "US",
  "manager": { 
    "age":     30,
    "name": { 
      "first": "John",
      "last":  "Smith"
    }
  }
}

# mapping类型
PUT my_index
{
  "mappings": {
    "_doc": { 
      "properties": {
        "region": {
          "type": "keyword"
        },
        "manager": { 
          "properties": {
            "age":  { "type": "integer" },
            "name": { 
              "properties": {
                "first": { "type": "text" },
                "last":  { "type": "text" }
              }
            }
          }
        }
      }
    }
  }
}
```



##### nested（对象数组类型）

```properties
# 该`nested`类型是object数据类型的专用版本，它允许以可以彼此独立地查询对象的方式对对象数组进行索引。
# 内部object字段数组无法按预期方式工作。Lucene没有内部对象的概念，因此Elasticsearch将对象层次结构简化为字段名称和值的简单列表。

# user是一个对象数组
PUT my_index/_doc/1
{
  "group" : "fans",
  "user" : [ 
    {
      "first" : "John",
      "last" :  "Smith"
    },
    {
      "first" : "Alice",
      "last" :  "White"
    }
  ]
}
```



##### ip（IP地址）

```properties
# 一个ip字段可以索引/存储任一的IPv4或 IPv6的地址。
"ip_field": {
	"type": "ip"
}

# 新增
PUT {"ip_field": "192.168.1.1"}

# 查询
GET {"query":{"term":{"ip_field":"192.168.0.0/16"}}}
# 或者ipv6
GET {"query":{"term":{"ip_field":"2001:db8::/48"}}}
```



##### geo_point（点地理位置）

```properties
# 类型的字段geo_point接受纬度-经度对，可以使用：在边界框内，中心点一定距离内或多边形内找到地理点，
PUT test
{
  "mappings": {
    "_doc": {
      "properties": {
        "location": {
          "type": "geo_point"
        }
      }
    }
  }
}

# 存储方式可以为：
# 对象
1、"location": { "lat": 41.12,"lon": -71.34}
# 字符串
2、"location": "41.12,-71.34" 
# geohash
3、"location": "drm3btev3e86"
# 数组
4、"location": [ -71.34, 41.12 ]
```

##### geo_shape（形状地理位置）

```properties
# 的geo_shape数据类型方便的索引和与任意的地理搜索为矩形和多边形的形状，例如。当正在索引的数据或正在执行的查询包含的形状不仅是点时，都应使用它。
# 建立索引
PUT test
{
    "mappings": {
        "doc": {
            "properties": {
                "location": {
                    "type": "geo_shape"
                }
            }
        }
    }
}

# 存储数据
```

​		查询geo_shape官方文档地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.7/query-dsl-geo-shape-query.html)

##### binary（二进制）

```properties
# 该binary类型接受二进制值作为 Base64编码的字符串。该字段默认情况下不存储，并且不可搜索，不能包含换行符
PUT test
{
  "mappings": {
    "_doc": {
      "properties": {
        "file": {
          "type": "binary"
        }
      }
    }
  }
}

```

##### range（范围）

```properties
# integer_range
	一个带符号的32位整数范围，最小值为-2的31次方，最大值为2的31次方-1。
# float_range
	一系列单精度32位IEEE 754浮点值。
# long_range
	一系列带符号的64位整数，最小值为-2的63次方，最大值为2的63次方-1。
# double_range
	一系列双精度64位IEEE 754浮点值。
# date_range
	自系统时代以来经过的一系列日期值，表示为无符号的64位整数毫秒。
# ip_range
	支持IPv4或 IPv6（或混合）地址的一系列ip值。
	
PUT range_index
{
  "settings": {
    "number_of_shards": 2
  },
  "mappings": {
    "_doc": {
      "properties": {
        "int_ran": {
          "type": "integer_range"
        },
        "time_ran": {
          "type": "date_range", 
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        },
        "ip_ran": {
          "type": "ip_range"
        }
      }
    }
  }
}


# 搜索，返回所有包含或者范围在12的数据
GET range_index/_search
{
  "query" : {
    "term" : {
      "int_ran" : {
        "value": 12
      }
    }
  }
}
# 添加一个Ip范围
PUT range_index/_doc/2
{
  "ip_whitelist" : "192.168.0.0/16"
}
```

##### array（数组）

```properties
# 不需要建立索引类型直接使用即可，但是不能使用混合类型，并且强调后续数据类型相同
PUT test/_doc/1
{
  "string_list": ["one","two"],
	"integer_list": [1 , 2],
	"integer2_list": [1 , [2 , 3]],
	"object_list": [{"name":"big1","age":18},{"name":"big2","age":19}]
}

# string_list 字符串的List，只能为List
# integer_list 纯数字的list
# integer2_list 使用数组嵌套数组的纯数字，将转变为[1,2,3]
# object_list 对象的List
```

##### alias（引用类型）

```properties
# 引用类型，可以引用其他属性的引用,并且搜索的时候可以采用引用进行搜索，但是日常并不推荐使用，有一些Api不支持如_source
PUT test
{
  "mappings": {
    "_doc": {
      "properties": {
        "age": {
          "type": "long"
        },
        "field_as_age": {
          "type": "alias",
          "path": "distance" 
        }
      }
    }
  }
}
```

​		



```

keyword
date
```



## Document（文档）

### 添加文档

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-index_.html)

​		参数属性

```properties
?op_type=create							指定操作类型为create，如果文档不存在则创建，如果存在则报错
或者跟上
_create

		示例:
				PUT /docment2/test/3?op_type=create	
				PUT /docment2/test/3/_create
				
				
?timeout=5m									指定超时为5分钟


?routing=user1							指定routing路由同样一份数据指定不同的路由，那么他就不是相同的数据
														例如创建时创建一个user1，路由一个user2路由，就算ID是一样的，那么他的路由不同我们也能根据路由查询到不一样的数据
																	?routing=user1
																	?routing=user2
```

​		指定id进行创建文档

​		我们使用Kibana进行添加文档，使用PUT新增一个id为1的索引，里面有字段name，age等等

```
PUT /docment1/test/1
{
  "name":"黄康",
  "age":18,
  "sex":"男",
  "address":"四川达州开江",
  "emaile":"bigkang@126.com",
  "like":"股票，游戏，户外",
  "groupid": [
      12,
      13,
      14,
      15
    ]
}
```

不指定id进行创建文档，那么他的id将会由Es自动生成

```
POST /docment1/test
{
  "name":"BigKang",
  "age":19,
  "sex":"男",
  "address":"四川达州开江",
  "emaile":"bigkang@126.com",
  "like":"股票，游戏，户外",
  "groupid": [
      22,
      23,
      24,
      25
    ]
}
```

​		添加文档后他会给我们返回相应的信息

```properties
{
  "_index" : "docment1",				#  索引名称
  "_type" : "test",							#	 type名称
  "_id" : "1",									#  
  "_version" : 1,								#  版本
  "result" : "created",					#  返回结果，created则为创建
  "_shards" : {
    "total" : 2,								#  一共操作了多少shard，如5主分片，1副本集，则为2，分片一条副本一条，如果2副本集则为3
    "successful" : 2,						#  成功条数
    "failed" : 0								#  失败条数
  },
  "_seq_no" : 0,								#  序列号
  "_primary_term" : 1
}
```



### 查看文档

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-get.html)

​		查看所有索引的所有文档

```
GET _search
{
  "query": {
    "match_all": {}
  }
}
```

​		查看docment1索引test类型Id为1的文档

```
GET /docment1/test/1
```

​		查询存储的sotre文档,表示我们在前面的Mapping是否额外存储了一份数据，用于查询设置了stored为true的属性

```
?stored_fields=age,name
```

​		通过路由查询相应路由的数据

```
GET /docment2/test/12?routing=user6

例如我们添加时，制定了routing，那么我们根据ID查询的时候指定上routing即可查询出相应的ruting，否则则根据词频获取最高的id的数据
注意：只有针对单个id时有效，并且只能指定一个routing
PUT /docment2/test/12?routing=user6
```

​		查看所有docment1下面test的文档

```
GET /docment1/test/_search
```

​		查看docment1下面test类型id为1的文档的name和age属性值，这样就能只查看name和age了

```properties
GET /docment1/test/1?_source=age,name

并且我们可以设置true或者false，true表示全部查询，false表示都不查询

_source_includes表示只查询哪几个			可以使用*号通配符
_source_excludes表示排除哪几个				 可以使用*号通配符
```

​		查看文档以及Id是否存在

```
HEAD docment2/test/1

存在返回：					200 - OK
不存在返回：			  404 - Not Found
```

​		查询分词向量分析，例如我们对文档字段进行分析（注意只对文本分词字段有效），例如我们分析content这个字段

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-termvectors.html)

​		以及我们的多索引向量分析

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-multi-termvectors.html)

```properties
GET /test_pinyin/test/1/_termvectors?fields=content
```

​		相应如下：

```properties
{
	# 索引名称
  "_index" : "test_pinyin",
  # 索引类型
  "_type" : "test",
  # Id
  "_id" : "1",
  # 版本
  "_version" : 1,
  # 是否存在
  "found" : true,
  # 消耗的毫秒数
  "took" : 2,
  # 词向量分析
  "term_vectors" : {
  	# 分析的字段content
    "content" : {
    	# 字段统计
      "field_statistics" : {
      	# 文档频率总和（此字段中所有术语的文档频率总和）
        "sum_doc_freq" : 47,
        # 文件计数（有多少文件包含此字段）
        "doc_count" : 2,
        # 总术语频率的总和（此字段中每个术语的总术语频率的总和）
        "sum_ttf" : 52
      },
      # 分词条例
      "terms" : {
      	# 分词结果
        "东西" : {
        	# 词频
          "term_freq" : 1,
          # 向量
          "tokens" : [
            {
            	# 位置
              "position" : 7,
              # 起始偏移量
              "start_offset" : 6,
              # 结束偏移量
              "end_offset" : 8
            }
          ]
        },
        "小米" : {
          "term_freq" : 1,
          "tokens" : [
            {
              "position" : 0,
              "start_offset" : 0,
              "end_offset" : 2
            }
          ]
        },
        "手机" : {
          "term_freq" : 1,
          "tokens" : [
            {
              "position" : 3,
              "start_offset" : 2,
              "end_offset" : 4
            }
          ]
        },
        "有点" : {
          "term_freq" : 1,
          "tokens" : [
            {
              "position" : 5,
              "start_offset" : 4,
              "end_offset" : 6
            }
          ]
        }
      }
    }
  }
}

```

​		

#### 批量查询文档

这样我们就可以批量查询，两个文档库的不同数据批量了

```
GET _mget
{
  "docs":[
    {
      "_index":"docment2",
      "_type":"test",
      "_id":"Kksg1GkBn4ABzJvPp8nh"
    },
       {
      "_index":"docment1",
      "_type":"test",
      "_id":"1"
    }
  ]
}

```

查询所有的批量字段，查询文档docment2和1的id，查询第一个只查询name，查询第二个查询name和age

```
GET /_mget
{
  "docs":[
   
   {
       "_index": "docment2",
       "_type": "test",
       "_id":"J0sb1GkBn4ABzJvPjsmv",
       "_source": "name"
   },
   {
       "_index": "docment1",
       "_type": "test",
       "_id": 1,
       "_source": ["name","age"]
   }
 ]
}
```

查询/docment2/test下面的ids，它的条件为id数组

```
GET /docment2/test/_mget
{
  "ids":[
    "Kksg1GkBn4ABzJvPp8nh","J0sb1GkBn4ABzJvPjsmv"]
}
```

#### scroll（游标查询）

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-request-scroll.html#scroll-search-context)

​		scroll（游标查询）主要是用于我们需要查询大量的数据时所使用，他的作用是帮助我们查询一些数据，但是这些数据量又特别大，并且如果使用分页的话性能不理想，所以我们使用scroll（游标查询），相当于我们查询一批数据，然后给这个数据定义一个游标，例如查询300w条数据，我们每次查询10w条，那么我们查询10w条然后将游标记录在10w，这时候会给我们一个游标id，我们根据这个游标id在进行查询，他从10w开始到20w的数据，然后我们根据这个游标Id再进行查询直至我们的数据查询完毕为止，如下我们来使用一下游标查询。

​		目前我们测试以1条为例，然后游标时间为5秒，如果在5秒钟左右我们没有对这个游标进行操作那么他则会从我们Es的上下文中删除。

```properties
GET /index1/doc1/_search?scroll=5s
{
  "size": 1
}
```

​		那么则会给我们返回如下数据

```properties
{
	# 游标Id
  "_scroll_id" : "DnF1ZXJ5VGhlbkZldGNoBQAAAAAACC6AFkUwMk9PMXJPUUgyLV9KYkkyRFZLYXcAAAAAAAeedBZKZi1kMU9RUVNfLThITHpPNldYbWxnAAAAAAAILn8WRTAyT08xck9RSDItX0piSTJEVkthdwAAAAAAB552FkpmLWQxT1FRU18tOEhMek82V1htbGcAAAAAAAeedRZKZi1kMU9RUVNfLThITHpPNldYbWxn",
  "took" : 15,
  # 是否超时
  "timed_out" : false,
  # 分片信息
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : 3,
    "max_score" : 1.0,
    "hits" : [
			查询的数据
    ]
  }
}
```

​			然后我们再根据这个游标ID进行查询即可

```properties
POST _search/scroll
{
    "scroll": "1s", 
    "scroll_id" : "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAIRaFjNCTm1MRU9GUWJlbmVPelVnQUxMUXcAAAAAAACEWBYzQk5tTEVPRlFiZW5lT3pVZ0FMTFF3AAAAAAAAhFkWM0JObUxFT0ZRYmVuZU96VWdBTExRdwAAAAAACC62FkUwMk9PMXJPUUgyLV9KYkkyRFZLYXcAAAAAAAgutxZFMDJPTzFyT1FIMi1fSmJJMkRWS2F3"
}
```

​			然后我们一直进行游标请求，直至返回中没有数据表示我们查询完成，至此游标查询完毕。

​			那么游标查询默认是没有上限的，我们可以在Es的节点配置中，也就是我们每个节点锁打开的scroll数量

```properties
search.max_open_scroll_context
```

​			我们也可以查看节点状态,查看我们的游标

```properties
GET _nodes/stats/indices/search
```

​			以及删除游标

```properties
# 删除所有游标
DELETE /_search/scroll/_all

# 删除一条或者多条，使用逗号隔开
DELETE /_search/scroll/DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAIRaFjNCTm1MRU9GUWJlbmVPelVnQUxMUXcAAAAAAACEWBYzQk5tTEVPRlFiZW5lT3pVZ0FMTFF3AAAAAAAAhFkWM0JObUxFT0ZRYmVuZU96VWdBTExRdwAAAAAACC62FkUwMk9PMXJPUUgyLV9KYkkyRFZLYXcAAAAAAAgutxZFMDJPTzFyT1FIMi1fSmJJMkRWS2F3
```

#### 性能分析

​		首先我们使用explain先来进行分析

​		我们在进行查询的时候我们对我们的查询语句进行分析

```properties
GET /index1/doc1/_search
{
  "explain": true, 
  "query": {
    "match": {
      "content": "你好"
    }
  }
}
```

​		然后返回的数据如下示例

```properties
{
    "took": 21,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 2,
        "max_score": 0.5753642,
        "hits": [
            {
            		# 处于哪个分片，这里表示index这个索引的2分片
                "_shard": "[index1][2]",
                # 属于哪个节点
                "_node": "E02OO1rOQH2-_JbI2DVKaw",
                "_index": "index1",
                "_type": "doc1",
                "_id": "2",
                "_score": 0.5753642,
                "_source": {
                    "name": "黄康你好啊",
                    "content": "真的秀你好",
                    "age": 20
                },
                # 分析说明
                "_explanation": {
                		# 分数
                    "value": 0.5753642,
                    # 表示累加
                    "description": "sum of:",
                    "details": [
                        {
                            "value": 0.2876821,
                            "description": "weight(content:你 in 0) [PerFieldSimilarity], result of:",
                            "details": [
                                {
                                    "value": 0.2876821,
                                    "description": "score(doc=0,freq=1.0 = termFreq=1.0\n), product of:",
                                    "details": [
                                        {
                                            "value": 0.2876821,
                                            "description": "idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:",
                                            "details": [
                                                {
                                                    "value": 1,
                                                    "description": "docFreq",
                                                    "details": []
                                                },
                                                {
                                                    "value": 1,
                                                    "description": "docCount",
                                                    "details": []
                                                }
                                            ]
                                        },
                                        {
                                            "value": 1,
                                            "description": "tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:",
                                            "details": [
                                                {
                                                    "value": 1,
                                                    "description": "termFreq=1.0",
                                                    "details": []
                                                },
                                                {
                                                    "value": 1.2,
                                                    "description": "parameter k1",
                                                    "details": []
                                                },
                                                {
                                                    "value": 0.75,
                                                    "description": "parameter b",
                                                    "details": []
                                                },
                                                {
                                                    "value": 5,
                                                    "description": "avgFieldLength",
                                                    "details": []
                                                },
                                                {
                                                    "value": 5,
                                                    "description": "fieldLength",
                                                    "details": []
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "value": 0.2876821,
                            "description": "weight(content:好 in 0) [PerFieldSimilarity], result of:",
                            "details": [
                                {
                                    "value": 0.2876821,
                                    "description": "score(doc=0,freq=1.0 = termFreq=1.0\n), product of:",
                                    "details": [
                                        {
                                            "value": 0.2876821,
                                            "description": "idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:",
                                            "details": [
                                                {
                                                    "value": 1,
                                                    "description": "docFreq",
                                                    "details": []
                                                },
                                                {
                                                    "value": 1,
                                                    "description": "docCount",
                                                    "details": []
                                                }
                                            ]
                                        },
                                        {
                                            "value": 1,
                                            "description": "tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:",
                                            "details": [
                                                {
                                                    "value": 1,
                                                    "description": "termFreq=1.0",
                                                    "details": []
                                                },
                                                {
                                                    "value": 1.2,
                                                    "description": "parameter k1",
                                                    "details": []
                                                },
                                                {
                                                    "value": 0.75,
                                                    "description": "parameter b",
                                                    "details": []
                                                },
                                                {
                                                    "value": 5,
                                                    "description": "avgFieldLength",
                                                    "details": []
                                                },
                                                {
                                                    "value": 5,
                                                    "description": "fieldLength",
                                                    "details": []
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    }
}
```

​		然后我们再来使用profile进行分析，profile不光可以分析query，也能分析带聚合的

```properties
GET /index1/doc1/_search
{
  "profile":true,
  "query": {
    "match": {
      "content": "你好"
    }
  }
}
```



#### 索引权重

​		例如我们在多个索引的时候，例如查询test和doc*这多个索引进行查询，当您使用别名或通配符表达式时，这一点很重要。如果找到多个匹配项，则将使用第一个匹配项。

​		使用方式如下，那么我们就会看到test索引的数据会比index通配符索引的权重高，排在前面

```properties
GET /_search
{
    "indices_boost" : [
        { "test" : 1.4 },
        { "index*" : 1.3 }
    ]
}
```

#### 匹配分数过滤

​		例如我们有时需要查询有比较高的一个精确度，我们使用min_score进行过滤，也就是说小于多少的分数我们就把它过滤掉，小于0.58的则不会查询，设置min_score表示数据精准，设置的越高越准确，但是不宜设置的过高。

```properties
GET /index1/doc1/_search
{
"min_score": 0.58,
  "query": {
    "match": {
      "content": "你好"
    }
  }
}
```

#### msearch批量查询

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-multi-search.html)

​		我们可以使用类似于批量删除更新的操作的批量查询，也就是说我们可以一条命令查询多个索引，多个不同条件，查询方式如下：

​		我们分别查询index1然后查询第一条，然后再查询index2从第二条开始查询，查询10条，这里的查询

```properties
GET _msearch
{"index" : "index1"}
{"query" : {"match_all" : {}}, "from" : 0, "size" : 1}
{"index" : "index2"}
{"query" : {"match_all" : {}}, "from" : 1, "size" : 10}
```

​		我们可以使用多种不同的方式查询

```properties
# {}为查询所有索引
GET _msearch
{}
{"query" : {"match_all" : {}}}


```

#### 统计数量

​		统计方式我们拥有采用统一的cont

```properties
GET /index1/doc1/_count
{
  "query": {
    "match": {
      "content": "你好"
    }
  }
}
```



### 更新文档

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-update.html)

​		更新文档（覆盖掉以前的文档），这样就把以前的文档覆盖掉了但是如果有一个字段没有设置那么那个字段也就会丢失

```properties
PUT /docment1/test/1
{
  "name":"黄康",
  "age":18,
  "sex":"男",
  "address":"四川达州开江",
  "emaile":"bigkang@126.com",
  "like":"股票，游戏，户外",
  "groupid" : [
      13,
      14,
      15,
      19
    ]
}
```

​		更新文档不覆盖，更新id为1的文档的age为19

```properties
POST /docment1/test/1/_update
{
  "doc": {
    "age":19
  }
}
```

​		并且我们是可以使用脚本来进行文档的更新的,例如我们把id为9的age+=4

```properties
# 对数字类型进行操作
POST /docment1/test/9/_update
{
    "script" : {
        "source": "ctx._source.age += params.count",
        "lang": "painless",
        "params" : {
            "count" : 4
        }
    }
}

# 对数组进行操作,例如我们向数组中添加上一个元素，值为19
POST /docment1/test/9/_update
{
    "script" : {
        "source": "ctx._source.groupid.add(params.tag)",
        "lang": "painless",
        "params" : {
            "tag" : 12
        }
    }
}
# 我们查找groupid这个属性下包含19的，并且将19这个元素删除
POST /docment1/test/9/_update
{
    "script" : {
        "source": "if (ctx._source.groupid.contains(params.tag)) { ctx._source.groupid.remove(ctx._source.groupid.indexOf(params.tag)) }",
        "lang": "painless",
        "params" : {
            "tag" : 12
        }
    }
}
# 我们将new_field字段
POST /docment1/test/9/_update
{
    "script" : "ctx._source.new_field = 'value_of_new_field'"
}


# upsert关键字，如果存在id为9的文档那么就执行修改，如果不存在则创建文档，内容为upsert的内容，也就是说存在则进行修改，不存在创建
POST /docment1/test/9/_update
{
    "script" : "ctx._source.new_field = 'value_of_new_field'",
    "lang": "painless",
    "upsert":{
    	"name":"bigkang",
    	"age":20
    }
}
```

​		查询修改,此处的UpdateByQuery的query查询和搜索的查询是一样的

```properties
# 修改该索引下所有的new_field值为value_of_new_field
POST /index1/doc1/_update_by_query
{
    "script" : "ctx._source.new_field = 'value_of_new_field'",
    "query":{
      "match_all":{}
    }
}
```



#### 批量操作_bulk

​		批量更新是指我们一次对多个除了查询之外的，增，删，改等等的操作，我们把它，例如我们想修改一个数据，并且新增一条数据，但是我们这个操作需要一次执行，不想分成两次，所以我们使用_bulk即可。

​		_bulk的操作有如下几种：

```java
			index													// 进行索引，可以使用op_type控制如果存在是否报错，参考前方添加文档API
			create												// 和index一样但是默认如果存在的话则不进行插入，并且他的下一行需要填写需要索引的数据
			delete												// 这个操作为删除操作，下一行不需要输入，直接添加操作即可
			update												// 这个操作为更新操作用于更新操作，操作的类型有doc，upsert，还有script
```

​		那么我们下面先来试一下index和create吧

​		例如我们现在使用_bulk插入两条数据,retry_on_conflict表示我们有版本冲突的时候进行重试的次数，由于我们创建时没有设置Id，就需要设置重试次数生成id，如下我们就创建了两条数据，但是第二次执行的话第二个操作则会失败，因为第二次的操作是创建，如果有这条数据那么则索引失败。

```properties
	 # 首先我们创建一个文件requests,并且在里面写入我们的操作
	 cd 
	 touch requests
	 # 然后编辑
	 vim requests
	 
	 写入如下命令
{ "index" : { "_index" : "index1", "_type" : "doc1","retry_on_conflict":3 } }
{ "name" : "bigkang" , "age" : 20 }
{ "create" : { "_index" : "index1", "_type" : "doc1", "_id" : "1"} }
{ "name" : "bigkang1" , "age" : 201 }
	 
	 然后请求，这里表示我们使用Post类型请求内容类型为application/x-ndjson，并且文件为requests文件
	 curl -s -H "Content-Type: application/x-ndjson" -XPOST localhost:19201/_bulk --data-binary "@requests"
	 
	 Kibanna的类型如下
POST _bulk
{ "index" : { "_index" : "index1", "_type" : "doc1","retry_on_conflict":3 } }
{ "name" : "bigkang" , "age" : 20 }
{ "create" : { "_index" : "index1", "_type" : "doc1", "_id" : "1"} }
{ "name" : "bigkang1" , "age" : 201 }
```

​		 然后我们清空删除所有文档

```properties
POST index1/doc1/_delete_by_query
{
  "query": { 
    "match_all": {}
  }
}

```

​		下面我们再进行一个删除操作，我们创建之后再把id为1的删除掉，那么es中就只有我们自动生成的Id了

```properties
{ "index" : { "_index" : "index1", "_type" : "doc1","retry_on_conflict":3 } }
{ "name" : "bigkang" , "age" : 20 }
{ "create" : { "_index" : "index1", "_type" : "doc1", "_id" : "1"} }
{ "name" : "bigkang1" , "age" : 201 }
{ "delete" : { "_index" : "index1", "_type" : "doc1", "_id" : "1" } }
```

​		 清空以前的文档

​		 最后我们再来进行一个修改,我们把id为2的这个数据中的test修改为test1，然后把id为1的这个数据全部修改只有一个属性，并且值为bigkang,如果需要全部修改直接使用index进行重新索引即可。

```properties
{ "update" : { "_index" : "index1", "_type" : "doc1", "_id" : "2" } }
{ "doc" : {"test" : "test1"} }
{ "index" : { "_index" : "index1", "_type" : "doc1", "_id" : "1" , "_source":true} }
{ "doc" : {"test" : "test1"} }
```

​		然后我们再来使用一下我们前面所使用到的脚本修改

```properties
{ "update" : { "_id" : "0", "_type" : "_doc", "_index" : "index1", "retry_on_conflict" : 3} }
{ "script" : { "source": "ctx._source.counter += params.param1", "lang" : "painless", "params" : {"param1" : 1}}, "upsert" : {"counter" : 1}}
```



### 删除文档

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-delete.html)

​		删除docment1这个索引的test类型的id等于1的这个文档

```properties
DELETE /docment1/test/1
```

​		删除指定routing的数据

```properties
DELETE /docment2/test/12?routing=user1
```

​		删除指定超时时间

```properties
DELETE /docment2/test/12?timeout=5m
```

​		按查询条件删除，删除address中包含开江的数据，这里的删除逻辑和查询是一样的

```properties
POST /docment2/_delete_by_query
{
  "query": { 
    "match": {
      "address": "开江"
    }
  }
}
```

​		支持多个索引删除数据

```properties
POST docment2,docment3/_delete_by_query
{
  "query": { 
    "match": {
      "address": "开江"
    }
  }
}
```

​		支持批量删除条数自定义，设置每批次5000，默认为1000条，这里的条数指Elasticsearch内部删除时每批次，并不是每次执行就只删除5000，例如一万条数据，Elasticsearch会把它分成2批进行删除，然后返回删除1w条成功。

```properties
POST docment2,docment3/_delete_by_query?scroll_size=5000
{
  "query": { 
    "match": {
      "address": "开江"
    }
  }
}
```

​		除了标准的参数，如`pretty`，删除通过查询API也持`refresh`，`wait_for_completion`，`wait_for_active_shards`，`timeout`，和`scroll`。

```
		refresh请求完成后，发送将会刷新通过查询删除的所有分片。这与删除API的refresh 参数不同，删除API的参数仅导致接收到删除请求的分片被刷新。也不同于delete API，它不支持wait_for
		
		wait_for_completion=false则Elasticsearch将执行一些预检检查，启动请求，然后返回task ，可与Tasks API 一起使用来取消或获取任务的状态。Elasticsearch还将在上创建此任务的记录作为文档.tasks/task/${taskId}。您可以根据自己的喜好保留或删除此文件。完成后，将其删除，以便Elasticsearch可以回收其使用的空间。
		
		wait_for_active_shards控制在进行请求之前必须激活多少个分片副本。有关 详细信息，请参见此处。timeout控制每个写入请求等待不可用的碎片变为可用的时间。两者在Bulk API中的工作方式完全相同 。由于_delete_by_query使用滚动搜索，你还可以指定scroll参数来控制多长时间保持“搜索上下文”活着，例如?scroll=10m。默认情况下是5分钟。
		
		requests_per_second可以被设置为任何正十进制数（1.4，6， 1000等）和节流的速率删除由删除操作的查询问题批次通过填充每批与等待时间。将设置requests_per_second为可以禁用节流-1。
		
		由于批处理是作为单个_bulk请求发出的，因此较大的批处理大小将导致Elasticsearch创建许多请求，然后等待一会儿再开始下一组请求。这是“突发”而不是“平滑”。默认值为-1。
```

​		删除时的相应示例如下

```properties
{
  "took" : 147,										# 从整个操作开始到结束的毫秒数。
  "timed_out": false,							# true如果通过查询执行删除期间执行的任何请求已超时， 则设置此标志。
  "total": 119,										# 成功处理的文档数。
  "deleted": 119,									# 成功删除的文档数。
  "batches": 1,										# 通过按查询删除而撤回的滚动响应数。
  "version_conflicts": 0,					# 被查询删除导致的版本冲突数量。
  "noops": 0,											# 对于要通过查询删除的字段，该字段始终等于零。它仅存在，以便按查询删除，按查询更新和重新索引API会返回具有相同结构的响应。
  "retries": {										# 通过查询删除尝试的重试次数。
    "bulk": 0,										# bulk是重试的批量操作数
    "search": 0										# search是重试的搜索操作数。
  },
  "throttled_millis": 0,					# 要求遵守的毫秒数requests_per_second。
  "requests_per_second": -1.0,		# 在通过查询删除期间有效执行的每秒请求数。
  "throttled_until_millis": 0,		# 在_delete_by_query响应中，该字段应始终等于零。它只有在使用Task API时才有意义，它表示下一次（自epoch以来的毫秒数）受限制的请求将再次执行以符合requests_per_second。
  "failures" : [ ]								# 如果在此过程中有任何不可恢复的错误，则表示一系列失败。如果这是非空的，则由于这些失败，请求中止。按查询删除是使用批处理实现的，任何失败都会导致整个过程中止，但是当前批处理中的所有失败都将收集到阵列中。您可以使用该conflicts选项来防止重新索引在版本冲突时中止。
}
```

​		查询所有的删除任务

```properties
GET _tasks?detailed=true&actions=*/delete/byquery
```

​		通过taskId查询

```properties
GET /_tasks/Jf-d1OQQS_-8HLzO6WXmlg:10938869
```

## Aggregations（聚合）

### 什么是聚合

​		聚合有助于基于搜索查询提供聚合数据。它基于称为聚合的简单构建基块，可以进行组合以构建复杂的数据汇总。
​		聚合可以看作是在一组文档上建立分析信息的工作单元。执行的上下文定义此文档集是什么（例如，在已执行的查询/搜索请求的过滤器的上下文中执行顶级聚合）。

​		我们也可以理解为将一批数据进行一组构建，从一批数据中得到我们想要的数据，例如我们想要根据类型统计每个类型的文档数量，或者并且分析平均值，最大值，最小值等等。

### 聚合有几种

​		在Elasticsearch中将聚合分为了4种聚合的类型分别是：

```properties
Bucketing									
						生成存储桶的一组聚合，其中每个存储桶都与一个键和一个文档条件相关联。执行聚合时，将对上下文中的每个文档评估所有存储桶条件，并且当条件匹配时，该文档将被视为“落入”相关存储桶。到聚合过程结束时，我们将得到一个存储桶列表-每个存储桶都有一组“属于”的文档。

Metric						
						聚合可跟踪和计算一组文档的指标。
						
Matrix
						一类聚合，可在多个字段上进行操作，并根据从请求的文档字段中提取的值生成矩阵结果。与指标和存储桶聚合不同，此聚合系列尚不支持脚本。
						
Pipeline
						聚合其他聚合及其相关指标的输出的聚合
```

​		聚合语句的结构为

```properties
# 聚合指令
"aggregations" : {
		# 聚合名称
    "<aggregation_name>" : {
    		# 聚合类型
        "<aggregation_type>" : {
        		# 聚合内容
            <aggregation_body>
        }
        # 源，原内容
        [,"meta" : {  [<meta_data_body>] } ]?
        [,"aggregations" : { [<sub_aggregation>]+ } ]?
    }
    [,"<aggregation_name_2>" : { ... } ]*
}
```

### Bucketing（桶聚合）

​		那么我们先试用桶聚合方式进行聚合：

​		我们查询所有content包含"你好"的数据，并且我们创建一个testagg属性，并且进行过滤grpA这个key进行统计，统计查询结果age为20和18的数量

```properties
GET /index1/doc1/_search
{
  "query": {
    "match": {
      "content": "你好"
    }
  },
  "aggs": {
    "testagg": {
      "adjacency_matrix": {
        "filters": {
          "grpA": {"terms" : { "age" : [20,18] }},
          "grpB": {"terms" : { "age" : [20,21] }}
        }
      }
    }
  }
}
```

​		结果如下，我们可以看到，testagg属性下有一个桶，桶下面分别是一个key为grpA，然后统计的数据的条数,并且统计了A和B的相同的数量

```properties
{
	... 其他查询结果
	,
  "aggregations" : {
    "testagg" : {
      "buckets" : [
        {
          "key" : "grpA",
          "doc_count" : 2
        },
        {
          "key" : "grpA&grpB",
          "doc_count" : 1
        },
        {
          "key" : "grpB",
          "doc_count" : 2
        }
      ]
    }
  }
}
```



### Metric



### Matrix



### Pipeline

### 常用聚合

#### 通用属性



#### 类型统计

​		在我们平时所使用的情况下，我们经常会对一些类型进行统计，例如按时间进行格式化统计，或者根据类型，统计每个类型的数量等等，下面我们先来使用统计吧。

​		！！注意atype这个字段的数据类型不能为text，因为text进行了分词直接聚合会报错

```properties
POST /index1/_search?size=0
{
  "query": {
     // 查询条件
  },
  "aggs": {
    "avg_corrected_grade": {
      "terms": {
        "field": "atype"
      }
    }
  }
}
```

​		报错解决方案：修改模板中的atype字段属性fielddata为true，但是结果会进行分词之后的统计，所以还是会出现问题的

```properties
PUT /index1/doc1/_mapping
{
  "properties": {
    "atype":{
      "type": "text",
      "fielddata": true
    }
  }
}
```

​		那么我们这里有3个类型我们可以不可统计这个类型的数量呢，答案是可以的，我们使用cardinality即可统计type的类型有多少个，例如我们统计的数据里面有 atype =一，atype=二，atype=三的，那么上面的方式统计就是一到三分别有多少个，cardinality则是统计这个atype一共有多少个，也就是3个

```properties
POST /index1/_search?size=0
{
  "aggs": {
    "type": {
      "cardinality": {
        "field": "type"
      }
    }
  }
}
```



#### 平均值

​		我们根据查询出来的数据的某一个字段进行平均值的统计,注意age为数字类型，否则报错

```spreadsheet
POST /index1/_search?size=0
{
  "query": {
    "match": {
      "content": "你好"
    }
  },
  "aggs": {
    "avgage": {
      "max": {
        "field": "age"
      }
    }
  }
}
```



#### 最大值

#### 最小值

#### 时间聚合

​		我们有时候需要根据时间来进行聚合，格式化成不同类型的时间

```

```





## Query DSL（DSL查询语句）

### 查询属性

#### 查询指定字段

​		我们可以只查询某一个或者多个字段

```properties
# 只返回age和name字段
GET /index1/doc1/_search
{
  "_source": ["age","name"], 
  "query": {
    "match_all": {
    }
  }
}

# 所有字段都不返回，true则为所有都返回
GET /index1/doc1/_search
{
  "_source": false, 
  "query": {
    "match_all": {
    }
  }
}

GET /index1/doc1/_search
{
  
  "_source": {
    "includes": "只查询的字段", 
    "excludes": "排除的字段"
  }, 
  "query": {
    "match_all": {
    }
  }
}
```



#### 排序

​		查询这些条件然后按照age进行正序排列从小到大，desc从大到小

```properties
GET /myindex/books/_search
{
      "_source": {
        "includes": ["na*","age"],
        "excludes": ["emaile.emaiiles","birthday"]

  },
  "query": {
    "match": {
      "name": "康"
    }
  },
  "sort": [
    {
      "age": {
        "order": "asc"
      }
    }
  ]
}
```

#### 分页

​		查询所有文档中的数据，查询第一页，每页3条数据

```properties
GET /index1/doc1/_search
{
  "query": {
    "match_all": {
    }
  },
  "from": 0,
  "size": 2
}

```

​		from的值为     (页码-1) * size    这个和其他分页一样

#### 高亮显示

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-request-highlighting.html)

​		高亮显示name，锁查询的关键字

```properties
GET /myindex/books/_search
{
    "query":{
        "match":{
            "name": "黄"
        }
    },
    "highlight": {
        "fields": {
             "name": {}
        }
    }
}

# 自定义左右标签高亮
GET  /index1/doc1/_search
{
    "query":{
        "match":{
            "name": "黄"
        }
    },
    "highlight": {
      "pre_tags" : "<tag1>",
        "post_tags" : "</tag1>",
        "fields": {
             "name": {}
        }
    }
}
```

#### 返回版本号

在查询时直接使用

"version":true     就能开启了

```properties
GET /myindex/books/_search
{
  "version":true,
  "query": {
    "match": {
      "name": "康"
    }
  },
  "from": 0,
  "size": 3
}	
```



### Url查询

​		我们可以通过最简单的url路径来进行查询

```http
# 错误方式
GET /index1/doc1/_search?q=content:你好啊
# 正确方式
GET /index1/doc1/_search?q=content:%E4%BD%A0%E5%A5%BD

首先我们使用Http编码将中文转码后进行请求否则会报错
```

​		Url查询的参数有以下：

​		q 字符串查询详解地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-query-string-query.html)

```
q												查询字符串（映射到query_string查询，请参阅 查询字符串查询以获取更多详细信息）。
df											在查询中未定义任何字段前缀时使用的默认字段。
analyzer								分析查询字符串时要使用的分析器名称。
analyze_wildcard				是否应分析通配符和前缀查询。默认为false。
batched_reduce_size			分片结果的数量应在协调节点上立即减少。如果请求中的分片数量可能很大，则此值应用作保护机制以减少每个搜索请求的内存开销。
default_operator				要使用的默认运算符可以是AND或 OR。默认为OR。
lenient									如果设置为true，将导致忽略基于格式的错误（例如，向数字字段提供文本）。默认为false。
explain									对于每个匹配，请说明如何计算匹配得分。
_source									设置为false禁用_source字段检索。您还可以使用_source_includes＆检索文档的一部分_source_excludes（ 有关更多详细信息，请参见请求正文文档）
stored_fields						每次命中将返回的文档的选择性存储字段，以逗号分隔。不指定任何值将导致不返回任何字段。
sort										排序执行。可以采用fieldName或 fieldName:asc/ 的形式fieldName:desc。fieldName可以是文档中的实际字段，也可以是特殊_score名称以指示基于得分的排序。可以有多个sort参数（顺序很重要）
track_scores						排序时，设置为true以便仍跟踪分数并将其作为每次命中的一部分返回。
track_total_hits				设置为false，以禁用跟踪与查询匹配的总点击数。（有关更多详细信息，请参见索引排序）。默认为true。
timeout									搜索超时，将搜索请求限制为在指定的时间值内执行，并保全过期时累积到该点的命中。默认为无超时。
terminate_after					为每个分片收集的最大文档数，达到该数量时查询执行将提前终止。如果设置，响应将具有一个布尔值字段，terminated_early以指示查询执行是否实际上已终止。默认为no terminate_after。
from										从匹配的索引开始到返回。默认为0。
size										返回的点击数。默认为10
search_type							要执行的搜索操作的类型。可以是 dfs_query_then_fetch或query_then_fetch。默认为query_then_fetch。有关可以执行的不同搜索类型的更多详细信息，请参见 搜索类型。
allow_partial_search_results		false如果请求将产生部分结果，则设置为返回整体失败。默认值为true，如果超时或部分失败，将允许部分结果。可以使用群集级别设置来控制此默认值 search.default_allow_partial_results。

```



### 查询所有

查询所有索引的所有数据

```properties
GET _search
{
  "query": {
    "match_all": {}
  }
}
```

查询索引下面的所有数据

```properties
GET /myindex/article/_search
```

### 按ID查询

根据ID进行查询，查询id为1的数据

```properties
GET /myindex/article/1
```

### Term匹配查询

​		对于keyword类型的数据不会分词，如果是text类型的数据则会自动分词，所以我们模糊也能，但是对于keyword类型的数据则需要全部匹配了

​		查询title字段为下雨的数据

```properties
GET /myindex/books/_search/
{
  "query": {
    "term": {
        "title": "下雨"
    }
  }
}
```

title字段为下雨的词

多个term一起查询，查询title为可以以及nice的数据

```properties
GET /myindex/books/_search/
{
  "query": {
    "terms": {
      "title": [
        "可以","nice"
      ]
    }
  }
}
```

### Match模糊查询

​		查询字段name包含康的数据

```properties
GET /myindex/books/_search
{
  "query": {
    "match": {
      "name": "康"
    }
  }
}
```

​		查询name或者title字段包含康这个词的数据，

```properties
GET /myindex/books/_search
{
  "query": {
    "multi_match": {
      "query": "康",
      "fields": [
        "name","title"
        ]
    }
  }
}
```

​		查询模糊关键字并且按照顺序

```properties
GET /myindex/books/_search
{
  "query": {
    "match_phrase": {
      "name": "黄康"
    }
  }
}
```

指定字段返回模糊查询，查询name包含康关键字的数据，并且只查询name和age字段的数据

```properties
GET /myindex/books/_search
{
  "_source": {
      "_source": {
      "includes": ["name","address"],
      "excludes": ["age","birthday"]
  		}
  },
  "query": {
    "match": {
      "name": "康"
    }
  }
}


  "_source": {
        "includes": ["name","age"],
        "excludes": ["emaile.emaiiles","birthday"]

  }
  可以只查询name和age
  excludes排除emaile.emaiiles和birthday字段
  
  还能使用*通配符，这样就能把name，或者是na开头的所有字段查询出来
    "_source": {
        "includes": ["na*","age"],
        "excludes": ["emaile.emaiiles","birthday"]

  }
```

按照前缀查询，头部模糊查询

```properties
GET /myindex/books/_search
{
  "query": {
    "match_phrase_prefix": {
        "name": {
            "query": "zhao"
        }
    }
  }
}

```

查询name姓黄的数据

```properties
GET /myindex/books/_search
{
  "query": {
    "match_phrase_prefix": {
        "name": {
            "query": "黄"
        }
    }
  }
}
```



### 范围查询

​		查询age    20 -21 的数据，包含20不包含21,include_lower表示是否包含最小的起始数，include_upper表示是否包含最大的数,默认都为true包含

```properties
GET /myindex/books/_search
{
  "query": {
    "range": {
      "age": {
        "gte": 20,
        "lte": 21,
        "include_lower": true,
        "include_upper": false
      }
    }
  }
}
```

### 通配符查询

​		通过*查询，只能通过首字符或者是尾字符才能进行查询

```properties
GET /myindex/books/_search
{
  "query": {
    "wildcard": {
      "name": "黄*"
    }
  }
}

```



### Filter过滤

查找age等于19的数据

```properties
GET /myindex/books/_search
{ 
       "post_filter": {
             "term": {
                 "age": 19
             }
       }
}




GET /myindex/books/_search
{ 
       "post_filter": {
             "terms": {
                 "age":[21,20]
             }
       }
}
term换成terms可以将19换为数组		[21,20]
```

过滤