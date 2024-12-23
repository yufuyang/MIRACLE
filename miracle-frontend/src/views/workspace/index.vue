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

      <!-- 企业数据概览 -->
      <div v-if="userInfo.role === 'company'" class="company-overview">
        <a-card title="企业数据概览">
          <a-row :gutter="16">
            <a-col :span="8">
              <a-statistic title="产品总数" :value="companyData.productCount" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="活动总数" :value="companyData.activityCount" />
            </a-col>
            <a-col :span="8">
              <a-statistic title="意向咨询" :value="companyData.intentionCount" />
            </a-col>
          </a-row>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCompanyStats } from '@/api/company'
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
const companyData = ref({
  productCount: 0,
  activityCount: 0,
  intentionCount: 0
})

// 欢迎文本
const welcomeText = computed(() => {
  const role = userInfo.value.role
  return role === 'company' ? '管理您的企业数据' : '浏览最新的产品和商机'
})

// 快捷操作
const quickActions = computed(() => {
  if (userInfo.value.role === 'company') {
    return [
      {
        key: 'products',
        title: '产品管理',
        description: '管理产品信息',
        icon: AppstoreOutlined
      },
      {
        key: 'activities',
        title: '活动管理',
        description: '管理企业活动',
        icon: FileTextOutlined
      },
      {
        key: 'inquiries',
        title: '意向管理',
        description: '查看商户咨询',
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

// 处理快捷操作点击
const handleQuickAction = (key) => {
  switch (key) {
    case 'products':
      router.push('/product/manage')
      break
    case 'activities':
      router.push('/activity/manage')
      break
    case 'inquiries':
      router.push('/inquiry')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'favorites':
      router.push('/favorite')
      break
    case 'browse':
      router.push('/browse')
      break
    default:
      console.log('未知的操作:', key)
  }
}

// 获取企业数据
const fetchCompanyData = async () => {
  if (userInfo.value.role === 'company') {
    try {
      const response = await getCompanyStats()
      if (response.data) {
        companyData.value = response.data
      }
    } catch (error) {
      console.error('获取企业数据失败:', error)
    }
  }
}

onMounted(() => {
  fetchCompanyData()
})
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

    .company-overview {
      margin-bottom: 24px;
    }
  }
}
</style> 