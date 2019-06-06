package com.kang.shop.mybatis.plus.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class InCondition {

    @NotEmpty
    private String field;

    @NotEmpty
    private List<Object> value;

    public String getField(){
        if(field.equals("String") || field.equals("string")){
            return null;
        }
        return this.field;
    }

}
