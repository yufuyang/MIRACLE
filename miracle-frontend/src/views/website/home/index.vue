<template>
  <div class="home">
    <!-- 左侧统计卡片 -->
    <div class="stat-card left">
      <div class="stat-content">
        <div class="icon-wrapper">
          <bank-outlined class="icon" />
        </div>
        <div class="stat-info">
          <div class="label">入驻企业</div>
          <div class="number">{{ statistics.companyCount }}</div>
          <div class="desc">累计入驻企业数量</div>
        </div>
      </div>
    </div>

    <!-- 右侧统计卡片 -->
    <div class="stat-card right">
      <div class="stat-content">
        <div class="icon-wrapper">
          <shop-outlined class="icon" />
        </div>
        <div class="stat-info">
          <div class="label">入驻商户</div>
          <div class="number">{{ statistics.merchantCount }}</div>
          <div class="desc">累计入驻商户数量</div>
        </div>
      </div>
    </div>

    <!-- 轮播图区域的容器 -->
    <div class="banner-container">
      <!-- 中间轮播图（展示产品） -->
      <div class="banner-section">
        <a-carousel autoplay>
          <template v-if="featuredProducts && featuredProducts.length > 0">
            <div 
              class="banner-item" 
              v-for="product in featuredProducts.slice(0, 8)" 
              :key="product.id" 
              @click="goToProduct(product.id)"
            >
              <img :src="product.imageUrl || defaultImage" :alt="product.productName" />
              <div class="banner-content">
                <h3>{{ product.productName }}</h3>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="banner-item">
              <div class="banner-placeholder">
                <picture-outlined />
                <span>暂无产品</span>
              </div>
            </div>
          </template>
        </a-carousel>
      </div>
    </div>

    <!-- 热门活动 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">热门活动</h2>
        <a class="more-link" @click="goToActivity()">查看全部 ></a>
      </div>
      <div class="activity-grid">
        <a-row :gutter="[24, 24]">
          <template v-if="activities.length > 0">
            <a-col :span="6" v-for="activity in activities.slice(0, 8)" :key="activity.id">
              <a-card hoverable class="activity-card" @click="goToActivity(activity.id)">
                <template #cover>
                  <a-image
                    :src="activity.coverImage || defaultImage"
                    :alt="activity.title"
                    :preview="false"
                    style="height: 200px; object-fit: cover"
                  />
                </template>
                <a-card-meta :title="activity.title">
                  <template #description>
                    <div class="activity-info">
                      <div class="activity-stats">
                        <span class="stat-item">
                          <eye-outlined /> 浏览数：{{ activity.viewCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <user-outlined /> 报名数：{{ activity.registerCount || 0 }}
                        </span>
                      </div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </template>
          <template v-else>
            <a-col :span="6" v-for="i in 8" :key="i">
              <a-card class="activity-card empty-card">
                <template #cover>
                  <a-skeleton-image :active="true" />
                </template>
                <a-card-meta>
                  <template #title>
                    <a-skeleton :active="true" :paragraph="false" />
                  </template>
                  <template #description>
                    <div class="activity-info">
                      <div class="activity-desc">
                        <a-skeleton :active="true" :paragraph="{ rows: 2 }" :title="false" />
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
            <a-col :span="6" v-for="company in featuredCompanies.slice(0, 8)" :key="company.id">
              <a-card hoverable class="company-card" @click="goToCompany(company.id)">
                <template #cover>
                  <img :alt="company.companyName" :src="company.logoUrl || defaultImage" />
                </template>
                <a-card-meta :title="company.companyName">
                  <template #description>
                    <div class="company-info">
                      <p>{{ company.province }} {{ company.city }}</p>
                      <div class="stats">
                        <span>
                          <ShopOutlined /> 产品数：{{ company.productCount || 0 }}
                        </span>
                        <span>
                          <HeartOutlined /> 意向数：{{ company.intentionCount || 0 }}
                        </span>
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
import { 
  EnvironmentOutlined, 
  PictureOutlined, 
  EyeOutlined, 
  HeartOutlined, 
  UserOutlined, 
  ShopOutlined, 
  BankOutlined 
} from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getFeaturedProducts, getHotActivities, getFeaturedCompanies } from '@/api/home'
import { getHomeStatistics } from '@/api/website'
import dayjs from 'dayjs'

const router = useRouter()
const featuredProducts = ref([])
const activities = ref([])
const featuredCompanies = ref([])

// 判断是否已登录
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

// 加载首页数据
const loadHomeData = async () => {
  try {
    // 并行加载所有数据
    const [
      productsRes,
      activitiesRes,
      companiesRes
    ] = await Promise.all([
      getFeaturedProducts(),
      getHotActivities(),
      getFeaturedCompanies()
    ])

    featuredProducts.value = productsRes.data || []
    activities.value = activitiesRes.data || []
    featuredCompanies.value = companiesRes.data || []
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 跳转函数
const goToActivity = (id) => {
  if (id) {
    router.push(`/activity/${id}`)
  } else {
    router.push('/activity')
  }
}

const goToProduct = (id) => {
  if (id) {
    router.push(`/product/${id}`)
  } else {
    router.push('/product')
  }
}

const goToCompany = (id) => {
  if (id) {
    router.push(`/company/${id}`)
  } else {
    router.push('/company')
  }
}

const goToRegister = () => {
  router.push('/register')
}

// 添加统计数据
const statistics = ref({
  companyCount: 0,    // 初始值设为0
  merchantCount: 0    // 初始值设为0
})

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const { data } = await getHomeStatistics()
    statistics.value = data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadHomeData()
  fetchStatistics()  // 添加获取统计数据的调用
})
</script>

<style scoped lang="less">
.home {
  position: relative;  // 添加相对定位
  max-width: 1200px;  // 保持原有的最大宽度
  margin: 0 auto;     // 居中显示
  padding: 0 24px;    // 添加内边距

  .stat-card {
    position: fixed;  // 改为固定定位
    top: 50%;        // 垂直居中
    transform: translateY(-50%);
    width: 200px;
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    z-index: 100;

    &:hover {
      transform: translateY(-50%) translateX(5px);  // 修改悬浮效果
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
    }

    &.left {
      left: 40px;  // 固定距离左侧40px
    }

    &.right {
      right: 40px;  // 固定距离右侧40px
    }

    .stat-content {
      .icon-wrapper {
        width: 48px;
        height: 48px;
        border-radius: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 16px;
        
        &.left {
          background: linear-gradient(135deg, #1890ff20 0%, #1890ff40 100%);
          .icon {
            color: #1890ff;
          }
        }
        
        &.right {
          background: linear-gradient(135deg, #52c41a20 0%, #52c41a40 100%);
          .icon {
            color: #52c41a;
          }
        }

        .icon {
          font-size: 24px;
        }
      }

      .stat-info {
        .label {
          font-size: 16px;
          color: rgba(0, 0, 0, 0.65);
          margin-bottom: 8px;
        }

        .number {
          font-size: 32px;
          font-weight: bold;
          color: #1890ff;
          margin-bottom: 8px;
        }

        .desc {
          font-size: 14px;
          color: rgba(0, 0, 0, 0.45);
        }
      }
    }
  }

  .banner-container {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    margin-bottom: 40px;

    .banner-section {
      margin: 0 auto;
      width: 100%;
    }
  }

  .banner-section {
    margin-bottom: 40px;
    background: #fff;

    :deep(.ant-carousel) {
      max-width: 1200px;
      margin: 0 auto;
      
      .banner-item {
        height: 600px;
        overflow: hidden;
        position: relative;
        border-radius: 8px;
        cursor: pointer;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .banner-content {
          position: absolute;
          left: 0;
          right: 0;
          bottom: 0;
          padding: 40px;
          background: linear-gradient(to top, rgba(0,0,0,0.9), rgba(0,0,0,0));
          
          h3 {
            color: #fff;
            font-size: 32px;
            margin: 0;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.6);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
        
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
      }
    }
  }

  .section {
    max-width: 1200px;
    margin: 0 auto 40px;
    background: #fff;
    padding: 40px 0;
    position: relative;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);

    &:not(:last-child) {
      &::after {
        content: '';
        position: absolute;
        left: 5%;
        right: 5%;
        bottom: -20px;
        height: 2px;
        background: linear-gradient(to right, 
          transparent,
          #e8e8e8 10%,
          #d9d9d9 50%,
          #e8e8e8 90%,
          transparent
        );
      }
    }

    .section-header {
      padding: 0 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .section-title {
        font-size: 24px;
        font-weight: 600;
        color: #262626;
        position: relative;
        padding-left: 12px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 20px;
          background: #1890ff;
          border-radius: 2px;
        }
      }

      .more-link {
        color: #1890ff;
        font-size: 14px;
        cursor: pointer;

        &:hover {
          color: #40a9ff;
        }
      }
    }

    .activity-grid,
    .company-grid {
      padding: 0 24px;
    }

    .product-grid,
    .company-grid {
      padding: 0 24px;
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
        .product-stats {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;
          
          .stat-item {
            color: #8c8c8c;
            font-size: 14px;

            .anticon {
              margin-right: 4px;
              color: #1890ff;
            }
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
      margin-bottom: 24px;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      :deep(.ant-card-cover) {
        height: 200px;
        overflow: hidden;
        background: #f5f5f5;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          padding: 0;
        }
      }

      :deep(.ant-card-meta) {
        padding: 16px;

        .ant-card-meta-title {
          font-size: 16px;
          margin-bottom: 12px;
          color: #262626;
        }
      }

      .company-info {
        p {
          margin: 0 0 12px;
          color: #666;
          font-size: 14px;
          display: flex;
          align-items: center;
          gap: 4px;

          .anticon {
            color: #1890ff;
            font-size: 14px;
          }
        }

        .stats {
          display: flex;
          justify-content: space-between;
          color: #666;
          font-size: 14px;

          span {
            display: flex;
            align-items: center;
            gap: 4px;

            .anticon {
              color: #1890ff;
            }
          }
        }
      }
    }
  }

  .join-section {
    background: #fff;
    padding: 60px 24px;
    margin-top: 40px;
    text-align: center;
    border-top: 1px solid #f0f0f0;

    .join-content {
      max-width: 600px;
      margin: 0 auto;

      h2 {
        font-size: 32px;
        font-weight: bold;
        margin-bottom: 16px;
        color: #1890ff;
      }

      p {
        font-size: 16px;
        margin-bottom: 24px;
        color: #666;
        opacity: 0.8;
      }

      .ant-btn {
        height: 44px;
        padding: 0 32px;
        font-size: 16px;
        background: #1890ff;
        border-color: #1890ff;
        color: #fff;

        &:hover {
          background: #40a9ff;
          border-color: #40a9ff;
        }
      }
    }
  }

  .activity-grid {
    .activity-card {
      height: 100%;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      }

      :deep(.ant-card-meta-title) {
        font-size: 16px;
        margin-bottom: 8px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .activity-info {
        .activity-stats {
          display: flex;
          justify-content: space-between;
          align-items: center;
          color: #666;
          font-size: 14px;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 4px;

            .anticon {
              color: #1890ff;
            }
          }
        }
      }
    }
  }

  // 响应式处理
  @media (max-width: 1400px) {
    .stat-card {
      width: 180px;
    }
  }

  @media (max-width: 1200px) {
    .stat-card {
      display: none;  // 在小屏幕上隐藏
    }
  }
}

@media (max-width: 768px) {
  .home {
    .banner-section {
      :deep(.ant-carousel) {
        .banner-item {
          height: 400px;
          
          .banner-content {
            padding: 30px;
            
            h3 {
              font-size: 24px;
            }
          }
        }
      }
    }

    .section {
      margin: 0 16px 40px;
      padding: 30px 0;

      .section-header {
        padding: 0 16px;
      }

      .activity-grid,
      .company-grid {
        padding: 0 16px;
      }
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

.activity-card {
  height: 100%;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  }

  :deep(.ant-card-meta-title) {
    font-size: 16px;
    margin-bottom: 8px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .activity-info {
    .activity-stats {
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: #666;
      font-size: 14px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 4px;

        .anticon {
          color: #1890ff;
        }
      }
    }
  }
}
</style>