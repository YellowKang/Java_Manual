# 引入依赖

```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```

# 编写配置

```
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

```
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

```
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

```
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

```
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

```


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

```
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

```
    @Test
    public void  testas(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("bigkang"));
    }

```

然后我们再进行访问即可