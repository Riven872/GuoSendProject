package com.Guo.GuoSend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching //开启SpringCache注解方式的缓存功能
public class GuoSendApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuoSendApplication.class, args);
        log.info("项目启动");
    }
}
