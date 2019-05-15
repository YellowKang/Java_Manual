package com.example.demoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//这里设置启动的时候把他注入进去，他会自己去查找配置中的注册中心地址
@EnableDiscoveryClient
public class DemoServiceApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DemoServiceApplication.class, args);
    }
}
