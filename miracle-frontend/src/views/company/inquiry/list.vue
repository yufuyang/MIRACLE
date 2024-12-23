<template>
  <div class="inquiry-list">
    <div class="page-header">
      <h2>意向列表</h2>
    </div>

    <!-- 搜索表单 -->
    <a-form layout="inline" class="search-form">
      <a-form-item label="产品名称">
        <a-input
          v-model:value="searchForm.productName"
          placeholder="请输入产品名称"
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
          <a-select-option value="PENDING">未处理</a-select-option>
          <a-select-option value="PROCESSED">已处理</a-select-option>
          <a-select-option value="IGNORED">已忽略</a-select-option>
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

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="inquiries"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <!-- 状态列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <!-- 操作列 -->
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button
              type="link"
              size="small"
              @click="handleView(record)"
              v-if="record.status === 'PENDING'"
            >
              <template #icon><eye-outlined /></template>
              查看
            </a-button>
            <a-button
              type="link"
              size="small"
              @click="handleProcess(record)"
              v-if="record.status === 'PENDING'"
            >
              <template #icon><check-outlined /></template>
              处理
            </a-button>
            <a-button
              type="link"
              size="small"
              @click="handleIgnore(record)"
              v-if="record.status === 'PENDING'"
            >
              <template #icon><stop-outlined /></template>
              忽略
            </a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 查看意向详情对话框 -->
    <a-modal
      v-model:visible="viewModalVisible"
      title="意向详情"
      :footer="null"
      width="600px"
    >
      <a-descriptions bordered>
        <a-descriptions-item label="产品名称" :span="3">
          {{ currentInquiry?.productName }}
        </a-descriptions-item>
        <a-descriptions-item label="商户名称" :span="3">
          {{ currentInquiry?.merchantName }}
        </a-descriptions-item>
        <a-descriptions-item label="联系人" :span="2">
          {{ currentInquiry?.contactName }}
        </a-descriptions-item>
        <a-descriptions-item label="联系电话">
          {{ currentInquiry?.contactPhone }}
        </a-descriptions-item>
        <a-descriptions-item label="意向描述" :span="3">
          {{ currentInquiry?.description }}
        </a-descriptions-item>
        <a-descriptions-item label="提交时间" :span="3">
          {{ currentInquiry?.createTime }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>

    <!-- 处理意向对话框 -->
    <a-modal
      v-model:visible="processModalVisible"
      title="处理意向"
      @ok="handleProcessSubmit"
    >
      <a-form :model="processForm" layout="vertical">
        <a-form-item
          label="处理备注"
          name="remark"
          :rules="[{ required: true, message: '请输入处理备注' }]"
        >
          <a-textarea
            v-model:value="processForm.remark"
            :rows="4"
            placeholder="请输入处理备注"
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
  EyeOutlined,
  CheckOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

// 搜索表单数据
const searchForm = reactive({
  productName: '',
  status: undefined,
  dateRange: []
})

// 表格列定义
const columns = [
  {
    title: '产品名称',
    dataIndex: 'productName',
    key: 'productName'
  },
  {
    title: '商户名称',
    dataIndex: 'merchantName',
    key: 'merchantName'
  },
  {
    title: '联系人',
    dataIndex: 'contactName',
    key: 'contactName'
  },
  {
    title: '联系电话',
    dataIndex: 'contactPhone',
    key: 'contactPhone'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: '提交时间',
    dataIndex: 'createTime',
    key: 'createTime',
    sorter: true
  },
  {
    title: '操作',
    key: 'action',
    width: 200
  }
]

// 表格数据
const inquiries = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true
})

// 查看详情相关
const viewModalVisible = ref(false)
const currentInquiry = ref(null)

// 处理意向相关
const processModalVisible = ref(false)
const processForm = reactive({
  remark: ''
})

// 获取状态颜色
const getStatusColor = (status) => {
  const colors = {
    PENDING: 'blue',
    PROCESSED: 'green',
    IGNORED: 'gray'
  }
  return colors[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '未处理',
    PROCESSED: '已处理',
    IGNORED: '已忽略'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchInquiries()
}

// 重置搜索条件
const handleReset = () => {
  searchForm.productName = ''
  searchForm.status = undefined
  searchForm.dateRange = []
  handleSearch()
}

// 表格变化处理
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchInquiries()
}

// 查看详情
const handleView = (record) => {
  currentInquiry.value = record
  viewModalVisible.value = true
}

// 处理意向
const handleProcess = (record) => {
  currentInquiry.value = record
  processForm.remark = ''
  processModalVisible.value = true
}

// 提交处理
const handleProcessSubmit = async () => {
  if (!processForm.remark) {
    message.error('请输入处理备注')
    return
  }

  loading.value = true
  try {
    const res = await processInquiry({
      id: currentInquiry.value.id,
      remark: processForm.remark
    })
    if (res.code === 200) {
      message.success('处理成功')
      processModalVisible.value = false
      fetchInquiries()
    }
  } catch (error) {
    console.error('处理意向失败:', error)
    message.error('处理失败')
  } finally {
    loading.value = false
  }
}

// 忽略意向
const handleIgnore = async (record) => {
  loading.value = true
  try {
    const res = await ignoreInquiry(record.id)
    if (res.code === 200) {
      message.success('已忽略')
      fetchInquiries()
    }
  } catch (error) {
    console.error('忽略意向失败:', error)
    message.error('操作失败')
  } finally {
    loading.value = false
  }
}

// 获取意向列表数据
const fetchInquiries = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.pageSize,
      productName: searchForm.productName,
      status: searchForm.status,
      startTime: searchForm.dateRange[0]?.format('YYYY-MM-DD HH:mm:ss'),
      endTime: searchForm.dateRange[1]?.format('YYYY-MM-DD HH:mm:ss')
    }

    const res = await getInquiryList(params)
    if (res.code === 200) {
      inquiries.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取意向列表失败:', error)
    message.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化
fetchInquiries()
</script>

<style scoped lang="less">
.inquiry-list {
  .page-header {
    margin-bottom: 16px;

    h2 {
      margin: 0;
    }
  }

  .search-form {
    margin-bottom: 16px;
    padding: 16px;
    background: #fff;
    border-radius: 4px;
  }

  :deep(.ant-descriptions-item-label) {
    width: 100px;
  }
}
</style> 