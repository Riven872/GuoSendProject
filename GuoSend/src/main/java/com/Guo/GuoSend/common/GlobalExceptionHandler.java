package com.Guo.GuoSend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器（基于AOP进行代理）
 */

//处理标注了这两个注解的类
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法，专门处理SQLIntegrityConstraintViolationException异常
     *
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.info("异常信息为 ：{}", ex.getMessage());
        //账号的name在数据库中设置的Unique，因此重复添加账号相同的账号名时会报"Duplicate entry '重复账号名' for key 'xxx_字段名'"异常
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            //将异常信息进行拆分，得出是哪个账号名字已经存在
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        //其他错误暂时无法确切定位，因此先临时处理
        return R.error("未知错误");
    }
}
