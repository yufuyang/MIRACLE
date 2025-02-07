<template>
  <div class="merchant-home">
    <!-- 商户基本信息 -->
    <a-card class="merchant-info" :loading="loading">
      <template #title>
        <div class="card-title">商户基本信息</div>
      </template>
      <template #extra>
        <a-button type="link" @click="handleEdit">
          <template #icon><EditOutlined /></template>
          编辑资料
        </a-button>
      </template>
      <div class="info-content">
        <div class="info-row">
          <div class="info-item">
            <span class="label">商户名称：</span>
            <span class="value">{{ merchantInfo.merchantName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">营业执照号：</span>
            <span class="value">{{ merchantInfo.licenseNo || '-' }}</span>
          </div>
        </div>
        <div class="info-row">
          <div class="info-item">
            <span class="label">联系人：</span>
            <span class="value">{{ merchantInfo.contactName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系电话：</span>
            <span class="value">{{ merchantInfo.contactPhone || '-' }}</span>
          </div>
        </div>
        <div class="info-row">
          <div class="info-item full">
            <span class="label">地址：</span>
            <span class="value">{{ merchantInfo.province }} {{ merchantInfo.city }} {{ merchantInfo.address }}</span>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 商户简介 -->
    <a-card class="merchant-desc" :loading="loading">
      <template #title>
        <div class="card-title">商户简介</div>
      </template>
      <div class="desc-content">
        {{ merchantInfo.merchantDesc || '暂无简介' }}
      </div>
    </a-card>

<!--    &lt;!&ndash; 数据统计卡片 &ndash;&gt;-->
<!--    <a-card class="stats-card">-->
<!--      <template #title>-->
<!--        <div class="card-title">数据统计</div>-->
<!--      </template>-->
<!--      <div class="stats-grid">-->
<!--        <div class="stats-item">-->
<!--          <div class="stats-value">{{ stats.intentionCount }}</div>-->
<!--          <div class="stats-label">意向数</div>-->
<!--        </div>-->
<!--        <div class="stats-item">-->
<!--          <div class="stats-value">{{ stats.orderCount }}</div>-->
<!--          <div class="stats-label">订单数</div>-->
<!--        </div>-->
<!--        <div class="stats-item">-->
<!--          <div class="stats-value">{{ stats.activityCount }}</div>-->
<!--          <div class="stats-label">活动数</div>-->
<!--        </div>-->
<!--        <div class="stats-item">-->
<!--          <div class="stats-value">¥{{ stats.totalAmount?.toFixed(2) }}</div>-->
<!--          <div class="stats-label">订单总额</div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </a-card>-->

    <!-- 合作企业 -->
    <a-card class="cooperation-list" :loading="cooperationLoading">
      <template #title>
        <div class="card-title">合作企业</div>
      </template>
      <template #extra>
        <a @click="handleViewMore">查看更多</a>
      </template>
      <a-empty v-if="!cooperationList.length" description="暂无合作企业" />
      <a-row v-else :gutter="[16, 16]">
        <a-col v-for="company in cooperationList" :key="company.id" :span="6">
          <a-card hoverable class="company-card" @click="handleViewCompany(company)">
            <img 
              :src="company.companyLogo || defaultImage" 
              :alt="company.companyName"
              class="company-logo"
            />
            <div class="company-info">
              <div class="company-name">{{ company.companyName }}</div>
              <div class="company-contact">
                <div class="contact-item">
                  <UserOutlined />
                  <span>{{ company.companyContactName }}</span>
                </div>
                <div class="contact-item">
                  <PhoneOutlined />
                  <span>{{ company.companyContactPhone }}</span>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-card>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="editVisible"
      title="编辑商户资料"
      @ok="handleSave"
      @cancel="editVisible = false"
    >
      <template #default>
        <a-form :model="editForm" :rules="rules" ref="editFormRef">
          <a-form-item label="商户名称" name="merchantName">
            <a-input v-model:value="editForm.merchantName" placeholder="请输入商户名称" />
          </a-form-item>
          <a-form-item label="营业执照号" name="licenseNo">
            <a-input v-model:value="editForm.licenseNo" placeholder="请输入营业执照号" />
          </a-form-item>
          <a-form-item label="联系人" name="contactName">
            <a-input v-model:value="editForm.contactName" placeholder="请输入联系人" />
          </a-form-item>
          <a-form-item label="联系电话" name="contactPhone">
            <a-input v-model:value="editForm.contactPhone" placeholder="请输入联系电话" />
          </a-form-item>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="省份" name="province">
                <a-select
                  v-model:value="editForm.province"
                  placeholder="请选择省份"
                  @change="handleProvinceChange"
                  allowClear
                >
                  <a-select-option 
                    v-for="province in provinces" 
                    :key="province.code" 
                    :value="province.name"
                  >
                    {{ province.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="城市" name="city">
                <a-select
                  v-model:value="editForm.city"
                  placeholder="请选择城市"
                  :disabled="!editForm.province"
                  allowClear
                >
                  <a-select-option 
                    v-for="city in cities" 
                    :key="city.code" 
                    :value="city.name"
                  >
                    {{ city.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item label="详细地址" name="address">
            <a-textarea v-model:value="editForm.address" placeholder="请输入详细地址" />
          </a-form-item>
          <a-form-item label="商户简介" name="merchantDesc">
            <a-textarea 
              v-model:value="editForm.merchantDesc" 
              :rows="4"
              placeholder="请输入商户简介" 
            />
          </a-form-item>
        </a-form>
      </template>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  EditOutlined,
  UserOutlined,
  PhoneOutlined,
  EnvironmentOutlined,
  IdcardOutlined,
  TeamOutlined,
  HeartOutlined,
  CalendarOutlined,
  TagOutlined,
  CameraOutlined
} from '@ant-design/icons-vue'
import { getMerchantInfo, updateMerchantInfo } from '@/api/merchant/merchant-base'
import { getCooperationList } from '@/api/merchant/cooperation'
import regionsData from '@/assets/data/regions.json'
import defaultImage from '@/assets/images/default.jpg'

const router = useRouter()
const loading = ref(false)
const cooperationLoading = ref(false)
const editVisible = ref(false)
const editFormRef = ref()

// 商户信息
const merchantInfo = ref({})
// 统计数据
const stats = ref({
  intentionCount: 0,    // 意向数
  orderCount: 0,        // 订单数
  activityCount: 0,     // 活动数
  totalAmount: 0        // 订单总金额
})
// 合作企业列表
const cooperationList = ref([])

// 编辑表单
const editForm = reactive({
  merchantName: '',
  licenseNo: '',
  merchantDesc: '',
  contactName: '',
  contactPhone: '',
  province: '',
  city: '',
  address: ''
})

// 表单校验规则
const rules = {
  merchantName: [{ required: true, message: '请输入商户名称' }],
  licenseNo: [{ required: true, message: '请输入营业执照号' }],
  contactName: [{ required: true, message: '请输入联系人' }],
  contactPhone: [{ required: true, message: '请输入联系电话' }],
  province: [{ required: true, message: '请选择省份' }],
  city: [{ required: true, message: '请选择城市' }],
  address: [{ required: true, message: '请输入详细地址' }]
}

// 省市数据
const provinces = ref(regionsData.provinces)
const cities = computed(() => {
  const selectedProvince = provinces.value.find(p => p.name === editForm.province)
  return selectedProvince ? selectedProvince.cities : []
})

// 省份变化时获取对应的城市列表
const handleProvinceChange = (value) => {
  editForm.province = value
  editForm.city = undefined
}

// 获取商户信息
const fetchMerchantInfo = async () => {
  loading.value = true
  try {
    const { data } = await getMerchantInfo()
    merchantInfo.value = data
    stats.value = {
      intentionCount: data.intentionCount || 0,
      orderCount: data.orderCount || 0,
      activityCount: data.activityCount || 0,
      totalAmount: data.totalAmount || 0
    }
  } catch (error) {
    message.error('获取商户信息失败')
  } finally {
    loading.value = false
  }
}

// 获取合作企业列表
const fetchCooperationList = async () => {
  cooperationLoading.value = true
  try {
    const { data } = await getCooperationList({
      pageNum: 1,
      pageSize: 8,
      status: 1  // 只获取已通过的合作
    })
    cooperationList.value = data
  } catch (error) {
    message.error('获取合作企业失败')
  } finally {
    cooperationLoading.value = false
  }
}

// 获取统计数据


// 编辑资料
const handleEdit = () => {
  Object.assign(editForm, merchantInfo.value)
  editVisible.value = true
}

// 保存资料
const handleSave = () => {
  editFormRef.value?.validate().then(async () => {
    try {
      await updateMerchantInfo(editForm)
      message.success('保存成功')
      editVisible.value = false
      fetchMerchantInfo()
    } catch (error) {
      message.error('保存失败')
    }
  })
}

// 查看更多合作企业
const handleViewMore = () => {
  router.push('/workspace/merchant/cooperation')
}

// 查看企业详情
const handleViewCompany = (company) => {
  router.push(`/company/${company.companyId}`)
}

// 处理头像上传
const handleAvatarUpload = async (file) => {
  // TODO: 实现头像上传逻辑
  return false
}

onMounted(() => {
  fetchMerchantInfo()
  fetchCooperationList()
})
</script>

<style lang="less" scoped>
.merchant-home {
  .stats-row {
    display: flex;
    margin-bottom: 24px;
    background: #fff;
    padding: 24px;
    border-radius: 2px;

    .stats-item {
      flex: 1;
      text-align: center;
      position: relative;
      padding: 0 24px;

      &:not(:last-child)::after {
        content: '';
        position: absolute;
        right: 0;
        top: 50%;
        transform: translateY(-50%);
        height: 40%;
        width: 1px;
        background: #f0f0f0;
      }

      .stats-value {
        font-size: 24px;
        font-weight: 500;
        color: #262626;
        margin-bottom: 8px;
      }

      .stats-label {
        color: #8c8c8c;
        font-size: 14px;
      }
    }
  }

  .card-title {
    font-size: 16px;
    font-weight: 500;
    color: #262626;
    position: relative;
    padding-left: 12px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 16px;
      background: #1890ff;
      border-radius: 2px;
    }
  }

  .merchant-info {
    margin-bottom: 24px;

    .info-content {
      .info-row {
        display: flex;
        margin-bottom: 24px;

        &:last-child {
          margin-bottom: 0;
        }

        .info-item {
          flex: 1;
          display: flex;
          align-items: center;

          &.full {
            flex: 0 0 100%;
          }

          .label {
            width: 100px;
            text-align: right;
            margin-right: 12px;
            color: #8c8c8c;
          }

          .value {
            color: #262626;
          }
        }
      }
    }
  }

  .merchant-desc {
    margin-bottom: 24px;

    .desc-content {
      color: #595959;
      line-height: 1.8;
      min-height: 100px;
      white-space: pre-wrap;
    }
  }

  .cooperation-list {
    .company-card {
      height: 100%;
      
      .company-logo {
        width: 100%;
        height: 160px;
        object-fit: cover;
        margin-bottom: 16px;
      }

      .company-info {
        .company-name {
          font-size: 16px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 12px;
          @include ellipsis;
        }

        .company-contact {
          .contact-item {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
            color: #8c8c8c;

            .anticon {
              margin-right: 8px;
              font-size: 14px;
            }

            span {
              @include ellipsis;
            }
          }
        }
      }
    }
  }
}

// 文本省略混入
@mixin ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style> 