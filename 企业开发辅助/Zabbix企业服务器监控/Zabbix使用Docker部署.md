# 拉取镜像

拉取mysql镜像

```sh
docker pull mysql:5.7.19
```

拉取zabbix服务端镜像

```sh
docker pull zabbix/zabbix-server-mysql 
```

拉取web-nginx端镜像

```sh
docker pull zabbix/zabbix-web-nginx-mysql
```

拉取zabbix客户端镜像

```sh
docker pull zabbix/zabbix-agent
```

# 运行容器

运行mysql,创建密码为topcom123的mysql，并且创建一个zabbix数据库

```sh
docker run -d -p 13307:3306 \
--name zabbix-mysql \
-e MYSQL_ROOT_PASSWORD=topcom123 \
--privileged=true \
-v /docker/mysql/data:/var/lib/mysql \
-v /docker/mysql/conf/my.cnf:/etc/mysql/conf.d/mysql.cnf \
mysql:5.7.19
```

运行zabbix-mysql服务端，并且link到mysql

```sh
docker run  --name zabbix-server-mysql --hostname zabbix-server-mysql \
--link zabbix-mysql:mysql \
-e DB_SERVER_HOST="mysql" \
-e MYSQL_USER="root" \
-e MYSQL_DATABASE="zabbix" \
-e MYSQL_PASSWORD="topcom123" \
-v /etc/localtime:/etc/localtime:ro \
-v /docker/zabbix/alertscripts:/usr/lib/zabbix/alertscripts \
-v /docker/zabbix/externalscripts:/usr/lib/zabbix/externalscripts \
-p 10051:10051 \
-d \
zabbix/zabbix-server-mysql 
```

运行zabbix-nginx，并且link到mysql以及server

```sh
docker run --name zabbix-web-nginx --hostname zabbix-web-nginx \
--link zabbix-mysql:mysql \
--link zabbix-server-mysql:zabbix-server \
-e DB_SERVER_HOST="mysql" \
-e MYSQL_USER="root" \
-e MYSQL_PASSWORD="topcom123" \
-e MYSQL_DATABASE="zabbix" \
-e ZBX_SERVER_HOST="zabbix-server" \
-e PHP_TZ="Asia/Shanghai" \
-p 7000:80 \
-p 8443:443 \
-d \
zabbix/zabbix-web-nginx-mysql
```

然后访问7000端口

然后我们登录Zabbix，用户名Admin，密码zabbix，



--------------不太熟悉Zabbix使用不太会，以及客户端配置





我们找到配置，然后找到主机，然后添加配置（20台全部配置。。。。。。。靠），选择地址然后选择服务器为linux server

然后

运行zabbix节点,例如我们有20台服务器分别从20-39，zabbixserver部署在28上面，我们分别去每一台上执行命令,注意修改本机hostname

```sh
docker run --name zabbix-agent \
-e ZBX_HOSTNAME="10.18.81.39" \
-e ZBX_SERVER_HOST="10.18.81.28" \
-e ZBX_TYPE="agent" \
--restart=always \
-e ZBX_SERVER_PORT=10051 \
-p 10050:10050 \
--privileged \
-d zabbix/zabbix-agent
```

