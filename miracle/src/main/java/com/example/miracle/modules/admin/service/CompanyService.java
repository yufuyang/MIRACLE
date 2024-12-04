package com.example.miracle.modules.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.admin.entity.Company;
import com.example.miracle.modules.admin.entity.CompanyUser;

public interface CompanyService extends IService<Company> {

    /**
     * 创建公司
     */
    void createCompany(Company company);

    /**
     * 更新公司信息
     */
    void updateCompany(Company company);

    /**
     * 审核公司
     */
    void auditCompany(Long id, Integer status, Long auditUserId);

    /**
     * 分页查询公司列表
     */
    Page<Company> pageCompany(Integer current, Integer size, String companyName, String companyCode, Integer status);

    /**
     * 获取公司详情
     */
    Company getCompanyDetail(Long id);

    /**
     * 生成公司编码
     */
    String generateCompanyCode();

    /**
     * 创建公司管理员
     */
    void createCompanyAdmin(Long companyId, CompanyUser companyUser);
}