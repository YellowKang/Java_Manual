# Redis5.0版本+Redis-Cli

### 带布隆过滤器插件版本（特殊需求）

#### 自定义DockerFile

首先我们

#### 快速使用现成镜像

```

docker run -d \
--name redis \
-p 16379:6379 \
redislabs/rebloom redis-server --requirepass 'bigkang' --loadmodule /usr/lib/redis/modules/redisbloom.so --appendonly yes 
```



### 单机版

#### 快速启动

```sh
docker run -d --name redis -p 6379:6379 redis

带密码加挂载数据
docker run -d \
--name redis \
-p 6379:6379 \
-v /docker/redis/data:/data \
redis:5.0.5 --requirepass 'bigkang'

docker run -d \
--name redis \
-p 16379:6379 \
redis:5.0.5 --requirepass 'bigkang'
```

#### 生产启动

首先创建挂载文件

```sh
mkdir -p /docker/redis/{data,conf}
```

首先编辑配置文件，注意我此处设置了密码，请修改为自己的密码

```sh
vim /docker/redis/conf/redis.conf

退出后授予权限，因为配置文件执行需要权限
chmod 777 /docker/redis/conf/redis.conf
-----------------------------------------------------

# Redis默认不是以守护进程的方式运行，可以通过该配置项修改，使用yes启用守护进程
daemonize no

# 当Redis以守护进程方式运行时，Redis默认会把pid写入/var/run/redis.pid文件，可以通过pidfile指定
#pidfile /var/run/redis.pid

# 指定Redis监听端口，默认端口为6379，作者在自己的一篇博文中解释了为什么选用6379作为默认端口，
#因为6379在手机按键上MERZ对应的号码，而MERZ取自意大利歌女Alessia Merz的名字
port 6379

# 绑定的主机地址
#bind 0.0.0.0

# 当 客户端闲置多长时间后关闭连接，如果指定为0，表示关闭该功能
timeout 300

# 指定日志记录级别，Redis总共支持四个级别：debug、verbose、notice、warning，
#默认为verbose
loglevel verbose

# 日志记录方式，默认为标准输出，如果配置Redis为守护进程方式运行，而这里又配置为日志
#记录方式为标准输出，则日志将会发送给/dev/null
logfile stdout

# 设置数据库的数量，默认数据库为0，可以使用SELECT <dbid>命令在连接上指定数据库id
databases 16

# 指定在多长时间内，有多少次更新操作，就将数据同步到数据文件，可以多个条件配合
#save <seconds> <changes>
#Redis默认配置文件中提供了三个条件：
save 900 1
save 300 10
save 60 10000
# 分别表示900秒（15分钟）内有1个更改，300秒（5分钟）内有10个更改以及60秒内有10000个更改。

# 指定存储至本地数据库时是否压缩数据，默认为yes，Redis采用LZF压缩，如果为了节省CPU时间，
#可以关闭该选项，但会导致数据库文件变的巨大
rdbcompression yes

# 指定本地数据库文件名，默认值为dump.rdb
dbfilename dump.rdb

# 指定本地数据库存放目录
dir /data

# 设置当本机为slav服务时，设置master服务的IP地址及端口，在Redis启动时，它会自动从master进行数据同步
#slaveof <masterip> <masterport>

# 当master服务设置了密码保护时，slav服务连接master的密码
#masterauth bigkang

# 设置Redis连接密码，如果配置了连接密码，客户端在连接Redis时需要通过AUTH <password>命令提供密码，默认关闭
#requirepass bigkang

# 设置同一时间最大客户端连接数，默认无限制，Redis可以同时打开的客户端连接数为Redis进程可以打开的最大文件描述符数，
#如果设置 maxclients 0，表示不作限制。当客户端连接数到达限制时，Redis会关闭新的连接并向客户端
#返回max number of clients reached错误信息
maxclients 128

# 指定Redis最大内存限制，Redis在启动时会把数据加载到内存中，达到最大内存后，Redis会先尝试清除已到期或即将到期的Key，
#当此方法处理 后，仍然到达最大内存设置，将无法再进行写入操作，但仍然可以进行读取操作。Redis新的vm机制，会把Key存放内存，
#Value会存放在swap区
#maxmemory <bytes>

# 指定是否在每次更新操作后进行日志记录，Redis在默认情况下是异步的把数据写入磁盘，如果不开启，可能会在断电时导致一段时间内的数据丢失。
#因为 redis本身同步数据文件是按上面save条件来同步的，所以有的数据会在一段时间内只存在于内存中。默认为no
appendonly no

# 指定更新日志文件名，默认为appendonly.aof
appendfilename appendonly.aof

# 指定更新日志条件，共有3个可选值： 
#no：表示等操作系统进行数据缓存同步到磁盘（快） 
#always：表示每次更新操作后手动调用fsync()将数据写到磁盘（慢，安全） 
#everysec：表示每秒同步一次（折衷，默认值）
appendfsync everysec
```

然后我们启动redis,首先我们运行以后台启动

然后指定名称

指定映射端口

将配置文件挂载到etc下

将data目录挂载

redis镜像 使用redis-server启动，并且指定配置文件，然后持久化

```sh
docker run -d \
--name redis \
-p 16371:6379 \
-v /docker/redis/conf/redis.conf:/etc/redis/redis.conf \
-v /docker/redis/data:/data \
redis:5.0.5 redis-server /etc/redis/redis.conf --appendonly yes
```

### 集群版

我们分别在两台主机上个搭建3个redis节点

