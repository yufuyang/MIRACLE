package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.admin.entity.Company;
import com.example.miracle.modules.admin.entity.CompanyUser;
import com.example.miracle.modules.admin.mapper.CompanyUserMapper;
import com.example.miracle.modules.admin.service.CompanyService;
import com.example.miracle.modules.company.dto.CompanyLoginDTO;
import com.example.miracle.modules.company.dto.CompanyUserDTO;
import com.example.miracle.modules.company.service.CompanyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl extends ServiceImpl<CompanyUserMapper, CompanyUser> implements CompanyUserService {

    private final CompanyService companyService;
    private final JwtUtil jwtUtil;

    @Override
    public CompanyUserDTO login(CompanyLoginDTO loginDTO) {
        // 查询用户
        CompanyUser user = this.getOne(new LambdaQueryWrapper<CompanyUser>()
                .eq(CompanyUser::getUsername, loginDTO.getUsername()));

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!Objects.equals(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 查询公司信息
        Company company = companyService.getById(user.getCompanyId());
        if (company == null || company.getStatus() != 1) {
            throw new BusinessException("公司已被禁用");
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getUsername(), "COMPANY");

        // 返回登录信息
        CompanyUserDTO companyUserDTO = new CompanyUserDTO();
        companyUserDTO.setUserId(user.getId());
        companyUserDTO.setUsername(user.getUsername());
        companyUserDTO.setRealName(user.getRealName());
        companyUserDTO.setToken(token);
        companyUserDTO.setCompanyId(company.getId());
        companyUserDTO.setCompanyName(company.getCompanyName());

        return companyUserDTO;
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        CompanyUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!Objects.equals(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        user.setPassword(newPassword);
        this.updateById(user);
    }

    @Override
    public CompanyUserDTO getCurrentUser(Long userId) {
        CompanyUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Company company = companyService.getById(user.getCompanyId());

        CompanyUserDTO companyUserDTO = new CompanyUserDTO();
        companyUserDTO.setUserId(user.getId());
        companyUserDTO.setUsername(user.getUsername());
        companyUserDTO.setRealName(user.getRealName());
        companyUserDTO.setCompanyId(company.getId());
        companyUserDTO.setCompanyName(company.getCompanyName());

        return companyUserDTO;
    }
}