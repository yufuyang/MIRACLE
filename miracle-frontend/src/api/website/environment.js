import request from '@/utils/request'

// 获取环境图片列表
export function getEnvironmentImages(params) {
  return request({
    url: '/website/environment-image/list',
    method: 'get',
    params
  })
} 