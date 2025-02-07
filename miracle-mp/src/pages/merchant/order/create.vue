<template>
  <view class="create-order">
    <!-- 步骤条 -->
    <view class="steps">
      <view 
        v-for="(step, index) in steps" 
        :key="index"
        class="step-item"
        :class="{ active: currentStep >= index }"
      >
        <text class="step-num">{{ index + 1 }}</text>
        <text class="step-text">{{ step }}</text>
      </view>
    </view>

    <!-- 第一步：选择企业和产品 -->
    <view v-if="currentStep === 0" class="step-content">
      <view class="form-item">
        <text class="label">选择企业</text>
        <picker 
          @change="handleCompanyChange" 
          :range="companyList" 
          range-key="companyName"
          :value="companyIndex"
        >
          <view class="picker-value" :class="{ placeholder: !formData.companyId }">
            {{ formData.companyName || '请选择企业' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="label">选择产品</text>
        <picker 
          @change="handleProductChange" 
          :range="productList" 
          range-key="productName"
          :value="productIndex"
          :disabled="!formData.companyId"
        >
          <view class="picker-value" :class="{ placeholder: !formData.productId }">
            {{ formData.productName || '请选择产品' }}
          </view>
        </picker>
      </view>
    </view>

    <!-- 第二步：选择物料 -->
    <view v-if="currentStep === 1" class="step-content">
      <view class="material-list">
        <view 
          v-for="(item, index) in materialList" 
          :key="item.id"
          class="material-item"
        >
          <view class="left">
            <image 
              :src="item.image" 
              mode="aspectFill" 
              class="material-image"
            />
          </view>
          <view class="right">
            <view class="material-info">
              <text class="name">{{ item.name }}</text>
              <view class="detail-row">
                <text class="spec">规格：{{ item.specification }}</text>
                <text class="unit">单位：{{ item.unit }}</text>
              </view>
              <view class="price-row">
                <text class="price">￥{{ item.price }}</text>
                <text class="recommended">建议数量：{{ item.recommendedQuantity }}{{ item.unit }}</text>
              </view>
              <text class="desc" v-if="item.description">{{ item.description }}</text>
            </view>
            <view class="quantity-control">
              <text class="minus" @tap="handleQuantityChange(index, -1)">-</text>
              <input 
                type="number" 
                class="quantity" 
                v-model="item.quantity"
                @input="handleQuantityInput($event, index)"
              />
              <text class="plus" @tap="handleQuantityChange(index, 1)">+</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 如果物料列表为空 -->
      <view class="empty-tip" v-if="materialList.length === 0">
        暂无可选物料
      </view>

      <!-- 添加总金额显示 -->
      <view class="total-amount" v-if="materialList.length > 0">
        总金额：<text class="price">¥{{ totalAmount }}</text>
      </view>
    </view>

    <!-- 第三步：确认订单 -->
    <view v-if="currentStep === 2" class="step-content">
      <!-- 修改表单字段名称 -->
      <view class="form-item">
        <text class="label">收货人</text>
        <input 
          class="input"
          v-model="formData.receiverName"
          placeholder="请输入收货人姓名"
        />
      </view>
      <view class="form-item">
        <text class="label">联系电话</text>
        <input 
          class="input"
          v-model="formData.receiverPhone"
          type="number"
          maxlength="11"
          placeholder="请输入联系电话"
        />
      </view>
      <view class="form-item">
        <text class="label">收货地址</text>
        <input 
          class="input"
          v-model="formData.receiverAddress"
          placeholder="请输入收货地址"
        />
      </view>
      <view class="form-item">
        <text class="label">备注</text>
        <textarea 
          class="textarea"
          v-model="formData.remark"
          placeholder="请输入备注信息（选填）"
        />
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="footer">
      <button 
        v-if="currentStep > 0"
        class="prev-btn" 
        @tap="handlePrev"
      >
        上一步
      </button>
      <button 
        v-if="currentStep < 2"
        class="next-btn" 
        @tap="handleNext"
      >
        下一步
      </button>
      <button 
        v-else
        class="submit-btn" 
        @tap="handleSubmit"
        :loading="loading"
      >
        提交
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { 
  getCooperationCompanyList, 
  getCompanyProductList,
  getProductMaterialList,
  createOrder 
} from '@/api/merchant/order'

const loading = ref(false)
const currentStep = ref(0)
const steps = ['选择企业和产品', '选择物料', '确认订单']

const companyList = ref([])
const productList = ref([])
const materialList = ref([])

const companyIndex = ref(-1)
const productIndex = ref(-1)

const formData = ref({
  companyId: '',
  companyName: '',
  productId: '',
  productName: '',
  materials: [],
  remark: '',
  receiverAddress: '',
  receiverName: '',
  receiverPhone: ''
})

// 计算总金额
const totalAmount = computed(() => {
  return materialList.value.reduce((total, item) => {
    return total + (item.price * (item.quantity || 0))
  }, 0)
})

// 获取合作企业列表
const loadCompanyList = async () => {
  try {
    const res = await getCooperationCompanyList({
      status: 1
    })
    if(res.code === 200) {
      companyList.value = res.data || []
      // 如果有企业数据，默认选择第一个
      if (companyList.value.length > 0) {
        companyIndex.value = 0
        const company = companyList.value[0]
        formData.value.companyId = company.companyId
        formData.value.companyName = company.companyName
        // 加载第一个企业的产品列表
        loadProductList(company.companyId)
      }
    }
  } catch (error) {
    console.error('获取企业列表失败:', error)
    uni.showToast({
      title: '获取企业列表失败',
      icon: 'none'
    })
  }
}

// 获取产品列表
const loadProductList = async (companyId) => {
  try {
    const res = await getCompanyProductList(companyId, {})
    if(res.code === 200) {
      productList.value = res.data || [] // 直接使用 data，不需要 .list
      // 如果有产品数据，默认选择第一个
      if (productList.value.length > 0) {
        productIndex.value = 0
        const product = productList.value[0]
        formData.value.productId = product.id // 使用 id 而不是 productId
        formData.value.productName = product.productName
      }
    }
  } catch (error) {
    console.error('获取产品列表失败:', error)
    uni.showToast({
      title: '获取产品列表失败',
      icon: 'none'
    })
  }
}

// 选择企业
const handleCompanyChange = async (e) => {
  const index = e.detail.value
  companyIndex.value = index
  const company = companyList.value[index]
  
  formData.value.companyId = company.companyId
  formData.value.companyName = company.companyName
  formData.value.productId = ''
  formData.value.productName = ''
  productIndex.value = -1
  
  // 加载选中企业的产品列表
  await loadProductList(company.companyId)
}

// 选择产品
const handleProductChange = async (e) => {
  const index = e.detail.value
  productIndex.value = index
  const product = productList.value[index]
  
  formData.value.productId = product.id // 使用 id 而不是 productId
  formData.value.productName = product.productName
}

// 下一步
const handleNext = async () => {
  if(currentStep.value === 0) {
    if(!formData.value.companyId || !formData.value.productId) {
      uni.showToast({
        title: '请选择企业和产品',
        icon: 'none'
      })
      return
    }
    
    try {
      const res = await getProductMaterialList(formData.value.productId)
      if(res.code === 200) {
        materialList.value = (res.data || []).map(item => ({
          ...item,
          quantity: 0
        }))
      }
    } catch (error) {
      console.error('获取物料列表失败:', error)
      uni.showToast({
        title: '获取物料列表失败',
        icon: 'none'
      })
      return
    }
  } else if(currentStep.value === 1) {
    // 验证是否选择了物料
    const hasSelectedMaterial = materialList.value.some(item => item.quantity > 0)
    if(!hasSelectedMaterial) {
      uni.showToast({
        title: '请至少选择一个物料',
        icon: 'none'
      })
      return
    }
  }
  currentStep.value++
}

// 处理物料数量变化
const handleQuantityChange = (index, delta) => {
  const material = materialList.value[index]
  const newQuantity = Math.max(0, (material.quantity || 0) + delta)
  material.quantity = newQuantity
}

// 处理物料数量输入
const handleQuantityInput = (event, index) => {
  const value = parseInt(event.detail.value) || 0
  materialList.value[index].quantity = Math.max(0, value)
}

// 上一步
const handlePrev = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 提交订单
const handleSubmit = async () => {
  // 表单验证
  if (!formData.value.receiverName) {
    uni.showToast({
      title: '请输入收货人姓名',
      icon: 'none'
    })
    return
  }
  if (!formData.value.receiverPhone) {
    uni.showToast({
      title: '请输入联系电话',
      icon: 'none'
    })
    return
  }
  if (!formData.value.receiverAddress) {
    uni.showToast({
      title: '请输入收货地址',
      icon: 'none'
    })
    return
  }

  // 构造物料列表和计算总金额
  const materials = materialList.value
    .filter(item => item.quantity > 0)
    .map(item => ({
      materialId: item.id,
      quantity: item.quantity,
      price: item.price,
      amount: item.price * item.quantity
    }))

  if (materials.length === 0) {
    uni.showToast({
      title: '请选择物料',
      icon: 'none'
    })
    return
  }

  try {
    loading.value = true
    const orderData = {
      companyId: formData.value.companyId,
      productId: formData.value.productId,
      receiverName: formData.value.receiverName,
      receiverPhone: formData.value.receiverPhone,
      receiverAddress: formData.value.receiverAddress,
      remark: formData.value.remark,
      totalAmount: totalAmount.value,
      materials: materials
    }

    const res = await createOrder(orderData)
    if (res.code === 200) {
      uni.showToast({
        title: '创建订单成功',
        icon: 'success',
        duration: 1500
      })
      // 等待 Toast 显示完成后跳转到订单列表页
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/merchant/order/list'
        })
      }, 1500)
    } else {
      uni.showToast({
        title: res.errMessage || '创建订单失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    uni.showToast({
      title: '创建订单失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCompanyList()
})
</script>

<style lang="scss" scoped>
.create-order {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;

  .steps {
    display: flex;
    justify-content: space-between;
    padding: 40rpx;
    background: #fff;
    margin-bottom: 20rpx;

    .step-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;
      position: relative;

      &:not(:last-child)::after {
        content: '';
        position: absolute;
        top: 30rpx;
        right: -50%;
        width: 100%;
        height: 2rpx;
        background: #eee;
        z-index: 1;
      }

      &.active {
        .step-num {
          background: #1890ff;
          color: #fff;
        }

        &:not(:last-child)::after {
          background: #1890ff;
        }
      }

      .step-num {
        width: 60rpx;
        height: 60rpx;
        line-height: 60rpx;
        text-align: center;
        border-radius: 50%;
        background: #eee;
        color: #999;
        font-size: 28rpx;
        margin-bottom: 16rpx;
        position: relative;
        z-index: 2;
      }

      .step-text {
        font-size: 24rpx;
        color: #666;
      }
    }
  }

  .step-content {
    background: #fff;
    border-radius: 12rpx;
    padding: 30rpx;

    .form-item {
      margin-bottom: 30rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        display: block;
        font-size: 28rpx;
        color: #666;
        margin-bottom: 16rpx;
      }

      .input {
        width: 100%;
        height: 80rpx;
        background: #f8f8f8;
        border-radius: 8rpx;
        padding: 0 24rpx;
        box-sizing: border-box;
        font-size: 28rpx;
      }

      .textarea {
        width: 100%;
        height: 160rpx;
        background: #f8f8f8;
        border-radius: 8rpx;
        padding: 20rpx 24rpx;
        box-sizing: border-box;
        font-size: 28rpx;
      }
    }
  }

  .material-list {
    .material-item {
      display: flex;
      padding: 20rpx;
      border-bottom: 1rpx solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .left {
        width: 160rpx;
        margin-right: 20rpx;

        .material-image {
          width: 160rpx;
          height: 160rpx;
          border-radius: 8rpx;
        }
      }

      .right {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .material-info {
          .name {
            font-size: 28rpx;
            color: #333;
            font-weight: bold;
            margin-bottom: 12rpx;
            display: block;
          }

          .detail-row {
            display: flex;
            align-items: center;
            margin-bottom: 8rpx;
            font-size: 24rpx;
            color: #666;

            .spec, .unit {
              margin-right: 20rpx;
            }
          }

          .price-row {
            display: flex;
            align-items: center;
            margin-bottom: 8rpx;

            .price {
              color: #ff4d4f;
              font-weight: bold;
              font-size: 28rpx;
              margin-right: 20rpx;
            }

            .recommended {
              color: #1890ff;
              font-size: 24rpx;
            }
          }

          .desc {
            font-size: 24rpx;
            color: #999;
            display: block;
          }
        }

        .quantity-control {
          display: flex;
          align-items: center;
          justify-content: flex-end;
          margin-top: 12rpx;

          .minus,
          .plus {
            width: 48rpx;
            height: 48rpx;
            line-height: 48rpx;
            text-align: center;
            background: #1890ff;
            color: #fff;
            border-radius: 50%;
            font-size: 28rpx;
          }

          .quantity {
            width: 80rpx;
            height: 48rpx;
            line-height: 48rpx;
            text-align: center;
            margin: 0 16rpx;
            background: #f5f5f5;
            border-radius: 4rpx;
          }
        }
      }
    }
  }

  .empty-tip {
    text-align: center;
    padding: 60rpx 0;
    color: #999;
    font-size: 28rpx;
  }

  .footer {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    padding: 20rpx;
    background: #fff;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);

    button {
      flex: 1;
      height: 80rpx;
      line-height: 80rpx;
      margin: 0;
      border-radius: 8rpx;
      font-size: 28rpx;

      &.prev-btn {
        margin-right: 20rpx;
        background: #fff;
        color: #666;
        border: 1rpx solid #ddd;
      }

      &.next-btn,
      &.submit-btn {
        background: #1890ff;
        color: #fff;
      }

      &[loading] {
        opacity: 0.7;
      }
    }
  }
}

.picker-value {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: #f8f8f8;
  border-radius: 8rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #333;

  &.placeholder {
    color: #999;
  }
}

.total-amount {
  margin-top: 30rpx;
  padding: 20rpx;
  text-align: right;
  font-size: 28rpx;
  color: #333;
  border-top: 1rpx solid #eee;

  .price {
    font-size: 32rpx;
    font-weight: bold;
    color: #ff4d4f;
  }
}
</style> 