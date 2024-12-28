<template>
  <div class="activity-statistics">
    <!-- 数据概览卡片 -->
    <a-row :gutter="[24, 24]">
      <a-col :span="6">
        <a-card class="stat-card">
          <statistic-card
            title="总活动数"
            :value="statistics.totalCount"
            :loading="loading"
          >
            <template #icon>
              <calendar-outlined />
            </template>
          </statistic-card>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <statistic-card
            title="进行中活动"
            :value="statistics.activeCount"
            :loading="loading"
          >
            <template #icon>
              <thunderbolt-outlined />
            </template>
          </statistic-card>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <statistic-card
            title="总浏览量"
            :value="statistics.totalViews"
            :loading="loading"
          >
            <template #icon>
              <eye-outlined />
            </template>
          </statistic-card>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <statistic-card
            title="总报名数"
            :value="statistics.totalRegistrations"
            :loading="loading"
          >
            <template #icon>
              <team-outlined />
            </template>
          </statistic-card>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="[24, 24]" style="margin-top: 24px">
      <!-- 活动趋势图 -->
      <a-col :span="16">
        <a-card title="活动数据趋势" :loading="loading">
          <template #extra>
            <a-radio-group v-model:value="trendType" button-style="solid">
              <a-radio-button value="week">近7天</a-radio-button>
              <a-radio-button value="month">近30天</a-radio-button>
            </a-radio-group>
          </template>
          <div ref="trendChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>

      <!-- 活动状态分布 -->
      <a-col :span="8">
        <a-card title="活动状态分布" :loading="loading">
          <div ref="pieChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 热门活动排行 -->
    <a-card title="热门活动排行" style="margin-top: 24px" :loading="loading">
      <a-table
        :columns="columns"
        :data-source="hotActivities"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'index'">
            <div class="rank-cell">
              <span :class="['rank-number', record.index <= 3 ? 'top-' + record.index : '']">
                {{ record.index }}
              </span>
            </div>
          </template>
          <template v-else-if="column.key === 'title'">
            <a @click="goToDetail(record)">{{ record.title }}</a>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  CalendarOutlined,
  ThunderboltOutlined,
  EyeOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import { getActivityStatistics, getActivityTrend, getHotActivities } from '@/api/activity'

const router = useRouter()
const loading = ref(false)
const trendType = ref('week')
const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart = null
let pieChart = null

// 统计数据
const statistics = ref({
  totalCount: 0,
  activeCount: 0,
  totalViews: 0,
  totalRegistrations: 0
})

// 热门活动表格列定义
const columns = [
  {
    title: '排名',
    key: 'index',
    width: 80,
    align: 'center'
  },
  {
    title: '活动名称',
    key: 'title',
    ellipsis: true
  },
  {
    title: '浏览量',
    key: 'viewCount',
    width: 120,
    align: 'right'
  },
  {
    title: '报名数',
    key: 'registerCount',
    width: 120,
    align: 'right'
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    align: 'center'
  }
]

const hotActivities = ref([])

// 获取统计数据
const fetchStatistics = async () => {
  loading.value = true
  try {
    const res = await getActivityStatistics()
    if (res.code === 200) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取趋势数据并渲染图表
const fetchTrendData = async () => {
  try {
    const res = await getActivityTrend({ type: trendType.value })
    if (res.code === 200) {
      renderTrendChart(res.data)
    }
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

// 渲染趋势图表
const renderTrendChart = (data) => {
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['浏览量', '报名数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '浏览量',
        type: 'line',
        smooth: true,
        data: data.views
      },
      {
        name: '报名数',
        type: 'line',
        smooth: true,
        data: data.registrations
      }
    ]
  }

  trendChart.setOption(option)
}

// 获取热门活动
const fetchHotActivities = async () => {
  try {
    const res = await getHotActivities()
    if (res.code === 200) {
      hotActivities.value = res.data.map((item, index) => ({
        ...item,
        index: index + 1
      }))
    }
  } catch (error) {
    console.error('获取热门活动失败:', error)
  }
}

// 跳转到活动详情
const goToDetail = (record) => {
  router.push(`/workspace/activity/detail/${record.id}`)
}

// 监听趋势类型变化
watch(trendType, () => {
  fetchTrendData()
})

// 窗口大小变化时重绘图表
const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  fetchStatistics()
  fetchTrendData()
  fetchHotActivities()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped lang="less">
.activity-statistics {
  .stat-card {
    :deep(.ant-statistic-title) {
      margin-bottom: 8px;
    }
    
    :deep(.ant-statistic-content) {
      font-size: 24px;
    }
  }

  .rank-cell {
    .rank-number {
      display: inline-block;
      width: 24px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      border-radius: 50%;
      background: #f0f0f0;
      
      &.top-1 {
        background: #f5222d;
        color: #fff;
      }
      
      &.top-2 {
        background: #fa8c16;
        color: #fff;
      }
      
      &.top-3 {
        background: #faad14;
        color: #fff;
      }
    }
  }
}
</style> 