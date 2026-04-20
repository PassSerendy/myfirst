<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { merchantApi } from '@/api'
import type { Merchant } from '@/types'
import { Search, Eye, EyeOff, Star, MapPin, Loader2 } from 'lucide-vue-next'

const merchants = ref<Merchant[]>([])
const loading = ref(true)
const searchKeyword = ref('')
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 10

onMounted(() => {
  fetchMerchants()
})

async function fetchMerchants() {
  loading.value = true
  try {
    const res = await merchantApi.getList(currentPage.value, pageSize)
    merchants.value = res.records || []
    totalPages.value = res.pages || 1
  } catch (error) {
    console.error('获取商家列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function toggleMerchantStatus(merchant: Merchant) {
  const newStatus = merchant.status === 1 ? 0 : 1
  try {
    await merchantApi.updateStatus(merchant.id, newStatus)
    merchant.status = newStatus
  } catch (error) {
    console.error('更新状态失败:', error)
    alert('更新失败，请重试')
  }
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const filteredMerchants = () => {
  if (!searchKeyword.value) return merchants.value
  const keyword = searchKeyword.value.toLowerCase()
  return merchants.value.filter(
    m => m.name.toLowerCase().includes(keyword) ||
         m.address?.toLowerCase().includes(keyword)
  )
}
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold text-foreground mb-6">商家管理</h1>

    <!-- 搜索栏 -->
    <div class="card p-4 mb-6">
      <div class="relative max-w-md">
        <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" />
        <input
          v-model="searchKeyword"
          type="text"
          class="input pl-10"
          placeholder="搜索商家名称或地址..."
        />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <!-- 商家列表 -->
    <div v-else-if="filteredMerchants().length > 0" class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-neutral-50 border-b border-border">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">商家</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">联系电话</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">评分</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">入驻时间</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">状态</th>
              <th class="px-4 py-3 text-right text-sm font-medium text-muted">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-border">
            <tr v-for="merchant in filteredMerchants()" :key="merchant.id" class="hover:bg-neutral-50">
              <td class="px-4 py-3">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0">
                    <img
                      v-if="merchant.logo"
                      :src="merchant.logo"
                      :alt="merchant.name"
                      class="w-full h-full object-cover"
                    />
                    <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-100 to-primary-200">
                      <span class="text-lg font-bold text-primary-600">{{ merchant.name.charAt(0) }}</span>
                    </div>
                  </div>
                  <div>
                    <p class="font-medium text-foreground">{{ merchant.name }}</p>
                    <p class="text-sm text-muted flex items-center gap-1">
                      <MapPin class="w-3 h-3" />
                      {{ merchant.address || '-' }}
                    </p>
                  </div>
                </div>
              </td>
              <td class="px-4 py-3 text-muted">{{ merchant.phone }}</td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-1 text-primary-600">
                  <Star class="w-4 h-4 fill-current" />
                  <span>{{ merchant.rating?.toFixed(1) || '5.0' }}</span>
                </div>
              </td>
              <td class="px-4 py-3 text-muted">{{ formatDate(merchant.createTime) }}</td>
              <td class="px-4 py-3">
                <span :class="['badge', merchant.status === 1 ? 'badge-success' : 'badge-danger']">
                  {{ merchant.status === 1 ? '营业中' : '已关闭' }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center justify-end">
                  <button
                    @click="toggleMerchantStatus(merchant)"
                    :class="[
                      'flex items-center gap-1 px-3 py-1.5 rounded-lg text-sm font-medium transition-colors',
                      merchant.status === 1
                        ? 'text-red-600 hover:bg-red-50'
                        : 'text-green-600 hover:bg-green-50'
                    ]"
                  >
                    <EyeOff v-if="merchant.status === 1" class="w-4 h-4" />
                    <Eye v-else class="w-4 h-4" />
                    <span>{{ merchant.status === 1 ? '关闭' : '开启' }}</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="p-4 border-t border-border flex items-center justify-center gap-2">
        <button
          @click="currentPage--; fetchMerchants()"
          :disabled="currentPage === 1"
          class="btn btn-outline btn-sm"
        >
          上一页
        </button>
        <span class="text-sm text-muted px-4">{{ currentPage }} / {{ totalPages }}</span>
        <button
          @click="currentPage++; fetchMerchants()"
          :disabled="currentPage === totalPages"
          class="btn btn-outline btn-sm"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="card p-12 text-center">
      <p class="text-muted">暂无商家</p>
    </div>
  </div>
</template>
