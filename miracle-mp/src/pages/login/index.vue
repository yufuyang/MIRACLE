<template>
  <view class="login-container">
    <view class="login-box">
      <view class="login-header">
        <text class="title">MIRACLE</text>
<!--        <text class="subtitle">工业品交易平台</text>-->
      </view>
      
      <view class="login-form">
        <view class="form-item">
          <input 
            v-model="loginForm.username"
            placeholder="请输入用户名"
            class="input"
          />
        </view>
        <view class="form-item">
          <input 
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            class="input"
          />
        </view>
        
        <button 
          class="login-btn" 
          @tap="handleLogin" 
          :loading="loading"
        >
          登录
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const loading = ref(false)

const loginForm = ref({
  username: '',
  password: '',
  role: 'merchant' // 默认固定为商户角色
})

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    uni.showToast({
      title: '请输入用户名和密码',
      icon: 'none'
    })
    return
  }

  loading.value = true
  try {
    const success = await userStore.login(loginForm.value)
    
    if (success) {
      uni.showToast({
        title: '登录成功',
        icon: 'success'
      })
      // 跳转到首页
      uni.switchTab({
        url: '/pages/index/index'
      })
    } else {
      uni.showToast({
        title: '登录失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('登录失败:', error)
    uni.showToast({
      title: '登录失败',
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .login-box {
    width: 100%;
    padding: 0 60rpx;
    
    .login-header {
      text-align: center;
      margin-bottom: 80rpx;
      
      .title {
        font-size: 64rpx;
        font-weight: bold;
        color: #333;
        letter-spacing: 4rpx;
      }
      
      .subtitle {
        font-size: 28rpx;
        color: #666;
      }
    }
    
    .login-form {
      .form-item {
        margin-bottom: 40rpx;
        
        .input {
          width: 100%;
          height: 100rpx;
          background: #f8f8f8;
          border-radius: 12rpx;
          padding: 0 40rpx;
          box-sizing: border-box;
          font-size: 32rpx;
          
          &::placeholder {
            color: #999;
          }
        }
      }
      
      .login-btn {
        width: 100%;
        height: 100rpx;
        background: #1890ff;
        color: #fff;
        border-radius: 12rpx;
        margin-top: 60rpx;
        font-size: 36rpx;
        font-weight: 500;
        letter-spacing: 2rpx;
        
        &[loading] {
          opacity: 0.7;
        }
        
        &:active {
          opacity: 0.9;
        }
      }
    }
  }
}
</style> 