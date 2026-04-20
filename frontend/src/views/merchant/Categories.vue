<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { categoryApi, merchantApi } from '@/api'
import type { Category } from '@/types'
import { Plus, Edit2, Trash2, GripVertical, Loader2, X } from 'lucide-vue-next'

const categories = ref<Category[]>([])
const merchantId = ref(0)
const loading = ref(true)

const showModal = ref(false)
const editingCategory = ref<Partial<Category> | null>(null)
const saving = ref(false)

onMounted(async () => {
  try {
    const merchant = await merchantApi.getCurrentMerchant()
    merchantId.value = merchant.id
    categories.value = await categoryApi.getByMerchant(merchant.id) || []
  } catch (error) {
    console.error('获取分类失败:', error)
  } finally {
    loading.value = false
  }
})

function openAddModal() {
  editingCategory.value = {
    name: '',
    sort: categories.value.length + 1,
    status: 1,
    merchantId: merchantId.value,
  }
  showModal.value = true
}

function openEditModal(cat: Category) {
  editingCategory.value = { ...cat }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingCategory.value = null
}

async function saveCategory() {
  if (!editingCategory.value?.name) {
    alert('请输入分类名称')
    return
  }

  saving.value = true
  try {
    if (editingCategory.value.id) {
      const updated = await categoryApi.update(editingCategory.value)
      const index = categories.value.findIndex(c => c.id === updated.id)
      if (index > -1) categories.value[index] = updated
    } else {
      const created = await categoryApi.create(editingCategory.value)
      categories.value.push(created)
    }
    closeModal()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function deleteCategory(id: number) {
  if (!confirm('确定要删除这个分类吗？删除后该分类下的菜品将变为未分类。')) return
  try {
    await categoryApi.delete(id)
    categories.value = categories.value.filter(c => c.id !== id)
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

async function updateSort(id: number, direction: 'up' | 'down') {
  const index = categories.value.findIndex(c => c.id === id)
  if (index === -1) return
  
  const targetIndex = direction === 'up' ? index - 1 : index + 1
  if (targetIndex < 0 || targetIndex >= categories.value.length) return
  
  // 交换位置
  const temp = categories.value[index]
  categories.value[index] = categories.value[targetIndex]
  categories.value[targetIndex] = temp
  
  // 更新排序值
  try {
    await Promise.all([
      categoryApi.updateSort(categories.value[index].id, index + 1),
      categoryApi.updateSort(categories.value[targetIndex].id, targetIndex + 1),
    ])
  } catch (error) {
    console.error('更新排序失败:', error)
  }
}
</script>

<template>
  <div>
    <!-- 标题和操作栏 -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-foreground">分类管理</h1>
      <button @click="openAddModal" class="btn btn-primary">
        <Plus class="w-5 h-5" />
        <span>添加分类</span>
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <!-- 分类列表 -->
    <div v-else-if="categories.length > 0" class="card overflow-hidden">
      <div class="divide-y divide-border">
        <div
          v-for="(cat, index) in categories"
          :key="cat.id"
          class="p-4 flex items-center gap-4 hover:bg-neutral-50"
        >
          <div class="flex flex-col gap-1">
            <button
              @click="updateSort(cat.id, 'up')"
              :disabled="index === 0"
              class="p-1 rounded hover:bg-neutral-200 disabled:opacity-30 disabled:cursor-not-allowed"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 15l-6-6-6 6"/>
              </svg>
            </button>
            <button
              @click="updateSort(cat.id, 'down')"
              :disabled="index === categories.length - 1"
              class="p-1 rounded hover:bg-neutral-200 disabled:opacity-30 disabled:cursor-not-allowed"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M6 9l6 6 6-6"/>
              </svg>
            </button>
          </div>
          
          <GripVertical class="w-5 h-5 text-muted cursor-move" />
          
          <div class="flex-1">
            <p class="font-medium text-foreground">{{ cat.name }}</p>
            <p class="text-sm text-muted">排序: {{ cat.sort }}</p>
          </div>
          
          <span :class="['badge', cat.status === 1 ? 'badge-success' : 'badge-danger']">
            {{ cat.status === 1 ? '启用' : '禁用' }}
          </span>
          
          <div class="flex items-center gap-2">
            <button
              @click="openEditModal(cat)"
              class="p-2 rounded-lg text-muted hover:text-primary-600 hover:bg-primary-50"
            >
              <Edit2 class="w-4 h-4" />
            </button>
            <button
              @click="deleteCategory(cat.id)"
              class="p-2 rounded-lg text-muted hover:text-red-600 hover:bg-red-50"
            >
              <Trash2 class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="card p-12 text-center">
      <p class="text-muted mb-4">暂无分类</p>
      <button @click="openAddModal" class="btn btn-primary">
        <Plus class="w-5 h-5" />
        <span>添加分类</span>
      </button>
    </div>

    <!-- 添加/编辑分类弹窗 -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/50" @click="closeModal" />
      <div class="relative bg-white rounded-xl w-full max-w-md p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold">
            {{ editingCategory?.id ? '编辑分类' : '添加分类' }}
          </h3>
          <button @click="closeModal" class="p-1 rounded-lg hover:bg-neutral-100">
            <X class="w-5 h-5" />
          </button>
        </div>

        <form @submit.prevent="saveCategory" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">分类名称 *</label>
            <input
              v-model="editingCategory!.name"
              class="input"
              placeholder="请输入分类名称"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">排序</label>
            <input
              v-model.number="editingCategory!.sort"
              type="number"
              min="1"
              class="input"
              placeholder="1"
            />
          </div>
          <div class="flex items-center gap-2">
            <input
              type="checkbox"
              :checked="editingCategory!.status === 1"
              @change="editingCategory!.status = ($event.target as HTMLInputElement).checked ? 1 : 0"
              class="accent-primary-500"
            />
            <span class="text-sm">启用分类</span>
          </div>
          <div class="flex gap-3 pt-2">
            <button type="button" @click="closeModal" class="btn btn-outline flex-1">
              取消
            </button>
            <button type="submit" :disabled="saving" class="btn btn-primary flex-1">
              <Loader2 v-if="saving" class="w-5 h-5 animate-spin" />
              <span v-else>保存</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
