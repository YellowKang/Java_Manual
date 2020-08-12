# 启动失败看这里

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
-v /docker/elasticsearch/data:/usr/share/elasticsearch/data \
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



## 下载镜像

首先先下载Kibana镜像（一定要对应的es版本）

```sh
docker pull docker.io/kibana:6.7.0
```

然后我们新建一个配置文件用来存储配置文件

然后添加以下内容，下面的hosts地址修改为es地址

```sh
mkdir -p /docker/kibana/conf

echo "server.name: kibana
server.host: \"0\"
elasticsearch.hosts: [ \"http://172.16.16.4:19201\" ]
xpack.monitoring.ui.container.elasticsearch.enabled: true" > /docker/kibana/conf/kibana.yml
```



然后我们就能启动容器了

```sh
docker run -d \
--name kibana6.7 \
-p 15601:5601 \
-v /docker/kibana/conf/kibana.yml:/usr/share/kibana/config/kibana.yml \
-e ELASTICSEARCH_URL=http://192.168.1.16:10092 docker.io/kibana:6.7.0 

docker run -d \
--name kibana6.7 \
-p 5601:5601 \
-v /docker/kibana/conf/kibana.yml:/usr/share/kibana/config/kibana.yml \
-e ELASTICSEARCH_URL=http://118.187.4.89:10092/ docker.io/kibana:6.7.0 
```

然后等待容器启动一会直接访问5601端口

注意：（如果连接公网请开放端口或者关闭防火墙否则一直无法连接）

# 安装Head插件

​	首先下载镜像（由于没有6的版本head插件所以使用5）

```sh
docker pull  docker.io/mobz/elasticsearch-head:5
```

​	然后启动容器

```sh
docker run -d --name head-es -p 9100:9100 docker.io/mobz/elasticsearch-head:5
```



## 解决Head插件无法连接Elasticsearch的问题

首先我们先来把Elasticsearch的配置文件修改一点点

```sh
先从Es中将这个文件从这个容器中拷贝出来
docker cp elasticsearch6.7:/usr/share/elasticsearch/config/elasticsearch.yml /root/elasticsearch.yml

然后编辑拷贝出来的容器
vim /root/elasticsearch.yml 
	
将下面两行代码加入Es当中，开启Elasticsearch的跨域
http.cors.enabled: true
http.cors.allow-origin: "*"

然后把这个文件cp到容器中
docker cp /root/elasticsearch.yml elasticsearch6.7:/usr/share/elasticsearch/config/elasticsearch.yml

然后重启容器
docker restart elasticsearch6.7
```

# 插件安装

### IK分词器安装

#### 在线联网安装

直接进入容器内部进行编辑

```sh
进入容器内部编辑
docker exec -it  elasticsearch bash

安装IK分词器插件
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.0/elasticsearch-analysis-ik-6.7.0.zip

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
进入容器内部编辑
docker exec -it  elasticsearch bash

安装IK分词器插件
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.7.0/elasticsearch-analysis-pinyin-6.7.0.zip
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
        }
  }
}
```

然后我们创建一个_mapping模板他的类型是test，用于设置字段指定使用哪个分词器，手动创建mapping

```json
PUT /test_pinyin/test/_mapping
{
    "settings": {
        "index": {
            "number_of_shards": "3",
            "number_of_replicas": "1"
        }
    },
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

以下就是我们的拼音+分词了

