package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_product_step")
public class CompanyProductStep extends BaseEntity {

    private Long productId;

    private String title;

    private String description;

    private String mediaType;

    private String mediaUrl;

    private Integer sort;
} 