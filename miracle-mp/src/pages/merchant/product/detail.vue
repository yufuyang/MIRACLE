<template>
  <view class="product-detail">
    <!-- 顶部操作按钮 -->
    <view class="action-bar">
      <button 
        class="action-btn view-company" 
        @tap="handleCompanyDetail"
      >查看企业</button>
      <button 
        class="action-btn add-intention"
        :class="{ 'has-intention': hasIntention }" 
        @tap="handleIntention"
      >{{ hasIntention ? '取消意向' : '添加意向' }}</button>
    </view>

    <!-- 产品图片轮播 -->
    <swiper 
      class="image-swiper" 
      :indicator-dots="true"
      :autoplay="false"
      :duration="500"
    >
      <swiper-item v-for="(image, index) in productImages" :key="index">
        <image 
          :src="image.url" 
          mode="aspectFill" 
          class="product-image"
          @tap="handlePreviewImage(image.url)"
        />
      </swiper-item>
    </swiper>

    <!-- 基本信息 -->
    <view class="info-section">
      <view class="section-title">基本信息</view>
      <view class="info-item">
        <text class="label">产品名称：</text>
        <text class="value">{{ productInfo.productName }}</text>
      </view>
      <view class="info-item">
        <text class="label">产品编号：</text>
        <text class="value">{{ productInfo.productCode }}</text>
      </view>
      <view class="info-item">
        <text class="label">产品分类：</text>
        <text class="value">{{ categoryInfo.categoryName || '暂无分类' }}</text>
      </view>
      <view class="info-item">
        <text class="label">所属企业：</text>
        <text class="value">{{ productInfo.companyName }}</text>
      </view>
      <view class="info-item">
        <text class="label">发布时间：</text>
        <text class="value">{{ formatTime(productInfo.createTime) }}</text>
      </view>
    </view>

    <!-- 产品描述 -->
    <view class="desc-section" v-if="productInfo.description">
      <view class="section-title">产品描述</view>
      <text class="description">{{ productInfo.description }}</text>
    </view>

    <!-- 生产步骤 -->
    <view class="step-section" v-if="productSteps.length > 0">
      <view class="section-title">生产步骤</view>
      <view class="step-list">
        <view 
          v-for="(step, index) in productSteps" 
          :key="step.id"
          class="step-item"
        >
          <text class="step-num">{{ index + 1 }}</text>
          <view class="step-content">
            <text class="step-title">{{ step.title }}</text>
            <text class="step-desc">{{ step.description }}</text>
            <!-- 步骤媒体内容 -->
            <image 
              v-if="step.mediaType === 'image'" 
              :src="step.mediaUrl" 
              mode="widthFix" 
              class="step-media"
              @tap="handlePreviewImage(step.mediaUrl)"
            />
            <video 
              v-if="step.mediaType === 'video'" 
              :src="step.mediaUrl"
              class="step-media"
            />
          </view>
        </view>
      </view>
    </view>

    <!-- 公司信息 -->
    <view class="company-section" v-if="companyInfo.id">
      <view class="section-title">企业信息</view>
      <view class="company-card" @tap="handleCompanyDetail">
        <image :src="companyInfo.logoUrl || defaultImage" class="company-logo" mode="aspectFill" />
        <view class="company-info">
          <text class="company-name">{{ companyInfo.companyName }}</text>
          <text class="company-desc">{{ companyInfo.description || '暂无描述' }}</text>
          <view class="company-stats">
            <text class="stat-item">产品 {{ companyInfo.productCount || 0 }}</text>
            <text class="stat-item">意向 {{ companyInfo.intentionCount || 0 }}</text>
          </view>
        </view>
        <text class="arrow">></text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { formatTime } from '@/utils/date'
import { 
  getProductDetail, 
  getProductImages, 
  getProductSteps,
  getCompanyInfo,
  getProductCategory,
  checkProductIntention,
  cancelProductIntention,
  addProductIntention
} from '@/api/merchant/product'

const defaultImage = '/static/images/default-company.png'
const productInfo = ref({})
const productImages = ref([])
const productSteps = ref([])
const companyInfo = ref({})
const categoryInfo = ref({})
const hasIntention = ref(false)

