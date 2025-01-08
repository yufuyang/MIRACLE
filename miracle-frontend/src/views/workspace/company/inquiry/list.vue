<template>
  <div class="inquiry-list">
    <a-card :bordered="false">
      <!-- 搜索表单 -->
      <div class="table-page-search-wrapper">
        <div class="search-form">
          <div class="search-item">
            <span class="label">产品名称：</span>
            <a-input v-model:value="queryParams.productName" placeholder="请输入产品名称" allow-clear />
          </div>
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
              <a-select-option :value="0">未合作</a-select-option>
              <a-select-option :value="1">已合作</a-select-option>
              <a-select-option :value="2">不可合作</a-select-option>
            </a-select>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
          </div>
        </div>
      </div>

      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="intentionList"
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
          <a-button 
            type="primary"
            :disabled="record.status !== 0"
            @click="showCooperateConfirm(record)"
          >
            发起合作
          </a-button>
        </template>
      </a-table>
    </a-card>

    <!-- 确认弹框 -->
    <a-modal
      v-model:visible="cooperateModal.visible"
      title="发起合作"
      @ok="handleCooperate"
      @cancel="cooperateModal.visible = false"
      :confirmLoading="cooperateModal.loading"
    >
      <p>确定要与 {{ cooperateModal.record?.merchantName }} 发起合作吗？</p>
      <p style="margin-top: 8px; color: #666;">
        <strong>产品名称：</strong>{{ cooperateModal.record?.productName }}
      </p>
      <p style="color: #666;">
        <strong>联系人：</strong>{{ cooperateModal.record?.contactName }}
      </p>
      <p style="color: #666;">
        <strong>联系电话：</strong>{{ cooperateModal.record?.contactPhone }}
      </p>
      <p style="color: #666;">
        <strong>意向描述：</strong>{{ cooperateModal.record?.description }}
      </p>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getIntentionList, handleCooperation } from '@/api/intention'

// 查询参数
const queryParams = ref({
  productName: '',
  merchantName: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 100,
  showSizeChanger: true,
  showQuickJumper: true
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
    key: 'status',
    slots: { customRender: 'status' }
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
    width: 120,
    slots: { customRender: 'action' }
  }
]

const loading = ref(false)

// 意向列表数据
const intentionList = ref([])

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const { data } = await getIntentionList({
      pageNum: pagination.value.current,
      pageSize: pagination.value.pageSize,
      productName: queryParams.value.productName,
      merchantName: queryParams.value.merchantName,
      status: queryParams.value.status
    })
    intentionList.value = data.list
    pagination.value.total = data.total
  } catch (error) {
    console.error('获取意向列表失败:', error)
    message.error('获取意向列表失败')
  } finally {
    loading.value = false
  }
}

// 合作确认弹框状态
const cooperateModal = ref({
  visible: false,
  loading: false,
  record: null
})

// 显示确认弹框
const showCooperateConfirm = (record) => {
  cooperateModal.value.record = record
  cooperateModal.value.visible = true
}

// 处理发起合作
const handleCooperate = async () => {
  const record = cooperateModal.value.record
  if (!record) return

  cooperateModal.value.loading = true
  try {
    await handleCooperation(record.merchantId)
    message.success('已发起合作')
    fetchList()  // 重新加载列表
    cooperateModal.value.visible = false
  } catch (error) {
    message.error('发起合作失败')
  } finally {
    cooperateModal.value.loading = false
  }
}

// 状态相关
const getStatusColor = (status) => {
  const colors = {
    0: 'blue',
    1: 'success',
    2: 'default'
  }
  return colors[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    0: '未合作',
    1: '已合作',
    2: '不可合作'
  }
  return texts[status] || '未知'
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
    productName: '',
    merchantName: '',
    status: undefined,
    pageNum: 1,
    pageSize: 10
  }
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>

<style lang="less" scoped>
.inquiry-list {
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

.ant-modal {
  .ant-modal-body {
    p {
      margin-bottom: 8px;
      
      strong {
        display: inline-block;
        width: 80px;
      }
    }
  }
}
</style> 