package com.example.miracle.common.dto;

import lombok.Data;

@Data
public class UploadResultDTO {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件URL
     */
    private String fileUrl;
}