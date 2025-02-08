import request from '@/utils/request'

// 获取产品详情
export function getProductDetail(id) {
  return request({
    url: `/website/product/${id}`,
    method: 'get'
  })
}

// 获取产品图片列表
export function getProductImages(id) {
  return request({
    url: `/website/company/product/image/list/${id}`,
    method: 'get'
  })
}

// 获取产品步骤
export function getProductSteps(id) {
  return request({
    url: `/website/product/steps/${id}`,
    method: 'get'
  })
}

// 获取产品分类信息
export function getProductCategory(id) {
  return request({
    url: `/website/product/category/${id}`,
    method: 'get'
  })
}

// 获取公司信息
export function getCompanyInfo(id) {
  return request({
    url: `/website/company/${id}`,
    method: 'get'
  })
}

// 检查产品意向状态
export function checkProductIntention(id) {
  return request({
    url: `/merchant/product/intention/check/${id}`,
    method: 'get'
  })
}

// 取消产品意向
export function cancelProductIntention(productId) {
  return request({
    url: '/merchant/product/intention/cancel',
    method: 'post',
    data: { productId }
  })
}

// 添加产品意向
export function addProductIntention(data) {
  return request({
    url: '/merchant/product/intention',
    method: 'post',
    data
  })
} 