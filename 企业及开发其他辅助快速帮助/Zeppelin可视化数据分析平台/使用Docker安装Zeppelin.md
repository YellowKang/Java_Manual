# 下载镜像

```
docker pull apache/zeppelin:0.8.0
```

# 运行镜像

根据实际使用情况进行参数配置

```
docker run -d \
-p 18089:8080 \
--name zeppelin \
-v  /docker/zeppelin/logs:/logs \
-v /docker/zeppelin/notebook:/notebook \
-e HOST_IP=0.0.0.0 \
-e ZEPPELIN_MEM:" -Xms256m -Xmx256m" \
-e ZEPPELIN_LOG_DIR="/logs" \
-e ZEPPELIN_NOTEBOOK_DIR="/notebook" \
--restart=always \
apache/zeppelin:0.8.0
```

