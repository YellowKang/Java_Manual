package com.kang.shop.mybatis.plus.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //给createTime"这个属性在插入的时候创建一个时间
        this.setFieldValByName("createTime",new Date(), metaObject);

        //给updateTime这个属性在插入的时候创建一个时间
        this.setFieldValByName("updateTime",new Date(), metaObject);

        //给version这个属性在插入的时候标记为1
        this.setFieldValByName("version",1, metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //给updateTime这个属性在修改的时候将时间改为最新的new Date（）
        this.setFieldValByName("updateTime",new Date(), metaObject);

    }
}
