# 首先我们引入Maven依赖

​		S3

```xml
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-s3</artifactId>
            <version>1.11.918</version>
        </dependency>
```

# 编写配置文件

我们创建配置文件

​	yml格式

```properties
minio:
  bucket: test
  accessKey: bigkang123
  accessKeySecret: bigkang123
  serverAddress: 127.0.0.1:9000
  autoCreateBucket: true
```

​	properties

```properties
minio.bucket=test
minio.accessKey=bigkang123
minio.accessKeySecret=bigkang123
minio.serverAddress=127.0.0.1:9000
minio.autoCreateBucket=true
```

​		我们的工具类会自动创建bucket所以可以不用自己新建

# 编配Properties配置类

​		编写SpringBoot配置类，将配置文件按SpringBoot方式进行配置

```java
package com.topcom.test.mp.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author BigKang
 * @Date 2021/1/20 11:43 上午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize Minio配置文件
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinIoProperties {

    /**
     * bucket名称
     */
    private String bucket;

    /**
     * Minio配置AccessKey
     */
    private String accessKey;

    /**
     * Minio配置AccessKeySecret
     */
    private String accessKeySecret;

    /**
     * MinioServer地址，默认本地
     */
    private String serverAddress = "http://127.0.0.1:9000";

    /**
     * 是否自动创建bucket，默认true
     */
    private Boolean autoCreateBucket = true;

}

```

# 编配Configuration配置类

​		Configuration配置类主要用于根据配置文件创建连接

```java
package com.topcom.test.mp.security.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.topcom.test.mp.security.properties.MinIoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @Author BigKang
 * @Date 2021/1/20 11:49 上午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize
 */
@Configuration
@Slf4j
public class MinIoConfig {

    /**
     * MinIo配置文件
     */
    private final MinIoProperties minIoProperties;

    @Autowired
    public MinIoConfig(MinIoProperties minIoProperties) {
        this.minIoProperties = minIoProperties;
    }

    /**
     * 将S3注入到Bean容器
     * @return
     */
    @Bean
    public AmazonS3 amazonS3(){
        // 创建Amazon S3对象使用明确凭证
        BasicAWSCredentials credentials = new BasicAWSCredentials(minIoProperties.getAccessKey(), minIoProperties.getAccessKeySecret());
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
                                minIoProperties.getServerAddress(),
                                // bucket名称
                                minIoProperties.getBucket())).
                // 是否使用路径方式，是的话s3.xxx.sn/bucketname
                        withPathStyleAccessEnabled(true)
                .build();
        if (minIoProperties.getAutoCreateBucket()) {
            // 判断是否存在bucket，不存在则创建
            AtomicBoolean flag = new AtomicBoolean(true);
            s3Client.listBuckets().forEach(v -> {
                if (v.getName().equals(minIoProperties.getBucket())) {
                    // 如果存在返回false
                    flag.set(false);
                }
            });
            if(flag.get()){
                log.warn("------------------------------------------------------------");
                log.warn("------------------------------------该Bukect不存在创建"+minIoProperties.getBucket());
                log.warn("------------------------------------------------------------");
                s3Client.createBucket(minIoProperties.getBucket());
            }
        }
        return s3Client;
    }

}

```

# 编写响应返回Vo

​		Vo用于SpringMvc响应给前端以及其他所使用的Vo实体

```java
package com.topcom.common.web.vo;

import lombok.*;

/**
 * @Author BigKang
 * @Date 2021/1/18 12:00 下午
 * @Motto 仰天大笑撸码去,我辈岂是蓬蒿人
 * @Summarize 文件上传Vo
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVo {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * HttpContent类型
     */
    private String contentType;

    /**
     * 文件大小（字节）
     */
    private Long size;

}

```

# 编写工具类

​		工具类也按照SpringBoot的方式封装成组件。

