# 引入依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

				<!-- 密码加密依赖 -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-crypto</artifactId>
        </dependency>
				<!-- hutool工具类 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-core</artifactId>
            <version>5.8.11</version>
        </dependency>
				<!-- jackson时间支持 -->
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
				<!-- UserAgent工具类用于抓取http header浏览器信息 -->
        <dependency>
            <groupId>eu.bitwalker</groupId>
            <artifactId>UserAgentUtils</artifactId>
            <version>1.21</version>
        </dependency>
```

# 编写注解

​		@PublicPath用于添加到接口上面，进行匿名访问不用登陆

```java

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HuangKang
 * @date 2022/10/8 10:28 AM
 * @describe 公共路径注解（匿名访问不登录放行）
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PublicPath {

}
```

​		@CurrentUserId用于从方法中直接获取用户ID参数

```java
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HuangKang
 * @date 2022/10/9 10:35 AM
 * @describe 获取当前登录用户ID注解
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CurrentUserId {
    
}
```

​		@CurrentUserInfo用于从方法中直接获取用户INFO信息

```java

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HuangKang
 * @date 2022/10/9 10:35 AM
 * @describe 当前登录用户信息注解
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CurrentUserInfo {

}
```

# 编写异常

​		CustomSystemException自定义系统异常

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
}

```

​		LoginTimeOutException登录超时异常处理器

```java
/**
 * @author HuangKang
 * @date 2022/10/8 3:28 PM
 * @describe 登录超时异常
 */
public class LoginTimeOutException extends CustomSystemException  {

    /**
     * 未登录异常信息
     */
    private static final String MESSAGE = "用户登录超时，请重新登录";

    public LoginTimeOutException() {
        super(MESSAGE);
    }

}
```

​		NotLoginException未登录异常

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



# 认证配置类

​		我们把和认证相关的配置全部都统一抽离出来

​		通过统一配置进行全局管控

​		通过SpringBoot的Properties属性注入

```java

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author HuangKang
 * @date 2022/10/9 10:44 AM
 * @describe 认证配置类，统一配置认证相关的参数全局控制
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * 匿名路径
     */
    private List<String> anonymityPath = new ArrayList<>();

    /**
     * 是否开启登录认证，默认关闭
     */
    private Boolean loginAuth = false;

    /**
     * 是否开启权限认证，默认关闭
     */
    private Boolean permissionsAuth = false;

    /**
     * token存放到哪个Header中（默认Authorization）
     */
    private String tokenHeader = "Authorization";

    /**
     * 头信息存放的Token的开始（默认 Authorization（$tokenHeader）: bearer($headerTokenStartsWith) $Token）
     */
    private String headerTokenStartsWith = "bearer";

    /**
     * 请求参数中的Token属性名（默认 access_token）
     */
    private String tokenParameter = "access_token";

}
```

# 拦截器

