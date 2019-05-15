# 使用addCorsMappings配置统一跨域

项目中编写config包，然后新建类CorsConfig

```
@Configuration
public class CorsConfig implements WebMvcConfigurer {
 
    @Override  
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  
                .allowedOrigins("*")  
                .allowCredentials(true)  
                .allowedMethods("GET", "POST", "DELETE", "PUT")  
                .maxAge(3600);  
    }  
 
}  
```

# 使用Filter统一配置跨域

在spring中配置bean对象，能被容器管理到，SpringBootApplication或者configretion等等

```
	@Bean
	public Filter corsFilter() {
		return new CorsFilter();
	}
```

然后编写CorsFilter类，在里面写入

```
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Collection<String> origins = response.getHeaders("Access-Control-Allow-Origin");
        if(null!=origins&&origins.size()>0){

        }else {
            if(!StringUtils.isEmpty(request.getHeader("Origin"))){
                response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type");
                response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
                response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"));
                response.setHeader("Allow", response.getHeader("Access-Control-Allow-Methods"));

            }}
        if(request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString())){
            return;
        }
        chain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
```

