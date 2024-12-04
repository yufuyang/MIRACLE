package com.example.miracle.modules.admin.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.modules.admin.entity.RankRule;
import com.example.miracle.modules.admin.service.RankDataService;
import com.example.miracle.modules.admin.service.RankRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankDataTask {

    private final RankRuleService rankRuleService;
    private final RankDataService rankDataService;

    /**
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyRankData() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 获取所有启用的规则
        List<RankRule> rules = rankRuleService.list(new LambdaQueryWrapper<RankRule>()
                .eq(RankRule::getStatus, 1));

        // 生成排行数据
        for (RankRule rule : rules) {
            try {
                rankDataService.generateRankData(rule.getId(), yesterday);
                log.info("生成排行数据成功：ruleId={}, date={}", rule.getId(), yesterday);
            } catch (Exception e) {
                log.error("生成排行数据失败：ruleId={}, date={}", rule.getId(), yesterday, e);
            }
        }
    }
}