<script>
export default {
  onLaunch: function () {
    console.log('App Launch')
    // 获取当前页面路径
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const currentPath = currentPage ? currentPage.route : ''
    
    // 如果当前不在登录页，才检查登录状态
    if (currentPath !== 'pages/login/index') {
      this.checkLogin()
    }

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
  onTabItemTap(item) {
    console.log('点击tab', item)
    // 触发自定义事件
    uni.$emit('tabChange', item)
  },
  methods: {
    checkLogin() {
      const token = uni.getStorageSync('token')
      console.log('App启动时的token:', token)
      if (token) {
        // 有token，验证token是否有效
        uni.request({
          url: '/merchant/user',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          },
          success: (res) => {
            if (res.statusCode === 200) {
              // token有效，更新用户信息
              const userInfo = res.data.data
              uni.setStorageSync('userInfo', JSON.stringify(userInfo))
            } else {
              // token无效，清除存储
              console.log('token无效，清除存储')
              uni.removeStorageSync('token')
              uni.removeStorageSync('userInfo')
            }
          },
          fail: () => {
            // 请求失败，清除存储
            console.log('验证token请求失败')
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
          }
        })
      }
    }
  }
}
</script>

<style>
/* 每个页面公共css */
@font-face {
  font-family: "iconfont";
  src: url('static/iconfont/iconfont.ttf') format('truetype');
}

.iconfont {
  font-family: "iconfont" !important;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-back:before {
  content: "\e679";  /* 这里的编码需要和你的图标字体文件对应 */
}

page {
  background-color: #f5f5f5;
}
</style>
