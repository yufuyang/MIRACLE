<template>
  <view class="activity-detail">
    <!-- 活动封面 -->
    <image 
      :src="activityInfo.coverImage || defaultImage" 
      mode="aspectFill" 
      class="cover-image"
    />

    <!-- 活动信息 -->
    <view class="info-card">
      <text class="title">{{ activityInfo.title }}</text>
      <view class="time">
        <text class="label">活动时间：</text>
        <text class="value">{{ formatTime(activityInfo.startTime) }} - {{ formatTime(activityInfo.endTime) }}</text>
      </view>
      <view class="stats">
        <text class="stat-item">浏览 {{ activityInfo.viewCount }}</text>
        <text class="stat-item">报名 {{ activityInfo.registerCount }}</text>
      </view>
      <view class="status" :class="activityStatus.class">
        {{ activityStatus.text }}
      </view>
    </view>

    <!-- 活动详情 -->
    <view class="detail-card">
      <view class="card-title">活动详情</view>
      <view class="content">
        {{ activityInfo.description || '暂无详情' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { formatTime } from '@/utils/date'
import { getActivityDetail } from '@/api/activity'

const defaultImage = '/static/images/default-activity.png'
const activityInfo = ref({})

// 活动状态
const activityStatus = computed(() => {
  if (!activityInfo.value.status) return { text: '', class: '' }
  
  const statusMap = {
    1: { text: '未开始', class: 'not-started' },
    2: { text: '进行中', class: 'in-progress' },
    3: { text: '已结束', class: 'ended' }
  }
  
  return statusMap[activityInfo.value.status] || { text: '', class: '' }
})

// 获取活动详情
const loadActivityDetail = async (id) => {
  try {
    const res = await getActivityDetail(id)
    if (res.code === 200) {
      activityInfo.value = res.data
    }
  } catch (error) {
    console.error('获取活动详情失败:', error)
    uni.showToast({
      title: '获取活动详情失败',
      icon: 'none'
    })
  }
}

// 页面加载
onLoad((options) => {
  const id = options.id
  if (id) {
    loadActivityDetail(id)
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
  }
})
</script>

<style lang="scss" scoped>
.activity-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20rpx;
}

.cover-image {
  width: 100%;
  height: 400rpx;
  background: #f0f0f0;
}

.info-card {
  margin: 20rpx;
  padding: 30rpx;
  background: #fff;
  border-radius: 12rpx;
  position: relative;

  .title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
    display: block;
  }

  .time {
    margin-bottom: 16rpx;
    
    .label {
      font-size: 28rpx;
      color: #666;
    }
    
    .value {
      font-size: 28rpx;
      color: #333;
    }
  }

  .stats {
    display: flex;
    gap: 32rpx;

    .stat-item {
      font-size: 26rpx;
      color: #999;
    }
  }

  .status {
    position: absolute;
    top: 30rpx;
    right: 30rpx;
    padding: 8rpx 24rpx;
    border-radius: 24rpx;
    font-size: 24rpx;
    
    &.not-started {
      background: #e6f7ff;
      color: #1890ff;
    }
    
    &.in-progress {
      background: #f6ffed;
      color: #52c41a;
    }
    
    &.ended {
      background: #f5f5f5;
      color: #999;
    }
  }
}

.detail-card {
  margin: 20rpx;
  padding: 30rpx;
  background: #fff;
  border-radius: 12rpx;

  .card-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
    padding-left: 16rpx;
    border-left: 4rpx solid #1890ff;
  }

  .content {
    font-size: 28rpx;
    color: #666;
    line-height: 1.6;
  }
}
</style> 