package com.example.miracle.modules.company.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.cmd.CompanyLogisticsCmd;
import com.example.miracle.modules.merchant.dto.MerchantOrderDTO;
import com.example.miracle.modules.merchant.dto.MerchantOrderInfoDTO;
import com.example.miracle.modules.merchant.dto.query.MerchantOrderPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantOrder;
import com.example.miracle.modules.merchant.service.MerchantOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/company/order")
@RequiredArgsConstructor
public class CompanyOrderController {

    private final MerchantOrderService merchantOrderService;

    private final BaseController baseController;


    @PostMapping("/list")
    public MultiResponse<MerchantOrderDTO> pageQuery(@RequestBody MerchantOrderPageQuery query) {

        Long companyId = baseController.getCompanyId();

        query.setCompanyId(companyId);


        return merchantOrderService.pageQuery(query);
    }

    @GetMapping("/{id}")
    public SingleResponse<MerchantOrderInfoDTO> get(@PathVariable Long id) {

        return merchantOrderService.getOrderMaterial(id);
    }

    @PutMapping("/approve/{id}")
    public SingleResponse approve(@PathVariable Long id) {

        Long companyId = baseController.getCompanyId();

        MerchantOrder merchantOrder = merchantOrderService.getById(id);

        if (merchantOrder == null || !merchantOrder.getCompanyId().equals(companyId)) {
            return SingleResponse.buildFailure("订单不存在");
        }

        merchantOrder.setStatus(2);
        merchantOrder.setApproveTime(LocalDateTime.now());
        merchantOrderService.updateById(merchantOrder);

        return SingleResponse.buildSuccess();
    }

    @PutMapping("/reject/{id}")
    public SingleResponse reject(@PathVariable Long id) {

        Long companyId = baseController.getCompanyId();

        MerchantOrder merchantOrder = merchantOrderService.getById(id);

        if (merchantOrder == null || !merchantOrder.getCompanyId().equals(companyId)) {
            return SingleResponse.buildFailure("订单不存在");
        }

        merchantOrder.setStatus(3);
        merchantOrder.setApproveTime(LocalDateTime.now());
        merchantOrderService.updateById(merchantOrder);

        return SingleResponse.buildSuccess();
    }


    @PutMapping("/complete/{id}")
    public SingleResponse complete(@PathVariable Long id) {

        Long companyId = baseController.getCompanyId();

        MerchantOrder merchantOrder = merchantOrderService.getById(id);

        if (merchantOrder == null || !merchantOrder.getCompanyId().equals(companyId)) {
            return SingleResponse.buildFailure("订单不存在");
        }

        merchantOrder.setStatus(5);
        merchantOrder.setFinishedTime(LocalDateTime.now());
        merchantOrderService.updateById(merchantOrder);

        return SingleResponse.buildSuccess();
    }

    @PostMapping("/deliver")
    public SingleResponse deliver(@RequestBody CompanyLogisticsCmd companyLogisticsCmd) {

        Long companyId = baseController.getCompanyId();

        MerchantOrder merchantOrder = merchantOrderService.getById(companyLogisticsCmd.getOrderId());

        if (merchantOrder == null || !merchantOrder.getCompanyId().equals(companyId)) {
            return SingleResponse.buildFailure("订单不存在");
        }

        merchantOrder.setLogisticsCompany(companyLogisticsCmd.getLogisticsCompany());

        merchantOrder.setLogisticsNo(companyLogisticsCmd.getLogisticsNo());

        merchantOrder.setLogisticsTime(LocalDateTime.now());

        merchantOrder.setStatus(4);

        merchantOrderService.updateById(merchantOrder);

        return SingleResponse.buildSuccess();
    }

}
