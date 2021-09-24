# 官网

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;首先我们定位到MongoDB的JavaAPI官网: [点击进入](https://mongodb.github.io/mongo-java-driver/4.0/driver/)

# 引入依赖

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;传统驱动

```xml
<dependencies>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.0.5</version>
    </dependency>
</dependencies>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;响应式驱动（异步非阻塞）

```xml
<dependencies>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-reactivestreams</artifactId>
        <version>4.0.5</version>
    </dependency>
</dependencies>
```

# 版本

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;下面是Mongo官网中对应的驱动版本的支持，低版本向上兼容

| Java驱动程序版本 | MongoDB 3.0 | MongoDB 3.2 | MongoDB 3.4 | MongoDB 3.6 | MongoDB 4.0 | MongoDB的4.2 | MongoDB 4.4 |
| :--------------- | :---------- | :---------- | :---------- | :---------- | :---------- | :----------- | :---------- |
| 版本4.0          | ✓           | ✓           | ✓           | ✓           | ✓           | ✓            |             |
| 版本3.12         | ✓           | ✓           | ✓           | ✓           | ✓           | ✓            | ✓*          |
| 版本3.11         | ✓           | ✓           | ✓           | ✓           | ✓           | ✓            |             |
| 版本3.10         | ✓           | ✓           | ✓           | ✓           | ✓           |              |             |
| 版本3.9          | ✓           | ✓           | ✓           | ✓           | ✓           |              |             |
| 版本3.9          | ✓           | ✓           | ✓           | ✓           | ✓           |              |             |
| 版本3.8          | ✓           | ✓           | ✓           | ✓           | ✓           |              |             |
| 版本3.7          | ✓           | ✓           | ✓           | ✓           |             |              |             |
| 版本3.6          | ✓           | ✓           | ✓           | ✓           |             |              |             |
| 版本3.5          | ✓           | ✓           | ✓           |             |             |              |             |
| 版本3.4          | ✓           | ✓           | ✓           |             |             |              |             |
| 版本3.3          | ✓           | ✓           |             |             |             |              |             |
| 版本3.2          | ✓           | ✓           |             |             |             |              |             |
| 版本3.1          | ✓           |             |             |             |             |              |             |
| 3.0版            | ✓           |             |             |             |             |              |             |

# MongoDB连接

## 单节点版本

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建一个超简单URI连接

```java
				 MongoClient mongoClient = MongoClients.create("mongodb://admin:admin123@192.168.1.11:27017/test-db");
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建一个简单的带设置URI连接单节点版本

```java
        // Mongo连接字符串，格式（mongodb:// 用户名 : 密码 @ Host地址 : 端口号 / 数据库名）
        ConnectionString connString = new ConnectionString("mongodb://admin:admin123@192.168.1.11:27017/test-db");
        // 创建一个连接设置
        MongoClientSettings settings = MongoClientSettings.builder()
                // 应用一个连接字符串
                .applyConnectionString(connString)
                // 设置是否由于网络原因重试写入，保证数据写入的可靠性
                .retryWrites(true)
                .build();
        // 创建连接
        MongoClient mongoClient = MongoClients.create(settings);
```

## 副本集版本

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;创建一个URI版本的Mongo副本集连接

```java
		MongoClient mongoClient = MongoClients.create("mongodb://192.168.1.11:27017,192.168.1.12:27017,192.168.1.13:27017/?replicaSet=myReplicaSet");
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;创建一个设置版本的Mongo副本集连接

```java
        // 创建一个Mongo连接设置
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(
                                // 设置3个节点的Mongo地址
                                new ServerAddress("192.168.1.11", 27017),
                                new ServerAddress("192.168.1.12", 27017),
                                new ServerAddress("192.168.1.13", 27017)))
                                // 指定需要的副本集名称
                                .requiredReplicaSetName("myReplicaSet"))
                .build();
        // 根据设置创建Mongo连接
        MongoClient mongoClient = MongoClients.create(clientSettings);
