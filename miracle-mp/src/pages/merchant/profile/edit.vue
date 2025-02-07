<template>
  <view class="edit-page">
    <!-- 基本信息卡片 -->
    <view class="info-card">
      <view class="card-title">基本信息</view>
      <view class="form-item">
        <text class="label">商户名称</text>
        <input 
          class="input"
          v-model="formData.merchantName"
          placeholder="请输入商户名称"
        />
      </view>
      <view class="form-item">
        <text class="label">联系人</text>
        <input 
          class="input"
          v-model="formData.contactName"
          placeholder="请输入联系人姓名"
        />
      </view>
      <view class="form-item">
        <text class="label">联系电话</text>
        <input 
          class="input"
          v-model="formData.contactPhone"
          type="number"
          maxlength="11"
          placeholder="请输入联系电话"
        />
      </view>
      <view class="form-item">
        <text class="label">营业执照号</text>
        <input 
          class="input"
          v-model="formData.licenseNo"
          placeholder="请输入营业执照号"
        />
      </view>
      <view class="form-item">
        <text class="label">商户简介</text>
        <textarea 
          class="textarea"
          v-model="formData.merchantDesc"
          placeholder="请输入商户简介"
        />
      </view>
    </view>

    <!-- 地址信息卡片 -->
    <view class="info-card">
      <view class="card-title">地址信息</view>
      <view class="form-item">
        <text class="label">所在地区</text>
        <picker 
          mode="region" 
          @change="handleRegionChange"
          :value="[formData.province, formData.city]"
        >
          <view class="picker">
            {{ formData.province || '请选择' }} {{ formData.city || '' }}
          </view>
        </picker>
      </view>
      <view class="form-item">
        <text class="label">详细地址</text>
        <textarea 
          class="textarea"
          v-model="formData.address"
          placeholder="请输入详细地址"
        />
      </view>
    </view>

    <!-- 提交按钮 -->
    <button 
      class="submit-btn" 
      @tap="handleSubmit"
      :loading="loading"
    >
      保存
    </button>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMerchantBase, updateMerchantBase } from '../../../api/merchant'

const loading = ref(false)
const formData = ref({
  merchantName: '',
  contactName: '',
  contactPhone: '',
  licenseNo: '',
  merchantDesc: '',
  province: '',
  city: '',
  address: ''
})

// 获取商户信息
const fetchMerchantInfo = async () => {
  try {
    const res = await getMerchantBase()
    if (res.code === 200) {
      Object.assign(formData.value, res.data)
    }
  } catch (error) {
    console.error('获取商户信息失败:', error)
    uni.showToast({
      title: '获取信息失败',
      icon: 'none'
    })
  }
}

// 处理地区选择
const handleRegionChange = (e) => {
  const [province, city] = e.detail.value
  formData.value.province = province
  formData.value.city = city
}

// 表单验证
const validateForm = () => {
  if (!formData.value.merchantName) {
    uni.showToast({
      title: '请输入商户名称',
      icon: 'none'
    })
    return false
  }
  if (!formData.value.contactName) {
    uni.showToast({
      title: '请输入联系人',
      icon: 'none'
    })
    return false
  }
  if (!formData.value.contactPhone) {
    uni.showToast({
      title: '请输入联系电话',
      icon: 'none'
    })
    return false
  }
  if (!/^1\d{10}$/.test(formData.value.contactPhone)) {
    uni.showToast({
      title: '请输入正确的手机号',
      icon: 'none'
    })
    return false
  }
  if (!formData.value.licenseNo) {
    uni.showToast({
      title: '请输入营业执照号',
      icon: 'none'
    })
    return false
  }
  if (!formData.value.province || !formData.value.city) {
    uni.showToast({
      title: '请选择所在地区',
      icon: 'none'
    })
    return false
  }
  if (!formData.value.address) {
    uni.showToast({
      title: '请输入详细地址',
      icon: 'none'
    })
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  try {
    const res = await updateMerchantBase(formData.value)
    if (res.code === 200) {
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      
      // 返回上一页并传递需要刷新的标记
      uni.navigateBack({
        delta: 1,
        success: () => {
          // 使用事件通道发送刷新消息
          uni.$emit('merchantInfoUpdated')
        }
      })
    } else {
      uni.showToast({
        title: res.errMessage || '保存失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('保存失败:', error)
    uni.showToast({
      title: '保存失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchMerchantInfo()
})
</script>

<style lang="scss" scoped>
.edit-page {
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
      margin-bottom: 24rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        display: block;
        font-size: 28rpx;
        color: #666;
        margin-bottom: 16rpx;
      }

      .input {
        width: 100%;
        height: 80rpx;
        background: #f8f8f8;
        border-radius: 8rpx;
        padding: 0 24rpx;
        box-sizing: border-box;
        font-size: 28rpx;
      }

      .textarea {
        width: 100%;
        height: 160rpx;
        background: #f8f8f8;
        border-radius: 8rpx;
        padding: 20rpx 24rpx;
        box-sizing: border-box;
        font-size: 28rpx;
      }

      .picker {
        width: 100%;
        height: 80rpx;
        line-height: 80rpx;
        background: #f8f8f8;
        border-radius: 8rpx;
        padding: 0 24rpx;
        box-sizing: border-box;
        font-size: 28rpx;
        color: #333;
      }
    }
  }

  .submit-btn {
    margin: 40rpx 30rpx;
    height: 88rpx;
    line-height: 88rpx;
    background: #1890ff;
    color: #fff;
    border-radius: 8rpx;
    font-size: 32rpx;

    &[loading] {
      opacity: 0.7;
    }
  }
}
</style> 