package com.example.miracle.modules.website.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 公司产品图片控制器
 */
@RestController
@RequestMapping("/website/company/product/image")
@RequiredArgsConstructor
public class WebsiteCompanyProductImageController {

    private final CompanyProductImageService imageService;



    /**
     * 查询产品图片列表
     */
    @GetMapping("/list/{productId}")
    public MultiResponse<CompanyProductImage> listByProductId(@PathVariable Long productId) {
        return imageService.listByProductId(productId);
    }

} 