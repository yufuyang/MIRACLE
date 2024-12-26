<template>
  <div class="activity-stats">
    <!-- 统计卡片 -->
    <a-row :gutter="16">
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
            :value="stats.ongoingCount"
            :precision="0"
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
            title="总浏览量"
            :value="stats.totalViews"
            :precision="0"
          >
            <template #prefix>
              <eye-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <statistic
            title="总报名人数"
            :value="stats.totalRegistrations"
            :precision="0"
          >
            <template #prefix>
              <team-outlined />
            </template>
          </statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="12">
        <a-card title="活动状态分布">
          <div ref="statusChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="近期活动趋势">
          <div ref="trendChartRef" style="height: 300px"></div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 热门活动列表 -->
    <a-card title="热门活动TOP10" style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="hotActivities"
        :pagination="false"
      >
        <template #coverUrl="{ text }">
          <a-image
            :width="80"
            :src="text || defaultImage"
            :fallback="defaultImage"
            style="object-fit: cover"
          />
        </template>
        <template #status="{ text }">
          <a-tag :color="getStatusColor(text)">
            {{ getStatusText(text) }}
          </a-tag>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Statistic } from 'ant-design-vue'
import {
  CalendarOutlined,
  ClockCircleOutlined,
  EyeOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import * as echarts from 'echarts'
import { getActivityStats, getHotActivities } from '@/api/activity'

const defaultImage = 'https://via.placeholder.com/200x200'

// 统计数据
const stats = ref({
  totalCount: 0,
  ongoingCount: 0,
  totalViews: 0,
  totalRegistrations: 0
})

// 图表实例
const statusChartRef = ref(null)
const trendChartRef = ref(null)
let statusChart = null
let trendChart = null

// 热门活动表格列定义
const columns = [
  {
    title: '活动封面',
    dataIndex: 'coverUrl',
    key: 'coverUrl',
    width: 100,
    slots: { customRender: 'coverUrl' }
  },
  {
    title: '活动名称',
    dataIndex: 'activityName',
    key: 'activityName',
    width: 200,
    ellipsis: true
  },
  {
    title: '活动时间',
    key: 'time',
    width: 300,
    customRender: ({ record }) => {
      return `${record.startTime} ~ ${record.endTime}`
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    slots: { customRender: 'status' }
  },
  {
    title: '浏览量',
    dataIndex: 'viewCount',
    key: 'viewCount',
    width: 100,
    align: 'right'
  },
  {
    title: '报名人数',
    dataIndex: 'registerCount',
    key: 'registerCount',
    width: 100,
    align: 'right'
  }
]

// 热门活动数据
const hotActivities = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await getActivityStats()
    if (res.code === 200) {
      stats.value = res.data
      initStatusChart(res.data.statusDistribution)
      initTrendChart(res.data.trend)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取热门活动
const fetchHotActivities = async () => {
  try {
    const res = await getHotActivities()
    if (res.code === 200) {
      hotActivities.value = res.data || []
    }
  } catch (error) {
    console.error('获取热门活动失败:', error)
  }
}

// 初始化状态分布图表
const initStatusChart = (data) => {
  if (!statusChartRef.value) return
  
  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        type: 'pie',
        radius: '50%',
        data: [
          { value: data.notStarted, name: '未开始' },
          { value: data.ongoing, name: '进行中' },
          { value: data.ended, name: '已结束' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  })
}

// 初始化趋势图表
const initTrendChart = (data) => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['活动数', '浏览量', '报名人数']
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '活动数',
        type: 'line',
        data: data.activityCounts
      },
      {
        name: '浏览量',
        type: 'line',
        data: data.viewCounts
      },
      {
        name: '报名人数',
        type: 'line',
        data: data.registerCounts
      }
    ]
  })
}

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 0:
      return 'blue'
    case 1:
      return 'green'
    case 2:
      return 'gray'
    default:
      return 'default'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '未开始'
    case 1:
      return '进行中'
    case 2:
      return '已结束'
    default:
      return '未知'
  }
}

// 监听窗口大小变化
window.addEventListener('resize', () => {
  statusChart?.resize()
  trendChart?.resize()
})

onMounted(() => {
  fetchStats()
  fetchHotActivities()
})
</script>

<style lang="less" scoped>
.activity-stats {
  padding: 16px;
}
</style> 