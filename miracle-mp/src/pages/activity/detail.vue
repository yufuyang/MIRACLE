<template>
  <view class="activity-detail">
    <!-- 活动封面 -->
    <image 
      :src="activityInfo.coverImage || defaultImage" 
      mode="aspectFill" 
      class="cover-image"
      @tap="previewImage"
    />

    <!-- 活动基本信息 -->
    <view class="basic-info">
      <text class="title">{{ activityInfo.title }}</text>
      <text class="time">活动时间：{{ formatTime(activityInfo.startTime) }} - {{ formatTime(activityInfo.endTime) }}</text>
      <view class="stats">
        <text class="stat">浏览 {{ activityInfo.viewCount }}</text>
        <text class="stat">报名 {{ activityInfo.registerCount }}</text>
        <text class="registered" v-if="activityInfo.status === 1 && isRegistered">已报名</text>
      </view>
      <view class="status" :class="activityStatus.class">
        {{ activityStatus.text }}
      </view>
    </view>

    <!-- 活动详情 -->
    <view class="detail-section">
      <view class="section-title">活动详情</view>
      <view class="content">{{ activityInfo.description || '暂无详情' }}</view>
    </view>

    <!-- 底部按钮 -->
    <view class="footer" v-if="activityInfo.status === 1">
      <button 
        class="register-btn" 
        :class="{ 'cancel': isRegistered }"
        @tap="handleRegistration"
      >
        {{ isRegistered ? '取消报名' : '立即报名' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getActivityDetail } from '@/api/activity'
import { checkRegistration, registerActivity, cancelRegistration } from '@/api/merchant'

export default {
  data() {
    return {
      activityInfo: {},
      defaultImage: '/static/images/default-activity.png',
      isRegistered: false
    }
  },
  computed: {
    activityStatus() {
      if (this.activityInfo.status === undefined) return { text: '', class: '' }
      
      const statusMap = {
        0: { text: '未开始', class: 'not-started' },
        1: { text: '进行中', class: 'in-progress' },
        2: { text: '已结束', class: 'ended' }
      }
      
      return statusMap[this.activityInfo.status] || { text: '', class: '' }
    }
  },
  methods: {
    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return date.toLocaleDateString()
    },
    previewImage() {
      if (!this.activityInfo.coverImage) return
      uni.previewImage({
        urls: [this.activityInfo.coverImage],
        current: 0
      })
    },
    async checkRegistrationStatus(id) {
      try {
        const res = await checkRegistration(id)
        console.log('报名状态检查结果:', res)
        if (res.code === 200) {
          this.isRegistered = res.data
        }
      } catch (error) {
        console.error('检查报名状态失败:', error)
      }
    },
    async loadActivityDetail(id) {
      console.log('开始加载活动详情, id:', id)
      try {
        const res = await getActivityDetail(id)
        console.log('活动详情返回:', res)
        if (res.code === 200) {
          this.activityInfo = res.data
          console.log('处理后的活动信息:', this.activityInfo)
          
          if (this.activityInfo.status === 1) {
            this.checkRegistrationStatus(id)
          }
        }
      } catch (error) {
        console.error('获取活动详情失败:', error)
        uni.showToast({
          title: '获取活动详情失败',
          icon: 'none'
        })
      }
    },
    // 处理报名/取消报名
    async handleRegistration() {
      try {
        if (this.isRegistered) {
          // 取消报名
          const res = await cancelRegistration(this.activityInfo.id)
          console.log('取消报名结果:', res)
          if (res.code === 200) {
            uni.showToast({
              title: '已取消报名',
              icon: 'success'
            })
            this.isRegistered = false
            // 刷新活动信息
            this.loadActivityDetail(this.activityInfo.id)
          }
        } else {
          // 报名
          const res = await registerActivity(this.activityInfo.id)
          console.log('报名结果:', res)
          if (res.code === 200) {
            uni.showToast({
              title: '报名成功',
              icon: 'success'
            })
            this.isRegistered = true
            // 刷新活动信息
            this.loadActivityDetail(this.activityInfo.id)
          }
        }
      } catch (error) {
        console.error('操作失败:', error)
        uni.showToast({
          title: this.isRegistered ? '取消报名失败' : '报名失败',
          icon: 'none'
        })
      }
    }
  },
  onLoad(options) {
    console.log('页面加载，参数:', options)
    const id = Number(options.id)
    if (id) {
      this.loadActivityDetail(id)
    } else {
      uni.showToast({
        title: '参数错误',
        icon: 'none'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.activity-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.cover-image {
  width: 100%;
  height: 400rpx;
  background: #f0f0f0;
}

.basic-info {
  margin: 20rpx;
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  position: relative;

  .title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    display: block;
    margin-bottom: 16rpx;
    padding-right: 120rpx; // 为状态标签留出空间
  }

  .time {
    font-size: 26rpx;
    color: #666;
    display: block;
    margin-bottom: 16rpx;
  }

  .stats {
    display: flex;
    gap: 32rpx;
    align-items: center;

    .stat {
      font-size: 24rpx;
      color: #999;
    }

    .registered {
      font-size: 24rpx;
      color: #52c41a;
      background: #f6ffed;
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
    }
  }

  .status {
    position: absolute;
    top: 20rpx;
    right: 20rpx;
    padding: 6rpx 20rpx;
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

.detail-section {
  margin: 20rpx;
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;

  .section-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 16rpx;
    padding-left: 16rpx;
    border-left: 4rpx solid #1890ff;
  }

  .content {
    font-size: 26rpx;
    color: #666;
    line-height: 1.6;
    word-break: break-all;
  }
}

.footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx;
  background: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);

  .register-btn {
    width: 100%;
    height: 80rpx;
    line-height: 80rpx;
    background: #1890ff;
    color: #fff;
    font-size: 28rpx;
    border-radius: 40rpx;
    border: none;

    &.cancel {
      background: #f5f5f5;
      color: #666;
    }

    &:active {
      opacity: 0.8;
    }
  }
}
</style> 