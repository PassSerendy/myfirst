<script setup lang="ts">
import { X } from 'lucide-vue-next'

defineProps<{
  show: boolean
  title?: string
  width?: string
}>()

const emit = defineEmits<{
  close: []
}>()
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="show"
        class="fixed inset-0 z-50 flex items-center justify-center p-4"
      >
        <div
          class="absolute inset-0 bg-black/50"
          @click="emit('close')"
        />
        <div
          class="relative bg-card rounded-xl shadow-xl max-h-[90vh] overflow-auto"
          :style="{ width: width || '480px', maxWidth: '100%' }"
        >
          <div v-if="title" class="flex items-center justify-between p-4 border-b border-border">
            <h3 class="text-lg font-semibold text-foreground">{{ title }}</h3>
            <button
              @click="emit('close')"
              class="p-1 rounded-lg hover:bg-muted transition-colors"
            >
              <X class="h-5 w-5 text-muted-foreground" />
            </button>
          </div>
          <div class="p-4">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active > div:last-child,
.modal-leave-active > div:last-child {
  transition: transform 0.2s ease;
}

.modal-enter-from > div:last-child,
.modal-leave-to > div:last-child {
  transform: scale(0.95);
}
</style>
