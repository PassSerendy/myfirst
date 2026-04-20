import request from '@/utils/request'
import type {
  User,
  UserDTO,
  LoginDTO,
  LoginResponse,
  Merchant,
  Category,
  Dish,
  CartItem,
  Address,
  Order,
  OrderDTO,
  PageResult,
} from '@/types'

// ================== 用户相关 API ==================
export const userApi = {
  login: (data: LoginDTO) => request.post<LoginResponse>('/user/login', data),
  register: (data: UserDTO) => request.post<User>('/user/register', data),
  getCurrentUser: () => request.get<User>('/user/current'),
  updateUser: (data: Partial<User>) => request.put<User>('/user', data),
  updatePassword: (oldPassword: string, newPassword: string) =>
    request.put<void>('/user/password', { oldPassword, newPassword }),
  getAllUsers: (page = 1, size = 10) => 
    request.get<PageResult<User>>(`/user/list?page=${page}&size=${size}`),
  updateUserStatus: (id: number, status: number) =>
    request.put<void>(`/user/${id}/status`, { status }),
}

// ================== 商家相关 API ==================
export const merchantApi = {
  getList: (page = 1, size = 10) =>
    request.get<PageResult<Merchant>>(`/merchant/list?page=${page}&size=${size}`),
  getById: (id: number) => request.get<Merchant>(`/merchant/${id}`),
  create: (data: Partial<Merchant>) => request.post<Merchant>('/merchant', data),
  update: (data: Partial<Merchant>) => request.put<Merchant>('/merchant', data),
  delete: (id: number) => request.delete<void>(`/merchant/${id}`),
  updateStatus: (id: number, status: number) =>
    request.put<void>(`/merchant/${id}/status`, { status }),
  getCurrentMerchant: () => request.get<Merchant>('/merchant/current'),
}

// ================== 分类相关 API ==================
export const categoryApi = {
  getByMerchant: (merchantId: number) =>
    request.get<Category[]>(`/category/merchant/${merchantId}`),
  create: (data: Partial<Category>) => request.post<Category>('/category', data),
  update: (data: Partial<Category>) => request.put<Category>('/category', data),
  delete: (id: number) => request.delete<void>(`/category/${id}`),
  updateSort: (id: number, sort: number) =>
    request.put<void>(`/category/${id}/sort`, { sort }),
}

// ================== 菜品相关 API ==================
export const dishApi = {
  getByMerchant: (merchantId: number) =>
    request.get<Dish[]>(`/dish/merchant/${merchantId}`),
  getByCategory: (categoryId: number) =>
    request.get<Dish[]>(`/dish/category/${categoryId}`),
  getById: (id: number) => request.get<Dish>(`/dish/${id}`),
  create: (data: Partial<Dish>) => request.post<Dish>('/dish', data),
  update: (data: Partial<Dish>) => request.put<Dish>('/dish', data),
  delete: (id: number) => request.delete<void>(`/dish/${id}`),
  updateStatus: (id: number, status: number) =>
    request.put<void>(`/dish/${id}/status`, { status }),
  search: (keyword: string, merchantId?: number) =>
    request.get<Dish[]>(`/dish/search?keyword=${keyword}${merchantId ? `&merchantId=${merchantId}` : ''}`),
}

// ================== 购物车相关 API ==================
export const cartApi = {
  getItems: () => request.get<CartItem[]>('/cart'),
  addItem: (dishId: number, merchantId: number, quantity = 1) =>
    request.post<CartItem>('/cart', { dishId, merchantId, quantity }),
  updateQuantity: (id: number, quantity: number) =>
    request.put<CartItem>(`/cart/${id}`, { quantity }),
  removeItem: (id: number) => request.delete<void>(`/cart/${id}`),
  clearCart: () => request.delete<void>('/cart/clear'),
  clearByMerchant: (merchantId: number) =>
    request.delete<void>(`/cart/merchant/${merchantId}`),
}

// ================== 地址相关 API ==================
export const addressApi = {
  getList: () => request.get<Address[]>('/address'),
  getById: (id: number) => request.get<Address>(`/address/${id}`),
  create: (data: Partial<Address>) => request.post<Address>('/address', data),
  update: (data: Partial<Address>) => request.put<Address>('/address', data),
  delete: (id: number) => request.delete<void>(`/address/${id}`),
  setDefault: (id: number) => request.put<void>(`/address/${id}/default`),
}

// ================== 订单相关 API ==================
export const orderApi = {
  create: (data: OrderDTO) => request.post<Order>('/order', data),
  getById: (id: number) => request.get<Order>(`/order/${id}`),
  getUserOrders: (page = 1, size = 10, status?: string) =>
    request.get<PageResult<Order>>(`/order/user?page=${page}&size=${size}${status ? `&status=${status}` : ''}`),
  getMerchantOrders: (page = 1, size = 10, status?: string) =>
    request.get<PageResult<Order>>(`/order/merchant?page=${page}&size=${size}${status ? `&status=${status}` : ''}`),
  getAllOrders: (page = 1, size = 10, status?: string) =>
    request.get<PageResult<Order>>(`/order/list?page=${page}&size=${size}${status ? `&status=${status}` : ''}`),
  updateStatus: (id: number, status: string) =>
    request.put<void>(`/order/${id}/status`, { status }),
  cancel: (id: number) => request.put<void>(`/order/${id}/cancel`),
  pay: (id: number) => request.put<void>(`/order/${id}/pay`),
}
