<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner">
      <a-carousel autoplay>
        <div class="banner-item">
          <h3>精选产品展示</h3>
        </div>
        <div class="banner-item">
          <h3>优质企业展示</h3>
        </div>
      </a-carousel>
    </div>

    <!-- 数据统计 -->
    <div class="stats">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card>
            <a-statistic title="入驻企业" :value="mockStats.companyCount" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="注册商户" :value="mockStats.merchantCount" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="产品总数" :value="mockStats.productCount" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="累计意向" :value="mockStats.intentionCount" />
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 热门产品 -->
    <div class="hot-products">
      <div class="section-header">
        <h2>热门产品</h2>
        <a-radio-group v-model:value="productSort">
          <a-radio-button value="view">最多浏览</a-radio-button>
          <a-radio-button value="intention">最多意向</a-radio-button>
          <a-radio-button value="new">最新上架</a-radio-button>
        </a-radio-group>
      </div>
      <a-row :gutter="16">
        <a-col :span="6" v-for="product in sortedProducts.slice(0, 8)" :key="product.id">
          <a-card hoverable class="product-card">
            <template #cover>
              <img :alt="product.productName" :src="product.imageUrl || defaultImage" />
            </template>
            <a-card-meta :title="product.productName">
              <template #description>
                <div class="product-info">
                  <span class="price">¥ {{ product.price }}</span>
                  <div class="stats">
                    <span><eye-outlined /> {{ product.viewCount }}</span>
                    <span><heart-outlined /> {{ product.intentionCount }}</span>
                  </div>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { EyeOutlined, HeartOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { mockProducts, mockStats } from '@/mock/data'

const productSort = ref('view')

// 根据排序方式获取产品列表
const sortedProducts = computed(() => {
  const products = [...mockProducts]
  switch (productSort.value) {
    case 'view':
      return products.sort((a, b) => b.viewCount - a.viewCount)
    case 'intention':
      return products.sort((a, b) => b.intentionCount - a.intentionCount)
    case 'new':
      return products.sort((a, b) => b.id - a.id)
    default:
      return products
  }
})
</script>

<style scoped lang="less">
.home {
  .banner {
    .banner-item {
      height: 400px;
      line-height: 400px;
      text-align: center;
      background: #364d79;
      color: #fff;
    }
  }

  .stats {
    max-width: 1200px;
    margin: 24px auto;
  }

  .hot-products {
    max-width: 1200px;
    margin: 24px auto;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
    }

    .product-card {
      margin-bottom: 16px;

      :deep(.ant-card-cover) {
        img {
          height: 200px;
          object-fit: cover;
        }
      }

      .product-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 8px;

        .price {
          color: #f5222d;
          font-size: 16px;
          font-weight: bold;
        }

        .stats {
          display: flex;
          gap: 8px;
          color: #999;
        }
      }
    }
  }
}
</style> 