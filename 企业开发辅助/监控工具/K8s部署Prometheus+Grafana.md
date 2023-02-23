# 简介

​		K8s中部署使用开源的kube-prometheus

​		Git地址：[点击进入](https://github.com/prometheus-operator/kube-prometheus)

​		官方文档地址：[点击进入](https://prometheus-operator.dev/)

​		版本支持如下

| kube-prometheus stack                                        | Kubernetes 1.20 | Kubernetes 1.21 | Kubernetes 1.22 | Kubernetes 1.23 | Kubernetes 1.24 |
| ------------------------------------------------------------ | --------------- | --------------- | --------------- | --------------- | --------------- |
| [`release-0.8`](https://github.com/prometheus-operator/kube-prometheus/tree/release-0.8) | ✔               | ✔               | ✗               | ✗               | ✗               |
| [`release-0.9`](https://github.com/prometheus-operator/kube-prometheus/tree/release-0.9) | ✗               | ✔               | ✔               | ✗               | ✗               |
| [`release-0.10`](https://github.com/prometheus-operator/kube-prometheus/tree/release-0.10) | ✗               | ✗               | ✔               | ✔               | ✗               |
| [`release-0.11`](https://github.com/prometheus-operator/kube-prometheus/tree/release-0.11) | ✗               | ✗               | ✗               | ✔               | ✔               |
| [`main`](https://github.com/prometheus-operator/kube-prometheus/tree/main) | ✗               | ✗               | ✗               | ✗               | ✔               |

​		这里我们的k8s是1.22所以采用[`release-0.10`](https://github.com/prometheus-operator/kube-prometheus/tree/release-0.10)

# 开始部署

```sh
# 创建部署文件夹
mkdir -p ~/deploy/kube-prometheus && cd ~/deploy/kube-prometheus 


# 下载包
git clone -b release-0.10 https://github.com/prometheus-operator/kube-prometheus.git

# 进入目录
cd kube-prometheus/manifests

# 修改k8s源为阿里云加速
sed -i "s#quay.io/prometheus/#registry.cn-hangzhou.aliyuncs.com/chenby/#g" *.yaml
sed -i "s#quay.io/brancz/#registry.cn-hangzhou.aliyuncs.com/chenby/#g" *.yaml
sed -i "s#registry.k8s.io/prometheus-adapter/#registry.cn-hangzhou.aliyuncs.com/chenby/#g" *.yaml
sed -i "s#quay.io/prometheus-operator/#registry.cn-hangzhou.aliyuncs.com/chenby/#g" *.yaml
sed -i "s#k8s.gcr.io/kube-state-metrics/#registry.cn-hangzhou.aliyuncs.com/chenby/#g" *.yaml


# 修改grafana时区
sed -i "s#UTC#UTC+8#g" grafana-dashboardDefinitions.yaml
sed -i "s#utc#utc+8#g" grafana-dashboardDefinitions.yaml

# 修改grafana 的端口为nodePort
sed -i  "/ports:/i\  type: NodePort" grafana-service.yaml
sed -i  "/targetPort: http/i\    nodePort: 31100" grafana-service.yaml
cat grafana-service.yaml

# 修改prometheus 的端口为nodePort
sed -i  "/ports:/i\  type: NodePort" prometheus-service.yaml
sed -i  "/targetPort: web/i\    nodePort: 31200" prometheus-service.yaml
sed -i  "/targetPort: reloader-web/i\    nodePort: 31300" prometheus-service.yaml
cat prometheus-service.yaml

# 修改alertmanager 的端口为nodePort
sed -i  "/ports:/i\  type: NodePort" alertmanager-service.yaml
sed -i  "/targetPort: web/i\    nodePort: 31400" alertmanager-service.yaml
sed -i  "/targetPort: reloader-web/i\    nodePort: 31500" alertmanager-service.yaml
cat alertmanager-service.yaml

# 执行初始化
kubectl create -f ./setup

# 启动创建
kubectl create -f ./

kubectl apply -f ./

# 查看验证Pod
kubectl  get pod -n monitoring


# 查看验证Svc
kubectl  get svc -n monitoring 
```

# 监控SpringBoot项目

​		         SpringBoot项目

​                引入Maven依赖

```XML
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
```

​                修改配置文件

```Properties
management:
  endpoints:
    web:
      exposure:
        include:
          - 'prometheus'
          - 'info'
          - 'health'
      base-path: /manager/actuator
  metrics:
    tags:
      application: ${spring.application.name}-${spring.profiles.active}
```

​       

​                配置完毕后可以下载现成的模板进行监控

​				https://grafana.com/grafana/dashboards/12900-springboot-apm-dashboard/

​		通过Service监听

```yaml
# 创建部署根目录
mkdir ~/deploy/boot-prometheus-endpoint && cd ~/deploy/boot-prometheus-endpoint

# 创建Deployment服务部署
cat > boot-prometheus-endpoint-deployment.yaml <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: boot-prometheus-endpoint
  labels:
    app: boot-prometheus-endpoint
spec:
  replicas: 3
  selector:
    matchLabels:
      app: boot-prometheus-endpoint
  template:
    metadata:
      labels:
        app: boot-prometheus-endpoint
    spec:
      containers:
      - name: boot-prometheus-endpoint
        image: registry.cn-beijing.aliyuncs.com/bigkang/boot-prometheus-endpoint
        ports:
        - containerPort: 8080
EOF

# 创建Deployment服务Service暴露
cat > boot-prometheus-endpoint-service.yaml <<EOF
apiVersion: v1
kind: Service
metadata:
  name: boot-prometheus-endpoint-service
  labels:
    app: boot-prometheus-endpoint
    release: prometheus
spec:
  selector:
    app: boot-prometheus-endpoint
  ports:
    - protocol: TCP
      name: http-metric
      port: 8080
      targetPort: 8080
EOF

# 创建ServiceMonitor监听Service的Pod
cat > boot-prometheus-endpoint-monitor.yaml <<EOF
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: boot-prometheus-endpoint-service-monitor
  labels:
    app: boot-prometheus-endpoint
    release: prometheus
spec:
  selector:
    matchLabels:
      app: boot-prometheus-endpoint
  endpoints:
  - port: http-metric
    path: "/actuator/prometheus"		
EOF


# 部署 service deployment
kubectl apply -f boot-prometheus-endpoint-service.yaml
kubectl apply -f boot-prometheus-endpoint-deployment.yaml

# 查看pod和svc
kubectl get pods | grep boot-prometheus
kubectl get svc | grep boot-prometheus

# 查看Pod信息
kubectl describe pods boot-prometheus-endpoint

# 部署 monitor
kubectl apply -f boot-prometheus-endpoint-monitor.yaml
```

# 监控外部服务端口

```sh
# 外部服务器节点中运行boot服务
mkdir -p ~/deploy/boot && cd ~/deploy/boot

# 写入DockerCompose启动脚本
cat > docker-compose.yaml <<EOF
version: '3'
services:
  node-boot:
    container_name: node-boot
    image: registry.cn-beijing.aliyuncs.com/bigkang/boot-prometheus-endpoint
    restart: always
    privileged: true
    ports:
     - 9080:8080
EOF

# 启动节点上的boot
docker-compose up -d
```

​		集成外部服务到K8s

```sh
mkdir -p ~/deploy/boot && cd ~/deploy/boot

# 部署Endpoints外部端点，以及Service,外部端点为非集群中的pod节点
cat > boot-node-endpoint-deploy.yaml <<EOF
apiVersion: v1
kind: Endpoints
metadata:
  name: boot-node
  namespace: monitoring
  labels:
    app: boot-node
subsets:
  - addresses:
    - ip: 192.168.100.11
    - ip: 192.168.100.12
    - ip: 192.168.100.13
    ports:
      - name: metrics
        port: 9080
---
kind: Service
apiVersion: v1
metadata:
  name: boot-node
  namespace: monitoring
  labels:
    app: boot-node
spec:
  ports:
    - name: metrics
      port: 9080
      protocol: TCP
      targetPort: 9080
EOF

# 应用
kubectl apply -f boot-node-endpoint-deploy.yaml 


# 创建Monitor监视器
cat > boot-node-endpoint-monitor.yaml <<EOF
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  labels:
    app: boot-node
  name: boot-node-monitor
  namespace: monitoring
spec:
  jobLabel: boot-node
  endpoints:
  - interval: 30s # 端点采集频率
    port: metrics	# 采集端口号
    path: "/actuator/prometheus" # 采集端点
  selector:
    matchLabels:
      app: boot-node
  namespaceSelector:
    matchNames:
    - monitoring
EOF

# 应用Monitor监视器
kubectl apply -f boot-node-endpoint-monitor.yaml

# 创建告警规则
cat > boot-node-endpoint-rule.yaml <<EOF
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  labels:
    prometheus: k8s
    role: external-machines-rules
  name: service-down-rule
  namespace: monitoring
spec:
  groups:
    - name: external-machines
      rules:
        - alert: ServiceDown
          annotations:
            description: '命名空间{{ $labels.namespace }}/{{ $labels.job }}任务{{ $labels.instance }} 实例已经下线一分钟了'
            summary: '实例 {{ $labels.instance }} 下线'
          expr: |
            up == 0
          for: 1m
          labels:
            severity: critical
EOF
```

​		参考博客：https://www.jianshu.com/p/b7d39cf7d499

# 命名空间新增角色（自定义命名空间）

​		

​		注意如果使用到其他命名空间的资源，需要进行配置，默认prod是没有权限的，需要新建角色提供命名空间资源权限，如果只使用default和monitoring则不需要新增

```properties
cat > prometheus-namespace-prod-role.yaml <<EOF
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: prometheus-k8s
  namespace: prod
rules:
- apiGroups:
  - ""
  resources:
  - services
  - endpoints
  - pods
  verbs:
  - get
  - list
  - watch
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: prometheus-k8s
  namespace: prod
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: prometheus-k8s
subjects:
- kind: ServiceAccount
  name: prometheus-k8s 
  namespace: monitoring
EOF
```

# 部署监控告警服务

​		官方文档：[点击进入](https://github.com/feiyu563/PrometheusAlert/blob/master/doc/readme/base-install.md)

## 初始化配置

​		创建初始化目录

```sh
# 初始化并且进入目录
mkdir -p ~/deploy/prometheusAlert && cd ~/deploy/prometheusAlert

# 命名空间 monitoring
```

​		初始化配置文件（数据库默认内置 sqlite3 注意是否挂载）,以及下方需要开启的告警通知，如飞书，钉钉，或者腾讯企业等

```sh
cat > prometheusAlert-config.yaml <<EOF
apiVersion: v1
data:
  app.conf: |
    #---------------------↓全局配置-----------------------
    appname = PrometheusAlert
    #登录用户名
    login_user=prometheusalert
    #登录密码
    login_password=prometheusalert
    #监听地址
    httpaddr = "0.0.0.0"
    #监听端口
    httpport = 8080
    runmode = dev
    #设置代理 proxy = http://123.123.123.123:8080
    proxy =
    #开启JSON请求
    copyrequestbody = true
    #告警消息标题
    title=PrometheusAlert
    #链接到告警平台地址
    GraylogAlerturl=http://graylog.org
    #钉钉告警 告警logo图标地址
    logourl=https://raw.githubusercontent.com/feiyu563/PrometheusAlert/master/doc/alert-center.png
    #钉钉告警 恢复logo图标地址
    rlogourl=https://raw.githubusercontent.com/feiyu563/PrometheusAlert/master/doc/alert-center.png
    #短信告警级别(等于3就进行短信告警) 告警级别定义 0 信息,1 警告,2 一般严重,3 严重,4 灾难
    messagelevel=3
    #电话告警级别(等于4就进行语音告警) 告警级别定义 0 信息,1 警告,2 一般严重,3 严重,4 灾难
    phonecalllevel=4
    #默认拨打号码(页面测试短信和电话功能需要配置此项)
    defaultphone=xxxxxxxx
    #故障恢复是否启用电话通知0为关闭,1为开启
    phonecallresolved=0
    #是否前台输出file or console
    logtype=file
    #日志文件路径
    logpath=logs/prometheusalertcenter.log
    #转换Prometheus,graylog告警消息的时区为CST时区(如默认已经是CST时区，请勿开启)
    prometheus_cst_time=0
    #数据库驱动，支持sqlite3，mysql,postgres如使用mysql或postgres，请开启db_host,db_port,db_user,db_password,db_name的注释
    db_driver=sqlite3
    #db_host=127.0.0.1
    #db_port=3306
    #db_user=root
    #db_password=root
    #db_name=prometheusalert
    #是否开启告警记录 0为关闭,1为开启
    AlertRecord=0
    #是否开启告警记录定时删除 0为关闭,1为开启
    RecordLive=0
    #告警记录定时删除周期，单位天
    RecordLiveDay=7
    # 是否将告警记录写入es7，0为关闭，1为开启
    alert_to_es=0
    # es地址，是[]string
    # beego.Appconfig.Strings读取配置为[]string，使用";"而不是","
    to_es_url=http://localhost:9200
    # to_es_url=http://es1:9200;http://es2:9200;http://es3:9200
    # es用户和密码
    # to_es_user=username
    # to_es_pwd=password
    
    #---------------------↓webhook-----------------------
    #是否开启钉钉告警通道,可同时开始多个通道0为关闭,1为开启
    open-dingding=1
    #默认钉钉机器人地址
    ddurl=https://oapi.dingtalk.com/robot/send?access_token=xxxxx
    #是否开启 @所有人(0为关闭,1为开启)
    dd_isatall=1
    
    #是否开启微信告警通道,可同时开始多个通道0为关闭,1为开启
    open-weixin=1
    #默认企业微信机器人地址
    wxurl=https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxx
    
    #是否开启飞书告警通道,可同时开始多个通道0为关闭,1为开启
    open-feishu=0
    #默认飞书机器人地址
    fsurl=https://open.feishu.cn/open-apis/bot/hook/xxxxxxxxx
    
    #---------------------↓腾讯云接口-----------------------
    #是否开启腾讯云短信告警通道,可同时开始多个通道0为关闭,1为开启
    open-txdx=0
    #腾讯云短信接口key
    TXY_DX_appkey=xxxxx
    #腾讯云短信模版ID 腾讯云短信模版配置可参考 prometheus告警:{1}
    TXY_DX_tpl_id=xxxxx
    #腾讯云短信sdk app id
    TXY_DX_sdkappid=xxxxx
    #腾讯云短信签名 根据自己审核通过的签名来填写
    TXY_DX_sign=腾讯云
    
    #是否开启腾讯云电话告警通道,可同时开始多个通道0为关闭,1为开启
    open-txdh=0
    #腾讯云电话接口key
    TXY_DH_phonecallappkey=xxxxx
    #腾讯云电话模版ID
    TXY_DH_phonecalltpl_id=xxxxx
    #腾讯云电话sdk app id
    TXY_DH_phonecallsdkappid=xxxxx
    
    #---------------------↓华为云接口-----------------------
    #是否开启华为云短信告警通道,可同时开始多个通道0为关闭,1为开启
    open-hwdx=0
    #华为云短信接口key
    HWY_DX_APP_Key=xxxxxxxxxxxxxxxxxxxxxx
    #华为云短信接口Secret
    HWY_DX_APP_Secret=xxxxxxxxxxxxxxxxxxxxxx
    #华为云APP接入地址(端口接口地址)
    HWY_DX_APP_Url=https://rtcsms.cn-north-1.myhuaweicloud.com:10743
    #华为云短信模板ID
    HWY_DX_Templateid=xxxxxxxxxxxxxxxxxxxxxx
    #华为云签名名称，必须是已审核通过的，与模板类型一致的签名名称,按照自己的实际签名填写
    HWY_DX_Signature=华为云
    #华为云签名通道号
    HWY_DX_Sender=xxxxxxxxxx
    
    #---------------------↓阿里云接口-----------------------
    #是否开启阿里云短信告警通道,可同时开始多个通道0为关闭,1为开启
    open-alydx=0
    #阿里云短信主账号AccessKey的ID
    ALY_DX_AccessKeyId=xxxxxxxxxxxxxxxxxxxxxx
    #阿里云短信接口密钥
    ALY_DX_AccessSecret=xxxxxxxxxxxxxxxxxxxxxx
    #阿里云短信签名名称
    ALY_DX_SignName=阿里云
    #阿里云短信模板ID
    ALY_DX_Template=xxxxxxxxxxxxxxxxxxxxxx
    
    #是否开启阿里云电话告警通道,可同时开始多个通道0为关闭,1为开启
    open-alydh=0
    #阿里云电话主账号AccessKey的ID
    ALY_DH_AccessKeyId=xxxxxxxxxxxxxxxxxxxxxx
    #阿里云电话接口密钥
    ALY_DH_AccessSecret=xxxxxxxxxxxxxxxxxxxxxx
    #阿里云电话被叫显号，必须是已购买的号码
    ALY_DX_CalledShowNumber=xxxxxxxxx
    #阿里云电话文本转语音（TTS）模板ID
    ALY_DH_TtsCode=xxxxxxxx
    
    #---------------------↓容联云接口-----------------------
    #是否开启容联云电话告警通道,可同时开始多个通道0为关闭,1为开启
    open-rlydh=0
    #容联云基础接口地址
    RLY_URL=https://app.cloopen.com:8883/2013-12-26/Accounts/
    #容联云后台SID
    RLY_ACCOUNT_SID=xxxxxxxxxxx
    #容联云api-token
    RLY_ACCOUNT_TOKEN=xxxxxxxxxx
    #容联云app_id
    RLY_APP_ID=xxxxxxxxxxxxx
    
    #---------------------↓邮件配置-----------------------
    #是否开启邮件
    open-email=0
    #邮件发件服务器地址
    Email_host=smtp.qq.com
    #邮件发件服务器端口
    Email_port=465
    #邮件帐号
    Email_user=xxxxxxx@qq.com
    #邮件密码
    Email_password=xxxxxx
    #邮件标题
    Email_title=运维告警
    #默认发送邮箱
    Default_emails=xxxxx@qq.com,xxxxx@qq.com
    
    #---------------------↓七陌云接口-----------------------
    #是否开启七陌短信告警通道,可同时开始多个通道0为关闭,1为开启
    open-7moordx=0
    #七陌账户ID
    7MOOR_ACCOUNT_ID=Nxxx
    #七陌账户APISecret
    7MOOR_ACCOUNT_APISECRET=xxx
    #七陌账户短信模板编号
    7MOOR_DX_TEMPLATENUM=n
    #注意：七陌短信变量这里只用一个var1，在代码里写死了。
    #-----------
    #是否开启七陌webcall语音通知告警通道,可同时开始多个通道0为关闭,1为开启
    open-7moordh=0
    #请在七陌平台添加虚拟服务号、文本节点
    #七陌账户webcall的虚拟服务号
    7MOOR_WEBCALL_SERVICENO=xxx
    # 文本节点里被替换的变量，我配置的是text。如果被替换的变量不是text，请修改此配置
    7MOOR_WEBCALL_VOICE_VAR=text
    
    #---------------------↓telegram接口-----------------------
    #是否开启telegram告警通道,可同时开始多个通道0为关闭,1为开启
    open-tg=0
    #tg机器人token
    TG_TOKEN=xxxxx
    #tg消息模式 个人消息或者频道消息 0为关闭(推送给个人)，1为开启(推送给频道)
    TG_MODE_CHAN=0
    #tg用户ID
    TG_USERID=xxxxx
    #tg频道name或者id, 频道name需要以@开始
    TG_CHANNAME=xxxxx
    #tg api地址, 可以配置为代理地址
    #TG_API_PROXY="https://api.telegram.org/bot%s/%s"
    
    #---------------------↓workwechat接口-----------------------
    #是否开启workwechat告警通道,可同时开始多个通道0为关闭,1为开启
    open-workwechat=0
    # 企业ID
    WorkWechat_CropID=xxxxx
    # 应用ID
    WorkWechat_AgentID=xxxx
    # 应用secret
    WorkWechat_AgentSecret=xxxx
    # 接受用户
    WorkWechat_ToUser="zhangsan|lisi"
    # 接受部门
    WorkWechat_ToParty="ops|dev"
    # 接受标签
    WorkWechat_ToTag=""
    # 消息类型, 暂时只支持markdown
    # WorkWechat_Msgtype = "markdown"
    
    #---------------------↓百度云接口-----------------------
    #是否开启百度云短信告警通道,可同时开始多个通道0为关闭,1为开启
    open-baidudx=0
    #百度云短信接口AK(ACCESS_KEY_ID)
    BDY_DX_AK=xxxxx
    #百度云短信接口SK(SECRET_ACCESS_KEY)
    BDY_DX_SK=xxxxx
    #百度云短信ENDPOINT（ENDPOINT参数需要用指定区域的域名来进行定义，如服务所在区域为北京，则为）
    BDY_DX_ENDPOINT=http://smsv3.bj.baidubce.com
    #百度云短信模版ID,根据自己审核通过的模版来填写(模版支持一个参数code：如prometheus告警:{code})
    BDY_DX_TEMPLATE_ID=xxxxx
    #百度云短信签名ID，根据自己审核通过的签名来填写
    TXY_DX_SIGNATURE_ID=xxxxx
    
    #---------------------↓百度Hi(如流)-----------------------
    #是否开启百度Hi(如流)告警通道,可同时开始多个通道0为关闭,1为开启
    open-ruliu=0
    #默认百度Hi(如流)机器人地址
    BDRL_URL=https://api.im.baidu.com/api/msg/groupmsgsend?access_token=xxxxxxxxxxxxxx
    #百度Hi(如流)群ID
    BDRL_ID=123456
    #---------------------↓bark接口-----------------------
    #是否开启telegram告警通道,可同时开始多个通道0为关闭,1为开启
    open-bark=0
    #bark默认地址, 建议自行部署bark-server
    BARK_URL=https://api.day.app
    #bark key, 多个key使用分割
    BARK_KEYS=xxxxx
    # 复制, 推荐开启
    BARK_COPY=1
    # 历史记录保存,推荐开启
    BARK_ARCHIVE=1
    # 消息分组
    BARK_GROUP=PrometheusAlert
    
    #---------------------↓语音播报-----------------------
    #语音播报需要配合语音播报插件才能使用
    #是否开启语音播报通道,0为关闭,1为开启
    open-voice=1
    VOICE_IP=127.0.0.1
    VOICE_PORT=9999
    
    #---------------------↓飞书机器人应用-----------------------
    #是否开启feishuapp告警通道,可同时开始多个通道0为关闭,1为开启
    open-feishuapp=1
    # APPID
    FEISHU_APPID=cli_xxxxxxxxxxxxx
    # APPSECRET
    FEISHU_APPSECRET=xxxxxxxxxxxxxxxxxxxxxx
    # 可填飞书 用户open_id、user_id、union_ids、部门open_department_id
    AT_USER_ID="xxxxxxxx"
  user.csv: |
    2019年4月10日,15888888881,小张,15999999999,备用联系人小陈,15999999998,备用联系人小赵
    2019年4月11日,15888888882,小李,15999999999,备用联系人小陈,15999999998,备用联系人小赵
    2019年4月12日,15888888883,小王,15999999999,备用联系人小陈,15999999998,备用联系人小赵
    2019年4月13日,15888888884,小宋,15999999999,备用联系人小陈,15999999998,备用联系人小赵
kind: ConfigMap
metadata:
  name: prometheus-alert-center-conf
  namespace: monitoring
EOF

# 然后应用config
kubectl apply -f prometheusAlert-config.yaml
```

## 部署实例

```sh
cat > prometheusAlert-deploy.yaml <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: prometheus-alert-center
    alertname: prometheus-alert-center
  name: prometheus-alert-center
  namespace: monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: prometheus-alert-center
      alertname: prometheus-alert-center
  template:
    metadata:
      labels:
        app: prometheus-alert-center
        alertname: prometheus-alert-center
    spec:
      containers:
      - image: feiyu563/prometheus-alert:master
        name: prometheus-alert-center
        env:
        - name: TZ
          value: "Asia/Shanghai"
        ports:
        - containerPort: 8080
          name: http
        resources:
          limits:
            cpu: 200m
            memory: 200Mi
          requests:
            cpu: 100m
            memory: 100Mi
        volumeMounts:
        - name: prometheus-alert-center-conf-map
          mountPath: /app/conf/app.conf
          subPath: app.conf
        - name: prometheus-alert-center-conf-map
          mountPath: /app/user.csv
          subPath: user.csv
      volumes:
      - name: prometheus-alert-center-conf-map
        configMap:
          name: prometheus-alert-center-conf
          items:
          - key: app.conf
            path: app.conf
          - key: user.csv
            path: user.csv
---
apiVersion: v1
kind: Service
metadata:
  labels:
    alertname: prometheus-alert-center
  name: prometheus-alert-center
  namespace: monitoring
  annotations:
    prometheus.io/scrape: 'true'
    prometheus.io/port: '8080'
spec:
  type: NodePort
  ports:
  - name: http
    port: 8080
    targetPort: http
    nodePort: 38080
  selector:
    app: prometheus-alert-center
EOF


# 访问我们使用 $service.$namespace:8080/
curl http://prometheus-alert-center.monitoring:8080/prometheusalert?type=fs&tpl=prometheus-fs&fsurl=https://open.feishu.cn/open-apis/bot/v2/hook/57746580-772c-4a93-bb44-26c4f252fffe
```

# 配置告警

​		修改alertmanager		

```sh
# 进入部署的prometheus目录
cd kube-prometheus/manifests

# 编辑 alertmanager-alertmanager.yaml
vim alertmanager-alertmanager.yaml
# 修改如下
spec:
  image: registry.cn-hangzhou.aliyuncs.com/chenby/alertmanager:v0.24.0
  nodeSelector:
    kubernetes.io/os: linux
  podMetadata:
    labels:
      app.kubernetes.io/component: alert-router
      app.kubernetes.io/instance: main
      app.kubernetes.io/name: alertmanager
      app.kubernetes.io/part-of: kube-prometheus
      app.kubernetes.io/version: 0.24.0
  replicas: 1
  # 新增指定config
  alertmanagerConfigSelector:
    matchLabels:
      alertmanagerConfig: main
```

## 配置Prometheus-Rule

```sh
# 创建告警规则 注意$labels 会被写入时变成系统变量，建议vim手动添加
cat > boot-node-endpoint-rule.yaml <<EOF
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  labels:
    prometheus: k8s
    role: service-down-rules
  name: service-down-rule
  namespace: monitoring
spec:
  groups:
    - name: serviceDown
      rules:
        - alert: ServiceDown
          annotations:
            description: '命名空间{{ $labels.namespace }}/{{ $labels.job }}任务{{ $labels.instance }} 实例已经下线超过一分钟了'
            summary: '实例 {{ $labels.instance }} 下线'
          expr: |
            up == 0
          for: 1m
          labels:
            severity: critical
EOF
```



## 配置Alert告警通知

​		参考博客：[点击进入](https://www.cnblogs.com/zhangb8042/p/16189365.html)

```sh

cat > boot-node-endpoint-alert.yaml <<EOF
apiVersion: monitoring.coreos.com/v1alpha1
kind: AlertmanagerConfig
metadata:
  labels:
    alertmanagerConfig: main
  name: service-down-alert
  namespace: monitoring
spec:
  route:
    groupBy: ['job']
    groupWait: 30s
    groupInterval: 5m
    repeatInterval: 3h
    receiver: 'webhook'
  receivers:
  - name: 'webhook'
    webhookConfigs:
    - url: 'http://prometheus-alert-center.monitoring:8080/prometheusalert?type=fs&tpl=prometheus-fs&fsurl=https://open.feishu.cn/open-apis/bot/v2/hook/577451280-772c-4a33-2b44-26c451ffe'
EOF
```

​		然后删除掉Alertmanager的pod重启新容器即可应用

# 异常问题

​		使用Swagger+prometheus抛出一个文档异常解决方案如下

​		修改配置文件

```Properties
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
```

​		修改Swagger配置类新增如下方法

```java
	/**
	 * 增加如下配置可解决Spring Boot 6.x 与Swagger 3.0.0 不兼容问题
	 **/
	@Bean
	public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties, Environment environment) {
		List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
		Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
		allEndpoints.addAll(webEndpoints);
		allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
		allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
		String basePath = webEndpointProperties.getBasePath();
		EndpointMapping endpointMapping = new EndpointMapping(basePath);
		boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
		return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
	}
	private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
		return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
	}
```

# 注意事项（发生问题的坑点）

## 无法监控Service

​		发生了无法监控Service，并且在prometheus中也无法看到targets

​		那么有可能是因为监控的服务的命名空间没有权限导致

​		需要从上面的

​	

## 告警无法发送





# 快速脚本

## 外部端点监控

```sh
# 设置变量属性
export yamlName="jiaanan-api-prod-endpoint"
export k8sName="jiaanan-api-prd"
export endNameSpace="prod"
export k8sLab="app: jiaanan-api-prd"
export endAddress="172.17.177.255"
export endPort="8187"
export endPortName="http"
export endPath="/manager/actuator/prometheus"
# 生成K8sYaml脚本
cat > ./$yamlName.yaml << EOF
apiVersion: v1
kind: Endpoints
metadata:
  name: $k8sName
  namespace: $endNameSpace
  labels:
    $k8sLab
subsets:
  - addresses:
    - ip: $endAddress
    ports:
      - name: $endPortName
        port: $endPort
---
kind: Service
apiVersion: v1
metadata:
  name: $k8sName
  namespace: $endNameSpace
  labels:
    $k8sLab
spec:
  ports:
    - name: $endPortName
      port: 80
      protocol: TCP
      targetPort: $endPort
---
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  labels:
    $k8sLab
  name: $k8sName-monitor
  namespace: $endNameSpace
spec:
  jobLabel: $k8sName
  endpoints:
  - interval: 30s # 端点采集频率
    port: $endPortName        # 采集端口号
    path: "$endPath" # 采集端点
  selector:
    matchLabels:
      $k8sLab
EOF

```

## K8s

​		如上是已经跑在K8s中的服务并且已经设置好服务名（注意labels标签，以及servicePort名称）

```sh
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jiaanan-api-{{ .AppStack.envName }}
  labels:
    app: jiaanan-api-{{ .AppStack.envName }}
  namespace: {{ .Values.namespace }}
spec:
  replicas: 2
  selector:
    matchLabels:
      app: jiaanan-api-{{ .AppStack.envName }}
  template:
    metadata:
      labels:
        app: jiaanan-api-{{ .AppStack.envName }}
    spec:
      containers:
        - name: main
          image: {{ .AppStack.image.backend }}
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: {{ .AppStack.envName }}
            - name: JAVA_OPTS
              value: {{ .Values.javaOpts }}
            - name: APP_OPTS
              value: {{ .Values.appOpts }}
          resources:
            limits:
              cpu: {{ .Values.cpuLimit }}
              memory: {{ .Values.memoryLimit }}
            requests:
              cpu: {{ .Values.cpuRequest }}
              memory: {{ .Values.memoryRequest }}
---
apiVersion: v1
kind: Service
metadata:
  name: jiaanan-api-{{ .AppStack.envName }}
  labels:
    app: jiaanan-api-{{ .AppStack.envName }}
  namespace: {{ .Values.namespace }}
spec:
  selector:
    app: jiaanan-api-{{ .AppStack.envName }}
  ports:
    - name: http
      protocol: TCP
      port: 80
      targetPort: 8187
```

​		直接部署监控即可

```sh
# 设置变量属性
export yamlName="jiaanan-api-test-monitor"
export k8sName="jiaanan-api-test"
export endNameSpace="test"
export k8sLab="app: jiaanan-api-test"
export endPortName="http"
export endPath="/manager/actuator/prometheus"
# 生成K8sYaml脚本
cat > ./$yamlName.yaml << EOF
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  labels:
    $k8sLab
  name: $k8sName
  namespace: $endNameSpace
spec:
  jobLabel: $k8sName
  endpoints:
  - port: $endPortName
    path: "$endPath"
    interval: 15s
  selector:
    matchLabels:
      $k8sLab
  namespaceSelector:
    matchNames:
    - $endNameSpace
EOF
```

## 告警Rule模板



```sh
# 设置变量属性
export yamlName="kafka-exporter-topic-rule"
export k8sName="kafka-exporter-topic-rule"
export endNameSpace="monitoring"

export groupName="topicHealthError"
export alertName="topicHealthError"

# 还需要手动修改描述，自动脚本变量会被覆盖
cat > ./$yamlName.yaml << EOF
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  name: $k8sName
  namespace: $endNameSpace
spec:
  groups:
    - name: $groupName
      rules:
        - alert: $alertName
          annotations:
            description: "命名空间{{ $labels.namespace }}/{{ $labels.job }}任务{{ $labels.instance }} 实例已经下线超过一分钟了"
            summary: "命名空间{{ $labels.namespace }}/{{ $labels.job }}任务{{ $labels.instance }} 实例已经下线超过一分钟了"
          expr: |
            up == 0
          for: 1m
          labels:
            severity: critical
            level: '严重'
EOF
```
