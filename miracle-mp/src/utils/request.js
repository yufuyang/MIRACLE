// 封装请求方法
const request = (options) => {
  // const baseURL = 'https://yang.miracleyang.top/miracle' // 替换为实际的API地址

  const baseURL = 'http://127.0.0.1:8080/miracle'

  const { url, method, data, params } = options
  
  // 处理 GET 请求的参数
  let finalUrl = url
  if (method.toLowerCase() === 'get' && params) {
    const queryString = Object.keys(params)
      .map(key => `${key}=${params[key]}`)
      .join('&')
    finalUrl = `${url}${url.includes('?') ? '&' : '?'}${queryString}`
  }

  // 获取存储的token
  const token = uni.getStorageSync('token') || ''
  console.log(`发送请求 ${url} 前获取到的token:`, token)
  // 构建请求头
  const headers = {
    'Content-Type': 'application/json'
  }

  // 如果有token，添加到请求头
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  console.log(`请求 ${url} 的headers:`, headers)

  console.log('发起请求:', baseURL + finalUrl, options)

  return new Promise((resolve, reject) => {
    uni.request({
      url: baseURL + finalUrl,
      method,
      data: method.toLowerCase() === 'get' ? undefined : data,
      header: {
        ...headers,
        ...options.header
      },
      complete: (res) => {
        console.log(`请求 ${url} 完成，状态码:`, res.statusCode)
        console.log(`请求 ${url} 的headers:`, res.header)
      },
      success: (res) => {
        console.log('请求成功:', res)
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // token失效，清除存储并跳转登录页
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({
            url: '/pages/login/index'
          })
          reject(res)
        } else {
          uni.showToast({
            title: res.data?.message || '请求失败',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export default request 