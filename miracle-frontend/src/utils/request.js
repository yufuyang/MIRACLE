import axios from 'axios'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

// 创建axios实例
const service = axios.create({
  baseURL: '/miracle', // 添加/miracle前缀
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果响应成功但业务状态码不是200，显示错误信息
    if (res.code && res.code !== 200) {
      message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('Response error:', error)
    // 处理401未授权的情况
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      const router = useRouter()
      router.push('/login')
    }
    message.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default service 