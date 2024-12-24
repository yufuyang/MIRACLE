package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductStep;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.service.CompanyProductStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/product/step")
public class CompanyProductStepController {


    private final CompanyProductStepService companyProductStepService;

    private final CompanyProductService companyProductService;

    private final BaseController baseController;

    /**
     * 获取产品步骤列表
     */
    @GetMapping("/list/{productId}")
    public MultiResponse<CompanyProductStep> getStepsByProductId(@PathVariable Long productId) {

        authCheck(productId);

        List<CompanyProductStep> companyProductSteps = companyProductStepService.list(
                new LambdaQueryWrapper<CompanyProductStep>()
                        .eq(CompanyProductStep::getProductId, productId)
                        .orderByAsc(CompanyProductStep::getSort)
        );

        return MultiResponse.of(companyProductSteps);
    }

    /**
     * 添加产品步骤
     */
    @PostMapping
    public SingleResponse addStep(@RequestBody CompanyProductStep step) {

        authCheck(step.getProductId());

        companyProductStepService.save(step);

        return SingleResponse.buildSuccess();
    }

    /**
     * 更新产品步骤
     */
    @PutMapping
    public SingleResponse updateStep(@RequestBody CompanyProductStep step) {

        authCheck(step.getProductId());

        companyProductStepService.updateById(step);

        return SingleResponse.buildSuccess();
    }

    /**
     * 删除产品步骤
     */
    @DeleteMapping("/{id}")
    public SingleResponse deleteStep(@PathVariable Long id) {

        CompanyProductStep companyProductStep = companyProductStepService.getById(id);

        if (Objects.isNull(companyProductStep)) {
            throw new BusinessException("步骤不存在");
        }

        authCheck(companyProductStep.getProductId());

        companyProductStepService.removeById(id);

        return SingleResponse.buildSuccess();
    }

    /**
     * 更新步骤排序
     */
    @PutMapping("/sort")
    public SingleResponse updateStepsSort(@RequestBody List<CompanyProductStep> steps) {

        if (steps.isEmpty()) {
            throw new BusinessException("步骤不能为空");
        }

        Long productId = steps.get(0).getProductId();

        authCheck(productId);

        for (CompanyProductStep step : steps) {
            if (!Objects.equals(step.getProductId(), productId)) {
                throw new BusinessException("步骤不属于同一产品");
            }

            if (Objects.isNull(step.getId())) {
                throw new BusinessException("步骤id不能为空");
            }

            companyProductStepService.updateById(step);
        }

        return SingleResponse.buildSuccess();
    }


    private void authCheck(Long productId) {
        CompanyProduct product = companyProductService.getById(productId);
        if (Objects.isNull(product)) {
            throw new BusinessException("产品不存在");
        }

        Long companyId = baseController.getCompanyId();
        if (!Objects.equals(product.getCompanyId(), companyId)) {
            throw new BusinessException("无权操作");
        }
    }
} 