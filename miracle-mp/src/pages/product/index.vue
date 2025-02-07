<template>
  <view class="product">
    <!-- 搜索栏 -->
    <view class="search">
      <input 
        type="text" 
        placeholder="搜索产品" 
        v-model="keyword"
        @confirm="onSearch"
      />
    </view>
    
    <!-- 产品列表 -->
    <view class="product-list">
      <view class="product-item" 
        v-for="item in products" 
        :key="item.id"
        @tap="onProductTap(item)"
      >
        <image :src="item.imageUrl" mode="aspectFill" class="cover"></image>
        <view class="info">
          <text class="name">{{ item.productName }}</text>
          <text class="desc">{{ item.description || '暂无描述' }}</text>
          <view class="company">
            <image :src="item.companyLogo" mode="aspectFill" class="logo"></image>
            <text class="name">{{ item.companyName }}</text>
          </view>
          <view class="stats">
            <text class="view">浏览 {{ item.viewCount }}</text>
            <text class="intention">意向 {{ item.intentionCount }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore">
      <text @tap="loadMore">加载更多</text>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="products.length === 0">
      <text>暂无产品</text>
    </view>
  </view>
</template>

<script>
import { getProducts } from '../../api/index'

export default {
  data() {
    return {
      keyword: '',
      products: [],
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
        const res = await getProducts({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.keyword
        })
        console.log('产品列表返回:', res)
        
        const list = res.data || []
        if (append) {
          this.products = [...this.products, ...list]
        } else {
          this.products = list
        }
        
        // 判断是否还有更多数据
        this.hasMore = list.length === this.pageSize
      } catch (error) {
        console.error('获取产品列表失败:', error)
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
    onProductTap(item) {
      uni.navigateTo({
        url: `/pages/product/detail?id=${item.id}`
      })
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
.product {
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

.product-list {
  padding: 20rpx;
}

.product-item {
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.product-item .cover {
  width: 100%;
  height: 400rpx;
}

.product-item .info {
  padding: 20rpx;
}

.product-item .name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.product-item .desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
  display: block;
}

.product-item .company {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.product-item .company .logo {
  width: 40rpx;
  height: 40rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
}

.product-item .company .name {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 0;
}

.product-item .stats {
  display: flex;
  font-size: 24rpx;
  color: #999;
}

.product-item .stats .view {
  margin-right: 20rpx;
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