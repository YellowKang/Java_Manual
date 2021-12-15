# 安装部署

## K8s方式

### 官方一键启动

```sh
# 下载源码
git clone https://github.com/apache/skywalking-showcase.git

# 进入目录
cd skywalking-showcase

# 部署启动skywalking
make deploy.kubernetes

# 取消部署
make undeploy.kubernetes

# 重新部署
make redeploy.kubernetes
```

## Docker方式

### 官方一键启动

```sh
# 下载源码
git clone https://github.com/apache/skywalking-showcase.git

# 进入目录
cd skywalking-showcase

# 部署启动skywalking
make deploy.docker

# 取消部署
make undeploy.docker

# 重新部署
make redeploy.docker
```

### 自定义容器化部署

- **部署Es（单节点版本）**

```sh
# 创建挂载目录
mkdir -p /data/skyWalking/es/{es-data,es-logs,es-conf,es-plugins}

# 进入目录
cd /data/skyWalking/es

# 写入配置文件
cat > ./es-conf/elasticsearch.yml << EOF
cluster.name: elasticsearch-cluster
node.name: elasticsearch
network.bind_host: 0.0.0.0
network.publish_host: 127.0.0.1
http.port: 9200
transport.tcp.port: 9300
http.cors.enabled: true
http.cors.allow-origin: "*"
discovery.type: single-node
EOF

# 创建compose启动文件
cat > ./docker-compose.yml << EOF
version: '3.4'
services:
  skywalking-es-node1:
    container_name: skywalking-es-node1       # 指定容器的名称
    image: elasticsearch:7.12.0        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: skywalking-es-node1					# 主机名
    ports:
      - 9200:9200
      - 9300:9300
    environment:
      ES_JAVA_OPTS: "-Xms512m -Xmx512m"				# JVM参数
    privileged: true
    volumes: # 挂载目录
      - ./es-data:/usr/share/elasticsearch/data
      - ./es-logs:/usr/share/elasticsearch/logs
      - ./es-plugins:/usr/share/elasticsearch/plugins
      - ./es-conf/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
EOF

# 设置es文件权限
mkdir -p ./es-data && chown -R 1000:0 ./es-data
mkdir -p ./es-logs && chown -R 1000:0 ./es-logs
mkdir -p ./es-conf && chown -R 1000:0 ./es-conf
mkdir -p ./es-plugins && chown -R 1000:0 ./es-plugins

# 启动容器
docker-compose up -d
```

**部署skyWalking（单节点版本）**

​		可以通过environment，环境变量直接设置存储方式，以及注册方式

```sh
# 创建挂载目录
mkdir -p /data/skyWalking/skyWalking-server

# 进入目录
cd /data/skyWalking/skyWalking-server

# 创建配置文件


# 创建Compose文件，注意镜像版本apache/skywalking-oap-server:8.7.0-es7   后缀的es6 Or 7
cat > ./docker-compose.yml << EOF
version: '3.4'
services:
  skyWalking-server:
    container_name: skyWalking-server       # 指定容器的名称
    image: apache/skywalking-oap-server:8.7.0-es7        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: skyWalking-server					# 主机名
    ports:
      - 11800:11800
      - 12800:12800
    environment:
      SW_STORAGE: "elasticsearch7"
      SW_STORAGE_ES_CLUSTER_NODES: "192.168.100.12:9200"
    privileged: true
EOF

# 启动服务
docker-compose up -d
```

**部署skyWalking-ui**

```sh
# 创建挂载目录
mkdir -p /data/skyWalking/skyWalking-ui

# 进入目录
cd /data/skyWalking/skyWalking-ui

# 创建配置文件


# 创建Compose文件，注意镜像版本apache/skywalking-oap-server:8.7.0-es7   后缀的es6 Or 7
cat > ./docker-compose.yml << EOF
version: '3.4'
services:
  skyWalking-ui:
    container_name: skyWalking-ui       # 指定容器的名称
    image: apache/skywalking-ui:8.7.0        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: skyWalking-ui					# 主机名
    ports:
      - 8080:8080
    environment:
      SW_OAP_ADDRESS: "http://192.168.100.12:12800"
    privileged: true
EOF

# 启动服务
docker-compose up -d
```

# Java项目设置探针

​		官方文档地址: https://skywalking.apache.org/docs/#JavaAgent

​		注意事项: 所有的插件需要也打包以及配置文件

## Docker打包方式

​		我们使用Docker打包服务的时候

​		我们知道我们有基础的JDK镜像，那么我们需要把agent包也打入镜像

```dockerfile
# 新建Dockerfile如下，构建新的基础镜像
FROM majiajue/jdk1.8
ADD skywalking-agent.jar /skywalking-agent.jar

# 构建镜像
docker build -t skywalking-agent/java1.8 .
```

​		镜像File

```dockerfile
FROM skywalking-agent/java1.8
ENV JAVA_OPTS=""
ENV APP_OPTS=""
ENV SKYWALKING_OPTS="-javaagent:/skywalking-agent.jar -Dskywalking.collector.backend_service=localhost:11800 -Dskywalking.agent.service_name=demo"
ADD app.jar /app.jar
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS $SKYWALKING_OPTS -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF8 -Duser.timezone=GMT+08  -jar /app.jar $APP_OPTS" ]
EXPOSE 8080
```

​		然后动镜像即可

## Jar包方式

```sh
# 启动Jar包直接添加参数即可(修改jar包地址，backend_service后端地址，以及服务名)
java -javaagent:localFilePath/skywalking-agent.jar 
		 -Dskywalking.collector.backend_service=localhost:11800 
		 -Dskywalking.agent.service_name=demo -jar app.jar
```

## IDEA调试Boot方式

​		首先打开Boot，选择Edit Config

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1638946213943.png)

