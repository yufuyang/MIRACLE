<template>
  <div class="activity-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <a-page-header
          :title="activity?.title"
          @back="() => router.back()"
      >
        <template #extra>
          <a-space>
            <a-button @click="handleViewCompany">查看企业</a-button>
            <a-button
                v-if="userStore.userInfo?.role === 'MERCHANT'"
                type="primary"
                :disabled="activity?.status !== 1"
                :danger="hasRegistered.value"
                @click="handleRegister"
            >
              {{ buttonText }}
            </a-button>
          </a-space>
        </template>
      </a-page-header>
    </div>

    <a-spin :spinning="loading">
      <!-- 活动封面 -->
      <a-card title="活动封面" style="margin-bottom: 24px">
        <div class="activity-cover">
          <div class="image-wrapper">
            <a-image
                :src="activity?.coverImage || defaultImage"
                :alt="activity?.title"
                :preview="true"
            />
          </div>
        </div>
      </a-card>

      <!-- 基本信息 -->
      <a-card title="基本信息" style="margin-bottom: 24px">
        <a-descriptions :column="2">
          <a-descriptions-item label="活动标题">{{ activity?.title }}</a-descriptions-item>
          <a-descriptions-item label="活动状态">
            <a-tag :color="getStatusColor(activity?.status)">
              {{ getStatusText(activity?.status) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="开始时间">{{ formatDate(activity?.startTime) }}</a-descriptions-item>
          <a-descriptions-item label="结束时间">{{ formatDate(activity?.endTime) }}</a-descriptions-item>
          <a-descriptions-item label="活动描述" :span="2">{{ activity?.description || '-' }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <!-- 数据统计 -->
      <div class="stats-section">
        <div class="stats-title">数据统计</div>
        <div class="stats-list">
          <div class="stats-item">
            <eye-outlined />
            <div class="stats-content">
              <div class="stats-label">浏览量</div>
              <div class="stats-value">{{ activity?.viewCount || 0 }}</div>
            </div>
          </div>
          <div class="stats-item">
            <team-outlined />
            <div class="stats-content">
              <div class="stats-label">报名人数</div>
              <div class="stats-value">{{ activity?.registerCount || 0 }}</div>
            </div>
          </div>
          <div class="stats-item">
            <clock-circle-outlined />
            <div class="stats-content">
              <div class="stats-label">剩余时间</div>
              <div class="stats-value">{{ getRemainingTime() }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 企业信息 -->
      <a-card title="企业信息">
        <div class="company-content">
          <div class="company-logo">
            <img :src="company?.logoUrl || defaultImage" :alt="company?.companyName" />
          </div>
          <div class="company-details">
            <div class="company-name">{{ company?.companyName }}</div>
            <div class="company-info">
              <div class="info-item">
                <environment-outlined />
                <span>{{ company?.address }}</span>
              </div>
              <div class="info-item">
                <phone-outlined />
                <span>{{ company?.contactPhone }}</span>
              </div>
              <div class="info-item">
                <user-outlined />
                <span>联系人：{{ company?.contactName }}</span>
              </div>
            </div>
            <div class="company-desc">{{ company?.description }}</div>
          </div>
        </div>
      </a-card>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  UserAddOutlined,
  TeamOutlined,
  EnvironmentOutlined,
  EyeOutlined,
  ClockCircleOutlined,
  PhoneOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import { getActivityDetail } from '@/api/website/activity'
import { 
  registerActivity, 
  checkActivityRegistration,
  cancelActivityRegistration
} from '@/api/merchant/activity'
import { getCompanyDetail } from '@/api/company'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'
import { USER_ROLE } from '@/constants'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const registerLoading = ref(false)
const activity = ref(null)
const company = ref(null)
const defaultImage = 'https://via.placeholder.com/800x400'
const hasRegistered = ref(false)  // 是否已报名

// 判断是否已登录 - 调用 store 中的方法
const isLoggedIn = computed(() => userStore.isLoggedIn())

// 判断是否是商户用户
const isMerchant = computed(() => userStore.userInfo?.role === 'MERCHANT')

// 获取活动状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '未开始',
    1: '进行中',
    2: '已结束'
  }
  return statusMap[status] || '未知'
}

// 获取活动状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'blue',
    1: 'green',
    2: 'gray'
  }
  return colorMap[status] || 'default'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 获取剩余时间
const getRemainingTime = () => {
  if (!activity.value?.endTime) return '-'
  const end = dayjs(activity.value.endTime)
  const now = dayjs()
  if (now.isAfter(end)) return '已结束'

  const days = end.diff(now, 'day')
  const hours = end.diff(now, 'hour') % 24

  if (days > 0) {
    return `${days}天${hours}小时`
  }
  return `${hours}小时`
}

// 确保用户信息是最新的
const ensureUserInfo = async () => {
  if (localStorage.getItem('token')) {
    userStore.initUserState()
    if (userStore.userInfo?.role === 'MERCHANT') {
      await userStore.fetchUserDetail()
    }
  }
}

// 按钮文本
const buttonText = computed(() => {
  console.log('计算按钮文本:', {
    hasRegistered: hasRegistered.value,
    type: typeof hasRegistered.value
  })
  return hasRegistered.value ? '已报名' : '立即报名'
})

// 检查是否已报名
const checkRegistrationStatus = async () => {
  if (userStore.isLoggedIn() && userStore.userInfo?.role === 'MERCHANT') {
    try {
      const res = await checkActivityRegistration(route.params.id)
      console.log('检查报名状态结果:', res)
      hasRegistered.value = Boolean(res.data)
      console.log('设置报名状态后:', hasRegistered.value)
    } catch (error) {
      console.error('检查报名状态失败:', error)
    }
  }
}

// 处理报名按钮点击
const handleRegister = async () => {
  // 未登录时跳转到登录页
  if (!userStore.isLoggedIn()) {
    message.warning('请先登录')
    router.push('/login')
    return
  }

  // 已登录但不是商户用户
  if (userStore.userInfo?.role !== 'MERCHANT') {
    message.warning('只有商户用户可以报名活动')
    return
  }

  // 如果已报名，则取消报名
  if (hasRegistered.value) {
    Modal.confirm({
      title: '确认取消报名',
      content: '确定要取消该活动的报名吗？',
      async onOk() {
        try {
          await cancelActivityRegistration(route.params.id)
          message.success('已取消报名')
          hasRegistered.value = false
          // 重新获取活动详情以更新数据
          fetchActivityDetail()
        } catch (error) {
          console.error('取消报名失败:', error)
          message.error('取消报名失败')
        }
      }
    })
    return
  }

  if (registerLoading.value) return
  registerLoading.value = true

  try {
    await registerActivity({
      activityId: route.params.id,
      companyId: activity.value.companyId
    })
    message.success('报名成功')
    // 更新报名状态
    hasRegistered.value = true
    // 重新获取活动详情以更新数据
    fetchActivityDetail()
  } catch (error) {
    message.error(error.response?.data?.message || '报名失败')
  } finally {
    registerLoading.value = false
  }
}

// 获取活动详情
const fetchActivityDetail = async () => {
  loading.value = true
  try {
    const res = await getActivityDetail(route.params.id)
    activity.value = res.data
    if (res.data?.companyId) {
      fetchCompanyDetail(res.data.companyId)
    }
  } catch (error) {
    message.error('获取活动详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 获取公司详情
const fetchCompanyDetail = async (companyId) => {
  try {
    const res = await getCompanyDetail(companyId)
    company.value = res.data
  } catch (error) {
    console.error('获取公司详情失败:', error)
  }
}

// 初始化
onMounted(async () => {
  const id = route.params.id
  if (id) {
    try {
      // 先初始化用户状态
      await ensureUserInfo()
      
      // 如果是商户用户，先检查报名状态
      if (userStore.userInfo?.role === 'MERCHANT') {
        console.log('开始检查报名状态')
        await checkRegistrationStatus()
        console.log('报名状态检查完成:', hasRegistered.value)
      }
      
      // 然后再加载其他数据
      await fetchActivityDetail()
    } catch (error) {
      console.error('初始化失败:', error)
    }
  }
})

// 调试用
console.log('用户信息:', {
  isLoggedIn: isLoggedIn.value,
  role: userStore.userInfo?.role,
  isMerchant: isMerchant.value
})
</script>

<style scoped lang="less">
.activity-detail {
  padding: 24px;
  background: #f5f5f5;

  .page-header {
    background: #fff;
    margin-bottom: 24px;
  }

  .activity-cover {
    .image-wrapper {
      width: 100%;
      max-width: 800px;
      margin: 0 auto;
      aspect-ratio: 16/9;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid #f0f0f0;
      background: #fafafa;

      :deep(.ant-image) {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
    }
  }

  .company-content {
    display: flex;
    padding: 16px;

    .company-logo {
      width: 120px;
      height: 120px;
      margin-right: 24px;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid #f0f0f0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .company-details {
      flex: 1;

      .company-name {
        font-size: 20px;
        font-weight: 500;
        color: #262626;
        margin-bottom: 16px;
      }

      .company-info {
        margin-bottom: 16px;

        .info-item {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          color: #595959;

          .anticon {
            margin-right: 8px;
            color: #1890ff;
          }
        }
      }

      .company-desc {
        color: #8c8c8c;
        font-size: 14px;
        line-height: 1.5;
      }
    }
  }

  .stats-section {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .stats-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 16px;
      color: #333;
    }

    .stats-list {
      display: flex;
      gap: 48px;

      .stats-item {
        display: flex;
        align-items: center;
        gap: 12px;

        .anticon {
          font-size: 24px;
          color: #1890ff;
        }

        .stats-content {
          .stats-label {
            font-size: 14px;
            color: #666;
            margin-bottom: 4px;
          }

          .stats-value {
            font-size: 20px;
            font-weight: 500;
            color: #333;
          }
        }
      }
    }
  }
}
</style>