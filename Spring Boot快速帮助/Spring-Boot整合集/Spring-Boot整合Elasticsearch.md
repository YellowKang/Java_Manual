

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

# 官方文档地址

​		官方文档地址为：

​		我们直接修改url中的版本即可

```http
https://docs.spring.io/spring-data/elasticsearch/docs/4.0.3.RELEASE/reference/html/#reference
```



# 配置连接

## 配置Transport连接

​		这里我们写入es的地址9300端口，然后配置它的集群名称，这个可以在es配置文件查看，默认elasticsearch

​		注意：这里填写地址为通信地址不是rest地址，并且注意cluster-name，必须和es的一样。

​		注意：我们这里使用的是采用的`TransportClient`从ES7开始不推荐使用，在ES8以后会将其删除掉，我们以后都会使用高级的Rest Client进行连接，也就是我们的9200端口

​		yml版本：

```properties
spring:
	data:
      elasticsearch:
        cluster-nodes: 111.67.196.127:9300
        repositories:
          enabled: true
        cluster-name: "docker-cluster"
```

​		properties版本：

```properties
spring.data.elasticsearch.cluster-nodes=111.67.196.127:9300
spring.data.elasticsearch.repositories.enabled=true
spring.data.elasticsearch.cluster-name=docker-cluster
```

## 自定义高级Rest连接

​		我们使用自定义的高级Rest连接，首先编写配置

```properties
spring:
  data:
    elasticsearch:
      client:
        rest: 192.168.1.11:9200,192.168.1.12:9200,192.168.1.13:9200
```

​		然后我们创建一个配置类ElasticsearchConfig

```java

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

/**
 * @Author BigKang
 * @Date 2020/8/24 5:14 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize Elasticsearch配置
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.data.elasticsearch.client.rest:null}")
    private String urls;

    /**
     * 创建Elasticsearch高级Rest连接
     * @return
     */
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        if(StringUtils.isEmpty(urls) || "null".equals(urls)){
            throw new RestClientException("Elasticsearch HighLevel Client Url is Null！");
        }
        String[] esUrls = urls.split(",");
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(esUrls)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

}
```



# 编写代码

### 编写实体类

​		我们引入lombok简化代码，不写getset

​		我们编写一个测试实体类

​		这里的Document就是一个索引，它的索引名称为testes，类型为test，然后它的id为String类型，并且加上id注解，然后加上CreatedDate创建时时间，  @LastModifiedDate为修改时间，    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")表示字段的分词配置，需要分词，    @Field(index = true)表示这个字段需要添加索引

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

​		这里只需要继承esRepository，然后定义实体类型以及主键类型

```java
public interface TestEsDao extends ElasticsearchRepository<TestEs, String>  {
}
```

​		然后我们就能直接使用Dao了（这里为测试方便，真实开发请不要偷懒，还是要写service和impl），这里只是简单的增删改查，下面我们将更深入系统的了解SpringDataElasticsearch

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

​		这个注解是加在我们的实体类上的，下面就是我们的属性以及含义。

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

```java
String mappingPath() default "";							// 指定mapping文件的路径，默认为空
```

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

### @Setting

```java
String settingPath() default "";							// 指定mapping文件的路径，默认为空
```

​		Setting是指我们在创建索引的时候指定的索引的设置，包括我们的分片，以及副本集，还有我们对分词和过滤的拓展，例如我们的分词+拼音搜索，在简单的分词情况下我们是很少使用到自定义Setting的，SpringData通过@Document注解也能设置分片信息，那么我们来看看如何设置吧。

​		我们在实体类上加上注解，并且指定resources目录下的文件位置

```java
@Setting(settingPath = "setting/test_boot.json")
```

​		然后我们文件中放入内容即可，可以看到我们这里还是放入了一个拼音+分词的设置

```json
{
  "analysis": {
    "analyzer": {
      "ik_smart_pinyin": {
        "type": "custom",
        "tokenizer": "ik_smart",
        "filter": ["my_pinyin", "word_delimiter"]
      },
      "ik_max_word_pinyin": {
        "type": "custom",
        "tokenizer": "ik_max_word",
        "filter": ["my_pinyin", "word_delimiter"]
      }
    },
    "filter": {
      "my_pinyin": {
        "type" : "pinyin",
        "keep_separate_first_letter" : true,
        "keep_full_pinyin" : true,
        "keep_original" : true,
        "first_letter": "prefix",
        "limit_first_letter_length" : 16,
        "lowercase" : true,
        "remove_duplicated_term" : true
      }
    }
  }
}
```

# SpringDataElasticsearch模板

​		我们知道在SpringData系列中，其实对于查询都封装了一个Template如MongoDB的MongoTemplate，那么我们知道Es中肯定也有一个ElasticsearchTemplate，那么由于版本升级的原因，连接Es分别有Rest方式和Transport方式，随着以后的版本升级Elasticsearch推荐我们使用Rest方式，那么下面就会介绍两个模板的相应配置。

