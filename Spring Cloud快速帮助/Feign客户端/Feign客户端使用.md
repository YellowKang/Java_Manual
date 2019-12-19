# 依赖

实际依赖这个是在dependencies中添加的实际依赖

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```

版本依赖，这个是放在dependencyManagement中的，定义cloud和alibaba的版本

# 配置

编写bootstrap.yml，不需要application.properties

```

```

注释版

```

```

# 代码

## 注解

```
服务发现
@EnableDiscoveryClient

启用feign客户端
@EnableFeignClients

配置刷新
@RefreshScope

服务调用
@FeignClient(name = "test",path = "/config")
```

# 打印Feign调用日志级别

表示某一个包下面的

```
logging:
  level:
    com.kang.boot.UserServiceClient:debug
```

打印某一个服务的日志级别

```
feign:
	client:
		config:
			user-service:
				loggerLevel: full
```

所有服务的日志级别

```
feign:
	client:
		config:
			default:
				loggerLevel: full
```

NONE：不输出日志

BASIC：输出请求方法、`URL`、响应状态码、执行时间

HEADERS：基本信息以及请求和响应头

FULL：请求和响应的`heads`、`body`、`metadata`，建议使用这个级别

# Feign配置详情

user-service表示表示服务名称

```
feign:
	client:
		config:
			user-service:
				connectTimeout: 5000 #连接超时时间
				readTimeout:	5000 #读取超时时间
				loggerLevel: full #日志级别
				errorDecoder: com.example.SimpleErrorDecoder #错误解码器
				retryer: com.example.SimpleRetryer #重试策略
				requestInterceptors:
					- com.example.FooRequestInterceptor	#拦截器
				decode404: false #是否处理404
				encoder: com.example.SimpleEncoder #编码器
				decoder: com.example.SimpleDecoder #解码器
				contract: com.example.SimpleContract #契约
```

# Feign性能优化

### HttpClient

引入依赖

```
					<dependency>
             <groupId>io.github.openfeign</groupId>
             <artifactId>feign-httpclient</artifactId>
          </dependency>
```

编写配置

```
feign:
  httpclient:
    enabled: true #启用httpclient
    max-connections: 200 #feign的最大连接数
    max-connections-per-route: 50 #feign单个路径的最大连接数
```

### OkHttp

引入依赖，需要添加版本

```
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
    <version>${version}</version>
</dependency>
```

编写配置

```
feign:
  httpclient:
    enabled: false #关闭httpclient
    max-connections: 200 #feign的最大连接数
    max-connections-per-route: 50 #feign单个路径的最大连接数
  okhttp:
		enable: true #启动okhttp
  # 请求与响应的压缩以提高通信效率
  compression:
    request:
      enabled: true
      min-request-size: 2048
      mime-types: text/xml,application/xml,application/json
    response:
      enabled: true
```



```
/**
 * 配置 okhttp 与连接池
 * ConnectionPool 默认创建5个线程，保持5分钟长连接
 */
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class) //SpringBoot自动配置
public class OkHttpConfig {

    // 默认老外留给你彩蛋中文乱码，加上它就 OK
    @Bean
    public Encoder encoder() {
        return new FormEncoder();
    }

    @Bean
    public okhttp3.OkHttpClient okHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                //设置连接超时
                .connectTimeout(10, TimeUnit.SECONDS)
                //设置读超时
                .readTimeout(10, TimeUnit.SECONDS)
                //设置写超时
                .writeTimeout(10, TimeUnit.SECONDS)
                //是否自动重连
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(10, 5L, TimeUnit.MINUTES))
                .build();
    }
}
```

