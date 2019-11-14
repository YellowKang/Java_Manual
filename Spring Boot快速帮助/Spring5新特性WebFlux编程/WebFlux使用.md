# 引入依赖

我们首先引入WebFlux的依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
```

# 然后编写代码

​		我们编写一个controller控制器，我们可以看到我们返回了两个控制器，一个是getMono，另一个是getFlux，然后我们分别用Mono和Flux定义了返回类型为String，然后我们分别使用Mongo.just和Flux.just返回了数据

```java
@RequestMapping("test")
@RestController
public class TestFlux {

    @GetMapping("getMono")
    public Mono<String> getMono(){
        return Mono.just("返回测试数据");
    }

    @GetMapping("getFlux")
    public Flux<String> getFlux(){
        return Flux.just("返回测试数据");
    }

}

```

# WebFlux和Servlet的不同

​			有一个Reactive Streams规范（响应式流），响应式流剪短的来说就是将数据以流的方式以异步加载，也就是异步和同步的区别，与传统的Servlet不同，Servlet采用同步方式，Servlet会接收到客户端发起的请求，然后进行处理，在处理完毕后将数据返回给客户端，那么在这个请求的过程中，如果客户端没有接收到服务端的相应他会一直等待，直到服务端将数据返回给客户端，这个我们称之为同步，而WebFlux是以响应式流的方式将数据返回，也就是说我们现在将数据以流的方式进行返回，我们再返回的时候可能会有多个响应，例如返回10条数据，我们在第6条的时候阻塞住了，那么我们就会先将前面的5条数据返回会来，等到阻塞结束，再将剩余的数据返回回来。

​			那么Reactive Streams和WebFlux到底有什么关系呢？WebFlux采用了一个框架叫做Reactor（异步事件驱动框架），而Reactor是遵循Reactive Streams规范而实现出来的一个框架基础，WebFlux又是基于Reactor产生的一个Web框架。

# WebFlux的核心