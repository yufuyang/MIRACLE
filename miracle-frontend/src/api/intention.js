import request from '@/utils/request'

// 获取意向列表
export function getIntentionList(params) {
  return request({
    url: '/company/product/intention/page',
    method: 'post',
    data: params
  })
}

// 处理意向 - 发起合作
export function handleCooperation(params) {
  return request({
    url: `/company/merchant/cooperation`,
    method: 'post',
    data:params
  })
}