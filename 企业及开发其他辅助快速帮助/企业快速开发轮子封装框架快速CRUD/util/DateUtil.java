package com.kang.shop.common.util;


import org.jsoup.helper.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author BigKang
 * @Date 2019/5/8 12:12
 * @Summarize 时间格式工具类
 */
public class DateUtil {

    /**
     * String转换Date类型
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date StringToDate(String date) {
        Date result;
        String parse = date.replaceFirst("[0-9]{4}([^0-9]?)", "yyyy$1");
        parse = parse.replaceFirst("^[0-9]{2}([^0-9]?)", "yy$1");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1MM$2");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}( ?)", "$1dd$2");
        parse = parse.replaceFirst("( )[0-9]{1,2}([^0-9]?)", "$1HH$2");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1mm$2");
        parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)", "$1ss$2");
        DateFormat format = new SimpleDateFormat(parse);
        try {
            result = format.parse(date);
        } catch (ParseException e) {
            result = null;
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Date类型转String
     * @param date
     * @return
     * @throws ParseException
     */
    public static String DateToString(Date date) {
        if(date == null){
            return null;
        }
        try {
            return DateToString(date,null);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DateToString(Date date,String pattern) throws ParseException {
        if(date == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat;

        if(StringUtil.isBlank(pattern)){
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else{
            simpleDateFormat = new SimpleDateFormat(pattern);
        }

        return simpleDateFormat.format(date);
    }

    /**
     * 时间转秒
     * @return
     */
    public static Integer DateToSeconds(Date date){
        if(date == null){
            return null;
        }
        Integer time = Math.toIntExact(date.getTime() / 1000);
        return time;
    }
}
