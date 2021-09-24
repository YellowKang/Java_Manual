# 引入依赖

```xml
       	<!-- elasticsearch依赖 -->
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>6.7.0</version>
        </dependency>
				<!-- transport连接客户端 -->
       	<dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
            <version>6.7.0</version>
        </dependency>
				<!-- 高级rest连接客户端 -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>6.7.0</version>
        </dependency>
        <!-- rest连接客户端 -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-client</artifactId>
            <version>6.7.0</version>
        </dependency>
```

​		管理版本方式

```xml
    <properties>
        <elasticsearch.version>6.7.0</elasticsearch.version>
        <log4j.version>2.11.1</log4j.version>
    </properties>
    <dependencies>
        <!-- elasticsearch依赖 -->
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
        <!-- transport连接客户端 -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
        <!-- 高级rest连接客户端 -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
        <!-- rest连接客户端 -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-client</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
        <!-- log4j依赖 -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>${log4j.version}</version>
        </dependency>
    </dependencies>
```



# 创建连接

​		连接创建各个版本官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/index.html)

## transport方式

​		引入上方依赖

```xml
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
            <version>${elasticsearch.version}</version>
        </dependency>
```

### 引入日志

​		并且引入日志，有三种方式，任选其中其一：

​				第一种log4j+配置：

```xml
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.11.1</version>
        </dependency>
				引入依赖然后再resource下新建log4j2.properties文件内容填入如下

				appender.console.type = Console
        appender.console.name = console
        appender.console.layout.type = PatternLayout
        appender.console.layout.pattern = [%d{ISO8601}][%-5p][%-25c] %marker%m%n
        rootLogger.level = info
        rootLogger.appenderRef.console.ref = console
```

​				第二种log4j桥接slf4j：		

```xml
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-to-slf4j</artifactId>
            <version>2.11.1</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.24</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-nop</artifactId>
            <version>1.7.2</version>
        </dependency>
```

​				第三种slf4j简单：

```xml
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.21</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.11.1</version>
        </dependency>

				引入依赖然后再resource下新建log4j2.xml文件内容填入如下
        <?xml version="1.0" encoding="UTF-8"?>
        <Configuration status="warn">
            <Appenders>
                <Console name="Console" target="SYSTEM_OUT">
                    <PatternLayout pattern="[%-5p] %d %c - %m%n" />
                </Console>
                <File name="File" fileName="dist/my.log">
                    <PatternLayout pattern="%m%n" />
                </File>
            </Appenders>

            <Loggers>
                <Logger name="mh.sample2.Log4jTest2" level="INFO">
                    <AppenderRef ref="File" />
                </Logger>
                <Root level="INFO">
                    <AppenderRef ref="Console" />
                </Root>
            </Loggers>
        </Configuration>

```

### 初始化

​		使用默认集群名称elasticsearch，空Setting方式

```java
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.1"), 9300));
```

​		修改集群名称，并且多节点连接方式

```java
        Settings settings = Settings.builder()
                .put("cluster.name", "kang-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.1"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.2"), 9300));
```

​		并且节点具有嗅探功能，官网的描述如下

```
		传输客户端具有群集嗅探功能，该功能允许它动态添加新主机并删除旧主机。启用嗅探后，传输客户端将连接到其内部节点列表中的节点，该列表是通过对的调用建立的addTransportAddress。此后，客户端将在那些节点上调用内部集群状态API以发现可用的数据节点。客户端的内部节点列表将仅替换为那些数据节点。默认情况下，此列表每五秒钟刷新一次。请注意，嗅探器连接到的IP地址是 那些节点的Elasticsearch配置中声明为发布地址的IP地址。
		请记住，如果该列表不是数据节点，则该列表可能不包括与其连接的原始节点。例如，如果在嗅探之后最初连接到主节点，则不会再有其他请求发送到该主节点，而是会发送到任何数据节点。传输客户端排除非数据节点的原因是为了避免将搜索流量发送到仅主节点。
		为了启用嗅探，请设置client.transport.sniff为true：
```

​		那么我们下面来试着开启嗅探功能

```java
       Settings settings = Settings.builder()
                .put("cluster.name", "kang-cluster")
                .put("client.transport.sniff", true)
                .build();
```

​		其他客户端设置还有如下

```properties
client.transport.ignore_cluster_name 	
	
	设置为true忽略连接节点的集群名称验证。（从0.19.4开始）

client.transport.ping_timeout

	等待来自节点的ping响应的时间。默认为5s。

client.transport.nodes_sampler_interval

	对列出和连接的节点进行采样/ ping的频率。默认为5s。
```

​		代码如下

```
       Settings settings = Settings.builder()
                .put("cluster.name", "kang-cluster")
                .put("client.transport.sniff", true)
                .put("client.transport.ignore_cluster_name", false)
                .put("client.transport.ping_timeout", "5s")
                .put("client.transport.nodes_sampler_interval", "5s")
                .build();
```

## rest方式

### 初始化

​		引入上方依赖

```xml
      <dependency>
          <groupId>org.elasticsearch.client</groupId>
          <artifactId>elasticsearch-rest-client</artifactId>
          <version>${elasticsearch.version}</version>
      </dependency>
```

