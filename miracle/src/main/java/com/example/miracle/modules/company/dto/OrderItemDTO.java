package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 购买数量
     */
    private Integer quantity;

}
