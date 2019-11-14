# 引入依赖

```xml
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

注意，这里填写地址为通信地址不是rest地址，并且注意cluster-name，必须和es的一样

yml版本：

```properties
spring:
	data:
      elasticsearch:
        cluster-nodes: 111.67.196.127:9300
        repositories:
          enabled: true
        cluster-name: "docker-cluster"
```

properties版本：

```properties
spring.data.elasticsearch.cluster-nodes=111.67.196.127:9300
spring.data.elasticsearch.repositories.enabled=true
spring.data.elasticsearch.cluster-name=docker-cluster
```

# 编写代码

### 编写实体类

我们引入lombok简化代码，不写getset

我们编写一个测试实体类

这里的Document就是一个索引，它的索引名称为testes，类型为test，然后它的id为String类型，并且加上id注解，然后加上CreatedDate创建时时间，  @LastModifiedDate为修改时间，    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")表示字段的分词配置，需要分词，    @Field(index = true)表示这个字段需要添加索引

```java
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

### 编写Dao层

这里只需要继承esRepository，然后定义实体类型以及主键类型

```java
public interface TestEsDao extends ElasticsearchRepository<TestEs, String>  {
}
```

然后我们就能直接使用Dao了

（这里为测试方便，真实开发请不要偷懒，还是要写service和impl）

```java

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

# 常用注解

### @Document

这个注解加在类上，此注解字段为：

```java
String indexName();						//索引库名称
			
String type() default "";			//文档类型名称

boolean useServerConfiguration() default false; 	//是否使用服务配置

short shards() default 5;			//默认分区5

short replicas() default 1;		//默认备份1

String refreshInterval() default "1s";	//默认刷新间隔，1秒

String indexStoreType() default "fs";		//索引文件存储类型

boolean createIndex() default true;			//是否创建索引

VersionType versionType() default VersionType.EXTERNAL;  //设置版本，默认为1
```

### @Field

```java
FieldType type() default FieldType.Auto;			//字段类型，默认自动映射，可以手动指定

boolean index() default true;									//是否创建归属性索引，默认true

DateFormat format() default DateFormat.none;	//时间格式化，默认不格式化

String pattern() default "";									//

boolean store() default false;								//是否存储

boolean fielddata() default false;						//

String searchAnalyzer() default "";						//查询时使用的分词器

String analyzer() default "";									//存储时指定的分词器

String normalizer() default "";

String[] ignoreFields() default {};						//需要忽略的字段

boolean includeInParent() default false;			//是否解析

String[] copyTo() default {};
```

### @Mapping

​			我们在创建索引后需要创建相应的实体映射，比如指定字段分词类型长度等等都需要用到，mapping一旦创建之后无法修改类型，我们只能新增而不能修改，所以一旦涉及到mapping修改的话是需要删除索引库，然后重新创建索引库的

```java
@Mapping(mappingPath = "articlesearch_mapping.json")	//指定mapping的目录，为resource目录下
```

如果我们安装并且制定ik分词，如果是注解方式的没有生效，那么必须制定mapping分词，下面就是通过mapping模板创建映射，从而进行分词

```properties
{
  "properties": {
    "address": {
      "type": "text",
      "analyzer": "ik_max_word",
      "search_analyzer": "ik_max_word",
      "fields": {
        "keyword": {
          "type": "keyword",
          "ignore_above": 256
        }
      }
    },
    "age": {
      "type": "long"
    },
    "createTime": {
      "type": "long"
    },
    "email": {
      "type": "text",
      "analyzer": "ik_max_word",
      "search_analyzer": "ik_max_word",
      "fields": {
        "keyword": {
          "type": "keyword",
          "ignore_above": 256
        }
      }
    },
    "name": {
      "type": "text",
      "analyzer": "ik_max_word",
      "search_analyzer": "ik_max_word",
      "fields": {
        "keyword": {
          "type": "keyword",
          "ignore_above": 256
        }
      }
    },
    "title": {
      "type": "text",
      "analyzer": "ik_max_word",
      "search_analyzer": "ik_max_word",
      "fields": {
        "keyword": {
          "type": "keyword",
          "ignore_above": 256
        }
      }
    },
    "updateTime": {
      "type": "long"
    },
    "url": {
      "type": "text",
      "fields": {
        "keyword": {
          "type": "keyword",
          "ignore_above": 256
        }
      }
    }
  }
}
```



# 条件构造器

在我们很多的时候，单纯的使用spring data的接口开发无法满足我们的需求，所以我们需要进行一些复杂的实现



# WebFlux整合Elasticsearch

引入依赖，注意此处引入webflux依赖而不是web

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
				<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>
```

编写配置文件

yaml版本

```properties
spring:
  data:
    elasticsearch:
      client:
        reactive:
          endpoints: ["114.67.80.169:9201","114.67.80.169:9202","114.67.80.169:9203"]
```

properties版本

```properties
spring.data.elasticsearch.client.reactive.endpoints[0]=114.67.80.169:9201
spring.data.elasticsearch.client.reactive.endpoints[1]=114.67.80.169:9202
spring.data.elasticsearch.client.reactive.endpoints[2]=114.67.80.169:9203
```

编写实体类

```java
@Data
@Document(indexName = "testes",type = "test")
@Mapping(mappingPath = "mapping/test.json")
public class TestEs {

    @Id
    private String id;
    private String name;
    private String email;
    private String address;
    private Integer age;
    private String url;
    private String title;

}
```

编写Dao层

```java
public interface TestEsDao extends ReactiveSortingRepository<TestEs,String> {

}
```

直接使用controller调用《注：演示环境快速使用，正式环境请编写service以及实现》

```java
@RequestMapping("test")
@RestController
public class TestFlux {

    @Autowired
    private TestEsDao testEsDao;
    
    @GetMapping("getEs")
    public Flux<TestEs> getEs(){
        return testEsDao.findAll();
    }

    @PostMapping("save")
    public Mono<TestEs> saveEs(@RequestBody TestEs testEs){
        return testEsDao.save(testEs);
    }

}
```

