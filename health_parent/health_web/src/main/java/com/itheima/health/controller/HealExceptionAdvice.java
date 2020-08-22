package com.itheima.health.controller;


import com.itheima.health.entity.Result;
import com.itheima.health.exception.HealthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常
 * ExceptionHandler注解的异常顺序是从小到大
 * RestControllerAdvice 注解返回时是json类型
 */
@RestControllerAdvice
public class HealExceptionAdvice {

    /**
     * 工作不用System.out.println 或者 e.printStackTrace
     * 使用log输出
     */
    private static final Logger log = LoggerFactory.getLogger(HealthException.class);

    @ExceptionHandler(HealthException.class)
    public Result HealHealthException(HealthException e){
        log.error(e.getMessage());
        return new Result(false,e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result HealException(Exception e){

        //error log记录异常的信息
        //debug 记录重要的数据,如id 等
        //info 打印日志,记录流程
        log.error("发生异常");
        return new Result(false,"服务器异常");
    }
}
