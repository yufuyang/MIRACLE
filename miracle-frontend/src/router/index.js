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
        meta: { title: '总览' }
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
            meta: { title: '产品列表' }
          },
          {
            path: 'detail/:id',
            name: 'CompanyProductDetail',
            component: () => import('@/views/company/product/detail.vue'),
            meta: { title: '产品详情' }
          },
          {
            path: 'category',
            name: 'CompanyProductCategory',
            component: () => import('@/views/company/product/category.vue'),
            meta: { title: '产品分类' }
          }
        ]
      },
      {
        path: 'activity/list',
        name: 'ActivityList',
        component: () => import('@/views/workspace/activity/list.vue'),
        meta: { title: '活动列表' }
      },
      {
        path: 'activity/detail/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/workspace/activity/detail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'activity/edit/:id',
        name: 'ActivityEdit',
        component: () => import('@/views/workspace/activity/components/ActivityFormModal.vue'),
        meta: { title: '编辑活动' }
      },
      {
        path: 'activity/stats',
        name: 'ActivityStats',
        component: () => import('@/views/workspace/activity/stats.vue'),
        meta: { title: '活动统计' }
      },
      {
        path: 'inquiry',
        name: 'WorkspaceInquiry',
        redirect: 'inquiry/list',
        children: [
          {
            path: 'list',
            name: 'InquiryList',
            component: () => import('@/views/workspace/inquiry/list.vue'),
            meta: { title: '询盘列表' }
          },
          {
            path: 'stats',
            name: 'InquiryStats',
            component: () => import('@/views/workspace/inquiry/stats.vue'),
            meta: { title: '询盘统计' }
          }
        ]
      },
      {
        path: 'order',
        name: 'WorkspaceOrder',
        redirect: 'order/list',
        children: [
          {
            path: 'list',
            name: 'OrderList',
            component: () => import('@/views/workspace/order/list.vue'),
            meta: { title: '订单列表' }
          },
          {
            path: 'stats',
            name: 'OrderStats',
            component: () => import('@/views/workspace/order/stats.vue'),
            meta: { title: '订单统计' }
          }
        ]
      },
      {
        path: 'cooperation',
        name: 'WorkspaceCooperation',
        redirect: 'cooperation/list',
        children: [
          {
            path: 'list',
            name: 'CooperationList',
            component: () => import('@/views/workspace/cooperation/list.vue'),
            meta: { title: '合作列表' }
          },
          {
            path: 'stats',
            name: 'CooperationStats',
            component: () => import('@/views/workspace/cooperation/stats.vue'),
            meta: { title: '合作统计' }
          }
        ]
      },
      {
        path: 'stats',
        name: 'WorkspaceStats',
        children: [
          {
            path: 'product',
            name: 'ProductStats',
            component: () => import('@/views/workspace/stats/product.vue'),
            meta: { title: '产品统计' }
          },
          {
            path: 'activity',
            name: 'WorkspaceActivityStats',
            component: () => import('@/views/workspace/stats/activity.vue'),
            meta: { title: '活动统计' }
          }
        ]
      },
      {
        path: 'profile',
        name: 'WorkspaceProfile',
        component: () => import('@/views/workspace/profile/index.vue'),
        meta: { title: '个人资料' }
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