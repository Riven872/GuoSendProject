package com.Guo.GuoSend.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常处理器（基于AOP进行代理）
 */

//处理标注了这两个注解的类
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

}
