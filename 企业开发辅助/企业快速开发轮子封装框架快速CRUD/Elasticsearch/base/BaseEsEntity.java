package com.kang.shop.es.base;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEsEntity implements Serializable {

    @Id
    protected String id;

    @CreatedDate
    protected Date createTime;

    @LastModifiedDate
    protected Date updateTime;

}
