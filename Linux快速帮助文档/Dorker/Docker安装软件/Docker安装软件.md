# Docker是什么？

# Docker常用命令



# Docker安装软件

## 安装RabbitMQ

首先先下载mq

docker pull docker.io/rabbitmq:3.7-management

然后我们去给他更新下名字

docker tag d69a5113ceae rabbitmq

由于我们更新名字之后他并不会删除掉原来的镜像所以我们需要去删除掉他

这里我们需要去加上他的版本
docker rmi docker.io/rabbitmq:3.7-management

然后我们来运行我们改过之后的镜像

docker   

```
-d    		后台运行
-p    		映射端口（由于Rabbitmq有一个客户端和服务的所以要映射两个端口）一会还需要把这两个映射到本地的端口的防火墙设置一下
--name 		设置一个名称，这个可以和我们的镜像一样
```

docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq



这里我们先开启5672端口
firewall-cmd --permanent --add-port=5672/tcp
然后我们开启15672端口
firewall-cmd --permanent --add-port=15672/tcp
最后我们刷新防火墙
firewall-cmd --reload

然后我们就能直接访问我们的rabbitmq了

## 安装MySQL

首先先搜索镜像



​			docker search redis



自己选择一个版本
然后我们把镜像下载下拉



​			docker pull docker.io/redis



然后我们给他修改名字



​			docker tag 看他的id  redis



然后我们删除原来的



​			docker rmi docker.io/redis



然后运行Docker容器

```
-d 			后台
--name		指定容器名字
-p			映射端口
然后启动容器镜像名字
然后我们指定Redis的密码
--requirepass "bigkang"

这样就运行完成了
```



docker run -d --name redis -p 6379:6379 redis --requirepass "bigkang"



记得别忘了开放自己的端口

​			firewall-cmd --permanent --add-port=6379/tcp

然后刷新防火墙
			firewall-cmd --reload 

## 安装Elasticsearch

### 出现错误解决方法

docker pull registry.docker-cn.com/library/elasticsearch

//修改容器内存大小，否则内存不够无法启动

docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 --name elasticsearch  elasticsearch  

然后我们的9300是不能访问的，因为他不让我们访问我们需要修改配置文件，但是文件在内存中运行，所以我们先把配置文件复制出来改掉然后再运行

docker exec -it  elasticsearch /bin/bash

进入容器内部

cd config 
进入配置文件复制他的路径
/usr/share/elasticsearch/config

然后找到他的yml配置文件

/usr/share/elasticsearch/config/elasticsearch.yml

exit退出

我们有这个路径了我们把他的文件复制出来 

docker cp elasticsearch:/usr/share/elasticsearch/config/elasticsearch.yml  /usr/share/elasticsearch.yml

复制到本地的 /usr/share/elasticsearch.yml

然后我们编辑他

vim /usr/share/elasticsearch.yml

将他第二行的注释去掉
然后保存退出

ok

然后我们将以前的启动的容器停止掉

docker run --name=elasticsearch -p 9200:9200 -p 9300:9300 -v /usr/share/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml myelasticsearch



docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -di --name=elasticsearch -p 9200:9200 -p 9300:9300 -v /usr/share/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml myelasticsearch



-v /usr/share/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -di --name=elasticsearch -p 9200:9200 -p 9300:9300  elasticsearch

elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.6.12/elasticsearch-analysis-ik-5.6.12.zip



curl -H 'Content-Type: application/json'  -XGET 'localhost:9200/_analyze?pretty' -d '{"text":"白俊遥技术博客"}'

docker cp ik elasticsearch:/usr/share/elasticsearch/plugins/



Head插件安装

docker pull mobz/elasticsearch-head:5

KiBana
docker pull docker.elastic.co/kibana/kibana:5.6.12

docker run --rm -p 5601:5601 --link elasticsearch-db:elasticsearch -e ELASTICSEARCH_URL=http://localhost:9200 --name kibana kibana



## 安装Kibana



## 安装FastDFS

首先下载镜像

```
docker pull morunchang/fastdfs
```

然后修改名字,修改为fastdfs

```
docker tag morunchang/fastdfs fastdfs
然后删除原来的
docker rmi morunchang/fastdfs
```

再来启动我们的storage，--name制定名称，-net不用修改，ip修改为自己的ip

```
docker run -d --name storage --net=host -e TRACKER_IP=39.108.158.33:22122  -v /var/fdfs/storage:/var/fdfs -e GROUP_NAME=storagegroup fastdfs sh storage.sh
```

然后启动tracker

```
docker run -d --name tracker --net=host -v /var/fdfs/tracker:/var/fdfs fastdfs sh tracker.sh
```

然后开放端口

```
firewall-cmd --zone=public --add-port=8080/tcp --permanent

firewall-cmd --zone=public --add-port=22122/tcp --permanent

firewall-cmd --zone=public --add-port=23000/tcp --permanent

firewall-cmd --reload
```

然后修改storage的nginx配置文件,先进入容器内部

```
docker exec -it storage  /bin/bash
```

然后再编辑配置文件

```
vim /data/nginx/conf/nginx.conf
	在他的location中加上配置
-----------------------------------------------------------------------------------------
        location /group1/M00 {
            proxy_next_upstream http_502 http_504 error timeout invalid_header;
            proxy_cache http-cache;
            proxy_cache_valid  20030412h;
            proxy_cache_key $uri$is_args$args;
            proxy_pass http://fdfs_group1;
            expires 30d;
        }
```

然后重新启动

```
先退出容器
exit
然后重新启动storage

docker restart storage
然后就能直接查看ip:8080了，如果出现nginx就成功了
注：阿里云上需要开放安全组8080端口
```

## 安装Conslu

首先先下载镜像

```
docker pull consul
```

然后运行镜像

运行节点一

```

```

## 安装TIDB数据库

### 单机版

首先下载TIDB的镜像

```
搜索镜像
docker search tidb

从搜索中的镜像选择一个然后
拉取镜像
docker pull docker.io/pingcap/tidb

然后重新标记版本名
docker tag docker.io/pingcap/tidb tidb

然后创建一个文件夹用来指定挂载的文件路径
mkdir -p /data/tidb/data

然后启动容器
docker run --name tidb -d -v /data/tidb/data:/tmp/tidb -p 4000:4000 -p 10080:10080 tidb
```



