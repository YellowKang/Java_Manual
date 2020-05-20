# 首先我们引入依赖

## Jpa+HikariCP版本

sharding-jdbc以及jpa ，mysql连接驱动，以及HikariCP连接池

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>3.1.0</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>3.4.1</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
```

## Mybatis+Druid版本

```xml
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.0.6</version>
        </dependency>

        <dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>3.1.0</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

     		<dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.22</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
```



# 然后编写配置文件

## 分库+分表配置

### Jpa版本

建表结构

```
首先我们先去两个库中创建两张表
localhost:13301		db0		第一个分库数据库
localhost:13302		db1		第二个分库数据库
分表
localhost:13301		db0		table0		第一个库，第一张表
localhost:13301		db0		table1		第一个库，第二张表
localhost:13302		db1		table0		第二个库，第一张表
localhost:13302		db1		table1		第二个库，第二张表
```

#### 建表语句

在两个数据库中新建库名为test的数据库，然后两个数据库都执行以下的建表语句

```sql
use test;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_test_table0
-- ----------------------------
DROP TABLE IF EXISTS `t_test_table0`;
CREATE TABLE `t_test_table0` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `age` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_test_table1
-- ----------------------------
DROP TABLE IF EXISTS `t_test_table1`;
CREATE TABLE `t_test_table1` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `age` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
```

#### 编写配置文件

新增application.yaml文件在resources目录下，修改数据库地址以及用户名密码。

```properties
worker:
  # 雪花算法Id生成机器码
  id: 1

spring:
  jpa:
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect #使用innoDb引擎
    hibernate:
      ddl-auto: none #每次启动如果发现有实体类不更新数据库
    show-sql: true #显示sql
    database: mysql
  shardingsphere:
    props:
      sql:
        # 显示Sql
        show: true
    datasource:
      # 数据源
      names: db0,db1
      # 数据源名称
      db0:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:13301/test?useSSL=false&useUnicode=true&characterEncoding=utf-8
        username: bigkang
        password: 123456
      # 数据源名称
      db1:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        jdbcUrl: jdbc:mysql://localhost:13302/test?useSSL=false&useUnicode=true&characterEncoding=utf-8
        username: bigkang
        password: 123456
    sharding:
      # 绑定表
      binding-tables: t_test_table,t_test
      # 默认数据源名称
      default-data-source-name: db0
      # 默认分库策略
      default-database-strategy:
        inline:
          # 根据Id，进行取模，决定分配到db0还是db1
          algorithm-expression: db$->{id % 2}
          sharding-column: id
      tables:
        t_test_table:
          # 生成主键字段
          key-generator-column-name: id
          key-generator: # 主键生成策略
            column: id
            type: SNOWFLAKE
          # 设置实际数据节点，分库+分表
          actual-data-nodes: db$->{0..1}.t_test_table$->{0..1}
          database-strategy:  #分库策略
            inline:
              # 分库字段
              sharding-column: id
              # 根据Id，进行取模，决定分配到db0还是db1
              algorithm-expression: db$->{id % 2}
          table-strategy:  #分表策略
            inline:
              # 分表字段
              shardingColumn: age
              # 根据年龄取模决定分配到哪张表
              algorithm-expression: t_test_table$->{age % 2}

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: ALWAYS
```

#### 编写代码

新建实体类

```java
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @Author BigKang
 * @Date 2020/4/9 10:43 AM
 * @Summarize sharding-jdbc分库分表测试表
 */
@Entity
@Table(name = "t_test_table")
@Data
public class TestTable {

    @Id
    private Long id;

    private String name;

    private Integer age;
}
```

新建Dao

```java
/**
 * @Author BigKang
 * @Date 2020/4/9 10:48 AM
 * @Summarize 测试Dao层
 */
public interface TestTableDao extends JpaRepository<TestTable, Long>, JpaSpecificationExecutor<TestTable> {


}
```

直接新建Controller测试

```java
/**
 * @Author BigKang
 * @Date 2020/4/9 2:48 PM
 * @Summarize 测试分库分表controller
 */
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private TestTableDao testTableDao;
  
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @PostMapping("/save")
    public void save(TestTable testTable){
        testTable.setId(snowflakeIdGenerator.nextId(false));
        TestTable save = testTableDao.save(testTable);
        System.out.println(save);
    }


}
```

## 分库+分表+读写分离

### 环境以及思路

目前有3台服务器

localhost 		13301		无主从单节点

localhost 		3301		主数据库

localhost 		3302		从数据库库

我们会稍后对这些数据源进行数据库划分，首先划分主

我们将一对主从数据库看为一个逻辑数据库

```
# 第一个逻辑数据库
db0													
							localhost 		13301
# 第二个逻辑数据库
db1
							localhost 		3301，localhost 		3302
```

也就是说db0我们是没有读写分离的但是db2有读写分离

分库分表规则如下

```
如Id为 161231，age 为 10

根据Id进行取模为1，入逻辑数据库   db1，再根据age年龄进行取模10取模为0，入数据库t_test_table0

```

上面测试代码无需修改只需要修改配置文件即可，配置文件如下

### 配置文件

```properties
worker:
  # 雪花算法Id生成机器码
  id: 1
  datacenterId: 1

