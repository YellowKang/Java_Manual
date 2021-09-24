package com.kang.shop.mybatis.plus.vo;

import com.kang.shop.mybatis.plus.entity.CurrencySearch;
import lombok.Data;

import java.io.Serializable;

@Data
public class CurrencyUpdateVo<T> implements Serializable {

    private CurrencySearch<T> currencySearch;

    private T entity;
}
