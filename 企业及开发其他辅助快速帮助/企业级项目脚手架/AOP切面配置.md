# 添加依赖

```
        <!-- SpringBoot整合AOP -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- lombok打印日志 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.2</version>
            <scope>provided</scope>
        </dependency>
```

# 编写配置类

我们去项目包中新建一个aspect包，然后新建个类AspectConfig

```

@Component
@Aspect
@Slf4j
public class AspectConfig {

    @Pointcut("execution(* com.kang.test.controller.TestOSSController.*(..))")
    public void testAspect(){};

    @Before("testAspect()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        log.info("URL : " + request.getRequestURL().toString());//打印请求url
        log.info("HTTP_METHOD : " + request.getMethod());//打印请求方法
        log.info("IP : " + request.getRemoteAddr());//打印请求IP来源
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());//打印类方法路径
        log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));//打印参数
    }
}

```

# 代理注解

```
@Before("testAspect()")							  	//前置通知
		在连接点前面执行，前置通知不会影响连接点的执
@AfterReturning("testAspect()")						//正常返回通知
		在连接点正常执行完成后执行，如果连接点抛出异常，则不会执行。
@AfterThrowing("testAspect()")					  	//异常返回通知
		在连接点抛出异常后执行。 
@After("testAspect()")								//返回通知
		在连接点执行完成后执行，不管是正常执行完成，还是抛出异常，都会执行返回通知中的内容。 
@Around()("testAspect()")							//环绕通知	
		环绕通知围绕在连接点前后，比如一个方法调用的前后。这是最强大的通知类型，能在方法调用前后自定义一些操作。环绕通知还需要负责决定是继续处理join point(调用ProceedingJoinPoint的proceed方法)还是中断执行。 
```

# 获取代理信息

```

		// 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
	
        // 获取请求url
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("IP : " + request.getRemoteAddr());
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    

```

# 切面表达式

```
任意公共方法的执行：
execution(public * *(..))

任何一个以“set”开始的方法的执行：
execution(* set*(..))

AccountService 接口的任意方法的执行：
execution(* com.xyz.service.AccountService.*(..))

定义在service包里的任意方法的执行：
execution(* com.xyz.service.*.*(..))

更多详细url
http://www.cnblogs.com/duenboa/p/6665474.html
```

