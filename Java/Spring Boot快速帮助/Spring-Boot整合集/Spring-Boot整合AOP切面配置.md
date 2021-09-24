# 添加依赖

```xml
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

```java

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

```java
@Before("testAspect()")							  	//前置通知
		在连接点前面执行，前置通知不会影响连接点的执
@AfterReturning(pointcut = "testAspect()",returning="result")						//正常返回通知
public void doAfterReturning(JoinPoint joinPoint, Object result){}
		在连接点正常执行完成后执行，如果连接点抛出异常，则不会执行。
@AfterThrowing("testAspect()")					  	//异常返回通知
		在连接点抛出异常后执行。 
@After("testAspect()")								//返回通知
		在连接点执行完成后执行，不管是正常执行完成，还是抛出异常，都会执行返回通知中的内容。 
@Around()("testAspect()")							//环绕通知	
		环绕通知围绕在连接点前后，比如一个方法调用的前后。这是最强大的通知类型，能在方法调用前后自定义一些操作。环绕通知还需要负责决定是继续处理join point(调用ProceedingJoinPoint的proceed方法)还是中断执行。 
```

# 获取代理信息

```java

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

```java
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

# 结合注解修改参数

​    	我们现在想通过一个注解进行修改我们请求的参数，例如如果我的注解中写到了一个数据，现在请求过来了有一个字段我们需要将他给修改了，如果注解中设置了修改我们就修改参数，如果他没有设置我们就使用原来的参数，下面我们来开始使用吧

​		首先新建注解	新建一个注解@MethodUpArgs,意思是修改方法中的参数，我们来试下

```java
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MethodUpArgs {
    String[] argsName() default "";
    String[] argsValue() default "";
}
```

我们新建好了注解之后，我们去新建一个Controller，我们通过注解，希望将test这个参数改为testAs这个参数

```java
    @MethodUpArgs(argsName = {"test"},argsValue = {"testAs"})
    @GetMapping("test")
    public String test(String test){
        return test;
    }
```

现在我们来使用Aop进行修改，新建一个配置类AspectConfig

代码示例如下

 

```java
@Component
@Aspect
@Slf4j
public class AspectConfig {
	
    /**
     * 根据注解修改参数值
     * @param point 代理信息
     * @param methodUpArgs 注解参数
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(methodUpArgs)",argNames = "methodUpArgs")
    public Object myprocess(ProceedingJoinPoint point,MethodUpArgs methodUpArgs) throws Throwable {
        //获取HttpServletRequest，然后获取头信息，并且搭建用户的系统以及浏览器
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        System.out.println(request.getHeader("User-Agent").toString());

        //获取用户方法签名
        MethodSignature msg = (MethodSignature)point.getSignature();
        //获取方法参数名称
        String[] paramName = msg.getParameterNames();
        //获取传入的参数值
        Object[] args = point.getArgs();
        //将参数名称转为集合
        List<String> paramNameList = Arrays.asList(paramName);
        //循环遍历需要修改的参数
        if(methodUpArgs.argsName().length > 0 && methodUpArgs.argsName() != null){
            for (int i = 0; i < methodUpArgs.argsName().length; i++) {
                //非空判断
                if(!StringUtils.isEmpty(methodUpArgs.argsName()[i])){
                    //判断是否包含参数
                    if (paramNameList.contains(methodUpArgs.argsName()[i])) {
                        //获取参数位置
                        Integer pos = paramNameList.indexOf(methodUpArgs.argsName()[i]);
                        //判断值是否为空
                        if (StringUtils.isEmpty(methodUpArgs.argsValue()[i])){
                            System.out.println("取消修改参数！！！");
                        }else{
                            args[pos] = methodUpArgs.argsValue()[i];
                            System.out.println("修改为注解参数" + methodUpArgs.argsValue()[i]);
                        }
                    }
                }
            }
        }
        //重新返回参数对象
        Object result = point.proceed(args);
        return result;
    }
}
```

# 记录方法执行时间以及日志

```java
    //需要代理的表达式，com.kang.boot.test.controller包下所有方法
    @Pointcut("execution(* com.kang.boot.test.controller.**.*(..))")
    private void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        log.info("请求URL: " + request.getRequestURL().toString());//打印请求url
        log.info("请求IP: " + request.getRemoteAddr());//打印请求IP来源


        // 获取目标Logger
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        // 获取目标类名称
        String clazzName = joinPoint.getTarget().getClass().getName();
        // 获取目标类方法名称
        String methodName = joinPoint.getSignature().getName();
        //获取开始时间戳
        long start = System.currentTimeMillis();
        logger.info( "{}: {}: 开始执行方法...", clazzName, methodName);
        // 调用目标方法
        Object result = joinPoint.proceed();
        //计算运行时间
        long time = System.currentTimeMillis() - start;
        logger.info( "{}: {}: 方法执行结束... 执行时间: {} ms", clazzName, methodName, time);
        return result;
    }
```

