import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref('')

  // 初始化用户状态
  const initUserState = () => {
    const storedToken = localStorage.getItem('token')
    const storedUserInfo = localStorage.getItem('userInfo')
    
    if (storedToken && storedUserInfo) {
      token.value = storedToken
      userInfo.value = JSON.parse(storedUserInfo)
    }
  }

  // 登录
  const login = (info, accessToken) => {
    userInfo.value = info
    token.value = accessToken
    localStorage.setItem('token', accessToken)
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 登出
  const logout = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 更新用户信息
  const updateUserInfo = async () => {
    try {
      const res = await getUserInfo()
      if (res.data) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
      }
    } catch (error) {
      console.error('Failed to update user info:', error)
    }
  }

  return {
    userInfo,
    token,
    initUserState,
    login,
    logout,
    updateUserInfo
  }
}) 