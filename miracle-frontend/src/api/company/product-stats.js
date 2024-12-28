import request from '@/utils/request'

// 获取产品统计概览
export function getProductStatsOverview(companyId) {
  return request({
    url: '/company/product/stats/overview',
    method: 'get',
    params: { companyId }
  })
}

// 获取产品趋势数据
export function getProductTrend(params) {
  return request({
    url: '/company/product/stats/trend',
    method: 'get',
    params
  })
}

// 获取热门产品排行
export function getHotProducts(params) {
  return request({
    url: '/company/product/stats/hot',
    method: 'get',
    params
  })
}

// 记录产品浏览
export function recordProductView(productId) {
  return request({
    url: `/company/product/stats/view/${productId}`,
    method: 'post'
  })
}

// 记录产品意向
export function recordProductIntention(productId) {
  return request({
    url: `/company/product/stats/intention/${productId}`,
    method: 'post'
  })
}

// 取消产品意向
export function cancelProductIntention(productId) {
  return request({
    url: `/company/product/stats/intention/${productId}`,
    method: 'delete'
  })
}

// 获取产品列表
export function getProductList() {
  return request({
    url: '/company/product/list/simple',  // 假设这是一个简化版的产品列表接口
    method: 'get'
  })
} 