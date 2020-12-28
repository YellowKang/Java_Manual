# Docker&单机版Zookeeper&单机版Kafka

​	首先我们先下载zookeeper和kafka镜像

```sh
下载Zookeeper镜像
docker pull docker.io/wurstmeister/zookeeper

下载Kafka镜像
docker pull docker.io/wurstmeister/kafka
```

​	然后按顺序启动Zookeeper和kafka

```sh
启动Zookeeper容器
docker run -d \
--name zookeeper \
-p 2181:2181 docker.io/wurstmeister/zookeeper

启动kafka容器
docker run -d \
--name kafka \
-p 9092:9092 \
-e KAFKA_ZOOKEEPER_CONNECT=39.108.158.33:2181 \
-e KAFKA_ADVERTISED_HOST_NAME=39.108.158.33 \
--env KAFKA_ADVERTISED_PORT=9092 \
-v /etc/localtime:/etc/localtime wurstmeister/kafka:latest
```



# Linux安装单机Kafka

首先确保有一个Zookeeper，然后我们将Kafka放进去解压

然后修改server.proerties中的zookeeper.connect=192.168.213.11:2181 （这里写Zookeeper的地址）

```sh
vim service.properties
```

然后我们进入到bin目录下然后启动kafka

```sh
nohup ./kafka-server-start.sh ../config/server.properties > my.log &
```

这样就启动了Kafka，那么我们来创建一个Topic

```sh
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

创建一个replication为一个，partition分区为一个，叫做test的topic

然后查询topic

```sh
./kafka-topics.sh -list -zookeeper localhost:2181
```

然后我们就来使用生产者和消费者了

首先是生产者，我们创建一个kafka生产者，topic为test，然后不要关闭，打开另一个窗口

```sh
./kafka-console-producer.sh --broker-list localhost:9092 --topic test
```

然后是消费者（消费当前的kafka中的test这个topic），这个窗口也不要关闭，我们回到生产者随便输入然后回车，再看消费者的窗口，那么这样就能消费数据了

```sh
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

### 搭建zk1节点

创建挂载目录

```sh
mkdir -p /docker/zookeeper-cluster/zk1/{data,log,conf,datalog}
```

创建配置文件

```sh
echo "clientPort=2181
dataDir=/data
dataLogDir=/datalog
tickTime=2000
initLimit=5
syncLimit=2
maxClientCnxns=60
server.1=127.0.0.1:2888:3888
server.2=182.61.2.16:2888:3888
server.3=106.12.113.62:2888:3888
quorumListenOnAllIPs=true" > /docker/zookeeper-cluster/zk1/conf/zoo.cfg
```

创建docker-compose文件

```sh
cd /docker/zookeeper-cluster/zk1
vim /docker/zookeeper-cluster/zk1/docker-compose.yaml
```

写入

```properties
version: '3'
services:
  zk1:
    container_name: zk1
    image: zookeeper:3.4.11
    restart: always
    network_mode: host
    volumes:
      - /docker/zookeeper-cluster/zk1/data:/data
      - /docker/zookeeper-cluster/zk1/log:/logs
      - /docker/zookeeper-cluster/zk1/datalog:/datalog
      - /docker/zookeeper-cluster/zk1/conf/zoo.cfg:/conf/zoo.cfg
    environment:
      ZOO_MY_ID: 1
```

启动

```
docker-compose up -d
```

#### 注意事项

将每一台的自己的节点的ip设置为内网ip或者127.0.0.1，然后开启所有ip访问，下面所有节点公网版本都设置为内网网卡或者127.0.0.1

### 搭建zk2节点

创建挂载目录

```sh
mkdir -p /docker/zookeeper-cluster/zk2/{data,log,conf,datalog}
```

创建配置文件

```sh
echo "clientPort=2181
dataDir=/data
dataLogDir=/datalog
tickTime=2000
initLimit=5
syncLimit=2
maxClientCnxns=60
server.1=114.67.80.169:2888:3888
server.2=127.0.0.1:2888:3888
server.3=106.12.113.62:2888:3888
quorumListenOnAllIPs=true" > /docker/zookeeper-cluster/zk2/conf/zoo.cfg
```

