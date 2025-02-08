package com.example.miracle.modules.merchant.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MerchantOrderDTO {

    private Long id;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 商户ID
     */
    private Long merchantId;

    private String merchantName;
    /**
     * 公司ID
     */
    private Long companyId;

    private String companyName;
    /**
     * 产品ID
     */
    private Long productId;

    private String productName;


    private String productUrl;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 订单状态：1-待付款，2-待确认，3-待发货，4-配送中，5-已送达，6-已完成，7-已取消
     */
    private Integer status;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 收货人电话
     */
    private String receiverPhone;
    /**
     * 收货人地址
     */
    private String receiverAddress;
    /**
     * 备注
     */
    private String remark;


    private LocalDateTime createTime;
}