// 获取产品详情和相关数据
const loadProductDetail = async (id) => {
  try {
    // 并行请求所有数据
    const [detailRes, imagesRes, stepsRes, intentionRes] = await Promise.all([
      getProductDetail(id),
      getProductImages(id),
      getProductSteps(id),
      checkProductIntention(id)
    ])

    if (detailRes.code === 200) {
      productInfo.value = detailRes.data
      // 获取公司信息
      if (detailRes.data.companyId) {
        const companyRes = await getCompanyInfo(detailRes.data.companyId)
        if (companyRes.code === 200) {
          companyInfo.value = companyRes.data
        }
      }
      // 获取分类信息
      if (detailRes.data.categoryId) {
        const categoryRes = await getProductCategory(detailRes.data.categoryId)
        if (categoryRes.code === 200) {
          categoryInfo.value = categoryRes.data
        }
      }
    }

    if (imagesRes.code === 200) {
      productImages.value = imagesRes.data.map(img => ({
        url: img.imageUrl,
        isMain: img.isMain === 1
      }))
    }

    if (stepsRes.code === 200) {
      productSteps.value = stepsRes.data
    }

    // 处理意向状态
    if (intentionRes.code === 200) {
      hasIntention.value = intentionRes.data
    }
  } catch (error) {
    console.error('获取产品数据失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
  }
}

// 处理图片预览
const handlePreviewImage = (url) => {
  if (!url) return
  const urls = productImages.value.map(img => img.url)
  uni.previewImage({
    urls,
    current: url
  })
}

// 跳转到公司详情
const handleCompanyDetail = () => {
  if (!companyInfo.value.id) return
  uni.navigateTo({
    url: `/pages/company/detail/index?id=${companyInfo.value.id}`
  })
}

// 处理意向操作
const handleIntention = async () => {
  if (!productInfo.value.id) return

  if (hasIntention.value) {
    // 取消意向
    uni.showModal({
      title: '提示',
      content: '确定要取消意向吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const res = await cancelProductIntention(productInfo.value.id)
            if (res.code === 200) {
              hasIntention.value = false
              uni.showToast({
                title: '已取消意向',
                icon: 'success'
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
  } else {
    // 添加意向
    try {
      const res = await addProductIntention({
        productId: productInfo.value.id,
        companyId: productInfo.value.companyId
      })
      if (res.code === 200) {
        hasIntention.value = true
        uni.showToast({
          title: '已添加意向',
          icon: 'success'
        })
      }
    } catch (error) {
      console.error('添加意向失败:', error)
      uni.showToast({
        title: '添加失败',
        icon: 'none'
      })
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  // 打印完整的页面信息，用于调试
  console.log('当前页面信息:', currentPage)
  
  // 尝试多种方式获取参数
  const id = currentPage?.options?.id || currentPage?.$page?.options?.id || currentPage?.query?.id
  console.log('获取到的产品ID:', id)

  if (id) {
    loadProductDetail(id)
  } else {
    console.error('未获取到产品ID，页面信息:', {
      pages: pages.length,
      currentPage,
      options: currentPage?.options,
      $page: currentPage?.$page,
      query: currentPage?.query
    })
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
  }
})

// 添加页面参数监听
defineProps({
  id: {
    type: [String, Number],
    default: ''
  }
})
</script>

<style lang="scss" scoped>
.product-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;

  .action-bar {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 100;
    display: flex;
    padding: 20rpx;
    gap: 20rpx;

    .action-btn {
      min-width: 160rpx;
      height: 64rpx;
      line-height: 64rpx;
      font-size: 28rpx;
      border-radius: 32rpx;
      margin: 0;
      padding: 0 30rpx;

      &.view-company {
        background: #fff;
        color: #333;
        border: 1rpx solid #ddd;
      }

      &.add-intention {
        background: #1890ff;
        color: #fff;

        &.has-intention {
          background: #ff4d4f;
          color: #fff;
        }
      }

      &:active {
        opacity: 0.8;
      }
    }
  }

  .image-swiper {
    width: 100%;
    height: 500rpx;
    background: #fff;
    position: relative;
    margin-top: 104rpx;

    .product-image {
      width: 100%;
      height: 100%;
    }

    .image-tag {
      position: absolute;
      top: 20rpx;
      left: 20rpx;
      background: #1890ff;
      color: #fff;
      font-size: 24rpx;
      padding: 4rpx 12rpx;
      border-radius: 4rpx;
    }
  }

  .info-section {
    margin-top: 20rpx;
    background: #fff;
    padding: 30rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
      border-left: 8rpx solid #1890ff;
      padding-left: 20rpx;
    }

    .info-item {
      display: flex;
      margin-bottom: 16rpx;
      font-size: 28rpx;
      line-height: 1.5;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        width: 160rpx;
        color: #666;
      }

      .value {
        flex: 1;
        color: #333;
      }
    }
  }

  .step-section {
    margin-top: 20rpx;
    background: #fff;
    padding: 30rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
      border-left: 8rpx solid #1890ff;
      padding-left: 20rpx;
    }

    .step-list {
      .step-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 20rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .step-num {
          width: 40rpx;
          height: 40rpx;
          line-height: 40rpx;
          text-align: center;
          background: #1890ff;
          color: #fff;
          border-radius: 50%;
          font-size: 24rpx;
          margin-right: 20rpx;
          flex-shrink: 0;
        }

        .step-content {
          flex: 1;

          .step-title {
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 8rpx;
            display: block;
          }

          .step-desc {
            font-size: 28rpx;
            color: #666;
            margin-bottom: 16rpx;
            display: block;
          }

          .step-media {
            width: 100%;
            border-radius: 8rpx;
            margin-top: 16rpx;
          }
        }
      }
    }
  }

  .desc-section {
    margin-top: 20rpx;
    background: #fff;
    padding: 30rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
      border-left: 8rpx solid #1890ff;
      padding-left: 20rpx;
    }

    .description {
      font-size: 28rpx;
      color: #333;
      line-height: 1.5;
    }
  }

  .company-section {
    margin-top: 20rpx;
    background: #fff;
    padding: 30rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
      border-left: 8rpx solid #1890ff;
      padding-left: 20rpx;
    }

    .company-card {
      display: flex;
      align-items: center;
      padding: 20rpx;
      background: #f8f8f8;
      border-radius: 8rpx;
      position: relative;

      .company-logo {
        width: 120rpx;
        height: 120rpx;
        border-radius: 8rpx;
        margin-right: 20rpx;
        background-color: #fff;
      }

      .company-info {
        flex: 1;
        overflow: hidden;

        .company-name {
          font-size: 28rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 8rpx;
          display: block;
        }

        .company-desc {
          font-size: 24rpx;
          color: #666;
          margin-bottom: 12rpx;
          display: block;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .company-stats {
          display: flex;
          align-items: center;

          .stat-item {
            font-size: 24rpx;
            color: #999;
            margin-right: 20rpx;

            &:last-child {
              margin-right: 0;
            }
          }
        }
      }

      .arrow {
        font-size: 32rpx;
        color: #999;
        margin-left: 20rpx;
      }

      &:active {
        opacity: 0.8;
      }
    }
  }
}
</style> 