package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class ReturnLogisticsDTO {

    /**
     * 退货单号
     */
    private String returnNo;

    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 物流单号
     */
    private String logisticsNo;

    /**
     * 操作人
     */
    private String operator;
}
