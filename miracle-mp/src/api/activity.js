import request from '@/utils/request'

// 获取活动详情
export function getActivityDetail(id) {
  return request({
    url: `/website/activity/${id}`,
    method: 'get'
  })
}

// 获取活动列表
export function getActivityList(params) {
  return request({
    url: '/website/activity/page',
    method: 'post',
    data: params
  })
} 