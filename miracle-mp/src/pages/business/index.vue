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
        @tap="handleProductDetail(item)"
        v-for="item in intentionList"
        :key="item.id"
      >
        <view class="product-info">
          <image :src="item.productLogo" mode="aspectFill" class="product-image"></image>
          <view class="info">
            <text class="name">{{ item.productName }}</text>
            <text class="company">{{ item.companyName }}</text>
            <text class="contact">联系人：{{ item.contactName }} {{ item.contactPhone }}</text>
            <text class="time">添加时间：{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
        <view class="action-btns">
          <button class="btn cancel" @tap.stop="handleCancelIntention(item)">取消意向</button>
        </view>
      </view>
    </view>
    
    <!-- 合作列表 -->
    <view class="list-content" v-if="currentTab === 'cooperation'">
      <view 
        class="list-item"
        @tap="handleCompanyDetail(item)"
        v-for="item in cooperationList"
        :key="item.id"
      >
        <view class="company-info">
          <image :src="item.companyLogo" mode="aspectFill" class="company-logo"></image>
          <view class="info">
            <text class="name">{{ item.companyName }}</text>
            <text class="contact">联系人：{{ item.companyContactName }} {{ item.companyContactPhone }}</text>
            <text class="status" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</text>
            <text class="time">开始时间：{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
        <view class="action-btns">
          <button class="btn detail" @tap.stop="handleCooperationDetail(item)">查看详情</button>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { formatTime } from '@/utils/date'
import { 
  getMerchantIntentionList, 
  getMerchantCooperationList, 
  cancelIntention,
  acceptCooperation,
  rejectCooperation
} from '@/api/business'

const init = () => {
  console.log('初始化数据')
  currentTab.value = 'intention'
  pageNum.value = 1
  intentionList.value = []
  cooperationList.value = []
  loadData()
}

// 页面首次加载
onMounted(() => {
  console.log('页面mounted')
  init()
})

// 页面显示时触发
uni.$on('tabChange', () => {
  console.log('tab切换')
  init()
})

// 组件销毁时清理
onUnmounted(() => {
  uni.$off('tabChange')
})

// Tab 配置
const tabs = [
  { key: 'intention', name: '意向管理' },
  { key: 'cooperation', name: '合作管理' }
]

const currentTab = ref('intention')
const intentionList = ref([])
const cooperationList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)

// 获取当前列表
const getCurrentList = computed(() => {
  return currentTab.value === 'intention' ? (intentionList.value || []) : (cooperationList.value || [])
})

// 切换 Tab
const switchTab = (tab) => {
  currentTab.value = tab
  pageNum.value = 1
  if (tab === 'intention') {
    intentionList.value = []
  } else {
    cooperationList.value = []
  }
  loadData()
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '待确认'
    case 1:
      return '合作中'
    case 2:
      return '已结束'
    case 3:
      return '已取消'
    default:
      return '未知状态'
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 0:
      return 'pending'
    case 1:
      return 'active'
    case 2:
      return 'finished'
    case 3:
      return 'cancelled'
    default:
      return ''
  }
}

// 加载数据
const loadData = async () => {
  console.log('开始加载数据，当前tab:', currentTab.value)
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    if (currentTab.value === 'intention') {
      console.log('请求意向列表数据')
      const res = await getMerchantIntentionList(params)
      console.log('意向列表响应:', res)
      if (res.code === 200) {
        if (!intentionList.value) intentionList.value = []
        intentionList.value = pageNum.value === 1 
          ? res.data 
          : [...intentionList.value, ...res.data]
      }
    } else {
      const res = await getMerchantCooperationList(params)
      if (res.code === 200) {
        if (!cooperationList.value) cooperationList.value = []
        cooperationList.value = pageNum.value === 1 
          ? res.data 
          : [...cooperationList.value, ...res.data]
      }
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
const handleCancelIntention = async (item) => {
  uni.showModal({
    title: '提示',
    content: '确定要取消意向吗？',
    async success(res) {
      if (res.confirm) {
        try {
          const res = await cancelIntention(item.productId)
          if (res.code === 200) {
            uni.showToast({
              title: '取消成功',
              icon: 'success'
            })
            pageNum.value = 1
            loadData()
          }
        } catch (error) {
          console.error('取消意向失败:', error)
          uni.showToast({
            title: '取消失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

// 查看合作详情
const handleCooperationDetail = (item) => {
  // 实现查看合作详情的逻辑
  uni.showToast({
    title: '详情功能开发中',
    icon: 'none'
  })
}

// 处理合作
const handleCooperation = async (item, type) => {
  const action = type === 'accept' ? '同意' : '拒绝'
  uni.showModal({
    title: '提示',
    content: `确定要${action}合作吗？`,
    async success(res) {
      if (res.confirm) {
        try {
          const api = type === 'accept' ? acceptCooperation : rejectCooperation
          const res = await api(item.companyId)
          if (res.code === 200) {
            uni.showToast({
              title: `${action}成功`,
              icon: 'success'
            })
            pageNum.value = 1
            loadData()
          }
        } catch (error) {
          console.error(`${action}合作失败:`, error)
          uni.showToast({
            title: `${action}失败`,
            icon: 'none'
          })
        }
      }
    }
  })
}

// 上拉加载更多
const onReachBottom = () => {
  pageNum.value++
  loadData()
}

// 跳转到产品详情
const handleProductDetail = (item) => {
  uni.navigateTo({
    url: `/pages/merchant/product/detail?id=${item.productId}`
  })
}

// 跳转到公司详情
const handleCompanyDetail = (item) => {
  uni.navigateTo({
    url: `/pages/company/detail/index?id=${item.companyId}`
  })
}
</script>

<style lang="scss" scoped>
.business-page {
  min-height: 100vh;
  background: #f5f5f5;
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
        
        .company {
          font-size: 24rpx;
          color: #666;
          margin-bottom: 8rpx;
          display: block;
        }
        
        .contact {
          font-size: 24rpx;
          color: #666;
          margin-bottom: 8rpx;
          display: block;
        }
        
        .status {
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
        
        &.detail {
          background: #fff;
          color: #1890ff;
          border: 1rpx solid #1890ff;
        }
      }
    }

    &:active {
      opacity: 0.8;
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