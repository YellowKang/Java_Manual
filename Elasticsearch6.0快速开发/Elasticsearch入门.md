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

## Mapping(映射)

### 新建Mapping

新建一个Mapping，设置分片3片副本0个，然后类型是books，然后配置它的字段title，类型为text

```
PUT /myindex
{
 "settings":{
	"number_of_shards" : 3,
	"number_of_replicas" : 0
},
 "mappings":{
  "books":{
    "properties":{
        "title":{"type":"text"},
        "name":{"type":"text","index":false},
        "publish_date":{"type":"date","index":false},
        "price":{"type":"double"},
        "number":{"type":"integer"}
    }
  }
 }
}
```

### 查看Mapping

如何查看mapping的属性设置呢？

```

```



### 新建Mapping属性

#### 定制类型

定制dynamic，定制自己的字段，这个mapping类型为integer，它的dynamic为true

```
"number":{
		"type":"integer",
		"dynamic":true
}
```

```
"dynamic":true					遇到陌生字段，就进行dynamic mapping
"dynamic":false					遇到陌生字段，就忽略
"dynamic":strict				遇到陌生字段，就报错
```

这个属性可以直接加在类型上"dynamic": "strict",例如，那么这个类型就是不可变的了，如果我们新建了mapping中没有的字段那么就会报错

```
PUT /myindex
{
 "settings":{
	"number_of_shards" : 3,
	"number_of_replicas" : 0
},
 "mappings":{
  "books":{
    "dynamic": "strict",
    "properties":{
        "title":{"type":"text"},
        "name":{"type":"text","index":false},
        "publish_date":{"type":"date","index":false},
        "price":{"type":"double"},
        "number":{"type":"integer"}
    }
  }
 }
}
```



#### 属性配置

```
"title":{
	"type":"text",
	"store":false     
}
```

​    //是否单独设置此字段的是否存储而从_source字段中分离，默认是false，如果设置只能搜索，不能获取值

```
"title":{
	"type":"text",
	"index": true   
}
```

//分词，不分词是：false,设置成false，字段将不会被索引

```
"title":{
	"type":"text",
	"analyzer":"ik_max_word"
}

可以设置两个属性
ik_max_word				最细粒度拆分		例如：我今天在北京{我，今天，北京，在，我今天，等等}
ik_smart				最粗粒度拆分		例如：我今天在北京{我，今天，在北京，我今天在北京}
```

//指定分词器,默认分词器为standard analyzer

```
"title":{
	"type":"text",
	"boost":1.23
}
```

//字段级别的分数加权，默认值是1.0

```
"title":{
	"type":"text",
	"doc_values":false
}
```

//对not_analyzed字段，默认都是开启，分词字段不能使用，对排序和聚合能提升较大性能，节约内存

```
"title":{
	"type":"text",
	"fielddata":{"format":"disabled"}
}
```

//针对分词字段，参与排序或聚合时能提高性能，不分词字段统一建议使用doc_value

```
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

//可以对一个字段提供多种索引模式，同一个字段的值，一个分词，一个不分词

```
"title":{
	"type":"text",
	"ignore_above":100
}
```

//超过100个字符的文本，将会被忽略，不被索引

```
"title":{
	"type":"text",
	"include_in_all":ture
}
```

//设置是否此字段包含在_all字段中，默认是true，除非index设置成no选项

```
"title":{
	"type":"text",
	"index_options":"docs"
}
```

//4个可选参数docs（索引文档号） ,freqs（文档号+词频），positions（文档号+词频+位置，通常用来距离查询），offsets（文档号+词频+位置+偏移量，通常被使用在高亮字段）分词字段默认是position，其他的默认是docs

```
"title":{
	"type":"text",
	"norms":{"enable":true,"loading":"lazy"}
}
```

//分词字段默认配置，不分词字段：默认{"enable":false}，存储长度因子和索引时boost，建议对需要参与评分字段使用 ，会额外增加内存消耗量

```
"title":{
	"type":"text",
	"null_value":"NULL"
}
```

//设置一些缺失字段的初始化值，只有string可以使用，分词字段的null值也会被分词

```
"title":{
	"type":"text",
	"position_increament_gap":0
}
```

//影响距离查询或近似查询，可以设置在多值字段的数据上火分词字段上，查询时可指定slop间隔，默认值是100

```
"title":{
	"type":"text",
	"search_analyzer":"ik"
}
```

//设置搜索时的分词器，默认跟ananlyzer是一致的，比如index时用standard+ngram，搜索时用standard用来完成自动提示功能

```
"title":{
	"type":"text",
	"similarity":"BM25"
}
```

//默认是TF/IDF算法，指定一个字段评分策略，仅仅对字符串型和分词类型有效

```
"title":{
	"type":"text",
	"term_vector":"no"
}
```

//默认不存储向量信息，支持参数yes（term存储），with_positions（term+位置）,with_offsets（term+偏移量），with_positions_offsets(term+位置+偏移量) 对快速高亮fast vector highlighter能提升性能，但开启又会加大索引体积，不适合大数据量用

## Index（索引）

### 索引创建

​		使用Kibana创建一个索引为docment1，然后设置中3个分片，1个副本备份（注：创建主分片以后不能更改，但是副本集是可以修改数量的）

```
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

