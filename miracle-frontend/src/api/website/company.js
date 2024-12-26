import request from '@/utils/request'

// 获取公司列表
export function getCompanyList(data) {
  return request({
    url: '/website/company/page',
    method: 'post',
    data
  })
}

// 获取公司详情
export function getCompanyDetail(id) {
  return request({
    url: `/website/company/${id}`,
    method: 'get'
  })
}

// 获取公司产品列表
export function getCompanyProducts(data) {
  return request({
    url: '/website/product/page',
    method: 'post',
    data
  })
}

// 获取公司环境图片
export function getEnvironmentImages(id) {
  return request({
    url: `/website/environment/list/${id}`,
    method: 'get'
  })
} 