spring:
  jpa:
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect #使用innoDb引擎
    hibernate:
      ddl-auto: none #每次启动如果发现有实体类不更新数据库
    show-sql: true #显示sql
    database: mysql
  shardingsphere:
    props:
      sql:
        # 显示Sql
        show: true
    datasource:
      # 数据源
      names: db0,dbmaster2,dbmaster2slave1
      # 数据源名称（第一个Master，没有从数据库），则不需要根据别名直接为db0同时不需要配置主从路由
      db0:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        jdbcUrl: jdbc:mysql://localhost:13301/test?useSSL=false&useUnicode=true&characterEncoding=utf-8
        username: bigkang
        password: 123456
      # 数据源名称（第二个Master,有从数据库）
      dbmaster2:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        jdbcUrl: jdbc:mysql://localhost:3301/test?useSSL=false&useUnicode=true&characterEncoding=utf-8
        username: bigkang
        password: 123456
      # 数据源名称（第二个Master的从数据库）
      dbmaster2slave1:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        jdbcUrl: jdbc:mysql://localhost:3302/test?useSSL=false&useUnicode=true&characterEncoding=utf-8
        username: bigkang
        password: 123456
    sharding:
      # 主从路由配置
      master-slave-rules:
      	# 逻辑库名称
        db1:
        	# 主数据库
          master-data-source-name: dbmaster2
          # 从数据库，多个则用逗号隔开
          slave-data-source-names: dbmaster2slave1

      # 绑定表
      binding-tables: t_test_table,t_test
      # 默认数据源名称
      default-data-source-name: db0
      # 默认分库策略
      default-database-strategy:
        inline:
          # 根据Id，进行取模，决定分配到db0还是db1
          algorithm-expression: db$->{id % 2}
          sharding-column: id
      tables:
        t_test_table:
          # 生成主键字段
          key-generator-column-name: id
          key-generator: # 主键生成策略
            column: id
            type: SNOWFLAKE
          # 设置实际数据节点，分库+分表
          actual-data-nodes: db$->{0..1}.t_test_table$->{0..1}
          database-strategy:  #分库策略
            inline:
              # 分库字段
              sharding-column: id
              # 根据Id，进行取模，决定分配到db0还是db1
              algorithm-expression: db$->{id % 2}
          table-strategy:  #分表策略
            inline:
              # 分表字段
              shardingColumn: age
              # 根据年龄取模决定分配到哪张表
              algorithm-expression: t_test_table$->{age % 2}

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: ALWAYS
```



## 配置信息

将type修改为相应的数据源连接池即可

### 配置Druid连接池

```
      db1:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/test
        username: root
        password: root
```

### 配置Hikari连接池

Hikari为jdbcUrl，Druid为url

```
      db1:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        jdbcUrl: jdbc:mysql://114.67.80.169:13302/test
        username: root
        password: bigkang
```

# SharidingJdbc官网

官方文档SpringBoot整合配置地址（最新版）：

```
https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/configuration/config-spring-boot/
```



# 雪花算法ID生成工具类（请查看）

​		这两个工具类都是根据修改之后的雪花ID，因为原雪花Id如果每秒钟生成一次，则每次结果都为偶数，如果进行取模分库分表之后会导致大量的数据在偶数库中，所以我们需要修改优化一下雪花算法，需要生成的Id既可能基数又可能为偶数，哪怕是在生成Id不频繁的情况下。

## 优化后雪花算法

优化后的雪花算法简化了机房id，并且支持

```java
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * SnowFlake 修改版雪花Id，优化机器码以及生成Id为偶数问题
 * 0 - 0000000000 00 - 0000000000 0000000000 0000000000 000000000 - 0000 - 00000000
 * 符号位 -12位年月位(表示yyMM,最大4096,即可用至2040年)-39位时间戳 （可用17年，即可用至2035年）-4位机器ID(最大16，即可部署16个节点)-8位序列号(z最大256)
 *
 * @author BigKang
 */
@Component
public class SnowflakeIdGenerator {
    // ==============================Fields===========================================
    /**
     * 开始时间截 (2018-01-01)
     */
    private final long twepoch = 1514736000000L;

    /**
     * 时间戳占的位数
     */
    public static final long timestampBits = 39L;

    /**
     * 机器id所占的位数
     */
    public static final long workerIdBits = 4L;

    /**
     * 支持的最大机器id，结果是15 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 序列在id中占的位数
     */
    public static final long sequenceBits = 8L;

    /**
     * 机器ID向左移6位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 时间截向左移12位(4+8)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits;

    /**
     * 年月标识左移51位(39 + 4 + 8)
     */
    private final long yearMonthLeftShift = sequenceBits + workerIdBits + timestampBits;

    /**
     * 生成序列的掩码，这里为255
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~16)
     */
    @Value("${worker.id}")
    private long workerId;

