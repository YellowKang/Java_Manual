#!/bin/bash
echo "开始卸载ElasticSearch-Server"
docker-compose -f docker-compose-elasticsearch-server.yaml stop && docker-compose -f docker-compose-elasticsearch-server.yaml rm -f
rm -rf ./es-data && rm -rf ./es-logs
sleep 2
echo "卸载ElasticSearch-Server成功"

