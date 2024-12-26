import request from '@/utils/request'

// 获取热门产品（用于轮播图）
export function getFeaturedProducts() {
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

// 获取优质企业
export function getFeaturedCompanies() {
  return request({
    url: '/website/home/featured-companies',
    method: 'get'
  })
} 