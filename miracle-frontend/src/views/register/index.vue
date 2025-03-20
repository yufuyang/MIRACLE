<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>MIRACLE</h2>
        <p>工业品交易平台</p>
      </div>
      <a-form
        :model="registerForm"
        :rules="rules"
        ref="registerFormRef"
        class="register-form"
      >
        <a-form-item name="role">
          <a-radio-group v-model:value="registerForm.role" size="large" class="role-group">
            <a-radio-button value="company">企业用户</a-radio-button>
            <a-radio-button value="merchant">商户用户</a-radio-button>
          </a-radio-group>
        </a-form-item>

        <!-- 基本信息 -->
        <a-form-item name="username">
          <a-input
            v-model:value="registerForm.username"
            placeholder="请输入用户名"
            size="large"
          >
            <template #prefix>
              <user-outlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item name="password">
          <a-input-password
            v-model:value="registerForm.password"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <lock-outlined />
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item name="confirmPassword">
          <a-input-password
            v-model:value="registerForm.confirmPassword"
            placeholder="请确认密码"
            size="large"
          >
            <template #prefix>
              <lock-outlined />
            </template>
          </a-input-password>
        </a-form-item>

        <!-- 企业/商户信息 -->
        <template v-if="registerForm.role === 'company'">
          <a-form-item name="companyName">
            <a-input
              v-model:value="registerForm.companyName"
              placeholder="请输入企业名称"
              size="large"
            >
              <template #prefix>
                <bank-outlined />
              </template>
            </a-input>
          </a-form-item>
        </template>
        <template v-else>
          <a-form-item name="merchantName">
            <a-input
              v-model:value="registerForm.merchantName"
              placeholder="请输入商户名称"
              size="large"
            >
              <template #prefix>
                <shop-outlined />
              </template>
            </a-input>
          </a-form-item>
        </template>

        <a-form-item name="licenseNo">
          <a-input
              v-model:value="registerForm.licenseNo"
              placeholder="请输入营业执照号"
              size="large"
          >
            <template #prefix>
              <file-outlined />
            </template>
          </a-input>
        </a-form-item>

        <!-- 联系信息 -->
        <a-form-item name="contactName">
          <a-input
            v-model:value="registerForm.contactName"
            placeholder="请输入联系人姓名"
            size="large"
          >
            <template #prefix>
              <contacts-outlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item name="contactPhone">
          <a-input
            v-model:value="registerForm.contactPhone"
            placeholder="请输入联系电话"
            size="large"
          >
            <template #prefix>
              <phone-outlined />
            </template>
          </a-input>
        </a-form-item>

        <!-- 地址信息 -->
        <a-form-item name="address">
          <a-input-group compact>
            <a-select
              v-model:value="registerForm.province"
              style="width: 50%"
              size="large"
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
              v-model:value="registerForm.city"
              style="width: 50%"
              size="large"
              placeholder="请选择城市"
              :disabled="!registerForm.province"
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
        <a-form-item name="detailAddress">
          <a-input
            v-model:value="registerForm.detailAddress"
            placeholder="请输入详细地址"
            size="large"
          >
            <template #prefix>
              <environment-outlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </a-button>
        </a-form-item>
        <div class="form-footer">
          <a-button type="link" @click="goToLogin">已有账号？立即登录</a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { companyRegister, merchantRegister } from '@/api/user'
import regionsData from '@/assets/data/regions.json'
import {
  UserOutlined,
  LockOutlined,
  BankOutlined,
  ShopOutlined,
  ContactsOutlined,
  PhoneOutlined,
  EnvironmentOutlined,
  FileOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

// 注册表单
const registerForm = reactive({
  role: 'company',
  username: '',
  password: '',
  confirmPassword: '',
  companyName: '',
  merchantName: '',
  licenseNo: '',
  contactName: '',
  contactPhone: '',
  province: undefined,
  city: undefined,
  detailAddress: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (value !== registerForm.password) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  companyName: [
    { required: true, message: '请输入企业名称', trigger: 'blur' }
  ],
  merchantName: [
    { required: true, message: '请输入商户名称', trigger: 'blur' }
  ],
  licenseNo: [
    { required: true, message: '请输入营业执照号', trigger: 'blur' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  province: [
    { required: true, message: '请选择省份', trigger: 'change' }
  ],
  city: [
    { required: true, message: '请选择城市', trigger: 'change' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 省市数据
const provinces = ref(regionsData.provinces)
const cities = computed(() => {
  const selectedProvince = provinces.value.find(p => p.name === registerForm.province)
  return selectedProvince ? selectedProvince.cities : []
})

// 处理省份选择变化
const handleProvinceChange = () => {
  registerForm.city = undefined
}

// 处理注册
const handleRegister = () => {
  registerFormRef.value.validate().then(async () => {
    loading.value = true
    try {
      // 根据角色选择不同的注册接口
      const registerApi = registerForm.role === 'company' ? companyRegister : merchantRegister
      
      // 构建注册数据
      let registerData = {
        username: registerForm.username,
        password: registerForm.password,
        contactName: registerForm.contactName,
        contactPhone: registerForm.contactPhone,
        province: registerForm.province,
        city: registerForm.city,
        address: registerForm.detailAddress,
        status: 1
      }

      // 根据角色添加不同的字段
      if (registerForm.role === 'company') {
        registerData = {
          ...registerData,
          companyName: registerForm.companyName,
          licenseNo: registerForm.licenseNo
        }
      } else {
        registerData = {
          ...registerData,
          merchantName: registerForm.merchantName
        }
      }

      const response = await registerApi(registerData)
      
      if (response.data) {
        message.success('注册成功')
        router.push('/login')
      }
    } catch (error) {
      console.error('Register failed:', error)
      message.error(error.response?.data?.message || '注册失败')
    } finally {
      loading.value = false
    }
  }).catch(error => {
    console.log('验证失败:', error)
  })
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped lang="less">
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
  //background-image: url('@/assets/images/login-bg.jpg');
  background-size: cover;
  background-position: center;

  .register-box {
    width: 500px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin: 40px 0;

    .register-header {
      text-align: center;
      margin-bottom: 40px;

      h2 {
        font-size: 33px;
        color: #1890ff;
        margin-bottom: 8px;
      }

      p {
        font-size: 14px;
        color: #666;
      }
    }

    .register-form {
      .role-group {
        width: 100%;
        display: flex;
        justify-content: center;
        margin-bottom: 8px;

        :deep(.ant-radio-button-wrapper) {
          height: 36px;
          line-height: 34px;
          padding: 0 20px;
          border-radius: 4px;
          border: 1px solid #d9d9d9;
          background: #fff;
          margin: 0 8px;
          transition: all 0.3s;
          
          &:first-child {
            border-radius: 4px;
          }
          
          &:last-child {
            border-radius: 4px;
          }
          
          &:hover {
            color: #1890ff;
            border-color: #1890ff;
          }
          
          &.ant-radio-button-wrapper-checked {
            color: #fff;
            background: #1890ff;
            border-color: #1890ff;
          }

          &::before {
            display: none;
          }
        }
      }

      .register-button {
        width: 100%;
        height: 40px;
        font-size: 16px;
        border-radius: 4px;
        background: #1890ff;
        border: none;
        transition: all 0.3s;

        &:hover {
          background: #40a9ff;
        }

        &:active {
          background: #096dd9;
        }
      }

      .form-footer {
        text-align: center;
        margin-top: 16px;

        .ant-btn-link {
          color: #1890ff;
          font-size: 14px;
          
          &:hover {
            color: #40a9ff;
          }
        }
      }
    }
  }
}
</style> 