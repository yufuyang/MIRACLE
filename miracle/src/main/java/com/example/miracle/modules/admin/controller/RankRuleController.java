package com.example.miracle.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
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
    public ResultDTO<Page<RankRule>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false) Integer timeType,
            @RequestParam(required = false) Integer status) {
        return ResultDTO.ok(rankRuleService.pageRule(current, size, ruleName, ruleType, timeType, status));
    }

    @PostMapping
    public ResultDTO<?> create(@RequestBody RankRule rankRule) {
        rankRuleService.createRule(rankRule);
        return ResultDTO.ok();
    }

    @PutMapping
    public ResultDTO<?> update(@RequestBody RankRule rankRule) {
        rankRuleService.updateRule(rankRule);
        return ResultDTO.ok();
    }

    @DeleteMapping("/{id}")
    public ResultDTO<?> delete(@PathVariable Long id) {
        rankRuleService.deleteRule(id);
        return ResultDTO.ok();
    }

    @PutMapping("/status/{id}")
    public ResultDTO<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        rankRuleService.updateStatus(id, status);
        return ResultDTO.ok();
    }
}