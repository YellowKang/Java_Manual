package com.kang.shop.usertestserver.controller;

import com.kang.shop.jpa.base.BaseJpaController;
import com.kang.shop.usertestserver.entity.TestJpa;
import com.kang.shop.usertestserver.service.TestJpaService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test-jpa")
@Api(tags = "测试Jpa脚手架")
public class TestJpaController extends BaseJpaController<TestJpa,Long,TestJpaService> {

    @Autowired
    private TestJpaService testJpaService;

}
