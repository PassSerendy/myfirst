<script setup lang="ts">
import { RouterView, RouterLink } from 'vue-router'
import { useUserStore, useCartStore } from '@/stores'
import { Home, ShoppingCart, ClipboardList, User, LogOut, LogIn } from 'lucide-vue-next'

const userStore = useUserStore()
const cartStore = useCartStore()

// 获取购物车数据
if (userStore.isLoggedIn) {
  cartStore.fetchItems()
}
</script>

<template>
  <div class="min-h-screen bg-background flex flex-col">
    <!-- 顶部导航 -->
    <header class="sticky top-0 z-50 bg-white border-b border-border">
      <div class="container mx-auto px-4 h-16 flex items-center justify-between">
        <RouterLink to="/" class="flex items-center gap-2">
          <div class="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center">
            <span class="text-white font-bold text-sm">点</span>
          </div>
          <span class="font-semibold text-lg text-foreground">智能点餐</span>
        </RouterLink>

        <nav class="flex items-center gap-1">
          <RouterLink
            to="/"
            class="flex items-center gap-2 px-3 py-2 rounded-lg text-muted hover:text-foreground hover:bg-neutral-100 transition-colors"
            active-class="!text-primary-600 !bg-primary-50"
          >
            <Home class="w-5 h-5" />
            <span class="hidden sm:inline">首页</span>
          </RouterLink>

          <template v-if="userStore.isLoggedIn">
            <RouterLink
              to="/cart"
              class="flex items-center gap-2 px-3 py-2 rounded-lg text-muted hover:text-foreground hover:bg-neutral-100 transition-colors relative"
              active-class="!text-primary-600 !bg-primary-50"
            >
              <ShoppingCart class="w-5 h-5" />
              <span class="hidden sm:inline">购物车</span>
              <span
                v-if="cartStore.totalItems > 0"
                class="absolute -top-1 -right-1 w-5 h-5 bg-primary-500 text-white text-xs rounded-full flex items-center justify-center"
              >
                {{ cartStore.totalItems > 99 ? '99+' : cartStore.totalItems }}
              </span>
            </RouterLink>

            <RouterLink
              to="/orders"
              class="flex items-center gap-2 px-3 py-2 rounded-lg text-muted hover:text-foreground hover:bg-neutral-100 transition-colors"
              active-class="!text-primary-600 !bg-primary-50"
            >
              <ClipboardList class="w-5 h-5" />
              <span class="hidden sm:inline">订单</span>
            </RouterLink>

            <RouterLink
              to="/profile"
              class="flex items-center gap-2 px-3 py-2 rounded-lg text-muted hover:text-foreground hover:bg-neutral-100 transition-colors"
              active-class="!text-primary-600 !bg-primary-50"
            >
              <User class="w-5 h-5" />
              <span class="hidden sm:inline">我的</span>
            </RouterLink>

            <button
              @click="userStore.logout"
              class="flex items-center gap-2 px-3 py-2 rounded-lg text-muted hover:text-red-600 hover:bg-red-50 transition-colors ml-2"
            >
              <LogOut class="w-5 h-5" />
              <span class="hidden sm:inline">退出</span>
            </button>
          </template>

          <template v-else>
            <RouterLink
              to="/login"
              class="flex items-center gap-2 px-4 py-2 rounded-lg bg-primary-500 text-white hover:bg-primary-600 transition-colors"
            >
              <LogIn class="w-5 h-5" />
              <span>登录</span>
            </RouterLink>
          </template>
        </nav>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="flex-1">
      <RouterView />
    </main>

    <!-- 底部 -->
    <footer class="border-t border-border bg-white py-6">
      <div class="container mx-auto px-4 text-center text-sm text-muted">
        <p>智能点餐系统 © 2024</p>
      </div>
    </footer>
  </div>
</template>
