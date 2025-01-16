package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.query.ProductMaterialPageQry;
import com.example.miracle.modules.company.entity.ProductMaterial;
import com.example.miracle.modules.company.service.ProductMaterialService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/product/material")
@RequiredArgsConstructor
public class CompanyProductMaterialController {

    private final ProductMaterialService productMaterialService;



    @PostMapping
    public SingleResponse save(@RequestBody ProductMaterial productMaterial) {

        productMaterialService.save(productMaterial);

        return SingleResponse.buildSuccess();
    }

    @PostMapping("/update")
    public SingleResponse update(@RequestBody ProductMaterial productMaterial) {

        productMaterialService.updateById(productMaterial);

        return SingleResponse.buildSuccess();
    }

    @DeleteMapping("/delete/{id}")
    public SingleResponse delete(@PathVariable Long id) {

        productMaterialService.removeById(id);

        return SingleResponse.buildSuccess();
    }
    @PostMapping("/list")
    public MultiResponse<ProductMaterial> pageQuery(@RequestBody ProductMaterialPageQry productMaterialPageQry) {

        LambdaQueryWrapper<ProductMaterial> query =new LambdaQueryWrapper<>();
        query.eq(productMaterialPageQry.getProductId()!= null, ProductMaterial::getProductId, productMaterialPageQry.getProductId());

        Page<ProductMaterial> page = productMaterialService.page(new Page<>(productMaterialPageQry.getPageNum(), productMaterialPageQry.getPageSize()), query);


        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }
}
