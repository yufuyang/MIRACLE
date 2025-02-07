// 封装请求方法
const request = (options) => {
  const baseURL = 'http://localhost:8080/miracle' // 替换为实际的API地址
  
  console.log('发起请求:', baseURL + options.url, options)

  return new Promise((resolve, reject) => {
    uni.request({
      url: baseURL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': uni.getStorageSync('token') || '',
        ...options.header
      },
      success: (res) => {
        console.log('请求成功:', res)
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // token 失效，跳转登录页
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