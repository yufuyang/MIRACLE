package com.example.miracle.modules.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.platform.dto.query.CompanyPageQuery;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.company.entity.CompanyUser;
import com.example.miracle.modules.platform.mapper.CompanyMapper;
import com.example.miracle.modules.platform.service.CompanyService;
import com.example.miracle.modules.company.service.CompanyUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final CompanyUserService companyUserService;

    @Override
    public MultiResponse<Company> pageQuery(CompanyPageQuery companyPageQuery) {
        // 构建查询条件
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<Company>()
                .like(StringUtils.isNotBlank(companyPageQuery.getCompanyName()), Company::getCompanyName, companyPageQuery.getCompanyName())
                .like(StringUtils.isNotBlank(companyPageQuery.getContactName()), Company::getContactName, companyPageQuery.getContactName())
                .eq(companyPageQuery.getStatus() != null, Company::getStatus, companyPageQuery.getStatus())
                .orderByDesc(Company::getCreateTime);

        // 执行分页查询
        Page<Company> page = this.page(new Page<>(companyPageQuery.getPageNum(), companyPageQuery.getPageSize()), wrapper);

        // 返回结果
        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Company company) {
        // 保存公司信息
        boolean result = super.save(company);

        if (result) {
            // 创建公司管理员

            CompanyUser companyUser = new CompanyUser();
            companyUser.setCompanyId(company.getId());
            companyUser.setUsername(company.getContactPhone());
            companyUser.setRealName(company.getContactName());
            companyUser.setPhone(company.getContactPhone());
            companyUser.setPassword("123456");
            companyUser.setStatus(1);
            companyUserService.save(companyUser);
        }

        return result;
    }
}