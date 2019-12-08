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

mongo配置

```
# MongoDB URI配置 重要，添加了用户名和密码验证
spring.data.mongodb.uri=mongodb://anjian:topcom123@192.168.68.138:27017,192.168.68.137:27017,192.168.68.139:27017/anjian-db?slaveOk=true&replicaSet=mongoreplset&write=1&readPreference=secondaryPreferred&connectTimeoutMS=300000

#每个主机的连接数
spring.data.mongodb.connections-per-host=50
#线程队列数，它以上面connectionsPerHost值相乘的结果就是线程队列最大值
spring.data.mongodb.threads-allowed-to-block-for-connection-multiplier=50
spring.data.mongodb.connect-timeout=5000
spring.data.mongodb.socket-timeout=3000
spring.data.mongodb.max-wait-time=1500
#控制是否在一个连接时，系统会自动重试
spring.data.mongodb.auto-connect-retry=true
spring.data.mongodb.socket-keep-alive=true
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

## 高阶带条件Distinct

```java
//创建查询条件
Query query = new Query();
//查询id大于1的数据
query.addCriteria(Criteria.where("id").gt(1));
//返回一个List<Object>,
	query:				查询条件封装
	"type":				需要进行distinct的字段
	"user":				集合名称《表名》
	Object.class:		返回的类型
List<Object> distinct = mongoTemplate.findDistinct(query, "type", "user", Object.class);
```



## 高阶查询字段以及条件过滤

```java
        //创建QueryBuilder对象
        QueryBuilder queryBuilder = new QueryBuilder();
		//创建BasicDBObject对象，用于查询字段，例如只查询公司名称
        BasicDBObject basicDBObject = new BasicDBObject();
 		//需要查询返回的字段，值为1表示返回
        basicDBObject.put("companyFullName",1);
		//创建Query对象
		Query query = 
            new BasicQuery(queryBuilder.get().toString(),basicDBObject.toJson());
		//添加查询条件，查询atype为危险化学品的
        query.addCriteria(Criteria.where("atype").is("危险化学品"));
		//返回一个hashmap
        List<HashMap> list = mongoTemplate.find(query,  HashMap.class, "accident");
		//这个hashmap返回时就是我们查询的字段我们直接get字段名即可取出
        for (HashMap hashMap : list) {
            System.out.println(hashMap.get("companyFullName").toString());
        }
```

## 高阶Api进行聚合查询

首先我们引入MongoTemplate

```
    @Autowired
    private MongoTemplate mongoTemplate;
    
		//首先我们创建一个集合，并且使用聚合条件
        List<AggregationOperation> operations = new ArrayList<>();
        
        //这里是根据atype进行查询
        operations.add(Aggregation.match(Criteria.where("atype").is("煤矿")));

		//然后根据时间进行查询，这里采用时间戳为秒的
        if(dateParam != null){
            if(dateParam.getStartDate().length() > 1){
                Long start =  dateParam.startDate().getTime() / 1000;
                operations.add(Aggregation.match(Criteria.where("atime").gte(start)));
            }
            if(dateParam.getEndDate().length() > 1){
                Long end =  dateParam.endDate().getTime() / 1000;
                operations.add(Aggregation.match(Criteria.where("atime").lte(end)));
            }
        }
        //在这里进行要聚合的属性，我们根据atype进行聚合，然后台统计数量，as表示返回的字段，sum为将聚合的例如每个大类的死亡人数统计到一起，然后返回字段为deathnumber，然后把所有的聚合到一起，然后返回province

operations.add(Aggregation.group("atype").count().as("count").sum("deathnumber").as("deathnumber").addToSet("province").as("province"));
		//生成聚合对象
        Aggregation aggregation = Aggregation.newAggregation(operations);
        //进行查询，将聚合对象传入，然后写入集合的名称，并且返回类型为HashMap（我们也可以自定义封装）
        AggregationResults<HashMap> accident = mongoTemplate.aggregate(aggregation, "accident", HashMap.class);
        //获取返回条件返回
        return accident.getUniqueMappedResult();

```

mongo的查询语句对应如下

```
db.accident.aggregate([
	{
        $match: {
            "atype":"煤矿",
            "atime":{$gte:1556691186},
            "atime":{$lte:1561961586}}
        },
        {$group: { 
            _id: "$atype2",
            count:{$sum:1}, 
            deathnumber: { $sum: "$deathnumber" },
            province:{$addToSet:"$province"}} 
        },
        {$sort:{
            deathnumber:1
        }
    }
])
```

### 时间表达式格式化查询

```
db.temp_MongoDateTime.aggregate(
   [
     {
       $project: {
           "_id":0,
           "Rec_CreateTime":1,
          Year: { $dateToString: { format: "%Y", date: "$Rec_CreateTime" } },
          Month: { $dateToString: { format: "%m", date: "$Rec_CreateTime" } },
          Day: { $dateToString: { format: "%d", date: "$Rec_CreateTime" } },
          yearMonthDay: { $dateToString: { format: "%Y-%m-%d", date: "$Rec_CreateTime" } },
          Time: { $dateToString: { format: "%H:%M:%S:%L", date: "$Rec_CreateTime"} }
       }
     }
   ]
)

