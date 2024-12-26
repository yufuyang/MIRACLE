<template>
  <div class="product-page">
    <div class="product-list">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="产品名称">
            <a-input
              v-model:value="searchForm.productName"
              placeholder="请输入产品名称"
              allowClear
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="onSearch">查询</a-button>
            <a-button style="margin-left: 8px" @click="onReset">重置</a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 排序区域 -->
      <div class="sort-section">
        <a-radio-group v-model:value="searchForm.orderField" @change="onSearch">
          <a-radio-button value="">默认排序</a-radio-button>
          <a-radio-button value="viewCount">
            浏览数
            <up-outlined v-if="searchForm.orderField === 'viewCount' && searchForm.asc" />
            <down-outlined v-if="searchForm.orderField === 'viewCount' && !searchForm.asc" />
          </a-radio-button>
          <a-radio-button value="intentionCount">
            意向数
            <up-outlined v-if="searchForm.orderField === 'intentionCount' && searchForm.asc" />
            <down-outlined v-if="searchForm.orderField === 'intentionCount' && !searchForm.asc" />
          </a-radio-button>
        </a-radio-group>
      </div>

      <!-- 产品列表 -->
      <div class="product-grid">
        <a-row :gutter="[24, 24]">
          <template v-if="products.length">
            <a-col :xs="24" :sm="12" :md="8" v-for="product in products" :key="product.id">
              <a-card hoverable class="product-card" @click="handleDetail(product)">
                <template #cover>
                  <a-image
                    :src="product.imageUrl"
                    :fallback="defaultImage"
                    :preview="false"
                    style="height: 280px; object-fit: cover"
                  />
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
                      <div class="product-company">{{ product.companyName }}</div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </template>
          <template v-else>
            <a-empty />
          </template>
        </a-row>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <a-pagination
          v-model:current="pagination.current"
          v-model:pageSize="pagination.pageSize"
          :total="pagination.total"
          show-size-changer
          show-quick-jumper
          @change="handleTableChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  RedoOutlined,
  EyeOutlined,
  HeartOutlined,
  UpOutlined,
  DownOutlined
} from '@ant-design/icons-vue'
import { getProductList } from '@/api/product'
import { useRouter } from 'vue-router'

const router = useRouter()

// 默认图片
const defaultImage = 'https://via.placeholder.com/200x200'

// 搜索表单
const searchForm = reactive({
  productName: '',
  orderField: '',  // 改为空字符串作为默认排序
  asc: true  // 默认升序
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true
})

// 数据
const loading = ref(false)
const products = ref([])
const categories = ref([])
const categoryTree = ref([])

// 获取产品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getProductList(params)
    products.value = res.data || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取产品列表失败:', error)
    message.error('获取产品列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await getProductCategories({})
    categories.value = res.data || []
    categoryTree.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败:', error)
    message.error('获取分类列表失败')
  }
}

// 搜索
const onSearch = () => {
  if (searchForm.orderField) {
    searchForm.asc = !searchForm.asc
  }
  pagination.current = 1
  fetchProducts()
}

// 重置
const onReset = () => {
  searchForm.productName = ''
  searchForm.orderField = ''
  searchForm.asc = true
  pagination.current = 1
  fetchProducts()
}

// 表格变化
const handleTableChange = (current, pageSize) => {
  pagination.current = current
  pagination.pageSize = pageSize
  fetchProducts()
}

// 查看详情
const handleDetail = (record) => {
  router.push(`/product/${record.id}`)
}

// 排序变化
const handleSortChange = () => {
  searchForm.asc = false // 每次切换排序方式时，默认为降序
  fetchProducts()
}

// 初始化
onMounted(() => {
  fetchProducts()
})
</script>

<style scoped lang="less">
.product-page {
  min-height: calc(100vh - 64px);
  background-color: #f0f2f5;
}

.product-list {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;

  .filter-section {
    background: #fff;
    padding: 24px;
    border-radius: 8px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .sort-section {
    margin-bottom: 24px;
    background: #fff;
    padding: 16px 24px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .product-grid {
    margin-bottom: 24px;
    background: #fff;
    padding: 24px;
    border-radius: 8px;

    .product-card {
      cursor: pointer;
      transition: all 0.3s;
      background: #fff;
      height: 100%;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      :deep(.ant-card-meta-title) {
        font-size: 14px;
        margin-bottom: 8px;
        white-space: normal;
        line-height: 1.3;
      }

      :deep(.ant-card-body) {
        padding: 12px;
      }

      .product-info {
        .product-stats {
          display: flex;
          justify-content: space-between;
          margin-bottom: 4px;
          
          .stat-item {
            color: #8c8c8c;
            font-size: 12px;

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
  }

  .pagination {
    text-align: center;
    margin-top: 24px;
    background: #fff;
    padding: 16px;
    border-radius: 8px;
  }
}
</style> 