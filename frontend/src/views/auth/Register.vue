<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useUserStore } from '@/stores'
import { Eye, EyeOff, Loader2, CheckCircle } from 'lucide-vue-next'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
})
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')
const success = ref(false)

async function handleSubmit() {
  error.value = ''
  
  if (!form.value.username || !form.value.password) {
    error.value = '请填写用户名和密码'
    return
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }
  
  if (form.value.password.length < 6) {
    error.value = '密码长度至少6位'
    return
  }

  loading.value = true

  try {
    await userStore.register({
      username: form.value.username,
      password: form.value.password,
      phone: form.value.phone,
    })
    success.value = true
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-background flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <!-- Logo -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary-500 rounded-2xl flex items-center justify-center mx-auto mb-4">
          <span class="text-white font-bold text-2xl">点</span>
        </div>
        <h1 class="text-2xl font-bold text-foreground">创建账户</h1>
        <p class="text-muted mt-1">注册新账户开始点餐</p>
      </div>

      <!-- 注册表单 -->
      <div class="card p-6">
        <!-- 注册成功提示 -->
        <div v-if="success" class="text-center py-8">
          <CheckCircle class="w-16 h-16 text-green-500 mx-auto mb-4" />
          <h2 class="text-xl font-semibold text-foreground mb-2">注册成功！</h2>
          <p class="text-muted">正在跳转到登录页面...</p>
        </div>

        <form v-else @submit.prevent="handleSubmit" class="space-y-4">
          <!-- 错误提示 -->
          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
            {{ error }}
          </div>

          <!-- 用户名 -->
          <div>
            <label class="block text-sm font-medium text-foreground mb-1.5">
              用户名 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.username"
              type="text"
              class="input"
              placeholder="请输入用户名"
              autocomplete="username"
            />
          </div>

          <!-- 手机号 -->
          <div>
            <label class="block text-sm font-medium text-foreground mb-1.5">
              手机号
            </label>
            <input
              v-model="form.phone"
              type="tel"
              class="input"
              placeholder="请输入手机号（选填）"
              autocomplete="tel"
            />
          </div>

          <!-- 密码 -->
          <div>
            <label class="block text-sm font-medium text-foreground mb-1.5">
              密码 <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="input pr-10"
                placeholder="请输入密码（至少6位）"
                autocomplete="new-password"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted hover:text-foreground"
              >
                <EyeOff v-if="showPassword" class="w-5 h-5" />
                <Eye v-else class="w-5 h-5" />
              </button>
            </div>
          </div>

          <!-- 确认密码 -->
          <div>
            <label class="block text-sm font-medium text-foreground mb-1.5">
              确认密码 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.confirmPassword"
              :type="showPassword ? 'text' : 'password'"
              class="input"
              placeholder="请再次输入密码"
              autocomplete="new-password"
            />
          </div>

          <!-- 注册按钮 -->
          <button
            type="submit"
            :disabled="loading"
            class="btn btn-primary w-full py-2.5"
          >
            <Loader2 v-if="loading" class="w-5 h-5 animate-spin" />
            <span v-else>注册</span>
          </button>
        </form>

        <!-- 登录链接 -->
        <p v-if="!success" class="text-center text-sm text-muted mt-6">
          已有账户？
          <RouterLink to="/login" class="text-primary-600 hover:text-primary-700 font-medium">
            立即登录
          </RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>
