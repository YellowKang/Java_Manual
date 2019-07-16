# 首先我们先引入依赖

```
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

```
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

![](img\jpa-1.png)

这个参数就是我们在查询的时候可以用来动态查询封装一些条件的，那么这个参数是如何创建的呢，如下所示

我们这里以lambda表达式来编写这个条件

```
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

```
List<T> all = baseService.findAll(specification);
```

## Jpa原生复杂查询

### 使用sql

首先我们需要注入EntityManager，然后使用EntityManager查询





```
        Query query = baseMg.createNativeQuery("select * from t_test_jpa");
```



### 使用代码