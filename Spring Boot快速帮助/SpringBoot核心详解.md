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