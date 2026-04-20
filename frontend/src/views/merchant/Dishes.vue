<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { dishApi, categoryApi, merchantApi } from '@/api'
import type { Dish, Category } from '@/types'
import { Plus, Edit2, Trash2, Search, Loader2, X, Eye, EyeOff } from 'lucide-vue-next'

const dishes = ref<Dish[]>([])
const categories = ref<Category[]>([])
const merchantId = ref(0)
const loading = ref(true)
const searchKeyword = ref('')
const filterCategory = ref<number | null>(null)

const showModal = ref(false)
const editingDish = ref<Partial<Dish> | null>(null)
const saving = ref(false)

const emptyForm = {
  name: '',
  categoryId: 0,
  price: 0,
  description: '',
  image: '',
  status: 1,
}

onMounted(async () => {
  try {
    const merchant = await merchantApi.getCurrentMerchant()
    merchantId.value = merchant.id
    
    const [dishesRes, categoriesRes] = await Promise.all([
      dishApi.getByMerchant(merchant.id),
      categoryApi.getByMerchant(merchant.id),
    ])
    dishes.value = dishesRes || []
    categories.value = categoriesRes || []
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
})

const filteredDishes = computed(() => {
  let result = dishes.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(d => d.name.toLowerCase().includes(keyword))
  }
  if (filterCategory.value) {
    result = result.filter(d => d.categoryId === filterCategory.value)
  }
  return result
})

function openAddModal() {
  editingDish.value = { ...emptyForm, merchantId: merchantId.value }
  showModal.value = true
}

function openEditModal(dish: Dish) {
  editingDish.value = { ...dish }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingDish.value = null
}

