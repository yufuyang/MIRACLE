<template>
  <view class="register-container">
    <view class="register-box">
      <view class="register-header">
        <text class="title">MIRACLE</text>
        <text class="subtitle">商户注册</text>
      </view>
      
      <view class="register-form">
        <view class="form-item">
          <input 
            v-model="registerForm.username"
            placeholder="请输入用户名"
            class="input"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            class="input"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            class="input"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerForm.companyName"
            placeholder="请输入商户名称"
            class="input"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerForm.businessLicense"
            placeholder="请输入营业执照号"
            class="input"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerForm.contactName"
            placeholder="请输入联系人姓名"
            class="input"
          />
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerForm.contactPhone"
            placeholder="请输入联系电话"
            class="input"
            type="number"
          />
        </view>
        
        <view class="form-item address-item">
          <picker 
            mode="region" 
            @change="onRegionChange"
            class="picker"
            :value="[registerForm.province, registerForm.city, registerForm.district]"
          >
            <view class="picker-value">
              {{ registerForm.province && registerForm.city ? 
                 `${registerForm.province} ${registerForm.city}` : '请选择所在地区' }}
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <input 
            v-model="registerForm.address"
            placeholder="请输入详细地址"
            class="input"
          />
        </view>
        
        <button 
          class="register-btn" 
          @tap="handleRegister" 
          :loading="loading"
        >注册</button>
        
        <view class="login-link">
          <text>已有账号？</text>
          <text class="link" @tap="goToLogin">立即登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { merchantRegister } from '@/api/user'

// 注册表单数据
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  companyName: '',
  businessLicense: '',
  contactName: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  address: ''
})

// 加载状态
const loading = ref(false)

// 地区选择变化
const onRegionChange = (e) => {
  const [province, city, district] = e.detail.value
  registerForm.value.province = province
  registerForm.value.city = city
  registerForm.value.district = district
}

// 跳转到登录页
const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/index'
  })
}

// 处理注册
const handleRegister = async () => {
  // 表单验证
  if (!registerForm.value.username) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  
  if (!registerForm.value.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  
  if (!registerForm.value.companyName) {
    uni.showToast({ title: '请输入商户名称', icon: 'none' })
    return
  }
  
  if (!registerForm.value.contactName || !registerForm.value.contactPhone) {
    uni.showToast({ title: '请完善联系人信息', icon: 'none' })
    return
  }
  
  if (!registerForm.value.province || !registerForm.value.city) {
    uni.showToast({ title: '请选择所在地区', icon: 'none' })
    return
  }
  
  loading.value = true
  
  try {
    const res = await merchantRegister({
      username: registerForm.value.username,
      password: registerForm.value.password,
      merchantInfo: {
        companyName: registerForm.value.companyName,
        businessLicense: registerForm.value.businessLicense,
        contactName: registerForm.value.contactName,
        contactPhone: registerForm.value.contactPhone,
        province: registerForm.value.province,
        city: registerForm.value.city,
        district: registerForm.value.district,
        address: registerForm.value.address
      }
    })
    
    if (res.code === 200) {
      uni.showToast({
        title: '注册成功',
        icon: 'success'
      })
      
      // 注册成功后延迟跳转到登录页
      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/login/index'
        })
      }, 1500)
    } else {
      uni.showToast({
        title: res.message || '注册失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('注册失败:', error)
    uni.showToast({
      title: '注册失败，请重试',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f2f5 0%, #e6f7ff 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 140rpx 40rpx 40rpx;
  box-sizing: border-box;
}

.register-box {
  width: 100%;
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.05);
  margin-top: -40rpx;
}

.register-header {
  text-align: center;
  margin-bottom: 60rpx;
  
  .title {
    font-size: 48rpx;
    font-weight: bold;
    color: #1890ff;
    display: block;
    margin-bottom: 20rpx;
  }
  
  .subtitle {
    font-size: 32rpx;
    color: #666;
  }
}

.register-form {
  .form-item {
    margin-bottom: 30rpx;
    
    .input {
      width: 100%;
      height: 88rpx;
      background: #f5f5f5;
      border-radius: 12rpx;
      padding: 0 30rpx;
      font-size: 28rpx;
      box-sizing: border-box;
    }
    
    &.address-item {
      display: block;
      
      .picker {
        width: 100%;
        height: 88rpx;
        background: #f5f5f5;
        border-radius: 12rpx;
        display: flex;
        align-items: center;
        padding: 0 30rpx;
        
        .picker-value {
          font-size: 28rpx;
          color: #333;
          
          &:empty::before {
            content: attr(placeholder);
            color: #999;
          }
        }
      }
    }
  }
  
  .register-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
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
  
  .login-link {
    text-align: center;
    margin-top: 30rpx;
    font-size: 28rpx;
    
    .link {
      color: #1890ff;
      margin-left: 10rpx;
    }
  }
}
</style> 