<template>
  <div class="activity-list">
    <!-- 搜索表单 -->
    <div class="search-wrapper">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="活动名称">
          <a-input
            v-model:value="queryParams.activityName"
            placeholder="请输入活动名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="企业名称">
          <a-input
            v-model:value="queryParams.companyName"
            placeholder="请输入企业名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="activityList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <!-- 活动封面列 -->
      <template #coverImage="{ text }">
        <a-image
          :src="text || defaultImage"
          alt="活动封面"
          style="width: 120px; height: 80px; object-fit: cover;"
          :preview="{
            src: text || defaultImage,
            mask: false
          }"
        />
      </template>

      <!-- 活动名称列 -->
      <template #activityName="{ text, record }">
        <a @click="handleViewActivity(record)">{{ text }}</a>
      </template>

      <!-- 活动时间列 -->
      <template #activityTime="{ record }">
        <div>{{ formatDateTime(record.startTime) }}</div>
        <div>至</div>
        <div>{{ formatDateTime(record.endTime) }}</div>
      </template>

      <!-- 状态列 -->
      <template #status="{ text }">
        <a-tag :color="getStatusColor(text)">
          {{ getStatusText(text) }}
        </a-tag>
      </template>

      <!-- 操作列 -->
      <template #action="{ record }">
        <a-button 
          v-if="record.status !== 2"
          type="link" 
          danger
          @click="handleCancel(record)"
        >
          取消报名
        </a-button>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getActivityList, registerActivity } from '@/api/merchant/activity'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  activityName: '',
  companyName: '',
  pageNum: 1,
  pageSize: 10
})

// 加载状态
const loading = ref(false)

// 表格列定义
const columns = [
  {
    title: '活动封面',
    dataIndex: 'coverImage',
    width: '150px',
    slots: { 
      customRender: 'coverImage' 
    }
  },
  {
    title: '活动名称',
    dataIndex: 'activityName',
    width: '20%',
    slots: {
      customRender: 'activityName'
    }
  },
  {
    title: '企业名称',
    dataIndex: 'companyName',
    width: '15%'
  },
  {
    title: '活动时间',
    dataIndex: 'activityTime',
    width: '20%',
    slots: {
      customRender: 'activityTime'
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: '10%',
    slots: {
      customRender: 'status'
    }
  },
  {
    title: '操作',
    width: '15%',
    slots: { 
      customRender: 'action' 
    }
  }
]

// 分页配置
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
  showSizeChanger: true,
  showQuickJumper: true
})

// 活动列表数据
const activityList = ref([])

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const { data } = await getActivityList({
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      activityName: queryParams.activityName,
      companyName: queryParams.companyName
    })
    activityList.value = data.map(item => ({
      id: item.id,
      coverImage: item.coverImage,
      activityName: item.title,
      companyId: item.companyId,
      companyName: item.companyName,
      startTime: item.startTime,
      endTime: item.endTime,
      status: item.status,
      registered: item.registered || false
    }))
    pagination.total = data.length || 0
  } catch (error) {
    console.error('获取活动列表失败:', error)
    message.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  queryParams.pageNum = 1
  fetchList()
}

// 重置
const handleReset = () => {
  queryParams.activityName = ''
  queryParams.companyName = ''
  queryParams.pageNum = 1
  fetchList()
}

// 表格变化
const handleTableChange = (pag) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  fetchList()
}

// 查看活动详情
const handleViewActivity = (record) => {
  router.push(`/activity/${record.id}`)
}

// 报名活动
const handleRegister = async (record) => {
  try {
    await registerActivity({
      activityId: record.id,
      companyId: record.companyId
    })
    message.success('报名成功')
    fetchList()
  } catch (error) {
    message.error('报名失败')
  }
}

// 判断是否可以报名
const canRegister = (record) => {
  if (record.registered) return false
  const now = dayjs()
  const endTime = dayjs(record.endTime)
  return record.status === 1 && now.isBefore(endTime)
}

// 格式化日期时间
const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colors = {
    0: 'default',   // 未开始
    1: 'success',   // 进行中
    2: 'warning',   // 已结束
    3: 'error'      // 已取消
  }
  return colors[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '未开始',
    1: '进行中',
    2: '已结束',
    3: '已取消'
  }
  return texts[status] || '未知'
}

// 初始化加载数据
onMounted(() => {
  fetchList()
})
</script>

<style lang="less" scoped>
.activity-list {
  .search-wrapper {
    margin-bottom: 16px;
    padding: 24px;
    background: #fff;
  }

  :deep(.ant-table-wrapper) {
    background: #fff;
    padding: 24px;

    .ant-table-cell a {
      color: #1890ff;
      cursor: pointer;
      
      &:hover {
        color: #40a9ff;
      }
    }
  }
}
</style> 