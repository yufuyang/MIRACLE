package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_order_fail_log")
public class OrderFailLog {
    private Long id;
    private Long orderId;
    private String type;
    private String reason;
    private Integer status;
    private LocalDateTime createTime;
} 