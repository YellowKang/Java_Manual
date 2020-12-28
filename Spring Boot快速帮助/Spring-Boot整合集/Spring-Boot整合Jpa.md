# 首先我们先引入依赖

```xml
<!--Spring-Boot版本-->
<parent>
   	 	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-parent</artifactId>
    	<version>2.0.6.RELEASE</version>
    	<relativePath/>
</parent>
<dependencies>
			<!--Spring-BootWeb的依赖-->
    	<dependency>
        		<groupId>org.springframework.boot</groupId>
        		<artifactId>spring-boot-starter-web</artifactId>
    	</dependency>

			<!--Spring-Boot测试的依赖-->
    	<dependency>
        		<groupId>org.springframework.boot</groupId>
        		<artifactId>spring-boot-starter-test</artifactId>
        		<scope>test</scope>
    	</dependency>
	
    	<!--Lombok插件-->
    	<dependency>
        		<groupId>org.projectlombok</groupId>
        		<artifactId>lombok</artifactId>
        		<version>1.18.0</version>
    	</dependency>

			<!--Spring-Boot整合的Jpa-->
			<dependency>
        		<groupId>org.springframework.boot</groupId>
        		<artifactId>spring-boot-starter-data-jpa</artifactId>
    	</dependency>

			<!--Mysql和SpringBoot的适配版本的连接驱动-->
    	<dependency>
        		<groupId>mysql</groupId>
        		<artifactId>mysql-connector-java</artifactId>
    	</dependency>

			<!--德鲁伊连接池-->
    	<dependency>
        		<groupId>com.alibaba</groupId>
        		<artifactId>druid</artifactId>
        		<version>1.1.10</version>
    	</dependency>	
</dependencies>
```

# 编写配置

```properties
spring:
  datasource:
    password: bigkang #密码
    url: jdbc:mysql://127.0.0.1:3306/test?useSSL=false&useUnicode=true&characterEncoding=utf-8 #数据库连接
    username: root #用户名
    driver-class-name: com.mysql.jdbc.Driver #连接驱动
  jpa:
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect #使用innoDb引擎
    hibernate:
        ddl-auto: update #每次启动如果发现有实体类更新数据库
    show-sql: true #显示sql
    database: mysql #数据库类型
```

# 编写实体类

```java
package club.ClubKang.www.base.pojo;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
//Lombok的插件注解（自动添加get和set方法）
@Data
//定义表的名称（和数据库对应的表名）
@Table(name="tb_label")
//标识实体类
@Entity
public class Label {
//    标识主键id
    @Id
//    注：表字段应该和属性名称一致否则需要配置字段名称
    private String id;
    private String labelname;
    private String state;
    private Long count;
    private String recommend;
    private Long fans;
}
```

# 编写DAO层

```java
package club.ClubKang.www.base.dao;

import club.ClubKang.www.base.pojo.Label;
import org.springframework.data.jpa.repository.JpaRepository;

//直接暴力继承jpa方法定义表的实体类型映射和主键类型
public interface LabelDao extends JpaRepository<Label,String> {

}

```

剩下的就是业务层接口的编写了

```java
package club.ClubKang.www.base.service;
import club.ClubKang.www.base.dao.LabelDao;
import club.ClubKang.www.base.pojo.Label;
import club.kang.www.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@Transactional
public class LabelService {
//注入Dao层，调用数据库连接
@Autowired
private LabelDao labelDao;
//注入ID生成雪花算法（可以不用写）
@Autowired
private IdWorker idWorker;
//    获取所有的数据
    public List<Label> findAll(){
        return labelDao.findAll();
    }
//    根据id获取数据
    public Label findById(String id){
        return labelDao.findById(id).get();
    }
//    添加数据，自动生成ID插入
    public void save(Label label){
        label.setId(idWorker.nextId()+"");
        labelDao.save(label);
    }
//    更新数据，和添加同一方法，如果数据库中有对应的id将会修改
    public void update(Label label){
        labelDao.save(label);
    }
//    根据id删除
    public void deleteByid(String id){
        labelDao.deleteById(id);
    }
}
```

