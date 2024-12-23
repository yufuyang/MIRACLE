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