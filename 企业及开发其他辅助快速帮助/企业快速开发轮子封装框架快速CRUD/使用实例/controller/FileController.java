package com.kang.shop.usertestserver.controller;

import com.kang.shop.usertestserver.config.FastDFSUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("test-file")
@Api(tags = "测试文件功能")
public class FileController {

    @Resource
    private FastDFSUtil fastDFSUtil;

    @PostMapping("upload")
    public String upload(MultipartFile file) throws IOException {
        String filePath = fastDFSUtil.uploadFile(file);
        return filePath;
    }
}
