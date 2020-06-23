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