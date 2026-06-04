// src/stores/auth.ts

import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { apiService } from '../services/api';

export interface Restaurant {
    restaurantId: number;
    restaurantName: string;
    restaurantImg: string;
    restaurantRating: number;
    restaurantGenre: string;
    restaurantPriceRange: string;
    restaurantArea: string;
    restaurantOpenHour: string;
    restaurantCloseHour: string;
    restaurantAddress: string;
    restaurantPhone: string;
    restaurantUrl: string;
    restaurantClosedDays: string;
}

// パスワードなどの機密情報を持たない、安全なユーザー情報
export interface UserResponse {
    userId: number;
    userName: string;
    userEmail: string;
    userPhone: string;
    favoriteIds?: number[]; // お気に入りの管理用
}

export const useAuthStore = defineStore('auth', () => {
    // ① 状態（State）

    const savedUser = localStorage.getItem('currentUser');
    const currentUser = ref<UserResponse | null>(savedUser ? JSON.parse(savedUser) : null);
    const favoriteRestaurants = ref<Restaurant[]>([]);

    // ② ゲッター（Getters）
    const isLoggedIn = computed(() => currentUser.value !== null);

    // 🌟 復活: お気に入りIDの配列だけを取得する（星マークの点灯判定用）
    const fetchFavoriteIds = async (userId: number) => {
        if (!currentUser.value) return;
        try {
            const response = await apiService.getFavoritesByUserId(userId);
            currentUser.value.favoriteIds = response.data;
            localStorage.setItem('currentUser', JSON.stringify(currentUser.value));
        } catch (error) {
            console.error("お気に入りIDの取得失敗:", error);
        }
    };

    // お気に入り詳細一覧を取得する
    const fetchFavoriteRestaurants = async (userId: number) => {
        try {
            const response = await apiService.getFavoriteDetails(userId);
            favoriteRestaurants.value = response.data;
        } catch (error) {
            console.error("お気に入り詳細の取得失敗:", error);
        }
    };

    // ③ アクション（Actions）

    // 💡 ログイン処理
    // ここで受け取るデータの型を明確に指定します（コンポーネント側で間違えないようにするため）
    const login = async (loginData: { userEmail: string; userPassword: string }) => {
        try {
            const response = await apiService.login(loginData);

            // 💡 サーバーからトークンが返ってくる想定（例: response.data.token）
            // ログインAPIのレスポンス形式を確認してください
            const { token, user } = response.data;

            // トークンを保存（これがないとインターセプターで送信されません）
            localStorage.setItem('token', token);

            currentUser.value = user;

            await fetchFavoriteIds(user.userId);
            await fetchFavoriteRestaurants(user.userId);

            localStorage.setItem('currentUser', JSON.stringify(currentUser.value));
            return true;
        } catch (error) {
            console.error("ログインエラー:", error);
            throw error;
        }
    };

    const updateProfile = async (updateData: UserResponse) => {
        try {
            // ※api.ts の updateProfile がこのデータを送る想定
            const response = await apiService.updateProfile(updateData);
            const updatedUserFromServer: UserResponse = response.data;

            // サーバーから返ってきた最新データをセットし、お気に入りIDをマージ
            updatedUserFromServer.favoriteIds = currentUser.value?.favoriteIds || [];
            currentUser.value = updatedUserFromServer;

            localStorage.setItem('currentUser', JSON.stringify(currentUser.value));
            return true;
        } catch (error) {
            console.error("プロフィール更新エラー:", error);
            throw error;
        }
    };

    const logout = () => {
        currentUser.value = null;
        favoriteRestaurants.value = [];
        localStorage.removeItem('currentUser');
        localStorage.removeItem('token');
    };

    const isFavorite = (restaurantId: number) => {
        return currentUser.value?.favoriteIds?.includes(restaurantId) || false;
    };

    const toggleFavorite = async (restaurantId: number) => {
        if (!currentUser.value) return;

        try {
            await apiService.toggleFavorite({
                userId: currentUser.value.userId,
                restaurantId: restaurantId
            });
            // サーバーから最新状態を再取得
            await fetchFavoriteIds(currentUser.value.userId);
            await fetchFavoriteRestaurants(currentUser.value.userId);
        } catch (error) {
            console.error("お気に入り更新失敗:", error);
        }
    };

    return {
        currentUser,
        isLoggedIn,
        fetchFavoriteIds,
        fetchFavoriteRestaurants,
        favoriteRestaurants,
        login,
        logout,
        updateProfile,
        isFavorite,
        toggleFavorite
    };
});