下面以

192.168.1.1

192.168.1.2

端口如下

|  服务器ip   | redis端口 | 集群通信端口 |
| :---------: | :-------: | :----------: |
| 192.168.1.1 |   16371   |    26371     |
| 192.168.1.1 |   16372   |    26372     |
| 192.168.1.1 |   16373   |    26373     |
| 192.168.1.2 |   16371   |    26371     |
| 192.168.1.2 |   16372   |    26372     |
| 192.168.1.2 |   16373   |    26373     |

作为例子

我们先去每台机器放3个节点的配置文件以及容器

```sh
第一台
mkdir -p /docker/redis-cluster/redis{1,2,3}/{data,conf}
第二台
mkdir -p /docker/redis-cluster/redis{4,5,6}/{data,conf}
```

 然后写入一个配置文件并且复制到所有的conf下

```sh
vim /root/redis.conf
```

记住修改下面的端口号为

然后将下面的配置文件放入每个conf目录下,修改密码为自己密码并且修改端口号分别为16371，16372

如上示例我们只修改port，如果需要设置密码请参考最上方集群，注意此处修改密码为自己的密码否则为bigkang

```sh
# Redis默认不是以守护进程的方式运行，可以通过该配置项修改，使用yes启用守护进程
daemonize no

# 当Redis以守护进程方式运行时，Redis默认会把pid写入/var/run/redis.pid文件，可以通过pidfile指定
#pidfile /var/run/redis.pid

# 指定Redis监听端口，默认端口为6379，作者在自己的一篇博文中解释了为什么选用6379作为默认端口，
#因为6379在手机按键上MERZ对应的号码，而MERZ取自意大利歌女Alessia Merz的名字
#我们将6个redis设置为1637   1-6的端口
port 16371

# 绑定的主机地址,指定 redis 只接收来自于该IP地址的请求，如果不进行设置，那么将处理所有请求
#bind 0.0.0.0

# 当 客户端闲置多长时间后关闭连接，如果指定为0，表示关闭该功能
timeout 300


#此参数确定了TCP连接中已完成队列(完成三次握手之后)的长度， 当然此值必须不大于Linux系统定义的/proc/sys/net/core/somaxconn值，默认是511，而Linux的默认参数值是128。当系统并发量大并且客户端速度缓慢的时候，可以将这二个参数一起参考设定。该内核参数默认值一般是128，对于负载很大的服务程序来说大大的不够。一般会将它修改为2048或者更大。在/etc/sysctl.conf中添加:net.core.somaxconn = 2048，然后在终端中执行sysctl -p
tcp-backlog 511


# 指定日志记录级别，Redis总共支持四个级别：debug、verbose、notice、warning，
#默认为verbose
loglevel verbose

# 日志记录方式，默认为标准输出，如果配置Redis为守护进程方式运行，而这里又配置为日志
#记录方式为标准输出，则日志将会发送给/dev/null
logfile stdout

# 设置数据库的数量，默认数据库为0，可以使用SELECT <dbid>命令在连接上指定数据库id
databases 16

# 指定在多长时间内，有多少次更新操作，就将数据同步到数据文件，可以多个条件配合
#save <seconds> <changes>
#Redis默认配置文件中提供了三个条件：
save 900 1
save 300 10
save 60 10000
# 分别表示900秒（15分钟）内有1个更改，300秒（5分钟）内有10个更改以及60秒内有10000个更改。

# 指定存储至本地数据库时是否压缩数据，默认为yes，Redis采用LZF压缩，如果为了节省CPU时间，
#可以关闭该选项，但会导致数据库文件变的巨大
rdbcompression yes

# 指定本地数据库文件名，默认值为dump.rdb
dbfilename dump.rdb

# 指定本地数据库存放目录
dir /data

# 设置当本机为slav服务时，设置master服务的IP地址及端口，在Redis启动时，它会自动从master进行数据同步
#slaveof <masterip> <masterport>

# 当master服务设置了密码保护时，slav服务连接master的密码
masterauth bigkang

# 设置Redis连接密码，如果配置了连接密码，客户端在连接Redis时需要通过AUTH <password>命令提供密码，默认关闭
requirepass bigkang

# 设置同一时间最大客户端连接数，默认无限制，Redis可以同时打开的客户端连接数为Redis进程可以打开的最大文件描述符数，
#如果设置 maxclients 0，表示不作限制。当客户端连接数到达限制时，Redis会关闭新的连接并向客户端
#返回max number of clients reached错误信息
maxclients 128

# 指定Redis最大内存限制，Redis在启动时会把数据加载到内存中，达到最大内存后，Redis会先尝试清除已到期或即将到期的Key，
#当此方法处理 后，仍然到达最大内存设置，将无法再进行写入操作，但仍然可以进行读取操作。Redis新的vm机制，会把Key存放内存，
#Value会存放在swap区
#maxmemory <bytes>

# 指定是否在每次更新操作后进行日志记录，Redis在默认情况下是异步的把数据写入磁盘，如果不开启，可能会在断电时导致一段时间内的数据丢失。
#因为 redis本身同步数据文件是按上面save条件来同步的，所以有的数据会在一段时间内只存在于内存中。默认为no
appendonly no

# 指定更新日志文件名，默认为appendonly.aof
appendfilename appendonly.aof

# 指定更新日志条件，共有3个可选值： 
#no：表示等操作系统进行数据缓存同步到磁盘（快） 
#always：表示每次更新操作后手动调用fsync()将数据写到磁盘（慢，安全） 
#everysec：表示每秒同步一次（折中，默认值）
appendfsync everysec

#在aof重写的时候，如果打开了aof-rewrite-incremental-fsync开关，系统会每32MB执行一次fsync。这对于把文件写入磁盘是有帮助的，可以避免过大的延迟峰值
aof-rewrite-incremental-fsync yes
 
#在rdb保存的时候，如果打开了rdb-save-incremental-fsync开关，系统会每32MB执行一次fsync。这对于把文件写入磁盘是有帮助的，可以避免过大的延迟峰值
rdb-save-incremental-fsync yes

#开启集群
cluster-enabled yes
```

