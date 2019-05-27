# 引入依赖

我们使用Boot整合Rabbit非常简单

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```

# 编写配置

Yml版本

```
spring:
  rabbitmq:
    host: 111.67.196.127
    port: 5672
    username: bigkang
    password: bigkang
```

Properties版本

```
spring.rabbitmq.host=111.67.196.127
spring.rabbitmq.port=5672
spring.rabbitmq.username=bigkang
spring.rabbitmq.password=bigkang
```

# 编写代码

我们使用注解方式编写消费者，那么我们就不用手动去RabbitMQ创建队列了

## 编写消费者

```
@Component
public class RabbitConsumer {

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange("test"),
            key = "test",
            value = @Queue("test")
    ))
    public void MyMqtListener(String rabt){
        System.out.println("MyComputer message： " + rabt);
    }
}
```

## 编写生产者

直接向test的exchange中的test这个routingKey发送一条消息

```

@RestController
public class SendMessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @GetMapping("send")
    public String send(String message){
        rabbitTemplate.convertAndSend("test","test",message);
        return "发送成功！";
    }
    
}
```

在这我们也可以使用AmqpTemplate直接向队列发送消息

```
    @Autowired
    private AmqpTemplate amqpTemplate;
    
    @GetMapping("send")
    public String send(String message){
        amqpTemplate.convertAndSend("testOne",message);
        return "发送成功！";
    }
```

以下两种方法我们都可以使用，详细了解可以查看官方文档以及查看方法参数

然后我们通过方法url进行发送消息

<http://localhost:8080/send?message=%E4%BD%A0%E5%A5%BD> 