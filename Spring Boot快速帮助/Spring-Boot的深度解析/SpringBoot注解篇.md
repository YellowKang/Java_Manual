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

​			是否允许子类继承该注解

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