package com.example.miracle.modules.company.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.CompanyUserLoginDTO;
import com.example.miracle.modules.company.dto.query.CompanyUserPageQuery;
import com.example.miracle.modules.company.dto.cmd.CompanyUserLoginCmd;
import com.example.miracle.modules.company.entity.CompanyUser;
import com.example.miracle.modules.company.service.CompanyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/company/user")
@RequiredArgsConstructor
public class CompanyUserController  {

    private final CompanyUserService companyUserService;

    private final HttpServletRequest request;

    private final BaseController baseController;


    /**
     * 用户登录
     */
    @PostMapping("/login")
    SingleResponse<CompanyUserLoginDTO> login(@RequestBody CompanyUserLoginCmd companyUserLoginCmd) {
        return companyUserService.login(companyUserLoginCmd);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    SingleResponse<Void> logout() {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return companyUserService.logout(token);
    }


    @GetMapping
    public SingleResponse<CompanyUser> getById() {
        Long id = baseController.getUserId();
        return SingleResponse.of(companyUserService.getById(id));
    }


    @PutMapping
    public SingleResponse<CompanyUser> update(@RequestBody CompanyUser companyUser) {
        companyUserService.updateById(companyUser);
        return SingleResponse.of(companyUser);
    }


    /**
     * 根据公司ID查询用户列表
     */
    @PostMapping("page")
    public MultiResponse<CompanyUser> listByCompanyId(@RequestBody CompanyUserPageQuery companyUserPageQuery) {
        companyUserPageQuery.setCompanyId(baseController.getCompanyId());
        return companyUserService.listByCompanyId(companyUserPageQuery);
    }
}
