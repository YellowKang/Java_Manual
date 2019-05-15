package com.kang.shop.mybatis.plus.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class BetweenCondition {

    @NotEmpty
    private String field;

    private Object start;

    private Object end;

}
