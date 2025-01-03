package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.ProductRankDTO;
import com.example.miracle.modules.company.dto.ProductStatsDTO;
import com.example.miracle.modules.company.dto.ProductTrendDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductStatsQuery;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.website.dto.ProductDTO;

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

    /**
     * 查询热门产品
     *
     * @param limit 数量限制
     * @return 热门产品列表
     */
    List<ProductDTO> selectHotProducts(int limit);


    /**
     * 获取产品统计概览
     */
    SingleResponse<ProductStatsDTO> getOverview(Long companyId);

    /**
     * 获取产品趋势数据
     */
    SingleResponse<ProductTrendDTO> getTrend(Long productId, String timeRange, String dataType);

    /**
     * 获取热门产品排行
     */
    MultiResponse<ProductRankDTO> getHotProducts(Long companyId, String timeRange, String rankType);

    SingleResponse<CompanyProductStats> get(Long productId);

    /**
     * 增加产品浏览量
     */
    void incrementViewCount(Long companyId, Long productId);

    /**
     * 增加产品意向数
     */
    void incrementIntentCount(Long companyId, Long productId);

    /**
     * 减少产品意向数
     */
    void decrementIntentCount(Long productId);

}
