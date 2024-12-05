package com.example.miracle.modules.company.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderEvaluationDTO {
    
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
     * 评价图片
     */
    private List<String> images;
    
    /**
     * 是否匿名
     */
    private Boolean anonymous;
} 