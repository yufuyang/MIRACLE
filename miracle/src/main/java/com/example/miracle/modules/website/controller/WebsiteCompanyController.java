package com.example.miracle.modules.website.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.entity.CompanyUser;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.company.service.CompanyUserService;
import com.example.miracle.modules.platform.dto.query.CompanyPageQuery;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.service.CompanyService;
import com.example.miracle.modules.website.dto.CompanyDTO;
import com.example.miracle.modules.website.dto.CompanyInfo;
import com.example.miracle.modules.website.dto.cmd.CompanyRegisterCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 公司信息控制器
 */
@RestController
@RequestMapping("/website/company")
@RequiredArgsConstructor
public class WebsiteCompanyController {

    private final CompanyService companyService;

    private final CompanyUserService companyUserService;

    private final CompanyProductService companyProductService;

    private final CompanyProductStatsService companyProductStatsService;


    /**
     * 获取公司详情
     */
    @GetMapping("/{id}")
    public SingleResponse<CompanyInfo> getById(@PathVariable Long id) {

        CompanyInfo companyDTO = new CompanyInfo();

        Company company = companyService.getById(id);
        BeanUtils.copyProperties(company, companyDTO);

        LambdaQueryWrapper<CompanyProduct> companyProductLambdaQueryWrapper = new LambdaQueryWrapper<>();
        companyProductLambdaQueryWrapper.eq(CompanyProduct::getCompanyId, id);
        long productCount = companyProductService.count(companyProductLambdaQueryWrapper);


        Integer intentionCount = companyProductStatsService.getCompanyProductStatsCount(id).getData();

        companyDTO.setProductCount((int) productCount);

        companyDTO.setIntentionCount(intentionCount);

        return SingleResponse.of(companyDTO);
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
    public SingleResponse<Company> save(@RequestBody CompanyRegisterCmd companyRegisterCmd) {

        Company company = new Company();
        BeanUtils.copyProperties(companyRegisterCmd, company);

        companyService.save(company);

        CompanyUser companyUser = new CompanyUser();
        BeanUtils.copyProperties(companyRegisterCmd, companyUser);
        companyUser.setCompanyId(company.getId());

        companyUserService.save(companyUser);

        return SingleResponse.of(company);
    }

} 