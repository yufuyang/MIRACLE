<template>
  <div class="activity-detail">
    <div class="page-header">
      <a-page-header
        :title="activity?.title"
        @back="() => router.back()"
      >
        <template #extra>
          <a-button @click="handleEdit">编辑</a-button>
        </template>
      </a-page-header>
    </div>

    <a-spin :spinning="loading">
      <a-card title="基本信息" style="margin-bottom: 24px">
        <a-descriptions :column="2">
          <a-descriptions-item label="活动标题">{{ activity?.title }}</a-descriptions-item>
          <a-descriptions-item label="活动状态">
            <a-tag :color="getStatusColor(activity?.status)">
              {{ getStatusText(activity?.status) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="开始时间">{{ activity?.startTime }}</a-descriptions-item>
          <a-descriptions-item label="结束时间">{{ activity?.endTime }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ activity?.createTime }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ activity?.updateTime }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <a-card title="活动封面" style="margin-bottom: 24px">
        <div class="cover-image">
          <div class="image-wrapper">
            <a-image :src="activity?.coverImage" />
          </div>
        </div>
      </a-card>

      <a-card title="活动描述" style="margin-bottom: 24px">
        <p class="description">{{ activity?.description || '-' }}</p>
      </a-card>

      <a-card title="报名人员">
        <div class="table-operations" style="margin-bottom: 16px">
          <a-form layout="inline">
            <a-form-item label="商户名">
              <a-input
                v-model:value="searchForm.merchantName"
                placeholder="请输入商户名"
                allowClear
              />
            </a-form-item>
            <a-form-item label="状态">
              <a-select
                v-model:value="searchForm.status"
                placeholder="请选择状态"
                allowClear
                style="width: 120px"
              >
                <a-select-option :value="0">待审核</a-select-option>
                <a-select-option :value="1">已通过</a-select-option>
                <a-select-option :value="2">已拒绝</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handleSearch">
                搜索
              </a-button>
              <a-button style="margin-left: 8px" @click="handleReset">
                重置
              </a-button>
            </a-form-item>
          </a-form>
        </div>

        <a-table
          :columns="columns"
          :data-source="participants"
          :loading="participantsLoading"
          :pagination="pagination"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getParticipantStatusColor(record.status)">
                {{ getParticipantStatusText(record.status) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button 
                  type="link" 
                  :disabled="record.status !== 0"
                  @click="handleAudit(record, 1)"
                >
                  通过
                </a-button>
                <a-button 
                  type="link" 
                  danger 
                  :disabled="record.status !== 0"
                  @click="handleAudit(record, 2)"
                >
                  拒绝
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { getActivityDetail, deleteActivity, getActivityRegistrations, auditActivityRegistration } from '@/api/activity'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const activity = ref(null)
const participants = ref([])
const participantsLoading = ref(false)
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: total => `共 ${total} 条`
})

const searchForm = ref({
  merchantName: '',
  status: undefined
})

const columns = [
  {
    title: '商户名',
    dataIndex: 'merchantName',
    key: 'merchantName'
  },
  {
    title: '报名时间',
    dataIndex: 'createTime',
    key: 'createTime'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: '操作',
    key: 'action',
    width: 150
  }
]

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

// 获取参与者状态文本
const getParticipantStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 获取参与者状态颜色
const getParticipantStatusColor = (status) => {
  const colorMap = {
    0: 'gold',
    1: 'green',
    2: 'red'
  }
  return colorMap[status] || 'default'
}

// 获取活动详情
const fetchActivityDetail = async () => {
  loading.value = true
  try {
    const res = await getActivityDetail(route.params.id)
    activity.value = res.data
  } catch (error) {
    message.error('获取活动详情失败')
  } finally {
    loading.value = false
  }
}

// 获取活动报名人员
const fetchParticipants = async () => {
  participantsLoading.value = true
  try {
    const params = {
      activityId: route.params.id,
      pageNum: pagination.value.current,
      pageSize: pagination.value.pageSize,
      merchantName: searchForm.value.merchantName,
      status: searchForm.value.status
    }
    const res = await getActivityRegistrations(params)
    participants.value = res.data || []
    pagination.value.total = res.total || 0
  } catch (error) {
    message.error('获取报名人员失败')
  } finally {
    participantsLoading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  pagination.value.current = 1
  fetchParticipants()
}

// 处理重置
const handleReset = () => {
  searchForm.value.merchantName = ''
  searchForm.value.status = undefined
  pagination.value.current = 1
  fetchParticipants()
}

// 处理表格分页变化
const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchParticipants()
}

// 处理审批
const handleAudit = (record, status) => {
  const statusText = status === 1 ? '通过' : '拒绝'
  Modal.confirm({
    title: `确定${statusText}该报名申请吗？`,
    async onOk() {
      try {
        await auditActivityRegistration({
          id: record.id,
          status
        })
        message.success('操作成功')
        fetchParticipants()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

// 编辑活动
const handleEdit = () => {
  router.push(`/workspace/activity/edit/${route.params.id}`)
}

onMounted(() => {
  fetchActivityDetail()
  fetchParticipants()
})
</script>

<style lang="less" scoped>
.activity-detail {
  padding: 24px;
  background: #f0f2f5;

  .page-header {
    margin-bottom: 24px;
    background: #fff;
  }

  .cover-image {
    display: flex;
    justify-content: center;
    align-items: center;
    
    .image-wrapper {
      width: 800px;  // 限制封面宽度
      
      :deep(.ant-image) {
        width: 100%;
        height: 400px;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
    }
  }

  .description {
    font-size: 14px;
    line-height: 1.6;
    color: rgba(0, 0, 0, 0.65);
    white-space: pre-wrap;
  }

  .table-operations {
    margin-bottom: 16px;

    :deep(.ant-form-item) {
      margin-bottom: 16px;
      
      &:last-child {
        margin-right: 0;
      }
    }
  }
}
</style> 