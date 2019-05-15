package com.example.demoservice.contrller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EurekaSev {

    //项目基本操作
    @GetMapping("/eurekaSev")
    public String getEure(){
        //我们把两个服务的输出内容改一下，方便区别他们是哪一个服务，是不是在轮询
        return "Hello Eureka!1";
    }
}
