import request from '@/utils/request'

// 获取活动列表
export function getActivityList(params) {
  return request({
    url: '/merchant/activity/list',
    method: 'post',
    data: params
  })
}

// 报名活动
export function registerActivity(data) {
  return request({
    url: '/merchant/activity/registration',
    method: 'post',
    data
  })
}

// 检查活动报名状态
export function checkActivityRegistration(activityId) {
  return request({
    url: `/merchant/activity/registration/check/${activityId}`,
    method: 'get'
  })
}

// 取消活动报名
export function cancelActivityRegistration(activityId) {
  return request({
    url: `/merchant/activity/registration/${activityId}`,
    method: 'delete'
  })
}

// 获取活动统计数据
export function getActivityStats(activityId) {
  return request({
    url: `/merchant/activity/stats/${activityId}`,
    method: 'get'
  })
} 