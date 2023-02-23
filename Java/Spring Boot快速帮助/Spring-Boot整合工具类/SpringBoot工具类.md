# BeanUtil工具类

```java
package com.sigreal.jiaanan.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HuangKang
 * @date 2022/10/27 9:42 AM
 * @describe Spring工具类
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (null == SpringUtil.applicationContext) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 获取应用上下文
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据Class获取Bean对象
     * @param classzz Class泛型
     * @return Bean对象
     * @param <T> 泛型类
     */
    public static <T> T getBean(Class<T> classzz) {
        return getApplicationContext().getBean(classzz);
    }

    /**
     * 根据Bean名称和Class获取Bean对象
     * @param beanName Bean名称
     * @param classzz classzz Class泛型
     * @return Bean对象
     * @param <T> 泛型类
     */
    public static <T> T getBean(String beanName, Class<T> classzz) {
        return getApplicationContext().getBean(beanName, classzz);
    }

    /**
     * 获取当前使用的环境
     * @return 定义使用的环境（dev，test，prod）
     */
    public static String getActiveEnv(){
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }


    /**
     * 是否包含环境
     * @param env 环境信息
     * @return 是否包含
     */
    public static Boolean hasActiveEnv(String env){
        return Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()).contains(env);
    }

    /**
     * 获取当前使用的环境
     * @return 定义使用的环境（dev，test，prod）等
     */
    public static List<String> getActiveEnvs(){
        return Arrays.stream(applicationContext.getEnvironment().getActiveProfiles()).collect(Collectors.toList());
    }
}
```

# 获取真实的请求IP

```java
    /**
     * 获取真实IP地址
     * @param request http请求
     * @return IP
     */
    public static String getRealIPAddress(HttpServletRequest request){
        final String UNKNOWN = "unknown";
        final String[] matchOptions = {"X-Real-IP","X-Forwarded-For","x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        final int size = matchOptions.length;
        try {
            String ip = UNKNOWN;
            int index = 0;
            while(index < size && (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip))){
                ip = request.getHeader(matchOptions[index]);
                index++;
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (!StringUtils.isEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip) && ip.length() > 15) {
                String[] ips = ip.split(",");
                int len = ips.length;
                for (int i = 0; i < len; i++) {
                    String strIp = ips[index];
                    if (!(UNKNOWN.equalsIgnoreCase(strIp))) {
                        ip = strIp;
                        break;
                    }
                }
            }

            return ip;
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
```

# 统一Http响应



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

    //地址类异常
    PARAM_ADDR(2000, "参数邮件抄送地址为空,无法发送邮件!"),
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

# 新增traceId拦截器

​	新建拦截器

```java
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author HuangKang
 * @Date 2021/4/23 下午2:23
 * @Summarize 请求TraceId拦截器
 */
@Component
public class RequestTraceIdInterceptor implements HandlerInterceptor {
    /**
     * 日志TRACE_ID名称常量
     */
    private static final String LOG_TRACE_ID_NAME = "traceId";

    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MDC.put(LOG_TRACE_ID_NAME, UUID.randomUUID().toString().replace("-",""));
        return true;
    }

}

```

​		新增Mvc配置拦截器

```java
package com.kang.test.k8s.config;

import com.kang.test.k8s.interceptor.RequestTraceIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author HuangKang
 * @Date 2021/4/23 下午4:00
 * @Summarize 自定义WebMvc配置
 */
@Configuration
public class CustomWebMvcConfig  implements WebMvcConfigurer {

    final RequestTraceIdInterceptor requestTraceIdInterceptor;

    @Autowired
    public CustomWebMvcConfig(RequestTraceIdInterceptor requestTraceIdInterceptor) {
        this.requestTraceIdInterceptor = requestTraceIdInterceptor;
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加TraceId拦截器
        registry.addInterceptor(requestTraceIdInterceptor);
    }
}

```