```sh
添加权限
chmod 777 /root/redis.conf
复制到文件中
第一台
cp /root/redis.conf /docker/redis-cluster/redis1/conf
cp /root/redis.conf /docker/redis-cluster/redis2/conf
cp /root/redis.conf /docker/redis-cluster/redis3/conf
第二台
cp /root/redis.conf /docker/redis-cluster/redis4/conf
cp /root/redis.conf /docker/redis-cluster/redis5/conf
cp /root/redis.conf /docker/redis-cluster/redis6/conf
```

然后启动容器

这里为什么使用host呢，因为由于docker的原因，他会给我们虚拟出来一个ip，这个ip不同于宿主机ip，这个时候在集群环境下就会出现问题，所以我们需要将网络映射到本地进行通信

然后我们启动6个redis，注意，redis通信端口为当前端口号+1万，例如16371，通信端口号为26371，如果不指定网络为host，则开放两个端口，推荐使用host，并且注意端口是否冲突

第一台

```sh
docker run -d \
--name redis1 \
--net=host \
-v /docker/redis-cluster/redis1/conf/redis.conf:/etc/redis/redis.conf \
-v /docker/redis-cluster/redis1/data:/data \
redis:5.0.5 redis-server /etc/redis/redis.conf --appendonly yes

docker run -d \
--name redis2 \
--net=host \
-v /docker/redis-cluster/redis2/conf/redis.conf:/etc/redis/redis.conf \
-v /docker/redis-cluster/redis2/data:/data \
redis:5.0.5 redis-server /etc/redis/redis.conf --appendonly yes

docker run -d \
--name redis3 \
--net=host \
-v /docker/redis-cluster/redis3/conf/redis.conf:/etc/redis/redis.conf \
-v /docker/redis-cluster/redis3/data:/data \
redis:5.0.5 redis-server /etc/redis/redis.conf --appendonly yes
```

第二台

```sh
docker run -d \
--name redis4 \
--net=host \
-v /docker/redis-cluster/redis4/conf/redis.conf:/etc/redis/redis.conf \
-v /docker/redis-cluster/redis4/data:/data \
redis:5.0.5 redis-server /etc/redis/redis.conf --appendonly yes

docker run -d \
--name redis5 \
--net=host \
-v /docker/redis-cluster/redis5/conf/redis.conf:/etc/redis/redis.conf \
-v /docker/redis-cluster/redis5/data:/data \
redis:5.0.5 redis-server /etc/redis/redis.conf --appendonly yes

docker run -d \
--name redis6 \
--net=host \
-v /docker/redis-cluster/redis6/conf/redis.conf:/etc/redis/redis.conf \
-v /docker/redis-cluster/redis6/data:/data \
redis:5.0.5 redis-server /etc/redis/redis.conf --appendonly yes
```

安装搭建已经完成了下面我们开始集群吧

```sh
随便进入一个容器
docker exec -it redis1 bash
进入bin目录
cd /bin
使用redis-cli进行集群
redis-cli --cluster create 192.168.1.1:16371 192.168.1.1:16372 192.168.1.1:16373 192.168.1.2:16371 192.168.1.2:16372 192.168.1.2:16373 --cluster-replicas 1 -a bigkang

-a   	密码


```

搭建完成后测试集群下载redis-cli快速（可省略）

```sh
wget http://download.redis.io/releases/redis-5.0.0.tar.gz


访问时使用redis集群访问
/root/redis-5.0.0/src/redis-cli -h 192.168.1.1 -p 16374 -p bigkang  -c
```

如果出现一直join那么需要配置公网

#### 公网搭建注意事项

如果公网搭建一直处于连接状态，那么需要去公网哪台redis将其他节点的公网ip配置完成，如果使用集群命令后一直在进行集群操作超过30秒，那么则进入其他服务器redis中输入以下命令

```sh
cluster meet 192.168.1.1 16371
cluster meet 192.168.1.1 16372
cluster meet 192.168.1.1 16373
cluster meet 192.168.1.2 16371
cluster meet 192.168.1.2 16372
cluster meet 192.168.1.2 16373
```



# 环境清理

### 单机版

停止容器以及删除容器

```
docker stop redis
docker rm redis
```

### 集群版

清理所有集群信息，（此操作清空集群信息可重新搭建集群），只清理集群信息不清理redis

```sh
rm -rf /docker/redis-cluster/redis{1,2,3,4,5,6}/data

然后重启所有redis
docker restart redis{1,2,3,4,5,6}
```

清除所有redis集群信息以及数据

```sh
rm -rf /docker/redis-cluster/redis{4,5,6}/data
docker restart redis{4,5,6}


rm -rf /docker/redis-cluster/redis{1,2,3}/data
docker restart redis{1,2,3}


docker stop redis{1,2,3}
docker stop redis{4,5,6}

docker rm redis{1,2,3}
docker rm redis{4,5,6}
```

