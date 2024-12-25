<template>
  <div class="product-edit">
    <div class="page-header">
      <a-page-header
        title="编辑产品"
        @back="() => router.back()"
      />
    </div>

    <a-spin :spinning="loading">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
      >
        <a-card title="基本信息" style="margin-bottom: 24px">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="产品名称" name="productName">
                <a-input v-model:value="formData.productName" placeholder="请输入产品名称" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="产品编号" name="productCode">
                <a-input v-model:value="formData.productCode" placeholder="请输入产品编号" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="产品价格" name="price">
                <a-input-number
                  v-model:value="formData.price"
                  placeholder="请输入产品价格"
                  :min="0"
                  :precision="2"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="计量单位" name="unit">
                <a-input v-model:value="formData.unit" placeholder="请输入计量单位" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="产品描述" name="description">
                <a-textarea
                  v-model:value="formData.description"
                  placeholder="请输入产品描述"
                  :rows="4"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-card>

        <div class="form-footer">
          <a-space>
            <a-button @click="() => router.back()">取消</a-button>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">保存</a-button>
          </a-space>
        </div>
      </a-form>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { getCompanyProducts, updateCompanyProduct } from '@/api/company'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)

const formData = ref({
  productName: '',
  productCode: '',
  price: undefined,
  unit: '',
  description: ''
})

const rules = {
  productName: [{ required: true, message: '请输入产品名称' }],
  productCode: [{ required: true, message: '请输入产品编号' }]
}

// 获取产品详情
const fetchProduct = async () => {
  loading.value = true
  try {
    const res = await getCompanyProducts({
      pageNum: 1,
      pageSize: 1,
      id: route.params.id
    })
    if (res.code === 200 && res.data?.length > 0) {
      const product = res.data[0]
      formData.value = {
        ...product,
        price: product.price ? Number(product.price) : undefined
      }
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    message.error('获取产品详情失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const res = await updateCompanyProduct({
      ...formData.value,
      id: route.params.id
    })
    
    if (res.code === 200) {
      message.success('保存成功')
      router.back()
    } else {
      message.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped lang="less">
.product-edit {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 24px;

  .page-header {
    margin: -24px -24px 24px;
    background: #fff;
  }

  .form-footer {
    margin-top: 24px;
    text-align: center;
  }
}
</style> 