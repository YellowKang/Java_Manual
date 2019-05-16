package com.kang.shop.jpa.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
/**
 * @Author BigKang
 * @Date 2019/5/13 9:18
 * @Summarize Betwenn条件，起始到结束
 */
@Data
public class BetweenCondition {

    @NotEmpty
    private String field;

    private Object start;

    private Object end;

}
