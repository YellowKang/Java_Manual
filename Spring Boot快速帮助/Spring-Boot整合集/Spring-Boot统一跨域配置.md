# 统一跨域

项目中编写config包，然后新建类CorsConfig

```

/**
 * @Author BigKang
 * @Date 2020/1/7 3:23 PM
 * @Summarize 自定义公共Web配置
 */
@Configuration
public class CustomWebCommonConfig{

    /**
     * 跨域配置
     * @return
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        //3) 允许的请求方式
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        config.setMaxAge(3600L);
        // 4）允许的头信息
        config.addAllowedHeader("*");

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //3.返回新的CorsFilter.
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(configSource));
        bean.setOrder(0);
        return bean;
    }

}
```

# 在线测试

打开浏览器，然后F12，点击Console控制台，直接执行js即可测试



```
var xhr = new XMLHttpRequest();
xhr.open('GET', 'http://localhost:8083/actuator');
xhr.send(null);
xhr.onload = function(e) {
    var xhr = e.target;
    console.log(xhr.responseText);
}
```

