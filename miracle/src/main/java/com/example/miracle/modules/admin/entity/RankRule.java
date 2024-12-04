package com.example.miracle.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("rank_rule")
@EqualsAndHashCode(callSuper = true)
public class RankRule extends BaseEntity {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型：1-销售排行 2-商户排行 3-商品排行
     */
    private Integer ruleType;

    /**
     * 时间类型：1-日榜 2-周榜 3-月榜 4-年榜
     */
    private Integer timeType;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}