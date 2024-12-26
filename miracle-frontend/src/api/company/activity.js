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