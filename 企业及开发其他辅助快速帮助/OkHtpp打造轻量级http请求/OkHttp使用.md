# 简介

​		OkHttp是一个默认有效的HTTP客户端

​				HTTP / 2支持允许对同一主机的所有请求共享套接字

​				连接池减少了请求延迟（如果HTTP / 2不可用）

​				透明GZIP缩小了下载大小

​				响应缓存可以完全避免网络重复请求

​		当网络很麻烦时，OkHttp坚持不懈：它将从常见的连接问题中无声地恢复。如果您的服务有多个IP地址，如果第一次连接失败，OkHttp将尝试备用地址。这对于IPv4 + IPv6和冗余数据中心中托管的服务是必需的。OkHttp支持现代TLS功能（TLS 1.3，ALPN，证书固定）。它可以配置为回退以实现广泛的连接。

​		使用OkHttp很容易。它的请求/响应API采用流畅的构建器和不变性设计。它支持同步阻塞调用和带回调的异步调用。

​		简单的来说，OkHttp轻量级，线程池效率高，压缩数据接受快，目前来说相对HttpClient而言OkHttp更加有优势。

# 依赖

​		自定义依赖版本

```xml
    		<dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.8.1</version>
        </dependency>
```

​		使用SpringBoot自定义内置版本

```xml
    		<dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
        </dependency>
```





# 自定义SpringBoot整合

## 配置文件

yml版本

```properties
okhttp:
  timeout: 7000 #超时时间
  maxConnection: 200 #最大连接数
  coreConnection: 10 #核心连接数
  resetConnection: false #是否重试
  maxHostConnection: 30 #单域名最大线程数
```

properties版本

```properties
okhttp.timeout=7000 #超时时间
okhttp.maxConnection=200 #最大连接数
okhttp.coreConnection=10 #核心连接数
okhttp.resetConnection=false #是否重试
okhttp.maxHostConnection=30 #单域名最大线程数
```

## properties配置类

```Java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author BigKang
 * @Date 2020/9/28 5:27 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize OkHttp配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "okhttp")
public class OkHttpProperties {

    /**
     * 超时时间
     */
    private Integer timeout = 7000;

    /**
     * 最大连接数
     */
    private Integer maxConnection = 8;

    /**
     * 核心连接数
     */
    private Integer coreConnection = 4;

    /**
     * 是否重试
     */
    private Boolean resetConnection = true;

    /**
     * 单域名最大线程数
     */
    private Integer maxHostConnection = 4;
}
```

## config配置类

```java
import com.kang.wangke.properties.OkHttpProperties;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author BigKang
 * @Date 2020/9/28 5:30 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize OkHttpConfig配置
 */
@Configuration
@Data
public class OkHttpConfig {

    private final OkHttpProperties properties;

    @Autowired
    public OkHttpConfig(OkHttpProperties properties) {
        this.properties = properties;
    }

    @Bean
    public OkHttpClient okHttpClient(){
        // 创建Dispatcher，对请求线程进行控制
        Dispatcher dispatcher = new Dispatcher();
        // 设置单域名最大连接
        dispatcher.setMaxRequestsPerHost(properties.getMaxHostConnection());
        // 设置最大连接
        dispatcher.setMaxRequests(properties.getMaxConnection());
        // 创建OkHttpClient连接
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .dispatcher(dispatcher)
                .retryOnConnectionFailure(properties.getResetConnection())
                // 拦截器可以自定义添加或者不添加拦截器
                //.addInterceptor(new CustomInterceptor())
                .callTimeout(properties.getTimeout(), TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(properties.getMaxConnection(),properties.getCoreConnection(),TimeUnit.MILLISECONDS))
                .build();
        return okHttpClient;
    }

}
```

目前OkHttp的线程池其实是和缓存线程池类似的（几乎一模一样）

```java
RealConnectionPool这个类中我们可以看到他的源码的线程池的定义

  /**
   * Background threads are used to cleanup expired connections. There will be at most a single
   * thread running per connection pool. The thread pool executor permits the pool itself to be
   * garbage collected.
   */
  private static final Executor executor = new ThreadPoolExecutor(0 /* corePoolSize */,
      Integer.MAX_VALUE /* maximumPoolSize */, 60L /* keepAliveTime */, TimeUnit.SECONDS,
      new SynchronousQueue<>(), Util.threadFactory("OkHttp ConnectionPool", true));
```

他这里采用的是Integer的Max，也就是无界队列，对线程池的控制完全是由OkHttp来进行控制，根据他自己的自定义策略去控制线程数，其实自定义的线程池更能根据项目实际情况进行配置。

# 拦截器

我们可以在http发送请求时添加拦截器进行拦截，我们可以自定义一个拦截器进行记录日志和其他操作

创建OkHttpClient时添加拦截器

```java
OkHttpClient okHttpClient = new OkHttpClient()
							.newBuilder()
							.addInterceptor(new CustomInterceptor())
							.build();
```

自定义拦截器类

```java
/**
 * @Author BigKang
 * @Date 2019/8/27 3:57 PM
 * @Summarize 自定义OkHttpClient拦截器
 */
@Slf4j
public class CustomInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取request请求对象
        Request request = chain.request();
        // 请求开始时间
        long startTime = System.currentTimeMillis();
        // 发送请求
        Response response =  chain.proceed(request);
        // 请求结束相应时间
        long endTime = System.currentTimeMillis();

        log.info(String.format("开始请求,请求url:%s,请求主体长度%s,请求头%s",request.url(),request.body().contentLength(),request.headers()));
        log.info(String.format("请求结束,相应时间：%s毫秒,响应数据长度：%s字节,响应头：%s",endTime-startTime,response.body().contentLength(),response.headers()));
        return response;
    }
}
```

