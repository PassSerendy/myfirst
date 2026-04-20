<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api'
import type { Order, OrderStatus } from '@/types'
import { OrderStatusMap, OrderStatusColor } from '@/types'
import { Search, Eye, Loader2, X, MapPin, Phone } from 'lucide-vue-next'

const orders = ref<Order[]>([])
const loading = ref(true)
const searchKeyword = ref('')
const filterStatus = ref<string>('')
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 15

const showDetailModal = ref(false)
const selectedOrder = ref<Order | null>(null)

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'PENDING', label: '待支付' },
  { value: 'PAID', label: '已支付' },
  { value: 'CONFIRMED', label: '已确认' },
  { value: 'PREPARING', label: '制作中' },
  { value: 'DELIVERING', label: '配送中' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' },
]

onMounted(() => {
  fetchOrders()
})

async function fetchOrders() {
  loading.value = true
  try {
    const res = await orderApi.getAllOrders(currentPage.value, pageSize, filterStatus.value || undefined)
    orders.value = res.records || []
    totalPages.value = res.pages || 1
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

function openDetail(order: Order) {
  selectedOrder.value = order
  showDetailModal.value = true
}

function closeDetail() {
  showDetailModal.value = false
  selectedOrder.value = null
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function handleFilterChange() {
  currentPage.value = 1
  fetchOrders()
}

const filteredOrders = () => {
  if (!searchKeyword.value) return orders.value
  const keyword = searchKeyword.value.toLowerCase()
  return orders.value.filter(
    o => o.orderNo?.toLowerCase().includes(keyword)
  )
}
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold text-foreground mb-6">订单管理</h1>

    <!-- 搜索和筛选 -->
    <div class="card p-4 mb-6">
      <div class="flex flex-col sm:flex-row gap-4">
        <div class="relative flex-1">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" />
          <input
            v-model="searchKeyword"
            type="text"
            class="input pl-10"
            placeholder="搜索订单号..."
          />
        </div>
        <select
          v-model="filterStatus"
          @change="handleFilterChange"
          class="input w-full sm:w-48"
        >
          <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
            {{ opt.label }}
          </option>
        </select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <!-- 订单列表 -->
    <div v-else-if="filteredOrders().length > 0" class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-neutral-50 border-b border-border">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">订单号</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">下单时间</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">商品数量</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">金额</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">状态</th>
              <th class="px-4 py-3 text-right text-sm font-medium text-muted">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-border">
            <tr v-for="order in filteredOrders()" :key="order.id" class="hover:bg-neutral-50">
              <td class="px-4 py-3">
                <span class="font-mono text-sm">{{ order.orderNo }}</span>
              </td>
              <td class="px-4 py-3 text-sm text-muted">
                {{ formatDate(order.createTime) }}
              </td>
              <td class="px-4 py-3 text-muted">
                {{ order.items?.reduce((sum, i) => sum + i.quantity, 0) || 0 }} 件
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
                <div class="flex items-center justify-end">
                  <button
                    @click="openDetail(order)"
                    class="p-2 rounded-lg text-muted hover:text-primary-600 hover:bg-primary-50"
                    title="查看详情"
                  >
                    <Eye class="w-4 h-4" />
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
          @click="currentPage--; fetchOrders()"
          :disabled="currentPage === 1"
          class="btn btn-outline btn-sm"
        >
          上一页
        </button>
        <span class="text-sm text-muted px-4">{{ currentPage }} / {{ totalPages }}</span>
        <button
          @click="currentPage++; fetchOrders()"
          :disabled="currentPage === totalPages"
          class="btn btn-outline btn-sm"
        >
          下一页
        </button>
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
        <div class="flex items-center justify-between p-4 bg-neutral-50 rounded-lg">
          <span class="font-medium">订单总额</span>
          <span class="text-xl font-bold text-primary-600">¥{{ selectedOrder.totalAmount?.toFixed(2) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
