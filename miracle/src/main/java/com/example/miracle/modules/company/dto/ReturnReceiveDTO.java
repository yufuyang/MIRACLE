package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class ReturnReceiveDTO {

    /**
     * 退货单号
     */
    private String returnNo;

    /**
     * 收货备注
     */
    private String receiveRemark;

    /**
     * 操作人
     */
    private String operator;

}
