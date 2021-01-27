# 单机版

## 	下载镜像

​	首先先下载Elasticsearch6.0的版本

```
docker pull docker.io/elasticsearch:6.7.0
```

## 简单测试容器

然后我们运行容器

```sh
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 --name elasticsearch docker.io/elasticsearch:6.7.0
```

​	这里并没有挂载文件，并且设置了Jvm启动参数（由于使用云服务器，内存不够）

​	然后我们docker ps 查看容器

```sh
docker ps
```

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1567161267350.png)

## 生产级容器启动

创建挂载文件夹

```sh
mkdir -p /docker/elasticsearch/{data,conf,plugins}
chmod -R 777 /docker/elasticsearch
```

创建挂载文件

```json
echo "cluster.name: elasticsearch-cluster
node.name: elasticsearch
network.bind_host: 0.0.0.0
network.publish_host: 127.0.0.1
http.port: 9200
transport.tcp.port: 9300
http.cors.enabled: true
http.cors.allow-origin: \"*\"
discovery.type: single-node" > /docker/elasticsearch/conf/es.yml
cat /docker/elasticsearch/conf/es.yml
```

然后直接启动容器，内存够的朋友不需要指定jvm参数

```sh
docker run -d \
--name elasticsearch \
-e ES_JAVA_OPTS="-Xms1g -Xmx1g" \
--restart=always \
-p 10092:9200 \
-p 10093:9300 \
-v /docker/elasticsearch/conf/es.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /docker/elasticsearch/data:/usr/share/elasticsearch/data \
-v /docker/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
docker.io/elasticsearch:6.7.0
```

# 集群版

## 镜像下载

首先我们也先下载一个Es6.0

```sh
docker pull docker.io/elasticsearch:6.7.0
```

## 配置文件编写

然后我们先来编写配置文件

我们先创建3个配置文件文件

```sh
首先创建文件夹
mkdir /docker/elasticsearch-cluster/node1/{data,conf,plugins}
mkdir /docker/elasticsearch-cluster/node2/{data,conf,plugins}
mkdir /docker/elasticsearch-cluster/node3/{data,conf,plugins}

然后进入创建三个文件分别为es-cluster.yml


touch /docker/elasticsearch-cluster/node1/conf/es-cluster.yml
touch /docker/elasticsearch-cluster/node2/conf/es-cluster.yml
touch /docker/elasticsearch-cluster/node3/conf/es-cluster.yml
```

然后在下面分别将下面三个配置文件中的内容写上去，直接复制命令执行即可，一键写入文件

请注意修改network.publish_host，以及端口号以及集群信息等等

```properties
# 集群名称（多个节点同一个集群名称）
cluster.name: 
# 节点名称各个节点名称不一样
node.name: 
# 绑定host，设置0.0.0.0表示所有ip可读
network.bind_host: 0.0.0.0
# 本机IP一般设置为本机的IP地址，内网或者公网
network.publish_host: 127.0.0.1
# http端口
http.port: 9200
# transport端口
transport.tcp.port: 9300
# 是否开启跨域
http.cors.enabled: true
# 跨域
http.cors.allow-origin: \"*\"
# 是否master节点
node.master: true
# 是否存储数据（一般节点比较多的时候master都是不存储数据的，节点少情况可以减少占用资源）
node.data: true
# 这里用来配置发现集群配置的单播节点，通常这里配置master节点地址
discovery.zen.ping.unicast.hosts: ["122.114.65.232:9300","122.114.65.233:9300"]
# 配置发现的最小master存活数
discovery.zen.minimum_master_nodes: 1
```

es-cluster1.yml

```sh
echo "cluster.name: es-cluster
node.name: es-node1
network.bind_host: 0.0.0.0
network.publish_host: 122.114.65.233
http.port: 9201
transport.tcp.port: 9301
http.cors.enabled: true
http.cors.allow-origin: \"*\"
node.master: true
node.data: true
discovery.zen.ping.unicast.hosts: [\"122.114.65.233:9301\",\"122.114.65.233:9302\",\"122.114.65.233:9303\"]
discovery.zen.minimum_master_nodes: 1" > /docker/elasticsearch-cluster/node1/conf/es-cluster.yml
```

es-cluster2.yml

```sh
echo "cluster.name: es-cluster
node.name: es-node2
network.bind_host: 0.0.0.0
network.publish_host: 122.114.65.233
http.port: 9202
transport.tcp.port: 9302
http.cors.enabled: true
http.cors.allow-origin: \"*\"
node.master: true
node.data: true
discovery.zen.ping.unicast.hosts: [\"122.114.65.233:9301\",\"122.114.65.233:9302\",\"122.114.65.233:9303\"]
discovery.zen.minimum_master_nodes: 1" > /docker/elasticsearch-cluster/node2/conf/es-cluster.yml
```

es-cluster3.yml

```sh
echo "cluster.name: es-cluster
node.name: es-node3
network.bind_host: 0.0.0.0
network.publish_host: 122.114.65.233
http.port: 9203
transport.tcp.port: 9303
http.cors.enabled: true
http.cors.allow-origin: \"*\"
node.master: true
node.data: true
discovery.zen.ping.unicast.hosts: [\"122.114.65.233:9301\",\"122.114.65.233:9302\",\"122.114.65.233:9303\"]
discovery.zen.minimum_master_nodes: 1" > /docker/elasticsearch-cluster/node3/conf/es-cluster.yml
```

这三个文件夹分别修改，node.name集群节点的名字，不能相同

还有就是ip地址，network.publish_host写每一台主机的当前节点主机ip

以及discovery.zen.ping.unicast.hosts这个集群的ip因为要和集群节点通信所以我们在一台机器虚拟化3个端口，这里可以自行修改

我们这里部署了三个master+data节点

## 启动容器

这样我们就准备好了然后我们进行容器的启动

启动第一个容器es-node1

```shell
docker run -d \
--name es-node1 \
-e ES_JAVA_OPTS="-Xms2g -Xmx2g" \
--restart=always \
-p 9201:9201 \
-p 9301:9301 \
-v /docker/elasticsearch-cluster-data/node1/conf/es.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /docker/elasticsearch-cluster-data/node1/data:/usr/share/elasticsearch/data \
-v /docker/elasticsearch-cluster-data/node1/plugins:/usr/share/elasticsearch/plugins \
docker.io/elasticsearch:6.7.0
```

