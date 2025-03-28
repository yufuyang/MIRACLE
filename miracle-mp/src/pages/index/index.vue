<template>
  <view class="container">
    <scroll-view 
      scroll-y 
      class="scroll-container"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 搜索框 -->
      <view class="search-box">
        <view class="custom-search" @tap="onSearchClick">
          <text class="placeholder">搜索商品</text>
        </view>
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
            :key="item.value"
            :class="{ active: currentCategory === item.value }"
            @tap="changeCategory(item.value)"
          >
            {{ item.key }}
          </view>
        </view>
      </scroll-view>

      <!-- 瀑布流产品列表 -->
      <view class="waterfall-wrapper">
        <view class="waterfall-column">
          <view 
            class="product-item" 
            v-for="item in leftProducts" 
            :key="item.id"
            @tap="handleProductDetail(item.id)"
            hover-class="product-item-hover"
            hover-stay-time="100"
          >
            <view class="image-wrapper">
              <image :src="item.imageUrl" mode="widthFix" class="product-image"></image>
              <view class="stats-overlay">
                <view class="stat-item" data-label="浏览">
                  <text class="iconfont icon-eye"></text>
                  <text>{{ item.viewCount }}</text>
                </view>
                <view class="stat-item" data-label="意向">
                  <text class="iconfont icon-heart"></text>
                  <text>{{ item.intentionCount }}</text>
                </view>
              </view>
            </view>
            <view class="product-info">
              <text class="product-name">{{ item.productName }}</text>
              <text class="product-desc">{{ item.description }}</text>
<!--              <view class="product-price-box">-->
<!--                <text class="price-symbol">¥</text>-->
<!--                <text class="product-price">{{ item.price }}</text>-->
<!--              </view>-->
            </view>
          </view>
        </view>
        <view class="waterfall-column">
          <view 
            class="product-item" 
            v-for="item in rightProducts" 
            :key="item.id"
            @tap="handleProductDetail(item.id)"
            hover-class="product-item-hover"
            hover-stay-time="100"
          >
            <view class="image-wrapper">
              <image :src="item.imageUrl" mode="widthFix" class="product-image"></image>
              <view class="stats-overlay">
                <view class="stat-item" data-label="浏览">
                  <text class="iconfont icon-eye"></text>
                  <text>{{ item.viewCount }}</text>
                </view>
                <view class="stat-item" data-label="意向">
                  <text class="iconfont icon-heart"></text>
                  <text>{{ item.intentionCount }}</text>
                </view>
              </view>
            </view>
            <view class="product-info">
              <text class="product-name">{{ item.productName }}</text>
              <text class="product-desc">{{ item.description }}</text>
<!--              <view class="product-price-box">-->
<!--                <text class="price-symbol">¥</text>-->
<!--                <text class="product-price">{{ item.price }}</text>-->
<!--              </view>-->
            </view>
          </view>
        </view>
      </view>
      <!-- 加载更多提示 -->
      <view class="loading-more" v-if="loading">正在加载更多...</view>
      <view class="no-more" v-if="noMore">没有更多数据了</view>
    </scroll-view>
  </view>
</template>

<script>
import { getProducts, getHotActivities, getRecommendCompanies, getBaseCategories } from '../../api/index'

