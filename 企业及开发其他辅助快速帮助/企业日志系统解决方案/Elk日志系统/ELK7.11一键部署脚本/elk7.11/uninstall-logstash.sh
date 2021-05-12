#!/bin/bash
echo "开始卸载Logstash"
docker-compose -f docker-compose-logstash.yaml stop && docker-compose -f docker-compose-logstash.yaml rm -f
sleep 2
echo "卸载Logstash成功"

