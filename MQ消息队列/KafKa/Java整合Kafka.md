# 引入依赖

```xml
        <!-- 根据自己的Kafka版本选择 -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>2.3.0</version>
        </dependency>
```

# Kafka配置

## Client通用

```java
    private static Properties getProperties(){
        Properties properties =  new Properties();
        // KafkaClient连接端通用配置
        // 配置Kafka连接地址
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,"139.9.70.155:9092,139.9.80.252:9092,124.71.9.101:9092");
        // 控制客户端如何使用DNS查找
        // 如果设置为use_all_dns_ips，则当查找返回主机名的多个IP地址时,将在连接失败之前尝试全部连接。适用于引导服务器和公告服务器
        // 如果值为resolve_canonical_bootstrap_servers_only，则将解析每个条目并将其扩展为规范名称列表。
        properties.put(CommonClientConfigs.CLIENT_DNS_LOOKUP_CONFIG,"use_all_dns_ips");
        // 以毫秒为单位的时间段，在此之后，即使没有看到任何分区Leader更改也可以强制刷新元数据以主动发现任何新的代理或分区。
        properties.put(CommonClientConfigs.METADATA_MAX_AGE_CONFIG,10000);
        // 发送数据时要使用的TCP发送缓冲区（SO_SNDBUF）的大小。如果值为-1，将使用操作系统默认值（默认-1）。
        properties.put(CommonClientConfigs.SEND_BUFFER_CONFIG,-1);
        // 读取数据时要使用的TCP接收缓冲区（SO_RCVBUF）的大小。如果值为-1，将使用操作系统默认值（默认-1）。
        properties.put(CommonClientConfigs.RECEIVE_BUFFER_CONFIG,-1);
        // 发出请求时传递给服务器的ID字符串。其目的是通过允许在服务器端请求日志中包含逻辑应用程序名称，从而能够跟踪IP/端口以外的请求源。
        properties.put(CommonClientConfigs.CLIENT_ID_CONFIG,"client_bigkang_localohst");
        // 该客户端的机架标识符。这可以是任何字符串值，用于指示此客户端的物理位置。它与代理配置“ broker.rack”相对应
        properties.put(CommonClientConfigs.CLIENT_RACK_CONFIG,"bigkang");
        // 在尝试重新连接到给定主机之前所等待的基本时间。这就避免了重复地以一个紧凑的循环连接到主机。这种退让适用于客户端对代理的所有连接尝试
        properties.put(CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG,10000);
        // 重新连接到反复连接失败的代理时等待的最大时间(以毫秒为单位),如果提供，每台主机的备份将在每次连续连接失败时呈指数增长，达到这个最大值。计算回退增加后，增加20%的随机抖动，以避免连接风暴
        properties.put(CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_CONFIG,10000);
        // 设置大于0的值将导致客户端重新发送任何带有潜在瞬时错误的失败请求。
        properties.put(CommonClientConfigs.RETRIES_CONFIG,1);
        // 在尝试重试对给定主题分区的失败请求之前所等待的时间。这避免了在某些失败场景下以一个紧凑的循环重复发送请求。
        properties.put(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG,1000);
        // 在此配置指定的毫秒数之后关闭空闲连接（毫秒）。
        properties.put(CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_CONFIG,100000);
        // 该配置控制客户端等待的最大时间,表示请求的响应。如果在超时之前没有收到响应失效客户端将重新发送请求，如果有必要或请求失败，如果重试已耗尽
        properties.put(CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG,10000);
        return properties;
    }
```

## Admin

```java
		// Admin的所有配置都是基于Client通用的,参照上方即可
```

## 生产者

```

```



## 消费者

