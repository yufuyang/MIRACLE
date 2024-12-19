<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo">MIRACLE</div>
        <a-menu mode="horizontal" v-model:selectedKeys="selectedKeys">
          <a-menu-item key="home">
            <router-link to="/">首页</router-link>
          </a-menu-item>
          <a-menu-item key="product">
            <router-link to="/product">产品</router-link>
          </a-menu-item>
          <a-menu-item key="company">
            <router-link to="/company">企业</router-link>
          </a-menu-item>
          <a-menu-item key="workspace" v-if="userInfo">
            <router-link to="/workspace">工作台</router-link>
          </a-menu-item>
        </a-menu>
        <div class="right">
          <template v-if="userInfo">
            <a-dropdown>
              <a class="user-dropdown" @click.prevent>
                <user-outlined />
                <span class="username">{{ userInfo.username }}</span>
              </a>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleLogout">
                    <logout-outlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
          <a-button v-else type="primary" class="login-btn" @click="goToLogin">
            登录
          </a-button>
        </div>
      </div>
    </a-layout-header>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref(null)

// 当前选中的菜单项
const selectedKeys = computed(() => {
  const path = route.path
  if (path.startsWith('/product')) return ['product']
  if (path.startsWith('/company')) return ['company']
  if (path.startsWith('/workspace')) return ['workspace']
  return ['home']
})

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  userInfo.value = null
  message.success('退出登录成功')
  if (route.path !== '/') {
    router.push('/')
  }
}

// 初始化用户信息
onMounted(() => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo)
  }
})
</script>

<style scoped lang="less">
.layout {
  min-height: 100vh;
  background: #f0f2f5;

  .header {
    position: fixed;
    top: 0;
    width: 100%;
    z-index: 1000;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
    padding: 0;

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      height: 64px;

      .logo {
        font-size: 20px;
        font-weight: bold;
        margin-right: 48px;
      }

      .right {
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: 16px;

        .user-dropdown {
          display: flex;
          align-items: center;
          gap: 8px;
          cursor: pointer;
          color: rgba(0, 0, 0, 0.85);

          &:hover {
            color: #1890ff;
          }

          .username {
            margin-right: 4px;
          }
        }
      }
    }
  }

  .main-content {
    margin-top: 64px;
    min-height: calc(100vh - 64px);
  }
}
</style> 