​		低级Java REST客户端内部使用 [Apache Http Async客户端](http://hc.apache.org/httpcomponents-asyncclient-dev/) 发送http请求。它取决于以下工件，即异步http客户端及其自身的传递依赖项

```properties
org.apache.httpcomponents：httpasyncclient
org.apache.httpcomponents：httpcore-nio
org.apache.httpcomponents：httpclient
org.apache.httpcomponents：httpcore
commons-codec：commons-codec
commons-logging：commons-logging
```

​		设置build

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals><goal>shade</goal></goals>
                    <configuration>
                        <relocations>
                            <relocation>
                                <pattern>org.apache.http</pattern>
                                <shadedPattern>hidden.org.apache.http</shadedPattern>
                            </relocation>
                            <relocation>
                                <pattern>org.apache.logging</pattern>
                                <shadedPattern>hidden.org.apache.logging</shadedPattern>
                            </relocation>
                            <relocation>
                                <pattern>org.apache.commons.codec</pattern>
                                <shadedPattern>hidden.org.apache.commons.codec</shadedPattern>
                            </relocation>
                            <relocation>
                                <pattern>org.apache.commons.logging</pattern>
                                <shadedPattern>hidden.org.apache.commons.logging</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

​		创建连接

```java
    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"),
        new HttpHost("localhost", 9201, "http")).build();
```

​		关闭连接

```java
    restClient.close();
```

​		设置默认请求头

```java
    RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200, "http"));
    Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
    builder.setDefaultHeaders(defaultHeaders); 
```

​		设置最大重试超时毫秒数

```java
    RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200, "http"));
    builder.setMaxRetryTimeoutMillis(10000); 
```

​		 设置一个侦听器，该侦听器在每次节点发生故障时得到通知，以防需要采取措施。启用嗅探失败时在内部使用。

```java
    RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200, "http"));
    builder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);
```

​		设置节点选择器以用于过滤客户端将向其自身设置的请求中的客户端发送请求的节点。例如，在启用嗅探功能时，这可防止阻止向专用主节点发送请求。默认情况下，客户端将请求发送到每个已配置的节点。

```java
    RestClientBuilder builder = RestClient.builder(
            new HttpHost("localhost", 9200, "http"));
    builder.setRequestConfigCallback(
        new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(
                    RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setSocketTimeout(10000); 
            }
        });
```

### 同步

​		一旦`RestClient`被创建，请求可以通过调用发送 `performRequest`或`performRequestAsync`。`performRequest`是同步的，将阻止调用线程并`Response`在请求成功时返回，否则将抛出异常。

```java
    Request request = new Request(
        "GET",  
        "/");   
    Response response = restClient.performRequest(request);
```

​		 HTTP方法（`GET`，`POST`，`HEAD`等）

​			“/”为请求的端点

### 异步

​		`		performRequestAsync`是异步的，接受请求成功时或请求失败时使用`ResponseListener`调用的参数 。`Response``Exception`。

​		onSuccess用于处理成功回调，onFailure用于处理失败回调

```java
    Request request = new Request(
        "GET",  
        "/");   
    restClient.performRequestAsync(request, new ResponseListener() {
        @Override
        public void onSuccess(Response response) {

        }

        @Override
        public void onFailure(Exception exception) {

        }
    });
```

### Request

​		您可以将请求参数添加到请求对象：

```java
    Request request = new Request(
        "GET",  
        "/");  
   	request.addParameter("pretty", "true");
```

​		您可以将请求的主体设置为任何`HttpEntity`：

```java
    request.setEntity(new NStringEntity(
            "{\"json\":\"text\"}",
            ContentType.APPLICATION_JSON));
```

​		或者直接设置Json

```java
    request.setJsonEntity("{\"json\":\"text\"}");
```

​		在`RequestOptions`此类包含应在相同的应用许多请求之间共享的请求的部件。您可以创建一个单例实例，并在所有请求之间共享它：

```java
    private static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Authorization", "Bearer " + TOKEN); 
        builder.setHttpAsyncResponseConsumerFactory(           
            new HttpAsyncResponseConsumerFactory
                .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }
		// 设置Options
		request.setOptions(COMMON_OPTIONS);
```

### Response

​		该`Response`对象（由同步`performRequest`方法返回或作为in中的参数接收）`ResponseListener#onSuccess(Response)`包装由http客户端返回的响应对象，并公开一些其他信息。

```java
    Response response = restClient.performRequest(new Request("GET", "/"));
    RequestLine requestLine = response.getRequestLine(); 
		// 返回响应的主机
    HttpHost host = response.getHost(); 
		// 获取http状态码200 400 等等
    int statusCode = response.getStatusLine().getStatusCode();
		// 响应头，尽管也可以通过名称检索 getHeader(String)
    Header[] headers = response.getHeaders(); 
		// 包含在org.apache.http.HttpEntity 对象中的响应主体
    String responseBody = EntityUtils.toString(response.getEntity());
```

### 配置连接

​		超时时间配置

```java
    RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200))
        .setRequestConfigCallback(
            new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(
                        RequestConfig.Builder requestConfigBuilder) {
                    return requestConfigBuilder
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000);
                }
            });
```

​		设置线程数：默认情况下，Apache Http Async Client启动一个调度程序线程，以及连接管理器使用的多个工作线程，数量与本地检测到的处理器数量相同（取决于 `Runtime.getRuntime().availableProcessors()`返回的内容）。线程数可以如下修改：

```java
    RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200))
        .setHttpClientConfigCallback(new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultIOReactorConfig(
                    IOReactorConfig.custom()
                        .setIoThreadCount(1)
                        .build());
            }
        });
```

​		基本认证：配置基本身份验证还可通过提供一种完成 `HttpClientConfigCallback`，同时建立了`RestClient`通过它的建造者。接口具有一种方法，该方法接收的实例，[`org.apache.http.impl.nio.client.HttpAsyncClientBuilder`](https://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/impl/nio/client/HttpAsyncClientBuilder.html) 作为参数，并具有相同的返回类型。可以修改http客户端构建器，然后返回。在以下示例中，我们设置了一个需要基本身份验证的默认凭据提供程序。

```java
    final CredentialsProvider credentialsProvider =
        new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials("user", "password"));

    RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200))
        .setHttpClientConfigCallback(new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider);
            }
        });
----------------------------------------
  可以禁用抢占式身份验证，这意味着将发送不带授权标头的每个请求以查看其是否被接受，并且在收到HTTP 401响应后，它将使用基本身份验证标头重新发送完全相同的请求。如果您希望这样做，可以通过以下方式禁用它HttpAsyncClientBuilder：
  
    final CredentialsProvider credentialsProvider =
      new BasicCredentialsProvider();
  credentialsProvider.setCredentials(AuthScope.ANY,
      new UsernamePasswordCredentials("user", "password"));

  RestClientBuilder builder = RestClient.builder(
      new HttpHost("localhost", 9200))
      .setHttpClientConfigCallback(new HttpClientConfigCallback() {
          @Override
          public HttpAsyncClientBuilder customizeHttpClient(
                  HttpAsyncClientBuilder httpClientBuilder) {
              httpClientBuilder.disableAuthCaching(); 
              return httpClientBuilder
                  .setDefaultCredentialsProvider(credentialsProvider);
          }
      });
```

​		加密通讯：也可以通过来配置加密的通信 `HttpClientConfigCallback`。的 [`org.apache.http.impl.nio.client.HttpAsyncClientBuilder`](https://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/impl/nio/client/HttpAsyncClientBuilder.html) ：作为一个参数暴露多种方法来配置加密通信接收`setSSLContext`，`setSSLSessionStrategy`并且 `setConnectionManager`，从最不重要的优先级顺序。以下是一个示例：

```java
    KeyStore truststore = KeyStore.getInstance("jks");
    try (InputStream is = Files.newInputStream(keyStorePath)) {
        truststore.load(is, keyStorePass.toCharArray());
    }
    SSLContextBuilder sslBuilder = SSLContexts.custom()
        .loadTrustMaterial(truststore, null);
    final SSLContext sslContext = sslBuilder.build();
    RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200, "https"))
        .setHttpClientConfigCallback(new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setSSLContext(sslContext);
            }
        });
```

​		节点选择器：客户端以循环方式将每个请求发送到配置的节点之一。可以选择通过初始化客户端时需要提供的节点选择器来过滤节点。启用嗅探功能时，如果仅专用主节点受到HTTP请求的攻击，此功能将非常有用。对于每个请求，客户端将运行最终配置的节点选择器以筛选候选节点，然后从其余请求中选择列表中的下一个。

```java
    RestClientBuilder builder = RestClient.builder(
            new HttpHost("localhost", 9200, "http"));
    builder.setNodeSelector(new NodeSelector() { 
        @Override
        public void select(Iterable<Node> nodes) {
           / *
             *优先选择属于rack_one的任何节点。如果周围没有
             *在尝试恢复之前，我们将转到另一个机架
             *属于rack_one的某些节点。
             * /
            boolean foundOne = false;
            for (Node node : nodes) {
                String rackId = node.getAttributes().get("rack_id").get(0);
                if ("rack_one".equals(rackId)) {
                    foundOne = true;
                    break;
                }
            }
            if (foundOne) {
                Iterator<Node> nodesIt = nodes.iterator();
                while (nodesIt.hasNext()) {
                    Node node = nodesIt.next();
                    String rackId = node.getAttributes().get("rack_id").get(0);
                    if ("rack_one".equals(rackId) == false) {
                        nodesIt.remove();
                    }
                }
            }
        }
    });
```

### 嗅探器

​		引入依赖

```xml
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-client-sniffer</artifactId>
        <version>${elasticsearch.version}</version>
    </dependency>
```

​		一旦`RestClient`创建了实例（如[Initialization](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-low-usage-initialization.html)所示），便`Sniffer`可以将其与之关联。在`Sniffer`将利用所提供的`RestClient` （默认情况下，每5分钟），定期从获取集群当前节点列表，并通过调用更新它们`RestClient#setNodes`。

```java
    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"))
        .build();
    Sniffer sniffer = Sniffer.builder(restClient).build();
```

​		重要的是关闭，`Sniffer`以便其后台线程正确关闭并释放其所有资源。该`Sniffer` 对象应具有相同的生命周期的`RestClient`，并得到客户端之前关闭的权利：

```java
    sniffer.close();
    restClient.close();
```

​		在`Sniffer`默认情况下更新节点，每5分钟。可以通过提供以下时间间隔（以毫秒为单位）来自定义此时间间隔：

```java
    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"))
        .build();
    Sniffer sniffer = Sniffer.builder(restClient)
        .setSniffIntervalMillis(60000).build();
```

​		也可以启用对失败的嗅探，这意味着在每次失败之后，节点列表将立即更新，而不是在随后的普通嗅探回合中进行更新。在这种情况下`SniffOnFailureListener`，首先需要创建一个并在`RestClient`创建时提供。同样，一旦 `Sniffer`稍后创建，它需要与该相同`SniffOnFailureListener`实例相关联，在每次失败时都将通知该 实例，并使用实例`Sniffer`执行附加的嗅探回合。

```java
    SniffOnFailureListener sniffOnFailureListener =
        new SniffOnFailureListener();
    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200))
        .setFailureListener(sniffOnFailureListener) 
        .build();
    Sniffer sniffer = Sniffer.builder(restClient)
        .setSniffAfterFailureDelayMillis(30000) 
        .build();
    sniffOnFailureListener.setSniffer(sniffer); 
```

​		当连接到节点时，Elasticsearch Nodes Info api不返回要使用的协议，而仅返回它们的`host:port`密钥对，因此`http` 默认情况下使用。万一`https`应该使用 `ElasticsearchNodesSniffer`实例，则必须手动创建实例，并按以下方式提供实例：

```java
    RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200, "http"))
            .build();
    NodesSniffer nodesSniffer = new ElasticsearchNodesSniffer(
            restClient,
            ElasticsearchNodesSniffer.DEFAULT_SNIFF_REQUEST_TIMEOUT,
            ElasticsearchNodesSniffer.Scheme.HTTPS);
    Sniffer sniffer = Sniffer.builder(restClient)
            .setNodesSniffer(nodesSniffer).build();
```

​		以相同的方式，还可以自定义`sniffRequestTimeout`，默认为一秒钟。这是`timeout`调用Nodes Info api时作为querystring参数提供的参数，因此，当超时在服务器端到期时，尽管它可能只包含属于集群的一部分节点，但仍返回有效响应，在那之前做出回应的人。

```java
    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"))
        .build();
    NodesSniffer nodesSniffer = new ElasticsearchNodesSniffer(
        restClient,
        TimeUnit.SECONDS.toMillis(5),
        ElasticsearchNodesSniffer.Scheme.HTTP);
    Sniffer sniffer = Sniffer.builder(restClient)
        .setNodesSniffer(nodesSniffer).build();
```

​		另外，`NodesSniffer`可以为高级用例提供自定义实现，这些高级用例可能需要从外部资源而不是从Elasticsearch获取`Node`：

```java
    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"))
        .build();
    NodesSniffer nodesSniffer = new NodesSniffer() {
            @Override
            public List<Node> sniff() throws IOException {
                return null; 
            }
        };
    Sniffer sniffer = Sniffer.builder(restClient)
        .setNodesSniffer(nodesSniffer).build();
```

## 高级rest方式（推荐使用）

### 简介

​		Java高级REST客户端在Java高级REST客户端之上工作。它的主要目的是公开API特定的方法，这些方法接受请求对象作为参数并返回响应对象，以便请求编组和响应解编组由客户端本身处理。

​		每个API可以同步或异步调用。同步方法返回一个响应对象，而名称以`async`后缀结尾的异步方法则需要一个侦听器参数，一旦接收到响应或错误，该参数就会被通知（在低级客户端管理的线程池上）。

​		Java高级REST客户端取决于Elasticsearch核心项目。它接受与相同的请求参数，`TransportClient`并返回相同的响应对象。

​		Java高级REST客户端需要Java 1.8，并依赖于Elasticsearch核心项目。客户端版本与为其开发客户端的Elasticsearch版本相同。它接受与相同的请求参数，`TransportClient` 并返回相同的响应对象。

​		确保高级客户端能够与在相同主要版本和较大或相等的次要版本上运行的任何Elasticsearch节点进行通信。它不需要与与其通信的Elasticsearch节点处于相同的次要版本，因为它是前向兼容的，这意味着它支持与比其开发的版本更高的Elasticsearch通信。

​		6.0客户端可以与任何6.x Elasticsearch节点进行通信，而6.1客户端可以与6.1、6.2和任何更高版本的6.x版本进行通信，但是与先前的Elasticsearch节点进行通信时可能会出现不兼容问题如果6.1客户端支持6.0节点不知道的某些API的新请求正文字段，则版本介于6.1和6.0之间。

​		建议将Elasticsearch集群升级到新的主要版本时升级High Level Client，因为REST API的重大更改可能会导致意外结果，具体取决于请求所命中的节点，并且新添加的API仅受支持。客户端的较新版本。一旦集群中的所有节点都已升级到新的主要版本，客户端应始终最后更新。

引入依赖

```xml
      <dependency>
          <groupId>org.elasticsearch.client</groupId>
          <artifactId>elasticsearch-rest-high-level-client</artifactId>
          <version>${elasticsearch.version}</version>
      </dependency>
```

### 初始化

​		创建连接

```java
     RestClientBuilder builder = RestClient.builder(
                new HttpHost("192.168.1.16", 10092, "http"));
     RestHighLevelClient client = new RestHighLevelClient(builder);
```

​		关闭连接

```java
client.close();
```



### 添加

#### 创建索引API

```java
        // 创建索引
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("test_create");
        // 设置主分片和副本集
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 0)
        );
        // 指定Mapping
        createIndexRequest.mapping(
                "{\n" +
                        "  \"properties\": {\n" +
                        "    \"name\": {\n" +
                        "      \"type\": \"text\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                XContentType.JSON);
        // 设置别名
        Alias alias = new Alias("testA");
        createIndexRequest.alias(alias);

        try {
            // 创建索引
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            System.out.println(createIndexResponse.index());
        } catch (IOException e) {
            e.printStackTrace();
        }
```



#### 索引数据API

​		 Index API 官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-index.html)

​		查询所有的索引

```java
        // 使用 * 或者 _all即可查询所有
				GetIndexRequest getIndexRequest = new GetIndexRequest("*");
        GetIndexResponse getIndexResponse = client.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        String[] indices = getIndexResponse.getIndices();
        for (String index : indices) {
            System.out.println(index);
        }
```

​		索引就是我们的添加操作，也就是对数据进行索引。

​		对Json字符串进行索引

```json
# json如下
{
	"name":"bigkang",
	"address":"四川成都",
	"title":"测试标题"
}

# 代码如下
        String json = "{\n" +
                        "\t\"name\":\"bigkang\",\n" +
                        "\t\"address\":\"四川成都\",\n" +
                        "\t\"title\":\"测试标题\"\n" +
                        "}";
				// 使用Index，Type，Id进行索引
        IndexRequest indexRequest = new IndexRequest("testes","test","test");
				// 设置source源，类型为json
        indexRequest.source(json, XContentType.JSON);
				// 进行索引
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("ID:"+index.getId());
```

​		对Map进行索引

```java
        Map<String,Object>  data = new HashMap<String, Object>();
        data.put("name","bigkang");
        data.put("address","四川成都");
        data.put("title","测试标题");
        // 使用Index，Type进行索引，Id自动生成
        IndexRequest indexRequest = new IndexRequest("testes","test");
        // 设置源
        indexRequest.source(data);
        // 进行索引
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("ID:"+index.getId());
```

​		

### 查询

#### GET API

​		GET API官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-get.html)

​		查询索引

```java
        // 根据Index，Type，Id查询
        GetRequest getRequest = new GetRequest("testes","test","2My7h3UBBSIfOfzfVRjW");
				        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSource());
```

​		设置检索源，也就是需要查询排除的字段，如下三种方式

```java
        // 设置检索源字段为所有字段
        getRequest.fetchSourceContext(FetchSourceContext.FETCH_SOURCE);

        // 设置不需要要检索源字段，查询后只有分数没有数据
        getRequest.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
				
				// 自定义检索源
				// 设置是否获取源
        boolean fetchSource = true;
        // includes获取的源字段，为name，以Time结尾，title
        String[] includes = new String[]{"name", "*Time","title"};
        // excludes排除的源字段，设置为空
        String[] excludes = Strings.EMPTY_ARRAY;
        // 创建FetchSourceContext
        FetchSourceContext fetchSourceContext = new FetchSourceContext(fetchSource,includes,excludes);
        // 检索源字段
        getRequest.fetchSourceContext(fetchSourceContext);
        
```

​		异步查询

```java
       client.getAsync(getRequest,RequestOptions.DEFAULT, new ActionListener<GetResponse>() {
            // 成功返回
            public void onResponse(GetResponse documentFields) {
                System.out.println("请求成功，响应："+documentFields.getSource());
            }
            // 失败
            public void onFailure(Exception e) {
                System.out.println("请求失败");
                e.printStackTrace();
            }
        });
```

​		GetRequest设置

```java
      // 设置routing
      request.routing("routing"); 

      // 设置parent
      request.parent("parent"); 

      // 设置preference
      request.preference("preference"); 

     	// 设置realtime
      request.realtime(false); 

      // 设置refresh
      request.refresh(true); 

      // 设置version
      request.version(2); 

      // 设置versionType
      request.versionType(VersionType.EXTERNAL); 
```

​		GetResponse操作

```java
        // 获取Source数据返回Map
        Map<String, Object> source = response.getSource();

        // 获取Source返回字符串
        String asString = response.getSourceAsString();

        // 获取Source返回字节数据
        byte[] asBytes = response.getSourceAsBytes();

        // 获取Source返回字节数据引用对象
        BytesReference asBytesRef = response.getSourceAsBytesRef();

        // 获取索引
        String index = response.getIndex();

        // 获取type
        String type = response.getType();

        // 获取version
        long version = response.getVersion();
```

#### Search API

​		Search API官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/_search_apis.html)

