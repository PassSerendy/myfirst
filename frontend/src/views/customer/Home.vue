<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { merchantApi } from '@/api'
import type { Merchant } from '@/types'
import { Search, MapPin, Star, Clock, Loader2 } from 'lucide-vue-next'

const merchants = ref<Merchant[]>([])
const loading = ref(true)
const searchKeyword = ref('')

onMounted(async () => {
  try {
    const res = await merchantApi.getList(1, 20)
    merchants.value = res.records || []
  } catch (error) {
    console.error('获取商家列表失败:', error)
  } finally {
    loading.value = false
  }
})

const filteredMerchants = () => {
  if (!searchKeyword.value) return merchants.value
  const keyword = searchKeyword.value.toLowerCase()
  return merchants.value.filter(
    m => m.name.toLowerCase().includes(keyword) || 
         m.description?.toLowerCase().includes(keyword)
  )
}
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <!-- 搜索栏 -->
    <div class="mb-8">
      <div class="max-w-2xl mx-auto">
        <div class="relative">
          <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" />
          <input
            v-model="searchKeyword"
            type="text"
            class="w-full pl-12 pr-4 py-3 border border-border rounded-xl bg-white text-foreground placeholder:text-muted focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-shadow"
            placeholder="搜索餐厅或菜品..."
          />
        </div>
      </div>
    </div>

    <!-- Banner -->
    <div class="mb-8 bg-gradient-to-r from-primary-500 to-primary-600 rounded-2xl p-8 text-white">
      <h1 class="text-3xl font-bold mb-2">美食，就在指尖</h1>
      <p class="text-primary-100 mb-4">发现周边美食，享受便捷点餐体验</p>
      <div class="flex gap-4">
        <div class="flex items-center gap-2 text-sm">
          <Clock class="w-4 h-4" />
          <span>30分钟送达</span>
        </div>
        <div class="flex items-center gap-2 text-sm">
          <Star class="w-4 h-4" />
          <span>品质保证</span>
        </div>
      </div>
    </div>

    <!-- 商家列表 -->
    <div>
      <h2 class="text-xl font-semibold text-foreground mb-4">附近餐厅</h2>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
      </div>

      <!-- 商家网格 -->
      <div v-else-if="filteredMerchants().length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        <RouterLink
          v-for="merchant in filteredMerchants()"
          :key="merchant.id"
          :to="`/merchant/${merchant.id}`"
          class="card overflow-hidden hover:shadow-md transition-shadow group"
        >
          <!-- 商家图片 -->
          <div class="aspect-[16/10] bg-neutral-100 relative overflow-hidden">
            <img
              v-if="merchant.logo"
              :src="merchant.logo"
              :alt="merchant.name"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            />
            <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-100 to-primary-200">
              <span class="text-4xl font-bold text-primary-600">{{ merchant.name.charAt(0) }}</span>
            </div>
            <div v-if="merchant.status === 0" class="absolute inset-0 bg-black/50 flex items-center justify-center">
              <span class="text-white font-medium">休息中</span>
            </div>
          </div>

          <!-- 商家信息 -->
          <div class="p-4">
            <h3 class="font-semibold text-foreground mb-1 truncate">{{ merchant.name }}</h3>
            <p class="text-sm text-muted mb-2 line-clamp-1">{{ merchant.description || '暂无简介' }}</p>
            <div class="flex items-center justify-between text-sm">
              <div class="flex items-center gap-1 text-primary-600">
                <Star class="w-4 h-4 fill-current" />
                <span>{{ merchant.rating?.toFixed(1) || '5.0' }}</span>
              </div>
              <div class="flex items-center gap-1 text-muted">
                <MapPin class="w-4 h-4" />
                <span class="truncate max-w-[120px]">{{ merchant.address || '暂无地址' }}</span>
              </div>
            </div>
          </div>
        </RouterLink>
      </div>

      <!-- 空状态 -->
      <div v-else class="text-center py-16">
        <div class="w-20 h-20 bg-neutral-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <Search class="w-8 h-8 text-muted" />
        </div>
        <p class="text-muted">暂无商家</p>
      </div>
    </div>
  </div>
</template>
