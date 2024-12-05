package com.example.miracle.modules.admin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.FileUtil;
import com.example.miracle.modules.admin.entity.Company;
import com.example.miracle.modules.admin.entity.CompanyUser;
import com.example.miracle.modules.admin.mapper.CompanyMapper;
import com.example.miracle.modules.admin.mapper.CompanyUserMapper;
import com.example.miracle.modules.admin.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final CompanyUserMapper companyUserMapper;
    private final FileUtil fileUtil;

    @Override
    public void createCompany(Company company) {
        // 验证公司名称是否存在
        if (this.count(new LambdaQueryWrapper<Company>()
                .eq(Company::getCompanyName, company.getCompanyName())) > 0) {
            throw new BusinessException("公司名称已存在");
        }

        // 验证营业执照号是否存在
        if (this.count(new LambdaQueryWrapper<Company>()
                .eq(Company::getLicenseNo, company.getLicenseNo())) > 0) {
            throw new BusinessException("营业执照号已存在");
        }

        // 生成公司编码
        company.setCompanyCode(generateCompanyCode());
        // 设置初始状态
        company.setStatus(0);

        this.save(company);

        // 创建默认管理员账号
        CompanyUser admin = new CompanyUser();
        admin.setCompanyId(company.getId());
        admin.setUsername(company.getContactPhone()); // 使用公司编码作为管理员账号前缀
        admin.setPassword("123456"); // 默认密码
        admin.setRealName(company.getLegalPerson());
        admin.setPhone(company.getContactPhone());
        admin.setEmail(company.getEmail());
        admin.setStatus(1);

        companyUserMapper.insert(admin);

    }

    @Override
    public void updateCompany(Company company) {
        Company existCompany = this.getById(company.getId());
        if (existCompany == null) {
            throw new BusinessException("公司不存在");
        }

        // 验证公司名称是否存在
        if (!existCompany.getCompanyName().equals(company.getCompanyName()) &&
                this.count(new LambdaQueryWrapper<Company>()
                        .eq(Company::getCompanyName, company.getCompanyName())) > 0) {
            throw new BusinessException("公司名称已存在");
        }

        // 验证营业执照号是否存在
        if (!existCompany.getLicenseNo().equals(company.getLicenseNo()) &&
                this.count(new LambdaQueryWrapper<Company>()
                        .eq(Company::getLicenseNo, company.getLicenseNo())) > 0) {
            throw new BusinessException("营业执照号已存在");
        }

        // 如果更新了营业执照，删除旧文件
        if (StringUtils.isNotBlank(existCompany.getLicenseImage())
                && !existCompany.getLicenseImage().equals(company.getLicenseImage())) {
            fileUtil.deleteFile(existCompany.getLicenseImage());
        }

        this.updateById(company);
    }

    @Override
    public void auditCompany(Long id, Integer status, Long auditUserId) {
        Company company = this.getById(id);
        if (company == null) {
            throw new BusinessException("公司不存在");
        }

        if (company.getStatus() != 0) {
            throw new BusinessException("公司已审核");
        }

        company.setStatus(status);
        company.setAuditUserId(auditUserId);
        company.setAuditTime(LocalDateTime.now());

        this.updateById(company);
    }

    @Override
    public Page<Company> pageCompany(Integer current, Integer size, String companyName, String companyCode, Integer status) {

        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(companyName), Company::getCompanyName, companyName)
                .eq(StringUtils.isNotBlank(companyCode), Company::getCompanyCode, companyCode)
                .eq(status != null, Company::getStatus, status)
                .orderByDesc(Company::getCreateTime);

        return this.page(new Page<>(current, size), wrapper);
    }

    @Override
    public Company getCompanyDetail(Long id) {
        Company company = this.getById(id);
        if (company == null) {
            throw new BusinessException("公司不存在");
        }
        return company;
    }

    @Override
    public String generateCompanyCode() {
        String code;
        do {
            // 生成6位随机数字
            code = "C" + RandomUtil.randomNumbers(6);
        } while (this.count(new LambdaQueryWrapper<Company>()
                .eq(Company::getCompanyCode, code)) > 0);
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCompanyAdmin(Long companyId, CompanyUser companyUser) {
        // 验证公司是否存在且状态正常
        Company company = this.getById(companyId);
        if (company == null) {
            throw new BusinessException("公司不存在");
        }
        if (company.getStatus() != 1) {
            throw new BusinessException("只能为正常状态的公司创建管理员");
        }

        // 验证用户名是否存在
        if (companyUserMapper.selectCount(new LambdaQueryWrapper<CompanyUser>()
                .eq(CompanyUser::getUsername, companyUser.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 设置公司ID和状态
        companyUser.setCompanyId(companyId);
        companyUser.setStatus(1);
        
        companyUserMapper.insert(companyUser);
    }
}