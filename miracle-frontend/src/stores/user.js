import { defineStore } from 'pinia'
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { companyLogin, merchantLogin } from '@/api/user'
import router from '@/router'

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

  // 更新用户信息
  const updateUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 登录
  const login = async (loginForm) => {
    try {
      const loginApi = loginForm.role === 'company' ? companyLogin : merchantLogin
      const res = await loginApi(loginForm)
      if (res.code === 200) {
        const { token: newToken, ...info } = res.data
        setToken(newToken)
        updateUserInfo({ ...info, role: loginForm.role })
        message.success('登录成功')
        router.push('/workspace')
        return true
      } else {
        message.error(res.msg || '登录失败')
        return false
      }
    } catch (error) {
      console.error('登录失败:', error)
      message.error('登录失败')
      return false
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
    message.success('退出登录成功')
  }

  // 检查是否登录
  const isLoggedIn = () => {
    return !!token.value && !!userInfo.value
  }

  return {
    userInfo,
    token,
    login,
    logout,
    isLoggedIn,
    initUserState,
    updateUserInfo
  }
}) 