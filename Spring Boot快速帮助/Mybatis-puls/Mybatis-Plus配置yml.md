
server:
  tomcat:
    uri-encoding: UTF-8
    max-threads: 1000
    min-spare-threads: 30
  port: 8081
  servlet:
    context-path: /

//spring配置

spring:
  aop:
    proxy-target-class: true

//DATABASE CONFIG 注意这里连得是sql server

  datasource:
    druid:
      driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
      username: tdxuser
      password: ${MSSQL_PASSWORD:tdxgps}
      url: jdbc:sqlserver://${MSSQL_HOST:192.168.2.77:5609};databaseName=TDXDB
      initial-size: 1
      max-active: 20
      min-idle: 1
      max-wait: 60000
      validation-query: select 'x'
      validationQueryTimeout: 5
      test-on-borrow: false
      test-on-return: false
      test-while-idle: true
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      filters: log4j
      filter:
        stat:
          enabled: true
          log-slow-sql: true
          slow-sql-millis: 1000
          merge-sql: true
          #db-type: sqlserver
        slf4j:
          enabled: true
          connection-log-enabled: true
          connection-close-after-log-enabled: true
          connection-commit-after-log-enabled: true
          connection-connect-after-log-enabled: true
          connection-connect-before-log-enabled: true
          connection-log-error-enabled: true
          data-source-log-enabled: true
          result-set-log-enabled: true
          statement-log-enabled: true
        wall:
          enabled: true
          config:
            alter-table-allow: false
            truncate-allow: false
            drop-table-allow: false
            #是否允许非以上基本语句的其他语句，缺省关闭，通过这个选项就能够屏蔽DDL
            none-base-statement-allow: false
            #检查UPDATE语句是否无where条件，这是有风险的，但不是SQL注入类型的风险
            update-where-none-check: true
            #SELECT ... INTO OUTFILE 是否允许，这个是mysql注入攻击的常见手段，缺省是禁止的
            select-into-outfile-allow: false
            #是否允许调用Connection.getMetadata方法，这个方法调用会暴露数据库的表信息
            metadata-allow: true
            #允许多条sql一起执行
            multiStatementAllow: true
          #对被认为是攻击的SQL进行LOG.error输出
          log-violation: true
          #对被认为是攻击的SQL抛出SQLExcepton
          throw-exception: true
          #db-type: mysql
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'
        #你可以配置principalSessionName，使得druid能够知道当前的cookie的用户是谁
        #principal-cookie-name: admin
        #你可以配置principalSessionName，使得druid能够知道当前的session的用户是谁
        #principal-session-name: admin
        #设置profileEnable能够监控单个url调用的sql列表。
        profile-enable: true
        #session统计功能
        session-stat-enable: false
        #最大session数
        session-stat-max-count: 100000
      stat-view-servlet:
        #allow: ${GATEWAY_HOST:172.26.114.241}
        enabled: true
        login-username: ${DRUID_USER:admin}
        login-password: ${DRUID_PWD:admin}
        url-pattern: /druid/*
        #允许清除记录
        reset-enable: false
      aop-patterns: com.tdx.account_service.service.*

# Redis配置

  redis:
    #集群模式
    #cluster:
    #  nodes:
    #    - 39.XXX.XX.69:6661
    #    - 39.XXX.XX.69:6662
    #    - 39.XXX.XX.69:6663
    #    - 39.XXX.XX.69:6664
    #    - 39.XXX.XX.69:6665
    #    - 39.XXX.XX.69:6666
    #单机模式
    host: ${REDIS_HOST:192.168.2.18}
    port: ${REDIS_PORT:7006}
    password: ${REDIS_PASSWORD:root}
     #连接超时时间（毫秒）
    timeout: 10000
    pool:
     max-idle: 20
     min-idle: 5
     max-active: 20
     max-wait: 2

# mybatis

mybatis-plus:

  mapper-locations: classpath*:/mapper/**Mapper.xml

# 实体扫描，多个package用逗号或者分号分隔

  typeAliasesPackage: com.tdx.account_service.entity

  global-config:

```
#主键类型  0:"数据库ID自增", 1:"用户输入ID",2:"全局唯一ID (数字类型唯一ID)", 3:"全局唯一ID UUID";
id-type: 2
```

```
#字段策略 0:"忽略判断",1:"非 NULL 判断"),2:"非空判断"
field-strategy: 2
```

```
#驼峰下划线转换
db-column-underline: true
```

```
#刷新mapper 调试神器
refresh-mapper: true
```

```
#数据库大写下划线转换
#capital-mode: true
```

```
#序列接口实现类配置
#key-generator: com.baomidou.springboot.xxx
```

```
#逻辑删除配置（下面3个配置）
logic-delete-value: 0
logic-not-delete-value: 1
```



```
#自定义SQL注入器
#sql-injector: com.baomidou.mybatisplus.mapper.LogicSqlInjector
```



```
#自定义填充策略接口实现
#meta-object-handler: com.baomidou.springboot.xxx
```

  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false