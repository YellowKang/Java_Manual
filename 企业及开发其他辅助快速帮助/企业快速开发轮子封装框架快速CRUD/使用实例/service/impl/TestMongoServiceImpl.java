package com.kang.shop.usertestserver.service.impl;

import com.kang.shop.mongo.base.BaseMongoServiceImpl;
import com.kang.shop.usertestserver.dao.TestMongoDao;
import com.kang.shop.usertestserver.entity.TestMongo;
import com.kang.shop.usertestserver.service.TestMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Primary
public class TestMongoServiceImpl extends BaseMongoServiceImpl<TestMongo,String,TestMongoDao> implements TestMongoService {

    @Autowired
    private TestMongoDao testMongoDao;

}
