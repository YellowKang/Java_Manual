# Elasticsearch的核心概念

cluster（集群）

```
	集群由一个或多个节点组成。一个集群有一个默认名称"Elasticsearch"。 注意：不同集群，集群名称应唯一。 
```

Node（节点）

```
节点是集群的一部分。ES 6.x中，有多种类型的节点：

		Master节点：存元数据。

		Data节点：存数据。

		Ingest节点：可在数据真正进入index前,通过配置pipline拦截器对数据ETL。

		Coordinate节点：协调节点。如接收搜索请求，并将请求转发到数据节点，每个数据节点在本地执行请求并将结果返回给协调节点。

		协调节点将每个数据节点的结果汇总并返回给客户端。每个节点默认都是一个协调节点。当将node.master，node.data和node.ingest设置为false时，该节点仅用作协调节点。

注意：Coordinate Tribe 是一种特殊类型的协调节点，可连接到多个集群并在所有连接的集群中执行搜索和其他操作。

```

分片(shard)和副本(replica)

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

## Index（索引）

```
一个index可以理解成一个关系型数据库。
```

## Type（类型）

```
一种type就像一类表。如用户表、充值表等。

注意：

    - ES 5.x中一个index可以有多种type。

    - ES 6.x中一个index只能有一种type。

    - ES 7.x以后，将移除type这个概念。
```

## Document（文档 ）

```
	一个document相当于关系型数据库中的一行row。 
```

## Filed（字段）

```
	文档中的一个字段field就相当于关系型数据库中的一列column。 
```

## Mapping（映射）

```
	mapping定义了每个字段的类型、字段所使用的分词器等。相当于关系型数据库中的表结构。 
```

Query DSL

```
	类似于MySQL的SQL语句用来编写查询数据的语句以及查询条件和处理等等
```

segment（分段）

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

## 倒排索引



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

## 索引

### 索引创建

使用Kibana创建一个索引为docment1，然后设置中3个分片，1个副本备份（注：创建分片以及副本以后不能更改）

```
PUT /docment1
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  }
}
```

如果不设置，默认分片5片，副本一份

### 查看索引

我们先来查看docment1这个索引的设置信息

```
GET /docment1/_settings
```

查看所有的索引信息

```
GET _all/_settings
```

### 删除索引

删除docment2这个索引

```
DELETE docment2
```

删除之后所有这个索引相关数据都没了

## 文档

### 添加文档

指定id进行创建文档

我们使用Kibana进行添加文档，使用PUT新增一个id为1的索引，里面有字段name，age等等

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

# DSL查询语句

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