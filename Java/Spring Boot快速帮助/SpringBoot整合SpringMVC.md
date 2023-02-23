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

​		注意千万不能配置SpringBoot默认的资源映射器，可以自定义，但不能使用/**，以及默认路径否则会引起一系列的问题

```
   private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    CLASSPATH_RESOURCE_LOCATIONS);
        }
    }
```

# 统一HTTP响应



```java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author BigKang
 * @Date 2020/1/14 2:15 PM
 * @Summarize 统一返回Vo对象
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    private T data;
    private Integer code;
    private String message;

    public Result(T data, Integer code) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code) {
        this.code = code;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> Result<T> result(ResultEnum resultEnum) {
        return result(null, resultEnum.getCode(), resultEnum.getMsg());
    }
    public static <T> Result<T> result(T data, ResultEnum resultEnum) {
        return result(data, resultEnum.getCode(), resultEnum.getMsg());
    }

    public static <T> Result<T> result(T data, Integer code, String message) {
        return new Result<T>(data, code, message);
    }

    public static <T> Result<T> result(T data, Integer code) {
        return new <T>Result<T>(data, code);
    }

    public static <T> Result<T> error(String message) {
        return new <T>Result<T>(ResultEnum.ERROR.getCode(), message);
    }

    public static <T> Result<T> error(ResultEnum resultEnum) {
        return new <T>Result<T>(resultEnum.getCode(), resultEnum.getMsg());
    }

    public static <T> Result<T> success() {
        return result(null, ResultEnum.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return result(data, ResultEnum.SUCCESS);
    }

}
```



```java

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author BigKang
 * @Date 2020/6/23 2:15 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize 返回枚举
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    // 成功
    SUCCESS(200, "请求成功"),

    // Token令牌异常  30开头
    TOKEN_PAST(301, "登录超时，请重新登录"),
    TOKEN_ERROR(302, "非法令牌"),

    // 登录异常        31开头
    LOGIN_FAILURE(311, "用户名或密码错误"),
    LOGIN_CODE_ERROR(312, "验证码错误"),
    REMOTE_ERROR(313, "异地登录"),
    LOGOUT_CODE_ERROR(314, "登出失败，令牌为空"),

    // 系统异常
    NO_LOGIN(401,"用户未登录,请先登录"),
    NO_PERMISSIONS(403,"用户权限不足"),
    NOT_FOUND(404, "资源不存在"),


    // 默认错误
    ERROR(500, "错误"),



    // 1000-2000 为sentinel异常 ///////////////////////////////

    SENTINEL_DEGRADE_ERROR(1000, "被降级规则阻挡"),
    SENTINEL_PARAM_ERROR(1001, "被热点参数规则阻挡"),
    SENTINEL_SYSTEM_ERROR(1002, "被系统规则阻挡"),
    SENTINEL_AUTHORITY_ERROR(1003, "被授权规则阻挡"),
    SENTINEL_ERROR(1999, "Unknown"),


    ////////////////2000-3000 为参数校验异常 ///////////////////////////

    HTTP_REQ_PARAM_ERROR(2000, "请求参数非法!"),
    PARAM_ADDR_ATTACH(2001, "参数附件地址为空,无法发送邮件!"),
    PARAM_ADDR_ASC(2002, "参数邮件静态资源路径和文件名为空,无法发送邮件!"),


    ////////////////////// 3000-4000系统级别错误 ///////////////////////////
    SYSTEM_REQUEST_METHOD_NOT_SUPPORTED(3000, "请求方法不支持访问方式");

    /**
     * Code码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;
}
```



# 参数解析器

​		例如我们

```java
/**
 * @Author BigKang
 * @Date 2021/1/14 4:34 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize 当前TokenUser方法参数解析器
 */
@Configuration
public class CurrentTokenUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenUtils tokenUtils;

    @Autowired
    public CurrentTokenUserMethodArgumentResolver(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    /**
     * 参数解析器支持条件，返回true表示解析，返回false不解析
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(TokenUserVo.class)
                && parameter.hasParameterAnnotation(CurrentTokenUser.class);
    }

    /**
     * 获取Token并且解析到参数上，然后返回
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = tokenUtils.getRequestToken(request);
        TokenUserVo tokenUser = tokenUtils.getTokenUser(token);
        return tokenUser;
    }
}
```

# 整合全局异常处理

​		自定义异常

```java

/**
 * @author HuangKang
 * @date 2022/10/8 10:54 AM
 * @describe 自定义系统异常
 */
public class CustomSystemException extends RuntimeException {

    public CustomSystemException(String message){
        super(message);
    }

    public CustomSystemException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMessage());
    }
}
```

```java
/**
 * @author HuangKang
 * @date 2022/10/8 10:41 AM
 * @describe 未登录异常
 */
public class NotLoginException extends CustomSystemException {

    /**
     * 未登录异常信息
     */
    private static final String MESSAGE = "用户未登录，请登录后访问";

    public NotLoginException() {
        super(MESSAGE);
    }

}
```

```java
@AllArgsConstructor
public enum ExceptionEnum {
    SAVE_DATA_FAILURE("添加数据失败!");

    private String message;

    public String getMessage() {
        return message;
    }
}
```

​		新建CustomExceptionHandler，放入能被扫描到的地方，配置异常以及404

```java
package com.sigreal.jdsettle.claim.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     * 捕获全局异常并且返回信息
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<Object> exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // 自定义的系统异常
        if (e instanceof CustomSystemException) {
            // 是否记录日志等处理

        }
        // 请求参数异常
        else if (e instanceof HttpMessageNotReadableException) {
            printErrorLog(request, e);
            return Result.error(ResultEnum.HTTP_REQ_PARAM_ERROR);
        }
        // 其他未定义捕获的异常
        else {
            // 捕获后提交到Sentry

            printErrorLog(request, e);
        }
        return Result.error(e.getMessage());
    }

    /**
     * 404配置相应
     *
     * @return
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    public Result noHandlerFound(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return Result.result(ResultEnum.NOT_FOUND);
    }

    /**
     * 未登录异常配置
     *
     * @param response
     * @return
     */
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public Result notLoginException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return Result.result(ResultEnum.NO_LOGIN);
    }


    private static void printErrorLog(HttpServletRequest request, Exception e) {
        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().equals("org.springframework.web.method.support.InvocableHandlerMethod")) {
                break;
            }
            log.error("异常类：{},异常方法：{},异常行数：{}", element.getClassName(), element.getMethodName(), element.getLineNumber());
        }
        log.error("异常类型：{}，异常信息：{}", e.getClass().getName(), e.getMessage());
        log.error("异常请求 {} ：{}", request.getMethod(), request.getRequestURI());
    }


    /**
     * 处理参数校验异常
     * @param exception
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class,BindException.class})
    @ResponseBody
    public Result<String> handleValidException(Exception exception) {
        StringBuilder msg = new StringBuilder();

        BindingResult br =  ((BindException)exception).getBindingResult();

        for (ObjectError err : br.getAllErrors()) {
            msg.append(err.getDefaultMessage());
        }
        return Result.error(msg.toString());
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

