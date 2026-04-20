<script setup lang="ts">
import { ChevronRight } from 'lucide-vue-next'
import type { Order } from '@/types'

defineProps<{
  order: Order
}>()

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '待接单',
    2: '制作中',
    3: '配送中',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知状态'
}

const getStatusClass = (status: number) => {
  const classMap: Record<number, string> = {
    0: 'bg-yellow-100 text-yellow-700',
    1: 'bg-blue-100 text-blue-700',
    2: 'bg-orange-100 text-orange-700',
    3: 'bg-purple-100 text-purple-700',
    4: 'bg-green-100 text-green-700',
    5: 'bg-gray-100 text-gray-600'
  }
  return classMap[status] || 'bg-gray-100 text-gray-600'
}
</script>

<template>
  <router-link
    :to="`/orders/${order.id}`"
    class="block bg-card rounded-xl border border-border p-4 hover:shadow-md transition-shadow"
  >
    <div class="flex items-center justify-between mb-3">
      <span class="text-sm text-muted-foreground">
        订单号：{{ order.id }}
      </span>
      <span
        :class="[
          'px-2 py-1 rounded-full text-xs font-medium',
          getStatusClass(order.status)
        ]"
      >
        {{ getStatusText(order.status) }}
      </span>
    </div>
    
    <div class="flex items-center gap-3 mb-3">
      <img
        src="/placeholder-merchant.jpg"
        alt="商家"
        class="w-12 h-12 rounded-lg object-cover"
      />
      <div class="flex-1 min-w-0">
        <h3 class="font-medium text-foreground truncate">
          商家名称
        </h3>
        <p class="text-sm text-muted-foreground">
          共 {{ order.items?.length || 0 }} 件商品
        </p>
      </div>
      <ChevronRight class="h-5 w-5 text-muted-foreground flex-shrink-0" />
    </div>
    
    <div class="flex items-center justify-between pt-3 border-t border-border">
      <span class="text-sm text-muted-foreground">
        {{ formatDate(order.createTime) }}
      </span>
      <span class="font-semibold text-foreground">
        {{ formatPrice(order.totalPrice) }}
      </span>
    </div>
  </router-link>
</template>
