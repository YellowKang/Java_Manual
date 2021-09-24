# ELK一键部署文件目录说明

```bash
├── README.md																			# 说明文档
├── docker-compose-elasticsearch-server.yaml			# elasticsearch服务端docker-compose脚本文件
├── docker-compose-kibana.yaml										# kibana图形化工具docker-compose脚本文件
├── docker-compose-logstash.yaml									# logstash日志收集工具
├── es-conf																				# Elasticsearch配置文件目录
│   ├── elasticsearch.keystore
│   ├── elasticsearch.yml													# Elasticsearch配置文件
│   ├── jvm.options																# JVM启动参数
│   ├── jvm.options.d															# JVM启动参数拓展目录
│   ├── log4j2.file.properties										# 日志文件配置
│   ├── log4j2.properties													# 日志配置
│   ├── role_mapping.yml													# 角色映射文件
│   ├── roles.yml																	# 角色文件
│   ├── users																			# 用户文件（存储命令方式创建的用户）
│   └── users_roles																# 用户角色绑定文件
├── es-plugins																		# Elasticsearch插件目录
├── kibana-conf						
│   └── kibana.yml																# Kibana配置文件
├── logstash-conf
│   ├── conf.d
│   │   └── tcp-log.conf													# logstash收集配置TCP文件
│   └── logstash.ymlf															# logstash收集配置文件
├── logstash-logs																	# logstash日志文件目录
├── start-all.sh																	# 启动部署all所有组件脚本（一键部署）
├── start-kibana.sh																# 启动部署Kibana组件脚本
├── start-logstash.sh															# 启动部署Logstash组件脚本
├── start-server.sh																# 启动部署Elasticsearch组件脚本
├── uninstall-all.sh															# 卸载并且清空all所有组件脚本（一键卸载）
├── uninstall-kibana.sh														# 卸载并且清空Kibana组件脚本
├── uninstall-logstash.sh													# 卸载并且清空Logstash组件脚本
└── uninstall-server.sh														# 卸载并且清空Elasticsearch组件脚本
```

# 部署方式

​		首先查询是否创建Docker网络

```bash
docker network ls
```

​		没有则创建botpy网络，有则删除重定义IP地址或者修改Compose文件网络

```bash
docker network create --subnet=172.18.0.0/24 botpy
```

​		一键启动

```sh
sudo ./start-all.sh
```

# 初始化信息

​		Elasticsearch用户名密码,可以自行新增添加修改

```
用户名: vosp
密码: vosp123
```

​		Elasticsearch用户名密码

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

# 服务信息

|         服务         |   虚拟IP    |          端口号          |        容器名        |        主机名        |
| :------------------: | :---------: | :----------------------: | :------------------: | :------------------: |
| elasticsearch-server | 172.18.0.35 | 9200:9200<br />9300:9300 | elasticsearch-server | elasticsearch-server |
| elasticsearch-kibana | 172.18.0.36 |           5601           | elasticsearch-kibana | elasticsearch-kibana |
|       logstash       | 172.18.0.37 | 9400:9400<br />9600:9600 |       logstash       |       logstash       |

# 修改设置

​		Elasticsearch

```sh
# 配置文件地址
es-conf/elasticsearch.yml # 修改Elasticsearch参数启动配置等等
```

​		Kibana

```sh
# 配置文件地址
kibana-conf/kibana.yml # 修改Elasticsearch服务地址，以及Kibana连接Es的用户名密码
```

​		Logstash

```sh
# 配置文件地址
logstash-conf/conf.d/tcp-log.conf  # 修改Logstash端口收集以及推送方式
```

# 一键卸载（注意会删除文件清空数据）

​		一键卸载

```sh
sudo ./uninstall-all.sh
```