# Java整合后问题

### 第一次启动寻找内网地址

​		公网连接查询内网ip，连接超时

​		我们将项目运行后第一次查询或者写入，发现他去查询内网地址并且查找内网ip，怎么办呢，这个原因肯定是因为我们集群默认他将自己节点的信息存储在，数据中了，我们找到data目录，将里面的node.conf自己的内网节点改为公网ip即可



# Redis配置文件详解

## 网络与数据库相关

```properties
# 指定 redis 只接收来自于该IP地址的请求，如果不进行设置，那么将处理所有请求，默认0.0.0.0，所有都可以访问
bind 0.0.0.0
 
# 是否开启保护模式，默认开启。要是配置里没有指定bind和密码。开启该参数后，redis只会本地进行访问，拒绝外部访问。要是开启了密码和bind，可以开启。否则最好关闭，设置为no
protected-mode yes
 
# redis监听的端口号
port 6379
 
# 此参数确定了TCP连接中已完成队列(完成三次握手之后)的长度， 当然此值必须不大于Linux系统定义的/proc/sys/net/core/somaxconn值，默认是511，而Linux的默认参数值是128。当系统并发量大并且客户端速度缓慢的时候，可以将这二个参数一起参考设定。该内核参数默认值一般是128，对于负载很大的服务程序来说大大的不够。一般会将它修改为2048或者更大。在/etc/sysctl.conf中添加:net.core.somaxconn = 2048，然后在终端中执行sysctl -p
tcp-backlog 2048
 
# 此参数为设置客户端空闲超过timeout，服务端会断开连接，为0则服务端不会主动断开连接，不能小于0
timeout 0
 
# tcp keepalive参数。如果设置不为0，就使用配置tcp的SO_KEEPALIVE值，使用keepalive有两个好处:检测挂掉的对端。降低中间设备出问题而导致网络看似连接却已经与对端端口的问题。在Linux内核中，设置了keepalive，redis会定时给对端发送ack。检测到对端关闭需要两倍的设置值
tcp-keepalive 300
 
# 是否在后台执行，yes：后台运行；no：不是后台运行,Redis默认不是以守护进程的方式运行，可以通过该配置项修改，使用yes启用守护进程
daemonize yes
 
# redis的进程文件，当Redis以守护进程方式运行时，Redis默认会把pid写入/var/run/redis.pid文件，可以通过pidfile指定
pidfile /var/run/redis/redis.pid
 
# 指定了服务端日志的级别。级别包括：debug（很多信息，方便开发、测试），verbose（许多有用的信息，但是没有debug级别信息多），notice（适当的日志级别，适合生产环境），warn（只有非常重要的信息）
loglevel notice
 
# 指定了记录日志的文件。空字符串的话，日志会打印到标准输出设备。后台运行的redis标准输出是/dev/null
logfile /usr/local/redis/var/redis.log
 
# 指定包含其它的配置文件，可以在同一主机上多个Redis实例之间使用同一份配置文件，而同时各个实例又拥有自己的特定配置文件
# include /path/to/local.conf**
 
# 是否打开记录syslog功能
# syslog-enabled no
 
#syslog的标识符。
# syslog-ident redis
 
#日志的来源、设备
# syslog-facility local0
 
#数据库的数量，默认使用的数据库是0。可以通过”SELECT 【数据库序号】“命令选择一个数据库，序号从0开始
databases 16
```

## RDB快照配置

```properties
#RDB核心规则配置 save <指定时间间隔> <执行指定次数更新操作>，满足条件就将内存中的数据同步到硬盘中。官方出厂配置默认是 900秒内有1个更改，300秒内有10个更改以及60秒内有10000个更改，则将内存中的数据快照写入磁盘。若不想用RDB方案，可以把 save "" 的注释打开，下面三个注释
#   save ""
save 900 1
save 300 10
save 60 10000
# 分别表示900秒（15分钟）内有1个更改，300秒（5分钟）内有10个更改以及60秒内有10000个更改
#当RDB持久化出现错误后，是否依然进行继续进行工作，yes：不能进行工作，no：可以继续进行工作，可以通过info中的rdb_last_bgsave_status了解RDB持久化是否有错误
stop-writes-on-bgsave-error yes
 
#配置存储至本地数据库时是否压缩数据，默认为yes。Redis采用LZF压缩方式，但占用了一点CPU的时间。若关闭该选项，但会导致数据库文件变的巨大。建议开启。
rdbcompression yes
 
#是否校验rdb文件;从rdb格式的第五个版本开始，在rdb文件的末尾会带上CRC64的校验和。这跟有利于文件的容错性，但是在保存rdb文件的时候，会有大概10%的性能损耗，所以如果你追求高性能，可以关闭该配置
rdbchecksum yes
 
#指定本地数据库文件名，一般采用默认的 dump.rdb
dbfilename dump.rdb
 
#数据目录，数据库的写入会在这个目录。rdb、aof文件也会写在这个目录
dir /usr/local/redis/var
```

## 复制集

