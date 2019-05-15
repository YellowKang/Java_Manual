package com.spring.boot.test.mybatis;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseMPController<T, PK , M extends IService<T>> {

    @Autowired
    protected M baseService;

}
