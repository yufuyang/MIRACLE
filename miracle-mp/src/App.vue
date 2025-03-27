<script>
export default {
  onLaunch: function () {
    console.log('App Launch')
    // 检查登录状态
    this.checkLogin()
    // 添加请求拦截器
    const oldRequest = uni.request
    uni.request = function(options) {
      console.log('请求拦截器 - headers:', options.header)
      return oldRequest.call(this, options)
    }
  },
  onShow: function () {
    console.log('App Show')
  },
  onHide: function () {
    console.log('App Hide')
  },
  methods: {
    checkLogin() {
      const token = uni.getStorageSync('token')
      console.log('App启动时的token:', token)
      if (!token) {
        console.log('无token，跳转到登录页')
        // 无token，跳转登录页
        uni.reLaunch({
          url: '/pages/login/index'
        })
        return
      }

      console.log('开始验证token')
      // 有token，验证token是否有效
      uni.request({
        url: baseURL + '/merchant/user',  // 添加baseURL
        method: 'GET',
        header: {
          'Authorization': `Bearer ${token}`
        },
        success: (res) => {
          console.log('验证token响应:', res)
          if (res.statusCode === 200) {
            // token有效，更新用户信息
            const userInfo = res.data.data
            uni.setStorageSync('userInfo', JSON.stringify(userInfo))  // 确保存储字符串
            console.log('token验证成功，更新用户信息:', userInfo)
          } else {
            console.log('token无效，清除存储')
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.reLaunch({
              url: '/pages/login/index'
            })
          }
        },
        fail: (err) => {
          console.log('验证token请求失败:', err)
          // 请求失败，清除存储并跳转登录页
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({
            url: '/pages/login/index'
          })
        }
      })
    }
  }
}
</script>

<style>
/* 每个页面公共css */
page {
  background-color: #f5f5f5;
}
</style>