```properties
# slave从节点的Ip以及端口号（可以不配置）
# slaveof <masterip> <masterport>

# 复制选项，slave复制对应的master。
# replicaof <masterip> <masterport>
 
#如果master设置了requirepass，那么slave要连上master，需要有master的密码才行。masterauth就是用来配置master的密码，这样可以在连上master后进行认证。
# masterauth <master-password>
 
#当从库同主机失去连接或者复制正在进行，从机库有两种运行方式：
#1) 如果slave-serve-stale-data设置为yes(默认设置)，从库会继续响应客户端的请求。
#2) 如果slave-serve-stale-data设置为no，INFO,replicaOF, AUTH, PING, SHUTDOWN, REPLCONF, ROLE, CONFIG,SUBSCRIBE, UNSUBSCRIBE,PSUBSCRIBE, PUNSUBSCRIBE, PUBLISH, PUBSUB,COMMAND, POST, HOST: and LATENCY命令之外的任何请求都会返回一个错误”SYNC with master in progress”。
replica-serve-stale-data yes
 
#作为从服务器，默认情况下是只读的（yes），可以修改成NO，用于写（不建议）
#replica-read-only yes
 
# 是否使用socket方式复制数据。目前redis复制提供两种方式，disk和socket。如果新的slave连上来或者重连的slave无法部分同步，就会执行全量同步，master会生成rdb文件。有2种方式：disk方式是master创建一个新的进程把rdb文件保存到磁盘，再把磁盘上的rdb文件传递给slave。socket是master创建一个新的进程，直接把rdb文件以socket的方式发给slave。disk方式的时候，当一个rdb保存的过程中，多个slave都能共享这个rdb文件。socket的方式就的一个个slave顺序复制。在磁盘速度缓慢，网速快的情况下推荐用socket方式。
repl-diskless-sync no
 
#diskless复制的延迟时间，防止设置为0。一旦复制开始，节点不会再接收新slave的复制请求直到下一个rdb传输。所以最好等待一段时间，等更多的slave连上来
repl-diskless-sync-delay 5
 
#slave根据指定的时间间隔向服务器发送ping请求。时间间隔可以通过 repl_ping_slave_period 来设置，默认10秒。
# repl-ping-slave-period 10
 
# 复制连接超时时间。master和slave都有超时时间的设置。master检测到slave上次发送的时间超过repl-timeout，即认为slave离线，清除该slave信息。slave检测到上次和master交互的时间超过repl-timeout，则认为master离线。需要注意的是repl-timeout需要设置一个比repl-ping-slave-period更大的值，不然会经常检测到超时
# repl-timeout 60
 
 
#是否禁止复制tcp链接的tcp nodelay参数，可传递yes或者no。默认是no，即使用tcp nodelay。如果master设置了yes来禁止tcp nodelay设置，在把数据复制给slave的时候，会减少包的数量和更小的网络带宽。但是这也可能带来数据的延迟。默认我们推荐更小的延迟，但是在数据量传输很大的场景下，建议选择yes
repl-disable-tcp-nodelay no
 
#复制缓冲区大小，这是一个环形复制缓冲区，用来保存最新复制的命令。这样在slave离线的时候，不需要完全复制master的数据，如果可以执行部分同步，只需要把缓冲区的部分数据复制给slave，就能恢复正常复制状态。缓冲区的大小越大，slave离线的时间可以更长，复制缓冲区只有在有slave连接的时候才分配内存。没有slave的一段时间，内存会被释放出来，默认1m
# repl-backlog-size 1mb
 
# master没有slave一段时间会释放复制缓冲区的内存，repl-backlog-ttl用来设置该时间长度。单位为秒。
# repl-backlog-ttl 3600
 
# 当master不可用，Sentinel会根据slave的优先级选举一个master。最低的优先级的slave，当选master。而配置成0，永远不会被选举
replica-priority 100
 
#redis提供了可以让master停止写入的方式，如果配置了min-replicas-to-write，健康的slave的个数小于N，mater就禁止写入。master最少得有多少个健康的slave存活才能执行写命令。这个配置虽然不能保证N个slave都一定能接收到master的写操作，但是能避免没有足够健康的slave的时候，master不能写入来避免数据丢失。设置为0是关闭该功能
# min-replicas-to-write 3
 
# 延迟小于min-replicas-max-lag秒的slave才认为是健康的slave
# min-replicas-max-lag 10
 
# 设置1或另一个设置为0禁用这个特性。
# Setting one or the other to 0 disables the feature.
# By default min-replicas-to-write is set to 0 (feature disabled) and
# min-replicas-max-lag is set to 10.
```

## 安全方面

```properties
#requirepass配置可以让用户使用AUTH命令来认证密码，才能使用其他命令。这让redis可以使用在不受信任的网络中。为了保持向后的兼容性，可以注释该命令，因为大部分用户也不需要认证。使用requirepass的时候需要注意，因为redis太快了，每秒可以认证15w次密码，简单的密码很容易被攻破，所以最好使用一个更复杂的密码
# requirepass foobared
#把危险的命令给修改成其他名称。比如CONFIG命令可以重命名为一个很难被猜到的命令，这样用户不能使用，而内部工具还能接着使用
# rename-command CONFIG b840fc02d524045429941cc15f59e41cb7be6c52
#设置成一个空的值，可以禁止一个命令
# rename-command CONFIG ""
```

## Client客户端

```properties
# 设置能连上redis的最大客户端连接数量。默认是10000个客户端连接。由于redis不区分连接是客户端连接还是内部打开文件或者和slave连接等，所以maxclients最小建议设置到32。如果超过了maxclients，redis会给新的连接发送’max number of clients reached’，并关闭连接
# maxclients 10000
```

## 内存管理

