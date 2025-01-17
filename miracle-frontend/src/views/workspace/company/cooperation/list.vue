<template>
  <div class="cooperation-list">
    <a-card :bordered="false">
      <!-- 搜索表单 -->
      <div class="table-page-search-wrapper">
        <div class="search-form">
          <div class="search-item">
            <span class="label">商户名称：</span>
            <a-input v-model:value="queryParams.merchantName" placeholder="请输入商户名称" allow-clear />
          </div>
          <div class="search-item">
            <span class="label">状态：</span>
            <a-select 
              v-model:value="queryParams.status" 
              placeholder="请选择状态" 
              style="width: 200px"
              allow-clear
            >
              <a-select-option :value="0">待处理</a-select-option>
              <a-select-option :value="1">已合作</a-select-option>
              <a-select-option :value="2">已拒绝</a-select-option>
              <a-select-option :value="3">已终止</a-select-option>
            </a-select>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
          </div>
        </div>
      </div>

      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="cooperationList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <!-- 状态列 -->
        <template #status="{ text }">
          <a-tag :color="getStatusColor(text)">
            {{ getStatusText(text) }}
          </a-tag>
        </template>

        <!-- 操作列 -->
        <template #action="{ record }">
          <template v-if="record.status === 1 || record.status === 3">
            <a-button 
              type="link" 
              danger
              @click="showCancelConfirm(record)"
              v-if="record.status === 1"
            >
              终止合作
            </a-button>
            <a-button 
              type="link"
              @click="handleRestart(record)"
              v-if="record.status === 3"
            >
              重新合作
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 确认弹框 -->
    <a-modal
      v-model:visible="cancelModal.visible"
      title="终止合作"
      @ok="handleCancel"
      @cancel="cancelModal.visible = false"
      :confirmLoading="cancelModal.loading"
    >
      <p>确定要终止与商户 "{{ cancelModal.record?.merchantName }}" 的合作吗？</p>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getCooperationList, cancelCooperation, handleCooperation } from '@/api/company/cooperation'

// 查询参数
const queryParams = ref({
  merchantName: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true
})

// 表格列定义
const columns = [
  {
    title: '商户名称',
    dataIndex: 'merchantName',
    key: 'merchantName'
  },
  {

    title: '营业执照号',

    dataIndex: 'licenseNo',

    key: 'licenseNo'

  },
  {

    title: '商户地址',

    dataIndex: 'address',

    key: 'address',

    customRender: ({ record }) => {
      return `${record.province} ${record.city} ${record.address}`
    }
  },
  {
    title: '联系人',
    dataIndex: 'merchantContactName',
    key: 'merchantContactName'
  },
  {
    title: '联系电话',
    dataIndex: 'merchantContactPhone',
    key: 'merchantContactPhone'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    slots: { customRender: 'status' }
  },
  {
    title: '开始时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    slots: { customRender: 'action' }
  }
]

const loading = ref(false)
const cooperationList = ref([])

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const { data } = await getCooperationList({
      pageNum: pagination.value.current,
      pageSize: pagination.value.pageSize,
      merchantName: queryParams.value.merchantName,
      status: queryParams.value.status
    })
    cooperationList.value = data.map(item => ({
      id: item.id,
      merchantId: item.merchantId,
      merchantName: item.merchantName,
      merchantContactName: item.merchantContactName,
      merchantContactPhone: item.merchantContactPhone,
      status: item.status,
      createTime: item.createTime,
      licenseNo: item.merchantLicenseNo || '-',
      province: item.merchantProvince || '',
      city: item.merchantCity || '',
      address: item.merchantAddress || ''
    }))
    pagination.value.total = data.total || 0
  } catch (error) {
    console.error('获取合作列表失败:', error)
    message.error('获取合作列表失败')
  } finally {
    loading.value = false
  }
}

// 状态相关
const getStatusColor = (status) => {
  const colors = {
    0: 'warning',    // 待处理
    1: 'success',    // 已合作
    2: 'error',      // 已拒绝
    3: 'default'     // 已终止
  }
  return colors[status] || 'default'
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待处理',
    1: '已合作',
    2: '已拒绝',
    3: '已终止'
  }
  return statusMap[status] || '未知'
}

// 取消合作相关
const cancelModal = ref({
  visible: false,
  loading: false,
  record: null
})

const showCancelConfirm = (record) => {
  cancelModal.value.record = record
  cancelModal.value.visible = true
}

const handleCancel = async () => {
  const record = cancelModal.value.record
  if (!record) return

  cancelModal.value.loading = true
  try {
    await cancelCooperation(record.id)
    message.success('已终止合作')
    fetchList()
    cancelModal.value.visible = false
  } catch (error) {
    message.error('终止合作失败')
  } finally {
    cancelModal.value.loading = false
  }
}

// 表格事件处理
const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  queryParams.value.pageNum = pag.current
  queryParams.value.pageSize = pag.pageSize
  fetchList()
}

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  queryParams.value.pageNum = 1
  fetchList()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    merchantName: '',
    status: undefined,
    pageNum: 1,
    pageSize: 10
  }
  fetchList()
}

// 重新合作
const handleRestart = async (record) => {
  try {
    await handleCooperation({
      id: record.id,
      merchantId: record.merchantId,
    })
    message.success('已重新合作')
    fetchList()
  } catch (error) {
    message.error('重新合作失败')
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style lang="less" scoped>
.cooperation-list {
  padding: 24px;

  .table-page-search-wrapper {
    margin-bottom: 24px;

    .search-form {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;
      
      .search-item {
        display: flex;
        align-items: center;
        
        .label {
          margin-right: 8px;
          white-space: nowrap;
        }
        
        .ant-input {
          width: 200px;
        }
        
        .ant-btn {
          margin-left: 16px;
        }
      }
    }
  }

  .ant-table-wrapper {
    .ant-table-pagination {
      margin: 16px 0;
    }
  }
}
</style> 