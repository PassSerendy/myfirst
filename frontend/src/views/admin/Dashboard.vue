<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userApi, merchantApi, orderApi } from '@/api'
import { Users, Store, ShoppingBag, DollarSign, Loader2 } from 'lucide-vue-next'

const stats = ref({
  totalUsers: 0,
  totalMerchants: 0,
  totalOrders: 0,
  totalRevenue: 0,
})
const loading = ref(true)

onMounted(async () => {
  try {
    const [usersRes, merchantsRes, ordersRes] = await Promise.all([
      userApi.getAllUsers(1, 1),
      merchantApi.getList(1, 1),
      orderApi.getAllOrders(1, 100),
    ])
    
    stats.value = {
      totalUsers: usersRes.total || 0,
      totalMerchants: merchantsRes.total || 0,
      totalOrders: ordersRes.total || 0,
      totalRevenue: (ordersRes.records || []).reduce((sum, o) => sum + (o.totalAmount || 0), 0),
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold text-foreground mb-6">系统概览</h1>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <template v-else>
      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="card p-6">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 bg-blue-100 rounded-xl flex items-center justify-center">
              <Users class="w-7 h-7 text-blue-600" />
            </div>
            <div>
              <p class="text-sm text-muted">注册用户</p>
              <p class="text-3xl font-bold text-foreground">{{ stats.totalUsers }}</p>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 bg-orange-100 rounded-xl flex items-center justify-center">
              <Store class="w-7 h-7 text-orange-600" />
            </div>
            <div>
              <p class="text-sm text-muted">入驻商家</p>
              <p class="text-3xl font-bold text-foreground">{{ stats.totalMerchants }}</p>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 bg-purple-100 rounded-xl flex items-center justify-center">
              <ShoppingBag class="w-7 h-7 text-purple-600" />
            </div>
            <div>
              <p class="text-sm text-muted">总订单数</p>
              <p class="text-3xl font-bold text-foreground">{{ stats.totalOrders }}</p>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 bg-green-100 rounded-xl flex items-center justify-center">
              <DollarSign class="w-7 h-7 text-green-600" />
            </div>
            <div>
              <p class="text-sm text-muted">总交易额</p>
              <p class="text-3xl font-bold text-foreground">¥{{ stats.totalRevenue.toFixed(0) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <RouterLink to="/admin/users" class="card p-6 hover:shadow-md transition-shadow">
          <div class="flex items-center gap-4">
            <Users class="w-8 h-8 text-blue-600" />
            <div>
              <h3 class="font-semibold text-foreground">用户管理</h3>
              <p class="text-sm text-muted">管理系统用户</p>
            </div>
          </div>
        </RouterLink>

        <RouterLink to="/admin/merchants" class="card p-6 hover:shadow-md transition-shadow">
          <div class="flex items-center gap-4">
            <Store class="w-8 h-8 text-orange-600" />
            <div>
              <h3 class="font-semibold text-foreground">商家管理</h3>
              <p class="text-sm text-muted">管理入驻商家</p>
            </div>
          </div>
        </RouterLink>

        <RouterLink to="/admin/orders" class="card p-6 hover:shadow-md transition-shadow">
          <div class="flex items-center gap-4">
            <ShoppingBag class="w-8 h-8 text-purple-600" />
            <div>
              <h3 class="font-semibold text-foreground">订单管理</h3>
              <p class="text-sm text-muted">查看所有订单</p>
            </div>
          </div>
        </RouterLink>
      </div>
    </template>
  </div>
</template>
