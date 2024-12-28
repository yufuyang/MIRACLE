<template>
  <div class="product-statistics">
    <!-- 数据概览卡片 -->
    <a-row :gutter="[24, 24]">
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="总浏览量"
            :value="statistics.totalViews"
            :loading="loading"
          >
            <template #prefix>
              <eye-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="总意向数"
            :value="statistics.totalIntentions"
            :loading="loading"
          >
            <template #prefix>
              <heart-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="今日浏览"
            :value="statistics.todayViews"
            :loading="loading"
          >
            <template #prefix>
              <line-chart-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="今日意向"
            :value="statistics.todayIntentions"
            :loading="loading"
          >
            <template #prefix>
              <rise-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 趋势图表 -->
    <a-card title="数据趋势" style="margin-top: 24px">
      <template #extra>
        <a-space>
          <a-select
            v-model:value="selectedProduct"
            style="width: 200px"
            placeholder="选择产品"
            :options="productOptions"
            @change="handleProductSelect"
          />
          <a-select
            v-model:value="dataType"
            style="width: 120px"
            :options="[
              { value: 'both', label: '全部数据' },
              { value: 'views', label: '浏览量' },
              { value: 'intentions', label: '意向数' }
            ]"
            @change="handleDataTypeChange"
          />
          <a-radio-group v-model:value="timeRange" button-style="solid">
            <a-radio-button value="week">最近7天</a-radio-button>
            <a-radio-button value="month">最近30天</a-radio-button>
          </a-radio-group>
        </a-space>
      </template>
      <div v-if="selectedProduct" ref="trendChartRef" style="height: 400px"></div>
      <a-empty v-else description="请选择要查看的产品" style="margin: 32px 0" />
    </a-card>

    <!-- 热门产品 -->
    <a-card style="margin-top: 24px">
      <template #title>
        <span>热门产品排行</span>
      </template>
      <template #extra>
        <a-space>
          <a-select
            v-model:value="timeRange"
            style="width: 120px"
            :options="[
              { value: 'week', label: '本周' },
              { value: 'month', label: '本月' }
            ]"
          />
          <a-radio-group v-model:value="rankType" button-style="solid">
            <a-radio-button value="views">按浏览量</a-radio-button>
            <a-radio-button value="intentions">按意向数</a-radio-button>
          </a-radio-group>
        </a-space>
      </template>
      <a-table
        :columns="columns"
        :data-source="hotProducts"
        :loading="loading"
        :pagination="false"
        :row-class-name="setRowClassName"
        size="middle"
        :scroll="{ x: 800 }"
      >
        <template #headerCell="{ column }">
          <template v-if="column.key === 'trend'">
            <div class="trend-title">
              <span>环比增长</span>
              <a-tooltip placement="top">
                <template #title>
                  <div>
                    与上一个时间段相比的增长率
                    <br />
                    - 按周查看时，对比上一周的数据
                    <br />
                    - 按月查看时，对比上一月的数据
                  </div>
                </template>
                <question-circle-outlined style="cursor: help" />
              </a-tooltip>
            </div>
          </template>
          <template v-else>
            {{ column.title }}
          </template>
        </template>
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'index'">
            <span :class="['rank-number', record.index <= 3 ? `top-${record.index}` : '']">
              {{ record.index }}
            </span>
          </template>
          <template v-else-if="column.key === 'productName'">
            <a @click="goToDetail(record)">{{ record.productName }}</a>
          </template>
          <template v-else-if="column.key === 'views'">
            <span>{{ record.views.toLocaleString() }}</span>
          </template>
          <template v-else-if="column.key === 'intentions'">
            <span>{{ record.intentions.toLocaleString() }}</span>
          </template>
          <template v-else-if="column.key === 'trend'">
            <span :class="getGrowth(record) > 0 ? 'trend-up' : 'trend-down'">
              <arrow-up-outlined v-if="getGrowth(record) > 0" />
              <arrow-down-outlined v-else />
              {{ getGrowth(record) > 0 ? '+' : '' }}{{ getGrowth(record) }}%
            </span>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { 
  getProductStatsOverview, 
  getProductTrend, 
  getHotProducts,
  getProductList  // 添加这行
} from '@/api/company/product-stats'
import * as echarts from 'echarts'
import {
  EyeOutlined,
  HeartOutlined,
  LineChartOutlined,
  RiseOutlined,
  QuestionCircleOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const timeRange = ref('week')
const selectedProduct = ref(null)  // 只保留这一个声明
const dataType = ref('both')  // 默认显示全部数据
const trendChartRef = ref(null)
let trendChart = null

// 表格列定义
const columns = [
  {
    title: '排名',
    key: 'index',
    width: 80,
    align: 'center',
    fixed: 'left'
  },
  {
    title: '产品名称',
    key: 'productName',
    ellipsis: true,
    width: 200
  },
  {
    title: '浏览量',
    key: 'views',
    width: 120,
    align: 'right'
  },
  {
    title: '意向数',
    key: 'intentions',
    width: 120,
    align: 'right'
  },
  {
    title: '环比增长',
    key: 'trend',
    width: 120,
    align: 'right'
  }
]

// 产品选项
const productOptions = ref([])

// 统计概览数据
const statistics = ref({
  totalViews: 0,
  totalIntentions: 0,
  todayViews: 0,
  todayIntentions: 0
})

// 热门产品数据
const hotProducts = ref([])
const rankType = ref('views')  // 排行榜类型：浏览量/意向数

// 获取统计概览数据
const fetchStatistics = async () => {
  try {
    loading.value = true
    const { data } = await getProductStatsOverview()
    statistics.value = data
  } finally {
    loading.value = false
  }
}

// 获取热门产品数据
const fetchHotProducts = async () => {
  try {
    loading.value = true
    const { data } = await getHotProducts({
      timeRange: timeRange.value,
      rankType: rankType.value
    })
    hotProducts.value = data.map((item, index) => ({
      ...item,
      index: index + 1
    }))

    // 使用热门产品数据来更新产品选项
    productOptions.value = data.map(item => ({
      label: item.productName,
      value: item.id
    }))

    // 如果还没有选中的产品，默认选中第一个
    if (!selectedProduct.value && productOptions.value.length > 0) {
      selectedProduct.value = productOptions.value[0].value
    }
  } finally {
    loading.value = false
  }
}

// 渲染趋势图表
const renderTrendChart = async () => {
  if (!selectedProduct.value || !trendChartRef.value) return

  try {
    loading.value = true
    const { data } = await getProductTrend({
      productId: selectedProduct.value,
      timeRange: timeRange.value
    })

    if (!trendChart) {
      trendChart = echarts.init(trendChartRef.value)
    }

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: dataType.value === 'both' ? ['浏览量', '意向数'] : 
              dataType.value === 'views' ? ['浏览量'] : ['意向数']
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.dates
      },
      yAxis: {
        type: 'value',
        name: '数量'
      },
      series: [
        // 只在显示全部数据或浏览量时添加浏览量系列
        ...(dataType.value === 'both' || dataType.value === 'views' ? [{
          name: '浏览量',
          type: 'line',
          smooth: true,  // 保持曲线平滑
          data: data.views,
          showSymbol: true  // 显示数据点
        }] : []),
        // 只在显示全部数据或意向数时添加意向数系列
        ...(dataType.value === 'both' || dataType.value === 'intentions' ? [{
          name: '意向数',
          type: 'line',
          smooth: true,  // 保持曲线平滑
          data: data.intentions,
          showSymbol: true  // 显示数据点
        }] : [])
      ].filter(Boolean)  // 过滤掉空数组
    }

    trendChart.setOption(option, true)  // 添加 true 参数，清除之前的配置
  } finally {
    loading.value = false
  }
}

