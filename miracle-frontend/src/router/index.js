import { createRouter, createWebHistory } from 'vue-router'
import WebsiteLayout from '@/layout/website/index.vue'
import WorkspaceLayout from '@/layout/workspace/index.vue'
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
  // 前台路由
  {
    path: '/',
    component: WebsiteLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/website/home/index.vue')
      },
      {
        path: 'product',
        name: 'Product',
        component: EmptyLayout,
        children: [
          {
            path: '',
            name: 'ProductList',
            component: () => import('@/views/website/product/list.vue'),
            meta: { title: '产品列表' }
          },
          {
            path: ':id',
            name: 'ProductDetail',
            component: () => import('@/views/website/product/detail.vue'),
            meta: { title: '产品详情' }
          }
        ]
      },
      {
        path: 'company',
        name: 'Company',
        component: EmptyLayout,
        children: [
          {
            path: '',
            name: 'CompanyList',
            component: () => import('@/views/website/company/list.vue')
          },
          {
            path: ':id',
            name: 'CompanyDetail',
            component: () => import('@/views/website/company/detail.vue')
          }
        ]
      },
      {
        path: 'activity',
        name: 'WebsiteActivity',
        component: EmptyLayout,
        children: [
          {
            path: '',
            name: 'WebsiteActivityList',
            component: () => import('@/views/website/activity/list.vue'),
            meta: { title: '活动列表' }
          },
          {
            path: ':id',
            name: 'WebsiteActivityDetail',
            component: () => import('@/views/website/activity/detail.vue'),
            meta: { title: '活动详情' }
          }
        ]
      }
    ]
  },
  // 工作台
  {
    path: '/workspace',
    component: WorkspaceLayout,
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
            component: () => import('@/views/workspace/company/product/list.vue'),
            meta: { title: '产品列表' }
          },
          {
            path: 'detail/:id',
            name: 'CompanyProductDetail',
            component: () => import('@/views/workspace/company/product/detail.vue'),
            meta: { title: '产品详情' }
          },
          {
            path: 'category',
            name: 'CompanyProductCategory',
            component: () => import('@/views/workspace/company/product/category.vue'),
            meta: { title: '产品分类' }
          },
          {
            path: 'statistics',
            name: 'ProductStatistics',
            component: () => import('@/views/workspace/company/product/statistics.vue'),
            meta: { title: '产品统计' }
          }
        ]
      },
      {
        path: 'activity/list',
        name: 'WorkspaceActivityList',
        component: () => import('@/views/workspace/company/activity/list.vue'),
        meta: { title: '活动列表' }
      },
      {
        path: 'activity/detail/:id',
        name: 'WorkspaceActivityDetail',
        component: () => import('@/views/workspace/company/activity/detail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'activity/edit/:id',
        name: 'ActivityEdit',
        component: () => import('@/views/workspace/company/activity/edit.vue'),
        meta: { title: '编辑活动', showSidebar: true }
      },
      {
        path: 'activity/stats',
        name: 'ActivityStats',
        component: () => import('@/views/workspace/company/activity/stats.vue'),
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
    component: WebsiteLayout,
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