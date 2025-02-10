<template>
  <view class="order-detail">
    <!-- 订单基本信息 -->
    <view class="info-section">
      <view class="info-title">订单基本信息</view>
      <view class="info-content">
        <view class="info-item">
          <text class="label">订单编号：</text>
          <text class="value">{{ orderInfo.orderNo }}</text>
        </view>
        <view class="info-item">
          <text class="label">订单状态：</text>
          <text class="value status">{{ getStatusText(orderInfo.status) }}</text>
        </view>
        <view class="info-item">
          <text class="label">订单金额：</text>
          <text class="value price">¥{{ orderInfo.totalAmount }}</text>
        </view>
        <view class="info-item">
          <text class="label">创建时间：</text>
          <text class="value">{{ formatTime(orderInfo.createTime) }}</text>
        </view>
      </view>
    </view>

    <!-- 产品信息 -->
    <view class="info-section">
      <view class="info-title">产品信息</view>
      <view class="product-info">
        <image 
          :src="orderInfo.productUrl || defaultImage" 
          mode="aspectFill" 
          class="product-image"
          @tap="previewImage(orderInfo.productUrl)"
        />
        <view class="info">
          <text class="name">{{ orderInfo.productName }}</text>
          <text class="company">{{ orderInfo.companyName }}</text>
        </view>
      </view>
    </view>

    <!-- 收货信息 -->
    <view class="info-section">
      <view class="info-title">收货信息</view>
      <view class="info-content">
        <view class="info-item">
          <text class="label">收货人：</text>
          <text class="value">{{ orderInfo.receiverName }}</text>
        </view>
        <view class="info-item">
          <text class="label">联系电话：</text>
          <text class="value">{{ orderInfo.receiverPhone }}</text>
        </view>
        <view class="info-item">
          <text class="label">收货地址：</text>
          <text class="value">{{ orderInfo.receiverAddress }}</text>
        </view>
        <view class="info-item">
          <text class="label">备注：</text>
          <text class="value">{{ orderInfo.remark || '无' }}</text>
        </view>
      </view>
    </view>

    <!-- 物料明细 -->
    <view class="material-section">
      <view class="info-title">物料明细</view>
      <view class="material-table">
        <view class="table-header">
          <text class="col">物料图片</text>
          <text class="col">物料名称</text>
          <text class="col">规格</text>
          <text class="col">单价</text>
          <text class="col">数量</text>
          <text class="col">金额</text>
        </view>
        <view 
          class="table-row" 
          v-for="item in orderInfo.orderMaterials" 
          :key="item.id"
        >
          <image 
            :src="item.image || defaultImage" 
            mode="aspectFill" 
            class="material-image col"
            @tap="previewImage(item.image)"
          />
          <text class="col">{{ item.materialName }}</text>
          <text class="col">{{ item.specification }}</text>
          <text class="col">¥{{ item.price }}</text>
          <text class="col">{{ item.quantity }}{{ item.unit }}</text>
          <text class="col">¥{{ item.amount }}</text>
        </view>
      </view>
    </view>

    <!-- 物流信息 -->
    <view class="info-section" v-if="orderInfo.status >= 4">
      <view class="info-title">物流信息</view>
      <view class="info-content">
        <view class="info-item">
          <text class="label">物流公司：</text>
          <text class="value">{{ orderInfo.logisticsCompany || '暂无' }}</text>
        </view>
        <view class="info-item">
          <text class="label">物流单号：</text>
          <text class="value">{{ orderInfo.logisticsNo || '暂无' }}</text>
        </view>
        <view class="info-item">
          <text class="label">发货时间：</text>
          <text class="value">{{ formatTime(orderInfo.logisticsTime) || '暂无' }}</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="footer-btns" v-if="orderInfo.status === 0">
      <button class="btn cancel" @tap="handleCancel">取消订单</button>
      <button class="btn pay" @tap="handlePay">立即支付</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderDetail, cancelOrder, payOrder } from '../../../api/merchant'
import { formatTime } from '../../../utils/date'

const defaultImage = '/static/images/default-product.png'

// 订单信息
const orderInfo = ref({})

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '待审批',
    2: '待发货',
    3: '审批拒绝',
    4: '发货中',
    5: '已完成',
    6: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取支付方式文本
const getPaymentText = (type) => {
  const paymentMap = {
    1: '微信支付',
    2: '支付宝'
  }
  return paymentMap[type] || '未支付'
}

