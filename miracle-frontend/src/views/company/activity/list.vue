<template>
  <div class="activity-list">
    <div class="page-header">
      <h2>活动列表</h2>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <a-form layout="inline" class="search-form">
        <a-form-item label="活动名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入活动名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            style="width: 120px"
            allow-clear
          >
            <a-select-option value="DRAFT">草稿</a-select-option>
            <a-select-option value="PUBLISHED">已发布</a-select-option>
            <a-select-option value="ENDED">已结束</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="时间范围">
          <a-range-picker
            v-model:value="searchForm.dateRange"
            :show-time="{ format: 'HH:mm' }"
            format="YYYY-MM-DD HH:mm"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <template #icon><search-outlined /></template>
              搜索
            </a-button>
            <a-button @click="handleReset">
              <template #icon><redo-outlined /></template>
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <div class="operation">
        <a-button type="primary" @click="handleAdd">
          <template #icon><plus-outlined /></template>
          新建活动
        </a-button>
      </div>
    </div>

    <!-- 活动列表 -->
    <a-table
      :columns="columns"
      :data-source="activities"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <!-- 封面图列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'cover'">
          <img
            :src="record.cover"
            alt="活动封面"
            style="width: 80px; height: 60px; object-fit: cover"
          />
        </template>
        <!-- 状态列 -->
        <template v-else-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <!-- 操作列 -->
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleEdit(record)">
              <template #icon><edit-outlined /></template>
              编辑
            </a-button>
            <a-button
              type="link"
              size="small"
              @click="handlePublish(record)"
              v-if="record.status === 'DRAFT'"
            >
              <template #icon><cloud-upload-outlined /></template>
              发布
            </a-button>
            <a-button
              type="link"
              size="small"
              @click="handleEnd(record)"
              v-if="record.status === 'PUBLISHED'"
            >
              <template #icon><poweroff-outlined /></template>
              结束
            </a-button>
            <a-button
              type="link"
              size="small"
              danger
              @click="handleDelete(record)"
            >
              <template #icon><delete-outlined /></template>
              删除
            </a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 活动表单对话框 -->
    <a-modal
      v-model:visible="formModalVisible"
      :title="formTitle"
      width="800px"
      @ok="handleFormSubmit"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-form-item label="活动名称" name="name">
          <a-input
            v-model:value="formData.name"
            placeholder="请输入活动名称"
          />
        </a-form-item>
        <a-form-item label="活动封面" name="cover">
          <a-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            :show-upload-list="false"
            :before-upload="beforeUpload"
            @change="handleCoverChange"
          >
            <img v-if="formData.cover" :src="formData.cover" alt="活动封面" />
            <div v-else>
              <plus-outlined />
              <div style="margin-top: 8px">上传封面</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="活动时间" name="timeRange">
          <a-range-picker
            v-model:value="formData.timeRange"
            :show-time="{ format: 'HH:mm' }"
            format="YYYY-MM-DD HH:mm"
          />
        </a-form-item>
        <a-form-item label="活动描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            :rows="4"
            placeholder="请输入活动描述"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {
  SearchOutlined,
  RedoOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  CloudUploadOutlined,
  PoweroffOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

// 搜索表单数据
const searchForm = reactive({
  name: '',
  status: undefined,
  dateRange: []
})

// 表格列定义
const columns = [
  {
    title: '活动封面',
    dataIndex: 'cover',
    key: 'cover',
    width: 100
  },
  {
    title: '活动名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '开始时间',
    dataIndex: 'startTime',
    key: 'startTime',
    width: 180
  },
  {
    title: '结束时间',
    dataIndex: 'endTime',
    key: 'endTime',
    width: 180
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 250,
    fixed: 'right'
  }
]

// 表格数据
const activities = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true
})

// 表单相关
const formRef = ref()
const formModalVisible = ref(false)
const formTitle = ref('新建活动')
const formData = reactive({
  name: '',
  cover: '',
  timeRange: [],
  description: ''
})
const formRules = {
  name: [{ required: true, message: '请输入活动名称' }],
  cover: [{ required: true, message: '请上传活动封面' }],
  timeRange: [{ required: true, message: '请选择活动时间' }],
  description: [{ required: true, message: '请输入活动描述' }]
}

