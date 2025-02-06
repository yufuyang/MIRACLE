import request from '@/utils/request'

// 获取公司产品列表
export function getCompanyProducts(params) {
  return request({
    url: '/website/product/page',
    method: 'post',
    data: params
  })
}

// 获取产品详情
export function getCompanyProductDetail(id) {
  return request({
    url: `/website/product/${id}`,
    method: 'get'
  })
}

// 获取产品图片列表
export function getProductImages(productId) {
  return request({
    url: `/website/company/product/image/list/${productId}`,
    method: 'get'
  })
}

// 获取产品统计信息
export function getProductStats(productId) {
  return request({
    url: `/website/company/product/stats/${productId}`,
    method: 'get'
  })
}

// 获取产品分类
export function getProductCategory(id) {
  return request({
    url: `/website/product/category/${id}`,
    method: 'get'
  })
}

// 获取产品生产步骤
export function getProductSteps(productId) {
  return request({
    url: `/website/product/steps/${productId}`,
    method: 'get'
  })
}