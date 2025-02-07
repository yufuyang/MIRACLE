import request from '../utils/request'

// 获取商户基本信息
export function getMerchantBase() {
  return request({
    url: '/merchant/base',
    method: 'get'
  })
}

// 更新商户基本信息
export function updateMerchantBase(data) {
  return request({
    url: '/merchant/base',
    method: 'put',
    data
  })
}

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

// 创建订单
export function createOrder(data) {
  return request({
    url: '/merchant/order/create',
    method: 'post',
    data
  })
}

// 获取合作企业列表
export function getCooperationCompanyList() {
  return request({
    url: '/merchant/company/cooperation/page',
    method: 'post',
    data: {}
  })
}

// 获取企业产品列表
export function getCompanyProductList(companyId) {
  return request({
    url: '/merchant/company/product/list',
    method: 'get',
    params: { companyId }
  })
}

// 获取产品物料列表
export function getProductMaterials(productId) {
  return request({
    url: '/merchant/product/material/list',
    method: 'get',
    params: { productId }
  })
} 