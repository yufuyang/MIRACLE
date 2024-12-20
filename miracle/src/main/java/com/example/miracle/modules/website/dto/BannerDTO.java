package com.example.miracle.modules.website.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BannerDTO {

    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Integer status;
    private Integer viewCount;
    private Integer registerCount;
}
