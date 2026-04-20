<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useUserStore } from '@/stores'
import { Eye, EyeOff, Loader2 } from 'lucide-vue-next'

const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
})
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  if (!form.value.username || !form.value.password) {
    error.value = '请填写用户名和密码'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await userStore.login(form.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '登录失败，请重试'
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
        <h1 class="text-2xl font-bold text-foreground">欢迎回来</h1>
        <p class="text-muted mt-1">登录您的账户继续点餐</p>
      </div>

      <!-- 登录表单 -->
      <div class="card p-6">
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <!-- 错误提示 -->
          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
            {{ error }}
          </div>

          <!-- 用户名 -->
          <div>
            <label class="block text-sm font-medium text-foreground mb-1.5">
              用户名
            </label>
            <input
              v-model="form.username"
              type="text"
              class="input"
              placeholder="请输入用户名"
              autocomplete="username"
            />
          </div>

          <!-- 密码 -->
          <div>
            <label class="block text-sm font-medium text-foreground mb-1.5">
              密码
            </label>
            <div class="relative">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="input pr-10"
                placeholder="请输入密码"
                autocomplete="current-password"
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

          <!-- 登录按钮 -->
          <button
            type="submit"
            :disabled="loading"
            class="btn btn-primary w-full py-2.5"
          >
            <Loader2 v-if="loading" class="w-5 h-5 animate-spin" />
            <span v-else>登录</span>
          </button>
        </form>

        <!-- 注册链接 -->
        <p class="text-center text-sm text-muted mt-6">
          还没有账户？
          <RouterLink to="/register" class="text-primary-600 hover:text-primary-700 font-medium">
            立即注册
          </RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>
