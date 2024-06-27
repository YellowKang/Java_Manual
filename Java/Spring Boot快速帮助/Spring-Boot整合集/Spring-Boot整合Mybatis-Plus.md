# 	什么是Mybatis-Plus

​		为简化开发而生 ，只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑。 只需简单配置，即可快速进行 CRUD 操作，从而节省大量时间。 热加载、代码生成、分页、性能分析等功能一应俱全。 

# 快速开始

## 先创建数据库数据

```sql
DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id BIGINT(20) NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT(11) NULL DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (id)
);

DELETE FROM user;

INSERT INTO user (id, name, age, email) VALUES
(1, 'Jone', 18, 'test1@baomidou.com'),
(2, 'Jack', 20, 'test2@baomidou.com'),
(3, 'Tom', 28, 'test3@baomidou.com'),
(4, 'Sandy', 21, 'test4@baomidou.com'),
(5, 'Billie', 24, 'test5@baomidou.com');
```

## 引入依赖《注：基于Spring-Boot环境》

```xml

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.0.6</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.0</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

      	<dependency>
        		<groupId>org.springframework.boot</groupId>
        		<artifactId>spring-boot-starter-test</artifactId>
        		<scope>test</scope>
    		</dependency>
```

## 编写配置文件

```properties
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test_mybatis_plus?characterEncoding=UTF-8&useSSL=false
    username: root
    password: root
```

## 扫描包注解

```java
@MapperScan("com.atguigu.mybatis_plus.mapper")
```

## 编写实体类

```java
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

## 创建Mapper接口

```java
public interface UserMapper extends BaseMapper<User> {
    
}
```

## 代码进行查询

```java
    @Test
    public void testSelectList() {
        System.out.println(("----- selectAll method test ------"));
        //UserMapper 中的 selectList() 方法的参数为 MP 内置的条件封装器 Wrapper
        //所以不填写就是无任何条件
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }
```

## 配置日志文件

```properties
#mybatis日志
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```



# 增删改查（条件构造器进行强化）

## 增加

### 单个增加

```java
        //新建一个User对象
        //由于id设置了自动生成id的策略所以不用填写
        User user = new User();
        user.setName("黄康");
        user.setEmail("bigkang@qq.com");
        //进行添加
        testMapper.insert(user);
```

## 删除

### 单个按id删除

```java
testMapper.deleteById(1);
```

### 批量按id删除

```java
        List<Integer> deleteIds = new ArrayList<>();
        deleteIds.add(1);
        deleteIds.add(5);
        int i = testMapper.deleteBatchIds(deleteIds);
```

### 按条件删除

```java
        Map<String,Object> map = new HashMap<>();
        //map的键为数据库的字段名
        map.put("email", "bigkang@qq.com");
        testMapper.deleteByMap(map);
```



## 修改

先查询出来然后修改名字《注。因为按主键进行修改所以要查询，并且如果null值的列他是不会修改的》

```java
       User user = testMapper.selectById(12);
       user.setName("社会康");
       testMapper.updateById(user);
```

## 查询

### 按id查询单个

查询id为1的用户

```java
User user1 = testMapper.selectById(1);
```

### 查询所有

查询所有用户（条件为空）

```java
 List<User> user1 = testMapper.selectList(null);
```

### 按条件查询多个

查询年龄为18的用户

```java
Map<String,Object> map = new HashMap<>();
map.put("age","18");
List<User> users = testMapper.selectByMap(map);
```

# Plus插件

## 新建config配置类

```java
/**
 * @Author BigKang
 * @Date 2019/5/9 17:37
 * @Summarize mybatis-plus配置类
 */
@EnableTransactionManagement
@Configuration
public class CustomMybatisPlusConfig {
  
    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 使用乐观锁
     * @return
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){
        return  new OptimisticLockerInterceptor();
    }

    /**
     * 逻辑删除功能，新版本默认配置，此Bean在新版本已经删除
     * @return
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }

    /**
     * SQL执行效率插件,性能测试，3.2.0以上版本移除
     */
    @Bean
    @Profile({"dev","test"})// 设置 dev test 环境开启
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}
```

## 自动填充插件

我们新建一个自定义的自动填充配置类

```java
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //给createTime"这个属性在插入的时候创建一个时间
        this.setFieldValByName("createTime",new Date(), metaObject);

        //给updateTime这个属性在插入的时候创建一个时间
        this.setFieldValByName("updateTime",new Date(), metaObject);

        //给version这个属性在插入的时候标记为1
        this.setFieldValByName("version",1, metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //给updateTime这个属性在修改的时候将时间改为最新的new Date（）
        this.setFieldValByName("updateTime",new Date(), metaObject);

    }
}
```

然后实体类中配置，我们可以看到我们在updateTime上加上了注解，加上了插入自动填充，以及修改自动填充

```java

