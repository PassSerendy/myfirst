// 用户相关类型
export interface User {
  id: number
  username: string
  phone: string
  email?: string
  avatar?: string
  role: 'USER' | 'MERCHANT' | 'ADMIN'
  status: number
  createTime?: string
  updateTime?: string
}

export interface UserDTO {
  username: string
  password: string
  phone?: string
  email?: string
}

export interface LoginDTO {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  user: User
}

// 商家相关类型
export interface Merchant {
  id: number
  userId: number
  name: string
  logo?: string
  description?: string
  address: string
  phone: string
  businessHours?: string
  rating?: number
  status: number
  createTime?: string
  updateTime?: string
}

// 分类相关类型
export interface Category {
  id: number
  merchantId: number
  name: string
  sort: number
  status: number
}

// 菜品相关类型
export interface Dish {
  id: number
  categoryId: number
  merchantId: number
  name: string
  price: number
  image?: string
  description?: string
  sales: number
  status: number
  createTime?: string
  updateTime?: string
}

// 购物车相关类型
export interface CartItem {
  id: number
  userId: number
  dishId: number
  merchantId: number
  quantity: number
  dish?: Dish
}

// 地址相关类型
export interface Address {
  id: number
  userId: number
  contactName: string
  contactPhone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: number
}

// 订单相关类型
export interface Order {
  id: number
  userId: number
  merchantId: number
  addressId: number
  orderNo: string
  totalAmount: number
  status: OrderStatus
  remark?: string
  createTime?: string
  updateTime?: string
  items?: OrderItem[]
  address?: Address
  merchant?: Merchant
}

export interface OrderItem {
  id: number
  orderId: number
  dishId: number
  dishName: string
  dishImage?: string
  price: number
  quantity: number
}

export interface OrderDTO {
  merchantId: number
  addressId: number
  remark?: string
  items: {
    dishId: number
    quantity: number
  }[]
}

export type OrderStatus = 
  | 'PENDING'      // 待支付
  | 'PAID'         // 已支付
  | 'CONFIRMED'    // 已确认
  | 'PREPARING'    // 制作中
  | 'DELIVERING'   // 配送中
  | 'COMPLETED'    // 已完成
  | 'CANCELLED'    // 已取消

export const OrderStatusMap: Record<OrderStatus, string> = {
  PENDING: '待支付',
  PAID: '已支付',
  CONFIRMED: '已确认',
  PREPARING: '制作中',
  DELIVERING: '配送中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}

export const OrderStatusColor: Record<OrderStatus, string> = {
  PENDING: 'badge-warning',
  PAID: 'badge-info',
  CONFIRMED: 'badge-info',
  PREPARING: 'badge-info',
  DELIVERING: 'badge-info',
  COMPLETED: 'badge-success',
  CANCELLED: 'badge-danger',
}

// API 响应类型
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
