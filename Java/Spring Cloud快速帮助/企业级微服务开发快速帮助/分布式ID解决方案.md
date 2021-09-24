# 为什么需要分布式ID？

​		分布式ID其实就是应对分布式的情况下的唯一ID，那么什么情况下我们需要使用到分布式ID呢？

​		例如我们的数据库MySQL，我们知道在数据库数据量大的情况下机器性能无法满足大数据量的处理，比如我们单表几千万甚至是上亿的情况下，那么我们就需要去对数据库进行分库分表了，那么我们分库分表我们知道数据库是有主键ID的，那么我们的数据库分库分表之后就无法使用数据库自己的自增ID了，比如两个MySQL，生成出来ID都是2那么就会导致ID冲突数据异常，那么在这个时候我们就需要去使用分布式的唯一的ID生成器了。

​		概述：为了解决分布式情况下的全局唯一ID生成，所以我们需要分布式ID。

# 主流的分布式ID生成策略？

​		那么主流的分布式ID生成策略有哪些呢？

​		有如下这些分布式ID生成策略。

## UUID

​		代码示例：

```java
public static void main(String[] args) { 
       String id = UUID.randomUUID().toString().replaceAll("-","");
       System.out.println(id);
 }
```

​		优缺点对比：

```properties
优点: 生成足够简单，本地生成无网络消耗，具有唯一性
缺点: 无序的字符串，不具备趋势自增特性，没有具体的业务含义，长度过长16 字节128位，36位长度的字符串，存储以及查询对MySQL的性能消耗较大，MySQL官方明确建议主键要尽量越短越好，作为数据库主键 UUID 的无序性会导致数据位置频繁变动，严重影响性能。
```

## 单机DB自增主键

​		实现方式：

```sql
-- 使用数据库的auto_increment自增ID就可以实现
CREATE TABLE distributed_id (
    id bigint(20) unsigned NOT NULL auto_increment, 
    value TINYINT(1),
    PRIMARY KEY (id)
) ENGINE=MyISAM;

-- 每次插入获取最新的插入ID即可
insert into distributed_id(value)  VALUES (null);
select LAST_INSERT_ID() as id;
```

​		优缺点对比：

```properties
优点: 实现简单，ID单调自增，数值类型查询速度快
缺点: 单节点数据库无法抵御高并发，并且单节点故障后容易引起ID生成崩溃，引起一系列问题
```

## 集群DB自增主键

​		实现方式：

```sql
-- 还是使用上方自增，但是结合集群使用
-- 以三个节点为示例

-- 设置MySQL1
-- 设置自增起始位置，以及每次自增长度
set @@auto_increment_offset = 1;
set @@auto_increment_increment = 3;

-- 设置MySQL2
-- 设置自增起始位置，以及每次自增长度
set @@auto_increment_offset = 2;
set @@auto_increment_increment = 3;

-- 设置MySQL3
-- 设置自增起始位置，以及每次自增长度
set @@auto_increment_offset = 3;
set @@auto_increment_increment = 3;

-- 结合如上MySQL生成序列ID为如下：

    -- MySQL1： 		1 - 4 - 7 - 10
    -- MySQL1： 		2 - 5 - 8 - 11
    -- MySQL1： 		3 - 6 - 9 - 12
```

​		优缺点对比：

```properties
优点: 解决单节点故障后容易引起ID生成崩溃，引起一系列问题
缺点: DB始终性能问题，无法应对高并发，扩容不方便
```

## 基于数据库的号段模式

​		实现方式：

