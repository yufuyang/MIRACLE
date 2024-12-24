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
        <a-form-item label="产品名称" name="productName">
          <a-input v-model:value="searchForm.productName" placeholder="请输入产品名称" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="分类">
          <a-tree-select
            v-model:value="searchForm.categoryId"
            :tree-data="categoryTree"
            :field-names="{
              children: 'children',
              label: 'categoryName',
              value: 'id'
            }"
            placeholder="请选择分类"
            allow-clear
            tree-default-expand-all
            style="width: 200px"
          />
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
        :scroll="{ x: 1300 }"
        style="margin-top: 16px"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'imageUrl'">
            <a-image
              :width="80"
              :src="record.imageUrl"
              :fallback="defaultImage"
              style="object-fit: cover"
            />
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="handleDetail(record)">详情</a>
              <a-divider type="vertical" />
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm
                :title="getActionTitle(record.status)"
                @confirm="handleToggleStatus(record)"
              >
                <a>{{ getActionText(record.status) }}</a>
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
        :model="formData"
        :rules="formRules"
        :label-col="{span: 6}"
        :wrapper-col="{span: 16}"
      >
        <a-form-item label="产品名称" name="productName">
          <a-input v-model:value="formData.productName" placeholder="请输入产品名称" />
        </a-form-item>
        <a-form-item label="产品编号" name="productCode">
          <a-input v-model:value="formData.productCode" placeholder="请输入产品编号" />
        </a-form-item>
        <a-form-item label="产品分类" name="categoryId">
          <a-tree-select
            v-model:value="formData.categoryId"
            :tree-data="categoryTree"
            :field-names="{
              children: 'children',
              label: 'categoryName',
              value: 'id'
            }"
            placeholder="请选择分类"
            allow-clear
            tree-default-expand-all
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="产品主图" name="imageUrl">
          <a-upload
            v-model:file-list="fileList"
            :before-upload="beforeUpload"
            :custom-request="customUpload"
            list-type="picture-card"
            :max-count="1"
          >
            <div v-if="fileList.length < 1">
              <plus-outlined />
              <div style="margin-top: 8px">上传主图</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="产品价格" name="price">
          <a-input-number
            v-model:value="formData.price"
            placeholder="请输入产品价格"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="计量单位" name="unit">
          <a-input v-model:value="formData.unit" placeholder="请输入计量单位" />
        </a-form-item>
        <a-form-item label="产品描述" name="description">
          <a-textarea
            v-model:value="formData.description"
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
import { getCompanyProducts, addCompanyProduct, updateCompanyProduct, deleteCompanyProduct, toggleProductStatus, getProductCategories, getProductImages, addProductImage, deleteProductImage, setMainImage, updateImageSort } from '@/api/company'
import { uploadImage } from '@/api/upload'
import { useRouter } from 'vue-router'

// 默认图片
const defaultImage = 'https://via.placeholder.com/200x200'

// 表格列定义
const columns = [
  {
    title: '产品图片',
    key: 'imageUrl',
    dataIndex: 'imageUrl',
    width: 120,
    align: 'center',
    fixed: 'left'
  },
  {
    title: '产品名称',
    dataIndex: 'productName',
    key: 'productName',
    width: 200,
    ellipsis: true
  },
  {
    title: '产品编号',
    dataIndex: 'productCode',
    key: 'productCode',
    width: 120
  },
  {
    title: '分类',
    dataIndex: 'categoryId',
    key: 'categoryId',
    width: 180,
    customRender: ({ record }) => {
      const findCategory = (id) => categories.value.find(c => c.id === id)
      const category = findCategory(record.categoryId)
      if (!category) return '-'
      
      if (category.parentId) {
        const parentCategory = findCategory(category.parentId)
        return parentCategory ? `${parentCategory.categoryName} / ${category.categoryName}` : category.categoryName
      }
      return category.categoryName
    }
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    width: 120,
    align: 'right',
    customRender: ({ text }) => text ? `¥${text}` : '-'
  },
  {
    title: '单位',
    dataIndex: 'unit',
    key: 'unit',
    width: 80,
    align: 'center'
  },
  {
    title: '状态',
    key: 'status',
    dataIndex: 'status',
    width: 100,
    align: 'center'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    align: 'center'
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
  productName: '',
  categoryId: undefined
})

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: total => `共 ${total} 条`,
  showSizeChanger: true,
  showQuickJumper: true
})

// 数据列表
const products = ref([])
const loading = ref(false)
const categories = ref([])
const categoryTree = ref([])

// 构建分类树
const buildCategoryTree = (data) => {
  const tree = []
  const map = {}

  // 先把所有节点存入map
  data.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })

  // 构建树形结构
  data.forEach(item => {
    const node = map[item.id]
    if (item.parentId) {
      const parent = map[item.parentId]
      if (parent) {
        parent.children.push(node)
      }
    } else {
      tree.push(node)
    }
  })

  return tree
}

