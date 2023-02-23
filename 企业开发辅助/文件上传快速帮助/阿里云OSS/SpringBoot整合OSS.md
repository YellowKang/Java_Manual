# 添加依赖

```xml
      <dependency>
          <groupId>com.aliyun.oss</groupId>
          <artifactId>aliyun-sdk-oss</artifactId>
          <version>3.15.1</version>
      </dependency>
```

# 编写配置文件

```properties
oss.Bucket=配置你的Bucket
oss.EndPoint=配置你自己的EndPoint
oss.AccessKey=配置你自己的AccessKey
oss.AccessKeySecret=配置你自己的AccessKeySecret
```

```yaml
oss:
  AccessKey: # 配置你自己的AccessKey
  AccessKeySecret: # 配置你自己的AccessKeySecret
  Bucket: # 配置你的Bucket
  EndPoint: # 配置你自己的EndPoint

```

# 编写配置类

配置文件类OSSConfiguration

```java
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HuangKang
 * @date 2023/1/12 6:18 PM
 * @describe OSS工具类配置属性
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oss")
public class OSSProperties {

    /**
     * OSS bucket
     */
    private String bucket;

    /**
     * 域名端点
     */
    private String endPoint;

    /**
     * accessKey 公钥
     */
    private String accessKey;

    /**
     * accessKey 私钥
     */
    private String accessKeySecret;

}

```



# 编写配置类以及工具包

## 编写工具包

工具包类名OSSUtil

```java
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.sigreal.jdsettle.claim.properties.OSSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author HuangKang
 * @date 2023/1/12 6:20 PM
 * @describe OSS工具类
 */
@Component
@Slf4j
public class OSSUtil {

    private final OSSProperties ossProperties;

    @Autowired
    public OSSUtil(OSSProperties ossProperties) {
        this.ossProperties = ossProperties;
    }


    /**
     * 获取OSS连接，每次上传后需要关闭，不能使用Bean对象，所以每次需要获取
     *
     * @return OSS连接对象
     */
    public OSS getClient() {
				// 代理设置，可以关闭
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setProxyHost("172.17.22.181");
        clientBuilderConfiguration.setProxyPort(3130);
        return new OSSClientBuilder()
                .build(
                        ossProperties.getEndPoint(),
                        ossProperties.getAccessKey(),
                        ossProperties.getAccessKeySecret(),
                        clientBuilderConfiguration
                );
    }

    /**
     * 上传文件
     *
     * @param inputStream 上传文件流
     * @param filePath    上传文件路径
     * @return
     */
    public boolean uploadObject(InputStream inputStream, String filePath) {
        if (inputStream == null || !StringUtils.hasText(filePath)) {
            log.warn("uploadObject is null!");
            return false;
        }

        // 获取OSS连接
        OSS client = getClient();

        PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucket(), filePath, inputStream);
        // 设置该属性可以返回response。如果不设置，则返回的response为空。
        putObjectRequest.setProcess("true");

        PutObjectResult result = null;
        try {
            // 上传文件。
            result = client.putObject(putObjectRequest);
        }catch (Exception e){
            log.error("uploadObject is error!" + e.getMessage());
            return false;
        }finally {
            //关闭连接
            client.shutdown();
        }



        if (result.getResponse().getStatusCode() == 200) {
            return true;
        } else {
            log.error("uploadObject error message: {}", result.getResponse().getErrorResponseAsString());
            return false;
        }
    }

    /**
     * 下载方法
     *
     * @param ossFilePath oss文件名称
     * @param filePath    本地保存路径
     * @return
     */
    public boolean download(String ossFilePath, String filePath) {

        if (!StringUtils.hasText(ossFilePath) || !StringUtils.hasText(filePath)) {
            log.error("download oss file is empty!");
            return false;
        }

        // 获取连接
        OSS client = getClient();
        try {

            // 下载文件
            client.getObject(new GetObjectRequest(ossProperties.getBucket(), ossFilePath), new File(filePath));

            return true;
        } catch (Exception e) {

            log.error("download oss object error!");

            return false;
        } finally {

            // 关闭连接
            client.shutdown();
        }

    }

    /**
     * 删除文件
     *
     * @param filePath 删除的文件名
     * @return 是否删除成功
     */
    public boolean deleteFile(String filePath) {
        OSS client = getClient();
        try {
            // 删除文件
            client.deleteObject(ossProperties.getBucket(), filePath);

            return true;
        } catch (Exception e) {
            log.error("delete oss object error!");
            return false;
        } finally {
            // 关闭连接
            client.shutdown();
        }
    }


    /**
     * 列举文件
     *
     * @param prefix 前缀，类似于文件夹
     * @return 列举的文件Path路径
     */
    public List<String> list(String prefix) {
        //获取连接
        OSS client = getClient();
        //创建返回值
        List<String> list = new CopyOnWriteArrayList();
        //获取返回结果
        ObjectListing objectListing = client.listObjects(ossProperties.getBucket(), prefix);
        // 关闭连接
        client.shutdown();

        List<OSSObjectSummary> summaries = objectListing.getObjectSummaries();
        //循环添加
        for (OSSObjectSummary summary : summaries) {
            list.add(summary.getKey());
        }
        //返回结果
        return list;
    }

    /**
     * 生成URL
     * @param filePath 文件路径
     * @return Url路径
     */
    public String generateUrl(String filePath){
        // 默认30分钟失效
        LocalDateTime expireTime = LocalDateTime.now();
        expireTime = expireTime.plusMinutes(30);
        return generateUrl(filePath, expireTime);
    }

    /**
     * 生成公网URL
     * @param filePath 文件路径
     * @param expireTime 超时时间
     * @return
     */
    public String generateUrl(String filePath, LocalDateTime expireTime) {
        OSS client = getClient();
        try {
            //生成URL
            URL url = client.generatePresignedUrl(ossProperties.getBucket(), filePath,Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant()));
            return url.toString();
        } catch (Exception e) {
            log.error("delete oss object error!");
            return null;
        } finally {
            // 关闭连接
            client.shutdown();
        }
    }
}

```

# 我们开始使用工具吧

进入TestOSSController，这里我们使用工具上传，我们获取到文件的流，和文件名称（我们可以自定义文件名和路径），注意！不能使用/开始，所以我们路径编写为bigkang/test/.test.java

```java
import com.kang.test.config.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 测试OSSController
 * 2019/4/23
 * Bigkang
 */
@RestController
public class TestOSSController {

    @Autowired
    private OSSUtil ossUtil;

    /**
     * ！！！！！！！！在当前项目下新建一个test.java文件，随便写入点什么
     * 上传
     * @param fileName 自定义上传文件名称
     * @return
     * @throws FileNotFoundException
     */
    @GetMapping("upload")
    public String upload(String fileName) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(new File("test.java"));
        return ossUtil.upload(fileInputStream,fileName);
    }

    /**
     * 下载
     * @param fileName 下载文件名
     * @return
     */
    @GetMapping("download")
    public String download(String fileName){
        return ossUtil.download(fileName,"java.test");
    }

    /**
     * 删除
     * @return
     */
    @GetMapping("deleteFile")
    public String deleteFile(){
        return ossUtil.removeFile("bigkang/test/test.java");
    }

    /**
     * 查询
     * @param prefix 根据前缀查询
     * @return
     */
    @GetMapping("lists")
    public List<String> lists(String prefix){
        return ossUtil.list(prefix);
    }
}

```

更多详细细节可以直接查看阿里云官网Api

<https://help.aliyun.com/product/31815.html?spm=5176.7933691.1309819.6.5bf52a66JNwkh1> 