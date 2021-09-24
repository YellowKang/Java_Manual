package com.kang.shop.usertestserver.controller;

import com.kang.shop.mongo.base.BaseMongoController;
import com.kang.shop.usertestserver.entity.TestMongo;
import com.kang.shop.usertestserver.service.TestMongoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test-mongo")
@Api(tags = "测试Mongo脚手架")
public class TestMongoController extends BaseMongoController<TestMongo,String,TestMongoService> {
    @Autowired
    private TestMongoService testMongoService;

}
