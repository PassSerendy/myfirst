package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("上传失败，文件为空");
        }

        try {
            // 1. 获取原始文件名，截取后缀 (如 .jpg)
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 2. 生成新文件名 (UUID防重名)
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 3. 动态获取项目运行的真实物理路径 (不管在哪跑，绝对不会错)
            // 比如：D:\1_LearningProgram\SmartOrdering\demo1\
            String realPath = request.getServletContext().getRealPath("/");

            // 拼接完整的图片存放目录
            File imgDir = new File(realPath + "img");
            if (!imgDir.exists()) {
                imgDir.mkdirs(); // 如果 img 文件夹不存在，自动创建
            }

            // 4. 存入本地磁盘
            file.transferTo(new File(imgDir, newFileName));

            // 5. 返回前端可以访问的相对网络路径
            String url = "/img/" + newFileName;

            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传文件时发生异常");
        }
    }
}
