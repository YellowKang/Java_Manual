# 引入依赖

```xml
        <!--SpringDataJpa-->
				<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
				<!--Lombok简化开发-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
				<!--SpringBootWeb依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!--Spring整合Quartz-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>

        <!--Spring-JDBC整合Hikari-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <!--mysql连接依赖-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
```

# 编写配置

新建application.yml,这里的Properties配置和quartz一样

```properties
spring:
  quartz:
    #相关属性配置
    properties:
      org:
        quartz:
          scheduler:
            instanceName: clusteredScheduler
            instanceId: AUTO
          jobStore:
            class: org.quartz.impl.jdbcjobstore.JobStoreTX
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            tablePrefix: QRTZ_
            isClustered: true
            clusterCheckinInterval: 10000
            useProperties: false
          threadPool:
            class: org.quartz.simpl.SimpleThreadPool
            threadCount: 10
            threadPriority: 5
            threadsInheritContextClassLoaderOfInitializingThread: true
    #数据库方式
    job-store-type: jdbc
  datasource:
    url: jdbc:mysql://127.0.0.1:3301/quartz?useUnicode=true&characterEncoding=utf-8&useSSL=false
    password: 1111
    username: 1111
    driver-class-name: com.mysql.jdbc.Driver
  jpa:
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect #使用innoDb引擎
    hibernate:
      ddl-auto: update #每次启动如果发现有实体类更新数据库
    show-sql: true #显示sql
    database: mysql #数据库类型
```

properties版本

```properties
spring.quartz.properties.org.quartz.scheduler.instanceName=clusteredScheduler
spring.quartz.properties.org.quartz.scheduler.instanceId=AUTO
spring.quartz.properties.org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
spring.quartz.properties.org.quartz.jobStore.isClustered=true
spring.quartz.properties.org.quartz.jobStore.clusterCheckinInterval=10000
spring.quartz.properties.org.quartz.jobStore.useProperties=false
spring.quartz.properties.org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
spring.quartz.properties.org.quartz.threadPool.threadCount=10
spring.quartz.properties.org.quartz.threadPool.threadPriority=5
spring.quartz.properties.org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread=true
spring.quartz.job-store-type=jdbc
spring.datasource.url=jdbc:mysql://127.0.0.1:3301/quartz?useUnicode=true&characterEncoding=utf-8&useSSL=false
spring.datasource.password=1111
spring.datasource.username=1111
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database=mysql
```

# 初始化数据库

