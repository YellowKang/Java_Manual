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

