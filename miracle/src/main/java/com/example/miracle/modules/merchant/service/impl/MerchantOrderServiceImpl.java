package com.example.miracle.modules.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.ProductMaterial;
import com.example.miracle.modules.company.mapper.CompanyProductMapper;
import com.example.miracle.modules.company.mapper.ProductMaterialMapper;
import com.example.miracle.modules.merchant.dto.MerchantOrderDTO;
import com.example.miracle.modules.merchant.dto.MerchantOrderInfoDTO;
import com.example.miracle.modules.merchant.dto.OrderMaterialDTO;
import com.example.miracle.modules.merchant.dto.cmd.MerchantOrderCreateCmd;
import com.example.miracle.modules.merchant.dto.cmd.OrderMaterialCreateCmd;
import com.example.miracle.modules.merchant.dto.query.MerchantOrderPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantOrder;
import com.example.miracle.modules.merchant.entity.OrderMaterial;
import com.example.miracle.modules.merchant.mapper.MerchantOrderMapper;
import com.example.miracle.modules.merchant.mapper.OrderMaterialMapper;
import com.example.miracle.modules.merchant.service.MerchantOrderService;
import com.example.miracle.modules.platform.mapper.CompanyMapper;
import com.example.miracle.modules.platform.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantOrderServiceImpl extends ServiceImpl<MerchantOrderMapper, MerchantOrder> implements MerchantOrderService {

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private CompanyProductMapper companyProductMapper;

    @Resource
    private ProductMaterialMapper productMaterialMapper;

    @Resource
    private OrderMaterialMapper orderMaterialMapper;

    @Override
    public MultiResponse<MerchantOrderDTO> pageQuery(MerchantOrderPageQuery query) {

        LambdaQueryWrapper<MerchantOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(query.getMerchantId()), MerchantOrder::getMerchantId, query.getMerchantId());
        queryWrapper.eq(Objects.nonNull(query.getCompanyId()), MerchantOrder::getCompanyId, query.getCompanyId());
        queryWrapper.eq(Objects.nonNull(query.getProductId()), MerchantOrder::getProductId, query.getProductId());
        queryWrapper.eq(Objects.nonNull(query.getStatus()), MerchantOrder::getStatus, query.getStatus());
        queryWrapper.like(Objects.nonNull(query.getReceiverName()), MerchantOrder::getReceiverName, query.getReceiverName());
        queryWrapper.like(Objects.nonNull(query.getReceiverPhone()), MerchantOrder::getReceiverPhone, query.getReceiverPhone());

        Page<MerchantOrder> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            return MultiResponse.buildSuccess();
        }

        List<MerchantOrderDTO> merchantOrderDTOList = page.getRecords().stream().map(merchantOrder -> {

            MerchantOrderDTO merchantOrderDTO = new MerchantOrderDTO();
            BeanUtils.copyProperties(merchantOrder, merchantOrderDTO);

            merchantOrderDTO.setMerchantName(merchantMapper.selectById(merchantOrder.getMerchantId()).getMerchantName());
            merchantOrderDTO.setCompanyName(companyMapper.selectById(merchantOrder.getCompanyId()).getCompanyName());
            merchantOrderDTO.setProductName(companyProductMapper.selectById(merchantOrder.getProductId()).getProductName());

            return merchantOrderDTO;
        }).collect(Collectors.toList());

        return MultiResponse.of(merchantOrderDTOList, (int) page.getTotal());
    }

    @Override
    public SingleResponse<MerchantOrderInfoDTO> getOrderMaterial(Long id) {

        MerchantOrder merchantOrder = this.getById(id);

        if (merchantOrder == null) {
            return SingleResponse.buildFailure("订单不存在");
        }

        MerchantOrderInfoDTO merchantOrderInfoDTO = new MerchantOrderInfoDTO();
        BeanUtils.copyProperties(merchantOrder, merchantOrderInfoDTO);

        merchantOrderInfoDTO.setMerchantName(merchantMapper.selectById(merchantOrder.getMerchantId()).getMerchantName());
        merchantOrderInfoDTO.setCompanyName(companyMapper.selectById(merchantOrder.getCompanyId()).getCompanyName());
        merchantOrderInfoDTO.setProductName(companyProductMapper.selectById(merchantOrder.getProductId()).getProductName());

        List<OrderMaterial> orderMaterialList = orderMaterialMapper.selectList(new LambdaQueryWrapper<OrderMaterial>().eq(OrderMaterial::getOrderId, id));

        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return SingleResponse.of(merchantOrderInfoDTO);
        }

        List<OrderMaterialDTO> orderMaterialDTOList = orderMaterialList.stream().map(orderMaterial -> {

            ProductMaterial productMaterial = productMaterialMapper.selectById(orderMaterial.getMaterialId());
            if (productMaterial == null) {
                return null;
            }

            OrderMaterialDTO orderMaterialDTO = new OrderMaterialDTO();
            BeanUtils.copyProperties(orderMaterial, orderMaterialDTO);
            orderMaterialDTO.setMaterialName(productMaterial.getName());
            orderMaterialDTO.setImage(productMaterial.getImage());
            orderMaterialDTO.setUnit(productMaterial.getUnit());
            orderMaterialDTO.setSpecification(productMaterial.getSpecification()    );

            return orderMaterialDTO;
        }).collect(Collectors.toList());

        merchantOrderInfoDTO.setOrderMaterials(orderMaterialDTOList);

        return SingleResponse.of(merchantOrderInfoDTO);
    }

    @Override
    public SingleResponse create(MerchantOrderCreateCmd merchantOrderCreateCmd) {

        MerchantOrder merchantOrder = new MerchantOrder();

        BeanUtils.copyProperties(merchantOrderCreateCmd, merchantOrder);

        merchantOrder.setStatus(1);

        merchantOrder.setOrderNo("NO" + System.currentTimeMillis());

        save(merchantOrder);

        List<OrderMaterialCreateCmd> materials = merchantOrderCreateCmd.getMaterials();

        for (OrderMaterialCreateCmd orderMaterialCreateCmd : materials) {

            OrderMaterial orderMaterial = new OrderMaterial();

            orderMaterial.setMaterialId(orderMaterialCreateCmd.getMaterialId());
            orderMaterial.setOrderId(merchantOrder.getId());
            orderMaterial.setQuantity(orderMaterialCreateCmd.getQuantity());
            orderMaterial.setPrice(orderMaterialCreateCmd.getPrice());
            orderMaterial.setAmount(orderMaterialCreateCmd.getAmount());

            orderMaterialMapper.insert(orderMaterial);
        }

        return SingleResponse.buildSuccess();
    }
}
