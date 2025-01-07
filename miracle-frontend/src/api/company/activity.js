import request from '@/utils/request'

// 获取活动列表
export function getActivityList(params) {
  return request({
    url: '/company/activity/list',
    method: 'post',
    data: params
  })
}

// 获取活动详情
export function getActivityDetail(id) {
  return request({
    url: `/company/activity/${id}`,
    method: 'get'
  })
}

// 创建活动
export function createActivity(data) {
  return request({
    url: '/company/activity',
    method: 'post',
    data
  })
}

// 更新活动
export function updateActivity(data) {
  return request({
    url: '/company/activity',
    method: 'put',
    data
  })
}

// 删除活动
export function deleteActivity(id) {
  return request({
    url: `/company/activity/${id}`,
    method: 'delete'
  })
}

// 获取活动统计数据
export function getActivityStats() {
  return request({
    url: '/company/activity/stats',
    method: 'get'
  })
}

// 获取活动报名列表
export function getActivityRegistrations(params) {
  return request({
    url: '/company/activity/registration/list',
    method: 'post',
    data: params
  })
}

// 审核活��报名
export function auditActivityRegistration(id, data) {
  return request({
    url: `/company/activity/registration/${id}/audit`,
    method: 'put',
    data
  })
}



// 获取活动统计概览
export function getActivityStatsOverview() {
  return request({
    url: '/company/activity/stats/overview',
    method: 'get'
  })
}

// 获取活动趋势数据
export function getActivityStatsTrend(params) {
  return request({
    url: '/company/activity/stats/trend',
    method: 'get',
    params
  })
}

// 获取活动选项列表（统计用）
export function getActivityStatsOptions() {
  return request({
    url: '/company/activity/stats/activities',
    method: 'get'
  })
}

// 获取热门活动排行（统计用）
export function getActivityStatsRanking(params) {
  return request({
    url: '/company/activity/stats/hot',
    method: 'get',
    params
  })
}