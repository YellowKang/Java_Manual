# Es常用操作

## elasticsearchdump 使用

### Linux导出

```
--山东
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_2*/ --output=sd_month2.json  --searchBody  '{"query":{"bool":{"filter":[{"range":{"pubTime":{"gte":1548950400000,"lt": 1551369600000}}},{"match_phrase":{"content":"山东"}}]}}}' &
--河北   
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=hb_month617.json  --searchBody  '{"query":{"bool":{"filter":[{"range":{"pubTime":{"gte":1559318400000,"lt": 1561910400000}}},{"match_phrase":{"content":"河北"}}]}}}' &
--新疆
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=hb_month624.json  --searchBody  '{"query":{"bool":{"filter":[{"range":{"pubTime":{"gte":1560614400000,"lt": 1561910400000}}},{"match_phrase":{"content":"新疆"}}]}}}' &
```

### windows导出

```
--山东
 elasticdump --input=http://192.168.1.14:20269/yuqing_2019_2*/ --output=E:\卓越讯通\煤矿项目\山东煤监局\sd_month2.json  --searchBody  {\"query\":{\"bool\":{\"filter\":[{\"range\":{\"pubTime\":{\"gte\":1548950400000,\"lt\":1551369600000}}},{\"match_phrase\":{\"content\":\"山东\"}}]}}} &
--新疆
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=E:\卓越讯通\煤矿项目\新疆\xj_month621.json      --searchBody  {\"query\":{\"bool\":{\"filter\":[{\"range\":{\"pubTime\":{\"gte\":1559318400000,\"lt\":1561910400000}}},{\"match_phrase\":{\"content\":\"新疆\"}}]}}} &
--河北
elasticdump --input=http://192.168.1.14:20269/yuqing_2019_6*/ --output=E:\卓越讯通\煤矿项目\河北煤矿\hb_month625.json    --searchBody  {\"query\":{\"bool\":{\"filter\":[{\"range\":{\"pubTime\":{\"gte\":1561305600000,\"lt\":1561910400000}}},{\"match_phrase\":{\"content\":\"河北\"}}]}}} &
```

### 导入（linux与windows相同）

```
--山东
elasticdump --output=http://10.212.1.33:20269/yuqing_2019_2/ --input=E:\卓越讯通\煤矿项目\山东煤监局\sd_month2.json --type=data    
 --后面这个有的版本加了报错  --headers='{"content-type": "application/json"}'
--河北
elasticdump --output=http://10.224.0.86:19200/yuqing_2019_6/ --input=E:\卓越讯通\煤矿项目\河北煤矿\hb_month625.json --type=data 
--新疆
elasticdump --output=http://172.35.0.33:20369/yuqing_2019_6/ --input=E:\卓越讯通\煤矿项目\新疆\xj_month621.json --type=data
```

## Head工具

### head启动 

```
head目录: setsid /node_modules/grunt/bin/grunt server &      
```

### head配置文件

_site/app.js 配置es集群， Gruntfile.js配置head端口

### head创建模板

```
PUT http://192.168.1.14:20269/_template/template_name
 {
 "template":indexName,
 mapping
 }
 ,在mapping最上边新增一行"template":indexName  即可   
```

### head删除模板

```
delete  http://192.168.1.14:20269/_template/template_name   
删除指定名称的模板    template_name为*即可删除所有模板
```

### 根据mapping创建索引

```
put  http://192.168.1.14:20269/indexname  {{mapping}}
```

### 删除索引

```
curl -XDELETE 'http://host.IP.address:9200/logstash-*'  
或者通过head删除
http://10.224.0.86:19200/indexName  DELETE
```

## 查询所有数据总数

```
通过head：  http://192.168.1.14:20269/_cat/count?v   或者
curl -s -XGET http://192.168.1.14:20269/_cat/count?v
```

## 定时删除数据脚本

```
#!/bin/bash 
dat="\""`date --date='15 days ago' "+%Y-%m-%d %H:%M:%S"`":000\""

dat1="\"2010-10-10 19:45:04:050\""

curl -XPOST http://192.168.186.229:9200/staap-log/staap-log_t/_delete_by_query -d "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"C_T\":{\"lt\":$dat}}}]}}}"  -H "Content-Type: application/json"

curl -XPOST http://192.168.186.229:9200/non-format-log/non-format-log_t/_delete_by_query -d "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"C_T\":{\"lt\":$dat}}}]}}}"  -H "Content-Type: application/json"

curl -XPOST http://192.168.186.229:9200/format-log/format-log_t/_delete_by_query -d "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"time\":{\"lt\":$dat}}}]}}}"  -H "Content-Type: application/json"

```

