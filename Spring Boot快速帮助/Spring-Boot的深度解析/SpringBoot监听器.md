我们首先来使用SpringApplicationRunListener

# 创建自动装配配置

我们首先在META-INF下面创建一个spring.factories

如下图所示

![](img\RunApplicationListener-1.png)

然后我们来创建一个类，并且实现SpringApplicationRunListener

我们创建一个MyBootRunListener，代码如下

```
public class MyBootRunListener implements SpringApplicationRunListener {
    //构造方法，必须加入否则启动报错
    public MyBootRunListener(SpringApplication application, String[] args){
    }
    public MyBootRunListener(){
    }
    //启动时执行
    @Override
    public void starting() {
        System.out.println("开始启动--------Listener");
    }
    //准备环境是运行
    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        System.out.println("开始加载环境--------Listener");
    }
    //容器准备时执行
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("容器准备--------Listener");
    }
    //容器加载时执行
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("容器加载--------Listener");
    }
    //启动完成后执行
    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("容器启动完成--------Listener");
    }
    //运行时执行
    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("容器运行--------Listener");
    }
    //启动失败时执行
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("启动失败--------Listener");
    }
}
```

然后我们去spring.factories编写配置，上面表示我们要配置一个SpringApplicationRunListener，然后他的实现类的类路径在哪，然后我们启动项目

```
# Run Listeners
org.springframework.boot.SpringApplicationRunListener=\
com.big.kang.test.listener.MyBootRunListener
```

然后启动如下图所示

![](img\RunApplicationListener-2.png)

![](img\RunApplicationListener-3.png)

这样我们就能在监听器中加入东西了