package com.kang.shop.jpa.entity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2019/5/13 9:22
 * @Summarize 分页排序实体类
 */
public class PageRequest {

    Integer page = 1;
    Integer limit = 20;
    List<Order> orders = new ArrayList();

    public List<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Pageable pageable() {
        Pageable pageable = new org.springframework.data.domain.PageRequest(this.page - 1, this.limit, this.sorter());
        return pageable;
    }

    public PageRequest() {
    }

    public PageRequest(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit;
    }

    public PageRequest(Integer page, Integer limit, List<Order> orders) {
        this.page = page;
        this.limit = limit;
        this.orders = orders;
    }

    public PageRequest(Integer page, Integer limit, Order... orders) {
        this.page = page;
        this.limit = limit;
        Order[] var4 = orders;
        int var5 = orders.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Order o = var4[var6];
            this.orders.add(o);
        }

    }

    public Sort sorter() {
        if (this.orders.size() == 0) {
            this.orders.add(new Order(Sort.Direction.DESC, "createTime"));
        }

        List<Sort.Order> sorters = new ArrayList();
        Iterator var2 = this.orders.iterator();

        while(var2.hasNext()) {
            Order order = (Order)var2.next();
            sorters.add(new Sort.Order(order.getDirection(), order.getOrderBy()));
        }

        return new Sort(sorters);
    }

}
