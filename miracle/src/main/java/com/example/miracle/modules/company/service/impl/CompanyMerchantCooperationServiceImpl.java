package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.CompanyMerchantCooperationDTO;
import com.example.miracle.modules.company.dto.query.CompanyMerchantCooperationPageQry;
import com.example.miracle.modules.company.entity.CompanyMerchantCooperation;
import com.example.miracle.modules.company.mapper.CompanyMerchantCooperationMapper;
import com.example.miracle.modules.company.service.CompanyMerchantCooperationService;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.entity.Merchant;
import com.example.miracle.modules.platform.mapper.CompanyMapper;
import com.example.miracle.modules.platform.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyMerchantCooperationServiceImpl extends ServiceImpl<CompanyMerchantCooperationMapper, CompanyMerchantCooperation> implements CompanyMerchantCooperationService {

    private final MerchantMapper merchantMapper;

    private final CompanyMapper companyMapper;

    @Override
    public MultiResponse<CompanyMerchantCooperationDTO> page(CompanyMerchantCooperationPageQry companyMerchantCooperationPageQry) {

        LambdaQueryWrapper<CompanyMerchantCooperation> wrapper = new LambdaQueryWrapper<CompanyMerchantCooperation>()
                .eq(Objects.nonNull(companyMerchantCooperationPageQry.getCompanyId()), CompanyMerchantCooperation::getCompanyId, companyMerchantCooperationPageQry.getCompanyId())
                .eq(Objects.nonNull(companyMerchantCooperationPageQry.getMerchantId()), CompanyMerchantCooperation::getMerchantId, companyMerchantCooperationPageQry.getMerchantId())
                .orderByDesc(CompanyMerchantCooperation::getCreateTime);

        if (StringUtils.hasText(companyMerchantCooperationPageQry.getMerchantName())) {

            LambdaQueryWrapper<Merchant> merchantWrapper = new LambdaQueryWrapper<Merchant>()
                    .like(Merchant::getMerchantName, companyMerchantCooperationPageQry.getMerchantName());

            List<Merchant> merchantList = merchantMapper.selectList(merchantWrapper);
            if (CollectionUtils.isEmpty(merchantList)) {
                return MultiResponse.buildSuccess();
            }else {
                wrapper.in(CompanyMerchantCooperation::getMerchantId, merchantList.stream().map(Merchant::getId).collect(Collectors.toList()));
            }
        }

        if (StringUtils.hasText(companyMerchantCooperationPageQry.getCompanyName())) {

            LambdaQueryWrapper<Company> companyWrapper = new LambdaQueryWrapper<Company>()
                    .like(Company::getCompanyName, companyMerchantCooperationPageQry.getCompanyName());

            List<Company> companyList = companyMapper.selectList(companyWrapper);
            if (CollectionUtils.isEmpty(companyList)) {
                return MultiResponse.buildSuccess();
            }else {
                wrapper.in(CompanyMerchantCooperation::getCompanyId, companyList.stream().map(Company::getId).collect(Collectors.toList()));
            }

        }

        Page<CompanyMerchantCooperation> page = this.page(new Page<>(companyMerchantCooperationPageQry.getPageNum(), companyMerchantCooperationPageQry.getPageSize()), wrapper);

        List<CompanyMerchantCooperationDTO> list = page.getRecords().stream().map(item -> {
            CompanyMerchantCooperationDTO dto = new CompanyMerchantCooperationDTO();
            dto.setId(item.getId());
            dto.setMerchantId(item.getMerchantId());
            dto.setCompanyId(item.getCompanyId());
            dto.setMerchantName(merchantMapper.selectById(item.getMerchantId()).getMerchantName());
            dto.setStatus(item.getStatus());
            dto.setCreateTime(item.getCreateTime());
            return dto;
        }).collect(Collectors.toList());

        return MultiResponse.of(list, (int) page.getTotal());
    }
}
