package com.example.miracle.modules.merchant.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MerchantOrderInfoDTO {


    private Long id;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 商户ID
     */
    private Long merchantId;
    /**
     * 商户名称
     */
    private String merchantName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 产品名称
     */
    private String productName;

    private String productUrl;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 订单状态：1-待确认，2-待发货，3-配送中，4-已送达，5-已完成，6-已取消
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

    /**
     * 订单物料
     */
    private List<OrderMaterialDTO> orderMaterials;

    /**
     * 物流公司
     */
    private String logisticsCompany;
    /**
     * 物流单号
     */
    private String logisticsNo;
    /**
     * 审批时间
     */
    private LocalDateTime approveTime;
    /**
     * 发货时间
     */
    private LocalDateTime logisticsTime;
    /**
     * 完成时间
     */
    private LocalDateTime finishedTime;
}
