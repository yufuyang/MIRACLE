<template>
  <div class="activity-statistics">
    <!-- 顶部统计卡片 -->
    <div class="statistics-cards">
      <a-row :gutter="[16, 16]">
        <a-col :span="6">
          <a-card hoverable>
            <div class="statistic-item">
              <div class="icon">
                <file-outlined />
              </div>
              <div class="content">
                <div class="label">总活动数</div>
                <div class="value">{{ statistics?.totalCount ?? 0 }}</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card hoverable>
            <div class="statistic-item">
              <div class="icon">
                <clock-circle-outlined />
              </div>
              <div class="content">
                <div class="label">进行中活动</div>
                <div class="value">{{ statistics?.activeCount ?? 0 }}</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card hoverable>
            <div class="statistic-item">
              <div class="icon">
                <eye-outlined />
              </div>
              <div class="content">
                <div class="label">总浏览量</div>
                <div class="value">{{ statistics?.viewCount ?? 0 }}</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card hoverable>
            <div class="statistic-item">
              <div class="icon">
                <team-outlined />
              </div>
              <div class="content">
                <div class="label">总报名人数</div>
                <div class="value">{{ statistics?.registerCount ?? 0 }}</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 图表区域 -->
    <div class="chart-section">
      <a-card title="活动数据趋势" :bordered="false">
        <template #extra>
          <a-space>
            <a-select
              v-model:value="selectedActivity"
              style="width: 200px"
              placeholder="请选择活动"
              @change="handleActivityChange"
              allowClear
            >
              <a-select-option 
                v-for="item in activityOptions" 
                :key="item.id" 
                :value="item.id"
              >
                {{ item.title }}
              </a-select-option>
            </a-select>
            <a-radio-group v-model:value="timeRange" button-style="solid">
              <a-radio-button value="7">近7天</a-radio-button>
              <a-radio-button value="30">近30天</a-radio-button>
            </a-radio-group>
          </a-space>
        </template>
        <div ref="chartRef" style="height: 350px"></div>
      </a-card>
    </div>

    <!-- 活动列表 -->
    <div class="list-section">
      <a-card title="活动列表" :bordered="false">
        <template #extra>
          <a-space>
            <a-select
              v-model:value="listType"
              style="width: 120px"
              @change="handleListTypeChange"
            >
              <a-select-option value="all">全部活动</a-select-option>
              <a-select-option value="active">进行中</a-select-option>
              <a-select-option value="ended">已结束</a-select-option>
            </a-select>
            <a-select
              v-model:value="sortBy"
              style="width: 120px"
              @change="handleSortChange"
            >
              <a-select-option value="view">按浏览量</a-select-option>
              <a-select-option value="register">按报名数</a-select-option>
              <a-select-option value="time">按时间</a-select-option>
            </a-select>
          </a-space>
        </template>
        <a-table
          :columns="columns"
          :data-source="activityList"
          :loading="loading"
          :pagination="pagination"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ getStatusText(record.status) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'action'">
              <a @click="handleViewDetail(record)">查看</a>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  FileOutlined,
  ClockCircleOutlined,
  EyeOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import { 
  getActivityStatsOverview, 
  getActivityStatsTrend,
  getHotActivities
} from '@/api/activity'

const router = useRouter()
const loading = ref(false)
const timeRange = ref('7')
const listType = ref('all')
const sortBy = ref('time')
const chartRef = ref(null)
let chart = null

// 统计数据初始化为 null
const statistics = ref(null)

// 表格列定义
const columns = [
  {
    title: '活动名称',
    dataIndex: 'title',
    key: 'title'
  },
  {
    title: '开始时间',
    dataIndex: 'startTime',
    key: 'startTime',
    width: 180
  },
  {
    title: '结束时间',
    dataIndex: 'endTime',
    key: 'endTime',
    width: 180
  },
  {
    title: '浏览量',
    dataIndex: 'viewCount',
    key: 'viewCount',
    width: 120,
    align: 'right'
  },
  {
    title: '报名数',
    dataIndex: 'registerCount',
    key: 'registerCount',
    width: 120,
    align: 'right'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    align: 'center'
  },
  {
    title: '操作',
    key: 'action',
    width: 80,
    align: 'center'
  }
]

