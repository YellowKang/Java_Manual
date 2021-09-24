# 官方地址 

​		GitHub地址：[点击进入](https://github.com/alibaba/easyexcel)

​		  语 雀 地 址：[点击进入](https://www.yuque.com/easyexcel/doc/easyexcel)

# 引入依赖

```xml
        <!-- https://mvnrepository.com/artifact/com.alibaba/easyexcel -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>easyexcel</artifactId>
            <version>2.1.3</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.0</version>
            <scope>provided</scope>
        </dependency>
```

# 进行读取

### 读取单个第一个sheet数据

创建读取实体

```java
@Data
public class Demo {
		// 姓名
    private String name;
    // 成绩
    private Long scope;
}
```

创建读取监听器

注意此处需要继承然后定义泛型并且实现方法

```java
package com.cloud.demo.actuator.test.domain;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Data
public class DemoListener extends AnalysisEventListener<Demo> {

    private List<Demo> list;

    public DemoListener() {
        list = new CopyOnWriteArrayList<>();
    }

    /**
     * 每读取到一条数据执行一次
     * @param data
     * @param analysisContext
     */
    @Override
    public void invoke(Demo data, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", data);
        list.add(data);
    }

    /**
     * 解析完成进行回调
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("解析数据完成");
        log.info("一共：读取{}条数据",list.size());
    }
}
```

使用main方法执行

```java
public class TestEsayExcel {
    public static void main(String[] args) {
        DemoListener listener = new DemoListener();
        EasyExcel.read("/Users/bigkang/Documents/笔记/Java_Manual/企业及开发其他辅助快速帮助/Excel表格快速帮助/test.xlsx", Demo.class,listener).sheet().doRead();
    }
}
```

然后我们查看日志,注意第一行的数据不会被读取他是从第二行开始读取

### 读取指定sheet数据

```java
public class TestEsayExcel {
    public static void main(String[] args) {
        // 创建reader对象
        ExcelReader excelReader = EasyExcel.read("/Users/bigkang/Documents/笔记/Java_Manual/企业及开发其他辅助快速帮助/Excel表格快速帮助/	test.xlsx", Demo.class, new DemoListener()).build();
        // 读取第几个Sheet，从索引指定0表示第一个依次类推，1表示第二个Sheet
        ReadSheet readSheet = EasyExcel.readSheet(1).build();
        // 读取Sheet
        excelReader.read(readSheet);
        // 关闭读取
        excelReader.finish();
    }
}
```

### 读取所有sheet

```java
public class TestEsayExcel {
    public static void main(String[] args) {
        DemoListener listener = new DemoListener();
        EasyExcel.read("/Users/bigkang/Documents/笔记/Java_Manual/企业及开发其他辅助快速帮助/Excel表格快速帮助/test.xlsx", Demo.class,listener).doReadAll();
    }
}
```

### 读取多个指定sheet

我们还能读取多个sheet并且指定不同的监听和实体

```java
public class TestEsayExcel {
    public static void main(String[] args) {
        ExcelReader excelReader = EasyExcel.read("/Users/bigkang/Documents/笔记/Java_Manual/企业及开发其他辅助快速帮助/Excel表格快速帮助/test.xlsx").build();
        // 这里为了简单 所以注册了 同样的head 和Listener 自己使用功能必须不同的Listener
        ReadSheet sheet1 =
                EasyExcel.readSheet(0).head(Demo.class).registerReadListener(new DemoListener()).build();

        // 创建读取的sheet
        ReadSheet sheet2 =
                EasyExcel.readSheet(1).head(DemoAK.class).registerReadListener(new DemoAkListener()).build();
        // 传入指定sheet
        excelReader.read(sheet1, sheet2);

        // 关闭
        excelReader.finish();
    }
}
```

### 使用字段名或者索引读取

表示读取第一行的字段名，为姓名的，index表示第几列读取，以索引计算2表示第三列

```java
@Data
public class Demo {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("年龄")
    private Long age;

    @ExcelProperty(index = 2)
    private Long scope;
}
```

Excel表格数据如下

```
姓名				年龄			成绩
黄康				 19				99
BigKang		  21				98
YellowKang	24				100
```

不用修改Listener监听再次执行

```java
public class TestEsayExcel {
    public static void main(String[] args) {
        DemoListener listener = new DemoListener();
        EasyExcel.read("/Users/bigkang/Documents/笔记/Java_Manual/企业及开发其他辅助快速帮助/Excel表格快速帮助/test.xlsx", Demo.class,listener).sheet().doRead();
    }
}
```

### 转换器

##### 时间转换器

时间字段上加上注解即可转换时间格式

```java
    @ExcelProperty(value = "生日")
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    private String birthday;
```

##### 浮点转换器

将小数点转为小数点后两位

```java
    @ExcelProperty(value = "价格")
    @NumberFormat("#.##元")
    private String price;
```

### 读取动态表头以及行列

​		大多数场景下我们是根据表头属性进行读取的，那么可能在某些场景下我们可能需要读取不定的列，

​		如下图所示：

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1626576937792.png)

​		对于如下的这种多行的列，那么我们怎么去读取呢？我们可以使用如下ReadListener去进行监听读取行列，自己实现返回DTO

```java

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.botpy.vosp.admin.business.supplier.dto.SupplierImportPriceDto;
import com.botpy.vosp.admin.business.supplier.dto.SupplierServicePriceDto;
import com.botpy.vosp.admin.enums.AdminResultCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class ImportPriceListener implements ReadListener {


    /**
     * 读取Header头中的服务
     */
    private List<String> service = new ArrayList<>();

    /**
     * 导入数据Data
     */
    private List<SupplierImportPriceDto> importData = new ArrayList<>();

    /**
     * 存储导入结果值，默认没发生异常
     */
    private boolean error = false;

    /**
     * 异常Code
     */
    private AdminResultCodeEnum codeEnum;


    @Override
    public void onException(Exception e, AnalysisContext analysisContext) {
        // 设置导入结果异常
        error = true;
        // 设置异常Code
        if (e.getClass().equals(NumberFormatException.class)) {
            codeEnum = AdminResultCodeEnum.IMPORT_CITY_PRICE_FORMAT_ERROR;
        } else {
            codeEnum = AdminResultCodeEnum.IMPORT_CITY_PRICE_ERROR;
        }
        log.error("导入Excel异常:{},异常信息{}:{}", codeEnum, e.getClass(), e.getMessage());
    }

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        // 如果异常直接跳过
        if (error) {
            return;
        }
        // 跳过表头，表头有两级
        if (analysisContext.readRowHolder().getRowIndex() > 1) {
            // 获取行数据
            Map<Integer, Cell> lineMap = analysisContext.readRowHolder().getCellMap();

            // 获取省份以及市名称,如果为空或者空字符则返回null
            String provinceName = lineMap.get(0) == null || StringUtils.isBlank(lineMap.get(0).toString()) ? null : lineMap.get(0).toString();
            String cityName = lineMap.get(1) == null || StringUtils.isBlank(lineMap.get(1).toString()) ? null : lineMap.get(1).toString();
            // 省市不能为空
            if(provinceName == null || cityName == null){
                error = true;
                codeEnum = AdminResultCodeEnum.IMPORT_PROVINCE_CITY_NOT_NULL;
            }
            // 获取导入行数据解析封装
            SupplierImportPriceDto supplierImportPrice = new SupplierImportPriceDto();
            supplierImportPrice.setProvinceName(provinceName);
            supplierImportPrice.setCityName(cityName);
            // 服务价格集合
            List<SupplierServicePriceDto> servicePrices = new ArrayList<>();
            // 遍历服务，从行中获取
            for (int i = 0; i < service.size(); i++) {
                // 第一个服务为 0 * 3 + 2 = 2，前面有两个省市略过，2下标对应3
                // 获得服务价格起始列,成本价,门店价，销售价列
                int costColumn = i * 3 + 2;
                int originalColumn = costColumn + 1;
                int salesColumn = costColumn + 2;

                // 获取成本价，门店价，以及销售价，如果没填写则为空，如果填写则转为BigDecimal
                BigDecimal costOf = lineMap.get(costColumn) == null || ((CellData) lineMap.get(costColumn)).getType().equals(CellDataTypeEnum.EMPTY) ? null : new BigDecimal(lineMap.get(costColumn).toString());
                BigDecimal originalPrice = lineMap.get(originalColumn) == null || ((CellData) lineMap.get(originalColumn)).getType().equals(CellDataTypeEnum.EMPTY) ? null : new BigDecimal(lineMap.get(originalColumn).toString());
                BigDecimal salesPrice = lineMap.get(salesColumn) == null || ((CellData) lineMap.get(salesColumn)).getType().equals(CellDataTypeEnum.EMPTY) ? null : new BigDecimal(lineMap.get(salesColumn).toString());

                // 服务Name
                String serviceName = service.get(i);
                // 如果填写了某个价格则加入集合中
                if (costOf != null || originalPrice != null || salesPrice != null) {
                    // 设置成本价、门店价、销售价、以及服务名称
                    SupplierServicePriceDto servicePrice = new SupplierServicePriceDto();
                    servicePrice.setCostOf(costOf);
                    servicePrice.setOriginalPrice(originalPrice);
                    servicePrice.setSalesPrice(salesPrice);
                    servicePrice.setServiceName(serviceName);

                    // 添加数据到集合中
                    servicePrices.add(servicePrice);
                }
            }
            supplierImportPrice.setServicePrices(servicePrices);
            // 添加到导入数据中
            importData.add(supplierImportPrice);
        }
    }

    @Override
    public void extra(CellExtra cellExtra, AnalysisContext analysisContext) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    @Override
    public boolean hasNext(AnalysisContext analysisContext) {
        return true;
    }

    /**
     * 读取表头，从表头中取出服务
     *
     * @param map             表头Map，Key为Integer的从0开始的数组
     * @param analysisContext
     */
    @Override
    public void invokeHead(Map map, AnalysisContext analysisContext) {
        for (Object column : map.keySet()) {
            // 列位置
            Integer columnLocation = (Integer) column;
            // 过滤掉省市表头
            if (columnLocation >= 2) {
                String header = map.get(column).toString();
                // 为空则跳出
                if (StringUtils.isBlank(header)) {
                    continue;
                }
                // 如果服务中包含或已经存在服务，也跳出
                if (service.contains(header)) {
                    continue;
                }
                service.add(header);
            }
        }
        // 读取表头设置
        int size = map.keySet().size();

        int serviceSize = service.size();
        // 校验表头长度是否正常，如果不正常,设置异常
        if (size != (serviceSize * 3 + 2)) {
            error = true;
            codeEnum = AdminResultCodeEnum.IMPORT_CITY_PRICE_HEADER_ERROR;
        } else {
            log.info("读取到导入表头:{}", service);
        }

    }

    /**
     * 获取导入结果
     *
     * @return
     */
    public boolean importError() {
        return error;
    }
}

```



# 通用监听工具类

编写通用工具类ExcelListener

```java

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author BigKang
 * @Date 2020/8/21 4:29 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize Excel监听工具
 */
@Data
@Slf4j
public class ExcelListener<T> extends AnalysisEventListener<T> {
    private List<T> list;

    public ExcelListener() {
        list = new CopyOnWriteArrayList<>();
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        list.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
//        log.info("解析数据完成");
//        log.info("一共：读取{}条数据",list.size());
    }
}

```



# 自适应列宽Excel导出

​		导出时候由于列名太长导致Excel导出太丑所以我们需要自定义一个WriteHandler

```java
package com.botpy.vosp.admin.business.supplier.excel;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

/**
 * @Author HuangKang
 * @Date 2021/7/12 10:20 上午
 * @Summarize 自定义Excel处理器，用于自适应列宽
 */
public class CustomHandler extends AbstractColumnWidthStyleStrategy {
    private static final int MAX_COLUMN_WIDTH = 255;
    //the maximum column width in Excel is 255 characters

    public CustomHandler() {
    }

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        if (isHead && cell.getRowIndex() != 0) {
            int columnWidth = cell.getStringCellValue().getBytes().length;
            if (columnWidth > MAX_COLUMN_WIDTH) {
                columnWidth = MAX_COLUMN_WIDTH;
            } else {
                columnWidth = columnWidth + 3;
            }
            writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
        }
    }

}
```

​		使用方式，注册WriteHandler

```java
      EasyExcel.write(response.getOutputStream())
        // 这里放入动态头
        .head(heder)
        // 设置自定义Handler自适应长度表头
        .registerWriteHandler(new CustomHandler())
        // 设置sheet
        .sheet("测试导入模板")
        .doWrite(listData);
```

# 多行复杂表头导出

```java

    @ApiOperation("导出模板")
    @PostMapping(value = {"/exportTemplate"})
    public void exportTemplate(HttpServletResponse response) {
      List<String> supplierService = Arrays.asList("0101-五座轿车", "0102-SUVMPV", "0301-国产漆修复", "0302-进口漆修复");
      // 设置Excel响应头，以及文件名称
      setResponseExcelHeader(response, "城市价格导入模板");
      
      // 集合数据，存储省市
      List<List<String>> listData = new ArrayList<>();
      EasyExcel.write(response.getOutputStream())
        // 这里放入动态头
        .head(createHeader(supplierService))
        // 设置自定义Handler自适应长度表头
        .registerWriteHandler(new CustomHandler())
        // 设置sheet
        .sheet("测试导入模板")
        .doWrite(listData);
    }


    /**
     * 创建ExcelHeader头，根据服务,返回二堆数组
     *
     * @param serviceNames 服务名称
     * @return 返回Excel Header头
     * 省份 | 城市 |     0101-五座洗车     |
     * 北京 | 北京 | 成本价 | 原价 | 销售价 |
     */
    public List<List<String>> createHeader(List<String> serviceNames) {
        List<List<String>> headers = new ArrayList<>();
        headers.add(Arrays.asList("省份", "省份"));
        headers.add(Arrays.asList("城市", "城市"));
        for (String serviceName : serviceNames) {
            headers.add(Arrays.asList(serviceName, "成本价"));
            headers.add(Arrays.asList(serviceName, "门店价"));
            headers.add(Arrays.asList(serviceName, "销售价(必填)"));
        }
        return headers;
    }

    /**
     * 设置响应Excel响应头
     *
     * @param response HttpServlet响应
     * @param fileName 设置文件名称
     */
    public void setResponseExcelHeader(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        fileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.setHeader("filename", fileName);
    }
```

​		导出后服务下有三列

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1626577555089.png)

# 进行写入

​		首先创建实体

```java

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author BigKang
 * @Date 2020/11/9 4:36 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize
 */
@Data
@AllArgsConstructor
public class TestVo {

    @ExcelProperty("标题ID")
    private Integer id;

    @ExcelProperty("名称")
    private String name;
}

```

​		然后测试写入文件

```java
    @Test
    public void test() throws IOException {
        List<TestVo> list = new ArrayList<>();
        list.add(new TestVo(1, "BigKang1"));
        list.add(new TestVo(2, "BigKang2"));
        list.add(new TestVo(3, "BigKang3"));
        list.add(new TestVo(4, "BigKang4"));
        list.add(new TestVo(5, "BigKang5"));
        String filePath = "/Users/bigkang/Documents/test/";
        String fileName = UUID.randomUUID().toString() + ".xlsx";
        File file = new File(filePath + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        EasyExcel.write(file, TestVo.class).sheet("测试Sheet").doWrite(list);
        System.out.println(fileName);
    }
```





