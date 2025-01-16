<template>
  <div class="product-detail">
    <div class="page-header">
      <a-page-header
        :title="product?.productName"
        @back="() => router.back()"
      >
        <template #extra>
          <a-space>
            <a-button @click="handleEdit">编辑</a-button>
            <a-modal
              v-model:visible="statusModalVisible"
              :title="getActionTitle(product?.status)"
              @ok="handleToggleStatus"
              :okText="getActionText(product?.status)"
              cancelText="取消"
              centered
            >
              <p>{{ getActionDescription(product?.status) }}</p>
            </a-modal>
            <a-button @click="statusModalVisible = true">{{ getActionText(product?.status) }}</a-button>
          </a-space>
        </template>
      </a-page-header>
    </div>

    <a-spin :spinning="loading">
      <a-card title="基本信息" style="margin-bottom: 24px">
        <a-descriptions :column="2">
          <a-descriptions-item label="产品名称">{{ product?.productName }}</a-descriptions-item>
          <a-descriptions-item label="产品编号">{{ product?.productCode }}</a-descriptions-item>
          <a-descriptions-item label="产品分类">{{ getCategoryName(product?.categoryId) }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ product?.createTime }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ product?.updateTime }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(product?.status)">
              {{ getStatusText(product?.status) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="产品描述" :span="2">{{ product?.description || '-' }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <a-card title="产品图片" style="margin-bottom: 24px">
        <a-spin :spinning="imageLoading">
          <div class="image-list" :class="`images-${productImages.length + (productImages.length < 8 ? 1 : 0)}`">
            <draggable
              v-model="productImages"
              item-key="id"
              ghost-class="ghost"
              chosen-class="chosen"
              @end="handleDragEnd"
              class="image-grid"
            >
              <template #item="{ element }">
                <div class="image-item">
                  <div v-if="element.isMain === 1" class="main-tag">主图</div>
                  <a-image :src="element.imageUrl" />
                  <div class="image-actions">
                    <a-popconfirm
                      title="确定删除该图片？"
                      @confirm="handleDeleteImage(element.id)"
                    >
                      <a-button type="link" danger>删除</a-button>
                    </a-popconfirm>
                  </div>
                </div>
              </template>

              <template #footer>
                <div v-if="productImages.length < 8" class="image-item upload-box">
                  <a-upload
                    :file-list="[]"
                    :custom-request="handleImageUpload"
                    :before-upload="beforeUpload"
                    :show-upload-list="false"
                  >
                    <div class="upload-content">
                      <plus-outlined />
                      <div style="margin-top: 8px">上传图片</div>
                    </div>
                  </a-upload>
                </div>
              </template>
            </draggable>
          </div>
        </a-spin>
      </a-card>

      <a-card title="生产步骤" style="margin-bottom: 24px">
        <template #extra>
          <a-button type="primary" @click="showStepModal">
            <template #icon>
              <plus-outlined />
            </template>
            添加步骤
          </a-button>
        </template>

        <a-spin :spinning="stepsLoading">
          <div class="steps-list">
            <draggable
              v-model="productSteps"
              item-key="id"
              ghost-class="ghost"
              chosen-class="chosen"
              handle=".drag-handle"
              @end="handleStepDragEnd"
            >
              <template #item="{ element, index }">
                <div class="step-item">
                  <div class="step-header">
                    <div class="step-index">
                      <menu-outlined class="drag-handle" />
                      <span class="index">步骤 {{ index + 1 }}</span>
                    </div>
                    <div class="step-actions">
                      <a-button type="link" @click="handleEditStep(element)">编辑</a-button>
                      <a-popconfirm
                        title="确定删除该步骤？"
                        @confirm="handleDeleteStep(element.id)"
                      >
                        <a-button type="link" danger>删除</a-button>
                      </a-popconfirm>
                    </div>
                  </div>
                  <div class="step-content">
                    <h3>{{ element.title }}</h3>
                    <p>{{ element.description }}</p>
                    <div v-if="element.mediaType === 'image'" class="media-content">
                      <a-image :src="element.mediaUrl" />
                    </div>
                    <div v-else-if="element.mediaType === 'video'" class="media-content">
                      <video :src="element.mediaUrl" controls></video>
                    </div>
                  </div>
                </div>
              </template>
            </draggable>

            <div v-if="productSteps.length === 0" class="empty-steps">
              <empty-outlined />
              <p>暂无生产步骤，点击上方按钮添加</p>
            </div>
          </div>
        </a-spin>

        <!-- 添加/编辑步骤对话框 -->
        <a-modal
          v-model:visible="stepModalVisible"
          :title="stepModalTitle"
          :confirm-loading="stepModalLoading"
          @ok="handleStepModalOk"
        >
          <a-form
            ref="stepFormRef"
            :model="stepForm"
            :rules="stepFormRules"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 20 }"
          >
            <a-form-item label="标题" name="title">
              <a-input v-model:value="stepForm.title" placeholder="请输入步骤标题" />
            </a-form-item>
            <a-form-item label="说明" name="description">
              <a-textarea
                v-model:value="stepForm.description"
                placeholder="请输入步骤说明"
                :rows="4"
              />
            </a-form-item>
            <a-form-item label="媒体类型" name="mediaType">
              <a-radio-group v-model:value="stepForm.mediaType">
                <a-radio value="none">无</a-radio>
                <a-radio value="image">图片</a-radio>
                <a-radio value="video">视频</a-radio>
              </a-radio-group>
            </a-form-item>
            <a-form-item
              v-if="stepForm.mediaType !== 'none'"
              :label="stepForm.mediaType === 'image' ? '图片' : '视频'"
              name="mediaUrl"
            >
              <div class="step-media-upload">
                <!-- 图片上传 -->
                <a-upload
                  v-if="stepForm.mediaType === 'image'"
                  v-model:file-list="stepMediaList"
                  :before-upload="beforeUpload"
                  :custom-request="handleStepImageUpload"
                  list-type="picture-card"
                  :max-count="1"
                >
                  <div v-if="stepMediaList.length < 1">
                    <plus-outlined />
                    <div style="margin-top: 8px">上传图片</div>
                  </div>
                </a-upload>

                <!-- 视频上传 -->
                <a-upload
                  v-else
                  v-model:file-list="stepMediaList"
                  :before-upload="beforeVideoUpload"
                  :custom-request="handleStepVideoUpload"
                  :max-count="1"
                >
                  <a-button v-if="stepMediaList.length < 1">
                    <upload-outlined />
                    <span>上传视频</span>
                  </a-button>
                </a-upload>
              </div>
            </a-form-item>
          </a-form>
        </a-modal>
      </a-card>
    </a-spin>

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
        <a-form-item label="产品描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入产品描述"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 所需物料 -->
    <a-card title="所需物料" style="margin-bottom: 24px">
      <template #extra>
        <a-button type="primary" @click="showMaterialModal">
          <template #icon>
            <plus-outlined />
          </template>
          添加物料
        </a-button>
      </template>

      <a-table
        :columns="materialColumns"
        :data-source="materialList"
        :loading="materialLoading"
        :pagination="materialPagination"
        @change="handleTableChange"
      >
        <!-- 物料图片 -->
        <template #materialImage="{ text }">
          <a-image
            :src="text || defaultImage"
            :alt="text"
            style="width: 80px; height: 80px; object-fit: cover"
          />
        </template>
        <!-- 操作列 -->
        <template #materialAction="{ record }">
          <a-space>
            <a @click="handleEditMaterial(record)">编辑</a>
            <a-popconfirm
              title="确定要删除该物料吗？"
              @confirm="handleDeleteMaterial(record)"
            >
              <a class="danger-link">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-card>

    <!-- 添加/编辑物料弹窗 -->
    <a-modal
      v-model:visible="materialModal.visible"
      :title="materialModal.isEdit ? '编辑物料' : '添加物料'"
      @ok="handleSubmitMaterial"
      :confirmLoading="materialModal.loading"
    >
      <a-form :model="materialModal.form" :rules="materialRules" ref="materialFormRef">
        <a-form-item label="物料名称" name="name">
          <a-input v-model:value="materialModal.form.name" placeholder="请输入物料名称" />
        </a-form-item>
        <a-form-item label="物料图片" name="image">
          <a-upload
            v-model:fileList="materialModal.form.fileList"
            :beforeUpload="beforeUpload"
            :maxCount="1"
            list-type="picture-card"
          >
            <div v-if="!materialModal.form.fileList.length">
              <plus-outlined />
              <div style="margin-top: 8px">上传</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="单位" name="unit">
          <a-input v-model:value="materialModal.form.unit" placeholder="请输入单位" />
        </a-form-item>
        <a-form-item label="规格" name="specification">
          <a-input v-model:value="materialModal.form.specification" placeholder="请输入规格" />
        </a-form-item>
        <a-form-item label="首次订购推荐量" name="recommendedQuantity">
          <a-input-number
            v-model:value="materialModal.form.recommendedQuantity"
            placeholder="请输入推荐量"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="说明" name="description">
          <a-textarea
            v-model:value="materialModal.form.description"
            placeholder="请输入说明"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { PlusOutlined, MenuOutlined, UploadOutlined } from '@ant-design/icons-vue'
import draggable from 'vuedraggable'
import { getCompanyProducts, updateCompanyProduct, getProductImages, addProductImage, deleteProductImage, updateImageSort, getProductCategories, getProductSteps, addProductStep, updateProductStep, deleteProductStep, updateStepsSort } from '@/api/company'
import { uploadImage } from '@/api/upload'
import { getMaterialList, addMaterial, updateMaterial, deleteMaterial } from '@/api/company/material'
import defaultImage from '@/assets/images/default.jpg'

const router = useRouter()
const route = useRoute()
const productId = ref(Number(route.params.id))
const loading = ref(false)
const imageLoading = ref(false)
const product = ref(null)
const productImages = ref([])
const categories = ref([])
const statusModalVisible = ref(false)

// 添加编辑相关变量
const modalVisible = ref(false)
const modalLoading = ref(false)
const modalTitle = ref('')
const formRef = ref(null)
const formData = ref({
  productName: '',
  productCode: '',
  categoryId: undefined,
  imageUrl: '',
  description: ''
})
const fileList = ref([])
const categoryTree = ref([])

// 表单校验规则
const formRules = {
  productName: [{ required: true, message: '请输入产品名称' }],
  productCode: [{ required: true, message: '请输入产品编号' }],
  categoryId: [{ required: true, message: '请选择产品分类' }],
  imageUrl: [{ required: true, message: '请上传产品主图' }],
  description: [{ required: true, message: '请输入产品描述' }]
}

// 添加生产步骤相关的变量和函数
const stepsLoading = ref(false)
const productSteps = ref([])
const stepModalVisible = ref(false)
const stepModalLoading = ref(false)
const stepModalTitle = ref('')
const stepFormRef = ref()
const stepForm = ref({
  title: '',
  description: '',
  mediaType: 'none',
  mediaUrl: ''
})
const stepMediaList = ref([])

const stepFormRules = {
  title: [{ required: true, message: '请输入步骤标题' }],
  description: [{ required: true, message: '请输入步骤说明' }]
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
      product.value = res.data[0]
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    message.error('获取产品详情失败')
  } finally {
    loading.value = false
  }
}

// 获取产品图片
const fetchProductImages = async () => {
  imageLoading.value = true
  try {
    const res = await getProductImages(route.params.id)
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

// 上传前校验
const beforeUpload = (file) => {
  // 检查文件类型
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }

  // 检查文件大小（例如限制为2MB）
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片必须小于2MB!')
    return false
  }

  return true
}

// 产品图片上传
const handleImageUpload = async ({ file }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const { data } = await uploadImage(formData)

    // 保存产品图片
    await addProductImage({
      productId: productId.value,
      imageUrl: data,
      isMain: productImages.value.length === 0 ? 1 : 0
    })

    fetchProductImages()
  } catch (error) {
    message.error('上传失败')
  }
}

