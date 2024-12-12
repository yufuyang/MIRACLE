package com.example.miracle.common.exception;

import com.example.miracle.common.dto.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public SingleResponse<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return SingleResponse.buildFailure(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public SingleResponse<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return SingleResponse.buildFailure("系统异常，请联系管理员");
    }
}