# 数据进行操作

```java
package club.ClubKang.www.base.controller;
import club.ClubKang.www.base.pojo.Label;
import club.ClubKang.www.base.service.LabelService;
import club.kang.www.entity.Result;
import club.kang.www.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/label")
@RestController
public class BaseController {

    @Autowired
    private LabelService labelService;
    
    @GetMapping("/")
    public Result result(){
        return new Result(true,StatusCode.OK,"查询成功！",labelService.findAll());
    }

    @GetMapping("/{labelId}")
    public Result resultByid(@PathVariable("labelId") String labelId){
        return new Result(true,StatusCode.OK,"查询成功！",labelService.findById(labelId));
    }

    @PostMapping("/")
    public Result resultAdd(@RequestBody Label label){
        labelService.save(label);
        return new Result(true,StatusCode.OK,"添加成功！");
    }

    @PutMapping("/{labelId}")
    public Result resultUpdate(@PathVariable("labelId") String labelId,@RequestBody Label label){
        label.setId(labelId);
        labelService.update(label);
        return new Result(true,StatusCode.OK,"修改成功！");
    }

    @DeleteMapping("/{labelId}")
    public Result resultUpdate(@PathVariable("labelId") String labelId){
        labelService.deleteByid(labelId);
        return new Result(true,StatusCode.OK,"删除成功！");
    }
}
```

# 复杂查询

## Jpa自带接口条件查询

在Jpa中的复杂查询分为很多种，那么我们先来用jpa封装好的吧

我们常常在分页查询中会用到各种条件，但是Jpa其实是给我们封装好了一部分的多条件查询的

例如Jpa的findAll，他有个参数，如下图所示

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1566791041411.png)

这个参数就是我们在查询的时候可以用来动态查询封装一些条件的，那么这个参数是如何创建的呢，如下所示

我们这里以lambda表达式来编写这个条件

```java
        Specification<T> specification = (root,query,buider) -> {
        	  //创建查询条件集合
            List<Predicate> predicateList = new CopyOnWriteArrayList<>();
            //查询id不等于1
            predicateList.add(buider.notEqual(root.get("id"),1));
            //查询年龄大于18的
            predicateList.add(buider.ge(root.get("age"),18));
            
            //将条件作为数组放入query，然后返回
            return query.where(predicateList.toArray(new Predicate[predicateList.size()])).getRestriction();
        };
```

然后再调用我们的方法即可，查询出想要的数据，这里的T为实体类

```java
List<T> all = baseService.findAll(specification);
```

## Jpa原生复杂查询

### 使用sql

首先我们需要注入EntityManager，然后使用EntityManager查询

```sql
        Query query = baseMg.createNativeQuery("select * from t_test_jpa");
```

### 使用代码

# Jpa关系映射

​		在我们日常开发中经常遇到连表查询，那么我们在连表查询的时候在Jpa中是尽量要避免写sql的，但是我们通过条件构造器去进行查询的话会很麻烦，那么我们怎么使用呢，这个时候就需要我们的关系映射了，举个例子，我们这有一个班级表，那么这个班级表中肯定会有学生，那么学生和班级相对应的就是一个班级对应多个学生，班级和学生是相关联的，那么我们在查询班级的时候就想将关联的学生都查询出来，那么我们只需要加上几个注解即可完成我们的功能。

## 核心注解

@OneToOne

```java
#属性
targetEntity						//属性表示默认关联的实体类型，默认为当前标注的实体类；
cascade									//属性表示与此实体一对一关联的实体的联级样式类型。联级样式上当对实体进行操作时的策略。
												不定义,则对关系表不会产生任何影响
												CascadeType.PERSIST 	（级联新建）
												CascadeType.REMOVE 		（级联删除）
												CascadeType.REFRESH 	（级联刷新）
												CascadeType.MERGE 		（级联更新）
												CascadeType.ALL 			 表示选择全部四项
fetch										//属性是该实体的加载方式，有两种：LAZY和EAGER。
optional								//属性表示关联的实体是否能够存在null值。默认为true，表示可以存在null值。如果为false，则要同时配合使用@JoinColumn标记。
mappedBy								//属性用于双向关联实体时，标注在不保存关系的实体中。
orphanRemoval						//此属性表示是否级联删除，效果与上方CascadeType.REMOVE相同
```

