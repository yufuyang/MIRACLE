package com.example.miracle.modules.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.merchant.dto.MerchantOrderDTO;
import com.example.miracle.modules.merchant.dto.MerchantOrderInfoDTO;
import com.example.miracle.modules.merchant.dto.OrderMaterialDTO;
import com.example.miracle.modules.merchant.dto.cmd.MerchantOrderCreateCmd;
import com.example.miracle.modules.merchant.dto.query.MerchantOrderPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantOrder;

public interface MerchantOrderService extends IService<MerchantOrder> {


    MultiResponse<MerchantOrderDTO> pageQuery(MerchantOrderPageQuery query);


    SingleResponse<MerchantOrderInfoDTO> getOrderMaterial(Long id);


    SingleResponse create(MerchantOrderCreateCmd merchantOrderCreateCmd);
}
