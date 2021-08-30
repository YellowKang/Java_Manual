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



## 基于雪花算法（**Snowflake**）



## 百度（**uid-generator**）



## 美团（Leaf）



## **滴滴（Tinyid）**



