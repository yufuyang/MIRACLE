package com.example.miracle.modules.company.dto.query;


import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityRegistrationPageQry  extends PageQuery {
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 状态:0-待审核,1-已通过,2-已拒绝
     */
    private Integer status;
}
