# 引入依赖

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```

# 编写配置

```properties
server:
  port: 8088
spring:
  security:
    user:
      name: bigkang
      password: bigkang
```

# 然后编写配置类

创建一个SecurityConfig类

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http.authorizeRequests();
        authorizeRequests
                //"/login"不进行权限验证，放开权限的接口
                .antMatchers("/login/*","/favicon.ico").permitAll()
                .anyRequest().authenticated()   //其他的需要登陆后才能访问
                .and().formLogin()
                //loginProcessingUrl用于指定前后端分离的时候调用后台登录接口的名称
                .loginProcessingUrl("/login")
                .and()
                //loginProcessingUrl用于指定前后端分离的时候调用后台注销接口的名称
                .logout().logoutUrl("/logout")
                .and()
                .cors()//新加入
                .and()
                .csrf().disable(); // 取消跨站请求伪造防护
    }
}
```

# 然后编写一个controller

我们这里随便给他设置一个controller，注意这个controller不要以login开头，因为login是不需要登录的，我们在配置文件中放开了权限

```java
@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping("list")
    public List<String> list(){
        List<String> list = new ArrayList<>();
        list.add("bigkang");
        list.add("yellowkang");
        return list;
    }

}
```

# 然后我们启动项目

访问<http://localhost:8089/user/list>

他就会跳转到登录页面，因为我们没有登录

![](img\security-1.png)

然后我们输入我们配置文件中配置的密码即可

![](img\security-2.png)

那么我们如果想自定义密码怎么办呢下面我们使用数据库认证

# 数据库认证用户名密码

## 数据层准备

我们首先新建数据库（数据库非常简单，测试示例）

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint(20) NOT NULL,
  `age` int(11) NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

只有id和age还有password和username以及role，role是角色为了我们后续使用

那么我们插入两条数据

分别是，密码都是bigkang，用户名分别为root和bigkang

```sql
INSERT INTO `t_user` VALUES (1, 18, 'bigkang', 'root', 'root');
INSERT INTO `t_user` VALUES (2, 18, 'bigkang', 'bigkang', 'user');
```

我们使用jpa整合访问层（使用mybatis也可以，在Service中将Mapper注入即可）

```java
@Entity
@Table(name = "t_user")
@Data
@ToString
public class User {
    @Id
    private Long id;
    //用户名称
    private String username;
    //密码
    private String password;
    //年龄
    private Integer age;
    //用户角色
    private String role;
}
```

```java
public interface UserDao extends JpaRepository<User,Long> {
	//根据用户名查询数据库
    public User findByUsername(String username);
}
```

## UserDetailsService

UserDetailsService是我们用户详情实现接口，我们新建一个service并且实现它，我们顺便将角色也传入过去了，注意User包名

```java


import org.springframework.security.core.userdetails.User;
/**
 * @Author BigKang
 * @Date 2019/7/22 11:47
 * @Summarize 用户信息查询Service
 */
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        com.kang.boot.security.entity.User user = userService.findByUsername(s);
        // 获取用户的角色
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 角色必须以`ROLE_`开头
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
```

## 修改配置类

```java
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    
	/**
     * 认证管理器构建器
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService);
    }
```

我们将这个实现了UserDetailsService的类放入验证中我们即可使用用户名加密码登录了，如果我们还需要使用加密我们可以这样，我们可以new一个PasswordEncoder并且实现里面的方法进行使用，也可以直接使用bCryptPasswordEncoder，我们可以将注册成一个bean

```java
@Configuration
public class SecurityBeanConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
```

```java
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //注入自定义的UserService，并且添加密码加密方式
        //自定义加密解密
//        auth.userDetailsService(myUserDetailsService).passwordEncoder(new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence charSequence) {
//                return charSequence.toString();
//            }
//            @Override
//            public boolean matches(CharSequence charSequence, String s) {
//                return charSequence.equals(s);
//            }
//        });

        //使用BCryptPasswordEncoder加密
        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
```

注意数据库的密码我们也需要保存为加密的，我们使用测试类加密后存入数据库

```java
    @Test
    public void  testas(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("bigkang"));
    }

```

然后我们再进行访问即可，就能根据数据库的用户进行认证，并且基于角色进行鉴权

# 配置详解

### UserDetails

我们这里使用Jpa进行数据库操作，这里使用了多对多的关系（就是查询角色）实现了UserDetails后，我们会实现5个方法，我们自己添加一个enable属性，就可以不用实现方法了，其他几个方法如注释所解释，重点是查询角色后添加到实现的getAuthorities方法中，如下示例，注意必须以ROLE_开头

