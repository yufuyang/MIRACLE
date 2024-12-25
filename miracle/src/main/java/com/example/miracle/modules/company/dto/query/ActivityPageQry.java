package com.example.miracle.modules.company.dto.query;

import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityPageQry  extends PageQuery {
    
    /**
     * 活动状态:0-未开始,1-进行中,2-已结束
     */
    private Integer status;
    
    /**
     * 排序字段:viewCount-浏览量,registerCount-报名人数
     */
    private String orderBy;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
    /**
     * 企业ID
     */
    private Long companyId;

    private Long merchantId;
} 