## 批量删除（delete_by_query)

```
POST /format-log/format-log_t/_delete_by_query  {"query": {"range": {"time": {"gte": "2016-01-01 00:00:00:000","lte": "2016-01-02 00:05:05:670"}}}}
  
POST /staap_event-201806/staap_event_t/_delete_by_query {"query": {"term": {"EVENT_TYPE_CODE": "48"}}}

POST /staap_event-201806/staap_event_t/_delete_by_query {"query": {"bool": {"must": [{"match_all": { }}]}}}

POST /staap_monitor/staap_monitor_process_status/_delete_by_query
{
    "query": {
        "term": {
           "EQU_IP": {
              "value": "10.236.207.123"
           }
        }
    }
}
```

## 批量删除（delete)

```
DELETE /non-format-log/non-format-log_t/_query  {"query": {"range": {"C_T":  {"gte": "2016-01-01 00:00:00:000","lte": "2016-01-02 23:59:59:000"}}}}
```

## 单条删除

```
DELETE /staap_event/staap_event_t/AV8zhzcFKC5LagHsJELl
```

## 批量插入

```
POST /staap_event/staap_event_t/_bulk
{ "index": {}}
{"EVENT_ID" : "15123158612731210304", "OCCUR_TIME":"2017-10-19 23:59:59:031", "DST_IP":"10.236.207.20","SRC_IP":"27.0.0.1","EVENT_TYPE_NAME":"审核日志清除检测","EVENT_TYPE_CODE":"33-34","OCCUR_COUNT":785, "BUSS_SYSTEM_NAME" : "BOSS应急_主机", "EQU_IP":"10.236.207.20", "ASSET_NAME":"pdb_XATSORA_10.236.204.16_d","MASTER_ACCOUNT":"ty_lj", "BUSS_SYSTEM_CODE":"18401-", "SEVERITY": "middle","BUSS_SYSTEM_CODE":"428-"}
{ "index": {}}
{"EVENT_ID" : "261691886123234511405", "OCCUR_TIME":"2017-10-19 07:42:50:031", "DST_IP":"23.20.0.1","SRC_IP":"27.0.0.1","EVENT_TYPE_NAME":"错误日志检测","EVENT_TYPE_CODE":"33-37","OCCUR_COUNT":414, "BUSS_SYSTEM_NAME" : "OA_主机", "EQU_IP":"10.236.207.19", "ASSET_NAME":"pdb_XATSORA_10.236.204.18_d","MASTER_ACCOUNT":"ty_lj", "SEVERITY": "middle","BUSS_SYSTEM_CODE":"429-"}
{ "index": {}}
{"EVENT_ID" : "241291886523345122406", "OCCUR_TIME":"2017-10-19 08:42:50:031", "DST_IP":"10.236.207.120","SRC_IP":"27.0.0.1","EVENT_TYPE_NAME":"试图使用已过期的账户登陆","EVENT_TYPE_CODE":"33-44","OCCUR_COUNT":254, "BUSS_SYSTEM_NAME" : "BASS_DB", "EQU_IP":"10.236.207.120", "ASSET_NAME":"pdb_XATSORA_10.236.204.19_d","MASTER_ACCOUNT":"ty_lj", "SEVERITY": "high","BUSS_SYSTEM_CODE":"432-"}
{ "index": {}}
{"EVENT_ID" : "2813915891233415166407", "OCCUR_TIME":"2017-10-19 09:42:50:031", "DST_IP":"10.236.207.18","SRC_IP":"27.0.0.1","EVENT_TYPE_NAME":"访问权限修改检测","EVENT_TYPE_CODE":"33-46","OCCUR_COUNT":477, "BUSS_SYSTEM_NAME" : "BOMC_DB", "EQU_IP":"10.236.207.18", "ASSET_NAME":"pdb_XATSORA_10.236.204.21_d","MASTER_ACCOUNT":"ty_lj", "SEVERITY": "low","BUSS_SYSTEM_CODE":"434-"}
```

## 单条插入日志

