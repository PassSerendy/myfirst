import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 布局组件
const CustomerLayout = () => import('@/layouts/CustomerLayout.vue')
const MerchantLayout = () => import('@/layouts/MerchantLayout.vue')
const AdminLayout = () => import('@/layouts/AdminLayout.vue')

// 公共页面
const Login = () => import('@/views/auth/Login.vue')
const Register = () => import('@/views/auth/Register.vue')

// 顾客端页面
const Home = () => import('@/views/customer/Home.vue')
const MerchantDetail = () => import('@/views/customer/MerchantDetail.vue')
const Cart = () => import('@/views/customer/Cart.vue')
const Checkout = () => import('@/views/customer/Checkout.vue')
const Orders = () => import('@/views/customer/Orders.vue')
const OrderDetail = () => import('@/views/customer/OrderDetail.vue')
const Profile = () => import('@/views/customer/Profile.vue')
const AddressList = () => import('@/views/customer/AddressList.vue')

// 商家端页面
const MerchantDashboard = () => import('@/views/merchant/Dashboard.vue')
const MerchantDishes = () => import('@/views/merchant/Dishes.vue')
const MerchantCategories = () => import('@/views/merchant/Categories.vue')
const MerchantOrders = () => import('@/views/merchant/Orders.vue')
const MerchantSettings = () => import('@/views/merchant/Settings.vue')

// 管理员页面
const AdminDashboard = () => import('@/views/admin/Dashboard.vue')
const AdminUsers = () => import('@/views/admin/Users.vue')
const AdminMerchants = () => import('@/views/admin/Merchants.vue')
const AdminOrders = () => import('@/views/admin/Orders.vue')

const routes: RouteRecordRaw[] = [
  // 认证页面
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { guest: true },
  },

  // 顾客端
  {
    path: '/',
    component: CustomerLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: Home,
      },
      {
        path: 'merchant/:id',
        name: 'MerchantDetail',
        component: MerchantDetail,
      },
      {
        path: 'cart',
        name: 'Cart',
        component: Cart,
        meta: { requiresAuth: true },
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: Checkout,
        meta: { requiresAuth: true },
      },
      {
        path: 'orders',
        name: 'Orders',
        component: Orders,
        meta: { requiresAuth: true },
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: OrderDetail,
        meta: { requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { requiresAuth: true },
      },
      {
        path: 'addresses',
        name: 'AddressList',
        component: AddressList,
        meta: { requiresAuth: true },
      },
    ],
  },

  // 商家端
  {
    path: '/merchant',
    component: MerchantLayout,
    meta: { requiresAuth: true, role: 'MERCHANT' },
    children: [
      {
        path: '',
        name: 'MerchantDashboard',
        component: MerchantDashboard,
      },
      {
        path: 'dishes',
        name: 'MerchantDishes',
        component: MerchantDishes,
      },
      {
        path: 'categories',
        name: 'MerchantCategories',
        component: MerchantCategories,
      },
      {
        path: 'orders',
        name: 'MerchantOrders',
        component: MerchantOrders,
      },
      {
        path: 'settings',
        name: 'MerchantSettings',
        component: MerchantSettings,
      },
    ],
  },

  // 管理员端
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: AdminDashboard,
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers,
      },
      {
        path: 'merchants',
        name: 'AdminMerchants',
        component: AdminMerchants,
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: AdminOrders,
      },
    ],
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  // 尝试获取用户信息
  if (userStore.token && !userStore.user) {
    try {
      await userStore.fetchCurrentUser()
    } catch {
      // 获取失败，token 可能已过期
    }
  }

  // 已登录用户访问 guest 页面
  if (to.meta.guest && userStore.isLoggedIn) {
    if (userStore.isAdmin) {
      return next('/admin')
    } else if (userStore.isMerchant) {
      return next('/merchant')
    }
    return next('/')
  }

  // 需要登录的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return next('/login')
  }

  // 角色验证
  if (to.meta.role) {
    const role = to.meta.role as string
    if (role === 'ADMIN' && !userStore.isAdmin) {
      return next('/')
    }
    if (role === 'MERCHANT' && !userStore.isMerchant) {
      return next('/')
    }
  }

  next()
})

export default router
