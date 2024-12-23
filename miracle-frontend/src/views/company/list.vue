<template>
  <div class="company-list">
    <!-- 筛选区域 -->
    <div class="filter-section">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="企业名称">
          <a-input
            v-model:value="searchForm.companyName"
            placeholder="请输入企业名称"
            allowClear
          />
        </a-form-item>
        <a-form-item label="所在地区">
          <a-input
            v-model:value="searchForm.region"
            placeholder="请输入地区"
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
            <a-card hoverable class="company-card" @click="goToDetail(company.id)">
              <template #cover>
                <img :alt="company.name" :src="company.logoUrl || defaultImage" />
              </template>
              <a-card-meta :title="company.name">
                <template #description>
                  <div class="company-info">
                    <div class="company-desc">{{ company.description }}</div>
                    <div class="company-meta">
                      <div class="company-location">
                        <environment-outlined /> {{ company.location }}
                      </div>
                      <div class="company-industry">
                        <apartment-outlined /> {{ company.industry }}
                      </div>
                      <div class="company-size">
                        <team-outlined /> {{ company.size }}
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
import { EyeOutlined, HeartOutlined, UpOutlined, DownOutlined, EnvironmentOutlined, ApartmentOutlined, TeamOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getCompanyList } from '@/api/company'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 12,
  companyName: '',
  region: '',
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
    companies.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取企业列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const onSearch = () => {
  searchForm.pageNum = 1
  loadCompanies()
}

// 重置
const onReset = () => {
  searchForm.companyName = ''
  searchForm.region = ''
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
  loadCompanies()
}

// 分页变化
const onPageChange = (page, pageSize) => {
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
  loadCompanies()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/company/${id}`)
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

  .filter-section {
    margin-bottom: 24px;
    padding: 24px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .sort-section {
    margin-bottom: 24px;
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
        height: 200px;
        object-fit: cover;
      }

      .company-info {
        margin-top: 12px;

        .company-desc {
          color: #666;
          font-size: 14px;
          margin-bottom: 12px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .company-meta {
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
  .company-list {
    padding: 16px;
  }
}
</style> 