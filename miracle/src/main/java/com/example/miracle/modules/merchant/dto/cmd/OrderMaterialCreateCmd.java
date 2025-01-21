package com.example.miracle.modules.merchant.dto.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderMaterialCreateCmd {

    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 总金额
     */
    private BigDecimal amount;
}
