<template>
  <div class="company-product-list">
    <div class="page-header">
      <h2>产品列表</h2>
      <a-button type="primary" @click="handleAdd">
        <template #icon>
          <plus-outlined />
        </template>
        添加产品
      </a-button>
    </div>

    <a-card>
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="产品名称" name="name">
          <a-input v-model:value="searchForm.name" placeholder="请输入产品名称" allow-clear />
        </a-form-item>
        <a-form-item label="产品分类" name="categoryId">
          <a-select
            v-model:value="searchForm.categoryId"
            placeholder="请选择产品分类"
            style="width: 200px"
            allow-clear
          >
            <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">
            <template #icon>
              <search-outlined />
            </template>
            搜索
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">
            <template #icon>
              <redo-outlined />
            </template>
            重置
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 产品列表 -->
      <a-table
        :columns="columns"
        :data-source="products"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        style="margin-top: 16px"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <a-image
              :width="80"
              :src="record.image"
              :fallback="defaultImage"
              style="object-fit: cover"
            />
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'default'">
              {{ record.status === 1 ? '已上架' : '已下架' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm
                :title="record.status === 1 ? '确定下架该产品？' : '确定上架该产品？'"
                @confirm="handleToggleStatus(record)"
              >
                <a>{{ record.status === 1 ? '下架' : '上架' }}</a>
              </a-popconfirm>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除该产品？" @confirm="handleDelete(record)">
                <a class="danger">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 添加/编辑产品对话框 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
    >
      <a-form
        ref="formRef"
        :model="formData.value"
        :rules="formRules"
        :label-col="{span: 6}"
        :wrapper-col="{span: 16}"
      >
        <a-form-item label="产品名称" name="name">
          <a-input v-model:value="formData.value.name" placeholder="请输入产品名称" />
        </a-form-item>
        <a-form-item label="产品分类" name="categoryId">
          <a-select
            v-model:value="formData.value.categoryId"
            placeholder="请选择产品分类"
            style="width: 100%"
          >
            <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="产品图片" name="image">
          <a-upload
            v-model:file-list="fileList"
            :before-upload="beforeUpload"
            :custom-request="customUpload"
            list-type="picture-card"
            :max-count="1"
          >
            <div v-if="fileList.length < 1">
              <plus-outlined />
              <div style="margin-top: 8px">上传</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="产品描述" name="description">
          <a-textarea
            v-model:value="formData.value.description"
            placeholder="请输入产品描述"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  SearchOutlined,
  RedoOutlined
} from '@ant-design/icons-vue'
import { getCompanyProducts, addCompanyProduct, updateCompanyProduct, deleteCompanyProduct, toggleProductStatus, getProductCategories } from '@/api/company'
import { uploadImage } from '@/api/upload'

// 默认图片
const defaultImage = 'https://via.placeholder.com/200x200'

// 表格列定义
const columns = [
  {
    title: '产品图片',
    key: 'image',
    width: 100,
    align: 'center'
  },
  {
    title: '产品名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true
  },
  {
    title: '产品分类',
    dataIndex: ['category', 'name'],
    key: 'category'
  },
  {
    title: '状态',
    key: 'status',
    align: 'center'
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
    width: 200,
    fixed: 'right',
    align: 'center'
  }
]

// 搜索表单
const searchForm = ref({
  name: '',
  categoryId: undefined
})

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: total => `共 ${total} 条`
})

// 数据列表
const products = ref([])
const loading = ref(false)
const categories = ref([])

// 获取产品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.current,
      size: pagination.value.pageSize,
      ...searchForm.value
    }
    const res = await getCompanyProducts(params)
    products.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取产品列表失败:', error)
    message.error('获取产品列表失败')
  } finally {
    loading.value = false
  }
}

// 表格变化处理
const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchProducts()
}

// 搜索处理
const handleSearch = () => {
  pagination.value.current = 1
  fetchProducts()
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    name: '',
    categoryId: undefined
  }
  pagination.value.current = 1
  fetchProducts()
}

// 表单相关
const modalVisible = ref(false)
const modalLoading = ref(false)
const modalTitle = ref('')
const formRef = ref(null)
const formData = ref({
  value: {
    name: '',
    categoryId: undefined,
    image: '',
    description: ''
  }
})
const fileList = ref([])

// 表单校验规则
const formRules = {
  name: [{ required: true, message: '请输入产品名称' }],
  categoryId: [{ required: true, message: '请选择产品分类' }],
  image: [{ required: true, message: '请上传产品图片' }]
}

// 添加产品
const handleAdd = () => {
  modalTitle.value = '添加产品'
  modalVisible.value = true
  formData.value = {
    value: {
      name: '',
      categoryId: undefined,
      image: '',
      description: ''
    }
  }
  fileList.value = []
}

// 编辑产品
const handleEdit = (record) => {
  modalTitle.value = '编辑产品'
  modalVisible.value = true
  formData.value = {
    value: { ...record }
  }
  fileList.value = record.image ? [{ url: record.image }] : []
}

// 删除产品
const handleDelete = async (record) => {
  try {
    await deleteCompanyProduct(record.id)
    message.success('删除成功')
    fetchProducts()
  } catch (error) {
    console.error('删除产品失败:', error)
    message.error('删除产品失败')
  }
}

// 切换产品状态
const handleToggleStatus = async (record) => {
  try {
    await toggleProductStatus(record.id)
    message.success('操作成功')
    fetchProducts()
  } catch (error) {
    console.error('操作失败:', error)
    message.error('操作失败')
  }
}

// 获取产品分类列表
const fetchCategories = async () => {
  try {
    const res = await getProductCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取产品分类失败:', error)
    message.error('获取产品分类失败')
  }
}

// 图片上传相关
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片必须小于2MB!')
    return false
  }
  return true
}

const customUpload = async ({ file, onSuccess, onError }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadImage(formData)
    if (res.code === 200) {
      formData.value.value.image = res.data
      fileList.value = [
        {
          uid: '-1',
          name: file.name,
          status: 'done',
          url: res.data
        }
      ]
      onSuccess(res)
    } else {
      message.error(res.msg || '上传失败')
      onError(new Error(res.msg))
    }
  } catch (error) {
    console.error('上传失败:', error)
    message.error('上传失败')
    onError(error)
  }
}

// 对话框确认
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true
    
    // 确保图片已上传
    if (!formData.value.value.image && !fileList.value.length) {
      message.error('请上传产品图片')
      modalLoading.value = false
      return
    }

    // 如果有新上传的图片，使用新图片的URL
    if (fileList.value.length > 0) {
      formData.value.value.image = fileList.value[0].url
    }
    
    if (formData.value.value.id) {
      await updateCompanyProduct(formData.value.value)
      message.success('更新成功')
    } else {
      await addCompanyProduct(formData.value.value)
      message.success('添加成功')
    }
    
    modalVisible.value = false
    fetchProducts()
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    modalLoading.value = false
  }
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>

<style scoped lang="less">
.company-product-list {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h2 {
      margin: 0;
    }
  }

  :deep(.ant-form-inline .ant-form-item) {
    margin-bottom: 16px;
  }

  .danger {
    color: #ff4d4f;
  }
}
</style> 