# 官方地址

```
https://github.com/alibaba/easyexcel
```

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
        ExcelReader excelReader = EasyExcel.read("/Users/bigkang/Documents/笔记/Java_Manual/企业及开发其他辅助快速帮助/Excel表格快速帮助/test.xlsx", Demo.class, new DemoListener()).build();
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

# 进行写入

```
FROM boystar/ubantu
# 设置apt源
RUN echo "deb http://mirrors.163.com/ubuntu precise main universe" > /etc/apt/sources.list
# 安装 vim ping ifconfig ip tcpdump nc curl iptables python 常用命令
RUN apt-get -y update && apt-get -qq -y install vim iputils-ping netcat curl
COPY so /usr/lib
RUN touch /test.log
CMD ["tail","-f","/test.log"]
```



```
FROM xhbjdk1.8
COPY target/coalmine-xhb-0.0.1-SNAPSHOT.jar coalmine-xhb.jar
ENV JAVA_OPTS=""
ENV APP_OPTS=""
RUN mkdir -p /xhb/lib
COPY so //usr/lib
RUN echo '/xhb/lib' >> /etc/ld.so.conf
RUN ldconfig
CMD [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF8 -Duser.timezone=GMT+08 -jar /coalmine-xhb.jar $APP_OPTS" ]
EXPOSE 8234
```

