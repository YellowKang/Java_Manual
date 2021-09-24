package com.kang.shop.usertestserver.service.impl;

import com.kang.shop.jpa.base.BaseJpaServiceimpl;
import com.kang.shop.usertestserver.dao.TestJpaDao;
import com.kang.shop.usertestserver.entity.TestJpa;
import com.kang.shop.usertestserver.service.TestJpaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@Slf4j
@Primary
public class TestJpaServiceImpl extends BaseJpaServiceimpl<TestJpa,Long,TestJpaDao> implements TestJpaService {

    @Autowired
    private TestJpaDao testJpaDao;

}
