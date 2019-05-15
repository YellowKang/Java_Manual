package com.kang.shop.mybatis.plus.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PageOrder {

    //排序字段
    @NotEmpty
    private String field;

    //排序方式
    @NotEmpty
    private String sort = "DESC";

}
