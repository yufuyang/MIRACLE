package com.example.miracle.common.dto;

import lombok.Data;

@Data
public class ResultDTO<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResultDTO<T> ok() {
        return ok(null);
    }

    public static <T> ResultDTO<T> ok(T data) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setCode(200);
        resultDTO.setMessage("success");
        resultDTO.setData(data);
        return resultDTO;
    }

    public static <T> ResultDTO<T> error(String message) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setCode(500);
        resultDTO.setMessage(message);
        return resultDTO;
    }
}