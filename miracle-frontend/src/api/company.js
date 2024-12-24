import request from '@/utils/request'

// 获取公司列表
export function getCompanyList(params) {
  return request({
    url: '/website/company/page',
    method: 'post',
    data: params
  })
}

// 获取公司详情
export function getCompanyDetail(id) {
  return request({
    url: `/website/company/${id}`,
    method: 'get'
  })
}

// 更新公司信息
export function updateCompany(data) {
  return request({
    url: '/company/base',
    method: 'put',
    data
  })
}

// 获取公司活动列表
export function getCompanyActivities(params) {
  return request({
    url: '/company/activity',
    method: 'get',
    params
  })
}

// 获取活动详情
export function getActivityDetail(id) {
  return request({
    url: `/company/activity/${id}`,
    method: 'get'
  })
}

// 获取企业统计数据
export function getCompanyStats() {
  return request({
    url: '/website/company/stats',
    method: 'get'
  })
}

/**
 * 获取产品列表
 */
export function getCompanyProducts(params) {
  return request({
    url: '/company/product/page',
    method: 'post',
    data: params
  })
}

// 添加公司产品
export function addCompanyProduct(data) {
  return request({
    url: '/company/product',
    method: 'post',
    data
  })
}

// 更新公司产品
export function updateCompanyProduct(data) {
  return request({
    url: '/company/product',
    method: 'put',
    data
  })
}

// 删除公司产品
export function deleteCompanyProduct(id) {
  return request({
    url: `/company/product/${id}`,
    method: 'delete'
  })
}

// 切换产品状态
export function toggleProductStatus(id) {
  return request({
    url: `/company/product/${id}/status`,
    method: 'put'
  })
}

// 获取产品分类列表
export function getProductCategories() {
  return request({
    url: '/company/product/category/list',
    method: 'post',
    data: {}
  })
}

// 添加产品分类
export function addCategory(data) {
  return request({
    url: '/company/product/category',
    method: 'post',
    data
  })
}

// 更新产品分类
export function updateCategory(data) {
  return request({
    url: '/company/product/category',
    method: 'put',
    data
  })
}

// 删除产品分类
export function deleteCategory(id) {
  return request({
    url: `/company/product/category/${id}`,
    method: 'delete'
  })
}

// 获取产品图片列表
export function getProductImages(productId) {
  return request({
    url: `/company/product/image/${productId}`,
    method: 'get'
  })
}

// 添加产品图片
export function addProductImage(data) {
  return request({
    url: '/company/product/image',
    method: 'post',
    data
  })
}

// 删除产品图片
export function deleteProductImage(id) {
  return request({
    url: `/company/product/image/${id}`,
    method: 'delete'
  })
}

// 设置主图
export function setMainImage(id) {
  return request({
    url: `/company/product/image/${id}/main`,
    method: 'put'
  })
}

// 更新图片排序
export function updateImageSort(data) {
  return request({
    url: '/company/product/image/sort',
    method: 'put',
    data
  })
} 