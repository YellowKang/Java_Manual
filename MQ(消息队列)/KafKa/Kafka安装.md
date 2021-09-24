# Docker&单机版Zookeeper&单机版Kafka

​		！！！生产环境推荐使用如下集群Kafka，并且挂载出配置文件以及数据盘等

​		首先我们先下载zookeeper和kafka镜像

```sh
# 下载Zookeeper镜像
docker pull docker.io/wurstmeister/zookeeper

# 下载Kafka镜像
docker pull docker.io/wurstmeister/kafka
```

​		然后按顺序启动Zookeeper和kafka

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

​		首先确保有一个Zookeeper，然后我们将Kafka放进去解压

​		然后修改server.proerties中的zookeeper.connect=192.168.213.11:2181 （这里写Zookeeper的地址）

```sh
vim service.properties
```

​		然后我们进入到bin目录下然后启动kafka

```sh
nohup ./kafka-server-start.sh ../config/server.properties > my.log &
```

​		这样就启动了Kafka，那么我们来创建一个Topic

```sh
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

​		创建一个replication为一个，partition分区为一个，叫做test的topic

​		然后查询topic

```sh
./kafka-topics.sh -list -zookeeper localhost:2181
```

​		然后我们就来使用生产者和消费者了

​		首先是生产者，我们创建一个kafka生产者，topic为test，然后不要关闭，打开另一个窗口

```sh
./kafka-console-producer.sh --broker-list localhost:9092 --topic test
```

​		然后是消费者（消费当前的kafka中的test这个topic），这个窗口也不要关闭，我们回到生产者随便输入然后回车，再看消费者的窗口，那么这样就能消费数据了

```sh
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
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
      # 监控端口，可选，kafka-eagle需要
    	# JMX_PORT: 9999
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
      - 9999:9999
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
    	# 监控端口，可选，kafka-eagle需要
    	# JMX_PORT: 9999
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
      - 9999:9999
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
      # 监控端口，可选，kafka-eagle需要
    	# JMX_PORT: 9999
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
      - 9999:9999
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
      # 监控端口，可选，kafka-eagle需要
    	# JMX_PORT: 9999
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
      - 9999:9999
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
      # 监控端口，可选，kafka-eagle需要
    	# JMX_PORT: 9999
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
      - 9999:9999
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
      # 监控端口，可选，kafka-eagle需要
    	# JMX_PORT: 9999
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
      - 9999:9999
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

# 搭建Kafka-Manager监控工具(简单方便)

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



# 搭建Kafka-Eagle监控工具（功能强大）

​		创建目录

```sh
mkdir -p ~/kafka-eagle && cd ~/kafka-eagle
```

​		再创建一个启动文件

```sh
vim entrypoint.sh
```

​		写入如下

```sh
#!/usr/bin/env bash
/opt/kafka-eagle/bin/ke.sh start
tail -f /opt/kafka-eagle/kms/logs/catalina.out
```

​		权限

```sh
chmod 777  entrypoint.sh
```

​		下载包

```sh
wget https://github.com/smartloli/kafka-eagle-bin/archive/v2.0.3.tar.gz
```

​		然后创建DockerFile文件

```
vim Dockerfile
```

​		写入如下

```dockerfile
FROM java:8-alpine
ENV KE_HOME=/opt/kafka-eagle
ENV EAGLE_VERSION=2.0.3

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
ADD entrypoint.sh /usr/bin
COPY v2.0.3.tar.gz /opt/v2.0.3.tar.gz
RUN apk --update add wget gettext tar bash sqlite
#get and unpack kafka eagle
RUN mkdir -p /opt/kafka-eagle/conf;cd /opt && \
    tar zxvf v${EAGLE_VERSION}.tar.gz -C kafka-eagle --strip-components 1 && \
    cd kafka-eagle;tar zxvf kafka-eagle-web-${EAGLE_VERSION}-bin.tar.gz --strip-components 1 && \
    chmod +x /opt/kafka-eagle/bin/ke.sh && \
    mkdir -p /hadoop/kafka-eagle/db
EXPOSE 8080
ENTRYPOINT ["/usr/bin/entrypoint.sh"]
WORKDIR /opt/kafka-eagle
```

​		然后构建镜像

```sh
docker build -t kafka-eagle:2.0.3 .
```

​		配置数据库以及Zk地址，数据库需要root或者创建表的权限，会自动创建表

