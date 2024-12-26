<template>
  <div class="company-list">
    <!-- 搜索区域 -->
    <div class="search-area">
      企业名称：<a-input v-model:value="searchForm.companyName" placeholder="请输入企业名称" style="width: 200px" allowClear />
      <a-button type="primary" @click="onSearch">
        <template #icon><search-outlined /></template>
        查询
      </a-button>
      <a-button @click="onReset">
        <template #icon><redo-outlined /></template>
        重置
      </a-button>
    </div>

    <!-- 排序区域 -->
    <div class="sort-buttons">
      <a-radio-group v-model:value="searchForm.orderField" @change="onSearch">
        <a-radio-button value="">默认排序</a-radio-button>
        <a-radio-button value="productCount">
          产品数
          <up-outlined v-if="searchForm.orderField === 'productCount' && searchForm.asc" />
          <down-outlined v-if="searchForm.orderField === 'productCount' && !searchForm.asc" />
        </a-radio-button>
        <a-radio-button value="intentionCount">
          意向数
          <up-outlined v-if="searchForm.orderField === 'intentionCount' && searchForm.asc" />
          <down-outlined v-if="searchForm.orderField === 'intentionCount' && !searchForm.asc" />
        </a-radio-button>
      </a-radio-group>
    </div>

    <!-- 企业列表 -->
    <div class="company-grid">
      <a-row :gutter="[24, 24]">
        <template v-if="companies.length > 0">
          <a-col :span="6" v-for="company in companies" :key="company.id">
            <a-card hoverable class="company-card" @click="goToDetail(company)">
              <template #cover>
                <img :alt="company.companyName" :src="company.logoUrl || defaultImage" />
              </template>
              <a-card-meta :title="company.companyName">
                <template #description>
                  <div class="company-info">
                    <p>{{ company.province }} {{ company.city }}</p>
                    <div class="stats">
                      <span>
                        <ShopOutlined /> 产品数：{{ company.productCount }}
                      </span>
                      <span>
                        <HeartOutlined /> 意向数：{{ company.intentionCount }}
                      </span>
                    </div>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </template>
        <template v-else>
          <a-col :span="6" v-for="i in 12" :key="i">
            <a-card class="company-card empty-card">
              <template #cover>
                <a-skeleton-image :active="true" />
              </template>
              <a-card-meta>
                <template #title>
                  <a-skeleton :active="true" :paragraph="false" />
                </template>
                <template #description>
                  <div class="company-info">
                    <div class="company-desc">
                      <a-skeleton :active="true" :paragraph="{ rows: 2 }" :title="false" />
                    </div>
                    <div class="company-meta">
                      <div class="company-location">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                      <div class="company-industry">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                      <div class="company-size">
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
        :total="total"
        :pageSize="searchForm.pageSize"
        show-size-changer
        show-quick-jumper
        @change="onPageChange"
        @showSizeChange="onShowSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { EyeOutlined, HeartOutlined, UpOutlined, DownOutlined, EnvironmentOutlined, ApartmentOutlined, TeamOutlined, PhoneOutlined, ShopOutlined, SearchOutlined, RedoOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getCompanyList } from '@/api/website/company'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 12,
  companyName: '',
  orderField: '',
  asc: true
})

// 数据
const loading = ref(false)
const total = ref(0)
const companies = ref([])

// 加载企业列表
const loadCompanies = async () => {
  loading.value = true
  try {
    const res = await getCompanyList(searchForm)
    companies.value = res.data || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取企业列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const onSearch = () => {
  if (searchForm.orderField) {
    searchForm.asc = !searchForm.asc
  }
  searchForm.pageNum = 1
  loadCompanies()
}

// 重置
const onReset = () => {
  searchForm.companyName = ''
  searchForm.orderField = ''
  searchForm.asc = true
  searchForm.pageNum = 1
  loadCompanies()
}

// 排序
const handleSort = (field) => {
  if (searchForm.orderField === field) {
    searchForm.asc = !searchForm.asc
  } else {
    searchForm.orderField = field
    searchForm.asc = true
  }
  loadCompanies()
}

// 分页变化
const onPageChange = (page, pageSize) => {
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
  loadCompanies()
}

// 跳转到详情页
const goToDetail = (company) => {
  router.push({
    path: `/company/${company.id}`,
    query: {
      productCount: company.productCount,
      intentionCount: company.intentionCount
    }
  })
}

// 初始化
onMounted(() => {
  loadCompanies()
})
</script>

<style scoped lang="less">
.company-list {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;

  .search-area {
    background: #fff;
    padding: 24px;
    border-radius: 8px;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 16px;

    .ant-btn {
      display: flex;
      align-items: center;
    }
  }

  .sort-buttons {
    margin-bottom: 24px;
    background: #fff;
    padding: 16px 24px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .company-grid {
    .company-card {
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
        height: 280px;
        object-fit: cover;
      }

      :deep(.ant-card-meta-title) {
        font-size: 16px;
        margin-bottom: 8px;
        text-align: left;
      }
    }
  }

  .pagination {
    margin-top: 40px;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .company-list {
    padding: 16px;
  }
}

.company-info {
  .stats {
    display: flex;
    justify-content: space-between;
    color: #666;
    font-size: 14px;
    margin-top: 8px;

    span {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}
</style> 