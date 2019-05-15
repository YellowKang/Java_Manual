package com.kang.shop.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * 封装排序实体类
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
