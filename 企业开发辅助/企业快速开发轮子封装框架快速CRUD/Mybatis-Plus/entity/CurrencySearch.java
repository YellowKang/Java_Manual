package com.kang.shop.mybatis.plus.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class CurrencySearch<T> implements Serializable {

    private List<InCondition> in;

    private List<InCondition> notIn;

    private List<Condition> query;

    private List<Condition> like;

    private List<BetweenCondition> between;

    private DateParam dateParam;

    private List<Condition> not;

    private PageRequest<T> pageOrder;

}
