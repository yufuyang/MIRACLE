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

    <!-- 企业列表 -->
    <div class="company-grid">
      <a-row :gutter="[16, 16]">
        <a-col :span="6" v-for="company in companies" :key="company.id">
          <a-card hoverable @click="goToCompany(company.id)">
            <template #cover>
              <img
                :alt="company.companyName"
                :src="company.logo || '/default-company.png'"
                style="height: 200px; object-fit: cover;"
              />
            </template>
            <a-card-meta :title="company.companyName">
              <template #description>
                <div class="company-info">
                  <p>{{ company.address }}</p>
                  <p class="description">{{ company.description }}</p>
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
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { mockCompanies } from '@/mock/data'

const router = useRouter()
const route = useRoute()

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 12,
  companyName: '',
  region: ''
})

// 根据搜索条件筛选企业
const filteredCompanies = computed(() => {
  let result = [...mockCompanies]
  
  // 按名称筛选
  if (searchForm.companyName) {
    result = result.filter(item => 
      item.companyName.toLowerCase().includes(searchForm.companyName.toLowerCase())
    )
  }
  
  // 按地区筛选
  if (searchForm.region) {
    result = result.filter(item => 
      item.address.includes(searchForm.region)
    )
  }
  
  return result
})

// 分页后的企业列表
const companies = computed(() => {
  const start = (searchForm.pageNum - 1) * searchForm.pageSize
  const end = start + searchForm.pageSize
  return filteredCompanies.value.slice(start, end)
})

// 总数
const total = computed(() => filteredCompanies.value.length)

// 搜索
const onSearch = () => {
  searchForm.pageNum = 1
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
}

// 每页条数变化
const onShowSizeChange = (current, size) => {
  searchForm.pageSize = size
  searchForm.pageNum = 1
}

// 跳转到详情页
const goToCompany = (companyId) => {
  router.push(`/company/${companyId}`)
}

// 初始化
onMounted(() => {
  // 从 URL 查询参数中获取搜索关键词
  const keyword = route.query.keyword
  if (keyword) {
    searchForm.companyName = keyword
  }
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
  }

  .company-grid {
    background: #fff;
    padding: 24px;
    border-radius: 4px;
    margin-bottom: 24px;

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
      }
    }
  }

  .pagination {
    text-align: center;
    background: #fff;
    padding: 16px;
    border-radius: 4px;
  }
}
</style> 