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
      <a-row :gutter="16">
        <a-col :span="6" v-for="item in productList" :key="item.id">
          <a-card hoverable class="product-card" @click="goToDetail(item.id)">
            <template #cover>
              <img :alt="item.productName" :src="item.imageUrl || defaultImage" />
            </template>
            <a-card-meta :title="item.productName">
              <template #description>
                <div class="product-info">
                  <h3>{{ item.productName }}</h3>
                  <div class="product-subtitle">{{ item.description }}</div>
                  <div class="price">¥ {{ item.price }}</div>
                  <div class="stats">
                    <span><eye-outlined /> {{ item.viewCount }} 浏览量</span>
                    <span><heart-outlined /> {{ item.intentionCount }} 意向数</span>
                  </div>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
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
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { EyeOutlined, HeartOutlined, UpOutlined, DownOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { mockProducts, mockCategories } from '@/mock/data'

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
const total = ref(mockProducts.length)
const categories = ref(mockCategories)

// 分类搜索过滤函数
const filterOption = (input, option) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 根据搜索条件和排序筛选产品
const filteredProducts = computed(() => {
  let result = [...mockProducts]
  
  // 按名称筛选
  if (searchForm.productName) {
    result = result.filter(item => 
      item.productName.toLowerCase().includes(searchForm.productName.toLowerCase())
    )
  }
  
  // 按分类筛选
  if (searchForm.category) {
    result = result.filter(item => 
      item.category.toLowerCase().includes(searchForm.category.toLowerCase())
    )
  }
  
  // 按价格区间筛选
  if (searchForm.minPrice !== undefined) {
    result = result.filter(item => item.price >= searchForm.minPrice)
  }
  if (searchForm.maxPrice !== undefined) {
    result = result.filter(item => item.price <= searchForm.maxPrice)
  }
  
  // 排序
  if (searchForm.orderField) {
    result.sort((a, b) => {
      const factor = searchForm.asc ? 1 : -1
      switch (searchForm.orderField) {
        case 'price':
          return (a.price - b.price) * factor
        case 'viewCount':
          return (a.viewCount - b.viewCount) * factor
        case 'intentionCount':
          return (a.intentionCount - b.intentionCount) * factor
        default:
          return 0
      }
    })
  }
  
  return result
})

// 分页的产品列表
const productList = computed(() => {
  const start = (searchForm.pageNum - 1) * searchForm.pageSize
  const end = start + searchForm.pageSize
  return filteredProducts.value.slice(start, end)
})

// 搜索
const onSearch = () => {
  searchForm.pageNum = 1
  total.value = filteredProducts.value.length
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

// 分页变化
const onPageChange = (page, pageSize) => {
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

// 初始化
onMounted(() => {
  // 从 URL 查询参数中获取搜索关键词
  const keyword = route.query.keyword
  if (keyword) {
    searchForm.productName = keyword
    onSearch()
  }
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
  }

  .sort-section {
    margin-bottom: 24px;
  }

  .product-grid {
    .product-card {
      margin-bottom: 16px;

      :deep(.ant-card-cover) {
        img {
          height: 200px;
          object-fit: cover;
        }
      }

      .product-info {
        padding: 16px;

        h3 {
          margin: 0 0 8px;
          font-size: 16px;
          font-weight: 500;
        }

        .product-subtitle {
          color: #666;
          font-size: 14px;
          margin-bottom: 8px;
        }

        .price {
          color: #ff4d4f;
          font-size: 18px;
          font-weight: 500;
          margin-bottom: 8px;
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
  }

  .pagination {
    margin-top: 24px;
    text-align: center;
  }

  :deep(.category-dropdown) {
    max-height: 400px;
    overflow-y: auto;

    .ant-select-item {
      padding: 8px 12px;
      
      &:hover {
        background-color: #f5f5f5;
      }
      
      &.ant-select-item-option-selected {
        background-color: #e6f7ff;
      }
    }
  }
}
</style> 