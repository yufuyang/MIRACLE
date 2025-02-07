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