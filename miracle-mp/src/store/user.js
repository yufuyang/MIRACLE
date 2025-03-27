import { defineStore } from 'pinia'
import { merchantLogin, companyLogin, getMerchantInfo, getCompanyInfo } from '../api/user'
import { ref } from 'vue'

// 使用组合式 API 方式定义 store
export const useUserStore = defineStore('user', () => {
  // state
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(null)

  // 初始化时尝试读取存储的用户信息
  try {
    const storedUserInfo = uni.getStorageSync('userInfo')
    if (storedUserInfo && typeof storedUserInfo === 'string') {
      const parsedInfo = JSON.parse(storedUserInfo)
      if (parsedInfo && typeof parsedInfo === 'object') {
        userInfo.value = parsedInfo
      } else {
        // 清除无效数据
        uni.removeStorageSync('userInfo')
      }
    }
  } catch (error) {
    console.error('解析用户信息失败:', error)
    // 清除损坏的数据
    uni.removeStorageSync('userInfo')
  }

  // actions
  async function login(data) {
    try {
      // 根据角色选择登录方法
      const loginApi = data.role === 'merchant' ? merchantLogin : companyLogin
      console.log('开始登录，角色:', data.role)
      const res = await loginApi({
        username: data.username,
        password: data.password
      })
      
      console.log('登录接口返回:', res)
      if (res.code === 200) {
        console.log('登录成功，获取到的token:', res.data.token)
        // 保存 token
        const newToken = res.data.token
        token.value = newToken
        uni.setStorageSync('token', newToken)
        console.log('存储后的token:', uni.getStorageSync('token'))
        
        // 保存用户信息
        const newUserInfo = {
          ...res.data,
          role: data.role.toUpperCase()
        }
        userInfo.value = newUserInfo
        // 确保存储的是字符串
        uni.setStorageSync('userInfo', JSON.stringify(newUserInfo))
        console.log('登录完成，当前token:', uni.getStorageSync('token'))
        console.log('当前用户信息:', uni.getStorageSync('userInfo'))
        return true
      }
      console.log('登录失败，返回码:', res.code)
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  async function getUserInfo() {
    try {
      const role = userInfo.value?.role
      const getInfoMethod = role === 'MERCHANT' ? getMerchantInfo : getCompanyInfo
      
      const res = await getInfoMethod()
      if (res.code === 200) {
        const newUserInfo = {
          ...res.data,
          role: role
        }
        userInfo.value = newUserInfo
        // 确保存储的是字符串
        uni.setStorageSync('userInfo', JSON.stringify(newUserInfo))
        return newUserInfo
      }
      return null
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.reLaunch({
      url: '/pages/login/index'
    })
  }

  return {
    token,
    userInfo,
    login,
    getUserInfo,
    logout
  }
}) 