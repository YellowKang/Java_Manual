# 引入依赖

```xml
        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-client</artifactId>
            <version>4.8.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

# 消息生产者示例

```java
        // 设置生产者组
        DefaultMQProducer producer = new
                DefaultMQProducer("producer_default_group_name");
        // 指定NameServer服务器地址。
        producer.setNamesrvAddr("139.9.80.252:9876");
        // 启动实例。
        try {
            producer.start();
            // 循环发送消息
            for (int i = 0; i < 100; i++) {
                // 创建消息实例，指定主题，标记和消息正文。
                Message msg = new Message(
                        // Topic名称
                        "TopicTest" ,
                        // 标记名称
                        "TagA",
                        // 消息主体
                        ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                );
                // 调用发送消息以向一个Broker发送消息。
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            }
            // 一旦生产者实例不再使用，就会关闭。
            producer.shutdown();
        } catch (MQClientException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }
```

# 异步消息生产者示例

```java
        // 设置生产者组
        DefaultMQProducer producer = new
                DefaultMQProducer("producer_default_group_name");
        // 指定NameServer服务器地址。
        producer.setNamesrvAddr("139.9.80.252:9876");

        int messageCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        // 启动实例。
        try {
            producer.start();
            // 循环发送消息
            for (int i = 0; i < messageCount; i++) {
                // 创建消息实例，指定主题，标记和消息正文。
                Message msg = new Message(
                        // Topic名称
                        "TopicTest" ,
                        // 标记名称
                        "TagA",
                        // 消息主体
                        ("Hello RocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                );
                // 调用发送消息以向一个Broker发送消息。
               producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        countDownLatch.countDown();
                        System.out.println("发送成功");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        countDownLatch.countDown();
                        System.out.println("发送失败");
                    }
                });
            }
            countDownLatch.await(5, TimeUnit.SECONDS);
            // 一旦生产者实例不再使用，就会关闭。
            producer.shutdown();
        } catch (MQClientException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        }
```

# 消费者消费示例

```java
        // 设置消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("producer_default_group_name");
        // 指定NameServer服务器地址。
        consumer.setNamesrvAddr("139.9.80.252:9876");

        // 订阅更有更多主题来消费。
        try {
            // 消费指定的Topic和Tag的表达式
            String topic = "TopicTest";
            String tagExpression = "TagB";
            consumer.subscribe(topic, tagExpression);
            AtomicInteger atomicInteger = new AtomicInteger();
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @SneakyThrows
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for (MessageExt msg : msgs) {
                        // 打印消息
                        System.out.printf("Topic：%s ,Tag：%s ,内容：%s %n", msg.getTopic(),msg.getTags(),new String(msg.getBody(),"UTF-8"));
                    }
                    atomicInteger.getAndIncrement();
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();

            Thread.sleep(20000);
        } catch (MQClientException | InterruptedException e) {
            e.printStackTrace();
        }

