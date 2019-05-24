# 引入依赖

```
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
```

# 编写配置

```
spring:
  kafka:
    bootstrap-servers: 39.108.158.33:9092
    consumer:
      group-id: mygroup
```

# 创建生产者

使用kafkaTemplate向test2这个topic随便简单发送一条消息

```
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    
    @GetMapping("send")
    public String send(){
        kafkaTemplate.send("test2","123");
        return "成功";
    }
```

# 创建消费者

消费test2这个topic，注意需要使用@Component注解将他标注成组件

```
@Component
public class ConsumerTest {

    @KafkaListener(topics = {"test2"})
    public void ok(String message){
        System.out.println("收到消息" + message);
    }
}
```
# 配置文件详解

```
# 指定kafka server的地址，集群配多个，中间，逗号隔开
spring.kafka.bootstrap-servers=127.0.0.1:9092


# 写入失败时，重试次数。当leader节点失效，一个repli节点会替代成为leader节点，此时可能出现写入失败，
# 当retris为0时，produce不会重复。retirs重发，此时repli节点完全成为leader节点，不会产生消息丢失。
spring.kafka.producer.retries=0

# 每次批量发送消息的数量,produce积累到一定数据，一次发送
spring.kafka.producer.batch-size=16384

# produce积累数据一次发送，缓存大小达到buffer.memory就发送数据
spring.kafka.producer.buffer-memory=33554432


#procedure要求leader在考虑完成请求之前收到的确认数，用于控制发送记录在服务端的持久化，其值可以为如下：
#acks = 0 如果设置为零，则生产者将不会等待来自服务器的任何确认，该记录将立即添加到套接字缓冲区并视为已发送。在这种情况下，无法保证服务器已收到记录，并且重试配置将不会生效（因为客户端通常不会知道任何故障），为每条记录返回的偏移量始终设置为-1。
#acks = 1 这意味着leader会将记录写入其本地日志，但无需等待所有副本服务器的完全确认即可做出回应，在这种情况下，如果leader在确认记录后立即失败，但在将数据复制到所有的副本服务器之前，则记录将会丢失。
#acks = all 这意味着leader将等待完整的同步副本集以确认记录，这保证了只要至少一个同步副本服务器仍然存活，记录就不会丢失，这是最强有力的保证，这相当于acks = -1的设置。
#可以设置的值为：all, -1, 0, 1
spring.kafka.producer.acks=1

# 指定消息key和消息体的编解码方式
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
```

