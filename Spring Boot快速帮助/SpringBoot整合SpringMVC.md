# SpringMVC













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

