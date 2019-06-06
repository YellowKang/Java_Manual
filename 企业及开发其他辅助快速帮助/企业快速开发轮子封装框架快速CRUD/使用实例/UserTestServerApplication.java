package com.kang.shop.usertestserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.kang.shop.common.web.config",
        "com.kang.shop.usertestserver",
        "com.kang.shop.mongo",
        "com.kang.shop.jpa",
        "com.kang.shop.mybatis.plus",
        "com.kang.shop.es"
})
@MapperScan("com.kang.shop.usertestserver.plus")
public class UserTestServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserTestServerApplication.class, args);
    }

}
