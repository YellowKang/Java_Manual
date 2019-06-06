package com.kang.shop.usertestserver.controller;

import com.kang.shop.es.base.BaseEsController;
import com.kang.shop.usertestserver.entity.TestEs;
import com.kang.shop.usertestserver.service.TestEsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test-es")
@Api(tags = "测试Es脚手架")
public class TestEsController extends BaseEsController<TestEs,String,TestEsService> {

    @Autowired
    private TestEsService testEsService;

}