```properties
# redis配置的最大内存容量。当内存满了，需要配合maxmemory-policy策略进行处理。注意slave的输出缓冲区是不计算在maxmemory内的。所以为了防止主机内存使用完，建议设置的maxmemory需要更小一些，默认内存无上限
# maxmemory <bytes>
#内存容量超过maxmemory后的处理策略。
	# volatile-lru：利用LRU算法移除设置过过期时间的key。
	# volatile-random：随机移除设置过过期时间的key。
	# volatile-ttl：移除即将过期的key，根据最近过期时间来删除（辅以TTL）
	# allkeys-lru：利用LRU算法移除任何key。
	# allkeys-random：随机移除任何key。
	# noeviction：不移除任何key，只是返回一个写错误。
# 上面的这些驱逐策略，如果redis没有合适的key驱逐，对于写命令，还是会返回错误。redis将不再接收写请求，只接收get请求。写命令包括：set setnx setex append incr decr rpush lpush rpushx lpushx linsert lset rpoplpush sadd sinter sinterstore sunion sunionstore sdiff sdiffstore zadd zincrby zunionstore zinterstore hset hsetnx hmset hincrby incrby decrby getset mset msetnx exec sort。
maxmemory-policy noeviction
# lru检测的样本数。使用lru或者ttl淘汰算法，从需要淘汰的列表中随机选择sample个key，选出闲置时间最长的key移除
# maxmemory-samples 5
# 是否开启salve的最大内存
# replica-ignore-maxmemory yes
```

## 懒释放

```properties
# 以非阻塞方式释放内存
# 使用以下配置指令调用了
lazyfree-lazy-eviction no
lazyfree-lazy-expire no
lazyfree-lazy-server-del no
replica-lazy-flush no
```

## AOF追加模式

```properties
# Redis 默认不开启。它的出现是为了弥补RDB的不足（数据的不一致性），所以它采用日志的形式来记录每个写操作，并追加到文件中。Redis 重启的会根据日志文件的内容将写指令从前到后执行一次以完成数据的恢复工作默认redis使用的是rdb方式持久化，这种方式在许多应用中已经足够用了。但是redis如果中途宕机，会导致可能有几分钟的数据丢失，根据save来策略进行持久化，Append Only File是另一种持久化方式，可以提供更好的持久化特性。Redis会把每次写入的数据在接收后都写入 appendonly.aof 文件，每次启动时Redis都会先把这个文件的数据读入内存里，先忽略RDB文件。若开启rdb则将no改为yes，默认关闭

appendonly no

# 指定本地数据库文件名，默认值为 appendonly.aof
appendfilename "appendonly.aof"
 
#aof持久化策略的配置
#no表示不执行fsync，由操作系统保证数据同步到磁盘，速度最快
#always表示每次写入都执行fsync，以保证数据同步到磁盘
#everysec表示每秒执行一次fsync，可能会导致丢失这1s数据
# appendfsync always
appendfsync everysec
# appendfsync no
 
# 在aof重写或者写入rdb文件的时候，会执行大量IO，此时对于everysec和always的aof模式来说，执行fsync会造成阻塞过长时间，no-appendfsync-on-rewrite字段设置为默认设置为no。如果对延迟要求很高的应用，这个字段可以设置为yes，否则还是设置为no，这样对持久化特性来说这是更安全的选择。设置为yes表示rewrite期间对新写操作不fsync,暂时存在内存中,等rewrite完成后再写入，默认为no，建议yes。Linux的默认fsync策略是30秒。可能丢失30秒数据
no-appendfsync-on-rewrite no
 
#aof自动重写配置。当目前aof文件大小超过上一次重写的aof文件大小的百分之多少进行重写，即当aof文件增长到一定大小的时候Redis能够调用bgrewriteaof对日志文件进行重写。当前AOF文件大小是上次日志重写得到AOF文件大小的二倍（设置为100）时，自动启动新的日志重写过程
auto-aof-rewrite-percentage 100
 
#设置允许重写的最小aof文件大小，避免了达到约定百分比但尺寸仍然很小的情况还要重写
auto-aof-rewrite-min-size 64mb
 
#aof文件可能在尾部是不完整的，当redis启动的时候，aof文件的数据被载入内存。重启可能发生在redis所在的主机操作系统宕机后，尤其在ext4文件系统没有加上data=ordered选项（redis宕机或者异常终止不会造成尾部不完整现象。）出现这种现象，可以选择让redis退出，或者导入尽可能多的数据。如果选择的是yes，当截断的aof文件被导入的时候，会自动发布一个log给客户端然后load。如果是no，用户必须手动redis-check-aof修复AOF文件才可以
aof-load-truncated yes
 
#加载redis时，可以识别AOF文件以“redis”开头。
#字符串并加载带前缀的RDB文件，然后继续加载AOF尾巴
aof-use-rdb-preamble yes
```

## Lua脚本

```properties
# 如果达到最大时间限制（毫秒），redis会记个log，然后返回error。当一个脚本超过了最大时限。只有SCRIPT KILL和SHUTDOWN NOSAVE可以用。第一个可以杀没有调write命令的东西。要是已经调用了write，只能用第二个命令杀
lua-time-limit 5000
```

## 集群