启动第二个容器es-node2

```shell
docker run -d \
--name es-node2 \
-e ES_JAVA_OPTS="-Xms2g -Xmx2g" \
--restart=always \
-p 9202:9202 \
-p 9302:9302 \
-v /docker/elasticsearch-cluster-data/node2/conf/es.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /docker/elasticsearch-cluster-data/node2/data:/usr/share/elasticsearch/data \
-v /docker/elasticsearch-cluster-data/node2/plugins:/usr/share/elasticsearch/plugins \
docker.io/elasticsearch:6.7.0
```

启动第三个容器es-node3

```shell
docker run -d \
--name es-node3 \
-e ES_JAVA_OPTS="-Xms2g -Xmx2g" \
--restart=always \
-p 9203:9203 \
-p 9303:9303 \
-v /docker/elasticsearch-cluster-data/node3/conf/es.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /docker/elasticsearch-cluster-data/node3/data:/usr/share/elasticsearch/data \
-v /docker/elasticsearch-cluster-data/node3/plugins:/usr/share/elasticsearch/plugins \
docker.io/elasticsearch:6.7.0
```

可以将jvm优化那个删除（由于内存不够设置），可以修改挂载的文件地址（不建议，因为修改地方多），可以修改数据挂载文件或者不指定（不建议） ，然后指定镜像运行容器

# Kibana安装

​		首先先下载Kibana镜像（一定要对应的es版本）

```sh
docker pull docker.io/kibana:6.7.0
```

​		然后我们新建一个配置文件用来存储配置文件

​		然后添加以下内容，下面的hosts地址修改为es地址

```sh
mkdir -p /docker/kibana/conf
echo "server.name: kibana
server.host: \"0\"
elasticsearch.hosts: [ \"http://192.168.1.12:10092\" ]
xpack.monitoring.ui.container.elasticsearch.enabled: true" > /docker/kibana/conf/kibana.yml
```

​		然后我们就能启动容器了

```sh
docker run -d \
--name kibana6.7 \
-p 15601:5601 \
--restart=unless-stopped \
-v /docker/kibana/conf/kibana.yml:/usr/share/kibana/config/kibana.yml \
-e ELASTICSEARCH_URL=http://http://192.168.1.12:10092 docker.io/kibana:6.7.0 
```

​		然后等待容器启动一会直接访问5601端口

​		注意：（如果连接公网请开放端口或者关闭防火墙否则一直无法连接）

# 监控工具

## Cerebro

​		目前介绍cerebro好用简单

```sh
docker run -itd \
--name cerebro \
-p 9000:9000 \
--restart=unless-stopped \
-v /etc/localtime:/etc/localtime \
-v cerebro:/opt/cerebro \
-h cerebro \
lmenezes/cerebro
```

## Head

​		拉取镜像（其实所有版本都支持，不用在意）

```sh
docker pull  docker.io/mobz/elasticsearch-head:5
```

​		启动容器

```sh
docker run -itd \
--name head-es \
--restart=unless-stopped \
-p 9100:9100 \
docker.io/mobz/elasticsearch-head:5
```

​		解决Head无法访问的问题

```properties
# 修改Es配置文件
# 将下面两行代码加入Es当中，开启Elasticsearch的跨域
http.cors.enabled: true
http.cors.allow-origin: "*"

# 然后重启容器
docker restart elasticsearch6.7
```

​		骚操作只谷歌插件：直接可以谷歌插件安装Es—Head

# 插件安装

### 官网插件

​		Es插件官网地址

```http
https://www.elastic.co/guide/en/elasticsearch/plugins/index.html
```



### 下载加速

​		我们可以看使用加速网站进行下载

```sh
https://github.91chifun.workers.dev//https://github.com/NLPchina/elasticsearch-sql/releases/download/7.9.3.0/elasticsearch-sql-7.9.3.0.zip
```

### IK分词器安装

#### 在线联网安装

直接进入容器内部进行编辑

```sh
# 进入容器内部编辑
docker exec -it  elasticsearch bash

# 安装IK分词器插件(Github官网)
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.0/elasticsearch-analysis-ik-6.7.0.zip

# 或者加速下载（第三方加速）
elasticsearch-plugin install https://github.91chifun.workers.dev//https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.0/elasticsearch-analysis-ik-6.7.0.zip

```

等待下载完成然后cd，然后查看是否有ik分词器

```sh
cd plugins/
ls
```

如果有ik分词器则安装完成，然后重新启动es然后访问

#### 离线安装



```shell
# 下载
wget https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.0/elasticsearch-analysis-ik-6.7.0.zip

# 加速
wget https://github.91chifun.workers.dev//https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.0/elasticsearch-analysis-ik-6.7.0.zip

# 解压
mkdir ./ik
unzip elasticsearch-analysis-ik-6.7.0.zip -d ./ik/
# 复制到容器内
docker cp ik elasticsearch:/usr/share/elasticsearch/plugins/

# 重启es节点
docker restart elasticsearch

```

查看日志出现ik则成功

```
docker logs	-f elasticsearch
```

#### 测试

我们使用kibanna或者发送请求

```json
# 最大分词（将词以细粒度分词，搜索分词数量多，精确）
GET _analyze
{
  "analyzer":"ik_max_word",
  "text":"我是中国人"
}

# 短语分词（将词拆分短语，分词数少）
GET _analyze
{
  "analyzer":"ik_smart",
  "text":"我是中国人"
}
```





如果返回如下的信息表示安装成功

```json
{
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "中国人",
      "start_offset" : 2,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "中国",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 3
    },
    {
      "token" : "国人",
      "start_offset" : 3,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 4
    }
  ]
}

```





### 拼音插件

#### 在线联网安装

直接进入容器内部进行编辑

```sh
# 进入容器内部编辑
docker exec -it  elasticsearch bash

# 安装IK分词器拼音插件(Github官网)
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.7.0/elasticsearch-analysis-pinyin-6.7.0.zip


# 安装IK分词器插件(第三方加速)
elasticsearch-plugin install https://github.91chifun.workers.dev//https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.7.0/elasticsearch-analysis-pinyin-6.7.0.zip
```

