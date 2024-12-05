package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 支付方式ID
     */
    private Long payMethodId;

    /**
     * 订单商品列表
     */
    private List<OrderItemDTO> orderItems;

    /**
     * 订单备注
     */
    private String remark;
}
