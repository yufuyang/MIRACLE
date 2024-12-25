<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner-section">
      <a-carousel autoplay>
        <template v-if="banners && banners.length > 0">
          <div class="banner-item" v-for="banner in banners" :key="banner.id" @click="goToActivity(banner.id)">
            <img :src="banner.coverImage" :alt="banner.title" />
            <div class="banner-content">
              <h3>{{ banner.title }}</h3>
              <p>{{ banner.description }}</p>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="banner-item">
            <div class="banner-placeholder">
              <picture-outlined />
              <span>暂无活动</span>
            </div>
          </div>
        </template>
      </a-carousel>
    </div>

    <!-- 热门产品 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">热门产品</h2>
        <a class="more-link" @click="goToProduct()">查看全部 ></a>
      </div>
      <div class="product-grid">
        <a-row :gutter="[24, 24]">
          <template v-if="hotProducts.length > 0">
            <a-col :span="6" v-for="product in hotProducts" :key="product.id">
              <a-card hoverable class="product-card" @click="goToProduct(product.id)">
                <template #cover>
                  <a-image
                    :src="product.imageUrl || defaultImage"
                    :alt="product.productName"
                    :preview="false"
                    style="height: 200px; object-fit: cover"
                  />
                </template>
                <a-card-meta :title="product.productName">
                  <template #description>
                    <div class="product-info">
                      <div class="product-price">
                        <span class="price">¥{{ product.price }}</span>
                        <span class="unit">/ {{ product.unit }}</span>
                      </div>
                      <div class="product-company">{{ product.companyName }}</div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </template>
          <template v-else>
            <a-col :span="6" v-for="i in 8" :key="i">
              <a-card class="product-card empty-card">
                <template #cover>
                  <a-skeleton-image :active="true" />
                </template>
                <a-card-meta>
                  <template #title>
                    <a-skeleton :active="true" :paragraph="false" />
                  </template>
                  <template #description>
                    <div class="product-info">
                      <div class="product-desc">
                        <a-skeleton :active="true" :paragraph="{ rows: 2 }" :title="false" />
                      </div>
                      <div class="product-company">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </template>
        </a-row>
      </div>
    </section>

    <!-- 优质企业 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">优质企业</h2>
        <a class="more-link" @click="goToCompany()">查看全部 ></a>
      </div>
      <div class="company-grid">
        <a-row :gutter="[24, 24]">
          <template v-if="featuredCompanies.length > 0">
            <a-col :span="6" v-for="company in featuredCompanies" :key="company.id">
              <a-card hoverable class="company-card" @click="goToCompany(company.id)">
                <template #cover>
                  <img :alt="company.name" :src="company.logoUrl || defaultImage" />
                </template>
                <a-card-meta :title="company.name">
                  <template #description>
                    <div class="company-info">
                      <div class="company-desc">{{ company.description }}</div>
                      <div class="company-location">
                        <environment-outlined /> {{ company.location }}
                      </div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </template>
          <template v-else>
            <a-col :span="6" v-for="i in 8" :key="i">
              <a-card class="company-card empty-card">
                <template #cover>
                  <a-skeleton-image :active="true" />
                </template>
                <a-card-meta>
                  <template #title>
                    <a-skeleton :active="true" :paragraph="false" />
                  </template>
                  <template #description>
                    <div class="company-info">
                      <div class="company-desc">
                        <a-skeleton :active="true" :paragraph="{ rows: 2 }" :title="false" />
                      </div>
                      <div class="company-location">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </template>
        </a-row>
      </div>
    </section>

    <!-- 加入我们 -->
    <section class="join-section" v-if="!isLoggedIn">
      <div class="join-content">
        <h2>欢迎加入 MIRACLE</h2>
        <p>立即注册，开启智能制造新征程</p>
        <a-button type="primary" size="large" @click="goToRegister">免费注册</a-button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { EnvironmentOutlined, PictureOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getBanners, getHotProducts, getFeaturedCompanies } from '@/api/home'

const router = useRouter()
const banners = ref([])