```java
@Entity
@Table(name = "t_user")
@Data
public class User extends BaseJpaEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 用户对角色
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_user_to_role", joinColumns = {@JoinColumn(name = "uid")},
            inverseJoinColumns = {@JoinColumn(name = "rid")})
    private List<Role> role;

    /**
     * 邮箱
     */
    @Email
    private String email;

    /**
     * 电话
     */
    private String phone;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role r : role) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getRoleName()));
        }
        return authorities;
    }

    /**
     * 是否可用（启用）,Security自带属性，直接使用属性即可不实现方法，实现方法则表示不使用该功能，只有当4个属性全是true时才能认证成功
     */
    private boolean enabled = true;


    /**
     * 账户是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账号是否被冻结
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 帐户密码是否过期，一般有的密码要求性高的系统会使用到，比较每隔一段时间就要求用户重置密码
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
```

### UserDetailsService

​		主要就是我们进行查询的service接口,它主要是通过loadUserByUsername方法来加载传入的用户名，并且根据用户名来查询用户信息以及权限角色等

​		如果我们实体继承了UserDetails那么直接查询出来返回即可，示例如下：

```java
/**
 * @Author BigKang
 * @Date 2019/7/22 11:47
 * @Summarize 用户信息查询Service
 */
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // 防止用户名为空
        if(StringUtils.isEmpty(s)){
            return new User();
        }

        // 根据用户名查询用户信息
        User user = userService.findByUsername(s);

        // 防止查询数据为空异常
        if(user == null){
            user = new User();
        }
        return user;
    }
}
```

​	如果没有实现他的UserDetails那么我们需要查询后返回

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2019/7/22 11:47
 * @Summarize 用户信息查询Service
 */
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
				//根据用户名查询用户信息
        com.kang.boot.auth.entity.User user = userService.findByUsername(s);
        List<GrantedAuthority> list = new ArrayList<>();
      	//遍历用户信息添加角色
        user.getRole().forEach(v -> {
            list.add(new SimpleGrantedAuthority("ROLE_" + v.getRoleName()));
        });
        //返回User对象，用户名，密码，是否启用，是否冻结等等详情查看UserDetails
        return new User(user.getUsername(),user.getPassword(),true,true,true,true,list);
    }
}
```

### 注入UserDetailsService以及定义加密算法

   	我们配置好了上方的UserDetailService后我们就需要将它注入进去，并且密码我们需要加密进行展示所以需要实现以下我们将自己定义的MyUserDetailsService也就是上面的UserDetailsService注入进来，并且注入加密算法实现

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class AuthServerConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 设置默认的加密方式
        return new BCryptPasswordEncoder();
    }

    /**
     * 数据库用户认证UserDetailsService实现
     */
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    /**
     * 注入密码加密
     */
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * 认证管理器构建器
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //使用BCryptPasswordEncoder加密
        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

}
```

自定义加密算法，我们这里还是自己采用的passwordEncoder，我们可以修改为MD5或者其他的加密算法都可以

```java
    /**
     * 认证管理器构建器
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(myUserDetailsService).passwordEncoder(new PasswordEncoder() {
              	//定义加密类型，将用户传入过来的密码进行加密然后返回
                public boolean matches(CharSequence charSequence, String s) {
                    return passwordEncoder().matches(charSequence, s);
                }

              	//和数据库的密码进行比对
                @Override
                public boolean matches(CharSequence charSequence, String s) {
                    return passwordEncoder().matches(charSequence, s);
                }
            });
    }
```

### 开放接口（允许匿名，开放权限）

我们可以自定义哪些接口不需要进行权限认证，直接就能访问，我们指定开发接口，并且开放静态资源

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //放开接口
                .antMatchers("/login/**").anonymous()
                //连续放开
                .antMatchers("/test/**").anonymous()
                //一次放开多个接口，并且指定Get方法
                .antMatchers(HttpMethod.GET,"/favicon.ico","/**/*.png","/**/*.html")
                //全部允许
                .permitAll()
                //所有其他请求进行验证
                .anyRequest().authenticated();
    }
```

### 自定义登录页面以及接口

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
				http                
                //使用表单登录，表单中使用username+password进行登录
                .formLogin()
                //未登录时拦截返回后的页面，如果未登录拦截到并进行跳转到登录页面
                .loginPage("/login/nologin")
                //loginProcessingUrl用于指定前后端分离的时候调用后台登录接口的路径，表单请求接口
                .loginProcessingUrl("/login/login");
    }
```



