package com.example.miracle.modules.website.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private Long id;
    private String productName;
    private String description;
    private String imageUrl;
    private Integer viewCount;
    private Integer intentionCount;
}