```java
package com.topcom.test.mp.security.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.topcom.common.web.vo.FileUploadVo;
import com.topcom.test.mp.security.properties.MinIoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author BigKang
 * @Date 2021/1/20 1:55 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize MinIo工具类
 */
@Component
public class MinIoUtil {

    /**
     * 注入MinIo连接
     */
    private final AmazonS3 amazonS3;
    /**
     * 注入MinIo配置文件
     */
    private final MinIoProperties minIoProperties;

    @Autowired
    public MinIoUtil(AmazonS3 amazonS3, MinIoProperties minIoProperties) {
        this.amazonS3 = amazonS3;
        this.minIoProperties = minIoProperties;
    }

    /**
     * 获取所有Bucket
     *
     * @return
     */
    public List<String> listBucket() {
        List<String> list = new ArrayList<>();
        for (Bucket bucket : amazonS3.listBuckets()) {
            list.add(bucket.getName());
        }
        return list;
    }

    /**
     * 重载上传方法
     * @param file
     * @return
     */
    public FileUploadVo uploadFile(MultipartFile file) {
        return uploadFile(file,null,true);
    }

    /**
     * 文件上传
     * @param multipartFile Servlet文件
     * @param userId        用户Id
     * @return
     */
    public FileUploadVo uploadFile(MultipartFile multipartFile, String userId,Boolean generateUrl) {
        String filename = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        // 获取文件后缀类型
        String fileType = getFileSuffixType(filename);
        long size = multipartFile.getSize();

        // filePath = 时间格式化月（yyyy-MM） +  用户Id  +  文件类型  +  文件大小（字节）-文件名
        StringBuilder filePath = new StringBuilder();
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String time = currentTime.format(dateTimeFormatter);
        filePath.append(time);
        filePath.append("/");
        if (StringUtils.isEmpty(userId)) {
            userId = "default";
        }
        filePath.append(userId);
        filePath.append("/");
        if (fileType == null) {
            fileType = "null";
        }
        filePath.append(fileType);
        filePath.append("/");
        filePath.append(size + "-" + filename);

        try (InputStream inputStream = multipartFile.getInputStream();) {
            String path = filePath.toString();

            // 设置元数据
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(size);
            objectMetadata.setContentType(contentType);
            // 创建请求并且发送请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(minIoProperties.getBucket(), path, inputStream, objectMetadata);
            PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);
            // 设置超时时间为10年
            Long expires = TimeUnit.DAYS.toSeconds(3660);

            String url = null;
            // 判断是否需要生成访问Url
            if(generateUrl != null && generateUrl){
                url = generateUrl(path, expires);
            }
            FileUploadVo fileUploadVo = FileUploadVo.builder()
                    .filePath(filePath.toString())
                    .type(fileType)
                    .size(size)
                    .fileName(filename)
                    .contentType(contentType)
                    .url(url)
                    .build();
            return fileUploadVo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件，根据key和文件名称
     * @param filePath
     * @return
     */
    public boolean deleteFile(String filePath) {
        try {
            amazonS3.deleteObject(minIoProperties.getBucket(), filePath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据对象路径+超时时间生成Url
     * @param objectPath
     * @param expires
     * @return
     */
    public String generateUrl(String objectPath, Long expires) {
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(minIoProperties.getBucket(), objectPath);
            Date expiresTime = new Date(Instant.now().toEpochMilli() + expires * 1000L);
            request.setExpiration(expiresTime);
            URL url = amazonS3.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件返回输入流
     * @param filePath
     * @return
     */
    public InputStream downloadFile(String filePath){
        S3Object object = amazonS3.getObject(minIoProperties.getBucket(), filePath);
        return object.getObjectContent().getDelegateStream();
    }


    /**
     * 获取文件后缀类型
     * @param originalFilename
     * @return
     */
    private static String getFileSuffixType(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        if(index != -1){
            return originalFilename.substring(index+1);
        }
        return null;
    }
}

```

# 编写Controller控制器

​		使用Controller控制器进行测试

```java
package com.topcom.test.mp.security.controller;

import com.topcom.common.web.annotation.PublicPath;
import com.topcom.common.web.vo.FileUploadVo;
import com.topcom.test.mp.security.properties.MinIoProperties;
import com.topcom.test.mp.security.util.MinIoUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@RestController
@Api(tags = "测试控制器")
@RequestMapping("test")
@PublicPath
public class TestController {

    private final MinIoProperties minioProperties;

    private final MinIoUtil minIoUtil;

    @Autowired
    public TestController(MinIoProperties minioProperties, MinIoUtil minIoUtil) {
        this.minioProperties = minioProperties;
        this.minIoUtil = minIoUtil;
    }


    @GetMapping("minioProperties")
    public MinIoProperties minioProperties(){
        return minioProperties;
    }

    @PostMapping("minioUpload")
    public FileUploadVo minioUpload(MultipartFile multipartFile,String userId){
        return minIoUtil.uploadFile(multipartFile,userId,true);
    }

    @GetMapping("minioDownload")
    public void downloadFile(String filePath,String fileName,HttpServletResponse httpServletResponse){
        try (InputStream inputStream = minIoUtil.downloadFile(filePath);) {
            responseDownload(inputStream, fileName, httpServletResponse);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("minioDelete")
    public boolean minioDelete(String filePath){
        boolean result = minIoUtil.deleteFile(filePath);
        return result;
    }




    public void responseDownload(InputStream inputStream, String fileName, HttpServletResponse response) {
        // 设置response响应类型
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 设置response header头，文件名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName));
        // 创建字节数组读写流
        byte[] buffer = new byte[1024];
        // 设置上传成功变量判断是否异常
        // try块创建流自动关闭
        try (BufferedInputStream bis = new BufferedInputStream(inputStream); OutputStream os = response.getOutputStream()) {
            int i = bis.read(buffer);
            // response输出写入流
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            // 循环结束没有异常写入成功
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

```

