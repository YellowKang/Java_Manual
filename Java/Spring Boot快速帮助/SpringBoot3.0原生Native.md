# 什么是Spring Native？

我们先来看看官方的描述:

​		Spring Native 支持使用[GraalVM](https://www.graalvm.org/) [本机映像](https://www.graalvm.org/reference-manual/native-image/)编译器将 Spring 应用程序编译为本机可执行文件。

​		与 Java 虚拟机相比，本机映像可以为多种类型的工作负载提供更便宜、更可持续的托管。其中包括非常适合容器的微服务、功能工作负载和[Kubernetes](https://kubernetes.io/)。

​		使用本机映像具有关键优势，例如即时启动、即时峰值性能和减少内存消耗。

​		GraalVM 原生项目还存在一些缺点和权衡，预计随着时间的推移会得到改进。构建本机映像是一个繁重的过程，比常规应用程序要慢。本机映像在预热后的运行时优化较少。最后，它不如 JVM 成熟，并且有一些不同的行为。

常规 JVM 和本机映像平台之间的主要区别是：

- ​	在构建时从主入口点对应用程序进行静态分析。
- ​	未使用的部分在构建时被删除。
- ​	反射、资源和动态代理需要配置。
- ​	类路径在构建时是固定的。
- ​	无类延迟加载：可执行文件中提供的所有内容都将在启动时加载到内存中。
- ​	一些代码将在构建时运行。
- ​	Java 应用程序的某些方面存在一些[限制](https://www.graalvm.org/reference-manual/native-image/Limitations/)，未得到完全支持。

该项目的目标是孵化对 Spring Native（Spring JVM 的替代方案）的支持，并提供旨在打包在轻量级容器中的本机部署选项。在实践中，目标是在这个新平台上支持您的 Spring 应用程序，几乎无需修改。

​		**总结**：简单的来说就是将Java代码使用GraalVM的native原生镜像，把我们的Spring应用程序直接编译成二进制的本机可以执行的文件程序，例如之前我们需要将Java项目打包成Jar包，然后放到**JRE（Java运行时环境）**环境下启动运行，现在我们可以直接打包成二进制的应用程序，只要安装了GraalVM，相比较于**JRE（Java运行时环境）**的好处就是上面的区别，例如静态分析，以及未被使用的部分会被删除，没有类延迟加载，一些代码会在构建时运行。

​		同样也会有些缺点，例如反射、资源和动态代理需要配置，Java 应用程序的某些方面存在一些限制未得到完全支持。

# Spring Native核心模块

Spring Native由以下模块组成

- `spring-native`：运行 Spring Native 所需的运行时依赖，还提供[Native 提示](https://docs.spring.io/spring-native/docs/0.12.1/reference/htmlsingle/#native-hints)API。
- `spring-native-configuration`：Spring AOT 插件使用的 Spring 类的配置提示，包括各种 Spring Boot 自动配置。
- `spring-native-docs`：参考指南，asciidoc 格式。
- `spring-native-tools`：用于检查映像构建配置和输出的工具。
- `spring-aot`：Maven 和 Gradle 插件通用的 AOT 生成基础设施。
- `spring-aot-test`：测试特定的 AOT 生成基础设施。
- `spring-aot-gradle-plugin`：调用 AOT 生成的 Gradle 插件。
- `spring-aot-maven-plugin`：调用 AOT 生成的 Maven 插件。
- `samples`：包含演示功能使用并用作集成测试的各种示例。

# 初始化SpringBoot3以及SpringNative

## 使用版本

​				**SpringBoot3.1.5**

​				JDK下载地址：https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-17.0.9

​				使用graalvm JDK，否则可能编译原生时有问题，推荐使用Jenv管理环境版本

​		版本说明：

- ​							  **JDK**		>=		17
- ​						**Maven**		>=		3.6.3
- ​						**Gradle**		>=		7.5
- ​						**Spring**		>=		6.0.13
- ​					  **Tomcat**		>=		10.1
- ​						   **Jetty**		>=		11.0
- ​				 **Undertow**		>=		2.3

​		其他环境：

- **GraalVM Community**    >=		22.3
- ​	 **Native Build Tools**    >=		0.9.27

## 初始化

​		pom.xml

```xml
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

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
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
          	<plugin>
              <groupId>org.graalvm.buildtools</groupId>
              <artifactId>native-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

​		main方法

```java
@SpringBootApplication
public class TestSpringNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSpringNativeApplication.class, args);
    }

}

```

​		controller控制器

```java

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuangKang
 * @date 2023/11/6 17:38:36
 * @describe 测试控制器
 */
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("name")
    public String name() {
        return "BigKang";
    }

}

