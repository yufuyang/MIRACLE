import request from '@/utils/request'

// 获取活动列表
export function getActivityList(params) {
  return request({
    url: '/website/activity/page',
    method: 'post',
    data: params
  })
}

// 获取活动详情
export function getActivityDetail(id) {
  return request({
    url: `/website/activity/${id}`,
    method: 'get'
  })
}

// 企业端 - 创建活动
export function createActivity(data) {
  return request({
    url: '/company/activity',
    method: 'post',
    data
  })
}

// 企业端 - 更新活动
export function updateActivity(data) {
  return request({
    url: '/company/activity',
    method: 'put',
    data
  })
}

// 企业端 - 删除活动
export function deleteActivity(id) {
  return request({
    url: `/company/activity/${id}`,
    method: 'delete'
  })
}

// 商户端 - 报名活动
export function registerActivity(data) {
  return request({
    url: '/merchant/activity/registration',
    method: 'post',
    data
  })
}

// 企业端 - 审核报名
export function auditActivityRegistration(data) {
  return request({
    url: `/company/activity/registration/${data.id}/audit`,
    method: 'put',
    data
  })
}

// 获取活动报名列表
export function getActivityRegistrations(params) {
  return request({
    url: '/company/activity/registration',
    method: 'get',
    params
  })
} 