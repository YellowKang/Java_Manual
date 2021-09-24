#!/bin/bash
echo "开始部署Logstash"
docker-compose -f docker-compose-logstash.yaml up -d
sleep 1
echo "部署Logstash成功"

