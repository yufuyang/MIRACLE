package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@TableName("t_order_evaluation")
@EqualsAndHashCode(callSuper = true)
public class OrderEvaluation extends BaseEntity {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 评分(1-5)
     */
    private Integer score;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片，多个图片用逗号分隔
     */
    private String images;
    
    /**
     * 是否匿名
     */
    private Boolean anonymous;
    
    /**
     * 商家回复
     */
    private String reply;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
} 