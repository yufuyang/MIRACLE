package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.ProductMaterial;
import com.example.miracle.modules.company.mapper.ProductMaterialMapper;
import com.example.miracle.modules.company.service.ProductMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductMaterialServiceImpl extends ServiceImpl<ProductMaterialMapper, ProductMaterial> implements ProductMaterialService {
}