```properties
# 集群开关，默认是不开启集群模式
# cluster-enabled yes
 
#集群配置文件的名称，每个节点都有一个集群相关的配置文件，持久化保存集群的信息。这个文件并不需要手动配置，这个配置文件有Redis生成并更新，每个Redis集群节点需要一个单独的配置文件，请确保与实例运行的系统中配置文件名称不冲突
# cluster-config-file nodes-6379.conf
 
#节点互连超时的阀值。集群节点超时毫秒数
# cluster-node-timeout 15000
 
#在进行故障转移的时候，全部slave都会请求申请为master，但是有些slave可能与master断开连接一段时间了，导致数据过于陈旧，这样的slave不应该被提升为master。该参数就是用来判断slave节点与master断线的时间是否过长。判断方法是：
#比较slave断开连接的时间和(node-timeout * slave-validity-factor) + repl-ping-slave-period
#如果节点超时时间为三十秒, 并且slave-validity-factor为10,假设默认的repl-ping-slave-period是10秒，即如果超过310秒slave将不会尝试进行故障转移
# cluster-replica-validity-factor 10
 
# master的slave数量大于该值，slave才能迁移到其他孤立master上，如这个参数若被设为2，那么只有当一个主节点拥有2 个可工作的从节点时，它的一个从节点会尝试迁移
# cluster-migration-barrier 1
 
#默认情况下，集群全部的slot有节点负责，集群状态才为ok，才能提供服务。设置为no，可以在slot没有全部分配的时候提供服务。不建议打开该配置，这样会造成分区的时候，小分区的master一直在接受写请求，而造成很长时间数据不一致
# cluster-require-full-coverage yes
```

## 集群DOCKER / NAT支持

```properties
# 群集公告IP
# 群集公告端口
# 群集公告总线端口
# Example:
#
# cluster-announce-ip 10.1.1.5
# cluster-announce-port 6379
# cluster-announce-bus-port 6380
```

## 慢日志

```properties
# slog log是用来记录redis运行中执行比较慢的命令耗时。当命令的执行超过了指定时间，就记录在slow log中，slog log保存在内存中，所以没有IO操作。
#执行时间比slowlog-log-slower-than大的请求记录到slowlog里面，单位是微秒，所以1000000就是1秒。注意，负数时间会禁用慢查询日志，而0则会强制记录所有命令。
slowlog-log-slower-than 10000
 
#慢查询日志长度。当一个新的命令被写进日志的时候，最老的那个记录会被删掉。这个长度没有限制。只要有足够的内存就行。你可以通过 SLOWLOG RESET 来释放内存
slowlog-max-len 128
```

## 延迟监视器

```properties
#延迟监控功能是用来监控redis中执行比较缓慢的一些操作，用LATENCY打印redis实例在跑命令时的耗时图表。只记录大于等于下边设置的值的操作。0的话，就是关闭监视。默认延迟监控功能是关闭的，如果你需要打开，也可以通过CONFIG SET命令动态设置
latency-monitor-threshold 0
```

## 活动通知

```properties
#键空间通知使得客户端可以通过订阅频道或模式，来接收那些以某种方式改动了 Redis 数据集的事件。因为开启键空间通知功能需要消耗一些 CPU ，所以在默认配置下，该功能处于关闭状态。
#notify-keyspace-events 的参数可以是以下字符的任意组合，它指定了服务器该发送哪些类型的通知：
##K 键空间通知，所有通知以 __keyspace@__ 为前缀
##E 键事件通知，所有通知以 __keyevent@__ 为前缀
##g DEL 、 EXPIRE 、 RENAME 等类型无关的通用命令的通知
##$ 字符串命令的通知
##l 列表命令的通知
##s 集合命令的通知
##h 哈希命令的通知
##z 有序集合命令的通知
##x 过期事件：每当有过期键被删除时发送
##e 驱逐(evict)事件：每当有键因为 maxmemory 政策而被删除时发送
##A 参数 g$lshzxe 的别名
#输入的参数中至少要有一个 K 或者 E，否则的话，不管其余的参数是什么，都不会有任何 通知被分发。详细使用可以参考http://redis.io/topics/notifications
 
notify-keyspace-events ""
```

## 高级配置

