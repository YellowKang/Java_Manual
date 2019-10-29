# 引入依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
```

# 编写配置

这里我们写入mongo的地址端口，然后配置它的连接url，和密码

yml版本：

```
spring:
  data:
    mongodb:
      uri: mongodb://bigkang:bigkang@39.106.158.23:27017/test
```

properties版本：

```
spring.data.mongodb.uri=mongodb://bigkang:bigkang@39.106.158.23:27017/test
```

# 编写代码

## 编写实体类

我们引入lombok简化代码，不写getset

我们编写一个测试实体类

这里的Document就是文档，文档的集合为test，@id标识为id，@create Date为创建时添加时间，@LastModifiedDate为修改时间

```

@Data
@ToString
@Document(collection = "test")
public class TestMongo{

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 创建时间
     */
    @CreatedDate
    private Date createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    private Date updateTime;

    private String username;
    private String email;
    private String birthday;
    private String password;
    private Integer age;
    private String address;
    private String phone;
}
```

## 编写Dao层

这里只需要继承esRepository，然后定义实体类型以及主键类型

```
public interface TestMongoDao extends MongoRepository<TestMongo, String>  {
}
```

然后我们就能直接使用Dao了

（这里为测试方便，真实开发请不要偷懒，还是要写service和impl）

```
   	@Autowired
    protected TestMongoDao baseDao;
    
    //根据id查询
    public TestEs findById(PK id) {
        return this.baseDao.findById(id).get();
    }
    
    //修改和添加同一方法
    public TestEs save(TestMongo entity) {
        return  this.baseDao.save(entity);
    }

	//根据主键删除
    public void delete(String pk) {
        this.baseDao.deleteById(pk);
    }
    
    //分页查询
    public Page<TestEs> findAll(Pageable pageable) {
        return this.baseDao.findAll(pageable);
    }
```

# MongoJpa进阶

在很多时候其实简单的接口查询并不能满足我们所以我们可以使用mongoTemplate来进行查询，

