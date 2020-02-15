package com.example.caffeine.handler;

import com.example.caffeine.bean.ResultBean;
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

    @ExceptionHandler(GlobalException.class)
    public ResultBean<?> globalException(GlobalException globalException) {
        return new ResultBean<>(globalException.getErrorCode(), globalException.getErrorMessage());
    }
}
