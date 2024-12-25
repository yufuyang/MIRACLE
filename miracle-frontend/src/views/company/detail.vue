<template>
  <div class="company-detail">
    <!-- 公司基本信息 -->
    <div class="company-header">
      <div class="company-info">
        <div class="company-logo">
          <img :src="company.logo || defaultImage" :alt="company.companyName" />
        </div>
        <div class="company-meta">
          <h1>{{ company.companyName }}</h1>
          <div class="meta-list">
            <div class="meta-item">
              <shop-outlined />
              <span>产品数：{{ company.productCount || 0 }}</span>
            </div>
            <div class="meta-item">
              <heart-outlined />
              <span>意向数：{{ company.intentionCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 联系方式 -->
    <div class="contact-info">
      <div class="contact-title">联系方式</div>
      <div class="contact-list">
        <div class="contact-item">
          <user-outlined />
          <span>联系人：{{ company.contactName }}</span>
        </div>
        <div class="contact-item">
          <phone-outlined />
          <span>联系电话：{{ company.contactPhone }}</span>
        </div>
      </div>
    </div>

    <!-- 标签页内容 -->
    <div class="tab-content">
      <a-tabs>
        <!-- 公司介绍 -->
        <a-tab-pane key="1" tab="公司介绍">
          <div class="company-intro">
            <div class="section">
              <div class="content">{{ company.description }}</div>
            </div>
          </div>
        </a-tab-pane>

        <!-- 公司图片 -->
        <a-tab-pane key="2" tab="公司图片">
          <div class="company-images">
            <div class="image-list">
              <div v-for="(image, index) in environmentImages" :key="index" class="image-item">
                <img :src="image.url" :alt="company.companyName" />
              </div>
            </div>
          </div>
        </a-tab-pane>

        <!-- 公司产品 -->
        <a-tab-pane key="3" tab="公司产品">
          <div class="company-products">
            <a-row :gutter="[24, 24]">
              <a-col :span="8" v-for="product in products" :key="product.id">
                <a-card hoverable class="product-card" @click="goToProduct(product.id)">
                  <template #cover>
                    <img :alt="product.productName" :src="product.imageUrl || defaultImage" />
                  </template>
                  <a-card-meta :title="product.productName">
                    <template #description>
                      <div class="product-info">
                        <div class="product-stats">
                          <span class="stat-item">
                            <eye-outlined /> 浏览数：{{ product.viewCount || 0 }}
                          </span>
                          <span class="stat-item">
                            <heart-outlined /> 意向数：{{ product.intentionCount || 0 }}
                          </span>
                        </div>
                      </div>
                    </template>
                  </a-card-meta>
                </a-card>
              </a-col>
            </a-row>
            <!-- 分页 -->
            <div class="pagination">
              <a-pagination
                v-model:current="productPage"
                :total="productTotal"
                :pageSize="productPageSize"
                show-size-changer
                @change="onPageChange"
              />
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  ShopOutlined, 
  HeartOutlined, 
  PhoneOutlined,
  UserOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'
import { getCompanyDetail } from '@/api/website/company'
import { getCompanyProducts } from '@/api/website/companyProduct'
import { getEnvironmentImages } from '@/api/website/environment'
import defaultImage from '@/assets/images/default.jpg'

const route = useRoute()
const router = useRouter()

// 数据
const company = ref({})
const products = ref([])
const productPage = ref(1)
const productPageSize = ref(9)
const productTotal = ref(0)
const environmentImages = ref([])
const companyId = ref(null)

// 获取公司详情
const loadCompanyDetail = async () => {
  try {
    const res = await getCompanyDetail(companyId.value)
    company.value = {
      ...res.data,
      productCount: route.query.productCount,
      intentionCount: route.query.intentionCount
    }
  } catch (error) {
    console.error('获取公司详情失败:', error)
    message.error('获取公司详情失败')
  }
}

// 获取公司产品
const loadCompanyProducts = async () => {
  try {
    const params = {
      companyId: companyId.value,
      pageNum: productPage.value,
      pageSize: productPageSize.value
    }
    const res = await getCompanyProducts(params)
    products.value = res.data || []
    productTotal.value = res.total || 0
  } catch (error) {
    console.error('获取公司产品失败:', error)
    message.error('获取公司产品失败')
  }
}

// 获取环境图片
const loadEnvironmentImages = async () => {
  try {
    const res = await getEnvironmentImages(companyId.value, 'company')
    if (res.data) {
      environmentImages.value = res.data
    }
  } catch (error) {
    console.error('获取环境图片失败:', error)
    message.error('获取环境图片失败')
  }
}

// 跳转到产品详情
const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 分页变化
const onPageChange = (page, pageSize) => {
  productPage.value = page
  productPageSize.value = pageSize
  loadCompanyProducts()
}

// 初始化
onMounted(async () => {
  const id = route.params.id
  if (id) {
    companyId.value = id
    await Promise.all([
      loadCompanyDetail(),
      loadCompanyProducts(),
      loadEnvironmentImages()
    ])
  }
})
</script>

<style scoped lang="less">
.company-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;

  .company-header {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .company-info {
      display: flex;
      gap: 24px;

      .company-logo {
        width: 120px;
        height: 120px;
        border-radius: 8px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .company-meta {
        flex: 1;

        h1 {
          font-size: 24px;
          font-weight: 600;
          margin-bottom: 16px;
          color: #333;
        }

        .meta-list {
          display: flex;
          gap: 24px;

          .meta-item {
            display: flex;
            align-items: center;
            color: #666;
            font-size: 14px;

            .anticon {
              margin-right: 8px;
              font-size: 16px;
            }
          }
        }
      }
    }
  }

  .contact-info {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .contact-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 16px;
      color: #333;
    }

    .contact-list {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;

      .contact-item {
        display: flex;
        align-items: center;
        color: #666;
        font-size: 14px;
        margin-right: 24px;

        .anticon {
          margin-right: 8px;
          font-size: 16px;
        }

        a {
          color: #1890ff;
          &:hover {
            text-decoration: underline;
          }
        }
      }
    }
  }

  .tab-content {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .company-intro {
      .content {
        color: #666;
        line-height: 1.8;
        font-size: 14px;
        padding: 16px;
        background: #fafafa;
        border-radius: 4px;
      }
    }

    .company-images {
      .image-list {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 16px;

        .image-item {
          aspect-ratio: 16/9;
          border-radius: 8px;
          overflow: hidden;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: transform 0.3s ease;

          &:hover {
            transform: translateY(-4px);
          }

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }
    }

    .company-products {
      .product-card {
        height: 100%;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-4px);
        }

        :deep(.ant-card-meta-title) {
          margin-bottom: 12px;
        }

        .product-info {
          .product-stats {
            display: flex;
            gap: 16px;
            color: #666;
            font-size: 14px;

            .stat-item {
              display: flex;
              align-items: center;
              gap: 4px;

              .anticon {
                font-size: 16px;
              }
            }
          }
        }
      }

      .pagination {
        margin-top: 24px;
        text-align: center;
      }
    }
  }
}

@media (max-width: 768px) {
  .company-detail {
    padding: 16px;

    .company-header {
      padding: 16px;

      .company-info {
        flex-direction: column;
        align-items: center;
        text-align: center;

        .company-logo {
          width: 100px;
          height: 100px;
        }

        .company-meta {
          .meta-list {
            justify-content: center;
            flex-wrap: wrap;
          }
        }
      }
    }

    .tab-content {
      padding: 16px;

      .company-intro {
        .section {
          .image-list {
            grid-template-columns: 1fr;
          }
        }
      }
    }
  }
}
</style> 