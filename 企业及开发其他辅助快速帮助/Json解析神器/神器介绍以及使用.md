# 简介

​	JsonPath 是一种信息抽取类库，是从JSON文档中抽取指定信息的工具，提供多种语言，解析Json通过表达式获取

# 使用

## 引入依赖

引入maven依赖

```
       	<dependency>
            <groupId>com.jayway.jsonpath</groupId>
            <artifactId>json-path</artifactId>
            <version>2.4.0</version>
        </dependency>
```

## 代码实现

```
String s = JSONObject.parse("[{\"cbf\":\"0\",\"id\":\"J_100000060090\",\"m\":\"5899.00\",\"op\":\"4899.00\",\"p\":\"4499.00\"}]").toString();
        System.out.println(JsonPath.parse(s).read("$[0].p",Object.class));
```

我们先获取一串json字符串，然后使用JsonPath进行转换然后获取它的数组第一个，的p这个属性，并且将它转换为Object类型



博客介绍：

<https://www.cnblogs.com/wynjauu/articles/9556396.html> 