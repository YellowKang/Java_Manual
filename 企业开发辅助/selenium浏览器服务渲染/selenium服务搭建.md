# 安装部署



```
# 创建 zk compose 文件
cat << EOF > docker-compose.yaml
version: '3'
services:
  selenium:
    image: selenium/standalone-chrome:118.0
    container_name: selenium
    shm_size: 2G
    ports:
      - 4444:4444
      - 7900:7900
    environment:
      - START_XVFB=false
      - SE_NODE_OVERRIDE_MAX_SESSIONS=true
      - SE_NODE_MAX_SESSIONS=3
      - JAVA_OPTS=-XX:ActiveProcessorCount=3
      - SE_NODE_SESSION_TIMEOUT=60
EOF
```

