package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.CompanyProductCategoryTreeDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductCategoryQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductCategory;
import com.example.miracle.modules.company.mapper.CompanyProductCategoryMapper;
import com.example.miracle.modules.company.service.CompanyProductCategoryService;
import com.example.miracle.modules.company.service.CompanyProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公司产品分类服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyProductCategoryServiceImpl extends ServiceImpl<CompanyProductCategoryMapper, CompanyProductCategory> implements CompanyProductCategoryService {

    @Override
    public MultiResponse<CompanyProductCategory> list(CompanyProductCategoryQuery query) {

        LambdaQueryWrapper<CompanyProductCategory> wrapper = new LambdaQueryWrapper<CompanyProductCategory>()
                .eq(CompanyProductCategory::getCompanyId, query.getCompanyId())
                .eq(query.getParentId() != null, CompanyProductCategory::getParentId, query.getParentId())
                .eq(query.getStatus() != null, CompanyProductCategory::getStatus, query.getStatus())
                .orderByAsc(CompanyProductCategory::getSort);

        return MultiResponse.of(this.list(wrapper));
    }

    @Override
    public SingleResponse<Boolean> updateSort(Long id, Integer sort) {
        CompanyProductCategory category = new CompanyProductCategory();
        category.setId(id);
        category.setSort(sort);
        return SingleResponse.of(this.updateById(category));
    }

    @Override
    public MultiResponse<CompanyProductCategoryTreeDTO> tree(Long companyId) {
        // 查询所有分类
        List<CompanyProductCategory> allCategories = this.list(new LambdaQueryWrapper<CompanyProductCategory>()
                .eq(CompanyProductCategory::getCompanyId, companyId)
                .orderByAsc(CompanyProductCategory::getSort));

        // 转换为树形结构
        List<CompanyProductCategoryTreeDTO> tree = buildTree(allCategories);

        return MultiResponse.of(tree);
    }



    /**
     * 构建树形结构
     */
    private List<CompanyProductCategoryTreeDTO> buildTree(List<CompanyProductCategory> categories) {
        // 转换为DTO
        List<CompanyProductCategoryTreeDTO> dtoList = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建父子关系
        Map<Long, List<CompanyProductCategoryTreeDTO>> parentIdMap = dtoList.stream()
                .collect(Collectors.groupingBy(dto -> 
                    dto.getParentId() != null ? dto.getParentId() : 0L
                ));

        // 设置子节点
        dtoList.forEach(dto -> 
            dto.setChildren(parentIdMap.getOrDefault(dto.getId(), new ArrayList<>()))
        );

        // 返回顶层节点
        return parentIdMap.getOrDefault(0L, new ArrayList<>());
    }

    /**
     * 转换为DTO
     */
    private CompanyProductCategoryTreeDTO convertToDTO(CompanyProductCategory category) {
        CompanyProductCategoryTreeDTO dto = new CompanyProductCategoryTreeDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setParentId(category.getParentId());
        dto.setSort(category.getSort());
        dto.setStatus(category.getStatus());
        return dto;
    }
} 