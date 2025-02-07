<template>
  <view class="intention-list">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchForm.productName"
        placeholder="搜索产品名称" 
        @confirm="handleSearch"
      />
      <button class="search-btn" @tap="handleSearch">搜索</button>
    </view>

    <!-- 意向列表 -->
    <view class="list-content">
      <view 
        class="intention-item" 
        v-for="item in intentionList" 
        :key="item.id"
      >
        <view class="product-info">
          <image :src="item.productLogo || defaultImage" mode="aspectFill" class="product-image" />
          <view class="info">
            <text class="name">{{ item.productName }}</text>
            <text class="company">{{ item.companyName }}</text>
          </view>
        </view>
        
        <view class="intention-info">
          <view class="info-item">
            <text class="label">联系人：</text>
            <text class="value">{{ item.contactName }}</text>
          </view>
          <view class="info-item">
            <text class="label">联系电话：</text>
            <text class="value">{{ item.contactPhone }}</text>
          </view>
        </view>

        <view class="footer">
          <text class="time">{{ formatTime(item.createTime) }}</text>
          <view class="action">
            <button 
              v-if="!item.status"
              class="cancel-btn" 
              @tap.stop="handleCancel(item)"
            >
              取消意向
            </button>
            <text 
              v-if="item.status" 
              class="status" 
              :class="'status-' + item.status"
            >
              {{ getStatusText(item.status) }}
            </text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view class="loading-more" v-if="loading">加载中...</view>
    <view class="no-more" v-if="!loading && !hasMore">没有更多了</view>
    <view class="empty" v-if="!loading && intentionList.length === 0">暂无意向数据</view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getMerchantIntentionList, cancelIntention } from '../../../api/merchant'
import { formatTime } from '../../../utils/date'

const defaultImage = '/static/images/default-product.png'

// 数据定义
const intentionList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const searchForm = ref({
  productName: '',
  pageNum: 1,
  pageSize: 10
})

// 获取意向列表
const fetchIntentionList = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true
  
  try {
    const res = await getMerchantIntentionList(searchForm.value)
    if (res.code === 200) {
      // 直接使用 data 数组
      const list = res.data || []
      const total = res.total || 0
      
      if (isLoadMore) {
        intentionList.value = [...intentionList.value, ...list]
      } else {
        intentionList.value = list
      }
      
      // 判断是否还有更多数据
      hasMore.value = intentionList.value.length < total
    } else {
      // 如果没有数据，设置为空数组
      intentionList.value = []
      hasMore.value = false
    }
  } catch (error) {
    console.error('获取意向列表失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
    // 发生错误时也要清空数据
    intentionList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  intentionList.value = [] // 清空列表
  hasMore.value = true // 重置加载状态
  fetchIntentionList()
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '已合作',
    2: '已拒绝',
    3: '已取消'
  }
  return statusMap[status] || ''
}

// 取消意向
const handleCancel = async (item) => {
  uni.showModal({
    title: '提示',
    content: '确定要取消该意向吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const res = await cancelIntention(item.productId)
          if (res.code === 200) {
            uni.showToast({
              title: '取消成功',
              icon: 'success'
            })
            // 刷新列表
            searchForm.value.pageNum = 1
            intentionList.value = []
            hasMore.value = true
            fetchIntentionList()
          } else {
            uni.showToast({
              title: res.errMessage || '取消失败',
              icon: 'none'
            })
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

// 下拉刷新
onPullDownRefresh(() => {
  searchForm.value.pageNum = 1
  intentionList.value = [] // 清空列表
  hasMore.value = true // 重置加载状态
  fetchIntentionList().finally(() => {
    uni.stopPullDownRefresh()
  })
})

// 上拉加载
onReachBottom(() => {
  if (!hasMore.value || loading.value) return
  searchForm.value.pageNum++
  fetchIntentionList(true)
})

onMounted(() => {
  fetchIntentionList()
})
</script>

<style lang="scss" scoped>
.intention-list {
  min-height: 100vh;
  background: #f5f5f5;

  .search-bar {
    display: flex;
    align-items: center;
    padding: 20rpx;
    background: #fff;
    position: sticky;
    top: 0;
    z-index: 100;

    input {
      flex: 1;
      height: 72rpx;
      background: #f5f5f5;
      border-radius: 36rpx;
      padding: 0 30rpx;
      font-size: 28rpx;
    }

    .search-btn {
      margin-left: 20rpx;
      height: 72rpx;
      line-height: 72rpx;
      padding: 0 30rpx;
      border-radius: 36rpx;
      font-size: 28rpx;
      background: #1890ff;
      color: #fff;
    }
  }

  .list-content {
    padding: 20rpx;

    .intention-item {
      background: #fff;
      border-radius: 12rpx;
      padding: 20rpx;
      margin-bottom: 20rpx;

      .product-info {
        display: flex;
        margin-bottom: 20rpx;
        padding-bottom: 20rpx;
        border-bottom: 1rpx solid #eee;

        .product-image {
          width: 160rpx;
          height: 160rpx;
          border-radius: 8rpx;
          margin-right: 20rpx;
        }

        .info {
          flex: 1;

          .name {
            font-size: 32rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 12rpx;
            display: block;
          }

          .company {
            font-size: 28rpx;
            color: #666;
            margin-bottom: 12rpx;
            display: block;
          }
        }
      }

      .intention-info {
        margin-bottom: 20rpx;
        padding-bottom: 20rpx;
        border-bottom: 1rpx solid #eee;

        .info-item {
          display: flex;
          margin-bottom: 12rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            width: 140rpx;
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

      .footer {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .time {
          font-size: 24rpx;
          color: #999;
        }

        .action {
          display: flex;
          align-items: center;
          gap: 20rpx;

          .cancel-btn {
            margin: 0;
            padding: 0 20rpx;
            height: 48rpx;
            line-height: 46rpx;
            font-size: 24rpx;
            background: #fff;
            color: #ff4d4f;
            border: 1rpx solid #ff4d4f;
            border-radius: 24rpx;

            &:active {
              opacity: 0.8;
            }
          }

          .status {
            font-size: 24rpx;
            
            &.status-1 { color: #52c41a; }
            &.status-2 { color: #ff4d4f; }
            &.status-3 { color: #999; }
          }
        }
      }
    }
  }

  .loading-more, .no-more, .empty {
    text-align: center;
    padding: 30rpx;
    color: #999;
    font-size: 28rpx;
  }
}
</style> 