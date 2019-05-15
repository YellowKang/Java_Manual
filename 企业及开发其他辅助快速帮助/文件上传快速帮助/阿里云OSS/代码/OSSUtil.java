package com.kang.test.config;

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
