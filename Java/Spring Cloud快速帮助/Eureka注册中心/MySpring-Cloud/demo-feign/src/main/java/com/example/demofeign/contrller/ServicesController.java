package com.example.demofeign.contrller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServicesController {

    //调用我们的调用远程服务的接口，将他注入进来
    @Autowired
    private Services services;


    //这里是客户端的访问路径，可以自定义
    @GetMapping("/getServices")
    public String getEureka(){
        return services.getEure();
    }
}
