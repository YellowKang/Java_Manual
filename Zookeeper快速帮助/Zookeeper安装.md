# Docker&单机版Zookeeper

​	首先我们先下载zookeeper

```sh
下载Zookeeper镜像
docker pull docker.io/wurstmeister/zookeeper
```

​	然后按顺序启动Zookeeper和kafka

```sh
启动Zookeeper容器
docker run -d \
--name zookeeper \
-p 2181:2181 docker.io/wurstmeister/zookeeper
```



# Zookeeper集群

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

### 测试集群

​		查看Server状态（1主2从）

```sh
zkServer.sh status
```

​		然后进入容器中连接集群

```sh
zkCli.sh  -server 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181
```

​		或者随便进入一台创建一个节点

```
create /test bigkang
```

​		然后进入其他节点查看是否存在

```
get /test
```

​		如果存在表示写入同步成功，然后我们找到Master停止掉他看是否会进行选举,然后会发现Master变成follower了

```
zkServer.sh status
```

# Docker安装监控工具

​		拉取镜像

```
docker pull qnib/zkui
```

​		启动镜像

```sh
docker run -itd \
--name zkui \
--restart=always \
-p 9090:9090 \
-e ZKLIST="192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181" \
maauso/zkui
```

​		访问WebUi，http://localhost:9090

```
默认用户名： admin
默认  密码：manager
```

