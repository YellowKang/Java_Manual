# 注解概述

​		Annontation是Java5开始引入的新特征，中文名称叫注解。它提供了一种安全的类似注释的机制，用来将任何的信息或元数据（metadata）与程序元素（类、方法、成员变量等）进行关联。为程序的元素（类、方法、成员变量）加上更直观更明了的说明，这些说明信息是与程序的业务逻辑无关，并且供指定的工具或框架使用。Annontation像一种修饰符一样，应用于包、类型、构造方法、方法、成员变量、参数及本地变量的声明语句中。

　　Java注解是附加在代码中的一些元信息，用于一些工具在编译、运行时进行解析和使用，起到说明、配置的功能。注解不会也不能影响代码的实际逻辑，仅仅起到辅助性的作用。包含在 java.lang.annotation 包中。

​		注解本质是一个继承了Annotation的特殊接口，其具体实现类是Java运行时生成的动态代理类。而我们通过反射获取注解时，返回的是Java运行时生成的动态代理对象$Proxy1。通过代理对象调用自定义注解（接口）的方法，会最终调用AnnotationInvocationHandler的invoke方法。该方法会从memberValues这个Map中索引出对应的值。而memberValues的来源是Java常量池。

# 注解核心

java.lang.annotation提供了四种元注解，专门注解其他的注解（在自定义注解的时候，需要使用到元注解）：

## @Documented 

​			注解表明这个注解应该被 javadoc工具记录. 默认情况下,javadoc是不包括注解的. 但如果声明注解时指定了 @Documented,则它会被 javadoc 之类的工具处理, 所以注解类型信息也会被包括在生成的文档中，是一个标记注解，没有成员。

## @Retention 

​			什么时候使用该注解：

​				RetentionPolicy.SOURCE							注解在源码时有效，将会被编译器抛弃。

​				RetentionPolicy.CLASS								注解在编译时有效，但在运行时没有保留。这也是默认行为。

​				RetentionPolicy.RUNTIME						  运行时有效，并且可以通过反射获取。

## @Target 

​			注解用于什么地方：														  作用范围

​				ElementType.TYPE														类、接口（包括注解类型）或enum声明

​				ElementType.FIELD													  域声明（包括enum实例），以及字段

​				ElementType.METHOD												方法上

​				ElementType.PARAMETER										  参数声明

​				ElementType.CONSTRUCTOR									 构造函数

​				ElementType.LOCAL_VARIABLE								  局部变量声明

​				ElementType.ANNOTATION_TYPE							  在注解中使用注解

​				ElementType.PACKAGE												包声明

​				ElementType.TYPE_USE												类型使用.可以用于标注任意类型除了 class

## @Inherited

​			是否允许子类获取该注解，如果不加这个注解，那么A使用了这个注解，B继承了A，B他是获取不到这个注解的

# 获取一个Class中的注解以及值

首先我们再UserInfo这个类上加上了一个注解叫做@TableName,如下示例

```
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TableName {

    String tableName() default "未设置表名";

}
```

这里的作用范围是运行时，并且只能放在类或者接口枚举上

我们创建一个UserInfo然后给他加上注解，如下示例

```
@Data
@TableName
public class UserInfo {
    private String id;
    private String name;

}
```

我们给了一个默认值所以可以不设置表名

然后我们找个测试类进行测试，我们首先获取Class，然后从Class中拿到注解，然后获取注解值

```
    @Test
    public void test(){
        TableName annotation = UserInfo.class.getAnnotation(TableName.class);
        System.out.println(annotation.tableName());
    }
```

这里有设置默认值，应该是 ： 	未设置表名

如下图示例

![](img\Annotation——1.png)

下面我们修改表名

![](img\Annotation——2.png)

然后再次运行

![](img\Annotation——3.png)

应该就能看到如下的结果了

# 使用动态代理修改参数

我们可以使用动态代理+注解实现修改请求参数，下面我们将会写到一个注解@MethodUpArgs

这个注解的意思就是方法上面update参数

我们首先先创建一个注解这个注解代码如下，这个注解生成文档，并且可以加在方法以及类上面，在运行时生效，并且子类可以获取到父类的注解

我们下面定义了3个属性

argsName()		需要修改的参数的名字

argsValue()		 需要修改的参数对应的值

enable() 			 是否开启

```
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MethodUpArgs {

    String[] argsName() default "";
    String[] argsValue() default "";
    boolean enable() default true;

}
```

下面我们再写一个代理类，我们先引入AOP的maven依赖

```
        <!-- SpringBoot整合AOP -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
```

然后创建aop配置类

```
@Component
@Aspect
@Slf4j
@Order(100)
public class AspectConfig {

}
```

方法如下两个分别代理为类和方法上的注解

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

