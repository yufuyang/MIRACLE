package com.example.miracle.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("rank_data")
@EqualsAndHashCode(callSuper = true)
public class RankData extends BaseEntity {

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 目标名称
     */
    private String targetName;

    /**
     * 排名数值
     */
    private BigDecimal rankValue;

    /**
     * 排名
     */
    private Integer rankNo;

    /**
     * 排名日期
     */
    private LocalDate rankDate;
}