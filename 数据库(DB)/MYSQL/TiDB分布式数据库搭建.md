# Docker搭建TIDB

我们需要搭建3个类型的节点

## PD Server

​		Placement Driver (简称 PD) 是整个集群的管理模块，其主要工作有三个： 一是存储集群的元信息（某个 Key 存储在哪个 TiKV 节点）；二是对 TiKV 集群进行调度和负载均衡（如数据的迁移、Raft group leader的迁移等）；三是分配全局唯一且递增的事务 ID，PD 是一个集群，需要部署奇数个节点，一般线上推荐至少部署 3 个节点。

​		我们可以将其理解为，Es的Master节点，Mongo的ConfigServer,本质上和业务数据没有关联，主要负责集群的管理和信息存储。



## TiKV Server

​		TiKV Server 负责存储数据，从外部看 TiKV 是一个分布式的提供事务的 Key-Value 存储引擎。存储数据的基本单位是 Region，每个 Region 负责存储一个 Key Range （从 StartKey 到EndKey 的左闭右开区间）的数据，每个 TiKV 节点会负责多个 Region 。TiKV 使用 Raft协议做复制，保持数据的一致性和容灾。副本以 Region 为单位进行管理，不同节点上的多个 Region 构成一个 RaftGroup，互为副本。数据在多个 TiKV 之间的负载均衡由 PD 调度，这里也是以 Region 为单位进行调度。

​		我们可以将其理解为Mongo的每一个分片组，Es的一个Data节点，主要负责存储业务数据。。

## TiDB Server

​		TiDB Server 负责接收 SQL 请求，处理 SQL 相关的逻辑，并通过 PD 找到存储计算所需数据的 TiKV 地址，与 TiKV 交互获取数据，最终返回结果。 TiDB Server是无状态的，其本身并不存储数据，只负责计算，可以无限水平扩展，可以通过负载均衡组件（如LVS、HAProxy 或F5）对外提供统一的接入地址。

​		我们可以将其理解为一个客户端，类似于Mongo的Mongos，还有Kafka的Broker，他并不储存信息，也不管理集群，只是我们需要通过它进行对数据的处理。

## 非高可用（快速搭建，不推荐）

#### 搭建PD Server

请将带Ip的参数修改为自己Ip，内网即内网Ip公网搭建即公网Ip

创建挂载目录

```
mkdir -p /docker/tidb-cluster/pd1/data
```

然后启动容器：将114Ip修改为自己的IP

```
docker run -d --name pd1 \
  --net=host \
  -v /docker/tidb-cluster/pd1/data:/tidb \
  pingcap/pd:latest \
  --name="pd1" \
  --data-dir="/tidb/pd1" \
  --client-urls="http://0.0.0.0:2379" \
  --advertise-client-urls="http://114.67.80.169:2379" \
  --peer-urls="http://0.0.0.0:2380" \
  --advertise-peer-urls="http://114.67.80.169:2380" \
  --initial-cluster="pd1=http://114.67.80.169:2380"
```

#### 搭建TiKV Server

请将带Ip的参数修改为自己Ip，内网即内网Ip公网搭建即公网Ip

##### 搭建TiKV1

创建挂载目录

```
mkdir -p /docker/tidb-cluster/tikv1/{data,conf}
```

运行容器

```
docker run -d --name tikv1 \
  --net=host \
  --ulimit nofile=1000000:1000000 \
  -v /docker/tidb-cluster/tikv1/data:/tidb \
  pingcap/tikv:latest \
  --addr="0.0.0.0:20160" \
  --advertise-addr="114.67.80.169:20160" \
  --data-dir="/tidb/tikv1" \
  --pd="114.67.80.169:2379"
```

##### 搭建TiKV2

创建挂载目录

```
mkdir -p /docker/tidb-cluster/tikv2/{data,conf}
```

运行容器

```
docker run -d --name tikv2 \
  --net=host \
  --ulimit nofile=1000000:1000000 \
  -v /docker/tidb-cluster/tikv2/data:/tidb \
  pingcap/tikv:latest \
  --addr="0.0.0.0:20161" \
  --advertise-addr="182.61.2.16:20161" \
  --data-dir="/tidb/tikv2" \
  --pd="114.67.80.169:2379"
```

##### 搭建TiKV3

创建挂载目录

```
mkdir -p /docker/tidb-cluster/tikv3/{data,conf}
```

运行容器

```
docker run -d --name tikv3 \
  --net=host \
  --ulimit nofile=1000000:1000000 \
  -v /docker/tidb-cluster/tikv3/data:/tidb \
  pingcap/tikv:latest \
  --addr="0.0.0.0:20162" \
  --advertise-addr="182.61.2.16:20162" \
  --data-dir="/tidb/tikv3" \
  --pd="114.67.80.169:2379"
```

#### 搭建TiDB

请将带Ip的参数修改为自己Ip，内网即内网Ip公网搭建即公网Ip

直接运行

```
docker run -d --name tidb \
  -p 4000:4000 \
  -p 10080:10080 \
  pingcap/tidb:latest \
  --store=tikv \
  --path="114.67.80.169:2379"
```

然后我们本地下载mysql然后连接

注：默认只能通过127.0.0.1访问然后创建用户，需要在TiDB中创建用户

下载MySQl(如果有则直接连接)

```
yum install mysql
```

然后访问

```
mysql -h 127.0.0.1 -P 4000 -u root -D test
```

进入后创建用户

```
CREATE USER 'bigkang'@'%' IDENTIFIED BY 'bigkang'; 
```

授予权限

```
GRANT All ON * . * TO 'bigkang'@'%';
```

