<template>
  <view class="order-list">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchForm.orderNo"
        placeholder="搜索订单编号" 
        @confirm="handleSearch"
      />
      <button class="search-btn" @tap="handleSearch">搜索</button>
    </view>

    <!-- 创建订单按钮 -->
    <view class="create-btn-wrap">
      <button class="create-btn" @tap="handleCreate">创建订单</button>
    </view>

    <!-- 订单列表 -->
    <view class="order-items">
      <view 
        v-for="item in orderList" 
        :key="item.id"
        class="order-item"
        @tap="handleOrderDetail(item.id)"
      >
        <view class="order-header">
          <text class="order-no">订单号：{{ item.orderNo }}</text>
          <text class="order-status" :class="'status-' + item.status">{{ getStatusText(item.status) }}</text>
        </view>
        
        <view class="order-content">
          <image 
            :src="item.productImage" 
            mode="aspectFill" 
            class="product-image"
          />
          <view class="order-info">
            <text class="product-name">{{ item.productName }}</text>
            <text class="company-name">{{ item.companyName }}</text>
            <view class="order-price">
              <text class="price">¥{{ item.totalAmount }}</text>
              <text class="time">{{ item.createTime }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-tip" v-if="orderList.length === 0">
      暂无订单数据
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getMerchantOrderList } from '@/api/merchant'
import { formatTime } from '@/utils/date'

const defaultImage = '/static/images/default-company.png'

// 数据定义
const orderList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const searchForm = ref({
  orderNo: '',
  pageNum: 1,
  pageSize: 10
})

// 获取订单列表
const fetchOrderList = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true
  
  try {
    const res = await getMerchantOrderList(searchForm.value)
    if (res.code === 200) {
      const list = res.data || []
      const total = res.total || 0
      
      if (isLoadMore) {
        orderList.value = [...orderList.value, ...list]
      } else {
        orderList.value = list
      }
      
      hasMore.value = orderList.value.length < total
    } else {
      orderList.value = []
      hasMore.value = false
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
    orderList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  orderList.value = []
  hasMore.value = true
  fetchOrderList()
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 创建订单
const handleCreate = () => {
  uni.navigateTo({
    url: '/pages/merchant/order/create'
  })
}

// 查看订单详情
const handleOrderDetail = (orderId) => {
  uni.navigateTo({
    url: `/pages/merchant/order/detail?id=${orderId}`
  })
}

// 下拉刷新
onPullDownRefresh(() => {
  searchForm.value.pageNum = 1
  orderList.value = []
  hasMore.value = true
  fetchOrderList().finally(() => {
    uni.stopPullDownRefresh()
  })
})

// 上拉加载
onReachBottom(() => {
  if (!hasMore.value || loading.value) return
  searchForm.value.pageNum++
  fetchOrderList(true)
})

// 页面显示时加载数据
onShow(() => {
  searchForm.value.pageNum = 1
  orderList.value = []
  hasMore.value = true
  fetchOrderList()
})
</script>

<style lang="scss" scoped>
.order-list {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;

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

  .create-btn-wrap {
    padding: 20rpx;
    background: #fff;
    border-top: 1rpx solid #eee;

    .create-btn {
      height: 80rpx;
      line-height: 80rpx;
      background: #1890ff;
      color: #fff;
      border-radius: 40rpx;
      font-size: 32rpx;
    }
  }

  .order-items {
    .order-item {
      background: #fff;
      border-radius: 12rpx;
      margin-bottom: 20rpx;
      overflow: hidden;

      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20rpx;
        border-bottom: 1rpx solid #eee;

        .order-no {
          font-size: 24rpx;
          color: #666;
        }

        .order-status {
          font-size: 24rpx;
          
          &.status-1 { color: #1890ff; }  // 待发货
          &.status-2 { color: #52c41a; }  // 已发货
          &.status-3 { color: #52c41a; }  // 已完成
          &.status-4 { color: #ff4d4f; }  // 已取消
        }
      }

      .order-content {
        display: flex;
        padding: 20rpx;

        .product-image {
          width: 160rpx;
          height: 160rpx;
          border-radius: 8rpx;
          margin-right: 20rpx;
        }

        .order-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .product-name {
            font-size: 28rpx;
            color: #333;
            font-weight: bold;
            margin-bottom: 8rpx;
          }

          .company-name {
            font-size: 24rpx;
            color: #666;
            margin-bottom: 8rpx;
          }

          .order-price {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .price {
              font-size: 32rpx;
              color: #ff4d4f;
              font-weight: bold;
            }

            .time {
              font-size: 24rpx;
              color: #999;
            }
          }
        }
      }
    }
  }

  .empty-tip {
    text-align: center;
    padding: 60rpx 0;
    color: #999;
    font-size: 28rpx;
  }
}
</style> 