```java

import com.sigreal.xp.annotation.PublicPath;
import com.sigreal.xp.exception.NotLoginException;
import com.sigreal.xp.properties.AuthProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenCheckInterceptor implements HandlerInterceptor {

    private final AuthProperties authProperties;

    /**
     * Ant风格路径匹配器
     */
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    public TokenCheckInterceptor(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    /**
     * 在执行方法之前执行拦截操作(用于拦截Token做前置处理)
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        // 获取目标的方法,只拦截方法静态资源不拦截
        if (handler instanceof HandlerMethod) {
            // 获取方法以及路径
            HandlerMethod method = (HandlerMethod) handler;
            String path = request.getServletPath();

            // 判断是否包含注解
            boolean isPublicPath = method.hasMethodAnnotation(PublicPath.class);
            if (isPublicPath) {
                return true;
            }

            // 判断是否匿名路径
            boolean present = false;
            if(!authProperties.getAnonymityPath().isEmpty()){
                present =authProperties.getAnonymityPath().stream().anyMatch(v -> antPathMatcher.match(v, path));
            }
            if (present) {
                return true;
            }

            // 检查请求用户登录
            checkRequestUserLogin(request);
        }

        return true;
    }


    /**
     * 处理用户登录请求
     *
     * @param request HttpRequest
     */
    public void checkRequestUserLogin(HttpServletRequest request) {
        // 从请求中获取Token
        String token = getToken(request);

        // 是否开启登录认证
        if (Boolean.TRUE.equals(authProperties.getLoginAuth())) {
            // 检查是否有Token
            if (token == null) {
                throw new NotLoginException();
            }

            // 检查Token是否有效（使用JWT OR 有状态token+refreshToken）两种方案进行选择后检查

        }

        // 是否开启登录认证并且开启权限认证
        if (authProperties.getLoginAuth() && authProperties.getPermissionsAuth()) {
            // 根据用户Token校验是否有权限访问

        }

    }

    /**
     * 从Request中获取Token信息，兼容Header以及RequestParameter方式
     * @param request HttpRequest请求
     * @return Token
     */
    private String getToken(HttpServletRequest request) {

        String token = null;
        // 从Header中获取Token
        String headerToken = request.getHeader(authProperties.getTokenHeader());

        // 获取Header中的Token
        if (headerToken != null && StringUtils.startsWithIgnoreCase(headerToken, authProperties.getHeaderTokenStartsWith())) {
            // 截取Token
            headerToken = headerToken.substring(authProperties.getHeaderTokenStartsWith().length(), headerToken.length()).trim();
        } else {
            headerToken = null;
        }

        // 获取参数中的Token
        String parameterToken = request.getParameter(authProperties.getTokenParameter());
        if (parameterToken != null && parameterToken.length() > 0) {
            token = parameterToken;
        }

        if (headerToken != null) {
            token = headerToken;
        }

        // 都存在则抛出异常
        if (headerToken != null && parameterToken != null && !parameterToken.equals(headerToken) && parameterToken.trim().length() > 0) {
            throw new RuntimeException("存在多条Token信息");
        }

        return token;
    }

    /**
     * 在执行方法之后，还没有返回视图时执行（在执行之后执行，再返回给用户），如果被preHandle拦截则不执行
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
    }
}
```

# 注册拦截器



```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TokenCheckConfiguration implements WebMvcConfigurer {

    private final TokenCheckInterceptor tokenCheckInterceptor;

    @Autowired
    public CustomWebMvcConfig(TokenCheckInterceptor tokenCheckInterceptor) {
        this.tokenCheckInterceptor = tokenCheckInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Token拦截器
        registry.addInterceptor(tokenCheckInterceptor).excludePathPatterns("/swagger-resources");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //项目中的所有接口都支持跨域
        registry.addMapping("/**")
                //所有地址都可以访问，也可以配置具体地址
                .allowedOriginPatterns("*")
                //允许的请求方式
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                //是否支持跨域Cookie
                .allowCredentials(true)
                // 跨域允许时间
                .maxAge(3600);
    }
}
```

# 两种Token方式

## 选型

### **优点**

​				**JWT**

- ​							无需依赖服务器端存放数据，减轻服务器依赖端的压力
- ​							适合单点登录，多系统聚合时使用统一秘钥直接校验即可
- ​							token自身包含用户信息且无法篡改，在服务（网关）中可以自行解析校验出用户信息，对认证服务器（account-svc）压力小

​				**有状态Token**

- ​							可以隐藏真实数据，无法泄露Token用户信息，无需关注秘钥被盗或者破解

- ​							安全系数高，统一管理有状态ToKen随时下线吊销即可



### **缺点**

​				**JWT**

- ​							Jwt生成之后无法修改（发生变化）无法吊销令牌（包括logout登出），只能等待令牌自身过期
- ​							令牌长度与其包含用户信息多少正相关，传输开销较大
- ​							秘钥泄露后无法吊销，容易被攻击，动态秘钥会出现旧秘钥失效
- ​							无法自动续期，想要续期只能生成新的Jwt令牌

​				**有状态Token**

- ​							每次都需要从管理Token服务端获取校验，依赖性大

- ​							单点登录时其他系统也需要引入管理Token服务端，不利于多系统单点登录

## JWT无状态

### JWT工具类

