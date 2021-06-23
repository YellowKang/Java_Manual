# Confluence镜像

​		将上面文件夹中的破解包，或者手动下载的Agent放入文件

​		Baidu网盘地址：[点击进入](https://pan.baidu.com/s/1AucTmTNPSG85hhWF7mkIcQ)   提取码：`n4ug`

​		或者参考一下博客   https://zhile.io/2018/12/20/atlassian-license-crack.html

​		以及安装流程博客：https://www.jianshu.com/p/b95ceabd3e9d

```dockerfile
# 创建文件夹开始构建Confluence镜像
mkdir -p ~/image/confluence && cd ~/image/confluence

# 写入Dockerfile文件
cat > ./Dockerfile << EOF
FROM cptactionhank/atlassian-confluence:7.7.2

USER root
# 将代理破解包加入容器
COPY "atlassian-agent.jar" /opt/atlassian/confluence/

# 设置启动加载代理包
RUN echo 'export CATALINA_OPTS="-javaagent:/opt/atlassian/confluence/atlassian-agent.jar ${CATALINA_OPTS}"' >> /opt/atlassian/confluence/bin/setenv.sh
EOF
```

​		然后开始构建镜像

```sh
docker build -t bigkang/confluence:7.7.2 .
```

​		创建启动Compose文件

```sh
cat > ./docker-compose-confluence-server.yml << EOF
version: '3.4'
services:
  confluence:
    container_name: confluence       # 指定容器的名称
    image: bigkang/confluence:7.7.2         # 指定镜像和版本
    restart: always  # 自动重启
    hostname: confluence
    ports:
      - 8099:8090
    # environment:
    # volumes:
    privileged: true
EOF
```

​		启动Compose文件

```sh
docker-compose -f docker-compose-confluence-server.yml up -d
```

​		访问地址：  IP地址:8099端口号

​		然后访问页面配置语言设置中文，然后点击产品安装

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620907447669.png)

​		然后选择安装拓展功能

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620907509845.png)

​		然后进入到授权页面

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620907550954.png)

​		看到如下界面，我们进入容器开始进行激活		

```sh
# 进入容器
docker exec -it confluence bash

# 进入Jar包目录
cd /opt/atlassian/confluence/

# 获取激活码,注意修改地址，以及服务器ID，-s
java -jar atlassian-agent.jar -d -m test@test.com -n BAT -p conf -o http://139.9.70.155:8099 -s B3YH-5BJ5-20DD-ZXGF

# 获取questions插件激活码
java -jar atlassian-agent.jar -d -m test@test.com -n BAT -p questions -o http://139.9.70.155:8099 -s B3YH-5BJ5-20DD-ZXGF

# 获取tc（团队）插件激活码
java -jar atlassian-agent.jar -d -m test@test.com -n BAT -p tc -o http://139.9.70.155:8099 -s B3YH-5BJ5-20DD-ZXGF


# 执行后返回如下
====================================================
=======        Atlassian Crack Agent         =======
=======           https://zhile.io           =======
=======          QQ Group: 30347511          =======
====================================================

Your license code(Don't copy this line!!!): 

AAABVg0ODAoPeJxtUF1rgzAUfc+vEPasTRRnFYS1Kls3bbdpx9hb6m5nQKMkscz9+sUPGIzC5ULuu
Tnn3HNTVL2R0cEgjkHsAK8DxzPus8KwsU1QJIAq1vKYKgjHiYldkzgoudC6n5DwTGsJKAZZCtZNk
yOvWcMUfBo1K4FLME6DUSnVyWC1+qlYDRZr0UF8Uc7kTDKiGiSOb/mWhy3iuqtgjX0flS0/W7RU7
AKhEj2gqOVKv5OMsjpUINXd2KyybebdXFGhQCy+plE62yiGDva0gTA6ZFnyGu02KdIsXAGnvITku
2NiWC5d+yb2dKHl7y4O012cJ3szJbc29rHnEV0Y5SAuIDS8JU+u+f6xic2Hx+zFfMLJcVbXjDQCP
nqaDlgYr8s996KsqIT/gS9JvoGQY142yvvTX+IT7yS275sTiMP5KPVmaBKkLYdXbC8pTnFsN8Uvd
/WmoDAsAhRLbAv0pxykqsiwJ13v9/SfXO8zPQIUKxdq4F9HiuldMHAo4rVzQGjA3yk=X02gs
```

​		然后设置内置数据库存储数据，选择MySQL或者其他数据库配置主机端口以及用户名密码

​		MySQL配置文件需要修改为如下：

​		没有的设置需要新增，有的设置修改为如下,并且给他创建一个数据库confluence，编码为utf8,!!!!!!一定是utf8不能是utf8mb4

```properties
[mysqld]
character-set-server=utf8mb4
innodb_default_row_format=DYNAMIC
innodb_large_prefix=ON
innodb_file_format=Barracuda
innodb_log_file_size=2G
sql_mode = NO_AUTO_VALUE_ON_ZERO
default-storage-engine=INNODB
collation-server=utf8mb4_bin
max_allowed_packet=256M
transaction-isolation=READ-COMMITTED
binlog_format=row
log-bin-trust-function-creators =1
[client]
default-character-set=utf8mb4
[mysql]
default-character-set=utf8mb4
```

​		输入完激活码并且创建完数据库后选择confluence是单机还是集群

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620978424614.png)

​		我们这里选择单机，然后设置数据库,注意mysql配置文件修改！！！以及utf8编码！！！！

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620978504445.png)