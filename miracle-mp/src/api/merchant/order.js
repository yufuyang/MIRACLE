import request from '@/utils/request'

// 获取可合作企业列表
export function getCooperationCompanyList(params) {
  return request({
    url: '/merchant/company/cooperation/page',
    method: 'post',
    data: {
      companyName: params?.companyName || '',
      status: 1,  // 只获取已合作的企业
      pageNum: params?.pageNum || 1,
      pageSize: params?.pageSize || 999
    }
  })
}

// 获取企业产品列表
export function getCompanyProductList(companyId, params) {
  return request({
    url: '/merchant/company/cooperation/product/page',
    method: 'post',
    data: {
      companyId: companyId,
      productName: params?.productName || '',
      pageNum: params?.pageNum || 1,
      pageSize: params?.pageSize || 999
    }
  })
}

// 获取产品物料列表
export function getProductMaterialList(productId) {
  return request({
    url: '/merchant/company/cooperation/product/material/page',
    method: 'post',
    data: {
      productId: productId,
      pageNum: 1,
      pageSize: 999
    }
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/merchant/order/save',
    method: 'post',
    data
  })
} 