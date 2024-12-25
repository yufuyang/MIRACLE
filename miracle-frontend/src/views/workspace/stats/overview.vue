<template>
  <div class="overview-container">
    <div class="stats-cards">
      <a-row :gutter="[16, 16]">
        <a-col :span="6">
          <a-card>
            <template #title>
              <message-outlined />
              <span>意向数</span>
            </template>
            <div class="stat-number">{{ stats.inquiryCount || 0 }}</div>
            <div class="stat-trend">
              <span :class="{ 'up': stats.inquiryTrend > 0, 'down': stats.inquiryTrend < 0 }">
                {{ Math.abs(stats.inquiryTrend || 0) }}%
              </span>
              较上月
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card>
            <template #title>
              <eye-outlined />
              <span>浏览数</span>
            </template>
            <div class="stat-number">{{ stats.visitCount || 0 }}</div>
            <div class="stat-trend">
              <span :class="{ 'up': stats.visitTrend > 0, 'down': stats.visitTrend < 0 }">
                {{ Math.abs(stats.visitTrend || 0) }}%
              </span>
              较上月
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card>
            <template #title>
              <shopping-cart-outlined />
              <span>订单数</span>
            </template>
            <div class="stat-number">{{ stats.orderCount || 0 }}</div>
            <div class="stat-trend">
              <span :class="{ 'up': stats.orderTrend > 0, 'down': stats.orderTrend < 0 }">
                {{ Math.abs(stats.orderTrend || 0) }}%
              </span>
              较上月
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card>
            <template #title>
              <team-outlined />
              <span>合作数</span>
            </template>
            <div class="stat-number">{{ stats.cooperationCount || 0 }}</div>
            <div class="stat-trend">
              <span :class="{ 'up': stats.cooperationTrend > 0, 'down': stats.cooperationTrend < 0 }">
                {{ Math.abs(stats.cooperationTrend || 0) }}%
              </span>
              较上月
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <div class="charts-section">
      <a-row :gutter="[16, 16]">
        <a-col :span="12">
          <a-card title="数据趋势">
            <div class="chart-container">
              <!-- 这里添加折线图，展示四个指标的趋势 -->
              <div class="chart-placeholder">数据趋势图</div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card title="订单金额统计">
            <div class="chart-container">
              <!-- 这里添加柱状图，展示订单金额统计 -->
              <div class="chart-placeholder">订单金额统计图</div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <div class="recent-section">
      <a-row :gutter="[16, 16]">
        <a-col :span="12">
          <a-card title="最新订单" extra="查看全部">
            <a-list :data-source="recentOrders" :pagination="false">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta
                    :title="item.title"
                    :description="item.createTime"
                  />
                  <div>{{ item.status }}</div>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card title="最新合作" extra="查看全部">
            <a-list :data-source="recentCooperations" :pagination="false">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta
                    :title="item.title"
                    :description="item.createTime"
                  />
                  <div>{{ item.status }}</div>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import {
  MessageOutlined,
  EyeOutlined,
  ShoppingCartOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

// 统计数据
const stats = ref({
  inquiryCount: 0,
  inquiryTrend: 0,
  visitCount: 0,
  visitTrend: 0,
  orderCount: 0,
  orderTrend: 0,
  cooperationCount: 0,
  cooperationTrend: 0
})

// 最新订单列表
const recentOrders = ref([
  {
    title: '订单20240120001',
    createTime: '2024-01-20 14:30:00',
    status: '待发货'
  },
  {
    title: '订单20240119001',
    createTime: '2024-01-19 16:45:00',
    status: '已完成'
  }
])

// 最新合作列表
const recentCooperations = ref([
  {
    title: '战略合作协议',
    createTime: '2024-01-20 10:00:00',
    status: '进行中'
  },
  {
    title: '技术合作协议',
    createTime: '2024-01-15 09:30:00',
    status: '已签署'
  }
])

// 获取统计数据
const fetchStats = async () => {
  try {
    // TODO: 调用后端API获取统计数据
    // const res = await getCompanyStats()
    // stats.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取热门商品列表
const fetchHotProducts = async () => {
  try {
    const res = await getHotProducts()
    if (res.code === 200) {
      // 只显示已上架的商品
      hotProducts.value = res.data.filter(product => product.status === 1)
    }
  } catch (error) {
    console.error('获取热门商品失败:', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped lang="less">
.overview-container {
  .welcome-section {
    margin-bottom: 24px;

    h2 {
      margin-bottom: 8px;
      font-size: 24px;
    }

    .subtitle {
      color: rgba(0, 0, 0, 0.45);
    }
  }

  .stats-cards {
    margin-bottom: 24px;

    :deep(.ant-card) {
      .ant-card-head-title {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    .stat-number {
      font-size: 24px;
      font-weight: bold;
      margin: 8px 0;
    }

    .stat-trend {
      color: rgba(0, 0, 0, 0.45);
      font-size: 14px;

      span {
        &.up {
          color: #52c41a;
        }

        &.down {
          color: #ff4d4f;
        }
      }
    }
  }

  .charts-section {
    margin-bottom: 24px;

    .chart-container {
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #fafafa;
      border-radius: 4px;
    }

    .chart-placeholder {
      color: rgba(0, 0, 0, 0.45);
    }
  }

  .recent-section {
    :deep(.ant-card-extra) {
      cursor: pointer;
      color: #1890ff;

      &:hover {
        color: #40a9ff;
      }
    }
  }
}
</style> 