export default {
  data() {
    return {
      products: [],
      activities: [],
      companies: [],
      defaultImage: 'https://via.placeholder.com/80x80',
      searchValue: '',
      categories: [{ key: '全部', value: 'all' }],
      currentCategory: 'all',
      page: 1,
      pageSize: 10,
      loading: false,
      noMore: false,
      isRefreshing: false
    }
  },
  computed: {
    // 左侧列表数据
    leftProducts() {
      return this.products.filter((_, index) => index % 2 === 0)
    },
    // 右侧列表数据
    rightProducts() {
      return this.products.filter((_, index) => index % 2 === 1)
    }
  },
  onLoad() {
    this.loadCategories()
    this.loadData()
  },
  methods: {
    // 加载分类数据
    async loadCategories() {
      console.log('开始加载分类数据')
      try {
        const res = await getBaseCategories()
        console.log('分类数据:', res)
        if (res.code === 200) {
          // 保留"全部"选项，添加后端返回的分类
          this.categories = [
            { key: '全部', value: 'all' },
            ...res.data
          ]
        }
      } catch (error) {
        console.error('获取分类失败:', error)
        uni.showToast({
          title: '获取分类失败',
          icon: 'none'
        })
      }
    },
    
    async loadData(isRefresh = false) {
      if (isRefresh) {
        this.page = 1
        this.noMore = false
        this.products = []
      }
      
      if (this.loading || this.noMore) return
      
      this.loading = true
      try {
        const params = {
          pageNum: this.page,
          pageSize: this.pageSize
        }
        
        // 修改分类参数的传递方式
        if (this.currentCategory !== 'all') {
          params.categoryType = this.currentCategory
        }
        
        // 改用 getProducts 方法
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
        console.error('加载产品失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
        if (isRefresh) {
          this.isRefreshing = false
        }
      }
    },
    async getActivities() {
      try {
        const res = await getHotActivities()
        console.log('热门活动接口返回:', res)
        if (res.code === 200) {
          return res.data || []
        }
        return []
      } catch (error) {
        console.error('获取热门活动失败:', error)
        return []
      }
    },
    async getCompanies() {
      try {
        const res = await getRecommendCompanies()
        console.log('企业接口返回:', res)
        return res.data || []
      } catch (error) {
        console.error('获取企业失败:', error)
        return []
      }
    },
    handleProductDetail(id) {
      uni.navigateTo({
        url: `/pages/merchant/product/detail?id=${id}`
      })
    },
    onActivityTap(item) {
      uni.navigateTo({
        url: `/pages/activity/detail?id=${item.id}`
      })
    },
    onCompanyTap(item) {
      uni.navigateTo({
        url: `/pages/company/detail/index?id=${item.id}`
      })
    },
    onMoreTap(type) {
      const urls = {
        product: '/pages/product/index',
        activity: '/pages/activity/index',
        company: '/pages/company/index'
      }
      uni.switchTab({
        url: urls[type]
      })
    },
    onBannerTap(item) {
      if (item.type === 'product') {
        uni.navigateTo({
          url: `/pages/product/detail?id=${item.targetId}`
        })
      } else if (item.type === 'activity') {
        uni.navigateTo({
          url: `/pages/activity/detail?id=${item.targetId}`
        })
      }
    },
    handleCompanyDetail(id) {
      uni.navigateTo({
        url: `/pages/company/detail/index?id=${id}`
      })
    },
    handleActivityDetail(id) {
      console.log('跳转到活动详情，id:', id)
      uni.navigateTo({
        url: `/pages/activity/detail?id=${id}`,
        fail: (err) => {
          console.error('跳转失败:', err)
          uni.showToast({
            title: '跳转失败',
            icon: 'none'
          })
        }
      })
    },
    formatTime(timestamp) {
      const date = new Date(timestamp)
      return date.toLocaleDateString()
    },
    onSearchClick() {
      // 暂时注释掉搜索跳转，直到搜索页面创建完成
      uni.navigateTo({
        url: '/pages/search/index'
      })
      // uni.showToast({
      //   title: '搜索功能开发中',
      //   icon: 'none'
      // })
    },
    changeCategory(value) {
      if (this.currentCategory === value) return
      this.currentCategory = value
      this.loadData(true)
    },
    // 加载更多
    loadMore() {
      this.loadData()
    },
    // 下拉刷新
    async onRefresh() {
      this.isRefreshing = true
      await this.loadData(true)
    },
    // 处理意向点击
    handleIntention(productId) {
      // 阻止事件冒泡
      event.stopPropagation()
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showModal({
          title: '提示',
          content: '登录后才能添加意向',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              uni.navigateTo({
                url: '/pages/login/index'
              })
            }
          }
        })
        return
      }
      // 原有的意向处理逻辑
      // TODO: 添加意向的API调用
      uni.showToast({
        title: '添加意向成功',
        icon: 'success'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  height: 100vh;
  background: #f8f8f8;
}

.scroll-container {
  height: 100%;
}

.search-box {
  background: #fff;
  padding: 20rpx 30rpx;
}

.custom-search {
  background: #f5f5f5;
  height: 72rpx;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  padding: 0 30rpx;
}

.placeholder {
  color: #999;
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
  
  &.active {
    color: #fff;
    background: #ff4444;
  }
  
  &:last-child {
    margin-right: 0;
  }
}

.waterfall-wrapper {
  display: flex;
  padding: 20rpx;
  box-sizing: border-box;
}

.waterfall-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  
  &:first-child {
    margin-right: 10rpx;
  }
  
  &:last-child {
    margin-left: 10rpx;
  }
}

.product-item {
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  transition: all 0.2s ease;
  
  .image-wrapper {
    position: relative;
    width: 100%;
    
    .product-image {
      width: 100%;
      height: auto;
      background: #f5f5f5;
    }
    
    .stats-overlay {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      position: absolute;
      left: 12rpx;
      top: 0;
      bottom: 12rpx;
      padding-top: 12rpx;
      
      .stat-item {
        display: flex;
        align-items: center;
        color: #fff;
        font-size: 24rpx;
        background: rgba(0, 0, 0, 0.3);
        padding: 4rpx 12rpx;
        border-radius: 20rpx;
        
        .iconfont {
          font-size: 28rpx;
          margin-right: 4rpx;
        }
        
        &::before {
          content: attr(data-label);
          margin-right: 4rpx;
        }
        
        text:last-child {
          font-weight: 500;
        }
      }
    }
  }
  
  .product-info {
    padding: 16rpx;
    
    .product-name {
      font-size: 28rpx;
      color: #333;
      font-weight: bold;
      display: block;
      margin-bottom: 8rpx;
    }
    
    .product-desc {
      font-size: 24rpx;
      color: #999;
      display: block;
      margin-bottom: 12rpx;
    }
    
    .product-price-box {
      display: flex;
      align-items: baseline;
      
      .price-symbol {
        font-size: 24rpx;
        color: #ff4444;
        margin-right: 2rpx;
      }
      
      .product-price {
        font-size: 32rpx;
        color: #ff4444;
        font-weight: bold;
      }
    }
  }
}

// 悬浮效果
.product-item-hover {
  transform: translateY(-3rpx);
  box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.1);
}

.loading-more, .no-more {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 24rpx;
}
</style>
