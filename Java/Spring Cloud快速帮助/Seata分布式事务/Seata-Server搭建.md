# Docker安装

## 拉取镜像

```
docker pull 
```

## 启动容器

### 单机版

#### 简单启动

```
docker run --name seata-server \
        -p 8091:8091 \
        -e SEATA_IP=114.67.80.169 \
        -e SEATA_PORT=8091 \
        seataio/seata-server
```

#### 生产启动

​		创建文件夹

```sh
mkdir -p ~/deploys/seata && cd ~/deploys/seata
```

​		创建Compose文件

```sh
cat > ./docker-compose-seata-server.yml << EOF
version: '3.4'
services:
  seata-server:
    container_name: seata-server       # 指定容器的名称
    image: seataio/seata-server:1.4.0         # 指定镜像和版本
    restart: always  # 自动重启
    hostname: seata-server
    ports:
      - 8091:8091
    environment:
      SEATA_PORT: 8091
      SEATA_IP: 192.168.1.28
      SEATA_CONFIG_NAME: file:/root/seata-config/registry
      STORE_MODE: file
      SERVER_NODE: 1
    volumes:
      - ./seata-config:/root/seata-config
      - ./seata-data:/seata-server/sessionStore
    privileged: true
EOF
```

​		创建配置文件

```sh
mkdir seata-config

# 创建注册配置
cat > ./seata-config/registry.conf << EOF
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "file"
  loadBalance = "RandomLoadBalance"
  loadBalanceVirtualNodes = 10

  nacos {
    application = "seata-server"
    serverAddr = "127.0.0.1:8848"
    group = "SEATA_GROUP"
    namespace = ""
    cluster = "default"
    username = ""
    password = ""
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    application = "default"
    weight = "1"
  }
  redis {
    serverAddr = "localhost:6379"
    db = 0
    password = ""
    cluster = "default"
    timeout = 0
  }
  zk {
    cluster = "default"
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  consul {
    cluster = "default"
    serverAddr = "127.0.0.1:8500"
  }
  etcd3 {
    cluster = "default"
    serverAddr = "http://localhost:2379"
  }
  sofa {
    serverAddr = "127.0.0.1:9603"
    application = "default"
    region = "DEFAULT_ZONE"
    datacenter = "DefaultDataCenter"
    cluster = "default"
    group = "SEATA_GROUP"
    addressWaitTime = "3000"
  }
  file {
    name = "/root/seata-config/file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  type = "file"

  nacos {
    serverAddr = "127.0.0.1:8848"
    namespace = ""
    group = "SEATA_GROUP"
    username = ""
    password = ""
  }
  consul {
    serverAddr = "127.0.0.1:8500"
  }
  apollo {
    appId = "seata-server"
    apolloMeta = "http://192.168.1.204:8801"
    namespace = "application"
    apolloAccesskeySecret = ""
  }
  zk {
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  etcd3 {
    serverAddr = "http://localhost:2379"
  }
  file {
    name = "/root/seata-config/file.conf"
  }
}
EOF

# 创建文件配置
cat > ./seata-config/file.conf << EOF
## transaction log store, only used in seata-server
## 事务日志存储，仅仅适用于seata-server
store {
  ## 存储模式store mode: file、db、redis
  mode = "file"

  ## 文件存储配置
  file {
    ## 存储的位置文件夹，默认容器内/seata-server/sessionStore，工作目录为seata-server所以不写根目录
    dir = "sessionStore"
    # 分支会话大小，如果超过了先尝试压缩lockkey，仍然超过抛出异常
    maxBranchSessionSize = 16384
    # 全局会话大小，如果超过则抛出异常
    maxGlobalSessionSize = 512
    # 文件缓冲区大小，如果超过分配新的缓冲区
    fileWriteBufferCacheSize = 16384
    # 恢复时批处理读大小
    sessionReloadReadSize = 100
    # 异步（async）、同步（sync），刷盘策略，
    flushDiskMode = async
  }

  ## 数据库存储配置
  db {
    ## javax.sql的实现数据源，例如DruidDataSource(druid)/BasicDataSource(dbcp)/HikariDataSource(hikari)等。
    datasource = "druid"
    ## 数据库类型
    ## mysql/oracle/postgresql/h2/oceanbase etc.
    dbType = "mysql"
    ## 数据库驱动
    driverClassName = "com.mysql.jdbc.Driver"
    url = "jdbc:mysql://127.0.0.1:3306/seata"
    user = "mysql"
    password = "mysql"
    minConn = 5
    maxConn = 100
    globalTable = "global_table"
    branchTable = "branch_table"
    lockTable = "lock_table"
    queryLimit = 100
    maxWait = 5000
  }

  ## redis存储配置
  redis {
    host = "127.0.0.1"
    port = "6379"
    password = ""
    database = "0"
    minConn = 1
    maxConn = 10
    maxTotal = 100
    queryLimit = 100
  }
}
EOF
```

​		启动命令

```sh
docker-compose -f docker-compose-seata-server.yml up -d
```



​		参数解释

```sh
      SEATA_PORT: 8091       			# seata端口号
      SEATA_IP: 192.168.1.28						# seata注册IP时的IP地址
      SEATA_CONFIG_NAME: "file:/root/seata-config/registry" # seata配置文件名称
      STORE_MODE: file						# 可选，指定seata-server的事务日志存储方式，支持db 和 file, 默认是 file.
      SERVER_NODE: 1							# 可选，用于指定seata-server节点ID, 如 1,2,3..., 默认为 1
      SEATA_ENV: dev							# 可选，指定 seata-server 运行环境, 如 dev, test 等. 服务启动时会使用 registry-dev.conf 这样的配置.
```

#### 修改Nacos存储

​		修改registry.conf

```
registry {
  # 修改类型为Nacos
  type = "nacos"
  loadBalance = "RandomLoadBalance"
  loadBalanceVirtualNodes = 10

```

