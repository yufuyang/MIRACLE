package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.query.CompanyProductStatsQuery;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.mapper.CompanyProductStatsMapper;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 产品统计服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyProductStatsServiceImpl extends ServiceImpl<CompanyProductStatsMapper, CompanyProductStats> implements CompanyProductStatsService {

    @Override
    public MultiResponse<CompanyProductStats> pageQuery(CompanyProductStatsQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<CompanyProductStats> wrapper = new LambdaQueryWrapper<CompanyProductStats>()
                .eq(query.getCompanyId() != null, CompanyProductStats::getCompanyId, query.getCompanyId())
                .eq(query.getProductId() != null, CompanyProductStats::getProductId, query.getProductId());

        // 添加排序
        if (query.getOrderField() != null) {

            switch (query.getOrderField()) {
                case "viewCount":
                    wrapper.orderBy(true, query.getAsc(), CompanyProductStats::getViewCount);
                    break;
                case "intentionCount":
                    wrapper.orderBy(true, query.getAsc(), CompanyProductStats::getIntentionCount);
                    break;
                default:
                    wrapper.orderByDesc(CompanyProductStats::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(CompanyProductStats::getCreateTime);
        }

        // 执行分页查询
        Page<CompanyProductStats> page = this.page(
                new Page<>(query.getPageNum(), query.getPageSize()),
                wrapper
        );

        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }
}
