# 前置准备

　　首先我们需要对系统进行设置防止启动的时候导致启动失败

```sh
# 关闭swap
sudo swapoff -a
# 设置最大线程数
sysctl -w vm.max_map_count=655360
# 设置文件最大打开数
ulimit -u 65535

# 执行查看
sysctl -p && sysctl -a | grep vm.max_map_count
```

# 单机版

## 	下载镜像

　　首先先下载Elasticsearch6.0的版本

```sh
docker pull docker.io/elasticsearch:7.12.0
```

## 部署

　　创建挂载文件夹，本次采用Docker+Docker-Compose进行部署

```sh
# 创建进入部署目录
cd ~ && mkdir -p deploy && cd deploy && mkdir -p elasticsearch-7.12 && cd elasticsearch-7.12 
mkdir -p ./es-data && chown -R 1000:0 ./es-data
mkdir -p ./es-logs && chown -R 1000:0 ./es-logs
mkdir -p ./es-conf && chown -R 1000:0 ./es-conf
mkdir -p ./es-plugins && chown -R 1000:0 ./es-plugins
```

　　创建Compose文件

```json
cat > ./es-conf/elasticsearch.yml << EOF
cluster.name: elasticsearch-cluster
node.name: elasticsearch
network.bind_host: 0.0.0.0
network.publish_host: 127.0.0.1
http.port: 9200
transport.tcp.port: 9300
http.cors.enabled: true
http.cors.allow-origin: "*"
discovery.type: single-node
EOF
```

　　创建Compose文件

```sh
cat > ./docker-compose-elasticsearch-7.12.yml << EOF
version: '3.4'
services:
  elasticsearch-7.12-server:
    container_name: elasticsearch-7.12-server       # 指定容器的名称
    image: docker.io/elasticsearch:7.12.0        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: elasticsearch-7.12-server					# 主机名
    ports:
      - 9200:9200
      - 9300:9300
    environment:
      ES_JAVA_OPTS: "-Xms512m -Xmx512m"				# JVM参数
    privileged: true
    volumes: # 挂载目录
      - ./es-data:/usr/share/elasticsearch/data
      - ./es-logs:/usr/share/elasticsearch/logs
      - ./es-plugins:/usr/share/elasticsearch/plugins
      - ./es-conf/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
EOF
```

　　启动Compose文件

```sh
docker-compose -f docker-compose-elasticsearch-7.12.yml  up -d
```

　　查看日志

```sh
docker logs -f elasticsearch-7.12-server
```

# 安装Kibana

　　安装Kibana

```sh
# 创建挂载目录
cd ~ && mkdir -p deploy && cd deploy && mkdir -p kibana-7.12 && cd kibana-7.12

# 创建配置文件,修改IP地址以及密码，如果没有则删除掉username以及password
echo "server.name: kibana
server.host: \"0\"
elasticsearch.hosts: [ \"http://192.168.1.28:9200\" ]
xpack.monitoring.ui.container.elasticsearch.enabled: true
elasticsearch.username: \"bigkang\"
elasticsearch.password: \"bigkang\"" > kibana.yml

# 创建Compose文件
cat > ./docker-compose-kibana-7.12.yml << EOF
version: '3.4'
services:
  kibana-7.12:
    container_name: kibana-7.12
    image: docker.io/kibana:7.12.0
    restart: always
    hostname: kibana-7.12
    ports:
      - 5601:5601
    privileged: true
    volumes:
      - ./kibana.yml:/usr/share/kibana/config/kibana.yml
EOF

# 启动Kibana
docker-compose -f docker-compose-kibana-7.12.yml  up -d
```

# 拓展方面

## 设置密码

　　配置文件新增参数

```sh
# 新增如下xpack配置开启安全认证
xpack.security.enabled: true
# 开启安全审计
xpack.security.audit.enabled: true
# 设置为基础，其他级别需要购买
xpack.license.self_generated.type: basic
# 开启transport端口ssl认证
xpack.security.transport.ssl.enabled: true
```

　　然后重启ES

```sh
docker restart elasticsearch-7.12-server
```

