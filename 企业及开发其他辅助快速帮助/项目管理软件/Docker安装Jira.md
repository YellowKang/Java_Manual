# Jira镜像

​		将上面文件夹中的破解包，或者手动下载的Agent放入文件

​		Baidu网盘地址：[点击进入](https://pan.baidu.com/s/1AucTmTNPSG85hhWF7mkIcQ)   提取码：`n4ug`

​		或者参考一下博客   https://zhile.io/2018/12/20/atlassian-license-crack.html

​		以及安装流程博客：https://www.jianshu.com/p/b95ceabd3e9d

```dockerfile
# 创建文件夹开始构建Jira镜像
mkdir -p ~/image/jira && cd ~/image/jira

# 写入Dockerfile文件
cat > ./Dockerfile << EOF
FROM cptactionhank/atlassian-jira-software:8.1.0
USER root
# 将代理破解包加入容器
COPY "atlassian-agent.jar" /opt/atlassian/jira/
# 设置启动加载代理包
RUN echo 'export CATALINA_OPTS="-javaagent:/opt/atlassian/jira/atlassian-agent.jar ${CATALINA_OPTS}"' >> /opt/atlassian/jira/bin/setenv.sh
EOF
```

​		然后开始构建镜像

```sh
docker build -t bigkang/jira:8.1.0 .
```

​		创建启动Compose文件

```sh
cat > ./docker-compose-jira-server.yml << EOF
version: '3.4'
services:
  jira:
    container_name: jira       # 指定容器的名称
    image: bigkang/jira:8.1.0         # 指定镜像和版本
    restart: always  # 自动重启
    hostname: jira
    ports:
      - 8088:8080
    # environment:
    # volumes:
    privileged: true
EOF
```

​		启动Compose文件

```sh
docker-compose -f docker-compose-jira-server.yml up -d
```

​		访问地址：  IP地址:8088端口号

​		然后访问页面配置语言设置中文，然后点击我将设置它自己，点击下一步

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620896859339.png)

​		然后设置内置数据库存储数据，选择MySQL或者其他数据库配置主机端口以及用户名密码

​		MySQL配置文件需要修改为如下：

​		没有的设置需要新增，有的设置修改为如下

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

<img src="https://blog-kang.oss-cn-beijing.aliyuncs.com/1620899924850.png" style="zoom:67%;" />

​		然后进行初始化，初始化完成后选择访问的IP地址（建议使用域名），以及是否允许用户注册

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620900827833.png)

​		然后开始破解

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620900853764.png)

​		看到如下界面，我们进入容器开始进行激活

```bash
# 进入容器
docker exec -it jira bash

# 进入Jar包目录
cd /opt/atlassian/jira/

# 获取激活码,注意修改地址，以及服务器ID，-s
java -jar atlassian-agent.jar -d -m test@test.com -n BAT -p jira -o http://127.0.0.1:8080 -s B8YH-5S1B-AHLK-HO8A

# 执行后返回如下
====================================================
=======        Atlassian Crack Agent         =======
=======           https://zhile.io           =======
=======          QQ Group: 30347511          =======
====================================================

Your license code(Don't copy this line!!!): 

AAABSA0ODAoPeJxtUMFugkAUvO9XbNIzuAtBgYSkCqSagjZFm/S40qdsCgvuLkb69UWgl8bkXd7Me
5OZedoXLU5Zh6mNKfHp3Lcofkn32CIWRaEEpnktIqYhuCMGcQxqo/jKynZgghMrFaAIVC55MyAHU
fKKa/jCJc9BKMDHDhdaN8qfzX4KXoLJa7STZya4GkXubE9S2zM9c0FM6ji+S1wX5bU4mSzX/AqBl
i2gsBa63+OU8TI48vM3E2fFb8+Xi5nX1XifaSY1yMnaACWjk33XwJZVEIS7NI3fw80yQb2S0CCYy
CG+NVx2U1jXM8iiHzT9bqIg2URZvDUSOreIR4jnUssmKAN5BdnTK/dzbTgZXRnLdfJqrHfu8u/5s
fJbK/OCKfhf79TbB0h1b8caM2zb6ghydzqoHg8MinovwQM/U0VDzqmhX7VYmvEwLAIUAm76+g4Uo
Lvc7kC4/snFecg8grYCFB/D62JYSKMnCeAnZ6EiRC3lwDBeX02g8
```

​		填入秘钥，

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620901026310.png)

​		然后设置用户名密码即可

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1620901284463.png)



