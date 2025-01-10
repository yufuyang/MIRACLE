import request from '@/utils/request'

// 获取合作列表
export function getCooperationList(params) {
  return request({
    url: '/merchant/company/cooperation/page',
    method: 'post',
    data: params
  })
}

// 同意合作
export function handleCooperation(data) {
  return request({
    url: '/merchant/company/cooperation',
    method: 'post',
    data
  })
}


// 取消合作
export function cancelCooperation(data) {
  return request({
    url: '/merchant/cooperation/cancel',
    method: 'post',
    data
  })
} 