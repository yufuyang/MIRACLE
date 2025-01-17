// 取消意向
import request from "@/utils/request";

export function cancelIntention(productId) {
  return request({
    url: `/merchant/product/intention/${productId}`,
    method: 'delete',
    headers: {
      'Content-Type': 'application/json'
    },
    data: {
      productId
    }
  })
}

export function createIntention(data) {
  return request({
    url: '/merchant/product/intention',
    method: 'post',
    data
  })
}

// 检查产品意向状态
export function checkProductIntention(productId) {
  return request({
    url: `/merchant/product/intention/check/${productId}`,
    method: 'get'
  })
}