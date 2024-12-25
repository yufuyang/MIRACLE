package com.example.miracle.modules.website.controller;

import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.dto.query.CompanyPageQuery;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.service.CompanyService;
import com.example.miracle.modules.website.dto.CompanyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公司信息控制器
 */
@RestController
@RequestMapping("/website/company")
@RequiredArgsConstructor
public class WebsiteCompanyController {

    private final CompanyService companyService;


    /**
     * 获取公司详情
     */
    @GetMapping("/{id}")
    public SingleResponse<Company> getById(@PathVariable Long id) {
        return SingleResponse.of(companyService.getById(id));
    }

    /**
     * 分页查询公司列表
     */
    @PostMapping("/page")
    public MultiResponse<CompanyDTO> pageQuery(@RequestBody CompanyPageQuery query) {
        return companyService.pageQueryDTO(query);
    }


    /**
     * 新增公司
     */
    @PostMapping("/register")
    public SingleResponse<Company> save(@RequestBody Company company) {
        companyService.save(company);
        return SingleResponse.of(company);
    }

} 