```java
   private static Properties getProperties(){
        Properties properties =  new Properties();

        // BOOTSTRAP_SERVERS_CONFIG，跟连接端一样配置不做过多讲解
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"139.9.70.155:9092,139.9.80.252:9092,124.71.9.101:9092");
        // KafkaConsumer消费者配置
        // 配置GroupID（消费者），标识此用户所属的用户组的唯一字符串。如果消费者使用subscribe(topic)组管理功能，或者使用基于kafka的偏移管理策略，这个属性是必需的。
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"kc_group");
        // 由最终用户提供的消费者实例的唯一标识符，只允许非空字符串，如果设置了，则使用者被视为一个静态成员，这意味着在任何时候，消费者组中只允许有一个具有此ID的实例
        // 这可以与更大的会话超时结合使用，以避免由于暂时不可用而导致的组重新平衡,如果没有设置，消费者将作为动态成员加入组，这是传统的行为
        properties.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "bigkang-instance");
        // 单个poll()调用中返回的最大记录数,每次拉取的数据条数
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,100);
        // 使用时，两次调用poll()之间的最大延迟,消费者组织管理。这就为用户空闲的时间设置了上限在获取更多记录之前
        // 如果poll()在这个超时过期之前没有被调用，那么消费者则认为失败，因此该组将重新平衡，以便将分区重新分配给另一个成员
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,20000);
        // 当使用时，用于检测消费者故障的超时卡夫卡的集团管理设施。消费者发送周期性的心跳来表示它的活力到代理。如果在此会话超时到期之前代理没有接收到心跳，然后中介将把这名消费者从群体中移除，并开始重新平衡。
        // 注意值必须在代理配置中<code>group.min.session.timeout配置的允许范围内。group.max.session.timeout.ms
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,20000);
        // 心跳之间的预期时间到消费者当使用Kafka的组管理工具时,心跳用于确保消费者会议保持活跃，并在新消费者加入或离开时促进重新平衡。
        // 该值必须设置为低于session.timeout.ms但通常不应该设置更高的比这个值的三分之一还多。它可以调整得更低，以控制正常再平衡的预期时间
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,15000);
        // 如果为真，消费者的偏移量将定期在后台提交
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
        // 如果enable.auto.commit被设置为true，消费者的偏移量自动提交到Kafka的频率(以毫秒为单位)。
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,1000);
        // 分区分配策略的类名，当使用组管理时，客户端将使用该策略在使用者实例中分配分区所有权，默认range
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, Collections.singletonList(RangeAssignor.class));
        // earliest 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
        // latest 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据(默认)
        // none topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");

        // 服务器为获取请求应该返回的最小数据量。,如果可用数据不足，则请求将在响应请求之前等待大量数据积累。默认设置为1字节意味着，只要有一个字节的数据可用，或者fetch请求在等待数据到达时超时，就会响应fetch请求。
        // 将此值设置为大于1的值将导致服务器等待更大数量的数据积累，这可以略微提高服务器吞吐量，但代价是增加一些额外的延迟。
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,1);

        // 服务器为获取请求应该返回的最大数据量,如果读取的第一个非空分区中的第一个记录批大于则由消费者批量获取记录此值时，记录批仍将返回，以确保消费者能取得进展。因此，这不是一个绝对最大值。
        // 代理接受的最大记录批处理大小是通过max.message.bytes定义的字节(代理配置)或message.max.bytes注意，消费者并行执行多个读取操作。
        // 默认 50 * 1024 * 1024 = 50m
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,50 * 1024 * 1024);

        // 如果没有足够的数据来立即满足fetch.min.bytes给出的要求，服务器在响应fetch请求之前阻塞的最大时间(毫秒)
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,1000);

        // 服务器每个分区的最大数据量将返回。记录由消费者批量获取。如果第一个记录批处理中的第一个非空分区的fetch大于这个限制，批次仍将被退回，以确保消费者能取得进展。
        // 最大记录批大小通过<code>message.max定义被代理接受。代理接受的最大记录批处理大小是通过max.message.bytes定义的字节(代理配置)或message.max.bytes注意，消费者并行执行多个读取操作。
        // 默认1 * 1024 * 1024 = 1m
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,1 * 1024 * 1024);

        // 自动检查所消耗记录的CRC32。这确保不会发生在线或磁盘上的消息损坏。这种检查会增加一些开销，因此在寻求极高性能的情况下可能会禁用它,默认true
        properties.put(ConsumerConfig.CHECK_CRCS_CONFIG,true);

        // Key的序列化方式（重点）,实现org.apache.kafka.common.serialization.Deserializer接口的key的反序列化类。
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
        // Value的序列化方式（重点）,实现org.apache.kafka.common.serialization.Deserializer接口的key的反序列化类。
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");

        // 为可能阻塞的消费者api指定超时(以毫秒为单位)。对于不显式接受timeout参数的所有消费者操作，此配置被用作默认超时。
        properties.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG,1000);
        // 作为拦截器使用的类列表。实现<code>org.apache.kafka.clients.consumer.ConsumerInterceptor</code>接口允许你拦截(并可能改变)记录消费者接收默认情况下，没有拦截器。
        // properties.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG,"");
        // 匹配订阅模式的内部主题是否应该是被排除在订阅之外。总是可以显式地订阅内部主题。默认true
        properties.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG,true);
        // 控制如何读取以事务方式写入的消息。
        // 如果设置为read_committed， consumer.poll()将只返回已提交的事务消息。
        // 如果设置为read_uncommitted(默认值)，consumer.poll()将返回所有消息，甚至是事务消息
        // 这一计划已经流产。
        // 在任何一种模式中，非事务性消息都将无条件返回。消息总是以偏移量顺序返回因此，在read_committed模式中consumer.poll()将只返回最后一个稳定偏移量(LSO)之前的消息，该偏移量小于第一个打开事务的偏移量。
        // 特别是在正在进行的交易的信息之后出现的任何信息将被扣留，直到相关交易完成。因此,read_committed当有航班交易时，消费者将无法读取到高水位。此外，当read_committed时，seekToEnd方法将还回LSO
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_uncommitted");
        // 允许在代理上自动创建主题订阅或分配一个主题。订阅的主题只有在true后才会自动创建broker允许使用auto.create.topics,启用代理配置。此配置必须是当使用大于0.11.0的broker时，设置为‘false’”;
        properties.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG,true);
        return properties;
    }
```