```java

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2019/10/10 5:35 PM
 * @Summarize Jwt工具类
 */
@Data
@Component
public class JwtUtil {

    /**
     * token生成盐-前缀
     */
    @Value("${jwt.secret-prefix:42945ae144bb3db71e8a02cb6c7602bc8571b481}")
    private String SECRET_PREFIX;

    /**
     * token生成盐-后缀
     */
    @Value("${jwt.secret-suffix:bigkang}")
    private String SECRET_SUFFIX;

    /**
     * 超时时间（默认30分钟）
     */
    @Value("${jwt.overtime:30}")
    private Integer OVERTIME;

    /**
     * 记住我后超时时间（默认7天）
     */
    @Value("${jwt.remember-timeout:10080}")
    private Integer REMEMBER_TIMEOUT;


    /**
     * 创建token
     *
     * @param username   用户名称
     * @param userId     userId
     * @param roles      角色列表
     * @param rememberMe 是否记住我
     * @return
     */
    public String createToken(String username, String userId, List<String> roles, boolean rememberMe) {
        Date createTime = new Date();
        JwtBuilder jwt = Jwts.builder()
                .setId(userId)
                // 设置主题
                .setSubject(username)
                // 设置创建时间
                .setIssuedAt(createTime);

        // 设置过期时间
        if (rememberMe) {
            jwt.setExpiration(new Date(createTime.getTime() + 1000L * 60L * REMEMBER_TIMEOUT));
        } else {
            jwt.setExpiration(new Date(createTime.getTime() + 1000L * 60L * OVERTIME));
        }

        // 使用盐秘值,前缀+后缀并且32位以上加密盐
        jwt.signWith(SignatureAlgorithm.HS256, this.SECRET_PREFIX + this.SECRET_SUFFIX)
                // 自定义用户的权限传输过去
                .claim("username", username);
        if (roles == null || roles.size() <= 0) {
            // 将角色放入token
            jwt.claim("roles", roles);
        }
        return jwt.compact();
    }

    /**
     * 重载方法
     *
     * @param username 用户名称
     * @param userId   用户Id
     * @param roles    角色列表
     * @return
     */
    public String createToken(String username, String userId, List<String> roles) {
        return createToken(username, userId, roles, false);
    }

    /**
     * 重载方法
     *
     * @param username 用户名称
     * @param userId   用户Id
     * @return
     */
    public String createToken(String username, String userId) {
        return createToken(username, userId, null, false);
    }

    /**
     * 解析Token
     *
     * @param token
     * @param key
     * @return
     */
    public Claims parseToken(String token, String key) {
        if (token == null || key == null) {
            return null;
        }

        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(key)) {
            return null;
        }

        try {
            return Jwts.parser()
                    // 设置盐
                    .setSigningKey(key)
                    // 添加Token，获取
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 重载方法
     *
     * @param token
     * @return
     */
    public Claims parseToken(String token) {
        return parseToken(token, this.SECRET_PREFIX + this.SECRET_SUFFIX);
    }

    /**
     * 验证Token是否合法
     *
     * @param token
     * @return
     */
    public boolean verifyToken(String token) {
        try {
            parseToken(token);
        } catch (SignatureException e) {
            // 解析失败返回false
            return false;
        } catch (ExpiredJwtException e) {
            // Token超时解析失败
            return false;
        } catch (Exception e) {
            // 发生错误返回false
            return false;
        }
        return true;
    }

}
```

## 有状态自定义ToKen+RefreshToken

### 实体

```java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author HuangKang
 * @date 2022/10/11 11:04 AM
 * @describe AccessToken实体
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccessToken {

    /**
     * accessToken 用户访问Token
     */
    private String accessToken;

    /**
     * accessToken 超时时间
     */
    private Date expireTime;

    /**
     * refreshToken 用户刷新Token，换取新的accessToken
     */
    private String refreshToken;

    /**
     * refreshToken 超时时间
     */
    private Date refreshTokenExpireTime;

}
```

```java

import lombok.Data;

/**
 * @author HuangKang
 * @date 2023/1/6 2:41 PM
 * @describe 登录响应Vo（用户登录后信息+token信息）
 */
@Data
public class LoginVo {


    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * token信息
     */
    private AccessToken tokenVo;
}

```



### Token管理器

