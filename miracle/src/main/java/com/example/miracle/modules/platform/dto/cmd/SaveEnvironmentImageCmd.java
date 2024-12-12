package com.example.miracle.modules.platform.dto.cmd;

import lombok.Data;


/**
 * 保存环境照片命令
 */
@Data
public class SaveEnvironmentImageCmd {


    private Long ownerId;


    private String ownerType;


    private String imageUrl;

    /**
     * 排序号
     */
    private Integer sort = 0;
} 