​		查询

```java
        SearchRequest searchRequest = new SearchRequest();
        // 设置搜索类型
        searchRequest.searchType(SearchType.DEFAULT);
        // 设置type，默认为空数组
        searchRequest.types(new String[0]);
        // 设置阈值，如果搜索请求扩展的分片数量超过阈值，则该阈值将强制执行预过滤器往返以基于查询重写来预过滤搜索分片。
        // 例如，如果一个分片基于其重写方法无法匹配任何文档，则此筛选器往返行程可以显着限制分片的数量。如果日期过滤器必须匹配，但分片范围和查询是不相交的。默认值为{@code 128}
        searchRequest.setPreFilterShardSize(128);
        // 设置应同时执行的分片请求数。此值应用作保护机制，以减少每个高级搜索请求触发的分片请求的数量。
        // 可以使用此数字限制到达整个群集的搜索，以减少群集负载。默认值随群集中节点数的增加而增加，但最多为{@code 256}。设置最大并发分片请求，默认0
        searchRequest.setMaxConcurrentShardRequests(20);
        // 设置应在协调节点上立即减少的分片结果数。如果请求中的分片数量可能很大，则此值应用作保护机制以减少每个搜索请求的内存开销。
        searchRequest.setBatchedReduceSize(512);
        // 控制如何处理不可用的具体索引(关闭或丢失)，如何将通配符表达式扩展为实际索引(所有、关闭或打开索引)，以及如何处理解析为没有索引的通配符表达式。
        // 默认要求存在每个指定的索引，仅将通配符扩展为开放索引，允许从通配符表达式解析任何索引(不返回错误)，通过抛出错误禁止使用封闭索引，并忽略被限制的索引。
        searchRequest.indicesOptions(IndicesOptions.strictExpandOpenAndForbidClosedIgnoreThrottled());
        // 设置查询的索引，默认空数组,*为通配符查询所有
        searchRequest.indices("*");
        // 设置执行搜索的首选项。默认值是随机分片
        // 可以再如下地址查看支持的选项https://www.elastic.co/guide/en/elasticsearch/reference/6.7/search-request-preference.html
        searchRequest.preference("_local");
        // 查询资源资源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 起始位置
        searchSourceBuilder.from(0);
        // 查询的条数
        searchSourceBuilder.size(10);
        // 设置是否进行分析
        searchSourceBuilder.explain(false);
        // 设置排序
        // searchSourceBuilder.sort(SortBuilders.fieldSort("age").order(SortOrder.ASC));

        // 设置Query对象，为查询所有，查询条件
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 设置Search的查询源
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : search.getHits().getHits()) {
                System.out.println(String.format("index: %s \t data: %s", hit.getIndex(), hit.getSourceAsMap().toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```

