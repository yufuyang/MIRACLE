package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.dto.query.ActivityPageQry;
import com.example.miracle.modules.company.entity.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService extends IService<Activity> {
    
    /**
     * 分页查询活动列表
     *
     * @param qry 查询条件
     * @return 活动列表
     */
    MultiResponse<ActivityDTO> listActivities(ActivityPageQry activityPageQry);

} 