<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { addressApi } from '@/api'
import type { Address } from '@/types'
import { Plus, MapPin, Edit2, Trash2, Star, Loader2, ArrowLeft, X } from 'lucide-vue-next'

const router = useRouter()

const addresses = ref<Address[]>([])
const loading = ref(true)
const showModal = ref(false)
const editingAddress = ref<Partial<Address> | null>(null)
const saving = ref(false)

const emptyForm = {
  contactName: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0,
}

onMounted(fetchAddresses)

async function fetchAddresses() {
  loading.value = true
  try {
    addresses.value = await addressApi.getList()
  } catch (error) {
    console.error('获取地址失败:', error)
  } finally {
    loading.value = false
  }
}

function openAddModal() {
  editingAddress.value = { ...emptyForm }
  showModal.value = true
}

function openEditModal(addr: Address) {
  editingAddress.value = { ...addr }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingAddress.value = null
}

async function saveAddress() {
  if (!editingAddress.value) return
  
  const { contactName, contactPhone, province, city, district, detail } = editingAddress.value
  if (!contactName || !contactPhone || !province || !city || !district || !detail) {
    alert('请填写完整信息')
    return
  }

  saving.value = true
  try {
    if (editingAddress.value.id) {
      await addressApi.update(editingAddress.value)
    } else {
      await addressApi.create(editingAddress.value)
    }
    await fetchAddresses()
    closeModal()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function deleteAddress(id: number) {
  if (!confirm('确定要删除这个地址吗？')) return
  try {
    await addressApi.delete(id)
    addresses.value = addresses.value.filter(a => a.id !== id)
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

async function setDefault(id: number) {
  try {
    await addressApi.setDefault(id)
    addresses.value.forEach(a => {
      a.isDefault = a.id === id ? 1 : 0
    })
  } catch (error) {
    console.error('设置失败:', error)
  }
}
</script>

<template>
  <div class="container mx-auto px-4 py-6">
    <div class="max-w-2xl mx-auto">
      <!-- 标题 -->
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-3">
          <button @click="router.back()" class="p-2 -ml-2 rounded-lg hover:bg-neutral-100">
            <ArrowLeft class="w-5 h-5" />
          </button>
          <h1 class="text-2xl font-bold text-foreground">收货地址</h1>
        </div>
        <button @click="openAddModal" class="btn btn-primary">
          <Plus class="w-5 h-5" />
          <span>添加</span>
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
      </div>

      <!-- 地址列表 -->
      <div v-else-if="addresses.length > 0" class="space-y-3">
        <div
          v-for="addr in addresses"
          :key="addr.id"
          class="card p-4"
        >
          <div class="flex items-start justify-between mb-2">
            <div class="flex items-center gap-2">
              <span class="font-medium text-foreground">{{ addr.contactName }}</span>
              <span class="text-muted">{{ addr.contactPhone }}</span>
              <span v-if="addr.isDefault === 1" class="badge badge-info">默认</span>
            </div>
            <div class="flex items-center gap-2">
              <button
                @click="openEditModal(addr)"
                class="p-1.5 rounded-lg text-muted hover:text-primary-600 hover:bg-primary-50"
              >
                <Edit2 class="w-4 h-4" />
              </button>
              <button
                @click="deleteAddress(addr.id)"
                class="p-1.5 rounded-lg text-muted hover:text-red-600 hover:bg-red-50"
              >
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>
          <div class="flex items-start gap-2 text-sm text-muted mb-3">
            <MapPin class="w-4 h-4 mt-0.5 flex-shrink-0" />
            <span>{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</span>
          </div>
          <button
            v-if="addr.isDefault !== 1"
            @click="setDefault(addr.id)"
            class="text-sm text-primary-600 hover:text-primary-700 flex items-center gap-1"
          >
            <Star class="w-4 h-4" />
            <span>设为默认</span>
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="card p-12 text-center">
        <MapPin class="w-12 h-12 text-muted mx-auto mb-4" />
        <h3 class="text-lg font-medium text-foreground mb-2">暂无收货地址</h3>
        <p class="text-muted mb-4">添加一个收货地址吧</p>
        <button @click="openAddModal" class="btn btn-primary">
          <Plus class="w-5 h-5" />
          <span>添加地址</span>
        </button>
      </div>
    </div>

    <!-- 添加/编辑地址弹窗 -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/50" @click="closeModal" />
      <div class="relative bg-white rounded-xl w-full max-w-md p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold">
            {{ editingAddress?.id ? '编辑地址' : '添加地址' }}
          </h3>
          <button @click="closeModal" class="p-1 rounded-lg hover:bg-neutral-100">
            <X class="w-5 h-5" />
          </button>
        </div>

        <form @submit.prevent="saveAddress" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">联系人</label>
              <input v-model="editingAddress!.contactName" class="input" placeholder="姓名" />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">电话</label>
              <input v-model="editingAddress!.contactPhone" class="input" placeholder="手机号" />
            </div>
          </div>
          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">省份</label>
              <input v-model="editingAddress!.province" class="input" placeholder="省" />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">城市</label>
              <input v-model="editingAddress!.city" class="input" placeholder="市" />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">区县</label>
              <input v-model="editingAddress!.district" class="input" placeholder="区" />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">详细地址</label>
            <input v-model="editingAddress!.detail" class="input" placeholder="街道、门牌号等" />
          </div>
          <label class="flex items-center gap-2">
            <input
              type="checkbox"
              :checked="editingAddress!.isDefault === 1"
              @change="editingAddress!.isDefault = ($event.target as HTMLInputElement).checked ? 1 : 0"
              class="accent-primary-500"
            />
            <span class="text-sm">设为默认地址</span>
          </label>
          <button type="submit" :disabled="saving" class="btn btn-primary w-full">
            <Loader2 v-if="saving" class="w-5 h-5 animate-spin" />
            <span v-else>保存</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
