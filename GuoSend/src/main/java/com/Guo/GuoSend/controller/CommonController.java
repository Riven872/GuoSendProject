package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    /**
     * 文件上传
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile foo) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(foo.toString());
        return null;
    }
}