​		Transport连接方式

```java
		@Autowired
		private ElasticsearchTemplate elasticsearchTemplate;
```

​		Rest连接方式

```java
		@Autowired
		private ElasticsearchRestTemplate elasticsearchRestTemplate;
```

​		注意这两个方式他是不一样的，因为他会根据我们初始化的连接去创建，如果我们直接注入则会报错，那么我们还有另外一种方式进行实现,那么我们知道Spring是有容器的，他肯定会去实现接口，所以我们的ElasticsearchTemplate和ElasticsearchRestTemplate都实现了同一个接口那就是我们的ElasticsearchOperations，所以我们使用ElasticsearchOperations就不会出现冲突，但是我们需要注意如果我们注入两个模板一个Transport一个Rest则会冲突，我们就不能使用ElasticsearchOperations。

```java
		@Autowired
		private ElasticsearchOperations elasticsearchOperations;
```

​		下面我们就以ElasticsearchRestTemplate为示例，因为他的功能和ElasticsearchTemplate差不多是一样的，并且我们知道实现接口，自己还有其他的拓展方法。下面我们就来了解模板分别有哪些功能吧。

## 索引

​		首先是我们的索引

​		那么我们首先肯定是要有创建索引的

### 创建索引

​		ElasticsearchRestTemplate默认给我们提供了4种创建索引的方式

​		第一种：使用索引名称创建，其他默认，这样的索引就只定义了索引名称其他的设置都是采用Es默认初始化的索引设置

```java
        // 使用索引名称indexName创建，其他设置默认
        elasticsearchRestTemplate.createIndex("indexName");
```

​		第二种：使用索引名称+字符串设置进行创建,注意这里不需要setting标签，setting可以有多种类型String，Map，以及XContentBuilder

```java
        // 使用Json字符串+索引名称进行创建
        String setting = "{\n" +
                "    \"number_of_shards\": 3,\n" +
                "    \"number_of_replicas\": 1\n" +
                "}";
        elasticsearchRestTemplate.createIndex("indexName",setting);
```

​		第三种：我们使用实体类进行创建，他会根据实体的注解进行解析（@Setting注解），然后创建索引

```java
			// 根据实体类创建索引
			elasticsearchRestTemplate.createIndex(TestEs.class);
```

​		第四种：

```java
        // 根据实体类+Setting设置创建，setting可以有多种类型String，Map，以及XContentBuilder
        Map<String,Object> objSetting = new HashMap<>();
        objSetting.put("index.number_of_shards",4);
        objSetting.put("index.number_of_replicas",1);
        elasticsearchRestTemplate.createIndex(TestEs.class,objSetting);
```



### 创建(修改)模板

​		因为模板中是没有Post方法的，也就是说我们每次其实是修改模板，但是模板中的属性一旦第一次创建之后，以后就都无法创建了，所以每次创建(修改)模板就是在向模板中添加属性，并不是真实的修改，或者创建，如果插入的属性没有响应的模板那么Elasticsearch则会自动添加一个模板属性，并且是不可修改的属性。

​		然后是我们的模板，我们知道Elasticsearch的文档结构都是由mapping进行维护的，mapping中包含了对属性类型，以及分词等等的维护，那么下面我们使用SpringDataElasticsearch模板来创建模板吧，模板的创建方式大概也分为了4种。

​		第一种：使用索引名称+字符串mapping进行创建，mapping可以有多种类型String，Map，以及XContentBuilder

```java
        // 根据索引名称+索引类型+mapping映射创建mapping
        String indexName = "indexName";
        String typeName = "typeName";
        String mapping = "{\n" +
                "    \"properties\": {\n" +
                "        \"age\": {\n" +
                "            \"type\": \"long\"\n" +
                "        },\n" +
                "        \"content\": {\n" +
                "            \"type\": \"text\",\n" +
                "            \"fields\": {\n" +
                "                \"keyword\": {\n" +
                "                    \"type\": \"keyword\",\n" +
                "                    \"ignore_above\": 256\n" +
                "                }\n" +
                "            },\n" +
                "            \"analyzer\": \"ik_smart_pinyin\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        elasticsearchRestTemplate.putMapping(indexName,typeName,mapping);
```

​		第二种：根据索引名称+索引类型+类class，这里的class主要是为了获取注解@Mapping，不一定是需要我们的实体类对象，写入指定了@Mapping注解的类即可

```java
       // 根据索引名称+索引类型+实体类型映射创建mapping
        String indexName = "indexName";
        String typeName = "typeName";
        elasticsearchRestTemplate.putMapping(indexName,typeName,Test.class);
```

​		第三种：这种就是完全采用实体类的配置进行创建了

