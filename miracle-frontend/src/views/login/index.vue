<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>MIRACLE</h2>
        <p>工业品交易平台</p>
      </div>
      <a-form
        :model="loginForm"
        :rules="rules"
        ref="loginFormRef"
        class="login-form"
      >
        <a-form-item name="username">
          <a-input
            v-model:value="loginForm.username"
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
            v-model:value="loginForm.password"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <lock-outlined />
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item name="role">
          <a-radio-group v-model:value="loginForm.role" size="large" class="role-group">
            <a-radio-button value="company">企业用户</a-radio-button>
            <a-radio-button value="merchant">商户用户</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </a-button>
        </a-form-item>
        <div class="form-footer">
          <a-button type="link" @click="goToRegister">还没有账号？立即注册</a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { companyLogin, merchantLogin } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  role: 'company'
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
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ]
}

// 处理登录
const handleLogin = () => {
  loginFormRef.value?.validate().then(async () => {
    loading.value = true
    try {
      // 根据角色选择不同的登录接口
      const loginApi = loginForm.role === 'company' ? companyLogin : merchantLogin
      const response = await loginApi({
        username: loginForm.username,
        password: loginForm.password
      })
      
      if (response.data) {
        // 存储用户信息和token
        localStorage.setItem('userInfo', JSON.stringify(response.data))
        localStorage.setItem('token', response.data.token)
        
        // 根据角色直接跳转到对应页面
        if (response.data.role === 'COMPANY') {
          router.push('/workspace/profile')
        } else if (response.data.role === 'MERCHANT') {
          router.push('/workspace/merchant/home')
        }
        message.success('登录成功')
      }
    } catch (error) {
      console.error('Login failed:', error)
      message.error(error.response?.data?.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped lang="less">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
  //background-image: url('@/assets/images/login-bg.jpg');
  background-size: cover;
  background-position: center;

  .login-box {
    width: 400px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

    .login-header {
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

    .login-form {
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

      .login-button {
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