```

​		直接启动即可访问 http://localhost:8080/test/name 即可

## 打包原生应用

​		直接使用命令打包即可

```bash
# 设置环境变量，打包时用到了GRAALVM_HOME环境变量进行编译
export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-community-openjdk-17.0.9+9.1/Contents/Home

# 编译将项目代码编译，并且编译二进制执行文件
/Users/bigkang/Documents/apache-maven-3.9.5/bin/mvn -DskipTests=true -Pnative native:compile


mvn -Pnative -DskipTests clean native:compile
```

​		封装镜像

```dockerfile
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.9

RUN microdnf install curl
RUN microdnf install iputils
RUN microdnf install nc
RUN microdnf install -y tzdata
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN microdnf update && microdnf install sudo iputils hostname findutils less nano && microdnf clean all
WORKDIR /work/
#RUN chown 1001 /work \
#    && chmod "g+rwX" /work \
#    && chown 1001:root /work
#COPY --chown=1001:root target/*-runner /work/application

COPY target/*-runner /work/application

EXPOSE 8080
#USER 1001
USER root

ENTRYPOINT ["./application"]


docker build  -t test-boot3-native:v1.0 .
```



## 打包Native Image镜像

```bash
# 设置环境变量，打包时用到了GRAALVM_HOME环境变量进行编译
export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-community-openjdk-17.0.9+9.1/Contents/Home

# 设置环境
export SPRING_PROFILES_ACTIVE=dev

# 将项目进行打包打包成镜像
/Users/bigkang/Documents/apache-maven-3.9.5/bin/mvn  -Pnative -DskipTests=true spring-boot:build-image
```

## 打包Jar包

```bash
# 将项目打包成jar包
/Users/bigkang/Documents/apache-maven-3.9.5/bin/mvn -DskipTests=true -Pnative package
```

```
docker run -p 8081:8081 --name test-native -e SPRING_PROFILES_ACTIVE=dev test-native:0.0.1-SNAPSHOT
```





# 集成Mybatis-Plus

## 新增依赖

```xml
		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>mybatis-plus-spring-boot3-starter</artifactId>
			<version>3.5.6</version>
		</dependency>
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.32</version>
    </dependency>
		<dependency>
		  <groupId>org.graalvm.sdk</groupId>
		  <artifactId>graal-sdk</artifactId>
		  <version>22.3.1</version>
		  <scope>provided</scope>
		</dependency>
		<dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.29</version>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
```

## Mybatis-Plus Native兼容

​		新增配置文件类MyBatisNativeConfiguration

```java
package com.example.demo.config;


import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.handlers.CompositeEnumTypeHandler;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.RuntimeSupport;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This configuration will move to mybatis-spring-native.
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(MyBatisNativeConfiguration.MyBaitsRuntimeHintsRegistrar.class)
public class MyBatisNativeConfiguration {

    @Bean
    MyBatisBeanFactoryInitializationAotProcessor myBatisBeanFactoryInitializationAotProcessor() {
        return new MyBatisBeanFactoryInitializationAotProcessor();
    }

    @Bean
    static MyBatisMapperFactoryBeanPostProcessor myBatisMapperFactoryBeanPostProcessor() {
        return new MyBatisMapperFactoryBeanPostProcessor();
    }

    static class MyBaitsRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            Stream.of(RawLanguageDriver.class,
                    // TODO 增加了MybatisXMLLanguageDriver.class
                    XMLLanguageDriver.class, MybatisXMLLanguageDriver.class,
                    RuntimeSupport.class,
                    ProxyFactory.class,
                    Slf4jImpl.class,
                    Log.class,
                    JakartaCommonsLoggingImpl.class,
                    Log4j2Impl.class,
                    Jdk14LoggingImpl.class,
                    StdOutImpl.class,
                    NoLoggingImpl.class,
                    SqlSessionFactory.class,
                    PerpetualCache.class,
                    FifoCache.class,
                    LruCache.class,
                    SoftCache.class,
                    WeakCache.class,
                    //TODO 增加了MybatisSqlSessionFactoryBean.class
                    SqlSessionFactoryBean.class, MybatisSqlSessionFactoryBean.class,
                    ArrayList.class,
                    HashMap.class,
                    TreeSet.class,
                    HashSet.class
            ).forEach(x -> hints.reflection().registerType(x, MemberCategory.values()));
            Stream.of(
                    "org/apache/ibatis/builder/xml/*.dtd",
                    "org/apache/ibatis/builder/xml/*.xsd"
            ).forEach(hints.resources()::registerPattern);

            hints.serialization().registerType(SerializedLambda.class);
            hints.serialization().registerType(SFunction.class);
            hints.serialization().registerType(java.lang.invoke.SerializedLambda.class);
            hints.reflection().registerType(SFunction.class);
            hints.reflection().registerType(SerializedLambda.class);
            hints.reflection().registerType(java.lang.invoke.SerializedLambda.class);

            hints.proxies().registerJdkProxy(StatementHandler.class);
            hints.proxies().registerJdkProxy(Executor.class);
            hints.proxies().registerJdkProxy(ResultSetHandler.class);
            hints.proxies().registerJdkProxy(ParameterHandler.class);

//        hints.reflection().registerType(MybatisPlusInterceptor.class);
            hints.reflection().registerType(AbstractWrapper.class, MemberCategory.values());
            hints.reflection().registerType(LambdaQueryWrapper.class, MemberCategory.values());
            hints.reflection().registerType(LambdaUpdateWrapper.class, MemberCategory.values());
            hints.reflection().registerType(UpdateWrapper.class, MemberCategory.values());
            hints.reflection().registerType(QueryWrapper.class, MemberCategory.values());

            hints.reflection().registerType(BoundSql.class, MemberCategory.DECLARED_FIELDS);
            hints.reflection().registerType(RoutingStatementHandler.class, MemberCategory.DECLARED_FIELDS);
            hints.reflection().registerType(BaseStatementHandler.class, MemberCategory.DECLARED_FIELDS);
            hints.reflection().registerType(MybatisParameterHandler.class, MemberCategory.DECLARED_FIELDS);


            hints.reflection().registerType(IEnum.class, MemberCategory.INVOKE_PUBLIC_METHODS);
            // register typeHandler
            hints.reflection().registerType(CompositeEnumTypeHandler.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            hints.reflection().registerType(FastjsonTypeHandler.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            hints.reflection().registerType(GsonTypeHandler.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            hints.reflection().registerType(JacksonTypeHandler.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            hints.reflection().registerType(MybatisEnumTypeHandler.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        }
    }

    static class MyBatisBeanFactoryInitializationAotProcessor
            implements BeanFactoryInitializationAotProcessor, BeanRegistrationExcludeFilter {

        private final Set<Class<?>> excludeClasses = new HashSet<>();

        MyBatisBeanFactoryInitializationAotProcessor() {
            excludeClasses.add(MapperScannerConfigurer.class);
        }

        @Override
        public boolean isExcludedFromAotProcessing(RegisteredBean registeredBean) {
            return excludeClasses.contains(registeredBean.getBeanClass());
        }

        @Override
        public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
            String[] beanNames = beanFactory.getBeanNamesForType(MapperFactoryBean.class);
            if (beanNames.length == 0) {
                return null;
            }
            return (context, code) -> {
                RuntimeHints hints = context.getRuntimeHints();
                for (String beanName : beanNames) {
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName.substring(1));
                    PropertyValue mapperInterface = beanDefinition.getPropertyValues().getPropertyValue("mapperInterface");
                    if (mapperInterface != null && mapperInterface.getValue() != null) {
                        Class<?> mapperInterfaceType = (Class<?>) mapperInterface.getValue();
                        if (mapperInterfaceType != null) {
                            registerReflectionTypeIfNecessary(mapperInterfaceType, hints);
                            hints.proxies().registerJdkProxy(mapperInterfaceType);
                            hints.resources()
                                    .registerPattern(mapperInterfaceType.getName().replace('.', '/').concat(".xml"));
                            registerMapperRelationships(mapperInterfaceType, hints);
                        }
                    }
                }
            };
        }

        private void registerMapperRelationships(Class<?> mapperInterfaceType, RuntimeHints hints) {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(mapperInterfaceType);
            for (Method method : methods) {
                if (method.getDeclaringClass() != Object.class) {
                    ReflectionUtils.makeAccessible(method);
                    registerSqlProviderTypes(method, hints, SelectProvider.class, SelectProvider::value, SelectProvider::type);
                    registerSqlProviderTypes(method, hints, InsertProvider.class, InsertProvider::value, InsertProvider::type);
                    registerSqlProviderTypes(method, hints, UpdateProvider.class, UpdateProvider::value, UpdateProvider::type);
                    registerSqlProviderTypes(method, hints, DeleteProvider.class, DeleteProvider::value, DeleteProvider::type);
                    Class<?> returnType = MyBatisMapperTypeUtils.resolveReturnClass(mapperInterfaceType, method);
                    registerReflectionTypeIfNecessary(returnType, hints);
                    MyBatisMapperTypeUtils.resolveParameterClasses(mapperInterfaceType, method)
                            .forEach(x -> registerReflectionTypeIfNecessary(x, hints));
                }
            }
        }

        @SafeVarargs
        private <T extends Annotation> void registerSqlProviderTypes(
                Method method, RuntimeHints hints, Class<T> annotationType, Function<T, Class<?>>... providerTypeResolvers) {
            for (T annotation : method.getAnnotationsByType(annotationType)) {
                for (Function<T, Class<?>> providerTypeResolver : providerTypeResolvers) {
                    registerReflectionTypeIfNecessary(providerTypeResolver.apply(annotation), hints);
                }
            }
        }

        private void registerReflectionTypeIfNecessary(Class<?> type, RuntimeHints hints) {
            if (!type.isPrimitive() && !type.getName().startsWith("java")) {
                hints.reflection().registerType(type, MemberCategory.values());
            }
        }

    }

    static class MyBatisMapperTypeUtils {
        private MyBatisMapperTypeUtils() {
            // NOP
        }

        static Class<?> resolveReturnClass(Class<?> mapperInterface, Method method) {
            Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
            return typeToClass(resolvedReturnType, method.getReturnType());
        }

        static Set<Class<?>> resolveParameterClasses(Class<?> mapperInterface, Method method) {
            return Stream.of(TypeParameterResolver.resolveParamTypes(method, mapperInterface))
                    .map(x -> typeToClass(x, x instanceof Class ? (Class<?>) x : Object.class)).collect(Collectors.toSet());
        }

        private static Class<?> typeToClass(Type src, Class<?> fallback) {
            Class<?> result = null;
            if (src instanceof Class<?>) {
                if (((Class<?>) src).isArray()) {
                    result = ((Class<?>) src).getComponentType();
                } else {
                    result = (Class<?>) src;
                }
            } else if (src instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) src;
                int index = (parameterizedType.getRawType() instanceof Class
                        && Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                        && parameterizedType.getActualTypeArguments().length > 1) ? 1 : 0;
                Type actualType = parameterizedType.getActualTypeArguments()[index];
                result = typeToClass(actualType, fallback);
            }
            if (result == null) {
                result = fallback;
            }
            return result;
        }

    }

    static class MyBatisMapperFactoryBeanPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {

        private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(
                MyBatisMapperFactoryBeanPostProcessor.class);

        private static final String MAPPER_FACTORY_BEAN = "org.mybatis.spring.mapper.MapperFactoryBean";

        private ConfigurableBeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }

        @Override
        public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
            if (ClassUtils.isPresent(MAPPER_FACTORY_BEAN, this.beanFactory.getBeanClassLoader())) {
                resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);
            }
        }

        private void resolveMapperFactoryBeanTypeIfNecessary(RootBeanDefinition beanDefinition) {
            if (!beanDefinition.hasBeanClass() || !MapperFactoryBean.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                return;
            }
            if (beanDefinition.getResolvableType().hasUnresolvableGenerics()) {
                Class<?> mapperInterface = getMapperInterface(beanDefinition);
                if (mapperInterface != null) {
                    // Exposes a generic type information to context for prevent early initializing
                    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                    constructorArgumentValues.addGenericArgumentValue(mapperInterface);
                    beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
                    beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(beanDefinition.getBeanClass(), mapperInterface));
                }
            }
        }

        private Class<?> getMapperInterface(RootBeanDefinition beanDefinition) {
            try {
                return (Class<?>) beanDefinition.getPropertyValues().get("mapperInterface");
            } catch (Exception e) {
                LOG.debug("Fail getting mapper interface type.", e);
                return null;
            }
        }

    }
}
```

## Mybatis-Plus配置

```java
package com.example.demo.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author HuangKang
 * @date 2024/6/4 17:25:38
 * @describe Mybatis-Plus
 */
