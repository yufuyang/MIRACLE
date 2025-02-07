import { defineStore } from 'pinia'
import { merchantLogin, companyLogin, getMerchantInfo, getCompanyInfo } from '../api/user'
import { ref } from 'vue'

// 使用组合式 API 方式定义 store
export const useUserStore = defineStore('user', () => {
  // state
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null)

  // actions
  async function login(data) {
    try {
      // 根据角色选择登录方法
      const loginApi = data.role === 'merchant' ? merchantLogin : companyLogin
      const res = await loginApi({
        username: data.username,
        password: data.password
      })
      
      if (res.code === 200) {
        // 保存 token
        const newToken = 'Bearer ' + res.data.token
        token.value = newToken
        uni.setStorageSync('token', newToken)
        
        // 保存用户信息
        const newUserInfo = {
          ...res.data,
          role: data.role.toUpperCase()
        }
        userInfo.value = newUserInfo
        uni.setStorageSync('userInfo', JSON.stringify(newUserInfo))
        return true
      }
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