# 代理注解

## 代理类上注解

我们这里代理类上面的注解，使用@within(methodUpArgs)，然后参数中加入注解

```java
@Around(value = "@within(methodUpArgs)",argNames = "methodUpArgs")
public Object myprocess(ProceedingJoinPoint point,MethodUpArgs methodUpArgs) throws Throwable {
    //获取HttpServletRequest，然后获取头信息，并且搭建用户的系统以及浏览器
    ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    log.info(request.getHeader("User-Agent").toString());
    //获取用户方法签名
    MethodSignature msg = (MethodSignature)point.getSignature();
    //获取方法参数名称
    String[] paramName = msg.getParameterNames();
    //获取传入的参数值
    Object[] args = point.getArgs();
    //将参数名称转为集合
    List<String> paramNameList = Arrays.asList(paramName);
    if(!methodUpArgs.enable()){
        System.out.println("取消修改参数");
    }
    //循环遍历需要修改的参数
    if(methodUpArgs.argsName().length > 0 && methodUpArgs.argsName() != null){
        for (int i = 0; i < methodUpArgs.argsName().length; i++) {
            //非空判断
            if(!StringUtils.isEmpty(methodUpArgs.argsName()[i])){
                //判断是否包含参数
                if (paramNameList.contains(methodUpArgs.argsName()[i])) {
                    //获取参数位置
                    Integer pos = paramNameList.indexOf(methodUpArgs.argsName()[i]);
                    //判断值是否为空
                    if (StringUtils.isEmpty(methodUpArgs.argsValue()[i])){
                        log.info("取消修改参数！！！");
                    }else{
                        args[pos] = methodUpArgs.argsValue()[i];
                        log.info("修改为注解参数" + methodUpArgs.argsValue()[i]);
                    }
                }
            }
        }
    }
    //执行方法
    Object result = point.proceed(args);
    return result;
}
```

## 代理方法上注解

方法上的注解我们使用@annotation

```java
@Around(value = "@annotation(methodUpArgs)",argNames = "methodUpArgs")
public Object myprocess(ProceedingJoinPoint point,MethodUpArgs methodUpArgs) throws Throwable {
    //获取HttpServletRequest，然后获取头信息，并且搭建用户的系统以及浏览器
    ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    log.info(request.getHeader("User-Agent").toString());
    //获取用户方法签名
    MethodSignature msg = (MethodSignature)point.getSignature();
    //获取方法参数名称
    String[] paramName = msg.getParameterNames();
    //获取传入的参数值
    Object[] args = point.getArgs();
    //将参数名称转为集合
    List<String> paramNameList = Arrays.asList(paramName);
    if(!methodUpArgs.enable()){
        System.out.println("取消修改参数");
    }
    //循环遍历需要修改的参数
    if(methodUpArgs.argsName().length > 0 && methodUpArgs.argsName() != null){
        for (int i = 0; i < methodUpArgs.argsName().length; i++) {
            //非空判断
            if(!StringUtils.isEmpty(methodUpArgs.argsName()[i])){
                //判断是否包含参数
                if (paramNameList.contains(methodUpArgs.argsName()[i])) {
                    //获取参数位置
                    Integer pos = paramNameList.indexOf(methodUpArgs.argsName()[i]);
                    //判断值是否为空
                    if (StringUtils.isEmpty(methodUpArgs.argsValue()[i])){
                        log.info("取消修改参数！！！");
                    }else{
                        args[pos] = methodUpArgs.argsValue()[i];
                        log.info("修改为注解参数" + methodUpArgs.argsValue()[i]);
                    }
                }
            }
        }
    }
    //执行方法
    Object result = point.proceed(args);
    return result;
}
```

写好之后controller如下，我们将请求的companyFullName给修改为“修改后参数”

