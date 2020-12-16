# SpringBoot启动流程

​		我们根据SpringBoot的SpringApplication进行启动的，通过他的Run方法进入进行调试吧，我们跟着Run方法一直跟踪到public ConfigurableApplicationContext run(String... args)，如下所示的代码，就是SpringBoot的启动流程。



```java
	/**
	 * 运行Spring应用程序，创建并刷新一个新应用程序
	 * {@link ApplicationContext}.
	 * @param设置应用程序参数(通常从Java main方法传递)
	 * 返回一个正在运行的{@link ApplicationContext}
	 */
	public ConfigurableApplicationContext run(String... args) {
    // 启动一个停止监听，用于记录SpringApplicationContext的上下文启动时间（非线程安全）
		StopWatch stopWatch = new StopWatch();
    // 开始计时
		stopWatch.start();
    // 创建ConfigurableApplicationContext变量，用于返回
		ConfigurableApplicationContext context = null;
    // 创建异常上报集合，用于存储启动时的异常信息
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    // 设置Headless模式是在缺少显示屏、键盘或者鼠标是的系统配置。在java.awt.toolkit和java.awt.graphicsenvironment类中有许多方法，除了对字体、图形和打印的操作外还可以调用显示器、键盘和鼠标的方法。但是有一些类中，比如Canvas和Panel，可以在headless模式下执行，我们可以设置为true或者false。
		configureHeadlessProperty();
    // 创建获取一个Spring应用程序的监听器，用于记录应用程序执行监听。
		SpringApplicationRunListeners listeners = getRunListeners(args);
    // 启动监听器
		listeners.starting();
		try {
      // 用于创建应用程序参数配置
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
			// 准备Application应用的环境，以及设置。
      ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
      // 根据环境配置决定是否跳过搜索BeanInfo类。
			configureIgnoreBeanInfo(environment);
      // 创建打印的Banner图标
			Banner printedBanner = printBanner(environment);
      // 创建应用程序上下文（核心），他会根据应用程序的类型，来判断初始化哪一种的应用程序上下文如Servlet，Reactive，或者默认的Annotation注解上下文
			context = createApplicationContext();
      // 获取Spring工厂实例，用于报告在Spring初始化时的实例保存信息。
			exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);
      // 准备应用的上下文，将环境以及参数配置等准备
			prepareContext(context, environment, listeners, applicationArguments, printedBanner);
			// 刷新上下文（重点核心），包括程序的启动初始化，以及Bean工厂的处理，以及初始化信息，初始化初始化事件多播，以及初始化Web服务器，注册监听器等等一系列的操作
      refreshContext(context);
      // 刷新后的后置处理操作，我们可以通过集成SpringApplication来进行刷新后的后置操作，默认不做任何处理，子类可以重写。
			afterRefresh(context, applicationArguments);
      // 停止监听器，停止计时，此时应用上下文已经初始化完毕，打印启动时间
			stopWatch.stop();
      // 如果启动日志没有异常，则根据监听器打印启动日志，通过启动的main线程打印日志
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
			}
      // 根据上下文启动监听器，发布应用程序启动事件
			listeners.started(context);
      // 通知各个Runner开始干活了，实现了ApplicationRunner或者CommandLineRunner接口的类，我们通过将它注册到IOC容器中就能进行通知了，例如ApplicationRunner来打印启动成功的信息，并且来初始化一些自定义的东西，例如初始化定时任务等等
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, listeners);
			throw new IllegalStateException(ex);
		}

		try {
      // 发布应用上下文的启动事件，表示上下文启动完毕
			listeners.running(context);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, null);
			throw new IllegalStateException(ex);
		}
		return context;
	}
```

 那么我们可以大概的将SpringBoot启动的流程做一个梳理

```
创建启动计时监听器 -》 
创建Spring应用程序启动监听器  -》 
创建参数配置初始化应用程序环境   -》  
创建Application应用上下文  -》 
准备Application应用上下文  -》
刷新Application应用上下文，初始化容器，以及启动Web服务器  -》
Application应用上下文刷新后置处理  -》
停止启动计时监听器，打印启动日志  -》
发布Spring应用程序启动监听器启动事件  -》
发布Spring应用程序启动监听器启动通知事件  -》
发布Spring应用程序启动监听器完成启动事件
```

# SpringBoot启动处理

## 计时监听器

### 创建启动计时监听器

我们在SpringBoot启动的时候看到如下初始化计时监听器的

