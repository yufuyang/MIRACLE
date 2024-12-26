import request from '@/utils/request'

// 获取活动列表
export function getActivityList(params) {
  return request({
    url: '/merchant/activity/list',
    method: 'post',
    data: params
  })
}

// 获取活动详情
export function getActivityDetail(id) {
  return request({
    url: `/merchant/activity/${id}`,
    method: 'get'
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

// 获取我的报名列表
export function getMyRegistrations(params) {
  return request({
    url: '/merchant/activity/registration/my',
    method: 'post',
    data: params
  })
}

// 取消报名
export function cancelRegistration(id) {
  return request({
    url: `/merchant/activity/registration/${id}/cancel`,
    method: 'put'
  })
}

// 获取活动统计数据
export function getActivityStats() {
  return request({
    url: '/merchant/activity/stats',
    method: 'get'
  })
} 