// 获取订单详情
const fetchOrderDetail = async () => {
  const orderId = getOrderIdFromRoute()
  try {
    const res = await getOrderDetail(orderId)
    if (res.code === 200) {
      orderInfo.value = res.data
    } else {
      uni.showToast({
        title: res.message || '获取订单详情失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    uni.showToast({
      title: '获取订单详情失败',
      icon: 'none'
    })
  }
}

// 从路由获取订单ID
const getOrderIdFromRoute = () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  return currentPage.options.id
}

// 取消订单
const handleCancel = () => {
  uni.showModal({
    title: '提示',
    content: '确定要取消该订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const res = await cancelOrder(orderInfo.value.id)
          if (res.code === 200) {
            uni.showToast({
              title: '取消成功',
              icon: 'success'
            })
            fetchOrderDetail()
          } else {
            uni.showToast({
              title: res.message || '取消失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('取消订单失败:', error)
          uni.showToast({
            title: '取消失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

// 支付订单
const handlePay = async () => {
  try {
    const res = await payOrder(orderInfo.value.id)
    if (res.code === 200) {
      // 处理支付逻辑
      const payParams = res.data
      // 调用支付接口
      uni.requestPayment({
        ...payParams,
        success: () => {
          uni.showToast({
            title: '支付成功',
            icon: 'success'
          })
          fetchOrderDetail()
        },
        fail: (err) => {
          console.error('支付失败:', err)
          uni.showToast({
            title: '支付失败',
            icon: 'none'
          })
        }
      })
    } else {
      uni.showToast({
        title: res.message || '支付失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('支付订单失败:', error)
    uni.showToast({
      title: '支付失败',
      icon: 'none'
    })
  }
}

// 添加图片预览方法
const previewImage = (url) => {
  if (!url) return
  uni.previewImage({
    urls: [url],
    current: url,
    fail: (err) => {
      console.error('图片预览失败:', err)
      uni.showToast({
        title: '图片预览失败',
        icon: 'none'
      })
    }
  })
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style lang="scss" scoped>
.order-detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.info-section {
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;

  .info-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .info-content {
    .info-item {
      display: flex;
      margin-bottom: 16rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        width: 160rpx;
        font-size: 28rpx;
        color: #666;
      }

      .value {
        flex: 1;
        font-size: 28rpx;
        color: #333;

        &.status {
          color: #1890ff;
        }

        &.price {
          color: #ff4d4f;
          font-weight: bold;
        }
      }
    }
  }
}

.material-section {
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx;

  .material-table {
    .table-header {
      display: flex;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #eee;
      font-size: 28rpx;
      color: #666;
    }

    .table-row {
      display: flex;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #eee;
      font-size: 28rpx;
      color: #333;

      &:last-child {
        border-bottom: none;
      }
    }

    .col {
      flex: 1;
      text-align: center;

      &.material-image {
        width: 80rpx;
        height: 80rpx;
        border-radius: 8rpx;
      }
    }
  }
}

.footer-btns {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx;
  background: #fff;
  display: flex;
  justify-content: flex-end;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.1);

  .btn {
    width: 180rpx;
    height: 72rpx;
    line-height: 72rpx;
    text-align: center;
    border-radius: 36rpx;
    font-size: 28rpx;
    margin-left: 20rpx;

    &.cancel {
      background: #f5f5f5;
      color: #666;
    }

    &.pay {
      background: #1890ff;
      color: #fff;
    }

    &:active {
      opacity: 0.8;
    }
  }
}

.material-table {
  .col {
    &:nth-child(1) { width: 120rpx; flex: none; } // 图片列
    &:nth-child(2) { flex: 2; } // 名称列
    &:nth-child(3) { flex: 1; } // 规格列
    &:nth-child(4) { flex: 1; } // 单价列
    &:nth-child(5) { flex: 1; } // 数量列
    &:nth-child(6) { flex: 1; } // 金额列
  }
}

.product-info {
  display: flex;
  padding: 20rpx 0;

  .product-image {
    width: 160rpx;
    height: 160rpx;
    border-radius: 8rpx;
    margin-right: 20rpx;
  }

  .info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .name {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .company {
      font-size: 28rpx;
      color: #666;
    }
  }
}

.material-table {
  .material-image {
    &:active {
      opacity: 0.8;
    }
  }
}
</style> 