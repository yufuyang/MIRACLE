<template>
  <div class="inquiry-stats">
    <div class="page-header">
      <h2>意向统计</h2>
    </div>

    <a-row :gutter="16">
      <!-- 数据概览卡片 -->
      <a-col :span="6">
        <a-card>
          <statistic
            title="总意向数"
            :value="stats.totalCount"
            :precision="0"
          >
            <template #prefix>
              <message-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <statistic
            title="未处理意向"
            :value="stats.pendingCount"
            :precision="0"
            :value-style="{ color: '#1890ff' }"
          >
            <template #prefix>
              <clock-circle-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <statistic
            title="已处理意向"
            :value="stats.processedCount"
            :precision="0"
            :value-style="{ color: '#52c41a' }"
          >
            <template #prefix>
              <check-circle-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <statistic
            title="已忽略意向"
            :value="stats.ignoredCount"
            :precision="0"
            :value-style="{ color: '#d9d9d9' }"
          >
            <template #prefix>
              <stop-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 意向趋势图 -->
    <a-card title="意向趋势" style="margin-top: 16px">
      <a-radio-group v-model:value="chartType" style="margin-bottom: 16px">
        <a-radio-button value="week">最近7天</a-radio-button>
        <a-radio-button value="month">最近30天</a-radio-button>
      </a-radio-group>
      <!-- 这里可以添加图表组件 -->
    </a-card>

    <!-- 产品意向排行 -->
    <a-card title="产品意向排行" style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="products"
        :loading="loading"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'trend'">
            <span :style="{ color: record.trend > 0 ? '#f5222d' : '#52c41a' }">
              {{ record.trend > 0 ? '+' : '' }}{{ record.trend }}%
            </span>
          </template>
          <template v-else-if="column.key === 'progress'">
            <a-progress
              :percent="(record.inquiryCount / maxInquiryCount * 100) || 0"
              :show-info="false"
              :stroke-color="{ from: '#108ee9', to: '#87d068' }"
            />
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Statistic } from 'ant-design-vue'
import {
  MessageOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  StopOutlined
} from '@ant-design/icons-vue'

// 统计数据
const stats = ref({
  totalCount: 0,
  pendingCount: 0,
  processedCount: 0,
  ignoredCount: 0
})

// 图表类型
const chartType = ref('week')

// 产品意向排行表格列定义
const columns = [
  {
    title: '排名',
    dataIndex: 'rank',
    key: 'rank',
    width: 80,
    align: 'center'
  },
  {
    title: '产品名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '意向数量',
    dataIndex: 'inquiryCount',
    key: 'inquiryCount',
    width: 100,
    align: 'right'
  },
  {
    title: '环比变化',
    key: 'trend',
    width: 100,
    align: 'right'
  },
  {
    title: '占比',
    key: 'progress',
    width: 200
  }
]

// 产品意向数据
const products = ref([])
const loading = ref(false)

// 计算最大意向数量，用于进度条展示
const maxInquiryCount = computed(() => {
  if (products.value.length === 0) return 0
  return Math.max(...products.value.map(p => p.inquiryCount))
})

// 获取统计数据
const fetchStats = async () => {
  loading.value = true
  try {
    const res = await getInquiryStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取产品意向排行
const fetchProductRanking = async () => {
  loading.value = true
  try {
    const res = await getInquiryProductRanking()
    if (res.code === 200) {
      products.value = res.data.map((item, index) => ({
        ...item,
        rank: index + 1
      }))
    }
  } catch (error) {
    console.error('获取产品意向排行失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
  fetchProductRanking()
})
</script>

<style scoped lang="less">
.inquiry-stats {
  .page-header {
    margin-bottom: 16px;

    h2 {
      margin: 0;
    }
  }

  :deep(.ant-card) {
    .ant-statistic-title {
      margin-bottom: 8px;
      color: rgba(0, 0, 0, 0.45);
    }
  }
}
</style> 