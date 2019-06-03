# 引入依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
```

# 编写配置

这里我们写入es的地址9300端口，然后配置它的集群名称，这个可以在es配置文件查看，默认elasticsearch

yml版本：

```
spring:
	data:
      elasticsearch:
        cluster-nodes: 111.67.196.127:9300
        repositories:
          enabled: true
        cluster-name: "docker-cluster"
```

properties版本：

```
spring.data.elasticsearch.cluster-nodes=111.67.196.127:9300
spring.data.elasticsearch.repositories.enabled=true
spring.data.elasticsearch.cluster-name=docker-cluster
```

# 编写代码

## 编写实体类

我们引入lombok简化代码，不写getset

我们编写一个测试实体类

这里的Document就是一个索引，它的索引名称为testes，类型为test，然后它的id为String类型，并且加上id注解，然后加上CreatedDate创建时时间，  @LastModifiedDate为修改时间，    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")表示字段的分词配置，需要分词，    @Field(index = true)表示这个字段需要添加索引

```
@Data
@Document(indexName = "testes",type = "test")
public class TestEs {
    @Id
    private String id;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;

    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String name;
    
    private String email;
    
    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String address;
    
    @Field(index = true)
    private Integer age;
    
    private String url;
    
    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String title;

}
```

## 编写Dao层

这里只需要继承esRepository，然后定义实体类型以及主键类型

```
public interface TestEsDao extends ElasticsearchRepository<TestEs, String>  {
}
```

然后我们就能直接使用Dao了

（这里为测试方便，真实开发请不要偷懒，还是要写service和impl）

```

   	@Autowired
    protected TestEsDao baseDao;
    
    //根据id查询
    public TestEs findById(PK id) {
        return this.baseDao.findById(id).get();
    }
    
    //修改和添加同一方法
    public TestEs save(TestEs entity) {
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

