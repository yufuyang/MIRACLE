<template>
  <view class="company-detail">
    <!-- 企业基本信息卡片 -->
    <view class="company-card">
      <view class="company-header">
        <image :src="companyInfo.logoUrl || defaultImage" class="logo" mode="aspectFill" />
        <view class="info">
          <text class="name">{{ companyInfo.companyName }}</text>
          <view class="stats">
            <view class="stat-item">
              <text class="count">{{ companyInfo.productCount || 0 }}</text>
              <text class="label">产品数</text>
            </view>
            <view class="stat-item">
              <text class="count">{{ companyInfo.intentionCount || 0 }}</text>
              <text class="label">意向数</text>
            </view>
          </view>
        </view>
      </view>
      <view class="contact-info">
        <view class="contact-item">
          <text class="icon">👤</text>
          <text class="label">联系人：</text>
          <text class="value">{{ companyInfo.contactName }}</text>
        </view>
        <view class="contact-item">
          <text class="icon">📱</text>
          <text class="label">联系电话：</text>
          <text class="value">{{ companyInfo.contactPhone }}</text>
        </view>
        <view class="contact-item">
          <text class="icon">📍</text>
          <text class="label">所在地：</text>
          <text class="value">{{ formatAddress }}</text>
        </view>
      </view>
    </view>

    <!-- 内容标签页 -->
    <view class="content-tabs">
      <view 
        v-for="(tab, index) in tabs" 
        :key="tab.key"
        class="tab-item"
        :class="{ active: currentTab === index }"
        @tap="handleTabChange(index)"
      >
        {{ tab.name }}
        <view class="tab-line" :class="{ active: currentTab === index }"></view>
      </view>
    </view>

    <!-- 标签页内容区域 -->
    <swiper 
      class="content-swiper" 
      :current="currentTab"
      @change="handleSwiperChange"
    >
      <!-- 公司介绍 -->
      <swiper-item>
        <scroll-view scroll-y class="scroll-view">
          <view class="intro-content" v-if="companyInfo.description">
            {{ companyInfo.description }}
          </view>
          <view class="empty-tip" v-else>暂无公司介绍</view>
        </scroll-view>
      </swiper-item>

      <!-- 公司图片 -->
      <swiper-item>
        <scroll-view scroll-y class="scroll-view">
          <view class="image-grid" v-if="companyImages.length > 0">
            <image 
              v-for="(image, index) in companyImages" 
              :key="index"
              :src="image.imageUrl"
              mode="aspectFill"
              class="grid-image"
              @tap="handlePreviewImage(image.imageUrl)"
            />
          </view>
          <view class="empty-tip" v-else>暂无公司图片</view>
        </scroll-view>
      </swiper-item>

      <!-- 公司产品 -->
      <swiper-item>
        <scroll-view 
          scroll-y 
          class="scroll-view"
          @scrolltolower="handleLoadMore"
        >
          <view class="product-grid" v-if="products.length > 0">
            <view 
              v-for="item in products" 
              :key="item.id"
              class="product-card"
              @tap="handleProductDetail(item.id)"
            >
              <image :src="item.imageUrl || defaultImage" mode="aspectFill" class="product-image" />
              <view class="product-info">
                <text class="title">{{ item.productName }}</text>
                <text class="desc">{{ item.description || '暂无描述' }}</text>
                <view class="data">
                  <text class="data-item">浏览 {{ item.viewCount }}</text>
                  <text class="data-item">意向 {{ item.intentionCount }}</text>
                </view>
              </view>
            </view>
          </view>
          <view class="empty-tip" v-else>暂无产品</view>
        </scroll-view>
      </swiper-item>

      <!-- 公司活动 -->
      <swiper-item>
        <scroll-view scroll-y class="scroll-view">
          <view class="activity-list" v-if="activities.length > 0">
            <view 
              v-for="item in activities" 
              :key="item.id"
              class="activity-card"
              @tap="handleActivityDetail(item.id)"
            >
              <image :src="item.coverImage || defaultImage" mode="aspectFill" class="activity-image" />
              <view class="activity-info">
                <text class="title">{{ item.title }}</text>
                <text class="desc">{{ item.description || '暂无描述' }}</text>
                <view class="activity-stats">
                  <text class="time">{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</text>
                  <text class="view">浏览 {{ item.viewCount }}</text>
                </view>
              </view>
            </view>
          </view>
          <view class="empty-tip" v-else>暂无活动</view>
        </scroll-view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { formatTime } from '@/utils/date'
import { 
  getCompanyDetail,
  getCompanyImages,
  getCompanyProducts,
  getCompanyActivities
} from '@/api/company'

const defaultImage = '/static/images/default-company.png'
const companyInfo = ref({})
const companyImages = ref([])
const products = ref([])
const activities = ref([])
const currentTab = ref(0)
const loading = ref(false)

// 产品列表分页
const productPage = ref({
  pageNum: 1,
  pageSize: 10,
  hasMore: true
})

const tabs = [
  { key: 'intro', name: '公司介绍' },
  { key: 'images', name: '公司图片' },
  { key: 'products', name: '公司产品' },
  { key: 'activities', name: '公司活动' }
]

// 获取企业详情
const loadCompanyDetail = async (id) => {
  try {
    const res = await getCompanyDetail(id)
    if (res.code === 200) {
      companyInfo.value = res.data
    }
  } catch (error) {
    console.error('获取企业详情失败:', error)
  }
}

// 获取企业图片
const loadCompanyImages = async (id) => {
  try {
    const res = await getCompanyImages(id)
    if (res.code === 200) {
      companyImages.value = res.data || []
    }
  } catch (error) {
    console.error('获取企业图片失败:', error)
  }
}

// 获取企业产品
const loadCompanyProducts = async (id, append = false) => {
  if (loading.value || (!append && !productPage.value.hasMore)) return
  
  loading.value = true
  try {
    const res = await getCompanyProducts(id, {
      pageNum: productPage.value.pageNum,
      pageSize: 9
    })
    
    if (res.code === 200) {
      const list = res.data || []
      if (append) {
        products.value.push(...list)
      } else {
        products.value = list
      }
      
      productPage.value.hasMore = products.value.length < res.total
      if (append) {
        productPage.value.pageNum++
      }
    }
  } catch (error) {
    console.error('获取企业产品失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取企业活动
const loadCompanyActivities = async (id) => {
  try {
    const res = await getCompanyActivities({
      pageNum: 1,
      pageSize: 10,
      companyId: id
    })
    if (res.code === 200) {
      activities.value = res.data || []
    }
  } catch (error) {
    console.error('获取企业活动失败:', error)
  }
}

// 加载所有数据
const loadAllData = async (id) => {
  await Promise.all([
    loadCompanyDetail(id),
    loadCompanyImages(id),
    loadCompanyProducts(id),
    loadCompanyActivities(id)
  ])
}

// 处理标签切换
const handleTabChange = (index) => {
  currentTab.value = index
}

// 处理滑动切换
const handleSwiperChange = (e) => {
  currentTab.value = e.detail.current
}

// 处理图片预览
const handlePreviewImage = (url) => {
  const urls = companyImages.value.map(img => img.imageUrl)
  uni.previewImage({
    urls,
    current: url
  })
}

// 处理加载更多产品
const handleLoadMore = () => {
  if (currentTab.value === 2) { // 产品标签页
    const id = getCurrentPages()[getCurrentPages().length - 1]?.options?.id
    if (id) {
      loadCompanyProducts(id, true)
    }
  }
}

// 跳转到产品详情
const handleProductDetail = (id) => {
  uni.navigateTo({
    url: `/pages/merchant/product/detail?id=${id}`
  })
}

// 跳转到活动详情
const handleActivityDetail = (id) => {
  uni.navigateTo({
    url: `/pages/activity/detail?id=${id}`
  })
}

// 格式化地址
const formatAddress = computed(() => {
  if (!companyInfo.value) return ''
  const { province, city, district, address } = companyInfo.value
  return [province, city, district, address].filter(Boolean).join(' ')
})

// 页面加载
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage?.options?.id
  
  if (id) {
    loadAllData(id)
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
  }
})
</script>

<style lang="scss" scoped>
.company-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.company-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

  .company-header {
    display: flex;
    align-items: center;
    padding-bottom: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .logo {
      width: 120rpx;
      height: 120rpx;
      border-radius: 12rpx;
      margin-right: 24rpx;
      background: #f5f5f5;
    }

    .info {
      flex: 1;

      .name {
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 16rpx;
      }

      .stats {
        display: flex;
        gap: 40rpx;

        .stat-item {
          display: flex;
          flex-direction: column;
          align-items: center;

          .count {
            font-size: 32rpx;
            font-weight: bold;
            color: #1890ff;
          }

          .label {
            font-size: 24rpx;
            color: #999;
            margin-top: 4rpx;
          }
        }
      }
    }
  }

  .contact-info {
    padding-top: 20rpx;

    .contact-item {
      display: flex;
      align-items: center;
      margin-bottom: 16rpx;

      .icon {
        font-size: 32rpx;
        margin-right: 12rpx;
      }

      .label {
        font-size: 28rpx;
        color: #666;
        width: 140rpx;
      }

      .value {
        font-size: 28rpx;
        color: #333;
        flex: 1;
      }
    }
  }
}

.content-tabs {
  display: flex;
  background: #fff;
  padding: 0 30rpx;
  border-radius: 16rpx 16rpx 0 0;
  position: relative;
  margin-bottom: 2rpx;

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 24rpx 0;
    font-size: 30rpx;
    color: #666;
    position: relative;

    &.active {
      color: #1890ff;
      font-weight: bold;
    }

    .tab-line {
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 48rpx;
      height: 4rpx;
      border-radius: 2rpx;
      background: transparent;
      transition: all 0.3s;

      &.active {
        background: #1890ff;
      }
    }
  }
}

