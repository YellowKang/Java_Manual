package com.kang.shop.usertestserver.service.impl;

import com.kang.shop.es.base.BaseEsServiceImpl;
import com.kang.shop.usertestserver.dao.TestEsDao;
import com.kang.shop.usertestserver.entity.TestEs;
import com.kang.shop.usertestserver.service.TestEsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@Slf4j
@Primary
public class TestEsSerivceImpl extends BaseEsServiceImpl<TestEs,String,TestEsDao> implements TestEsService {

    @Autowired
    private TestEsDao testEsDao;

}