​		同样在VM options中配置启动的Vm参数

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1638946267338.png)

```
-javaagent:/Users/bigkang/Documents/test/skywalking-agent/skywalking-agent.jar -Dskywalking.collector.backend_service=localhost:11800 -Dskywalking.agent.service_name=demo
```



# 进阶方式

## Logback日志整合

#### 引入依赖

Maven方式：

```xml
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
				<dependency>
            <groupId>org.apache.skywalking</groupId>
            <artifactId>apm-toolkit-logback-1.x</artifactId>
            <version>8.7.0</version>
        </dependency>
```

#### 配置日志

​		新建logback-spring.xml配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <!--从配置文件读取配置路径，并且添加默认值-->
    <springProperty scope="context" name="log.pattern" source="log.pattern"
                    defaultValue="%d{yyyy-MM-dd HH:mm:ss.SSS} [%tid] [%thread] %-5level %logger{36} -%msg%n"/>

    <!--控制台日志打印格式 1-->
    <appender name="CONSOLE1" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
        </filter>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
                <Pattern>${log.pattern}</Pattern>
            </layout>
        </encoder>
    </appender>

    <!--控制台日志打印格式 2-->
    <appender name="CONSOLE2" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
        </filter>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.mdc.TraceIdMDCPatternLogbackLayout">
                <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{tid}] [%thread] %-5level %logger{36} -%msg%n</Pattern>
            </layout>
        </encoder>
    </appender>

    <root level="info">
        // 控制台输出
        <appender-ref ref="CONSOLE1"/>
    </root>

</configuration>
```

#### 使用方式

```java
@RestController
@RequestMapping
@Slf4j
public class ParamUtilController {

    @GetMapping("/index")
    public String formStrToJson(String name) {
    		log.info("传入参数:{}",name);
      	return name;
    }

}
```

调用接口就可以看到如下信息了

```java
2021-12-08 15:45:55.759 [TID:2143460b339d47018b8d41b645178d19.74.16389495557580001] [http-nio-8080-exec-5] INFO  c.t.b.u.d.c.ParamUtilController -传入参数:bigkang
```

#### 配置LogStash-TCP

​		引入maven依赖

```xml
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>6.1</version>
        </dependency>
```

​		配置文件配置

```yaml
spring:
  application:
    name: demo
  profiles:
    active: dev
logging:
  # 配置Logstash地址
  logstash:
    address: logstash:9400
```

​		logback-spring.xml配置修改Or新增如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <!--从配置文件读取配置路径，并且添加默认值-->
    <springProperty scope="context" name="log.pattern" source="log.pattern"
                    defaultValue="%d{yyyy-MM-dd HH:mm:ss.SSS} [%tid] [%thread] %-5level [%c] [%M] %logger{36} -%msg%n"/>
    <!--从SpringBoot配置文件读取项目名，环境，以及logstash地址-->
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>
    <springProperty scope="context" name="springProfile" source="spring.profiles.active"/>
    <springProperty scope="context" name="logstashAddress" source="logging.logstash.address"/>

    <!-- add converter for %tid -->
    <conversionRule conversionWord="tid"
                    converterClass="org.apache.skywalking.apm.toolkit.log.logback.v1.x.LogbackPatternConverter"/>
    <!-- add converter for %sw_ctx -->
    <conversionRule conversionWord="sw_ctx"
                    converterClass="org.apache.skywalking.apm.toolkit.log.logback.v1.x.LogbackSkyWalkingContextPatternConverter"/>

    <!--控制台日志打印格式 1-->
    <appender name="CONSOLE1" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
        </filter>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
                <Pattern>${log.pattern}</Pattern>
            </layout>
        </encoder>
    </appender>

    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>${logstashAddress}</destination>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp>
                    <timeZone>UTC</timeZone>
                </timestamp>
                <pattern>
                    <pattern>
                        {
                        <!--设置项目-->
                        "app": "${springAppName:-}",
                        <!--设置环境-->
                        "profile": "${springProfile:-}",
                        <!--设置等级-->
                        "level": "%level",
                        <!--设置类名 会失效-->
                        "class": "%c",
                        <!--设置方法名-->
                        "method": "%M",
                        <!--设置消息-->
                        "message": "%msg",
                        <!--设置sw上下文-->
                        "skyWalkingContext": "%sw_ctx",
                        <!--设置异常栈信息-->
                        "stackTrace": "%exception{10}"
                        }
                    </pattern>
                </pattern>
            </providers>
        </encoder>
        <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder">
            <!-- add TID(traceId) field -->
            <provider class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.logstash.TraceIdJsonProvider">
            </provider>
            <!-- add SW_CTX(SkyWalking context) field -->
            <provider class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.logstash.SkyWalkingContextJsonProvider">
            </provider>
        </encoder>
    </appender>


    <root level="info">
        // 控制台输出
        <appender-ref ref="CONSOLE1"/>
        <appender-ref ref="logstash"/>
    </root>

</configuration>
```

#### SkyWalkin日志收集

​		引入依赖后，使用SkyWalkin的日志收集

​		SkyWalkin日志收集采用GRPC方式进行收集我们需要先进行配置

​		在skywalking-agent.jar包目录下的config/agent.config

​		追加Or覆盖注意参数

```properties
# GRPC服务地址
plugin.toolkit.log.grpc.reporter.server_host=${SW_GRPC_LOG_SERVER_HOST:139.198.41.123}
# GRPC服务端口号
plugin.toolkit.log.grpc.reporter.server_port=${SW_GRPC_LOG_SERVER_PORT:11800}
# GRPC最大消息大小
plugin.toolkit.log.grpc.reporter.max_message_size=${SW_GRPC_LOG_MAX_MESSAGE_SIZE:10485760}
# GRPC服务上游超时时间
plugin.toolkit.log.grpc.reporter.upstream_timeout=${SW_GRPC_LOG_GRPC_UPSTREAM_TIMEOUT:30}
```

