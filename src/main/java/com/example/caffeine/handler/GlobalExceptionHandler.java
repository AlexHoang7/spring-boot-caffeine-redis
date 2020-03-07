package com.example.caffeine.handler;

import com.example.caffeine.bean.ResultBean;
import com.example.caffeine.constant.ResponseEnum;
import com.example.caffeine.exception.GlobalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author Alex
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(GlobalException.class)
//    public ResultBean<?> globalException(GlobalException globalException) {
//        System.out.println("----------global exception handler----------");
//        return new ResultBean<>(globalException.getErrorCode(), globalException.getErrorMessage());
//    }

    @ExceptionHandler(Exception.class)
    public ResultBean<?> exception(Exception e) {
        System.out.println(e.getClass());
        if (e instanceof  GlobalException) {
            System.out.println(((GlobalException) e).getErrorCode() + " ------------ " + ((GlobalException) e).getErrorMessage());
        }
        System.out.println("--------------exception handler--------???");
        return new ResultBean<>(ResponseEnum.RESPONSE_CODE_99.value(), ResponseEnum.RESPONSE_DESC_99.value());
    }
}
