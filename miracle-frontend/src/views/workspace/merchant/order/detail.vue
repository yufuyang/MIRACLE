<template>
  <div class="order-detail">
    <!-- 操作按钮 -->
    <div class="action-bar">
      <div class="action-bar-container">
        <div class="left">
          <a-button @click="handleBack">返回</a-button>
        </div>
        <div class="right">
          <a-space>
            <a-button v-if="orderDetail.status === 1" type="primary" @click="handlePay">付款</a-button>
            <a-button v-if="orderDetail.status === 4" type="primary" @click="handleConfirmReceive">确认收货</a-button>
            <a-button v-if="orderDetail.status === 1" type="primary" danger @click="handleCancel">取消订单</a-button>
          </a-space>
        </div>
      </div>
    </div>

    <!-- 基本信息卡片 -->
    <a-card title="订单基本信息" class="info-card">
      <a-descriptions :column="3">
        <a-descriptions-item label="订单编号">{{ orderDetail.orderNo }}</a-descriptions-item>
        <a-descriptions-item label="订单状态">
          <a-tag :color="getStatusColor(orderDetail.status)">
            {{ orderStatusMap[orderDetail.status] }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="订单金额">¥{{ orderDetail.totalAmount }}</a-descriptions-item>
        <a-descriptions-item label="企业名称">{{ orderDetail.companyName }}</a-descriptions-item>
        <a-descriptions-item label="产品名称">{{ orderDetail.productName }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ formatDateTime(orderDetail.createTime) }}</a-descriptions-item>
        <a-descriptions-item label="审批时间">{{ formatDateTime(orderDetail.approveTime) }}</a-descriptions-item>
        <a-descriptions-item label="收货人">{{ orderDetail.receiverName }}</a-descriptions-item>
        <a-descriptions-item label="联系电话">{{ orderDetail.receiverPhone }}</a-descriptions-item>
        <a-descriptions-item label="收货地址" :span="3">{{ orderDetail.receiverAddress }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="3">{{ orderDetail.remark || '-' }}</a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 物料列表卡片 -->
    <a-card title="物料明细" class="material-card">
      <a-table
        :columns="materialColumns"
        :data-source="orderDetail.orderMaterials || []"
        :pagination="false"
      >
        <template #materialImage="{ text }">
          <a-image
            :src="text || defaultImage"
            alt="物料图片"
            :style="{
              width: '80px',
              height: '80px',
              objectFit: 'cover',
              borderRadius: '4px'
            }"
          />
        </template>
      </a-table>
    </a-card>

    <!-- 间隔 -->
    <div class="card-spacer"></div>

    <!-- 物流信息卡片 -->
    <a-card v-if="orderDetail.status >= 4" title="物流信息" class="logistics-card">
      <div class="logistics-info">
        <div class="info-item">
          <span class="label">发货时间：</span>
          <span class="value">{{ formatDateTime(orderDetail.logisticsTime) }}</span>
        </div>
        <div class="info-item">
          <span class="label">物流公司：</span>
          <span class="value">{{ orderDetail.logisticsCompany }}</span>
        </div>
        <div class="info-item">
          <span class="label">物流单号：</span>
          <span class="value">{{ orderDetail.logisticsNo }}</span>
        </div>
        <div class="info-item" v-if="orderDetail.status === 5">
          <span class="label">完成时间：</span>
          <span class="value">{{ formatDateTime(orderDetail.finishedTime) }}</span>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getOrderDetail, cancelOrder } from '@/api/merchant/order'
import defaultImage from '@/assets/images/default-image.jpg'

const route = useRoute()
const router = useRouter()
const orderDetail = ref({})
const loading = ref(false)

// 订单状态映射
const orderStatusMap = {
  1: '待审批',
  2: '待发货',
  3: '审批拒绝',
  4: '发货中',
  5: '已完成',
  6: '已取消'
}

// 物料列表列定义
const materialColumns = [
  {
    title: '物料图片',
    dataIndex: 'image',
    key: 'image',
    width: 120,
    slots: { customRender: 'materialImage' }
  },
  {
    title: '物料名称',
    dataIndex: 'materialName',
    key: 'name'
  },
  {
    title: '规格',
    dataIndex: 'specification',
    key: 'specification',
    customRender: ({ text }) => text || '-'
  },
  {
    title: '单价',
    dataIndex: 'price',
    key: 'price',
    customRender: ({ text }) => `¥${text}`
  },
  {
    title: '数量',
    dataIndex: 'quantity',
    key: 'quantity'
  },
  {
    title: '金额',
    dataIndex: 'amount',
    key: 'amount',
    customRender: ({ text }) => `¥${text}`
  }
]

// 获取状态颜色
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

// 格式化日期时间
const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 获取订单详情
const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const { data } = await getOrderDetail(route.params.id)
    orderDetail.value = data
  } catch (error) {
    console.error('获取订单详情失败:', error)
    message.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

// 返回列表
const handleBack = () => {
  router.back()
}

// 付款
const handlePay = () => {
  message.info('支付功能开发中')
}

// 确认收货
const handleConfirmReceive = () => {
  message.info('确认收货功能开发中')
}

// 取消订单
const handleCancel = async () => {
  try {
    await cancelOrder(orderDetail.value.id)
    message.success('订单取消成功')
    fetchOrderDetail()
  } catch (error) {
    console.error('取消订单失败:', error)
    message.error('取消订单失败')
  }
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped lang="less">
.order-detail {
  padding: 24px;

  .action-bar {
    margin-bottom: 24px;
    background: #fff;
    padding: 16px 24px;
    border-radius: 2px;

    .action-bar-container {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .info-card {
    margin-bottom: 24px;
  }

  .card-spacer {
    height: 24px;
  }

  .material-card {
    margin-bottom: 0;
    :deep(.ant-table-wrapper) {
      .ant-table-thead > tr > th {
        background-color: #fafafa;
      }
    }
  }

  .logistics-card {
    margin-bottom: 24px;
    
    .logistics-info {
      display: flex;
      flex-wrap: wrap;
      padding: 16px;
      
      .info-item {
        flex: 0 0 33.33%;
        margin-bottom: 16px;
        
        .label {
          color: #8c8c8c;
          margin-right: 8px;
        }
        
        .value {
          color: #262626;
          font-weight: 500;
        }
      }
    }
  }
}
</style> 