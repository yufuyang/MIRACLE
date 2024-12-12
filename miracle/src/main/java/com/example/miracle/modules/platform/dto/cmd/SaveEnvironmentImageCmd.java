package com.example.miracle.modules.platform.dto.cmd;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 * 保存环境照片命令
 */
@Data
public class SaveEnvironmentImageCmd {

    /**
     * 所属者ID（公司ID或商户ID）
     */
    @NotNull(message = "所属者ID不能为空")
    private Long ownerId;

    /**
     * 所属者类型（company/merchant）
     */
    @NotBlank(message = "所属者类型不能为空")
    private String ownerType;

    /**
     * 图片地址
     */
    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    /**
     * 排序号
     */
    private Integer sort = 0;
} 