# Docker安装单节点RocketMQ

​		创建挂载目录

```
mkdir -p ~/deploy/rocketmq-single/ && cd ~/deploy/rocketmq-single/
```

​		搭建nameserver

```sh
cat > docker-compose-rocketmq-nameserver.yaml << EOF
version: '3.4'
services:
  rocketmq-nameserver:
    container_name: rocketmq-nameserver   # 指定容器的名称
    image: foxiswho/rocketmq:4.8.0        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: rocketmq-nameserver
    ports:
      - "9876:9876"	# 端口映射
    volumes: # 挂载目录
      - ./nameserver-logs:/home/rocketmq/logs  # 挂载数据目录
    command:
      mqnamesrv
EOF
```

​		启动nameserver

```sh
docker-compose -f docker-compose-rocketmq-nameserver.yaml up -d
```

​		搭建broker,注意修改宿主机IP

```sh
cat > docker-compose-rocketmq-brokerserver.yaml << EOF
version: '3.4'
services:
  rocketmq-brokerserver:
    container_name: rocketmq-brokerserver   # 指定容器的名称
    image: foxiswho/rocketmq:4.8.0        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: rocketmq-brokerserver
    ports:
      - 10911:10911
      - 10912:10912
      - 10909:10909
    environment:
      NAMESRV_ADDR: "192.168.1.28:9876"
      JAVA_OPTS: "-Duser.home=/opt"
      JAVA_OPT_EXT: " -Xms512M -Xmx512M -Xmn128m"
    privileged: true
    volumes: # 挂载目录
      - ./broker-logs:/home/rocketmq/logs
      - ./broker-store:/home/rocketmq/store
      - ./broker-conf:/home/rocketmq/conf
    command:
      mqbroker -c /home/rocketmq/conf/broker.conf
EOF
```

​		创建挂载数据目录broker-store以及broker.conf

```sh
# 创建存储目录
mkdir -p ./broker-store && chmod 777 ./broker-store

# 创建配置文件挂载目录
mkdir -p ./broker-conf
cat > ./broker-conf/broker.conf << EOF
# broker集群名称
brokerClusterName=DefaultCluster
# brokerName节点名称多broker唯一
brokerName=broker-01
# brokerId 主节点为0
brokerId=0
# 宿主机IP，公网情况下填写公网IP
brokerIP1=192.168.1.28
# 默认Topic队列数量
defaultTopicQueueNums=4
# 自动创建Topic是否开启
autoCreateTopicEnable=true
# 自动创建订阅组
autoCreateSubscriptionGroup=true
# 监听端口
listenPort=10911
deleteWhen=04
# 文件保留时间
fileReservedTime=120
mapedFileSizeCommitLog=1073741824
mapedFileSizeConsumeQueue=300000
diskMaxUsedSpaceRatio=88
maxMessageSize=65536
brokerRole=ASYNC_MASTER
flushDiskType=ASYNC_FLUSH
EOF
```

​		启动

```sh
docker-compose -f docker-compose-rocketmq-brokerserver.yaml up -d
```

​		测试发送

```bash
# 进入容器
docker exec -it rocketmq-brokerserver bash
# 环境变量设置
export NAMESRV_ADDR=192.168.1.28:9876

# 测试发送
./tools.sh org.apache.rocketmq.example.quickstart.Producer

# 测试消费
./tools.sh org.apache.rocketmq.example.quickstart.Consumer
```



# 安装RocketMQ监控工具

​		创建挂载目录

```
mkdir -p ~/deploy/rocketmq-console/ && cd ~/deploy/rocketmq-console/
```

​		创建Compose配置文件,注意修改rmqserver地址

```sh
cat > docker-compose-rocketmq-console.yaml << EOF
version: '3.4'
services:
  rocketmq-console:
    container_name: rocketmq-console    # 指定容器的名称
    image: styletang/rocketmq-console-ng        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: rocketmq-console
    ports:
      - 8180:8080
    environment:
      JAVA_OPTS: "-Drocketmq.namesrv.addr=192.168.1.28:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false"
EOF
```

​		启动

```
docker-compose -f docker-compose-rocketmq-console.yaml up -d
```