/**
 * <p>
 * 
 * </p>
 *
 * @author 黄康
 * @since 2019-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TestPlus implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String address;

    private Integer age;

    private String birthday;

    private String email;

    private String password;

    private String phone;

    private String username;


}
```

## 分页插件

分页插件非常简洁方便，按照上方配置config配置类即可

```java
IPage<TestPlus> page = baseService.page(new Page<>(1, 10));
```

## 乐观锁插件

乐观锁，简单来说就是我们认为他每次访问之前都有可能会被修改掉，所以我们加上版本号进行区分，如果被别人修改掉了我们则修改失败，它类似与原子引用，简单的来说就是比较版本号并且赋值，我修改3这个版本，但是别人已经修改现在版本号变为4，则修改失败，它支持的数据类型有**int,Integer,long,Long,Date,Timestamp,LocalDateTime**

```java
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄康
 * @since 2019-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TTestJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String address;

    private Integer age;

    private String birthday;

    private String email;

    private String password;

    private String phone;

    private String username;
    
    @Version
    private Integer version;

}
```

## 逻辑删除插件

配置文件（可选）

```properties
mybatis-plus:
  global-config:
    db-config:
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

然后按照上方配置config配置类

实体类中写到

```java
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄康
 * @since 2019-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TestPlus implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String address;

    private Integer age;

    private String birthday;

    private String email;

    private String password;

    private String phone;

    private String username;
    
    @Version
    private Integer version;
  
  	@TableLogic
		private Integer deleted;

}
```



## SQL效率插件

详情如上方config配置类所示

```java
    /**
     * SQL执行效率插件,性能测试
     */
    @Bean
    @Profile({"dev","test"})// 设置 dev test 环境开启
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
```

## 数据安全保护

也可以使用

```

<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
		<artifactId>jasypt-spring-boot-starter</artifactId>
    <version>1.8</version>
</dependency>
```

测试启动异常注意！！！

首先编写工具类

```java

import com.baomidou.mybatisplus.core.toolkit.AES;
import org.bouncycastle.crypto.DataLengthException;
import org.springframework.util.StringUtils;

/**
 * @Author BigKang
 * @Date 2020/6/22 2:09 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize Mybatis-Plus数据保护工具类，生成数据库秘钥连接
 */
public class GenDatabaseKey {

    public static String genKey(String host,Integer port,String db,String username,String password,String append,String key){
        if(key == null || key.length() != 16){
            throw new DataLengthException("秘钥长度必须等于16！");
        }
        StringBuffer str = new StringBuffer();
        String url = "jdbc:mysql://"+ host + ":" + port+ "/" + db;
        if (StringUtils.isEmpty(append)) {
            url += "?useSSL=false&useUnicode=true&characterEncoding=utf-8";
        }
        str.append("\t Url:\t");
        str.append(genKey(url,key));
        str.append("\n");

        str.append("username:\t");
        str.append(genKey(username,key));
        str.append("\n");

        str.append("password:\t");
        str.append(genKey(password,key));
        str.append("\n");

        return str.toString();
    }

    /**
     * 根据data以及key生成单条值
     * @param data
     * @param key
     * @return
     */
    public static String genKey(String data,String key){
        String encrypt = AES.encrypt(data, key);
        return "mpw:" + encrypt;
    }
}

```

然后进行加密,调用打印一下即可

```java
        System.out.println(GenDatabaseKey.genKey(
                // Host地址
                "192.168.1.11",
                // 端口号
                3306,
                // 数据库名
                "testdb",
                // 用户名
                "root",
                // 密码
                "root",
                // Url追加设置如useSSL=false&useUnicode=true&characterEncoding=utf-8等等
                null,
                // 加密秘钥16位
                "bigkangsixsixsix"));
```



## 执行SQL分析打印

版本差异请直接官网阅读查看

[点击进入](https://mp.baomidou.com/guide/p6spy.html)

# 条件构造器

## 查询条件构造器

```java
        //创建QueryWrapper对象
				QueryWrapper<TestPlus> queryWrapper = new QueryWrapper<>();
				//查询name等于BigKang，年龄大于等于1，小于等于20，并且id在1-100之间，然后id不等于散的数据，并且进行分页，查询第一页，一页10条
        queryWrapper
                .eq("name","Bigkang")
                .ge("age",1)
                .le("age",20)
                .between("id",1,100)
                .ne("id",3);

        IPage<TestPlus> pages =  testPlusService.page(new Page<>(1,10),queryWrapper);
```

## 修改条件构造器

```java
        //这次我们将同样的查询条件的数据的邮箱修改
				UpdateWrapper<TestPlus> updateWrapper = new QueryWrapper<>();
        updateWrapper
                .eq("name","Bigkang")
                .ge("age",1)
                .le("age",20)
                .between("age",1,100)
                .ne("id",3);
        TestPlus testPlus = new TestPlus();
        testPlus.setEmaile("bigkangsix@qq.com");

        testPlusService.update(testPlus,queryWrapper);
