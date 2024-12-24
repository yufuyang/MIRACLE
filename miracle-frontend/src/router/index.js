import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'
import EmptyLayout from '@/layout/empty.vue'

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
        component: () => import('@/views/activity/index.vue'),
        meta: { title: '活动' }
      },
      {
        path: 'activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/activity/detail.vue'),
        meta: { title: '活动详情' }
      }
    ]
  },
  {
    path: '/workspace',
    component: Layout,
    meta: { requiresAuth: true, showSidebar: true },
    children: [
      {
        path: '',
        name: 'Workspace',
        redirect: '/workspace/stats/overview'
      },
      {
        path: 'stats/overview',
        name: 'StatsOverview',
        component: () => import('@/views/workspace/stats/overview.vue'),
        meta: {
          title: '总览'
        }
      },
      {
        path: 'product',
        name: 'CompanyProduct',
        redirect: 'product/list',
        children: [
          {
            path: 'list',
            name: 'CompanyProductList',
            component: () => import('@/views/company/product/list.vue'),
            meta: {
              title: '产品列表'
            }
          },
          {
            path: 'detail/:id',
            name: 'CompanyProductDetail',
            component: () => import('@/views/company/product/detail.vue'),
            meta: {
              title: '产品详情'
            }
          },
          {
            path: 'category',
            name: 'CompanyProductCategory',
            component: () => import('@/views/company/product/category.vue'),
            meta: {
              title: '产品分类'
            }
          }
        ]
      }
    ]
  },
  {
    path: '/company',
    component: Layout,
    meta: { requiresAuth: true, showSidebar: true },
    children: [
      {
        path: 'product/list',
        redirect: '/workspace/product/list'
      },
      {
        path: 'product/category',
        redirect: '/workspace/product/category'
      },
      {
        path: 'activity/list',
        redirect: '/workspace/activity/list'
      },
      {
        path: 'activity/stats',
        redirect: '/workspace/activity/stats'
      },
      {
        path: 'inquiry/list',
        redirect: '/workspace/inquiry/list'
      },
      {
        path: 'inquiry/stats',
        redirect: '/workspace/inquiry/stats'
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
    return
  }
  
  next()
})

export default router 