```
    // 启动一个停止监听，用于记录SpringApplicationContext的上下文启动时间（非线程安全）
		StopWatch stopWatch = new StopWatch();
    // 开始计时
		stopWatch.start();
```





## Spring应用程序启动监听器



## 参数配置应用程序环境



## Application应用上下文



## Banner图标





# SpringBoot如何启动Web服务器

首先我们进入SpringApplication的run方法中，根据方法找到实际的启动方法

```
public ConfigurableApplicationContext run(String... args)
```

 我们可以看到他在这里创建了一个应用上下文

![](/Users/bigkang/Library/Application Support/typora-user-images/image-20200528103235856.png)

像我们引入的boot-start-web都是采用的SERVLET容器，而Spring5的新特性则是采用的REACTIVE

![](/Users/bigkang/Library/Application Support/typora-user-images/image-20200528103622602.png)

那么我们来看一下这个DEFAULT_SERVLET_WEB_CONTEXT_CLASS

是一个静态常量，并且通过final进行修饰

```java
	/**
	 * The class name of application context that will be used by default for web
	 * environments.
	 */
	public static final String DEFAULT_SERVLET_WEB_CONTEXT_CLASS = "org.springframework.boot."
			+ "web.servlet.context.AnnotationConfigServletWebServerApplicationContext";
```

我们可以看到这个上下文的类是AnnotationConfigServletWebServerApplicationContext

我们进入这个AnnotationConfigServletWebServerApplicationContext

这个类中有一个方法叫onRefresh，在Springboot创建后会有一个刷新的方法进行调用

我们可以看到这个createWebServer方法，就是创建我们的Web服务器的方法了

```java
	@Override
	protected void onRefresh() {
		super.onRefresh();
		try {
			createWebServer();
		}
		catch (Throwable ex) {
			throw new ApplicationContextException("Unable to start web server", ex);
		}
	}
```

而在SpringBoot中默认引入了tomcat的starter

