import request from '@/utils/request'

// 获取产品分类列表
export function getProductCategories(query) {
  return request({
    url: '/website/product/category/list',
    method: 'post',
    data: query
  })
}

// 获取产品分类树
export function getProductCategoryTree(companyId) {
  return request({
    url: `/website/product/category/tree/${companyId}`,
    method: 'get'
  })
}

// 获取产品列表
export function getProductList(params) {
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

// 获取产品图片列表
export function getProductImages(productId) {
  return request({
    url: `/website/company/product/image/list/${productId}`,
    method: 'get'
  })
}

// 企业端 - 新增产品
export function createProduct(data) {
  return request({
    url: '/company/product',
    method: 'post',
    data
  })
}

// 企业端 - 更新产品
export function updateProduct(data) {
  return request({
    url: '/company/product',
    method: 'put',
    data
  })
}

// 企业端 - 删除产品
export function deleteProduct(id) {
  return request({
    url: `/company/product/${id}`,
    method: 'delete'
  })
}

// 添加意向
export function addIntention(data) {
  return request({
    url: '/merchant/product/intention',
    method: 'post',
    data
  })
}

// 取消意向
export function cancelIntention(data) {
  return request({
    url: '/merchant/product/intention/cancel',
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