```

# SpringBoot+Java自定义整合集

## 编写配置文件

```properties
rocketmq:
  # 服务NameServer地址，默认 "localhost:9876"
  nameSrvAddr: 139.9.80.252:9876
  # 默认生产者消费者组,默认 "default-botpy-group"
  defaultGroup: default-botpy-group
  # 默认生产者消费者Topic，默认 "default-botpy-topic"
  defaultTopic: default-botpy-topic
  # 默认Namespace，默认为空
  defaultNamespace: bigkang
  # 是否开启RocketMQ
  enable: true
  # 生产者配置
  producer:
    # 生产者命名空间，默认空
    namespace: botpy
    # 生产者组，默认空，不设置则采用默认
    group: botpy
    # 生产者Topic，默认空，不设置则采用默认
    topic: botpy
    # 生产者Tag，不设置则采用默认
    tag: botpy
    # 生产者默认的Topic创建队列数，默认4
    defaultTopicQueueNums: 4
    # 消息最大长度 默认1024 * 1024 * 4(4M),单位字节
    maxMessageSize: 4194304
    # 压缩消息正文阈值，即大于的消息正文将默认压缩，默认 1024 * 4(4K)，单位字节
    compressMsgBodyOverHowmuch: 4096
    # 发送消息超时时间，默认3000，单位毫秒
    sendMsgTimeout: 3000
    # 在同步模式下发送故障之前，在内部重试次数,这可能导致消息重复，需要开发人员来解决。
    retryTimesWhenSendFailed: 2
    # 在异步模式下发送故障之前，在内部重试次数,这可能导致消息重复，需要开发人员来解决。
    retryTimesWhenSendAsyncFailed: 2
  consumer:
    # 消费者命名空间，默认空
    namespace: botpy
    # 消费者组，默认空，不设置则采用默认
    group: botpy
    # 消费者Topic，默认空，不设置则采用默认
    topic: botpy
    # 消费者标签表达式，默认所有 *
    tagExpression: "*"
    # 消费者最小线程数，默认20
    consumeThreadMin: 20
    # 消费者最大线程数，默认20
    consumeThreadMax: 20
    # 动态调整线程池数的阈值
    adjustThreadPoolNumsThreshold: 100000
    # 同时最大跨度偏移量，它对顺序消耗没有影响
    consumeConcurrentlyMaxSpan: 2000
    # 消息拉取间隔（毫秒），默认0
    pullInterval: 0
    # 批量拉取数量，默认32
    pullBatchSize: 32
    # 批量消费数量最大值，默认1
    consumeMessageBatchMaxSize: 1
    # 队列级的流量控制阈值，默认1000
    pullThresholdForQueue: 1000
    # 限制队列级别的缓存大小，默认100MiB
    pullThresholdSizeForQueue: 100
    # 消息的最大时间量可以阻止消费线程
    consumeTimeout: 15
    # 关闭消费者时等待邮件消耗的最长时间, 0表示没有等待
    awaitTerminationMillisWhenShutdown: 0
    # 费者状态不正常的时候，采用定时拉取的拉取间隔，默认1000（1秒）
    pullTimeDelayMillsWhenException: 1000
    # 消费者持久化Offset间隔，默认1000 * 5，5秒
    persistConsumerOffsetInterval: 5000
```

## 编写配置类

```java
package com.test.statemachine.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author BigKang
 * @Date 2021/3/31 上午10:51
 * @Summarize RocketMQ配置类
 */
@Component
@ConfigurationProperties(prefix = "rocketmq")
@Getter
@Setter
@ToString
public class RocketMqProperties {

    /**
     * NameServer地址，与Broker中的NameServer一致
     */
    private String nameSrvAddr = "localhost:9876";

    /**
     * 默认生产者消费者组
     */
    private String defaultGroup = "default-botpy-group";

    /**
     * 默认生产者消费者Topic
     */
    private String defaultTopic = "default-botpy-topic";

    /**
     * 默认生产者消费者Namespace
     */
    private String defaultNamespace;

    /**
     * 生产者默认配置
     */
    private ProducerProperties producer = new ProducerProperties();

    /**
     * 消费者默认配置
     */
    private ConsumerProperties consumer = new ConsumerProperties();

    /**
     * 是否开启RocketMQ
     */
    private boolean enable = true;

    @Getter
    @Setter
    @ToString
    public static class ProducerProperties{
        /**
         * 生产者命名空间，不设置则不使用
         */
        private String namespace;

        /**
         * 生产者组，不设置则采用默认
         */
        private String group;

        /**
         * 生产者Topic，不设置则采用默认
         */
        private String topic;

        /**
         * 生产者标签，不设置则采用默认
         */
        private String tag = "";

        /**
         * 生产者默认的Topic创建队列数
         */
        private int defaultTopicQueueNums = 4;

        /**
         * 消息最大长度 默认1024 * 1024 * 4(4M)
         */
        private int maxMessageSize = 1024 * 1024 * 4;

        /**
         * 压缩消息正文阈值，即大于4K的消息正文将默认压缩。
         */
        private int compressMsgBodyOverHowmuch  = 1024 * 4;