​		滚动查询

```java

        // 创建Search请求，以及Search资源构建器
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 设置Query对象，为查询所有，查询条件，每次查询2条
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(2);
        searchRequest.source(searchSourceBuilder);
        // 设置滚动查询的超时时间为1分钟
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        try {
            // 第一次查询
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 滚动查询Id
            String scrollId = search.getScrollId();
            Integer count = 1;
            System.out.println(String.format("第%s次查询：", count));
            ++count;
            for (SearchHit hit : search.getHits().getHits()) {
                System.out.println(String.format("index: %s \t data: %s", hit.getIndex(), hit.getSourceAsMap().toString()));
            }

            while (true) {
                if (scrollId != null && scrollId.trim().length() > 0) {
                    System.out.println(String.format("第%s次查询：", count));
                    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                    scrollRequest.scroll(TimeValue.timeValueSeconds(30));
                    SearchResponse response = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                    // 如果查询不到跳出循环
                    if (response.getHits().getHits().length == 0) {
                        break;
                    }
                    for (SearchHit hit : response.getHits().getHits()) {
                        System.out.println(String.format("index: %s \t data: %s", hit.getIndex(), hit.getSourceAsMap().toString()));
                    }
                    ++count;
                    scrollId =response.getScrollId();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```

​		批量查询

