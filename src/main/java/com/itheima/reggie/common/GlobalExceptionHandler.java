package com.itheima.reggie.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author myz
 * @create 2022/12/28-15:14
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if(ex.getMessage().contains("Duplicate entry")){
            String[] strings = ex.getMessage().split(" ");
            return R.error(strings[2] + "已存在");
        }
        return R.error("未知错误");
    }
    @ExceptionHandler
    public R<String> exceptionHandler(CustomException ex) {
        String message = ex.getMessage();
        return R.error(message);
    }
}
