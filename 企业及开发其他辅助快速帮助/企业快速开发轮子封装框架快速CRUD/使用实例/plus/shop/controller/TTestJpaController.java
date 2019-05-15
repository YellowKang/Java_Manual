package com.kang.shop.usertestserver.plus.shop.controller;


import com.kang.shop.mybatis.plus.base.BaseMPController;
import com.kang.shop.usertestserver.plus.shop.entity.TTestJpa;
import com.kang.shop.usertestserver.plus.shop.service.TTestJpaService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄康
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/shop/t-test-jpa")
@Api(tags = "测试Mybatis-plus脚手架")
public class TTestJpaController extends BaseMPController<TTestJpa,Long,TTestJpaService> {

}

