package com.example.miracle.modules.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.platform.dto.query.CompanyPageQuery;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.website.dto.CompanyDTO;

/**
 * 公司信息服务接口
 */
public interface CompanyService extends IService<Company> {

    /**
     * 分页查询公司列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    MultiResponse<Company> pageQuery(CompanyPageQuery companyPageQuery);

    /**
     * 分页查询公司DTO列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    MultiResponse<CompanyDTO> pageQueryDTO(CompanyPageQuery query);
}
