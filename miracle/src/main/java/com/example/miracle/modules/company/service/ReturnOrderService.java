package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.dto.RefundItemDTO;
import com.example.miracle.modules.company.dto.ReturnDetailDTO;
import com.example.miracle.modules.company.dto.ReturnLogisticsDTO;
import com.example.miracle.modules.company.dto.ReturnReceiveDTO;
import com.example.miracle.modules.company.entity.RefundOrder;
import com.example.miracle.modules.company.entity.ReturnOrder;

import java.util.List;

public interface ReturnOrderService extends IService<ReturnOrder> {

    /**
     * 创建退货单
     */
    ReturnOrder createReturnOrder(RefundOrder refundOrder, List<RefundItemDTO> items);

    /**
     * 更新退货地址
     * @param refundNo 退款单号
     * @param returnAddress 退货地址
     */
    void updateReturnAddress(String refundNo, String returnAddress);

    /**
     * 提交退货物流信息
     */
    void submitLogistics(ReturnLogisticsDTO dto);

    /**
     * 确认收货
     */
    void confirmReceive(ReturnReceiveDTO dto);

    /**
     * 获取已退货数量
     */
    int getRefundedQuantity(Long orderItemId);

    /**
     * 查询退货单详情
     */
    ReturnDetailDTO getReturnDetail(String returnNo);

    /**
     * 根据退款单号查询退货单
     */
    ReturnOrder getByRefundNo(String refundNo);

    /**
     * 获取退货商品列表
     */
    List<RefundItemDTO> getRefundItems(String refundNo);

    /**
     * 取消退货
     */
    void cancelReturn(String refundNo);
}