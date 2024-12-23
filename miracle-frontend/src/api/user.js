import request from '@/utils/request'

// 企业用户登录
export function companyLogin(data) {
  return request({
    url: '/company/user/login',
    method: 'post',
    data
  })
}

// 商户用户登录
export function merchantLogin(data) {
  return request({
    url: '/merchant/user/login',
    method: 'post',
    data
  })
}

// 企业用户注册
export function companyRegister(data) {
  return request({
    url: '/website/company/register',
    method: 'post',
    data
  })
}

// 商户用户注册
export function merchantRegister(data) {
  return request({
    url: '/website/merchant/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
} 