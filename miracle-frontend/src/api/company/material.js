import request from '@/utils/request'

// 获取产品物料列表
export function getMaterialList(data) {
  return request({
    url: '/company/product/material/list',
    method: 'post',
    data
  })
}

// 添加物料
export function addMaterial(data) {
  return request({
    url: '/company/product/material',
    method: 'post',
    data
  })
}

// 更新物料
export function updateMaterial(data) {
  return request({
    url: '/company/product/material/update',
    method: 'post',
    data
  })
}

// 删除物料
export function deleteMaterial(id) {
  return request({
    url: `/company/product/material/delete/${id}`,
    method: 'delete'
  })
} 