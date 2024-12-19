<template>
  <div class="company-detail">
    <div class="container">
      <!-- 面包屑导航 -->
      <a-breadcrumb class="breadcrumb">
        <a-breadcrumb-item>
          <router-link to="/">首页</router-link>
        </a-breadcrumb-item>
        <a-breadcrumb-item>
          <router-link to="/company">企业</router-link>
        </a-breadcrumb-item>
        <a-breadcrumb-item>{{ company.companyName }}</a-breadcrumb-item>
      </a-breadcrumb>

      <!-- 公司基本信息 -->
      <div class="company-info">
        <a-row :gutter="24">
          <!-- 左侧公司信息 -->
          <a-col :span="16">
            <div class="info-main">
              <div class="company-header">
                <img :src="company.logo || defaultImage" class="company-logo" alt="公司logo" />
                <div class="company-title">
                  <h1>{{ company.companyName }}</h1>
                  <div class="company-stats">
                    <span><shop-outlined /> {{ company.productCount }} 产品</span>
                    <span><eye-outlined /> {{ company.viewCount }} 浏览</span>
                  </div>
                </div>
              </div>
              <div class="company-description">
                <h3>公司简介</h3>
                <p>{{ company.description }}</p>
              </div>
              <div class="company-images">
                <h3>公司图片</h3>
                <div class="image-list">
                  <img 
                    v-for="image in company.images" 
                    :key="image.id" 
                    :src="image.imageUrl || defaultImage" 
                    :alt="company.companyName"
                  />
                </div>
              </div>
            </div>
          </a-col>

          <!-- 右侧联系信息 -->
          <a-col :span="8">
            <div class="contact-info">
              <h3>联系方式</h3>
              <div class="info-item">
                <phone-outlined />
                <span>{{ company.phone }}</span>
              </div>
              <div class="info-item">
                <mail-outlined />
                <span>{{ company.email }}</span>
              </div>
              <div class="info-item">
                <environment-outlined />
                <span>{{ company.address }}</span>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 产品列表 -->
      <div class="company-products">
        <h2>公司产品</h2>
        <a-row :gutter="[24, 24]">
          <a-col :span="6" v-for="product in products" :key="product.id">
            <a-card hoverable class="product-card" @click="goToProduct(product.id)">
              <img :src="product.mainImage || defaultImage" alt="产品图片" />
              <a-card-meta :title="product.productName">
                <template #description>
                  <div class="product-price">¥ {{ product.price }}</div>
                  <div class="product-stats">
                    <span><eye-outlined /> {{ product.viewCount }}</span>
                    <span><heart-outlined /> {{ product.intentionCount }}</span>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </a-row>
        <!-- 分页 -->
        <div class="pagination">
          <a-pagination
            v-model:current="currentPage"
            :total="total"
            :pageSize="pageSize"
            @change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ShopOutlined,
  EyeOutlined,
  HeartOutlined,
  PhoneOutlined,
  MailOutlined,
  EnvironmentOutlined
} from '@ant-design/icons-vue'
import { getCompanyDetail } from '@/api/company'
import { getCompanyProducts } from '@/api/product'
import defaultImage from '@/assets/images/default.jpg'

const router = useRouter()
const route = useRoute()

// 公司信息
const company = ref({
  companyName: '',
  description: '',
  logo: '',
  phone: '',
  email: '',
  address: '',
  productCount: 0,
  viewCount: 0,
  images: []
})

// 产品列表
const products = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

// 获取公司详情
const loadCompanyDetail = async () => {
  try {
    const res = await getCompanyDetail(route.params.id)
    company.value = res.data
  } catch (error) {
    console.error('获取公司详情失败:', error)
  }
}

// 获取公司产品列表
const loadCompanyProducts = async () => {
  try {
    const res = await getCompanyProducts({
      companyId: route.params.id,
      page: currentPage.value,
      size: pageSize.value
    })
    products.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取公司产品列表失败:', error)
  }
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadCompanyProducts()
}

// 跳转到产品详情
const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 初始化
onMounted(() => {
  loadCompanyDetail()
  loadCompanyProducts()
})
</script>

<style scoped lang="less">
.company-detail {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;

    .breadcrumb {
      margin-bottom: 24px;
    }

    .company-info {
      background: #fff;
      padding: 24px;
      border-radius: 4px;
      margin-bottom: 24px;

      .info-main {
        .company-header {
          display: flex;
          align-items: center;
          margin-bottom: 24px;

          .company-logo {
            width: 120px;
            height: 120px;
            object-fit: cover;
            border-radius: 4px;
            margin-right: 24px;
          }

          .company-title {
            h1 {
              margin-bottom: 16px;
            }

            .company-stats {
              display: flex;
              gap: 16px;
              color: #666;
            }
          }
        }

        .company-description {
          margin-bottom: 24px;

          h3 {
            margin-bottom: 16px;
          }

          p {
            color: #666;
            line-height: 1.6;
          }
        }

        .company-images {
          h3 {
            margin-bottom: 16px;
          }

          .image-list {
            display: flex;
            gap: 16px;
            flex-wrap: wrap;

            img {
              width: 200px;
              height: 150px;
              object-fit: cover;
              border-radius: 4px;
            }
          }
        }
      }

      .contact-info {
        background: #f5f5f5;
        padding: 24px;
        border-radius: 4px;

        h3 {
          margin-bottom: 16px;
        }

        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 16px;
          color: #666;

          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }

    .company-products {
      background: #fff;
      padding: 24px;
      border-radius: 4px;

      h2 {
        margin-bottom: 24px;
      }

      .product-card {
        img {
          width: 100%;
          height: 200px;
          object-fit: cover;
        }

        .product-price {
          color: #f5222d;
          font-size: 16px;
          margin-bottom: 8px;
        }

        .product-stats {
          display: flex;
          gap: 16px;
          color: #666;
        }
      }

      .pagination {
        margin-top: 24px;
        text-align: center;
      }
    }
  }
}
</style> 