​		我们先来查看docment1这个索引的设置信息

```
GET /docment1/_settings
```

​		查看所有的索引信息

```
GET _all/_settings
```

### 删除索引

​		删除docment2这个索引

```
DELETE docment2
```

​		删除之后所有这个索引相关数据都没了

## Document（文档）

### 添加文档

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

### 查看文档

查看所有索引的所有文档

```
GET _search
{
  "query": {
    "match_all": {}
  }
}
```

查看docment1索引test类型Id为1的文档

```
GET /docment1/test/1
```

查看所有docment1下面test的文档

```
GET /docment1/test/_search
```

查看docment1下面test类型id为1的文档的name和age属性值，这样就能只查看name和age了

```
GET /docment1/test/1?_source=age,name
```

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



### 更新文档

更新文档（覆盖掉以前的文档），这样就把以前的文档覆盖掉了但是如果有一个字段没有设置那么那个字段也就会丢失

```
PUT /docment1/test/1
{
  "name":"黄康",
  "age":18,
  "sex":"男",
  "address":"四川达州开江",
  "emaile":"bigkang@126.com",
  "like":"股票，游戏，户外"
}
```

更新文档不覆盖，更新id为1的文档的age为19

```
POST /docment1/test/1/_update
{
  "doc": {
    "age":19
  }
}
```

### 删除文档

删除docment1这个索引的test类型的id等于1的这个文档

```
DELETE /docment1/test/1
```

## Query DSL（DSL查询语句）

### 查询所有

查询所有索引的所有数据

```
GET _search
{
  "query": {
    "match_all": {}
  }
}
```

查询索引下面的所有数据

```
GET /myindex/article/_search
```

### 按条件查询

根据ID进行查询，查询id为1的数据

```
GET /myindex/article/1
```

### term包含查询

查询title字段包含下雨的数据

```
GET /myindex/books/_search/
{
  "query": {
    "term": {
        "title": "下雨"
    }
  }
}
```

查询title字段包含下雨的词

注意：如果根据名字进行查询不能使用全名，否则搜索不到，只能是包含

多个term一起查询，查询title下面包含可以活着nice的数据

```
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

查询字段name包含康的数据

```
GET /myindex/books/_search
{
  "query": {
    "match": {
      "name": "康"
    }
  }
}
```

查询name或者title字段包含康这个词的数据，

```
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

查询模糊关键字并且按照顺序

```
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

```
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

```
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

```
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

### 分页查询

查询name包含康的数据，查询第一页，每页3条数据

```
GET /myindex/books/_search
{
  "query": {
    "match": {
      "name": "康"
    }
  },
  "from": 0,
  "size": 3
}
```

from的值为     (页码-1) * size    这个和其他分页一样

### 返回版本号

在查询时直接使用

"version":true     就能开启了

```
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

### 排序

查询这些条件然后按照age进行正序排列从小到大，desc从大到小

```
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

### 范围查询

查询age    20 -21 的数据，包含20不包含21

```
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

通过*查询

```
GET /myindex/books/_search
{
  "query": {
    "wildcard": {
      "name": "黄*"
    }
  }
}

```

### 高亮显示

高亮显示

```
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

高亮显示name，锁查询的关键字
```

### Filter过滤

查找age等于19的数据

```
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