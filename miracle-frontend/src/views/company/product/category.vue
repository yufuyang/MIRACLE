<template>
  <div class="product-category">
    <div class="page-header">
      <h2>产品分类</h2>
      <a-button type="primary" @click="handleAdd">
        <template #icon>
          <plus-outlined />
        </template>
        添加分类
      </a-button>
    </div>

    <a-card>
      <!-- 分类列表 -->
      <a-table
        :columns="columns"
        :data-source="categories"
        :loading="loading"
        :pagination="false"
        row-key="id"
        :scroll="{ x: 800 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button 
                type="link" 
                size="small"
                @click="handleMoveUp(record)"
              >
                <template #icon><arrow-up-outlined /></template>
              </a-button>
              <a-button 
                type="link" 
                size="small"
                @click="handleMoveDown(record)"
              >
                <template #icon><arrow-down-outlined /></template>
              </a-button>
              <a-divider type="vertical" />
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定删除该分类？删除后该分类下的所有产品也将被删除！"
                @confirm="handleDelete(record)"
              >
                <a class="danger">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 添加/编辑分类对话框 -->
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
        <a-form-item label="分类名称" name="categoryName">
          <a-input v-model:value="formData.value.categoryName" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="上级分类" name="parentId">
          <a-tree-select
            v-model:value="formData.value.parentId"
            :tree-data="categoryTree"
            :field-names="{
              children: 'children',
              label: 'categoryName',
              value: 'id'
            }"
            placeholder="请选择上级分类"
            allow-clear
            tree-default-expand-all
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons-vue'
import { getProductCategories, addCategory, updateCategory, deleteCategory } from '@/api/company'

// 表格列定义
const columns = [
  {
    title: '分类名称',
    dataIndex: 'categoryName',
    key: 'categoryName',
    width: '70%'
  },
  {
    title: '操作',
    key: 'action',
    width: '30%',
    align: 'center',
    fixed: 'right'
  }
]

// 数据列表
const categories = ref([])
const categoryTree = ref([])
const loading = ref(false)

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getProductCategories()
    if (res.code === 200) {
      // 按sort字段排序
      categories.value = (res.data || []).sort((a, b) => a.sort - b.sort)
      // 构建树形数据
      categoryTree.value = buildCategoryTree(categories.value)
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    message.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

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

// 表单相关
const modalVisible = ref(false)
const modalLoading = ref(false)
const modalTitle = ref('')
const formRef = ref(null)
const formData = ref({
  value: {
    categoryName: '',
    parentId: undefined,
    sort: 0
  }
})

// 表单校验规则
const formRules = {
  categoryName: [{ required: true, message: '请输入分类名称' }]
}

// 添加获取最大排序值的方法
const getMaxSort = () => {
  if (!categories.value || categories.value.length === 0) {
    return 0
  }
  return Math.max(...categories.value.map(item => item.sort || 0)) + 1
}

// 添加分类
const handleAdd = () => {
  modalTitle.value = '添加分类'
  modalVisible.value = true
  formData.value = {
    value: {
      categoryName: '',
      parentId: undefined,
      sort: getMaxSort()
    }
  }
}

// 编辑分类
const handleEdit = (record) => {
  modalTitle.value = '编辑分类'
  modalVisible.value = true
  formData.value = {
    value: { ...record }
  }
}

// 删除分类
const handleDelete = async (record) => {
  try {
    await deleteCategory(record.id)
    message.success('删除成功')
    fetchCategories()
  } catch (error) {
    console.error('删除分类失败:', error)
    message.error('删除分类失败')
  }
}

// 对话框确认
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true
    
    if (formData.value.value.id) {
      await updateCategory(formData.value.value)
      message.success('更新成功')
    } else {
      await addCategory(formData.value.value)
      message.success('添加成功')
    }
    
    modalVisible.value = false
    fetchCategories()
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    modalLoading.value = false
  }
}

// 添加排序相关方法
const handleMoveUp = async (record) => {
  const currentIndex = categories.value.findIndex(item => item.id === record.id)
  if (currentIndex <= 0) {
    message.warning('已经是第一个了')
    return
  }
  
  const prevItem = categories.value[currentIndex - 1]
  const currentSort = record.sort
  const prevSort = prevItem.sort
  
  try {
    // 交换排序值
    await updateCategory({ ...record, sort: prevSort })
    await updateCategory({ ...prevItem, sort: currentSort })
    message.success('移动成功')
    fetchCategories()
  } catch (error) {
    console.error('移动失败:', error)
    message.error('移动失败')
  }
}

const handleMoveDown = async (record) => {
  const currentIndex = categories.value.findIndex(item => item.id === record.id)
  if (currentIndex >= categories.value.length - 1) {
    message.warning('已经是最后一个了')
    return
  }
  
  const nextItem = categories.value[currentIndex + 1]
  const currentSort = record.sort
  const nextSort = nextItem.sort
  
  try {
    // 交换排序值
    await updateCategory({ ...record, sort: nextSort })
    await updateCategory({ ...nextItem, sort: currentSort })
    message.success('移动成功')
    fetchCategories()
  } catch (error) {
    console.error('移动失败:', error)
    message.error('移动失败')
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped lang="less">
.product-category {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100%;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
    }
  }

  .danger {
    color: #ff4d4f;
  }

  :deep(.ant-table-thead > tr > th) {
    background: #fafafa;
    font-weight: 500;
  }

  :deep(.ant-card) {
    border-radius: 4px;
  }
}
</style> 