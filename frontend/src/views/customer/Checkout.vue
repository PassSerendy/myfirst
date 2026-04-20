<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores'
import { addressApi, orderApi } from '@/api'
import type { Address } from '@/types'
import { MapPin, Plus, ChevronRight, Loader2, CheckCircle } from 'lucide-vue-next'

const router = useRouter()
const cartStore = useCartStore()

const addresses = ref<Address[]>([])
const selectedAddressId = ref<number | null>(null)
const remark = ref('')
const loading = ref(true)
const submitting = ref(false)
const showSuccess = ref(false)

const selectedAddress = computed(() => 
  addresses.value.find(a => a.id === selectedAddressId.value)
)

onMounted(async () => {
  try {
    addresses.value = await addressApi.getList()
    const defaultAddr = addresses.value.find(a => a.isDefault === 1)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    } else if (addresses.value.length > 0) {
      selectedAddressId.value = addresses.value[0].id
    }
  } catch (error) {
    console.error('获取地址失败:', error)
  } finally {
    loading.value = false
  }
})

async function submitOrder() {
  if (!selectedAddressId.value) {
    alert('请选择收货地址')
    return
  }

  if (cartStore.items.length === 0) {
    alert('购物车为空')
    return
  }

  submitting.value = true

  try {
    // 按商家分组下单
    const merchantIds = [...new Set(cartStore.items.map(item => item.merchantId))]
    
    for (const merchantId of merchantIds) {
      const items = cartStore.items
        .filter(item => item.merchantId === merchantId)
        .map(item => ({
          dishId: item.dishId,
          quantity: item.quantity,
        }))

      await orderApi.create({
        merchantId,
        addressId: selectedAddressId.value,
        remark: remark.value,
        items,
      })
    }

    await cartStore.clearCart()
    showSuccess.value = true
    
    setTimeout(() => {
      router.push('/orders')
    }, 2000)
  } catch (error) {
    console.error('下单失败:', error)
    alert('下单失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <div class="max-w-2xl mx-auto">
      <h1 class="text-2xl font-bold text-foreground mb-6">确认订单</h1>

      <!-- 下单成功 -->
      <div v-if="showSuccess" class="card p-12 text-center">
        <CheckCircle class="w-16 h-16 text-green-500 mx-auto mb-4" />
        <h2 class="text-xl font-semibold text-foreground mb-2">下单成功！</h2>
        <p class="text-muted">正在跳转到订单页面...</p>
      </div>

      <template v-else>
        <!-- 收货地址 -->
        <div class="card p-4 mb-4">
          <div class="flex items-center justify-between mb-3">
            <h3 class="font-medium text-foreground">收货地址</h3>
            <button
              @click="router.push('/addresses')"
              class="text-sm text-primary-600 hover:text-primary-700 flex items-center gap-1"
            >
              <span>管理地址</span>
              <ChevronRight class="w-4 h-4" />
            </button>
          </div>

          <div v-if="loading" class="flex items-center justify-center py-8">
            <Loader2 class="w-6 h-6 text-primary-500 animate-spin" />
          </div>

          <div v-else-if="addresses.length === 0" class="py-8 text-center">
            <MapPin class="w-12 h-12 text-muted mx-auto mb-2" />
            <p class="text-muted mb-3">暂无收货地址</p>
            <button
              @click="router.push('/addresses')"
              class="btn btn-outline"
            >
              <Plus class="w-4 h-4" />
              <span>添加地址</span>
            </button>
          </div>

          <div v-else class="space-y-2">
            <label
              v-for="addr in addresses"
              :key="addr.id"
              :class="[
                'flex items-start gap-3 p-3 rounded-lg border cursor-pointer transition-colors',
                selectedAddressId === addr.id
                  ? 'border-primary-500 bg-primary-50'
                  : 'border-border hover:border-primary-300'
              ]"
            >
              <input
                type="radio"
                :value="addr.id"
                v-model="selectedAddressId"
                class="mt-1 accent-primary-500"
              />
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span class="font-medium">{{ addr.contactName }}</span>
                  <span class="text-muted">{{ addr.contactPhone }}</span>
                  <span v-if="addr.isDefault === 1" class="badge badge-info">默认</span>
                </div>
                <p class="text-sm text-muted">
                  {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
                </p>
              </div>
            </label>
          </div>
        </div>

        <!-- 订单商品 -->
        <div class="card p-4 mb-4">
          <h3 class="font-medium text-foreground mb-3">订单商品</h3>
          <div class="space-y-3">
            <div
              v-for="item in cartStore.items"
              :key="item.id"
              class="flex items-center gap-3"
            >
              <div class="w-12 h-12 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0">
                <img
                  v-if="item.dish?.image"
                  :src="item.dish.image"
                  :alt="item.dish?.name"
                  class="w-full h-full object-cover"
                />
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-medium text-foreground truncate">{{ item.dish?.name }}</p>
                <p class="text-sm text-muted">x{{ item.quantity }}</p>
              </div>
              <span class="font-medium text-foreground">
                ¥{{ ((item.dish?.price || 0) * item.quantity).toFixed(2) }}
              </span>
            </div>
          </div>
        </div>

        <!-- 备注 -->
        <div class="card p-4 mb-4">
          <h3 class="font-medium text-foreground mb-3">订单备注</h3>
          <textarea
            v-model="remark"
            class="input resize-none"
            rows="2"
            placeholder="请输入备注信息（选填）"
          />
        </div>

        <!-- 结算 -->
        <div class="card p-4">
          <div class="flex items-center justify-between mb-4">
            <span class="text-muted">共 {{ cartStore.totalItems }} 件</span>
            <div>
              <span class="text-muted">合计：</span>
              <span class="text-xl font-bold text-primary-600">
                ¥{{ cartStore.totalAmount.toFixed(2) }}
              </span>
            </div>
          </div>
          <button
            @click="submitOrder"
            :disabled="submitting || !selectedAddressId"
            class="btn btn-primary w-full py-3"
          >
            <Loader2 v-if="submitting" class="w-5 h-5 animate-spin" />
            <span v-else>提交订单</span>
          </button>
        </div>
      </template>
    </div>
  </div>
</template>
