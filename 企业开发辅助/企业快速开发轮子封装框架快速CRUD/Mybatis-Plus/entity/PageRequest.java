package com.kang.shop.mybatis.plus.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageRequest<T> {

    private Integer page;
    private Integer size;
    private List<PageOrder> order;

    public Page<T> generatePage(){
        if(page == null || size == null || page < 1 || size < 1){
            return new Page<>(1,10);
        }
        return new Page<>(page,size);
    }
}
