package com.example.miracle.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.Result;
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

    @GetMapping("/page")
    public Result<Page<Company>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String companyCode,
            @RequestParam(required = false) Integer status) {
        return Result.ok(companyService.pageCompany(current, size, companyName, companyCode, status));
    }

    @GetMapping("/{id}")
    public Result<Company> detail(@PathVariable Long id) {
        return Result.ok(companyService.getCompanyDetail(id));
    }

    @PostMapping
    public Result<?> create(@RequestBody Company company) {
        companyService.createCompany(company);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody Company company) {
        companyService.updateCompany(company);
        return Result.ok();
    }

    @PutMapping("/audit/{id}")
    public Result<?> audit(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam Long auditUserId) {
        companyService.auditCompany(id, status, auditUserId);
        return Result.ok();
    }


    @PostMapping("/{companyId}/admin")
    public Result<?> createAdmin(@PathVariable Long companyId, @RequestBody CompanyUser companyUser) {
        companyService.createCompanyAdmin(companyId, companyUser);
        return Result.ok();
    }
}