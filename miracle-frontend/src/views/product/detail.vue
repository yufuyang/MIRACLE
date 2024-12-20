<template>
  <div class="product-detail">
    <div class="container">
      <!-- 产品信息 -->
      <div class="product-info">
        <a-row :gutter="24">
          <!-- 左侧图片展示 -->
          <a-col :span="12">
            <div class="product-images">
              <a-carousel class="main-image">
                <div v-for="image in product.images" :key="image.id">
                  <img :src="image.imageUrl || defaultImage" :alt="product.productName" />
                </div>
              </a-carousel>
              <div class="thumbnail-list">
                <div
                  v-for="image in product.images"
                  :key="image.id"
                  class="thumbnail"
                  :class="{ active: currentImage === image.id }"
                  @click="currentImage = image.id"
                >
                  <img :src="image.imageUrl || defaultImage" :alt="product.productName" />
                </div>
              </div>
            </div>
          </a-col>

          <!-- 右侧产品信息 -->
          <a-col :span="12">
            <div class="product-details">
              <h1 class="product-name">{{ product.productName }}</h1>
              <div class="product-code">产品编号：{{ product.productCode }}</div>
              <div class="price">¥ {{ product.price }}</div>
              <div class="stats">
                <span><eye-outlined /> {{ product.viewCount }} 浏览</span>
                <span><heart-outlined /> {{ product.intentionCount }} 意向</span>
              </div>
              <div class="company-info">
                <h3>供应商信息</h3>
                <p>{{ product.companyName }}</p>
              </div>
              <div class="actions">
                <a-button 
                  type="primary" 
                  size="large" 
                  @click="handleIntention"
                  :loading="intentionLoading"
                >
                  <template #icon>
                    <heart-outlined />
                  </template>
                  {{ isLoggedIn ? '添加意向' : '登录后添加意向' }}
                </a-button>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 产品详情 -->
      <div class="product-content">
        <a-tabs v-model:activeKey="activeTab">
          <a-tab-pane key="detail" tab="产品详情">
            <div class="detail-content">
              <div class="detail-text" v-html="product.description"></div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="steps" tab="产品步骤">
            <div class="steps-content">
              <a-steps :current="0" direction="vertical">
                <a-step title="准备工作" description="检查设备电源和连接是否正常" />
                <a-step title="安装配置" description="按照说明书进行安装和基础配置" />
                <a-step title="调试运行" description="进行设备调试和测试运行" />
                <a-step title="正式使用" description="开始正式使用设备进行生产" />
              </a-steps>
            </div>
          </a-tab-pane>
          <a-tab-pane key="company" tab="企业信息">
            <div class="company-detail">
              <h3>{{ product.companyName }}</h3>
              <p>{{ product.companyDescription }}</p>
              <div class="company-images">
                <img
                  v-for="image in product.companyImages"
                  :key="image.id"
                  :src="image.imageUrl || defaultImage"
                  :alt="product.companyName"
                />
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>

    <!-- 意向弹窗 -->
    <a-modal
      v-model:visible="intentionVisible"
      title="添加意向"
      @ok="submitIntention"
      :confirmLoading="intentionLoading"
    >
      <a-form :model="intentionForm" :rules="rules" ref="intentionFormRef">
        <a-form-item label="备注" name="remark">
          <a-textarea
            v-model:value="intentionForm.remark"
            :rows="4"
            placeholder="请输入意向备注"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  EyeOutlined,
  HeartOutlined
} from '@ant-design/icons-vue'
import { getProductDetail, addIntention } from '@/api/product'
import defaultImage from '@/assets/images/default.jpg'

const router = useRouter()
const route = useRoute()
const activeTab = ref('detail')
const currentImage = ref(null)

// 判断是否已登录
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

// 产品数据
const product = ref({
  productName: '',
  productCode: '',
  price: 0,
  viewCount: 0,
  intentionCount: 0,
  description: '',
  companyName: '',
  companyDescription: '',
  images: [],
  companyImages: []
})

// 意向相关
const intentionVisible = ref(false)
const intentionLoading = ref(false)
const intentionFormRef = ref(null)
const intentionForm = reactive({
  remark: ''
})
const rules = {
  remark: [{ required: true, message: '请输入意向备注', trigger: 'blur' }]
}

// 获取产品详情
const loadProductDetail = async () => {
  try {
    const data = await getProductDetail(route.params.id)
    product.value = data
    if (data.images && data.images.length > 0) {
      currentImage.value = data.images[0].id
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
  }
}

// 处理添加意向
const handleIntention = () => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  intentionVisible.value = true
}

const submitIntention = () => {
  intentionFormRef.value.validate().then(async () => {
    intentionLoading.value = true
    try {
      await addIntention({
        productId: route.params.id,
        companyId: product.value.companyId,
        remark: intentionForm.remark
      })
      message.success('添加意向成功')
      intentionVisible.value = false
      intentionForm.remark = ''
      // 重新加载产品详情，更新意向数
      loadProductDetail()
    } catch (error) {
      console.error('添加意向失败:', error)
    } finally {
      intentionLoading.value = false
    }
  })
}

// 初始化
onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped lang="less">
.product-detail {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;

    .product-info {
      background: #fff;
      padding: 24px;
      border-radius: 4px;
      margin-bottom: 24px;

      .product-images {
        .main-image {
          margin-bottom: 16px;
          
          img {
            width: 100%;
            height: 400px;
            object-fit: cover;
          }
        }

        .thumbnail-list {
          display: flex;
          gap: 8px;

          .thumbnail {
            width: 80px;
            height: 80px;
            cursor: pointer;
            border: 2px solid transparent;

            &.active {
              border-color: #1890ff;
            }

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
        }
      }

      .product-details {
        padding-left: 24px;

        .product-name {
          font-size: 24px;
          margin-bottom: 16px;
        }

        .product-code {
          color: #666;
          margin-bottom: 16px;
        }

        .price {
          font-size: 28px;
          color: #f5222d;
          margin-bottom: 16px;
        }

        .stats {
          display: flex;
          gap: 16px;
          color: #666;
          margin-bottom: 24px;
        }

        .company-info {
          margin-bottom: 24px;
          padding: 16px;
          background: #f5f5f5;
          border-radius: 4px;
        }

        .actions {
          display: flex;
          gap: 16px;
        }
      }
    }

    .product-content {
      background: #fff;
      padding: 24px;
      border-radius: 4px;

      .detail-content {
        padding: 24px 0;
      }

      .steps-content {
        padding: 40px 0;
        max-width: 800px;
        margin: 0 auto;

        :deep(.ant-steps-vertical) {
          .ant-steps-item {
            margin-bottom: 24px;
            
            .ant-steps-item-title {
              font-size: 18px;
              font-weight: 500;
            }
            
            .ant-steps-item-description {
              font-size: 14px;
              color: #666;
            }
          }
        }
      }

      .company-detail {
        padding: 24px 0;

        .company-images {
          display: flex;
          gap: 16px;
          margin-top: 16px;
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
  }
}
</style> 