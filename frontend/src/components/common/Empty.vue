<script setup lang="ts">
import { Package } from 'lucide-vue-next'

defineProps<{
  title?: string
  description?: string
}>()
</script>

<template>
  <div class="flex flex-col items-center justify-center py-12 text-center">
    <div class="rounded-full bg-muted p-4 mb-4">
      <Package class="h-8 w-8 text-muted-foreground" />
    </div>
    <h3 class="text-lg font-medium text-foreground mb-1">
      {{ title || '暂无数据' }}
    </h3>
    <p v-if="description" class="text-sm text-muted-foreground max-w-sm">
      {{ description }}
    </p>
    <slot />
  </div>
</template>
