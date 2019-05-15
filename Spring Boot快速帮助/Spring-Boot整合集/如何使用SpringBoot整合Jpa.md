



```
springboot2.0.6

druid1.1.10  连接池

mysql整合boot连接驱动

springbootweb整合

springbootjpa整合

首先我们先添加依赖

插件我们使用lombok来快速开发（注：可以不使用，lombok插件安装有Bug谨慎安装）
```

## 首先我们先引入依赖

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

?	
	server:
	设置端口号
	  port: 8011
	spring:
	  application:
	定义项目名称
	  name: ClubKang-Base
	  datasource:
	数据库连接驱动
	url: jdbc:mysql://localhost:3306/tensquare_base?characterEncoding=utf-8&useSSL=false
	数据库连接用户名
	username: root
	数据库连接密码
	password: 123
	数据库连接驱动类型
	type: com.alibaba.druid.pool.DruidDataSource
	  jpa:
	使用的数据库
	database: mysql
	是否启用操作日志
	show-sql: true
	是否跟随服务开启而创建
	generate-ddl: true

## 编写实体类

```
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

package club.ClubKang.www.base.dao;

import club.ClubKang.www.base.pojo.Label;
import org.springframework.data.jpa.repository.JpaRepository;

//直接暴力继承jpa方法定义表的实体类型映射和主键类型
public interface LabelDao extends JpaRepository<Label,String> {

}

```
剩下的就是业务层接口的编写了
```



```
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

## 数据进行操作

```
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
```

```
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