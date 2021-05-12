#!/bin/bash
echo "开始部署Kibana"
docker-compose -f docker-compose-kibana.yaml up -d
sleep 1
echo "部署Kibana成功"

