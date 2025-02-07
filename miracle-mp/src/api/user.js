import request from '../utils/request'

// 商户登录
export function merchantLogin(data) {
  return request({
    url: '/merchant/user/login',
    method: 'POST',
    data
  })
}

// 企业登录
export function companyLogin(data) {
  return request({
    url: '/company/user/login',
    method: 'POST',
    data
  })
}

// 获取商户信息
export function getMerchantInfo() {
  return request({
    url: '/merchant/user',
    method: 'GET'
  })
}

// 获取企业信息
export function getCompanyInfo() {
  return request({
    url: '/company/user',
    method: 'GET'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'POST'
  })
} 