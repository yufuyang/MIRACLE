package com.example.miracle.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("notice")
@EqualsAndHashCode(callSuper = true)
public class Notice extends BaseEntity {

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型：1-系统公告 2-活动公告
     */
    private Integer type;

    /**
     * 状态：0-未发布 1-已发布 2-已下线
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建人ID
     */
    private Long createBy;
}