import request from '@/utils/request'

// 获取商户信息
export function getMerchantInfo() {
  return request({
    url: '/merchant/base',
    method: 'get'
  })
}

// 更新商户信息
export function updateMerchantInfo(data) {
  return request({
    url: '/merchant/base',
    method: 'put',
    data
  })
} 