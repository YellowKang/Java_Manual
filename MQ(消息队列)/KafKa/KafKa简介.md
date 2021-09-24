# 什么是Kafka？

​		KafKa是一款由Apache软件基金会开源,由Scala编写的一个分布式发布订阅消息系统，它是遵循FIFO（First Input Fitst Output      先进先出）队列的比较传统的执行方式 

# Kafka能做什么？

​		KafKa它最初的目的是为了解决,统一,高效低延时,高通量(同时能传输的数据量)并且高可用一个消息平台.

​		他可以用来

​			应用解耦

​			异步处理

​			数据限流

​			消息通信等等

### Kafka的优势

​		1、高吞吐量

​		2、高可用队列

​		3、递延时

​		4、分布式机制

​		5、异步生产数据

​		6、偏移量迁移

### Kafka的适用场景

​		在实际的使用场景中，kafka有着广泛的应用，例如，日志收集，消息系统，活动追踪，运营指标，流式处理，事件源等等

# Kafka的核心

### Broker（服务代理）

​			在Kafka集群中一个Kafka进程我们称之为一个代理

​			例如我们在搭建了一个由6个节点组成的kafka节点那么他就有6个Broker，简单的来说Broker就是我们的kafka实例。

​			如果我们在两台服务器中部署了6个节点，每台服务器3个节点那么结构图（以Zookeeper版本为例）如下：

​			![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1624435705985.png)

### Producer（生产者）

​			在Kafka中生产者被称之为Producer，生产者是消息的发送者，他们将消息发送到指定的主题（Topic）中，同时生产者也能通过自定义算法决定将消息记录发送到哪个分区(Partition),例如通过获取消息记录的主键的Hash值然后对该值进行分区，然后进行取模操作得到分区索引然后查找分区。

​			他们讲消息发送到指定的主题（Topic）中，同时生产者也能通过自定义算法决定将消息记录发送到哪个分区(Partition),例如通过获取消息记录的主键的Hash值然后对该值进行分区，然后进行取模操作得到分区索引然后查找分区。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1624436141079.png)

### Consumer（消费者）

​			消费者从Kafka集群中从指定的Topic中读取消息记录，在读取主体数据时需要设置消费组名（GroupId），如果不设置则kafka会生成一个消费组名称。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1624436435944.png)

### Topic（话题）	

​			kafka通过主题来区分不同业务类型的消息记录，例如用户登录的数据存储在主题A中，用户的充值几率存储在主题B中，则如果应用程序订阅了主题A，没有订阅主题B，那么该应用程序只能读取主题A中的数据。

​			Topic就是用来区分我们的消息的，例如超市里面水果我们需要放到水果区，生活用品要放到用品区，那么我们的用户消息就应该放到用户Topic里面，订单的消息就应该放到订单的Topic里面。![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1624437669213.png)

### Consumer Group（消费组）

​			消费者程序在读取Kafka系统主题的Topic中的数据时，通常会使用多个线程来执行，一个消费者组可以包含一个或多个消费者程序，使用多分区和多线程模式，可以极大提高读取数据的效率。

​			同一个Topic由不同的消费组进行消费，那么这个消息是不会被影响的，如果由同一个消费者组中的不同的消费者来进行消费，那么一条消息只能被同一个消费者组中的某一个消费者消费。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1624436811591.png)

### Partition（分区）

​			每一个主题（Topic）中可以有一个或者多个分区（Partition），在Kafka系统的设计中，分区是基于物理层面上的，不同的分区对应着不同的数据文件。

​			我们可以在创建Topic的时候指定分区的数量。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1624438625767.png)			

### Replication（副本）

​			在Kafka系统中，每个主题（Topic）在创建时会要求指定它的副本数，默认是副本（Replication）机制来保证Kafka分布式系统集群数据的高可用性。

​			下面我们以一个User-Topic为例子，他的分区是3，副本为2，我们现在有3个Broker，那么他们的结构图如下。

![image-20210623172340222](/Users/bigkang/Library/Application Support/typora-user-images/image-20210623172340222.png)

### Record（记录）

​			被实际写入到Kafka集群并且可以被消费者应用程序读取的数据，被称之为记录（Record），每条记录包含一个键（Key），值（Value），和时间戳（Timestamp）

### Kafka工作图

​		Kafka通过生产者Push消息到Broker代理中



​		kafka消费者从Broker代理中Pull消费数据



​		Kafka是基于Zookeeper分布式协调工具的，他是用Zookeeper来进行存储他的各个Broker代理节点，当Kafka集群中添加了一个代理节点，或者某一台代理节点出现故障时，Zookeeper服务将会通知生产者应用程序和消费者应用程序去其他的正常代理节点进行读写，因为Zookeeper的理念就是一个分布式协调工具，所以在Kafka的设计当中考虑到了分布式问题，作为去中心化的集群模式

​		![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1568283736061.png)

# Kafka的工作机制

## 生产者



## 消费者



# Kafka生产者

​		连接生产者向执行Topic发送消息

```sh
kafka-console-producer.sh --bootstrap-server 139.9.70.155:9092,139.9.80.252:9092,124.71.9.101:9092  --topic test_topic
```

​		带Key发送

```
kafka-console-producer.sh --bootstrap-server 139.9.70.155:9092,139.9.80.252:9092,124.71.9.101:9092  --topic test_topic --property parse.key=true
```





```
kafka-console-producer.sh --bootstrap-server 139.9.70.155:9092,139.9.80.252:9092,124.71.9.101:9092  --topic test_topic
```

