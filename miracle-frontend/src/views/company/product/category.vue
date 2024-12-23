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
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
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
        :model="formData"
        :rules="formRules"
        label-col="{ span: 6 }"
        wrapper-col="{ span: 16 }"
      >
        <a-form-item label="分类名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="上级分类" name="parentId">
          <a-tree-select
            v-model:value="formData.parentId"
            :tree-data="categoryTree"
            :field-names="{
              children: 'children',
              label: 'name',
              value: 'id'
            }"
            placeholder="请选择上级分类"
            allow-clear
            tree-default-expand-all
          />
        </a-form-item>
        <a-form-item label="排序" name="sort">
          <a-input-number
            v-model:value="formData.sort"
            :min="0"
            :max="999"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getProductCategories, addCategory, updateCategory, deleteCategory } from '@/api/company'

// 表格列定义
const columns = [
  {
    title: '分类名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '排序',
    dataIndex: 'sort',
    key: 'sort',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
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
      categories.value = res.data || []
      // 构建树形数据
      categoryTree.value = buildCategoryTree(res.data)
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
  name: '',
  parentId: undefined,
  sort: 0
})

// 表单校验规则
const formRules = {
  name: [{ required: true, message: '请输入分类名称' }]
}

// 添加分类
const handleAdd = () => {
  modalTitle.value = '添加分类'
  modalVisible.value = true
  formData.value = {
    name: '',
    parentId: undefined,
    sort: 0
  }
}

// 编辑分类
const handleEdit = (record) => {
  modalTitle.value = '编辑分类'
  modalVisible.value = true
  formData.value = { ...record }
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
    
    if (formData.value.id) {
      await updateCategory(formData.value)
      message.success('更新成功')
    } else {
      await addCategory(formData.value)
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

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped lang="less">
.product-category {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h2 {
      margin: 0;
    }
  }

  .danger {
    color: #ff4d4f;
  }
}
</style> 