创建docker-compose文件

```sh
cd /docker/zookeeper-cluster/zk2
vim /docker/zookeeper-cluster/zk2/docker-compose.yaml
```

写入

```properties
version: '3'
services:
  zk2:
    container_name: zk2
    image: zookeeper:3.4.11
    restart: always
    network_mode: host
    volumes:
      - /docker/zookeeper-cluster/zk2/data:/data
      - /docker/zookeeper-cluster/zk2/log:/logs
      - /docker/zookeeper-cluster/zk2/datalog:/datalog
      - /docker/zookeeper-cluster/zk2/conf/zoo.cfg:/conf/zoo.cfg
    environment:
      ZOO_MY_ID: 2
```

启动

```
docker-compose up -d
```



### 搭建zk3节点

创建挂载目录

```sh
mkdir -p /docker/zookeeper-cluster/zk3/{data,log,conf,datalog}
```

创建配置文件

```sh
echo "clientPort=2181
dataDir=/data
dataLogDir=/datalog
tickTime=2000
initLimit=5
syncLimit=2
maxClientCnxns=60
server.1=114.67.80.169:2888:3888
server.2=182.61.2.16:2888:3888
server.3=127.0.0.1:2888:3888
quorumListenOnAllIPs=true" > /docker/zookeeper-cluster/zk3/conf/zoo.cfg
```

创建docker-compose文件

```sh
cd /docker/zookeeper-cluster/zk3
vim /docker/zookeeper-cluster/zk3/docker-compose.yaml
```

写入

```properties
version: '3'
services:
  zk3:
    container_name: zk3
    image: zookeeper:3.4.11
    restart: always
    network_mode: host
    volumes:
      - /docker/zookeeper-cluster/zk3/data:/data
      - /docker/zookeeper-cluster/zk3/log:/logs
      - /docker/zookeeper-cluster/zk3/datalog:/datalog
      - /docker/zookeeper-cluster/zk3/conf/zoo.cfg:/conf/zoo.cfg
    environment:
      ZOO_MY_ID: 3
```

启动

```
docker-compose up -d
```



### 搭建Kafka1

​		IP为公网IP此处采用

​		创建挂载目录

```sh
mkdir -p /docker/kafka-cluster/kafka1/{data,config,logs}
```

​		创建docker-compose文件,  ！！ 注意每台Kafka的BrokerID都不能一样

```properties
# 创建文件存放Docker-compose文件
mkdir /root/kafka1
cd /root/kafka1
# 写入Docker-Compose文件
cat > /root/kafka1/docker-compose.yaml << EOF
version: '3'
services:
  kafka1:
    container_name: kafka1
    image: wurstmeister/kafka
    restart: always
    privileged: true
    environment:
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ADVERTISED_HOST_NAME: 139.9.70.155
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_ZOOKEEPER_CONNECT: 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
      KAFKA_BROKER_ID: 0
      KAFKA_HEAP_OPTS: "-Xmx500m -Xms500m"
    volumes:
      - /docker/kafka-cluster/kafka1/data:/kafka
      # - /docker/kafka-cluster/kafka1/config:/opt/kafka/config
      - /docker/kafka-cluster/kafka1/logs:/opt/kafka/logs
    ports:
      - 9092:9092
EOF
```

​		运行

```sh
docker-compose up -d
```

​		运行成功后将配置文件Copy出来

```sh
docker cp kafka1:/opt/kafka/config  /docker/kafka-cluster/kafka1
```

​		然后修改配置文件，放开config重新启动即可

