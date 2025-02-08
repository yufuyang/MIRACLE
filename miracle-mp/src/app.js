import Vue from 'vue'
import App from './App'

Vue.config.productionTip = false

App.mpType = 'app'

const app = new Vue({
  ...App
})

// 注册自定义 tabBar
if (typeof wx !== 'undefined') {
  wx.onAppRoute(() => {
    const pages = getCurrentPages()
    const page = pages[pages.length - 1]
    if (page) {
      const selected = {
        'pages/index/index': 0,
        'pages/product/index': 1,
        'pages/activity/index': 2,
        'pages/company/index': 3,
        'pages/user/index': 4
      }[page.route]
      
      if (typeof selected !== 'undefined') {
        const customTabBar = page.getTabBar()
        if (customTabBar) {
          customTabBar.setData({
            selected
          })
        }
      }
    }
  })
}

app.$mount() 