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

// 登录
export function login(data) {
  return request({
    url: '/merchant/login',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/merchant/user',
    method: 'get'
  })
}

// 注册
export function register(data) {
  return request({
    url: '/merchant/register',
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

// 退出登录
export function logout() {
  return request({
    url: '/merchant/logout',
    method: 'post'
  })
} 