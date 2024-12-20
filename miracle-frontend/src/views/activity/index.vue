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
        <a-col :span="8" v-for="activity in activityList" :key="activity.id">
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
                  >
                    {{ getButtonText(activity.status) }}
                  </a-button>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
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
        @showSizeChange="onShowSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { TeamOutlined, CalendarOutlined, EnvironmentOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/images/default.jpg'

const router = useRouter()

// 模拟活动数据
const mockActivities = [
  {
    id: 1,
    title: '2024智能制造创新峰会',
    description: '探讨智能制造最新趋势，分享行业创新实践',
    companyName: '科技创新有限公司',
    startTime: '2024-03-15',
    location: '上海',
    status: '即将开始',
    imageUrl: defaultImage
  },
  {
    id: 2,
    title: '工业互联网技术研讨会',
    description: '深入探讨工业互联网技术应用与发展方向',
    companyName: '环保科技股份公司',
    startTime: '2024-03-20',
    location: '北京',
    status: '报名中',
    imageUrl: defaultImage
  },
  {
    id: 3,
    title: '智能工厂解决方案展',
    description: '展示最新智能工厂解决方案和成功案例',
    companyName: '智能制造技术公司',
    startTime: '2024-04-01',
    location: '深圳',
    status: '报名中',
    imageUrl: defaultImage
  }
]

// 搜索表单
const searchForm = reactive({
  pageNum: 1,
  pageSize: 9,
  companyName: '',
  status: undefined
})

// 根据搜索条件筛选活动
const filteredActivities = computed(() => {
  let result = [...mockActivities]
  
  // 按企业名称筛选
  if (searchForm.companyName) {
    result = result.filter(item => 
      item.companyName.toLowerCase().includes(searchForm.companyName.toLowerCase())
    )
  }
  
  // 按状态筛选
  if (searchForm.status) {
    result = result.filter(item => item.status === searchForm.status)
  }
  
  return result
})

// 分页后的活动列表
const activityList = computed(() => {
  const start = (searchForm.pageNum - 1) * searchForm.pageSize
  const end = start + searchForm.pageSize
  return filteredActivities.value.slice(start, end)
})

// 总数
const total = computed(() => filteredActivities.value.length)

// 搜索
const onSearch = () => {
  searchForm.pageNum = 1
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
}

// 每页条数变化
const onShowSizeChange = (current, size) => {
  searchForm.pageSize = size
  searchForm.pageNum = 1
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

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/activity/${id}`)
}
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

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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