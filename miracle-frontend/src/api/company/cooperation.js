import request from '@/utils/request'

// 获取合作列表
export function getCooperationList(params) {
  return request({
    url: '/company/merchant/cooperation/page',
    method: 'post',
    data: params
  })
}

// 终止合作
export function cancelCooperation(id) {
  return request({
    url: `/company/merchant/cooperation/dissolution/${id}`,
    method: 'delete'
  })
}

// 发起合作
export function handleCooperation(params) {
  return request({
    url: '/company/merchant/cooperation',
    method: 'post',
    data: params
  })
} 