等待下载完成然后cd，然后查看是否有pinyin插件

```sh
cd plugins/
ls
```

如果有pinyin插件则安装完成，然后重新启动es然后访问

#### 离线安装

```shell
# 下载
wget install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.7.0/elasticsearch-analysis-pinyin-6.7.0.zip
# 解压
mkdir ./pinyin
unzip elasticsearch-analysis-pinyin-6.7.0.zip -d ./pinyin/

# 重启es节点
docker restart elasticsearch
```

#### 测试

```json
GET _analyze
{
  "text":"刘德华",
  "analyzer":"pinyin_analyzer"
}
```

如果出现如下则表示成功

```json
{
  "tokens" : [
    {
      "token" : "liu",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "liudehua",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "de",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 1
    },
    {
      "token" : "hua",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 2
    }
  ]
}
```

下面我们将拼音以及分词都结合起来进行搜索

首先我们创建一个索引，这里表示我们分词采用自定义的方式进行分词我们分别将ik_smart以及ik_max_word都对pinyin进行了整合，并且我们的主分片3个，每个分片一个副本集

```json
PUT /test_pinyin
{
  "settings": {
        "analysis": {
            "analyzer": {
                "ik_smart_pinyin": {
                    "type": "custom",
                    "tokenizer": "ik_smart",
                    "filter": ["my_pinyin", "word_delimiter"]
                },
                "ik_max_word_pinyin": {
                    "type": "custom",
                    "tokenizer": "ik_max_word",
                    "filter": ["my_pinyin", "word_delimiter"]
                }
            },
            "filter": {
                "my_pinyin": {
                    "type" : "pinyin",
                    "keep_separate_first_letter" : true,
                    "keep_full_pinyin" : true,
                    "keep_original" : true,
                    "first_letter": "prefix",
                    "limit_first_letter_length" : 16,
                    "lowercase" : true,
                    "remove_duplicated_term" : true 
                }
            }
        },
        "number_of_shards": 3,
        "number_of_replicas": 1
  }
}
```

然后我们创建一个_mapping模板他的类型是test，用于设置字段指定使用哪个分词器，手动创建mapping

```json
PUT /test_pinyin/test/_mapping
{
    "properties": {
        "content": {
            "type": "text",
						"analyzer": "ik_smart_pinyin",
						"search_analyzer": "ik_smart_pinyin",
            "fields": {
                "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                }
            }
        },
        "age": {
            "type": "long"
        }
    }
}
```

然后创建之后我们来导入条数据

```json
POST /test_pinyin/test/
{
  "content":"小米手机有点东西",
  "age":18
}


POST /test_pinyin/test/
{
  "content":"中华人民共和国有个刘德华",
  "age":18
}

```

然后我们就能开始愉快的查询了,首先我们不分词直接中文搜索

```json
# 搜索刘德华查询出结果
POST /test_pinyin/test/_search
{
  "query":{
    "match":{
      "content":"刘德华"
    }
  }
}
# 搜索liudehua查询出结果
POST /test_pinyin/test/_search
{
  "query":{
    "match":{
      "content":"liudehua"
    }
  }
}
# 搜索小米查询出结果
POST /test_pinyin/test/_search
{
  "query":{
    "match":{
      "content":"小米"
    }
  }
}

# 搜索xiaomi查询出结果
POST /test_pinyin/test/_search
{
  "query":{
    "match":{
      "content":"xiaomi"
    }
  }
}



```

​		以下就是我们的拼音+分词了

### 文件搜索Ingest-Attachment插件

#### 在线联网安装		

```sh
# 进入容器内部编辑
docker exec -it  elasticsearch bash

# 安装Ingest-Attachment文件插件(Github官网)
elasticsearch-plugin install https://artifacts.elastic.co/downloads/elasticsearch-plugins/ingest-attachment/ingest-attachment-6.7.0.zip


```

#### 离线安装

```sh
# 下载
wget install https://artifacts.elastic.co/downloads/elasticsearch-plugins/ingest-attachment/ingest-attachment-6.7.0.zip
# 解压
mkdir ./ingest-attachment
unzip ingest-attachment-6.7.0.zip -d ./ingest-attachment/

# 重启es节点
docker restart elasticsearch
```

#### 测试

​		首先我们需要节点中

```properties
# 首选需要建立ingest管道建立一个attachment管道,然后我们对filebase64这个字段进行处理

# 然后我们添加一条数据进行测试即可
curl -X PUT "http://192.168.1.11:9200/demo/pipeline/attachment" -d '{
    "description": "文件摄取管道",
    "processors": [
        {
            "attachment": {
                "field": "filebase64",
                "properties": [
                  "content",
                  "title",
                  "content_type",
                  "content_length",
                  "author"
                ],
                "indexed_chars": -1,
                "ignore_missing": true
            }
        },
        {
            "remove": {
                "field": "filebase64"
            }
        }
    ]
}'

# 我们需要建立一个管道为attachment，然后对里面的文件进行处理
# 我们会将data字段进行解析，处理成attachment对象，以及三个属性
		attachment.content
		attachment.title
		attachment.content_type
# 然后我们来建立Mapping映射,并且我们需要进行文档搜索，使用分词器
PUT /demo/_mapping/demo
{
    "properties": {
        "attachment": {
            "properties": {
                "content": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "search_analyzer": "ik_max_word"
                },
                "content_type": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "search_analyzer": "ik_max_word"
                },
                "title": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "search_analyzer": "ik_max_word"
                }
            }
        }
    }
}

# 然后我们上传一个文档的Base64，filebase64字段的值为Base64的文件编码，然后指定处理管道为添加的attachment
PUT /demo/demo/2?pipeline=attachment
{
  "filebase64":"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"
}
```

​		文档内容如下

<img src="https://blog-kang.oss-cn-beijing.aliyuncs.com/1611656732931.png" style="zoom:50%;" />

