import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/company/order/list',
    method: 'post',
    data: params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/company/order/${id}`,
    method: 'get'
  })
}

// 发货
export function deliverOrder(data) {
  return request({
    url: '/company/order/deliver',
    method: 'post',
    data
  })
}

// 审批通过
export function approveOrder(id) {
  return request({
    url: `/company/order/approve/${id}`,
    method: 'put'
  })
}

// 审批拒绝
export function rejectOrder(id) {
  return request({
    url: `/company/order/reject/${id}`,
    method: 'put'
  })
}

// 完成订单
export function completeOrder(id) {
  return request({
    url: `/company/order/complete/${id}`,
    method: 'put'
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/company/order/cancel/${id}`,
    method: 'put'
  })
} 