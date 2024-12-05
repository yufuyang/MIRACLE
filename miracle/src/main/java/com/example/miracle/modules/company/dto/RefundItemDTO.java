package com.example.miracle.modules.company.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RefundItemDTO {

    /**
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 退货数量
     */
    private Integer quantity;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退货原因
     */
    private String reason;
}