@OneToMany

```java
#属性
targetEntity						//属性表示默认关联的实体类型，默认为当前标注的实体类；
cascade									//属性表示与此实体一对一关联的实体的联级样式类型。联级样式上当对实体进行操作时的策略。
												不定义,则对关系表不会产生任何影响
												CascadeType.PERSIST 	（级联新建）
												CascadeType.REMOVE 		（级联删除）
												CascadeType.REFRESH 	（级联刷新）
												CascadeType.MERGE 		（级联更新）
												CascadeType.ALL 			 表示选择全部四项
fetch										//属性是该实体的加载方式，有两种：LAZY和EAGER。
optional								//属性表示关联的实体是否能够存在null值。默认为true，表示可以存在null值。如果为false，则要同时配合使用@JoinColumn标记。
mappedBy								//属性用于双向关联实体时，标注在不保存关系的实体中。
orphanRemoval						//此属性表示是否级联删除，效果与上方CascadeType.REMOVE相同
```

@ManyToOne

```java
#属性
targetEntity						//属性表示默认关联的实体类型，默认为当前标注的实体类；
cascade									//属性表示与此实体一对一关联的实体的联级样式类型。联级样式上当对实体进行操作时的策略。
												不定义,则对关系表不会产生任何影响
												CascadeType.PERSIST 	（级联新建）
												CascadeType.REMOVE 		（级联删除）
												CascadeType.REFRESH 	（级联刷新）
												CascadeType.MERGE 		（级联更新）
												CascadeType.ALL 			 表示选择全部四项
fetch										//属性是该实体的加载方式，有两种：LAZY和EAGER。
optional								//属性表示关联的实体是否能够存在null值。默认为true，表示可以存在null值。如果为false，则要同时配合使用@JoinColumn标记。
```

@ManyToMany

```java
#属性
targetEntity						//属性表示默认关联的实体类型，默认为当前标注的实体类；
cascade									//属性表示与此实体一对一关联的实体的联级样式类型。联级样式上当对实体进行操作时的策略。
												不定义,则对关系表不会产生任何影响
												CascadeType.PERSIST 	（级联新建）
												CascadeType.REMOVE 		（级联删除）
												CascadeType.REFRESH 	（级联刷新）
												CascadeType.MERGE 		（级联更新）
												CascadeType.ALL 			 表示选择全部四项
fetch										//属性是该实体的加载方式，有两种：LAZY和EAGER。
mappedBy								//属性用于双向关联实体时，标注在不保存关系的实体中。
```

@JoinColumn

```java
name												//指定该外键列的列名《当前类的所对应的mysql字段名》
referencedColumnName				//指定该外列所参照的主键列的列名
unique											//该字段是否添加唯一约束，默认不添加
nullable										//是否允许为空，默认允许空
insertable									//指定该列是否包含在Hibernate生成的insert语句的列表中。默认true
updatable										//指定该列是否包含在Hibernate生成的update语句的列表中。
columnDefinition						//指定Hibernate使用该属性值指定的SQL片段来创建外键列
table												//指定该列所在数据表的表名
foreignKey									//所建立的外键名称
```

@JoinTable

```java
name												//新建中间表表名
catalog											//属性表示实体指定点目录名称或数据库名称
schema											//属性表示实体指定点目录名称或数据库名称
joinColumns									//该属性值可接受多个@JoinColumn，用于配置连接表中外键列的信息，这些外键列参照当前实体对应表的主键列
inverseJoinColumns					//与joinColumn类似，它保存的是保存关系的另外一个外键字段
foreignKey									//建立外键名称
inverseForeignKey						//与foreignKey类似
uniqueConstraints						//属性是否添加唯一约束
indexes											//该属性值为@Index注解数组，用于为该连接表定义多个索引
```

## 关系映射正确使用方法

### @OneToOne

