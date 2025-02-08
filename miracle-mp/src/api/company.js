import request from '../utils/request'

// 获取企业列表
export function getCompanyList(params) {
  return request({
    url: '/website/company/page',
    method: 'post',
    data: {
      companyName: params.name,
      pageNum: params.pageNum,
      pageSize: params.pageSize
    }
  })
}

// 获取企业基本信息
export function getCompanyDetail(id) {
  return request({
    url: `/website/company/${id}`,
    method: 'get'
  })
}

// 获取企业图片列表
export function getCompanyImages(id) {
  return request({
    url: `/website/environment-image/list?ownerId=${id}&ownerType=company`,
    method: 'get'
  })
}

// 获取企业产品列表
export function getCompanyProducts(id, params) {
  return request({
    url: '/website/product/page',
    method: 'post',
    data: {
      companyId: id,
      pageNum: params.pageNum,
      pageSize: params.pageSize
    }
  })
}

// 获取企业活动列表
export function getCompanyActivities(params) {
  return request({
    url: '/website/activity/page',
    method: 'post',
    data: params
  })
} 