```java

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class AccessTokenManager {

    /**
     * Redis存储的access_token前缀
     */
    @Value("${token.redisPrefix:token-}")
    private String REDIS_TOKEN_PREFIX;

    /**
     * Redis存储的refresh_token前缀
     */
    @Value("${token.redisRefreshPrefix:refreshToken-}")
    private String REDIS_REFRESH_TOKEN_PREFIX;

    /**
     * access_token超时时间（默认30分钟）
     */
    @Value("${token.timeout:30}")
    private Integer TOKEN_TIMEOUT_TIME;

    /**
     * refresh_token超时时间（默认7天）
     */
    @Value("${token.refreshTimeout:10080}")
    private Integer REFRESH_TOKEN_TIMEOUT_TIME;

    /**
     * json转换器
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AccessTokenManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 创建Token
     *
     * @param dataVal 根据用户信息实体创建的Json
     * @return AccessToken实体
     */
    public AccessToken createToken(String dataVal) {
        return refreshToken(null, null, Boolean.TRUE, dataVal);
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新的Token
     * @param token accessToken令牌
     * @return AccessToken实体
     */
    public AccessToken refreshToken(String refreshToken, String token) {
        return refreshToken(refreshToken, token, Boolean.FALSE, null);
    }

    /**
     * @param refreshToken 刷新Token
     * @param token        Token
     * @param isCreate     是否为创建
     * @param dataVal      Token存储的用户信息
     * @return AccessToken 实体
     */
    public AccessToken refreshToken(String refreshToken, String token, Boolean isCreate, String dataVal) {

        // 当请求为刷新Token的时候
        if (!isCreate) {
            // 取出原来的信息
            dataVal = redisTemplate.opsForValue().get(REDIS_TOKEN_PREFIX + token);
            // 刷新Token没有用户信息则为超时
            if (!StringUtils.hasText(dataVal)) {
                throw new LoginTimeOutException();
            }
            // 从refresh中取出原来的Token信息
            String refreshVal = redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);

            // 校验refreshToken和token是否匹配
            if (!token.equals(refreshVal)) {
                throw new CustomSystemException("refreshToken 与 token 不匹配!");
            }
        }
        // 新的Token 和 RefreshToken
        String newToken = UUID.randomUUID().toString().replace("-", "");
        String newRefreshToken = UUID.randomUUID().toString().replace("-", "");

        // 设置Token val为data超时单位为分钟
        redisTemplate.opsForValue().set(REDIS_TOKEN_PREFIX + newToken, dataVal, TOKEN_TIMEOUT_TIME, TimeUnit.MINUTES);
        Calendar tokenCalendar = Calendar.getInstance();
        tokenCalendar.setTime(new Date());
        tokenCalendar.add(Calendar.MINUTE, TOKEN_TIMEOUT_TIME);

        // 设置RefreshToken val为token超时单位为分钟
        redisTemplate.opsForValue().set(REDIS_REFRESH_TOKEN_PREFIX + newRefreshToken, newToken, REFRESH_TOKEN_TIMEOUT_TIME, TimeUnit.MINUTES);
        Calendar refreshCalendar = Calendar.getInstance();
        refreshCalendar.setTime(new Date());
        refreshCalendar.add(Calendar.MINUTE, REFRESH_TOKEN_TIMEOUT_TIME);

        // 如果属于刷新Token
        if (!isCreate) {
            if (StringUtils.hasText(token)) {
                // 删除老的Token
                redisTemplate.delete(REDIS_TOKEN_PREFIX + token);
            }
            if (StringUtils.hasText(refreshToken)) {
                // 删除老的refreshToken
                redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);
            }
        }

        return new AccessToken(
                newToken,
                tokenCalendar.getTime(),
                newRefreshToken,
                refreshCalendar.getTime()
        );
    }


    /**
     * 检查Token并且获取用户信息
     * @param token accessToken令牌
     * @param tClass 写入的Json信息
     * @return 用户信息实体
     * @param <T> 存入Token的用户信息实体类
     */
    public <T> T checkTokenAndGet(String token, Class<T> tClass) {
        // 根据Token获取信息
        String strData = redisTemplate.opsForValue().get(REDIS_TOKEN_PREFIX + token);
        if (!StringUtils.hasText(strData)) {
            throw new NotLoginException();
        }
        try {
            T tData = objectMapper.readValue(strData, tClass);
            return tData;
        } catch (JsonProcessingException e) {
            throw new CustomSystemException("用户信息异常,请检查账户是否可用!");
        }
    }

    /**
     * 注销Token
     * @param token  accessToken 令牌
     * @param refreshToken 刷新令牌
     */
    public void logoutToken(String token,String refreshToken) {
        // 从refresh中取出原来的Token信息
        String refreshVal = redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);

        // 校验refreshToken和token是否匹配
        if (!token.equals(refreshVal)) {
            throw new CustomSystemException("refreshToken 与 token 不匹配!");
        }

        // 清除token 以及 refreshToken
        redisTemplate.delete(REDIS_TOKEN_PREFIX + token);
        redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);

    }
}

```

