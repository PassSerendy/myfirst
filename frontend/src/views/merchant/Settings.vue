<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { merchantApi } from '@/api'
import type { Merchant } from '@/types'
import { Store, Loader2, Save } from 'lucide-vue-next'

const merchant = ref<Merchant | null>(null)
const loading = ref(true)
const saving = ref(false)

const form = ref({
  name: '',
  logo: '',
  description: '',
  address: '',
  phone: '',
  businessHours: '',
})

onMounted(async () => {
  try {
    merchant.value = await merchantApi.getCurrentMerchant()
    if (merchant.value) {
      form.value = {
        name: merchant.value.name,
        logo: merchant.value.logo || '',
        description: merchant.value.description || '',
        address: merchant.value.address,
        phone: merchant.value.phone,
        businessHours: merchant.value.businessHours || '',
      }
    }
  } catch (error) {
    console.error('获取商家信息失败:', error)
  } finally {
    loading.value = false
  }
})

async function saveSettings() {
  if (!form.value.name || !form.value.address || !form.value.phone) {
    alert('请填写店铺名称、地址和电话')
    return
  }

  saving.value = true
  try {
    await merchantApi.update({
      id: merchant.value?.id,
      ...form.value,
    })
    alert('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold text-foreground mb-6">店铺设置</h1>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <template v-else>
      <div class="card p-6 max-w-2xl">
        <!-- 店铺头像 -->
        <div class="flex items-center gap-6 mb-8 pb-8 border-b border-border">
          <div class="w-24 h-24 rounded-xl overflow-hidden bg-neutral-100 flex-shrink-0">
            <img
              v-if="form.logo"
              :src="form.logo"
              :alt="form.name"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-100 to-primary-200">
              <Store class="w-10 h-10 text-primary-600" />
            </div>
          </div>
          <div class="flex-1">
            <h3 class="font-medium text-foreground mb-2">店铺Logo</h3>
            <input
              v-model="form.logo"
              class="input"
              placeholder="请输入Logo图片链接"
            />
          </div>
        </div>

        <form @submit.prevent="saveSettings" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">
              店铺名称 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.name"
              class="input"
              placeholder="请输入店铺名称"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-1">店铺简介</label>
            <textarea
              v-model="form.description"
              class="input resize-none"
              rows="3"
              placeholder="请输入店铺简介"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-1">
              店铺地址 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.address"
              class="input"
              placeholder="请输入店铺地址"
            />
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">
                联系电话 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.phone"
                class="input"
                placeholder="请输入联系电话"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">营业时间</label>
              <input
                v-model="form.businessHours"
                class="input"
                placeholder="例如: 09:00-22:00"
              />
            </div>
          </div>

          <div class="flex justify-end pt-4">
            <button type="submit" :disabled="saving" class="btn btn-primary">
              <Loader2 v-if="saving" class="w-5 h-5 animate-spin" />
              <template v-else>
                <Save class="w-5 h-5" />
                <span>保存设置</span>
              </template>
            </button>
          </div>
        </form>
      </div>
    </template>
  </div>
</template>
