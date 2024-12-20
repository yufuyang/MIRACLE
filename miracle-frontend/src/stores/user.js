import { defineStore } from 'pinia'
import request from '@/utils/request'
import { message } from 'ant-design-vue'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: false,
    userInfo: {
      id: null,
      username: '',
      email: '',
      role: '',
      avatar: ''
    },
    token: null
  }),

  actions: {
    setLoginState(state) {
      this.isLoggedIn = state
    },

    setUserInfo(info) {
      this.userInfo = info
    },

    setToken(token) {
      this.token = token
      if (token) {
        localStorage.setItem('token', token)
        if (this.userInfo.role) {
          localStorage.setItem('userRole', this.userInfo.role)
        }
      } else {
        localStorage.removeItem('token')
        localStorage.removeItem('userRole')
      }
    },

    login(userInfo, token) {
      this.setUserInfo(userInfo)
      this.setToken(token)
      this.setLoginState(true)
    },

    async logout() {
      try {
        // 根据当前用户角色调用对应的登出接口
        const logoutUrl = this.userInfo.role === 'company'
          ? '/company/user/logout'
          : '/merchant/user/logout'
        
        await request.post(logoutUrl)
        message.success('登出成功')
      } catch (error) {
        console.error('Logout failed:', error)
        message.error('登出失败：' + (error.message || '未知错误'))
      } finally {
        // 无论登出是否成功，都清除本地状态
        this.setUserInfo({
          id: null,
          username: '',
          email: '',
          role: '',
          avatar: ''
        })
        this.setToken(null)
        this.setLoginState(false)
      }
    },

    // 初始化用户状态，从localStorage恢复token
    async initUserState() {
      const token = localStorage.getItem('token')
      const role = localStorage.getItem('userRole')
      
      if (token && role) {
        this.setToken(token)
        try {
          // 根据角色调用不同的用户信息接口
          const infoUrl = role === 'company' 
            ? '/company/user/info' 
            : '/merchant/user/info'
            
          const response = await request.get(infoUrl)
          if (response.data) {
            const userInfo = response.data
            userInfo.role = role
            this.setUserInfo(userInfo)
            this.setLoginState(true)
          } else {
            this.logout() // token无效，清除状态
          }
        } catch (error) {
          console.error('Failed to get user info:', error)
          this.logout() // 出错时清除状态
        }
      }
    }
  }
}) 