package com.kang.shop.jpa.entity;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

/**
 * 封装查询请求
 * @param <T>
 */
public class ExampleRequest<T> {
    T example;

    public ExampleRequest() {
    }

    public T getExample() {
        return this.example;
    }

    public void setExample(T example) {
        this.example = example;
    }

    public Example<T> example() {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnorePaths(new String[]{"entityName", "deleted", "id", "createTime", "updateTime"});
        Example<T> example = Example.of(this.getExample(), matcher);
        return example;
    }

    public Example<T> example(ExampleMatcher matcher) {
        Example<T> example = Example.of(this.getExample(), matcher);
        return example;
    }

}