　　设置用户名密码，两种方式

　　使用系统的用户设置密码

```sh
# 同样使用系统的用户可以自定义密码以及随机生成
# 系统一共有elastic, kibana, logstash_system,beats_system,apm_system,remote_monitoring_user 六个用户
# elastic 内置的超级用户。
# kibana_system 用户Kibana用来连接Elasticsearch并与之通信。
# logstash_system Logstash用户在将监控信息存储在Elasticsearch中时使用。
# beats_system Beats在Elasticsearch中存储监视信息时使用的用户。
# apm_system APM服务器在Elasticsearch中存储监视信息时使用的用户。
# remote_monitoring_user Metricbeat用户在Elasticsearch中收集和存储监视信息时使用。它具有remote_monitoring_agent和 remote_monitoring_collector内置角色。

# 1、随机生成密码
bin/elasticsearch-setup-passwords auto
# 返回如下信息

Changed password for user apm_system
PASSWORD apm_system = xndfSwjmOlL8pL9XHaoS

Changed password for user kibana_system
PASSWORD kibana_system = BbcCYpwKHFeMCVDHMmiu

Changed password for user kibana
PASSWORD kibana = BbcCYpwKHFeMCVDHMmiu

Changed password for user logstash_system
PASSWORD logstash_system = XR35KHeJw7cZzFtLuimT

Changed password for user beats_system
PASSWORD beats_system = rzObLaIWHm2CQWvLGTzY

Changed password for user remote_monitoring_user
PASSWORD remote_monitoring_user = lxBO2m1vN9voe1uK81Kl

Changed password for user elastic
PASSWORD elastic = j7q15LpSCm44W0UzHagY



# 2、自定义输入密码，需要确认，每个用户输入两次
bin/elasticsearch-setup-passwords interactive

```

　　自定义用户，密码，以及权限

```sh
# 自定义用户我们可以使用命令来进行用户的设置

# 查询所有用户
bin/elasticsearch-users list
# 内置角色：https://www.elastic.co/guide/en/elasticsearch/reference/current/built-in-roles.html

# 新增用户bigkang,密码为bigkang，设置权限为所有权限
bin/elasticsearch-users useradd bigkang -p bigkang -r kibana_dashboard_only_user,apm_system,watcher_admin,logstash_system,rollup_user,kibana_user,beats_admin,remote_monitoring_agent,rollup_admin,data_frame_transforms_admin,snapshot_user,monitoring_user,enrich_user,kibana_admin,logstash_admin,machine_learning_user,data_frame_transforms_user,machine_learning_admin,watcher_user,apm_user,beats_system,reporting_user,transform_user,kibana_system,transform_admin,transport_client,remote_monitoring_collector,superuser,ingest_admin

# 修改角色,修改bigkang角色，删除kibana_dashboard_only_user,watcher_admin，新增data_frame_transforms_admin角色
bin/elasticsearch-users roles bigkang -r kibana_dashboard_only_user,watcher_admin -a data_frame_transforms_admin

# 修改bigkang用户密码
bin/elasticsearch-users passwd bigkang

# 删除用户
bin/elasticsearch-users userdel bigkang
```

## 安装插件

　　安装IK分词器

```sh
# 下载
wget https://github.91chifun.workers.dev//https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.12.0/elasticsearch-analysis-ik-7.12.0.zip
# 解压
unzip elasticsearch-analysis-ik-7.12.0.zip -d ./ik/
```

　　安装pinyin插件

```sh
# 下载
wget https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v7.12.0/elasticsearch-analysis-pinyin-7.12.0.zip
# 解压
unzip elasticsearch-analysis-pinyin-7.12.0.zip -d ./pinyin/
```

　　安装ingest-attachment插件(推荐下载后复制到服务器上，下载非常慢)

```sh
# 下载
wget https://artifacts.elastic.co/downloads/elasticsearch-plugins/ingest-attachment/ingest-attachment-7.12.0.zip

# 解压
unzip ingest-attachment-7.12.0.zip -d ./ingest-attachment/
```

