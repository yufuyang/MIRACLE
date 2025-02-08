import request from '@/utils/request'

// 获取活动详情
export function getActivityDetail(id) {
  return request({
    url: `/website/activity/${id}`,
    method: 'get'
  })
} 