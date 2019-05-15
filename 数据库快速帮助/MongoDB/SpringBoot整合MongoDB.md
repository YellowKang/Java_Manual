# 首先创建数据库以及用户

进入mongo，创建test数据库，以及用户

```
use test

创建一个用户，用户名test密码test，为这个数据库的管理员，这个用户属于test这个数据库
db.createUser({user:"test",pwd:"test",roles:[{role:'userAdmin',db:'test'}]})
然后我们去SpringBoot中进行整合
```

# 引入依赖

lombok为了简化代码使用《懒得写get，set方法》

```
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.4</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
```

# 编写配置

yml版本

mongodb://用户名:用户密码@ip地址:端口号/数据库

我们假设一个test用户密码为test，ip为192.168.0.1，端口号为27017，数据库为test

```
spring:
  data:
    mongodb:
      uri: mongodb://test:test@192.168.0.1:27017/test
```

properties版本

```
spring.data.mongodb.uri=mongodb://test:test@192.168.0.1:27017/test
```

# 编写代码实现

编写实体类，Document为文档类似MySQL的表名，可以自动创建，id为主键id，随着创建而创建自动生成

```
@Data
@ToString
@Document("testmongo")
public class TestMongo {

    @Id
    @CreatedBy
    private String id;

    private String name;

    private String email;
}
```

编写Dao层，继承MongoRepository，后面类型为主键类型

```
public interface TestMongoDao extends MongoRepository<TestMongo,String> {
}
```

为了快速开始不编写service以及impl实现，直接controller调用

先编写几个简单的查询，新增和删除

```
@RestController
public class TestController {
    
    @Autowired
    private TestMongoDao dao;

    @GetMapping("findAll")
    public List<TestMongo> findAll(){
       return dao.findAll();
    }

    @GetMapping("save")
    public TestMongo save(TestMongo testMongo){
        TestMongo insert = dao.insert(testMongo);
        return insert;
    }

    @GetMapping("deleteById")
    public String deleteById(String id){
        dao.deleteById(id);
        return "删除成功！";
    }

}
```

这样我们直接可以通过下面的连接来访问了

http://localhost:8080/findAll     										查询所有

http://localhost:8080/deleteById?id=1								根据id删除

http://localhost:8080/save?name=bigkang&email=bigkangsix@qq.com	新增

简单的整合就完成了

# SpringDataAPI操作

