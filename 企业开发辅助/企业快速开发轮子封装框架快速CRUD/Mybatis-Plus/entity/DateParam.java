package com.kang.shop.mybatis.plus.entity;

import com.kang.shop.common.util.DateUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;

@Data
public class DateParam {

    private String startDate;

    private String endDate;

    public Date start() throws ParseException {
        return DateUtil.StringToDate(this.startDate);
    }

    public Date end() throws ParseException {
        return DateUtil.StringToDate(this.endDate);
    }

    public Boolean yesNull(){
        if(StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)){
            return true;
        }else {
            return false;
        }
    }
}