​		然后logback配置文件配置插入如下

```xml
    <!-- SkyWalkin GRPC appender pattern 采用简单方式直接查看日志体-->
    <appender name="sw_grpc" class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.log.GRPCLogClientAppender">
        <encoder>
            <pattern>%msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="info">
        <appender-ref ref="sw_grpc"/>
    </root>
```

## 追踪

​		可能存在这样的场景，当前应用中某些方法没有被追踪。但是我们又想看这一部分方法的调用情况。这个时候就可以使用指定方法的追踪来实现。不过这种方式的缺点是对代码有侵入。

​		官方文档: https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/application-toolkit-trace/

​		引入Maven依赖

```xml
        <dependency>
            <groupId>org.apache.skywalking</groupId>
            <artifactId>apm-toolkit-trace</artifactId>
            <version>8.7.0</version>
            <scope>provided</scope>
        </dependency>
```

### 方法追踪

​		如下代码,formStrToJson 调用了 jsonOrder，如果不加上@Trace则无法追踪到jsonOrder方法，加上即可

```java
    @PostMapping("Json排序")
    @Trace
    public String jsonOrder(String strForm, Boolean clearNull) {
        JSONObject map = (JSONObject) JSON.parse(strForm);
        Map<String, Object> innerMap = map.getInnerMap();
        Map<String, Object> tree = new TreeMap<>(innerMap);
        if (clearNull != null && clearNull) {
            return JSON.toJSONString(tree);
        } else {
            return JSON.toJSONString(tree,SerializerFeature.WRITE_MAP_NULL_FEATURES);
        }

    }
    @GetMapping("/index")
    public Object formStrToJson(String name) {
        log.info("传入参数:{}",name);
        String strForm = "{\"status\":\"0000\",\"message\":\"success\",\"data\":{\"title\":{\"id\":\"001\",\"name\":\"白菜\"},\"content\":[{\"id\":\"001\",\"value\":\"你好 白菜\"},{\"id\":\"002\",\"value\":\"你好 萝卜\"}]}}";
        String s = jsonOrder(strForm, false);
        return name;
    }
```

### 参数追踪

​		我们可以结合追踪，然后在控制台中查看到我们追踪的参数，修改后如下

```java
    @PostMapping("Json排序")
    @Trace
    @Tags({@Tag(key = "param", value = "arg[0]"),
            @Tag(key = "return", value = "returnedObj")})
    public String jsonOrder(String strForm, Boolean clearNull) {
        JSONObject map = (JSONObject) JSON.parse(strForm);
        Map<String, Object> innerMap = map.getInnerMap();
        Map<String, Object> tree = new TreeMap<>(innerMap);
        if (clearNull != null && clearNull) {
            return JSON.toJSONString(tree);
        } else {
            return JSON.toJSONString(tree, SerializerFeature.WRITE_MAP_NULL_FEATURES);
        }

    }

    @GetMapping("/index")
    @Trace
    @Tags({@Tag(key = "param", value = "arg[0]"),
            @Tag(key = "return", value = "returnedObj")})
    public Object formStrToJson(String name) {
        log.info("传入参数:{}", name);
        String strForm = "{\"status\":\"0000\",\"message\":\"success\",\"data\":{\"title\":{\"id\":\"001\",\"name\":\"白菜\"},\"content\":[{\"id\":\"001\",\"value\":\"你好 白菜\"},{\"id\":\"002\",\"value\":\"你好 萝卜\"}]}}";
        String s = jsonOrder(strForm, false);
        return name;
    }
```

​		点击追踪找到我们的相应请求即可发现如下信息

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1638956733973.png)

### 子线程追踪

​		使用@TraceCrossThread

```java
@RestController
@RequestMapping("paramUtil")
@Api("参数工具控制器")
@Slf4j
@TraceCrossThread
public class ParamUtilController {

    ExecutorService executorService = Executors.newFixedThreadPool(3);

    @PostMapping("Json排序")
    @Trace
    @Tags({@Tag(key = "param", value = "arg[0]"),
            @Tag(key = "return", value = "returnedObj")})
    public String jsonOrder(String strForm, Boolean clearNull) {
        JSONObject map = (JSONObject) JSON.parse(strForm);
        Map<String, Object> innerMap = map.getInnerMap();
        Map<String, Object> tree = new TreeMap<>(innerMap);

        // 使用@TraceCrossThread注解或使用SupplierWrapper/RunnableWrapper/TraceCrossThread

        // Runnable方式
        new Thread(RunnableWrapper.of(() -> log.info("RunnableWrapper 子线程的信息"))).start();
        new Thread(() -> log.info("new Thread子线程的信息")).start();

        // Callable方式
        executorService.submit(CallableWrapper.of(() -> {
            log.info("CallableWrapper 子线程的信息");
            return "CallableWrapper 子线程的信息";
        }));
        executorService.submit(() -> {
            log.info("new Callable 子线程的信息");
            return "new Callable 子线程的信息";
        });

        // Supplier方式
        CompletableFuture.supplyAsync(SupplierWrapper.of(() -> {
            log.info("SupplierWrapper 子线程的信息");
            return "SupplierWrapper 子线程的信息";
        }));
        CompletableFuture.supplyAsync(() -> {
            log.info("new Supplier 子线程的信息");
            return "new Supplier 子线程的信息";
        });

        if (clearNull != null && clearNull) {
            return JSON.toJSONString(tree);
        } else {
            return JSON.toJSONString(tree, SerializerFeature.WRITE_MAP_NULL_FEATURES);
        }

    }

    @GetMapping("/index")
    @Trace
    @Tags({@Tag(key = "param", value = "arg[0]"),
            @Tag(key = "return", value = "returnedObj")})
    public Object formStrToJson(String name) {
        log.info("传入参数:{}", name);
        String strForm = "{\"status\":\"0000\",\"message\":\"success\",\"data\":{\"title\":{\"id\":\"001\",\"name\":\"白菜\"},\"content\":[{\"id\":\"001\",\"value\":\"你好 白菜\"},{\"id\":\"002\",\"value\":\"你好 萝卜\"}]}}";
        String s = jsonOrder(strForm, false);
        return name;
    }
}
```

