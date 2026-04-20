<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores'
import { Plus, Minus, Trash2, ShoppingCart, ArrowRight } from 'lucide-vue-next'

const router = useRouter()
const cartStore = useCartStore()

const isEmpty = computed(() => cartStore.items.length === 0)

async function updateQuantity(id: number, quantity: number) {
  if (quantity <= 0) {
    await cartStore.removeItem(id)
  } else {
    await cartStore.updateQuantity(id, quantity)
  }
}

async function removeItem(id: number) {
  await cartStore.removeItem(id)
}

async function clearAll() {
  if (confirm('确定要清空购物车吗？')) {
    await cartStore.clearCart()
  }
}

function goToCheckout() {
  router.push('/checkout')
}
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <div class="max-w-3xl mx-auto">
      <!-- 标题 -->
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-foreground">购物车</h1>
        <button
          v-if="!isEmpty"
          @click="clearAll"
          class="text-sm text-red-500 hover:text-red-600 flex items-center gap-1"
        >
          <Trash2 class="w-4 h-4" />
          <span>清空</span>
        </button>
      </div>

      <!-- 空购物车 -->
      <div v-if="isEmpty" class="card p-12 text-center">
        <div class="w-20 h-20 bg-neutral-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <ShoppingCart class="w-8 h-8 text-muted" />
        </div>
        <h3 class="text-lg font-medium text-foreground mb-2">购物车是空的</h3>
        <p class="text-muted mb-4">快去挑选美食吧</p>
        <button @click="router.push('/')" class="btn btn-primary">
          去逛逛
        </button>
      </div>

      <!-- 购物车列表 -->
      <div v-else class="space-y-4">
        <div
          v-for="item in cartStore.items"
          :key="item.id"
          class="card p-4 flex gap-4"
        >
          <!-- 菜品图片 -->
          <div class="w-20 h-20 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0">
            <img
              v-if="item.dish?.image"
              :src="item.dish.image"
              :alt="item.dish?.name"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center">
              <span class="text-xl">{{ item.dish?.name?.charAt(0) }}</span>
            </div>
          </div>

          <!-- 菜品信息 -->
          <div class="flex-1 min-w-0 flex flex-col">
            <div class="flex items-start justify-between mb-2">
              <h3 class="font-medium text-foreground">{{ item.dish?.name }}</h3>
              <button
                @click="removeItem(item.id)"
                class="text-muted hover:text-red-500 p-1"
              >
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
            
            <div class="flex items-center justify-between mt-auto">
              <span class="text-primary-600 font-bold">
                ¥{{ ((item.dish?.price || 0) * item.quantity).toFixed(2) }}
              </span>
              
              <div class="flex items-center gap-3">
                <button
                  @click="updateQuantity(item.id, item.quantity - 1)"
                  class="w-7 h-7 rounded-full border border-border text-muted flex items-center justify-center hover:border-primary-500 hover:text-primary-500"
                >
                  <Minus class="w-4 h-4" />
                </button>
                <span class="w-6 text-center font-medium">{{ item.quantity }}</span>
                <button
                  @click="updateQuantity(item.id, item.quantity + 1)"
                  class="w-7 h-7 rounded-full bg-primary-500 text-white flex items-center justify-center hover:bg-primary-600"
                >
                  <Plus class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="card p-4">
          <div class="flex items-center justify-between mb-4">
            <span class="text-muted">共 {{ cartStore.totalItems }} 件商品</span>
            <div class="text-right">
              <span class="text-muted text-sm">合计：</span>
              <span class="text-xl font-bold text-primary-600 ml-1">
                ¥{{ cartStore.totalAmount.toFixed(2) }}
              </span>
            </div>
          </div>
          <button
            @click="goToCheckout"
            class="btn btn-primary w-full py-3"
          >
            <span>去结算</span>
            <ArrowRight class="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