### 会话配置

设置指定会话创建策略,如果我们基于Jwt来获取信息的话那么久禁止Security创建Session

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
```

SessionCreationPolicy.class

```java
public enum SessionCreationPolicy {
    ALWAYS,								//总是创建HttpSession
    NEVER,								//Spring Security只会在需要时创建一个HttpSession
    IF_REQUIRED,					//Spring Security不会创建HttpSession，但如果它已经存在，将可以使用HttpSession
    STATELESS;						//Spring Security永远不会创建HttpSession，它不会使用HttpSession来获取SecurityContext

    private SessionCreationPolicy() {
    }
}
```

### 自定义登录成功失败以及权限控制

登录成功处理器

首先我们添加一个登录成功处理器叫做自定义成功处理器CustomSuccessHandler，我们登陆成功之后获取被拦截的url，然后判断是否访问时被跳转到登录，在登陆成功之后重定向到原来的url

```java
/**
 * @Author BigKang
 * @Date 2019/7/22 16:41
 * @Summarize 自定义认证成功处理器
 */
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //返回数据为utf8的json
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        //返回状态码200请求成功，登陆成功
        httpServletResponse.setStatus(HttpStatus.OK.value());
        String url = null;
        RequestCache requestCache = new HttpSessionRequestCache();

        SavedRequest request = requestCache.getRequest(httpServletRequest, httpServletResponse);

        if(request != null)
            url = request.getRedirectUrl();

        if(url != null)
            httpServletResponse.sendRedirect(url);

        //返回登录成功信息
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(ResultVo.result(authentication,Code.OK_CODE,Message.AUTH_SUCCESS)));
    }
}
```

然后我们在配置类中将成功处理器注入进去，如下

```java
    /**
     * 注入登陆成功处理器
     */
    @Autowired
    private CustomSuccessHandler customSuccessHandler;
    
        /**
     * 配置拦截规则
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //匿名接口login不进行权限验证,以及需要放开的权限
                .antMatchers("/login/*","/favicon.ico")
                //开放所有匿名接口
                .permitAll()
                //其他的需要登陆后才能访问
                .anyRequest().authenticated()
                .and()

                //表单登录，表单中使用username+password进行登录
                .formLogin()
                //未登录时拦截返回后的路径
//                .loginPage("/login/nologin")
                //loginProcessingUrl用于指定前后端分离的时候调用后台登录接口的路径
                .loginProcessingUrl("/login/login")
                //登录控制器
                //配置登录成功的自定义处理类
                .successHandler(customSuccessHandler)
                .and()

                //登出配置
                //loginProcessingUrl用于指定前后端分离的时候调用后台注销接口的名称
                .logout()
                .logoutUrl("/logout");
    }
```

剩下的登录失败以及权限错误的处理器分别如下

登录失败：CustomFailureHandler

```java
/**
 * @Author BigKang
 * @Date 2019/7/22 12:15
 * @Summarize 自定义登录失败控制器
 */
@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //定义返回的数据为utf8的json
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        //定义返回的状态码为400，登录失败
        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        //定义返回登录失败
				Map<String,Object> map = new HashMap<>();
        map.put("code",400);
        map.put("message","用户名或密码错误");

        //定义返回登录失败
	 httpServletResponse.getWriter().write(objectMapper.writeValueAsString(map));
    
    }
}
```

配置中为：（参考如上）

```
                .failureHandler(customFailureHandler)
```

登出：实现LogoutSuccessHandler

```
 implements LogoutSuccessHandler 
 
 然后配置登出（在.logout("/logou").logoutSuccessHandler()后面）
 .logoutSuccessHandler(customLogoutSuccessHandler)         
```

权限不足：实现AccessDeniedHandler

```
 implements AccessDeniedHandler
 
 然后
 //配置没有权限的自定义处理类
.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
```



### Cookie设置

设置Cookie

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
           http     //开启cookie保存用户数据
        //是否记住我
                .rememberMe()
                //设置cookie有效期
                .tokenValiditySeconds(60 * 60 * 24 * 7)
                //设置cookie的私钥
                .key("kang-shop")
                .and();
    }
```

### 方法级别注解控制

首先我们先在Security的配置类中或者启动类上加上注解

```java
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
```

prePostEnabled = true

表示开启注解控制权限，使用方法如下，我们只需要在这个controller方法上面加上@PreAuthorize并且配置上访问它的权限的角色即可

