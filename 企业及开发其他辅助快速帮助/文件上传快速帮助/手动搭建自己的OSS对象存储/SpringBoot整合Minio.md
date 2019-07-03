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

```
minio:
  bucket: test
  accessKey: bigkang123
  accessKeySecret: bigkang123
  serverAddress: 127.0.0.1:9000
```

我们的工具类会自动创建bucket所以可以不用自己新建

# 编写代码

我们这里先编写工具类

创建MinioOSSUtil.java，如下所示

```
/**
 * @Author BigKang
 * @Date 2019/7/2 16:24
 * @Summarize OSS对象存储工具类
 */
@Component
@Slf4j
public class MinioOSSUtil {

    //获取配置文件Bucket
    @Value("${minio.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3 amazonS3;


    /**
     * 重载上传接口
     * @param file
     * @return
     */
    public String upload(MultipartFile file){
       return upload(file,UUID.randomUUID().toString());
    }

    /**
     * 文件上传，并返回文件路径
     * @param file
     * @param userId
     * @return
     */
    public String upload(MultipartFile file,String userId){
        //以用户Id作为oss头，然后拼接时间，最后加入文件名
        String fileName = userId;
        try {
            //文件路径拼接时间，为每天
            String dateToString = DateUtil.DateToString(new Date(), "yyyy-MM-dd");
            fileName += "/" + dateToString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //再拼接UUID
        fileName += "/"+UUID.randomUUID().toString()+FileUtil.getFileSuffix(file.getOriginalFilename());
        //设置ObjectMetadata属性，文件类型以及长度
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        try {
            //上传文件到OSS服务器上
            amazonS3.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //生成请求
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
                bucket, fileName);
        //生成连接有效期，为10年
        Date expiration = new Date();
        expiration.setYear(expiration.getYear() + 10);
        urlRequest.setExpiration(expiration);
        //获取返回URL
        URL url = amazonS3.generatePresignedUrl(urlRequest);
        return url.toString();
    }

    /**
     * 删除文件，根据key和文件名称
     * @param filePath
     * @return
     */
    public boolean deleteFile(String filePath){
        try {
            amazonS3.deleteObject(bucket,filePath);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
```

然后我们需要把连接对象也注入进去，创建配置文件OSSConfig.java

如下所示，注：（此处采用Slf4j没有添加或不想添加的同学可以改成打印日志）

```
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
        //创建Amazon S3对象使用明确凭证
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, accessKeySecret);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignerOverride("S3SignerType");//凭证验证方式
        clientConfig.setProtocol(Protocol.HTTP);//访问协议
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(//设置要用于请求的端点配置（服务端点和签名区域）
                                serverAddress,//我的s3服务器
                                bucket)).withPathStyleAccessEnabled(true)//是否使用路径方式，是的话s3.xxx.sn/bucketname
                .build();
        //判断是否存在bucket，不存在则创建
        AtomicBoolean flag = new AtomicBoolean(true);
        s3Client.listBuckets().forEach(v -> {
            if (v.getName().equals(bucket)) {
                //如果存在返回false
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

这样我们就配置好了，记得把这两个配置放在能被容器扫描的包下

然后我们直接使用controller进行上传

```
    @Autowired
    private MinioOSSUtil oSSUtil;

    @PostMapping("upload")
    public String upload(MultipartFile file) {
        String filePath = oSSUtil.upload(file,"康哥");
        return filePath;
    }

    @DeleteMapping("delete")
    public ResultVo upload(String filePath) {
        boolean b = oSSUtil.deleteFile(filePath);
        return b ?
                ResultVo.result("删除成功",Code.OK_CODE,Message.OK) :
                ResultVo.result("删除失败",Code.Failure_CODE,Message.Failure);
    }

```

