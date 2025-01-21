<template>
  <div class="order-list">
    <!-- 搜索表单 -->
    <a-card class="search-card" :bordered="false">
      <a-form layout="inline">
        <a-form-item label="订单编号">
          <a-input v-model:value="queryParams.orderNo" placeholder="请输入订单编号" allowClear />
        </a-form-item>
        <a-form-item label="企业名称">
          <a-input v-model:value="queryParams.companyName" placeholder="请输入企业名称" allowClear />
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
            <a-button type="primary" @click="handleCreate">创建订单</a-button>
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
                    <a @click="handlePay(record)">付款</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 4">
                    <a @click="handleConfirmReceive(record)">确认收货</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 1">
                    <a @click="handleCancel(record)">取消订单</a>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { getOrderList, cancelOrder } from '@/api/merchant/order'
import dayjs from 'dayjs'

const router = useRouter()

// 订单状态映射
const orderStatusMap = {
  1: '待付款',
  2: '待发货',
  3: '配送中',
  4: '已送达',
  5: '已完成',
  6: '已取消'
}

// 状态颜色映射
const getStatusColor = (status) => {
  const colorMap = {
    1: 'warning',
    2: 'processing',
    3: 'processing',
    4: 'processing',
    5: 'success',
    6: 'success',
    7: 'default'
  }
  return colorMap[status] || 'default'
}

// 查询参数
const queryParams = reactive({
  orderNo: '',
  companyName: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 分页配置
const pagination = reactive({
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  current: 1,
  pageSize: 10,
  onChange: (page, pageSize) => {
    queryParams.pageNum = page
    queryParams.pageSize = pageSize
    fetchOrderList()
  }
})

// 表格列定义
const columns = [
  {
    title: '订单编号',
    dataIndex: 'orderNo',
    key: 'orderNo',
    width: 180
  },
  {
    title: '企业名称',
    dataIndex: 'companyName',
    key: 'companyName',
    width: 180
  },
  {
    title: '产品名称',
    dataIndex: 'productName',
    key: 'productName',
    width: 180
  },
  {
    title: '订单金额',
    dataIndex: 'totalAmount',
    key: 'totalAmount',
    width: 120,
    customRender: ({ text }) => `¥${text}`
  },
  {
    title: '订单状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    slots: { customRender: 'status' }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    customRender: ({ text }) => text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    slots: { customRender: 'action' },
    fixed: 'right'
  }
]

const loading = ref(false)
const orderList = ref([])

// 处理查询
const handleSearch = () => {
  queryParams.pageNum = 1
  fetchOrderList()
}

// 处理重置
const handleReset = () => {
  Object.assign(queryParams, {
    orderNo: '',
    companyName: '',
    status: undefined,
    pageNum: 1,
    pageSize: 10
  })
  handleSearch()
}

// 处理创建订单
const handleCreate = () => {
  router.push('/workspace/merchant/order/create')
}

// 处理查看详情
const handleViewDetail = (record) => {
  router.push({
    path: `/workspace/merchant/order/${record.id}`
  })
}

// 处理付款
const handlePay = (record) => {
  message.info('支付功能开发中')
}

// 处理确认收货
const handleConfirmReceive = async (record) => {
  message.info('确认收货功能开发中')
}

// 处理取消订单
const handleCancel = async (record) => {
  try {
    await cancelOrder(record.id)
    message.success('订单取消成功')
    fetchOrderList()
  } catch (error) {
    console.error('取消订单失败:', error)
    message.error(error.response?.data?.message || '取消订单失败')
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
    if (data && Array.isArray(data)) {
      // 如果data直接是数组
      orderList.value = data.map(item => ({
        ...item,
        key: item.id
      }))
      pagination.total = data.length
    } else if (data && Array.isArray(data.list)) {
      // 如果data是包含list的对象
      orderList.value = data.list.map(item => ({
        ...item,
        key: item.id
      }))
      pagination.total = data.total
    } else {
      orderList.value = []
      pagination.total = 0
    }
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