# 引入依赖

```
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
 	<version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
 	<version>2.9.2</version>
</dependency>
```

# 编写配置类

## 单环境使用

```
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(webApiInfo())// 调用apiInfo方法,创建一个ApiInfo实例,里面是展示在文档页面信息内容
                .select()//创建ApiSelectorBuilder对象
                .apis(RequestHandlerSelectors.basePackage("com.kang.test"))//扫描的包
                .paths(Predicates.not(PathSelectors.regex("/error.*")))//过滤掉错误路径
                .build();
    }

    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("BigKang-----V4.0API接口文档")
                .description("BigKang最新4.0接口文档")
                .termsOfServiceUrl("http://bigkang.club:3000")
                .version("4.0")
                .build();
    }

}

```



## 双环境区分

```
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())// 调用apiInfo方法,创建一个ApiInfo实例,里面是展示在文档页面信息内容
                .select()//创建ApiSelectorBuilder对象
                .apis(RequestHandlerSelectors.basePackage("com.kang.test"))//扫描的包
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))//过滤掉admin接口
                .paths(Predicates.not(PathSelectors.regex("/error.*")))//过滤掉错误路径
                .build();
    }
    @Bean
    public Docket adminApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())// 调用apiInfo方法,创建一个ApiInfo实例,里面是展示在文档页面信息内容
                .select()//创建ApiSelectorBuilder对象
                .apis(RequestHandlerSelectors.basePackage("com.kang.test"))//扫描的包
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))//过滤的接口
                .paths(Predicates.not(PathSelectors.regex("/error.*")))//过滤掉错误路径
                .build();
    }

    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("BigKang----V4.0后台管理接口文档")
                .description("BigKang最新4.0接口文档")
                .termsOfServiceUrl("http://bigkang.club:3000")
                .version("4.0")
                .build();
    }

    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("BigKang----V4.0API接口文档")
                .description("BigKang最新4.0接口文档")
                .termsOfServiceUrl("http://bigkang.club:3000")
                .version("4.0")
                .build();
    }

}

```