```properties
cd /root/kafka1
# 写入Docker-Compose文件
cat > /root/kafka1/docker-compose.yaml << EOF
version: '3'
services:
  kafka1:
    container_name: kafka1
    image: wurstmeister/kafka
    restart: always
    privileged: true
    environment:
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ADVERTISED_HOST_NAME: 139.9.70.155
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_ZOOKEEPER_CONNECT: 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
      KAFKA_BROKER_ID: 0
      KAFKA_HEAP_OPTS: "-Xmx500m -Xms500m"
    volumes:
      - /docker/kafka-cluster/kafka1/data:/kafka
      - /docker/kafka-cluster/kafka1/config:/opt/kafka/config
      - /docker/kafka-cluster/kafka1/logs:/opt/kafka/logs
    ports:
      - 9092:9092
EOF
```

​		然后重启

```sh
docker-compose stop && docker-compose rm -f && docker-compose up -d
```

### 搭建Kafka2

​		创建挂载目录

```sh
mkdir -p /docker/kafka-cluster/kafka2/{data,config,logs}
```

​		创建docker-compose文件

```properties
# 创建文件存放Docker-compose文件
mkdir /root/kafka2
cd /root/kafka2
# 写入Docker-Compose文件
cat > /root/kafka2/docker-compose.yaml << EOF
version: '3'
services:
  kafka2:
    container_name: kafka2
    image: wurstmeister/kafka
    restart: always
    privileged: true
    environment:
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ADVERTISED_HOST_NAME: 139.9.80.252
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_ZOOKEEPER_CONNECT: 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
      KAFKA_BROKER_ID: 1
      KAFKA_HEAP_OPTS: "-Xmx500m -Xms500m"
    volumes:
      - /docker/kafka-cluster/kafka2/data:/kafka
      # - /docker/kafka-cluster/kafka2/config:/opt/kafka/config
      - /docker/kafka-cluster/kafka2/logs:/opt/kafka/logs
    ports:
      - 9092:9092
EOF
```

​		运行	

```sh
docker-compose up -d
```

​		运行成功后将配置文件Copy出来

```sh
docker cp kafka2:/opt/kafka/config  /docker/kafka-cluster/kafka2
```

​		然后修改配置文件，放开config重新启动即可

```properties
cd /root/kafka2
# 写入Docker-Compose文件
cat > /root/kafka2/docker-compose.yaml << EOF
version: '3'
services:
  kafka2:
    container_name: kafka2
    image: wurstmeister/kafka
    restart: always
    privileged: true
    environment:
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ADVERTISED_HOST_NAME: 139.9.80.252
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_ZOOKEEPER_CONNECT: 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
      KAFKA_BROKER_ID: 1
      KAFKA_HEAP_OPTS: "-Xmx500m -Xms500m"
    volumes:
      - /docker/kafka-cluster/kafka2/data:/kafka
      - /docker/kafka-cluster/kafka2/config:/opt/kafka/config
      - /docker/kafka-cluster/kafka2/logs:/opt/kafka/logs
    ports:
      - 9092:9092
EOF
```

​		然后重启

```sh
docker-compose stop && docker-compose rm -f && docker-compose up -d
```

​		

### 搭建Kafka3

创建挂载目录

```sh
mkdir -p /docker/kafka-cluster/kafka3/{data,config,logs}
```

创建docker-compose文件

```properties
mkdir /root/kafka3
cd /root/kafka3
cat > /root/kafka3/docker-compose.yaml << EOF
version: '3'
services:
  kafka3:
    container_name: kafka3
    image: wurstmeister/kafka
    restart: always
    privileged: true
    environment:
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ADVERTISED_HOST_NAME: 124.71.9.101
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_ZOOKEEPER_CONNECT: 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
      KAFKA_BROKER_ID: 2
      KAFKA_HEAP_OPTS: "-Xmx500m -Xms500m"
    volumes:
      - /docker/kafka-cluster/kafka3/data:/kafka
      #- /docker/kafka-cluster/kafka3/config:/opt/kafka/config
      - /docker/kafka-cluster/kafka3/logs:/opt/kafka/logs
    ports:
      - 9092:9092
EOF
```

​		运行

```sh
docker-compose up -d
```