```java

        MultiSearchRequest multiSearchRequest = new MultiSearchRequest();
        // 创建第一个查询
        SearchRequest searchRequest1 = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.from(0);
        searchSourceBuilder1.size(2);
        searchSourceBuilder1.query(QueryBuilders.matchAllQuery());
        searchRequest1.source(searchSourceBuilder1);
        // 创建第二个查询
        SearchRequest searchRequest2 = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder2 = new SearchSourceBuilder();
        searchSourceBuilder2.from(2);
        searchSourceBuilder2.size(2);
        searchSourceBuilder2.query(QueryBuilders.matchAllQuery());
        searchRequest2.source(searchSourceBuilder2);

        // 将两个查询封装为一个multiSearchRequest
        multiSearchRequest.add(searchRequest1);
        multiSearchRequest.add(searchRequest2);

        try {
            MultiSearchResponse mSearchResponse = restHighLevelClient.msearch(multiSearchRequest, RequestOptions.DEFAULT);
            Integer count = 1;
            for (MultiSearchResponse.Item item : mSearchResponse.getResponses()) {
                SearchResponse response = item.getResponse();
                System.out.println(String.format("第%s次查询：",count));
                for (SearchHit hit : response.getHits().getHits()) {
                    System.out.println(hit.getSourceAsMap());
                }
                ++count;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```

