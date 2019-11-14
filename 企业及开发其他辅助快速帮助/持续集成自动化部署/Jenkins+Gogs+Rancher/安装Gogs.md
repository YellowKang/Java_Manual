# 直接运行容器

```
创建目录
mkdir -p /docker/gogs/data
docker run -d -p 10022:22 -p 3000:3000 \
--name=gogs \
--privileged=true \
-v /docker/gogs/data/:/data/ \
gogs/gogs
```

然后访问3000端口

根据客户端提示进行配置（推荐使用mysql数据库）