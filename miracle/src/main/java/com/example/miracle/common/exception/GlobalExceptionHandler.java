package com.example.miracle.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultDTO<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return ResultDTO.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultDTO<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return ResultDTO.error("系统异常，请联系管理员");
    }
}