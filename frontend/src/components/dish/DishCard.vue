<script setup lang="ts">
import { Plus, Minus } from 'lucide-vue-next'
import type { Dish } from '@/types'

defineProps<{
  dish: Dish
  quantity?: number
}>()

const emit = defineEmits<{
  add: []
  remove: []
}>()

const formatPrice = (price: number) => {
  return `¥${price.toFixed(2)}`
}
</script>

<template>
  <div class="flex gap-4 p-4 bg-card rounded-xl border border-border hover:shadow-md transition-shadow">
    <img
      :src="dish.image || '/placeholder-dish.jpg'"
      :alt="dish.name"
      class="w-24 h-24 object-cover rounded-lg flex-shrink-0"
    />
    <div class="flex-1 min-w-0">
      <h3 class="font-medium text-foreground truncate">{{ dish.name }}</h3>
      <p class="text-sm text-muted-foreground mt-1 line-clamp-2">
        {{ dish.description || '暂无描述' }}
      </p>
      <div class="flex items-center justify-between mt-3">
        <span class="text-lg font-semibold text-primary">
          {{ formatPrice(dish.price) }}
        </span>
        <div class="flex items-center gap-2">
          <button
            v-if="quantity && quantity > 0"
            @click="emit('remove')"
            class="p-1.5 rounded-full bg-muted hover:bg-muted/80 transition-colors"
          >
            <Minus class="h-4 w-4 text-foreground" />
          </button>
          <span
            v-if="quantity && quantity > 0"
            class="w-8 text-center font-medium text-foreground"
          >
            {{ quantity }}
          </span>
          <button
            @click="emit('add')"
            class="p-1.5 rounded-full bg-primary hover:bg-primary/90 transition-colors"
          >
            <Plus class="h-4 w-4 text-primary-foreground" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
