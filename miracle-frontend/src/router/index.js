import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/register/index.vue')
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/index.vue')
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/product/index.vue')
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/product/detail.vue')
      },
      {
        path: 'company',
        name: 'Company',
        component: () => import('@/views/company/list.vue')
      },
      {
        path: 'company/:id',
        name: 'CompanyDetail',
        component: () => import('@/views/company/detail.vue')
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('@/views/activity/index.vue')
      },
      {
        path: 'activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/activity/detail.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 始终滚动到顶部
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (!token && to.meta.requiresAuth) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router 