```properties
# 数据量小于等于hash-max-ziplist-entries的用ziplist，大于hash-max-ziplist-entries用hash
hash-max-ziplist-entries 512
 
# value大小小于等于hash-max-ziplist-value的用ziplist，大于hash-max-ziplist-value用hash
hash-max-ziplist-value 64
 
#-5:最大大小：64 KB<--不建议用于正常工作负载
#-4:最大大小：32 KB<--不推荐
#-3:最大大小：16 KB<--可能不推荐
#-2:最大大小：8kb<--良好
#-1:最大大小：4kb<--良好
list-max-ziplist-size -2
 
#0:禁用所有列表压缩
#1：深度1表示“在列表中的1个节点之后才开始压缩，
#从头部或尾部
#所以：【head】->node->node->…->node->【tail】
#[头部]，[尾部]将始终未压缩；内部节点将压缩。
#2:[头部]->[下一步]->节点->节点->…->节点->[上一步]->[尾部]
#2这里的意思是：不要压缩头部或头部->下一个或尾部->上一个或尾部，
#但是压缩它们之间的所有节点。
#3:[头部]->[下一步]->[下一步]->节点->节点->…->节点->[上一步]->[上一步]->[尾部]
list-compress-depth 0
 
# 数据量小于等于set-max-intset-entries用iniset，大于set-max-intset-entries用set
set-max-intset-entries 512
 
#数据量小于等于zset-max-ziplist-entries用ziplist，大于zset-max-ziplist-entries用zset
zset-max-ziplist-entries 128
 
#value大小小于等于zset-max-ziplist-value用ziplist，大于zset-max-ziplist-value用zset
zset-max-ziplist-value 64
 
#value大小小于等于hll-sparse-max-bytes使用稀疏数据结构（sparse），大于hll-sparse-max-bytes使用稠密的数据结构（dense）。一个比16000大的value是几乎没用的，建议的value大概为3000。如果对CPU要求不高，对空间要求较高的，建议设置到10000左右
hll-sparse-max-bytes 3000
 
#宏观节点的最大流/项目的大小。在流数据结构是一个基数
#树节点编码在这项大的多。利用这个配置它是如何可能#大节点配置是单字节和
#最大项目数，这可能包含了在切换到新节点的时候
# appending新的流条目。如果任何以下设置来设置
# ignored极限是零，例如，操作系统，它有可能只是一集通过设置限制最大#纪录到最大字节0和最大输入到所需的值
stream-node-max-bytes 4096
stream-node-max-entries 100
 
#Redis将在每100毫秒时使用1毫秒的CPU时间来对redis的hash表进行重新hash，可以降低内存的使用。当你的使用场景中，有非常严格的实时性需要，不能够接受Redis时不时的对请求有2毫秒的延迟的话，把这项配置为no。如果没有这么严格的实时性要求，可以设置为yes，以便能够尽可能快的释放内存
activerehashing yes
 
##对客户端输出缓冲进行限制可以强迫那些不从服务器读取数据的客户端断开连接，用来强制关闭传输缓慢的客户端。
#对于normal client，第一个0表示取消hard limit，第二个0和第三个0表示取消soft limit，normal client默认取消限制，因为如果没有寻问，他们是不会接收数据的
client-output-buffer-limit normal 0 0 0
 
#对于slave client和MONITER client，如果client-output-buffer一旦超过256mb，又或者超过64mb持续60秒，那么服务器就会立即断开客户端连接
client-output-buffer-limit replica 256mb 64mb 60
 
#对于pubsub client，如果client-output-buffer一旦超过32mb，又或者超过8mb持续60秒，那么服务器就会立即断开客户端连接
client-output-buffer-limit pubsub 32mb 8mb 60
 
# 这是客户端查询的缓存极限值大小
# client-query-buffer-limit 1gb
 
#在redis协议中，批量请求，即表示单个字符串，通常限制为512 MB。但是您可以更改此限制。
# proto-max-bulk-len 512mb
 
#redis执行任务的频率为1s除以hz
hz 10
 
#当启用动态赫兹时，实际配置的赫兹将用作作为基线，但实际配置的赫兹值的倍数
#在连接更多客户端后根据需要使用。这样一个闲置的实例将占用很少的CPU时间，而繁忙的实例将反应更灵敏
dynamic-hz yes
 
#在aof重写的时候，如果打开了aof-rewrite-incremental-fsync开关，系统会每32MB执行一次fsync。这对于把文件写入磁盘是有帮助的，可以避免过大的延迟峰值
aof-rewrite-incremental-fsync yes
 
#在rdb保存的时候，如果打开了rdb-save-incremental-fsync开关，系统会每32MB执行一次fsync。这对于把文件写入磁盘是有帮助的，可以避免过大的延迟峰值
rdb-save-incremental-fsync yes





# 指定是否启用虚拟内存机制，默认值为no，简单的介绍一下，VM机制将数据分页存放，由Redis将访问量较少的页即冷数据swap到磁盘上，访问多的页面由磁盘自动换出到内存中（在后面的文章我会仔细分析Redis的VM机制）
# vm-enabled no

# 虚拟内存文件路径，默认值为/tmp/redis.swap，不可多个Redis实例共享
# vm-swap-file /tmp/redis.swap

# 将所有大于vm-max-memory的数据存入虚拟内存,无论vm-max-memory设置多小,所有索引数据都是内存存储的(Redis的索引数据 就是keys),也就是说,当vm-max-memory设置为0的时候,其实是所有value都存在于磁盘。默认值为0
# vm-max-memory 0

# Redis swap文件分成了很多的page，一个对象可以保存在多个page上面，但一个page上不能被多个对象共享，vm-page-size是要根据存储的 数据大小来设定的，作者建议如果存储很多小对象，page大小最好设置为32或者64bytes；如果存储很大大对象，则可以使用更大的page，如果不 确定，就使用默认值
# vm-page-size 32

# 设置swap文件中的page数量，由于页表（一种表示页面空闲或使用的bitmap）是在放在内存中的，，在磁盘上每8个pages将消耗1byte的内存。
# vm-pages 134217728

# 设置访问swap文件的线程数,最好不要超过机器的核数,如果设置为0,那么所有对swap文件的操作都是串行的，可能会造成比较长时间的延迟。默认值为4
# vm-max-threads 4

# 设置在向客户端应答时，是否把较小的包合并为一个包发送，默认为开启
# glueoutputbuf yes

# 指定在超过一定的数量或者最大的元素超过某一临界值时，采用一种特殊的哈希算法
# hash-max-zipmap-entries 64
# hash-max-zipmap-value 512
```

## 主动碎片整理

```properties
# 已启用活动碎片整理
# activedefrag yes
# 启动活动碎片整理的最小碎片浪费量
# active-defrag-ignore-bytes 100mb
# 启动活动碎片整理的最小碎片百分比
# active-defrag-threshold-lower 10
# 我们使用最大努力的最大碎片百分比
# active-defrag-threshold-upper 100
# 以CPU百分比表示的碎片整理的最小工作量
# active-defrag-cycle-min 5
# 在CPU的百分比最大的努力和碎片整理
# active-defrag-cycle-max 75
#将从中处理的set/hash/zset/list字段的最大数目
#主词典扫描
# active-defrag-max-scan-fields 1000
```

