

# 安装Docker

```sh
yum -y remove docker  docker-common docker-selinux docker-engine
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
sudo yum -y install docker-ce
systemctl start docker.service
systemctl start docker
systemctl enable docker.service
systemctl enable docker
```

# Docker-Shell

```sh
docker run -p 8388:8388 \
--name shadowsocks \
-e PASSWORD=bigkang \
--restart=always \
--privileged=true \
-d shadowsocks/shadowsocks-libev、



docker run -p 8388:8388 \
--name shadowsocks \
-e PASSWORD=bigkang \
--restart=always \
--privileged=true \
-d shadowsocks/shadowsocks-libev



docker run -d \
-p 8090:80 \
--name nginx-server \
--restart=always \
--privileged=true \
-v /docker/nginx/nginx.conf:/etc/nginx/nginx.conf \
-v /docker/nginx/conf/:/etc/nginx/conf.d/ \
-v /docker/nginx/logs:/var/log/nginx nginx:1.16
```

# Docker-Compose

```sh
cat > ./docker-compose.yaml << EOF
version: '3.4'
services:
  shadowsocks:
    container_name: shadowsocks       # 指定容器的名称
    image: shadowsocks/shadowsocks-libev         # 指定镜像和版本
    restart: always  # 自动重启
    hostname: shadowsocks
    ports:
      - 8388:8388
      - 8388/udp:8388/udp
    environment:
      PASSWORD: "bigkang"
EOF


```

