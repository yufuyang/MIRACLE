package com.example.miracle.modules.company.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.FileUtil;
import com.example.miracle.modules.admin.entity.Company;
import com.example.miracle.modules.admin.service.CompanyService;
import com.example.miracle.modules.company.entity.Merchant;
import com.example.miracle.modules.company.entity.MerchantUser;
import com.example.miracle.modules.company.mapper.MerchantMapper;
import com.example.miracle.modules.company.mapper.MerchantUserMapper;
import com.example.miracle.modules.company.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final CompanyService companyService;
    private final MerchantUserMapper merchantUserMapper;

    private final FileUtil fileUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMerchant(Merchant merchant) {
        // 验证公司是否存在
        Company company = companyService.getById(merchant.getCompanyId());
        if (company == null) {
            throw new BusinessException("所属公司不存在");
        }

        // 验证商户名称是否存在
        if (this.count(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMerchantName, merchant.getMerchantName())) > 0) {
            throw new BusinessException("商户名称已存在");
        }

        // 验证营业执照号是否存在
        if (this.count(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getBusinessLicenseNo, merchant.getBusinessLicenseNo())) > 0) {
            throw new BusinessException("营业执照号已存在");
        }

        // 生成商户编码
        merchant.setMerchantCode(generateMerchantCode(merchant.getCompanyId()));
        // 设置初始状态
        merchant.setStatus(0);

        this.save(merchant);

        // 创建默认管理员账号
        MerchantUser admin = new MerchantUser();
        admin.setMerchantId(merchant.getId());
        admin.setUsername(merchant.getMerchantCode() + "_admin"); // 使用商户编码作为管理员账号前缀
        admin.setPassword("123456"); // 默认密码
        admin.setRealName(merchant.getContactPerson());
        admin.setPhone(merchant.getContactPhone());
        admin.setEmail(merchant.getEmail());
        admin.setStatus(1);

        merchantUserMapper.insert(admin);
    }

    @Override
    public void updateMerchant(Merchant merchant) {
        Merchant existMerchant = this.getById(merchant.getId());
        if (existMerchant == null) {
            throw new BusinessException("商户不存在");
        }

        // 验证商户名称是否存在
        if (!existMerchant.getMerchantName().equals(merchant.getMerchantName()) &&
                this.count(new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getMerchantName, merchant.getMerchantName())) > 0) {
            throw new BusinessException("商户名称已存在");
        }

        // 验证营业执照号是否存在
        if (!existMerchant.getBusinessLicenseNo().equals(merchant.getBusinessLicenseNo()) &&
                this.count(new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getBusinessLicenseNo, merchant.getBusinessLicenseNo())) > 0) {
            throw new BusinessException("营业执照号已存在");
        }

        // 如果更新了营业执照，删除旧文件
        if (StringUtils.isNotBlank(existMerchant.getBusinessLicenseImage())
                && !existMerchant.getBusinessLicenseImage().equals(merchant.getBusinessLicenseImage())) {
            fileUtil.deleteFile(existMerchant.getBusinessLicenseImage());
        }


        this.updateById(merchant);
    }

    @Override
    public void auditMerchant(Long id, Integer status, Long auditUserId) {
        Merchant merchant = this.getById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        if (merchant.getStatus() != 0) {
            throw new BusinessException("商户已审核");
        }

        merchant.setStatus(status);
        merchant.setAuditUserId(auditUserId);
        merchant.setAuditTime(LocalDateTime.now());

        this.updateById(merchant);
    }

    @Override
    public Page<Merchant> pageMerchant(Integer current, Integer size, Long companyId,
                                       String merchantName, String merchantCode, Integer status) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(companyId != null, Merchant::getCompanyId, companyId)
                .like(StringUtils.isNotBlank(merchantName), Merchant::getMerchantName, merchantName)
                .eq(StringUtils.isNotBlank(merchantCode), Merchant::getMerchantCode, merchantCode)
                .eq(status != null, Merchant::getStatus, status)
                .orderByDesc(Merchant::getCreateTime);

        return this.page(new Page<>(current, size), wrapper);
    }

    @Override
    public Merchant getMerchantDetail(Long id) {
        Merchant merchant = this.getById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        return merchant;
    }

    @Override
    public String generateMerchantCode(Long companyId) {
        Company company = companyService.getById(companyId);
        if (company == null) {
            throw new BusinessException("所属公司不存在");
        }

        String code;
        do {
            // 使用公司编码作为前缀 + 4位随机数字
            code = company.getCompanyCode() + RandomUtil.randomNumbers(4);
        } while (this.count(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMerchantCode, code)) > 0);
        return code;
    }

    @Override
    public void createMerchantAdmin(Long merchantId, MerchantUser merchantUser) {
        // 验证商户是否存在
        Merchant merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        // 验证商户状态
        if (merchant.getStatus() != 1) {
            throw new BusinessException("商户未审核或已禁用");
        }

        // 验证用户名是否存在
        if (merchantUserMapper.selectCount(new LambdaQueryWrapper<MerchantUser>()
                .eq(MerchantUser::getUsername, merchantUser.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 设置商户ID
        merchantUser.setMerchantId(merchantId);
        // 加密密码
        merchantUser.setPassword(merchantUser.getPassword());
        // 设置状态
        merchantUser.setStatus(1);

        merchantUserMapper.insert(merchantUser);
    }

    @Override
    public Page<MerchantUser> pageMerchantUser(Long merchantId, Integer current, Integer size) {
        // 验证商户是否存在
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        Page<MerchantUser> page = new Page<>(current, size);
        LambdaQueryWrapper<MerchantUser> wrapper = new LambdaQueryWrapper<MerchantUser>()
                .eq(MerchantUser::getMerchantId, merchantId)
                .orderByDesc(MerchantUser::getCreateTime);

        return merchantUserMapper.selectPage(page, wrapper);
    }

    @Override
    public void updateMerchantUserStatus(Long userId, Integer status) {
        MerchantUser user = merchantUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        merchantUserMapper.updateById(user);
    }
}