​		然后我们会发现日志如下

```java
2021-12-08 18:09:28.418 [TID:N/A] [Thread-20] INFO  [com.test.boot.utils.demo.controller.ParamUtilController] [lambda$jsonOrder$1] c.t.b.u.d.c.ParamUtilController -new Thread子线程的信息
2021-12-08 18:09:28.421 [TID:3d4ddc6aa3c841a98f23153a46ab5abd.72.16389581681990001] [Thread-19] INFO  [com.test.boot.utils.demo.controller.ParamUtilController] [lambda$jsonOrder$0] c.t.b.u.d.c.ParamUtilController -RunnableWrapper 子线程的信息
2021-12-08 18:09:28.428 [TID:3d4ddc6aa3c841a98f23153a46ab5abd.72.16389581681990001] [pool-2-thread-1] INFO  [com.test.boot.utils.demo.controller.ParamUtilController] [lambda$jsonOrder$2] c.t.b.u.d.c.ParamUtilController -CallableWrapper 子线程的信息
2021-12-08 18:09:28.428 [TID:N/A] [pool-2-thread-2] INFO  [com.test.boot.utils.demo.controller.ParamUtilController] [lambda$jsonOrder$3] c.t.b.u.d.c.ParamUtilController -new Callable 子线程的信息
2021-12-08 18:09:28.463 [TID:N/A] [ForkJoinPool.commonPool-worker-2] INFO  [com.test.boot.utils.demo.controller.ParamUtilController] [lambda$jsonOrder$5] c.t.b.u.d.c.ParamUtilController -new Supplier 子线程的信息
2021-12-08 18:09:28.463 [TID:3d4ddc6aa3c841a98f23153a46ab5abd.72.16389581681990001] [ForkJoinPool.commonPool-worker-9] INFO  [com.test.boot.utils.demo.controller.ParamUtilController] [lambda$jsonOrder$4] c.t.b.u.d.c.ParamUtilController -SupplierWrapper 子线程的信息
```

​		然后发现所有的包装类上都添加了一个注解@TraceCrossThread

​		如果我们需要追踪子线程那么直接在实现类中加上@TraceCrossThread注解即可即可

```java
@TraceCrossThread
public class RunnableWrapper implements Runnable {

}
```

### 日志

​		使用ActiveSpan，打印日志并且标记，ActiveSpan.tag等价注解@Tag

```java
        ActiveSpan.info("Span 日志Info");
        ActiveSpan.error("Span 日志Error");
        ActiveSpan.tag("val","测试Val");
```

## 配置

​		SkyWalking配置信息官网地址：[点击进入](https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/configurations/)

### 告警