```

## 集群版本

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;连接Mongos不用指定副本集，直接连接集群

```java
        // 创建一个Mongo连接设置
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(
                                // 设置3个节点的Mongo地址
                                new ServerAddress("192.168.1.11", 27017),
                                new ServerAddress("192.168.1.12", 27017),
                                new ServerAddress("192.168.1.13", 27017))))
                .build();
        // 根据设置创建Mongo连接
        MongoClient mongoClient = MongoClients.create(clientSettings);
```

## 连接池连接

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;连接池+设置版

```java
        ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                // 允许的最大连接数。这些连接在空闲时将保留在池中。一旦池耗尽，任何需要连接的操作都将阻塞等待可用的连接,默认值为100。
                .maxSize(100)
                // 最小连接数。这些连接在空闲时将保留在池中，并且池将确保它至少包含此最小数目。默认值为0。
                .minSize(5)
                // 这是池中连接可用的最大侍者数(等待队列数)。所有进一步的操作都会立即获得异常。默认值为500。
                .maxWaitQueueSize(500)
                // 线程等待连接可用的最长时间。默认值为2分钟。值为0表示将不等待。负值表示它将无限期等待。
                .maxWaitTime(2, TimeUnit.MINUTES)
                // 池化连接可以生存的最长时间。零值表示寿命没有限制。超过使用寿命的池化连接将被关闭，并在必要时用新连接代替。
                .maxConnectionLifeTime(3,TimeUnit.HOURS)
                // 池连接的最大空闲时间。零值表示对空闲时间没有限制。超过空闲时间的池化连接将被关闭，并在必要时由新连接代替。
                .maxConnectionIdleTime(20,TimeUnit.MINUTES)
                // 在连接池上运行第一个维护作业之前要等待的时间,初始化延迟（通常不设置）
                 .maintenanceInitialDelay(0,TimeUnit.MILLISECONDS)
                // 维护作业运行之间的时间段。（通常不设置）
                 .maintenanceFrequency(10,TimeUnit.MINUTES)
                .build();
        String username = "test";
        String password = "test123";
        String database = "test-db";
        // 根据用户名数据库以及密码创建凭证
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        // 创建一个Mongo连接设置
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                // 设置访问凭证（认证）
                .credential(credential)
                .applyToClusterSettings(builder ->
                        builder.
                                hosts(Arrays.asList(
                                // 设置3个节点的Mongo地址
                                new ServerAddress("192.168.1.11", 27017))))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                .build();
        // 根据设置创建Mongo连接
        MongoClient mongoClient = MongoClients.create(clientSettings);

```

# Mongo数据操作

## 数据库

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;获取数据库

```java
        // 从连接中获取数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;删除数据库

```java
        // 从连接中获取数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
        // 删除数据库
        mongoDatabase.drop();
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行runCommand

```java
        // 从连接中获取数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        // 执行runCommand命令
        Document document = mongoDatabase.runCommand(
                new BasicDBObject()
                        .append("findAndModify", "acc")
                        .append("query", new BasicDBObject()
                                .append("from", "安监总局")
                        )
                        .append("sort", new BasicDBObject()
                                .append("deathnumber", -1)
                        )
                        .append("remove",true)
        );
        System.out.println(document);
```

## 集合

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;获取所有集合名称

```java
        // 从连接中获取数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        // 获取所有的集合名称
        MongoIterable<String> collectionNames = mongoDatabase.listCollectionNames();
        for (String collectionName : collectionNames) {
            System.out.println(collectionName);
        }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;获取指定名称的集合

