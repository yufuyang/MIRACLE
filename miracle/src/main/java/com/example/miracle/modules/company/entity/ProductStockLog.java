package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("product_stock_log")
@EqualsAndHashCode(callSuper = true)
public class ProductStockLog extends BaseEntity {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 变动数量
     */
    private Integer quantity;

    /**
     * 变动类型
     */
    private String type;

    /**
     * 变动前库存
     */
    private Integer beforeStock;

    /**
     * 变动后库存
     */
    private Integer afterStock;
}