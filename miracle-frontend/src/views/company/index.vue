<template>
  <div class="company-list">
    <!-- 顶部导航 -->
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo">MIRACLE</div>
        <a-menu mode="horizontal" v-model:selectedKeys="selectedKeys">
          <a-menu-item key="home">
            <router-link to="/">首页</router-link>
          </a-menu-item>
          <a-menu-item key="product">
            <router-link to="/product">产品</router-link>
          </a-menu-item>
          <a-menu-item key="company">
            <router-link to="/company/list">企业</router-link>
          </a-menu-item>
        </a-menu>
        <div class="right">
          <a-input-search
            v-model:value="searchForm.companyName"
            placeholder="搜索企业"
            style="width: 200px"
            @search="onSearch"
          />
          <a-button type="primary" class="login-btn">登录</a-button>
        </div>
      </div>
    </a-layout-header>

    <div class="container">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <a-form layout="inline" :model="searchForm">
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

      <!-- 企业列表 -->
      <div class="company-grid">
        <a-row :gutter="[16, 16]">
          <a-col :span="6" v-for="company in companies" :key="company.id">
            <a-card hoverable @click="goToDetail(company.id)">
              <template #cover>
                <img
                  :alt="company.companyName"
                  :src="company.logoUrl || '/default-company.png'"
                  style="height: 200px; object-fit: cover;"
                />
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
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ShopOutlined, HeartOutlined } from '@ant-design/icons-vue'
import { getCompanyList } from '@/api/company'

const router = useRouter()
const route = useRoute()
const selectedKeys = ref(['company'])

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 12,
  companyName: '',
  region: ''
})

// 数据
const companies = ref([])
const total = ref(0)

// 获取企业列表
const loadCompanies = async () => {
  try {
    const response = await getCompanyList(searchForm)
    console.log('企业列表数据:', response)
    if (response.data) {
      companies.value = response.data.map(company => ({
        ...company,
        productCount: company.productCount || 0,
        intentionCount: company.intentionCount || 0
      }))
      total.value = response.total
    }
  } catch (error) {
    console.error('获取企业列表失败:', error)
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
  onSearch()
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
  .header {
    position: fixed;
    top: 0;
    width: 100%;
    z-index: 1000;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      align-items: center;

      .logo {
        font-size: 20px;
        font-weight: bold;
        margin-right: 48px;
      }

      .right {
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: 16px;
      }
    }
  }

  .container {
    margin-top: 88px;
    padding: 24px;
    max-width: 1200px;
    margin-left: auto;
    margin-right: auto;
  }

  .filter-section {
    margin-bottom: 24px;
    padding: 24px;
    background: #fff;
    border-radius: 4px;
  }

  .company-grid {
    .company-info {
      p {
        margin-bottom: 8px;
      }

      .description {
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        margin-bottom: 12px;
      }

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
  }

  .pagination {
    margin-top: 24px;
    text-align: center;
  }
}
</style> 