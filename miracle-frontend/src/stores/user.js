import { defineStore } from 'pinia'
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { companyLogin, merchantLogin, getUserDetail } from '@/api/user'
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
      console.log('初始化用户状态:', {
        token: token.value,
        userInfo: userInfo.value
      })
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

  // 获取用户详情
  const fetchUserDetail = async () => {
    try {
      const detailApi = userInfo.value?.role === 'MERCHANT' ? 
        '/merchant/user' : '/company/user'
      const res = await getUserDetail(detailApi)
      if (res.code === 200) {
        updateUserInfo({
          ...userInfo.value,
          ...res.data
        })
        return res.data
      }
    } catch (error) {
      console.error('获取用户详情失败:', error)
    }
  }

  // 登录
  const login = async (loginForm) => {
    try {
      const loginApi = loginForm.role === 'company' ? companyLogin : merchantLogin
      const res = await loginApi({
        username: loginForm.username,
        password: loginForm.password
      })
      
      if (res.code === 200) {
        const { token: newToken, role, ...info } = res.data
        // 设置token和基本用户信息
        setToken('Bearer ' + newToken)
        updateUserInfo({
          ...info,
          role // 确保role被保存到userInfo中
        })
        
        message.success('登录成功')
        const homePath = role === 'MERCHANT' ? '/workspace/merchant/home' : '/workspace/company/home'
        router.push(homePath)
        return true
      } else {
        message.error(res.msg || '登录失败')
        return false
      }
    } catch (error) {
      console.error('登录失败:', error)
      message.error(error.response?.data?.message || '登录失败')
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
    // 从 localStorage 中获取登录状态
    const storedToken = localStorage.getItem('token')
    const storedUserInfo = localStorage.getItem('userInfo')
    return !!storedToken && !!storedUserInfo
  }

  return {
    userInfo,
    token,
    login,
    logout,
    isLoggedIn,
    initUserState,
    updateUserInfo,
    fetchUserDetail
  }
}) 