package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("return_order")
@EqualsAndHashCode(callSuper = true)
public class ReturnOrder extends BaseEntity {

    /**
     * 退货单号
     */
    private String returnNo;

    /**
     * 关联退款单ID
     */
    private Long refundId;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 退货地址
     */
    private String returnAddress;

    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 物流单号
     */
    private String logisticsNo;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 收货人
     */
    private String receiveUser;

    /**
     * 收货备注
     */
    private String receiveRemark;

    /**
     * 状态：0-待退货 1-退货中 2-已收货 3-已拒收
     */
    private Integer status;

    /**
     * 操作人
     */
    private String operator;
}