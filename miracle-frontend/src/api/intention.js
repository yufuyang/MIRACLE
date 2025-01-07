import request from '@/utils/request'

// 获取意向列表
export function getIntentionList(params) {
  return request({
    url: '/company/intention/list',
    method: 'post',
    data: params
  })
}

// 处理意向
export function handleIntention(id, status) {
  return request({
    url: `/company/intention/${id}/status`,
    method: 'put',
    data: { status }
  })
} 