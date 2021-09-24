package com.kang.shop.mongo.base;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author BigKang
 * @Date 2019/5/8 14:24
 * @Summarize MongoBase实体父类
 */
@Data
public class BaseMongoEntity implements Serializable {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 创建时间
     */
    @CreatedDate
    protected Date createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    protected Date updateTime;


}
