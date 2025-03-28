import request from '../utils/request'

// 获取意向列表
export function getMerchantIntentionList(params) {
  return request({
    url: '/merchant/product/intention/page',
    method: 'post',
    data: params
  })
}

// 获取合作列表
export function getMerchantCooperationList(params) {
  return request({
    url: '/merchant/company/cooperation/page',
    method: 'post',
    data: params
  })
}

// 获取订单列表
export function getMerchantOrderList(params) {
  return request({
    url: '/merchant/order/list',
    method: 'post',
    data: params
  })
}

// 取消意向
export function cancelIntention(productId) {
  return request({
    url: '/merchant/product/intention/cancel',
    method: 'post',
    data: { productId }
  })
}

// 同意合作
export function acceptCooperation(companyId) {
  return request({
    url: '/merchant/company/cooperation/accept',
    method: 'post',
    data: { companyId }
  })
}

// 拒绝合作
export function rejectCooperation(companyId) {
  return request({
    url: '/merchant/company/cooperation/reject',
    method: 'post',
    data: { companyId }
  })
} 