​		模板搜索

```java

        // 创建模板查询
        SearchTemplateRequest request = new SearchTemplateRequest();
        // 查询所有索引
        request.setRequest(new SearchRequest("*"));
        request.setScriptType(ScriptType.INLINE);
        // 设置脚本查询所有，并且将size指定为动态
        request.setScript(
                "{" +
                        "  \"query\": { \"match_all\" : { } }," +
                        "  \"size\" : \"{{size}}\"" +
                        "}");

        // 指定脚本查询参数
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("size", 2);
        request.setScriptParams(scriptParams);

        try {
            SearchTemplateResponse templateResponse = restHighLevelClient.searchTemplate(request, RequestOptions.DEFAULT);
            SearchResponse response = templateResponse.getResponse();
            for (SearchHit hit : response.getHits().getHits()) {
                System.out.println(hit.getSourceAsMap());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```



#### Count API

```java
        // 查询所有索引数量
        CountRequest countRequest = new CountRequest("*");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        countRequest.source(searchSourceBuilder);
        try {
            CountResponse count = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            System.out.println(count.getCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
```







#### Exists API

​		Exists API官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-exists.html)

​		`GetRequest`就像[Get API](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-get.html)一样使用。 支持其所有[可选参数](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-get.html#java-rest-high-document-get-request-optional-arguments)。由于`exists()`仅返回`true`或`false`，因此我们建议您关闭提取功能`_source`和所有存储的字段，这样请求的内容会稍微减轻一些。

​		首先我们创建一个查询请求，然后设置返回的和storedFields字段全部都不要

```java
      GetRequest getRequest = new GetRequest(
          "posts", 
          "doc",   
          "1");    
      getRequest.fetchSourceContext(new FetchSourceContext(false)); 
      getRequest.storedFields("_none_");   
```

​		然后判断是否存在

```java
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
```

#### MultiGet（_mget）批量获取API

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-multi-get.html)

