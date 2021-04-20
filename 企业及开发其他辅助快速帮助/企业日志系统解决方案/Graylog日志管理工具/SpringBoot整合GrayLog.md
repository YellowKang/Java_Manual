# GrayLog新建输入

​		找到System，选中Inputs，选择UDP接收，然后Launch new input![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1573011860749.png)

然后，选中全局，输入title，随便输入，端口就用12201

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1573011925518.png)

# 引入依赖

官网地址：https://github.com/osiegmar/logback-gelf

```
<dependency>
    <groupId>de.siegmar</groupId>
    <artifactId>logback-gelf</artifactId>
    <version>2.1.2</version>
</dependency>
```

官网地址有TCP以及UDP推送还有异步TCP推送

# 配置文件编写

创建logback-spring.xml配置文件放入resources，此处演示采用UDP推送

请修改localhost为GrayLog的ip地址

```
	<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <appender name="GELF" class="de.siegmar.logbackgelf.GelfUdpAppender">
        <graylogHost>localhost</graylogHost>
        <graylogPort>12201</graylogPort>
        <maxChunkSize>508</maxChunkSize>
        <useCompression>true</useCompression>
        <encoder class="de.siegmar.logbackgelf.GelfEncoder">
            <originHost>localhost</originHost>
            <includeRawMessage>false</includeRawMessage>
            <includeMarker>true</includeMarker>
            <includeMdcData>true</includeMdcData>
            <includeCallerData>false</includeCallerData>
            <includeRootCauseData>false</includeRootCauseData>
            <includeLevelName>false</includeLevelName>
            <shortPatternLayout class="ch.qos.logback.classic.PatternLayout">
                <pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>
            </shortPatternLayout>
            <fullPatternLayout class="ch.qos.logback.classic.PatternLayout">
                <pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>
            </fullPatternLayout>
            <numbersAsString>false</numbersAsString>
            <staticField>app_name:backend</staticField>
            <staticField>os_arch:${os.arch}</staticField>
            <staticField>os_name:${os.name}</staticField>
            <staticField>os_version:${os.version}</staticField>
        </encoder>
    </appender>

    <root level="info">
        <appender-ref ref="GELF" />
    </root>

</configuration>

```

然后启动项目就能在控制台中打印