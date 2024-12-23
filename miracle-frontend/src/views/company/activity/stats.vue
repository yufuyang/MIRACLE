<template>
  <div class="activity-stats">
    <div class="page-header">
      <h2>活动统计</h2>
    </div>

    <a-row :gutter="16">
      <!-- 数据概览卡片 -->
      <a-col :span="6">
        <a-card>
          <statistic
            title="总活动数"
            :value="stats.totalCount"
            :precision="0"
          >
            <template #prefix>
              <calendar-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <statistic
            title="进行中活动"
            :value="stats.activeCount"
            :precision="0"
            :value-style="{ color: '#1890ff' }"
          >
            <template #prefix>
              <thunderbolt-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <statistic
            title="已结束活动"
            :value="stats.endedCount"
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
            title="总浏览量"
            :value="stats.totalViews"
            :precision="0"
            :value-style="{ color: '#faad14' }"
          >
            <template #prefix>
              <eye-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 活动趋势图 -->
    <a-card title="活动趋势" style="margin-top: 16px">
      <a-radio-group v-model:value="chartType" style="margin-bottom: 16px">
        <a-radio-button value="week">最近7天</a-radio-button>
        <a-radio-button value="month">最近30天</a-radio-button>
      </a-radio-group>
      <!-- 这里可以添加图表组件 -->
    </a-card>

    <!-- 活动排行榜 -->
    <a-card title="活动排行榜" style="margin-top: 16px">
      <a-tabs v-model:activeKey="rankingType">
        <a-tab-pane key="views" tab="浏览量排行">
          <a-table
            :columns="viewsColumns"
            :data-source="viewsRanking"
            :loading="loading"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'views'">
                <a-progress
                  :percent="(record.views / maxViews * 100) || 0"
                  :show-info="false"
                  :stroke-color="{ from: '#108ee9', to: '#87d068' }"
                />
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="inquiries" tab="咨询量排行">
          <a-table
            :columns="inquiryColumns"
            :data-source="inquiryRanking"
            :loading="loading"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'inquiries'">
                <a-progress
                  :percent="(record.inquiryCount / maxInquiries * 100) || 0"
                  :show-info="false"
                  :stroke-color="{ from: '#108ee9', to: '#87d068' }"
                />
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Statistic } from 'ant-design-vue'
import {
  CalendarOutlined,
  ThunderboltOutlined,
  CheckCircleOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'

// 统计数据
const stats = ref({
  totalCount: 0,
  activeCount: 0,
  endedCount: 0,
  totalViews: 0
})

// 图表类型
const chartType = ref('week')

// 排行榜类型
const rankingType = ref('views')

// 浏览量排行表格列定义
const viewsColumns = [
  {
    title: '排名',
    dataIndex: 'rank',
    key: 'rank',
    width: 80,
    align: 'center'
  },
  {
    title: '活动名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '浏览量',
    dataIndex: 'views',
    key: 'views',
    width: 100,
    align: 'right'
  },
  {
    title: '占比',
    key: 'views',
    width: 200
  }
]

// 咨询量排行表格列定义
const inquiryColumns = [
  {
    title: '排名',
    dataIndex: 'rank',
    key: 'rank',
    width: 80,
    align: 'center'
  },
  {
    title: '活动名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '咨询量',
    dataIndex: 'inquiryCount',
    key: 'inquiryCount',
    width: 100,
    align: 'right'
  },
  {
    title: '占比',
    key: 'inquiries',
    width: 200
  }
]

// 排行榜数据
const viewsRanking = ref([])
const inquiryRanking = ref([])
const loading = ref(false)

// 计算最大浏览量，用于进度条展示
const maxViews = computed(() => {
  if (viewsRanking.value.length === 0) return 0
  return Math.max(...viewsRanking.value.map(p => p.views))
})

// 计算最大咨询量，用于进度条展示
const maxInquiries = computed(() => {
  if (inquiryRanking.value.length === 0) return 0
  return Math.max(...inquiryRanking.value.map(p => p.inquiryCount))
})

// 获取统计数据
const fetchStats = async () => {
  loading.value = true
  try {
    const res = await getActivityStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取浏览量排行
const fetchViewsRanking = async () => {
  loading.value = true
  try {
    const res = await getActivityViewsRanking()
    if (res.code === 200) {
      viewsRanking.value = res.data.map((item, index) => ({
        ...item,
        rank: index + 1
      }))
    }
  } catch (error) {
    console.error('获取浏览量排行失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取咨询量排行
const fetchInquiryRanking = async () => {
  loading.value = true
  try {
    const res = await getActivityInquiryRanking()
    if (res.code === 200) {
      inquiryRanking.value = res.data.map((item, index) => ({
        ...item,
        rank: index + 1
      }))
    }
  } catch (error) {
    console.error('获取咨询量排行失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
  fetchViewsRanking()
  fetchInquiryRanking()
})
</script>

<style scoped lang="less">
.activity-stats {
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