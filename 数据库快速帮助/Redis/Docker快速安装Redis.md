# 单机版

## 快速启动

```
docker run -d --name redis -p 6379:6379 redis
```

## 生产启动

首先创建挂载文件

```
mkdir -p /docker/redis/data
```

然后我们启动并且设置密码

```
docker run -d \
--name redis \
-p 6379:6379 \
-v /docker/redis/data:/data \
redis --requirepass 'bigkang' 

```



# 环境清理

## 单机版

停止容器以及删除容器

```
docker stop redis
docker rm redis
```

