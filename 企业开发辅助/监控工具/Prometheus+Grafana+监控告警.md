# 简介

​		监控告警平台组件：

​					**Prometheus（系统，服务监控指标收集）**						

​					**Grafana（图形化监控平台）**

​					**Alertmanager（警报管理器）**

​		我们可以结合上上方的组件打造监控告警系统

​		**Prometheus官方架构图**

![](https://prometheus.io/assets/architecture.png)

## Prometheus（指标收集）

​		Prometheus 是一个[云原生计算基金会](https://cncf.io/)项目，是一个系统和服务监控系统。它以给定的时间间隔从配置的目标收集指标，评估规则表达式，显示结果，并在观察到指定条件时触发警报。

​		Prometheus 与其他指标和监控系统的区别在于：

- **多维**数据模型（由指标名称和键/值维度集定义的时间序列）
- PromQL，一种**强大且灵活的查询语言**，可利用此维度
- 不依赖分布式存储；**单个服务器节点是自治的**
- 用于时间序列收集的 HTTP**拉取模型**
- 通过用于批处理作业的中间网关支持**推送时间序列**
- 通过**服务发现**或**静态配置发现目标**
- **图形和仪表板支持**的多种模式
- 支持分层和水平**联合**

​		**什么时候使用Prometheus合适？**

​				Prometheus 可以很好地记录任何纯数字时间序列。它既适合以机器为中心的监控，也适合监控高度动态的面向服务的架构。在微服务的世界中，它对多维数据收集和查询的支持是一个特殊的优势。

​		**什么时候不合适？**

​				Prometheus 重视可靠性。即使在故障情况下，您也可以随时查看有关系统的可用统计信息。如果您需要 100% 的准确性，例如按请求计费，Prometheus 不是一个好的选择，因为收集的数据可能不够详细和完整。在这种情况下，您最好使用其他系统来收集和分析数据以进行计费，并使用 Prometheus 进行其余的监控。

​		**项目地址**

​			GitHub地址：[点击进入](https://github.com/prometheus/prometheus)

​			官方文档地址：[点击进入](https://prometheus.io/docs/introduction/overview/)

### 数据模型

​		Prometheus 从根本上将所有数据存储为[时间序列](https://en.wikipedia.org/wiki/Time_series)：属于同一指标和同一组标记维度的时间戳值流。除了存储的时间序列，Prometheus 可能会生成临时派生的时间序列作为查询的结果。

​		每个时间序列都由其度量名称和称为**标签的可选键值对唯一标识。

​		度量名称指定被测量的系统的一般特征（例如-`http_requests_total`接收到的 HTTP 请求的总数）。它可能包含 ASCII 字母和数字，以及下划线和冒号。它必须与正则表达式匹配`[a-zA-Z_:][a-zA-Z0-9_:]*`。

​		注意：冒号是为用户定义的录制规则保留的。出口商或直接仪器不应使用它们。

​		标签启用 Prometheus 的维度数据模型：相同度量名称的任何给定标签组合标识该度量的特定维度实例（例如：使用该方法的所有 HTTP 请求`POST`到`/api/tracks`处理程序）。查询语言允许基于这些维度进行过滤和聚合。更改任何标签值，包括添加或删除标签，都会创建一个新的时间序列。

​		标签名称可能包含 ASCII 字母、数字以及下划线。它们必须与正则表达式匹配`[a-zA-Z_][a-zA-Z0-9_]*`。以开头的标签名称`__` 保留供内部使用。

​		标签值可以包含任何 Unicode 字符。

​		具有空标签值的标签被认为等同于不存在的标签。

​		另请参阅[命名度量和标签的最佳实践](https://prometheus.io/docs/practices/naming/)。

​		给定一个度量名称和一组标签，时间序列经常使用这个符号来识别

```properties
<metric name>{<label name>=<label value>, ...}
```

​		例如，具有度量名称`api_http_requests_total`和标签`method="POST"`的时间序列`handler="/messages"`可以这样

```properties
api_http_requests_total{method="POST", handler="/messages"}
```

​		[这与OpenTSDB](http://opentsdb.net/)使用的表示法相同。

### metrics（指标）

​		用外行的话来说，度量是数字度量。时间序列意味着随着时间的推移记录变化。用户想要测量的内容因应用程序而异。对于 Web 服务器，它可能是请求时间，对于数据库，它可能是活动连接数或活动查询数等。

​		指标在理解为什么您的应用程序以某种方式工作方面起着重要作用。假设您正在运行一个 Web 应用程序并发现该应用程序很慢。您将需要一些信息来了解您的应用程序发生了什么。例如，当请求数量很高时，应用程序可能会变慢。如果您有请求计数指标，您可以找出原因并增加服务器数量来处理负载。

​		**指标类型**

​		Prometheus 客户端库提供四种核心指标类型。这些目前仅在客户端库（以启用针对特定类型的使用量身定制的 API）和有线协议中进行区分。Prometheus 服务器尚未使用类型信息，并将所有数据扁平化为无类型时间序列。这在未来可能会改变。

#### Counter（计数器）

​		计数器是一个累积度量，它代表一个[单调递增的计数器](https://en.wikipedia.org/wiki/Monotonic_function)，其值只能在重新启动时增加或重置为零。例如，您可以使用计数器来表示服务的请求数、完成的任务数或错误数。

​		不要使用计数器来公开可能减少的值。例如，不要对当前正在运行的进程数使用计数器；而是使用量规。

​		计数器的客户端库使用文档：

- ​				[Go](https://godoc.org/github.com/prometheus/client_golang/prometheus#Counter)
- ​				[Java](https://github.com/prometheus/client_java#counter)
- ​				[Python](https://github.com/prometheus/client_python#counter)
- ​				[Ruby](https://github.com/prometheus/client_ruby#counter)

#### Gauge（仪表）

​		仪表是一种度量，表示可以任意上下的单个数值。

​		仪表通常用于测量值，例如温度或当前内存使用情况，但也用于可以上下波动的“计数”，例如并发请求的数量。

​		仪表的客户端库使用文档：

- ​				[Go](https://godoc.org/github.com/prometheus/client_golang/prometheus#Gauge)
- ​				[Java](https://github.com/prometheus/client_java#gauge)
- ​				[Python](https://github.com/prometheus/client_python#gauge)
- ​				[Ruby](https://github.com/prometheus/client_ruby#gauge)

#### **Histogram（直方图；柱状图）**

​		直方图对观察结果进行采样（通常是请求持续时间或响应大小等），并将它们计入可配置的存储桶中。它还提供所有观察值的总和。

​		具有基本度量名称的直方图`<basename>`在抓取期间公开多个时间序列：

- ​				观察桶的累积计数器，暴露为`<basename>_bucket{le="<upper inclusive bound>"}`
- ​				所有观察值的**总和**，暴露为`<basename>_sum`
- ​				已观察到的事件计数，暴露为（**与**`<basename>_count`上述相同`<basename>_bucket{le="+Inf"}`）

​		使用该 [`histogram_quantile()`函数](https://prometheus.io/docs/prometheus/latest/querying/functions/#histogram_quantile) 从直方图甚至直方图的聚合中计算分位数。直方图也适用于计算 [Apdex 分数](https://en.wikipedia.org/wiki/Apdex)。在桶上操作时，请记住直方图是 [累积](https://en.wikipedia.org/wiki/Histogram#Cumulative_histogram)的。有关直方图用法的详细信息以及与摘要的差异，请参阅 [直方图和](https://prometheus.io/docs/practices/histograms)[摘要](https://prometheus.io/docs/concepts/metric_types/#summary)。

​		直方图的客户端库使用文档：

- ​				[Go](https://godoc.org/github.com/prometheus/client_golang/prometheus#Histogram)
- ​				[Java](https://github.com/prometheus/client_java#histogram)
- ​				[Python](https://github.com/prometheus/client_python#histogram)
- ​				[Ruby](https://github.com/prometheus/client_ruby#histogram)

#### **Summary（总结，概要）**

​		与histogram类似，摘要对观察结果进行采样（通常是请求持续时间和响应大小等）。虽然它还提供了观察总数和所有观察值的总和，但它计算了滑动时间窗口上的可配置分位数。

​		具有基本指标名称的摘要`<basename>`在抓取期间公开多个时间序列：

- ​				流式传输观察到的事件的**φ 分位数**(0 ≤ φ ≤ 1)，暴露为`<basename>{quantile="<φ>"}`
- ​				所有观察值的**总和**，暴露为`<basename>_sum`
- ​				已观察到的事件计数，暴露**为**`<basename>_count`

​		有关φ 分位数、摘要用法以及与直方图的差异的详细说明，[请](https://prometheus.io/docs/concepts/metric_types/#histogram)参阅[直方图和摘要](https://prometheus.io/docs/practices/histograms)。

​		摘要的客户端库使用文档：

- ​				[Go](https://godoc.org/github.com/prometheus/client_golang/prometheus#Summary)
- ​				[Java](https://github.com/prometheus/client_java#summary)
- ​				[Python](https://github.com/prometheus/client_python#summary)
- ​				[Ruby](https://github.com/prometheus/client_ruby#summary)



### Jobs and Instances(作业和实例)

​		在 Prometheus 术语中，您可以抓取的端点称为实例，通常对应于单个进程。具有相同目的的实例集合，例如为可伸缩性或可靠性而复制的流程，称为作业。

​		例如，具有四个复制实例的 API 服务器作业：

​		**job(作业)**：api-server

- ​				**Instances**实例1：`1.2.3.4:5670`
- ​				**Instances**实例2：`1.2.3.4:5671`
- ​				**Instances**实例 3：`5.6.7.8:5670`
- ​				**Instances**实例 4：`5.6.7.8:5671`

​		当 Prometheus 抓取一个目标时，它会自动将一些标签附加到抓取的时间序列上，用于识别抓取的目标：

- ​				`job`：目标所属的已配置作业名称。
- ​				`instance`:`<host>:<port>`被抓取的目标 URL 的一部分。

​		如果这些标签中的任何一个已经存在于抓取的数据中，则行为取决于`honor_labels`配置选项。有关更多信息，请参阅 [抓取配置文档](https://prometheus.io/docs/prometheus/latest/configuration/configuration/#scrape_config) 。

​		对于每个实例抓取，Prometheus 将[样本](https://prometheus.io/docs/introduction/glossary#sample)存储在以下时间序列中：

- `up{job="<job-name>", instance="<instance-id>"}`：`1`如果实例是健康的，即可达，或者`0`如果抓取失败。
- `scrape_duration_seconds{job="<job-name>", instance="<instance-id>"}`：刮擦的持续时间。
- `scrape_samples_post_metric_relabeling{job="<job-name>", instance="<instance-id>"}`：应用度量重新标记后剩余的样本数。
- `scrape_samples_scraped{job="<job-name>", instance="<instance-id>"}`：目标暴露的样本数。
- `scrape_series_added{job="<job-name>", instance="<instance-id>"}`：本次抓取中新系列的大致数量。*v2.10 中的新功能*

### Components（组件）

​		Prometheus 生态系统由多个组件组成，其中许多是可选的：

- ​						主要的[Prometheus 服务器](https://github.com/prometheus/prometheus)，用于抓取和存储时间序列数据
- ​						用于检测应用程序代码的[客户端库](https://prometheus.io/docs/instrumenting/clientlibs/)
- ​						支持短期工作的[推送网关](https://github.com/prometheus/pushgateway)
- ​						HAProxy、StatsD、Graphite 等服务的专用[出口商。](https://prometheus.io/docs/instrumenting/exporters/)
- ​						处理警报的[警报管理器](https://github.com/prometheus/alertmanager)
- ​						各种支持工具

​		大多数 Prometheus 组件都是用[Go](https://golang.org/)编写的，这使得它们易于构建和部署为静态二进制文件。

​		以及各种开源的exporters：

​		exporters大全：[点击进入](https://prometheus.io/docs/instrumenting/exporters/)

## Grafana（仪表盘）

​		Grafana 允许您查询、可视化、提醒和了解您的指标，无论它们存储在何处。与您的团队创建、探索和共享仪表板，并培养数据驱动的文化：

- **可视化：**具有多种选项的快速灵活的客户端图表。面板插件提供了许多不同的方式来可视化指标和日志。
- **动态仪表板：**使用在仪表板顶部显示为下拉列表的模板变量创建动态和可重复使用的仪表板。
- **探索指标：**通过即席查询和动态钻取探索您的数据。拆分视图并并排比较不同的时间范围、查询和数据源。
- **探索日志：**体验从指标切换到带有保留标签过滤器的日志的魔力。快速搜索所有日志或实时流式传输它们。
- **警报：**为您最重要的指标直观地定义警报规则。Grafana 将持续评估并向 Slack、PagerDuty、VictorOps、OpsGenie 等系统发送通知。
- **混合数据源：**在同一个图中混合不同的数据源！您可以基于每个查询指定数据源。这甚至适用于自定义数据源。

​		**项目地址**

​			GitHub地址：[点击进入](https://github.com/grafana/grafana)

​			官方文档地址：[点击进入](https://grafana.com/docs/)

## Alertmanager（告警）

​		Alertmanager 处理由 Prometheus 服务器等客户端应用程序发送的警报。由于 webhook 接收器，它负责对它们进行重复数据删除、分组和路由到正确的[接收器集成](https://prometheus.io/docs/alerting/latest/configuration/#receiver)，例如电子邮件、PagerDuty、OpsGenie 或许多其他[机制。](https://prometheus.io/docs/operating/integrations/#alertmanager-webhook-receiver)它还负责警报的静音和抑制。

​		[Alertmanager](https://github.com/prometheus/alertmanager)处理由 Prometheus 服务器等客户端应用程序发送的警报。它负责对它们进行重复数据删除、分组并将它们路由到正确的接收器集成，例如电子邮件、PagerDuty 或 OpsGenie。它还负责警报的静音和抑制。

​		下面介绍 Alertmanager 实现的核心概念。请查阅[配置文档](https://prometheus.io/docs/alerting/latest/configuration/)以了解如何更详细地使用它们。

​		**项目地址**

​			GitHub地址：[点击进入](https://github.com/prometheus/alertmanager)

​			官方文档地址：[点击进入](https://prometheus.io/docs/alerting/latest/alertmanager/)

### Grouping（分组）

​		分组将类似性质的警报分类为单个通知。当许多系统同时发生故障并且可能同时触发数百到数千个警报时，这在较大的中断期间特别有用。

​		**示例：**发生网络分区时，集群中正在运行数十或数百个服务实例。您的一半服务实例无法再访问数据库。Prometheus 中的警报规则配置为在每个服务实例无法与数据库通信时发送警报。因此，数百个警报被发送到 Alertmanager。

​		作为用户，您只想获得一个页面，同时仍然能够准确查看哪些服务实例受到了影响。因此，可以将 Alertmanager 配置为按其集群和警报名称对警报进行分组，以便发送单个紧凑通知。

​		警报的分组、分组通知的时间以及这些通知的接收者由配置文件中的路由树配置。

### Inhibition（抑制）

​		抑制是在某些其他警报已经触发时抑制某些警报通知的概念。

​		**示例：**正在触发通知整个集群不可访问的警报。如果该特定警报正在触发，Alertmanager 可以配置为静音有关此集群的所有其他警报。这可以防止通知与实际问题无关的数百或数千个触发警报。

​		抑制是通过 Alertmanager 的配置文件配置的。

### Silences（静音）

​		静音是在给定时间内简单地将警报静音的简单方法。静音是基于匹配器配置的，就像路由树一样。检查传入警报是否匹配活动静音的所有相等或正则表达式匹配器。如果他们这样做，则不会针对该警报发送任何通知。

​		静音在 Alertmanager 的 Web 界面中配置。

### Client behavior（客户端行为）

​		Alertmanager 对其客户的行为有[特殊要求](https://prometheus.io/docs/alerting/latest/clients/)。这些仅与 Prometheus 不用于发送警报的高级用例相关。

### High Availability（高可用）

​		Alertmanager 支持配置以创建集群以实现高可用性。这可以使用[--cluster-*](https://github.com/prometheus/alertmanager#high-availability)标志进行配置。

​		重要的是不要在 Prometheus 和它的 Alertmanagers 之间对流量进行负载平衡，而是将 Prometheus 指向所有 Alertmanagers 的列表。

## PushGateway（推送网关）

​		Prometheus Pushgateway 的存在是为了允许临时和批处理作业向 Prometheus 公开其指标。由于这些类型的工作可能存在的时间不够长而无法被抓取，因此他们可以将指标推送到 Pushgateway。然后 Pushgateway 将这些指标公开给 Prometheus。

​		首先，Pushgateway 无法将 Prometheus 变成基于推送的监控系统。有关 Pushgateway 用例的一般描述，请阅读[何时使用 Pushgateway](https://prometheus.io/docs/practices/pushing/)。

​		Pushgateway 明确不是聚合器或分布式计数器，而是指标缓存。它没有 [类似 statsd](https://github.com/etsy/statsd)的语义。推送的指标与您在永久运行的程序中为抓取提供的指标完全相同。如果您需要分布式计数，您可以将实际的 statsd与[Prometheus statsd exporter](https://github.com/prometheus/statsd_exporter)结合使用，或者查看 [Weavework 的聚合网关](https://github.com/weaveworks/prom-aggregation-gateway)。随着更多经验的积累Prometheus 项目有朝一日可能能够提供一种本地解决方案，与 Pushgateway 分开，甚至可能作为Pushgateway 的一部分。

​		对于机器级别的指标，Node 导出器的 [文本文件](https://github.com/prometheus/node_exporter/blob/master/README.md#textfile-collector) 收集器通常更合适。Pushgateway 旨在用于服务级别指标。

Pushgateway 不是事件存储。虽然您可以将 Prometheus 用作 [Grafana 注释](http://docs.grafana.org/reference/annotations/)的数据源，但跟踪诸如发布事件之类的事情必须使用某些事件日志框架进行。

​		**我们仅建议在某些有限情况下使用 Pushgateway。**盲目地使用 Pushgateway 而不是 Prometheus 通常的 pull 模型进行一般指标收集时，有几个陷阱：

- ​				当通过单个 Pushgateway 监控多个实例时，Pushgateway 成为单点故障和潜在瓶颈。
- ​				`up` 您会失去 Prometheus 通过指标（在每次抓取时生成）的自动实例健康监控。
- ​				Pushgateway 永远不会忘记推送给它的系列，并将它们永远暴露给 Prometheus，除非这些系列是通过 Pushgateway 的 API 手动删除的。

​		**通常，Pushgateway 唯一有效的用例是用于捕获服务级批处理作业的结果**。“服务级别”批处理作业是与特定机器或作业实例在语义上不相关的作业（例如，删除整个服务的多个用户的批处理作业）。此类作业的指标不应包含机器或实例标签，以将特定机器或实例的生命周期与推送的指标分离。这减轻了在 Pushgateway 中管理陈旧指标的负担。另请参阅[监控批处理作业的最佳实践](https://prometheus.io/docs/practices/instrumentation/#batch-jobs)。

​		**项目地址**

​			GitHub地址：[点击进入](https://github.com/prometheus/pushgateway)

## PrometheusAlert（开源告警）

​		PrometheusAlert是开源的运维告警中心消息转发系统，支持主流的监控系统Prometheus、Zabbix，日志系统Graylog2，Graylog3、数据可视化系统Grafana、SonarQube。阿里云-云监控，以及所有支持WebHook接口的系统发出的预警消息，支持将收到的这些消息发送到钉钉，微信，email，飞书，腾讯短信，腾讯电话，阿里云短信，阿里云电话，华为短信，百度云短信，容联云电话，七陌短信，七陌语音，TeleGram，百度Hi(如流)等。

​		**项目地址**

​			GitHub地址：[点击进入](https://github.com/feiyu563/PrometheusAlert)

​			Gitee地址：[点击进入](https://gitee.com/feiyu563/PrometheusAlert)

# 组件使用

## Prometheus

### 安装部署

#### Docker-Compose

​		部署Node-Exporter

```sh
# 创建部署文件夹
mkdir -p ~/deploy/prometheus-node-exporter && cd ~/deploy/prometheus-node-exporter 

# 创建Compose脚本
cat > ./docker-compose.yml << EOF
version: '3'
services:
  prom-node:
    container_name: prom-node
    image: prom/node-exporter
    restart: always
    privileged: true
    network_mode: host
    volumes:
     - ./proc:/host/proc:ro
     - ./sys:/host/sys:ro
     - ./rootfs:/rootfs:ro
EOF

# 启动Node-Exporte
docker-compose up -d

# 访问地址为
http://${ip}:9100
```

​		部署Prometheus

```sh
# 创建部署文件夹
mkdir -p ~/deploy/prometheus && cd ~/deploy/prometheus
mkdir -p rules
# 创建prometheus配置文件
cat > ./prometheus.yml << EOF
global:
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
      - targets: ['localhost:9100']
        labels:
          instance: localhost-node-exporter
rule_files:
  - "/etc/prometheus/rules/*.yml"
EOF



# 创建Compose脚本
cat > ./docker-compose.yml << EOF
version: '3'
services:
  prometheus:
    container_name: prometheus
    image: prom/prometheus
    restart: always
    privileged: true
    network_mode: host
    ports:
     - 9090:9090
    volumes:
     - ./prometheus.yml:/etc/prometheus/prometheus.yml
     - ./rules:/etc/prometheus/rules
EOF

# 启动Prometheus
docker-compose up -d


# 访问地址为
http://${ip}:9090
```

#### K8s

```

```



### 告警配置

​		告警规则以及配置参考官方文档：[点击进入](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/)

```sh
# 新增规则如下
cat >> ./rules/instance-down-rule.yml << EOF
groups:
- name: instance
  rules:
  - alert: InstanceDown
  	# 条件为up = 0的时候表示宕机
    expr: up == 0
    for: 1m
    labels:
      severity: page
    annotations:
      summary: "实例 {{ $labels.instance }} 下线"
      description: "{{ $labels.job }} 的实例 {{ $labels.instance }} 已经停机 1 分钟。"
EOF
cat >> ./rules/instance-down-rule.yml << EOF
# 配置Alertmanager地址
cat >> ./prometheus.yml << EOF
alerting:
  alertmanagers:
  - static_configs:
    - targets:
      - localhost:9093
EOF
```



## Grafana

### 安装部署

#### Docker-Compose

​		部署Grafana

```sh
# 创建部署文件夹
mkdir -p ~/deploy/grafana && cd ~/deploy/grafana
mkdir -p storage
chmod 777 storage

# 创建Compose脚本
cat > ./docker-compose.yml << EOF
version: '3'
services:
  grafana:
    container_name: grafana
    image: grafana/grafana
    restart: always
    privileged: true
    ports:
     - 3000:3000
    volumes:
     - ./storage:/var/lib/grafana
EOF

# 启动脚本
docker-compose up -d


# 访问地址为
http://${ip}:3000
```



### Prometheus整合

​		修改prometheus.yml，新增job

```properties
  - job_name: grafana
    static_configs:
      - targets: ['localhost:3000']
        labels:
          instance: localhost-grafana
```



## Alertmanager

### 安装部署

​		部署Alertmanager

```sh
# 创建部署文件夹
mkdir -p ~/deploy/alertmanager && cd ~/deploy/alertmanager


# 创建alertmanager配置文件
cat > ./alertmanager.yml << EOF
route:
  group_by: ['alertname']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 1h
  receiver: 'web.hook'
receivers:
- name: 'web.hook'
  webhook_configs:
  - url: 'http://127.0.0.1:5001/'
inhibit_rules:
  - source_match:
      severity: 'critical'
    target_match:
      severity: 'warning'
    equal: ['alertname', 'dev', 'instance']
EOF

# 创建Compose脚本
cat > ./docker-compose.yml << EOF
version: '3'
services:
  prometheus-alertmanager:
    container_name: prometheus-alertmanager
    image: prom/alertmanager
    restart: always
    privileged: true
    network_mode: host
    ports:
     - 9093:9093
    volumes:
     - ./alertmanager.yml:/etc/alertmanager/alertmanager.yml
EOF

# 启动脚本
docker-compose up -d


# 访问地址为
http://${ip}:9093
```

## PushGateway

### 安装部署

​		部署PushGateway

```sh
# 创建部署文件夹
mkdir -p ~/deploy/pushgateway && cd ~/deploy/pushgateway

# 创建Compose脚本
cat > ./docker-compose.yml << EOF
version: '3'
services:
  pushgateway:
    container_name: pushgateway
    image: prom/pushgateway
    restart: always
    privileged: true
    network_mode: host
    ports:
     - 9091:9091
EOF


# 启动脚本
docker-compose up -d


# 访问地址为
http://${ip}:9091
```

## PrometheusAlert

### 安装部署

#### Docker-Compose

​		配置文件 app.conf 的内容可以使用环境变量的方式初始化。

​		所设置项的首选项文件必须以`PA_`开始，后面使用配置的配置名称，但**需要将配置项中所有的`-`替换为`_`**。

​		特别注意的是使用环境变量对配置项中的大小写不敏感。

​		还需要新建数据库以及表信息

​				初始化SQL地址如下（注意版本对应）：[点击进入](https://github.com/feiyu563/PrometheusAlert/blob/master/db/prometheusalert.sql)

​				如果不使用外部化数据库PrometheusAlert内置了sqlite3进行存储

​				配置文件参数：[点击进入](https://github.com/feiyu563/PrometheusAlert/blob/master/conf/app-example.conf)

```sh
# 创建部署文件夹
mkdir -p ~/deploy/prometheusAlert && cd ~/deploy/prometheusAlert

# 创建Compose脚本
cat > ./docker-compose.yml << EOF
version: '3.4'
services:
  prometheusAlert:
    container_name: prometheusAlert
    image: feiyu563/prometheus-alert:master
    restart: always
    privileged: true
    ports:
     - 9098:8080
    environment:
      PA_LOGIN_USER: "prometheusAlert"
      PA_LOGIN_PASSWORD: "prometheusAlert"
      PA_TITLE: "PrometheusAlert"
      PA_open_feishu: 1
      PA_OPEN_DINGDING: 1
      PA_OPEN_WEIXIN: 1
EOF


# 启动脚本
docker-compose up -d


# 访问地址为
http://${ip}:9098


https://open.feishu.cn/open-apis/bot/v2/hook/57746580-772c-4a93-bb44-26c4f252fffe
```



# 监控系统整合流程

## 整合Java服务

​		SpringBoot项目

​		引入Maven依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
```

​		修改配置文件

```properties
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
      application: ${spring.application.name}
      env: ${spring.profiles.active}
```

​		修改prometheus.yml配置端点

```properties
  - job_name: demo
    scrape_interval: 5s
    metrics_path: '/manager/actuator/prometheus'
    static_configs:
      - targets: ['192.168.100.11:9080']
        labels:
          group: 'SpringBoot-APP'
```

​		配置完毕后可以下载现成的模板进行监控

```sh
https://grafana.com/grafana/dashboards/12900-springboot-apm-dashboard/
```

# 组件运维

## Prometheus



## Grafana

​		Grafana 不占用大量资源，在内存和 CPU 的使用上非常轻量级。

​		推荐的最低内存：255 MB 推荐的最低 CPU：1

​		某些功能可能需要更多内存或 CPU。需要更多资源的功能包括：



- ​						[图像的服务器端渲染](https://grafana.com/grafana/plugins/grafana-image-renderer#requirements)
- ​						[警报](https://grafana.com/docs/grafana/latest/alerting/)
- ​						[数据源代理](https://grafana.com/docs/grafana/latest/developers/http_api/data_source/)



​		Grafana 需要一个数据库来存储其配置数据，例如用户、数据源和仪表板。具体要求取决于 Grafana 安装的大小和使用的功能。

​		Grafana 支持以下数据库：

- ​						[SQLite 3](https://www.sqlite.org/index.html)
- ​						[MySQL 5.7+](https://www.mysql.com/support/supportedplatforms/database.html)
- ​						[PostgreSQL 10+](https://www.postgresql.org/support/versioning/)



​		默认情况下，Grafana 安装并使用 SQLite，它是存储在 Grafana 安装位置的嵌入式数据库。

​		Grafana 将支持在 Grafana 版本发布时项目正式支持的这些数据库的版本。当某个版本不受支持时，Grafana 也可能会放弃对该版本的支持。有关每个项目的支持政策，请参阅上面的链接。

​		以下浏览器的当前版本支持 Grafana。这些浏览器的旧版本可能不受支持，因此在使用 Grafana 时应始终升级到最新版本。

- Chrome/Chromium
- Firefox
- Safari
- Microsoft Edge
- 仅在 v6.0 之前的 Grafana 版本中完全支持 Internet Explorer 11。

​		

​		

## Alertmanager



## PrometheusAlert





# 配置文件详解

## Prometheus

​		官方文档参考如下：[点击进入](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)

```properties
global:
  # 默认情况下的抓取Metrics频率默认一分钟
  [ scrape_interval: <duration> | default = 1m ]

  # 抓取Metrics超时时间默认10秒
  [ scrape_timeout: <duration> | default = 10s ]

  # 统计区间的时间默认1分钟
  [ evaluation_interval: <duration> | default = 1m ]

  # 要添加到外部系统的标签可以自定义 为 key : value
  # 外部系统 (federation, remote storage, Alertmanager).
  external_labels:
    [ <labelname>: <labelvalue> ... ]

  # PromQL查询记录日志
  # Reloading the configuration will reopen the file.
  [ query_log_file: <string> ]

# 指定规则文件，可以通配符/rules/*.yaml
# 所有匹配的文件
rule_files:
  [ - <filepath_glob> ... ]

# 抓取的配置信息
scrape_configs:
  [ - <scrape_config> ... ]

# 通知的配置alertmanager地址等等
alerting:
  alert_relabel_configs:
    [ - <relabel_config> ... ]
  alertmanagers:
    [ - <alertmanager_config> ... ]

# 远程写相关的配置
remote_write:
  [ - <remote_write> ... ]

# 远程读相关的配置
remote_read:
  [ - <remote_read> ... ]

# 运行时存储的配置
storage:
  [ tsdb: <tsdb> ]
  [ exemplars: <exemplars> ]

# 配置导出的痕迹
tracing:
  [ <tracing_config> ]
```

## Alertmanager

​		官方文档参考如下：[点击进入](https://prometheus.io/docs/alerting/latest/configuration/)

```properties
global:
  # 邮箱账号
  [ smtp_from: <tmpl_string> ]
  # 邮件服务器地址
  # 示例: smtp.example.org:587
  [ smtp_smarthost: <string> ]
  # 向邮件系统发送的主机名默认localhost
  [ smtp_hello: <string> | default = "localhost" ]
  # 邮箱认证用户名
  [ smtp_auth_username: <string> ]
  # 邮箱认证密码
  [ smtp_auth_password: <secret> ]
  # SMTP Auth using PLAIN.
  [ smtp_auth_identity: <string> ]
  # 邮箱认证秘钥 CRAM-MD5.
  [ smtp_auth_secret: <secret> ]
  # 邮箱使用tls，默认开启
  [ smtp_require_tls: <bool> | default = true ]

  # 用于Slack通知的API URL。
  [ slack_api_url: <secret> ]
  [ slack_api_url_file: <filepath> ]
  [ victorops_api_key: <secret> ]
  [ victorops_api_url: <string> | default = "https://alert.victorops.com/integrations/generic/20131114/alert/" ]
  [ pagerduty_url: <string> | default = "https://events.pagerduty.com/v2/enqueue" ]
  [ opsgenie_api_key: <secret> ]
  [ opsgenie_api_key_file: <filepath> ]
  [ opsgenie_api_url: <string> | default = "https://api.opsgenie.com/" ]
  [ wechat_api_url: <string> | default = "https://qyapi.weixin.qq.com/cgi-bin/" ]
  [ wechat_api_secret: <secret> ]
  [ wechat_api_corp_id: <string> ]
  [ telegram_api_url: <string> | default = "https://api.telegram.org" ]
  # 默认的HTTP客户端配置
  [ http_config: <http_config> ]

  # 处理超时时间默认5分钟
  [ resolve_timeout: <duration> | default = 5m ]

# 读取自定义的模板路径
# 可以使用路径匹配 'templates/*.tmpl'.
templates:
  [ - <filepath> ... ]

# 路由树的节点
route: <route>
	receiver:
	continue:
	match:
	match_re:
	matchers:
	# 最初等待发送组通知的时间警报数量。允许等待抑制警报的到达或收集,同一组的更多初始警报。(通常是~0s到几分钟。)(默认 30s)
	group_wait:
	# 发送新警报通知之前等待多长时间被添加到一组警报中，初始通知已发送。(通常~5m以上。)
	group_interval:
# 通知接受者列表
receivers:
  - <receiver> ...

# 禁止规则列表不通知
inhibit_rules:
  [ - <inhibit_rule> ... ]

# 静音或者激活的配置
time_intervals:
  [ - <time_interval> ... ]
```





```sh

node-exporter 	http://139.198.34.27:9100
Prometheus 			http://139.198.34.27:9090
Grafana 				http://139.198.34.27:3000
Alertmanager 		http://139.198.34.27:9093
PushGateway 		http://139.198.34.27:9091
PrometheusAlert	http://139.198.34.27:9098

admin
bigkang
```