​		镜像需要自己构建，创建配置文件

```sh
# 创建配置文件挂载目录
mkdir -p /docker/kafka-eagle/conf
vim /docker/kafka-eagle/conf/system-config.properties
```

​		编辑文件写入如下，修改集群zk，kafka存储，web端口以及数据库地址

```properties
# 配置集群，可以配置多个，使用逗号隔开
kafka.eagle.zk.cluster.alias=cluster1
# zk地址  集群名.zk.list=${KAFKA_ZOOKEEPER_HOSTS}
cluster1.zk.list=192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181


cluster1.kafka.eagle.broker.size=20
######################################
# 配置Kafka存储Offset
######################################
# kafka存储  集群名.kafka.eagle.offset.storage=kafka
cluster1.kafka.eagle.offset.storage=kafka

######################################
# Zk连接线程
######################################
kafka.zk.limit.size=25

######################################
# Web端口
######################################
kafka.eagle.webui.port=8080


######################################
# enable kafka 开启图表
# 及开始sql查询
######################################
kafka.eagle.metrics.charts=true
kafka.eagle.sql.fix.error=true

######################################
# 提醒的email
######################################
kafka.eagle.mail.enable=true
kafka.eagle.mail.sa=alert_sa
kafka.eagle.mail.username=alert_sa@163.com
kafka.eagle.mail.password=mqslimczkdqabbbh
kafka.eagle.mail.server.host=smtp.163.com
kafka.eagle.mail.server.port=25


######################################
# 删除kafka topic 的token
######################################
kafka.eagle.topic.token=admin

######################################
# kafka sasl authenticate
######################################
kafka.eagle.sasl.enable=false
kafka.eagle.sasl.protocol=SASL_PLAINTEXT
kafka.eagle.sasl.mechanism=PLAIN
kafka.eagle.sasl.client=/hadoop/kafka-eagle/conf/kafka_client_jaas.conf

# Default use sqlite to store data
#kafka.eagle.driver=org.sqlite.JDBC
# It is important to note that the '/hadoop/kafka-eagle/db' path must exist.
#kafka.eagle.url=jdbc:sqlite:/hadoop/kafka-eagle/db/ke.db
#kafka.eagle.username=root
#kafka.eagle.password=smartloli

# 配置数据库地址
kafka.eagle.driver=com.mysql.jdbc.Driver
kafka.eagle.url=jdbc:mysql://192.168.1.11:3306/eagle?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull
kafka.eagle.username=root
kafka.eagle.password=123
```

​		需要Kafka开启JMX

​		需要修改ZK  以及数据库的地址用户名密码

​		Or使用我上传打包好的镜像

```sh
docker pull bigkang/kafka-eagle:2.0.3
```

​		编写docker-compose启动脚本

```properties
# 创建目录
mkdir -p ~/kafka-eagle && cd ~/kafka-eagle
cat > docker-compose.yaml << EOF
version: '3'
services:
  kafka-eagle:
    container_name: kafka-eagle
    image: kafka-eagle:2.0.3
    ports:
      - "8048:8080"
    volumes:
      - /docker/kafka-eagle/conf/system-config.properties:/opt/kafka-eagle/conf/system-config.properties
EOF
```

​		启动后访问

```http
http://192.168.1.11:8048/ke
用户名：admin
密码：123456
```

​		

# Kafka配置详解



