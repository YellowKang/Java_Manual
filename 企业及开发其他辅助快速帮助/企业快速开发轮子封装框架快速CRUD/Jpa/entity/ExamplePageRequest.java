package com.kang.shop.jpa.entity;

/**
 * 分页查询，条件请求封装
 * @param <T>
 */
public class ExamplePageRequest<T> extends ExampleRequest<T> {
    private PageRequest page = new PageRequest();

    public ExamplePageRequest() {
    }

    public PageRequest page() {
        return this.page;
    }

    public PageRequest getPage() {
        return this.page;
    }

    public void setPage(PageRequest page) {
        this.page = page;
    }

    public void page(PageRequest page) {
        this.page = page;
    }
}
