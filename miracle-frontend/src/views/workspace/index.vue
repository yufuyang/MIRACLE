<template>
  <div class="workspace">
    <div class="container">
      <!-- 欢迎信息 -->
      <div class="welcome-section">
        <h2>欢迎回来，{{ userInfo?.username }}</h2>
        <p>{{ welcomeText }}</p>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <a-row :gutter="16">
          <a-col :span="6" v-for="action in quickActions" :key="action.key">
            <a-card hoverable @click="handleQuickAction(action.key)">
              <template #cover>
                <div class="action-icon">
                  <component :is="action.icon" style="font-size: 24px" />
                </div>
              </template>
              <a-card-meta :title="action.title">
                <template #description>{{ action.description }}</template>
              </a-card-meta>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <!-- 数据统计 -->
      <div class="stats-section">
        <a-row :gutter="16">
          <a-col :span="8" v-for="stat in stats" :key="stat.key">
            <a-card>
              <a-statistic
                :title="stat.title"
                :value="stat.value"
                :precision="stat.precision"
              >
                <template #suffix>
                  <span class="stat-trend" :class="{ up: stat.trend > 0, down: stat.trend < 0 }">
                    {{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%
                  </span>
                </template>
              </a-statistic>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <!-- 最近活动 -->
      <div class="recent-activities">
        <a-card title="最近活动">
          <a-timeline>
            <a-timeline-item v-for="activity in activities" :key="activity.id">
              <template #dot>
                <component :is="activity.icon" style="font-size: 16px" />
              </template>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  ShopOutlined,
  AppstoreOutlined,
  FileTextOutlined,
  MessageOutlined,
  EyeOutlined,
  HeartOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

// 欢迎文本
const welcomeText = computed(() => {
  const role = userInfo.value.role
  return role === 'company' ? '开始管理您的产品和订单' : '浏览最新的产品和商机'
})

// 快捷操作
const quickActions = computed(() => {
  if (userInfo.value.role === 'company') {
    return [
      {
        key: 'products',
        title: '产品管理',
        description: '管理您的产品信息',
        icon: AppstoreOutlined
      },
      {
        key: 'orders',
        title: '订单管理',
        description: '查看和处理订单',
        icon: FileTextOutlined
      },
      {
        key: 'messages',
        title: '消息中心',
        description: '查看商户意向',
        icon: MessageOutlined
      },
      {
        key: 'profile',
        title: '企业资料',
        description: '更新企业信息',
        icon: ShopOutlined
      }
    ]
  } else {
    return [
      {
        key: 'favorites',
        title: '我的收藏',
        description: '查看收藏的产品',
        icon: HeartOutlined
      },
      {
        key: 'intentions',
        title: '我的意向',
        description: '管理产品意向',
        icon: CheckCircleOutlined
      },
      {
        key: 'browse',
        title: '浏览记录',
        description: '查看浏览历史',
        icon: EyeOutlined
      },
      {
        key: 'profile',
        title: '商户资料',
        description: '更新商户信息',
        icon: ShopOutlined
      }
    ]
  }
})

// 统计数据
const stats = ref([
  {
    key: 'views',
    title: '浏览量',
    value: 1234,
    precision: 0,
    trend: 12.5
  },
  {
    key: 'intentions',
    title: '意向数',
    value: 56,
    precision: 0,
    trend: 8.2
  },
  {
    key: 'messages',
    title: '未读消息',
    value: 3,
    precision: 0,
    trend: -5.0
  }
])

// 最近活动
const activities = ref([
  {
    id: 1,
    title: '新增产品意向：高性能工业机器人',
    time: '10分钟前',
    icon: HeartOutlined
  },
  {
    id: 2,
    title: '更新产品信息：智能包装生产线',
    time: '2小时前',
    icon: AppstoreOutlined
  },
  {
    id: 3,
    title: '收到新消息：关于产品规格的咨询',
    time: '昨天 14:23',
    icon: MessageOutlined
  }
])

// 处理快捷操作点击
const handleQuickAction = (key) => {
  // TODO: 实现具体的跳转逻辑
  console.log('Quick action clicked:', key)
}
</script>

<style scoped lang="less">
.workspace {
  padding: 24px;

  .container {
    max-width: 1200px;
    margin: 0 auto;

    .welcome-section {
      margin-bottom: 24px;

      h2 {
        font-size: 24px;
        margin-bottom: 8px;
      }

      p {
        color: #666;
      }
    }

    .quick-actions {
      margin-bottom: 24px;

      .action-icon {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 80px;
        background: #f5f5f5;
        color: #1890ff;
      }

      :deep(.ant-card) {
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
      }
    }

    .stats-section {
      margin-bottom: 24px;

      .stat-trend {
        font-size: 12px;
        margin-left: 8px;

        &.up {
          color: #52c41a;
        }

        &.down {
          color: #f5222d;
        }
      }
    }

    .recent-activities {
      .activity-content {
        .activity-title {
          color: rgba(0, 0, 0, 0.85);
        }

        .activity-time {
          font-size: 12px;
          color: #999;
          margin-top: 4px;
        }
      }
    }
  }
}
</style> 