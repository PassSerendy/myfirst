import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { CartItem, Dish } from '@/types'
import { cartApi } from '@/api'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const loading = ref(false)

  const totalItems = computed(() => 
    items.value.reduce((sum, item) => sum + item.quantity, 0)
  )

  const totalAmount = computed(() =>
    items.value.reduce((sum, item) => {
      const price = item.dish?.price || 0
      return sum + price * item.quantity
    }, 0)
  )

  const itemsByMerchant = computed(() => {
    const grouped: Record<number, CartItem[]> = {}
    items.value.forEach(item => {
      if (!grouped[item.merchantId]) {
        grouped[item.merchantId] = []
      }
      grouped[item.merchantId].push(item)
    })
    return grouped
  })

  async function fetchItems() {
    loading.value = true
    try {
      items.value = await cartApi.getItems()
    } catch (error) {
      console.error('获取购物车失败:', error)
    } finally {
      loading.value = false
    }
  }

  async function addItem(dish: Dish, merchantId: number, quantity = 1) {
    try {
      const existingItem = items.value.find(
        item => item.dishId === dish.id && item.merchantId === merchantId
      )
      
      if (existingItem) {
        await updateQuantity(existingItem.id, existingItem.quantity + quantity)
      } else {
        const newItem = await cartApi.addItem(dish.id, merchantId, quantity)
        newItem.dish = dish
        items.value.push(newItem)
      }
    } catch (error) {
      console.error('添加购物车失败:', error)
      throw error
    }
  }

  async function updateQuantity(id: number, quantity: number) {
    if (quantity <= 0) {
      return removeItem(id)
    }
    
    try {
      await cartApi.updateQuantity(id, quantity)
      const item = items.value.find(i => i.id === id)
      if (item) {
        item.quantity = quantity
      }
    } catch (error) {
      console.error('更新数量失败:', error)
      throw error
    }
  }

  async function removeItem(id: number) {
    try {
      await cartApi.removeItem(id)
      items.value = items.value.filter(item => item.id !== id)
    } catch (error) {
      console.error('删除失败:', error)
      throw error
    }
  }

  async function clearCart() {
    try {
      await cartApi.clearCart()
      items.value = []
    } catch (error) {
      console.error('清空购物车失败:', error)
      throw error
    }
  }

  async function clearByMerchant(merchantId: number) {
    try {
      await cartApi.clearByMerchant(merchantId)
      items.value = items.value.filter(item => item.merchantId !== merchantId)
    } catch (error) {
      console.error('清空失败:', error)
      throw error
    }
  }

  return {
    items,
    loading,
    totalItems,
    totalAmount,
    itemsByMerchant,
    fetchItems,
    addItem,
    updateQuantity,
    removeItem,
    clearCart,
    clearByMerchant,
  }
})
