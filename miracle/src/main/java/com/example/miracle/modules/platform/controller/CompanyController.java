package com.example.miracle.modules.platform.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.dto.query.CompanyPageQuery;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公司信息控制器
 */
@RestController
@RequestMapping("/platform/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;


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
        companyService.updateById(company);
        return SingleResponse.of(company);
    }

    /**
     * 获取公司详情
     */
    @GetMapping("/{id}")
    public SingleResponse<Company> getById(@PathVariable Long id) {
        return SingleResponse.of(companyService.getById(id));
    }

    /**
     * 删除公司
     */
    @DeleteMapping("/{id}")
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {
        return SingleResponse.of(companyService.removeById(id));
    }

    /**
     * 分页查询公司列表
     */
    @PostMapping("/page")
    public MultiResponse<Company> pageQuery(@RequestBody CompanyPageQuery query) {
        return companyService.pageQuery(query);
    }
} 