### 拦截器使用

​		注入Token管理器，然后找到开启登录认证直接调用checkTokenAndGet

​		然后放开 **auth.loginAuth=true** 的SpringBoot参数	

​		LoginAccessToken为存入Token的Json实体，可以自行切换，在登录的时候写入即可

```java
    @Autowired
    private AccessTokenManager accessTokenManager;


        // 是否开启登录认证
        if (Boolean.TRUE.equals(authProperties.getLoginAuth())) {
            // 检查是否有Token
            if (token == null) {
                throw new NotLoginException();
            }

            // 检查Token是否有效
            LoginVo loginVo = accessTokenManager.checkTokenAndGet(token, LoginVo.class);

        }
```

​		登录调用如下查询用户信息后dataObject，生成Token即可

```java
       ObjectMapper objectMapper = new ObjectMapper();
       String dataVal = null;
       try {
       dataVal = objectMapper.writeValueAsString(dataObject);
       } catch (JsonProcessingException e) {
       throw new RuntimeException(e);
       }
       AccessToken accessToken = accessTokenManager.createToken(dataVal);
```

​		刷新Token调用如下

```java
    @PostMapping("refreshToken")
    @ApiOperation(value = "刷新Token")
    @PublicPath
    public ResponseData<AccessToken> refreshToken(String refreshToken,String token){
        AccessToken accessToken = accessTokenManager.refreshToken(refreshToken, token);
        return ResponseData.success(accessToken);
    }
```

​		获取当前登录用户直接参考下方的ThreadLocal使用

# ThreadLocal获取当前登录用户

​		新建

```java

/**
 * @author HuangKang
 * @date 2022/10/11 6:24 PM
 * @describe 用户信息ThreadLocal
 */
public class UserContext {

    /**
     * ThreadLocal变量存储ThreadLocal信息
     */
    private final static ThreadLocal<LoginVo> threadLocal = new ThreadLocal<>();

    public static void set(LoginVo loginVo){
        threadLocal.set(loginVo);
    }

    public static  void get(){
        threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
```

​		拦截器改造,登录认证后set到ThreadLocal

```java
        // 是否开启登录认证
        if (Boolean.TRUE.equals(authProperties.getLoginAuth())) {
            // 检查是否有Token
            if (token == null) {
                throw new NotLoginException();
            }


            // 检查Token是否有效（使用JWT OR 有状态token+refreshToken）两种方案进行选择后检查
            LoginVo loginVo = accessTokenManager.checkTokenAndGet(token, LoginVo.class);
            // 写入ThreadLocal
            UserContext.set(loginVo);
        }
```

​		同时拦截器重写postHandle（重点！！！）

```java
    /**
     * 在执行方法之后，还没有返回视图时执行（在执行之后执行，再返回给用户），如果被preHandle拦截则不执行
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        UserContext.remove();
    }
```

# 数据库表设计

## 用户设计

### 用户表

```sql
CREATE TABLE `t_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `user_status` tinyint(2) NOT NULL COMMENT '用户状态： 1、正常用户 2、禁言用户 3、封禁用户',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码',
  `email` varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `login_token` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录Token',
  `login_token_timeout` datetime NULL DEFAULT NULL COMMENT '登录Token失效时间',
  `phone_number` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号码',
  `deleted` tinyint(1) NOT NULL COMMENT '逻辑删除（0 正常、1 删除）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户' ROW_FORMAT = Dynamic;
```

### 用户基础信息表

```sql
CREATE TABLE `t_user_basics_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户id',
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别（1 男 、2 女）',
  `birthday` datetime NULL DEFAULT NULL COMMENT '生日',
  `signature` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '签名',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户头像',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户基础信息' ROW_FORMAT = Dynamic;
```

### 用户三方认证表

​		三方认证自行采集设备信息存储

```sql
CREATE TABLE `t_user_oauth`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户id',
  `oauth_type` tinyint(2) NOT NULL COMMENT '认证类型，如QQ、微信、微博、等第三方认证类型',
  `oauth_id` int(11) NOT NULL COMMENT '认证ID，如QQ号码、微信openid、微博账号、等第三方认证类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户三方认证' ROW_FORMAT = Dynamic;
```

### 用户登录设备表

​		设备表自行采集设备信息存储