```java
        // 获取指定集合
        MongoCollection<Document> collection = mongoDatabase.getCollection("test");
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;根据指定实体获取集合，需要注册编码解析器才行

```java
        // 编码解析器注册
        CodecRegistry codecRegistry =
                CodecRegistries.fromRegistries(
                        CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                        MongoClientSettings.getDefaultCodecRegistry(),
                        // 新增一个PoJo解析器，解析类型自动
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // 根据用户名数据库以及密码创建凭证
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        // 创建一个Mongo连接设置
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                // 设置访问凭证（认证）
                .credential(credential)
                // 代码类型注册器
                .codecRegistry(codecRegistry)
                .applyToClusterSettings(builder ->
                        builder.
                                hosts(Arrays.asList(
                                // 设置3个节点的Mongo地址
                                new ServerAddress("192.168.1.15", 20168))))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                .build();


				// 根据指定泛型获取集合
        MongoCollection<Demo> test = mongoDatabase.getCollection("test", Demo.class);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;删除集合

```java
        // 获取指定集合
        MongoCollection<Document> collection = mongoDatabase.getCollection("test");
        // 删除集合
        collection.drop();
```



## 文档

### 添加

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;新增数据

```java
       	// 从连接中获取数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        // 获取指定集合
        MongoCollection<Document> collection = mongoDatabase.getCollection("test");

        // 插入一个文档
        Document document = new Document();
        document.put("name","bigkang");
        document.put("email","XXXX@qq.com");
        collection.insertOne(document);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;新增多条数据

```java
        Document doc1 = new Document("name", "Amarcord Pizzeria")
                .append("contact", new Document("phone", "264-555-0193")
                        .append("email", "amarcord.pizzeria@example.net")
                        .append("location",Arrays.asList(-73.88502, 40.749556)))
                .append("stars", 2)
                .append("categories", Arrays.asList("Pizzeria", "Italian", "Pasta"));


        Document doc2 = new Document("name", "Blue Coffee Bar")
                .append("contact", new Document("phone", "604-555-0102")
                        .append("email", "bluecoffeebar@example.com")
                        .append("location",Arrays.asList(-73.97902, 40.8479556)))
                .append("stars", 5)
                .append("categories", Arrays.asList("Coffee", "Pastries"));

        List<Document> documents = new ArrayList<Document>();
        documents.add(doc1);
        documents.add(doc2);

        collection.insertMany(documents);
```



### 删除

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;删除数据

```java
				# 删除单条数据，等价于: db.test.deleteOne({"name":"1"})
        collection.deleteOne(new BasicDBObject().append("name","1"));
				# 删除多条数据，等价于: db.test.deleteMany({"name":"1"})
        collection.deleteMany(new BasicDBObject().append("name","1"));
```

### 修改

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;修改单条数据

```java
        UpdateResult updateResult = collection.updateOne(
                // 查询条件
                Filters.eq("name", "bigkang"),
                Updates.combine(
                        // 设置nikename为bigkang1
                        Updates.set("nikename", "bigkang1"),
                        // 设置updateTime为当前时间
                        Updates.currentDate("updateTime")));
        System.out.println(String.format("修改了%s条数据！",updateResult.getModifiedCount()));

```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;修改多条数据

```java
        UpdateResult updateResult = collection.updateMany(
                // 查询条件
                Filters.eq("name", "bigkang"),
                Updates.combine(
                        // 设置nikename为bigkang1
                        Updates.set("nikename", "bigkang1"),
                        // 设置updateTime为当前时间
                        Updates.currentDate("updateTime")));
        System.out.println(String.format("修改了%s条数据！",updateResult.getModifiedCount()));
```



### 查询

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用BasicDBObject构建对象查询

```java
        MongoCollection<Document> collection = mongoDatabase.getCollection("test");

        // 构件查询条件,等价于mongo语句db.getCollection('test').find({"name": "bigkang", "age": {"$gte": 18}})
        FindIterable<Document> documents = collection.find(new BasicDBObject()
                .append("name", "bigkang")
                .append("age", new BasicDBObject()
                        .append("$gte",18)
                )
        );
        // 遍历文档
        for (Document document : documents) {
            System.out.println(document.toJson());
        }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用Filters构建查询Bson

```java
        // 使用Filters构建查询Bson
        Bson filters = Filters.and(
                Filters.eq("name","bigkang"),
                Filters.gte("age",14));
        // 查询数据,从0开始查询1条
        FindIterable<Document> documents = collection.find(filters).limit(1).skip(0);
        // 遍历数据
        for (Document document : documents) {
            System.out.println(document.toJson());
        }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;指定字段以及排序查询

```java
       // 使用Filters构建查询Bson
        Bson filters = Filters.and(
                Filters.eq("name","bigkang"),
                Filters.gte("age",14));
        // 等价于{"find": "test", "filter": {"name": "bigkang", "age": {"$gte": 14}}, "sort": {"name": -1, "age": 1}, "projection": {"name": 1, "email": 1, "_id": 0}}
        FindIterable<Document> documents = collection
                .find(filters)
                .projection(
                        Projections.fields(
                                Projections.include("name","email"),
                                Projections.excludeId()
                        )
                ).sort(Sorts.orderBy(Sorts.descending("name"),Sorts.ascending("age")));
        // 遍历数据
        for (Document document : documents) {
            System.out.println(document.toJson());
        }
```

## 索引

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;添加索引

```java
        // age倒序,并且指定索引名称为test_index_age_desc
        collection.createIndex(Indexes.descending("age"),new IndexOptions().name("test_index_age_desc"));
        // type和level正序,并且指定索引名称为
        collection.createIndex(Indexes.ascending("type","level"),new IndexOptions().name("test_index_TypeAndLevel_asc"));
        // 创建一个复合索引，age倒序，type正序
        collection.createIndex(Indexes.compoundIndex(Indexes.descending("age"), Indexes.ascending("type")));

        String filed = "name";
        // 文字索引
        Bson text = Indexes.text(filed);
        // 正序
        Bson asc = Indexes.ascending(filed);
        // 倒序
        Bson desc = Indexes.descending(filed);
        // 复合索引
        Bson bson = Indexes.compoundIndex(asc,desc);
        // Hash索引
        Bson hashed = Indexes.hashed(filed);
        // 地理位置索引
        Bson geo2dsphere = Indexes.geo2dsphere(filed);
        // 几何索引
        Bson geo2d = Indexes.geo2d(filed);
        // 唯一索引
        IndexOptions index = new IndexOptions().name("index_name").unique(true);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;删除索引

```java
				// 根据索引名称删除索引
				collection.dropIndex("test_index");
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查询索引

```java
        // 查询所有索引
       	ListIndexesIterable<Document> indexes = collection.listIndexes();
        for (Document index : indexes) {
            System.out.println(index);
        }
```

## 批量操作

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;批量操作

```java
          // 有序批量操作-保证顺序
          collection.bulkWrite(
            Arrays.asList(new InsertOneModel<>(new Document("_id", 4)),
                          new InsertOneModel<>(new Document("_id", 5)),
                          new InsertOneModel<>(new Document("_id", 6)),
                          new UpdateOneModel<>(new Document("_id", 1),
                                               new Document("$set", new Document("x", 2))),
                          new DeleteOneModel<>(new Document("_id", 2)),
                          new ReplaceOneModel<>(new Document("_id", 3),
                                                new Document("_id", 3).append("x", 4))));


          // 无序批量操作-无法保证操作顺序
          collection.bulkWrite(
            Arrays.asList(new InsertOneModel<>(new Document("_id", 4)),
                          new InsertOneModel<>(new Document("_id", 5)),
                          new InsertOneModel<>(new Document("_id", 6)),
                          new UpdateOneModel<>(new Document("_id", 1),
                                               new Document("$set", new Document("x", 2))),
                          new DeleteOneModel<>(new Document("_id", 2)),
                          new ReplaceOneModel<>(new Document("_id", 3),
                                                new Document("_id", 3).append("x", 4))),
            new BulkWriteOptions().ordered(false));
```

## 聚合

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;简单类型聚合

```java
          // 聚合，条件为type等于第一类型，然后根据atype统计数据的条数，Mongo中没有count，直接使用sum+1
          AggregateIterable<Document> aggregate = collection.aggregate(
                  Arrays.asList(
                          Aggregates.match(Filters.eq("type", "第一类型")),
                          Aggregates.group("$atype",
                                  Accumulators.sum("count", 1))
                  )
          );
          // 打印结果
          for (Document document : aggregate) {
              System.out.println(document.toJson());
          }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;字段筛选过滤

```java
          // 将Id过滤掉，只需要name字段，以及firstCategory字段，firstCategory值为categories数组的第一个
          collection.aggregate(
                Arrays.asList(
                    Aggregates.project(
                        Projections.fields(
                              Projections.excludeId(),
                              Projections.include("name"),
                              Projections.computed(
                                      "firstCategory",
                                      new Document("$arrayElemAt", Arrays.asList("$categories", 0))
                              )
                        )
                    )
                )
          ).forEach(printBlock);
```



# table

```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
```

