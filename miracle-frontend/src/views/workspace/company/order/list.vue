<template>
  <div class="order-list">
    <!-- 搜索表单 -->
    <a-card class="search-card" :bordered="false">
      <a-form layout="inline">
        <a-form-item label="订单编号">
          <a-input v-model:value="queryParams.orderNo" placeholder="请输入订单编号" allowClear />
        </a-form-item>
        <a-form-item label="商户名称">
          <a-input v-model:value="queryParams.merchantName" placeholder="请输入商户名称" allowClear />
        </a-form-item>
        <a-form-item label="订单状态">
          <a-select v-model:value="queryParams.status" placeholder="请选择状态" allowClear style="width: 120px">
            <a-select-option v-for="(text, value) in orderStatusMap" :key="value" :value="Number(value)">
              {{ text }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 订单列表 -->
    <a-card class="list-card">
      <a-table
        :columns="columns"
        :data-source="orderList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <!-- 订单状态 -->
        <template #status="{ text }">
          <a-tag :color="getStatusColor(text)">{{ orderStatusMap[text] }}</a-tag>
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <a-space>
            <a @click="handleViewDetail(record)">查看</a>
            <a-divider type="vertical" />
            <a-dropdown>
              <a class="ant-dropdown-link" @click.prevent>
                更多 <down-outlined />
              </a>
              <template #overlay>
                <a-menu>
                  <a-menu-item v-if="record.status === 1">
                    <a @click="handleApprove(record)">审批通过</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 1">
                    <a @click="handleReject(record)">审批拒绝</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 2">
                    <a @click="handleDeliver(record)">发货</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 4">
                    <a @click="handleComplete(record)">完成订单</a>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </a-table>
    </a-card>

    <!-- 发货弹窗 -->
    <a-modal
      v-model:visible="deliverModal.visible"
      title="订单发货"
      @ok="handleDeliverSubmit"
      :confirmLoading="deliverModal.loading"
    >
      <a-form :model="deliverForm" :rules="deliverRules" ref="deliverFormRef">
        <a-form-item label="物流公司" name="logisticsCompany">
          <a-input v-model:value="deliverForm.logisticsCompany" placeholder="请输入物流公司" />
        </a-form-item>
        <a-form-item label="物流单号" name="logisticsNo">
          <a-input v-model:value="deliverForm.logisticsNo" placeholder="请输入物流单号" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { getOrderList, deliverOrder, cancelOrder, approveOrder, rejectOrder, completeOrder } from '@/api/company/order'
import dayjs from 'dayjs'

const router = useRouter()

// 订单状态映射
const orderStatusMap = {
  1: '待审批',
  2: '待发货',
  3: '审批拒绝',
  4: '发货中',
  5: '已完成',
  6: '已取消'
}

// 状态颜色映射
const getStatusColor = (status) => {
  const colorMap = {
    1: 'warning',     // 待审批 - 黄色
    2: 'processing',  // 待发货 - 蓝色
    3: 'error',      // 审批拒绝 - 红色
    4: 'processing', // 发货中 - 蓝色
    5: 'success',    // 已完成 - 绿色
    6: 'default'     // 已取消 - 灰色
  }
  return colorMap[status] || 'default'
}

// 查询参数
const queryParams = reactive({
  orderNo: '',
  merchantName: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 分页配置
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
  showSizeChanger: true,
  showQuickJumper: true
})

// 表格列定义
const columns = [
  {
    title: '订单编号',
    dataIndex: 'orderNo',
    width: 200
  },
  {
    title: '商户名称',
    dataIndex: 'merchantName',
    width: 200
  },
  {
    title: '订单金额',
    dataIndex: 'totalAmount',
    width: 120,
    customRender: ({ text }) => `¥${text}`
  },
  {
    title: '订单状态',
    dataIndex: 'status',
    width: 120,
    slots: { customRender: 'status' }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    customRender: ({ text }) => dayjs(text).format('YYYY-MM-DD HH:mm:ss')
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 150,
    fixed: 'right'
  }
]

const loading = ref(false)
const orderList = ref([])

// 发货弹窗相关
const deliverModal = reactive({
  visible: false,
  loading: false,
  record: null
})

const deliverForm = reactive({
  logisticsCompany: '',
  logisticsNo: ''
})

const deliverFormRef = ref()
const deliverRules = {
  logisticsCompany: [{ required: true, message: '请输入物流公司' }],
  logisticsNo: [{ required: true, message: '请输入物流单号' }]
}

// 处理查询
const handleSearch = () => {
  queryParams.pageNum = 1
  fetchOrderList()
}

// 处理重置
const handleReset = () => {
  Object.assign(queryParams, {
    orderNo: '',
    merchantName: '',
    status: undefined,
    pageNum: 1,
    pageSize: 10
  })
  handleSearch()
}

// 处理查看详情
const handleViewDetail = (record) => {
  router.push(`/workspace/order/detail/${record.id}`)
}

// 处理发货
const handleDeliver = (record) => {
  deliverModal.record = record
  deliverModal.visible = true
}

// 提交发货
const handleDeliverSubmit = async () => {
  try {
    await deliverFormRef.value.validate()
    deliverModal.loading = true
    
    await deliverOrder({
      orderId: deliverModal.record.id,
      ...deliverForm
    })
    
    message.success('发货成功')
    deliverModal.visible = false
    fetchOrderList()
    
  } catch (error) {
    console.error('发货失败:', error)
    message.error('发货失败')
  } finally {
    deliverModal.loading = false
  }
}

// 审批通过
const handleApprove = async (record) => {
  try {
    await approveOrder(record.id)
    message.success('审批通过')
    fetchOrderList()
  } catch (error) {
    console.error('审批失败:', error)
    message.error('审批失败')
  }
}

// 审批拒绝
const handleReject = async (record) => {
  try {
    await rejectOrder(record.id)
    message.success('已拒绝订单')
    fetchOrderList()
  } catch (error) {
    console.error('操作失败:', error)
    message.error('操作失败')
  }
}

// 完成订单
const handleComplete = async (record) => {
  try {
    await completeOrder(record.id)
    message.success('订单已完成')
    fetchOrderList()
  } catch (error) {
    console.error('操作失败:', error)
    message.error('操作失败')
  }
}

// 处理表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchOrderList()
}

// 获取订单列表
const fetchOrderList = async () => {
  loading.value = true
  try {
    const { data } = await getOrderList({
      ...queryParams,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    })
    
    orderList.value = data.map(item => ({
      ...item,
      key: item.id
    }))
    pagination.total = data.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
    message.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 初始化
fetchOrderList()
</script>

<style scoped lang="less">
.order-list {
  padding: 24px;
  
  .search-card {
    margin-bottom: 24px;
  }

  .list-card {
    :deep(.ant-card-body) {
      padding: 0;
    }
  }
}
</style> 