​		参考简书文章: [点击进入](https://www.jianshu.com/p/5cc42569af6f)

### 服务配置

#### 集群方式

​		集群方式如下，支持（standalone（单机），zookeeper，kubernetes，consul，etcd，nacos）

```sh
cluster:
  selector: ${SW_CLUSTER:standalone}
  standalone:
  # Please check your ZooKeeper is 3.5+, However, it is also compatible with ZooKeeper 3.4.x. Replace the ZooKeeper 3.5+
  # library the oap-libs folder with your ZooKeeper 3.4.x library.
  zookeeper:
    nameSpace: ${SW_NAMESPACE:""}
    hostPort: ${SW_CLUSTER_ZK_HOST_PORT:localhost:2181}
    # Retry Policy
    baseSleepTimeMs: ${SW_CLUSTER_ZK_SLEEP_TIME:1000} # initial amount of time to wait between retries
    maxRetries: ${SW_CLUSTER_ZK_MAX_RETRIES:3} # max number of times to retry
    # Enable ACL
    enableACL: ${SW_ZK_ENABLE_ACL:false} # disable ACL in default
    schema: ${SW_ZK_SCHEMA:digest} # only support digest schema
    expression: ${SW_ZK_EXPRESSION:skywalking:skywalking}
  kubernetes:
    namespace: ${SW_CLUSTER_K8S_NAMESPACE:default}
    labelSelector: ${SW_CLUSTER_K8S_LABEL:app=collector,release=skywalking}
    uidEnvName: ${SW_CLUSTER_K8S_UID:SKYWALKING_COLLECTOR_UID}
  consul:
    serviceName: ${SW_SERVICE_NAME:"SkyWalking_OAP_Cluster"}
    # Consul cluster nodes, example: 10.0.0.1:8500,10.0.0.2:8500,10.0.0.3:8500
    hostPort: ${SW_CLUSTER_CONSUL_HOST_PORT:localhost:8500}
    aclToken: ${SW_CLUSTER_CONSUL_ACLTOKEN:""}
  etcd:
    # etcd cluster nodes, example: 10.0.0.1:2379,10.0.0.2:2379,10.0.0.3:2379
    endpoints: ${SW_CLUSTER_ETCD_ENDPOINTS:localhost:2379}
    namespace: ${SW_CLUSTER_ETCD_NAMESPACE:/skywalking}
    serviceName: ${SW_SCLUSTER_ETCD_ERVICE_NAME:"SkyWalking_OAP_Cluster"}
    authentication: ${SW_CLUSTER_ETCD_AUTHENTICATION:false}
    user: ${SW_SCLUSTER_ETCD_USER:}
    password: ${SW_SCLUSTER_ETCD_PASSWORD:}
  nacos:
    serviceName: ${SW_SERVICE_NAME:"SkyWalking_OAP_Cluster"}
    hostPort: ${SW_CLUSTER_NACOS_HOST_PORT:localhost:8848}
    # Nacos Configuration namespace
    namespace: ${SW_CLUSTER_NACOS_NAMESPACE:"public"}
    # Nacos auth username
    username: ${SW_CLUSTER_NACOS_USERNAME:""}
    password: ${SW_CLUSTER_NACOS_PASSWORD:""}
    # Nacos auth accessKey
    accessKey: ${SW_CLUSTER_NACOS_ACCESSKEY:""}
    secretKey: ${SW_CLUSTER_NACOS_SECRETKEY:""}
```

​		使用Nacos只需要修改Compose启动参数即可

```sh
cat > ./docker-compose.yml << EOF
version: '3.4'
services:
  skyWalking-server:
    container_name: skyWalking-server       # 指定容器的名称
    image: apache/skywalking-oap-server:8.7.0-es7        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: skyWalking-server					# 主机名
    ports:
      - 11800:11800
      - 12800:12800
    environment:
      SW_STORAGE: "elasticsearch7"
      SW_STORAGE_ES_CLUSTER_NODES: "192.168.100.12:9200"
      
      SW_CLUSTER: "nacos"
      SW_CLUSTER_NACOS_HOST_PORT: "192.168.100.11:8848"
      SW_CLUSTER_NACOS_USERNAME: nacos
      SW_CLUSTER_NACOS_PASSWORD: bigkang
    privileged: true
EOF

# 启动服务
docker-compose up -d
```



#### 配置中心

​		配置方式如下，支持（grpc，apollo，zookeeper，etcd，consul，k8s-configmap，nacos）

```properties
configuration:
  selector: ${SW_CONFIGURATION:none}
  none:
  grpc:
    host: ${SW_DCS_SERVER_HOST:""}
    port: ${SW_DCS_SERVER_PORT:80}
    clusterName: ${SW_DCS_CLUSTER_NAME:SkyWalking}
    period: ${SW_DCS_PERIOD:20}
  apollo:
    apolloMeta: ${SW_CONFIG_APOLLO:http://localhost:8080}
    apolloCluster: ${SW_CONFIG_APOLLO_CLUSTER:default}
    apolloEnv: ${SW_CONFIG_APOLLO_ENV:""}
    appId: ${SW_CONFIG_APOLLO_APP_ID:skywalking}
    period: ${SW_CONFIG_APOLLO_PERIOD:5}
  zookeeper:
    period: ${SW_CONFIG_ZK_PERIOD:60} # Unit seconds, sync period. Default fetch every 60 seconds.
    nameSpace: ${SW_CONFIG_ZK_NAMESPACE:/default}
    hostPort: ${SW_CONFIG_ZK_HOST_PORT:localhost:2181}
    # Retry Policy
    baseSleepTimeMs: ${SW_CONFIG_ZK_BASE_SLEEP_TIME_MS:1000} # initial amount of time to wait between retries
    maxRetries: ${SW_CONFIG_ZK_MAX_RETRIES:3} # max number of times to retry
  etcd:
    period: ${SW_CONFIG_ETCD_PERIOD:60} # Unit seconds, sync period. Default fetch every 60 seconds.
    endpoints: ${SW_CONFIG_ETCD_ENDPOINTS:localhost:2379}
    namespace: ${SW_CONFIG_ETCD_NAMESPACE:/skywalking}
    authentication: ${SW_CONFIG_ETCD_AUTHENTICATION:false}
    user: ${SW_CONFIG_ETCD_USER:}
    password: ${SW_CONFIG_ETCD_password:}
  consul:
    # Consul host and ports, separated by comma, e.g. 1.2.3.4:8500,2.3.4.5:8500
    hostAndPorts: ${SW_CONFIG_CONSUL_HOST_AND_PORTS:1.2.3.4:8500}
    # Sync period in seconds. Defaults to 60 seconds.
    period: ${SW_CONFIG_CONSUL_PERIOD:60}
    # Consul aclToken
    aclToken: ${SW_CONFIG_CONSUL_ACL_TOKEN:""}
  k8s-configmap:
    period: ${SW_CONFIG_CONFIGMAP_PERIOD:60}
    namespace: ${SW_CLUSTER_K8S_NAMESPACE:default}
    labelSelector: ${SW_CLUSTER_K8S_LABEL:app=collector,release=skywalking}
  nacos:
    # Nacos Server Host
    serverAddr: ${SW_CONFIG_NACOS_SERVER_ADDR:127.0.0.1}
    # Nacos Server Port
    port: ${SW_CONFIG_NACOS_SERVER_PORT:8848}
    # Nacos Configuration Group
    group: ${SW_CONFIG_NACOS_SERVER_GROUP:skywalking}
    # Nacos Configuration namespace
    namespace: ${SW_CONFIG_NACOS_SERVER_NAMESPACE:}
    # Unit seconds, sync period. Default fetch every 60 seconds.
    period: ${SW_CONFIG_NACOS_PERIOD:60}
    # Nacos auth username
    username: ${SW_CONFIG_NACOS_USERNAME:""}
    password: ${SW_CONFIG_NACOS_PASSWORD:""}
    # Nacos auth accessKey
    accessKey: ${SW_CONFIG_NACOS_ACCESSKEY:""}
    secretKey: ${SW_CONFIG_NACOS_SECRETKEY:""}
```

​		使用Nacos只需要修改Compose启动参数即可

```sh
cat > ./docker-compose.yml << EOF
version: '3.4'
services:
  skyWalking-server:
    container_name: skyWalking-server       # 指定容器的名称
    image: apache/skywalking-oap-server:8.7.0-es7        # 指定镜像和版本
    restart: always  # 自动重启
    hostname: skyWalking-server					# 主机名
    ports:
      - 11800:11800
      - 12800:12800
    environment:
      SW_STORAGE: "elasticsearch7"
      SW_STORAGE_ES_CLUSTER_NODES: "192.168.100.12:9200"
      SW_CONFIGURATION: nacos
      
      SW_CONFIG_NACOS_SERVER_ADDR: 127.0.0.1
      SW_CONFIG_NACOS_SERVER_PORT: 8848
    privileged: true
EOF

# 启动服务
docker-compose up -d
```

#### 存储方式

```properties
storage:
  selector: ${SW_STORAGE:h2}
  elasticsearch:
    nameSpace: ${SW_NAMESPACE:""}
    clusterNodes: ${SW_STORAGE_ES_CLUSTER_NODES:localhost:9200}
    protocol: ${SW_STORAGE_ES_HTTP_PROTOCOL:"http"}
    connectTimeout: ${SW_STORAGE_ES_CONNECT_TIMEOUT:500}
    socketTimeout: ${SW_STORAGE_ES_SOCKET_TIMEOUT:30000}
    user: ${SW_ES_USER:""}
    password: ${SW_ES_PASSWORD:""}
    trustStorePath: ${SW_STORAGE_ES_SSL_JKS_PATH:""}
    trustStorePass: ${SW_STORAGE_ES_SSL_JKS_PASS:""}
    secretsManagementFile: ${SW_ES_SECRETS_MANAGEMENT_FILE:""} # Secrets management file in the properties format includes the username, password, which are managed by 3rd party tool.
    dayStep: ${SW_STORAGE_DAY_STEP:1} # Represent the number of days in the one minute/hour/day index.
    indexShardsNumber: ${SW_STORAGE_ES_INDEX_SHARDS_NUMBER:1} # Shard number of new indexes
    indexReplicasNumber: ${SW_STORAGE_ES_INDEX_REPLICAS_NUMBER:1} # Replicas number of new indexes
    # Super data set has been defined in the codes, such as trace segments.The following 3 config would be improve es performance when storage super size data in es.
    superDatasetDayStep: ${SW_SUPERDATASET_STORAGE_DAY_STEP:-1} # Represent the number of days in the super size dataset record index, the default value is the same as dayStep when the value is less than 0
    superDatasetIndexShardsFactor: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_SHARDS_FACTOR:5} #  This factor provides more shards for the super data set, shards number = indexShardsNumber * superDatasetIndexShardsFactor. Also, this factor effects Zipkin and Jaeger traces.
    superDatasetIndexReplicasNumber: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_REPLICAS_NUMBER:0} # Represent the replicas number in the super size dataset record index, the default value is 0.
    indexTemplateOrder: ${SW_STORAGE_ES_INDEX_TEMPLATE_ORDER:0} # the order of index template
    bulkActions: ${SW_STORAGE_ES_BULK_ACTIONS:5000} # Execute the async bulk record data every ${SW_STORAGE_ES_BULK_ACTIONS} requests
    # flush the bulk every 10 seconds whatever the number of requests
    # INT(flushInterval * 2/3) would be used for index refresh period.
    flushInterval: ${SW_STORAGE_ES_FLUSH_INTERVAL:15}
    concurrentRequests: ${SW_STORAGE_ES_CONCURRENT_REQUESTS:2} # the number of concurrent requests
    resultWindowMaxSize: ${SW_STORAGE_ES_QUERY_MAX_WINDOW_SIZE:10000}
    metadataQueryMaxSize: ${SW_STORAGE_ES_QUERY_MAX_SIZE:5000}
    segmentQueryMaxSize: ${SW_STORAGE_ES_QUERY_SEGMENT_SIZE:200}
    profileTaskQueryMaxSize: ${SW_STORAGE_ES_QUERY_PROFILE_TASK_SIZE:200}
    oapAnalyzer: ${SW_STORAGE_ES_OAP_ANALYZER:"{\"analyzer\":{\"oap_analyzer\":{\"type\":\"stop\"}}}"} # the oap analyzer.
    oapLogAnalyzer: ${SW_STORAGE_ES_OAP_LOG_ANALYZER:"{\"analyzer\":{\"oap_log_analyzer\":{\"type\":\"standard\"}}}"} # the oap log analyzer. It could be customized by the ES analyzer configuration to support more language log formats, such as Chinese log, Japanese log and etc.
    advanced: ${SW_STORAGE_ES_ADVANCED:""}
  elasticsearch7:
    nameSpace: ${SW_NAMESPACE:""}
    clusterNodes: ${SW_STORAGE_ES_CLUSTER_NODES:localhost:9200}
    protocol: ${SW_STORAGE_ES_HTTP_PROTOCOL:"http"}
    connectTimeout: ${SW_STORAGE_ES_CONNECT_TIMEOUT:500}
    socketTimeout: ${SW_STORAGE_ES_SOCKET_TIMEOUT:30000}
    trustStorePath: ${SW_STORAGE_ES_SSL_JKS_PATH:""}
    trustStorePass: ${SW_STORAGE_ES_SSL_JKS_PASS:""}
    dayStep: ${SW_STORAGE_DAY_STEP:1} # Represent the number of days in the one minute/hour/day index.
    indexShardsNumber: ${SW_STORAGE_ES_INDEX_SHARDS_NUMBER:1} # Shard number of new indexes
    indexReplicasNumber: ${SW_STORAGE_ES_INDEX_REPLICAS_NUMBER:1} # Replicas number of new indexes
    # Super data set has been defined in the codes, such as trace segments.The following 3 config would be improve es performance when storage super size data in es.
    superDatasetDayStep: ${SW_SUPERDATASET_STORAGE_DAY_STEP:-1} # Represent the number of days in the super size dataset record index, the default value is the same as dayStep when the value is less than 0
    superDatasetIndexShardsFactor: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_SHARDS_FACTOR:5} #  This factor provides more shards for the super data set, shards number = indexShardsNumber * superDatasetIndexShardsFactor. Also, this factor effects Zipkin and Jaeger traces.
    superDatasetIndexReplicasNumber: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_REPLICAS_NUMBER:0} # Represent the replicas number in the super size dataset record index, the default value is 0.
    indexTemplateOrder: ${SW_STORAGE_ES_INDEX_TEMPLATE_ORDER:0} # the order of index template
    user: ${SW_ES_USER:""}
    password: ${SW_ES_PASSWORD:""}
    secretsManagementFile: ${SW_ES_SECRETS_MANAGEMENT_FILE:""} # Secrets management file in the properties format includes the username, password, which are managed by 3rd party tool.
    bulkActions: ${SW_STORAGE_ES_BULK_ACTIONS:5000} # Execute the async bulk record data every ${SW_STORAGE_ES_BULK_ACTIONS} requests
    # flush the bulk every 10 seconds whatever the number of requests
    # INT(flushInterval * 2/3) would be used for index refresh period.
    flushInterval: ${SW_STORAGE_ES_FLUSH_INTERVAL:15}
    concurrentRequests: ${SW_STORAGE_ES_CONCURRENT_REQUESTS:2} # the number of concurrent requests
    resultWindowMaxSize: ${SW_STORAGE_ES_QUERY_MAX_WINDOW_SIZE:10000}
    metadataQueryMaxSize: ${SW_STORAGE_ES_QUERY_MAX_SIZE:5000}
    segmentQueryMaxSize: ${SW_STORAGE_ES_QUERY_SEGMENT_SIZE:200}
    profileTaskQueryMaxSize: ${SW_STORAGE_ES_QUERY_PROFILE_TASK_SIZE:200}
    oapAnalyzer: ${SW_STORAGE_ES_OAP_ANALYZER:"{\"analyzer\":{\"oap_analyzer\":{\"type\":\"stop\"}}}"} # the oap analyzer.
    oapLogAnalyzer: ${SW_STORAGE_ES_OAP_LOG_ANALYZER:"{\"analyzer\":{\"oap_log_analyzer\":{\"type\":\"standard\"}}}"} # the oap log analyzer. It could be customized by the ES analyzer configuration to support more language log formats, such as Chinese log, Japanese log and etc.
    advanced: ${SW_STORAGE_ES_ADVANCED:""}

  h2:
    driver: ${SW_STORAGE_H2_DRIVER:org.h2.jdbcx.JdbcDataSource}
    url: ${SW_STORAGE_H2_URL:jdbc:h2:mem:skywalking-oap-db;DB_CLOSE_DELAY=-1}
    user: ${SW_STORAGE_H2_USER:sa}
    metadataQueryMaxSize: ${SW_STORAGE_H2_QUERY_MAX_SIZE:5000}
    maxSizeOfArrayColumn: ${SW_STORAGE_MAX_SIZE_OF_ARRAY_COLUMN:20}
    numOfSearchableValuesPerTag: ${SW_STORAGE_NUM_OF_SEARCHABLE_VALUES_PER_TAG:2}
  mysql:
    properties:
      jdbcUrl: ${SW_JDBC_URL:"jdbc:mysql://localhost:3306/swtest"}
      dataSource.user: ${SW_DATA_SOURCE_USER:root}
      dataSource.password: ${SW_DATA_SOURCE_PASSWORD:root@1234}
      dataSource.cachePrepStmts: ${SW_DATA_SOURCE_CACHE_PREP_STMTS:true}
      dataSource.prepStmtCacheSize: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_SIZE:250}
      dataSource.prepStmtCacheSqlLimit: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_LIMIT:2048}
      dataSource.useServerPrepStmts: ${SW_DATA_SOURCE_USE_SERVER_PREP_STMTS:true}
    metadataQueryMaxSize: ${SW_STORAGE_MYSQL_QUERY_MAX_SIZE:5000}
    maxSizeOfArrayColumn: ${SW_STORAGE_MAX_SIZE_OF_ARRAY_COLUMN:20}
    numOfSearchableValuesPerTag: ${SW_STORAGE_NUM_OF_SEARCHABLE_VALUES_PER_TAG:2}
  tidb:
    properties:
      jdbcUrl: ${SW_JDBC_URL:"jdbc:mysql://localhost:4000/tidbswtest"}
      dataSource.user: ${SW_DATA_SOURCE_USER:root}
      dataSource.password: ${SW_DATA_SOURCE_PASSWORD:""}
      dataSource.cachePrepStmts: ${SW_DATA_SOURCE_CACHE_PREP_STMTS:true}
      dataSource.prepStmtCacheSize: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_SIZE:250}
      dataSource.prepStmtCacheSqlLimit: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_LIMIT:2048}
      dataSource.useServerPrepStmts: ${SW_DATA_SOURCE_USE_SERVER_PREP_STMTS:true}
      dataSource.useAffectedRows: ${SW_DATA_SOURCE_USE_AFFECTED_ROWS:true}
    metadataQueryMaxSize: ${SW_STORAGE_MYSQL_QUERY_MAX_SIZE:5000}
    maxSizeOfArrayColumn: ${SW_STORAGE_MAX_SIZE_OF_ARRAY_COLUMN:20}
    numOfSearchableValuesPerTag: ${SW_STORAGE_NUM_OF_SEARCHABLE_VALUES_PER_TAG:2}
  influxdb:
    # InfluxDB configuration
    url: ${SW_STORAGE_INFLUXDB_URL:http://localhost:8086}
    user: ${SW_STORAGE_INFLUXDB_USER:root}
    password: ${SW_STORAGE_INFLUXDB_PASSWORD:}
    database: ${SW_STORAGE_INFLUXDB_DATABASE:skywalking}
    actions: ${SW_STORAGE_INFLUXDB_ACTIONS:1000} # the number of actions to collect
    duration: ${SW_STORAGE_INFLUXDB_DURATION:1000} # the time to wait at most (milliseconds)
    batchEnabled: ${SW_STORAGE_INFLUXDB_BATCH_ENABLED:true}
    fetchTaskLogMaxSize: ${SW_STORAGE_INFLUXDB_FETCH_TASK_LOG_MAX_SIZE:5000} # the max number of fetch task log in a request
    connectionResponseFormat: ${SW_STORAGE_INFLUXDB_CONNECTION_RESPONSE_FORMAT:MSGPACK} # the response format of connection to influxDB, cannot be anything but MSGPACK or JSON.
  postgresql:
    properties:
      jdbcUrl: ${SW_JDBC_URL:"jdbc:postgresql://localhost:5432/skywalking"}
      dataSource.user: ${SW_DATA_SOURCE_USER:postgres}
      dataSource.password: ${SW_DATA_SOURCE_PASSWORD:123456}
      dataSource.cachePrepStmts: ${SW_DATA_SOURCE_CACHE_PREP_STMTS:true}
      dataSource.prepStmtCacheSize: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_SIZE:250}
      dataSource.prepStmtCacheSqlLimit: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_LIMIT:2048}
      dataSource.useServerPrepStmts: ${SW_DATA_SOURCE_USE_SERVER_PREP_STMTS:true}
    metadataQueryMaxSize: ${SW_STORAGE_MYSQL_QUERY_MAX_SIZE:5000}
    maxSizeOfArrayColumn: ${SW_STORAGE_MAX_SIZE_OF_ARRAY_COLUMN:20}
    numOfSearchableValuesPerTag: ${SW_STORAGE_NUM_OF_SEARCHABLE_VALUES_PER_TAG:2}
  zipkin-elasticsearch7:
    nameSpace: ${SW_NAMESPACE:""}
    clusterNodes: ${SW_STORAGE_ES_CLUSTER_NODES:localhost:9200}
    protocol: ${SW_STORAGE_ES_HTTP_PROTOCOL:"http"}
    trustStorePath: ${SW_STORAGE_ES_SSL_JKS_PATH:""}
    trustStorePass: ${SW_STORAGE_ES_SSL_JKS_PASS:""}
    dayStep: ${SW_STORAGE_DAY_STEP:1} # Represent the number of days in the one minute/hour/day index.
    indexShardsNumber: ${SW_STORAGE_ES_INDEX_SHARDS_NUMBER:1} # Shard number of new indexes
    indexReplicasNumber: ${SW_STORAGE_ES_INDEX_REPLICAS_NUMBER:1} # Replicas number of new indexes
    # Super data set has been defined in the codes, such as trace segments.The following 3 config would be improve es performance when storage super size data in es.
    superDatasetDayStep: ${SW_SUPERDATASET_STORAGE_DAY_STEP:-1} # Represent the number of days in the super size dataset record index, the default value is the same as dayStep when the value is less than 0
    superDatasetIndexShardsFactor: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_SHARDS_FACTOR:5} #  This factor provides more shards for the super data set, shards number = indexShardsNumber * superDatasetIndexShardsFactor. Also, this factor effects Zipkin and Jaeger traces.
    superDatasetIndexReplicasNumber: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_REPLICAS_NUMBER:0} # Represent the replicas number in the super size dataset record index, the default value is 0.
    user: ${SW_ES_USER:""}
    password: ${SW_ES_PASSWORD:""}
    secretsManagementFile: ${SW_ES_SECRETS_MANAGEMENT_FILE:""} # Secrets management file in the properties format includes the username, password, which are managed by 3rd party tool.
    bulkActions: ${SW_STORAGE_ES_BULK_ACTIONS:5000} # Execute the async bulk record data every ${SW_STORAGE_ES_BULK_ACTIONS} requests
    # flush the bulk every 10 seconds whatever the number of requests
    # INT(flushInterval * 2/3) would be used for index refresh period.
    flushInterval: ${SW_STORAGE_ES_FLUSH_INTERVAL:15}
    concurrentRequests: ${SW_STORAGE_ES_CONCURRENT_REQUESTS:2} # the number of concurrent requests
    resultWindowMaxSize: ${SW_STORAGE_ES_QUERY_MAX_WINDOW_SIZE:10000}
    metadataQueryMaxSize: ${SW_STORAGE_ES_QUERY_MAX_SIZE:5000}
    segmentQueryMaxSize: ${SW_STORAGE_ES_QUERY_SEGMENT_SIZE:200}
    profileTaskQueryMaxSize: ${SW_STORAGE_ES_QUERY_PROFILE_TASK_SIZE:200}
    oapAnalyzer: ${SW_STORAGE_ES_OAP_ANALYZER:"{\"analyzer\":{\"oap_analyzer\":{\"type\":\"stop\"}}}"} # the oap analyzer.
    oapLogAnalyzer: ${SW_STORAGE_ES_OAP_LOG_ANALYZER:"{\"analyzer\":{\"oap_log_analyzer\":{\"type\":\"standard\"}}}"} # the oap log analyzer. It could be customized by the ES analyzer configuration to support more language log formats, such as Chinese log, Japanese log and etc.
    advanced: ${SW_STORAGE_ES_ADVANCED:""}
```

