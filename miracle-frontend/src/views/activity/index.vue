<template>
  <div class="activity-list">
    <!-- 搜索筛选区域 -->
    <div class="filter-section">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="企业名称">
          <a-input
            v-model:value="searchForm.companyName"
            placeholder="请输入企业名称"
            allowClear
          />
        </a-form-item>
        <a-form-item label="活动状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择活动状态"
            style="width: 160px"
            allowClear
          >
            <a-select-option value="即将开始">即将开始</a-select-option>
            <a-select-option value="报名中">报名中</a-select-option>
            <a-select-option value="已结束">已结束</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="onSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="onReset">重置</a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 活动列表 -->
    <div class="activity-grid">
      <a-row :gutter="[24, 24]">
        <template v-if="activities.length > 0">
          <a-col :span="8" v-for="activity in activities" :key="activity.id">
            <a-card hoverable class="activity-card" @click="goToDetail(activity.id)">
              <template #cover>
                <div class="activity-cover">
                  <img :alt="activity.title" :src="activity.imageUrl || defaultImage" />
                  <div class="activity-status" :class="activity.status">
                    {{ activity.status }}
                  </div>
                </div>
              </template>
              <a-card-meta :title="activity.title">
                <template #description>
                  <div class="activity-info">
                    <div class="activity-desc">{{ activity.description }}</div>
                    <div class="activity-meta">
                      <div class="company-name">
                        <team-outlined /> {{ activity.companyName }}
                      </div>
                      <div class="activity-time">
                        <calendar-outlined /> {{ activity.startTime }}
                      </div>
                      <div class="activity-location">
                        <environment-outlined /> {{ activity.location }}
                      </div>
                    </div>
                    <a-button 
                      type="primary" 
                      class="register-btn"
                      :disabled="activity.status === '已结束'"
                      @click.stop="handleRegister(activity)"
                    >
                      {{ getButtonText(activity.status) }}
                    </a-button>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </template>
        <template v-else>
          <a-col :span="8" v-for="i in 6" :key="i">
            <a-card class="activity-card empty-card">
              <template #cover>
                <div class="activity-cover">
                  <a-skeleton-image :active="true" />
                </div>
              </template>
              <a-card-meta>
                <template #title>
                  <a-skeleton :active="true" :paragraph="false" />
                </template>
                <template #description>
                  <div class="activity-info">
                    <div class="activity-desc">
                      <a-skeleton :active="true" :paragraph="{ rows: 2 }" :title="false" />
                    </div>
                    <div class="activity-meta">
                      <div class="company-name">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                      <div class="activity-time">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                      <div class="activity-location">
                        <a-skeleton :active="true" :paragraph="false" />
                      </div>
                    </div>
                    <a-skeleton.button :active="true" size="large" block />
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </template>
      </a-row>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <a-pagination
        v-model:current="searchForm.pageNum"
        v-model:pageSize="searchForm.pageSize"
        :total="total"
        show-size-changer
        show-quick-jumper
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { TeamOutlined, CalendarOutlined, EnvironmentOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getActivityList, registerActivity } from '@/api/activity'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 9,
  companyName: '',
  status: undefined
})

// 数据
const loading = ref(false)
const total = ref(0)
const activities = ref([])

// 加载活动列表
const loadActivities = async () => {
  loading.value = true
  try {
    const res = await getActivityList(searchForm)
    activities.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

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
const handleRegister = async (activity) => {
  try {
    await registerActivity({
      activityId: activity.id
    })
    message.success('报名成功')
    loadActivities()
  } catch (error) {
    console.error('报名失败:', error)
    message.error(error.response?.data?.message || '报名失败')
  }
}

// 搜索
const onSearch = () => {
  searchForm.pageNum = 1
  loadActivities()
}

// 重置
const onReset = () => {
  searchForm.companyName = ''
  searchForm.status = undefined
  onSearch()
}

// 分页变化
const onPageChange = (page, pageSize) => {
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
  loadActivities()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/activity/${id}`)
}

// 初始化
onMounted(() => {
  loadActivities()
})
</script>

<style scoped lang="less">
.activity-list {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;

  .filter-section {
    margin-bottom: 24px;
    padding: 24px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .activity-grid {
    .activity-card {
      height: 100%;
      transition: all 0.3s;

      &:hover:not(.empty-card) {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      &.empty-card {
        cursor: default;
        
        .activity-cover {
          background: #fafafa;
          display: flex;
          align-items: center;
          justify-content: center;
          
          :deep(.ant-skeleton-image) {
            width: 100%;
            height: 200px;
            background: #f5f5f5;
          }
        }

        :deep(.ant-skeleton) {
          .ant-skeleton-title {
            margin: 0;
          }
        }
      }

      .activity-cover {
        position: relative;
        height: 200px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .activity-status {
          position: absolute;
          top: 16px;
          right: 16px;
          padding: 4px 12px;
          border-radius: 12px;
          font-size: 12px;
          color: #fff;

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
      }

      .activity-info {
        margin-top: 16px;

        .activity-desc {
          color: #666;
          font-size: 14px;
          margin-bottom: 16px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .activity-meta {
          margin-bottom: 16px;
          color: #666;
          font-size: 14px;

          > div {
            display: flex;
            align-items: center;
            margin-bottom: 8px;

            .anticon {
              margin-right: 8px;
            }
          }
        }

        .register-btn {
          width: 100%;
          border-radius: 4px;

          &[disabled] {
            color: rgba(0, 0, 0, 0.25);
            background: #f5f5f5;
            border-color: #d9d9d9;
          }
        }
      }
    }
  }

  .pagination {
    margin-top: 40px;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .activity-list {
    padding: 16px;

    .activity-grid {
      .activity-card {
        .activity-cover {
          height: 160px;
        }
      }
    }
  }
}
</style> 