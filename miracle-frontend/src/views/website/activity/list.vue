<template>
  <div class="activity-page">
    <div class="activity-list">
      <!-- 搜索区域 -->
      <div class="search-area">
        <div class="search-item">
          <span class="label">活动状态：</span>
          <a-select v-model:value="searchForm.status" placeholder="请选择状态" allowClear>
            <a-select-option :value="0">未开始</a-select-option>
            <a-select-option :value="1">进行中</a-select-option>
            <a-select-option :value="2">已结束</a-select-option>
          </a-select>
        </div>
        <a-button type="primary" @click="onSearch">查询</a-button>
        <a-button @click="onReset">重置</a-button>
      </div>

      <!-- 活动列表 -->
      <div class="activity-grid">
        <a-spin :spinning="loading">
          <a-row :gutter="[24, 24]">
            <a-col :xs="24" :sm="12" :md="8" v-for="activity in activities" :key="activity.id">
              <a-card hoverable class="activity-card" @click="goToDetail(activity.id)">
                <template #cover>
                  <img :alt="activity.title" :src="activity.coverImage || defaultImage" style="height: 200px; object-fit: cover;" />
                </template>
                <a-card-meta :title="activity.title">
                  <template #description>
                    <div class="activity-info">
                      <div class="activity-time">
                        <calendar-outlined /> {{ formatDateTime(activity.startTime) }} ~ {{ formatDateTime(activity.endTime) }}
                      </div>
                      <div class="activity-stats">
                        <span class="stat-item">
                          <eye-outlined /> 浏览数：{{ activity.viewCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <user-outlined /> 报名数：{{ activity.registerCount || 0 }}
                        </span>
                        <a-tag :color="getStatusColor(activity.status)">
                          {{ getStatusText(activity.status) }}
                        </a-tag>
                      </div>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </a-col>
          </a-row>
        </a-spin>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <a-pagination
          v-model:current="searchForm.pageNum"
          :total="total"
          :pageSize="searchForm.pageSize"
          @change="onPageChange"
          show-size-changer
          show-total
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  EyeOutlined,
  UserOutlined,
  CalendarOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getActivityList } from '@/api/website/activity'

console.log('前台活动列表组件开始加载')

const router = useRouter()

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

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 10,
  status: undefined
})

// 数据
const loading = ref(false)
const total = ref(0)
const activities = ref([])

// 加载活动列表
const loadActivities = async () => {
  console.log('开始加载活动列表')
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: searchForm.pageNum - 1 // 后端分页从0开始
    }
    console.log('请求参数:', params)
    const res = await getActivityList(params)
    console.log('活动列表返回:', res)
    if (res.code === 200) {
      if (Array.isArray(res.data)) {
        activities.value = res.data
        total.value = res.total || 0
      } else if (res.data && typeof res.data === 'object') {
        activities.value = res.data.list || []
        total.value = res.data.total || 0
      } else {
        activities.value = []
        total.value = 0
      }
      console.log('处理后的活动列表:', activities.value)
      console.log('总数:', total.value)
      if (activities.value.length === 0) {
        message.info('暂无活动数据')
      }
    } else {
      console.error('请求失败:', res)
      message.error(res.message || '获取活动列表失败')
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    message.error(error.message || '获取活动列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const onSearch = () => {
  console.log('执行搜索')
  searchForm.pageNum = 1
  loadActivities()
}

// 重置
const onReset = () => {
  console.log('执行重置')
  searchForm.status = undefined
  searchForm.pageNum = 1
  loadActivities()
}

// 分页变化
const onPageChange = (page, pageSize) => {
  console.log('分页变化:', { page, pageSize })
  searchForm.pageNum = page
  searchForm.pageSize = pageSize
  loadActivities()
}

// 跳转到详情页
const goToDetail = (id) => {
  console.log('跳转到详情页:', id)
  router.push({
    name: 'WebsiteActivityDetail',
    params: { id }
  })
}

// 格式化时间
const formatDateTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').slice(0, 16)
}

// 初始化
onMounted(() => {
  console.log('前台活动列表组件加载完成，开始获取活动列表')
  nextTick(() => {
    loadActivities()
  })
})

// 添加错误处理
window.addEventListener('error', (event) => {
  console.error('全局错误:', event)
})
</script>

<style scoped lang="less">
.activity-page {
  min-height: calc(100vh - 64px);
  background-color: #f0f2f5;
  
  .activity-list {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;

    .search-area {
      background: #fff;
      padding: 24px;
      border-radius: 8px;
      margin-bottom: 24px;
      display: flex;
      align-items: center;
      gap: 16px;

      .search-item {
        display: flex;
        align-items: center;
        gap: 8px;

        .label {
          white-space: nowrap;
          color: #666;
        }

        .ant-select {
          width: 200px;
        }
      }
    }

    .activity-grid {
      margin-bottom: 24px;
      background: #fff;
      padding: 24px;
      border-radius: 8px;

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
          .activity-time {
            color: #666;
            font-size: 14px;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            gap: 8px;

            .anticon {
              color: #1890ff;
            }
          }

          .activity-stats {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-top: 12px;
            padding-top: 12px;
            border-top: 1px solid #f0f0f0;

            .stat-item {
              color: #666;
              font-size: 14px;
              display: flex;
              align-items: center;
              gap: 8px;

              .anticon {
                color: #1890ff;
                margin-left: 8px;
              }
            }
          }

          .activity-description {
            margin-top: 12px;
            color: #666;
            font-size: 14px;
            line-height: 1.5;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }
        }
      }
    }

    .pagination {
      text-align: center;
      margin-top: 24px;
      background: #fff;
      padding: 16px;
      border-radius: 8px;
    }
  }
}
</style> 