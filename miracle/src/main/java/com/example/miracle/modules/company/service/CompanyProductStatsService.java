package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.query.CompanyProductStatsQuery;
import com.example.miracle.modules.company.entity.CompanyProductStats;

import java.util.List;
import java.util.Map;

/**
 * 产品统计服务接口
 */
public interface CompanyProductStatsService extends IService<CompanyProductStats> {

    /**
     * 分页查询产品统计
     */
    MultiResponse<CompanyProductStats> pageQuery(CompanyProductStatsQuery query);

    /**
     * 查询前N个优质企业
     *
     * @param limit 数量限制
     * @return 企业统计数据列表
     */
    List<Map<String, Object>> selectTopCompanies(int limit);
}
