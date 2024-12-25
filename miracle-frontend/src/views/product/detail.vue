<template>
  <div class="product-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <a-page-header
        :title="product?.productName"
        @back="() => router.back()"
      >
        <template #extra>
          <a-space>
            <a-button type="primary" @click="handleIntention">
              <template #icon><heart-outlined /></template>
              {{ isLoggedIn ? '添加意向' : '登录后添加意向' }}
            </a-button>
            <a-button @click="goToCompany">
              <template #icon><team-outlined /></template>
              查看企业
            </a-button>
          </a-space>
        </template>
      </a-page-header>
    </div>

    <a-spin :spinning="loading">
      <!-- 产品图片 -->
      <a-card title="产品图片" style="margin-bottom: 24px">
        <div class="product-images">
          <div class="image-grid">
            <div 
              v-for="(image, index) in productImages.slice(0, 8)" 
              :key="image.id"
              class="image-item"
            >
              <a-image
                :src="image.imageUrl || defaultImage"
                :alt="product.productName"
                :preview="true"
              />
              <div class="image-tag" v-if="index === 0">主图</div>
            </div>
          </div>
        </div>
      </a-card>

      <!-- 基本信息 -->
      <a-card title="基本信息" style="margin-bottom: 24px">
        <a-descriptions :column="2">
          <a-descriptions-item label="产品名称">{{ product?.productName }}</a-descriptions-item>
          <a-descriptions-item label="产品编号">{{ product?.productCode }}</a-descriptions-item>
          <a-descriptions-item label="产品分类">{{ getCategoryName(product?.categoryId) }}</a-descriptions-item>
          <a-descriptions-item label="发布时间">{{ formatDate(product?.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="产品描述" :span="2">{{ product?.description || '-' }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <!-- 数据统计 -->
      <a-card title="数据统计" style="margin-bottom: 24px">
        <div class="stats-list">
          <div class="stat-item">
            <eye-outlined />
            <div class="stat-info">
              <div class="label">浏览量</div>
              <div class="value">{{ product?.viewCount || 0 }}</div>
            </div>
          </div>
          <div class="stat-item">
            <heart-outlined />
            <div class="stat-info">
              <div class="label">意向数</div>
              <div class="value">{{ product?.intentionCount || 0 }}</div>
            </div>
          </div>
          <div class="stat-item">
            <clock-circle-outlined />
            <div class="stat-info">
              <div class="label">在线时长</div>
              <div class="value">{{ formatDuration(product?.createTime) }}</div>
            </div>
          </div>
        </div>
      </a-card>

      <!-- 公司信息 -->
      <a-card title="公司信息">
        <div class="company-content">
          <div class="company-logo">
            <img :src="company?.logoUrl || defaultImage" :alt="company?.companyName" />
          </div>
          <div class="company-details">
            <div class="company-name">{{ company?.companyName }}</div>
            <div class="company-info">
              <div class="info-item">
                <environment-outlined />
                <span>{{ company?.province }} {{ company?.city }} {{ company?.address }}</span>
              </div>
              <div class="info-item">
                <phone-outlined />
                <span>{{ company?.contactPhone }}</span>
              </div>
              <div class="info-item">
                <user-outlined />
                <span>联系人：{{ company?.contactName }}</span>
              </div>
            </div>
            <div class="company-desc">{{ company?.companyDesc }}</div>
          </div>
        </div>
      </a-card>
    </a-spin>

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
import { ref, onMounted, reactive, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  HeartOutlined,
  TeamOutlined,
  EnvironmentOutlined,
  EyeOutlined,
  ClockCircleOutlined,
  PhoneOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import { getProductDetail, getProductImages, getProductCategories, addIntention } from '@/api/product'
import { getCompanyDetail } from '@/api/company'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const currentImageIndex = ref(0)
const loading = ref(false)

// 默认图片
const defaultImage = 'https://via.placeholder.com/200x200'

// 判断是否已登录
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

// 产品数据
const product = ref({})
const productImages = ref([])
const categories = ref([])

// 公司数据
const company = ref({})

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
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
  } catch (error) {
    console.error('获取产品详情失败:', error)
    message.error('获取产品详情失败')
  } finally {
    loading.value = false
  }
}

// 获取产品图片
const loadProductImages = async () => {
  try {
    const res = await getProductImages(route.params.id)
    productImages.value = res.data || []
  } catch (error) {
    console.error('获取产品图片失败:', error)
    message.error('获取产品图片失败')
  }
}

// 获取分类列表
const loadCategories = async () => {
  try {
    const res = await getProductCategories({})
    categories.value = res.data || []
  } catch (error) {
    console.error('获取���类列表失败:', error)
    message.error('获取分类列表失败')
  }
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const findCategory = (id) => categories.value.find(c => c.id === id)
  const category = findCategory(categoryId)
  if (!category) return '-'
  
  if (category.parentId) {
    const parentCategory = findCategory(category.parentId)
    return parentCategory ? `${parentCategory.categoryName} / ${category.categoryName}` : category.categoryName
  }
  return category.categoryName
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 处理轮播图切换
const handleCarouselChange = (current) => {
  currentImageIndex.value = current
}

// 处理略图点击
const handleThumbnailClick = (index) => {
  currentImageIndex.value = index
}

// 处理添加意向
const handleIntention = () => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  intentionVisible.value = true
}

// 跳转到企业详情
const goToCompany = () => {
  router.push(`/company/${product.value.companyId}`)
}

// 提交意向
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
      message.error('添加意向失败')
    } finally {
      intentionLoading.value = false
    }
  })
}

