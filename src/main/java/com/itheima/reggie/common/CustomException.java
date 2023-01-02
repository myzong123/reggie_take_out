package com.itheima.reggie.common;

/**
 * 自定义异常类
 * @author myz
 * @create 2022/12/28-21:25
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
