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
          <a-tree-select
            v-model:value="searchForm.categoryId"
            style="width: 200px"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="categories"
            placeholder="请选择分类"
            tree-default-expand-all
            allow-clear
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
                  <span class="price">¥ {{ item.price }}</span>
                  <div class="stats">
                    <span><eye-outlined /> {{ item.viewCount }}</span>
                    <span><heart-outlined /> {{ item.intentionCount }}</span>
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
  categoryId: undefined,
  minPrice: undefined,
  maxPrice: undefined,
  orderField: '',
  asc: true
})

// 数据
const total = ref(mockProducts.length)
const categories = ref(mockCategories)

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
  if (searchForm.categoryId) {
    result = result.filter(item => item.categoryId === searchForm.categoryId)
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

// 分页后的产品列表
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
  searchForm.categoryId = undefined
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
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 8px;

        .price {
          color: #f5222d;
          font-size: 16px;
          font-weight: bold;
        }

        .stats {
          display: flex;
          gap: 8px;
          color: #999;
        }
      }
    }
  }

  .pagination {
    margin-top: 24px;
    text-align: center;
  }
}
</style> 