```properties
# 然后我们再来查看这个2
GET /demo/demo/2
# 会查看到如下内容
# 返回结果集
{
  "_index" : "demo",
  "_type" : "demo",
  "_id" : "2",
  "_version" : 2,
  "_seq_no" : 1,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "attachment" : {
      "date" : "2016-11-19T08:45:00Z",
      "content_type" : "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      "author" : "peng liu",
      "language" : "lt",
      "content" : "测试内容，搜索关键字   文档  四川 达州 BigKang",
      "content_length" : 31
    }
  }
}

# 然后我们再来进行文档搜索即可，就可以发现能够进行文档搜索了
POST /demo/_search
{
  "query": {
    "match": {
      "attachment.content": "四川"
    }
  }
}
```

​		将文件转Base64方法如下

```java
        File file = new File("/Users/bigkang/Documents/项目文档/测试文档.docx");
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        String encode = new BASE64Encoder().encode(buffer);
        System.out.println(encode);
        System.out.println(encode.length());
```

​		通常我们还会将文件上传到文件服务器上，搜索完毕后我们需要存储文件的上传路径然后查询出来，根据路径下载文件，这就是Es搜索Word文档，然后进行下载的业务流程，并且也可以结合拼音插件，进行文档的拼音搜索。

​		如果文本内容过长，我们则可以在建立mapping时隐藏掉该字段,但是不推荐使用

```properties
# 但是不推荐使用
PUT /demo/_mapping/demo
{
  "_source": {
    "excludes":["attachment.content"]
  }, 
    "properties": {
        "attachment": {
            "properties": {
                "filebase64":{
                   	"type": "keyword"
                },
                "content": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "search_analyzer": "ik_max_word"
                },
                "content_type": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "search_analyzer": "ik_max_word"
                },
                "title": {
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "search_analyzer": "ik_max_word"
                }
            }
        }
    }
}

# 推荐保存，然后在查询中进行过滤，否则重新索引的时候无法重新索引导致数据丢失,可以排除掉content
GET /demo/demo/2?_source_exclude=attachment.content
```



### SQL插件