```java
    @GetMapping("test")
    @MethodUpArgs(argsName= {"companyFullName"},argsValue = {"修改后参数"})
    public String test(String companyFullName){
        String and = "(";
        List<String> address = HanlpUtil.baseCoreWord(companyFullName, "ns");
        for (int i = 0; i < address.size(); i++) {
            if(i > 0){
                and += "AND";
            }
            and += "\"" + address.get(i) + "\"";
        }
        and += ")";
        //or条件
        String or = "(";
        //核心短语
        List<String> phrase = HanlpUtil.corePhrase(companyFullName, 2);
        for (int i = 0; i < phrase.size(); i++) {
            if(i > 0){
                or += "OR";
            }
            or += "\"" + phrase.get(i) + "\"";
        }

        List<String> keyword = HanlpUtil.corekeyword(companyFullName, 3);
        for (int i = 0; i < keyword.size(); i++) {
            or += "OR\"" + keyword.get(i) + "\"";
        }
        or += ")";
        String result = and + "AND" + or;
        return result;
    }
```



# 利用AOP记录日志操作流程

## 创建注解

首先我们创建一个注解，只要是我们加上了这个注解的类，对他进行切面的时候则进行Log日志打印

```java
/**
 * @Author BigKang
 * @Date 2020/3/19 11:15 AM
 * @Summarize 记录日志名称
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogName {

    /**
     * 日志记录名称
     * @return
     */
    String value();
}

```

## 配置切面

我们会给一些

```java
/**
 * @Author BigKang
 * @Date 2020/3/19 10:47 AM
 * @Summarize 操作日志Aop切面
 */
@Component
@Aspect
@Slf4j
public class DataxLogAspect {

		// 记录通过Base接口save的切面关注点
		@Pointcut("execution(public * com.topcom.cms.mongo.base.BaseController.save*(..))")
    public void dataxSave1(){}

    // 记录某一个Controller包下面的Controller的所有save方法
    @Pointcut("execution(public * com.topcom.*.controller.*Controller.save*(..))")
    public void dataxSave2(){}

    @Pointcut("execution(public * com.topcom.cms.mongo.base.BaseController.delete*(..))")
    public void dataxDelete1(){}

    @Pointcut("execution(public * com.topcom.*.controller.*Controller.delete*(..))")
    public void dataxDelete2(){}


    @Pointcut("execution(public * com.topcom.cms.mongo.base.BaseController.update*(..))")
    public void dataxUpdate1(){}

    @Pointcut("execution(public * com.topcom.*.controller.*Controller.update*(..))")
    public void dataxUpdate2(){}

    // 正常返回通知，表示添加成功，下方删除修改同理
    @AfterReturning(returning="result", pointcut="dataxSave1() || dataxSave2()")
    public void doAfterReturnintSave(JoinPoint joinPoint, Object result){
				// 同构joinPoint点获取目标类，并且判断是否包含LogName注解
        Annotation annotation = joinPoint.getTarget().getClass().getAnnotation(LogName.class);
        if(annotation == null){
            return;
        }
      	// 获取Request请求
        ServletRequestAttributes sra =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        LogName logName = (LogName) annotation;
        // 通过方法创建日志实体类，并且从request中取出token解析，获取用户，插入数据库，此步骤可以省略
        OperationDataxLog log = genDataxLog(request, joinPoint,logName,"添加");
    		// 标记类型为添加
        log.setType("SAVE");
        // 将所有的返回的data数据记录
        log.setReturnData(result);
        operationDataxLogDao.save(log);
    }

    @AfterReturning(returning="result", pointcut="dataxDelete1() || dataxDelete2()")
    public void doAfterReturnintDelete(JoinPoint joinPoint, Object result){

        Annotation annotation = joinPoint.getTarget().getClass().getAnnotation(LogName.class);
        if(annotation == null){
            return;
        }
        ServletRequestAttributes sra =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        LogName logName = (LogName) annotation;
        OperationDataxLog log = genDataxLog(request, joinPoint,logName,"删除");

        log.setReturnData(result);
        log.setType("DELETE");
        operationDataxLogDao.save(log);
    }

    @AfterReturning(returning="result", pointcut="dataxUpdate1() || dataxUpdate2()")
    public void doAfterReturnintUpdate(JoinPoint joinPoint, Object result){

        Annotation annotation = joinPoint.getTarget().getClass().getAnnotation(LogName.class);
        if(annotation == null){
            return;
        }
        ServletRequestAttributes sra =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        LogName logName = (LogName) annotation;
        OperationDataxLog log = genDataxLog(request, joinPoint,logName,"修改");

        log.setReturnData(result);

        log.setType("UPDATE");
        operationDataxLogDao.save(log);
    }


}
```

## 日志使用

我们只需要在类上添加一个LogName接口即可

```java
@RestController
@RequestMapping("subjectTable")
@Api(tags = "主题表接口")
@LogName("主题表")
public class SubjectTableController extends BaseController<SubjectTable, String, SubjectTableService> {
}
```