​		首先我们去官网下载相应的sql，[点击进入](http://www.quartz-scheduler.org/downloads/)，根据相应的版本下载相应的包，并且找到相应的数据库初始化sql，解压后打开docs下面的dbTables即可，如何不想自己下载，下面这个是quartz2.2.3的Mysql版本的数据库初始化语句。

```sql
#
# Quartz seems to work best with the driver mm.mysql-2.0.7-bin.jar
#
# PLEASE consider using mysql with innodb tables to avoid locking issues
#
# In your Quartz properties file, you'll need to set 
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
#

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;


CREATE TABLE QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);


commit;
```

下面是各个表对应的表含义。

|           表名           |                             含义                             |
| :----------------------: | :----------------------------------------------------------: |
|    QRTZ_BLOB_TRIGGERS    | 自定义的triggers使用blog类型进行存储，非自定义的triggers不会存放在此表中，Quartz提供的triggers包括：CronTrigger，CalendarIntervalTrigger，DailyTimeIntervalTrigger以及SimpleTrigger，这几个trigger信息会保存在后面的几张表中。 |
|      QRTZ_CALENDARS      | Quartz为我们提供了日历的功能，可以自己定义一个时间段，可以控制触发器在这个时间段内触发或者不触发；现在提供6种类型：AnnualCalendar，CronCalendar，DailyCalendar，HolidayCalendar，MonthlyCalendar，WeeklyCalendar； |
|    QRTZ_CRON_TRIGGERS    | 存储CronTrigger，这也是我们使用最多的触发器，在配置文件中做如下配置，即可在qrtz_cron_triggers生成记录。 |
|   QRTZ_FIRED_TRIGGERS    | 存储已经触发的trigger相关信息，trigger随着时间的推移状态发生变化，直到最后trigger执行完成，从表中被删除。 |
|     QRTZ_JOB_DETAILS     | 存储jobDetails信息，相关信息在定义的时候指定，JOB_DATA存放的就是定义task时指定的jobDataMap属性，所以此属性需要实现Serializable接口，方便持久化到数据库； |
|        QRTZ_LOCKS        | Quartz提供的锁表，为多个节点调度提供分布式锁，实现分布式调度，默认有2个锁。 |
| QRTZ_PAUSED_TRIGGER_GRPS |                     存放暂停掉的触发器。                     |
|   QRTZ_SCHEDULER_STATE   | 存储所有节点的scheduler，会定期检查scheduler是否失效，启动多个scheduler，查询数据库记录了最后最新的检查时间，在quartz.properties中设置了CHECKIN_INTERVAL为1000，也就是每秒检查一次； |
|   QRTZ_SIMPLE_TRIGGERS   | 存储SimpleTrigger，指定了开始延迟时间，重复间隔时间已经重复的次数限制。 |
|  QRTZ_SIMPROP_TRIGGERS   | 存储CalendarIntervalTrigger和DailyTimeIntervalTrigger两种类型的触发器。 |
|      QRTZ_TRIGGERS       | 存储定义的trigger和qrtz_fired_triggers存放的不一样，不管trigger触发了多少次都只有一条记录。 |

# Quartz概述

​			Quartz 是 OpenSymphony 开源组织在 Job Scheduling 领域又一个开源项目，Quartz 是一个任务调度框架，















# Quartz配置详情

​			注意，我们采用SpringBoot整合Quartz，所以没有编写quartz.proerties，全部采用SpringBoot的配置文件。所以前缀需要加上，示例如下：org.quartz.scheduler.instanceName

```
spring:
  quartz:
    #相关属性配置
    properties:
    	org:
    	 quartz:
    	   scheduler:
    	     instanceName:
    		
```

​		此处并没有列出所有的配置选项如果需要更加详细的配置项请查看官方文档配置。http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/

## 主要配置

|                           配置名称                           |                             概述                             |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
|              org.quartz.scheduler.instanceName               | 可以是任何字符串，并且值对调度程序本身没有意义。如果使用群集功能，则必须在群集中的每个实例在逻辑上相同的Scheduler中使用相同的名称。 |
|               org.quartz.scheduler.instanceId                | 可以是任何字符串，但是对于所有工作的调度程序来说必须是唯一的，就像它们在集群中是相同的“逻辑”调度程序一样。如果希望为您生成ID，则可以将值“ AUTO”用作instanceId。如果希望该值来自系统属性“ org.quartz.scheduler.instanceId”，则为“ SYS_PROP”。 |
|        org.quartz.scheduler.instanceIdGenerator.class        | 仅当*org.quartz.scheduler.instanceId*设置为“ AUTO”时使用。默认为“ org.quartz.simpl.SimpleInstanceIdGenerator”，它根据主机名和时间戳生成实例ID。其他IntanceIdGenerator实现包括SystemPropertyInstanceIdGenerator（从系统属性“ org.quartz.scheduler.instanceId”获取实例ID）和HostnameInstanceIdGenerator（使用本地主机名（InetAddress.getLocalHost（）。getHostName（）））。您也可以实现InstanceIdGenerator接口自己。 |
|               org.quartz.scheduler.threadName                | 可以是Java线程的有效名称的任何String。如果未指定此属性，则线程将接收调度程序的名称（“ org.quartz.scheduler.instanceName”）以及附加的字符串“ _QuartzSchedulerThread”。 |
|        org.quartz.scheduler.makeSchedulerThreadDaemon        | 一个布尔值（“ true”或“ false”），它指定调度程序的主线程是否应该是守护程序线程。另请参见*org.quartz.scheduler.makeSchedulerThreadDaemon*属性，以调整[SimpleThreadPool（](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/ConfigThreadPool.html)如果这是您正在使用的线程池实现）（很可能是这种情况）。 |
| org.quartz.scheduler.threadsInheritContextClassLoaderOfInitializer | 一个布尔值（“ true”或“ false”），用于指定Quartz产生的线程是否将继承初始化线程（用于初始化Quartz实例的线程）的上下文ClassLoader。这将影响Quartz主调度线程，JDBCJobStore的失火处理线程（如果使用JDBCJobStore），集群恢复线程（如果使用集群）和SimpleThreadPool中的线程（如果使用SimpleThreadPool）。将此值设置为“ true”可能有助于类加载，JNDI查找以及与在应用程序服务器中使用Quartz相关的其他问题。 |
|              org.quartz.scheduler.idleWaitTime               | 是当调度程序处于空闲状态时，调度程序在重新查询可用触发器之前等待的时间（以毫秒为单位）。通常，除非您正在使用XA事务，并且遇到延迟触发应立即触发的触发器，否则您不必“调整”此参数。不建议使用小于5000 ms的值，因为它将导致过多的数据库查询。小于1000的值不合法。 |
|         org.quartz.scheduler.dbFailureRetryInterval          | 是调度程序在检测到JobStore内部（例如，到数据库）的连接丢失时在重试之间等待的时间（以毫秒为单位）。使用RamJobStore时，此参数显然没有太大意义。 |
|          org.quartz.scheduler.classLoadHelper.class          | 默认为最可靠的方法，即使用“ org.quartz.simpl.CascadingClassLoadHelper”类-该类依次使用所有其他ClassLoadHelper类，直到一个可用为止。尽管应用程序服务器中似乎发生了奇怪的事情，但您可能不应该发现需要为此属性指定任何其他类。当前所有可能的ClassLoadHelper实现都可以在*org.quartz.simpl*包中找到。 |
|            org.quartz.scheduler.jobFactory.class             | 要使用的JobFactory的类名。JobFatcory负责产生JobClasses的实例。默认值为“ org.quartz.simpl.PropertySettingJobFactory”，它每次执行将要执行时，仅在类上调用newInstance（）来生成一个新实例。PropertySettingJobFactory还使用SchedulerContext和Job和Trigger JobDataMaps的内容来反射性地设置作业的bean属性。 |
|               org.quartz.context.key.SOME_KEY                | 表示将以字符串形式放置在“调度程序上下文”中的名称/值对。（请参阅Scheduler.getContext（））。因此，例如，设置“ org.quartz.context.key.MyKey = MyValue”将执行scheduler.getContext（）。put（“ MyKey”，“ MyValue”）的等效操作。 |
|           org.quartz.scheduler.userTransactionURL            | 应该设置为Quartz可以在其中找到Application Server的UserTransaction管理器的JNDI URL。默认值（如果未指定）是“ java：comp / UserTransaction”-几乎适用于所有Application Server。Websphere用户可能需要将此属性设置为“ jta / usertransaction”。仅当Quartz配置为使用JobStoreCMT且*org.quartz.scheduler.wrapJobExecutionInUserTransaction*设置为true时，才使用此选项。 |
|    org.quartz.scheduler.wrapJobExecutionInUserTransaction    | 如果您希望Quartz在调用对您的作业执行execute之前启动UserTransaction，应将其设置为“ true”。在作业的execute方法完成之后，以及JobDataMap更新后（如果它是StatefulJob），Tx将提交。默认值为“ false”。您可能还对在作业类上使用*@ExecuteInJTATransaction*批注感兴趣，该批注可让您控制单个作业是否Quartz应该启动JTA事务-而此属性使它在所有作业中都发生。 |
|             org.quartz.scheduler.skipUpdateCheck             | 是否跳过运行快速Web请求以确定是否有可供下载的Quartz更新版本。如果检查运行，并且找到更新，它将在Quartz日志中报告为可用。您还可以使用系统属性“ org.terracotta.quartz.skipUpdateCheck = true”（可以在系统环境中设置或在Java命令行中将其设置为-D）来禁用更新检查。建议您禁用生产部署的更新检查。 |
|     org.quartz.scheduler.batchTriggerAcquisitionMaxCount     | 允许调度程序节点一次获取（触发）的最大触发器数。默认值是1。数字越大，触发效率越高（在一次触发很多触发器的情况下），但代价是群集节点之间的负载可能不平衡。如果此属性的值设置为> 1，并且使用JDBC JobStore，则必须将属性“ org.quartz.jobStore.acquireTriggersWithinLock”设置为“ true”，以避免数据损坏。 |
| org.quartz.scheduler.batchTriggerAcquisitionFireAheadTimeWindow | 允许触发器被捕获并在其计划的触发时间之前触发的时间（以毫秒为单位）。<br/>默认值为0。数字越大，触发触发器的批量采集越有可能一次选择并触发1个以上的触发器-代价是无法精确遵守触发时间表（触发器可能会提前触发此数量） ）。在调度程序具有大量需要同时或接近同时触发的触发器的情况下，这可能是有用的（出于性能考虑）。 |

## 线程池配置

|                           配置名称                           |                             概述                             |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
|                 org.quartz.threadPool.class                  | 是您要使用的ThreadPool实现的名称。Quartz附带的线程池是“ org.quartz.simpl.SimpleThreadPool”，并且应该满足几乎每个用户的需求。它的行为非常简单，并且经过了很好的测试。它提供了一个固定大小的线程池，这些线程池“活跃”了调度程序的生命周期。 |
|              org.quartz.threadPool.threadCount               | 可以是任何正整数，尽管您应该意识到只有1到100之间的数字是非常实用的。这是可用于并发执行作业的线程数。如果您每天只能执行少量工作，那么一个线程就足够了！如果您有成千上万的工作，每分钟触发许多工作，那么您可能希望线程数更像50或100（这在很大程度上取决于您工作执行的工作性质以及系统资源！）。 |
|             org.quartz.threadPool.threadPriority             | 可以是*Thread.MIN_PRIORITY*（为1）和*Thread.MAX_PRIORITY*（为10）之间的任何整数。默认值为*Thread.NORM_PRIORITY*（5）。 |
|           org.quartz.threadPool.makeThreadsDaemons           | 可以设置为“ true”以将池中的线程创建为守护程序线程。默认值为“ false”。另请参见*[org.quartz.scheduler.makeSchedulerThreadDaemon](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/ConfigMain.html)*属性 |
| org.quartz.threadPool.threadsInheritGroupOfInitializingThread |            可以为“ true”或“ false”，默认为true。             |
| org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread |            可以为“ true”或“ false”，默认为false。            |
|            org.quartz.threadPool.threadNamePrefix            |           工作池中线程名称的前缀-将附加一个数字。            |

自定义线程池，如果您使用自己的线程池实现，则可以简单地通过如下命名属性来反射地设置属性：

**在自定义线程池上设置属性**

```
org.quartz.threadPool.class = com.mycompany.goo.FooThreadPool
org.quartz.threadPool.somePropOfFooThreadPool = someValue
```

## JDBC数据库配置

|                配置名称                 |                             概述                             |
| :-------------------------------------: | :----------------------------------------------------------: |
|        org.quartz.jobStore.class        | JobStoreTX通过在执行每个操作（例如添加作业）之后在数据库连接上调用commit（）（或rollback（））本身来管理所有事务。如果您在独立应用程序中使用Quartz，或者在Servlet容器中使用Quartz（如果应用程序未使用JTA事务），则JDBCJobStore是合适的。org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX |
| org.quartz.jobStore.driverDelegateClass |                                                              |
|     org.quartz.jobStore.dataSource      |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |
|                                         |                                                              |

# 编写Job

```java
/**
 * @Author BigKang
 * @Date 2020/3/4 4:46 PM
 * @Summarize 自定义job任务
 * @PersistJobDataAfterExecution 获取最新的JobData
 * @DisallowConcurrentExecution 同步运行，不执行并发job
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CustomJob extends QuartzJobBean {

    @Autowired
    private SpiderJobDao spiderJobDao;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        TriggerKey key = jobExecutionContext.getTrigger().getKey();

        log.info("开始执行任务：{}-{}",key.getGroup(),key.getName());
        try {
            for (SpiderJob spiderJob : spiderJobDao.findAll()) {
                log.info("job：",spiderJob);
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行任务完成：{}-{}",key.getGroup(),key.getName());
    }

}
```

# 编写工具类

```java

/**
 * @Author BigKang
 * @Date 2020/3/5 2:23 PM
 * @Summarize Quartz工具类
 */
@Component
@Slf4j
public class QuartzUtil {

    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    /**
     * 默认优先级
     */
    private static final Integer DEFAULT_PRIORITY = 5;

    /**
     * 注入调度器
     */
    @Autowired
    private Scheduler scheduler;

    /**
     * 检查任务是否存在
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public boolean checkJobExists(String jobName, String jobGroup) {
        try {
            return scheduler.checkExists(TriggerKey.triggerKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            return false;
        }
    }

    /**
     * 停止定时任务
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public boolean stopJob(String jobName, String jobGroup) {
        log.info("开始暂停任务：{}-{}", jobGroup, jobName);
        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroup));
            log.info("成功暂停任务：{}-{}", jobGroup, jobName);
        } catch (SchedulerException e) {
            log.info("暂停任务失败：{}-{}", jobGroup, jobName);
            return false;
        }
        return true;
    }

    /**
     * 运行暂停的定时任务
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public boolean runJob(String jobName, String jobGroup) {
        log.info("开始运行任务：{}-{}", jobGroup, jobName);
        try {
            scheduler.resumeTrigger(TriggerKey.triggerKey(jobName, jobGroup));
            log.info("成功运行任务：{}-{}", jobGroup, jobName);
        } catch (SchedulerException e) {
            log.info("运行任务失败：{}-{}", jobGroup, jobName);
            return false;
        }

        return true;
    }

    /**
     * 添加Cron表达式Job任务
     * @param classzz class对象
     * @param cron cron表达式
     * @param jobName 任务名称
     * @param jobGroup 任务分组
     * @param description 描述
     * @param startDate 开始时间
     * @param endDate  结束时间
     * @param dataMap dataMap数据
     * @param priority 优先级
     * @return
     */
    public boolean addCronJob(Class classzz, String cron, String jobName, String jobGroup, String description, Date startDate, Date endDate, Map<String, Object> dataMap, int priority) {
        log.info("添加任务：{}-{}", jobGroup, jobName);
        try {
            // 创建Cron表达式触发器
            CronTrigger cronTrigger = genCronTrigger(classzz, cron, jobName, jobGroup, description, startDate, endDate, dataMap, priority);
            // 创建任务详情
            JobDetail jobDetail = genJobDetail(classzz, jobName, jobGroup, jobName);
            scheduler.scheduleJob(jobDetail, cronTrigger);
            log.info("成功添加任务：{}-{}", jobGroup, jobName);
        } catch (SchedulerException e) {
            log.info("添加任务失败：{}-{}", jobGroup, jobName);
            return false;
        }
        return true;
    }


    public boolean addCronJob(Class classzz, String cron, String jobName, String jobGroup, Date startDate, Date endDate, Map<String, Object> dataMap, int priority){
        return addCronJob(classzz,cron,jobName,jobGroup,"",startDate,endDate,dataMap,priority);
    }

    public boolean addCronJob(Class classzz, String cron, String jobName, String jobGroup, Date startDate, Date endDate, Map<String, Object> dataMap){
        return addCronJob(classzz,cron,jobName,jobGroup,startDate,endDate,dataMap,DEFAULT_PRIORITY);
    }

    public boolean addCronJob(Class classzz, String cron, String jobName, String jobGroup, Date endDate, Map<String, Object> dataMap){
        return addCronJob(classzz,cron,jobName,jobGroup,null,endDate,dataMap);
    }

    public boolean addCronJob(Class classzz, String cron, String jobName, String jobGroup, Map<String, Object> dataMap){
        return addCronJob(classzz,cron,jobName,jobGroup,null,dataMap);
    }

    public boolean addCronJob(String className, String cron, String jobName, String jobGroup, Map<String, Object> dataMap) throws ClassNotFoundException {
        Class<?> classzz = Class.forName(className);
        return addCronJob(classzz,cron,jobName,jobGroup,dataMap);
    }



    /**
     * 生成JobDetail
     *
     * @return
     */
    public JobDetail genJobDetail(Class classzz, String jobName, String jobGroup, String description, boolean storeDurably) {

        // 创建JobDetail任务详情
        // 新建Job，设置Job class
        JobBuilder jobBuilder = JobBuilder.newJob(classzz);

        jobName = getJobName(classzz, jobName);

        jobGroup = getJobGroup(jobGroup);

        if (StringUtils.isEmpty(description)) {
            description = String.format("DEFAULT %s CREATE", jobName);
        }
        JobDetail jobDetail = jobBuilder.withIdentity(jobName, jobGroup).withDescription(description).storeDurably(storeDurably).build();
        return jobDetail;
    }

    /**
     * 重载方法JobDetail
     *
     * @return
     */
    public JobDetail genJobDetail(Class classzz, String jobName, String jobGroup, String description) {
        return this.genJobDetail(classzz, jobName, jobGroup, description, false);
    }


    /**
     * 重载方法JobDetail
     *
     * @return
     */
    public JobDetail genJobDetail(Class classzz, String jobName, String jobGroup) {
        return this.genJobDetail(classzz, jobName, jobGroup, null, false);
    }


    /**
     * 重载方法JobDetail
     *
     * @return
     */
    public JobDetail genJobDetail(Class classzz, String jobName) {
        return this.genJobDetail(classzz, jobName, null, null, false);
    }

    /**
     * 重载方法JobDetail
     *
     * @return
     */
    public JobDetail genJobDetail(Class classzz) {
        return this.genJobDetail(classzz, null, null, null, false);
    }


    /**
     * 创建CronTrigger触发器
     *
     * @param classzz     job类
     * @param cron        定时表达式
     * @param jobName     任务名称
     * @param jobGroup    任务分组
     * @param description 任务描述
     * @param startDate   起始时间
     * @param endDate     结束时间
     * @param dataMap     数据集合
     * @param priority    优先级
     * @return
     */
    public CronTrigger genCronTrigger(Class classzz, String cron, String jobName, String jobGroup, String description, Date startDate, Date endDate, Map<String, Object> dataMap, int priority) {
        // 创建Cron调度器Builder
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();

        // 获取jobName以及jobGroup
        jobName = getJobName(classzz, jobName);
        jobGroup = getJobGroup(jobGroup);

        // 设置触发器优先级，触发器Key，以及描述
        triggerBuilder = triggerBuilder
                .withPriority(priority)
                .withIdentity(TriggerKey.triggerKey(jobName, jobGroup))
                .withDescription(description);

        // 判断任务开始结束时间是否为空，并且设置
        if (startDate != null) {
            triggerBuilder = triggerBuilder.startAt(startDate);
        }
        if (endDate != null) {
            triggerBuilder = triggerBuilder.endAt(endDate);
        }
        // 判断Jobdata是否为空
        if (dataMap != null) {
            triggerBuilder = triggerBuilder.usingJobData(new JobDataMap(dataMap));
        }

        return triggerBuilder.withSchedule(scheduleBuilder).build();
    }

    /**
     * 重载方法genCronTrigger
     *
     * @return
     */
    public CronTrigger genCronTrigger(Class classzz, String cron, String jobName, String jobGroup, Date startDate, Date endDate, Map<String, Object> dataMap) {
        return genCronTrigger(classzz, cron, jobName, jobGroup, "Auto Create，No Description。", startDate, endDate, dataMap, DEFAULT_PRIORITY);
    }

    /**
     * 重载方法genCronTrigger
     *
     * @return
     */
    public CronTrigger genCronTrigger(Class classzz, String cron, String jobName, String jobGroup, Map<String, Object> dataMap) {
        return genCronTrigger(classzz, cron, jobName, jobGroup, null, null, dataMap);
    }


    /**
     * 重载方法genCronTrigger
     * @return
     */
    public CronTrigger genCronTrigger(Class classzz, String cron, String jobName, String jobGroup, Date startDate, Date endDate) {
        return genCronTrigger(classzz, cron, jobName, jobGroup, startDate, endDate, null);
    }

    /**
     * 重载方法genCronTrigger
     * @return
     */
    public CronTrigger genCronTrigger(Class classzz, String cron, String jobName, String jobGroup) {
        return genCronTrigger(classzz, cron, jobName, jobGroup, null);
    }

    /**
     * 获取JobName，为空则根据规则创建
     * @param classzz
     * @param jobName
     * @return
     */
    public String getJobName(Class classzz, String jobName) {
        // 非空验证，为空则默认值初始化
        if (StringUtils.isEmpty(jobName)) {
            jobName = classzz.getSimpleName() + "" + System.currentTimeMillis();
        }
        return jobName;
    }

    /**
     * 获取JobGroup，为空则使用默认
     *
     * @param jobGroup
     * @return
     */
    public String getJobGroup(String jobGroup) {

        // 非空验证，为空则默认值初始化
        if (StringUtils.isEmpty(jobGroup)) {
            jobGroup = DEFAULT_GROUP;
        }
        return jobGroup;
    }


    /**
     * 获取所有定时器信息
     * @return
     * @throws SchedulerException
     */
    public List<Map> listJob() throws SchedulerException {
        return listJob(null);
    }

    /**
     * 根据组名获取定时器中的触发器
     * @param group
     * @return
     * @throws SchedulerException
     */
    public List<Map> listJob(String group) throws SchedulerException {
        List<Map> list = new CopyOnWriteArrayList<>();
        GroupMatcher<TriggerKey> matcher;

        // 组名为空获取所有组任务
        if(StringUtils.isEmpty(group)){
            matcher = GroupMatcher.anyGroup();
        }else {
            matcher = GroupMatcher.groupEquals(group);
        }

        // 获取所有的group的job信息
        Set<TriggerKey> jobKeys = scheduler.getTriggerKeys(matcher);
        jobKeys.forEach(v -> {
            try {
                Map<String,Object> map = new HashMap<>();
                Trigger trigger = scheduler.getTrigger(v);
                map.put("jobName",trigger.getKey());
                map.put("startDate",trigger.getStartTime());
                map.put("endDate",trigger.getEndTime());
                map.put("description",trigger.getDescription());
                map.put("data",trigger.getJobDataMap());
                map.put("nextTime",trigger.getNextFireTime());
                map.put("priority",trigger.getPriority());
                map.put("status",scheduler.getTriggerState(v).name());
                map.put("cron",((CronTrigger)trigger).getCronExpression());
                list.add(map);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });

        return list;
    }
}
```

# 编写Controller

```java
@RestController
@RequestMapping("spiderJob")
@Api(tags = "爬虫任务")
public class SpiderJobController {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private QuartzUtil quartzUtil;


    @GetMapping("getJobs")
    @ApiOperation("获取job任务信息")
    public List<Map> getJobs(String group) throws SchedulerException {
        List<Map> list = new CopyOnWriteArrayList<>();
        // 获取指定group下的job信息
        // GroupMatcher.groupEquals(group);
        
        // 获取所有的group的job信息
        GroupMatcher<TriggerKey> matcher = GroupMatcher.anyGroup();
        Set<TriggerKey> jobKeys = scheduler.getTriggerKeys(matcher);

        jobKeys.forEach(v -> {
            try {
                Map<String,Object> map = new HashMap<>();
                Trigger trigger = scheduler.getTrigger(v);
                map.put("jobName",trigger.getKey());
                map.put("startDate",trigger.getStartTime());
                map.put("endDate",trigger.getEndTime());
                map.put("description",trigger.getDescription());
                map.put("data",trigger.getJobDataMap());
                map.put("nextTime",trigger.getNextFireTime());
                map.put("priority",trigger.getPriority());
                map.put("status",scheduler.getTriggerState(v).name());
                map.put("cron",((CronTrigger)trigger).getCronExpression());
                list.add(map);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });


        return list;
    }


    @PostMapping("addJob")
    @ApiOperation("添加job任务")
    public boolean addJob(String jobName,String groupName,String cron){
        return quartzUtil.addJob(jobName,groupName,cron);
    }

    @PostMapping("stopJob")
    @ApiOperation("停止job任务")
    public boolean stopJob(String jobName,String groupName){
        return quartzUtil.stopJob(jobName,groupName);
    }

    @PostMapping("runJob")
    @ApiOperation("运行job任务")
    public boolean runJob(String jobName,String groupName){
        return quartzUtil.runJob(jobName,groupName);
    }


}
```

我们在Job中编写我们需要调度的代码即可

```
None：Trigger已经完成，且不会在执行，或者找不到该触发器，或者Trigger已经被删除
NORMAL:正常状态
PAUSED：暂停状态
COMPLETE：触发器完成，但是任务可能还正在执行中
BLOCKED：线程阻塞状态
ERROR：出现错误
```



# 自定义调度器

   		大多时候我们都知道整合Quartz集成的功能非常强大，但是我们有时不需要持久化到数据库中，并且Quartz本身是非常重的一个框架，我们想要单独使用它的定时器功能。

只引入依赖

## 创建调度器工厂

解决在Job中无法注入Bean的问题

```java
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * @Author BigKang
 * @Date 2020/5/15 2:25 下午
 * @Summarize 自定义定时器工厂
 */
@Component
public class CustomSchedulerFactory extends AdaptableJobFactory {

    /**
     * AutowireCapableBeanFactory接口是BeanFactory的子类
     * 可以连接和填充那些生命周期不被Spring管理的已存在的bean实例
     */
    private AutowireCapableBeanFactory factory;

    /**
     * 构造方法
     * @param factory
     */
    public CustomSchedulerFactory(AutowireCapableBeanFactory factory) {
        this.factory = factory;
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        // 进行注入（Spring管理该Bean）
        factory.autowireBean(job);
        return job;
    }

}
```

## 创建调度器配置

```java

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @Author BigKang
 * @Date 2020/5/15 2:25 下午
 * @Summarize 自定义定时器配置
 */
@Configuration
@Slf4j
public class CustomSchedulerConfig {

    @Autowired
    private CustomSchedulerFactory customSchedulerFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        log.info("初始化定时器工厂......");
        // Spring提供SchedulerFactoryBean为Scheduler提供配置信息,并被Spring容器管理其生命周期
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // 设置自定义Job Factory，用于Spring管理Job bean
        factory.setJobFactory(customSchedulerFactory);
        log.info("初始化定时器成功......");
        return factory;
    }

    /**
     * 定时器Bean
     * @return
     */
    @Bean
    public Scheduler scheduler(){
        return schedulerFactoryBean().getScheduler();
    }

}
```



采用上方工具类调用