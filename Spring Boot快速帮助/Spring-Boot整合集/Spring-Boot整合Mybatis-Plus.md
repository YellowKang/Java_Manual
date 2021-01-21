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
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
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

