<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userApi } from '@/api'
import type { User } from '@/types'
import { Search, UserCheck, UserX, Loader2 } from 'lucide-vue-next'

const users = ref<User[]>([])
const loading = ref(true)
const searchKeyword = ref('')
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 10

onMounted(() => {
  fetchUsers()
})

async function fetchUsers() {
  loading.value = true
  try {
    const res = await userApi.getAllUsers(currentPage.value, pageSize)
    users.value = res.records || []
    totalPages.value = res.pages || 1
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function toggleUserStatus(user: User) {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await userApi.updateUserStatus(user.id, newStatus)
    user.status = newStatus
  } catch (error) {
    console.error('更新状态失败:', error)
    alert('更新失败，请重试')
  }
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const filteredUsers = () => {
  if (!searchKeyword.value) return users.value
  const keyword = searchKeyword.value.toLowerCase()
  return users.value.filter(
    u => u.username.toLowerCase().includes(keyword) ||
         u.phone?.toLowerCase().includes(keyword) ||
         u.email?.toLowerCase().includes(keyword)
  )
}

const roleMap: Record<string, string> = {
  USER: '普通用户',
  MERCHANT: '商家',
  ADMIN: '管理员',
}
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold text-foreground mb-6">用户管理</h1>

    <!-- 搜索栏 -->
    <div class="card p-4 mb-6">
      <div class="relative max-w-md">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" />
        <input
          v-model="searchKeyword"
          type="text"
          class="input pl-10"
          placeholder="搜索用户名、手机号或邮箱..."
        />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <!-- 用户列表 -->
    <div v-else-if="filteredUsers().length > 0" class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-neutral-50 border-b border-border">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">用户</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">手机号</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">角色</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">注册时间</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">状态</th>
              <th class="px-4 py-3 text-right text-sm font-medium text-muted">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-border">
            <tr v-for="user in filteredUsers()" :key="user.id" class="hover:bg-neutral-50">
              <td class="px-4 py-3">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
                    <span class="text-primary-600 font-medium">
                      {{ user.username.charAt(0).toUpperCase() }}
                    </span>
                  </div>
                  <div>
                    <p class="font-medium text-foreground">{{ user.username }}</p>
                    <p class="text-sm text-muted">{{ user.email || '-' }}</p>
                  </div>
                </div>
              </td>
              <td class="px-4 py-3 text-muted">{{ user.phone || '-' }}</td>
              <td class="px-4 py-3">
                <span :class="[
                  'badge',
                  user.role === 'ADMIN' ? 'badge-danger' :
                  user.role === 'MERCHANT' ? 'badge-warning' : 'badge-info'
                ]">
                  {{ roleMap[user.role] }}
                </span>
              </td>
              <td class="px-4 py-3 text-muted">{{ formatDate(user.createTime) }}</td>
              <td class="px-4 py-3">
                <span :class="['badge', user.status === 1 ? 'badge-success' : 'badge-danger']">
                  {{ user.status === 1 ? '正常' : '禁用' }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center justify-end">
                  <button
                    v-if="user.role !== 'ADMIN'"
                    @click="toggleUserStatus(user)"
                    :class="[
                      'flex items-center gap-1 px-3 py-1.5 rounded-lg text-sm font-medium transition-colors',
                      user.status === 1
                        ? 'text-red-600 hover:bg-red-50'
                        : 'text-green-600 hover:bg-green-50'
                    ]"
                  >
                    <UserX v-if="user.status === 1" class="w-4 h-4" />
                    <UserCheck v-else class="w-4 h-4" />
                    <span>{{ user.status === 1 ? '禁用' : '启用' }}</span>
                  </button>
                  <span v-else class="text-sm text-muted">-</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="p-4 border-t border-border flex items-center justify-center gap-2">
        <button
          @click="currentPage--; fetchUsers()"
          :disabled="currentPage === 1"
          class="btn btn-outline btn-sm"
        >
          上一页
        </button>
        <span class="text-sm text-muted px-4">{{ currentPage }} / {{ totalPages }}</span>
        <button
          @click="currentPage++; fetchUsers()"
          :disabled="currentPage === totalPages"
          class="btn btn-outline btn-sm"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="card p-12 text-center">
      <p class="text-muted">暂无用户</p>
    </div>
  </div>
</template>
