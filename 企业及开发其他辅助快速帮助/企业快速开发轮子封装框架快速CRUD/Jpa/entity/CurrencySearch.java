package com.kang.shop.jpa.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2019/5/13 9:19
 * @Summarize 多功能通用查询接口类
 */
@Data
@ToString
public class CurrencySearch<T> implements Serializable {

    private List<InCondition> in;

    private List<Condition> query;

    private List<Condition> like;

    private List<BetweenCondition> between;

    private DateParam dateParam;

    private List<Condition> not;

    private PageRequest pageOrder;


}
