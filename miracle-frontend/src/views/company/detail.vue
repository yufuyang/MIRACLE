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
            <div class="meta-item">
              <environment-outlined />
              <span>地址：{{ company.address }}</span>
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
          <link-outlined />
          <a :href="company.website" target="_blank">{{ company.website }}</a>
        </div>
        <div class="contact-item">
          <mail-outlined />
          <span>{{ company.email }}</span>
        </div>
        <div class="contact-item">
          <environment-outlined />
          <span>{{ company.address }}</span>
        </div>
      </div>
    </div>

    <!-- 标签页内容 -->
    <div class="tab-content">
      <a-tabs>
        <!-- 公司简介 -->
        <a-tab-pane key="1" tab="公司简介">
          <div class="company-intro">
            <div class="section">
              <h2>公司介绍</h2>
              <div class="content">{{ company.description }}</div>
            </div>
            <div class="section">
              <h2>主营业务</h2>
              <div class="content">{{ company.business }}</div>
            </div>
            <div class="section">
              <h2>公司图片</h2>
              <div class="image-list">
                <div v-for="(image, index) in company.images" :key="index" class="image-item">
                  <img :src="image" :alt="company.companyName" />
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>

        <!-- 公司产品 -->
        <a-tab-pane key="2" tab="公司产品">
          <div class="company-products">
            <a-row :gutter="[24, 24]">
              <a-col :span="8" v-for="product in company.products" :key="product.id">
                <a-card hoverable class="product-card" @click="goToProduct(product.id)">
                  <template #cover>
                    <img :alt="product.productName" :src="product.imageUrl || defaultImage" />
                  </template>
                  <a-card-meta :title="product.productName">
                    <template #description>
                      <div class="product-info">
                        <div class="product-desc">{{ product.description }}</div>
                        <div class="stats">
                          <span><eye-outlined /> {{ product.viewCount }} 浏览量</span>
                          <span><heart-outlined /> {{ product.intentionCount }} 意向数</span>
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
                :total="company.products.length"
                :pageSize="9"
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
import { 
  ShopOutlined, 
  HeartOutlined, 
  EnvironmentOutlined,
  LinkOutlined,
  MailOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'

const route = useRoute()
const router = useRouter()
const productPage = ref(1)

// 模拟公司数据
const company = ref({
  id: 1,
  companyName: '科技创新有限公司',
  logo: defaultImage,
  description: '专注于人工智能和大数据技术的创新型企业，致力于为客户提供智能化解决方案。',
  business: '人工智能应用开发、大数据分析、智能制造解决方案等。',
  address: '上海市浦东新区张江高科技园区',
  website: 'https://www.example.com',
  email: 'contact@example.com',
  productCount: 12,
  intentionCount: 1580,
  images: [defaultImage, defaultImage, defaultImage],
  products: [
    {
      id: 1,
      productName: '高性能工业机器人',
      description: '智能化工业机器人，适用于各类制造场景',
      imageUrl: defaultImage,
      viewCount: 1234,
      intentionCount: 56
    },
    {
      id: 2,
      productName: '智能包装生产线',
      description: '全自动智能包装系统，提高生产效率',
      imageUrl: defaultImage,
      viewCount: 890,
      intentionCount: 45
    },
    {
      id: 3,
      productName: '工业物联网传感器',
      description: '高精度工业传感器，实时监控生产数据',
      imageUrl: defaultImage,
      viewCount: 2345,
      intentionCount: 120
    }
  ]
})

// 跳转到产品详情
const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 分页变化
const onPageChange = (page) => {
  productPage.value = page
}

// 初始化
onMounted(() => {
  const id = route.params.id
  // 这里应该根据id获取公司详情
  console.log('公司ID:', id)
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
      .section {
        margin-bottom: 32px;

        h2 {
          font-size: 18px;
          font-weight: 500;
          margin-bottom: 16px;
          color: #333;
        }

        .content {
          color: #666;
          line-height: 1.8;
        }

        .image-list {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 16px;

          .image-item {
            aspect-ratio: 16/9;
            border-radius: 8px;
            overflow: hidden;

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
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
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        :deep(.ant-card-cover) {
          img {
            height: 200px;
            object-fit: cover;
          }
        }

        .product-info {
          .product-desc {
            color: #666;
            font-size: 14px;
            margin: 8px 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
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