@Configuration
@MapperScan(basePackages = {"com.example.demo.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return plusInterceptor;
    }

}
```



## Lambda支持

​		新增配置类LambdaRegistrationFeature,注意后续的Service以及实现类以及其他都需要添加注册类，否则运行时Lambda表达式会报错

```java
package com.example.demo.config;

import com.example.demo.controller.TestController;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

/**
 * @author HuangKang
 * @date 2024/6/4 09:06:45
 * @describe Lambda注册特征（注册使用Lambda创建使用到的类，如Service中使用了MyBaitsPlus Query Lambda进行查询，则需要将Service注册进来）
 * 如果不注册编译无异常，运行时则会找不到
 */
public class LambdaRegistrationFeature implements Feature {
    @Override
    public void duringSetup(DuringSetupAccess access) {
        RuntimeSerialization.registerLambdaCapturingClass(TestController.class);
    }
}
```

​		修改pom.xml编译插件,修改native-maven-plugin 如下

```xml
			<plugin>
				<groupId>org.graalvm.buildtools</groupId>
				<artifactId>native-maven-plugin</artifactId>
				<configuration>
				  <buildArgs combine.children="append">
					<buildArg>--enable-url-protocols=http</buildArg>
					<buildArg>--features=com.example.demo.config.LambdaRegistrationFeature</buildArg>
				  </buildArgs>
				</configuration>
			</plugin>
