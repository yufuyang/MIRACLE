package com.example.miracle.common.constant;

import lombok.Getter;

public enum ProductCategory {


    BREAK_FAST("break_fast", "早餐"),

    LUNCH("lunch", "午餐"),

    DINNER("dinner", "晚餐"),

    NIGHT_SNACK("night_snack", "宵夜"),

    AFTERNOON_TEA("afternoon_tea", "下午茶");

    @Getter
    private String code;
    @Getter
    private String desc;

    ProductCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProductCategory of(String code) {
        if (code == null) {
            return null;
        }
        for (ProductCategory productCategory : ProductCategory.values()) {
            if (productCategory.code.equals(code)) {
                return productCategory;
            }
        }
        return null;
    }
}
