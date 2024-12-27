package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("company_product_stats")
public class CompanyProductStats extends BaseEntity {

    private Long companyId;

    private Long productId;


    private LocalDate statsDate;

    private Integer viewCount;

    private Integer intentionCount;

}