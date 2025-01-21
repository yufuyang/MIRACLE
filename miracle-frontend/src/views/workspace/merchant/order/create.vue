<template>
  <div class="create-order">
    <a-card>
      <a-steps :current="currentStep">
        <a-step title="选择企业" />
        <a-step title="选择产品" />
        <a-step title="选择物料" />
        <a-step title="确认订单" />
      </a-steps>

      <!-- 步骤内容 -->
      <div class="steps-content">
        <!-- 步骤1：选择企业 -->
        <div v-if="currentStep === 0">
          <a-form layout="inline" style="margin-bottom: 24px">
            <a-form-item label="企业名称">
              <a-input v-model:value="queryParams.companyName" placeholder="请输入企业名称" allowClear />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handleSearch">查询</a-button>
            </a-form-item>
          </a-form>

          <a-table
            :columns="companyColumns"
            :data-source="companyList"
            :loading="loading"
            :pagination="false"
            :row-selection="{ type: 'radio', selectedRowKeys: selectedCompanyKeys, onChange: onCompanySelect }"
          >
            <template #status="{ text }">
              <a-tag :color="text === 1 ? 'success' : 'default'">
                {{ text === 1 ? '已合作' : '未合作' }}
              </a-tag>
            </template>
          </a-table>
        </div>

        <!-- 步骤2：选择产品 -->
        <div v-if="currentStep === 1">
          <a-form layout="inline" style="margin-bottom: 24px">
            <a-form-item label="产品名称">
              <a-input v-model:value="queryParams.productName" placeholder="请输入产品名称" allowClear />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handleSearch">查询</a-button>
            </a-form-item>
          </a-form>

          <a-table
            :columns="productColumns"
            :data-source="productList"
            :loading="loading"
            :pagination="false"
            :row-selection="{ type: 'radio', selectedRowKeys: selectedProductKeys, onChange: onProductSelect }"
          />
        </div>

        <!-- 步骤3：选择物料 -->
        <div v-if="currentStep === 2">
          <a-table
            :columns="materialColumns"
            :data-source="materialList"
            :loading="loading"
            :pagination="false"
          >
            <template #quantity="{ record }">
              <a-input-number
                v-model:value="record.quantity"
                :min="0"
                :max="record.stock"
                @change="(value) => handleQuantityChange(record, value)"
              />
            </template>
            <template #amount="{ record }">
              ¥{{ calculateAmount(record) }}
            </template>
          </a-table>

          <div class="total-amount">
            总金额：¥{{ totalAmount }}
          </div>
        </div>

        <!-- 步骤4：确认订单 -->
        <div v-if="currentStep === 3">
          <a-form
            ref="orderFormRef"
            :model="orderForm"
            :rules="rules"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 16 }"
          >
            <a-form-item label="收货人" name="receiverName">
              <a-input v-model:value="orderForm.receiverName" placeholder="请输入收货人姓名" />
            </a-form-item>
            <a-form-item label="联系电话" name="receiverPhone">
              <a-input v-model:value="orderForm.receiverPhone" placeholder="请输入联系电话" />
            </a-form-item>
            <a-form-item label="收货地址" name="receiverAddress">
              <a-textarea v-model:value="orderForm.receiverAddress" :rows="3" placeholder="请输入详细地址" />
            </a-form-item>
            <a-form-item label="备注" name="remark">
              <a-textarea v-model:value="orderForm.remark" :rows="3" placeholder="请输入备注信息" />
            </a-form-item>
          </a-form>

          <!-- 订单预览 -->
          <a-divider />
          <h3>订单预览</h3>
          <a-descriptions :column="2">
            <a-descriptions-item label="企业名称">{{ selectedCompany?.companyName }}</a-descriptions-item>
            <a-descriptions-item label="产品名称">{{ selectedProduct?.productName }}</a-descriptions-item>
            <a-descriptions-item label="订单金额">¥{{ totalAmount }}</a-descriptions-item>
          </a-descriptions>
        </div>
      </div>

      <!-- 步骤操作按钮 -->
      <div class="steps-action">
        <a-space>
          <a-button v-if="currentStep > 0" @click="prevStep">上一步</a-button>
          <a-button
            v-if="currentStep < 3"
            type="primary"
            :disabled="!canNext"
            @click="nextStep"
          >
            下一步
          </a-button>
          <a-button
            v-if="currentStep === 3"
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            提交订单
          </a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  createOrder,
  getCooperationCompanyList,
  getCompanyProductList,
  getProductMaterialList
} from '@/api/merchant/order'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const currentStep = ref(0)

// 查询参数
const queryParams = reactive({
  companyName: '',
  productName: ''
})

// 选中的数据
const selectedCompanyKeys = ref([])
const selectedProductKeys = ref([])
const selectedCompany = ref(null)
const selectedProduct = ref(null)

