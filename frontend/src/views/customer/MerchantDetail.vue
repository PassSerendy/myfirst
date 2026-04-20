<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { merchantApi, categoryApi, dishApi } from '@/api'
import { useCartStore, useUserStore } from '@/stores'
import type { Merchant, Category, Dish } from '@/types'
import { Star, MapPin, Clock, Plus, Minus, ShoppingCart, Loader2, ArrowLeft } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const merchantId = Number(route.params.id)
const merchant = ref<Merchant | null>(null)
const categories = ref<Category[]>([])
const dishes = ref<Dish[]>([])
const activeCategory = ref<number | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const [merchantRes, categoriesRes, dishesRes] = await Promise.all([
      merchantApi.getById(merchantId),
      categoryApi.getByMerchant(merchantId),
      dishApi.getByMerchant(merchantId),
    ])
    merchant.value = merchantRes
    categories.value = categoriesRes || []
    dishes.value = dishesRes || []
    if (categories.value.length > 0) {
      activeCategory.value = categories.value[0].id
    }
  } catch (error) {
    console.error('获取商家信息失败:', error)
  } finally {
    loading.value = false
  }
})

const filteredDishes = computed(() => {
  if (!activeCategory.value) return dishes.value
  return dishes.value.filter(d => d.categoryId === activeCategory.value)
})

const cartItems = computed(() => {
  const items: Record<number, number> = {}
  cartStore.items
    .filter(item => item.merchantId === merchantId)
    .forEach(item => {
      items[item.dishId] = item.quantity
    })
  return items
})

const cartTotal = computed(() => {
  return cartStore.items
    .filter(item => item.merchantId === merchantId)
    .reduce((sum, item) => sum + (item.dish?.price || 0) * item.quantity, 0)
})

const cartCount = computed(() => {
  return cartStore.items
    .filter(item => item.merchantId === merchantId)
    .reduce((sum, item) => sum + item.quantity, 0)
})

async function addToCart(dish: Dish) {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    await cartStore.addItem(dish, merchantId)
  } catch (error) {
    console.error('添加失败:', error)
  }
}

async function updateCartQuantity(dish: Dish, delta: number) {
  const cartItem = cartStore.items.find(
    item => item.dishId === dish.id && item.merchantId === merchantId
  )
  if (cartItem) {
    await cartStore.updateQuantity(cartItem.id, cartItem.quantity + delta)
  }
}

function goToCart() {
  router.push('/cart')
}
</script>

<template>
  <div class="min-h-screen bg-background">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <template v-else-if="merchant">
      <!-- 商家头部 -->
      <div class="bg-white border-b border-border">
        <div class="container mx-auto px-4 py-6">
          <button @click="router.back()" class="flex items-center gap-2 text-muted hover:text-foreground mb-4">
            <ArrowLeft class="w-5 h-5" />
            <span>返回</span>
          </button>
          
          <div class="flex gap-4">
            <div class="w-24 h-24 rounded-xl overflow-hidden bg-neutral-100 flex-shrink-0">
              <img
                v-if="merchant.logo"
                :src="merchant.logo"
                :alt="merchant.name"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-100 to-primary-200">
                <span class="text-2xl font-bold text-primary-600">{{ merchant.name.charAt(0) }}</span>
              </div>
            </div>
            <div class="flex-1 min-w-0">
              <h1 class="text-xl font-bold text-foreground mb-1">{{ merchant.name }}</h1>
              <p class="text-sm text-muted mb-2 line-clamp-2">{{ merchant.description || '暂无简介' }}</p>
              <div class="flex flex-wrap items-center gap-4 text-sm">
                <div class="flex items-center gap-1 text-primary-600">
                  <Star class="w-4 h-4 fill-current" />
                  <span>{{ merchant.rating?.toFixed(1) || '5.0' }}</span>
                </div>
                <div class="flex items-center gap-1 text-muted">
                  <Clock class="w-4 h-4" />
                  <span>{{ merchant.businessHours || '09:00-22:00' }}</span>
                </div>
                <div class="flex items-center gap-1 text-muted">
                  <MapPin class="w-4 h-4" />
                  <span>{{ merchant.address }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 菜单区域 -->
      <div class="container mx-auto px-4 py-6">
        <div class="flex gap-6">
          <!-- 分类侧边栏 -->
          <div class="w-28 flex-shrink-0">
            <div class="sticky top-20 bg-white rounded-xl border border-border overflow-hidden">
              <button
                v-for="category in categories"
                :key="category.id"
                @click="activeCategory = category.id"
                :class="[
                  'w-full px-3 py-3 text-sm text-left transition-colors',
                  activeCategory === category.id
                    ? 'bg-primary-50 text-primary-600 font-medium border-l-2 border-primary-500'
                    : 'text-muted hover:bg-neutral-50'
                ]"
              >
                {{ category.name }}
              </button>
            </div>
          </div>

          <!-- 菜品列表 -->
          <div class="flex-1">
            <div class="space-y-3">
              <div
                v-for="dish in filteredDishes"
                :key="dish.id"
                class="card p-4 flex gap-4"
              >
                <!-- 菜品图片 -->
                <div class="w-24 h-24 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0">
                  <img
                    v-if="dish.image"
                    :src="dish.image"
                    :alt="dish.name"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center">
                    <span class="text-2xl">{{ dish.name.charAt(0) }}</span>
                  </div>
                </div>

                <!-- 菜品信息 -->
                <div class="flex-1 min-w-0 flex flex-col">
                  <h3 class="font-medium text-foreground mb-1">{{ dish.name }}</h3>
                  <p class="text-sm text-muted mb-2 line-clamp-2 flex-1">{{ dish.description || '暂无描述' }}</p>
                  <div class="flex items-center justify-between">
                    <div class="flex items-baseline gap-1">
                      <span class="text-primary-600 font-bold text-lg">¥{{ dish.price.toFixed(2) }}</span>
                      <span class="text-xs text-muted">月售{{ dish.sales || 0 }}</span>
                    </div>
                    
                    <!-- 加入购物车 -->
                    <div class="flex items-center gap-2">
                      <template v-if="cartItems[dish.id]">
                        <button
                          @click="updateCartQuantity(dish, -1)"
                          class="w-7 h-7 rounded-full border border-primary-500 text-primary-500 flex items-center justify-center hover:bg-primary-50"
                        >
                          <Minus class="w-4 h-4" />
                        </button>
                        <span class="w-6 text-center font-medium">{{ cartItems[dish.id] }}</span>
                      </template>
                      <button
                        @click="addToCart(dish)"
                        class="w-7 h-7 rounded-full bg-primary-500 text-white flex items-center justify-center hover:bg-primary-600"
                      >
                        <Plus class="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 空状态 -->
              <div v-if="filteredDishes.length === 0" class="text-center py-12">
                <p class="text-muted">该分类下暂无菜品</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部购物车栏 -->
      <div
        v-if="cartCount > 0"
        class="fixed bottom-0 left-0 right-0 bg-white border-t border-border p-4 shadow-lg"
      >
        <div class="container mx-auto flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="relative">
              <ShoppingCart class="w-8 h-8 text-primary-500" />
              <span class="absolute -top-2 -right-2 w-5 h-5 bg-primary-500 text-white text-xs rounded-full flex items-center justify-center">
                {{ cartCount }}
              </span>
            </div>
            <div>
              <span class="text-lg font-bold text-primary-600">¥{{ cartTotal.toFixed(2) }}</span>
            </div>
          </div>
          <button
            @click="goToCart"
            class="btn btn-primary px-8"
          >
            去结算
          </button>
        </div>
      </div>
    </template>
  </div>
</template>
