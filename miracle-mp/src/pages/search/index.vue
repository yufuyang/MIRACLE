<template>
  <view class="search-page">
    <view class="search-header">
      <view class="search-input-box">
        <input 
          type="text" 
          v-model="productName"
          placeholder="搜索商品"
          @confirm="onSearch"
          confirm-type="search"
          focus
        />
        <text class="cancel" @tap="onCancel">取消</text>
      </view>
    </view>

    <!-- 搜索结果列表 -->
    <scroll-view 
      scroll-y 
      class="search-results"
      @scrolltolower="loadMore"
      v-if="productName"
    >
      <view class="product-list">
        <view 
          class="product-item" 
          v-for="item in products" 
          :key="item.id"
          @tap="handleProductDetail(item.id)"
        >
          <view class="image-wrapper">
            <image :src="item.imageUrl" mode="aspectFill" class="product-image"></image>
          </view>
          <view class="product-info">
            <text class="product-name">{{ item.productName }}</text>
            <text class="product-desc">{{ item.description }}</text>
            <view class="stats-info">
              <view class="stat-item">
                <text class="iconfont icon-eye"></text>
                <text>浏览 {{ item.viewCount }}</text>
              </view>
              <view class="stat-item">
                <text class="iconfont icon-heart"></text>
                <text>意向 {{ item.intentionCount }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view class="loading-more" v-if="loading">加载中...</view>
      <view class="no-more" v-if="noMore">没有更多了</view>
      <view class="empty" v-if="!loading && products.length === 0">
        暂无相关商品
      </view>
    </scroll-view>

    <!-- 搜索历史 -->
    <view class="search-history" v-else>
      <view class="history-header">
        <text>搜索历史</text>
        <text class="clear" @tap="clearHistory">清空</text>
      </view>
      <view class="history-list">
        <view 
          class="history-item" 
          v-for="(item, index) in searchHistory" 
          :key="index"
          @tap="onHistoryTap(item)"
        >
          {{ item }}
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getProducts } from '../../api/index'

export default {
  data() {
    return {
      productName: '',
      products: [],
      page: 1,
      pageSize: 10,
      loading: false,
      noMore: false,
      searchHistory: []
    }
  },
  onLoad() {
    // 获取搜索历史
    this.searchHistory = uni.getStorageSync('searchHistory') || []
  },
  methods: {
    async onSearch() {
      if (!this.productName.trim()) return
      
      // 保存搜索历史
      if (!this.searchHistory.includes(this.productName)) {
        this.searchHistory.unshift(this.productName)
        if (this.searchHistory.length > 10) {
          this.searchHistory.pop()
        }
        uni.setStorageSync('searchHistory', this.searchHistory)
      }
      
      this.page = 1
      this.products = []
      this.noMore = false
      await this.loadData()
    },
    
    async loadData() {
      if (this.loading || this.noMore) return
      
      this.loading = true
      try {
        const params = {
          pageNum: this.page,
          pageSize: this.pageSize,
          productName: this.productName
        }
        
        const res = await getProducts(params)
        
        if (res.success && res.data) {
          if (this.page === 1) {
            this.products = res.data
          } else {
            this.products = [...this.products, ...res.data]
          }
          
          if (res.data.length < this.pageSize) {
            this.noMore = true
          }
          this.page++
        } else {
          this.noMore = true
        }
      } catch (error) {
        console.error('搜索失败:', error)
        uni.showToast({
          title: '搜索失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    loadMore() {
      this.loadData()
    },
    
    onCancel() {
      uni.navigateBack()
    },
    
    onHistoryTap(productName) {
      this.productName = productName
      this.onSearch()
    },
    
    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定清空搜索历史？',
        success: (res) => {
          if (res.confirm) {
            this.searchHistory = []
            uni.removeStorageSync('searchHistory')
          }
        }
      })
    },
    
    handleProductDetail(id) {
      uni.navigateTo({
        url: `/pages/merchant/product/detail?id=${id}`
      })
    }
  }
}
</script>

<style lang="scss">
.search-page {
  min-height: 100vh;
  background: #f8f8f8;
}

.search-header {
  background: #fff;
  padding: 20rpx;
  
  .search-input-box {
    display: flex;
    align-items: center;
    
    input {
      flex: 1;
      background: #f5f5f5;
      height: 72rpx;
      border-radius: 36rpx;
      padding: 0 30rpx;
      font-size: 28rpx;
      margin-right: 20rpx;
    }
    
    .cancel {
      font-size: 28rpx;
      color: #666;
    }
  }
}

.search-results {
  height: calc(100vh - 112rpx);
}

.product-list {
  padding: 20rpx;
}

.product-item {
  display: flex;
  background: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 12rpx;
  
  .image-wrapper {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    margin-right: 20rpx;
    
    .product-image {
      width: 100%;
      height: 100%;
      border-radius: 8rpx;
    }
  }
  
  .product-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    
    .product-name {
      font-size: 28rpx;
      color: #333;
      margin-bottom: 8rpx;
    }
    
    .product-desc {
      font-size: 24rpx;
      color: #999;
      margin-bottom: 12rpx;
    }
    
    .stats-info {
      display: flex;
      align-items: center;
      gap: 16rpx;
      
      .stat-item {
        display: flex;
        align-items: center;
        color: #666;
        font-size: 22rpx;
        
        .iconfont {
          font-size: 26rpx;
          margin-right: 4rpx;
          color: #999;
        }
      }
    }
  }
}

.search-history {
  padding: 30rpx 20rpx;
  
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    text {
      font-size: 28rpx;
      color: #333;
    }
    
    .clear {
      color: #999;
    }
  }
  
  .history-list {
    display: flex;
    flex-wrap: wrap;
    
    .history-item {
      padding: 12rpx 24rpx;
      background: #f5f5f5;
      border-radius: 26rpx;
      font-size: 24rpx;
      color: #666;
      margin: 0 20rpx 20rpx 0;
    }
  }
}

.loading-more, .no-more, .empty {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 24rpx;
}
</style> 