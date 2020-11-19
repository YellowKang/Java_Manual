# 首先创建数据库以及用户

进入mongo，创建test数据库，以及用户

```shell
use test

创建一个用户，用户名test密码test，为这个数据库的管理员，这个用户属于test这个数据库
db.createUser({user:"test",pwd:"test",roles:[{role:'userAdmin',db:'test'}]})
然后我们去SpringBoot中进行整合
```

# 引入依赖

lombok为了简化代码使用《懒得写get，set方法》

```xml
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

```properties
spring:
  data:
    mongodb:
      uri: mongodb://test:test@192.168.0.1:27017/test
```

properties版本

```properties
spring.data.mongodb.uri=mongodb://test:test@192.168.0.1:27017/test
```

mongo配置

```properties
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

```java
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

```java
public interface TestMongoDao extends MongoRepository<TestMongo,String> {
}
```

为了快速开始不编写service以及impl实现，直接controller调用

先编写几个简单的查询，新增和删除

```java
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

## 高阶Api

### 聚合查询

首先我们引入MongoTemplate

```java
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

```json
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

```json
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





### 多重聚合

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



### 字符串长度查询

​		我们需要筛选过滤长度为25的某一个字段的字符串长度，大于25我们才进行查询否则不查询，我们采用正则进行过滤匹配，查询0-25字符长度的数据。

```Java
      	Integer maxLength = 25;
				String patternStr = String.format("^.{0,%s}$", maxLength);
        Pattern pattern = Pattern.compile(patternStr);
        Query query = new Query();
        query.addCriteria(Criteria.where("title").regex(pattern));
```

​		查询最少为25长度的字符

```Java
      	Integer minLength = 25;
				String patternStr = String.format("^.{%s,}$", minLength);
        Pattern pattern = Pattern.compile(patternStr);
        Query query = new Query();
        query.addCriteria(Criteria.where("title").regex(pattern));
```

### 数组查询

​		有时候我们的数据结构是一个数组，那么我们针对这种数据怎么样来进行查询呢，数据结构如下所示：

```json
{
		"questionerName" : "超级管理员",
    "answered" : false,
    "reply" : [ 
        {
            "answerDept" : "法制支队",
            "answererID" : NumberLong(16),
            "answered" : false
        }, 
        {
            "answerDept" : "警务支援大队",
            "answererID" : NumberLong(15),
            "answered" : false
        }
    ],
    "entityName" : "SearchKeyword"
}
```

​		那么我们需要对这个answerDept进行一个模糊查询，代码如下：

```java
// 创建Query对象        
Query query = new Query();   
// 根据reply下的answerDept字段进行匹配（模糊查询），
query.addCriteria(Criteria.where("reply").elemMatch(Criteria.where("answerDept").regex(“大队”)));
```

### 经纬度地理位置查询

​		首先我们需要存储经纬度结构，如下，那么我们一定需要一个实体来进行存储这个location

```properties
{
    "_id" : ObjectId("5f9a94b19973f23ef82e4c05"),
    "name" : "天安门-李四",
    "location" : {
        "type" : "Point",
        "coordinates" : [ 
            116.404412, 
            39.915046
        ]
    },
    "type" : "GEO"
}
```

​		下面我们编写一个location用于存储我们的经纬度类型

```java
import com.mongodb.client.model.geojson.GeoJsonObjectType;


/**
 * @Author BigKang
 * @Date 2020/10/29 5:34 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize Mongo点位置信息
 */
public class MongoPoint {

    /**
     * Mongo点索引类型
     */
    private String type = GeoJsonObjectType.POINT.getTypeName();

    /**
     * 最小经度下限
     */
    private static final double MIN_LONGITUDE = -180;
    /**
     * 最大经度上限
     */
    private static final double MAX_LONGITUDE = 180;
    /**
     * 最小纬度下限
     */
    private static final double MIN_LATITUDE = -90;
    /**
     * 最大纬度上限
     */
    private static final double MAX_LATITUDE = 90;

    /**
     * 坐标数组长度
     */
    private static final Integer COORDINATES_LENGTH = 2;

    /**
     * 经纬度数组【经度，纬度】
     */
    private double[] coordinates;

    public MongoPoint(){

    }

    /**
     * 设置创建构造方法初始化信息
     * @param longitude
     * @param latitude
     */
    public MongoPoint(double longitude,double latitude){
        checkLongitude(longitude);
        checkLatitude(latitude);
        coordinates =  new double[]{longitude,latitude};
    }

