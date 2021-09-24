package com.kang.shop.usertestserver.plus.shop.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kang.shop.common.web.Code;
import com.kang.shop.common.web.Message;
import com.kang.shop.common.web.ResultVo;
import com.kang.shop.mybatis.plus.base.BaseMPController;
import com.kang.shop.mybatis.plus.entity.CurrencySearch;
import com.kang.shop.usertestserver.plus.shop.entity.TTestJpa;
import com.kang.shop.usertestserver.plus.shop.service.TTestJpaService;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

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

    @GetMapping("ttestas")
    public ResultVo test() throws ParseException {
        CurrencySearch<TTestJpa> currency = new CurrencySearch<>();
        QueryWrapper<TTestJpa> tTestJpaQueryWrapper = searchPageQueryWrapper(currency);
        IPage<TTestJpa> page = baseService.page(currency.getPageOrder().generatePage(), tTestJpaQueryWrapper);
        return ResultVo.result(page,Code.OK_CODE,Message.OK);
    }

}

