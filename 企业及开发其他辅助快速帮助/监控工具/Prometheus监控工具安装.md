# 搭建收集数据node

```
mkdir /root/prom-node
cd /root/prom-node
echo "version: '3'
services:
  prom-node:
    container_name: prom-node
    image: prom/node-exporter
    restart: always
    privileged: true
    network_mode: host
    volumes:
     - /docker/prom-node/proc:/host/proc:ro
     - /docker/prom-node/sys:/host/sys:ro
     - /docker/prom-node/rootfs:/rootfs:ro" > docker-compose.yaml
```

然后启动

```
docker-compose up -d
```

# 搭建Prometheus

创建挂载目录配置文件

```
mkdir -p /docker/prometheus/
cd /docker/prometheus/
echo "global:
  scrape_interval:     60s
  evaluation_interval: 60s
 
scrape_configs:
  - job_name: prometheus
    static_configs:
      - targets: ['localhost:9090']
        labels:
          instance: prometheus
 
  - job_name: linux
    static_configs:
      - targets: ['114.67.80.169:9100']
        labels:
          instance: localhost" > prometheus.yml
```

创建docker-compose

```
mkdir /root/prometheus
cd /root/prometheus
echo "version: '3'
services:
  prometheus:
    container_name: prometheus
    image: prom/prometheus
    restart: always
    privileged: true
    ports:
     - 9090:9090
    volumes:
     - /docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml" > docker-compose.yaml
```

# 启动Grafana

```
mkdir /docker/grafana/storage
chmod 777 -R /docker/grafana/storage
```

创建docker-compose文件

```
mkdir /root/grafana
cd /root/grafana
echo "version: '3'
services:
  grafana:
    container_name: grafana
    image: grafana/grafana
    restart: always
    privileged: true
    ports:
     - 3000:3000
    volumes:
     - /docker/grafana/storage:/var/lib/grafana" > docker-compose.yaml
```



Prometheus + Grafana

