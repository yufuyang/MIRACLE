import request from '@/utils/request'

// 获取首页统计数据
export function getHomeStatistics() {
  return request({
    url: '/website/home/statistics',
    method: 'get'
  })
} 