# 生产者

​		创建一个生产者

```

```



# 消费者

​		监听指定Topic的消息

```java
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(getProperties());

        consumer.subscribe(Arrays.asList("test_topic"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
```



# Admin管理

## Topic

```java
    private static Properties getProps(){
        Properties props =  new Properties();
        props.put("bootstrap.servers", "192.168.1.15:9092,192.168.1.16:9092,192.168.1.17:9092");
        return props;
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建Admin连接
        AdminClient adminClient = AdminClient.create(getProps());
        // 获取Topic结果集
        ListTopicsResult result = adminClient.listTopics();
        // 获取所有Topic
        Collection<TopicListing> Topics = result.listings().get();
        for (TopicListing topic : Topics) {
            // 打印Topic名称以及是否内部Topic
            System.out.println("TopicName:" + topic.name() + "\t" + "isInternal:" + topic.isInternal());
        }
    }
```

​		删除Topic

```java
        // 创建Admin连接
        AdminClient adminClient = AdminClient.create(getProps());
        // 获取Topic结果集
        DeleteTopicsResult result = adminClient.deleteTopics(Arrays.asList("test_topic"));
```



## ConsumerGroups

​		查询所有ConsumerGroup

```java
    private static Properties getProps(){
        Properties props =  new Properties();
        props.put("bootstrap.servers", "192.168.1.15:9092,192.168.1.16:9092,192.168.1.17:9092");
        return props;
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建Admin连接
        AdminClient adminClient = AdminClient.create(getProps());
        // 获取Group结果集
        ListConsumerGroupsResult groups = adminClient.listConsumerGroups();
        // 获取所有的ConsumerGroup,正常以及异常状态的
        Collection<ConsumerGroupListing> consumerGroups = groups.all().get();
        // 遍历所有的ConsumerGroup
        for (ConsumerGroupListing consumerGroup : consumerGroups) {
            System.out.println("GroupId:"+consumerGroup.groupId()+"\t\t" + "是否简单的ConsumerGroup:" + consumerGroup.isSimpleConsumerGroup());
        }
    }
```



