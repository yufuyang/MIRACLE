<template>
  <view class="cooperation-list">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchForm.companyName"
        placeholder="搜索企业名称" 
        @confirm="handleSearch"
      />
      <button class="search-btn" @tap="handleSearch">搜索</button>
    </view>

    <!-- 合作列表 -->
    <view class="list-content">
      <view 
        class="cooperation-item" 
        v-for="item in cooperationList" 
        :key="item.id"
      >
        <view class="company-info">
          <image :src="item.companyLogo || defaultImage" mode="aspectFill" class="company-logo" />
          <view class="info">
            <text class="name">{{ item.companyName }}</text>
            <text class="time">合作时间：{{ formatTime(item.createTime) }}</text>
          </view>
        </view>

        <view class="cooperation-info">
          <view class="info-item">
            <text class="label">联系人：</text>
            <text class="value">{{ item.companyContactName }}</text>
          </view>
          <view class="info-item">
            <text class="label">联系电话：</text>
            <text class="value">{{ item.companyContactPhone }}</text>
          </view>
        </view>

        <view class="footer">
          <text class="time">{{ formatTime(item.createTime) }}</text>
          <view class="action">
            <template v-if="item.status === 0">
              <button 
                class="action-btn accept" 
                @tap.stop="handleCooperate(item)"
              >
                合作
              </button>
              <button 
                class="action-btn reject" 
                @tap.stop="handleReject(item)"
              >
                拒绝
              </button>
            </template>
            <text 
              v-else 
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
    <view class="empty" v-if="!loading && cooperationList.length === 0">暂无合作数据</view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { 
  getMerchantCooperationList, 
  acceptCooperation,
  rejectCooperation 
} from '../../../api/merchant'
import { formatTime } from '../../../utils/date'

const defaultImage = '/static/images/default-company.png'

// 数据定义
const cooperationList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const searchForm = ref({
  companyName: '',
  pageNum: 1,
  pageSize: 10
})

// 获取合作列表
const fetchCooperationList = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true
  
  try {
    const res = await getMerchantCooperationList(searchForm.value)
    if (res.code === 200) {
      const list = res.data || []
      const total = res.total || 0
      
      if (isLoadMore) {
        cooperationList.value = [...cooperationList.value, ...list]
      } else {
        cooperationList.value = list
      }
      
      hasMore.value = cooperationList.value.length < total
    } else {
      cooperationList.value = []
      hasMore.value = false
    }
  } catch (error) {
    console.error('获取合作列表失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
    cooperationList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  cooperationList.value = []
  hasMore.value = true
  fetchCooperationList()
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待处理',
    1: '已合作',
    2: '已拒绝',
    3: '已终止'
  }
  return statusMap[status] || '未知'
}

// 同意合作
const handleCooperate = async (item) => {
  uni.showModal({
    title: '提示',
    content: '确定要同意该合作吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const res = await acceptCooperation(item.companyId)
          if (res.code === 200) {
            uni.showToast({
              title: '操作成功',
              icon: 'success'
            })
            // 刷新列表
            searchForm.value.pageNum = 1
            cooperationList.value = []
            hasMore.value = true
            fetchCooperationList()
          } else {
            uni.showToast({
              title: res.errMessage || '操作失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('合作失败:', error)
          uni.showToast({
            title: '操作失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

// 拒绝合作
const handleReject = async (item) => {
  uni.showModal({
    title: '提示',
    content: '确定要拒绝该合作吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const res = await rejectCooperation(item.companyId)
          if (res.code === 200) {
            uni.showToast({
              title: '操作成功',
              icon: 'success'
            })
            // 刷新列表
            searchForm.value.pageNum = 1
            cooperationList.value = []
            hasMore.value = true
            fetchCooperationList()
          } else {
            uni.showToast({
              title: res.errMessage || '操作失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('拒绝失败:', error)
          uni.showToast({
            title: '操作失败',
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
  cooperationList.value = []
  hasMore.value = true
  fetchCooperationList().finally(() => {
    uni.stopPullDownRefresh()
  })
})

// 上拉加载
onReachBottom(() => {
  if (!hasMore.value || loading.value) return
  searchForm.value.pageNum++
  fetchCooperationList(true)
})

onMounted(() => {
  fetchCooperationList()
})
</script>

<style lang="scss" scoped>
.cooperation-list {
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

    .cooperation-item {
      background: #fff;
      border-radius: 12rpx;
      padding: 20rpx;
      margin-bottom: 20rpx;

      .company-info {
        display: flex;
        margin-bottom: 20rpx;
        padding-bottom: 20rpx;
        border-bottom: 1rpx solid #eee;

        .company-logo {
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

          .time {
            font-size: 28rpx;
            color: #666;
          }
        }
      }

      .cooperation-info {
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

          .action-btn {
            margin: 0;
            padding: 0 20rpx;
            height: 48rpx;
            line-height: 46rpx;
            font-size: 24rpx;
            background: #fff;
            border-radius: 24rpx;

            &.accept {
              color: #52c41a;
              border: 1rpx solid #52c41a;
            }

            &.reject {
              color: #ff4d4f;
              border: 1rpx solid #ff4d4f;
            }

            &:active {
              opacity: 0.8;
            }
          }

          .status {
            font-size: 24rpx;
            
            &.status-0 { color: #1890ff; }
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