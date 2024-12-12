package com.example.miracle.modules.platform.dto.cmd;

import lombok.Data;


/**
 * 更新环境照片排序命令
 */
@Data
public class UpdateEnvironmentImageSortCmd {


    private Long id;

    /**
     * 排序号
     */
    private Integer sort;
} 