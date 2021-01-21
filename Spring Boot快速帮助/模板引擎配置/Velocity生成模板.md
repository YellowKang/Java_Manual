# 引入依赖

```xml
      	<dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-engine-core</artifactId>
            <version>2.2</version>
        </dependency>

```

​		引入依赖

```xml
      	<dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity</artifactId>
        </dependency>
```

​		

# 使用velocity生成字符串

​		首先Resource下的 file/test.vm新建模板

```
时间：${date}
姓名：${name}
爱好: ${like}
```

​		然后生成

```java
        // 加载classpath目录下的vm文件
        Properties properties = new Properties();
        properties.setProperty("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        // 定义字符集
        properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        VelocityEngine ve = new VelocityEngine();
        // 初始化Velocity引擎，指定配置Properties
        ve.init(properties);

        // 创建一个Map用来存储数据
        Map<String,Object> map = new HashMap<>();
        map.put("date","2020-10-12 00:00:00");
        map.put("name","Bigkang");
        map.put("like","游戏");


        VelocityContext context = new VelocityContext(map);
        Template template = ve.getTemplate("file/test.vm", "utf-8");

        StringWriter strWriter = new StringWriter();
        template.merge(context,strWriter);
        System.out.println(strWriter.toString());
```

# 使用velocity生成World

然后我们在Resource下创建一个文件velocity文件夹然后新建一个test.vm文件

​		然后新建一个World

​		我们将一个如下的World文档填写参数

<img src="https://blog-kang.oss-cn-beijing.aliyuncs.com/1610677550147.png" style="zoom:50%;" />

​		选择导出

​		<img src="https://blog-kang.oss-cn-beijing.aliyuncs.com/1610677711850.png" style="zoom:50%;" />

​		然后把导出的文件用文件编辑器打开，然后粘贴到test.vm文件下即可。

​		然后我们使用如下代码

```java
   	@Test
    public void testVel() throws IOException {
        // 加载classpath目录下的vm文件
        Properties properties = new Properties();
        properties.setProperty("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        // 定义字符集
        properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        VelocityEngine ve = new VelocityEngine();
        // 初始化Velocity引擎，指定配置Properties
        ve.init(properties);

        // 创建一个Map用来存储数据
        Map<String,Object> map = new HashMap<>();
        map.put("date","2020-10-12 00:00:00");
        map.put("type","事故类型");
        map.put("value","事故信息");
        map.put("executorMan","领导1");
        map.put("commandMan","领导2");

        // 根据模板路径生成
        VelocityContext context = new VelocityContext(map);
        Template template = ve.getTemplate("velocity/test.vm", "utf-8");
        // 文件输出路径
        File srcFile = new File("/Users/bigkang/Documents/test/test.html");
        FileOutputStream fos = new FileOutputStream(srcFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
        template.merge(context, writer);
        writer.flush();
        writer.close();
        fos.close();
    }
```

# velocity工具类

```java
package com.topcom.test.mp.security.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * @Author BigKang
 * @Date 2021/1/20 10:33 上午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize Velocity工具类
 */
public class VelocityUtil {

    private static VelocityEngine velocityEngine = new VelocityEngine();

    private static final String DEFAULT_ENCODING = "UTF-8";

    static {
        Properties properties = new Properties();
        properties.setProperty("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        // 定义字符集
        properties.setProperty(Velocity.INPUT_ENCODING, DEFAULT_ENCODING);
        properties.setProperty(Velocity.OUTPUT_ENCODING,DEFAULT_ENCODING);
        // 初始化Velocity引擎，指定配置Properties
        velocityEngine.init(properties);
    }

    /**
     * 模板生成字符串
     * @param templatePath 模板路径
     * @param mapContext map对象
     * @return
     */
    public static String generateString(String templatePath, Map<String,Object> mapContext){
        return generateString(templatePath,mapContext,DEFAULT_ENCODING);
    }

    /**
     * 模板生成字符串
     * @param templatePath 模板路径
     * @param mapContext map对象
     * @param enCoding 编码
     * @return
     */
    public static String generateString(String templatePath, Map<String,Object> mapContext,String enCoding){
        VelocityContext context = new VelocityContext(mapContext);
        Template template = velocityEngine.getTemplate(templatePath, enCoding);
        try (StringWriter strWriter = new StringWriter()){
            template.merge(context,strWriter);
            return strWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成文件
     * @param templatePath 模板路径
     * @param mapContext map对象
     * @param filePath 文件
     * @return
     */
    public static Boolean generateFile(String templatePath, Map<String,Object> mapContext,String filePath){
        File file = new File(filePath);
        // 如果文件不存在
        if (!file.exists()) {
            // 父文件夹不存在创建
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return generateFile(templatePath,mapContext,file,DEFAULT_ENCODING);
    }

    /**
     * 生成文件
     * @param templatePath 模板路径
     * @param mapContext map对象
     * @param file 文件
     * @return
     */
    public static Boolean generateFile(String templatePath, Map<String,Object> mapContext,File file){
        return generateFile(templatePath,mapContext,file,DEFAULT_ENCODING);
    }

    /**
     * 生成文件
     * @param templatePath 模板路径
     * @param mapContext map对象
     * @param file 文件
     * @param enCoding 编码
     * @return
     */
    public static Boolean generateFile(String templatePath, Map<String,Object> mapContext,File file,String enCoding){
        VelocityContext context = new VelocityContext(mapContext);
        Template template = velocityEngine.getTemplate(templatePath, enCoding);
        try(FileOutputStream fos = new FileOutputStream(file);BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));) {
            template.merge(context, writer);
            writer.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

```

​		使用方式

​		字符串

```java
        // 创建一个Map用来存储数据
        Map<String,Object> map = new HashMap<>();
        map.put("date","2020-10-12 00:00:00");
        map.put("name","Bigkang");
        map.put("like","游戏1111");
        String str = VelocityUtil.generateString("file/test.vm", map);
        System.out.println(str);
```

​		文件

```java
        // 创建一个Map用来存储数据
        Map<String,Object> map = new HashMap<>();
        map.put("date","2020-10-12 00:00:00");
        map.put("name","Bigkang");
        map.put("like","游戏1111");
        Boolean aBoolean = VelocityUtil.generateFile("file/test.vm", map, "/Users/bigkang/Documents/test/c++/testas/test.txt");
        System.out.println(aBoolean);
```



# 注意事项

​		如果模板不放在Resource目录下，而放在Java代码包下面，那么Maven编译的时候需要将非Java文件过滤掉，为了不过滤这些文件需要修改maven打包时的资源。

```xml
    <build>
        <resources>
              <resource>
                  <directory>${basedir}/src/main/java</directory>
                  <includes>
                      <include>**</include>
                  </includes>
              </resource>
              <resource>
                  <directory>${basedir}/src/main/resources</directory>
                  <includes>
                      <include>**</include>
                  </includes>
              </resource>
        </resources>
    </build>
```

