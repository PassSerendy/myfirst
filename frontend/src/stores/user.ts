import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginDTO, UserDTO } from '@/types'
import { userApi } from '@/api'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isMerchant = computed(() => user.value?.role === 'MERCHANT')
  const isUser = computed(() => user.value?.role === 'USER')

  async function login(data: LoginDTO) {
    try {
      const res = await userApi.login(data)
      token.value = res.token
      user.value = res.user
      localStorage.setItem('token', res.token)
      
      // 根据角色跳转
      if (res.user.role === 'ADMIN') {
        router.push('/admin')
      } else if (res.user.role === 'MERCHANT') {
        router.push('/merchant')
      } else {
        router.push('/')
      }
      return res
    } catch (error) {
      throw error
    }
  }

  async function register(data: UserDTO) {
    try {
      const res = await userApi.register(data)
      return res
    } catch (error) {
      throw error
    }
  }

  async function fetchCurrentUser() {
    if (!token.value) return null
    try {
      const res = await userApi.getCurrentUser()
      user.value = res
      return res
    } catch (error) {
      logout()
      throw error
    }
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    isMerchant,
    isUser,
    login,
    register,
    fetchCurrentUser,
    logout,
  }
})