​		`MultiGetRequest`构建为空，然后添加MultiGetRequest.Item以配置要获取的内容：

```java
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        // 直接通过Index，Type，以及Id方式进行添加
        multiGetRequest.add("testes","test","1");
        multiGetRequest.add("testes","test","2");
        // 通过对象Item方式Add并且指定存储字段
        multiGetRequest.add(new MultiGetRequest.Item("testes","test","3").fetchSourceContext(FetchSourceContext.FETCH_SOURCE));
        MultiGetResponse multiGetItemResponses = client.multiGet(multiGetRequest, RequestOptions.DEFAULT);
        for (MultiGetItemResponse respons : multiGetItemResponses.getResponses()) {
            System.out.println(respons.getResponse().getSourceAsMap());
        }
```

#### Es信息查询

​		查询Es的集群名称，节点名称，以及版本和UUID

```java
        try {
            MainResponse response = restHighLevelClient.info(RequestOptions.DEFAULT);
            String clusterName = response.getClusterName().value();
            String nodeName = response.getNodeName();
            String version = response.getVersion().toString();
            String clusterUuid = response.getClusterUuid();

            System.out.println(String.format("集群名称:%s,节点名称:%s,Es版本:%s,集群UUID:%s",clusterName,nodeName,version,clusterUuid));
        } catch (IOException e) {
            e.printStackTrace();
        }
```

​		测试集群是否能够连接

```java
        boolean response = restHighLevelClient.ping(RequestOptions.DEFAULT);
```

### 删除

#### DELETE API

​		普通删除API官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-delete.html)

​		Delete By Query地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-delete-by-query.html)

​		我们创建一个DeleteRequest

```java
      DeleteRequest request = new DeleteRequest(
              "posts",    
              "doc",      
              "1");       
```

​		如下设置

```java
        // 根据Index，Type，Id查询
        DeleteRequest deleteRequest = new DeleteRequest("testes","test","2My7h3UBBSIfOfzfVRjW");

        // 设置routing
        deleteRequest.routing("routing");

        // 设置parent
        deleteRequest.parent("parent");

        // 设置超时时间，按单位以及时间
        deleteRequest.timeout(TimeValue.timeValueMinutes(2));
        // 字符串方式时间设置
        deleteRequest.timeout("2m");

        // 设置Version版本
        deleteRequest.version(2);

        // 设置Version版本
        deleteRequest.versionType(VersionType.EXTERNAL);

        // 刷新策略作为WriteRequest.RefreshPolicy实例
        deleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        // 刷新策略作为字符串
        deleteRequest.setRefreshPolicy("wait_for");
```

​		执行

```java
     		// 同步请求方式
        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);

        // 异步监听器
        ActionListener<DeleteResponse> deleteResponseActionListener = new ActionListener<DeleteResponse>() {
            @Override
            public void onResponse(DeleteResponse deleteResponse) {
                System.out.println(deleteResponse.toString());
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        };
        // 异步执行
        client.deleteAsync(deleteRequest,RequestOptions.DEFAULT,deleteResponseActionListener);
```

#### DeleteByQuery批量删除API

​		`DeleteByQueryRequest`可用于从索引中删除文档。它需要在其上执行删除的现有索引（或一组索引）。

```java

        // 创建UpdateByQuery请求，设置索引
        DeleteByQueryRequest deleteByQueryRequest =
                new DeleteByQueryRequest("testes", "testes1");
        // 默认情况下，版本冲突会中止该deleteByQueryRequest过程，但是您可以使用以下方法来计算它们
        deleteByQueryRequest.setConflicts("proceed");
        // 添加查询条件
        deleteByQueryRequest.setQuery(new TermQueryBuilder("email", "strings"));
        // 设置修改的数量
        deleteByQueryRequest.setSize(5);
        // 设置批量操作条数
        deleteByQueryRequest.setBatchSize(3);
        // 设置文档type类型
        deleteByQueryRequest.setDocTypes("test","_doc");
        BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        System.out.println(bulkByScrollResponse);

```

#### 删除索引

```java
        // 删除请求，删除test_create索引
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("test_create");
        // 设置请求超时时间2分钟
        deleteIndexRequest.timeout(TimeValue.timeValueMinutes(2));
        // 设置Master节点超时时间，2分钟
        deleteIndexRequest.masterNodeTimeout(TimeValue.timeValueMinutes(1));
        // 设置IndicesOptions控制如何解决不可用的索引以及如何扩展通配符表达式
        deleteIndexRequest.indicesOptions(IndicesOptions.lenientExpandOpen());
        try {
             restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
```



### 修改

#### Update API

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-update.html)

​		Request设置以及同步异步如上方所有API类似

​		使用脚本更新

