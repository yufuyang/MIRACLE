package com.example.miracle.modules.company.service;

import com.example.miracle.modules.admin.dto.RankDataDTO;
import com.example.miracle.modules.admin.entity.RankRule;

import java.time.LocalDate;
import java.util.List;

public interface CompanyRankService {

    /**
     * 获取排行规则列表
     */
    List<RankRule> listRules(Integer ruleType, Integer timeType);

    /**
     * 获取最新排行榜
     */
    List<RankDataDTO> getLatestRank(Long companyId, Long ruleId, Integer limit);

    /**
     * 获取指定日期的排行榜
     */
    List<RankDataDTO> getRankByDate(Long companyId, Long ruleId, LocalDate date, Integer limit);

    /**
     * 获取排名趋势
     */
    List<RankDataDTO> getRankTrend(Long companyId, Long ruleId, Long targetId, LocalDate startDate, LocalDate endDate);
}