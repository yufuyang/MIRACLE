<template>
  <div class="product-list">
    <!-- 筛选区域 -->
    <div class="filter-section">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="产品名称">
          <a-input
            v-model:value="searchForm.productName"
            placeholder="请输入产品名称"
            allowClear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="分类">
          <a-tree-select
            v-model:value="searchForm.categoryId"
            :tree-data="categoryTree"
            :field-names="{
              children: 'children',
              label: 'categoryName',
              value: 'id'
            }"
            placeholder="请选择分类"
            allow-clear
            tree-default-expand-all
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="价格区间">
          <a-input-number
            v-model:value="searchForm.minPrice"
            placeholder="最低价"
            style="width: 100px"
          />
          <span style="margin: 0 8px">-</span>
          <a-input-number
            v-model:value="searchForm.maxPrice"
            placeholder="最高价"
            style="width: 100px"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <template #icon>
              <search-outlined />
            </template>
            搜索
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">
            <template #icon>
              <redo-outlined />
            </template>
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 排序区域 -->
    <div class="sort-section">
      <a-radio-group v-model:value="searchForm.orderField" @change="handleSearch">
        <a-radio-button value="">默认排序</a-radio-button>
        <a-radio-button value="price">
          价格
          <up-outlined v-if="searchForm.orderField === 'price' && searchForm.asc" />
          <down-outlined v-if="searchForm.orderField === 'price' && !searchForm.asc" />
        </a-radio-button>
        <a-radio-button value="viewCount">
          浏览量
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
        <template v-if="products.length > 0">
          <a-col :span="6" v-for="product in products" :key="product.id">
            <a-card hoverable class="product-card" @click="handleDetail(product)">
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
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  RedoOutlined,
  UpOutlined,
  DownOutlined,
  TeamOutlined,
  TagOutlined,
  EyeOutlined,
  HeartOutlined
} from '@ant-design/icons-vue'
import { getProductList, getProductCategories } from '@/api/product'

const router = useRouter()

// 默认图片
const defaultImage = 'https://via.placeholder.com/200x200'

// 搜索表单
const searchForm = reactive({
  productName: '',
  categoryId: undefined,
  minPrice: undefined,
  maxPrice: undefined,
  orderField: '',
  asc: true
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

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchProducts()
}

// 重置
const handleReset = () => {
  searchForm.productName = ''
  searchForm.categoryId = undefined
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  searchForm.orderField = ''
  searchForm.asc = true
  handleSearch()
}

// 排序
const handleSort = (field) => {
  if (searchForm.orderField === field) {
    searchForm.asc = !searchForm.asc
  } else {
    searchForm.orderField = field
    searchForm.asc = true
  }
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

// 初始化
onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped lang="less">
.product-list {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;

  .filter-section {
    margin-bottom: 24px;
    padding: 24px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .sort-section {
    margin-bottom: 24px;
    padding: 16px 24px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .product-grid {
    margin-bottom: 24px;

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
          .stat-item {
            font-size: 12px;
            color: #666;
            margin-bottom: 4px;

            .anticon {
              margin-right: 4px;
            }
          }
        }

        .product-company {
          font-size: 12px;
          color: #666;
          margin-bottom: 4px;
        }
      }
    }
  }

  .pagination {
    text-align: right;
    padding: 16px 24px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}
</style> 