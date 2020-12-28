package com.test.mp.security.util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2020/12/24 4:48 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize Url工具类
 */
public class UrlUtil {

    /**
     * 表达式关键字前缀表达式 ${
     */
    private static final String EXPRESSION_KEYWORDS_PREFIX = "${";

    /**
     * 表达式关键字后缀表达式 }
     */
    private static final String EXPRESSION_KEYWORDS_SUFFIX = "}";

    /**
     * 表达式分隔符
     */
    private static final String EXPRESSION_KEYWORDS_DELIMITER = ":";


    /**
     * 表达式类型
     */
    private static final String EXPRESSION_TYPE_INT = "int";
    private static final String EXPRESSION_TYPE_TIME = "time";
    private static final String EXPRESSION_TYPE_LIST = "list";

    /**
     * 表达式类型分隔符
     */
    private static final String EXPRESSION_TYPE_LIST_DELIMITER  = ",";
    private static final String EXPRESSION_TYPE_INT_DELIMITER  = "-";

    /**
     * 根据Url表达式生成Url
     * @param url 表达式Url
     *    示例: http://baidu.com?time=${time:yyyy-MM-dd}&page=${list:1,2,3,4,5,6,7,8}
     * @return
     */
    public static List<String> genUrl(String url) {
        // 是否包含表达式关键字
        boolean contains = url.contains(EXPRESSION_KEYWORDS_PREFIX);
        List<String> list = new ArrayList<>();
        if (contains) {
            String newUrl = url;
            List<Expression> expressions = new ArrayList<>();
            // 最多允许9层表达式,根据Url解析出表达式
            for (int i = 0; i < 10; i++) {
                int startIndex = newUrl.indexOf(EXPRESSION_KEYWORDS_PREFIX);
                if (startIndex == -1) {
                    break;
                }
                newUrl = newUrl.substring(startIndex);
                int endIndex = newUrl.indexOf(EXPRESSION_KEYWORDS_SUFFIX);
                if (endIndex == -1) {
                    break;
                }
                // 截取后的表达式,如： ${list:1,3,5}
                String str = newUrl.substring(0, endIndex + 1);
                Expression expression = new Expression();
                // 根据分隔符 ":" 拆分表达式
                String[] split = str.split(EXPRESSION_KEYWORDS_DELIMITER);
                if (split.length < 2) {
                    throw new RuntimeException("表达式不合法");
                }

                // 解析表达式的类型，格式化，以及格式化后的值
                String type = split[0].substring(EXPRESSION_KEYWORDS_PREFIX.length());
                String format = split[1].substring(0, split[1].length() - EXPRESSION_KEYWORDS_SUFFIX.length());
                expression.setValue(str);
                expression.setType(type);
                expression.setFormat(format);
                expression.setFormatValue(formatByType(type, format));
                expressions.add(expression);
                newUrl = newUrl.substring(endIndex + 1);
            }
            List<String> urls = new ArrayList<>();
            Boolean first = true;
            // 循环生成表达式
            for (Expression expression : expressions) {
                List<String> newUrls = new ArrayList<>();
                // 获取表达式值
                String value = expression.getValue();
                if (first) {
                    // 第一次截取原Url
                    first = false;

                    // 根据表达式截取后的值
                    String[] split = urlSplit(url, value);
                    for (String data : expression.getFormatValue()) {
                        newUrls.add(split[0]+data+split[1]);
                    }
                } else {
                    for (String urlStr : urls) {
                        String[] split = urlSplit(urlStr, value);
                        for (String data : expression.getFormatValue()) {
                            newUrls.add(split[0]+data+split[1]);
                        }
                    }
                }
                urls = newUrls;
            }
            list = urls;
        } else {
            list.add(url);
        }
        return list;
    }

    /**
     * 将字符串切割成两个字符数组
     *
     * @param url   原来的Url
     * @param split 分割符
     * @return
     */
    public static String[] urlSplit(String url, String split) {
        String[] array = new String[2];
        int index = url.indexOf(split);
        if (index == -1) {
            return null;
        }
        array[0] = url.substring(0, index);
        array[1] = url.substring(index + split.length());
        return array;
    }

    /**
     * 根据表达式以及类型，解析出相应的结果
     * @param type 类型
     * @param format 格式
     * @return
     */
    public static List<String> formatByType(String type, String format) {
        List<String> list = new ArrayList<>();
        if (EXPRESSION_TYPE_INT.equals(type)) {
            String[] values = format.split(EXPRESSION_TYPE_INT_DELIMITER);
            if (values.length < 2) {
                throw new RuntimeException("int表达式错误!");
            } else {
                Long start = Long.valueOf(values[0]);
                Long end = Long.valueOf(values[1]);
                for (Long i = start; i <= end; i++) {
                    list.add(i.toString());
                }
            }
        } else if (EXPRESSION_TYPE_TIME.equals(type)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                list.add(sdf.format(new Date()));
            } catch (Exception e) {
                throw new RuntimeException("时间格式化错误!");
            }
        } else if (EXPRESSION_TYPE_LIST.equals(type)) {
            String[] values = format.split(EXPRESSION_TYPE_LIST_DELIMITER);
            for (String value : values) {
                list.add(value);
            }
        }
        return list;
    }


    static class Expression {
        /**
         * 类型(list,date,int)
         */
        private String type;

        /**
         * 表达式值,例如：${list:1,2,3} OR ${time:yyyy-MM-dd} OR ${int:1-10}
         */
        private String value;

        /**
         * 格式化,如解析出来的value，1,2,3 OR yyyy-MM-dd OR 1-10
         */
        private String format;

        /**
         * 格式化后的值,根据表达式类型以及格式化将字符串转换为集合
         */
        private List<String> formatValue;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public List<String> getFormatValue() {
            return formatValue;
        }

        public void setFormatValue(List<String> formatValue) {
            this.formatValue = formatValue;
        }
    }
}
