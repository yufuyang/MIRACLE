<template>
  <div class="product-list">
    <!-- 搜索表单 -->
    <a-card>
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="产品名称" name="productName">
          <a-input v-model:value="searchForm.productName" placeholder="请输入产品名称" allow-clear style="width: 200px" />
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
        <a-form-item>
          <a-button type="primary" html-type="submit">
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

      <!-- 产品列表 -->
      <div class="product-grid" style="margin-top: 16px">
        <a-row :gutter="[24, 24]">
          <a-col :span="6" v-for="product in products" :key="product.id">
            <a-card hoverable class="product-card" @click="handleDetail(product)">
              <template #cover>
                <a-image
                  :src="product.imageUrl"
                  :fallback="defaultImage"
                  :preview="false"
                  style="height: 200px; object-fit: cover"
                />
              </template>
              <a-card-meta :title="product.productName">
                <template #description>
                  <div class="product-info">
                    <div class="product-price">
                      <span class="price">¥{{ product.price }}</span>
                      <span class="unit">/ {{ product.unit }}</span>
                    </div>
                    <div class="product-company">{{ product.companyName }}</div>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <!-- 分页 -->
      <div class="pagination" style="margin-top: 16px; text-align: right">
        <a-pagination
          v-model:current="pagination.current"
          v-model:pageSize="pagination.pageSize"
          :total="pagination.total"
          show-size-changer
          show-quick-jumper
          @change="handleTableChange"
        />
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  RedoOutlined
} from '@ant-design/icons-vue'
import { getProductList, getProductCategories } from '@/api/product'
import { useRouter } from 'vue-router'

const router = useRouter()

// 默认图片
const defaultImage = 'https://via.placeholder.com/200x200'

// 搜索表单
const searchForm = reactive({
  productName: '',
  categoryId: undefined
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
const handleSearch = () => {
  pagination.current = 1
  fetchProducts()
}

// 重置
const handleReset = () => {
  searchForm.productName = ''
  searchForm.categoryId = undefined
  handleSearch()
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

  .product-card {
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .product-info {
      .product-price {
        margin-bottom: 8px;
        
        .price {
          font-size: 16px;
          font-weight: bold;
          color: #f5222d;
        }

        .unit {
          font-size: 12px;
          color: #999;
          margin-left: 4px;
        }
      }

      .product-company {
        font-size: 12px;
        color: #666;
      }
    }
  }
}
</style> 