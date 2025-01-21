package com.example.miracle.modules.merchant.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.merchant.dto.MerchantOrderDTO;
import com.example.miracle.modules.merchant.dto.MerchantOrderInfoDTO;
import com.example.miracle.modules.merchant.dto.OrderMaterialDTO;
import com.example.miracle.modules.merchant.dto.cmd.MerchantOrderCreateCmd;
import com.example.miracle.modules.merchant.dto.cmd.OrderMaterialCreateCmd;
import com.example.miracle.modules.merchant.dto.query.MerchantOrderPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantOrder;
import com.example.miracle.modules.merchant.service.MerchantOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant/order")
@RequiredArgsConstructor
public class MerchantOrderController {


    private final MerchantOrderService merchantOrderService;

    private final BaseController baseController;

    @PostMapping("/list")
    public MultiResponse<MerchantOrderDTO> pageQuery(@RequestBody MerchantOrderPageQuery query) {

        Long merchantId = baseController.getMerchantId();
        query.setMerchantId(merchantId);

        return merchantOrderService.pageQuery(query);
    }

    @PostMapping("/save")
    public SingleResponse save(@RequestBody MerchantOrderCreateCmd merchantOrderCreateCmd) {

        Long merchantId = baseController.getMerchantId();

        merchantOrderCreateCmd.setMerchantId(merchantId);

        merchantOrderService.create(merchantOrderCreateCmd);

        return SingleResponse.buildSuccess();
    }

    @PutMapping("/cancel/{id}")
    public SingleResponse cancel(@PathVariable Long id) {

        Long merchantId = baseController.getMerchantId();

        MerchantOrder merchantOrder = merchantOrderService.getById(id);

        if (merchantOrder == null || !merchantOrder.getMerchantId().equals(merchantId)) {
            return SingleResponse.buildFailure("订单不存在");
        }

        if (merchantOrder.getStatus() != 1) {
            return SingleResponse.buildFailure("订单状态不正确");
        }

        merchantOrder.setStatus(6);

        merchantOrderService.updateById(merchantOrder);

        return SingleResponse.buildSuccess();

    }

    @GetMapping("/get/{id}")
    public SingleResponse<MerchantOrderInfoDTO> get(@PathVariable Long id) {

        return merchantOrderService.getOrderMaterial(id);
    }


}