```sql
-- 号段模式是当下分布式ID生成器的主流实现方式之一，号段模式可以理解为从数据库批量的获取自增ID，每次从数据库取出一个号段范围，例如 (1,1000] 代表1000个ID，具体的业务服务将本号段，生成1~1000的自增ID并加载到内存。
CREATE TABLE paragraph_id (
  biz_id	int(20) NOT NULL COMMENT '业务id',
  max_id bigint(20) NOT NULL COMMENT '当前最大id',
  step int(20) NOT NULL COMMENT '号段的布长',
  version int(20) NOT NULL COMMENT '版本号',
  PRIMARY KEY (`biz_id`)
);

-- 初始化数据
INSERT INTO paragraph_id VALUES (1,1,10000,1);

-- 举例，号段为10000，业务类型为1
-- 实现流程  从数据库申请 业务类型为1的数据，并且获取10000段ID，然后更新数据库

-- Java代码  查询 业务ID为1的数据最大ID，以及Version版本，还有步长（每次自增数据）
SELECT max_id,version,step FROM paragraph_id WHERE biz_id = 1;
-- 查询出 1 ，1 ，10000
-- 然后使用乐观锁方式进行修改
-- 注意条件中加入Version以及业务ID
update paragraph_id set max_id = max_id+step, version = version + 1 where version = 1 and biz_id = 1;

-- 如果修改成功表示争抢到了这10000个ID，那么用本地变量存储查询出来的数据
```

​		伪代码：

```java

/**
 * @author HuangKang
 * @date 2021/8/30 4:15 下午
 * @describe 分段ID伪代码测试
 */
public class ParagraphIdTest {

    static class ParagraphInfo {
        // 记录的数据库中的最大ID
        Long max_id;
        // 步长
        Integer step;
        // 版本
        Integer version;
    }

    /**
     * 根据业务类型查询最新的分段信息
     * @param bizId 业务ID
     * @return 分段信息
     */
    public static ParagraphInfo selectParagraph(Integer bizId) {
        // 查询SQL，查询最新的分段ID器
        String sql = "SELECT max_id,version,step FROM paragraph_id WHERE biz_id = " + bizId;
        // 直接返回
        return null;
    }

    /**
     * 获取最新的分段信息，使用乐观锁进行修改
     * @param bizId 业务ID
     * @param paragraphInfo 分段信息
     * @return
     */
    public static ParagraphInfo getParagraph(Integer bizId,ParagraphInfo paragraphInfo) {
        // 修改新版本业务ID
        String sql = "update paragraph_id set max_id = max_id+step, version = version + 1 where version = " + paragraphInfo.version +" and biz_id =" + bizId;

        // 大于一表示修改成功
        Integer row = Integer.valueOf("执行上方SQL返回影响行数");
        if(row >= 1){
            return paragraphInfo;
        }else {
            // 查询最新的分段信息
            ParagraphInfo newParagraphInfo = selectParagraph(1);
            // 自旋操作循环修改，查询最新信息 -》 自旋修改 -》 修改成功返回
            return getParagraph(bizId,newParagraphInfo);
        }
    }


    public static void main(String[] args) {
        // 业务ID
        Integer bizId = 1;

        // 首次查询最新的分段信息
        ParagraphInfo paragraphInfo = selectParagraph(bizId);

        // 获取分段,使用乐观锁
        ParagraphInfo paragraph = getParagraph(bizId, paragraphInfo);

        // 内存起始ID
        Long memoryStartId = paragraph.max_id;
        // 内存扩容ID
        Long memoryMaxId = paragraph.max_id + paragraph.step;

        // 分段ID生成
        memoryStartId++;
        memoryStartId++;
        // memoryStartId++;
        // 内存起始ID如果大于等于Max表示需要扩容，重新走自旋操作
        if(memoryStartId >= memoryMaxId){
            ParagraphInfo newParagraph = getParagraph(bizId, paragraphInfo);
            // 刷新内存分段ID
            memoryStartId = newParagraph.max_id;
            memoryMaxId = newParagraph.max_id + newParagraph.step;
        }

        // 只需要重复达到Max重新刷新即可
    }

}
```

​		优缺点对比：

```properties
优点: 根据业务类型区分ID生成，并且不强依赖于数据库，也不会频繁对数据库产生压力
缺点: 需要依赖数据库
```

## 基于Redis自增模式

​		实现方式：

```sql
-- Redis也同样可以实现，原理就是利用redis的 incr命令实现ID的原子性自增。

-- 初始化自增ID为1
set seq_id 1
-- 增加1，并返回递增后的数值
incr seq_id
```

