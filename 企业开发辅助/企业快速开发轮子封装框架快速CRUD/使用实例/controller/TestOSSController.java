package com.kang.shop.usertestserver.controller;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.kang.shop.common.web.config.oss.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * 测试OSSController
 * 2019/4/23
 * Bigkang
 */
@RestController
@RequestMapping("file")
@RefreshScope
public class TestOSSController {

    @Value("${test}")
    private String test;

    @Autowired
    private OSSUtil ossUtil;

    @PostMapping("upload")
    public String upload(MultipartFile  file) throws IOException {
        return ossUtil.upload(file.getInputStream(),file.getOriginalFilename());
    }

    @GetMapping("download")
    public ResponseEntity<FileSystemResource> download(String fileName){
        return null;
    }

    @GetMapping("deleteFile")
    public String deleteFile(){
        return ossUtil.removeFile("bigkang/test/test.java");
    }

    @GetMapping("lists")
    public List<String> lists(String prefix){
        return ossUtil.list(prefix);
    }

    @GetMapping("test")
    public String test(){
        return test;
    }
}
