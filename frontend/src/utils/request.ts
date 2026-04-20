import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { ApiResponse } from '@/types'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response
    if (data.code === 200 || data.code === 0) {
      return data.data as unknown as AxiosResponse
    }
    // 业务错误
    return Promise.reject(new Error(data.message || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    }
    return Promise.reject(error.response?.data?.message || error.message || '网络错误')
  }
)

// 封装请求方法
export const request = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return instance.get(url, config) as Promise<T>
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.post(url, data, config) as Promise<T>
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.put(url, data, config) as Promise<T>
  },
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return instance.delete(url, config) as Promise<T>
  },
}

export default request
