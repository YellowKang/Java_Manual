# Docker一键安装单节点RabbitMQ

这里我们直接采用Docker一键安装

```sh
docker run -d \
--name rabbitmq \
-e RABBITMQ_DEFAULT_USER=bigkang \
-e RABBITMQ_DEFAULT_PASS=bigkang \
-p 15672:15672 \
-p 5672:5672 \
rabbitmq:3-management
```

```properties
RABBITMQ_DEFAULT_USER				//RabbitMQ用户名
RABBITMQ_DEFAULT_PASS				//RabbitMQ密码
15672												//RabbitMQ图形化WEB端
5672												//服务通信接口
```

