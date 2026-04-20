<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { useUserStore } from '@/stores'
import { User, MapPin, ClipboardList, Settings, ChevronRight, LogOut } from 'lucide-vue-next'

const userStore = useUserStore()

const menuItems = [
  { to: '/orders', icon: ClipboardList, label: '我的订单', desc: '查看所有订单' },
  { to: '/addresses', icon: MapPin, label: '收货地址', desc: '管理收货地址' },
]
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <div class="max-w-2xl mx-auto">
      <!-- 用户信息卡片 -->
      <div class="card p-6 mb-6">
        <div class="flex items-center gap-4">
          <div class="w-20 h-20 bg-primary-100 rounded-full flex items-center justify-center">
            <User class="w-10 h-10 text-primary-600" />
          </div>
          <div class="flex-1">
            <h2 class="text-xl font-bold text-foreground">
              {{ userStore.user?.username }}
            </h2>
            <p class="text-muted">{{ userStore.user?.phone || '未绑定手机' }}</p>
            <span class="badge badge-info mt-2">
              {{ userStore.user?.role === 'USER' ? '普通用户' : userStore.user?.role }}
            </span>
          </div>
        </div>
      </div>

      <!-- 菜单列表 -->
      <div class="card overflow-hidden mb-6">
        <RouterLink
          v-for="item in menuItems"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-4 p-4 hover:bg-neutral-50 transition-colors border-b border-border last:border-b-0"
        >
          <div class="w-10 h-10 bg-primary-50 rounded-lg flex items-center justify-center">
            <component :is="item.icon" class="w-5 h-5 text-primary-600" />
          </div>
          <div class="flex-1">
            <p class="font-medium text-foreground">{{ item.label }}</p>
            <p class="text-sm text-muted">{{ item.desc }}</p>
          </div>
          <ChevronRight class="w-5 h-5 text-muted" />
        </RouterLink>
      </div>

      <!-- 退出登录 -->
      <button
        @click="userStore.logout"
        class="w-full card p-4 flex items-center justify-center gap-2 text-red-600 hover:bg-red-50 transition-colors"
      >
        <LogOut class="w-5 h-5" />
        <span>退出登录</span>
      </button>
    </div>
  </div>
</template>
