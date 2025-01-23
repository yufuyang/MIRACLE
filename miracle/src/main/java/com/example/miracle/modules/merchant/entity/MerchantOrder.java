package com.example.miracle.modules.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_order")
public class MerchantOrder extends BaseEntity {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 商户ID
     */
    private Long merchantId;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 订单状态：1-待审批，2-待发货，3-审批拒绝，4-发货中，5-已完成，6-已取消
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
    /**
     * 备注
     */
    private String remark;
}
