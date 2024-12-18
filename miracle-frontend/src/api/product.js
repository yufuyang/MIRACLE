import request from '@/utils/request'

// 分页查询产品列表
export function getProductList(params) {
  return request({
    url: '/api/company/product/page',
    method: 'post',
    data: params
  })
}

// 获取产品分类
export function getProductCategories() {
  return request({
    url: '/api/company/product/category/list',
    method: 'get'
  })
}

// 获取产品详情
export function getProductDetail(id) {
  return request({
    url: `/api/company/product/${id}`,
    method: 'get'
  })
}

// 添加意向
export function addIntention(data) {
  return request({
    url: '/api/merchant/product/intention',
    method: 'post',
    data
  })
}

// 取消意向
export function cancelIntention(data) {
  return request({
    url: '/api/merchant/product/intention/cancel',
    method: 'post',
    data
  })
}

// 获取公司产品列表
export function getCompanyProducts(params) {
  return request({
    url: '/product/company',
    method: 'get',
    params
  })
} 