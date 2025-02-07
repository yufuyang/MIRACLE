<template>
  <view class="profile-page" ref="profile">
    <!-- 基本信息卡片 -->
    <view class="info-card">
      <view class="card-title">基本信息</view>
      <view class="form-item">
        <text class="label">商户名称</text>
        <text class="value">{{ merchantInfo.merchantName }}</text>
      </view>
      <view class="form-item">
        <text class="label">联系人</text>
        <text class="value">{{ merchantInfo.contactName }}</text>
      </view>
      <view class="form-item">
        <text class="label">联系电话</text>
        <text class="value">{{ merchantInfo.contactPhone }}</text>
      </view>
      <view class="form-item">
        <text class="label">营业执照号</text>
        <text class="value">{{ merchantInfo.licenseNo }}</text>
      </view>
      <view class="form-item">
        <text class="label">商户简介</text>
        <text class="value">{{ merchantInfo.merchantDesc }}</text>
      </view>
    </view>

    <!-- 地址信息卡片 -->
    <view class="info-card">
      <view class="card-title">地址信息</view>
      <view class="form-item">
        <text class="label">所在地区</text>
        <text class="value">{{ merchantInfo.province }} {{ merchantInfo.city }}</text>
      </view>
      <view class="form-item">
        <text class="label">详细地址</text>
        <text class="value">{{ merchantInfo.address }}</text>
      </view>
    </view>

    <!-- 编辑按钮 -->
    <button class="edit-btn" @tap="handleEdit">编辑资料</button>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getMerchantBase } from '../../../api/merchant'

const merchantInfo = ref({})

// 获取商户信息
const fetchMerchantInfo = async () => {
  try {
    const res = await getMerchantBase()
    if (res.code === 200) {
      merchantInfo.value = res.data
    }
  } catch (error) {
    console.error('获取商户信息失败:', error)
    uni.showToast({
      title: '获取信息失败',
      icon: 'none'
    })
  }
}

// 编辑资料
const handleEdit = () => {
  uni.navigateTo({
    url: '/pages/merchant/profile/edit'
  })
}

// 监听更新事件
const handleInfoUpdated = () => {
  fetchMerchantInfo()
}

onMounted(() => {
  fetchMerchantInfo()
  // 添加事件监听
  uni.$on('merchantInfoUpdated', handleInfoUpdated)
})

onUnmounted(() => {
  // 移除事件监听
  uni.$off('merchantInfoUpdated', handleInfoUpdated)
})
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;

  .info-card {
    background: #fff;
    border-radius: 12rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;

    .card-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 30rpx;
    }

    .form-item {
      display: flex;
      margin-bottom: 24rpx;

      &:last-child {
        margin-bottom: 0;
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

  .edit-btn {
    margin: 40rpx 30rpx;
    height: 88rpx;
    line-height: 88rpx;
    background: #1890ff;
    color: #fff;
    border-radius: 8rpx;
    font-size: 32rpx;
  }
}
</style> 