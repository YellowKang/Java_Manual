# 添加配置文件

SpringBoot默认就是使用的logback，我们直接添加配置文件即可，在SpringBoot的resource目录下添加一个logback-spring.xml

文件内容如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <!--从配置文件读取配置路径，并且添加默认值-->
    <!--日志全局设置-->
    <springProperty scope="context" name="log.charset" source="log.charset" defaultValue="UTF-8"/>


    <springProperty scope="context" name="log.pattern" source="log.pattern" defaultValue="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

    <!--INFO日志设置-->
    <springProperty scope="context" name="info.path" source="log.info.path" defaultValue="logs/info"/>
    <springProperty scope="context" name="info.history" source="log.info.history" defaultValue="10"/>
    <springProperty scope="context" name="info.maxsize" source="log.info.maxsize" defaultValue="1GB"/>
    <springProperty scope="context" name="info.filesize" source="log.info.filesize" defaultValue="10MB"/>
    <springProperty scope="context" name="info.pattern" source="log.info.pattern" defaultValue="%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n"/>



    <!--ERROR日志设置-->
    <springProperty scope="context" name="error.path" source="log.error.path" defaultValue="logs/error"/>
    <springProperty scope="context" name="error.history" source="log.error.history" defaultValue="10"/>
    <springProperty scope="context" name="error.maxsize" source="log.error.maxsize" defaultValue="1GB"/>
    <springProperty scope="context" name="error.filesize" source="log.error.filesize" defaultValue="10MB"/>
    <springProperty scope="context" name="error.pattern" source="log.error.pattern" defaultValue="%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n"/>


    <!-- 彩色日志 -->
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
    <conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
    <!-- 定义彩色日志格式模板 -->


    <!--控制台日志打印格式-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level> 
        </filter>
        <encoder>
            <pattern>${log.pattern}</pattern>
            <charset>${log.charset}</charset>
        </encoder>
    </appender>

    <!--INFO日志打印-->
    <appender name="FILE_INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--如果只是想要 Info 级别的日志，只是过滤 info 还是会输出 Error 日志，因为 Error 的级别高， 所以我们使用下面的策略，可以避免输出 Error 的日志-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!--过滤 Error-->
            <level>ERROR</level>
            <!--匹配到就禁止-->
            <onMatch>DENY</onMatch>
            <!--没有匹配到就允许-->
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <!--日志名称，如果没有File 属性，那么只会使用FileNamePattern的文件路径规则如果同时有<File>和<FileNamePattern>，那么当天日志是<File>，明天会自动把今天的日志改名为今天的日期。即，<File> 的日志都是当天的。-->
        <!--<File>logs/info.spring-boot-demo-logback.log</File>-->
        <!--滚动策略，按照时间滚动 TimeBasedRollingPolicy-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--文件路径,定义了日志的切分方式——把每一天的日志归档到一个文件中,以防止日志填满整个磁盘空间-->
            <FileNamePattern>${info.path}/info_%d{yyyy-MM-dd}.part_%i.log</FileNamePattern>
            <!--只保留最近10天的日志-->
            <maxHistory>${info.history}</maxHistory>
            <!--用来指定日志文件的上限大小，那么到了这个值，就会删除旧的日志-->
            <totalSizeCap>${info.maxsize}</totalSizeCap>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <!-- maxFileSize:这是活动文件的大小，默认值是10MB,本篇设置为1KB，只是为了演示 -->
                <maxFileSize>${info.filesize}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <!--<triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">-->
        <!--<maxFileSize>1KB</maxFileSize>-->
        <!--</triggeringPolicy>-->
        <encoder>
            <pattern>${info.pattern}</pattern>
            <charset>${log.charset}</charset> <!-- 此处设置字符集 -->
        </encoder>
    </appender>
    <!--ERROR日志打印-->
    <appender name="FILE_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--如果只是想要 Error 级别的日志，那么需要过滤一下，默认是 info 级别的，ThresholdFilter-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>Error</level>
        </filter>
        <!--日志名称，如果没有File 属性，那么只会使用FileNamePattern的文件路径规则如果同时有<File>和<FileNamePattern>，那么当天日志是<File>，明天会自动把今天的日志改名为今天的日期。即，<File> 的日志都是当天的。-->
        <!--<File>logs/error.spring-boot-demo-logback.log</File>-->
        <!--滚动策略，按照时间滚动 TimeBasedRollingPolicy-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--文件路径,定义了日志的切分方式——把每一天的日志归档到一个文件中,以防止日志填满整个磁盘空间-->
            <FileNamePattern>${error.path}/error_%d{yyyy-MM-dd}.part_%i.log</FileNamePattern>
            <!--只保留最近90天的日志-->
            <maxHistory>${error.history}</maxHistory>
            <!--用来指定日志文件的上限大小，那么到了这个值，就会删除旧的日志-->
            <totalSizeCap>${error.maxsize}</totalSizeCap>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <!-- maxFileSize:这是活动文件的大小，默认值是10MB,本篇设置为1KB，只是为了演示 -->
                <maxFileSize>${error.filesize}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <encoder>
            <pattern>${error.pattern}</pattern>
            <charset>${log.charset}</charset> <!-- 此处设置字符集 -->
        </encoder>
    </appender>

    <!--Gray Log配置-->
    <!--<appender name="GELF" class="de.siegmar.logbackgelf.GelfUdpAppender">-->
        <!--<graylogHost>114.67.80.169</graylogHost>-->
        <!--<graylogPort>12201</graylogPort>-->
        <!--<maxChunkSize>508</maxChunkSize>-->
        <!--<useCompression>true</useCompression>-->
        <!--<encoder class="de.siegmar.logbackgelf.GelfEncoder">-->
            <!--<originHost>localhost</originHost>-->
            <!--<includeRawMessage>false</includeRawMessage>-->
            <!--<includeMarker>true</includeMarker>-->
            <!--<includeMdcData>true</includeMdcData>-->
            <!--<includeCallerData>false</includeCallerData>-->
            <!--<includeRootCauseData>false</includeRootCauseData>-->
            <!--<includeLevelName>false</includeLevelName>-->
            <!--<shortPatternLayout class="ch.qos.logback.classic.PatternLayout">-->
                <!--<pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>-->
            <!--</shortPatternLayout>-->
            <!--<fullPatternLayout class="ch.qos.logback.classic.PatternLayout">-->
                <!--<pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>-->
            <!--</fullPatternLayout>-->
            <!--<numbersAsString>false</numbersAsString>-->
            <!--<staticField>app_name:backend</staticField>-->
            <!--<staticField>os_arch:${os.arch}</staticField>-->
            <!--<staticField>os_name:${os.name}</staticField>-->
            <!--<staticField>os_version:${os.version}</staticField>-->
        <!--</encoder>-->
    <!--</appender>-->

    <root level="info">
        // 控制台输出
        <appender-ref ref="CONSOLE" />

        // INFO日志输出
        <appender-ref ref="FILE_INFO" />

        // ERROR日志输出
        <appender-ref ref="FILE_ERROR" />

        <!--// GRAY LOG输出-->
        <!--<appender-ref ref="GELF" />-->
    </root>

</configuration>
```

然后启动项目即可

# 动态配置

yaml版本

```properties
#日志配置路径
log:
  info:
    path: logs/info #INFO日志路径
    history: 20 #保留INFO日志天数
    maxsize: 10GB #INFO日志文件最大大小
    filesize: 10MB #活动文件大小
    pattern: '%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n' #INFO日志输出格式
  error:
    path: logs/error #ERROR日志路径
    history: 20 #保留ERROR日志天数
    maxsize: 10GB #ERROR日志文件最大大小
    filesize: 10MB #活动文件大小
    pattern: '%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n' #ERROR日志输出格式
  pattern: '${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}' #控制台打印日志格式
  charset: UTF-8 #编码格式
```

properties版本

```

```

