# Mvc配置

​		我们可以通过Java类配合上注解的方式来动态的配置我们的Mvc配置。

​		如下，新建配置类。CustomWebMvcConfig

```java
/**
 * @Author BigKang
 * @Date 2020/2/28 5:33 PM
 * @Summarize WebMvc配置
 */
@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

    // 路径匹配规则,帮助我们对于某些path路径的匹配规则，一般使用较少
    public void configurePathMatch(PathMatchConfigurer configurer) {}

    // 对应url解析不同的返回资源类型，例如/**/***.json,我们设置MediaType为utf8的json，其实就是根据不同的url路径为我们定义返回的类型，如json，mp4，zip等等
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {}

    // 配置异步请求处理
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {}

    // 定义使用Servlet方式加载静态资源
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) { }

    // 自定义Formatter格式化以及Convert转换
    public void addFormatters(FormatterRegistry registry) {}

    // 自定义添加拦截器
    public void addInterceptors(InterceptorRegistry registry) {}

    // 自定义静态资源路径映射
    public void addResourceHandlers(ResourceHandlerRegistry registry) {}

    // 添加跨域映射
    public void addCorsMappings(CorsRegistry registry) {}

    // 添加视图控制器
    public void addViewControllers(ViewControllerRegistry registry) {}

    // 添加视图解析器
    public void configureViewResolvers(ViewResolverRegistry registry) {}

    // 自定义添加参数解析器，可以解析方法中的参数，结合注解使用更佳
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {}

    // 自定义添加返回结果处理器，可以统一处理返回结果，结合注解使用更佳
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {}

    // 自定义配置序列化方式，包括double精度保留问题，null问题等等，会覆盖掉默认的相同类型的转换器
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {}

    // 不影响缺省消息转换器，不影响默认的MessageConverters，如果没有该类型转换器则使用我们的自定义转换器
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {}

    // 自定义添加异常视图处理器，发生异常时统一处理并且返回
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {}

    // 不影响默认的异常处理器，如果没有该类型异常处理器则使用我们自定义的
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {}

}
```



# 过滤器

​		首先我们新建一个过滤器。Order表示执行顺序，@WebFilter是Servlet的注解，可以帮助我们简便的定义过滤器。

```java
/**
 * @Author BigKang
 * @Date 2020/2/28 5:37 PM
 * @Summarize 自定义过滤器
 */
@WebFilter(filterName = "customFilter",urlPatterns = "/*")
public class CustomFilter implements Filter {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("初始化自定义过滤器！！！");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 判断request请求中是否包含test请求头
        String test = httpServletRequest.getHeader("test");
        if(StringUtils.isEmpty(test)){
            // 重置响应
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            Map<String,Object> map = new HashMap<>();
            map.put("code",403);
            map.put("message","非法访问！！！");
            map.put("data",null);

            PrintWriter pw = response.getWriter();
            pw.write(objectMapper.writeValueAsString(map));
            pw.flush();
            pw.close();
        }else {
            filterChain.doFilter(request,response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("销毁自定义过滤器！！！");
    }
}
```

​			然后我们在启动类上加上注解。@ServletComponentScan，扫描统计包以及子包的Servlet组件。

```java
@SpringBootApplication
@ServletComponentScan
public class BootMvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootMvcApplication.class,args);
    }
}
```

​			下面是关于@WebFilter的详细概述。

```java
    // 描述
    String description() default "";

		// 过滤器显示名称
    String displayName() default "";

		// Web初始化参数，和xml中的<init-param> 等价
    WebInitParam[] initParams() default {};
	
		// 指定过滤器的名称
    String filterName() default "";

		// 小图标
    String smallIcon() default "";

		// 大图标
    String largeIcon() default "";

		// 指定过滤器将作用于哪些Servlet的名字
    String[] servletNames() default {};

		// 等价于urlPatterns
    String[] value() default {};
	
		// 指定过滤器的匹配模式，和xml中的<url-pattern> 等价
    String[] urlPatterns() default {};

		// 过滤器的转发模式FORWARD,INCLUDE,REQUEST,ASYNC,ERROR;(默认为REQUEST请求)
    DispatcherType[] dispatcherTypes() default {DispatcherType.REQUEST};

		// 过滤器是否支持异步操作（默认false）
    boolean asyncSupported() default false;
```

