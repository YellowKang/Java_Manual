# 单机版

## 	下载镜像

​	首先先下载Elasticsearch6.0的版本

```
docker pull docker.io/elasticsearch:6.7.0
```

## 运行容器

然后我们运行容器

```
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 --name elasticsearch docker.io/elasticsearch:6.7.0
```

​	这里并没有挂载文件，并且设置了Jvm启动参数（由于使用云服务器，内存不够）

​	然后我们docker ps 查看容器

```
docker ps
```

![](img\dockerps.png)



## 生产级启动

创建挂载文件夹

```
mkdir -p /docker/elasticsearch/data
chmod -R 777 /docker/elasticsearch/data
```

然后直接启动容器

```
docker run -d \
-e ES_JAVA_OPTS="-Xms256m -Xmx256m" \
-p 10092:9200 \
-p 10093:9300 \
-v /docker/elasticsearch/data:/usr/share/elasticsearch/data \
--name elasticsearch6.7 docker.io/elasticsearch:6.7.0
```

```
docker run -d \
-e ES_JAVA_OPTS="-Xms1g -Xmx1g" \
-p 9200:9200 \
-p 9300:9300 \
-v /docker/elasticsearch/data:/usr/share/elasticsearch/data \
--name elasticsearch6.7 docker.io/elasticsearch:6.7.0
```



# 集群版

## 镜像下载

首先我们也先下载一个Es6.0

```
docker pull docker.io/elasticsearch:6.7.0
```

## 配置文件编写

然后我们先来编写配置文件

我们先创建3个配置文件文件

```
首先创建文件夹
mkdir /root/elasticsearch-cluster-yml

然后进入创建三个文件分别为es-cluster1.yml  es-cluster2.yml  es-cluster3.yml

touch /root/elasticsearch-cluster-yml/es-cluster{1,2,3}.yml

```

然后在下面分别将下面三个配置文件中的内容写上去

es-cluster1.yml

```
echo "cluster.name: es-cluster
node.name: es-master1
network.bind_host: 0.0.0.0
network.publish_host: 118.187.4.89
http.port: 9201
transport.tcp.port: 9301
http.cors.enabled: true
http.cors.allow-origin: \"*\"
node.master: true
node.data: true
discovery.zen.ping.unicast.hosts: [\"118.187.4.89:9302\",\"118.187.4.89:9303\"]
discovery.zen.minimum_master_nodes: 1" > /root/elasticsearch-cluster-yml/es-cluster1.yml
```

es-cluster2.yml

```
echo "cluster.name: es-cluster
node.name: es-data1
network.bind_host: 0.0.0.0
network.publish_host: 118.187.4.89
http.port: 9202
transport.tcp.port: 9302
http.cors.enabled: true
http.cors.allow-origin: \"*\"
node.master: true
node.data: true
discovery.zen.ping.unicast.hosts: [\"118.187.4.89:9301\",\"118.187.4.89:9303\"]
discovery.zen.minimum_master_nodes: 1" > /root/elasticsearch-cluster-yml/es-cluster2.yml
```

es-cluster3.yml

```
echo "cluster.name: es-cluster
node.name: es-data2
network.bind_host: 0.0.0.0
network.publish_host: 118.187.4.89
http.port: 9203
transport.tcp.port: 9303
http.cors.enabled: true
http.cors.allow-origin: \"*\"
node.master: true
node.data: true
discovery.zen.ping.unicast.hosts: [\"118.187.4.89:9301\",\"118.187.4.89:9302\"]
discovery.zen.minimum_master_nodes: 1" > /root/elasticsearch-cluster-yml/es-cluster3.yml
```

这三个文件夹分别修改，node.name集群节点的名字，不能相同

还有就是ip地址，network.publish_host写每一台主机的当前节点主机ip

以及discovery.zen.ping.unicast.hosts这个集群的ip因为要和集群节点通信所以我们在一台机器虚拟化3个端口，这里可以自行修改

我们这里部署了一个master，两个data节点

## 挂载数据文件夹

然后我们创建文件夹，用来存储它的数据



```
首先创建父文件夹
mkdir /root/elasticsearch-cluster-data

然后创建子文件夹
mkdir /root/elasticsearch-cluster-data/data{1,2,3}

然后授予权限因为在使用时需要有权限
chmod -R 777 /root/elasticsearch-cluster-data
```



## 启动容器

这样我们就准备好了然后我们进行容器的启动

启动第一个容器es-node1

```
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d \
-p 9201:9201 \
-p 9301:9301 \
-v /root/elasticsearch-cluster-yml/es-cluster1.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /root/elasticsearch-cluster-data/data1:/usr/share/elasticsearch/data --name es-master1 \
docker.io/elasticsearch:6.7.0
```

启动第二个容器es-node2

```
docker run -d \
-e ES_JAVA_OPTS="-Xms256m -Xmx256m" \
-p 9202:9202 \
-p 9302:9302 \
-v /root/elasticsearch-cluster-yml/es-cluster2.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /root/elasticsearch-cluster-data/data2:/usr/share/elasticsearch/data --name es-data1 \
docker.io/elasticsearch:6.7.0
```

启动第三个容器es-node3

```
docker run -d \
-e ES_JAVA_OPTS="-Xms256m -Xmx256m" \
-p 9203:9203 \
-p 9303:9303 \
-v /root/elasticsearch-cluster-yml/es-cluster3.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /root/elasticsearch-cluster-data/data3:/usr/share/elasticsearch/data --name es-data2 \
docker.io/elasticsearch:6.7.0
```

可以将jvm优化那个删除（由于内存不够设置），可以修改挂载的文件地址（不建议，因为修改地方多），可以修改数据挂载文件或者不指定（不建议） ，然后指定镜像运行容器





# Kibana安装



## 下载镜像

首先先下载Kibana镜像（一定要对应的es版本）

```
docker pull docker.io/kibana:6.7.0
```

然后我们新建一个配置文件用来存储配置文件

```
mkdir -p /docker/kibana/conf
vim /docker/kibana/conf/kibana.yml
```

然后添加以下内容，下面的hosts地址修改为es地址

```
server.name: kibana
server.host: "0"
elasticsearch.hosts: [ "http://111.67.196.127:9200" ]
xpack.monitoring.ui.container.elasticsearch.enabled: true
```

然后我们就能启动容器了

```
docker run -d \
--name kibana6.7 \
-p 5601:5601 \
-v /docker/kibana/conf/kibana.yml:/usr/share/kibana/config/kibana.yml \
-e ELASTICSEARCH_URL=http://111.67.196.127:9200 docker.io/kibana:6.7.0 

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

```
docker pull  docker.io/mobz/elasticsearch-head:5
```

​	然后启动容器

```
docker run -d --name head-es -p 9100:9100 docker.io/mobz/elasticsearch-head:5
```



## 解决Head插件无法连接Elasticsearch的问题

首先我们先来把Elasticsearch的配置文件修改一点点

```
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

# IK分词器安装

直接进入容器内部进行编辑

```
进入容器内部编辑
docker exec -it  elasticsearch bash

安装IK分词器插件
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.7.0/elasticsearch-analysis-ik-6.7.0.zip


```

等待下载完成然后cd，然后查看是否有ik分词器

```
cd plugins/
ls
```

如果有ik分词器则安装完成

# 启动失败看这里

或者是由于权限的原因

我们使用docker logs -f 容器查看日志

如果发现

![](img\error1.png)

解决方法

```
vim /etc/sysctl.conf 
在最后一行添加
vm.max_map_count=655360
然后退出执行
sysctl -p
```

