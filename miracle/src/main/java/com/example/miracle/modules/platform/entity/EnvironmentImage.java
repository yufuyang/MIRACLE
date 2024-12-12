package com.example.miracle.modules.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 环境照片实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("environment_image")
public class EnvironmentImage extends BaseEntity {

    /**
     * 所属者ID（公司ID或商户ID）
     */
    private Long ownerId;

    /**
     * 所属者类型（company/merchant）
     */
    private String ownerType;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 排序号
     */
    private Integer sort;
} 