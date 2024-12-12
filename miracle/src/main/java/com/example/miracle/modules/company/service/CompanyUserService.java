package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.CompanyUserLoginDTO;
import com.example.miracle.modules.company.dto.query.CompanyUserPageQuery;
import com.example.miracle.modules.company.dto.cmd.CompanyUserLoginCmd;
import com.example.miracle.modules.company.entity.CompanyUser;

/**
 * 公司用户服务接口
 */
public interface CompanyUserService extends IService<CompanyUser> {

    /**
     * 用户登录
     */
    SingleResponse<CompanyUserLoginDTO> login(CompanyUserLoginCmd companyUserLoginCmd);

    /**
     * 用户登出
     */
    SingleResponse<Void> logout(String token);

    /**
     * 根据公司ID查询用户列表
     */
    MultiResponse<CompanyUser> listByCompanyId(CompanyUserPageQuery companyUserPageQuery);
}