​		运行成功后将配置文件Copy出来

```sh
docker cp kafka3:/opt/kafka/config  /docker/kafka-cluster/kafka3
```

​		然后修改配置文件，放开config重新启动即可

```properties
mkdir /root/kafka3
cd /root/kafka3
cat > /root/kafka3/docker-compose.yaml << EOF
version: '3'
services:
  kafka3:
    container_name: kafka3
    image: wurstmeister/kafka
    restart: always
    privileged: true
    environment:
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ADVERTISED_HOST_NAME: 124.71.9.101
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_ZOOKEEPER_CONNECT: 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
      KAFKA_BROKER_ID: 2
      KAFKA_HEAP_OPTS: "-Xmx500m -Xms500m"
    volumes:
      - /docker/kafka-cluster/kafka3/data:/kafka
      - /docker/kafka-cluster/kafka3/config:/opt/kafka/config
      - /docker/kafka-cluster/kafka3/logs:/opt/kafka/logs
    ports:
      - 9092:9092
EOF
```

​		然后重启

```sh
docker-compose stop && docker-compose rm -f && docker-compose up -d
```

​		

### 测试Kafka集群

​		我们使用命令创建一个topic

```sh
# 进入容器
docker exec -it kafka3 bash
# 创建topic
./kafka-topics.sh --create --zookeeper 114.67.80.169:2181,182.61.2.16:2181,106.12.113.62:2181 --replication-factor 1 --partitions 1 --topic test2
```

```
./kafka-topics.sh --create --zookeeper 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181 --replication-factor 1 --partitions 1 --topic test2
```

# 搭建Kafka-Manager监控工具

​		创建挂载盘

```sh
mkdir -p /root/kafka-manager && mkdir -p /docker/kafka-manager/conf
cd /docker/kafka-manager/conf
```

​		修改配置文件用户名和密码

```sh
# 编辑配置文件
vim /docker/kafka-manager/conf/application.conf
# 添加如下
play.crypto.secret="^<csmm5Fx4d=r2HEX8pelM3iBkFVv?k[mc;IZE<_Qoq8EkX_/7@Zt6dP05Pzea3U"
play.crypto.secret=${?APPLICATION_SECRET}

play.i18n.langs=["en"]
play.http.requestHandler = "play.http.DefaultHttpRequestHandler"
play.http.context = "/"
play.application.loader=loader.KafkaManagerLoader

kafka-manager.zkhosts="kafka-manager-zookeeper:2181"
kafka-manager.zkhosts=${?ZK_HOSTS}
pinned-dispatcher.type="PinnedDispatcher"
pinned-dispatcher.executor="thread-pool-executor"
application.features=["KMClusterManagerFeature","KMTopicManagerFeature","KMPreferredReplicaElectionFeature","KMReassignPartitionsFeature"]
akka {
  loggers = ["akka.event.slf4j.Slf4jLogger"]
  loglevel = "INFO"
}
basicAuthentication.enabled=true
basicAuthentication.username="admin"
basicAuthentication.password="admin123"
basicAuthentication.realm="Kafka-Manager"
kafka-manager.consumer.properties.file=${?CONSUMER_PROPERTIES_FILE}
```

​		启动文件

```properties
# 进入Compose目录
cd /root/kafka-manager
# APPLICATION_SECRET 为KafkaManager密码

echo "version: '3'
services:
  kafka3:
    container_name: kafka-manager
    image: sheepkiller/kafka-manager:1.3.1.8
    restart: always
    privileged: true
    ports:
      - 19000:9000
    environment:
      ZK_HOSTS: 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
      APPLICATION_SECRET: bigkang" > docker-compose.yaml
    volumes:
      - /docker/kafka-manager/conf/application.conf:/kafka-manager-1.3.1.8/conf/application.conf
```

​		然后启动

```sh
docker-compose up -d
```

​		然后访问：http://localhost:19000

​		然后启动访问添加集群

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1609078719891.png)







```

```

