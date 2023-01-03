package com.itheima.reggie.common;

/**
 * @author myz
 * @create 2022/12/28-20:08
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long empId){
        threadLocal.set(empId);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
