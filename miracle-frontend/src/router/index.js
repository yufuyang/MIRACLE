import { createRouter, createWebHistory } from 'vue-router'
import WebsiteLayout from '@/layout/website/index.vue'
import WorkspaceLayout from '@/layout/workspace/index.vue'
import EmptyLayout from '@/layout/empty.vue'
import { useUserStore } from '@/stores/user'

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
      // 企业用户路由
      {
        path: 'profile',
        component: () => import('@/views/workspace/company/profile/index.vue'),
        meta: { title: '企业资料' }
      },
      {
        path: 'product',
        redirect: 'product/list',
        children: [
          {
            path: 'list',
            component: () => import('@/views/workspace/company/product/list.vue'),
            meta: { title: '产品列表' }
          },
          {
            path: 'detail/:id',
            component: () => import('@/views/workspace/company/product/detail.vue'),
            meta: { title: '产品详情' }
          },
          {
            path: 'category',
            component: () => import('@/views/workspace/company/product/category.vue'),
            meta: { title: '产品分类' }
          },
          {
            path: 'statistics',
            component: () => import('@/views/workspace/company/product/statistics.vue'),
            meta: { title: '产品统计' }
          }
        ]
      },
      {
        path: 'activity',
        children: [
          {
            path: 'list',
            component: () => import('@/views/workspace/company/activity/list.vue'),
            meta: { title: '活动列表' }
          },
          {
            path: 'detail/:id',
            component: () => import('@/views/workspace/company/activity/detail.vue'),
            meta: { title: '活动详情' }
          },
          {
            path: 'edit/:id',
            component: () => import('@/views/workspace/company/activity/edit.vue'),
            meta: { title: '编辑活动' }
          },
          {
            path: 'stats',
            component: () => import('@/views/workspace/company/activity/stats.vue'),
            meta: { title: '活动统计' }
          }
        ]
      },
      {
        path: 'inquiry',
        redirect: 'inquiry/list',
        children: [
          {
            path: 'list',
            component: () => import('@/views/workspace/company/inquiry/list.vue'),
            meta: { title: '意向列表' }
          }
        ]
      },
      {
        path: 'cooperation',
        children: [
          {
            path: 'list',
            component: () => import('@/views/workspace/company/cooperation/list.vue'),
            meta: { title: '合作列表' }
          }
        ]
      },
      {
        path: 'order',
        children: [
          {
            path: 'list',
            component: () => import('@/views/workspace/company/order/list.vue'),
            meta: { title: '订单列表' }
          },
          {
            path: 'detail/:id',
            component: () => import('@/views/workspace/company/order/detail.vue'),
            meta: { title: '订单详情' }
          }
        ]
      },
      // 商户用户路由
      {
        path: 'merchant',
        children: [
          {
            path: 'home',
            component: () => import('@/views/workspace/merchant/home/index.vue'),
            meta: { title: '商户首页' }
          },
          {
            path: 'activity',
            children: [
              {
                path: 'list',
                component: () => import('@/views/workspace/merchant/activity/list.vue'),
                meta: { title: '活动列表' }
              }
            ]
          },
          {
            path: 'intention',
            children: [
              {
                path: 'list',
                component: () => import('@/views/workspace/merchant/intention/list.vue'),
                meta: { title: '意向列表' }
              }
            ]
          },
          {
            path: 'cooperation',
            children: [
              {
                path: 'list',
                component: () => import('@/views/workspace/merchant/cooperation/list.vue'),
                meta: { title: '合作列表' }
              }
            ]
          },
          {
            path: 'order',
            children: [
              {
                path: 'list',
                component: () => import('@/views/workspace/merchant/order/list.vue'),
                meta: { title: '订单列表' }
              },
              {
                path: 'create',
                component: () => import('@/views/workspace/merchant/order/create.vue'),
                meta: { title: '订单创建' }
              },
              {
                path: ':id',
                component: () => import('@/views/workspace/merchant/order/detail.vue')
              }
            ]
          }
        ]
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
      },
      {
        path: 'order/list',
        redirect: '/workspace/order/list'
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  const token = localStorage.getItem('token')
  console.log('Router guard - userInfo:', userInfo)

  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
      return
    }

    if (userInfo?.role === 'MERCHANT' && !to.path.startsWith('/workspace/merchant')) {
      next('/workspace/merchant/home')
      return
    }

    if (userInfo?.role === 'COMPANY' && to.path === '/workspace') {
      next('/workspace/profile')
      return
    }
  }

  next()
})

export default router 