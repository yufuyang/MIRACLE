package com.example.miracle.common.controller;

import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.company.entity.CompanyUser;
import com.example.miracle.modules.company.service.CompanyUserService;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import com.example.miracle.modules.merchant.service.MerchantUserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class BaseController {

    private final HttpServletRequest request;

    private final JwtUtil jwtUtil;

    private final CompanyUserService companyUserService;

    private final MerchantUserService merchantUserService;

    public Long getCompanyId() {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserId(token);
        if (Objects.isNull(userId)) {
            throw new BusinessException("token无效");
        }
        CompanyUser companyUser = companyUserService.getById(userId);
        if (Objects.isNull(companyUser)) {
            throw new BusinessException("没有该用户");
        }
        return companyUser.getCompanyId();
    }


    public Long getMerchantId() {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserId(token);
        if (Objects.isNull(userId)) {
            throw new BusinessException("token无效");
        }
        MerchantUser merchantUser = merchantUserService.getById(userId);
        if (Objects.isNull(merchantUser)) {
            throw new BusinessException("没有该用户");
        }
        return merchantUser.getMerchantId();
    }

    public Long getUserId() {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserId(token);
        if (Objects.isNull(userId)) {
            throw new BusinessException("token无效");
        }
        return userId;
    }


}
