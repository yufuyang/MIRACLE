import request from '../utils/request'

// 测试数据
const mockData = {
  products: [
    { 
      id: 1, 
      name: '测试产品1', 
      price: '999',
      companyName: '测试企业A',
      coverImage: 'https://via.placeholder.com/600x400'
    },
    { 
      id: 2, 
      name: '测试产品2', 
      price: '888',
      companyName: '测试企业B',
      coverImage: 'https://via.placeholder.com/600x400'
    }
  ],
  activities: [
    {
      id: 1,
      name: '测试活动1',
      startTime: '2024-02-01',
      endTime: '2024-02-29',
      companyName: '测试企业A',
      coverImage: 'https://via.placeholder.com/600x300'
    }
  ],
  companies: [
    {
      id: 1,
      name: '测试企业A',
      description: '这是一段测试企业介绍...',
      logo: 'https://via.placeholder.com/120'
    }
  ]
}

// 获取热门产品
export function getHotProducts() {
  return request({
    url: '/website/home/hot-products',
    method: 'get'
  })
}

// 获取热门活动
export function getHotActivities() {
  return request({
    url: '/website/home/banners',
    method: 'get'
  })
}

// 获取推荐企业
export function getRecommendCompanies() {
  return request({
    url: '/website/home/featured-companies',
    method: 'get'
  })
}

// 获取产品列表
export function getProducts(params) {
  return request({
    url: '/website/product/page',
    method: 'post',
    data: params
  })
}

// 获取产品详情
export function getProductDetail(id) {
  return request({
    url: `/website/product/${id}`,
    method: 'get'
  })
}

// 获取活动列表
export function getActivities(params) {
  return request({
    url: '/website/activity/page',
    method: 'post',
    data: params
  })
}

// 获取活动详情
export function getActivityDetail(id) {
  return request({
    url: `/website/activity/${id}`,
    method: 'get'
  })
}

// 获取企业列表
export function getCompanies(params) {
  return request({
    url: '/website/company/page',
    method: 'post',
    data: params
  })
}

// 获取企业详情
export function getCompanyDetail(id) {
  return request({
    url: `/website/company/${id}`,
    method: 'get'
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/website/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/website/auth/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/website/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/website/user/info',
    method: 'put',
    data
  })
}

// 收藏/取消收藏
export function toggleFavorite(data) {
  return request({
    url: '/website/user/favorite',
    method: 'post',
    data
  })
}

// 获取收藏列表
export function getFavorites(params) {
  return request({
    url: '/website/user/favorites',
    method: 'get',
    data: params
  })
} 