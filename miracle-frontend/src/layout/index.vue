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

    <a-layout>
      <!-- 左侧菜单栏 - 仅企业用户可见 -->
      <a-layout-sider v-if="isCompanyUser" width="200" style="background: #fff">
        <a-menu
          v-model:selectedKeys="sideMenuKeys"
          v-model:openKeys="openKeys"
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
        >
          <a-menu-item key="workspace">
            <template #icon>
              <dashboard-outlined />
            </template>
            <router-link to="/workspace">工作台</router-link>
          </a-menu-item>

          <a-sub-menu key="product">
            <template #icon>
              <shop-outlined />
            </template>
            <template #title>产品管理</template>
            <a-menu-item key="product-list">
              <router-link to="/company/product/list">产品列表</router-link>
            </a-menu-item>
            <a-menu-item key="product-category">
              <router-link to="/company/product/category">产品分类</router-link>
            </a-menu-item>
          </a-sub-menu>

          <a-sub-menu key="activity">
            <template #icon>
              <calendar-outlined />
            </template>
            <template #title>活动管理</template>
            <a-menu-item key="activity-list">
              <router-link to="/company/activity/list">活动列表</router-link>
            </a-menu-item>
            <a-menu-item key="activity-stats">
              <router-link to="/company/activity/stats">活动统计</router-link>
            </a-menu-item>
          </a-sub-menu>

          <a-sub-menu key="inquiry">
            <template #icon>
              <message-outlined />
            </template>
            <template #title>意向管理</template>
            <a-menu-item key="inquiry-list">
              <router-link to="/company/inquiry/list">意向列表</router-link>
            </a-menu-item>
            <a-menu-item key="inquiry-stats">
              <router-link to="/company/inquiry/stats">意向统计</router-link>
            </a-menu-item>
          </a-sub-menu>

          <a-sub-menu key="stats">
            <template #icon>
              <bar-chart-outlined />
            </template>
            <template #title>数据统计</template>
            <a-menu-item key="stats-overview">
              <router-link to="/stats/overview">总览</router-link>
            </a-menu-item>
            <a-menu-item key="stats-product">
              <router-link to="/stats/product">产品统计</router-link>
            </a-menu-item>
            <a-menu-item key="stats-activity">
              <router-link to="/stats/activity">活动统计</router-link>
            </a-menu-item>
          </a-sub-menu>

          <a-menu-item key="profile">
            <template #icon>
              <user-outlined />
            </template>
            <router-link to="/profile">企业资料</router-link>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>

      <!-- 主要内容区域 -->
      <a-layout-content :style="{ padding: '24px', minHeight: 'calc(100vh - 64px)' }">
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LogoutOutlined,
  DashboardOutlined,
  ShopOutlined,
  CalendarOutlined,
  MessageOutlined,
  BarChartOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref(null)

// 顶部导航选中的菜单项
const menuItems = [
  {
    key: '/',
    label: '首页'
  },
  {
    key: '/product',
    label: '产品'
  },
  {
    key: '/company',
    label: '企业',
    matchPath: '/company', // 只匹配/company和/company/:id
    excludePath: '/company/product' // 排除产品管理相关路径
  },
  {
    key: '/activity',
    label: '活动'
  },
  {
    key: '/workspace',
    label: '工作台',
    auth: true,
    matchPath: ['/workspace', '/company/product'] // 工作台相关路径
  }
]

// 判断当前路由是否匹配菜单项
const isMenuActive = (item) => {
  const currentPath = route.path
  if (item.matchPath) {
    if (Array.isArray(item.matchPath)) {
      return item.matchPath.some(path => currentPath.startsWith(path))
    }
    return currentPath.startsWith(item.matchPath)
  }
  if (item.excludePath && currentPath.startsWith(item.excludePath)) {
    return false
  }
  return currentPath.startsWith(item.key)
}

// 当前选中的菜单
const selectedKeys = computed(() => {
  const currentPath = route.path
  // 如果是工作台相关路径，返回workspace
  if (currentPath.startsWith('/workspace')) {
    return ['workspace']
  }
  // 如果是企业管理相关路径，返回workspace
  if (currentPath.startsWith('/company/product') || 
      currentPath.startsWith('/company/activity') || 
      currentPath.startsWith('/company/inquiry')) {
    return ['workspace']
  }
  // 如果是企业路径，但不是管理相关路径，返回company
  if (currentPath.startsWith('/company')) {
    return ['company']
  }
  // 其他情况根据路径第一段返回
  const mainPath = currentPath.split('/')[1] || 'home'
  return [mainPath]
})

// 左侧菜单选中的菜单项
const sideMenuKeys = computed(() => {
  const path = route.path
  const parts = path.split('/')
  if (parts.length >= 4 && parts[1] === 'company') {
    return [`${parts[2]}-${parts[3]}`]
  }
  return [parts[1] || 'workspace']
})

// 展开的子菜单
const openKeys = ref(['product', 'activity', 'inquiry', 'stats'])

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

// 判断是否为企业用户
const isCompanyUser = computed(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.role === 'company'
})
</script>

<style scoped lang="less">
.layout {
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

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

:deep(.ant-menu-item a) {
  color: inherit;
  text-decoration: none;
}

:deep(.ant-layout-sider) {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.06);
}
</style> 