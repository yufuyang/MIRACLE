<template>
  <div class="activity-detail">
    <!-- 活动基本信息 -->
    <div class="activity-header">
      <div class="activity-cover">
        <img :src="activity.imageUrl || defaultImage" :alt="activity.title" />
      </div>
      <div class="activity-info">
        <div class="activity-status" :class="activity.status">{{ activity.status }}</div>
        <h1>{{ activity.title }}</h1>
        <div class="meta-list">
          <div class="meta-item">
            <team-outlined />
            <span>主办方：{{ activity.companyName }}</span>
          </div>
          <div class="meta-item">
            <calendar-outlined />
            <span>活动时间：{{ activity.startTime }}</span>
          </div>
          <div class="meta-item">
            <environment-outlined />
            <span>活动地点：{{ activity.location }}</span>
          </div>
          <div class="meta-item">
            <user-outlined />
            <span>报名人数：{{ activity.registeredCount || 0 }}/{{ activity.maxCount || '不限' }}</span>
          </div>
        </div>
        <div class="action-buttons">
          <a-button 
            type="primary" 
            size="large"
            :disabled="activity.status === '已结束'"
            @click="handleRegister"
          >
            {{ getButtonText() }}
          </a-button>
          <a-button size="large" @click="handleShare">
            <share-alt-outlined /> 分享活动
          </a-button>
        </div>
      </div>
    </div>

    <!-- 活动内容区域 -->
    <div class="activity-content">
      <a-tabs>
        <!-- 活动介绍 -->
        <a-tab-pane key="1" tab="活动介绍">
          <div class="content-section">
            <h2>活动介绍</h2>
            <div class="rich-text">{{ activity.description }}</div>
          </div>
          <div class="content-section">
            <h2>参与对象</h2>
            <div class="rich-text">{{ activity.targetAudience }}</div>
          </div>
        </a-tab-pane>

        <!-- 活动日程 -->
        <a-tab-pane key="2" tab="活动日程">
          <div class="schedule-list">
            <a-timeline>
              <a-timeline-item v-for="(schedule, index) in activity.schedules" :key="index">
                <div class="schedule-item">
                  <div class="schedule-time">{{ schedule.time }}</div>
                  <div class="schedule-title">{{ schedule.title }}</div>
                  <div class="schedule-desc">{{ schedule.description }}</div>
                </div>
              </a-timeline-item>
            </a-timeline>
          </div>
        </a-tab-pane>

        <!-- 报名须知 -->
        <a-tab-pane key="3" tab="报名须知">
          <div class="notice-list">
            <div class="notice-item" v-for="(notice, index) in activity.notices" :key="index">
              <h3>{{ notice.title }}</h3>
              <p>{{ notice.content }}</p>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { TeamOutlined, CalendarOutlined, EnvironmentOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getActivityDetail, registerActivity } from '@/api/activity'

const router = useRouter()
const route = useRoute()

// 数据
const loading = ref(false)
const activity = ref({
  title: '',
  description: '',
  companyName: '',
  startTime: '',
  location: '',
  status: '',
  imageUrl: defaultImage,
  registeredCount: 0,
  maxCount: 0,
  targetAudience: '',
  schedules: [],
  notices: []
})

// 加载活动详情
const loadActivityDetail = async (id) => {
  loading.value = true
  try {
    const res = await getActivityDetail(id)
    if (res.data) {
      activity.value = {
        ...res.data,
        imageUrl: res.data.imageUrl || defaultImage
      }
    }
  } catch (error) {
    console.error('获取活动详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取按钮文字
const getButtonText = () => {
  switch (activity.value.status) {
    case '即将开始':
      return '预约提醒'
    case '报名中':
      return '立即报名'
    case '已结束':
      return '活动结束'
    default:
      return '立即报名'
  }
}

// 处理报名
const handleRegister = async () => {
  try {
    await registerActivity({
      activityId: activity.value.id
    })
    message.success('报名成功')
    loadActivityDetail(activity.value.id)
  } catch (error) {
    console.error('报名失败:', error)
    message.error(error.response?.data?.message || '报名失败')
  }
}

// 初始化
onMounted(() => {
  const id = route.params.id
  if (id) {
    loadActivityDetail(id)
  }
})
</script>

<style scoped lang="less">
.activity-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;

  .activity-header {
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .activity-cover {
      width: 100%;
      height: 400px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .activity-info {
      padding: 24px;
      position: relative;

      .activity-status {
        position: absolute;
        top: -20px;
        right: 24px;
        padding: 4px 16px;
        border-radius: 16px;
        color: #fff;
        font-size: 14px;

        &.即将开始 {
          background: #faad14;
        }

        &.报名中 {
          background: #52c41a;
        }

        &.已结束 {
          background: #8c8c8c;
        }
      }

      h1 {
        font-size: 28px;
        font-weight: 600;
        margin-bottom: 24px;
        color: #333;
      }

      .meta-list {
        margin-bottom: 24px;

        .meta-item {
          display: flex;
          align-items: center;
          margin-bottom: 12px;
          color: #666;
          font-size: 14px;

          .anticon {
            margin-right: 8px;
            font-size: 16px;
          }
        }
      }

      .action-buttons {
        display: flex;
        gap: 16px;

        .ant-btn {
          min-width: 120px;
        }
      }
    }
  }

  .activity-content {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .content-section {
      margin-bottom: 32px;

      h2 {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 16px;
        color: #333;
      }

      .rich-text {
        color: #666;
        line-height: 1.8;
      }
    }

    .schedule-list {
      padding: 16px;

      .schedule-item {
        margin-bottom: 8px;

        .schedule-time {
          font-weight: 500;
          color: #333;
          margin-bottom: 4px;
        }

        .schedule-title {
          color: #1890ff;
          margin-bottom: 4px;
        }

        .schedule-desc {
          color: #666;
          font-size: 14px;
        }
      }
    }

    .notice-list {
      .notice-item {
        margin-bottom: 24px;

        h3 {
          font-size: 16px;
          font-weight: 500;
          margin-bottom: 8px;
          color: #333;
        }

        p {
          color: #666;
          line-height: 1.6;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .activity-detail {
    padding: 16px;

    .activity-header {
      .activity-cover {
        height: 200px;
      }

      .activity-info {
        padding: 16px;

        h1 {
          font-size: 24px;
          margin-bottom: 16px;
        }
      }
    }

    .activity-content {
      padding: 16px;
    }
  }
}
</style> 