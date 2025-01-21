<template>
  <div class="company-profile">
    <a-spin :spinning="loading">
      <!-- 基本信息部分 -->
      <div class="basic-info section-card">
        <div class="section-header">
          <div class="section-title">基本信息</div>
          <a-button type="primary" @click="showEditModal">
            编辑
          </a-button>
        </div>
        <div class="info-content">
          <div class="info-logo">
            <span class="label">企业Logo：</span>
            <div class="logo-wrapper">
              <a-image
                v-if="formData.logoUrl"
                :src="formData.logoUrl"
                :preview="{
                  src: formData.logoUrl,
                  mask: '预览'
                }"
                class="logo-img"
              />
              <div v-else class="logo-placeholder">暂无Logo</div>
            </div>
          </div>
          <div class="info-item">
            <span class="label">企业名称：</span>
            <span class="value">{{ formData.companyName }}</span>
          </div>
          <div class="info-item">
            <span class="label">营业执照号：</span>
            <span class="value">{{ formData.licenseNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系人：</span>
            <span class="value">{{ formData.contactName }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系电话：</span>
            <span class="value">{{ formData.contactPhone }}</span>
          </div>
          <div class="info-item">
            <span class="label">所在地区：</span>
            <span class="value">{{ formData.province }} {{ formData.city }}</span>
          </div>
          <div class="info-item">
            <span class="label">详细地址：</span>
            <span class="value">{{ formData.address }}</span>
          </div>
          <div class="info-item full-width">
            <span class="label">企业简介：</span>
            <span class="value">{{ formData.companyDesc }}</span>
          </div>
        </div>
      </div>

      <!-- 企业环境部分 -->
      <div class="company-images section-card">
        <div class="section-header">
          <div class="section-title">企业环境</div>
        </div>
        <div class="info-content">
          <div class="image-list" :class="`images-${environmentImages.length + (environmentImages.length < 8 ? 1 : 0)}`">
            <div class="image-grid">
              <div v-for="(image, index) in environmentImages" :key="image.uid" class="image-item">
                <a-image :src="image.url" />
                <div class="image-actions">
                  <a-button type="link" danger @click="handleRemoveImage(index)">
                    删除
                  </a-button>
                </div>
              </div>
              
              <div v-if="environmentImages.length < 8" class="image-item upload-box">
                <a-upload
                  :file-list="[]"
                  :custom-request="handleEnvironmentImageUpload"
                  :before-upload="beforeUpload"
                  :show-upload-list="false"
                >
                  <div class="plus">+</div>
                  <div class="text">上传图片</div>
                </a-upload>
              </div>
            </div>
          </div>
          <div class="upload-tip">建议尺寸：1920x1080px，支持jpg、png格式，文件小于2MB</div>
        </div>
      </div>
    </a-spin>

    <!-- 编辑信息弹框 -->
    <a-modal
      v-model:visible="editModalVisible"
      title="编辑企业信息"
      @ok="handleEditSubmit"
      @cancel="editModalVisible = false"
      :confirmLoading="submitLoading"
      width="600px"
    >
      <a-form
        ref="editFormRef"
        :model="editForm"
        :rules="formRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="企业Logo" name="logoUrl">
          <div class="logo-upload">
            <a-upload
              v-model:file-list="logoFileList"
              :before-upload="beforeUpload"
              :custom-request="handleLogoUpload"
              list-type="picture-card"
              :max-count="1"
            >
              <div v-if="logoFileList.length < 1">
                <plus-outlined />
                <div style="margin-top: 8px">上传Logo</div>
              </div>
            </a-upload>
            <div class="upload-tip">建议尺寸：200x200px，支持jpg、png格式，文件小于2MB</div>
          </div>
        </a-form-item>
        
        <a-form-item label="企业名称">
          <span>{{ editForm.companyName }}</span>
        </a-form-item>
        
        <a-form-item label="营业执照号">
          <span>{{ editForm.licenseNo }}</span>
        </a-form-item>

        <a-form-item label="法定代表人" name="legalPerson">
          <a-input v-model:value="editForm.legalPerson" />
        </a-form-item>

        <a-form-item label="联系人" name="contactName">
          <a-input v-model:value="editForm.contactName" />
        </a-form-item>

        <a-form-item label="联系电话" name="contactPhone">
          <a-input v-model:value="editForm.contactPhone" />
        </a-form-item>

        <a-form-item label="所在地区">
          <a-input-group compact>
            <a-select
              v-model:value="editForm.province"
              style="width: 50%"
              placeholder="请选择省份"
              @change="handleProvinceChange"
            >
              <a-select-option 
                v-for="province in provinces" 
                :key="province.code" 
                :value="province.name"
              >
                {{ province.name }}
              </a-select-option>
            </a-select>
            <a-select
              v-model:value="editForm.city"
              style="width: 50%"
              placeholder="请选择城市"
              :disabled="!editForm.province"
            >
              <a-select-option 
                v-for="city in cities" 
                :key="city.code" 
                :value="city.name"
              >
                {{ city.name }}
              </a-select-option>
            </a-select>
          </a-input-group>
        </a-form-item>

        <a-form-item label="详细地址" name="address">
          <a-input v-model:value="editForm.address" />
        </a-form-item>

        <a-form-item label="企业简介" name="companyDesc">
          <a-textarea
            v-model:value="editForm.companyDesc"
            :rows="4"
            placeholder="请输入企业简介"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { PageHeader } from 'ant-design-vue'
import { getCompanyInfo, updateCompanyInfo, saveCompanyEnvironmentImage, deleteCompanyEnvironmentImage, getCompanyEnvironmentImages } from '@/api/company'
import { uploadImage } from '@/api/upload'
import regionsData from '@/assets/data/regions.json'

const loading = ref(false)
const submitLoading = ref(false)
const editModalVisible = ref(false)
const editFormRef = ref()
const environmentImages = ref([])
const logoFileList = ref([])

// 省市数据
const provinces = ref(regionsData.provinces)
const cities = computed(() => {
  const selectedProvince = provinces.value.find(p => p.name === editForm.value.province)
  return selectedProvince ? selectedProvince.cities : []
})

// 处理省份变化
const handleProvinceChange = () => {
  editForm.value.city = undefined
}

// 处理Logo上传
const handleLogoUpload = async ({ file, onSuccess, onError }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', 'logo')
    
    const res = await uploadImage(formData)
    if (res.code === 200) {
      editForm.value.logoUrl = res.data
      logoFileList.value = [{
        uid: '-1',
        name: file.name,
        status: 'done',
        url: res.data
      }]
      onSuccess()
    } else {
      onError()
      message.error(res.msg || '上传失败')
    }
  } catch (error) {
    onError()
    message.error('上传失败')
  }
}

// 表单数据
const formData = ref({
  companyName: '',
  licenseNo: '',
  legalPerson: '',
  contactName: '',
  contactPhone: '',
  province: '',
  city: '',
  address: '',
  companyDesc: '',
  status: 1
})

// 编辑表单数据
const editForm = ref({})

// 表单校验规则
const formRules = {
  legalPerson: [{ required: true, message: '请输入法定代表人' }],
  contactName: [{ required: true, message: '请输入联系人' }],
  contactPhone: [{ required: true, message: '请输入联系电话' }],
  address: [{ required: true, message: '请输入详细地址' }],
  companyDesc: [{ required: true, message: '请输入企业简介' }]
}

// 显示编辑弹框
const showEditModal = () => {
  editForm.value = { ...formData.value }
  editModalVisible.value = true
}

// 处理编辑提交
const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate()
    submitLoading.value = true
    
    const res = await updateCompanyInfo(editForm.value)
    if (res.code === 200) {
      message.success('保存成功')
      editModalVisible.value = false
      formData.value = { ...editForm.value }
    } else {
      message.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    message.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

// 处理环境图片上传
const handleEnvironmentImageUpload = async ({ file, onSuccess, onError }) => {
  try {
    // 1. 先上传图片
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', 'environment')
    
    const uploadRes = await uploadImage(formData)
    if (uploadRes.code === 200) {
      // 2. 上传成功后，调用保存环境图片接口
      const saveRes = await saveCompanyEnvironmentImage({
        imageUrl: uploadRes.data,
        sort: environmentImages.value.length + 1  // 排序号
      })
      
      if (saveRes.code === 200) {
        // 3. 保存成功后，更新本地数据
        environmentImages.value.push({
          id: saveRes.data,  // 保存返回的图片ID
          uid: Date.now(),
          name: file.name,
          status: 'done',
          url: uploadRes.data
        })
        onSuccess()
        message.success('上传成功')
      } else {
        onError()
        message.error(saveRes.msg || '保存失败')
      }
    } else {
      onError()
      message.error(uploadRes.msg || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    onError()
    message.error('上传失败')
  }
}

// 添加删除图片的处理函数
const handleRemoveImage = async (index) => {
  try {
    const image = environmentImages.value[index]
    const res = await deleteCompanyEnvironmentImage(image.id)
    if (res.code === 200) {
      environmentImages.value.splice(index, 1)
      message.success('删除成功')
    } else {
      message.error(res.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

// 在组件挂载时获取环境图片列表
const fetchEnvironmentImages = async () => {
  try {
    const res = await getCompanyEnvironmentImages()
    if (res.code === 200) {
      environmentImages.value = res.data.map(item => ({
        id: item.id,
        uid: item.id,
        name: item.imageUrl.split('/').pop(),
        status: 'done',
        url: item.imageUrl
      }))
    }
  } catch (error) {
    console.error('获取环境图片失败:', error)
    message.error('获取环境图片失败')
  }
}

// 获取企业资料
const fetchInfo = async () => {
  loading.value = true
  try {
    const res = await getCompanyInfo()
    if (res.code === 200) {
      formData.value = res.data
      if (res.data.logoUrl) {
        logoFileList.value = [{ url: res.data.logoUrl }]
      }
    }
  } catch (error) {
    console.error('获取企业资料失败:', error)
    message.error('获取企业资料失败')
  } finally {
    loading.value = false
  }
}

// 上传前校验
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

onMounted(() => {
  fetchInfo()
  fetchEnvironmentImages()
})
</script>

<style scoped lang="less">
.company-profile {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 24px;

  .section-card {
    background: #fff;
    border-radius: 2px;
    margin-bottom: 24px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 24px;
      border-bottom: 1px solid #f0f0f0;

      .section-title {
        font-size: 16px;
        font-weight: 500;
        color: rgba(0, 0, 0, 0.85);
      }
    }
  }

  .basic-info {
    .info-content {
      padding: 24px;
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 24px;

      .info-logo {
        grid-column: span 2;
        display: flex;
        margin-bottom: 24px;

        .label {
          width: 120px;
          color: rgba(0, 0, 0, 0.85);
          text-align: right;
          padding-right: 16px;
          line-height: 100px;
        }

        .logo-wrapper {
          width: 100px;
          height: 100px;
          border: 1px dashed #d9d9d9;
          border-radius: 2px;
          display: flex;
          align-items: center;
          justify-content: center;
          overflow: hidden;

          .logo-img {
            width: 100%;
            height: 100%;
            object-fit: contain;
            cursor: pointer;

            &:hover {
              opacity: 0.8;
            }
          }

          .logo-placeholder {
            color: #999;
          }
        }
      }

      .info-item {
        display: flex;
        align-items: flex-start;

        &.full-width {
          grid-column: span 2;
        }

        .label {
          width: 120px;
          color: rgba(0, 0, 0, 0.85);
          text-align: right;
          padding-right: 16px;
          line-height: 32px;
        }

        .value {
          flex: 1;
          line-height: 32px;
          color: rgba(0, 0, 0, 0.65);
        }
      }
    }
  }

  .company-images {
    .info-content {
      padding: 24px;

      .image-list {
        .image-grid {
          display: grid;
          grid-template-columns: repeat(4, 1fr);
          gap: 16px;
          align-items: start;
        }

        .image-item {
          position: relative;
          width: 100%;
          aspect-ratio: 1;
          background: #fff;
          border-radius: 4px;
          box-shadow: none;
          overflow: hidden;

          &.upload-box {
            border: 1px dashed #d9d9d9;
            transition: all 0.3s;
            cursor: pointer;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 20px 0;

            :deep(.ant-upload) {
              width: 100% !important;
              height: 100% !important;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              background: none;
              border: none;
              margin: 0;
              padding: 0;

              .plus {
                font-size: 24px;
                color: #999;
                line-height: 1;
                margin-bottom: 8px;
              }

              .text {
                font-size: 14px;
                color: #999;
              }
            }

            &:hover {
              border-color: #1890ff;
            }
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

              &.ant-btn-link {
                &:hover {
                  color: #ff4d4f;
                }
              }
            }
          }

          &:hover .image-actions {
            opacity: 1;
          }
        }
      }

      .upload-tip {
        margin-top: 16px;
        color: #999;
        font-size: 12px;
        text-align: center;
      }
    }
  }
}
</style> 