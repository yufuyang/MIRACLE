package com.example.miracle.modules.merchant.dto.cmd;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MerchantOrderCreateCmd {

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


    private List<OrderMaterialCreateCmd> materials;
}