```



# 拓展操作

## 返回自动递增id

```java
   	@Options(useGeneratedKeys = true,keyProperty = "id")
   	@Insert("insert into admin(name) values(#{name})")
    int insertUser(User user);
```

## 自定义封装动态SQL批量增删（不推荐）

```java
    //批量增加方法
    @InsertProvider(type = Provider.class, method = "addAssign")
    Integer addAssignRole(@Param("userid") Integer userid,@Param("ids") Integer[] ids);

    //批量删除方法
    @DeleteProvider(type = Provider.class, method = "delAssign")
    Integer delAssignRole(@Param("userid") Integer userid,@Param("ids") Integer[] ids);

    //自定义类
    class Provider {
        public String addAssign(Map map) {
            Integer[] integers=(Integer[])map.get("ids");
            Integer userid = (Integer) map.get("userid");
            StringBuilder sb = new StringBuilder();
            sb.append("insert into t_user_role VALUES ");
            for (int i = 0; i < integers.length; i++) {
                if(i>0){
                    sb.append(",");
                }
                sb.append("(null," + userid + "," + integers[i] + ")");
            }
            return sb.toString();
        }

        public String delAssign(Map map) {
            Integer[] integers=(Integer[])map.get("ids");
            Integer userid = (Integer) map.get("userid");
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM t_user_role where userid = " + userid + " and roleid in (");
            for (int i = 0; i < integers.length; i++) {
                if(i>0){
                    sb.append(",");
                }
                sb.append(integers[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }
```

## 实体枚举

​		有时候我们实体中有枚举类型，我们怎么将枚举类型给存储到数据库里面呢？

​		例如：

```java
    private SysUserStatus status;
```

​		我们枚举为，直接继承IEnum,定义存储的泛型，然后重写getValue接即可，返回的Value属性就是我们存储到数据库中的值，并且在状态中设置JsonValue，返回给前端：

```java

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author BigKang
 * @Date 2021/1/8 2:33 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 系统用户状态
 */
@Getter
@AllArgsConstructor
public enum SysUserStatus  implements IEnum<Integer> {

    FREEZE(0,"冻结"),
    NORMAL(1,"正常"),
    BAN(2,"封禁"),
    APPROVE(3,"待审批");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String status;

    @Override
    public Integer getValue() {
        return this.code;
    }
}
```

​		然后我们配置配置文件即可，枚举包路径，使用;隔开

```properties
mybatis-plus:
  type-enums-package: com.topcom.mp.security.enums
```

## 自定义返回值类型

```xml
    <resultMap type="com.topcom.mp.security.vo.DeptTreeVo" id="DeptTreeVo">
        <id column="id" property="id"/>
        <!-- 定义普通列封装规则 -->
        <result column="name" property="name"/>
        <result column="parent_id" property="parentId"/>
    </resultMap>

    <select id="getDeptTreeList" resultMap="DeptTreeVo">
        select id,name,parent_id from t_sys_dept
    </select>
```

## 结果集封装List

```sql
# 有如下SQL，根据消息类型，查询需要告警的用户， 用户消息表 '关联' 用户表 '关联' 用户免打扰时间表
    SELECT
      tpu.username as username,
      tpu.userPhone as userPhone,
      tpu.batchId as batchId,
      tput.weekday as weekday,
      tput.startTime as startTime
    FROM
      (
        SELECT
          *
        FROM
          t_push_user_message
        WHERE
          delFlag = 0
          AND messageCode = #{messageCode}
      ) tpum
    LEFT JOIN (SELECT * FROM t_push_user WHERE delFlag = 0) tpu ON tpu.pushUserId = tpum.pushUserId
    LEFT JOIN (SELECT * FROM t_push_user_time WHERE delFlag = 0) tput ON tpu.pushUserId = tput.pushUserId
    
# 返回结果如下，查询到两个用户通知信息 而且属于一个团队
李孟	16600075711	3	2	02:00:00
李孟	16600075711	3	2	03:00:00
李孟	16600075711	3	2	04:00:00
李孟	16600075711	3	2	10:00:00

臧亮	18665833218	3	1	00:00:00
臧亮	18665833218	3	1	01:00:00
臧亮	18665833218	3	1	02:00:00
臧亮	18665833218	3	1	03:00:00
臧亮	18665833218	3	1	04:00:00

# 我们需要把他封装成如下结构（注意！！！！不能使用公共静态类，只能拆开，结果集映射会报错）

		@ApiModelProperty("团队ID")
    private Integer batchId;

    private List<PushUser> pushUsers;
    {
    	    @ApiModelProperty("用户名称")
    			private String username;

    			@ApiModelProperty("用户手机号")
    			private String userPhone;

    			@ApiModelProperty("免打扰时间")
    			private List<PushUserTime> notDisturbTimes;
    			{
              @ApiModelProperty("周日类型 1：周一到周五  2：周六日")
              private String weekday;

              @ApiModelProperty("免打扰时间")
              private String startTime;
    			}
    }
    
# 封装成一个两层的树结构，后续根据团队规则进行告警通知
# 我们需要自定义resultMap，标签如下
  <resultMap id="PushUserTimeVo" type="com.lpv.api.bean.user.PushUserTimeVo" >
    <result column="batchId" property="batchId" jdbcType="INTEGER" />

    <collection property="pushUsers" ofType="com.lpv.api.bean.user.PushUser">
      <result column="username" property="username" jdbcType="VARCHAR"  />
      <result column="userPhone" property="userPhone" jdbcType="VARCHAR"  />
      <collection property="notDisturbTimes" ofType="com.lpv.api.bean.user.PushUserTime">
        <result column="weekday" property="weekday" jdbcType="VARCHAR"  />
        <result column="startTime" property="startTime" jdbcType="VARCHAR"  />
      </collection>
    </collection>
  </resultMap>
```



## 树结构查询

### 循环查询方式

### 封装树方式

​		封装树表示我们先将数据查询出List，然后封装为树状数据。

​		首先我们需要封装实体类，需要Id，父Id以及排序字段，和子节点。

```java

/**
 * @Author BigKang
 * @Date 2021/1/14 2:30 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize MP通用树实体
 */
@Getter
@Setter
public class BaseMpTreeEntity<T,PK> extends BaseMpEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    protected PK id;

    @ApiModelProperty(value = "父节点Id")
    protected PK parentId;

    @ApiModelProperty(value = "排序")
    protected Integer sorted;

    @ApiModelProperty(value = "子节点")
    @TableField(exist = false)
    protected List<T> children = new ArrayList<>();

}
```

​		然后实体类集成

```java
@Getter
@Setter
@ApiModel("系统部门")
@TableName("t_sys_dept")
public class SysDept extends BaseMpTreeEntity<SysDept,Long> {

    @ApiModelProperty(value = "部门名称")
    private String name;

}
```

​		然后调用查询

```java
    @Override
    public List<SysDept> getDeptTree() {
        QueryWrapper<SysDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sorted");
        List<SysDept> sysDepts = baseDao.selectList(queryWrapper);
        // 调用Tree工具类
        List<SysDept> sysDeptsTree = TreeVoUtil.convertTreeVo2(sysDepts, null);
        return sysDeptsTree;
    }
```

​		工具类如下

```java
import com.topcom.base.mp.BaseMpTreeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2021/1/14 11:42 上午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 树Vo工具类
 */
public class TreeVoUtil {

    /**
     * @param list 继承了原来的树实体的对象集合
     * @param parentId 父ID，表示从哪个节点获取树，空为根节点
     * @param <T> 对象泛型
     * @param <PK> 主键泛型
     * @return
     */
    public static <T extends BaseMpTreeEntity<T, PK>, PK> List<T> convertTree(List<T> list, PK parentId) {
        List<T> trees = new ArrayList<>();
        for (T item : list) {
            // 获取到根节点
            if (parentId == null) {
                if (item.getParentId() == null) {
                    trees.add(item);
                }
            }
            // 获取指定节点
            else if (parentId.equals(item.getParentId())) {
                trees.add(item);
            }
            for (T it : list) {
                if (it.getParentId() != null && it.getParentId().equals(item.getId())) {
                    if (item.getChildren() == null) {
                        item.setChildren(new ArrayList<T>());
                    }
                    boolean isPut = true;
                    for (T childItem : item.getChildren()) {
                        if (it.getId().equals(childItem.getId())) {
                            isPut = false;
                        }
                    }
                    if (isPut) {
                        item.getChildren().add(it);
                    }

                }
            }
        }
        return trees;
    }
}
```

## 骚操作之mybatis-dynamic-sql

​		我们可以引入mybatis-dynamic-sql，直接使用Java代码从而放弃掉Xml

```
      	<!-- https://mvnrepository.com/artifact/org.mybatis.dynamic-sql/mybatis-dynamic-sql -->
        <dependency>
            <groupId>org.mybatis.dynamic-sql</groupId>
            <artifactId>mybatis-dynamic-sql</artifactId>
            <version>1.2.1</version>
        </dependency>
```

## XML-SQL

​		结果集映射

```xml
    <resultMap type="com.botpy.vosp.admin.business.activity.resp.ActivityStyleCityResp" id="cityObject">
        <id property="province" column="PROVINCE"/>
        <collection property="city" ofType="java.lang.String">
            <id column="CITY"/>
        </collection>
    </resultMap>
```

​		list查询

```xml
				<if test="meituanCaseInfoList != null and meituanCaseInfoList.size()>0">
            AND meituanCaseNo in (
            <foreach collection="meituanCaseInfoList" item="item" separator=",">
                #{item.meituanCaseNo}
            </foreach>
            )
        </if>
```

## QueryWrapper转Lambda

​		业务代码使用LambdaQueryWrapper，后续进行优化新增权限金额排序不同用户优先展示不同金额案件排序

```java
        QueryWrapper<CaseInfo> basisQuery = new QueryWrapper<>();
        basisQuery.select(
                String.join(",", MybatisPlusUtil.getEntitySQLColumn(CaseInfo.class)) + 
                        ",if(claim_amount > 5000,1,0)  as sortField");
        basisQuery.orderByDesc("sortField");
        LambdaQueryWrapper<CaseInfo> query = basisQuery.lambda();
```

## 封装通用Page

```java

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HuangKang
 * @date 2023/1/10 1:42 PM
 * @describe BaseMybaitsPlus分页
 */
@Data
public class BaseMPPage {

    @ApiModelProperty(value = "页码")
    private Integer page;

    @ApiModelProperty(value = "条数")
    private Integer size;

    @ApiModelProperty(value = "排序")
    private List<OrderItem> orders;

    public <T> Page<T> genPage(Class<T> tClass) {
        return genPage(tClass, null, null, 1000L);
    }

    public <T> Page<T> genPage(Class<T> tClass, List<String> orderField) {
        return genPage(tClass, orderField, null, 1000L);
    }

    public <T> Page<T> genPage(Class<T> tClass, Map<String, String> orderFieldMap) {
        return genPage(tClass, null, orderFieldMap, 1000L);
    }

    public <T> Page<T> genPage(Class<T> tClass, List<String> orderField, Map<String, String> orderFieldMap, Long maxSize) {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }

        // 排序字段封装，校验以及转换
        if (orders != null && !orders.isEmpty()) {
            for (OrderItem order : orders) {

                // 实体和数据库Map映射,转换为数据库排序字段
                if (orderFieldMap != null) {
                    String dbColumn = orderFieldMap.get(order.getColumn());
                    if (dbColumn == null) {
                        throw new CustomSystemException(ExceptionEnum.ORDER_FIELD_ILLEGAL);
                    }
                    order.setColumn(dbColumn);
                }

                // 字段集合，对应实体的数据库字段集合
                if (orderField != null && !orderField.isEmpty()) {
                    // 排序字段在其中则抛出异常
                    if (!orderField.contains(order.getColumn())) {
                        throw new CustomSystemException(ExceptionEnum.ORDER_FIELD_ILLEGAL);
                    }
                }
                // 防止SQL注入
                if (SqlInjectionUtils.check(order.getColumn())) {
                    throw new CustomSystemException(ExceptionEnum.ORDER_FIELD_ILLEGAL);
                }
            }
        }
        Page<T> of = Page.of(page, size);
        of.setOrders(orders);
        of.setMaxLimit(maxSize);
        return of;
    }

}

```

​		通用简单分页（不需要返回原page一堆字段封装）

```java
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author HuangKang
 * @date 2023/1/10 2:33 PM
 * @describe 简单分页
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplePage<T> {

    @ApiModelProperty(value = "总页码")
    private Long pages;

    @ApiModelProperty(value = "当前页码")
    private Long current;


    @ApiModelProperty(value = "页码条数")
    private Long size;

    @ApiModelProperty(value = "总条数")
    private Long total;

    @ApiModelProperty(value = "列表数据")
    private List<T> list;

    /**
     * 获取简单分页
     * @param pageData 原Page数据
     * @return 简单分页数据
     */
    public static <T> SimplePage<T> genSimplePage(Page<T> pageData) {
        return new SimplePage(
                pageData.getPages(),
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords());
    }

}

```

​		分页使用，使用下方工具类

```java
        private static Map<String,String> COLUMN_MAP = MybatisPlusUtil.getEntitySQLColumnMap(CaseInfo.class);

        private static Map<String,String> COLUMN_LIST = MybatisPlusUtil.getEntitySQLColumnMap(CaseInfo.class);



      {
        	// dto为继承了BaseMPPage的DTO实体
          Page<CaseInfo> page = dto.genPage(CaseInfo.class,COLUMN_MAP);
        	// CaseInfo 为实体
          LambdaQueryWrapper<CaseInfo> queryWrapper = new LambdaQueryWrapper<>();

          Page<CaseInfo> pageData = baseMapper.selectPage(page, queryWrapper);
        	return SimplePage.genSimplePage(pageData);
      }

        
```

## 工具类

```java

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HuangKang
 * @date 2023/1/10 3:17 PM
 * @describe MybatisPlus工具类
 */
public class MybatisPlusUtil {
    private static final String DEFAULT_TABLE_NAME = "t_default_table";

    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.############");

    /**
     * 数据库类型映射Mapping
     */
    private static final Map<Class<?>, String> TYPE_MAPPING = new HashMap<>();

    static {
        TYPE_MAPPING.put(Date.class, "datetime");
        TYPE_MAPPING.put(LocalDateTime.class, "datetime");
        TYPE_MAPPING.put(BigDecimal.class, "decimal(11,2)");
        TYPE_MAPPING.put(Double.class, "double(11,2)");
        TYPE_MAPPING.put(Float.class, "float(11,2)");
        TYPE_MAPPING.put(Long.class, "bigint");
        TYPE_MAPPING.put(String.class, "varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL");
        TYPE_MAPPING.put(Integer.class, "int(11)");
        TYPE_MAPPING.put(Boolean.class, "tinyint(2) DEFAULT 0");
    }

    /**
     * 获取表名
     *
     * @param tClass Class类
     * @return 表名
     */
    public static <T> String getTableName(Class<T> tClass) {
        TableName annotation = tClass.getAnnotation(TableName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return DEFAULT_TABLE_NAME;
    }

    /**
     * 获取字段和数据库字段Map映射
     *
     * @param tClass 数据库实体
     * @return 实体对应数据库字段
     */
    public static <T> Map<String, String> getEntitySQLColumnMap(Class<T> tClass) {
        Map<String, String> columns = new HashMap<>();
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            TableField tableField = declaredField.getAnnotation(TableField.class);
            if (tableField == null) {
                columns.put(declaredField.getName(), declaredField.getName());
                continue;
            }
            String value = tableField.value();
            if (StringUtils.hasText(value)) {
                columns.put(declaredField.getName(), value);
            }
        }

        return columns;
    }


    /**
     * 获取实体上数据库字段List
     *
     * @param tClass 数据库实体
     * @return 数据库字段
     */
    public static <T> List<String> getEntitySQLColumn(Class<T> tClass) {
        List<String> columns = new ArrayList<>();
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            TableField tableField = declaredField.getAnnotation(TableField.class);
            if (tableField == null) {
                continue;
            }
            String value = tableField.value();
            if (StringUtils.hasText(value)) {
                columns.add(value);
            }
        }

        return columns;
    }


    /**
     * 获取实体上数据库字段List
     *
     * @param tClass 数据库实体
     * @return 数据库字段
     */
    public static <T> List<String> getEntityAddColumnSQL(Class<T> tClass) {
        List<String> columns = new ArrayList<>();
        String tableName = getTableName(tClass);
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            TableField tableField = declaredField.getAnnotation(TableField.class);
            if (tableField == null) {
                continue;
            }
            String value = tableField.value();
            if (StringUtils.hasText(value)) {
                StringBuilder addColumnSQL = new StringBuilder();
                String sqlLine = String.format("alter table %s add column %s ", tableName, value);

                // 添加字段
                addColumnSQL.append(sqlLine);
                String sqlType = TYPE_MAPPING.get(declaredField.getType());
                if (sqlType == null) {
                    sqlType = TYPE_MAPPING.get(String.class);
                }
                addColumnSQL.append(sqlType);
                // 添加注释
                ApiModelProperty apiModelProperty = declaredField.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty != null) {
                    addColumnSQL.append(String.format(" COMMENT '%s'", apiModelProperty.value()));
                }
                addColumnSQL.append(";");
                columns.add(addColumnSQL.toString());
            }
        }

        return columns;
    }


    /**
     * 反射拆取字段
     *
     * @param tClass         泛型类
     * @param includeColumns 只提取的字段，可以为空，设置后只返回该字段集合中的字段
     * @param excludeColumns 排除的字段，可以为空，设置后返回的字段不包含集合中的字段
     * @param <T>            泛型类
     * @return 字段集合
     */
    public static <T> List<Field> getFields(Class<T> tClass, List<String> includeColumns, List<String> excludeColumns) {
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        if (fields.length < 1) {
            return fieldList;
        }
        fieldList = Arrays.stream(fields).filter(v -> {
            // 如果不包含在字段中则直接过滤掉
            if (includeColumns != null && !includeColumns.isEmpty() && !includeColumns.contains(v.getName())) {
                return false;
            }
            // 如果在包含在排除字段中过滤掉
            if (excludeColumns != null && !excludeColumns.isEmpty() && excludeColumns.contains(v.getName())) {
                return false;
            }
            v.setAccessible(true);
            return true;
        }).collect(Collectors.toList());
        return fieldList;
    }

    /**
     * 根据List生成InsertSql
     *
     * @param list           集合元数据
     * @param tClass         对象Class类
     * @param excludeColumns 排除的字段
     * @param <T>            泛型类
     * @return List字符串sql
     */
    public static <T> List<String> genListInsertSql(List<T> list, Class<T> tClass, List<String> excludeColumns) {
        return genListInsertSql(null, list, tClass, null, excludeColumns);
    }

    /**
     * 根据List生成InsertSql
     *
     * @param tableName 表名
     * @param list      集合元数据
     * @param tClass    对象Class类
     * @param <T>       泛型类
     * @return List字符串sql
     */
    public static <T> List<String> genListInsertSql(String tableName, List<T> list, Class<T> tClass) {
        return genListInsertSql(tableName, list, tClass, null, null);
    }

    /**
     * 根据List生成InsertSql
     *
     * @param tableName      表名
     * @param list           集合元数据
     * @param tClass         对象Class类
     * @param includeColumns 只需要使用的字段
     * @param excludeColumns 排除的字段
     * @param <T>            泛型类
     * @return List字符串sql
     */
    public static <T> List<String> genListInsertSql(String tableName, List<T> list, Class<T> tClass, List<String> includeColumns, List<String> excludeColumns) {
        List<String> sqlList = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return sqlList;
        }

        Map<String, String> columnMap = getEntitySQLColumnMap(tClass);
        List<Field> fields = getFields(tClass, includeColumns, excludeColumns);
        if (fields.isEmpty()) {
            return sqlList;
        }

        // 获取表名
        String tableNameStr = tableName == null ? getTableName(tClass) : tableName;

        // 获取字段
        List<String> sqlColumns = fields.stream().map(v -> columnMap.get(v.getName())).collect(Collectors.toList());
        String sqlColumnsStr = String.join(",", sqlColumns);

        for (T obj : list) {
            // 获取Value字段List
            List<String> valueList = fields.stream().map(v -> {
                Object o = null;
                try {
                    o = v.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return getFieldValueStr(o);
            }).collect(Collectors.toList());

            String valueStr = String.join(",", valueList);
            String sqlStr = String.format("insert into %s (%s) values(%s);", tableNameStr, sqlColumnsStr, valueStr);
            sqlList.add(sqlStr);
        }
        return sqlList;
    }

    /**
     * 将字段转为SQL字符串
     *
     * @param obj 根据Object提取的字段Filed对象
     * @return sql字符串
     */
    public static String getFieldValueStr(Object obj) {
        String val = "null";
        if (obj == null) {
            return val;
        }
        if (obj instanceof String || obj instanceof Character ) {
            val = "'" + obj.toString() + "'";
        } else if (obj instanceof Number) {
            val = DECIMAL_FORMAT.format(obj);
        } else if (obj instanceof Boolean) {
            val = Boolean.TRUE.equals(obj) ? "1" : "0";
        } else if (obj instanceof Date) {
            val = "'" + DATE_TIME_FORMATTER.format((Date) obj) + "'";
        } else if (obj instanceof LocalDateTime) {
            val = "'" + LOCAL_DATE_TIME_FORMATTER.format((LocalDateTime) obj) + "'";
        } else if (obj instanceof LocalDate) {
            val = "'" + LOCAL_DATE_TIME_FORMATTER.format(((LocalDate) obj).atStartOfDay()) + "'";
        } else {
            throw  new IllegalArgumentException("不支持的数据类型:" + obj.getClass());
        }
        return val;
    }

    public static void main(String[] args) {
        System.out.println(String.format("表名称:%s", getTableName(TestBean.class)));
        System.out.println(String.format("字段:%s", String.join(",", getEntitySQLColumn(TestBean.class))));
        System.out.println(String.format("新增字段SQL:\n%s", String.join("\n", getEntityAddColumnSQL(TestBean.class))));
        getEntitySQLColumnMap(TestBean.class).forEach((k, v) -> {
            System.out.println(String.format("字段映射:%s  -->  %s", k, v));
        });

        // 打印InsertSql
        genListInsertSql(Arrays.asList(new TestBean(1, "BigKang", LocalDateTime.now()), new TestBean(2, "YellowKang", LocalDateTime.now())), TestBean.class, Arrays.asList("id")).forEach(System.out::println);
    }

    @Data
    @TableName(value = "t_test_bean")
    @ApiModel("字典信息")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestBean {

        @TableId(value = "id", type = IdType.AUTO)
        @ApiModelProperty("主键")
        private Integer id;

        @TableField("name")
        @ApiModelProperty("名称")
        private String name;

        @TableField("create_time")
        @ApiModelProperty("创建时间")
        private LocalDateTime createTime;
    }
    
}
```

## 自定义序列化器Json对象字符串互转

​		示例对象,listData 以Json方式存储

```java
@Data
// 设置自动映射
@TableName(value = "****",autoResultMap = true)
@ApiModel("案件医疗文件材料")
public class MeituanCaseMedicalFile extends Model<MeituanCaseMedicalFile> {
 
