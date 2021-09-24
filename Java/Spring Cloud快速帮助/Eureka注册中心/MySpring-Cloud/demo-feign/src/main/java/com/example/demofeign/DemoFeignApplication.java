package com.example.demofeign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//这里表示我们是客户端，将自己注册成客户端
@EnableFeignClients
//这里设置启动的时候把他注入进去，他会自己去查找配置中的注册中心地址
@EnableDiscoveryClient
@SpringBootApplication
public class DemoFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoFeignApplication.class, args);
    }
}