%Y		->		年
%m		->		月
%d		->		日
%H		->		时
%M		->		分
%S		->		秒
%L		->		毫秒

%Y/%m/%d						 ->			年/月/日
%Y:%m:%d %H:%M:%S		 ->			年:月:日 时:分:秒



解决时间相差8小时问题

pjOperation = Aggregation.project("time").andExpression("{ $dateToString: { format: '%H时',date: {$add: {'$dateModified',28800000}}}}").as("time").and("dateModified").as("dateModified").and("value").as("value");


注意如果使用mongo语法请使用
['$dateModified',28800000]，由于SPEL表达式解析问题[2]表示占位参数，所以使用{}替换，经过解析之后还是[]

```



```java
    @GetMapping("planAnalyzeByDay")
    public List<HashMap> planAnalyzeByDay(Date date, Integer day) {

        List<AggregationOperation> okOper = new ArrayList<>();
        Criteria criteria = Criteria.where("checkStatus").is("已完成");

        if (day == null || day > 10) {
            day = 6;
        }

        if(date == null){
            date = new Date();
        }
        Date dayFirstTime = DateUtil.getDayFirstTime(date);
        dayFirstTime.setHours(dayFirstTime.getHours() - 24 * day);
        criteria.and("planningTime").gte(dayFirstTime);


        okOper.add(Aggregation.match(criteria));

        ProjectionOperation okTimFromat = Aggregation.project("time").andExpression("{ '$dateToString': { format: '%m/%d', date: '$planningTime' } }").as("time");
        okOper.add(okTimFromat);

        GroupOperation time = Aggregation.group("time").count().as("value");
        okOper.add(time);
        Aggregation aggregation = Aggregation.newAggregation(okOper);
        AggregationResults<HashMap> check_plan = mongoTemplate.aggregate(aggregation, "check_plan", HashMap.class);
        return check_plan.getMappedResults();
    }
```

### OR条件



```java
new Criteria().orOperator(Criteria.where("sysType").is("抽放系统").and("sid").is("4"),Criteria.where("sysType").is("安全监控系统").and("sid").is("1"));
```



### 显示Mongo执行语句

properties版本

```properties
logging.level.org.springframework.data.mongodb.core.MongoTemplate: DEBUG
```

yaml版本

```properties
logging:
  level:
    org.springframework.data.mongodb.core.MongoTemplate: DEBUG
```

### 聚合

按照时间加上类型进行聚合

mongo语法

```sql
db.supervise_process.aggregate([
    {$group:{_id:{hosr:"$hosr",type:"$type"},count:{$sum:1}}},
    {$sort:{"_id.hosr":-1}}
])
```

效果如下，我们可以看到聚合的id变成了小时加上类型，然后统计数量

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1570605919019.png)

Java Data Api

我们这里将聚合字段设置为两个而不是直接设置为某一个字段，其他的还是和以前一样

```java
List<AggregationOperation> aggOper = new ArrayList<>();
List<Field> list = new ArrayList<>();
Fields from = Fields.from(Fields.field("hosr", "hosr"), Fields.field("type", "type"));
GroupOperation count = Aggregation.group(from).count().as("count");
aggOper.add(count);
aggOper.add(Aggregation.sort(new Sort(Sort.Direction.DESC,"hosr")));
Aggregation aggregation = Aggregation.newAggregation(aggOper);
AggregationResults<HashMap> aggregate = mongoTemplate.aggregate(aggregation, "supervise_process", HashMap.class);

return aggregate.getMappedResults();
```

# Mongo整合WebFlux

这里我们采用WebFlux和Mongo进行整合

### 引入依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
        </dependency>
```

### 编写实体类

```java
@Data
@Document(collection = "t_user_test")
public class User {

    @Id
    private String id;

    private String username;

    private String password;
}

```

### 编写Dao层

我们这里继承ReactiveMongoRepository，而不是MongoRepository

```java
public interface UserDao extends ReactiveMongoRepository<User,String> {


}
```

### 编写Controller层

注：这里我们直接调用Controller调用Dao层，而没有Service层，是为了演示所以简洁，正常开发中请编写Service层,我们发现ReactiveMongoTemplate也代替了MongoTemplate，这说明ReactiveMongo已经是比较成熟了，对原来的Api完全过度。

```java
@RequestMapping("test")
@RestController
@Api(tags = "测试WebFlux")
public class TestFlux {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private UserDao userDao;

    @GetMapping("getAll")
    public Flux<User> getAll(){
        return userDao.findAll();
    }

    @GetMapping("getById")
    public Mono<User> getById(String id){
      return userDao.findById(id);
    }

}

```

