# 什么是Mybatis-Plus

​		为简化开发而生 ，只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑。 只需简单配置，即可快速进行 CRUD 操作，从而节省大量时间。 热加载、代码生成、分页、性能分析等功能一应俱全。 

# 快速开始

## 先创建数据库数据

```
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

```
        //依赖版本可以修改成新版本
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.0.6</version>
        </dependency>
		//Lombok插件
    	<dependency>
        	<groupId>org.projectlombok</groupId>
        	<artifactId>lombok</artifactId>
        	<optional>1.18.0</optional>
    	</dependency>
    	//连接驱动
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        //连接池
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        //测试包
      	<dependency>
        	<groupId>org.springframework.boot</groupId>
        	<artifactId>spring-boot-starter-test</artifactId>
        	<scope>test</scope>
    	</dependency>

```

## 编写配置文件

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test_mybatis_plus?characterEncoding=UTF-8&useSSL=false
    username: bigkang
    password: bigkang
```

## 扫描包注解

```
@MapperScan("com.atguigu.mybatis_plus.mapper")
```

## 编写实体类

```
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

## 创建Mapper接口

```
public interface UserMapper extends BaseMapper<User> {
    
}
```

## 代码进行查询

```
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

```
#mybatis日志
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```



# 增删改查（条件构造器进行强化）

## 增加

### 单个增加

```
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

```
testMapper.deleteById(1);
```

### 批量按id删除

```
        List<Integer> deleteIds = new ArrayList<>();
        deleteIds.add(1);
        deleteIds.add(5);
        int i = testMapper.deleteBatchIds(deleteIds);
```

### 按条件删除

```
        Map<String,Object> map = new HashMap<>();
        //map的键为数据库的字段名
        map.put("email", "bigkang@qq.com");
        testMapper.deleteByMap(map);
```



## 修改

先查询出来然后修改名字《注。因为按主键进行修改所以要查询，并且如果null值的列他是不会修改的》

```
       User user = testMapper.selectById(12);
       user.setName("社会康");
       testMapper.updateById(user);
```

## 查询

### 按id查询单个

查询id为1的用户

```
User user1 = testMapper.selectById(1);
```

### 查询所有

查询所有用户（条件为空）

```
 List<User> user1 = testMapper.selectList(null);
```

### 按条件查询多个

查询年龄为18的用户

```
Map<String,Object> map = new HashMap<>();
map.put("age","18");
List<User> users = testMapper.selectByMap(map);
```

# 条件构造器



# 配置方面

主键生成

乐观锁

属性绑定

# MP代码生成器

直接在测试类中加入下面的代码就能生成

```
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