![](http://yanxuan.nosdn.127.net/6da80daf67d483fd9e2947981fe1d8de.png)

# SpringBoot如何实现AOP

## 自动装配

​			SpringBoot的自动装配实现中有一个EnableAutoConfiguration，他是定义自动装配的注解，同时在这个Jar包的META-INF下有一个spring.factories文件里面配置了许多自动装配的类，如下。



此处只列举一部分，如需了解自动装配流程请翻阅SpringBoot自动装配

```properties
# Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.AutoConfigurationReportLoggingInitializer

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer

# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\   #这里就是自动装配的AOP的配置类了
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration,\
```

这个类如下

```java
// Configuration表示他是一个配置类
@Configuration
// 如果有包含这三个注解的时候才会生效，也就是引入了这几个类依赖的时候
@ConditionalOnClass({ EnableAspectJAutoProxy.class, Aspect.class, Advice.class })
// 并且spring.aop.auto这个属性为true的时候进行装配，默认值为false(但是在spring-configuration-metadata.json中把它设置成了true)，也就是只要我们引入AOP依赖则一定会注入
@ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
public class AopAutoConfiguration {

  // 开启配置，同时类上加上EnableAspectJAutoProxy注解proxyTargetClass=false
	@Configuration
	@EnableAspectJAutoProxy(proxyTargetClass = false)
  // 如果spring.aop.proxy-target-class是false则启用这个类的配置，默认设置为false(在spring-configuration-metadata.json中配置为了false)（matchIfMissing这个属性太恶心了，反向操作打死他）
	@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false", matchIfMissing = true)
	public static class JdkDynamicAutoProxyConfiguration {
	}

	@Configuration
  // 开启配置，同时类上加上EnableAspectJAutoProxy注解proxyTargetClass=true
	@EnableAspectJAutoProxy(proxyTargetClass = true)
  // 如果为true则开启，否则不启用，也没有默认值
	@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true", matchIfMissing = false)
	public static class CglibAutoProxyConfiguration {
	}

}
```

总结：

```
		Spring-Boot-Aop的Starter中引入了依赖，所以在EnableAutoConfiguration中会找到 EnableAspectJAutoProxy.class, Aspect.class, Advice.class这几个类，并且spring.aop.auto默认值为true，所以引入了starter之后这个配置类就会启动。然后我们走到下面，spring.aop.proxy-target-class这个类，
```

```
eval "local result = redis.call('set',KEYS[1],'bigkang','EX',20,'NX')
if  type(result) == 'nil'
then
    return false
else
     return true
end" 1 name

eval "if redis.call('get',KEYS[1]) == ARGV[1] then
        return redis.call('del',KEYS[1]) 
else
        return false
end" 1 name "bigkang"

```

# SpringBoot创建Bean流程



```yaml
SpringApplication.run方法
	# run方法中刷新上下文	
	SpringApplication.this.refreshContext(context);
# 调用Springapplicaiont刷新上下文
SpringApplication.refreshContext(ConfigurableApplicationContext context)
	# refresh方法
	SpringApplication.refresh(ApplicationContext applicationContext)
			# 转换抽象应用上下文调用刷新方法
			((AbstractApplicationContext)applicationContext).refresh();
# 执行刷新
AbstractApplicationContext.refresh()
	# 调用刷新Bean工厂（初始化Bean对象）
	this.finishBeanFactoryInitialization(beanFactory);
	# 调用实例化单例，ConfigurableListableBeanFactory工厂
	ConfigurableListableBeanFactory.beanFactory.preInstantiateSingletons();
# 实现为DefaultListableBeanFactory，调用实例化单例
DefaultListableBeanFactory.preInstantiateSingletons()
```

​		开始实例化Bean

```java
		// 触发所有非懒加载单例bean的初始化，默认Bean都是非懒加载的所以基本都会走这个
		for (String beanName : beanNames) {
			RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
			if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
				if (isFactoryBean(beanName)) {
					Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
					if (bean instanceof FactoryBean) {
						FactoryBean<?> factory = (FactoryBean<?>) bean;
						boolean isEagerInit;
						if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
							isEagerInit = AccessController.doPrivileged(
									(PrivilegedAction<Boolean>) ((SmartFactoryBean<?>) factory)::isEagerInit,
									getAccessControlContext());
						}
						else {
							isEagerInit = (factory instanceof SmartFactoryBean &&
									((SmartFactoryBean<?>) factory).isEagerInit());
						}
						if (isEagerInit) {
							getBean(beanName);
						}
					}
				}
				else {
					getBean(beanName);
				}
			}
		}
```

​		调用getBean(beanName);

```java
// 调用getBean（核心）
getBean(beanName);
	AbstractBeanFactory.doGetBean
```

​		然后判断是否单例然后初始化，进行二级缓存以及三级缓存的构建

```java
				// 创建单例Bean
				if (mbd.isSingleton()) {
          // 获取单例（核心，循环依赖），getSingleton构建二级缓存
					sharedInstance = getSingleton(beanName, () -> {
						try {
              // 创建Bean对象，创建Bean并且实例以及createBean构建三级缓存（核心）
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							// 从单例缓存中显式删除实例：它可能已经放在那里
							// 急于通过创建过程，以允许循环引用解析。
							// 还删除所有收到对bean的临时引用的bean。
							destroySingleton(beanName);
							throw ex;
						}
					});
					bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
				}
```

​		createBean调用doCreateBean

```java
		try {
      // 进行创建Bean对象（核心）
			Object beanInstance = doCreateBean(beanName, mbdToUse, args);
			if (logger.isTraceEnabled()) {
				logger.trace("Finished creating instance of bean '" + beanName + "'");
			}
			return beanInstance;
		}
		catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
			// 先前检测到的具有正确的bean创建上下文的异常，
			// 或非法的单例状态，最多可以传达给DefaultSingletonBeanRegistry。
			throw ex;
		}
```

​		进入doCreateBean方法，在这里会创建我们的三级缓存singletonFactories，也就是工厂缓存

```java
		// 实例化Bean
		BeanWrapper instanceWrapper = null;
		// 如果是单例
		if (mbd.isSingleton()) {
      // 删除工厂Bean实例，如果有则返回
			instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		// 如果没有实例则进行实例的创建
		if (instanceWrapper == null) {
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
		Object bean = instanceWrapper.getWrappedInstance();
		Class<?> beanType = instanceWrapper.getWrappedClass();
		if (beanType != NullBean.class) {
			mbd.resolvedTargetType = beanType;
		}

		// 允许后处理器修改合并的bean定义。
		synchronized (mbd.postProcessingLock) {
			if (!mbd.postProcessed) {
				try {
					applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
				}
				catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,
							"Post-processing of merged bean definition failed", ex);
				}
				mbd.postProcessed = true;
			}
		}

		// 急于缓存单例，以便能够解析循环引用（核心重点），如果是单例并且允许循环依赖（默认true），并且属于正在创建中的Bean
		boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
				isSingletonCurrentlyInCreation(beanName));
		// 如果符合条件进入方法
		if (earlySingletonExposure) {
			if (logger.isTraceEnabled()) {
				logger.trace("Eagerly caching bean '" + beanName +
						"' to allow for resolving potential circular references");
			}
      // 添加单例工厂，也就是我们添加的singletonFactories，也就是工厂缓存（核心）
			addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
		}
		// 初始化Bean实例
		Object exposedObject = bean;
		try {
      // 填充属性给Bean
			populateBean(beanName, mbd, instanceWrapper);
      // 将Bean进行实例话，并且调用PostProcessors后置处理器
			exposedObject = initializeBean(beanName, exposedObject, mbd);
		}
		catch (Throwable ex) {
			if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
				throw (BeanCreationException) ex;
			}
			else {
				throw new BeanCreationException(
						mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
			}
		}
```

​		addSingletonFactory添加单例工厂

```java
	/**
	 * 添加给定的单例工厂以构建指定的单例
	 * 如果有必要.
	 * 渴望注册单例，例如能够解析循环引用.
	 * @param beanName 这个Bean的名字
	 * @param singletonFactory 单例对象的工厂
	 */
	protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(singletonFactory, "Singleton factory must not be null");
		synchronized (this.singletonObjects) {
      // 如果singletonObjects一级缓存不包含
			if (!this.singletonObjects.containsKey(beanName)) {
        // 添加到三级缓存工厂中
				this.singletonFactories.put(beanName, singletonFactory);
        // 从二级缓存中删除
				this.earlySingletonObjects.remove(beanName);
        // 添加到以及注册的单例Set中
				this.registeredSingletons.add(beanName);
			}
		}
	}
```

​		返回创建的对象工厂给getSingleton，回到getSingleton方法

```java
	/**
	 * 根据Bean名称，以及对象单例工厂返回Bean对象
	 * 如果尚未注册，则创建并注册一个新的
	 * @param beanName Bean的名字
	 * @param singletonFactory 延迟创建单例的ObjectFactory
	 * @return 注册的单例对象
	 */
	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(beanName, "Bean name must not be null");
		synchronized (this.singletonObjects) {
      // 从一级缓存中获取，通常刚创建都是没有的
			Object singletonObject = this.singletonObjects.get(beanName);
			if (singletonObject == null) {
				if (this.singletonsCurrentlyInDestruction) {
					throw new BeanCreationNotAllowedException(beanName,
							"Singleton bean creation not allowed while singletons of this factory are in destruction " +
							"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
				}
				beforeSingletonCreation(beanName);
				boolean newSingleton = false;
				boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = new LinkedHashSet<>();
				}
        // 核心方法，从单例工厂中获取doCreateBean创建的对象
				try {
					singletonObject = singletonFactory.getObject();
          // 设置为新创建的单例
					newSingleton = true;
				}
				catch (IllegalStateException ex) {
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						throw ex;
					}
				}
				catch (BeanCreationException ex) {
					if (recordSuppressedExceptions) {
						for (Exception suppressedException : this.suppressedExceptions) {
							ex.addRelatedCause(suppressedException);
						}
					}
					throw ex;
				}
        // 核心重点！！！
				finally {
					if (recordSuppressedExceptions) {
						this.suppressedExceptions = null;
					}
          // 创建后我们需要从正在创建的状态中删掉，因为已经完成了创建状态不是创建中
					afterSingletonCreation(beanName);
				}
        // 新创建的单例
				if (newSingleton) {
          // 核心，新增单例，表示我们已经完成单例的创建以及注册，并且需要添加到一级缓存中
					addSingleton(beanName, singletonObject);
				}
			}
			return singletonObject;
		}
	}
```

​		addSingleton

```java
	/**
	 * 将给定的单例对象添加到该工厂的单例缓存中
	 * 渴望注册单例
	 * @param beanName BeanName名称
	 * @param singletonObject 单例对象
	 */
	protected void addSingleton(String beanName, Object singletonObject) {
    // 锁对象
		synchronized (this.singletonObjects) {
      // 添加到一级缓存
			this.singletonObjects.put(beanName, singletonObject);
      // 删除三级缓存
			this.singletonFactories.remove(beanName);
      // 删除二级缓存
			this.earlySingletonObjects.remove(beanName);
      // 添加到已经注册的单例集中
			this.registeredSingletons.add(beanName);
		}
	}
```

​		

​		那么我们的二级缓存在哪里呢？其实在我们循环依赖的时候，我们会去进行注入，例如我们创建A但是依赖了B，那么创建完A之后进行属性填充，则会跟着创建B。

​		代码如下：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @Summarize Bean对象A，将其标志为Spring组件
 */
@Component
public class BeanA {
    /**
     * b 属性
     */
    private BeanB b;
    /**
     * setter方法注入B
     * @param b
     */
    @Autowired
    public void setB(BeanB b){
        this.b = b;
    }
}

/**
 * @Summarize Bean对象B，将其标志为Spring组件
 */
@Component
public class BeanB {
    /**
     * A 属性初始化
     */
    private BeanA a;
    /**
     * setter方法注入A
     * @param a
     */
    @Autowired
    public void setA(BeanA a){
        this.a = a;
    }
}
```

​		流程

```
创建A  -》 填充属性 -》 创建B
```

​		那么A把B给创建了，轮到了B自己来进行创建的时候现在已经有缓存了所以能直接查询到，这块的代码在我们的最外层的doGetBean中。在我们创建单例前有一个操作叫做getSingleton，获取单例，这是我们循环依赖的解决方案，核心在这里，我们会发现B自己已经被A创建工厂，然后构建二级缓存。

```java

		String beanName = transformedBeanName(name);
		Object bean;

		// 认真检查单例缓存是否有注册的单例。
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null && args == null) {
			if (logger.isTraceEnabled()) {
				if (isSingletonCurrentlyInCreation(beanName)) {
					logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
							"' that is not fully initialized yet - a consequence of a circular reference");
				}
				else {
					logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
				}
			}
			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
		}
		........其他代码
						// 创建单例Bean
				if (mbd.isSingleton()) {
          // 获取单例（核心，循环依赖），getSingleton构建二级缓存
					sharedInstance = getSingleton(beanName, () -> {
						try {
              // 创建Bean对象，创建Bean并且实例以及createBean构建三级缓存（核心）
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							// 从单例缓存中显式删除实例：它可能已经放在那里
							// 急于通过创建过程，以允许循环引用解析。
							// 还删除所有收到对bean的临时引用的bean。
							destroySingleton(beanName);
							throw ex;
						}
					});
					bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
				}
