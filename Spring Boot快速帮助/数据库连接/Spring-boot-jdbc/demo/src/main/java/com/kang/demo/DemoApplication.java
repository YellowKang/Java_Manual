package com.kang.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//自动扫面同级包及其子包
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
//        SpringBoot启动项
        SpringApplication.run(DemoApplication.class, args);
    }
}