```java
        // 根据Index，Type，Id查询
        UpdateRequest updateRequest = new UpdateRequest("testes","test","2sy7h3UBBSIfOfzfVxjv");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("count", 4);

        // 创建脚本,把字段+上4
        Script inline = new Script(ScriptType.INLINE, "painless",
                "ctx._source.age += params.count", parameters);
        updateRequest.script(inline);
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update);
```

​		更新文档对原有字段不覆盖

```java
        // 根据Index，Type，Id查询
        UpdateRequest updateRequest = new UpdateRequest("testes","test","2sy7h3UBBSIfOfzfVxjv");
        // 创建Map存储修改的数据
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("age",18);
        update.put("name","黄康");
        updateRequest.doc(update);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse);
```

​			upsert,如果不存在则进行新增，如果索引不存在则会使用upsert 的 Map的数据进行写入

```java
   			// 如果索引存在进行修改的数据
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("age",19);
        update.put("name","黄康19");
        // 如果索引不存在则使用upsert的数据
        Map<String, Object> upsert = new HashMap<String, Object>();
        upsert.put("age",18);
        upsert.put("name","黄康");
        // 修改的数据
        updateRequest.doc(update);
        // 不存在创建的数据
        updateRequest.upsert(upsert);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse);
```

#### UpdateByQuery API

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-update-by-query.html#java-rest-high-document-update-by-query)

​		`UpdateByQueryRequest`可用于更新索引中的文档。它需要在其上执行更新的现有索引（或一组索引）。

```java

        // 创建UpdateByQuery请求，设置索引
        UpdateByQueryRequest updateByQueryRequest =
                new UpdateByQueryRequest("testes", "testes1");
        // 默认情况下，版本冲突会中止该UpdateByQueryRequest过程，但是您可以使用以下方法来计算它们
        updateByQueryRequest.setConflicts("proceed");
        // 添加查询条件
        updateByQueryRequest.setQuery(new TermQueryBuilder("email", "string"));
        // 设置修改的数量
        updateByQueryRequest.setSize(5);

        // 设置批量操作条数
        updateByQueryRequest.setBatchSize(3);
        // 设置文档type类型
        updateByQueryRequest.setDocTypes("test","_doc");
        // 设置Pipeline
        updateByQueryRequest.setPipeline("pipeline");
        // 设置脚本
        Script inline = new Script("ctx._source.age += 4");
        updateByQueryRequest.setScript(inline);
        // UpdateByQueryRequest可以使用并行sliced-scroll有setSlices
        updateByQueryRequest.setSlices(2);
        // 设置游标时间
        updateByQueryRequest.setScroll(TimeValue.timeValueMinutes(2));
        // 设置路由
        updateByQueryRequest.setRouting("routing");
        // 设置超时时间
        updateByQueryRequest.setTimeout(TimeValue.timeValueMinutes(2));
        // 设置通过查询调用更新后刷新索引
        updateByQueryRequest.setRefresh(true);
        // 设置索引选项
        updateByQueryRequest.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
```

### Bulk (_bulk)批量操作API

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-bulk.html)

​		`BulkRequest`可用于通过单个请求执行多个索引，更新和/或删除操作。它要求至少一个操作要添加到Bulk请求中。

​		支持DeleteRequest、UpdateRequest、IndexRequest		

```java
        // 下面是批量操作，我们新增了3条索引数据
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest("testes", "test", "1")
                .source(XContentType.JSON,"name", "名字1"));
        bulkRequest.add(new IndexRequest("testes", "test", "2")
                .source(XContentType.JSON,"field", "名字2"));
        bulkRequest.add(new IndexRequest("testes", "test", "3")
                .source(XContentType.JSON,"field", "名字3"));
        client.bulk(bulkRequest,RequestOptions.DEFAULT);
```



### Reindex（reindex）重新索引API

​		官网地址：[点击进入](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.7/java-rest-high-document-reindex.html)

​		一个`ReindexRequest`可以用来从一个或多个索引文件复制到目标指数。

​		它要求在请求之前可能存在或可能不存在的现有源索引和目标索引。Reindex不会尝试设置目标索引。它不会复制源索引的设置。您应该在运行_reindex操作之前设置目标索引，包括设置映射，分片计数，副本等。

```java
        ReindexRequest reindexRequest = new ReindexRequest();
        // 设置reindex源索引
        reindexRequest.setSourceIndices("index1","index2");
        // 设置源查询条件
        reindexRequest.setSourceQuery(new BoolQueryBuilder());
        // 设置源索引类型
        reindexRequest.setSourceDocTypes("type1","type2");
        // 设置批量数量
        reindexRequest.setSourceBatchSize(1000);


        // 设置操作类型，如果reindex时出现ID重复情况则报错
        reindexRequest.setDestOpType("create");
        // 设置文档类型
        reindexRequest.setDestDocType("destType");
        // 设置版本类型，保留或者重置或者其他
        reindexRequest.setDestVersionType(VersionType.EXTERNAL);
        // 设置reindex目标索引
        reindexRequest.setDestIndex("new_index");
        // 设置目标路由
        reindexRequest.setDestRouting("routing");

        // 设置脚本
        Script inline = new Script("ctx._source.age += 4");
        reindexRequest.setScript(inline);
        // 设置游标Scroll时间
        reindexRequest.setScroll(TimeValue.timeValueMinutes(3));
```





# 文档（Document）API