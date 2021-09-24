#!/bin/bash
echo "开始卸载Kibana"
docker-compose -f docker-compose-kibana.yaml stop && docker-compose -f docker-compose-kibana.yaml rm -f
sleep 2
echo "卸载Kibana成功"