// 获取产品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.pageSize,
      ...searchForm.value
    }
    const res = await getCompanyProducts(params)
    if (res.code === 200) {
      products.value = res.data || []
      pagination.value.total = res.total || 0
    }
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
    productName: '',
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
  productName: '',
  productCode: '',
  categoryId: undefined,
  imageUrl: '',
  price: undefined,
  unit: '',
  description: ''
})
const fileList = ref([])

// 表单校验规则
const formRules = {
  productName: [{ required: true, message: '请输入产品名称' }],
  productCode: [{ required: true, message: '请输入产品编号' }],
  categoryId: [{ required: true, message: '请选择产品分类' }],
  imageUrl: [{ required: true, message: '请上传产品主图' }],
  price: [{ required: true, message: '请输入产品价格' }],
  unit: [{ required: true, message: '请输入计量单位' }]
}

// 添加产品
const handleAdd = () => {
  modalTitle.value = '添加产品'
  modalVisible.value = true
  formData.value = {
    productName: '',
    productCode: '',
    categoryId: undefined,
    imageUrl: '',
    price: undefined,
    unit: '',
    description: '',
    status: null
  }
  fileList.value = []
}

// 编辑产品
const handleEdit = (record) => {
  modalTitle.value = '编辑产品'
  modalVisible.value = true
  formData.value = { ...record }
  fileList.value = record.imageUrl ? [{ url: record.imageUrl }] : []
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
    const newStatus = record.status === 1 ? 0 : 1
    await updateCompanyProduct({
      ...record,
      status: newStatus
    })
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
    if (res.code === 200) {
      categories.value = res.data || []
      // 构建树形数据
      categoryTree.value = buildCategoryTree(res.data)
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    message.error('获取分类失败')
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
    const uploadFormData = new FormData()
    uploadFormData.append('file', file)
    const res = await uploadImage(uploadFormData)
    if (res.code === 200) {
      formData.value.imageUrl = res.data
      fileList.value = [
        {
          uid: '-1',
          name: file.name,
          status: 'done',
          url: res.data
        }
      ]
      onSuccess()
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
    
    // 确保主图已上传
    if (!fileList.value.length) {
      message.error('请上传产品主图')
      modalLoading.value = false
      return
    }

    // 构建提交数据
    const submitData = {
      ...formData.value,
      imageUrl: formData.value.imageUrl
    }
    
    if (formData.value.id) {
      await updateCompanyProduct(submitData)
      message.success('更新成功')
    } else {
      await addCompanyProduct(submitData)
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

// 修改详情处理方法
const router = useRouter()

const handleDetail = (record) => {
  router.push(`/workspace/product/detail/${record.id}`)
}

// 获取产品图片
const fetchProductImages = async (productId) => {
  imageLoading.value = true
  try {
    const res = await getProductImages(productId)
    if (res.code === 200) {
      productImages.value = res.data || []
    }
  } catch (error) {
    console.error('获取产品图片失败:', error)
    message.error('获取产品图片失败')
  } finally {
    imageLoading.value = false
  }
}

// 上传产品图片
const handleImageUpload = async ({ file, onSuccess, onError }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadImage(formData)
    if (res.code === 200) {
      const imageData = {
        productId: currentProduct.value.id,
        imageUrl: res.data,
        sort: productImages.value.length + 1,
        isMain: productImages.value.length === 0 ? 1 : 0
      }
      await addProductImage(imageData)
      await fetchProductImages(currentProduct.value.id)
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

// 删除产品图片
const handleDeleteImage = async (id) => {
  try {
    await deleteProductImage(id)
    message.success('删除成功')
    await fetchProductImages(currentProduct.value.id)
  } catch (error) {
    console.error('删除图片失败:', error)
    message.error('删除图片失败')
  }
}

// 设置主图
const handleSetMainImage = async (id) => {
  try {
    await setMainImage(id)
    message.success('设置成功')
    await fetchProductImages(currentProduct.value.id)
  } catch (error) {
    console.error('设置主图失败:', error)
    message.error('设置主图失败')
  }
}

// 添加状态处理函数
const getStatusColor = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 0:
      return 'error'
    default:
      return 'default'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 1:
      return '已上架'
    case 0:
      return '已下架'
    default:
      return '未上架'
  }
}

// 添加操作文本处理函数
const getActionTitle = (status) => {
  if (status === 1) {
    return '确定下架该产品？'
  }
  return '确定上架该产品？'
}

const getActionText = (status) => {
  if (status === 1) {
    return '下架'
  }
  return '上架'
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

.product-images {
  margin-top: 24px;
  
  h3 {
    margin-bottom: 16px;
  }

  .image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
  }

  .image-item {
    position: relative;
    width: 100px;
    
    .image-actions {
      position: absolute;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.45);
      padding: 4px;
      display: flex;
      justify-content: center;
      
      .ant-btn {
        color: #fff;
        padding: 0 4px;
        height: 24px;
        
        &[disabled] {
          color: rgba(255, 255, 255, 0.45);
        }
      }
    }
  }
}
</style> 