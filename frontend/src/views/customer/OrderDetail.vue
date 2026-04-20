<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '@/api'
import type { Order, OrderStatus } from '@/types'
import { OrderStatusMap, OrderStatusColor } from '@/types'
import { ArrowLeft, MapPin, Phone, Loader2 } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

const orderId = Number(route.params.id)
const order = ref<Order | null>(null)
const loading = ref(true)
const paying = ref(false)
const cancelling = ref(false)

onMounted(async () => {
  try {
    order.value = await orderApi.getById(orderId)
  } catch (error) {
    console.error('获取订单详情失败:', error)
  } finally {
    loading.value = false
  }
})

async function payOrder() {
  if (!order.value) return
  paying.value = true
  try {
    await orderApi.pay(order.value.id)
    order.value.status = 'PAID'
  } catch (error) {
    console.error('支付失败:', error)
    alert('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

async function cancelOrder() {
  if (!order.value) return
  if (!confirm('确定要取消订单吗？')) return
  
  cancelling.value = true
  try {
    await orderApi.cancel(order.value.id)
    order.value.status = 'CANCELLED'
  } catch (error) {
    console.error('取消失败:', error)
    alert('取消失败，请重试')
  } finally {
    cancelling.value = false
  }
}

function formatDate(dateStr?: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <div class="max-w-2xl mx-auto">
      <!-- 返回按钮 -->
      <button
        @click="router.back()"
        class="flex items-center gap-2 text-muted hover:text-foreground mb-6"
      >
        <ArrowLeft class="w-5 h-5" />
        <span>返回</span>
      </button>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
      </div>

      <template v-else-if="order">
        <!-- 订单状态 -->
        <div class="card p-6 mb-4 text-center">
          <span :class="['badge text-base px-4 py-1', OrderStatusColor[order.status as OrderStatus]]">
            {{ OrderStatusMap[order.status as OrderStatus] }}
          </span>
          <p class="text-sm text-muted mt-2">订单号: {{ order.orderNo }}</p>
        </div>

        <!-- 收货地址 -->
        <div v-if="order.address" class="card p-4 mb-4">
          <h3 class="font-medium text-foreground mb-3">收货信息</h3>
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <span class="font-medium">{{ order.address.contactName }}</span>
              <span class="text-muted flex items-center gap-1">
                <Phone class="w-4 h-4" />
                {{ order.address.contactPhone }}
              </span>
            </div>
            <div class="flex items-start gap-2 text-muted">
              <MapPin class="w-4 h-4 mt-0.5 flex-shrink-0" />
              <span>
                {{ order.address.province }}{{ order.address.city }}{{ order.address.district }}{{ order.address.detail }}
              </span>
            </div>
          </div>
        </div>

        <!-- 订单商品 -->
        <div class="card p-4 mb-4">
          <h3 class="font-medium text-foreground mb-3">订单商品</h3>
          <div class="space-y-3">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="flex items-center gap-3"
            >
              <div class="w-16 h-16 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0">
                <img
                  v-if="item.dishImage"
                  :src="item.dishImage"
                  :alt="item.dishName"
                  class="w-full h-full object-cover"
                />
                <div v-else class="w-full h-full flex items-center justify-center">
                  {{ item.dishName?.charAt(0) }}
                </div>
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-medium text-foreground">{{ item.dishName }}</p>
                <p class="text-sm text-muted">¥{{ item.price?.toFixed(2) }} x {{ item.quantity }}</p>
              </div>
              <span class="font-medium text-foreground">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </span>
            </div>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="card p-4 mb-4">
          <h3 class="font-medium text-foreground mb-3">订单信息</h3>
          <div class="space-y-2 text-sm">
            <div class="flex justify-between">
              <span class="text-muted">下单时间</span>
              <span class="text-foreground">{{ formatDate(order.createTime) }}</span>
            </div>
            <div v-if="order.remark" class="flex justify-between">
              <span class="text-muted">备注</span>
              <span class="text-foreground">{{ order.remark }}</span>
            </div>
          </div>
        </div>

        <!-- 金额 -->
        <div class="card p-4 mb-4">
          <div class="flex items-center justify-between">
            <span class="text-muted">订单总额</span>
            <span class="text-xl font-bold text-primary-600">¥{{ order.totalAmount?.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div v-if="order.status === 'PENDING'" class="flex gap-3">
          <button
            @click="cancelOrder"
            :disabled="cancelling"
            class="btn btn-outline flex-1"
          >
            <Loader2 v-if="cancelling" class="w-5 h-5 animate-spin" />
            <span v-else>取消订单</span>
          </button>
          <button
            @click="payOrder"
            :disabled="paying"
            class="btn btn-primary flex-1"
          >
            <Loader2 v-if="paying" class="w-5 h-5 animate-spin" />
            <span v-else>立即支付</span>
          </button>
        </div>
      </template>
    </div>
  </div>
</template>
