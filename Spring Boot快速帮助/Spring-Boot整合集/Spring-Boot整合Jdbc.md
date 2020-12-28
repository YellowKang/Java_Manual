# 引入依赖







# 不整合SpringBoot

​		引入依赖

```xml
       	<!-- 引入JDBC -->
				<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
        </dependency>
				<!-- 引入HikariCP连接池 -->
        <dependency>
          <groupId>com.zaxxer</groupId>
          <artifactId>HikariCP</artifactId>
          <version>3.4.5</version>
          <scope>compile</scope>
        </dependency>
				<!-- 连接MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.22</version>
        </dependency>
```

​		直接连接

```java
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername("root");
        config.setPassword("123456");
        config.setJdbcUrl("jdbc:mysql://192.168.1.11:3306/test-kang?useUnicode=true&useSSL=false");
        DataSource dataSource  = new HikariDataSource(config);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Map<String, Object> map = jdbcTemplate.queryForMap("select  * from t_user");
        System.out.println(map);
```

