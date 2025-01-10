import request from '@/utils/request'

// 创建商品意向
export function createIntention(data) {
  return request({
    url: '/merchant/product/intention',
    method: 'post',
    data
  })
}

// 获取商户意向列表
export function getMerchantIntentionList(params) {
  return request({
    url: '/merchant/product/intention/page',
    method: 'post',
    data: params
  })
}

// 取消意向
export function cancelIntention(params) {
  return request({
    url: `/merchant/product/intention/cancel`,
    method: 'post',
    data: params
  })
} 