    /**
     * 检查经度
     */
    public void checkLongitude(double lng){
        if(lng > MAX_LONGITUDE || lng < MIN_LONGITUDE){
            throw new RuntimeException("Illegal longitude exception！");
        }
    }

    /**
     * 检查纬度
     */
    public void checkLatitude(double lat){
        if(lat > MAX_LATITUDE || lat < MIN_LATITUDE){
            throw new RuntimeException("Illegal latitude exception！");
        }
    }

    /**
     * 获取坐标信息
     * @return
     */
    public double[] getCoordinates(){
        if(coordinates == null || coordinates.length <= 0 || coordinates.length != COORDINATES_LENGTH){
            throw new NullPointerException("Longitude and latitude information is empty or illegal length exception！");
        }
        checkLongitude(coordinates[0]);
        checkLongitude(coordinates[1]);
        return coordinates;
    }

}

```

​		然后我们编写一个工具类，帮助我们快速生成Query接口，并且添加一个计算距离的工具类

```java

import com.mongodb.BasicDBObject;
import com.topcom.emergency.vo.MongoPoint;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @Author BigKang
 * @Date 2020/10/30 11:07 上午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize Mongo查询工具类
 */
public class MongoQueryUtil {

    /**
     * 赤道半径
     */
    private static Integer EARTH_RADIUS = 6378137;

    /**
     * Mongo near 地理位置查询
     *
     * @param field      地理位置字段
     * @param mongoPoint Mongo地图点
     * @param radius     半径范围，默认米
     * @return
     */
    public static Query near(String field, MongoPoint mongoPoint, Integer radius) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(field,
                new BasicDBObject(
                        "$near", new BasicDBObject()
                        .append("$geometry", new BasicDBObject()
                                .append("type", "Point")
                                .append("coordinates", mongoPoint.getCoordinates()))
                        .append("$maxDistance", radius)));
        Query query = new BasicQuery(basicDBObject);
        return query;
    }


    /**
     * 计算两个经纬度的距离返回（米）
     *
     * @param origin      当前起点经纬度
     * @param destination 目标经纬度
     * @return
     */
    public static double GetDistance(MongoPoint origin, MongoPoint destination) {
        double lng1 = origin.getCoordinates()[0];
        double lat1 = origin.getCoordinates()[1];
        double lng2 = destination.getCoordinates()[0];
        double lat2 = destination.getCoordinates()[1];
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

}
```

​		然后我们编写实体

```java
import com.topcom.emergency.vo.MongoPoint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 测试实体
 * @Author BigKang
 * @Time 2020-10-29 16:02:49
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize 
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "t_test")
// 地理位置索引
@CompoundIndexes(value = {@CompoundIndex(name = "SurroundingsInfo_index",def = "{'location':'2dsphere'}")})
public class Test{

    /**
    * 名称
    */
    private String name;

    /**
    * 类型
    */
    private String type;


    /**
    * 位置
    */
    private MongoPoint location;

}
```

​		然后我们进行查询,我们查询这个经纬度附近1000米的点信息，然后查询type等于test的数据

```java
    public static void main(String[] args) {
        // 经纬度数组
        double[] coordinates = new double[]{116.403406, 39.923236};
        // 创建点对象
        MongoPoint mongoPoint = new MongoPoint(coordinates[0], coordinates[1]);

        // 半径范围，1000米
        Integer radius = 1000;
        String filed = "location";
        // 生成查询条件
        Query query = MongoQueryUtil.near(filed, mongoPoint,radius);
        query.addCriteria(Criteria.where("type").is("test"));
        List<Test> tests = mongoTemplate.find(query, Test.class);
    }
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

```json
{
"sendUrl":"http://gzmkjcglj.com/uploadData/",
"startDate":"2019-12-1",
"endDate":"2019-12-30"
}
```



# 显示Mongo执行语句

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

# 去掉Mongo添加数据_class

这里我们创建一个MongoConverter的Bean对象我们不修改MongoTemplate

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * @Author BigKang
 * @Date 2020/8/1 10:52 上午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize MongoDb配置去掉_class
 */
@Configuration
public class MongoConfig {

    private final MongoDatabaseFactory mongoDatabaseFactory;

    public MongoConfig(MongoDatabaseFactory mongoDatabaseFactory) {
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    @Bean
    public MongoConverter mongoConverter(){
        MappingMongoConverter converter =
                new MappingMongoConverter(mongoDatabaseFactory, new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return converter;
    }

}

```

