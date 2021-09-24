#!/bin/bash
echo "开始部署ElasticSearch-Server"
mkdir ./es-data && chown -R 1000:0 ./es-data
mkdir ./es-logs && chown -R 1000:0 ./es-logs
sysctl -w vm.max_map_count=655360
ulimit -u 65535
sysctl -p
docker-compose -f docker-compose-elasticsearch-server.yaml up -d
sleep 1
echo "部署ElasticSearch-Server成功"