// 删除产品图片
const handleDeleteImage = async (id) => {
  try {
    const res = await deleteProductImage(id)
    if (res.code === 200) {
      message.success('删除成功')
      await fetchProductImages()
    } else {
      message.error(res.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除图片失败:', error)
    message.error('删除失败，请重试')
  }
}

// 编辑产品
const handleEdit = () => {
  modalTitle.value = '编辑产品'
  modalVisible.value = true
  formData.value = { ...product.value }
  fileList.value = product.value.imageUrl ? [{ url: product.value.imageUrl }] : []
}

// 获取产品分类列表
const fetchCategories = async () => {
  try {
    const res = await getProductCategories()
    if (res.code === 200) {
      categories.value = res.data || []
      categoryTree.value = buildCategoryTree(res.data)
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    message.error('获取分类失败')
  }
}

// 构建分类树
const buildCategoryTree = (data) => {
  const tree = []
  const map = {}

  // 先把所有节点入map
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

// 图片上传相关
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

    const res = await updateCompanyProduct(submitData)
    if (res.code === 200) {
      message.success('更新成功')
      modalVisible.value = false
      await fetchProduct()
    } else {
      message.error(res.msg || '更新失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    modalLoading.value = false
  }
}

// 切换产品状态
const handleToggleStatus = async () => {
  try {
    const newStatus = product.value.status === 1 ? 0 : 1
    await updateCompanyProduct({
      ...product.value,
      status: newStatus
    })
    message.success('操作成功')
    statusModalVisible.value = false  // 关闭弹窗
    await fetchProduct()
  } catch (error) {
    console.error('操作失败:', error)
    message.error('操作失败')
  }
}

// 状态相关函数
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

const getActionTitle = (status) => {
  if (status === 1) {
    return '确定要下架该产品吗？下架后该产品将不会前台展示'
  }
  return '确定要上架该产品吗？上架后该产品将将会在前台展示。'
}

const getActionText = (status) => {
  if (status === 1) {
    return '下架'
  }
  return '上架'
}

const getActionDescription = (status) => {
  if (status === 1) {
    return '下架后该产品将不会在前台展示，确定要继续吗？'
  }
  return '上架后该产品将会在前台展示，确定要继续吗？'
}

// 处理拖拽结束
const handleDragEnd = async () => {
  try {
    // 更新所有图片的排序
    const sortData = productImages.value.map((img, index) => ({
      ...img,
      sort: index + 1
    }))

    if (sortData.length === 0) {
      return
    }

    console.log('Updating sort with data:', sortData)
    const res = await updateImageSort(sortData)
    if (res.code === 200) {
      message.success('排序更新成功')
      await fetchProductImages()
    } else {
      message.error(res.msg || '排序更新失败')
      await fetchProductImages()
    }
  } catch (error) {
    console.error('更新排序失败:', error)
    message.error('排序更新失败，请重试')
    await fetchProductImages()
  }
}

// 添加步骤
const handleAddStep = () => {
  stepModalTitle.value = '添加步骤'
  stepForm.value = {
    title: '',
    description: '',
    mediaType: 'none',
    mediaUrl: ''
  }
  stepMediaList.value = []
  stepModalVisible.value = true
}

// 编辑步骤
const handleEditStep = (step) => {
  stepModalTitle.value = '编辑步骤'
  stepForm.value = { ...step }
  stepMediaList.value = step.mediaUrl ? [{ url: step.mediaUrl }] : []
  stepModalVisible.value = true
}

// 删除步骤
const handleDeleteStep = async (id) => {
  try {
    const res = await deleteProductStep(id)
    if (res.code === 200) {
      message.success('删除成功')
      await fetchProductSteps()
    } else {
      message.error(res.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除步骤失败:', error)
    message.error('删除失败')
  }
}

// 处理步骤拖拽结束
const handleStepDragEnd = async () => {
  try {
    const sortData = productSteps.value.map((step, index) => ({
      id: step.id,
      productId: step.productId,
      sort: index + 1
    }))

    const res = await updateStepsSort(sortData)
    if (res.code === 200) {
      message.success('排序更新成功')
    } else {
      message.error(res.msg || '排序更新失败')
      await fetchProductSteps()
    }
  } catch (error) {
    console.error('更新排序失败:', error)
    message.error('排序更新失败')
    await fetchProductSteps()
  }
}

// 保存步骤
const handleStepModalOk = async () => {
  try {
    await stepFormRef.value.validate()
    stepModalLoading.value = true

    const submitData = {
      ...stepForm.value,
      productId: route.params.id
    }

    const res = submitData.id
      ? await updateProductStep(submitData)
      : await addProductStep(submitData)

    if (res.code === 200) {
      message.success('保存成功')
      stepModalVisible.value = false
      await fetchProductSteps()
    } else {
      message.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    stepModalLoading.value = false
  }
}

// 获取产品步骤列表
const fetchProductSteps = async () => {
  stepsLoading.value = true
  try {
    const res = await getProductSteps(route.params.id)
    if (res.code === 200) {
      productSteps.value = res.data || []
    } else {
      message.error(res.msg || '获取步骤列表失败')
    }
  } catch (error) {
    console.error('获取步骤列表失败:', error)
    message.error('获取步骤列表失败')
  } finally {
    stepsLoading.value = false
  }
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  if (!categoryId) return '-'
  const category = categories.value.find(c => c.id === categoryId)
  if (!category) return '-'

  if (category.parentId) {
    const parentCategory = categories.value.find(c => c.id === category.parentId)
    return parentCategory ? `${parentCategory.categoryName} / ${category.categoryName}` : category.categoryName
  }
  return category.categoryName
}

// 处理步骤图片上传
const handleStepImageUpload = async ({ file, onSuccess, onError }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)

    const res = await uploadImage(formData)
    if (res.code === 200) {
      // 更新步骤表单的媒体信息
      stepForm.value.mediaUrl = res.data
      stepForm.value.mediaType = 'image'

      // 更新文件列表，完全参考 customUpload 的处理方式
      stepMediaList.value = [{
        uid: '-1',
        name: file.name,
        status: 'done',
        url: res.data
      }]

      // 重要：强制更新组件
      stepMediaList.value = [...stepMediaList.value]

      onSuccess()
      message.success('上传成功')
    } else {
      onError()
      message.error(res.msg || '上传失败')
    }
  } catch (error) {
    onError()
    message.error('上传失败')
  }
}

// 视频上传前校验
const beforeVideoUpload = (file) => {
  // 检查文件类型
  const isVideo = file.type.startsWith('video/')
  if (!isVideo) {
    message.error('只能上传视频文件!')
    return false
  }

  // 检查文件大小（限制为100MB）
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isLt100M) {
    message.error('视频必须小于100MB!')
    return false
  }

  return true
}

// 处理步骤视频上传
const handleStepVideoUpload = async ({ file, onSuccess, onError }) => {
  let hide = null
  try {
    const formData = new FormData()
    formData.append('file', file)

    // 显示上传中提示
    hide = message.loading({
      content: '视频上传中...',
      duration: 0,
      key: 'videoUpload'  // 添加唯一的 key
    })

    const res = await uploadImage(formData)
    if (res.code === 200) {
      stepForm.value.mediaUrl = res.data
      stepForm.value.mediaType = 'video'

      stepMediaList.value = [{
        uid: '-1',
        name: file.name,
        status: 'done',
        url: res.data
      }]

      onSuccess()
      message.success('上传成功')
    } else {
      onError()
      message.error(res.msg || '上传失败')
    }
  } catch (error) {
    console.error('视频上传失败:', error)
    onError()
    message.error('视频上传失败，请重试')
  } finally {
    // 确保关闭加载提示
    if (hide) {
      hide()
    }
    // 额外保证加载提示被关闭
    message.destroy('videoUpload')
  }
}

// 物料相关
const materialLoading = ref(false)
const materialList = ref([])
const materialPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})
const materialFormRef = ref()

const materialColumns = [
  {
    title: '物料图片',
    dataIndex: 'image',
    width: 120,
    slots: { customRender: 'materialImage' }
  },
  {
    title: '物料名称',
    dataIndex: 'name'
  },
  {
    title: '单位',
    dataIndex: 'unit'
  },
  {
    title: '规格',
    dataIndex: 'specification'
  },
  {
    title: '建议数量',
    dataIndex: 'recommendedQuantity'
  },
  {
    title: '操作',
    width: 150,
    slots: { customRender: 'materialAction' }
  }
]

const materialModal = ref({
  visible: false,
  loading: false,
  isEdit: false,
  form: {
    name: '',
    unit: '',
    specification: '',
    recommendedQuantity: undefined,
    description: '',
    fileList: []
  }
})

const materialRules = {
  name: [{ required: true, message: '请输入物料名称' }],
  unit: [{ required: true, message: '请输入单位' }],
  specification: [{ required: true, message: '请输入规格' }],
  recommendedQuantity: [{ required: true, message: '请输入推荐量' }]
}

// 获取物料列表
const fetchMaterialList = async () => {
  try {
    const { data } = await getMaterialList({
      productId: productId.value
    })
    materialList.value = data.map(item => ({
      id: item.id,
      name: item.name,
      unit: item.unit,
      specification: item.specification,
      recommendedQuantity: item.recommendedQuantity,
      description: item.description,
      image: item.image
    }))
  } catch (error) {
    console.error('获取物料列表失败:', error)
    message.error('获取物料列表失败')
  }
}

// 处理分页变化
const handleTableChange = (pagination) => {
  materialPagination.value.current = pagination.current
  materialPagination.value.pageSize = pagination.pageSize
  fetchMaterialList()
}

// 显示添加物料弹窗
const showMaterialModal = () => {
  materialModal.value.isEdit = false
  materialModal.value.form = {
    name: '',
    unit: '',
    specification: '',
    recommendedQuantity: undefined,
    description: '',
    fileList: []
  }
  materialModal.value.visible = true
}

// 编辑物料
const handleEditMaterial = (record) => {
  materialModal.value.isEdit = true
  materialModal.value.visible = true
  materialModal.value.form = {
    id: record.id,
    name: record.name,
    unit: record.unit,
    specification: record.specification,
    recommendedQuantity: record.recommendedQuantity,
    description: record.description,
    image: record.image,
    fileList: record.image ? [
      {
        uid: '-1',
        name: 'image.png',
        status: 'done',
        url: record.image
      }
    ] : []
  }
}

// 提交物料表单
const handleSubmitMaterial = async () => {
  try {
    await materialFormRef.value.validate()
    materialModal.value.loading = true

    let imageUrl = ''
    if (materialModal.value.form.fileList.length > 0) {
      // 如果是已有图片，直接使用 url
      if (materialModal.value.form.fileList[0].url) {
        imageUrl = materialModal.value.form.fileList[0].url
      } else {
        // 否则上传新图片
        const formData = new FormData()
        formData.append('file', materialModal.value.form.fileList[0].originFileObj)
        const { data } = await uploadImage(formData)
        imageUrl = data
      }
    }

    const data = {
      productId: productId.value,
      name: materialModal.value.form.name,
      unit: materialModal.value.form.unit,
      specification: materialModal.value.form.specification,
      recommendedQuantity: materialModal.value.form.recommendedQuantity,
      description: materialModal.value.form.description,
      image: imageUrl
    }

    if (materialModal.value.isEdit) {
      data.id = materialModal.value.form.id
      await updateMaterial(data)
    } else {
      await addMaterial(data)
    }

    message.success(materialModal.value.isEdit ? '编辑成功' : '添加成功')
    materialModal.value.visible = false
    fetchMaterialList()
  } catch (error) {
    console.error('提交物料失败:', error)
    message.error(materialModal.value.isEdit ? '编辑失败' : '添加失败')
  } finally {
    materialModal.value.loading = false
  }
}

// 删除物料
const handleDeleteMaterial = async (record) => {
  try {
    await deleteMaterial(record.id)
    message.success('删除成功')
    fetchMaterialList()
  } catch (error) {
    message.error('删除失败')
  }
}

// 修复添加步骤按钮
const showStepModal = () => {
  stepModalVisible.value = true
  stepForm.value = {
    title: '',
    description: '',
    mediaType: 'none',
    mediaUrl: ''
  }
  stepMediaList.value = []
}

onMounted(() => {
  fetchProduct()
  fetchProductImages()
  fetchCategories()
  fetchProductSteps()
  fetchMaterialList()
})
</script>

<style scoped lang="less">
.product-detail {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 24px;

  .page-header {
    margin: -24px -24px 24px;
    background: #fff;
  }

  .image-list {
    .image-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 24px;
      grid-auto-rows: 1fr;
      align-items: start;
    }

    &.images-1 .image-grid {
      grid-template-columns: repeat(1, 1fr);
    }

    &.images-2 .image-grid {
      grid-template-columns: repeat(2, 1fr);
    }

    &.images-3 .image-grid {
      grid-template-columns: repeat(3, 1fr);
    }

    &.images-4 .image-grid {
      grid-template-columns: repeat(4, 1fr);
    }

    &.images-5, &.images-6, &.images-7, &.images-8 {
      .image-grid {
        grid-template-columns: repeat(4, 1fr);
      }
    }

    .image-item {
      position: relative;
      width: 100%;
      aspect-ratio: 1;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      
      &:not(.upload-box) {
        cursor: move;
      }
      
      &.ghost {
        opacity: 0.5;
        background: #c8ebfb;
      }
      
      &.chosen {
        opacity: 0.8;
        box-shadow: 0 3px 6px rgba(0, 0, 0, 0.2);
      }

      .main-tag {
        position: absolute;
        top: 8px;
        left: 8px;
        background: rgba(0, 0, 0, 0.65);
        color: #fff;
        padding: 2px 8px;
        border-radius: 2px;
        font-size: 12px;
        z-index: 1;
      }

      :deep(.ant-image) {
        width: 100%;
        height: 100%;
        display: block;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          display: block;
        }
      }
      
      .image-actions {
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.45);
        padding: 8px;
        display: flex;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s;
        
        .ant-btn {
          color: #fff;
          padding: 0 8px;
          height: 24px;
          
          &[disabled] {
            color: rgba(255, 255, 255, 0.45);
          }

          &.ant-btn-link {
            &:hover {
              color: #40a9ff;
            }

            &.danger:hover {
              color: #ff4d4f;
            }
          }
        }
      }

      &:hover {
        .image-actions {
          opacity: 1;
        }
      }
    }

    .upload-box {
      border: 1px dashed #d9d9d9;
      transition: all 0.3s;
      cursor: pointer;

      &:hover {
        border-color: #1890ff;
      }

      :deep(.ant-upload) {
        width: 100% !important;
        height: 100% !important;
        background: none;
        border: none;
        margin: 0;
        
        .upload-content {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
        }
      }
    }
  }

  .steps-list {
    .step-item {
      margin-bottom: 24px;
      padding: 16px;
      background: #fafafa;
      border-radius: 8px;
      
      .step-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        .step-index {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .drag-handle {
            cursor: move;
            color: #999;
          }
          
          .index {
            font-weight: 500;
          }
        }
      }
      
      .step-content {
        h3 {
          margin: 0 0 8px;
        }
        
        p {
          margin: 0 0 16px;
          color: #666;
        }
        
        .media-content {
          max-width: 100%;
          
          img, video {
            max-width: 100%;
            border-radius: 4px;
          }
        }
      }
      
      &.ghost {
        opacity: 0.5;
        background: #e6f7ff;
      }
      
      &.chosen {
        background: #f0f5ff;
      }
    }
    
    .empty-steps {
      text-align: center;
      padding: 32px;
      color: #999;
      
      .anticon {
        font-size: 48px;
        margin-bottom: 16px;
      }
    }
  }
}
</style> 