        /**
         * 发送消息超时时间
         */
        private int sendMsgTimeout = 3000;

        /**
         * 在同步模式下发送故障之前，在内部重试次数,这可能导致消息重复，需要开发人员来解决。
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * 在异步模式下发送故障之前，在内部重试次数,这可能导致消息重复，需要开发人员来解决。
         */
        private int retryTimesWhenSendAsyncFailed = 2;

    }

    @Getter
    @Setter
    @ToString
    public static class ConsumerProperties{

        /**
         * 消费者命名空间，不设置则不使用
         */
        private String namespace;

        /**
         * 消费者组，不设置则采用默认
         */
        private String group;

        /**
         * 消费者Topic，不设置则采用默认
         */
        private String topic;

        /**
         * 消费者标签表达式，不设置则采用，*（所有Tag）
         */
        private String tagExpression = "*";

        /**
         * 消费者最小线程数
         */
        private int consumeThreadMin = 20;

        /**
         * 消费者最大线程数
         */
        private int consumeThreadMax = 20;

        /**
         * 动态调整线程池数的阈值
         */
        private long adjustThreadPoolNumsThreshold = 100000;

        /**
         * 同时最大跨度偏移量，它对顺序消耗没有影响
         */
        private int consumeConcurrentlyMaxSpan = 2000;

        /**
         * 消息拉取间隔
         */
        private long pullInterval = 0;

        /**
         * 批量拉取数量
         */
        private int pullBatchSize = 32;

        /**
         * 批量消费数量最大值
         */
        private int consumeMessageBatchMaxSize = 1;

        /**
         * 队列级的流量控制阈值，每个消息队列将默认为大多数1000个消息缓存
         * 考虑到这一点 {@code pullBatchSize}, 瞬时值可能超过极限
         */
        private int pullThresholdForQueue = 1000;

        /**
         * 限制队列级别的缓存大小，每个消息队列将默认为大多数100个MIB消息缓存,
         * 考虑到这一点 {@code pullBatchSize}, 瞬时值可能超过极限
         * 只有消息体测量的消息的大小，所以它不准确
         */
        private int pullThresholdSizeForQueue = 100;

        /**
         * 消息的最大时间量可以阻止消费线程
         */
        private long consumeTimeout = 15;

        /**
         * 关闭消费者时等待邮件消耗的最长时间, 0表示没有等待
         */
        private long awaitTerminationMillisWhenShutdown = 0;

        /**
         * 消费者状态不正常的时候，采用定时拉取的拉取间隔（默认一秒）
         */
        private int pullTimeDelayMillsWhenException = 1000;

        /**
         * 消费者持久化Offset间隔
         */
        private int persistConsumerOffsetInterval = 1000 * 5;
    }

    /**
     * 获取生产者Topic
     * @return
     */
    public String getProducerTopic(){
        if (StringUtils.isNotEmpty(producer.topic)) {
           return producer.topic;
        }else {
            return defaultTopic;
        }
    }

    /**
     * 获取生产者Group
     * @return
     */
    public String getProducerGroup(){
        if (StringUtils.isNotEmpty(producer.group)) {
            return producer.group;
        }else {
            return defaultGroup;
        }
    }

    /**
     * 获取生产者Namespace
     * @return
     */
    public String getProducerNamespace(){
        if (StringUtils.isNotEmpty(producer.namespace)) {
            return producer.namespace;
        }else {
            return defaultNamespace;
        }
    }


    /**
     * 获取消费者Topic
     * @return
     */
    public String getConsumerTopic(){
        if (StringUtils.isNotEmpty(consumer.topic)) {
            return consumer.topic;
        }else {
            return defaultTopic;
        }
    }

    /**
     * 获取消费者Group
     * @return
     */
    public String getConsumerGroup(){
        if (StringUtils.isNotEmpty(consumer.group)) {
            return consumer.group;
        }else {
            return defaultGroup;
        }
    }