// 判断是否已登录
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

// 数据
const hotProducts = ref([])
const featuredCompanies = ref([])

// 加载首页数据
const loadHomeData = async () => {
  try {
    // 并行加载所有数据
    const [
      bannersRes,
      productsRes,
      companiesRes
    ] = await Promise.all([
      getBanners(),
      getHotProducts(),
      getFeaturedCompanies()
    ])

    console.log('轮播图数据:', bannersRes)
    banners.value = bannersRes.data || []
    console.log('处理后的轮播图数据:', banners.value)
    hotProducts.value = productsRes.data || []
    featuredCompanies.value = companiesRes.data || []
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}

// 跳转函数
const goToActivity = (id) => {
  router.push(`/activity/detail/${id}`)
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const goToCompany = (id) => {
  router.push(`/company/${id}`)
}

const goToRegister = () => {
  router.push('/register')
}

// 初始化
onMounted(() => {
  loadHomeData()
})
</script>

<style scoped lang="less">
.home {
  .banner-section {
    margin-bottom: 40px;
    background: #fff;
    padding: 24px;

    :deep(.ant-carousel) {
      max-width: 1200px;
      margin: 0 auto;
      
      .banner-item {
        height: 300px;
        overflow: hidden;
        position: relative;
        border-radius: 8px;
        cursor: pointer;
        
        .banner-placeholder {
          width: 100%;
          height: 100%;
          background: #f0f2f5;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: #bfbfbf;
          font-size: 16px;

          .anticon {
            font-size: 48px;
            margin-bottom: 16px;
          }
        }

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .banner-content {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          padding: 20px;
          background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
          color: #fff;

          h3 {
            font-size: 24px;
            margin-bottom: 8px;
          }

          p {
            font-size: 14px;
            margin: 0;
            opacity: 0.8;
          }
        }
      }
    }
  }

  .section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 24px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .section-title {
        font-size: 24px;
        font-weight: bold;
        margin: 0;
      }

      .more-link {
        color: #1890ff;
        cursor: pointer;

        &:hover {
          color: #40a9ff;
        }
      }
    }

    .product-card {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      :deep(.ant-card-meta-title) {
        font-size: 16px;
        margin-bottom: 8px;
      }

      .product-info {
        .product-price {
          margin-bottom: 8px;
          
          .price {
            font-size: 16px;
            font-weight: bold;
            color: #f5222d;
          }

          .unit {
            font-size: 12px;
            color: #999;
            margin-left: 4px;
          }
        }

        .product-company {
          font-size: 12px;
          color: #666;
        }
      }
    }

    .company-card {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      :deep(.ant-card-meta-title) {
        font-size: 16px;
        margin-bottom: 8px;
      }

      .company-info {
        .company-desc {
          font-size: 14px;
          color: #666;
          margin-bottom: 8px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .company-location {
          font-size: 12px;
          color: #999;

          .anticon {
            margin-right: 4px;
          }
        }
      }
    }
  }

  .join-section {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    padding: 60px 24px;
    margin-top: 40px;
    text-align: center;
    color: #fff;

    .join-content {
      max-width: 600px;
      margin: 0 auto;

      h2 {
        font-size: 32px;
        font-weight: bold;
        margin-bottom: 16px;
        color: #fff;
      }

      p {
        font-size: 16px;
        margin-bottom: 24px;
        opacity: 0.8;
      }

      .ant-btn {
        height: 44px;
        padding: 0 32px;
        font-size: 16px;
        background: #fff;
        border-color: #fff;
        color: #1890ff;

        &:hover {
          background: #f0f0f0;
          border-color: #f0f0f0;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .home {
    .banner-section {
      :deep(.ant-carousel) {
        .banner-item {
          height: 200px;
        }
      }
    }

    .section {
      padding: 0 16px;
    }

    .join-section {
      padding: 48px 16px;

      .join-content {
        h2 {
          font-size: 24px;
        }

        p {
          font-size: 14px;
        }
      }
    }
  }
}

@keyframes ant-skeleton-loading {
  0% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
  }
}
</style>