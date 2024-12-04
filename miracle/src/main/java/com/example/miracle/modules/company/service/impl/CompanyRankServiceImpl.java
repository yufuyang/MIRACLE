package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.admin.dto.RankDataDTO;
import com.example.miracle.modules.admin.entity.RankData;
import com.example.miracle.modules.admin.entity.RankRule;
import com.example.miracle.modules.admin.service.RankDataService;
import com.example.miracle.modules.admin.service.RankRuleService;
import com.example.miracle.modules.company.entity.Merchant;
import com.example.miracle.modules.company.service.CompanyRankService;
import com.example.miracle.modules.company.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyRankServiceImpl implements CompanyRankService {

    private final RankRuleService rankRuleService;
    private final RankDataService rankDataService;
    private final MerchantService merchantService;

    @Override
    public List<RankRule> listRules(Integer ruleType, Integer timeType) {
        LambdaQueryWrapper<RankRule> wrapper = new LambdaQueryWrapper<RankRule>()
                .eq(RankRule::getStatus, 1)
                .eq(ruleType != null, RankRule::getRuleType, ruleType)
                .eq(timeType != null, RankRule::getTimeType, timeType)
                .orderByDesc(RankRule::getCreateTime);
        return rankRuleService.list(wrapper);
    }

    @Override
    public List<RankDataDTO> getLatestRank(Long companyId, Long ruleId, Integer limit) {
        // 验证规则
        RankRule rule = validateRule(ruleId);

        // 获取公司的商户ID列表
        List<Long> merchantIds = getMerchantIds(companyId);

        // 获取最新排行数据
        List<RankDataDTO> rankList = rankDataService.getLatestRank(ruleId, limit);

        // 过滤出公司的商户数据
        return filterCompanyData(rankList, merchantIds);
    }

    @Override
    public List<RankDataDTO> getRankByDate(Long companyId, Long ruleId, LocalDate date, Integer limit) {
        // 验证规则
        RankRule rule = validateRule(ruleId);

        // 获取公司的商户ID列表
        List<Long> merchantIds = getMerchantIds(companyId);

        // 获取指定日期的排行数据
        List<RankDataDTO> rankList = rankDataService.getRankByDate(ruleId, date, limit);

        // 过滤出公司的商户数据
        return filterCompanyData(rankList, merchantIds);
    }

    @Override
    public List<RankDataDTO> getRankTrend(Long companyId, Long ruleId, Long targetId, LocalDate startDate, LocalDate endDate) {
        // 验证规则
        RankRule rule = validateRule(ruleId);

        // 验证目标是否属于当前公司
        Merchant merchant = merchantService.getById(targetId);
        if (merchant == null || !merchant.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权查看该数据");
        }

        // 获取排名趋势
        return rankDataService.getRankTrend(ruleId, targetId, startDate, endDate);
    }

    /**
     * 验证规则
     */
    private RankRule validateRule(Long ruleId) {
        RankRule rule = rankRuleService.getById(ruleId);
        if (rule == null || rule.getStatus() != 1) {
            throw new BusinessException("排行规则不存在或已禁用");
        }
        return rule;
    }

    /**
     * 获取公司的商户ID列表
     */
    private List<Long> getMerchantIds(Long companyId) {
        return merchantService.list(new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getCompanyId, companyId)
                        .select(Merchant::getId))
                .stream()
                .map(Merchant::getId)
                .collect(Collectors.toList());
    }

    /**
     * 过滤公司数据
     */
    private List<RankDataDTO> filterCompanyData(List<RankDataDTO> rankList, List<Long> merchantIds) {
        return rankList.stream()
                .filter(rank -> merchantIds.contains(rank.getTargetId()))
                .collect(Collectors.toList());
    }
}