```java
       // 根据实体创建mapping，根据@Mapping注解获取mapping文件，以及@Document上的index信息创建
        elasticsearchRestTemplate.putMapping(Test.class);
```

​		第四种：

```java
        // 根据Mapping+实体类class创建mapping模板
				String mapping = "{\n" +
                "    \"properties\": {\n" +
                "        \"age\": {\n" +
                "            \"type\": \"long\"\n" +
                "        },\n" +
                "        \"content\": {\n" +
                "            \"type\": \"text\",\n" +
                "            \"fields\": {\n" +
                "                \"keyword\": {\n" +
                "                    \"type\": \"keyword\",\n" +
                "                    \"ignore_above\": 256\n" +
                "                }\n" +
                "            },\n" +
                "            \"analyzer\": \"ik_smart_pinyin\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        elasticsearchRestTemplate.putMapping(Test.class,mapping);
```

### 查询索引

​		我们可以查询索引的信息，例如索引的Setting设置，或者模板，方式如下：

​		1、查询索引的设置，我们首先查询索引是否存在，然后根据索引名称查询相应的Setting

```java
       	boolean exists = elasticsearchOperations.indexExists(indexName);
        Assert.isTrue(exists,EsConstant.INDEX_NO_EXISTS_MESSAGE);
        Map<String, Object> setting = elasticsearchOperations.getSetting(indexName);

				通常默认创建的Index都会有如下几个设置：
          {
		  			"index.creation_date": "1598328176886",				//	创建的索引时间，时间戳（毫秒）
		  			"index.uuid": "DuOljcZmSr-E13k3MpVTaw",				//	索引的标识UUID，Elasticsearch生成
		  			"index.version.created": "6081099",						//	索引创建的Elasticsearch版本此处为6.8.10
		  			"index.provided_name": "test_map",						//	索引名称
		  			"index.number_of_replicas": "2",							//	索引副本集数量
		  			"index.number_of_shards": "4"									//	索引Shard分片数量
					}
```

​		2、查询索引的Mapping模板映射，首先我们也是需要查询索引类型是否存在，然后查询相应的Mapping

```java
				boolean typeExists = elasticsearchOperations.typeExists(indexName, typeName);
        Assert.isTrue(typeExists, EsConstant.INDEX_TYPE_NO_EXISTS_MESSAGE);
        Map<String, Object> mapping = elasticsearchOperations.getMapping(indexName, typeName);

				返回的信息和Setting有一点不一样，properties的属性名称为age和name，：
        {
				  "properties": {
				    "age": {
				      "type": "long"
				    },
				    "content": {
				      "type": "text",
				      "fields": {
				        "keyword": {
				          "type": "keyword",
				          "ignore_above": 256
				        }
				      },
				      "analyzer": "ik_smart_pinyin"
				    }
				  }
				}
```

### 添加数据

​		那么在我们向索引添加数据的时候我们没有什么Save方法了，而是采用统一的Index索引方法，也就是对我们的数据进行索引，那么如何向索引添加数据呢，如下：

```java
				// 首先创建Map存储数据
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("test", "bigkang");
        objectMap.put("age", 20);

				// 创建一个IndexQueryBuilder然后构建成IndexQuery
				IndexQuery indexQuery = new IndexQueryBuilder()
          			// 设置索引名称
                .withIndexName("test_index")
          			// 设置索引类型
                .withType("test_type")
          			// 设置Map对象
                .withObject(objectMap)
                .build();
				// 进行索引
				elasticsearchRestTemplate.index(indexQuery);
```

### 删除索引

​		那么我们的索引可以创建自然也可以进行删除，SpringDataElasticsearch模板给我们提供了两种方式删除索引：

​				第一种：根据索引名称进行删除

```java
        String indexName = "indexName";
        elasticsearchRestTemplate.deleteIndex(indexName);
```

​				第二种：根据实体类设置删除索引,会根据实体类中的注解设置的索引名称进行删除

```java
			  elasticsearchRestTemplate.deleteIndex(Test.class);
```

## 查询

​		Es查询相应类的List

```java
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(QueryBuilders.matchAllQuery());
        List<Demo> demos = elasticsearchRestTemplate.queryForList(nativeSearchQuery, Demo.class);
```

​		根据不同的索引，以及类型，查询数据（不使用实体类中的索引，只映射结果集）



```java
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(QueryBuilders.matchAllQuery());
				nativeSearchQuery.addIndices("test");
    		nativeSearchQuery.addTypes("test");
        List<Demo> demos = elasticsearchRestTemplate.queryForList(nativeSearchQuery, Demo.class);
```
### QueryString