async function saveDish() {
  if (!editingDish.value) return
  
  const { name, categoryId, price } = editingDish.value
  if (!name || !categoryId || !price) {
    alert('请填写菜品名称、分类和价格')
    return
  }

  saving.value = true
  try {
    if (editingDish.value.id) {
      const updated = await dishApi.update(editingDish.value)
      const index = dishes.value.findIndex(d => d.id === updated.id)
      if (index > -1) dishes.value[index] = updated
    } else {
      const created = await dishApi.create(editingDish.value)
      dishes.value.push(created)
    }
    closeModal()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function deleteDish(id: number) {
  if (!confirm('确定要删除这个菜品吗？')) return
  try {
    await dishApi.delete(id)
    dishes.value = dishes.value.filter(d => d.id !== id)
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

async function toggleStatus(dish: Dish) {
  const newStatus = dish.status === 1 ? 0 : 1
  try {
    await dishApi.updateStatus(dish.id, newStatus)
    dish.status = newStatus
  } catch (error) {
    console.error('更新状态失败:', error)
  }
}

function getCategoryName(categoryId: number) {
  return categories.value.find(c => c.id === categoryId)?.name || '未分类'
}
</script>

<template>
  <div>
    <!-- 标题和操作栏 -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
      <h1 class="text-2xl font-bold text-foreground">菜品管理</h1>
      <button @click="openAddModal" class="btn btn-primary">
        <Plus class="w-5 h-5" />
        <span>添加菜品</span>
      </button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="card p-4 mb-6">
      <div class="flex flex-col sm:flex-row gap-4">
        <div class="relative flex-1">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" />
          <input
            v-model="searchKeyword"
            type="text"
            class="input pl-10"
            placeholder="搜索菜品..."
          />
        </div>
        <select
          v-model="filterCategory"
          class="input w-full sm:w-48"
        >
          <option :value="null">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-16">
      <Loader2 class="w-8 h-8 text-primary-500 animate-spin" />
    </div>

    <!-- 菜品列表 -->
    <div v-else-if="filteredDishes.length > 0" class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-neutral-50 border-b border-border">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">菜品</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">分类</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">价格</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">销量</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-muted">状态</th>
              <th class="px-4 py-3 text-right text-sm font-medium text-muted">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-border">
            <tr v-for="dish in filteredDishes" :key="dish.id" class="hover:bg-neutral-50">
              <td class="px-4 py-3">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 rounded-lg overflow-hidden bg-neutral-100 flex-shrink-0">
                    <img
                      v-if="dish.image"
                      :src="dish.image"
                      :alt="dish.name"
                      class="w-full h-full object-cover"
                    />
                    <div v-else class="w-full h-full flex items-center justify-center text-lg">
                      {{ dish.name.charAt(0) }}
                    </div>
                  </div>
                  <div>
                    <p class="font-medium text-foreground">{{ dish.name }}</p>
                    <p class="text-sm text-muted line-clamp-1">{{ dish.description || '暂无描述' }}</p>
                  </div>
                </div>
              </td>
              <td class="px-4 py-3 text-muted">{{ getCategoryName(dish.categoryId) }}</td>
              <td class="px-4 py-3 font-medium text-foreground">¥{{ dish.price.toFixed(2) }}</td>
              <td class="px-4 py-3 text-muted">{{ dish.sales || 0 }}</td>
              <td class="px-4 py-3">
                <button
                  @click="toggleStatus(dish)"
                  :class="[
                    'badge cursor-pointer',
                    dish.status === 1 ? 'badge-success' : 'badge-danger'
                  ]"
                >
                  {{ dish.status === 1 ? '上架' : '下架' }}
                </button>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center justify-end gap-2">
                  <button
                    @click="toggleStatus(dish)"
                    class="p-2 rounded-lg text-muted hover:text-foreground hover:bg-neutral-100"
                    :title="dish.status === 1 ? '下架' : '上架'"
                  >
                    <Eye v-if="dish.status === 1" class="w-4 h-4" />
                    <EyeOff v-else class="w-4 h-4" />
                  </button>
                  <button
                    @click="openEditModal(dish)"
                    class="p-2 rounded-lg text-muted hover:text-primary-600 hover:bg-primary-50"
                  >
                    <Edit2 class="w-4 h-4" />
                  </button>
                  <button
                    @click="deleteDish(dish.id)"
                    class="p-2 rounded-lg text-muted hover:text-red-600 hover:bg-red-50"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="card p-12 text-center">
      <p class="text-muted mb-4">暂无菜品</p>
      <button @click="openAddModal" class="btn btn-primary">
        <Plus class="w-5 h-5" />
        <span>添加菜品</span>
      </button>
    </div>

    <!-- 添加/编辑菜品弹窗 -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/50" @click="closeModal" />
      <div class="relative bg-white rounded-xl w-full max-w-lg p-6 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold">
            {{ editingDish?.id ? '编辑菜品' : '添加菜品' }}
          </h3>
          <button @click="closeModal" class="p-1 rounded-lg hover:bg-neutral-100">
            <X class="w-5 h-5" />
          </button>
        </div>

        <form @submit.prevent="saveDish" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">菜品名称 *</label>
            <input v-model="editingDish!.name" class="input" placeholder="请输入菜品名称" />
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">分类 *</label>
            <select v-model="editingDish!.categoryId" class="input">
              <option :value="0" disabled>请选择分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">价格 *</label>
            <input
              v-model.number="editingDish!.price"
              type="number"
              step="0.01"
              min="0"
              class="input"
              placeholder="0.00"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">图片URL</label>
            <input v-model="editingDish!.image" class="input" placeholder="请输入图片链接" />
          </div>
          <div>
            <label class="block text-sm font-medium text-foreground mb-1">描述</label>
            <textarea
              v-model="editingDish!.description"
              class="input resize-none"
              rows="3"
              placeholder="请输入菜品描述"
            />
          </div>
          <div class="flex items-center gap-2">
            <input
              type="checkbox"
              :checked="editingDish!.status === 1"
              @change="editingDish!.status = ($event.target as HTMLInputElement).checked ? 1 : 0"
              class="accent-primary-500"
            />
            <span class="text-sm">立即上架</span>
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