```properties
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# see kafka.server.KafkaConfig for additional details and defaults

############################# 服务器基础 #############################

# Broker的ID。对于每个代理，必须将其设置为唯一的整数，集群中的节点ID，不能重复。
broker.id=0

############################# Socket套接字服务器设置 #############################

# socket服务器监听的地址。它将获取返回的值
# java.net.InetAddress.getCanonicalHostName()如果没有配置。
#   格式:
#     listeners = listener_name://host_name:port
#   示例:
#     listeners = PLAINTEXT://your.host.name:9092
listeners=PLAINTEXT://0.0.0.0:9092
# 代理将向生产者和消费者通告，如果没有设置,如果配置了，它会使用“listener”的值。否则，它将使用该值
# 返回java.net.InetAddress.getCanonicalHostName()。（默认注释）
#advertised.listeners=PLAINTEXT://your.host.name:9092
# 将监听器名称映射到安全协议，默认情况下它们是相同的。更多细节请参阅配置文档（默认注释）
#listener.security.protocol.map=PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL
# 服务器用于接收来自网络的请求并向网络发送响应的线程数
num.network.threads=3
# 服务器用于处理请求的线程数，可能包括磁盘I/O
num.io.threads=8
# socket服务器使用的发送缓冲区
socket.send.buffer.bytes=102400
# socket服务器使用的接收缓冲区
socket.receive.buffer.bytes=102400
# socket服务器接收请求的最大大小(防止OOM)
socket.request.max.bytes=104857600

############################# 日志基础设置 #############################

# 一个用逗号分隔的目录列表，用于存储日志文件（存放数据目录）
log.dirs=/kafka/kafka-logs-fc1aa97971a2
# 每个主题的默认日志分区数。更多的分区允许更大的
# 并行处理，但这也会导致更多的文件
# Broker
num.partitions=1
# 每个数据目录用于启动时日志恢复和关闭时刷新的线程数。
# 当数据目录位于RAID阵列时，建议增加该值。
num.recovery.threads.per.data.dir=1

############################# 内部Topic设置  #############################

# 组元数据内部主题“__consumer_offsets”和“__transaction_state”的复制因子
# 对于开发测试以外的任何内容，建议使用大于1的值，以确保可用性，例如3。
offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1

############################# 日志刷新策略 #############################

#消息会立即写入文件系统，但是默认情况下我们只使用fsync()进行同步
#操作系统缓存懒惰。以下配置控制将数据刷新到磁盘。
#这里有几个重要的权衡:
# 1。
#		持久性:如果不使用复制，未刷新的数据可能会丢失。
# 2。
#		延迟:当刷新发生时，非常大的刷新间隔可能会导致延迟峰值，因为有很多数据需要刷新。
# 3。
#		吞吐量:刷新通常是成本最高的操作，较小的刷新间隔可能会导致过多的查找。
#下面的设置允许配置刷新策略以在一段时间或之后刷新数据
#每N条消息(或同时)。这可以全局执行，并在每个主题的基础上覆盖。
# 在强制将数据刷新到磁盘之前要接受的消息数量（默认不开启）
#log.flush.interval.messages=10000
# 强制刷新之前，一条消息可以在日志中保存的最大时间（默认不开启）
#log.flush.interval.ms=1000

############################# 日志保留策略 #############################

# 以下配置控制日志段的处理。政策可以
# 设置为在一个时间段后或在给定大小已累积后删除段 .设置在一段时间后删除段。
# 一个段将被删除，只要满足*任一*条件。删除总是发生
# 从日志的末尾开始。
# 删除日志文件的最小年龄（小时）
log.retention.hours=168
# 基于大小的日志保留策略。从日志中删除段，除非剩余的
# 段落在log.retention.bytes下面。独立于log.retention.hours的功能。（默认不开启）
#log.retention.bytes=1073741824
# 日志段文件的最大大小。当达到这个大小时，将创建一个新的日志段（单位：字节）。
log.segment.bytes=1073741824
# 检查日志段以确定它们是否可以被删除的时间间隔
# 保留策略
log.retention.check.interval.ms=300000

############################# Zookeeper配置 #############################

# Zookeeper连接字符串(详见Zookeeper文档)。这是一个逗号分隔的主机:端口对，每个对应一个zk
# 服务器。如。“127.0.0.1:3000 127.0.0.1:3001 127.0.0.1:3002”。您还可以在url中附加一个可选的chroot字符串来指定
# kafka znode的根目录。
zookeeper.connect=192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
# 连接zookeeper超时时间单位（ms）
zookeeper.connection.timeout.ms=18000

############################# Group协调器设置 #############################

# 下面的配置指定了GroupCoordinator延迟初始消费者再平衡的时间(以毫秒为单位)。当新成员加入组时，再平衡将被group.initial.rebalance.delay.ms的值进一步延迟，直到max.poll.interval.ms的最大值。
# 默认值是3秒。
# 我们在这里重写为0，因为它为开发和测试提供了更好的开箱即用体验。
# 但是，在生产环境中，默认值3秒更合适，因为这将有助于避免在应用程序启动时进行不必要的、潜在的昂贵的重新平衡。
group.initial.rebalance.delay.ms=0
# 发布的端口号以及Host，会存入Zookeeper提供集群通信
advertised.port=9092
advertised.host.name=139.9.70.155
# 端口号
port=9092
```

