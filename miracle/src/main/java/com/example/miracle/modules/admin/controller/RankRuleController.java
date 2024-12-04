package com.example.miracle.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.admin.entity.RankRule;
import com.example.miracle.modules.admin.service.RankRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rank/rule")
@RequiredArgsConstructor
public class RankRuleController {

    private final RankRuleService rankRuleService;

    @GetMapping("/page")
    public Result<Page<RankRule>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false) Integer timeType,
            @RequestParam(required = false) Integer status) {
        return Result.ok(rankRuleService.pageRule(current, size, ruleName, ruleType, timeType, status));
    }

    @PostMapping
    public Result<?> create(@RequestBody RankRule rankRule) {
        rankRuleService.createRule(rankRule);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody RankRule rankRule) {
        rankRuleService.updateRule(rankRule);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        rankRuleService.deleteRule(id);
        return Result.ok();
    }

    @PutMapping("/status/{id}")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        rankRuleService.updateStatus(id, status);
        return Result.ok();
    }
}