    /**
     * 毫秒内序列(0~256)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        System.out.println("SnowflakeId : workerId：" + workerId);
        if (this.workerId < 0 || this.workerId > maxWorkerId) {
            throw new RuntimeException("workerId(" + this.workerId + ") is out of range [0, 15]");
        }
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId(long yyMM) {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        long preId = (yyMM << yearMonthLeftShift) | ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return preId;
    }

    public long nextId() {
        return nextId(true);
    }

    /**
     * 获得不带年月位的id
     *
     * @return
     */
    public synchronized long nextId(boolean ifEvenNum) {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }


        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            if (ifEvenNum) {
                // 时间戳改变，毫秒内序列重置
                sequence = 0L;
            } else {
                // 相同毫秒内，序列号自增
                sequence = (sequence + 1) & sequenceMask;
            }
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        long preId = ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return preId;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public static void main(String[] args) throws InterruptedException {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator();
        snowflakeIdGenerator.workerId = 1;

        for (int i = 0; i < 5; i++) {
            // 获取5次雪花Id同一秒内可能为基数或者偶数
            System.out.println(snowflakeIdGenerator.nextId(false));
            Thread.sleep(1000);
        }

        for (int i = 0; i < 5; i++) {
            // 获取5次雪花Id同一秒内全部为偶数（原版雪花算法每秒一次则全是偶数）
            System.out.println(snowflakeIdGenerator.nextId(true));
            Thread.sleep(1000);
        }


    }
}

```

配置文件添加

```
worker:
  # 雪花算法Id生成机器码
  id: 1
```



## 原雪花算法

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author BigKang
 * @Date 2020/4/10 10:53 AM
 * @Summarize IdWorker雪花算法Id生成
 */
@Component
public class IdWorker {

    /**
     * 因为二进制里第一个 bit 为如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是 0。
     * 机器ID  2进制5位  32位减掉1位 31个
     */
    @Value("${worker.id}")
    private long workerId;

    /**
     * 机房ID 2进制5位  32位减掉1位 31个
     */
    @Value("${worker.datacenterId}")
    private long datacenterId;

    /**
     * 代表一毫秒内生成的多个id的最新序号  12位 4096 -1 = 4095 个
     */
    private long sequence;

    /**
     * 设置一个时间初始值    2^41 - 1   差不多可以用69年
     */
    private long twepoch = 1585644268888L;

    /**
     * 5位的机器id
     */
    private long workerIdBits = 5L;
    /**
     * 5位的机房id
     */
    private long datacenterIdBits = 5L;

    /**
     * 每毫秒内产生的id数 2 的 12次方
     */
    private long sequenceBits = 12L;
    /**
     * 这个是二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
     */
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /**
     * 这个是一个意思，就是5 bit最多只能有31个数字，机房id最多只能是32以内
     */
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private long workerIdShift = sequenceBits;
    private long datacenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);
    /**
     * 记录产生时间毫秒数，判断是否是同1毫秒
     */
    private long lastTimestamp = -1L;

    public long getWorkerId(){
        return workerId;
    }
    public long getDatacenterId() {
        return datacenterId;
    }
    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    public IdWorker(){
    }

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        System.out.println("SnowflakeId : workerId：" + workerId);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0",maxDatacenterId));
        }
    }


    public IdWorker(long workerId, long datacenterId, long sequence) {

        // 检查机房id和机器id是否超过31 不能小于0
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {

            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0",maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }

    /**
     * 重载方法
     * @return
     */
    public long nextId(){
        return nextId(true);
    }

    /**
     * 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
     */
    public synchronized long nextId(boolean isEven) {
        // 这儿就是获取当前时间戳，单位是毫秒
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {

            System.err.printf(
                    "clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
        // 这个时候就得把seqence序号给递增1，最多就是4096
        if (lastTimestamp == timestamp) {

            // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
            // 这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
            sequence = (sequence + 1) & sequenceMask;
            // 当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }

        } else {
            // 判断是否使用偶数结果，如果是则计数器清0
            if(isEven){
                sequence = 0;
            }
            // 判断是否使用偶数结果，如果不是则计数器根据时间取模决定基数或是偶数
            else {
                sequence = timeGen() % 2 == 0 ? 0L : 1L;
            }

        }

        // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
        lastTimestamp = timestamp;
        // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
        // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
        // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) | sequence;
    }

    /**
     * 当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
     * @param lastTimestamp
     * @return
     */
    private long tilNextMillis(long lastTimestamp) {

        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    //获取当前时间戳
    private long timeGen(){
        return System.currentTimeMillis();
    }

    /**
     *  main 测试类
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        IdWorker idWorker = new IdWorker();
        idWorker.workerId = 1;
        idWorker.datacenterId = 1;


        for (int i = 0; i < 5; i++) {
            // 获取5次雪花Id同一秒内可能为基数或者偶数
            System.out.println(idWorker.nextId(false));
            Thread.sleep(1000);
        }

        for (int i = 0; i < 5; i++) {
            // 获取5次雪花Id同一秒内全部为偶数（原版雪花算法每秒一次则全是偶数）
            System.out.println(idWorker.nextId());
            Thread.sleep(1000);
        }
    }
}
```

配置文件添加

```
worker:
  # 雪花算法Id生成机器码
  id: 1
  # 机房Id
  datacenterId: 1
```