通常用于一个用户对应一个用户的详情，例如User对UserInfo，我们可以把UserInfo的字段也放在User中，但是我们想把它拆分成两张表，并且我们在删除User的时候级联删除掉UserInfo

首先我们需要确认一个问题，这个关联关系是由User来保存还是由UserInfo进行保存，也就是我们在User中新增一个info_id，还是在UserInfo中新增一个user_id让他们关联起来。

首先我们先使用第一种这个关系由user来保存

我们在User中新增UserInfo，ALL表示我们可以通过User来删除创建修改Info

```java
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "info_id",referencedColumnName = "id")
    private UserInfo userInfo;
```

那么我们在UserInfo中就不需要保存了



那么我们再来试试将关系维护放在Info端，User端的代码就不需要@JoinColumn了，我们在User类中的Info使用mappedBy然后属性名，表示我们将关系交给info类中的user来进行关系维护。

```
    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    @JsonManagedReference
    private UserInfo userInfo;
```

我们再来看一看Info端的代码，我们可以看到我们在User中也引用了一个OneToOne然后关联user中Id

```java
    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
```



```java
@JsonManagedReference
@JsonBackReference
```

这两个注解帮助我们解决查询嵌套的问题，因为我们的User中有Info，Info中又引用了User所以会引起无限递归，所以我们在User加上JsonManagedReference，在Info中使用JsonBackReference

### @OneToMany

一对多的话我们就只能将关系放在Info端进行维护了，示例如下。User类代码如下

```
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    @JsonManagedReference
    private List<UserInfo> userInfo;
```

Info类代码如下

```
@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
@JsonBackReference
@JoinColumn(name = "user_id", referencedColumnName = "id")
private User user;
```

我们在User端放弃维护关系，将关系都保存在Info中

### @ManyToMany



### 级联查询优化

我们在级联查询的时候，我们发现他在查询的时候竟然是分开两次进行查询的那么我们肯定需要优化一下，让他只执行一次sql，首先我们在实体类上加上注解NamedEntityGraph，然后name属性设置一个名字，再设置级联的属性节点，这里用的是属性名字，我们写上UserInfo。

```java
@NamedEntityGraph(name = "User.BYINFO",attributeNodes = {@NamedAttributeNode("userInfo")})
public class User extends BaseJpaEntity {
  
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    @JsonManagedReference
    private List<UserInfo> userInfo;

}
```

然后我们再去Dao层方法上加上注解加上，在Dao层的方法上加上EntityGraph注解即可，再次查询发现sql变成了一条。

```
    @Override
    @EntityGraph(value = "User.BYINFO", type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findById(Long var1);
```

### 级联反向查询

通常我们通过用户关联多个部门，那么我们又如何根据这个部门查询多个用户呢。

这是User中的多个Group的多对多的关系映射。

```
   	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "t_user_t_group", joinColumns = {@JoinColumn(name = "users_ID")},
            inverseJoinColumns = {@JoinColumn(name = "groups_ID")})
    private Set<Group> groups;
```

那么我们怎么根据这个Group来进行查询呢，我们使用findByGroupIs

```
    List<User> findByGroupsIs(Group var1);
```

如果使用条件构造器则试下下面的Join关联查询。

### Jpa多表条件构造

​		通过

```java
    //两张表关联查询
    Join<User, Role> roleJoin = root.join(root.getModel().getSet("roles", Role.class), JoinType.LEFT);
    predicate.add(cb.like(roleJoin.get("roleName"),"%管理员%"));
```

​		首先我们需要查询用户，所以Join的实体是User，以及Role，然后我们从root中获得模型，再从模型中设置角色，然后Join的类型为左连接。

​		然后add我们的查询条件为角色包含管理员的角色关联的用户，将用户查询出来。

​		双层关联，用户关联角色，角色关联角色信息，我们想通过角色信息来进行查询，直接使用roleInfo即可。

```java
		Join<User, Role> roleJoin = root.join(root.getModel().getSet("roles", Role.class), JoinType.LEFT);
		Join<Object, RoleInfo> join = roleJoin.join("roleInfo", JoinType.LEFT);
```

