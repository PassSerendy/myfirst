<script setup lang="ts">
import { ref, watch } from 'vue'
import { CheckCircle, XCircle, AlertCircle, Info } from 'lucide-vue-next'

const props = defineProps<{
  show: boolean
  type?: 'success' | 'error' | 'warning' | 'info'
  message: string
  duration?: number
}>()

const emit = defineEmits<{
  close: []
}>()

const icons = {
  success: CheckCircle,
  error: XCircle,
  warning: AlertCircle,
  info: Info
}

const colors = {
  success: 'bg-green-50 text-green-800 border-green-200',
  error: 'bg-red-50 text-red-800 border-red-200',
  warning: 'bg-yellow-50 text-yellow-800 border-yellow-200',
  info: 'bg-blue-50 text-blue-800 border-blue-200'
}

const iconColors = {
  success: 'text-green-500',
  error: 'text-red-500',
  warning: 'text-yellow-500',
  info: 'text-blue-500'
}

watch(() => props.show, (newVal) => {
  if (newVal && props.duration !== 0) {
    setTimeout(() => {
      emit('close')
    }, props.duration || 3000)
  }
})
</script>

<template>
  <Teleport to="body">
    <Transition name="toast">
      <div
        v-if="show"
        class="fixed top-4 left-1/2 -translate-x-1/2 z-[100]"
      >
        <div
          :class="[
            'flex items-center gap-3 px-4 py-3 rounded-lg border shadow-lg',
            colors[type || 'info']
          ]"
        >
          <component
            :is="icons[type || 'info']"
            :class="['h-5 w-5', iconColors[type || 'info']]"
          />
          <span class="text-sm font-medium">{{ message }}</span>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}
</style>
