<template>
  <a-layout class="layout">
    <!-- 顶部导航栏 -->
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
          <a-menu-item key="activity">
            <router-link to="/activity">活动</router-link>
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
                <span class="username">{{ userInfo.realName || userInfo.username }}</span>
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
    <a-layout-content :style="{ padding: '24px', minHeight: 'calc(100vh - 64px)' }">
      <div class="content-container">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref(null)

// 选中的菜单项
const selectedKeys = ref([])

// 监听路由变化，更新选中的菜单项
watch(() => route.path, (path) => {
  const key = path.split('/')[1] || 'home'
  selectedKeys.value = [key]
}, { immediate: true })

// 初始化用户信息
const initUserInfo = () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    try {
      userInfo.value = JSON.parse(info)
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }
}

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
  router.push('/')
}

// 初始化
initUserInfo()
</script>

<style scoped lang="less">
.layout {
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 1000;

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    height: 100%;
    padding: 0 24px;

    .logo {
      font-size: 20px;
      font-weight: bold;
      margin-right: 48px;
      color: #1890ff;
    }

    .right {
      margin-left: auto;
      display: flex;
      align-items: center;

      .user-dropdown {
        display: flex;
        align-items: center;
        gap: 8px;
        color: rgba(0, 0, 0, 0.85);

        &:hover {
          color: #1890ff;
        }

        .username {
          margin-left: 8px;
        }
      }

      .login-btn {
        margin-left: 16px;
      }
    }
  }
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  min-height: calc(100vh - 112px);
}
</style> 