// 文件上传相关
const fileList = ref([])

// 获取状态颜色
const getStatusColor = (status) => {
  const colors = {
    DRAFT: 'orange',
    PUBLISHED: 'green',
    ENDED: 'gray'
  }
  return colors[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ENDED: '已结束'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchActivities()
}

// 重置搜索条件
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = undefined
  searchForm.dateRange = []
  handleSearch()
}

// 表格变化处理
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchActivities()
}

// 新建活动
const handleAdd = () => {
  formTitle.value = '新建活动'
  formData.name = ''
  formData.cover = ''
  formData.timeRange = []
  formData.description = ''
  fileList.value = []
  formModalVisible.value = true
}

// 编辑活动
const handleEdit = (record) => {
  formTitle.value = '编辑活动'
  formData.id = record.id
  formData.name = record.name
  formData.cover = record.cover
  formData.timeRange = [record.startTime, record.endTime]
  formData.description = record.description
  fileList.value = record.cover ? [{ url: record.cover }] : []
  formModalVisible.value = true
}

// 发布活动
const handlePublish = async (record) => {
  try {
    const res = await publishActivity(record.id)
    if (res.code === 200) {
      message.success('发布成功')
      fetchActivities()
    }
  } catch (error) {
    console.error('发布活动失败:', error)
    message.error('发布失败')
  }
}

// 结束活动
const handleEnd = async (record) => {
  try {
    const res = await endActivity(record.id)
    if (res.code === 200) {
      message.success('已结束活动')
      fetchActivities()
    }
  } catch (error) {
    console.error('结束活动失败:', error)
    message.error('操作失败')
  }
}

// 删除活动
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除该活动吗？',
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await deleteActivity(record.id)
        if (res.code === 200) {
          message.success('删除成功')
          fetchActivities()
        }
      } catch (error) {
        console.error('删除活动失败:', error)
        message.error('删除失败')
      }
    }
  })
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片必须小于2MB!')
  }
  return isImage && isLt2M
}

// 处理封面变化
const handleCoverChange = (info) => {
  if (info.file.status === 'uploading') {
    loading.value = true
    return
  }
  if (info.file.status === 'done') {
    formData.cover = info.file.response.data
    loading.value = false
  }
}

// 提交表单
const handleFormSubmit = () => {
  formRef.value.validate().then(async () => {
    const params = {
      ...formData,
      startTime: formData.timeRange[0].format('YYYY-MM-DD HH:mm:ss'),
      endTime: formData.timeRange[1].format('YYYY-MM-DD HH:mm:ss')
    }
    delete params.timeRange

    try {
      const res = await (formData.id
        ? updateActivity(params)
        : createActivity(params))
      if (res.code === 200) {
        message.success(formData.id ? '更新成功' : '创建成功')
        formModalVisible.value = false
        fetchActivities()
      }
    } catch (error) {
      console.error('保存活动失败:', error)
      message.error('保存失败')
    }
  })
}

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.pageSize,
      name: searchForm.name,
      status: searchForm.status,
      startTime: searchForm.dateRange[0]?.format('YYYY-MM-DD HH:mm:ss'),
      endTime: searchForm.dateRange[1]?.format('YYYY-MM-DD HH:mm:ss')
    }

    const res = await getActivityList(params)
    if (res.code === 200) {
      activities.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    message.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化
fetchActivities()
</script>

<style scoped lang="less">
.activity-list {
  .page-header {
    margin-bottom: 16px;

    h2 {
      margin: 0;
    }
  }

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
    padding: 16px;
    background: #fff;
    border-radius: 4px;

    .search-form {
      flex: 1;
    }

    .operation {
      margin-left: 16px;
    }
  }

  :deep(.ant-upload-select-picture-card) {
    width: 300px;
    height: 169px;
  }

  :deep(.ant-upload-list-picture-card-container) {
    width: 300px;
    height: 169px;
  }
}
</style> 