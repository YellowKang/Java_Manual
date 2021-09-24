# 快速使用

​      由于各种框架和配置集成插件等等默认使用的日志都不一样，但Spring-Boot默认采用的是slf4j加上logback来进行录，所以他将其他的日志文件都进行了转换，全部都转换成slf4j 

```
    //SLF4J日志记录器
    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void contextLoads() {
        //trace < debug < info < warn < error
        //由低到高，可以输出的日志的级别，如果是error级别则只会输出error
        //spring-boot默认给我们设置的级别是info，所以不会打印debug和trace

        logger.trace("这是trcae日志");
        logger.debug("这是DEBUG日志");
        logger.info("这是Info日志");
        logger.warn("这是warn日志");
        logger.error("这是error日志");
    }
```

