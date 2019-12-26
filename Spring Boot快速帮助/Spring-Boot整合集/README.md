# 文档

​		编写文档是为了方便记忆学习过的东西，在很多时候我们学了一项新的技术之后，如果我们一段时间不去使用就会陌生，而长时间不使用则会忘记很多，并且也能记录自己的学习过程，丰富自己的知识库，并且还能帮助我们快速定位问题和解决，但是当很多的时候我们我们通过各种方法百度或者查询博客都解决不了的时候，最好还是查阅官方文档，这里我会放入很多的官方文档的链接提供大家快速查找。

注：此文档地址都是Spring官方所整合的各项技术官网，并非官方地址，如Redis为Spring-Data-Redis。

# SpringBoot如何整合各项技术依赖的？

​		SpringBoot官方帮助我们集成了非常多的常用技术，并且进行了一些封装，让我们的开发更加方便快捷，主要是因为他将多项技术的依赖版本整合到了一起，这样就减少了我们在构建项目的时候所引入的依赖的版本冲突，我们在初始化SpringBoot的时候他会自动帮助我们添加依赖。

​		依赖如下，他表示我们的父工程是spring-boot-starter-parent

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
```

​		我们进入这个父工程，这个父工程它是一个pom工程，那么我们知道pom的主要功能就是帮助我们定义依赖版本，它里面帮助我们默认整合了非常多的版本的Spring家族的依赖。

​		我们点击进入spring-boot-starter-parent。我们会发现他还有个parent。这个parent叫做spring-boot-dependencies，表示是spring-boot的依赖版本定义。

```xml
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>2.1.7.RELEASE</version>
    <relativePath>../../spring-boot-dependencies</relativePath>
  </parent>
```

​		我们看看这个dependencies定义了什么：

```xml
  <properties>
    <activemq.version>5.15.9</activemq.version>
    <antlr2.version>2.7.7</antlr2.version>
    <appengine-sdk.version>1.9.76</appengine-sdk.version>
    <artemis.version>2.6.4</artemis.version>
    <aspectj.version>1.9.4</aspectj.version>
    <assertj.version>3.11.1</assertj.version>
    <atomikos.version>4.0.6</atomikos.version>
    <bitronix.version>2.1.4</bitronix.version>
    <build-helper-maven-plugin.version>3.0.0</build-helper-maven-plugin.version>
    <byte-buddy.version>1.9.16</byte-buddy.version>
    <caffeine.version>2.6.2</caffeine.version>
    <cassandra-driver.version>3.6.0</cassandra-driver.version>
    <classmate.version>1.4.0</classmate.version>
    <commons-codec.version>1.11</commons-codec.version>
    <commons-dbcp2.version>2.5.0</commons-dbcp2.version>
    <commons-lang3.version>3.8.1</commons-lang3.version>
    <commons-pool.version>1.6</commons-pool.version>
    。。。。。。还有很多
  </properties>
```

​			就是这个spring-boot-dependencies，帮助我们定义了许多的依赖的版本，减少了依赖太多时的版本冲突的问题。

# Elasticsearch

官方文档地址：[点击进入](https://docs.spring.io/spring-data/elasticsearch/docs/)点击相应的版本，然后选择reference，然后点击html即可进入相应文档。

如果是3.1.12的RELEASE版本则是下面的路径。

```
https://docs.spring.io/spring-data/elasticsearch/docs/3.1.12.RELEASE/reference/html/
```

## 依赖配置

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>
```

# MongoDB

官方文档地址：[点击进入](https://docs.spring.io/spring-data/mongodb/docs/) ，点击相应的版本，然后选择reference，然后点击html即可进入相应文档。

如果是2.1.7的RELEASE版本则是下面的路径。

```
https://docs.spring.io/spring-data/mongodb/docs/2.1.7.RELEASE/reference/html/
```

## 依赖配置

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
```

