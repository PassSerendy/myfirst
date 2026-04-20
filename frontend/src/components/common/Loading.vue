<script setup lang="ts">
defineProps<{
  size?: 'sm' | 'md' | 'lg'
  text?: string
}>()
</script>

<template>
  <div class="flex flex-col items-center justify-center gap-3">
    <div
      :class="[
        'animate-spin rounded-full border-2 border-muted border-t-primary',
        {
          'h-5 w-5': size === 'sm',
          'h-8 w-8': size === 'md' || !size,
          'h-12 w-12': size === 'lg'
        }
      ]"
    />
    <span v-if="text" class="text-sm text-muted-foreground">{{ text }}</span>
  </div>
</template>
