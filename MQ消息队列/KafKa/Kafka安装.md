# Docker&单机版Zookeeper&单机版Kafka

​	首先我们先下载zookeeper和kafka镜像

```
下载Zookeeper镜像
docker pull docker.io/wurstmeister/zookeeper

下载Kafka镜像
docker pull docker.io/wurstmeister/kafka
```

​	然后按顺序启动Zookeeper和kafka

```
启动Zookeeper容器
docker run --name zookeeper -d -p 2181:2181 docker.io/wurstmeister/zookeeper

启动kafka容器
docker run -d --name kafka --publish 9092:9092 --env KAFKA_ZOOKEEPER_CONNECT=39.108.158.33:2181 --env KAFKA_ADVERTISED_HOST_NAME=39.108.158.33 --env KAFKA_ADVERTISED_PORT=9092 --volume /etc/localtime:/etc/localtime wurstmeister/kafka:latest




docker run -d --name kafka --publish 9092:9092 --env KAFKA_ZOOKEEPER_CONNECT=39.108.158.33:2181 --env KAFKA_ADVERTISED_HOST_NAME=111.67.194.204 --env KAFKA_ADVERTISED_PORT=9092 --volume /etc/localtime:/etc/localtime wurstmeister/kafka:latest
```



```

```



# Linux安装单机Kafka

首先确保有一个Zookeeper，然后我们将Kafka放进去解压

然后修改server.proerties中的zookeeper.connect=192.168.213.11:2181 （这里写Zookeeper的地址）

然后我们进入到bin目录下然后启动kafka

```
nohup ./kafka-server-start.sh ../config/server.properties > my.log &
```

这样就启动了Kafka，那么我们来创建一个Topic

```
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

创建一个replication为一个，partition分区为一个，叫做test的topic

然后查询topic

```
./kafka-topics.sh -list -zookeeper localhost:2181
```

然后我们就来使用生产者和消费者了

首先是生产者，我们创建一个kafka生产者，topic为test，然后不要关闭，打开另一个窗口

```
./kafka-console-producer.sh --broker-list localhost:9092 --topic test
```



然后是消费者（消费当前的kafka中的test这个topic），这个窗口也不要关闭，我们回到生产者随便输入然后回车，再看消费者的窗口，那么这样就能消费数据了

```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
```



ps -ef | grep nacos | grep -v grep|awk '{print $2}'|xargs kill -9



清理缓存

```
查看缓存
free -m
清理缓存
echo 1 > /proc/sys/vm/drop_caches
echo 2 > /proc/sys/vm/drop_caches
echo 3 > /proc/sys/vm/drop_caches
```

# Kafka集群以及Zookeeper集群



```


docker run  -d --name kafka -p 9092:9092 -e KAFKA_BROKER_ID= -e KAFKA_ZOOKEEPER_CONNECT=140.143.0.227:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://111.67.194.204:9092 -e KAFKA_LISTENERS=PLAINTEXT://111.67.194.204:9092 -t wurstmeister/kafka 

```

