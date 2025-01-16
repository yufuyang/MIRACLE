package com.example.miracle.modules.company.dto.query;

import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductMaterialPageQry extends PageQuery {

    private Long productId;
}
