# POI是什么？

​		[Apache](https://baike.baidu.com/item/Apache/6265) POI [1] 是用[Java](https://baike.baidu.com/item/Java/85979)编写的免费开源的跨平台的 Java API，Apache POI提供API给Java程式对[Microsoft Office](https://baike.baidu.com/item/Microsoft Office)格式档案读和写的功能。POI为“Poor Obfuscation Implementation”的首字母缩写，意为“简洁版的模糊实现”。

# 使用POI进行读取

​		首先我们创建一个Excel表格如下

```
名称				地址		生日					分数
BigKang			达州		2019/1/11		123.112312
黄康				成都		2019/2/13		 1123.123123
NiceNB			北京		2019/12/14	231.132131
```

​		然后来进行读取

```java
    @Test
    public void readPoi() throws IOException, InvalidFormatException {
        File xlsx = new File("/Users/bigkang/Documents/笔记/Java_Manual/企业及开发其他辅助快速帮助/Excel表格快速帮助/cs.xlsx");
        // 创建文件或者读取文件
        Workbook workbook = WorkbookFactory.create(xlsx);
        // 获取Sheet第一个或者按名字
        Sheet sheetAt = workbook.getSheetAt(0);
        // 获取所有的行
        Iterator<Row> rowIterator = sheetAt.rowIterator();
        Row row;
        // 循环所有行
        while (rowIterator.hasNext()) {
            // 获取下一行
            row = rowIterator.next();
            // 行字符串
            String rowLineStr = "";
            // 指定读取一行的几列,例如现在读取4列
            for (int j = 0; j < 4; j++) {
                if (j != 0) {
                    rowLineStr += ",";
                }
                Cell cell = row.getCell(j);
                Object value = getCellValue(cell);
                rowLineStr += value;
            }
            System.out.println(rowLineStr);
        }
    }

    public Object getCellValue(Cell cell) {
        if (cell != null) {
            CellType cellTypeEnum = cell.getCellTypeEnum();
            // 如果不存在返回null
            if (cellTypeEnum.equals(CellType._NONE)) {
                return null;
            } else if (cellTypeEnum.equals(CellType.ERROR)) {
                return null;
            } else if (cellTypeEnum.equals(CellType.BLANK)) {
                return null;
            } else if (cellTypeEnum.equals(CellType.BOOLEAN)) {
                return cell.getBooleanCellValue();
            } else if (cellTypeEnum.equals(CellType.STRING)) {
                return cell.getStringCellValue();
            } else if (cellTypeEnum.equals(CellType.NUMERIC)) {
                if(HSSFDateUtil.isCellDateFormatted(cell)){
                    return cell.getDateCellValue();
                }else {
                    DecimalFormat df = new DecimalFormat("####.####");
                    return df.format(cell.getNumericCellValue());
                }
            } else if (cellTypeEnum.equals(CellType.FORMULA)) {
                return null;
            } else {
                return null;
            }
        }
        return null;
    }
```

# 使用POI进行写入

​		新建实体

```java
/**
 * @Author BigKang
 * @Date 2020/11/9 4:36 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize
 */
@Data
@AllArgsConstructor
public class TestVo {

    private Integer id;

    private String name;
}

```

​		编写测试类

```java
   @Test
    public void writePoi() throws IOException {
        List<TestVo> list = new ArrayList<>();
        list.add(new TestVo(1, "BigKang1"));
        list.add(new TestVo(2, "BigKang2"));
        list.add(new TestVo(3, "BigKang3"));
        list.add(new TestVo(4, "BigKang4"));
        list.add(new TestVo(5, "BigKang5"));

        // 创建文件
        String filePath = "/Users/bigkang/Documents/test/";
        String fileName = UUID.randomUUID().toString() + ".xlsx";
        File file = new File(filePath + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        // 创建表格
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建Sheet
        XSSFSheet sheet = workbook.createSheet("sheet1");
        // 设置第一行
        XSSFRow firstRow = sheet.createRow(0);//第一行表头
        // 设置表头
        String[] header = new String[]{"ID","姓名"};
        for (int i = 0; i < header.length; i++) {
            // 创建第一行第 1 - 2 列
            XSSFCell cell = firstRow.createCell(i);
            // 分别为1 - 2 列设置值
            cell.setCellValue(header[i]);
        }

        // 循环插入数据
        for (int i = 0; i < list.size(); i++) {
            // 从第二行开始，排除表头
            XSSFRow row = sheet.createRow(i + 1);

            // 创建第一列数据
            XSSFCell cell = row.createCell(0);
            // 设置第一列数据
            cell.setCellValue(list.get(i).getId());
            // 创建第二列数据
            cell = row.createCell(1);
            // 设置第二列数据
            cell.setCellValue(list.get(i).getName());
        }

        // 创建文件输出流
        FileOutputStream outputStream = new FileOutputStream(file);
        // 写入文件流
        workbook.write(outputStream);
        // 关闭流
        outputStream.close();
        // 返回文件名称
        System.out.println(file.getName());
    }
```

