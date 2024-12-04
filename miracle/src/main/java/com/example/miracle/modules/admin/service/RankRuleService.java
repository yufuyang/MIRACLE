package com.example.miracle.modules.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.admin.entity.RankRule;

public interface RankRuleService extends IService<RankRule> {

    /**
     * 创建排行规则
     */
    void createRule(RankRule rankRule);

    /**
     * 更新排行规则
     */
    void updateRule(RankRule rankRule);

    /**
     * 删除排行规则
     */
    void deleteRule(Long id);

    /**
     * 更新规则状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 分页查询排行规则
     */
    Page<RankRule> pageRule(Integer current, Integer size, String ruleName, Integer ruleType, Integer timeType, Integer status);
}