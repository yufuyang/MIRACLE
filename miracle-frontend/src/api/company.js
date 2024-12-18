import request from '@/utils/request'

// 获取公司列表
export function getCompanyList(params) {
  return request({
    url: '/api/platform/company/page',
    method: 'post',
    data: {
      pageNum: params.page,
      pageSize: params.size,
      companyName: params.companyName,
      region: params.region
    }
  })
}

// 获取公司详情
export function getCompanyDetail(id) {
  return request({
    url: `/api/platform/company/${id}`,
    method: 'get'
  })
}

// 获取企业产品列表
export function getCompanyProducts(params) {
  return request({
    url: '/api/company/product/page',
    method: 'post',
    data: {
      pageNum: params.page,
      pageSize: params.size,
      companyId: params.companyId
    }
  })
} 