​		优缺点对比：

```properties
优点: 性能高，快，并且灵活
缺点: 依赖Redis，incr语句过多，发生宕机AOF恢复效率慢
```

## 基于雪花算法（**Snowflake**）

​		雪花算法（Snowflake）是twitter公司内部分布式项目采用的ID生成算法，开源后广受国内大厂的好评，在该算法影响下各大公司相继开发出各具特色的分布式生成器。

​		`Snowflake`生成的是Long类型的ID，一个Long类型占8个字节，每个字节占8比特，也就是说一个Long类型占64个比特。

​		Snowflake ID组成结构：`正数位`（占1比特）+ `时间戳`（占41比特）+ `机器ID`（占5比特）+ `数据中心`（占5比特）+ `自增值`（占12比特），总共64比特组成的一个Long类型。

- 第一个bit位（1bit）：Java中long的最高位是符号位代表正负，正数是0，负数是1，一般生成ID都为正数，所以默认为0。
- 时间戳部分（41bit）：毫秒级的时间，不建议存当前时间戳，而是用（当前时间戳 - 固定开始时间戳）的差值，可以使产生的ID从更小的值开始；41位的时间戳可以使用69年，(1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69年
- 工作机器id（10bit）：也被叫做`workId`，这个可以灵活配置，机房或者机器号组合都可以。
- 序列号部分（12bit），自增值支持同一毫秒内同一个节点可以生成4096个ID

根据这个算法的逻辑，只需要将这个算法用Java语言实现出来，封装为一个工具方法，那么各个业务应用可以直接使用该工具方法来获取分布式ID，只需保证每个业务应用有自己的工作机器id即可，而不需要单独去搭建一个获取分布式ID的应用。

​		实现代码：

```java
/**
 * Twitter的SnowFlake算法,使用SnowFlake算法生成一个整数，然后转化为62进制变成一个短地址URL
 *
 * https://github.com/beyondfengyu/SnowFlake
 */
public class SnowFlakeShortUrl {

    /**
     * 起始的时间戳
     */
    private final static long START_TIMESTAMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12;   //序列号占用的位数
    private final static long MACHINE_BIT = 5;     //机器标识占用的位数
    private final static long DATA_CENTER_BIT = 5; //数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private long dataCenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastTimeStamp = -1L;  //上一次时间戳

    private long getNextMill() {
        long mill = getNewTimeStamp();
        while (mill <= lastTimeStamp) {
            mill = getNewTimeStamp();
        }
        return mill;
    }

    private long getNewTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 根据指定的数据中心ID和机器标志ID生成指定的序列号
     *
     * @param dataCenterId 数据中心ID
     * @param machineId    机器标志ID
     */
    public SnowFlakeShortUrl(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("DtaCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0！");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("MachineId can't be greater than MAX_MACHINE_NUM or less than 0！");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currTimeStamp = getNewTimeStamp();
        if (currTimeStamp < lastTimeStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currTimeStamp == lastTimeStamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currTimeStamp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastTimeStamp = currTimeStamp;

        return (currTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT //时间戳部分
                | dataCenterId << DATA_CENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }
    
    public static void main(String[] args) {
        SnowFlakeShortUrl snowFlake = new SnowFlakeShortUrl(2, 3);

        for (int i = 0; i < (1 << 4); i++) {
            //10进制
            System.out.println(snowFlake.nextId());
        }
    }
}
```

## 百度（**uid-generator**）（推荐）

​		`uid-generator`是由百度技术部开发uid-generator`是由百度技术部开发。`uid-generator`是基于`Snowflake`算法实现的，与原始的`snowflake`算法不同在于，`uid-generator`支持自`定义时间戳`、`工作机器ID`和 `序列号` 等各部分的位数，而且`uid-generator`中采用用户自定义`workId`的生成策略。

​		`uid-generator`需要与数据库配合使用，需要新增一个`WORKER_NODE`表。当应用启动时会向数据库表中去插入一条数据，插入成功后返回的自增ID就是该机器的`workId`数据由host，port组成。

​		`workId`，占用了22个bit位，时间占用了28个bit位，序列化占用了13个bit位，需要注意的是，和原始的`snowflake`不太一样，时间的单位是秒，而不是毫秒，`workId`也不一样，而且同一应用每次重启就会消费一个`workId`。

​		项目地址： [点击进入](https://link.zhihu.com/?target=https%3A//github.com/baidu/uid-generator)		

## 美团（Leaf）（推荐）

​		项目地址： [点击进入](https://github.com/Meituan-Dianping/Leaf)		

​		部署Leaf-Server

```sh
git clone https://github.com/Meituan-Dianping/Leaf.git

# 修改leaf.properties

# 号段方式ID依赖于MySQl，雪花依赖于Zookeeper

leaf.name=com.sankuai.leaf.opensource.test
# 开启号段模式
leaf.segment.enable=true
# 设置MySQl地址
#leaf.jdbc.url=jdbc:mysql://localhost:3306/leaf?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true
#leaf.jdbc.username=
#leaf.jdbc.password=

# 是否开启雪花
leaf.snowflake.enable=false
# 设置Zookeeper地址以及端口
#leaf.snowflake.zk.address=
#leaf.snowflake.port=
```

​		修改完成后打Jar包启动即可

```sh
mvn clean install -DskipTests
cd leaf-server/target
java -jar leaf.jar
```

​		号段模式数据库新建表

```sql
-- 新建表
CREATE TABLE `leaf_alloc` (
  `biz_tag` varchar(128)  NOT NULL DEFAULT '', -- your biz unique name
  `max_id` bigint(20) NOT NULL DEFAULT '1',
  `step` int(11) NOT NULL,
  `description` varchar(256)  DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB;

-- 使用方式参考上方基于数据库号段方式
-- 号段模式需要插入数据库表数据
-- biz_tag   			业务标识(业务名)
-- max_id					当前最大ID
-- step						步段（号段）
-- description		描述
insert into leaf_alloc(biz_tag, max_id, step, description) values('leaf-segment-test', 1, 2000, 'Test leaf Segment Mode Get Id')
```

​		测试是否可以使用

```sql
-- 测试号段方式：http://localhost:8080/api/segment/get/{业务名}   
-- 测试雪花方式：http://localhost:8080/api/snowflake/get/{业务名}
```

​		优缺点对比：

```properties
优点: 在现有的号段方式又拓展了雪花ID，在订单中也可以使用，并且提供了HTTP方式调用
缺点: 依赖于MySQL以及ZK，MySQL不支持多Master容灾
```

## **滴滴（Tinyid）**（推荐）

​		`Tinyid`是滴滴开发的一款分布式ID系统，`Tinyid`是在`美团（Leaf）`的`leaf-segment`算法基础上升级而来，不仅支持了数据库多主节点模式，还提供了`tinyid-client`客户端的接入方式，使用起来更加方便。但和美团（Leaf）不同的是，Tinyid只支持号段一种模式不支持雪花模式。

- 全局唯一的long型ID
- 趋势递增的id
- 提供 http 和 java-client 方式接入
- 支持批量获取ID
- 支持生成1,3,5,7,9…序列的ID
- 支持多个db的配置

​		项目地址： [点击进入](https://github.com/Meituan-Dianping/Leaf)		

​		部署Tinyid-Server

```sh
# 下载并且进入目录
git clone https://github.com/didi/tinyid.git
cd tinyid/tinyid-server/ 
# 构建jar包
sh build.sh offline

# 初始化数据到数据库
CREATE TABLE `tiny_id_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `biz_type` varchar(63) NOT NULL DEFAULT '' COMMENT '业务类型，唯一',
  `begin_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始id，仅记录初始值，无其他含义。初始化时begin_id和max_id应相同',
  `max_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '当前最大id',
  `step` int(11) DEFAULT '0' COMMENT '步长',
  `delta` int(11) NOT NULL DEFAULT '1' COMMENT '每次id增量',
  `remainder` int(11) NOT NULL DEFAULT '0' COMMENT '余数',
  `create_time` timestamp NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '更新时间',
  `version` bigint(20) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_biz_type` (`biz_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT 'id信息表';

CREATE TABLE `tiny_id_token` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `token` varchar(255) NOT NULL DEFAULT '' COMMENT 'token',
  `biz_type` varchar(63) NOT NULL DEFAULT '' COMMENT '此token可访问的业务类型标识',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT 'token信息表';

INSERT INTO `tiny_id_info` (`id`, `biz_type`, `begin_id`, `max_id`, `step`, `delta`, `remainder`, `create_time`, `update_time`, `version`)
VALUES
	(1, 'test', 1, 1, 100000, 1, 0, '2018-07-21 23:52:58', '2018-07-22 23:19:27', 1);

INSERT INTO `tiny_id_info` (`id`, `biz_type`, `begin_id`, `max_id`, `step`, `delta`, `remainder`, `create_time`, `update_time`, `version`)
VALUES
	(2, 'test_odd', 1, 1, 100000, 2, 1, '2018-07-21 23:52:58', '2018-07-23 00:39:24', 3);


INSERT INTO `tiny_id_token` (`id`, `token`, `biz_type`, `remark`, `create_time`, `update_time`)
VALUES
	(1, '0f673adf80504e2eaa552f5d791b644c', 'test', '1', '2017-12-14 16:36:46', '2017-12-14 16:36:48');

INSERT INTO `tiny_id_token` (`id`, `token`, `biz_type`, `remark`, `create_time`, `update_time`)
VALUES
	(2, '0f673adf80504e2eaa552f5d791b644c', 'test_odd', '1', '2017-12-14 16:36:46', '2017-12-14 16:36:48');


-----------------------------------
# 启动Jar包
java -jar tinyid-server-0.1.0-SNAPSHOT.jar --datasource.tinyid.primary.url=jdbc:mysql://localhost:3306/test --datasource.tinyid.primary.username=#{数据用户} --datasource.tinyid.primary.password=#{数据库密码}
```

​		测试访问

```sh
nextId:
curl 'http://localhost:9999/tinyid/id/nextId?bizType=test&token=0f673adf80504e2eaa552f5d791b644c'
response:{"data":[2],"code":200,"message":""}

nextId Simple:
curl 'http://localhost:9999/tinyid/id/nextIdSimple?bizType=test&token=0f673adf80504e2eaa552f5d791b644c'
response: 3

with batchSize:
curl 'http://localhost:9999/tinyid/id/nextIdSimple?bizType=test&token=0f673adf80504e2eaa552f5d791b644c&batchSize=10'
response: 4,5,6,7,8,9,10,11,12,13

Get nextId like 1,3,5,7,9...
bizType=test_odd : delta is 2 and remainder is 1
curl 'http://localhost:9999/tinyid/id/nextIdSimple?bizType=test_odd&batchSize=10&token=0f673adf80504e2eaa552f5d791b644c'
response: 3,5,7,9,11,13,15,17,19,21
```

​		Java客户端连接，需要先Install源码

```java
// 引入Maven
<dependency>
    <groupId>com.xiaoju.uemc.tinyid</groupId>
    <artifactId>tinyid-client</artifactId>
    <version>${tinyid.version}</version>
</dependency>
  
// resource文件新建tinyid_client.properties下写入
tinyid.server=localhost:9999
tinyid.token=0f673adf80504e2eaa552f5d791b644c
```

​		使用方式

```java
    public static void main(String[] args) {
        Long id = TinyId.nextId("test");
        System.out.println(id);
        List<Long> ids = TinyId.nextId("test", 10);
        System.out.println(ids);
    }
```

​		优缺点对比：

```properties
优点: 提供自增序列ID，Tinyid扩展了leaf-segment算法，支持了多db(master)
缺点: 类似订单id的业务(因为生成的id大部分是连续的，容易被扫库、或者测算出订单量)
```

