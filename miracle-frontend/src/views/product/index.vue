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
          />
        </a-form-item>
        <a-form-item label="分类">
          <a-input
            v-model:value="searchForm.category"
            placeholder="请输入分类"
            allowClear
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
          <a-button type="primary" @click="onSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="onReset">重置</a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 排序区域 -->
    <div class="sort-section">
      <a-radio-group v-model:value="searchForm.orderField" @change="onSearch">
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
            <a-card hoverable class="product-card" @click="goToDetail(product.id)">
              <template #cover>
                <img :alt="product.name" :src="product.imageUrl || defaultImage" />
              </template>
              <a-card-meta :title="product.name">
                <template #description>
                  <div class="product-info">
                    <div class="product-desc">{{ product.description }}</div>
                    <div class="product-meta">
                      <div class="company-name">
                        <team-outlined /> {{ product.companyName }}
                      </div>
                      <div class="product-category">
                        <tag-outlined /> {{ product.category }}
                      </div>
                    </div>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </template>
        <template v-else>
          <a-col :span="6" v-for="i in 12" :key="i">
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
                    <div class="product-meta">
                      <div class="company-name">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                      <div class="product-category">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                    </div>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </template>
      </a-row>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <a-pagination
        v-model:current="searchForm.pageNum"
        v-model:pageSize="searchForm.pageSize"
        :total="total"
        show-size-changer
        show-quick-jumper
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { EyeOutlined, HeartOutlined, UpOutlined, DownOutlined, TeamOutlined, TagOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getProductList, getProductCategories } from '@/api/product'

const router = useRouter()
const route = useRoute()

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 12,
  productName: '',
  category: '',
  minPrice: undefined,
  maxPrice: undefined,
  orderField: '',
  asc: true
})

// 数据
const loading = ref(false)
const total = ref(0)
const products = ref([])
const categories = ref([])

// 加载产品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getProductList(searchForm)
    products.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取产品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getProductCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 分类搜索过滤函数
const filterOption = (input, option) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 搜索
const onSearch = () => {
  searchForm.pageNum = 1
  loadProducts()
}

// 重置
const onReset = () => {
  searchForm.productName = ''
  searchForm.category = ''
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  searchForm.orderField = ''
  searchForm.asc = true
  onSearch()
}

// 排序
const handleSort = (field) => {
  if (searchForm.orderField === field) {
    searchForm.asc = !searchForm.asc
  } else {
    searchForm.orderField = field
    searchForm.asc = true
  }
  loadProducts()
}

// 分页变化
const onPageChange = (page, pageSize) => {
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
  loadProducts()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

// 初始化
onMounted(() => {
  loadCategories()
  loadProducts()
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

  .product-grid {
    .product-card {
      height: 100%;
      transition: all 0.3s;

      &:hover:not(.empty-card) {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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

      .product-info {
        margin-top: 12px;

        .product-desc {
          color: #666;
          font-size: 14px;
          margin-bottom: 12px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .product-meta {
          color: #999;
          font-size: 14px;

          > div {
            display: flex;
            align-items: center;
            margin-bottom: 8px;

            .anticon {
              margin-right: 8px;
            }
          }
        }
      }
    }
  }

  .pagination {
    margin-top: 40px;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .product-list {
    padding: 16px;
  }
}
</style> 