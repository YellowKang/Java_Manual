package com.example.demofeign.contrller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

//通过客户端的远程调用,调用My-Eureka-Registration-Server-Service这个项目，也就是上面两个服务的两个项目
@FeignClient("My-Eureka-Registration-Server-Service")
public interface Services {

    //这里是调用的服务的请求，切记要和服务里的一样
    @GetMapping("/eurekaSev")
    String getEure();
}
