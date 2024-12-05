package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.dto.RefundItemDTO;
import com.example.miracle.modules.company.entity.ReturnItem;

import java.util.List;

public interface ReturnItemService extends IService<ReturnItem> {

    /**
     * 创建退货商品
     */
    void createReturnItems(Long returnId, List<RefundItemDTO> items);

    /**
     * 查询退货商品列表
     */
    List<ReturnItem> getReturnItems(Long returnId);
}