```

​		这里就是解决循环依赖的方法了，我们B被A创建了单例工厂，进行实例，然后轮到B自己进来了以后发现工厂已经被A创建好了，这个时候我们直接获取实例并且添加到二级缓存中。

```java
	/**
	 * 返回以给定名称注册的（原始）单例对象
	 * 检查已经实例化的单例并且还允许早期引用当前创建的单例(解决循环参考).
	 * @参数 beanName the 要寻找的Bean的名字
	 * @参数 allowEarlyReference 是否循环依赖进行获取
	 * @返回 注册的单例对象；如果找不到，则为{@code null}
	 */
  protected Object getSingleton(String beanName, boolean allowEarlyReference) {
     // 从一级缓存中进行获取，也就是完成了创建的
     Object singletonObject = this.singletonObjects.get(beanName);
     // 如果一级缓存没有并且这个Bean正在创建中
     if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
       // 从二级缓存进行获取
        singletonObject = this.earlySingletonObjects.get(beanName);
        // 二级缓存也没有，并且允许循环依赖的话进入方法，并且锁对象
        if (singletonObject == null && allowEarlyReference) {
           synchronized (this.singletonObjects) {
              // 再次从一级缓存获取，重新走一遍流程，加锁保证原子性，防止操作时从其他一级缓存添加
              singletonObject = this.singletonObjects.get(beanName);
              // 重新走流程
              if (singletonObject == null) {
                 singletonObject = this.earlySingletonObjects.get(beanName);
                 if (singletonObject == null) {
                    // 再从三级缓存进行获取，此时B以及被A实例并且创建了线程工厂，表示有循环依赖，如果没有表示第一次创建对象
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    // 如果从三级缓存查询到了对象工厂，表示出现了循环依赖的问题
                    if (singletonFactory != null) {
                       // 从工厂获取实例
                       singletonObject = singletonFactory.getObject();
                       // 添加到二级缓存
                       this.earlySingletonObjects.put(beanName, singletonObject);
                       // 删除掉三级缓存
                       this.singletonFactories.remove(beanName);
                    }
                 }
              }
           }
        }
     }
     return singletonObject;
  }
```

​		如果是第一次A创建则会返回空，进行CreateBean操作，如果A创建并且填充B，B这个类也会进行单例获取，这个时候就可以从三级缓存中获取到，并且创建二级缓存了。

​		如果是没有出现循环依赖，则不会创建二级缓存！！！