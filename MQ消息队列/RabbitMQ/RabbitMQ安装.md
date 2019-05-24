这里我们直接采用Docker一键安装

```
docker run -d \
--hostname test-rabbitmq \
--name rabbitmq \
-e RABBITMQ_DEFAULT_USER=bigkang \
-e RABBITMQ_DEFAULT_PASS=bigkang \
-p 15672:15672 \
-p 5672:5672 \
rabbitmq:3-management
```