​			关于过滤器使用Order排序无效的解决方案，参考此篇博客：https://www.cnblogs.com/ixixi/p/11685269.html

# 拦截器

​			拦截器并不属于MVC属于Spring，为了方便所以放在一起写在这里。

​			首先我们创建一个拦截器，并且把它注册到Spring容器中。

​			代码示例如下：

```java
/**
 * @Author BigKang
 * @Date 2020/2/28 4:41 PM
 * @Summarize 自定义拦截器
 */
@Component
public class CustomHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 在执行方法之前执行拦截操作
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws IOException
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // 获取目标的方法
        HandlerMethod method = (HandlerMethod) handler;

        // 获取方法上的注解有多少个，我们可以通过这个方法进行注解的操作，拿到这个方法我们可以执行非常多的操作
//        System.out.println(method.getMethod().getAnnotations().length);

        // 判断request请求中是否包含test请求头
        String test = request.getHeader("test");
        if(StringUtils.isEmpty(test)){
            // 重置响应
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            Map<String,Object> map = new HashMap<>();
            map.put("code",403);
            map.put("message","非法访问！！！");
            map.put("data",null);

            PrintWriter pw = response.getWriter();
            pw.write(objectMapper.writeValueAsString(map));
            pw.flush();
            pw.close();
            return false;
        }

        // 正常返回
        return true;
    }

    /**
     * 在执行方法之后，还没有返回视图时执行（在执行之后执行，再返回给用户），如果被preHandle拦截则不执行
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView){
        System.out.println("执行方法完毕！！！");
    }

    /**
     * 在执行方法之后，返回视图之后执行（在执行之后，再返回给用户之后执行，主要用于日志记录，以及异常处理，资源释放等等）
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex){
        System.out.println("返回数据完毕！！！");
    }
}
```

​			然后我们再新增MVC的配置，将这个拦截器注入。

```java
/**
 * @Author BigKang
 * @Date 2020/2/28 5:33 PM
 * @Summarize WebMvc配置
 */
@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private CustomHandlerInterceptor customHandlerInterceptor;

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customHandlerInterceptor);
    }
}

```





# 404配置

​			此处采用Rest方式返回json串，首先配置SpringMVC的no-handler-found异常，让他抛出这个异常，然后给这个资源不添加映射，最后我们使用全局异常处理进行返回。

```
spring:
  mvc:
    # 出现没有Handler映射时抛出这个异常
    throw-exception-if-no-handler-found: true
  resources:
    # 不为资源添加映射
    add-mappings: false
```

异常捕获，这里的ResultVo为自己封装的统一返回结果，data，message，code

```java
/**
 * @Author BigKang
 * @Date 2020/1/7 3:11 PM
 * @Summarize 全局异常捕获
 */
@ControllerAdvice
@Slf4j
@Component
public class CustomExceptionHandler {

    /**
     * 404配置相应
     * @return
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    public ResultVo noHandlerFound(HttpServletResponse response){
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return ResultVo.result(HttpServletResponse.SC_NOT_FOUND, "请求的资源不存在");
    }
}
```



# 重定向至其他页面

例如将请求重定向到百度度首页

```java
@Controller
public class TestController {

    @GetMapping("baidu")
    public void redirectBaidu (HttpServletResponse response){
        try {
            response.sendRedirect("https://www.baidu.com");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```



```
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial main restricted universe multiverse>>/etc/apt/sources.list
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse>>/etc/apt/sources.list
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse>>/etc/apt/sources.list
echo deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-security main restricted universe multiverse>>/etc/apt/sources.list
```