```

## 使用

### Entity

```java
package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author HuangKang
 * @date 2024/6/3 11:16:09
 * @describe 用户
 */

@Data
@TableName(value = "t_user",autoResultMap = true)
public class User extends Model<User> {

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("user_name")
    private String userName;

    @TableField("user_status")
    private String userStatus;

    @TableField("password")
    private String passWord;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("create_time")
    private LocalDateTime createTime;


}

```

### Mapper

```java
package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> list2();

}

```

### MapperXml

​		**！！！！注意**：xml文件放入resources时需要和包名保持一致，否则编译以及jar包正常，打包native运行后找不到Mapper

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.example.demo.mapper.UserMapper">


    <select id="list2" resultType="com.example.demo.entity.User">
        SELECT * FROM t_user limit 2
    </select>


</mapper>
```

### IService（不兼容，无法使用IService以及Impl）

### Controller

​		注意Wrappers使用方式，以及XmlMapper兼容，以及使用Lambda类需要提前注册

```java
package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private UserMapper userMapper;


    @GetMapping("list")
    public Object list() {
        return userMapper.selectList(new LambdaQueryWrapper<User>());
    }


    @GetMapping("page")
    public Object page(@RequestParam Integer page) {
        return userMapper.selectPage(new Page<>(page,1),new LambdaQueryWrapper<User>());
    }

    @GetMapping("get")
    public Object get(@RequestParam(required = false) Long id) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(id != null, User::getId, id), false);
    }

    @GetMapping("list2")
    public Object lis2() {
        return userMapper.list2();
    }

}

```



