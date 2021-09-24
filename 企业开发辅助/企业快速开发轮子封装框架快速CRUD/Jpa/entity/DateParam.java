package com.kang.shop.jpa.entity;

import com.kang.shop.common.util.DateUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @Author BigKang
 * @Date 2019/5/13 9:20
 * @Summarize 创建时间参数
 */
@Data
public class DateParam {

    //起始时间
    private String startDate;
    //结束时间
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