​		官网地址：[点击进入](https://github.com/NLPchina/elasticsearch-sql)

​		SQL有两种方式进行实现：使用插件，或者使用官方的[Xpack-SQL](https://www.elastic.co/guide/en/elasticsearch/reference/6.7/xpack-sql.html)

#### 在线联网安装		

​		直接进入容器内部进行编辑

```sh
进入容器内部编辑
docker exec -it  elasticsearch bash

安装IK分词器插件
elasticsearch-plugin install https://github.com/NLPchina/elasticsearch-sql/releases/download/6.7.0.0/elasticsearch-sql-6.7.0.0.zip
```

​		等待下载完成然后cd，然后查看是否有sql插件

#### 离线安装

```sh
# 下载
wget install https://github.com/NLPchina/elasticsearch-sql/releases/download/6.7.0.0/elasticsearch-sql-6.7.0.0.zip
# 解压
mkdir ./pinyin
unzip elasticsearch-analysis-pinyin-6.7.0.zip -d ./pinyin/

# 重启es节点
docker restart elasticsearch

```

#### 测试





# Elasticsearch配置详解

## 路径设置

​		我们可以设置数据存储路径和日志路径

​				path.data			Elasticsearch存储数据的路径

​				path.logs			 Elasticsearch存储日志的路径

```sh
# 数据存储路径,可以设置多个，扩容磁盘格式  $PATH,$PATH,使用逗号分割
path.data:/usr/share/elasticsearch/data
# 日志路径
path.logs:/usr/share/elasticsearch/logs
```

​		设置多个数据存储路径，以数组方式

```properties
path.data[0]:/usr/share/elasticsearch/data/es_data_1
path.data[1]:/usr/share/elasticsearch/data/es_data_2
path.data[2]:/usr/share/elasticsearch/data/es_data_3
```

​		格式化后Yaml版本

```properties
path:
	# 数据存储路径
  data: /usr/share/elasticsearch/data
  # 日志路径
  logs: /usr/share/elasticsearch/logs
```

​		多个数据路径Yaml

```yaml
path:
  data:
  	- /usr/share/elasticsearch/data/es_data_1
  	- /usr/share/elasticsearch/data/es_data_2
  	- /usr/share/elasticsearch/data/es_data_3
```

## 集群名称设置

​		我们可以设置集群名称

​		注意：当`cluster.name`节点与集群中的所有其他节点共享节点时，该节点只能加入集群。默认名称为`elasticsearch`，但您应将其更改为描述群集用途的适当名称。确保不要在不同的环境中重复使用相同的集群名称，否则最终可能会导致节点加入了错误的集群。

​		如果想要搭建集群，则集群名称都要设置成一样的

```properties
cluster.name:bigkang-cluster
```

## 节点设置

### 节点名称

​		默认情况下，Elasticsearch将使用随机生成的UUID的前七个字符作为节点ID。请注意，节点ID是持久的，并且在节点重新启动时不会更改，因此默认节点名称也不会更改。值得配置一个更有意义的名称，该名称还将具有重新启动节点后仍然存在的优点。

​		我们设置的时候通常会使用两种方式进行设置：

​				第一种（自定义节点名称）：

```sh
node.name: prod-data-2
```

​				第二种（我们使用主机名称当做节点名称）：

```sh
node.name: ${HOSTNAME}
```

### 节点角色

​		默认的ELasticsearch如果我们不进行设置，那么他会有多个角色，那么这个在我们的小规模的集群中是可以的，但是当我们的数据觉来越多，节点越来越多，集群越来越庞大的时候那么我们

​		Elasticsearch中我们使用节点角色大概分别分为四种（不包括远程集群多个集群）：

```properties
			Master节点（主节点，管理节点）
			Data节点（数据节点，存储节点）
			Ingest节点（预处理节点，预处理数据）
			Query节点（查询节点，负载均衡分发请求仅协调节点）
```

​		在ELasticsearch中分别的设置为：

```properties
node.master:  			（默认启用）
node.data:  				（默认启用）
node.ingest:  			（默认启用）
```

​		那么ELasticsearch官方在设置中也给我们进行了描述分别针对各个角色的设置

​		**Master节点**

```properties
		# Master主节点负责集群范围内的轻量级操作，例如创建或删除索引，跟踪哪些节点是集群的一部分以及确定将哪些分片分配给哪些节点。拥有稳定的主节点对于群集健康非常重要。可以通过主选举过程将任何符合主资格的节点（默认情况下为所有节点）选举为主节点。
		# 索引和搜索数据是占用大量CPU，内存和I / O的工作，这可能会对节点的资源造成压力。为了确保您的主节点稳定且不受压力，在较大的群集中，最好将符合角色的专用主节点和专用数据节点分开。
		# 尽管主节点还可以充当协调节点， 并将搜索和索引请求从客户端路由到数据节点，但最好不要为此目的使用专用的主节点。对于符合主机要求的节点，其工作量应尽可能少，这对于群集的稳定性很重要。
		
		# 上面我们可以看到针对我们的Master节点，就是管理集群的作用，并且我们不能让他有太大的负担（虽然也能作为data节点），所以官方推荐如下设置
		
		node.master: true 
		node.data: false 
		node.ingest: false 
		cluster.remote.connect: false
		
		# 我们可以看到他的作用就只有master节点，并且data和ingest节点都是false，cluster.remote.connect是禁止其他的集群远程连接我们
		# 并且查看后面的服务发现章节会对master的脑裂问题进行处理，master是集群中非常重要的
```

​		**Data节点**

```properties
		# 数据节点包含包含您已建立索引的文档的分片。数据节点处理与数据相关的操作，例如CRUD，搜索和聚合。这些操作是I/O，内存和CPU密集型的。监视这些资源并在过载时添加更多数据节点非常重要。

		# 具有专用数据节点的主要好处是将主角色和数据角色分开。
		
		# 那么我们肯定就负责干活的节点不需要当master主节点了，所以设置如下
		
		node.master: false 
		node.data: true 
		node.ingest: false 
```

​		**Ingest节点**

```properties
		# Ingest接收节点可以执行由一个或多个接收处理器组成的预处理管道。根据摄取处理器执行的操作类型和所需的资源，拥有专用的摄取节点可能有意义，该节点仅执行此特定任务。
		# 这里我们可以看到Ingest节点主要是用于我们执行数据处理执行任务所使用，平时我们单独部署Ingest节点的情况较少。
		
		# 配置如下：
		
		node.master: false
		node.data: false
		node.ingest: true
		cluster.remote.connect: false
```

​		**Query节点**

```properties
		# 如果您不具备处理主要职责，保存数据和预处理文档的能力，那么您将拥有一个仅可路由请求，处理搜索缩减阶段并分配批量索引的协调节点。本质上，仅协调节点可充当智能负载平衡器。
		# 仅协调节点可以通过从数据和符合资格的主节点上卸载协调节点角色来使大型集群受益。他们像其他节点一样加入集群并接收完整的集群状态，并且使用集群状态将请求直接路由到适当的位置。
		# 在集群中添加过多的仅协调节点可能会增加整个集群的负担，因为选择的主节点必须等待每个节点的集群状态更新确认！仅协调节点的好处不应被夸大-数据节点可以愉快地达到相同的目的。
		
		# 我们可以看到官网中的解释是这样的，Query节点不需要保存数据和处理文档的功能，我们只需要用它来进行路由查询即可。也就是他只负责我们请求的转发，真实的处理由其他节点执行，那么并且我们不能添加太多的查询节点，否则增加了集群负担，并且数据节点同样也能处理。
		
		# 那么Query的设置如下
			
		node.master: false
		node.data: false
		node.ingest: false 
		cluster.remote.connect: false
		
		# 我们可以看到所有的节点角色都为false了，简称3无产品
```

## 网络设置

### 通用网络设置

​		**network.host**

​			注意：通常我们设置的是**network.bind_host**

```properties
		# 该节点将绑定到该主机名或IP地址，并将该主机发布（发布）到群集中的其他节点。接受IP地址，主机名， 特殊值或它们的任意组合的数组。请注意，任何包含的值:（例如IPv6地址或包含特殊值之一）都必须加引号，因为它:是YAML中的特殊字符。0.0.0.0是可接受的IP地址，并将绑定到所有网络接口。该值0与值具有相同的作用0.0.0.0。

		# 默认为_local_。
		
		# 特殊处理的值有一下几种
		
						1、_[networkInterface]_			网络接口的地址，例如_en0_
						2、_local_										系统上的任何回送地址，例如127.0.0.1。
						3、_site_										系统上的任何站点本地地址，例如192.168.0.1。
						4、_global_									例如，系统上的任何全局作用域地址8.8.8.8
						
```

​		**network.bind_host**	

```properties
		# 这指定了节点应绑定到哪个网络接口以侦听传入的请求。一个节点可以绑定到多个接口，例如两个网卡，或者一个站点本地地址和一个本地地址。默认为 network.host，我们可以理解为能绑定多个，但是默认的话他就绑定了一个network.host，并且默认值是_local_本机
		
		network.bind_host: 0.0.0.0					# 允许所有访问
		network.bind_host: _local_					# 本机访问
		network.bind_host: _site_						# 系统上的所有站点
```

​		**network.publish_host**

```properties
		# 发布主机是节点向集群中其他节点发布的单个接口，以便这些节点可以连接到该主机。
		
		# 当前，Elasticsearch节点可能绑定到多个地址，但仅发布一个。如果未指定，则默认为的“最佳”地址network.host，按IPv4 / IPv6堆栈首选项，然后按可达性排序。如果设置 network.host导致多个绑定地址，但仍依赖特定地址进行节点到节点通信，则应显式设置 network.publish_host。
		
		# 以上两个设置都可以像配置一样network.host 接受IP地址，主机名和 特殊值。
		
		# 我们这里可以看到network.publish_host可以和network.bind_host一样设置主机名和特殊值，但是只能配置一个，也就是说他是单个的，并且这个host我们是需要发送到集群中进行通信的
		
		# 这里我们通常都会写定当前的集群能够访问的IP地址,尤其是Docker搭建的时候，否则集群通信会有问题
		
		network.publish_host: 192.168.1.177
```

​		**discovery.zen.ping.unicast.hosts**

```properties
		# 为了加入集群，节点需要知道集群中至少其他一些节点的主机名或IP地址。此设置提供了该节点将尝试联系的其他节点的初始列表。
		
		# 接受IP地址或主机名。如果主机名查找解析为多个IP地址，则将使用每个IP地址进行发现。 
		
		# 轮询DNS（每次查询从列表中返回不同的IP）可用于发现；不存在的IP地址将引发异常，并在下一轮ping时引起另一个DNS查找（取决于JVM DNS缓存）。
		
		# 默认为["127.0.0.1", "[::1]"]。
		
		# 通常我们这里就是配置的其他节点的地址，一般我们会把所有的节点都配置上去，也可以选择只配置一部分，因为他会根据轮询DNS进行节点发现。
		
		discovery.zen.ping.unicast.hosts: ["122.114.65.233:9301","122.114.65.233:9302","122.114.65.233:9303"]
```

​		**http.port**

```properties
		# 绑定到传入HTTP请求的端口。接受单个值或范围。如果指定了范围，则该节点将绑定到该范围中的第一个可用端口。

		# 默认为9200-9300。
		
	  #	这就是我们所说的http端口了
		
		 transport.port: 9200
		
		# 我们可以指定范围或者固定单个端口号，范围是指第一个被占用就是用第二个依此类推
```

​		**transport.port**

```properties
		# 用于绑定节点之间通信的端口。接受单个值或范围。如果指定了范围，则该节点将绑定到该范围中的第一个可用端口。

		# 默认为9300-9400。
		
		# 这就是我们的transport通信端口了
		
		transport.port: 9300		
		
		# 我们可以指定范围或者固定单个端口号，范围是指第一个被占用就是用第二个依此类推
```

### 进阶TCP配制

```properties
network.tcp.no_delay											启用或禁用“ TCP无延迟” 设置。默认为true。

network.tcp.keep_alive										启用或禁用TCP keep alive。默认为true。

network.tcp.reuse_address									地址是否应该重用。在非Windows计算机上默认为true。

network.tcp.send_buffer_size							TCP发送缓冲区的大小（以size为单位指定）。默认情况下未明确设置。

network.tcp.receive_buffer_size						TCP接收缓冲区的大小（以size为单位指定）。默认情况下未

			size单位
					
					k
					
					m
					
					g
					
					t

					p
```



## HTTP设置

​		此处网络配置基本都是引用上网络设置，尽量统一，如果需要灵活动态设置可以自定义设置。

```yaml
http.enabled											 # 设置为false可以完全禁用http模块，默认为true，Elasticsearch节点（和Java客户端）使用传输接口（而非HTTP ）在内部进行通信。http在不打算直接服务REST请求的节点上完全禁用该层可能是有意义的。例如，如果您还有 用于服务所有REST请求的客户端节点，则可以在纯数据节点上禁用HTTP 。但是请注意，您将无法直接向禁用了HTTP的节点发送任何REST请求（例如，检索节点统计信息）。

http.port													# 绑定端口范围。默认为9200-9300。

http.publish_port									# 与该节点通信时，HTTP客户端应使用的端口。当群集节点位于代理服务器或防火墙之后且http.port无法从外部直接寻址时，此选项很有用。

http.bind_host										# 绑定HTTP服务的主机地址。默认为http.host（如果设置）或network.bind_host

http.publish_host									# 要发布以供HTTP客户端连接的主机地址。默认为http.host（如果设置）或network.publish_host。
																							
http.host													# 用于将http.bind_host和http.publish_host默认设置为http.host或network.host。
																							
http.max_content_length						# HTTP请求的最大内容。默认为 100mb。如果设置为大于Integer.MAX_VALUE，它将重置为100mb。

http.max_initial_line_length			# HTTP URL的最大长度。默认为4kb

http.max_header_size							# 允许的标头的最大大小。默认为8kB

http.compression									# 尽可能支持压缩（使用Accept-Encoding）。默认为true。

http.compression_level						# 定义用于HTTP响应的压缩级别。有效值的范围是1（最小压缩）和9（最大压缩）。默认为3。

http.cors.enabled									# 启用或禁用跨域资源共享，即，另一源上的浏览器是否可以对Elasticsearch执行请求。设置为true启用以使Elasticsearch处理飞行前 CORS请求。Access-Control-Allow-Origin如果 列表Origin允许在请求中发送，Elasticsearch将使用标头响应那些请求http.cors.allow-origin。设置为false（默认值）以使Elasticsearch忽略Origin 请求标头，从而有效禁用CORS请求，因为Elasticsearch将永远不会使用Access-Control-Allow-Origin响应标头进行响应。请注意，如果客户端未发送带有Origin标头的飞行前请求，或者客户端未检查服务器的响应标头以验证 Access-Control-Allow-Origin响应标头，则跨域安全性将受到威胁。如果在Elasticsearch上未启用CORS，则客户端知道的唯一方法是发送飞行前请求并意识到缺少所需的响应标头。

http.cors.allow-origin						# 允许哪个起源。默认为不允许原点。如果/在值之前加上和，则将其视为正则表达式，从而允许您支持HTTP和HTTP。例如，/https?:\/\/localhost(:[0-9]+)?/在两种情况下，using 都会适当地返回请求标头。*是有效值，但由于您的Elasticsearch实例可以从任何地方跨越源请求而被视为安全风险。

http.cors.max-age									# 浏览器发送“预检”选项请求以确定CORS设置。max-age定义结果应缓存的时间。默认为1728000（20天）

http.cors.allow-methods						# 允许哪些方法。默认为 OPTIONS, HEAD, GET, POST, PUT, DELETE。

http.cors.allow-headers						# 允许哪些标题。默认为 X-Requested-With, Content-Type, Content-Length。

http.cors.allow-credentials				# 是否Access-Control-Allow-Credentials 应返回标头。注意：仅当设置设置为时，才返回此标头true。默认为false

http.detailed_errors.enabled			# 在响应输出中启用或禁用详细错误消息和堆栈跟踪的输出。注意：当设置为false且error_trace指定了request参数时，将返回错误；如果error_trace未指定，则将返回一条简单消息。默认为true

http.pipelining										# 启用或禁用HTTP流水线，默认为true。

http.pipelining.max_events				# 关闭HTTP连接之前要在内存中排队的最大事件数，默认为10000。

http.max_warning_header_count			# 客户端HTTP响应中警告标头的最大数量，默认为无界。

http.max_warning_header_size			# 客户端HTTP响应中警告标头的最大总大小，默认为无限制。
```

## Transport设置

​		官网介绍：[点击进入](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/modules-transport.html)

```yaml
transport.port										# 绑定端口范围。默认为9300-9400。

transport.publish_port						# 集群中其他节点与此节点通信时应使用的端口。当群集节点位于代理服务器或防火墙之后且transport.port无法从外部直接寻址时，此选项很有用。默认为通过分配的实际端口 transport.port。

transport.bind_host								# 绑定传输服务的主机地址。默认为transport.host（如果设置）或network.bind_host。

transport.publish_host						# 要发布以供集群中的节点连接的主机地址。默认为transport.host（如果设置）或network.publish_host。

transport.host										# 用于将transport.bind_host和transport.publish_host默认设置为transport.host或network.host。

transport.connect_timeout					# 用于启动新连接的连接超时（以时间设置格式）。默认为30s。

transport.compress								# 设置为true启用DEFLATE所有节点之间的压缩（）。默认为false。

transport.ping_schedule						# 安排常规的应用程序级ping消息，以确保节点之间的传输连接保持活动状态。5s在传输客户端中默认为，在-1其他位置默认为 （禁用）。最好正确配置TCP保持活动而不是使用此功能，因为TCP保持活动适用于所有种类的长期连接，而不仅仅是传输连接。
```

## 服务发现设置

​		Elasticsearch使用名为“ Zen Discovery”的自定义发现实现来进行节点到节点的集群和主选举。在投入生产之前，应配置两个重要的发现设置。

​		**discovery.zen.ping.unicast.hosts**

```yaml
		# 开箱即用，无需任何网络配置，Elasticsearch将绑定到可用的环回地址，并将扫描端口9300至9305以尝试连接到同一服务器上运行的其他节点。这提供了自动群集体验，而无需进行任何配置。
		# 当要在其他服务器上形成带有节点的集群时，您必须提供集群中其他可能处于活动状态且可联系的其他节点的节点列表。
```

配置如下

```properties
discovery.zen.ping.unicast.hosts: ["122.114.65.232:9300","122.114.65.233:9300"]
```

yaml格式化后

```yaml
discovery.zen.ping.unicast.hosts:
   - 122.114.65.232:9300
   - 122.114.65.233:9300
```

​		**discovery.zen.minimum_master_nodes**		

```properties
		# 为防止数据丢失，至关重要的是配置此 discovery.zen.minimum_master_nodes设置，以便每个符合主机要求的节点都知道为形成群集而必须可见的符合主机要求的最小数量。
		# 没有此设置，遭受网络故障的群集就有将群集拆分为两个独立的群集（裂脑）的风险，这将导致数据丢失。
		
		# 简单的来说选举节点的最少存活数，当和master发生网络问题，但是和其他节点可以通信时进行选举的最小节点数量，例如我们设置为1，当断开连接的时候我们只要存在1个节点我们就能进行选举。
		
		# 并且Elasticsearch官方给我们提供了一个合理的设置算法
		
				 （master_eligible_nodes / 2）+ 1
		
		# 换句话说，如果有三个符合主条件的节点，则最小主节点应设置为(3 / 2) + 1或2：
		
		 			默认值：1
		
					设置: discovery.zen.minimum_master_nodes: 2
		
		# 脑裂说明：假设您有一个由两个符合主机资格的节点组成的集群。网络故障会中断这两个节点之间的通信。每个节点都会看到一个符合主控条件的节点...... 随着minimum_master_nodes设置为默认1，这是足以形成一个集群。每个节点都将自己选举为新的主节点（认为其他符合主节点资格的节点已经死亡），结果是两个集群或一个裂脑。在重新启动一个节点之前，这两个节点将永远不会重新加入。已写入重新启动的节点的所有数据都将丢失。
		# 现在，假设您有一个具有三个主节点的集群，并将其 minimum_master_nodes设置为2。如果网络拆分将一个节点与其他两个节点分开，则具有一个节点的一侧将看不到足够的符合主机资格的节点，并且将意识到自己无法选举为主机。具有两个节点的一侧将选举一个新的主机（如果需要）并继续正常运行。解决网络拆分后，单个节点将重新加入群集并再次开始服务请求。
```

​		并且我们可以通过Rest方式直接修改

```yaml
PUT _cluster/settings 
{ “ transient” ：{ “ discovery.zen.minimum_master_nodes” ：2 } } 
```

## 内存设置

​		我们ELasticsearch是非常吃内存的，因为ELasticsearch数据都会放到内存中进行缓存，并且随着数据量越来越大内存占用会越来越高，以及Java的一个垃圾回收机制，所以我们对于内存这块需要设置，尤其是生产环境的内存设置由为重要，官网对内存设置的描述如下：

​		Elasticsearch将通过（最小堆大小）和（最大堆大小）设置分配[jvm.options中](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/jvm-options.html)指定的整个堆 。`Xms``Xmx`

​		那么我们可以通过jvm.options进行配置也可以通过环境变量ES_JAVA_OPTS进行配置

​		那么我们这里采用Docker搭建所以直接使用环境变量，通过容器指定环境变量的方式启动

```
docker run -d \
--name elasticsearch \
-e ES_JAVA_OPTS="-Xms1g -Xmx1g" \
--restart=always \
```

​		下面ELasticsearch官方对于内存设置也给予了我们一些设置提示，如下：

​			这些设置的值取决于服务器上可用的RAM数量。好的经验法则是：

​					1、将最小堆大小（`Xms`）和最大堆大小（`Xmx`）设置为彼此相等。

```
		也就是说，我们的-Xms 和-Xmx大小最好一致，都设置成一样的内存大小
```

​					2、Elasticsearch可用的堆越多，可用于缓存的内存就越多。但是请注意，过多的堆可能会使您长时间停滞垃圾回收。

```
		我们知道JVM会进行垃圾回收，但随着我们缓存的内存越来越多，太大的内存会导致长时间停留在垃圾回收
```

​					3、设置`Xmx`为不超过物理RAM的50％，以确保有足够的物理RAM用于内核文件系统缓存。

```
		那么Xmx不要超过系统物理内存的50，也就是说我们如果是一个32g内存的服务器，那么尽量不要把它设置为16以上的对大小，流出足够的内存给系统使用
```

​					4、不要将其设置`Xmx`为高于JVM用于压缩对象指针（压缩oop）的临界值；确切的截止时间有所不同，但接近32 GB。您可以通过在日志中查找如下一行来验证您是否处于限制范围内。

```
		我们知道JVM在对于64位系统的时候，会对对象指针进行压缩，那么这个压缩是有一个条件限制的，也就是32GB左右，如果超过了这个内存那么JVM的指针压缩将不会生效，简单的来说就是设置32GB以上堆内存，那么我们的对象指针长度就会增长一倍，占用更多的系统资源
```

​					5、更好的是，尝试保持在基于零的压缩oop的阈值以下；确切的截止时间有所不同，但是在大多数系统上26 GB是安全的，但是在某些系统上可以达到30 GB。您可以通过使用JVM选项启动Elasticsearch `-XX:+UnlockDiagnosticVMOptions -XX:+PrintCompressedOopsMode`并查找类似于以下内容的行来验证您是否处于限制范围内。

```
		也就是说我们可以通过设置来查看他是否启用了指针压缩
		-XX:+UnlockDiagnosticVMOptions					解锁诊断参数
		-XX:+PrintCompressedOopsMode						打印压缩指针的工作模式
```

​		如果出现如下信息，正常的话出现第一种情况表示使用了指针压缩，第二种表示没有使用，所以我们可以自己查看是否启用指针压缩，并且根据结果做出优化

```
		heap address: 0x000000011be00000, size: 27648 MB, zero based Compressed Oops
				表示我们从0开始进行指针压缩
		
		heap address: 0x0000000118400000, size: 28672 MB, Compressed Oops with base: 0x00000001183ff000
				表示我们没有从0开始指针压缩
```

## 堆内存溢出日志设置

​		其实这里就是为了防止我们的ELasticsearch内存溢出，我们知道他是使用Java开发的，那么Java在内存过大时会抛出异常，OOM内存溢出，并且生成相应的dump文件，这里我们要做的就是设置dump文件的生成路径，我们使用Docker搭建指定环境变量，如下

```
docker run -d \
--name elasticsearch \
-e ES_JAVA_OPTS="-Xms1g -Xmx1g -XX:HeapDumpPath=/usr/share/elasticsearch/logs/" \
--restart=always \
```

## 零时目录

```
		默认情况下，Elasticsearch使用启动脚本在系统临时目录下立即创建的私有临时目录。

		在某些Linux发行版中，/tmp如果最近未访问过文件和目录，则系统实用程序将从中清除文件和目录。如果长时间不使用需要使用临时目录的功能，这可能会导致在运行Elasticsearch时删除私有临时目录。如果随后使用需要临时目录的功能，则将导致问题。
		
		如果使用.deb或.rpm软件包安装Elasticsearch 并在其下运行，systemd则定期清理将排除Elasticsearch使用的私有临时目录。
		
		但是，如果打算.tar.gz长时间在Linux 上运行发行版，则应考虑为Elasticsearch创建专用的临时目录，该目录不在将清除旧文件和目录的路径下。该目录应设置权限，以便只有运行Elasticsearch的用户才能访问它。然后$ES_TMPDIR在启动Elasticsearch之前将环境变量设置 为指向它。
		
		简单的来说就是我们的零时生成的文件的文件夹路径可以通过环境变量进行指定
		
		$ES_TMPDIR
```

## JVM致命错误日志

```
		默认情况下，Elasticsearch配置JVM写致命错误日志的默认日志目录（这是/var/log/elasticsearch对RPM和Debian的软件包分发，并logs 在Elasticsearch安装的根目录下 的tar和zip压缩文件分布）。这些是JVM在遇到致命错误（例如，分段错误）时生成的日志。如果该路径不适合于接收的日志，则应修改条目-XX:ErrorFile=...中 jvm.options到备用路径。
```

## 其他设置

​		禁止多Index执行时，路径上也带有Index，导致Body中的Index覆盖了Url上的Index，我们使用[multi-search](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-multi-search.html), [multi-get](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-multi-get.html), and [bulk](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/docs-bulk.html) 等等API都会有这个问题的出现

​		如：

​			curl localhost:9200/test/_mget?pretty -d '{"docs":[{"_index":"test1","_id":1},{"_index":"test2","_id":2}]}'

​			请求体中的test1和test2覆盖了test，实际上没有使用到，那么我们设置如下设置，重启Es后出现如下请求则会报错

```properties
rest.action.multi.allow_explicit_index: false
```



# Elasticsearch系统配置（Docker启动无需设置）	

```sh
#	设置打开文件最大数
sudo su  
ulimit -n 65535 
su elasticsearch 
```

```sh
# 关闭swap
sudo swapoff -a

# EsRest请求路径判断是否设置成功
GET _nodes/stats/process?filter_path=**.max_file_descriptors
```

```sh
# 启用内存锁，防止内存溢出
bootstrap.memory_lock: true

# 请求路径判断是否设置成功
GET _nodes?filter_path=**.mlockall
```

​		设置Elasticsearch [`mmapfs`](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/index-modules-store.html#mmapfs)默认使用目录来存储其索引。默认的操作系统对mmap计数的限制可能太低，这可能会导致内存不足异常。

```
sysctl -w vm.max_map_count=262144
```

​		设置线程数

```
ulimit -u 4096

Elasticsearch对不同类型的操作使用许多线程池。能够在需要时创建新线程很重要。确保Elasticsearch用户可以创建的线程数至少为4096。包分发作为服务运行时，systemd将自动为Elasticsearch进程配置线程数。无需其他配置。
```

​		

启动失败看这里

或者是由于权限的原因

我们使用docker logs -f 容器查看日志

如果发现

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/error1.png)

解决方法

```sh
vim /etc/sysctl.conf 

在最后一行添加
vm.max_map_count=655360
然后退出执行
sysctl -p


```

https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1596190258135.png