# springboot-native原生镜像打包编译

## 编译环境

```sh
cat << EOF > builder-graalvm17-maven-docker-file
# 使用 Ubuntu 20.04 作为基础镜像
FROM ubuntu:20.04
ENV TZ=Asia/Shanghai
# 安装基本依赖工具和 JDK 17（GraalVM）
RUN apt-get update
RUN	apt-get install -y curl
RUN	apt-get install -y wget 
RUN	apt-get install -y unzip 
RUN	apt-get install -y build-essential
RUN apt-get install -y zlib1g-dev
RUN rm -rf /var/lib/apt/lists/*

# 安装 GraalVM 17
RUN curl -fsSL https://download.oracle.com/graalvm/17/latest/graalvm-jdk-17_linux-x64_bin.tar.gz -o /tmp/graalvm.tar.gz

RUN	mkdir -p /opt/graalvm 
RUN	tar -xzf /tmp/graalvm.tar.gz -C /opt/graalvm --strip-components=1
RUN rm /tmp/graalvm.tar.gz

# 设置环境变量
ENV JAVA_HOME=/opt/graalvm
ENV GRAALVM_HOME=/opt/graalvm
ENV PATH=$JAVA_HOME/bin:$PATH

# 安装 Maven
RUN wget https://downloads.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz -O /tmp/maven.tar.gz && \
    tar -xzf /tmp/maven.tar.gz -C /opt && \
    mv /opt/apache-maven-3.9.5 /opt/maven && \
    rm /tmp/maven.tar.gz

ENV MAVEN_HOME=/opt/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$JAVA_HOME/bin:/opt/graalvm/bin:/opt/maven/bin
# 验证安装
RUN java -version
RUN mvn -version
EOF

docker build -f builder-graalvm17-maven-docker-file -t builder-graalvm17-maven .
```

