package com.example.miracle.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.admin.entity.Company;
import com.example.miracle.modules.admin.entity.CompanyUser;
import com.example.miracle.modules.admin.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final JwtUtil jwtUtil;

    @GetMapping("/page")
    public ResultDTO<Page<Company>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String companyCode,
            @RequestParam(required = false) Integer status) {
        return ResultDTO.ok(companyService.pageCompany(current, size, companyName, companyCode, status));
    }

    @GetMapping("/{id}")
    public ResultDTO<Company> detail(@PathVariable Long id) {
        return ResultDTO.ok(companyService.getCompanyDetail(id));
    }

    @PostMapping
    public ResultDTO<?> create(@RequestBody Company company) {
        companyService.createCompany(company);
        return ResultDTO.ok();
    }

    @PutMapping
    public ResultDTO<?> update(@RequestBody Company company) {
        companyService.updateCompany(company);
        return ResultDTO.ok();
    }

    @PutMapping("/audit/{id}")
    public ResultDTO<?> audit(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestHeader("Authorization") String token) {
        Long operator = jwtUtil.getUserId(token.replace("Bearer ", ""));
        companyService.auditCompany(id, status, operator);
        return ResultDTO.ok();
    }


    @PostMapping("/{companyId}/admin")
    public ResultDTO<?> createAdmin(@PathVariable Long companyId, @RequestBody CompanyUser companyUser) {
        companyService.createCompanyAdmin(companyId, companyUser);
        return ResultDTO.ok();
    }
}