```java
    @PreAuthorize("hasAnyRole('ROOT')")
    @GetMapping("test")
    public String test() {
        return "测试返回数据";
    }
```

他可以开启4个注解进行权限控制，如下

```java
@PreAuthorize 在方法调用之前,基于表达式的计算结果来限制对方法的访问

@PostAuthorize 允许方法调用,但是如果表达式计算结果为false,将抛出一个安全性异常

@PostFilter 允许方法调用,但必须按照表达式来过滤方法的结果

@PreFilter 允许方法调用,但必须在进入方法之前过滤输入值
```

securedEnabled = true

```
开启@Secured 注解过滤权限
```

jsr250Enabled=true

```
开启@RolesAllowed 注解过滤权限
```

### 指定json格式登录

首先我们需要先新建一个过滤器然后我们继承UsernamePasswordAuthenticationFilter

```java
/**
 * @Author BigKang
 * @Date 2019/9/18 6:24 PM
 * @Summarize 自定义认证过滤器，支持json登录
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 首先判断请求类型
        if (request.getContentType() != null) {
            // 然后判断请求是不是json格式的数据
            if (request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    || request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {

                // 将用户的请求json数据封装到自定义的实体中
                ObjectMapper mapper = new ObjectMapper();
                UsernamePasswordAuthenticationToken authRequest = null;
                try (InputStream is = request.getInputStream()) {
                    // 封装实体
                    AuthLoginEntity authLoginEntity = mapper.readValue(is, AuthLoginEntity.class);

                    // 将实体中的用户名和密码取出，然后放入验证请求中
                    authRequest = new UsernamePasswordAuthenticationToken(
                            authLoginEntity.getUsername(), authLoginEntity.getPassword());
                } catch (IOException e) {
                    // 如果异常，将空用户名和密码传入验证请求
                    e.printStackTrace();
                    authRequest = new UsernamePasswordAuthenticationToken(
                            "", "");
                } finally {
                    // 将request，以及authRequest设置回去
                    setDetails(request, authRequest);
                    return this.getAuthenticationManager().authenticate(authRequest);
                }

            }else {
                // 如果请求不是json格式，调用父类方法继续执行，进行表单验证
                return super.attemptAuthentication(request, response);
            }
        }

        // 如果请求主体类型为空，直接调用父类方法，继续执行
        else {
            return super.attemptAuthentication(request, response);
        }
    }
}
```

这里我们用到了一个实体类，实体类非常简单，如下

```java
/**
 * @Author BigKang
 * @Date 2019/9/18 6:23 PM
 * @Summarize 认证登录实体类
 */

@Data
public class AuthLoginEntity {

    private String username;
  
    private String password;
}
```

然后我们需要去Security的配置类中将这个过滤器添加进去

```java
/**
 * @Author BigKang
 * @Date 2019/9/17 10:41 AM
 * @Summarize Security认证配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class AuthServerConfig extends WebSecurityConfigurerAdapter {

    /**
     * 数据库用户认证UserDetailsService实现
     */
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    /**
     * 注入密码加密
     */
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    CustomAuthenticationFilter customAuthenticationFilter(){
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
        try {
            filter.setFilterProcessesUrl("/login/login");
            filter.setAuthenticationSuccessHandler(customSuccessHandler);
            filter.setAuthenticationFailureHandler(customFailureHandler);
            filter.setAuthenticationManager(authenticationManagerBean());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filter;
    }

    /**
     * 配置拦截规则
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //匿名接口login不进行权限验证,以及需要放开的权限
                .antMatchers("/login/*","/favicon.ico")
                //开放所有匿名接口
                .permitAll()

                //其他的需要登陆后才能访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                //未登录时拦截返回后的路径
//                .loginPage("/login/nologin")
                //loginProcessingUrl用于指定前后端分离的时候调用后台登录接口的路径
                .loginProcessingUrl("/login/login")
                .and()

                //登出配置
                //loginProcessingUrl用于指定前后端分离的时候调用后台注销接口的名称
                .logout()
                .logoutUrl("/logout");

        // 添加自定义的过滤器拦截并且使用json进行登录
        http.addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
    }

    /**
     * 认证管理器构建器
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //使用BCryptPasswordEncoder加密
        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

}
```

然后我们直接使用swagger或者postman登录即可

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1570775870644.png)

记得将登录成功返回的Handler设置好，自定义登录成功的返回信息

### 使用Token进行登录认证