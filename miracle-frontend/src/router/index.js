import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
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
          name: 'home',
          component: () => import('@/views/home/index.vue')
        },
        {
          path: 'product',
          name: 'product',
          component: () => import('@/views/product/index.vue')
        },
        {
          path: 'product/:id',
          name: 'productDetail',
          component: () => import('@/views/product/detail.vue')
        },
        {
          path: 'company',
          name: 'company',
          component: () => import('@/views/company/list.vue')
        },
        {
          path: 'company/:id',
          name: 'companyDetail',
          component: () => import('@/views/company/detail.vue')
        },
        {
          path: 'workspace',
          name: 'workspace',
          component: () => import('@/views/workspace/index.vue'),
          meta: { requiresAuth: true }
        }
      ]
    }
  ],
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