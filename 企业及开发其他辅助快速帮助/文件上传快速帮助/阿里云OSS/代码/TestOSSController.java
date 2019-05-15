package com.kang.test.controller;

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

    @GetMapping("upload")
    public String upload(String fileName) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(new File("test.java"));
        return ossUtil.upload(fileInputStream,fileName);
    }

    @GetMapping("download")
    public String download(String fileName){
        return ossUtil.download(fileName,"java.test");
    }

    @GetMapping("deleteFile")
    public String deleteFile(){
        return ossUtil.removeFile("bigkang/test/test.java");
    }

    @GetMapping("lists")
    public List<String> lists(String prefix){
        return ossUtil.list(prefix);
    }
}
