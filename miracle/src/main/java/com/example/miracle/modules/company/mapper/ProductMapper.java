package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.modules.company.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询商品
     */
    IPage<Product> pageProduct(Page<Product> page,
                               @Param("merchantId") Long merchantId,
                               @Param("productName") String productName,
                               @Param("productCode") String productCode,
                               @Param("category") String category,
                               @Param("status") Integer status);
}