const activityList = ref([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

const selectedActivity = ref(undefined)
const activityOptions = ref([])

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const { data } = await getActivityStatsOverview()
    statistics.value = data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化图表
const initChart = () => {
  if (!chart) {
    chart = echarts.init(chartRef.value)
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
      data: [],
      axisLabel: {
        formatter: (value) => {
          return value  // 直接显示日期字符串 MM-DD 格式
        }
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '浏览量',
        type: 'line',
        smooth: true,
        data: []
      },
      {
        name: '报名数',
        type: 'line',
        smooth: true,
        data: []
      }
    ]
  }
  
  chart.setOption(option)
}

// 更新图表数据的方法
const updateChart = (data) => {
  const option = {
    xAxis: {
      data: data.dates
    },
    series: [
      {
        name: '浏览量',
        data: data.viewCounts
      },
      {
        name: '报名数',
        data: data.regCounts
      }
    ]
  }
  chart.setOption(option)
}

// 获取活动列表
const fetchActivityList = async () => {
  loading.value = true
  try {
    const { data } = await getHotActivities({
      type: sortBy.value  // 'view' 或 'register'
    })
    activityList.value = data
  } catch (error) {
    console.error('获取热门活动失败:', error)
    message.error('获取热门活动失败')
  } finally {
    loading.value = false
  }
}

// 状态处理
const getStatusColor = (status) => {
  const colors = {
    0: 'default',
    1: 'success',
    2: 'error'
  }
  return colors[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    0: '未开始',
    1: '进行中',
    2: '已结束'
  }
  return texts[status] || '未知'
}

// 事件处理
const handleListTypeChange = () => {
  pagination.value.current = 1
  fetchActivityList()
}

const handleSortChange = () => {
  fetchActivityList()
}

const handleTableChange = () => {
  // 移除分页处理
}

const handleViewDetail = (record) => {
  router.push(`/workspace/activity/detail/${record.id}`)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  chart?.resize()
}

// 获取活动选项
const fetchActivityOptions = async () => {
  try {
    const { data } = await getHotActivities()
    activityOptions.value = data.map(item => ({
      id: item.id,
      title: item.title
    }))
    // 设置默认选中第一个活动
    if (activityOptions.value.length > 0) {
      selectedActivity.value = activityOptions.value[0].id
      fetchTrendData()  // 获取第一个活动的趋势数据
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
  }
}

// 处理活动选择变化
const handleActivityChange = (value) => {
  fetchChartData()
}

// 获取图表数据
const fetchChartData = async () => {
  try {
    // TODO: 根据 selectedActivity 和 timeRange 获取对应的图表数据
    const option = {
      // ... 图表配置保持不变 ...
    }
    chart.setOption(option)
  } catch (error) {
    console.error('获取图表数据失败:', error)
  }
}

// 监听活动选择和时间范围变化
watch([selectedActivity, timeRange], () => {
  fetchTrendData()
})

// 获取趋势数据
const fetchTrendData = async () => {
  try {
    const { data } = await getActivityStatsTrend({
      activityId: selectedActivity.value,
      days: Number(timeRange.value)
    })
    updateChart(data)
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

// 获取热门活动
const fetchHotActivities = async () => {
  loading.value = true
  try {
    const { data } = await getHotActivityStats({ type: rankType.value })
    hotActivities.value = data
  } catch (error) {
    console.error('获取热门活动失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStatistics()
  fetchActivityList()
  initChart()
  window.addEventListener('resize', handleResize)
  fetchActivityOptions()
  fetchTrendData()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped lang="less">
.activity-statistics {
  padding: 24px;

  .statistics-cards {
    margin-bottom: 24px;

    .statistic-item {
      display: flex;
      align-items: center;
      
      .icon {
        font-size: 24px;
        margin-right: 16px;
        padding: 12px;
        background-color: #f5f5f5;
        border-radius: 50%;
      }
      
      .content {
        .label {
          color: rgba(0, 0, 0, 0.45);
          font-size: 14px;
          margin-bottom: 4px;
        }
        
        .value {
          font-size: 24px;
          font-weight: 500;
        }
      }
    }
  }

  .chart-section {
    margin-bottom: 24px;
  }

  .list-section {
    :deep(.ant-card-head) {
      border-bottom: none;
    }
  }
}
</style> 