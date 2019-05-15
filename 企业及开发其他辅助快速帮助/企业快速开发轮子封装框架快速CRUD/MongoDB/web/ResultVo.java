package com.kang.shop.common.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo implements Serializable {

    private Object data;
    private Integer code;
    private String message = Message.OK;

    public ResultVo(Object data,Integer code) {
        this.code = code;
        this.data = data;
    }

    public ResultVo(Integer code) {
        this.code = code;
    }

    public ResultVo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultVo result(Integer code, String message){
        return new ResultVo(code,message);
    }

    public static ResultVo result(Integer code){
        return new ResultVo(code);
    }

    public static ResultVo result(Object data,Integer code, String message){
        return new ResultVo(data,code,message);
    }

    public static ResultVo result(Object data,Integer code){
        return new ResultVo(data,code);
    }
}