// 列表数据
const companyList = ref([])
const productList = ref([])
const materialList = ref([])

// 订单表单
const orderFormRef = ref()
const orderForm = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: ''
})

// 表单验证规则
const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名' }],
  receiverPhone: [{ required: true, message: '请输入联系电话' }],
  receiverAddress: [{ required: true, message: '请输入收货地址' }]
}

// 列定义
const companyColumns = [
  {
    title: '企业名称',
    dataIndex: 'companyName',
    key: 'companyName'
  },
  {
    title: '合作状态',
    dataIndex: 'status',
    key: 'status',
    slots: { customRender: 'status' }
  }
]

const productColumns = [
  {
    title: '产品名称',
    dataIndex: 'productName',
    key: 'productName'
  },
  {
    title: '产品编号',
    dataIndex: 'productCode',
    key: 'productCode'
  }
]

const materialColumns = [
  {
    title: '物料名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '规格',
    dataIndex: 'specification',
    key: 'specification'
  },
  {
    title: '单价',
    dataIndex: 'price',
    key: 'price'
  },
  {
    title: '库存',
    dataIndex: 'stock',
    key: 'stock'
  },
  {
    title: '数量',
    dataIndex: 'quantity',
    key: 'quantity',
    slots: { customRender: 'quantity' }
  },
  {
    title: '金额',
    dataIndex: 'amount',
    key: 'amount',
    slots: { customRender: 'amount' }
  }
]

// 计算属性
const canNext = computed(() => {
  switch (currentStep.value) {
    case 0:
      return selectedCompanyKeys.value.length > 0
    case 1:
      return selectedProductKeys.value.length > 0
    case 2:
      return materialList.value.some(item => item.quantity > 0)
    default:
      return true
  }
})

const totalAmount = computed(() => {
  return materialList.value.reduce((sum, item) => {
    return sum + calculateAmount(item)
  }, 0)
})

// 方法
const calculateAmount = (material) => {
  return (material.price || 0) * (material.quantity || 0)
}

const handleSearch = () => {
  loadStepData()
}

const onCompanySelect = (selectedRowKeys, selectedRows) => {
  selectedCompanyKeys.value = [selectedRowKeys[0]]
  selectedCompany.value = selectedRows[0]
}

const onProductSelect = (selectedRowKeys, selectedRows) => {
  selectedProductKeys.value = [selectedRowKeys[0]]
  selectedProduct.value = selectedRows[0]
}

const handleQuantityChange = (record, value) => {
  record.quantity = value
}

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
    loadStepData()
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const handleSubmit = async () => {
  try {
    await orderFormRef.value.validate()
    submitting.value = true
    
    const orderData = {
      companyId: selectedCompany.value.companyId,
      productId: selectedProduct.value.id,
      materials: materialList.value
        .filter(item => item.quantity > 0)
        .map(item => ({
          materialId: item.id,
          quantity: item.quantity,
          price: item.price,
          amount: item.quantity * item.price
        })),
      receiverName: orderForm.receiverName,
      receiverPhone: orderForm.receiverPhone,
      receiverAddress: orderForm.receiverAddress,
      remark: orderForm.remark || '',
      totalAmount: totalAmount.value
    }
    
    await createOrder(orderData)
    
    message.success('订单创建成功')

    router.push('/workspace/merchant/order/list')

  } catch (error) {
    console.error('创建订单失败:', error)
    message.error(error.response?.data?.message || '创建订单失败')
  } finally {
    submitting.value = false
  }
}

const loadStepData = async () => {
  loading.value = true
  try {
    switch (currentStep.value) {
      case 0:
        const { data } = await getCooperationCompanyList({
          companyName: queryParams.companyName,
          status:1
        })
        companyList.value = data.map(item => ({
          ...item,
          key: item.companyId.toString()
        }))
        break
      case 1:
        if (!selectedCompany.value) break
        const { data: products } = await getCompanyProductList({
          companyId: selectedCompany.value.companyId,
          productName: queryParams.productName
        })
        productList.value = products.map(item => ({
          ...item,
          key: item.id.toString()
        }))
        break
      case 2:
        if (!selectedProduct.value) break
        const { data: materials } = await getProductMaterialList({
          productId:selectedProduct.value.id
        })
        materialList.value = materials.map(item => ({
          ...item,
          quantity: 0
        }))
        break
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化
loadStepData()
</script>

<style scoped lang="less">
.create-order {
  padding: 24px;

  .steps-content {
    min-height: 400px;
    margin-top: 24px;
    padding: 24px;
    background-color: #fafafa;
    border: 1px dashed #e9e9e9;
    border-radius: 2px;
  }

  .steps-action {
    margin-top: 24px;
  }

  .total-amount {
    margin-top: 16px;
    text-align: right;
    font-size: 16px;
    font-weight: 500;
  }
}
</style> 