    /**
     * 获取消费者Namespace
     * @return
     */
    public String getConsumerNamespace(){
        if (StringUtils.isNotEmpty(consumer.namespace)) {
            return consumer.namespace;
        }else {
            return defaultNamespace;
        }
    }
}

```

## 编写工具类

```java
package com.test.statemachine.utils;

import com.test.statemachine.properties.RocketMqProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * @Author BigKang
 * @Date 2021/3/31 下午2:32
 * @Summarize RocketMQ工具类
 */
@Component
@Getter
@Slf4j
public class RocketMqUtil {

    /**
     * 默认搜索Tag表达式
     */
    private static final String ALL_TAGS_EXPRESSION = "*";

    /**
     * RocketMQ配置文件
     */
    private final RocketMqProperties rocketMqProperties;

    /**
     * 默认消息生产者
     */
    private DefaultMQProducer defaultProducer;

    @Autowired
    public RocketMqUtil(RocketMqProperties rocketMqProperties) {
        this.rocketMqProperties = rocketMqProperties;
        // 根据配置决定是否初始化默认生产者
        if (rocketMqProperties.isEnable()) {
            // 根据配置获取默认生产者
            defaultProducer = getProducer();
            log.info("begin RocketMQ init default producer!!!");
            try {
                // 启动默认生产者
                defaultProducer.start();
            } catch (MQClientException e) {
                log.error("RocketMQ init default producer failure!!!");
                e.printStackTrace();
            }
            log.info("RocketMQ init default producer success!!!");
        } else {
            log.info("RocketMQ cancel initialization default producer!!!");

        }
    }

    /**
     * 获取消费者实例(重载)
     *
     * @return
     */
    public DefaultMQPushConsumer getConsumer() {
        return getConsumer(
                rocketMqProperties.getConsumerTopic());
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param topic 订阅主题
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String topic) {
        return getConsumer(
                rocketMqProperties.getConsumerGroup(),
                topic);
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param group 消费者组
     * @param topic 订阅主题
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String group, String topic) {
        String tagExpression = null;
        // 设置默认Tag表达式
        if (StringUtils.isNotEmpty(rocketMqProperties.getConsumer().getTagExpression())) {
            tagExpression = rocketMqProperties.getConsumer().getTagExpression();
        }
        return getConsumer(
                group,
                topic,
                tagExpression);
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param group         消费者组
     * @param topic         订阅主题
     * @param tagExpression 标签表达式
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String group, String topic, String tagExpression) {
        return getConsumer(
                group,
                topic,
                tagExpression,
                rocketMqProperties.getConsumer().getPullBatchSize());
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param group         消费者组
     * @param topic         订阅主题
     * @param tagExpression 标签表达式
     * @param pullBatchSize 批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String group, String topic, String tagExpression, int pullBatchSize) {
        return getConsumer(
                rocketMqProperties.getConsumerNamespace(),
                group,
                topic,
                tagExpression,
                pullBatchSize);
    }

