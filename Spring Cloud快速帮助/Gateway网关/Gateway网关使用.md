# 官方文档地址

Spring Cloud Gateway官方文档地址： [点击进入](https://spring.io/projects/spring-cloud-gateway#learn)

# 引入依赖

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
```

# 编写配置

```

```



# 配置跨域

## 代码配置

```java
@Configuration
public class CustomGatewayConfig {

    /**
     * 网关跨域配置
     * @return
     */
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
```



# Gateway工作流程

​			下面是Gateway网关请求进来之后我们的工作流程，首先他会经过Gateway HandlerMapping（ 也就是请求路径映射处理器，实现类RoutePredicateHandlerMapping.class），然后到Gateway WebHandler（通过他找到我们的服务实例映射，实现类FilteringWebHandler.class），然后由这个Web Handler中的多个过滤器进行过滤，然后通过我们的Proxy代理请求到我们的各个服务中，然后将服务的数据响应回代理。

​		![spring_cloud_gateway_diagram.png](https://i.loli.net/2020/01/09/6ADnpiHgsxrZPy7.png)

​			下面是我个人根据Gateway的流程图的理解。

​		