// 添加一个格式化时长的方法
const formatDuration = (startTime) => {
  if (!startTime) return '-'
  const start = dayjs(startTime)
  const now = dayjs()
  const days = now.diff(start, 'day')
  const months = now.diff(start, 'month')
  const years = now.diff(start, 'year')
  
  if (years > 0) {
    return `${years}年${months % 12}月`
  } else if (months > 0) {
    return `${months}月${days % 30}天`
  } else {
    return `${days}天`
  }
}

// 获取公司详情
const loadCompanyDetail = async () => {
  try {
    const res = await getCompanyDetail(product.value?.companyId)
    company.value = res.data
  } catch (error) {
    console.error('获取公司详情失败:', error)
    message.error('获取公司详情失败')
  }
}

// 初始化
onMounted(() => {
  loadCategories()
  loadProductDetail()
  loadProductImages()
})

// 监听产品数据变化，获取公司详情
watch(() => product.value?.companyId, (newVal) => {
  if (newVal) {
    loadCompanyDetail()
  }
})
</script>

<style scoped lang="less">
.product-detail {
  padding: 24px;
  background: #f5f5f5;

  .page-header {
    background: #fff;
    margin-bottom: 24px;
  }

  .company-content {
    display: flex;
    padding: 16px;

    .company-logo {
      width: 120px;
      height: 120px;
      margin-right: 24px;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid #f0f0f0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .company-details {
      flex: 1;

      .company-name {
        font-size: 20px;
        font-weight: 500;
        color: #262626;
        margin-bottom: 16px;
      }

      .company-info {
        margin-bottom: 16px;

        .info-item {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          color: #595959;

          .anticon {
            margin-right: 8px;
            color: #1890ff;
          }
        }
      }

      .company-desc {
        color: #8c8c8c;
        font-size: 14px;
        line-height: 1.5;
      }
    }
  }

  .product-images {
    .image-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 24px;
      padding: 16px;

      .image-item {
        position: relative;
        aspect-ratio: 1;
        border-radius: 8px;
        overflow: hidden;
        border: 1px solid #f0f0f0;
        background: #fafafa;
        min-height: 240px;

        :deep(.ant-image) {
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;

          img {
            width: 100%;
            height: 100%;
            object-fit: contain;
            padding: 16px;
          }
        }

        .image-tag {
          position: absolute;
          top: 8px;
          left: 8px;
          padding: 2px 8px;
          background: rgba(24, 144, 255, 0.9);
          color: #fff;
          border-radius: 4px;
          font-size: 12px;
        }

        &:hover {
          border-color: #1890ff;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }

  .stats-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;

    .stat-item {
      display: flex;
      align-items: center;
      padding: 16px;
      background: #fafafa;
      border-radius: 4px;

      .anticon {
        font-size: 24px;
        color: #1890ff;
        margin-right: 16px;
      }

      .stat-info {
        flex: 1;

        .label {
          font-size: 14px;
          color: #8c8c8c;
          margin-bottom: 4px;
        }

        .value {
          font-size: 24px;
          font-weight: 500;
          color: #262626;
        }
      }
    }
  }
}
</style> 