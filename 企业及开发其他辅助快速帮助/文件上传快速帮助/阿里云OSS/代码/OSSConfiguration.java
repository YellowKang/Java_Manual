package com.kang.test.config;

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
