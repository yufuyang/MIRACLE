import axios from 'axios'
import { message } from 'ant-design-vue'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/miracle',
  timeout: 10000
})

// 是否正在重定向到登录页面
let isRedirecting = false

const redirectToLogin = () => {
  if (!isRedirecting) {
    isRedirecting = true
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login').finally(() => {
      isRedirecting = false
    })
  }
}

// 请求拦截器
service.interceptors.request.use(
  config => {
    console.log('发起请求:', {
      url: config.url,
      method: config.method,
      data: config.data,
      params: config.params
    })
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log('收到响应:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    })
    const res = response.data
    if (res.code === 401) {
      message.error('登录已过期，请重新登录')
      redirectToLogin()
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error.response || error)
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message.error('登录已过期，请重新登录')
          redirectToLogin()
          break
        case 403:
          message.error('没有权限访问')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error('服务器错误')
          break
        default:
          message.error(error.response.data.message || '请求失败')
      }
    } else {
      message.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default service 