// 监听数据变化
watch([timeRange, rankType], () => {
  fetchHotProducts()
})

// 修改这个 watch，添加 immediate 选项
watch([selectedProduct, timeRange, dataType], () => {
  if (selectedProduct.value) {
    renderTrendChart()
  }
})

// 生命周期钩子
onMounted(async () => {  // 添加 async
  await fetchStatistics()
  await fetchHotProducts()  // 等待获取产品列表完成
  
  // 使用 nextTick 确保 DOM 更新后再渲染图表
  nextTick(async () => {
    if (selectedProduct.value) {
      await renderTrendChart()
    }
  })
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
})

// 窗口大小变化时重绘图表
const handleResize = () => {
  trendChart?.resize()
}

// 设置表格行的类名
const setRowClassName = (record) => {
  return record.index <= 3 ? `rank-${record.index}` : ''
}

// 计算增长率
const getGrowth = (record) => {
  const currentValue = rankType.value === 'views' ? record.views : record.intentions
  const lastValue = rankType.value === 'views' ? record.lastViews : record.lastIntentions
  if (!lastValue) return 0
  return Math.round((currentValue - lastValue) / lastValue * 100)
}

// 跳转到产品详情
const goToDetail = (record) => {
  router.push(`/product/${record.id}`)
}
</script>

<style scoped lang="less">
.product-statistics {
  :deep(.ant-card) {
    height: 100%;
  }

  :deep(.ant-statistic-title) {
    margin-bottom: 8px;
  }

  :deep(.ant-statistic-content) {
    font-size: 24px;
  }

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

  .trend-up, .trend-down {
    display: inline-flex;
    align-items: center;
    gap: 4px;
  }

  .trend-up {
    color: #52c41a;
  }
  .trend-down {
    color: #ff4d4f;
  }

  :deep(.ant-checkbox-group) {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-right: 16px;
  }

  .rank-header {
    margin-bottom: 16px;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  :deep(.ant-table) {
    .ant-table-thead > tr > th {
      background: #fafafa;
      font-weight: 500;
    }

    .rank-1 {
      background-color: rgba(245, 34, 45, 0.05);
    }
    .rank-2 {
      background-color: rgba(250, 140, 22, 0.05);
    }
    .rank-3 {
      background-color: rgba(250, 173, 20, 0.05);
    }
  }

  .trend-title {
    display: inline-flex;
    align-items: center;
    gap: 4px;
  }
}
</style> 