```java
        // 构建查询请求
        SearchRequest searchRequest  = new SearchRequest("demo");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(Strings.EMPTY_ARRAY,new String[]{"attachment.content"});
        searchSourceBuilder.query(QueryBuilders
                .queryStringQuery("(测试)OR(河南OR开发手册)NOT(文件)")
                .field("fileName", 1.7F));
        searchSourceBuilder.size(10);
        searchRequest.source(searchSourceBuilder);
        try {
            // 获取查询结果
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 创建默认对象转换
            DefaultResultMapper defaultResultMapper = new DefaultResultMapper();
            // 创建分页对象
            PageRequest pageRequest = PageRequest.of(1, 10);
            Page<Demo> demos = defaultResultMapper.mapResults(search, Demo.class, pageRequest);
            System.out.println(demos.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
```



## 删除



## 修改

# 使用Rest连接对象转换使用SpringData

​		有时候我们经常会使用原生的查询去查询Es，查询出来以后我们又想将它转为SpringData的对象那么我们可以使用如下方式。

```

```



# 整合文件搜索

​		前期准备以及插件安装参考ingest-attachment插件：[点进入](https://github.com/YellowKang/Java_Manual/blob/master/Elasticsearch6.0%E5%BF%AB%E9%80%9F%E5%BC%80%E5%8F%91/Docker%E5%AE%89%E8%A3%85Elasticsearch6.0.md)

​		假设我们已经建立好了索引，并且创建了管道处理attachment，下面是我们的代码示例

​		Attachment实体信息

```java
public class Attachment {

    private String content;

    private Long content_length;

    private String content_type;

    private String author;
}
```

​		Es实体

```java
@Data
@Document(indexName = "demo",type = "demo",shards = 3,replicas = 0)
public class Demo extends BaseEsEntity {
    @Id
    protected String id;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String fileName;

    private String filebase64;

    private Attachment attachment;
}
```

​		然后我们编写一个控制器，注意此处我采用Rest高级连接其他情况请采用相应的连接

```java

    @Autowired
    private  ElasticsearchOperations elasticsearchOperations;

		@PostMapping("saveFile")
    public void saveFile(Demo demo, MultipartFile multipartFile){
        // 从elasticsearchOperations中获取ElasticsearchRestTemplate然后拿到RestHighLevelClient
        RestHighLevelClient client = ((ElasticsearchRestTemplate) elasticsearchOperations).getClient();
        // 拿到实体类上的注解
        Document document = AnnotationUtils.getAnnotation(Demo.class, Document.class);
        IndexRequest indexRequest = new IndexRequest(document.indexName(),document.type());
        // 获取文件字节转base64
        try {
            String base64 = Base64.getEncoder().encodeToString(multipartFile.getBytes());
            demo.setFilebase64(base64);
            // 设置管道
            indexRequest.setPipeline("attachment");
            // 将demo转为Json
            ObjectMapper mapper=new ObjectMapper();
            String json = mapper.writeValueAsString(demo);
            indexRequest.source(json,XContentType.JSON);
            IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
            if (index.getIndex().equals(document.indexName())) {
                System.out.println("索引成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

​		然后使用测试swagger上传

<img src="https://blog-kang.oss-cn-beijing.aliyuncs.com/1611718724726.png" style="zoom:50%;" />

​		然后调用，此处即可上传成功，然后我们来进行查询即可。

```java
    @GetMapping("searchFile")
    public Page<Demo> searchFile(String keyword){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 查询文档内容或者文件名称的关键词
        queryBuilder.must(QueryBuilders.multiMatchQuery(keyword,"attachment.content","fileName"));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryBuilder);
        Page<Demo> demos = elasticsearchOperations.queryForPage(nativeSearchQuery, Demo.class);
        return demos;
    }
```

​		并且需要注释事项注意Servlet文件大小限制导致无法上传

```properties
spring:
  servlet:
    multipart:
      max-file-size: 10MB
```



# 不整合SpringBoot

​		单独使用SpringData

```java
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("139.9.7.11", 9200, "http"));
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
        ElasticsearchRestTemplate elasticsearchRestTemplate = new ElasticsearchRestTemplate(restHighLevelClient);

```



# 条件构造器

​		在我们很多的时候，单纯的使用spring data的接口开发无法满足我们的需求，所以我们需要进行一些复杂的实现

​		核心使用DefaultResultMapper

```java

        // 构建查询请求
        SearchRequest searchRequest  = new SearchRequest("demo");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(Strings.EMPTY_ARRAY,new String[]{"attachment.content"});
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("均线操盘","attachment.content","fileName"));
        searchSourceBuilder.size(10);
        searchRequest.source(searchSourceBuilder);
        try {
            // 获取查询结果
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 创建默认对象转换
            DefaultResultMapper defaultResultMapper = new DefaultResultMapper();
            // 创建分页对象
            PageRequest pageRequest = PageRequest.of(1, 10);
            Page<Demo> demos = defaultResultMapper.mapResults(search, Demo.class, pageRequest);
            System.out.println(demos.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
```



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
