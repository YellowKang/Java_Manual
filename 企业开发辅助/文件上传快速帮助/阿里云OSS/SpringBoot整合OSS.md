# 添加依赖

```
        <dependency>
            <groupId>com.aliyun.oss</groupId>
            <artifactId>aliyun-sdk-oss</artifactId>
            <version>2.8.3</version>
        </dependency>
```

# 编写配置文件

```
oss.Bucket=配置你的Bucket
oss.EndPoint=配置你自己的EndPoint
oss.AccessKey=配置你自己的AccessKey
oss.AccessKeySecret=配置你自己的AccessKeySecret
```

# 编写配置类以及工具包

## 编写工具包

工具包类名OSSUtil

```
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 阿里云OSS工具
 * 2019/4/23
 * Bigkang
 */
public class OSSUtil {

    //获取配置文件Bucket
    private String bucket;
    //获取配置文件EndPoint
    private String endPoint;
    //获取配置文件AccessKey
    private String accessKey;
    //获取配置文件AccessKeySecret
    private String accessKeySecret;

    //构造方法创建
    public OSSUtil(String endPoint,String accessKey,String accessKeySecret,String bucket){
        this.endPoint  = endPoint;
        this.accessKey = accessKey;
        this.accessKeySecret = accessKeySecret;
        this.bucket = bucket;
    }

    /**
     * 获取OSS连接，每次上传后需要关闭，不能使用Bean对象，所以每次需要获取
     * @return
     */
    public OSSClient getClient(){
        return new OSSClient(endPoint,accessKey,accessKeySecret);
    }

    /**
     * 上传文件
     * @param inputStream 上传文件流
     * @param fileName 上传文件路径
     * @return
     */
    public String upload(FileInputStream inputStream,String fileName){
        if(inputStream == null || StringUtils.isEmpty(fileName)){
            return null;
        }
        //获取OSS连接
        OSSClient client = getClient();
        //上传文件
        client.putObject(bucket, fileName, inputStream);
        //设置url过期时间为10年
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 60 * 24 * 365 * 10);
        //生成URL
        URL url = client.generatePresignedUrl(bucket, fileName, expiration);
        //关闭连接
        client.shutdown();
        return url.toString();
    }

    /**
     * 下载方法
     * @param fileName oss文件名称
     * @param filePath 本地保存路径
     * @return
     */
    public String download(String fileName,String filePath){
        //验证非空
        if(StringUtils.isEmpty(fileName) || StringUtils.isEmpty(filePath)){
            return null;
        }
        //获取连接
        OSSClient client = getClient();
        //下载文件
        client.getObject(new GetObjectRequest(bucket,fileName),new File(filePath));
        //关闭文件
        client.shutdown();
        //返回路径
        return filePath;
    }

    /**
     * 删除文件
     * @param fileName 删除的文件名
     * @return
     */
    public String removeFile(String fileName){
        OSSClient ossClient = getClient();
        ossClient.deleteObject(bucket,fileName);
        return "删除成功";
    }


    /**
     * 列举文件
     * @param prefix 前缀，类似于文件夹
     * @return
     */
    public List<String> list(String prefix){
        //获取连接
        OSSClient ossClient = getClient();
        //创建返回值
        List<String> list = new CopyOnWriteArrayList();
        //获取返回结果
        ObjectListing objectListing = ossClient.listObjects(bucket, prefix);
        List<OSSObjectSummary> summaries = objectListing.getObjectSummaries();
        //循环添加
        for (OSSObjectSummary summary : summaries) {
            list.add(summary.getKey());
        }
        //返回结果
        return list;
    }
}
```

## 编写配置类

配置文件类OSSConfiguration

```
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 * 2019/4/23
 * Bigkang
 */
@Configuration
public class OSSConfiguration {

    //获取配置文件Bucket
    @Value("${oss.Bucket}")
    private String bucket;
    //获取配置文件EndPoint
    @Value("${oss.EndPoint}")
    private String endPoint;
    //获取配置文件AccessKey
    @Value("${oss.AccessKey}")
    private String accessKey;
    //获取配置文件AccessKeySecret
    @Value("${oss.AccessKeySecret}")
    private String accessKeySecret;

    //将OSSUtil注册为Bean对象
    @Bean
    public OSSUtil ossUtil(){
        return new OSSUtil(endPoint,accessKey,accessKeySecret,bucket);
    }

}

```

# 我们开始使用工具吧

进入TestOSSController，这里我们使用工具上传，我们获取到文件的流，和文件名称（我们可以自定义文件名和路径），注意！不能使用/开始，所以我们路径编写为bigkang/test/.test.java

```
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