import request from '@/utils/request'

// 获取轮播图
export function getBanners() {
  return request({
    url: '/website/home/banners',
    method: 'get'
  })
}

// 获取热门产品
export function getHotProducts() {
  return request({
    url: '/website/home/hot-products',
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