```
POST /staap_event/staap_event_t
{"OCCUR_TIME": "2017-10-20 11:16:29:000","AVG_PACKET_FLOW": 47474,"DST_IP": "45.2.2.0","SRC_IP": "74.24.8.0","EVENT_TYPE_NAME": "网络DOS和DDOS攻击检测","RAW_MSG_ID": "51542427561,51542427528,51542427429,51542427594,51542427495,51542427462","SUM_BYTE_FLOW": 4317,"EVENT_TYPE_CODE": "38-39","AVG_BYTE_FLOW": 14178,"SUM_PACKET_FLOW": 7723,"MAX_PACKET_FLOW": 1235,"ALERT_ID": "5503563257468452103171233","ALERT_GRADE": "3","END_TIME": "2017-10-20 11:17:29:000","COLLECT_TIME": "1970-01-01 08:00:00:000","EVENT_ID": "1512224165392111122","CREATE_TIME": "1970-01-01 08:00:00:000","SEVERITY": "low","MAX_BYTE_FLOW": 12344,"ABNORMAL_FLOW_ATTACK_TYPE": "DNS RESPONSE FLOOD","OCCUR_COUNT": 8241}
```

## 单机多节点部署

1）.将原来es复制一份

2）.修改node.name、 http.port、path.data、 path.logs

3）.node.max_local_storage_nodes：2  （每台机器运行的节点数）

## 根据查询批量修改

```
{"script":{"inline":"ctx._source.EVENT_TYPE_CODE='net_attack:other_attack'","lang":"painless"},"query":{"match":{"EVENT_TYPE_CODE":"27-32"}}}
```

## 根据查询新增字段

```
{"script":{"inline":"ctx._source.SYSTEM_NAME=ctx._source.BUSS_SYSTEM_NAME","lang":"painless"},"query":{"bool":{"must":[{"exists":{"field":"BUSS_SYSTEM_NAME"}}]}}}
```

## 查询未分配分片信息

```
curl -XGET 10.174.66.210:9200/_cat/shards?h=index,shard,prirep,state,unassigned.reason| grep UNASSIGNED
```

## 强制分配分片（可能会导致数据丢失）

```
curl -XPOST '10.174.66.210:9200/_cluster/reroute?retry_failed=5&pretty' -d '{"commands":[{"allocate_stale_primary": {"index": "staap_event-201809","shard": 7,"node": "es02", "accept_data_loss" : true}}]}' 
```

## 获取分片不分配原因

```
http://10.174.66.211:9200/_cluster/allocation/explain
```

## 批量插入文件

```
curl -X POST "192.168.186.87:9200/_bulk" -H 'Content-Type: application/json' --data-binary @test1.txt
```

## 修改副本个数

```
PUT index01/_settings  {"number_of_replicas": 2}
```

## 聚合排序--javaapi

```
.addAggregation(AggregationBuilders.terms("attackIp").field("DST_IP")
                        .order(Terms.Order.aggregation("attackCount", "value", false))                 .subAggregation(AggregationBuilders.sum("attackCount").field("OCCUR_COUNT"))
                ).setFrom(0).setSize(10).get();   
```

## 聚合返回结果类型--javaapi

```
terms ： StringTerms 
min：	IntervalMin
max：	IntervalMax
count: 	IntervalCount
```

## elasticdump 安装

先安装node.js   官网： https://nodejs.org/en/download/  选择版本 下载 安装 
执行命令：  nmp install
			nmp install elasticdump -g

## 聚合查询

```
GET /staap_event/staap_event_t/_search
{
  "size": 0,             
  "aggs": {
    " attackIp": {   
      "terms": {
        "field": "DST_IP",
        "order": {
          "attackCount": "desc"
        },
        "size": 5
      },
      "aggs": {
        "attackCount": {
          "sum": {
            "field": "OCCUR_COUNT"
          }
        }
      }
    }
  }
}

```

## 插入数据

```
POST /staap_event/staap_event_t
{
"OCCUR_TIME": "2017-10-20 04:16:29:000",
"AVG_PACKET_FLOW": 47474,
"DST_IP": "49.112.0.0",
"SRC_IP": "14.134.64.10",
"EVENT_TYPE_NAME": "密码猜测攻击",
"RAW_MSG_ID": "51542427561,51542427528,51542427429,51542427594,51542427495,51542427462",
"SUM_BYTE_FLOW": 1417,
"EVENT_TYPE_CODE": "27-28",
"AVG_BYTE_FLOW": 14178,
"SUM_PACKET_FLOW": 14178,
"MAX_PACKET_FLOW": 367550,
"ALERT_ID": "345356324152012791384",
"ALERT_GRADE": "3",
"END_TIME": "2017-10-20 05:17:29:000",
"COLLECT_TIME": "1970-01-10 08:00:00:000",
"EVENT_ID": "45666632345883",
"CREATE_TIME": "1970-01-01 08:00:00:000",
"SEVERITY": "low",
"MAX_BYTE_FLOW": 14578,
"ABNORMAL_FLOW_ATTACK_TYPE": "UDP FLOOD",
"OCCUR_COUNT": 53140
}
```

