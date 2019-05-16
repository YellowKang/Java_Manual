package com.kang.shop.jpa.entity;

import lombok.Data;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * @Author BigKang
 * @Date 2019/5/13 9:22
 * @Summarize spring-data排序实体类
 */
@Data
public class Order implements Serializable {

    private String orderBy;
    private Sort.Direction direction;

    public Order() {
        this.direction = Sort.Direction.DESC;
    }
    public Order(Sort.Direction direction, String orderBy) {
        this.direction = Sort.Direction.DESC;
        this.orderBy = orderBy;
        this.direction = direction;
    }
}
