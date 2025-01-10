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