```sql
CREATE TABLE `t_user_login_device`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户id',
  `os_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '系统名称',
  `os_version` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '系统版本',
  `device_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '设备型号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户登录设备' ROW_FORMAT = Dynamic;
```

### 用户登录日志表

```sql
CREATE TABLE `t_user_login_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户id',
  `os_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '系统名称',
  `device_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '设备名称',
  `type` tinyint(2) NOT NULL COMMENT '登录方式 用户名/第三方/手机短信 等',
  `login_time` datetime NOT NULL COMMENT '创建时间',
  `login_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '登录IP',
  `client` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端',
  `client_version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户登录日志' ROW_FORMAT = Dynamic;
```

## RBAC权限设计

### 角色表

​		角色表

```

id（租户ID）
name（租户名称）
description（租户描述）





```



## 代码

### controller

```java

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HuangKang
 * @date 2023/1/9 11:29 AM
 * @describe 登录控制器
 */
@RestController
@RequestMapping("login")
@Api(tags = "登录控制器")
public class LoginController {

    private final LoginService loginService;

    @UnstableApi
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @PostMapping("userLogin")
    @PublicPath
    @ApiOperation("用户登录")
    public Result<LoginVo> userLogin(@RequestBody UserLoginDto dto, HttpServletRequest httpServletRequest) {
        return Result.success(loginService.userLogin(dto,httpServletRequest));
    }


    @PostMapping("logout")
    @ApiOperation("用户登出")
    public Result userLogin(String accessToken,String refreshToken) {
        loginService.userLogout(accessToken,refreshToken);
        return Result.success();
    }


    @PostMapping("refreshToken")
    @ApiOperation("刷新Token")
    public Result<AccessToken> refreshToken(String accessToken, String refreshToken) {
        AccessToken accessTokenVo =  loginService.refreshToken(accessToken,refreshToken);
        return Result.success(accessTokenVo);
    }


}

```

### service

```java

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author HuangKang
 * @date 2023/1/9 11:40 AM
 * @describe 用户登录Service实现
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AccessTokenManager accessTokenManager;

    private final ObjectMapper objectMapper;

    private final UserLoginLogService userLoginLogService;

    public LoginServiceImpl(UserService userService, PasswordEncoder passwordEncoder, AccessTokenManager accessTokenManager, ObjectMapper objectMapper, UserLoginLogService userLoginLogService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenManager = accessTokenManager;
        this.objectMapper = objectMapper;
        this.userLoginLogService = userLoginLogService;
    }


    @Override
    public LoginVo userLogin(UserLoginDto dto, HttpServletRequest httpServletRequest) {
        UserInfoVo userInfoVo = userService.searchUser(dto.getUsername());
        AssertUtil.isNotNull(userInfoVo,"用户不存在!");
        LoginVo loginVo = new LoginVo();

        boolean flag = passwordEncoder.matches(dto.getPassword(),userInfoVo.getPassword());
        AssertUtil.isTrue(flag,"用户名或密码错误!");

        // 密码正确登录成功
        try {

            loginVo.setUserId(userInfoVo.getUserId());
            loginVo.setUserName(userInfoVo.getUserName());
            loginVo.setUserStatus(userInfoVo.getUserStatus());
            loginVo.setUserAvatar(userInfoVo.getUserAvatar());
            loginVo.setBirthday(userInfoVo.getBirthday());
            loginVo.setNickName(userInfoVo.getNickName());
            loginVo.setPhoneNumber(userInfoVo.getPhoneNumber());
            loginVo.setGender(userInfoVo.getGender());
            loginVo.setEmail(userInfoVo.getEmail());

            AccessToken accessToken = accessTokenManager.createToken(objectMapper.writeValueAsString(loginVo));

            loginVo.setTokenVo(accessToken);

            saveLoginLog(loginVo,httpServletRequest,LoginTypeEnum.USERNAME);
        } catch (JsonProcessingException e) {
           throw new CustomSystemException("登录失败，服务繁忙!");
        }

        return loginVo;
    }

    @Override
    public void userLogout(String accessToken,String refreshToken) {
        accessTokenManager.logoutToken(accessToken,refreshToken);
        String userId = UserContext.get().getUserId();
        userService.logOutUser(userId);
    }

    @Override
    public AccessToken refreshToken(String accessToken, String refreshToken) {
        AccessToken accessTokenVo = accessTokenManager.refreshToken(refreshToken, accessToken);

        LoginVo loginVo = UserContext.get();
        User user = new User();
        user.setLoginToken(loginVo.getTokenVo().getAccessToken());
        user.setLoginTokenTimeout(loginVo.getTokenVo().getExpireTime());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getUserId,loginVo.getUserId());
        // 更新用户Token
        userService.update(user,queryWrapper);

        return accessTokenVo;
    }


    public void saveLoginLog(LoginVo loginVo, HttpServletRequest httpServletRequest, LoginTypeEnum loginTypeEnum){
        UserLoginLog userLoginLog = new UserLoginLog();

        // 设置日志信息
        userLoginLog.setType(loginTypeEnum);
        userLoginLog.setUserId(loginVo.getUserId());
        userLoginLog.setLoginIp(getRealIPAddress(httpServletRequest));
        userLoginLog.setLoginTime(LocalDateTime.now());

        String agent= httpServletRequest.getHeader("User-Agent");
        // 设置登录设备信息
        if (StringUtils.hasText(agent)) {
            UserAgent userAgent = UserAgent.parseUserAgentString(agent);
            Browser browser = userAgent.getBrowser();
            OperatingSystem operatingSystem = userAgent.getOperatingSystem();
            userLoginLog.setClient(browser.getName());
            userLoginLog.setClientVersion(userAgent.getBrowserVersion().getVersion());
            userLoginLog.setOsName(operatingSystem.getGroup().getName());
            userLoginLog.setDeviceName(operatingSystem.getManufacturer().getName());

        }

        User user = new User();
        user.setLoginToken(loginVo.getTokenVo().getAccessToken());
        user.setLoginTokenTimeout(loginVo.getTokenVo().getExpireTime());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getUserId,loginVo.getUserId());
        // 更新用户Token
        userService.update(user,queryWrapper);

        // 记录登录日志
        userLoginLogService.save(userLoginLog);
    }


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
            while(index < size && (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip))){
                ip = request.getHeader(matchOptions[index]);
                index++;
            }
            if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (StringUtils.hasText(ip) && !UNKNOWN.equalsIgnoreCase(ip) && ip.length() > 15) {
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
}

