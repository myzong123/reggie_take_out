package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author myz
 * @create 2022/12/29-14:42
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 得到文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // UUID+后缀设置新的文件名
        String filename = UUID.randomUUID().toString() + suffix;
        File mdr = new File(basePath);
        // 若文件不存在则自动创建
        if(!mdr.exists()){
            mdr.mkdir();
        }
        try {
            // 文件转存
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(filename);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            // 获取文件输入流
            FileInputStream fis = new FileInputStream(basePath + name);
            // 获取浏览器输出流
            ServletOutputStream os = response.getOutputStream();
            // 设置图片返回类型
            response.setContentType("/image/jpeg");
            int len = 0;
            // 缓存
            byte[] bytes = new byte[1024];
            // 写出到浏览器
            while((len = fis.read(bytes)) != -1){
                os.write(bytes,0,len);
            }
            // 关闭资源
            fis.close();
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
