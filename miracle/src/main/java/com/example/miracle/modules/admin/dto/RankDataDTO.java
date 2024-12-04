package com.example.miracle.modules.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RankDataDTO {

    /**
     * 排名
     */
    private Integer rankNo;

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
     * 排名日期
     */
    private LocalDate rankDate;

    /**
     * 环比变化（与上一期相比）
     */
    private Integer change;

    /**
     * 趋势：1-上升 0-不变 -1-下降
     */
    private Integer trend;
}