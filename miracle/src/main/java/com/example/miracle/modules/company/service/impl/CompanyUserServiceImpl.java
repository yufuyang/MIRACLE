package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.constant.CommonConstant;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.company.dto.CompanyUserLoginDTO;
import com.example.miracle.modules.company.dto.query.CompanyUserPageQuery;
import com.example.miracle.modules.company.dto.cmd.CompanyUserLoginCmd;
import com.example.miracle.modules.company.entity.CompanyUser;
import com.example.miracle.modules.company.mapper.CompanyUserMapper;
import com.example.miracle.modules.company.service.CompanyUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 公司用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl extends ServiceImpl<CompanyUserMapper, CompanyUser> implements CompanyUserService {

    private final JwtUtil jwtUtil;

    @Override
    public SingleResponse<CompanyUserLoginDTO> login(CompanyUserLoginCmd companyUserLoginCmd) {
        // 查询用户
        CompanyUser user = this.getOne(new LambdaQueryWrapper<CompanyUser>().eq(CompanyUser::getUsername, companyUserLoginCmd.getUsername()));

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!Objects.equals(companyUserLoginCmd.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 验证状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成token
        String token = jwtUtil.generateToken(user);

        // 构建返回对象
        CompanyUserLoginDTO loginDTO = new CompanyUserLoginDTO();
        loginDTO.setUsername(user.getUsername());
        loginDTO.setToken(token);
        loginDTO.setRole(CommonConstant.PLATFORM_ROLE);
        return SingleResponse.of(loginDTO);
    }

    @Override
    public SingleResponse<Void> logout(String token) {
        jwtUtil.invalidateToken(token);
        return SingleResponse.buildSuccess();
    }

    @Override
    public MultiResponse<CompanyUser> listByCompanyId(CompanyUserPageQuery companyUserPageQuery) {

        LambdaQueryWrapper<CompanyUser> wrapper = new LambdaQueryWrapper<CompanyUser>()
                .eq(CompanyUser::getCompanyId, companyUserPageQuery.getCompanyId())
                .like(StringUtils.isNotBlank(companyUserPageQuery.getUsername()), CompanyUser::getUsername, companyUserPageQuery.getUsername())
                .like(StringUtils.isNotBlank(companyUserPageQuery.getRealName()), CompanyUser::getRealName, companyUserPageQuery.getRealName())
                .eq(companyUserPageQuery.getStatus() != null, CompanyUser::getStatus, companyUserPageQuery.getStatus())
                .orderByDesc(CompanyUser::getCreateTime);


        // 执行分页查询
        Page<CompanyUser> page = this.page(new Page<>(companyUserPageQuery.getPageNum(), companyUserPageQuery.getPageSize()), wrapper);

        // 返回结果
        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }
} 