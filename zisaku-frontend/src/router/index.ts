import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// components から各画面（コンポーネント）をインポート
import siteLogin from '../components/siteLogin.vue'
import siteSignUp from '../components/siteSignUp.vue'
import siteSearch from '../components/siteSearch.vue'
import siteResult from '../components/siteResult.vue'
import siteResultDetail from '../components/siteResultDetail.vue'
import siteMyPage from '../components/siteMyPage.vue'

// パスと画面の紐付け定義
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login' // 初期アクセス時はログイン画面にリダイレクト
  },
  {
    path: '/login',
    name: 'Login',
    component: siteLogin
  },
  {
    path: '/signup',
    name: 'SignUp',
    component: siteSignUp
  },
  {
    path: '/search',
    name: 'Search',
    component: siteSearch
  },
  {
    path: '/result',
    name: 'Result',
    component: siteResult
  },
  {
    path: '/result-detail/:restaurantId', // 店舗IDをURLパラメータとして受け取れるように設定
    name: 'ResultDetail',
    component: siteResultDetail
  },
  {
    path: '/mypage',
    name: 'MyPage',
    component: siteMyPage
  }
]



export const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from) => {
  const authStore = useAuthStore();
  // マイページへのアクセス、かつ未ログインの場合
  if (to.path !== '/login' && to.path !== '/signup' && !authStore.isLoggedIn) {
    return "/login";
  }
});

export const goToResult = () => {
  router.push('/result')
}

