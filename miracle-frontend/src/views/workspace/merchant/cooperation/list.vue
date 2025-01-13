<template>
  <div class="cooperation-list">
    <!-- 搜索表单 -->
    <div class="search-wrapper">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="企业名称">
          <a-input
            v-model:value="queryParams.companyName"
            placeholder="请输入企业名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="queryParams.status"
            placeholder="请选择状态"
            style="width: 120px"
            allow-clear
          >
            <a-select-option :value="0">待处理</a-select-option>
            <a-select-option :value="1">已合作</a-select-option>
            <a-select-option :value="2">已拒绝</a-select-option>
            <a-select-option :value="3">已终止</a-select-option>
          </a-select>
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
      :data-source="cooperationList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <!-- 企业Logo列 -->
      <template #companyLogo="{ text }">
        <a-image
          :src="text || defaultImage"
          alt="企业Logo"
          style="width: 80px; height: 80px; object-fit: cover;"
          :preview="{
            src: text || defaultImage,
            mask: false
          }"
        />
      </template>

      <!-- 状态列 -->
      <template #status="{ text }">
        <a-tag :color="getStatusColor(text)">
          {{ getStatusText(text) }}
        </a-tag>
      </template>

      <!-- 操作列 -->
      <template #action="{ record }">
        <a-space>
          <a @click="handleViewCompany(record)">查看企业</a>
          <!-- 待处理状态显示同意/拒绝按钮 -->
          <template v-if="record.status === 0">
            <a-button 
              type="link" 
              @click="handleApprove(record)"
            >
              同意
            </a-button>
            <a-button 
              type="link" 
              danger
              @click="handleReject(record)"
            >
              拒绝
            </a-button>
          </template>
        </a-space>
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
import {
  getCooperationList,
  handleCooperation,
  cancelCooperation
} from '@/api/merchant/cooperation'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  companyName: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 加载状态
const loading = ref(false)

// 表格列定义
const columns = [
  {
    title: '企业Logo',
    dataIndex: 'companyLogo',
    width: '120px',
    slots: { 
      customRender: 'companyLogo' 
    }
  },
  {
    title: '企业名称',
    dataIndex: 'companyName',
    width: '20%'
  },
  {
    title: '联系人',
    dataIndex: 'contactName',
    width: '15%'
  },
  {
    title: '联系电话',
    dataIndex: 'contactPhone',
    width: '15%'
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
    title: '申请时间',
    dataIndex: 'createTime',
    width: '15%',
    customRender: ({ text }) => formatDateTime(text)
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

// 合作列表数据
const cooperationList = ref([])

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const { data } = await getCooperationList({
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      companyName: queryParams.companyName,
      status: queryParams.status
    })
    cooperationList.value = data.map(item => ({
      id: item.id,
      companyId: item.companyId,
      companyLogo: item.companyLogo,
      companyName: item.companyName,
      contactName: item.companyContactName,
      contactPhone: item.companyContactPhone,
      status: item.status,
      createTime: item.createTime
    }))
    pagination.total = data.length || 0
  } catch (error) {
    console.error('获取合作列表失败:', error)
    message.error('获取合作列表失败')
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
  queryParams.companyName = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  fetchList()
}

// 表格变化
const handleTableChange = (pag) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  fetchList()
}

// 查看企业
const handleViewCompany = (record) => {
  router.push(`/company/${record.companyId}`)
}

// 格式化日期时间
const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colors = {
    0: 'warning',    // 待处理
    1: 'success',    // 已合作
    2: 'error',      // 已拒绝
    3: 'default'     // 已终止
  }
  return colors[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '待处理',
    1: '已合作',
    2: '已拒绝',
    3: '已终止'
  }
  return texts[status] || '未知'
}

// 同意合作
const handleApprove = async (record) => {
  try {
    await handleCooperation({
      id: record.id,
      companyId: record.companyId,
      status: 1
    })
    message.success('已同意合作')
    fetchList()
  } catch (error) {
    message.error('操作失败')
  }
}

// 拒绝合作
const handleReject = async (record) => {
  try {
    await handleCooperation({
      id: record.id,
      companyId: record.companyId,
      status: 2
    })
    message.success('已拒绝合作')
    fetchList()
  } catch (error) {
    message.error('操作失败')
  }
}

// 初始化加载数据
onMounted(() => {
  fetchList()
})
</script>

<style lang="less" scoped>
.cooperation-list {
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