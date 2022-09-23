package com.Guo.GuoSend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class GuoSendApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuoSendApplication.class, args);
        log.info("项目启动");
    }
}