```

### loginVo

```java

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author HuangKang
 * @date 2023/1/6 2:41 PM
 * @describe 登录响应Vo（用户登录后信息+token信息）
 */
@Data
public class LoginVo {
    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户状态： 1、正常用户 2、禁言用户 3、封禁用户")
    private Integer userStatus;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号码")
    private String phoneNumber;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "性别（1 男 、2 女）")
    private Integer gender;

    @ApiModelProperty(value = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime birthday;

    @ApiModelProperty(value = "签名")
    private String signature;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "Token信息")
    private AccessToken tokenVo;
}

```

### AccessToken

```java

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author HuangKang
 * @date 2022/10/11 11:04 AM
 * @describe AccessToken实体
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccessToken {

    /**
     * accessToken 用户访问Token
     */
    private String accessToken;

    /**
     * accessToken 超时时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * refreshToken 用户刷新Token，换取新的accessToken
     */
    private String refreshToken;

    /**
     * refreshToken 超时时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refreshTokenExpireTime;

}
```



### AccessTokenManager

```java

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class AccessTokenManager {

    /**
     * Redis存储的access_token前缀
     */
    @Value("${token.redisPrefix:token-}")
    private String REDIS_TOKEN_PREFIX;

    /**
     * Redis存储的refresh_token前缀
     */
    @Value("${token.redisRefreshPrefix:refreshToken-}")
    private String REDIS_REFRESH_TOKEN_PREFIX;

    /**
     * access_token超时时间（默认30分钟）
     */
    @Value("${token.timeout:30}")
    private Integer TOKEN_TIMEOUT_TIME;

    /**
     * refresh_token超时时间（默认7天）
     */
    @Value("${token.refreshTimeout:10080}")
    private Integer REFRESH_TOKEN_TIMEOUT_TIME;

    /**
     * json转换器
     */

    private final ObjectMapper objectMapper;

    final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AccessTokenManager(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建Token
     *
     * @param dataVal 根据用户信息实体创建的Json
     * @return AccessToken实体
     */
    public AccessToken createToken(String dataVal) {
        return refreshToken(null, null, Boolean.TRUE, dataVal);
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新的Token
     * @param token accessToken令牌
     * @return AccessToken实体
     */
    public AccessToken refreshToken(String refreshToken, String token) {
        return refreshToken(refreshToken, token, Boolean.FALSE, null);
    }

    /**
     * @param refreshToken 刷新Token
     * @param token        Token
     * @param isCreate     是否为创建
     * @param dataVal      Token存储的用户信息
     * @return AccessToken 实体
     */
    public AccessToken refreshToken(String refreshToken, String token, Boolean isCreate, String dataVal) {

        // 当请求为刷新Token的时候
        if (!isCreate) {
            // 取出原来的信息
            dataVal = redisTemplate.opsForValue().get(REDIS_TOKEN_PREFIX + token);
            // 刷新Token没有用户信息则为超时
            if (!StringUtils.hasText(dataVal)) {
                throw new LoginTimeOutException();
            }
            // 从refresh中取出原来的Token信息
            String refreshVal = redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);

            // 校验refreshToken和token是否匹配
            if (!token.equals(refreshVal)) {
                throw new CustomSystemException("refreshToken 与 token 不匹配!");
            }
        }
        // 新的Token 和 RefreshToken
        String newToken = UUID.randomUUID().toString().replace("-", "");
        String newRefreshToken = UUID.randomUUID().toString().replace("-", "");

        // 设置Token val为data超时单位为分钟
        redisTemplate.opsForValue().set(REDIS_TOKEN_PREFIX + newToken, dataVal, TOKEN_TIMEOUT_TIME, TimeUnit.MINUTES);
        Calendar tokenCalendar = Calendar.getInstance();
        tokenCalendar.setTime(new Date());
        tokenCalendar.add(Calendar.MINUTE, TOKEN_TIMEOUT_TIME);

        // 设置RefreshToken val为token超时单位为分钟
        redisTemplate.opsForValue().set(REDIS_REFRESH_TOKEN_PREFIX + newRefreshToken, newToken, REFRESH_TOKEN_TIMEOUT_TIME, TimeUnit.MINUTES);
        Calendar refreshCalendar = Calendar.getInstance();
        refreshCalendar.setTime(new Date());
        refreshCalendar.add(Calendar.MINUTE, REFRESH_TOKEN_TIMEOUT_TIME);

        // 如果属于刷新Token
        if (!isCreate) {
            if (StringUtils.hasText(token)) {
                // 删除老的Token
                redisTemplate.delete(REDIS_TOKEN_PREFIX + token);
            }
            if (StringUtils.hasText(refreshToken)) {
                // 删除老的refreshToken
                redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);
            }
        }

        return new AccessToken(
                newToken,
                DateUtil.toLocalDateTime(tokenCalendar.getTime()),
                newRefreshToken,
                DateUtil.toLocalDateTime(refreshCalendar.getTime())
        );
    }


    /**
     * 检查Token并且获取用户信息
     * @param token accessToken令牌
     * @param tClass 写入的Json信息
     * @return 用户信息实体
     * @param <T> 存入Token的用户信息实体类
     */
    public <T> T checkTokenAndGet(String token, Class<T> tClass) {
        // 根据Token获取信息
        String strData = redisTemplate.opsForValue().get(REDIS_TOKEN_PREFIX + token);
        if (!StringUtils.hasText(strData)) {
            throw new NotLoginException();
        }
        try {
            T tData = objectMapper.readValue(strData, tClass);
            return tData;
        } catch (JsonProcessingException e) {
            throw new CustomSystemException("用户信息异常,请检查账户是否可用!");
        }
    }

    /**
     * 注销Token
     * @param token  accessToken 令牌
     * @param refreshToken 刷新令牌
     */
    public void logoutToken(String token,String refreshToken) {
        // 从refresh中取出原来的Token信息
        String refreshVal = redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);
        if(!StringUtils.hasText(refreshVal)){
            return;
        }
        // 校验refreshToken和token是否匹配
        if (!token.equals(refreshVal)) {
            throw new CustomSystemException("refreshToken 与 token 不匹配!");
        }

        // 清除token 以及 refreshToken
        redisTemplate.delete(REDIS_TOKEN_PREFIX + token);
        redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + refreshToken);

    }
}

```



### JacksonConfig

```java
/**
 * @author HuangKang
 * @date 2023/1/9 2:20 PM
 * @describe jackson配置
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
```







# 多租户设计



