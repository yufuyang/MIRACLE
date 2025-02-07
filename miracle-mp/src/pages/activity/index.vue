<template>
  <view class="activity">
    <!-- 搜索栏 -->
    <view class="search">
      <input 
        type="text" 
        placeholder="搜索活动" 
        v-model="keyword"
        @confirm="onSearch"
      />
    </view>
    
    <!-- 活动列表 -->
    <view class="activity-list">
      <view class="activity-item" 
        v-for="item in activities" 
        :key="item.id"
        @tap="onActivityTap(item)"
      >
        <image :src="item.coverImage" mode="aspectFill" class="cover"></image>
        <view class="info">
          <text class="name">{{ item.title }}</text>
          <text class="desc">{{ item.description || '暂无描述' }}</text>
          <view class="time">
            <text class="icon">🕒</text>
            <text>{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</text>
          </view>
          <view class="company" v-if="item.companyName">
            <text class="name">{{ item.companyName }}</text>
          </view>
          <view class="stats">
            <text class="view">浏览 {{ item.viewCount }}</text>
            <text class="sign">报名 {{ item.registerCount }}</text>
          </view>
          <view class="status" :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore">
      <text @tap="loadMore">加载更多</text>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="activities.length === 0">
      <text>暂无活动</text>
    </view>
  </view>
</template>

<script>
import { getActivities } from '../../api/index'

export default {
  data() {
    return {
      keyword: '',
      activities: [],
      pageNum: 1,
      pageSize: 10,
      hasMore: true,
      loading: false
    }
  },
  onLoad() {
    this.loadData()
  },
  methods: {
    async loadData(append = false) {
      if (this.loading) return
      this.loading = true
      
      try {
        const res = await getActivities({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.keyword
        })
        console.log('活动列表返回:', res)
        
        const list = res.data || []
        if (append) {
          this.activities = [...this.activities, ...list]
        } else {
          this.activities = list
        }
        
        // 判断是否还有更多数据
        this.hasMore = list.length === this.pageSize
      } catch (error) {
        console.error('获取活动列表失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (!this.hasMore || this.loading) return
      this.pageNum++
      this.loadData(true)
    },
    onSearch() {
      this.pageNum = 1
      this.loadData()
    },
    onActivityTap(item) {
      uni.navigateTo({
        url: `/pages/activity/detail?id=${item.id}`
      })
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    getStatusClass(status) {
      const statusMap = {
        0: 'NOT_START',
        1: 'IN_PROGRESS',
        2: 'ENDED'
      }
      return statusMap[status] || ''
    },
    getStatusText(status) {
      const statusMap = {
        0: '未开始',
        1: '进行中',
        2: '已结束'
      }
      return statusMap[status] || '未知'
    }
  },
  // 下拉刷新
  onPullDownRefresh() {
    this.pageNum = 1
    this.loadData().then(() => {
      uni.stopPullDownRefresh()
    })
  },
  // 上拉加载
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMore()
    }
  }
}
</script>

<style>
.activity {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20rpx;
}

.search {
  padding: 20rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.search input {
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.activity-list {
  padding: 20rpx;
}

.activity-item {
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  position: relative;
}

.activity-item .cover {
  width: 100%;
  height: 300rpx;
}

.activity-item .info {
  padding: 20rpx;
}

.activity-item .name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.activity-item .desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
  display: block;
}

.activity-item .time {
  display: flex;
  align-items: center;
  font-size: 24rpx;
  color: #1890ff;
  margin-bottom: 20rpx;
}

.activity-item .time .icon {
  margin-right: 10rpx;
}

.activity-item .company {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.activity-item .company .logo {
  width: 40rpx;
  height: 40rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
}

.activity-item .company .name {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 0;
}

.activity-item .stats {
  display: flex;
  font-size: 24rpx;
  color: #999;
}

.activity-item .stats .view {
  margin-right: 20rpx;
}

.activity-item .status {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  padding: 6rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #fff;
}

.activity-item .status.NOT_START {
  background: #1890ff;
}

.activity-item .status.IN_PROGRESS {
  background: #52c41a;
}

.activity-item .status.ENDED {
  background: #999;
}

.load-more {
  text-align: center;
  padding: 20rpx;
  color: #999;
  font-size: 24rpx;
}

.empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}
</style> 