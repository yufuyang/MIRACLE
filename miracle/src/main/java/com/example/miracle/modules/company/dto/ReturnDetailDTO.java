package com.example.miracle.modules.company.dto;

import com.example.miracle.modules.company.entity.ReturnItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReturnDetailDTO {

    /**
     * 退货单号
     */
    private String returnNo;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 订单编号
     */
    private String orderNo;

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
     * 退货商品列表
     */
    private List<ReturnItem> items;
}
