package com.example.miracle.modules.company.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/base")
@RequiredArgsConstructor
public class CompanyBaseController {

    private final CompanyService companyService;

    private final BaseController baseController;

    /**
     * 新增公司
     */
    @PostMapping
    public SingleResponse<Company> save(@RequestBody Company company) {
        companyService.save(company);
        return SingleResponse.of(company);
    }

    /**
     * 修改公司信息
     */
    @PutMapping
    public SingleResponse<Company> update(@RequestBody Company company) {

        Long companyId = baseController.getCompanyId();
        company.setId(companyId);
        companyService.updateById(company);

        return SingleResponse.of(company);
    }

    /**
     * 获取公司详情
     */
    @GetMapping("/info")
    public SingleResponse<Company> getById() {

        Long companyId = baseController.getCompanyId();
        return SingleResponse.of(companyService.getById(companyId));
    }
}
