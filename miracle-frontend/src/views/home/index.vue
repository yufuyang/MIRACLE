<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner-section">
      <a-carousel autoplay>
        <div class="banner-item" v-for="i in 4" :key="i">
          <div class="banner-placeholder">
            <picture-outlined />
            <span>轮播图 {{ i }}</span>
          </div>
        </div>
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
                  <img :alt="product.name" :src="product.imageUrl || defaultImage" />
                </template>
                <a-card-meta :title="product.name">
                  <template #description>
                    <div class="product-info">
                      <div class="product-desc">{{ product.description }}</div>
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

// 判断是否已登录
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

// 数据
const banners = ref([])
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

    banners.value = bannersRes.data || []
    hotProducts.value = productsRes.data || []
    featuredCompanies.value = companiesRes.data || []
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}

// 跳转函数
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

        .banner-skeleton {
          width: 100%;
          height: 100%;
          background: #f5f5f5;
          border-radius: 8px;
          overflow: hidden;

          .full-skeleton {
            width: 100%;
            height: 100%;
            
            :deep(.ant-skeleton-image) {
              width: 100%;
              height: 100%;
              border-radius: 8px;
              margin: 0;
              background: linear-gradient(90deg, #f2f2f2 25%, #e6e6e6 37%, #f2f2f2 63%);
              background-size: 400% 100%;
              animation: ant-skeleton-loading 1.4s ease infinite;
            }
          }
        }
      }

      .slick-dots {
        li {
          button {
            background: #d9d9d9;
          }

          &.slick-active {
            button {
              background: #1890ff;
            }
          }
        }
      }
    }
  }

  .section {
    max-width: 1200px;
    margin: 0 auto 48px;
    padding: 0 24px;
    background: #fff;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding-top: 24px;

      .section-title {
        font-size: 24px;
        font-weight: 600;
        margin: 0;
        color: #1f1f1f;
      }

      .more-link {
        color: #1890ff;
        cursor: pointer;

        &:hover {
          color: #40a9ff;
        }
      }
    }
  }

  .product-card,
  .company-card {
    height: 100%;
    transition: all 0.3s;

    &:not(.empty-card) {
      cursor: pointer;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
    }

    &.empty-card {
      cursor: default;
      
      :deep(.ant-skeleton-image) {
        width: 100%;
        height: 200px;
        background: #f5f5f5;
      }

      :deep(.ant-skeleton) {
        .ant-skeleton-title {
          margin: 0;
        }
      }
    }

    img {
      width: 100%;
      height: 200px;
      object-fit: cover;
    }

    .product-info,
    .company-info {
      margin-top: 12px;

      .product-desc,
      .company-desc {
        color: #666;
        font-size: 14px;
        margin-bottom: 12px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .product-company,
      .company-location {
        color: #999;
        font-size: 14px;
        display: flex;
        align-items: center;

        .anticon {
          margin-right: 8px;
        }
      }
    }
  }

  .join-section {
    background: #fff;
    padding: 80px 24px;
    text-align: center;
    margin-top: 48px;

    .join-content {
      max-width: 600px;
      margin: 0 auto;

      h2 {
        font-size: 32px;
        margin-bottom: 16px;
        color: #1f1f1f;
      }

      p {
        font-size: 16px;
        margin-bottom: 32px;
        color: #666;
      }

      .ant-btn {
        height: 48px;
        padding: 0 40px;
        font-size: 16px;
        border-radius: 24px;
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