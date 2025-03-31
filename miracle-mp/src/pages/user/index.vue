<template>
  <view class="user-page">
    <template v-if="userStore.token">
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="avatar-wrap">
          <image 
            src="/static/images/img.png" 
            mode="aspectFill" 
            class="avatar"
          />
        </view>
        <view class="info">
          <text class="name">{{ userStore.userInfo?.username }}</text>
        </view>
      </view>
      
      <!-- 页面标题 -->
      <view class="page-title">商户资料</view>
      
      <!-- 基本信息 -->
      <view class="section">
        <view class="section-title">基本信息</view>
        <view class="info-item">
          <text class="label">商户名称</text>
          <text class="value">{{ merchantInfo.merchantName }}</text>
        </view>
        <view class="info-item">
          <text class="label">联系人</text>
          <text class="value">{{ merchantInfo.contactName }}</text>
        </view>
        <view class="info-item">
          <text class="label">联系电话</text>
          <text class="value">{{ merchantInfo.contactPhone }}</text>
        </view>
        <view class="info-item">
          <text class="label">营业执照号</text>
          <text class="value">{{ merchantInfo.licenseNo || '-' }}</text>
        </view>
        <view class="info-item">
          <text class="label">商户简介</text>
          <text class="value">{{ merchantInfo.merchantDesc || '-' }}</text>
        </view>
      </view>
      
      <!-- 地址信息 -->
      <view class="section">
        <view class="section-title">地址信息</view>
        <view class="info-item">
          <text class="label">所在地区</text>
          <text class="value">{{ merchantInfo.province }} {{ merchantInfo.city }}</text>
        </view>
        <view class="info-item">
          <text class="label">详细地址</text>
          <text class="value">{{ merchantInfo.address }}</text>
        </view>
      </view>
      
      <!-- 底部按钮组 -->
      <view class="footer-btns">
        <button class="btn edit-btn" @tap="handleEdit">编辑资料</button>
        <button class="btn logout-btn" @tap="handleLogout">退出登录</button>
      </view>
    </template>
    <template v-else>
      <view class="login-container">
        <text class="login-tip">登录后可以查看更多信息</text>
        <view class="btn-group">
          <button class="btn login-btn" @tap="goToLogin">登录</button>
          <button class="btn register-btn" @tap="goToRegister">注册</button>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '../../store/user'
import { getMerchantBase } from '../../api/merchant'

const userStore = useUserStore()
const merchantInfo = ref({})

// 获取商户基本信息
const fetchMerchantInfo = async () => {
  console.log('开始获取商户信息')
  try {
    const res = await getMerchantBase()
    console.log('获取商户信息结果:', res)
    if (res.code === 200) {
      merchantInfo.value = res.data
    }
  } catch (error) {
    console.error('获取商户信息失败:', error)
    uni.showToast({
      title: '获取商户信息失败',
      icon: 'none'
    })
  }
}

// 编辑资料
const handleEdit = () => {
  uni.navigateTo({
    url: '/pages/merchant/profile/edit?id=' + merchantInfo.value.id
  })
}

const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/index'
  })
}

const goToRegister = () => {
  uni.navigateTo({
    url: '/pages/register/index'
  })
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    }
  })
}

// 监听 token 变化
watch(() => userStore.token, (newToken) => {
  console.log('token changed:', newToken)
  if (newToken) {
    fetchMerchantInfo()
  }
})

// 每次显示页面时获取最新数据
onShow(() => {
  if (userStore.token) {
    fetchMerchantInfo()
  }
})

// 初始化
onMounted(() => {
  console.log('页面加载，token:', userStore.token)
  if (userStore.token) {
    fetchMerchantInfo()
  }
})
</script>

<style lang="scss" scoped>
.user-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.user-card {
  background-color: #fff;
  padding: 32rpx;
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;

  .avatar-wrap {
    margin-right: 24rpx;
    
    .avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 60rpx;
    }
  }

  .info {
    flex: 1;

    .name {
      font-size: 36rpx;
      font-weight: 500;
      color: #333;
      display: block;
    }
  }
}

.page-title {
  padding: 32rpx;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.section {
  background-color: #fff;
  padding: 32rpx;
  margin-bottom: 32rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 24rpx;
  }

  .info-item {
    display: flex;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .label {
      width: 160rpx;
      font-size: 28rpx;
      color: #666;
    }

    .value {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
  }
}

.footer-btns {
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;

  .btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    text-align: center;
    border-radius: 44rpx;
    font-size: 32rpx;

    &.edit-btn {
      background: #1890ff;
      color: #fff;
    }

    &.logout-btn {
      background: #fff;
      color: #ff4d4f;
      border: 1px solid #ff4d4f;
    }

    &:active {
      opacity: 0.8;
    }
  }
}

.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 40vh;
  
  .login-tip {
    font-size: 28rpx;
    color: #999;
    margin-bottom: 40rpx;
  }
  
  .btn-group {
    display: flex;
    gap: 30rpx;
  }
}

.btn {
  width: 240rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 28rpx;
  
  &.login-btn {
    background: #1890ff;
    color: #fff;
  }
  
  &.register-btn {
    background: #fff;
    color: #1890ff;
    border: 1px solid #1890ff;
  }
}
</style> 