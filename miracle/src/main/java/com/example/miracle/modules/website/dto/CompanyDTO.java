package com.example.miracle.modules.website.dto;

import lombok.Data;

@Data
public class CompanyDTO {

    private Long id;
    private String companyName;
    private String description;
    private String logo;
    private Integer productCount;
    private Integer intentionCount;
}
