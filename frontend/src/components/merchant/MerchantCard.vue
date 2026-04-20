<script setup lang="ts">
import { MapPin, Star, Clock } from 'lucide-vue-next'
import type { Merchant } from '@/types'

defineProps<{
  merchant: Merchant
}>()

const getStatusText = (status: number) => {
  return status === 1 ? '营业中' : '休息中'
}

const getStatusClass = (status: number) => {
  return status === 1 
    ? 'bg-green-100 text-green-700' 
    : 'bg-gray-100 text-gray-600'
}
</script>

<template>
  <router-link
    :to="`/merchant/${merchant.id}`"
    class="block bg-card rounded-xl border border-border overflow-hidden hover:shadow-lg transition-shadow"
  >
    <div class="relative h-40">
      <img
        :src="merchant.image || '/placeholder-merchant.jpg'"
        :alt="merchant.name"
        class="w-full h-full object-cover"
      />
      <div
        :class="[
          'absolute top-3 right-3 px-2 py-1 rounded-full text-xs font-medium',
          getStatusClass(merchant.status)
        ]"
      >
        {{ getStatusText(merchant.status) }}
      </div>
    </div>
    <div class="p-4">
      <h3 class="font-semibold text-foreground text-lg truncate">
        {{ merchant.name }}
      </h3>
      <p class="text-sm text-muted-foreground mt-1 line-clamp-1">
        {{ merchant.description || '暂无简介' }}
      </p>
      <div class="flex items-center gap-4 mt-3 text-sm text-muted-foreground">
        <div class="flex items-center gap-1">
          <Star class="h-4 w-4 text-yellow-500 fill-yellow-500" />
          <span>4.8</span>
        </div>
        <div class="flex items-center gap-1">
          <Clock class="h-4 w-4" />
          <span>{{ merchant.businessHours || '9:00-21:00' }}</span>
        </div>
      </div>
      <div class="flex items-center gap-1 mt-2 text-sm text-muted-foreground">
        <MapPin class="h-4 w-4 flex-shrink-0" />
        <span class="truncate">{{ merchant.address || '暂无地址' }}</span>
      </div>
    </div>
  </router-link>
</template>
