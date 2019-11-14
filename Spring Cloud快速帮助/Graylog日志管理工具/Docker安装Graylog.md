# 安装Es

光剑挂载目录

```
mkdir -p /docker/elasticsearch/data
chmod -R 777 /docker/elasticsearch/data
```

启动es容器

```
docker run -d \
-p 9202:9200 \
-p 9203:9300 \
-v /docker/elasticsearch/data:/usr/share/elasticsearch/data \
--name elasticsearch6.7 docker.io/elasticsearch:6.7.0
```

# 安装Mongo

```sh
#首先创建文件夹用于挂载目录

mkdir -p /docker/mongo/{conf,data}
#赋予权限
chmod 777 /docker/mongo/conf
chmod 777 /docker/mongo/data
#然后直接启动容器
docker run --name mongo -d \
-p 27017:27017 \
--privileged=true \
-v /docker/mongo/conf:/data/configdb \
-v /docker/mongo/data:/data/db \
docker.io/mongo:latest \
--auth
```

创建用户以及创建数据库

创建root

```sh
#-----进入容器
docker exec -it mongo bash
#-----进入mongo
mongo
#-----选中admin数据库
use admin
#-----创建用户，root用户
db.createUser({user:"root",pwd:"root",roles:[{role:'root',db:'admin'}]})
```

重新登录

```sh
use admin;
db.auth("root","root")
```

新建mongo库

```sh
use graylog;
#创建用户
db.createUser({user:"graylog",pwd:"graylog",roles:[{role:'dbOwner',db:'graylog'}]})
```

退出后登录graylog库然后验证

```
mongo

use graylog

db.auth("graylog","graylog")
```

如果出现1表示成功

# 安装graylog

下面我们将ip写成自己的ip，下面示例统一采用192.168.1.177

我们采用Docker-Compose进行部署

选个文件夹新建文件 docker-compose.yaml,内容输入如下，请修改es地址以及mongo地址和密码，以及web地址，相应ip为自己的ip

```sh
version: '3'
services:
  graylog:
    image: graylog/graylog:3.1
    environment:
      - GRAYLOG_PASSWORD_SECRET=somepasswordpepper
      - GRAYLOG_ROOT_USERNAME=admin
      - GRAYLOG_ROOT_PASSWORD_SHA2=8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
      - GRAYLOG_HTTP_EXTERNAL_URI=http://192.168.1.177:9000/
      - GRAYLOG_ELASTICSEARCH_HOSTS=http://192.168.1.177:9202
      - GRAYLOG_MONGODB_URI=mongodb://graylog:graylog@192.168.1.177:27017/graylog
      - GRAYLOG_ROOT_TIMEZONE=Asia/Shanghai
      - GRAYLOG_WEB_ENDPOINT_URI=http://192.168.1.177:9000/api
      - GRAYLOG_WEB_LISTEN_URI=http://0.0.0.0:9000/
      - GRAYLOG_REST_LISTEN_URI=http://0.0.0.0:9000/api
    ports:
      - 9000:9000
      - 1514:1514
      - 1514:1514/udp
      - 12201:12201
      - 12201:12201/udp
```

然后启动

```
docker-compose up
```

然后查看是否有异常若无异常，直接访问http://192.168.1.177:9000，然后登陆，用户名：admin，密码：admin

然后结束，直接start

```
docker-compose start
```



修改密码直接修改用户名称，然后采用SHA-256加密密码，然后复制进去

在线加密地址：http://encode.chahuo.com/