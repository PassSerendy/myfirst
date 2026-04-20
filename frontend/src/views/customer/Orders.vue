<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { orderApi } from '@/api'
import type { Order, OrderStatus } from '@/types'
import { OrderStatusMap, OrderStatusColor } from '@/types'
import { ClipboardList, ChevronRight, Loader2 } from 'lucide-vue-next'

const orders = ref<Order[]>([])
const loading = ref(true)
const activeTab = ref<string>('')

const tabs = [
  { value: '', label: '全部' },
  { value: 'PENDING', label: '待支付' },
  { value: 'PAID', label: '待确认' },
  { value: 'PREPARING', label: '制作中' },
  { value: 'DELIVERING', label: '配送中' },
  { value: 'COMPLETED', label: '已完成' },
]

onMounted(() => {
  fetchOrders()
})

async function fetchOrders() {
  loading.value = true
  try {
    const res = await orderApi.getUserOrders(1, 50, activeTab.value || undefined)
    orders.value = res.records || []
  } catch (error) {
    console.error('获取订单失败:', error)
  } finally {
    loading.value = false
  }
}

function changeTab(tab: string) {
  activeTab.value = tab
  fetchOrders()
}

function formatDate(dateStr?: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <div class="max-w-3xl mx-auto">
      <h1 class="text-2xl font-bold text-foreground mb-6">我的订单</h1>

      <!-- 标签页 -->
      <div class="flex gap-2 mb-6 overflow-x-auto pb-2">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          @click="changeTab(tab.value)"
          :class="[
            'px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors',
            activeTab === tab.value
              ? 'bg-primary-500 text-white'
              : 'bg-neutral-100 text-muted hover:bg-neutral-200'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
      </div>

      <!-- 订单列表 -->
      <div v-else-if="orders.length > 0" class="space-y-4">
        <RouterLink
          v-for="order in orders"
          :key="order.id"
          :to="`/order/${order.id}`"
          class="card p-4 block hover:shadow-md transition-shadow"
        >
          <div class="flex items-center justify-between mb-3">
            <span class="text-sm text-muted">{{ formatDate(order.createTime) }}</span>
            <span :class="['badge', OrderStatusColor[order.status as OrderStatus]]">
              {{ OrderStatusMap[order.status as OrderStatus] }}
            </span>
          </div>

          <!-- 订单商品 -->
          <div class="flex items-center gap-3 mb-3">
            <div class="flex -space-x-2">
              <div
                v-for="(item, index) in (order.items || []).slice(0, 3)"
                :key="index"
                class="w-12 h-12 rounded-lg overflow-hidden bg-neutral-100 border-2 border-white"
              >
                <img
                  v-if="item.dishImage"
                  :src="item.dishImage"
                  :alt="item.dishName"
                  class="w-full h-full object-cover"
                />
                <div v-else class="w-full h-full flex items-center justify-center text-xs">
                  {{ item.dishName?.charAt(0) }}
                </div>
              </div>
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium text-foreground truncate">
                {{ order.items?.map(i => i.dishName).join('、') }}
              </p>
              <p class="text-sm text-muted">
                共 {{ order.items?.reduce((sum, i) => sum + i.quantity, 0) }} 件商品
              </p>
            </div>
            <ChevronRight class="w-5 h-5 text-muted flex-shrink-0" />
          </div>

          <div class="flex items-center justify-between pt-3 border-t border-border">
            <span class="text-sm text-muted">订单号: {{ order.orderNo }}</span>
            <span class="font-bold text-foreground">¥{{ order.totalAmount?.toFixed(2) }}</span>
          </div>
        </RouterLink>
      </div>

      <!-- 空状态 -->
      <div v-else class="card p-12 text-center">
        <div class="w-20 h-20 bg-neutral-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <ClipboardList class="w-8 h-8 text-muted" />
        </div>
        <h3 class="text-lg font-medium text-foreground mb-2">暂无订单</h3>
        <p class="text-muted">快去下单吧</p>
      </div>
    </div>
  </div>
</template>
