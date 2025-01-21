<template>
  <a-layout class="layout">
    <!-- 顶部导航栏 -->
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo">MIRACLE</div>
        <a-menu mode="horizontal" v-model:selectedKeys="selectedKeys">
          <a-menu-item key="home">
            <router-link to="/">返回首页</router-link>
          </a-menu-item>
        </a-menu>
        <div class="right">
          <a-dropdown>
            <a class="user-dropdown" @click.prevent>
              <user-outlined />
              <span class="username">{{ userInfo?.username }}</span>
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
        </div>
      </div>
    </a-layout-header>

    <a-layout>
      <!-- 左侧菜单栏 -->
      <a-layout-sider width="200" style="background: #fff">
        <a-menu
          v-model:selectedKeys="sideMenuKeys"
          v-model:openKeys="openKeys"
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
        >
          <!-- 企业菜单 -->
          <template v-if="userRole === 'COMPANY'">
            <a-menu-item key="profile">
              <template #icon>
                <user-outlined />
              </template>
              <router-link to="/workspace/profile">企业资料</router-link>
            </a-menu-item>

            <a-sub-menu key="product">
              <template #icon>
                <shop-outlined />
              </template>
              <template #title>产品管理</template>
              <a-menu-item key="product-list">
                <router-link to="/workspace/product/list">产品列表</router-link>
              </a-menu-item>
              <a-menu-item key="product-category">
                <router-link to="/workspace/product/category">产品分类</router-link>
              </a-menu-item>
              <a-menu-item key="product-statistics">
                <router-link to="/workspace/product/statistics">产品统计</router-link>
              </a-menu-item>
            </a-sub-menu>

            <a-sub-menu key="activity">
              <template #icon>
                <calendar-outlined />
              </template>
              <template #title>活动管理</template>
              <a-menu-item key="activity-list">
                <router-link to="/workspace/activity/list">活动列表</router-link>
              </a-menu-item>
              <a-menu-item key="activity-stats">
                <router-link to="/workspace/activity/stats">活动统计</router-link>
              </a-menu-item>
            </a-sub-menu>

            <a-sub-menu key="inquiry">
              <template #icon>
                <message-outlined />
              </template>
              <template #title>意向管理</template>
              <a-menu-item key="inquiry-list">
                <router-link to="/workspace/inquiry/list">意向列表</router-link>
              </a-menu-item>
              <a-menu-item key="inquiry-stats">
                <router-link to="/workspace/inquiry/stats">意向统计</router-link>
              </a-menu-item>
            </a-sub-menu>

            <a-sub-menu key="order">
              <template #icon>
                <shopping-cart-outlined />
              </template>
              <template #title>订单管理</template>
              <a-menu-item key="order-list">
                <router-link to="/workspace/order/list">订单列表</router-link>
              </a-menu-item>
              <a-menu-item key="order-stats">
                <router-link to="/workspace/order/stats">订单统计</router-link>
              </a-menu-item>
            </a-sub-menu>

            <a-sub-menu key="cooperation">
              <template #icon>
                <team-outlined />
              </template>
              <template #title>合作管理</template>
              <a-menu-item key="cooperation-list">
                <router-link to="/workspace/cooperation/list">合作列表</router-link>
              </a-menu-item>
              <a-menu-item key="cooperation-stats">
                <router-link to="/workspace/cooperation/stats">合作统计</router-link>
              </a-menu-item>
            </a-sub-menu>
          </template>

          <!-- 商户菜单 -->
          <template v-else>
            <a-menu-item key="merchant-dashboard">
              <template #icon>
                <dashboard-outlined />
              </template>
              <router-link to="/workspace/merchant/home">商户首页</router-link>
            </a-menu-item>
            
            <a-sub-menu key="merchant-activity">
              <template #icon>
                <calendar-outlined />
              </template>
              <template #title>活动管理</template>
              <a-menu-item key="merchant-activity-list">
                <router-link to="/workspace/merchant/activity/list">活动列表</router-link>
              </a-menu-item>
            </a-sub-menu>
            
            <a-sub-menu key="merchant-intention">
              <template #icon>
                <message-outlined />
              </template>
              <template #title>意向管理</template>
              <a-menu-item key="merchant-intention-list">
                <router-link to="/workspace/merchant/intention/list">意向列表</router-link>
              </a-menu-item>
            </a-sub-menu>
            
            <a-sub-menu key="merchant-cooperation">
              <template #icon>
                <team-outlined />
              </template>
              <template #title>合作管理</template>
              <a-menu-item key="merchant-cooperation-list">
                <router-link to="/workspace/merchant/cooperation/list">合作列表</router-link>
              </a-menu-item>
            </a-sub-menu>

            <a-sub-menu key="order">
              <template #icon>
                <shopping-cart-outlined />
              </template>
              <template #title>订单管理</template>
              <a-menu-item key="order-list">
                <router-link to="/workspace/merchant/order/list">订单列表</router-link>
              </a-menu-item>
            </a-sub-menu>
          </template>
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
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LogoutOutlined,
  DashboardOutlined,
  ShopOutlined,
  CalendarOutlined,
  MessageOutlined,
  BarChartOutlined,
  ShoppingCartOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref(null)

// 选中的菜单项
const selectedKeys = ref(['home'])
const sideMenuKeys = ref([])
const openKeys = ref([])

// 监听路由变化，更新选中的菜单项
watch(() => route.path, (path) => {
  const parts = path.split('/')
  if (parts[1] === 'workspace') {
    const module = parts[2]
    const page = parts[3]
    if (module && page) {
      sideMenuKeys.value = [`${module}-${page}`]
      openKeys.value = [module]
    }
  }
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

const userRole = computed(() => {
  return userInfo.value?.role || ''
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
  position: sticky;
  top: 0;
  z-index: 1000;

  .header-content {
    padding: 0 24px;
    display: flex;
    align-items: center;
    height: 100%;

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
    }
  }
}
</style> 