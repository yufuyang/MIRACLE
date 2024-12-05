package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("order")
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {

    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-已退款
     */
    private Integer status;


    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单商品列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<OrderItem> orderItems;

    /**
     * 订单日志列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<OrderLog> orderLogs;

    /**
     * 支付方式ID
     */
    private Long payMethodId;

    /**
     * 支付方式类型：1-微信 2-支付宝 3-现金
     */
    private Integer payType;

    /**
     * 支付方式名称
     */
    private String payMethodName;
}