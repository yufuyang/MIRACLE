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
    
    <!-- 分类选项卡 -->
    <scroll-view 
      scroll-x 
      class="category-scroll"
      :show-scrollbar="false"
    >
      <view class="category-list">
        <view 
          class="category-item" 
          v-for="item in categories" 
          :key="item.id"
          :class="{ active: currentCategory === item.id }"
          @tap="changeCategory(item.id)"
        >
          {{ item.name }}
        </view>
      </view>
    </scroll-view>
    
    <!-- 产品列表 -->
    <view class="product-list">
      <view class="product-item" 
        v-for="item in products" 
        :key="item.id"
        @tap="handleProductDetail(item.id)"
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
    <view class="load-more" v-if="loading">
      <text>加载中...</text>
    </view>

    <!-- 没有更多数据 -->
    <view class="no-more" v-if="noMore">
      <text>没有更多数据了</text>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="products.length === 0 && !loading">
      <text>暂无产品</text>
    </view>
  </view>
</template>

<script>
import { getProducts, getCategories } from '../../api/index'

export default {
  data() {
    return {
      keyword: '',
      products: [],
      categories: [{ id: 'all', name: '全部' }],
      currentCategory: 'all',
      page: 1,
      pageSize: 10,
      noMore: false,
      loading: false
    }
  },
  onLoad() {
    this.loadCategories()
    this.loadData()
  },
  methods: {
    // 加载分类数据
    async loadCategories() {
      try {
        const res = await getCategories()
        if (res.success && res.data) {
          this.categories = [
            { id: 'all', name: '全部' },
            ...res.data.map(item => ({
              id: item.id,
              name: item.name
            }))
          ]
        }
      } catch (error) {
        console.error('获取分类失败:', error)
      }
    },
    
    async loadData(isRefresh = false) {
      if (this.loading || (this.noMore && !isRefresh)) return
      
      if (isRefresh) {
        this.page = 1
        this.noMore = false
        this.products = []
      }
      
      this.loading = true
      
      try {
        const params = {
          pageNum: this.page,
          pageSize: this.pageSize,
          keyword: this.keyword
        }
        
        if (this.currentCategory !== 'all') {
          params.categoryId = this.currentCategory
        }
        
        const res = await getProducts(params)
        
        if (res.success && res.data) {
          const newProducts = res.data
          
          if (isRefresh) {
            this.products = newProducts
          } else {
            this.products = [...this.products, ...newProducts]
          }
          
          if (newProducts.length < this.pageSize) {
            this.noMore = true
          }
          this.page++
        } else {
          this.noMore = true
        }
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
    
    changeCategory(categoryId) {
      if (this.currentCategory === categoryId) return
      this.currentCategory = categoryId
      this.loadData(true)
    },
    
    onSearch() {
      this.loadData(true)
    },
    
    handleProductDetail(id) {
      uni.navigateTo({
        url: `/pages/merchant/product/detail?id=${id}`
      })
    }
  },
  // 下拉刷新
  onPullDownRefresh() {
    this.loadData(true).then(() => {
      uni.stopPullDownRefresh()
    })
  },
  // 上拉加载
  onReachBottom() {
    if (!this.noMore && !this.loading) {
      this.loadData()
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

.category-scroll {
  background: #fff;
  white-space: nowrap;
  border-bottom: 1rpx solid #f0f0f0;
}

.category-list {
  display: flex;
  padding: 20rpx;
}

.category-item {
  padding: 12rpx 30rpx;
  margin-right: 20rpx;
  font-size: 28rpx;
  color: #666;
  background: #f5f5f5;
  border-radius: 26rpx;
  transition: all 0.3s;
}

.category-item:last-child {
  margin-right: 0;
}

.category-item.active {
  color: #fff;
  background: #ff4444;
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

.no-more {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}

.empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}
</style> 