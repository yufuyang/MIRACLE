<template>
  <view class="company-detail">
    <!-- 企业基本信息 -->
    <view class="company-info">
      <image :src="company.logo" mode="aspectFill" class="logo"></image>
      <view class="info">
        <text class="name">{{ company.name }}</text>
        <text class="desc">{{ company.desc }}</text>
        <view class="tags">
          <text class="tag" v-for="(tag, index) in company.tags" :key="index">{{ tag }}</text>
        </view>
      </view>
    </view>

    <!-- 企业介绍 -->
    <view class="section">
      <view class="title">企业介绍</view>
      <text class="content">{{ company.introduction }}</text>
    </view>

    <!-- 企业产品 -->
    <view class="section">
      <view class="title">企业产品</view>
      <scroll-view scroll-x class="product-list">
        <view class="product-item" v-for="item in products" :key="item.id">
          <image :src="item.image" mode="aspectFill"></image>
          <text class="name">{{ item.name }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 企业活动 -->
    <view class="section">
      <view class="title">企业活动</view>
      <view class="activity-list">
        <view class="activity-item" v-for="item in activities" :key="item.id">
          <image :src="item.image" mode="aspectFill"></image>
          <view class="info">
            <text class="name">{{ item.name }}</text>
            <text class="date">{{ item.startDate }} - {{ item.endDate }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      company: {
        id: 0,
        name: '',
        desc: '',
        logo: 'https://via.placeholder.com/120',
        tags: [],
        introduction: ''
      },
      products: [],
      activities: []
    }
  },
  onLoad(options) {
    // 获取企业ID
    const id = options.id
    this.getCompanyDetail(id)
  },
  methods: {
    getCompanyDetail(id) {
      // TODO: 调用接口获取企业详情
      this.company = {
        id: 1,
        name: '示例企业',
        desc: '企业简介...',
        logo: 'https://via.placeholder.com/120',
        tags: ['机械制造', '智能装备'],
        introduction: '这是一段详细的企业介绍...'
      }
      
      // 获取企业产品
      this.products = [
        { id: 1, name: '产品1', image: 'https://via.placeholder.com/200' },
        { id: 2, name: '产品2', image: 'https://via.placeholder.com/200' }
      ]
      
      // 获取企业活动
      this.activities = [
        {
          id: 1,
          name: '活动1',
          startDate: '2024-02-01',
          endDate: '2024-02-29',
          image: 'https://via.placeholder.com/300'
        }
      ]
    }
  }
}
</script>

<style>
.company-detail {
  padding: 20rpx;
  background: #f5f5f5;
}

.company-info {
  background: #fff;
  padding: 30rpx;
  border-radius: 12rpx;
  display: flex;
  margin-bottom: 20rpx;
}

.company-info .logo {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  margin-right: 30rpx;
}

.company-info .info {
  flex: 1;
}

.company-info .name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.company-info .desc {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 16rpx;
  display: block;
}

.company-info .tags {
  display: flex;
  flex-wrap: wrap;
}

.company-info .tag {
  font-size: 24rpx;
  color: #1890ff;
  background: rgba(24, 144, 255, 0.1);
  padding: 4rpx 16rpx;
  border-radius: 4rpx;
  margin-right: 16rpx;
  margin-bottom: 16rpx;
}

.section {
  background: #fff;
  padding: 30rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.section .title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.section .content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}

.product-list {
  white-space: nowrap;
}

.product-item {
  display: inline-block;
  width: 200rpx;
  margin-right: 20rpx;
}

.product-item image {
  width: 100%;
  height: 200rpx;
  border-radius: 8rpx;
}

.product-item .name {
  font-size: 26rpx;
  color: #333;
  margin-top: 10rpx;
  display: block;
  text-align: center;
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  margin-bottom: 20rpx;
}

.activity-item image {
  width: 100%;
  height: 300rpx;
  border-radius: 8rpx;
}

.activity-item .info {
  padding: 16rpx 0;
}

.activity-item .name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 8rpx;
  display: block;
}

.activity-item .date {
  font-size: 24rpx;
  color: #999;
  display: block;
}
</style> 