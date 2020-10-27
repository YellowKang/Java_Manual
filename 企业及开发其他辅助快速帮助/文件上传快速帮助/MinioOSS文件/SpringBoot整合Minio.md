# 首先我们引入Maven依赖

```
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-s3</artifactId>
            <version>1.11.490</version>
        </dependency>
```



# 然后编写配置

我们创建配置文件

​	yml格式

```properties
minio:
  bucket: test
  accessKey: bigkang123
  accessKeySecret: bigkang123
  serverAddress: 127.0.0.1:9000
```

​	properties

```properties
minio.bucket=test
minio.accessKey=bigkang123
minio.accessKeySecret=bigkang123
minio.serverAddress=127.0.0.1:9000
```

我们的工具类会自动创建bucket所以可以不用自己新建

# 编配Config配置类

如下所示，注：（此处采用Slf4j没有添加或不想添加的同学可以改成打印日志）

```java

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author BigKang
 * @Date 2019/7/3 9:58
 * @Summarize OSS对象存储配置类
 */
@Configuration
@Slf4j
public class OSSConfig {

    //获取配置文件Bucket
    @Value("${minio.bucket:请设置oss配置}")
    private String bucket;
    //获取配置文件AccessKey
    @Value("${minio.accessKey:请设置oss配置}")
    private String accessKey;
    //获取配置文件AccessKeySecret
    @Value("${minio.accessKeySecret:请设置oss配置}")
    private String accessKeySecret;
    //获取配置文件AccessKeySecret
    @Value("${minio.serverAddress:请设置oss配置}")
    private String serverAddress;

    /**
     * 将S3注入到Bean容器
     * @return
     */
    @Bean
    public AmazonS3 amazonS3(){
        // 创建Amazon S3对象使用明确凭证
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, accessKeySecret);
        ClientConfiguration clientConfig = new ClientConfiguration();
        // 凭证验证方式
        clientConfig.setSignerOverride("S3SignerType");
        // 访问协议
        clientConfig.setProtocol(Protocol.HTTP);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(
                        // 设置要用于请求的端点配置（服务端点和签名区域）
                        new AwsClientBuilder.EndpointConfiguration(
                                // S3(OSS)服务器地址
                                serverAddress,
                                // bucket名称
                                bucket)).
                        // 是否使用路径方式，是的话s3.xxx.sn/bucketname
                        withPathStyleAccessEnabled(true)
                .build();
        // 判断是否存在bucket，不存在则创建
        AtomicBoolean flag = new AtomicBoolean(true);
        s3Client.listBuckets().forEach(v -> {
            if (v.getName().equals(bucket)) {
                // 如果存在返回false
                flag.set(false);
            }
        });
        if(flag.get()){
            log.info("------------------------------------------------------------");
            log.info("------------------------------------该Bukect不存在创建"+bucket);
            log.info("------------------------------------------------------------");
            s3Client.createBucket(bucket);
        }
        return s3Client;
    }

}
```



# 编写工具类

我们这里先编写工具类

我们需要把连接对象也注入进去

创建MinioOSSUtil.java，如下所示

```java

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.topcom.pisearch.vo.FileUploadVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * @Author BigKang
 * @Date 2019/7/2 16:24
 * @Summarize OSS对象存储工具类
 */
@Component
@Slf4j
public class MinioOSSUtil {

    // 获取配置文件Bucket
    @Value("${minio.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3 amazonS3;

    /**
     * 重载上传接口
     *
     * @param file
     * @return
     */
    public FileUploadVo upload(MultipartFile file) {
        return upload(file, UUID.randomUUID().toString());
    }

    /**
     * 文件上传，并返回文件路径
     *
     * @param file
     * @param userId
     * @return
     */
    public FileUploadVo upload(MultipartFile file, String userId) {
        // 以用户Id作为oss头，然后拼接时间，最后加入文件名
        String filePrefix = userId;
        // 文件路径拼接时间，为每天
        String dateToString = DateUtil.DateToString(new Date(), "yyyy-MM-dd");
        filePrefix += "/" + dateToString;


        // 再拼接UUID
        filePrefix += "/" + UUID.randomUUID().toString();

        String fileName = file.getOriginalFilename();
        // 设置ObjectMetadata属性，文件类型以及长度
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        try {
            // 上传文件到OSS服务器上
            amazonS3.putObject(bucket, filePrefix + fileName, file.getInputStream(), objectMetadata);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 生成请求
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
                bucket, filePrefix + fileName);
        // 生成连接有效期，为10年
        Date expiration = new Date();
        expiration.setYear(expiration.getYear() + 30);
        urlRequest.setExpiration(expiration);
        // 获取返回URL
        URL url = amazonS3.generatePresignedUrl(urlRequest);
        String urlStr = url.toString();
        FileUploadVo vo = new FileUploadVo();
        vo.setFileUrl(urlStr);
        vo.setFileName(getFilePrefixName(fileName));
        vo.setType(getFileSuffixType(fileName));
        String encoderFileName = filePrefix;
        String filePath = urlStr.substring(urlStr.indexOf(encoderFileName));
        vo.setFilePath(filePath);
        return vo;
    }

    /**
     * 删除文件，根据key和文件名称
     *
     * @param filePath
     * @return
     */
    public boolean deleteFile(String filePath) {
        try {
            amazonS3.deleteObject(bucket, filePath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取文件前缀名称
     * @param originalFilename
     * @return
     */
    private String getFilePrefixName(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        if(index != -1){
            return originalFilename.substring(0,index);
        }
        return originalFilename;
    }

    /**
     * 获取文件后缀类型
     * @param originalFilename
     * @return
     */
    private String getFileSuffixType(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        if(index != -1){
            return originalFilename.substring(index+1);
        }
        return originalFilename;
    }
}
```

# 编写Controller

这样我们就配置好了，记得把这两个配置放在能被容器扫描的包下

然后我们直接使用controller进行上传

```java

import com.topcom.cms.domain.User;
import com.topcom.cms.web.bind.annotation.CurrentUser;
import com.topcom.pisearch.util.MinioOSSUtil;
import com.topcom.pisearch.vo.FileUploadVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author BigKang
 * @Date 2020/8/27 6:08 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize OSS文件上传
 */
@RestController
@RequestMapping("ossUpload")
public class OSSFileUploadController {


    @Autowired
    private MinioOSSUtil oSSUtil;

    @PostMapping("upload")
    public FileUploadVo upload(@CurrentUser @ApiIgnore User user, MultipartFile file) {
        FileUploadVo vo = oSSUtil.upload(file, user.getId().toString());
        return vo;
    }

    @DeleteMapping("delete")
    public Boolean upload(String filePath) {
        boolean deleted = oSSUtil.deleteFile(filePath);
        return deleted;
    }

}

```

