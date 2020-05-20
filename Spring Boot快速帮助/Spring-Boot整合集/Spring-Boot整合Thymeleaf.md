# 整合Thymeleaf

## 引入依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
```

## 编写配置文件

```properties
server:
  port: 8881
  servlet:
    context-path: /generator #上下文路径
spring:
  thymeleaf:
    cache: false #关闭缓存
    check-template: true #检查模板
    check-template-location: true #监察模板是否存在
    encoding: UTF-8 #模板编码
    mode: HTML #模型
    prefix: classpath:/templates/ #页面解析器前缀
    suffix: .html #页面解析器后缀

```

并且新建/resources/templates/page/index.html

内容如下

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
</head>
<body>
    <h4>亲爱的<span th:text="${name}"></span>，你好！</h4>
</body>
</html>
```

## 编写控制器

新建IndexController，然后启动项目，我们访问

http://localhost:8881/generator/?name=bigkang

```java
/**
 * @Author BigKang
 * @Date 2020/4/27 6:03 下午
 * @Summarize 首页控制器
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(HttpServletRequest request, @RequestParam(value = "name", defaultValue = "springboot-thymeleaf") String name){
        request.setAttribute("name",name);
        return "/page/index";
    }

}
```

我们就能根据参数访问相应的页面了

## 引入css

我们使用th:href="@{/css/login/login.css}"

即可引入/css/login/login.css，这里指的是当前服务器的路径下的目录，也就是resource下的static目录或其他的静态目录下的静态资源，这里会跟随上下文路劲进行切换。

```html
 <link th:href="@{/css/login/login.css}" rel='stylesheet' type='text/css' media="all">
```

## 引入js

同样我们使用th:src进行引入js

```html
<script th:src="@{/js/Chart.bundle.min.js}"></script>
```

## 引入图片

```html
<img th:src="@{/images/favicon.ico}" alt="Logo">
```

## 组件模块化

组建的模块化我们使用

```
th:fragment						导出的代码片段

th:include						引入代码块
th:replace						替换代码块	
th:insert							插入代码块
```

首先我们配置了模板引擎的位置，在classpath下的templates

那么我们新建一个common

并且在下方新建一个test.html

```html
<!DOCTYPE html>
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:http="http://www.w3.org/1999/xhtml"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
<p th:fragment="frg">
			1111
</p>
</html>
```

那么我们的代码模块就是

```html
<p th:fragment="frg">
			1111
</p>
```

我们在其他的页面需要进行引入，则为

我们这里采用替换的方式，我们寻找common下的test不需要加.html，下面的frg代码片段

```html
<div th:replace="/common/test :: frg"></div>
```

替换后此处代码变为

```
<p>1111</p>
```

如果使用include或者insert则为

```
<div><p>1111</p></div>
```

