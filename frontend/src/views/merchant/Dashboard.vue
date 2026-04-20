<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { merchantApi, orderApi, dishApi } from '@/api'
import type { Merchant, Order } from '@/types'
import { OrderStatusMap, OrderStatusColor, type OrderStatus } from '@/types'
import { DollarSign, ShoppingBag, UtensilsCrossed, TrendingUp, Loader2 } from 'lucide-vue-next'

const merchant = ref<Merchant | null>(null)
const recentOrders = ref<Order[]>([])
const stats = ref({
  todayOrders: 0,
  todayRevenue: 0,
  totalDishes: 0,
  pendingOrders: 0,
})
const loading = ref(true)

onMounted(async () => {
  try {
    const [merchantRes, ordersRes, dishesRes] = await Promise.all([
      merchantApi.getCurrentMerchant(),
      orderApi.getMerchantOrders(1, 10),
      dishApi.getByMerchant(0), // 获取当前商家的所有菜品
    ])
    
    merchant.value = merchantRes
    recentOrders.value = ordersRes.records || []
    
    // 计算统计数据
    const today = new Date().toDateString()
    const todayOrders = recentOrders.value.filter(
      o => new Date(o.createTime || '').toDateString() === today
    )
    
    stats.value = {
      todayOrders: todayOrders.length,
      todayRevenue: todayOrders.reduce((sum, o) => sum + (o.totalAmount || 0), 0),
      totalDishes: (dishesRes as unknown[])?.length || 0,
      pendingOrders: recentOrders.value.filter(o => o.status === 'PAID').length,
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
})

function formatDate(dateStr?: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<template>
  <div>
    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <template v-else>
      <!-- 欢迎信息 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-foreground">
          欢迎回来，{{ merchant?.name }}
        </h1>
        <p class="text-muted">这是您的店铺今日概况</p>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="card p-4">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
              <DollarSign class="w-6 h-6 text-green-600" />
            </div>
            <div>
              <p class="text-sm text-muted">今日收入</p>
              <p class="text-2xl font-bold text-foreground">¥{{ stats.todayRevenue.toFixed(2) }}</p>
            </div>
          </div>
        </div>

        <div class="card p-4">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <ShoppingBag class="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <p class="text-sm text-muted">今日订单</p>
              <p class="text-2xl font-bold text-foreground">{{ stats.todayOrders }}</p>
            </div>
          </div>
        </div>

        <div class="card p-4">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center">
              <UtensilsCrossed class="w-6 h-6 text-orange-600" />
            </div>
            <div>
              <p class="text-sm text-muted">菜品总数</p>
              <p class="text-2xl font-bold text-foreground">{{ stats.totalDishes }}</p>
            </div>
          </div>
        </div>

        <div class="card p-4">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center">
              <TrendingUp class="w-6 h-6 text-red-600" />
            </div>
            <div>
              <p class="text-sm text-muted">待处理订单</p>
              <p class="text-2xl font-bold text-foreground">{{ stats.pendingOrders }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近订单 -->
      <div class="card">
        <div class="p-4 border-b border-border">
          <h2 class="font-semibold text-foreground">最近订单</h2>
        </div>
        <div class="divide-y divide-border">
          <div
            v-for="order in recentOrders.slice(0, 5)"
            :key="order.id"
            class="p-4 flex items-center justify-between"
          >
            <div>
              <p class="font-medium text-foreground">订单 #{{ order.orderNo?.slice(-8) }}</p>
              <p class="text-sm text-muted">{{ formatDate(order.createTime) }}</p>
            </div>
            <div class="text-right">
              <p class="font-medium text-foreground">¥{{ order.totalAmount?.toFixed(2) }}</p>
              <span :class="['badge', OrderStatusColor[order.status as OrderStatus]]">
                {{ OrderStatusMap[order.status as OrderStatus] }}
              </span>
            </div>
          </div>
          <div v-if="recentOrders.length === 0" class="p-8 text-center text-muted">
            暂无订单
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
