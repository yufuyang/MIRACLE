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
        component: () => import('@/views/activity/index.vue'),
        meta: { title: '活动' }
      },
      {
        path: 'activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/activity/detail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'workspace',
        name: 'Workspace',
        component: () => import('@/views/workspace/index.vue'),
        meta: { 
          requiresAuth: true,
          title: '工作台'
        }
      },
      {
        path: 'company/product',
        name: 'CompanyProduct',
        component: () => import('@/layout/empty.vue'),
        meta: {
          requiresAuth: true
        },
        children: [
          {
            path: 'list',
            name: 'CompanyProductList',
            component: () => import('@/views/company/product/list.vue'),
            meta: {
              requiresAuth: true,
              title: '产品列表'
            }
          },
          {
            path: 'category',
            name: 'CompanyProductCategory',
            component: () => import('@/views/company/product/category.vue'),
            meta: {
              requiresAuth: true,
              title: '产品分类'
            }
          }
        ]
      },
      {
        path: 'company/activity',
        name: 'CompanyActivity',
        component: () => import('@/layout/empty.vue'),
        meta: {
          requiresAuth: true
        },
        children: [
          {
            path: 'list',
            name: 'CompanyActivityList',
            component: () => import('@/views/company/activity/list.vue'),
            meta: {
              requiresAuth: true,
              title: '活动列表'
            }
          },
          {
            path: 'stats',
            name: 'CompanyActivityStats',
            component: () => import('@/views/company/activity/stats.vue'),
            meta: {
              requiresAuth: true,
              title: '活动统计'
            }
          }
        ]
      },
      {
        path: 'company/inquiry',
        name: 'CompanyInquiry',
        component: () => import('@/layout/empty.vue'),
        meta: {
          requiresAuth: true
        },
        children: [
          {
            path: 'list',
            name: 'CompanyInquiryList',
            component: () => import('@/views/company/inquiry/list.vue'),
            meta: {
              requiresAuth: true,
              title: '意向列表'
            }
          },
          {
            path: 'stats',
            name: 'CompanyInquiryStats',
            component: () => import('@/views/company/inquiry/stats.vue'),
            meta: {
              requiresAuth: true,
              title: '意向统计'
            }
          }
        ]
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