## Application Interceptors与Network Interceptors的区别

​			在OkHttp中拦截器分为两个，分别是：

​					Application Interceptors（应用拦截器）

​					Network Interceptors（网络拦截器）

​			那么他们的区别是什么呢

​					应用拦截器只关心我们的请求和他所响应的结果，如果我们在请求的时候我们转发或者重定向到其他地址我们是拦截不到的，但是我们如果使用网络拦截器他能帮我们拦截到重定向或者转发的请求，从而更深刻了解请求过程，如果我们只关注请求结果的话推荐使用应用拦截器，如果更加关注请求的过程的话推荐使用网络拦截器

# 使用

## 发送Get请求

### 同步

```java
        // 请求Url
        String url = "http://bigkang.club/";
        // 创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建request请求
        Request request = new Request.Builder().url(url).build();
        // 捕获异常
        try {
            // 发起请求
            Response execute = okHttpClient.newCall(request).execute();
            // 判断是否请求成功，根据Response的响应状态码
            if (execute.isSuccessful()) {
                // 获取返回Body主体信息
                System.out.println(execute.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```

### 异步

使用@Test注解测试请注意，如果当前线程执行完毕直接销毁，不会等待其他线程执行完毕，所以睡眠一秒，否则不一定会打印出相应结果《只要我响应的够快，Jvm就销毁不了》

```java
        // 请求Url
        String url = "http://bigkang.club/";
        // 创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建Request请求
        Request request = new Request.Builder().url(url).build();
        // 创建连接请求，调用异步
        okHttpClient.newCall(request).enqueue(new Callback() {
            // 请求失败回调
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(call);
            }
            // 请求成功回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 输出相应数据
                System.out.println(response.body().string());
            }
        });
        System.out.println("1111");
        Thread.sleep(1000);
```

## 发送Post请求

此post方法采用Mock数据，在线easy-mock，感兴趣的朋友可以注册一个，挺好用的

https://www.easy-mock.com/

### 同步

```java
        // 请求Url
        String url = "https://www.easy-mock.com/mock/5d649cf8fe04523cd4a9af88/bigkang/okhttp-post";
        // 创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建Request请求
        Request request = new Request.Builder().url(url).post(new FormBody.Builder().build()).build();
        // 创建连接请求，调用异步
        try {
            // 发起请求
            Response execute = okHttpClient.newCall(request).execute();
            // 判断是否请求成功
            if(execute.isSuccessful()){
                // 输出响应数据
                System.out.println(execute.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```



### 异步

```java
        // 请求Url
        String url = "https://www.easy-mock.com/mock/5d649cf8fe04523cd4a9af88/bigkang/okhttp-post";
        // 创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建Request请求
        Request request = new Request.Builder()
          .url(url)
          //指定post请求，并且创建表单
          .post(new FormBody.Builder().build())
          .build();
        // 创建连接请求，调用异步
        okHttpClient.newCall(request).enqueue(new Callback() {
            // 请求失败回调
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(call);
            }
            // 请求成功回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 输出相应数据
                System.out.println(response.body().string());
            }
        });
        System.out.println("1111");
        Thread.sleep(1000);
```

## 其他方法

其他方法如Put，Delete，等等就不再过多演示了

```java
       	//put方法
				Request put = new Request.Builder()
          .url(url)
          .put(new FormBody.Builder().build())
          .build();
				//delete方法
       	Request delete = new Request.Builder()
          .url(url)
          .delete(new FormBody.Builder().build())
          .build();

```

## 带表单json以及头请求请求

### 带表单数据请求

```java
        // 请求Url
        String url = "https://www.easy-mock.com/mock/5d649cf8fe04523cd4a9af88/bigkang/okhttp-post";
        // 创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建Request请求
        Request request = new Request.Builder()
                .url(url)
          			//post添加表单
                .post(new FormBody.Builder()
                   .add("name","bigkang")
                   .add("status","迷茫")
                   .build())
                .build();
        try {
            // 发起请求
            Response execute = okHttpClient.newCall(request).execute();
            // 判断是否请求成功
            if(execute.isSuccessful()){
                // 输出响应数据
                System.out.println(execute.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```

### 带json数据请求

```java
        // 请求Url
        String url = "http://localhost:8080/test/nice";
        // 创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 请求书数据
        String requestData = "{\"name\":\"BigKang\"}";
        // 创建请求体,指定json格式请求，字符编码utf8
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData);
        // 创建Request请求
        Request request = new Request.Builder()
                .url(url)
                // 传入请求体
                .post(requestBody)
                .build();
        try {
            // 发起请求
            Response execute = okHttpClient.newCall(request).execute();
            // 判断是否请求成功
            if(execute.isSuccessful()){
                // 输出响应数据
                System.out.println(execute.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```



### 带请求头请求

okhttp请求代码

```java
        // 请求Url
        String url = "http://localhost:8080/test/nice";
        // 创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 请求书数据
        String requestData = "{\"name\":\"BigKang\"}";
        // 创建请求体,指定json格式请求，字符编码utf8
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestData);
        // 创建Request请求
        Request request = new Request.Builder()
                .url(url)
                // 传入请求体
                .post(requestBody)
                .addHeader("test","testHeader")
                .build();
        try {
            // 发起请求
            Response execute = okHttpClient.newCall(request).execute();
            // 判断是否请求成功
            if(execute.isSuccessful()){
                // 输出响应数据
                System.out.println(execute.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```

接受服务端代码

```java
    @PostMapping("testHeader")
    public String testHeader(String name, HttpServletRequest request){
        return "你好收到请求头：" + request.getHeader("test");
    }
```