    @ApiModelProperty(value = "清单数据")
    @TableField(value = "list_data", typeHandler = ListMedicalDataTypeHandler.class)
    private List<MedicalListDataVo> listData;
  
}
```

​		自定义TypeHandler

```java

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sigreal.jiaanan.bean.vo.MedicalListDataVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ListMedicalDataTypeHandler extends BaseTypeHandler<List<MedicalListDataVo>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<MedicalListDataVo> medicalListDataVos, JdbcType jdbcType) throws SQLException {
        if (medicalListDataVos == null) {
            ps.setString(i, null);
            return;
        }
        ps.setString(i, JSON.toJSONString(medicalListDataVos));
    }

    @Override
    public List<MedicalListDataVo> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String json = resultSet.getString(s);
        if (json == null) {
            return null;
        }
        return JSONArray.parseArray(json, MedicalListDataVo.class);
    }

    @Override
    public List<MedicalListDataVo> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String json = resultSet.getString(i);
        if (json == null) {
            return null;
        }
        return JSONArray.parseArray(json, MedicalListDataVo.class);
    }

    @Override
    public List<MedicalListDataVo> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String json = callableStatement.getString(i);
        if (json == null) {
            return null;
        }
        return JSONArray.parseArray(json, MedicalListDataVo.class);
    }
}

