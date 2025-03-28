<template>
  <view class="business-page">
    <!-- Tab 切换 -->
    <view class="tab-bar">
      <view 
        class="tab-item" 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="{ active: currentTab === tab.key }"
        @tap="switchTab(tab.key)"
      >
        {{ tab.name }}
      </view>
    </view>
    
    <!-- 意向列表 -->
    <view class="list-content" v-if="currentTab === 'intention'">
      <view 
        class="list-item"
        v-for="item in intentionList"
        :key="item.id"
      >
        <view class="product-info">
          <image :src="item.productImage" mode="aspectFill" class="product-image"></image>
          <view class="info">
            <text class="name">{{ item.productName }}</text>
            <text class="company">{{ item.companyName }}</text>
            <text class="time">添加时间：{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
        <view class="action-btns">
          <button class="btn cancel" @tap="handleCancelIntention(item)">取消意向</button>
          <button class="btn contact" @tap="handleContact(item)">联系商家</button>
        </view>
      </view>
    </view>
    
    <!-- 合作列表 -->
    <view class="list-content" v-if="currentTab === 'cooperation'">
      <view 
        class="list-item"
        v-for="item in cooperationList"
        :key="item.id"
      >
        <view class="company-info">
          <image :src="item.companyLogo" mode="aspectFill" class="company-logo"></image>
          <view class="info">
            <text class="name">{{ item.companyName }}</text>
            <text class="status" :class="item.status">{{ getStatusText(item.status) }}</text>
            <text class="time">开始时间：{{ formatTime(item.startTime) }}</text>
          </view>
        </view>
        <view class="action-btns">
          <button class="btn detail" @tap="handleCooperationDetail(item)">查看详情</button>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view class="empty" v-if="getCurrentList.length === 0">
      <text>暂无{{ currentTab === 'intention' ? '意向' : '合作' }}记录</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { formatTime } from '@/utils/date'
import { getIntentionList, getCooperationList, cancelIntention } from '@/api/business'

// Tab 配置
const tabs = [
  { key: 'intention', name: '意向管理' },
  { key: 'cooperation', name: '合作管理' }
]

const currentTab = ref('intention')
const intentionList = ref([])
const cooperationList = ref([])

// 获取当前列表
const getCurrentList = computed(() => {
  return currentTab.value === 'intention' ? intentionList.value : cooperationList.value
})

// 切换 Tab
const switchTab = (tab) => {
  currentTab.value = tab
  loadData()
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待确认',
    'active': '合作中',
    'finished': '已结束',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

// 加载数据
const loadData = async () => {
  try {
    if (currentTab.value === 'intention') {
      // 加载意向列表
      const res = await getIntentionList()
      intentionList.value = res.data
    } else {
      // 加载合作列表
      const res = await getCooperationList()
      cooperationList.value = res.data
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  }
}

// 取消意向
const handleCancelIntention = (item) => {
  uni.showModal({
    title: '提示',
    content: '确定要取消该意向吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelIntention(item.id)
          uni.showToast({
            title: '已取消意向',
            icon: 'success'
          })
          loadData()
        } catch (error) {
          uni.showToast({
            title: '操作失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

// 联系商家
const handleContact = (item) => {
  // 实现联系商家逻辑
}

// 查看合作详情
const handleCooperationDetail = (item) => {
  uni.navigateTo({
    url: `/pages/merchant/cooperation/detail?id=${item.id}`
  })
}

// 页面加载时获取数据
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.business-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.tab-bar {
  display: flex;
  background: #fff;
  padding: 20rpx 40rpx;
  position: sticky;
  top: 0;
  z-index: 100;
  
  .tab-item {
    flex: 1;
    text-align: center;
    font-size: 28rpx;
    color: #666;
    position: relative;
    padding: 20rpx 0;
    
    &.active {
      color: #1890ff;
      font-weight: 500;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40rpx;
        height: 4rpx;
        background: #1890ff;
        border-radius: 2rpx;
      }
    }
  }
}

.list-content {
  padding: 20rpx;
  
  .list-item {
    background: #fff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    
    .product-info, .company-info {
      display: flex;
      margin-bottom: 20rpx;
      
      .product-image, .company-logo {
        width: 120rpx;
        height: 120rpx;
        border-radius: 8rpx;
        margin-right: 20rpx;
      }
      
      .info {
        flex: 1;
        
        .name {
          font-size: 28rpx;
          font-weight: 500;
          color: #333;
          margin-bottom: 8rpx;
          display: block;
        }
        
        .company, .status {
          font-size: 24rpx;
          color: #666;
          margin-bottom: 8rpx;
          display: block;
          
          &.active {
            color: #52c41a;
          }
          
          &.pending {
            color: #faad14;
          }
          
          &.finished {
            color: #999;
          }
          
          &.cancelled {
            color: #ff4d4f;
          }
        }
        
        .time {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
    
    .action-btns {
      display: flex;
      justify-content: flex-end;
      gap: 20rpx;
      
      .btn {
        min-width: 160rpx;
        height: 60rpx;
        line-height: 60rpx;
        font-size: 24rpx;
        margin: 0;
        padding: 0 30rpx;
        
        &.cancel {
          background: #fff;
          color: #ff4d4f;
          border: 1rpx solid #ff4d4f;
        }
        
        &.contact {
          background: #1890ff;
          color: #fff;
        }
        
        &.detail {
          background: #fff;
          color: #1890ff;
          border: 1rpx solid #1890ff;
        }
      }
    }
  }
}

.empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}
</style> 