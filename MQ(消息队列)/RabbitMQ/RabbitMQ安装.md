# Docker一键安装单节点RabbitMQ

​		这里我们直接采用Docker一键安装

```sh

#运行容器命令
docker run -d --name rabbitmq1 --network host 
-v /usr/local/rabbitmq/data:/var/lib/rabbitmq 
-v /usr/local/rabbitmq/conf:/etc/rabbitmq 
-v /usr/local/rabbitmq/log:/var/log/rabbitmq rabbitmq


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

# Docker-Compose版本

```sh
# 首先我们创建部署目录
mkdir ~/deploys/rabbitmq && cd ~/deploys/rabbitmq

# 创建挂载文件夹
mkdir {data,conf,log}

# 创建配置文件
# 创建MQ配置文件
cat > conf/rabbitmq.conf << EOF
loopback_users.guest = false
# 用户名密码
default_pass = bigkang
default_user = bigkang
# Rabbit端口号以及管理端口号
listeners.tcp.default = 5672
management.tcp.port = 15672
EOF
# 创建插件配置
cat > conf/enabled_plugins << EOF
[rabbitmq_management,rabbitmq_prometheus].
EOF
# 创建conf.d
mkdir conf/conf.d




# 创建Compose文件
cat > docker-compose.yaml << EOF
version: '3'
services:
  rabbitmq:
    container_name: rabbitmq
    image: rabbitmq:3.8.17-management
    restart: always
    privileged: true
    hostname: rabbitmq
    volumes:
      - ./data:/var/lib/rabbitmq 
      - ./conf:/etc/rabbitmq 
      - ./log:/var/log/rabbitmq
    ports:
      - 15672:15672
      - 5672:5672
EOF
```

# Docker-Compose搭建集群

