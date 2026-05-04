package com.bdqn.class7.springboot.dms.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：让 Controller 不用写 try-catch，异常统一转 Result。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBiz(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValid(MethodArgumentNotValidException e) {
        FieldError err = e.getBindingResult().getFieldError();
        return Result.fail(400, err == null ? "参数不合法" : err.getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBind(BindException e) {
        FieldError err = e.getBindingResult().getFieldError();
        return Result.fail(400, err == null ? "参数不合法" : err.getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleAll(Exception e) {
        log.error("系统异常", e);
        return Result.fail("系统异常: " + e.getMessage());
    }
}
