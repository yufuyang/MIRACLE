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
            {{ getButtonText(activity.status) }}
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  TeamOutlined, 
  CalendarOutlined, 
  EnvironmentOutlined,
  UserOutlined,
  ShareAltOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import defaultImage from '@/assets/images/default.jpg'

const route = useRoute()
const router = useRouter()

// 模拟活动详情数据
const activity = ref({
  id: 1,
  title: '2024智能制造创新峰会',
  description: '探讨智能制造最新趋势，分享行业创新实践。本次峰会将邀请行业领军企业和专家，共同探讨智能制造发展趋势、技术创新、解决��案等热点话题。通过主题演讲、圆桌论坛、案例分享等多种形式，深入交流智能制造实践经验。',
  companyName: '科技创新有限公司',
  startTime: '2024-03-15 09:00-17:00',
  location: '上海市浦东新区张江高科技园区',
  status: '即将开始',
  imageUrl: defaultImage,
  registeredCount: 180,
  maxCount: 300,
  targetAudience: '制造业企业负责人、技术总监、项目经理等相关人员',
  schedules: [
    {
      time: '09:00-09:30',
      title: '签到入场',
      description: '领取会议资料'
    },
    {
      time: '09:30-10:30',
      title: '开幕式及主题演讲',
      description: '智能制造发展趋势与机遇'
    },
    {
      time: '10:45-12:00',
      title: '技术创新专场',
      description: '人工智能在制造业的应用实践'
    },
    {
      time: '14:00-15:30',
      title: '案例分享',
      description: '智能工厂建设经验分享'
    },
    {
      time: '15:45-16:45',
      title: '圆桌论坛',
      description: '智能制造解决方案探讨'
    }
  ],
  notices: [
    {
      title: '报名方式',
      content: '线上报名，审核通过后将收到确认邮件'
    },
    {
      title: '参会要求',
      content: '请务必携带身份证件，提前15分钟到达会场'
    },
    {
      title: '其他说明',
      content: '会议提供午餐，请提前告知是否有特殊饮食要求'
    }
  ]
})

// 获取按钮文字
const getButtonText = (status) => {
  switch (status) {
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
const handleRegister = () => {
  if (activity.value.status === '即将开始') {
    message.success('预约提醒设置成功')
  } else if (activity.value.status === '报名中') {
    message.success('报名成功')
  }
}

// 处理分享
const handleShare = () => {
  message.success('分享链接已复制')
}

// 初始化
onMounted(() => {
  const id = route.params.id
  // 这里应该根据id获取活动详情
  console.log('活动ID:', id)
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