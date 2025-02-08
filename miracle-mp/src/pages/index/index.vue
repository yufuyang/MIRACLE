<template>
  <view class="index">
    <!-- 热门产品轮播 -->
    <view class="section" v-if="products.length > 0">
      <view class="header">
        <text class="title">热门产品</text>
        <text class="more" @tap="onMoreTap('product')">查看更多</text>
      </view>
      <swiper 
        class="product-swiper" 
        :indicator-dots="true"
        :autoplay="true"
        :interval="3000"
        :duration="500"
        :circular="true"
      >
        <swiper-item v-for="item in products" :key="item.id">
          <view class="product-item" @tap="handleProductDetail(item.id)">
            <image :src="item.imageUrl" mode="aspectFill"></image>
            <view class="info">
              <text class="name">{{ item.productName }}</text>
              <text class="desc">{{ item.description }}</text>
              <view class="stats">
                <text class="view">浏览 {{ item.viewCount }}</text>
                <text class="intention">意向 {{ item.intentionCount }}</text>
              </view>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 轮播图 -->
    <view class="section" v-if="activities.length > 0">
      <swiper 
        class="banner-swiper" 
        :indicator-dots="true"
        :autoplay="true"
        :interval="4000"
        :duration="500"
        :circular="true"
      >
        <swiper-item v-for="item in activities" :key="item.id">
          <view class="banner-item" @tap="onBannerTap(item)">
            <image :src="item.imageUrl" mode="aspectFill"></image>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 推荐企业 -->
    <view class="section">
      <view class="header">
        <text class="title">推荐企业</text>
        <text class="more" @tap="onMoreTap('company')">查看更多</text>
      </view>
      <swiper 
        class="company-swiper" 
        :indicator-dots="true"
        :autoplay="true"
        :interval="3000"
        :duration="500"
        circular
      >
        <swiper-item 
          v-for="item in companies" 
          :key="item.id"
        >
          <view class="company-card" @tap="handleCompanyDetail(item.id)">
            <image :src="item.logoUrl || defaultImage" mode="aspectFill" class="logo" />
            <view class="info">
              <text class="name">{{ item.companyName }}</text>
              <text class="desc">{{ item.description || '暂无描述' }}</text>
              <view class="stats">
                <text class="stat">浏览 {{ item.viewCount }}</text>
                <text class="stat">意向 {{ item.intentionCount }}</text>
              </view>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>
  </view>
</template>

<script>
import { getHotProducts, getHotActivities, getRecommendCompanies } from '../../api/index'

export default {
  data() {
    return {
      products: [],
      activities: [],
      companies: [],
      defaultImage: 'https://via.placeholder.com/80x80'
    }
  },
  onLoad() {
    console.log('页面加载')
    this.loadData()
  },
  methods: {
    async loadData() {
      console.log('开始加载数据')
      try {
        // 分开加载数据，方便调试
        const products = await this.getProducts()
        console.log('产品数据:', products)
        this.products = products

        const activities = await this.getActivities()
        console.log('活动数据:', activities)
        this.activities = activities

        const companies = await this.getCompanies()
        console.log('企业数据:', companies)
        this.companies = companies
      } catch (error) {
        console.error('加载数据失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      }
    },
    async getProducts() {
      try {
        const res = await getHotProducts()
        console.log('产品接口返回:', res)
        return res.data || []
      } catch (error) {
        console.error('获取产品失败:', error)
        return []
      }
    },
    async getActivities() {
      try {
        const res = await getHotActivities()
        console.log('轮播图接口返回:', res)
        return res.data || []
      } catch (error) {
        console.error('获取轮播图失败:', error)
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
    }
  }
}
</script>

<style lang="scss" scoped>
.index {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.section {
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  padding: 20rpx;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding: 0 20rpx;

  .title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
  }

  .more {
    font-size: 24rpx;
    color: #999;
  }
}

/* 产品轮播样式 */
.product-swiper {
  height: 600rpx;
}

.product-item {
  padding: 20rpx;
}

.product-item image {
  width: 100%;
  height: 400rpx;
  border-radius: 12rpx;
}

.product-item .info {
  padding: 20rpx 0;
}

.product-item .name {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.product-item .desc {
  font-size: 24rpx;
  color: #666;
  display: block;
}

.product-item .stats {
  display: flex;
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.product-item .stats .view {
  margin-right: 20rpx;
}

/* 轮播图样式 */
.banner-swiper {
  height: 300rpx;
}

.banner-item image {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

/* 企业列表样式 */
.company-swiper {
  height: 600rpx;
}

.company-card {
  padding: 20rpx;
}

.company-card .logo {
  width: 100%;
  height: 400rpx;
  border-radius: 12rpx;
  background-color: #f5f5f5;
}

.company-card .info {
  padding: 20rpx 0;
}

.company-card .name {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.company-card .desc {
  font-size: 24rpx;
  color: #666;
  display: block;
  margin-bottom: 10rpx;
}

.company-card .stats {
  display: flex;
  font-size: 24rpx;
  color: #999;
}

.company-card .stat {
  margin-right: 20rpx;
}

.company-card:active {
  opacity: 0.8;
}
</style>
