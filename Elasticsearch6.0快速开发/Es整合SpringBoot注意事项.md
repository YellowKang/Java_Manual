# 整合时引入健康组件

如果我们引入SpringBootAdmin他会使用健康组件，那么他会监视es，会一直发起请求，请求的默认的路径是127.0.0.1，那么就会报错保存信息如下

![](img\boot-es-error.png)

他回去一直请求es的健康状态会一直报错，有两种解决方式都可以在配置文件中配置

第一种，配置es的rest地址

properties版本

```
spring.elasticsearch.jest.uris[0]=http://111.67.196.127:9200
```

yml版本

```
spring:
    elasticsearch:
      jest:
        uris: ["http://111.67.196.127:9200"]
```

第二种，排除掉检查es

properties版本

```
management.health.elasticsearch.enabled=false
```

yml版本

```
management:
    health:
        elasticsearch:
            enabled: false
```

