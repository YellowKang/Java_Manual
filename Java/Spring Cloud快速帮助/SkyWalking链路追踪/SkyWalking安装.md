# 先安装ES

首先我们先创建es的配置文件以及es的挂载数据盘

```
创建配置文件夹
mkdir -p /docker/skyWalking/es/conf

创建数据文件夹
mkdir -p /docker/skyWalking/es/data

赋予权限
chmod 777 /docker/skyWalking/es/conf
chmod 777 /docker/skyWalking/es/data
编辑配置文件
vim /docker/skyWalking/es/conf/elasticsearch.yml
```

配置文件内容如下

```
cluster.name: CollectorDBCluster   
network.host: 0.0.0.0
# 增加
thread_pool.bulk.queue_size: 1000
```

然后我们来启动es,这里我设置jvm小了，由于测试环境不需要那么大，切记生产关闭

```
docker run --name es-skyWalking \
-e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d \
--privileged=true \
-v /docker/skyWalking/es/conf/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /docker/skyWalking/es/data:/usr/share/elasticsearch/data \
-p 9200:9200 \
-p 9300:9300 \
docker.io/elasticsearch:6.7.0
```

# 下载

首先创建文件夹

```
mkdir /root/skywalking
cd /root/skywalking
```

我们使用linux一键下载

```
wget http://mirrors.tuna.tsinghua.edu.cn/apache/skywalking/6.1.0/apache-skywalking-apm-6.1.0.tar.gz
```

然后解压文件，并且重命名

```
tar -zxvf apache-skywalking-apm-6.1.0.tar.gz 
mv apache-skywalking-apm-bin/ skywalking/
```

