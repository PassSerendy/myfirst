<script setup lang="ts">
import { ref } from 'vue'
import { RouterView, RouterLink } from 'vue-router'
import { useUserStore } from '@/stores'
import {
  LayoutDashboard,
  UtensilsCrossed,
  FolderOpen,
  ClipboardList,
  Settings,
  LogOut,
  Menu,
  X,
} from 'lucide-vue-next'

const userStore = useUserStore()
const sidebarOpen = ref(false)

const navItems = [
  { to: '/merchant', icon: LayoutDashboard, label: '数据概览', exact: true },
  { to: '/merchant/dishes', icon: UtensilsCrossed, label: '菜品管理' },
  { to: '/merchant/categories', icon: FolderOpen, label: '分类管理' },
  { to: '/merchant/orders', icon: ClipboardList, label: '订单管理' },
  { to: '/merchant/settings', icon: Settings, label: '店铺设置' },
]
</script>

<template>
  <div class="min-h-screen bg-background flex">
    <!-- 侧边栏 -->
    <aside
      :class="[
        'fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-border transform transition-transform lg:translate-x-0 lg:static',
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <div class="flex flex-col h-full">
        <!-- Logo -->
        <div class="h-16 flex items-center justify-between px-4 border-b border-border">
          <RouterLink to="/merchant" class="flex items-center gap-2">
            <div class="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center">
              <span class="text-white font-bold text-sm">商</span>
            </div>
            <span class="font-semibold text-foreground">商家后台</span>
          </RouterLink>
          <button
            @click="sidebarOpen = false"
            class="lg:hidden p-1 rounded-lg hover:bg-neutral-100"
          >
            <X class="w-5 h-5" />
          </button>
        </div>

        <!-- 导航菜单 -->
        <nav class="flex-1 p-4 space-y-1">
          <RouterLink
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            :class="[
              'flex items-center gap-3 px-3 py-2.5 rounded-lg text-muted hover:text-foreground hover:bg-neutral-100 transition-colors',
            ]"
            :active-class="item.exact ? '' : '!text-primary-600 !bg-primary-50'"
            :exact-active-class="item.exact ? '!text-primary-600 !bg-primary-50' : ''"
            @click="sidebarOpen = false"
          >
            <component :is="item.icon" class="w-5 h-5" />
            <span>{{ item.label }}</span>
          </RouterLink>
        </nav>

        <!-- 用户信息 -->
        <div class="p-4 border-t border-border">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
              <span class="text-primary-600 font-medium">
                {{ userStore.user?.username?.charAt(0)?.toUpperCase() }}
              </span>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-foreground truncate">
                {{ userStore.user?.username }}
              </p>
              <p class="text-xs text-muted">商家</p>
            </div>
          </div>
          <button
            @click="userStore.logout"
            class="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-red-600 hover:bg-red-50 transition-colors"
          >
            <LogOut class="w-4 h-4" />
            <span>退出登录</span>
          </button>
        </div>
      </div>
    </aside>

    <!-- 遮罩层 -->
    <div
      v-if="sidebarOpen"
      class="fixed inset-0 bg-black/50 z-40 lg:hidden"
      @click="sidebarOpen = false"
    />

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col min-w-0">
      <!-- 顶部栏 -->
      <header class="h-16 bg-white border-b border-border flex items-center px-4 lg:px-6">
        <button
          @click="sidebarOpen = true"
          class="lg:hidden p-2 -ml-2 rounded-lg hover:bg-neutral-100"
        >
          <Menu class="w-5 h-5" />
        </button>
        <h1 class="text-lg font-semibold text-foreground ml-2 lg:ml-0">
          商家管理后台
        </h1>
      </header>

      <!-- 页面内容 -->
      <main class="flex-1 p-4 lg:p-6 overflow-auto">
        <RouterView />
      </main>
    </div>
  </div>
</template>