.content-swiper {
  height: calc(100vh - 400rpx);
  background: #fff;
  border-radius: 0 0 16rpx 16rpx;
}

.scroll-view {
  height: 100%;
  padding: 20rpx;
}

.intro-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  padding: 20rpx;
}

.image-grid {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  padding: 20rpx;

  .grid-image {
    width: 100%;
    height: 320rpx;
    border-radius: 12rpx;
  }
}

.product-grid {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  padding: 20rpx;

  .product-card {
    background: #fff;
    border-radius: 12rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
    display: flex;

    .product-image {
      width: 240rpx;
      height: 240rpx;
      flex-shrink: 0;
    }

    .product-info {
      flex: 1;
      padding: 20rpx;
      display: flex;
      flex-direction: column;
      justify-content: space-between;

      .title {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 12rpx;
      }

      .desc {
        font-size: 26rpx;
        color: #666;
        margin-bottom: auto;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .data {
        display: flex;
        justify-content: flex-start;
        gap: 32rpx;

        .data-item {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
  }
}

.activity-list {
  padding: 20rpx;

  .activity-card {
    background: #fff;
    border-radius: 12rpx;
    overflow: hidden;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

    .activity-image {
      width: 100%;
      height: 320rpx;
    }

    .activity-info {
      padding: 20rpx;

      .title {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 12rpx;
      }

      .desc {
        font-size: 26rpx;
        color: #666;
        margin-bottom: 12rpx;
        line-height: 1.5;
      }

      .activity-stats {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 12rpx;

        .time {
          font-size: 24rpx;
          color: #1890ff;
        }

        .view {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
  }
}

.empty-tip {
  text-align: center;
  color: #999;
  font-size: 28rpx;
  padding: 40rpx 0;
}
</style> 