    /**
     * 获取消费者实例(重载)
     *
     * @param namespace     消费者命名空间
     * @param group         消费者组
     * @param topic         订阅主题
     * @param tagExpression 标签表达式
     * @param pullBatchSize 批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String namespace, String group, String topic, String tagExpression, int pullBatchSize) {
        return getConsumer(
                namespace,
                group,
                topic,
                tagExpression,
                rocketMqProperties.getConsumer().getConsumeThreadMin(),
                rocketMqProperties.getConsumer().getConsumeThreadMax(),
                pullBatchSize);
    }


    /**
     * 获取消费者实例(重载)
     *
     * @param namespace        消费者命名空间
     * @param group            消费者组
     * @param topic            订阅主题
     * @param tagExpression    标签表达式
     * @param consumeThreadMin 消费者最小的线程数
     * @param consumeThreadMax 消费者最大的线程数
     * @param pullBatchSize    批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String namespace, String group, String topic, String tagExpression, int consumeThreadMin, int consumeThreadMax, int pullBatchSize) {
        return getConsumer(
                namespace,
                group,
                topic,
                tagExpression,
                consumeThreadMin,
                consumeThreadMax,
                rocketMqProperties.getConsumer().getPullInterval(),
                pullBatchSize);
    }

    /**
     * 获取消费者实例
     *
     * @param namespace        消费者命名空间
     * @param group            消费者组
     * @param topic            订阅主题
     * @param tagExpression    标签表达式
     * @param consumeThreadMin 消费者最小的线程数
     * @param consumeThreadMax 消费者最大的线程数
     * @param pullInterval     拉取时间间隔
     * @param pullBatchSize    批量拉取数量
     * @return
     */
    public DefaultMQPushConsumer getConsumer(String namespace, String group, String topic, String tagExpression, int consumeThreadMin, int consumeThreadMax, long pullInterval, int pullBatchSize) {
        enableCheck();
        DefaultMQPushConsumer consumer = null;
        // 如果命名空间不为空才进行设置
        if (StringUtils.isNotEmpty(namespace)) {
            consumer = new DefaultMQPushConsumer(namespace, group);
        } else {
            consumer = new DefaultMQPushConsumer(group);
        }
        // 设置NameServer地址
        consumer.setNamesrvAddr(rocketMqProperties.getNameSrvAddr());
        // 设置消费者最小线程数
        consumer.setConsumeThreadMin(consumeThreadMin);
        // 设置消费者最大线程数
        consumer.setConsumeThreadMax(consumeThreadMax);
        // 设置拉取间隔
        consumer.setPullInterval(pullInterval);
        // 设置拉取批量数量
        consumer.setPullBatchSize(pullBatchSize);
        // Topic为空则先不订阅
        if (StringUtils.isNotEmpty(topic)) {
            try {
                // Tag表达式为空则订阅所有
                if (StringUtils.isNotEmpty(tagExpression)) {
                    consumer.subscribe(topic, tagExpression);
                } else {
                    consumer.subscribe(topic, ALL_TAGS_EXPRESSION);
                }
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }
        return consumer;
    }

    /**
     * 获取生产者实例
     *
     * @return
     */
    public DefaultMQProducer getProducer() {
        return getProducer(rocketMqProperties.getProducerGroup());
    }

    /**
     * 获取生产者实例
     *
     * @param group 生产者组
     * @return
     */
    public DefaultMQProducer getProducer(String group) {
        return getProducer(rocketMqProperties.getProducerNamespace(), group);
    }

    /**
     * 获取生产者实例
     *
     * @param namespace 生产者命名空间
     * @param group     生产者组
     * @return
     */
    public DefaultMQProducer getProducer(String namespace, String group) {
        enableCheck();
        DefaultMQProducer producer = null;
        // 如果命名空间不为空才进行设置
        if (StringUtils.isNotEmpty(namespace)) {
            producer = new DefaultMQProducer(namespace, group);
        } else {
            producer = new DefaultMQProducer(group);
        }
        // 设置NameServer地址
        producer.setNamesrvAddr(rocketMqProperties.getNameSrvAddr());
        // 设置Topic队列数量
        producer.setDefaultTopicQueueNums(rocketMqProperties.getProducer().getDefaultTopicQueueNums());
        // 设置消息最大长度
        producer.setMaxMessageSize(rocketMqProperties.getProducer().getMaxMessageSize());
        // 设置压缩消息正文阈值
        producer.setCompressMsgBodyOverHowmuch(rocketMqProperties.getProducer().getCompressMsgBodyOverHowmuch());
        // 设置消息发送超时时间
        producer.setSendMsgTimeout(rocketMqProperties.getProducer().getSendMsgTimeout());
        // 设置同步模式下发送故障，重试次数
        producer.setRetryTimesWhenSendFailed(rocketMqProperties.getProducer().getRetryTimesWhenSendFailed());
        // 设置异步模式下发送故障，重试次数
        producer.setRetryTimesWhenSendAsyncFailed(rocketMqProperties.getProducer().getRetryTimesWhenSendAsyncFailed());
        return producer;
    }

    /**
     * 发送消息(重载方法)
     *
     * @param msg
     */
    public SendResult sendMsg(String msg) {
        checkMessage(msg);
        Message message = strToMessage(msg);
        return sendMsg(message);
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public SendResult sendMsg(Message msg) {
        enableCheck();
        SendResult result = null;
        try {
            result = defaultProducer.send(msg);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送异步消息
     *
     * @param msg
     * @param sendCallback
     */
    public void sendAsyncMsg(String msg, SendCallback sendCallback) {
        checkMessage(msg);
        Message message = strToMessage(msg);
        sendAsyncMsg(message, sendCallback);
    }

    /**
     * 发送异步消息
     *
     * @param msg
     * @param sendCallback
     */
    public void sendAsyncMsg(Message msg, SendCallback sendCallback) {
        enableCheck();
        try {
            defaultProducer.send(msg, sendCallback);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符串转Message
     *
     * @param msg
     * @return
     */
    public Message strToMessage(String msg) {
        Message message = new Message(rocketMqProperties.getProducerTopic(), rocketMqProperties.getProducer().getTag(), msg.getBytes());
        return message;
    }

    /**
     * 检查消息
     *
     * @param msg
     */
    private void checkMessage(String msg) {
        Assert.isTrue(StringUtils.isNotEmpty(msg), "RocketMQ send async message is null!!!");
    }

    /**
     * 是否启用检查
     */
    private void enableCheck() {
        Assert.isTrue(rocketMqProperties.isEnable(), "RocketMq is not enable,use ${rocketmq.enable}=true Open!!!");
    }
}

```



## SpringBoot+RocketMQ日志问题

​		SpringBoot启动类添加如下

```java
 				// 设置Client日志
 				System.setProperty(ClientLogger.CLIENT_LOG_USESLF4J,"true");
        SpringApplication application = new SpringApplication(StatemachineApplication.class);
        application.run(args);
```

## 使用

### SpringBoot

​		注入Rocker工具类

```java
    @Autowired
    private RocketMqUtil rocketMqUtil;
```

​		生产者简单示例,使用默认组以及命名空间等等信息发送，默认Topic，NameSpace，Group都是botpy

```java
        for (int i = 0; i < 100; i++) {
            SendResult result = rocketMqUtil.sendMsg("第二次测试"+i);
        }
```

​		自定义信息生产者示例

```java
        // 定义namespace，和group，创建消费者
        String namespace = "test";
        String group = "test";
        // 根据信息获取生产者
        DefaultMQProducer producer = rocketMqUtil.getProducer(namespace, group);
        try {
            // 生产者启动
            producer.start();
            // 定义topic，tag和tag
            String topic = "test_topic";
            String tag = "tag";
            String msg = "hello world";
            // 创建消息对象
            Message message = new Message(topic, tag, msg.getBytes());
            // 获取返回对象
            SendResult result = producer.send(message);
            if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                System.out.println("发送成功！！！");
            }
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }
```

​		消费者

```java
        // 根据配置获取默认消费者
				DefaultMQPushConsumer consumer = rocketMqUtil.getConsumer();
				// 注册消息监听
        consumer.registerMessageListener(new MessageListenerConcurrently(){
            @SneakyThrows
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    // 打印消息
                    System.out.printf("Topic：%s ,Tag：%s ,内容：%s %n", msg.getTopic(),msg.getTags(),new String(msg.getBody(),"UTF-8"));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
				// 启动消费者
        consumer.start();
```

### Java

​		使用方式与SpringBoot一致，不可以注入，直接手动创建配置

```java
        RocketMqProperties properties = new RocketMqProperties();
        properties.setNameSrvAddr("139.9.80.252:9876");
        RocketMqUtil rocketMqUtil = new RocketMqUtil(properties);
```

## 线程池问题

​		用户可以使用自定义线程池拓展

​		生产者异步线程池设置

```java
        // 设置异步发送线程池
				producer.setAsyncSenderExecutor();
				// 设置回调线程池
				producer.setCallbackExecutor();
```





# SpringBoot官方整合

## 引入依赖

```

```

