import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/merchant/order/list',
    method: 'post',
    data: params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/merchant/order/get/${id}`,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/merchant/order/save',
    method: 'post',
    data
  })
}

// 获取可合作企业列表
export function getCooperationCompanyList(params) {
  return request({
    url: '/merchant/company/cooperation/page',
    method: 'post',
    data: params
  })
}

// 获取企业产品列表
export function getCompanyProductList(params) {
  return request({
    url: '/merchant/company/cooperation/product/page',
    method: 'post',
    data: params
  })
}

// 获取产品物料列表
export function getProductMaterialList(params) {
  return request({
    url: `/merchant/company/cooperation/product/material/page`,
    method: 'post',
    data: params
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/merchant/order/cancel/${id}`,
    method: 'put'
  })
}

// 确认收货
export function confirmReceive(id) {
  return request({
    url: `/merchant/order/${id}/receive`,
    method: 'post'
  })
} 