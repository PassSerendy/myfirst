<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api'
import type { Order, OrderStatus } from '@/types'
import { OrderStatusMap, OrderStatusColor } from '@/types'
import { Search, Eye, Loader2, X, MapPin, Phone } from 'lucide-vue-next'

const orders = ref<Order[]>([])
const loading = ref(true)
const activeTab = ref<string>('')

const showDetailModal = ref(false)
const selectedOrder = ref<Order | null>(null)
const updating = ref(false)

const tabs = [
  { value: '', label: '全部' },
  { value: 'PAID', label: '待确认' },
  { value: 'CONFIRMED', label: '已确认' },
  { value: 'PREPARING', label: '制作中' },
  { value: 'DELIVERING', label: '配送中' },
  { value: 'COMPLETED', label: '已完成' },
]

const statusFlow: Record<string, string> = {
  PAID: 'CONFIRMED',
  CONFIRMED: 'PREPARING',
  PREPARING: 'DELIVERING',
  DELIVERING: 'COMPLETED',
}

const statusActions: Record<string, string> = {
  PAID: '确认订单',
  CONFIRMED: '开始制作',
  PREPARING: '开始配送',
  DELIVERING: '完成订单',
}

onMounted(() => {
  fetchOrders()
})

async function fetchOrders() {
  loading.value = true
  try {
    const res = await orderApi.getMerchantOrders(1, 50, activeTab.value || undefined)
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

function openDetail(order: Order) {
  selectedOrder.value = order
  showDetailModal.value = true
}

function closeDetail() {
  showDetailModal.value = false
  selectedOrder.value = null
}

async function updateOrderStatus(order: Order) {
  const nextStatus = statusFlow[order.status]
  if (!nextStatus) return
  
  updating.value = true
  try {
    await orderApi.updateStatus(order.id, nextStatus)
    order.status = nextStatus as OrderStatus
    // 如果在详情弹窗中更新，同时更新选中的订单
    if (selectedOrder.value?.id === order.id) {
      selectedOrder.value.status = nextStatus as OrderStatus
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    alert('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

function formatDate(dateStr?: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<template>
  <div>
    <!-- 标题 -->
    <h1 class="text-2xl font-bold text-foreground mb-6">订单管理</h1>

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
    <div v-else-if="orders.length > 0" class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-neutral-50 border-b border-border">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">订单号</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">下单时间</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">商品</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">金额</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">状态</th>
              <th class="px-4 py-3 text-right text-sm font-medium text-muted">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-border">
            <tr v-for="order in orders" :key="order.id" class="hover:bg-neutral-50">
              <td class="px-4 py-3">
                <span class="font-mono text-sm">{{ order.orderNo?.slice(-8) }}</span>
              </td>
              <td class="px-4 py-3 text-sm text-muted">
                {{ formatDate(order.createTime) }}
              </td>
              <td class="px-4 py-3">
                <p class="text-sm text-foreground line-clamp-1">
                  {{ order.items?.map(i => i.dishName).join('、') }}
                </p>
                <p class="text-xs text-muted">
                  共 {{ order.items?.reduce((sum, i) => sum + i.quantity, 0) }} 件
                </p>
              </td>
              <td class="px-4 py-3 font-medium text-foreground">
                ¥{{ order.totalAmount?.toFixed(2) }}
              </td>
              <td class="px-4 py-3">
                <span :class="['badge', OrderStatusColor[order.status as OrderStatus]]">
                  {{ OrderStatusMap[order.status as OrderStatus] }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center justify-end gap-2">
                  <button
                    @click="openDetail(order)"
                    class="p-2 rounded-lg text-muted hover:text-primary-600 hover:bg-primary-50"
                    title="查看详情"
                  >
                    <Eye class="w-4 h-4" />
                  </button>
                  <button
                    v-if="statusActions[order.status]"
                    @click="updateOrderStatus(order)"
                    :disabled="updating"
                    class="btn btn-primary btn-sm"
                  >
                    {{ statusActions[order.status] }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="card p-12 text-center">
      <p class="text-muted">暂无订单</p>
    </div>

    <!-- 订单详情弹窗 -->
    <div v-if="showDetailModal && selectedOrder" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/50" @click="closeDetail" />
      <div class="relative bg-white rounded-xl w-full max-w-2xl p-6 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold">订单详情</h3>
          <button @click="closeDetail" class="p-1 rounded-lg hover:bg-neutral-100">
            <X class="w-5 h-5" />
          </button>
        </div>

        <!-- 订单状态 -->
        <div class="flex items-center justify-between mb-4 p-4 bg-neutral-50 rounded-lg">
          <div>
            <p class="text-sm text-muted">订单号: {{ selectedOrder.orderNo }}</p>
            <p class="text-sm text-muted">{{ formatDate(selectedOrder.createTime) }}</p>
          </div>
          <span :class="['badge text-base px-4 py-1', OrderStatusColor[selectedOrder.status as OrderStatus]]">
            {{ OrderStatusMap[selectedOrder.status as OrderStatus] }}
          </span>
        </div>

        <!-- 收货信息 -->
        <div v-if="selectedOrder.address" class="mb-4 p-4 border border-border rounded-lg">
          <h4 class="font-medium text-foreground mb-2">收货信息</h4>
          <div class="space-y-1 text-sm">
            <div class="flex items-center gap-2">
              <span class="font-medium">{{ selectedOrder.address.contactName }}</span>
              <span class="text-muted flex items-center gap-1">
                <Phone class="w-4 h-4" />
                {{ selectedOrder.address.contactPhone }}
              </span>
            </div>
            <div class="flex items-start gap-2 text-muted">
              <MapPin class="w-4 h-4 mt-0.5 flex-shrink-0" />
              <span>
                {{ selectedOrder.address.province }}{{ selectedOrder.address.city }}{{ selectedOrder.address.district }}{{ selectedOrder.address.detail }}
              </span>
            </div>
          </div>
        </div>

        <!-- 订单商品 -->
        <div class="mb-4 p-4 border border-border rounded-lg">
          <h4 class="font-medium text-foreground mb-3">订单商品</h4>
          <div class="space-y-3">
            <div
              v-for="item in selectedOrder.items"
              :key="item.id"
              class="flex items-center justify-between"
            >
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0">
                  <img
                    v-if="item.dishImage"
                    :src="item.dishImage"
                    :alt="item.dishName"
                    class="w-full h-full object-cover"
                  />
                </div>
                <div>
                  <p class="font-medium text-foreground">{{ item.dishName }}</p>
                  <p class="text-sm text-muted">¥{{ item.price?.toFixed(2) }} x {{ item.quantity }}</p>
                </div>
              </div>
              <span class="font-medium text-foreground">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </span>
            </div>
          </div>
        </div>

        <!-- 备注 -->
        <div v-if="selectedOrder.remark" class="mb-4 p-4 border border-border rounded-lg">
          <h4 class="font-medium text-foreground mb-2">备注</h4>
          <p class="text-sm text-muted">{{ selectedOrder.remark }}</p>
        </div>

        <!-- 金额 -->
        <div class="flex items-center justify-between mb-4 p-4 bg-neutral-50 rounded-lg">
          <span class="font-medium">订单总额</span>
          <span class="text-xl font-bold text-primary-600">¥{{ selectedOrder.totalAmount?.toFixed(2) }}</span>
        </div>

        <!-- 操作按钮 -->
        <div v-if="statusActions[selectedOrder.status]" class="flex justify-end">
          <button
            @click="updateOrderStatus(selectedOrder)"
            :disabled="updating"
            class="btn btn-primary"
          >
            <Loader2 v-if="updating" class="w-5 h-5 animate-spin" />
            <span v-else>{{ statusActions[selectedOrder.status] }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
