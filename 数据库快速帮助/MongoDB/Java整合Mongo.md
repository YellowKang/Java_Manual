# 官网

​		首先我们定位到MongoDB的JavaAPI官网: [点击进入](https://mongodb.github.io/mongo-java-driver/4.0/driver/)

# 引入依赖

​		传统驱动

```xml
<dependencies>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.0.5</version>
    </dependency>
</dependencies>
```

​		响应式驱动（异步非阻塞）

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

​		下面是Mongo官网中对应的驱动版本的支持，低版本向上兼容

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