<template>
  <div class="activity-list">
    <!-- 搜索区域 -->
    <div class="filter-section">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="企业名称">
          <a-input
            v-model:value="searchForm.companyName"
            placeholder="请输入企业名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="活动状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择活动状态"
            style="width: 120px"
            allow-clear
          >
            <a-select-option value="0">未开始</a-select-option>
            <a-select-option value="1">进行中</a-select-option>
            <a-select-option value="2">已结束</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="onSearch">
              <template #icon><search-outlined /></template>
              搜索
            </a-button>
            <a-button @click="onReset">
              <template #icon><redo-outlined /></template>
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>

    <!-- 活动列表 -->
    <div class="activity-grid">
      <a-spin :spinning="loading">
        <a-row :gutter="[24, 24]">
          <template v-if="activities.length > 0">
            <a-col :xs="24" :sm="12" :md="8" v-for="activity in activities" :key="activity.id">
              <a-card hoverable class="activity-card" @click="goToDetail(activity.id)">
                <template #cover>
                  <a-image
                    :src="activity.coverImage || defaultImage"
                    :alt="activity.title"
                    :preview="false"
                    style="height: 200px; object-fit: cover"
                  />
                </template>
                <a-card-meta :title="activity.title">
                  <template #description>
                    <div class="activity-info">
                      <div class="activity-time">
                        <calendar-outlined />
                        <div class="time-range">
                          <div>开始：{{ formatDate(activity.startTime) }}</div>
                          <div>结束：{{ formatDate(activity.endTime) }}</div>
                        </div>
                      </div>
                      <div class="activity-company" v-if="activity.companyName">
                        <team-outlined />
                        <span>{{ activity.companyName }}</span>
                      </div>
                      <div class="activity-location" v-if="activity.address">
                        <environment-outlined />
                        <span>{{ activity.address }}</span>
                      </div>
                      <div class="activity-stats">
                        <div class="stats-group">
                          <span class="stat-item">
                            <eye-outlined /> 浏览数：{{ activity.viewCount || 0 }}
                          </span>
                          <span class="stat-item">
                            <user-outlined /> 报名数：{{ activity.registerCount || 0 }}
                          </span>
                        </div>
                        <a-tag :color="getStatusColor(activity.status)">
                          {{ getStatusText(activity.status) }}
                        </a-tag>
                      </div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </template>
          <template v-else>
            <a-empty description="暂无活动" />
          </template>
        </a-row>
      </a-spin>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
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
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { 
  SearchOutlined, 
  RedoOutlined,
  TeamOutlined, 
  CalendarOutlined, 
  EnvironmentOutlined,
  EyeOutlined,
  UserOutlined 
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import defaultImage from '@/assets/images/default.jpg'
import { getActivityList } from '@/api/activity'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 12,
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
    if (res.success) {
      activities.value = res.data || []
      total.value = res.total || 0
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

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
  return dayjs(date).format('YYYY-MM-DD HH:mm')
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
  searchForm.pageNum = 1
  loadActivities()
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
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  }

  .activity-grid {
    margin-bottom: 24px;

    .activity-card {
      height: 100%;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      }

      :deep(.ant-card-meta-title) {
        font-size: 16px;
        margin-bottom: 12px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .activity-info {
        .activity-time,
        .activity-company,
        .activity-location {
          margin-bottom: 12px;
          color: #666;
          font-size: 14px;
          display: flex;
          align-items: flex-start;
          gap: 8px;

          .anticon {
            color: #1890ff;
            margin-top: 3px;
          }

          .time-range {
            flex: 1;
            div {
              line-height: 1.6;
            }
          }
        }

        .activity-stats {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-top: 12px;
          padding-top: 12px;
          border-top: 1px solid #f0f0f0;

          .stats-group {
            display: flex;
            gap: 16px;
          }

          .stat-item {
            color: #666;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 4px;

            .anticon {
              color: #1890ff;
            }
          }
        }
      }
    }
  }

  .pagination {
    text-align: center;
    margin-top: 24px;
  }
}

@media (max-width: 768px) {
  .activity-list {
    padding: 16px;

    .filter-section {
      padding: 16px;
      
      :deep(.ant-form-item) {
        margin-bottom: 16px;
      }
    }
  }
}
</style> 