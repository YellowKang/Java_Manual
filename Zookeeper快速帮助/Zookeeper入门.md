# 什么是Zookeeper？

​		ZooKeeper是用于维护配置信息，命名，提供分布式同步以及提供组服务的集中式服务。所有这些类型的服务都以某种形式被分布式应用程序使用。每次实施它们时，都会进行很多工作来修复不可避免的错误和竞争条件。由于难以实现这类服务，因此应用程序最初通常会跳过它们，这会使它们在存在更改的情况下变得脆弱并且难以管理。即使部署正确，这些服务的不同实现也会导致管理复杂。

​		简单的来说Zookeeper是一个分布式中提供集中服务的组件，可以帮助我们协调分布式中的各个节点，也就是集群。

​		ZooKeeper提供的名称空间与标准文件系统的名称空间非常相似。名称是由斜杠（/）分隔的一系列路径元素。ZooKeeper名称空间中的每个节点都由路径标识。

# Zookeeper的核心概念？

## Cluster Role（集群角色）

​		在Zookeeper集群中，机器被分为以下三种角色：

- ​			**Leader** ：为客户端提供读写服务，并维护集群状态，它是由集群选举所产生的；
- ​            **Follower** ：为客户端提供读写服务，并定期向 Leader 汇报自己的节点状态。同时也参与写操作“过半写成功”的策略和 Leader 的选举；
- ​           **Observer** ：为客户端提供读写服务，并定期向 Leader 汇报自己的节点状态，但不参与写操作“过半写成功”的策略和 Leader 的选举，因此 Observer 可以在不影响写性能的情况下提升集群的读性能。

## Session（会话）

​		Zookeeper 客户端通过 TCP 长连接连接到服务集群，会话 (Session) 从第一次连接开始就已经建立，之后通过心跳检测机制来保持有效的会话状态。通过这个连接，客户端可以发送请求并接收响应，同时也可以接收到 Watch 事件的通知。

​		关于会话中另外一个核心的概念是 sessionTimeOut(会话超时时间)，当由于网络故障或者客户端主动断开等原因，导致连接断开，此时只要在会话超时时间之内重新建立连接，则之前创建的会话依然有效。

## ZNode（节点）

​		Zookeeper 数据模型是由一系列基本数据单元 `Znode`(数据节点) 组成的节点树，其中根节点为 `/`。每个节点上都会保存自己的数据和节点信息。Zookeeper 中节点可以分为两大类：

- **持久节点** ：节点一旦创建，除非被主动删除，否则一直存在；
- **临时节点** ：一旦创建该节点的客户端会话失效，则所有该客户端创建的临时节点都会被删除。

​		临时节点和持久节点都可以添加一个特殊的属性：`SEQUENTIAL`，代表该节点是否具有递增属性。如果指定该属性，那么在这个节点创建时，Zookeeper 会自动在其节点名称后面追加一个由父节点维护的递增数字。

## Stat（状态，节点信息）

​		每个 ZNode 节点在存储数据的同时，都会维护一个叫做 `Stat` 的数据结构，里面存储了关于该节点的全部状态信息。如下：

每个 ZNode 节点在存储数据的同时，都会维护一个叫做 `Stat` 的数据结构，里面存储了关于该节点的全部状态信息。如下：

|  **状态属性**  |                           **说明**                           |
| :------------: | :----------------------------------------------------------: |
|     czxid      |                   数据节点创建时的事务 ID                    |
|     ctime      |                     数据节点创建时的时间                     |
|     mzxid      |               数据节点最后一次更新时的事务 ID                |
|     mtime      |                 数据节点最后一次更新时的时间                 |
|     pzxid      |          数据节点的子节点最后一次被修改时的事务 ID           |
|    cversion    |                       子节点的更改次数                       |
|    version     |                      节点数据的更改次数                      |
|    aversion    |                    节点的 ACL 的更改次数                     |
| ephemeralOwner | 如果节点是临时节点，则表示创建该节点的会话的 SessionID；如果节点是持久节点，则该属性值为 0 |
|   dataLength   |                        数据内容的长度                        |
|  numChildren   |                   数据节点当前的子节点个数                   |

## Watcher（事件监听器）

​		Zookeeper 中一个常用的功能是 Watcher(事件监听器)，它允许用户在指定节点上针对感兴趣的事件注册监听，当事件发生时，监听器会被触发，并将事件信息推送到客户端。该机制是 Zookeeper 实现分布式协调服务的重要特性。

​		我们可以对某个节点的某个时间进行监听，例如某个节点创建了，删除了，或者修改了。

## ACL（访问控制列表，权限控制）

​		Zookeeper 采用 ACL(Access Control Lists) 策略来进行权限控制，类似于 UNIX 文件系统的权限控制。它定义了如下五种权限：

- ​			**CREATE**：允许创建子节点；
- ​			**READ**：允许从节点获取数据并列出其子节点；
- ​			**WRITE**：允许为节点设置数据；
- ​			**DELETE**：允许删除子节点；
- ​			**ADMIN**：允许为节点设置权限。

# Zookeeper操作

## 连接到Zookeeper

​		使用命令zkCli.sh

```sh
zkCli.sh  -timeout 5000 -server 192.168.1.12:2181,192.168.1.28:2181,192.168.1.115:2181

# -timeout超时时间单位毫秒
# -server表示zk节点地址
```

​		再使用帮助命令查看命令

```sh
h
# 返回如下
ZooKeeper -server host:port cmd args
        stat path [watch]
        set path data [version]
        ls path [watch]
        delquota [-n|-b] path
        ls2 path [watch]
        setAcl path acl
        setquota -n|-b val path
        history 
        redo cmdno
        printwatches on|off
        delete path [version]
        sync path
        listquota path
        rmr path
        get path [watch]
        create [-s] [-e] path data acl
        addauth scheme auth
        quit 
        getAcl path
        close 
        connect host:port
```

## 创建节点

​		创建一个简单的test节点

```sh
create /test bigkang

# 然后获取节点
get /test

# 返回如下，其他属性参照上方的Stat
bigkang
cZxid = 0x200000ef0
ctime = Sat Feb 20 07:06:22 GMT 2021
mZxid = 0x200000ef0
mtime = Sat Feb 20 07:06:22 GMT 2021
pZxid = 0x200000ef0
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 7
numChildren = 0
```

​		创建一个自增节点

```sh
# 使用命令
create -s /test 123
# 返回如下
Created /test0000000030

# 继续创建
create -s /test 123
# 返回如下
Created /test0000000031

# 我们可以看到ZNode节点的名字是自增的了
```

​		创建一个零时节点

```sh
create -e /test 123

# 然后我们退出命令行断开连接，发现节点被删除了
-e 表示创建的连接断开的时候跟随着删除节点
```

## 修改节点

​		将节点值修改为123

```sh
 # 创建节点
 create /test bigkang
 # 查看版本,dataVersion为版本号，默认0开始
 stat /test
 # 修改值为123
 set /test 123
 
 # 然后再次查看节点版本，发现+1
 stat /test
```

## 删除节点

​		删除节点

```
delete /test
```

## 监听节点

​		监听子节点创建，或者节点删除

```sh
# 监听test节点
ls /test watch

# 如果删除或者添加子节点则会触发NodeChildrenChanged
# 如果删除当前节点则会触发NodeDeleted 
```

​		监听节点内容改变或者删除，使用get可以监听节点的内容更变，而ls不行

```sh
get /test watch

# 如果内容发生改变则会触发NodeDataChanged
# 如果删除或者添加子节点则会触发NodeChildrenChanged
# 如果删除当前节点则会触发NodeDeleted 
```

​		