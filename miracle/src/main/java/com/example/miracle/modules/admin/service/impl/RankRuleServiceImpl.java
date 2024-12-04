package com.example.miracle.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.admin.entity.RankRule;
import com.example.miracle.modules.admin.mapper.RankRuleMapper;
import com.example.miracle.modules.admin.service.RankRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class RankRuleServiceImpl extends ServiceImpl<RankRuleMapper, RankRule> implements RankRuleService {

    @Override
    public void createRule(RankRule rankRule) {
        // 验证时间范围
        validateTimeRange(rankRule);

        // 验证规则名称是否存在
        if (this.count(new LambdaQueryWrapper<RankRule>()
                .eq(RankRule::getRuleName, rankRule.getRuleName())) > 0) {
            throw new BusinessException("规则名称已存在");
        }

        // 设置状态
        rankRule.setStatus(1);

        this.save(rankRule);
    }

    @Override
    public void updateRule(RankRule rankRule) {
        // 验证规则是否存在
        RankRule existRule = this.getById(rankRule.getId());
        if (existRule == null) {
            throw new BusinessException("规则不存在");
        }

        // 验证时间范围
        validateTimeRange(rankRule);

        // 验证规则名称是否存在
        if (!existRule.getRuleName().equals(rankRule.getRuleName()) &&
                this.count(new LambdaQueryWrapper<RankRule>()
                        .eq(RankRule::getRuleName, rankRule.getRuleName())) > 0) {
            throw new BusinessException("规则名称已存在");
        }

        this.updateById(rankRule);
    }

    @Override
    public void deleteRule(Long id) {
        RankRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException("规则不存在");
        }

        this.removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RankRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException("规则不存在");
        }

        rule.setStatus(status);
        this.updateById(rule);
    }

    @Override
    public Page<RankRule> pageRule(Integer current, Integer size, String ruleName,
                                   Integer ruleType, Integer timeType, Integer status) {
        LambdaQueryWrapper<RankRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(ruleName), RankRule::getRuleName, ruleName)
                .eq(ruleType != null, RankRule::getRuleType, ruleType)
                .eq(timeType != null, RankRule::getTimeType, timeType)
                .eq(status != null, RankRule::getStatus, status)
                .orderByDesc(RankRule::getCreateTime);

        return this.page(new Page<>(current, size), wrapper);
    }

    /**
     * 验证时间范围
     */
    private void validateTimeRange(RankRule rankRule) {
        if (rankRule.getStartTime() == null || rankRule.getEndTime() == null) {
            throw new BusinessException("开始时间和结束时间不能为空");
        }

        if (rankRule.getStartTime().isAfter(rankRule.getEndTime())) {
            throw new BusinessException("开始时间不能大于结束时间");
        }
    }
}