## 项目编译

```sh
cat << EOF > builder-native-image

# 第一阶段：使用 builder-graalvm17-maven 镜像进行构建
FROM builder-graalvm17-maven AS builder

# 设置工作目录
WORKDIR /build
ENV GRAALVM_HOME=/opt/graalvm
# 复制本地项目到容器中
COPY . .

# 指定默认环境
ARG SPRING_PROFILE=dev

# 构建应用程序
RUN mvn -DskipTests=true -Dspring.profiles.active=${SPRING_PROFILE} -Pnative native:compile -X

RUN mkdir /runtime
RUN cp ./target/boot3-native /runtime/application

# 清理构建环境
RUN rm -rf /build

# 第二阶段：使用 UBI 最小镜像作为基础镜像，处理构建好的文件
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.9

RUN microdnf install curl
RUN microdnf install iputils
RUN microdnf install nc

# 安装必要的工具和依赖项
RUN microdnf install curl iputils nc tzdata &&     ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime &&     microdnf update && microdnf install -y sudo iputils hostname findutils less nano && microdnf clean all

# 设置工作目录
WORKDIR /work

# 从第一阶段的 builder 阶段复制构建好的应用程序
COPY --from=builder /runtime/application /work/application

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}

# 暴露端口
EXPOSE 8087

# 设置容器用户
USER root

# 设置入口点
ENTRYPOINT ["/work/application"]

EOF


# 编译打包镜像
docker build --progress=plain -f builder-native-image -t native-image:boot3 --build-arg SPRING_PROFILE=prod 
```

​		容器启动

```sh
docker run -it --rm -e SPRING_PROFILES_ACTIVE=prod --net=host -p 8087:8087 native-image:boot3 sh
```

# 集成knife4j

## 新增依赖

```xml
		<dependency>
			<groupId>com.github.xiaoymin</groupId>
			<artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
			<version>4.4.0</version>
		</dependency>
```

## 配置

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /v3/api-docs
  group-configs:
    - group: 'default'
      paths-to-match: '/**'
      packages-to-scan:
        - com.example.demo
knife4j:
  enable: true
  setting:
    language: zh_cn
```

## 文档

### 实体

```java
package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author HuangKang
 * @date 2024/6/3 11:16:09
 * @describe 用户
 */

@Data
@TableName(value = "t_user",autoResultMap = true)
@Schema(description = "用户实体")
public class User extends Model<User> {

    @TableId(type = IdType.AUTO, value = "id")
    @Schema(description = "主键")
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private String userId;

    @TableField("user_name")
    @Schema(description = "用户名")
    private String userName;

    @TableField("user_status")
    @Schema(description = "用户状态")
    private String userStatus;

    @TableField("password")
    @Schema(description = "密码")
    private String passWord;

    @TableField("deleted")
    @Schema(description = "删除标记")
    private Boolean deleted;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;


}
```

### Controller

```java
@RestController
@RequestMapping("test")
@Tag(name = "测试控制器")
public class TestController {

    @Autowired
    private UserMapper userMapper;


    @GetMapping("list")
    @Operation(summary = "List查询")
    public Object list() {
        return userMapper.selectList(new LambdaQueryWrapper<User>());
    }
}
```