```

## 手动注册Mapper不使用扫描包方式

```java

import jakarta.annotation.PostConstruct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuangKang
 * @date 2023/11/20 14:54:51
 * @describe MybatisPlus配置
 */
@Configuration
public class MybatisPlusConfig {


    // 在这个列表中添加所有需要注册的 Mapper 接口类
    private static final List<Class<?>> MAPPER_INTERFACES = new ArrayList<>();

    static {
        // 预先添加所有需要注册的 Mapper 接口类
        MAPPER_INTERFACES.add(UserLoginLogMapper.class);
    }

    private final SqlSessionFactory sqlSessionFactory;

    public MybatisPlusConfig(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }


    /**
     * 初始化并且注册Mapper
     */
    @PostConstruct
    public void init() {
        for (Class<?> mapperInterface : MAPPER_INTERFACES) {
            sqlSessionFactory.getConfiguration().getMapperRegistry().addMapper(mapperInterface.getClass());
        }
    }
}



```

# 配置方面

## XML配置

​		我们设置配置文件，配置扫描包路径没如果是放在Resource可以扫描到，如果放在Java代码中则需要修改Build打包文件

​		配置扫描代码中的xml文件（否则打包时，com下的包路径里面的xml扫描不到）

```xml
    <build>
          <resources>
              <resource>
                  <directory>${basedir}/src/main/java</directory>
                  <includes>
                      <include>**/*.xml</include>
                  </includes>
              </resource>
          </resources>
    </build>
```

​		配置文件扫描路径：

​			yaml版本

```properties
mybatis-plus:
  mapper-locations:
    - "classpath*:/mapper/**/*.xml"
    - "classpath*:/**/mapper/xml/*.xml"
