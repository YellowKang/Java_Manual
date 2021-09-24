package com.kang.shop.jpa.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2019/5/13 9:19
 * @Summarize 条件
 */
@Data
public class Condition {

    @NotEmpty
    private String field;

    @NotEmpty
    private Object value;

    public String getField(){
        if(field.equals("String") || field.equals("string")){
            return null;
        }
        return this.field;
    }

}
