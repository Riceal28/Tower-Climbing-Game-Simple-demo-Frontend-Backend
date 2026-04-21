import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../pages/LoginPage.vue'
import RegisterPage from '../pages/RegisterPage.vue'
import Home from '../pages/Home.vue'
import PlayerViewPage from '../pages/PlayerViewPage.vue'
import BattlePage from '../pages/BattlePage.vue'
import SavePage from '../pages/SavePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginPage
    },
    {
      path: '/register',
      name: 'Register',
      component: RegisterPage
    },
    {
      path: '/home',
      name: 'Home',
      component: Home,
      meta: { requiresAuth: true }
    },
    {
      path: '/player',
      name: 'PlayerView',
      component: PlayerViewPage,
      meta: { requiresAuth: true }
    },
    {
      path: '/battle',
      name: 'Battle',
      component: BattlePage,
      meta: { requiresAuth: true }
    },
    {
      path: '/save',
      name: 'Save',
      component: SavePage,
      meta: { requiresAuth: true }
    }
  ],
})

// Navigation guard to check authentication
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    return '/login'
  }
  
  if ((to.path === '/login' || to.path === '/register') && token) {
    return '/home'
  }
})

export default router