```

​		 properties版本

```properties
mybatis-plus.mapper-locations[0]=classpath*:/mapper/**/*.xml
mybatis-plus.mapper-locations[1]=classpath*:/**/mapper/xml/*.xml
```

​		然后编写Xml文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--配置Mapper映射-->
<mapper namespace="com.topcom.mp.security.mapper.SysUserMapper">
		<!--查询语句，定义接口名称，参数类型，以及结果集映射-->
    <select id="getById" parameterType="java.lang.Long" resultType="java.util.Map">
       select * from t_sys_user where id = #{id}
    </select>
</mapper>
```

​		然后编写Mapper，调用即可

```java
public interface SysUserMapper extends BaseMpDao<SysUser, Long> {

   Map<String,Object> getById(Long id);

}
```

# MP代码生成器

直接在测试类中加入下面的代码就能生成

```java
    @Test
    public void ganerreter(){
        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        
        String projectPath = System.getProperty("user.dir");
        //生成路径
        gc.setOutputDir(projectPath + "/src/main/java");
        //作者
        gc.setAuthor("黄康");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setFileOverride(false); //重新生成时文件是否覆盖
        gc.setServiceName("%sService"); //去掉Service接口的首字母I
        //gc.setIdType(IdType.ID_WORKER); //主键策略
        gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
        //gc.setSwagger2(true);//开启Swagger2模式
        mpg.setGlobalConfig(gc);
        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/testsql");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123");                                                                         dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);
        // 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("plus"); //模块名
        pc.setParent("com.spring.boot.test.mybatis");
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);
        // 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude("t_emp");
        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        strategy.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
        strategy.setRestControllerStyle(true); //restful api风格控制器
        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
        mpg.setStrategy(strategy);
        // 6、执行
        mpg.execute();
    }
```

