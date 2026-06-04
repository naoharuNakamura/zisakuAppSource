<script setup lang="ts">

import { ref, computed, onMounted } from "vue";
import { storeToRefs } from 'pinia';
import { useRouter } from "vue-router";
// 必要なインポートを追加
import { apiService } from "../services/api";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAuthStore();
const { currentUser } = storeToRefs(authStore);

// 1. ストアの値を直接 computed で監視することでリアクティブにする
const isNewSignUp = computed(() => !currentUser.value);


// 入力フォームの ref
const userPassword = ref("");
const reUserPassword = ref("");
const userName = ref("");
const userEmail = ref("");
const userPhone = ref("");

const handleSubmit = async () => {
    const isValid = await validateForm();
    if (!isValid) {
        return; // エラーがあればここで処理をストップ
    }

    try {
        // userId を一度定義し、条件によって含めるか決める
        const userData: any = {
            userPassword: userPassword.value,
            userName: userName.value,
            userEmail: userEmail.value,
            userPhone: userPhone.value
        };

        if (!isNewSignUp.value && currentUser.value) {
            userData.userId = currentUser.value.userId;
        }

        if (userPassword.value) {
            userData.userPassword = userPassword.value;
        }

        if (isNewSignUp.value) {
            await apiService.signup(userData);
            router.push("/login");
        } else {
            const response = await apiService.updateProfile(userData);
            currentUser.value = response.data;
            router.push("/mypage");
        }
    } catch (error: any) {
        // 💡 確実にあらゆる場所からステータスコードを探し出し、数値(Number)に変換する
        const statusCode = error.response?.status || error.status || 0;

        if (Number(statusCode) === 409) {
            // 409（重複エラー）を検知！バナーは出さず、入力欄の下を赤文字にするだけ
            errors.value.userEmail = "このメールアドレスは既に登録されています";
        } else {
            // 本当にサーバーが落ちている時（500）など、想定外の時だけバナーを出す
            console.error("サーバーエラー:", error);
            alert("処理に失敗しました。再度お試しください。");
        }
    }
};

// 2. 編集モード（既存ユーザーがいる）なら初期値をセット
onMounted(() => {
    if (currentUser.value) {
        userName.value = currentUser.value.userName;
        userEmail.value = currentUser.value.userEmail;
        userPhone.value = currentUser.value.userPhone;
    }
});

const errors = ref({
    userPassword: "",
    reUserPassword: "",
    userName: "",
    userEmail: "",
    userPhone: ""
});

const validateForm = async () => {
    // 一度エラーメッセージをすべてクリア
    errors.value = {
        userPassword: "",
        reUserPassword: "",
        userName: "",
        userEmail: "",
        userPhone: ""
    };

    let isValid = true;

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;

    if (isNewSignUp.value && !userPassword.value) {
        // 💡 新規登録で空の場合はエラー
        errors.value.userPassword = "パスワードを入力してください";
        isValid = false;
    } else if (userPassword.value && !passwordRegex.test(userPassword.value)) {
        // 💡 新規・更新問わず、「入力がある場合」のみ強度チェックを行う
        errors.value.userPassword = "大文字・小文字・数字・特殊文字をすべて含む8文字以上で入力してください";
        isValid = false;
    }

    // ③ パスワード再入力チェック
    // 💡 「パスワードに入力がある場合」のみ一致チェックを行う
    if (userPassword.value && userPassword.value !== reUserPassword.value) {
        errors.value.reUserPassword = "パスワードが一致しません";
        isValid = false;
    }
    // ④ お名前（空チェック）
    if (!userName.value.trim()) {
        errors.value.userName = "お名前を入力してください";
        isValid = false;
    }

    // ⑤ メールアドレス：形式チェック
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!userEmail.value) {
        errors.value.userEmail = "メールアドレスを入力してください";
        isValid = false;
    } else if (!emailRegex.test(userEmail.value)) {
        errors.value.userEmail = "有効なメールアドレスの形式で入力してください";
        isValid = false;
    } else {
        // --- ここから重複チェック ---
        try {
            if (!isNewSignUp.value && userEmail.value === currentUser?.value?.userEmail) {
            } else {
                // Spring BootのAPIを叩く
                const response = await apiService.getEmailExists(userEmail.value);

                if (response.data === true) {
                    errors.value.userEmail = "このメールアドレスは既に登録されています";
                    isValid = false;
                }
            }
        } catch (error) {
            console.error("重複チェックに失敗しました", error);
            // 500エラーなどが起きた場合はとりあえずここに入る
        }
    }

    // ⑥ 電話番号：形式チェック（ハイフン有無両対応）
    const phoneRegex = /^0\d{1,4}-\d{1,4}-\d{4}$|^0\d{9,10}$/;
    if (!userPhone.value) {
        errors.value.userPhone = "電話番号を入力してください";
        isValid = false;
    } else if (!phoneRegex.test(userPhone.value)) {
        errors.value.userPhone = "有効な電話番号（例: 09012345678）を入力してください";
        isValid = false;
    }

    return isValid;
};

</script>

<template>
    <div class="signup-container">
        <div class="signup-card">
            <p class="signup-subtitle">
                {{ isNewSignUp ? "会員情報を入力してください" : "会員情報を修正してください" }}
            </p>

            <form class="signup-form" @submit.prevent="handleSubmit">
                <div class="form-grid">
                    <div class="grid-column">
                        <div class="form-group">
                            <label for="userName">お名前</label>
                            <input v-model="userName" id="userName" type="text" placeholder="名前を入力してください"
                                class="form-control" :class="{ 'input-error': errors.userName }" required />
                            <span v-if="errors.userName" class="error-text">{{ errors.userName }}</span>
                        </div>

                        <div class="form-group">
                            <label for="userEmail">メールアドレス</label>
                            <input v-model="userEmail" id="userEmail" type="text" placeholder="メールアドレスを入力してください"
                                class="form-control" :class="{ 'input-error': errors.userEmail }" required />
                            <span v-if="errors.userEmail" class="error-text">{{ errors.userEmail }}</span>
                        </div>

                        <div class="form-group">
                            <label for="userPhone">電話番号</label>
                            <input v-model="userPhone" id="userPhone" type="text" placeholder="電話番号を入力してください"
                                class="form-control" :class="{ 'input-error': errors.userPhone }" required />
                            <span v-if="errors.userPhone" class="error-text">{{ errors.userPhone }}</span>
                        </div>
                    </div>
                    <div class="grid-column">
                        <div class="form-group">
                            <label for="userPassword">パスワード {{ isNewSignUp ? "" : "（変更する場合のみ）" }}</label>

                            <input v-model="userPassword" id="userPassword" type="password"
                                :placeholder="isNewSignUp ? 'パスワードを入力してください' : '変更する場合のみ入力してください'" class="form-control"
                                :class="{ 'input-error': errors.userPassword }" :required="isNewSignUp" />

                            <span v-if="errors.userPassword" class="error-text">{{ errors.userPassword }}</span>
                        </div>

                        <div class="form-group">
                            <label for="reUserPassword">パスワード（再入力）</label>

                            <input v-model="reUserPassword" id="reUserPassword" type="password"
                                :placeholder="isNewSignUp ? 'もう一度パスワードを入力してください' : '変更する場合のみ入力してください'"
                                class="form-control" :class="{ 'input-error': errors.reUserPassword }"
                                :disabled="!userPassword" :required="!!userPassword" />

                            <span v-if="errors.reUserPassword" class="error-text">{{ errors.reUserPassword }}</span>
                        </div>
                    </div>
                </div>
                <button type="submit" id="signup-btn" class="signup-btn">
                    {{ isNewSignUp ? "登録" : "更新" }}
                </button>
            </form>
        </div>
    </div>
</template>

<style scoped>
.signup-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh;
    background-color: #fafafa;
    padding: 20px;
}

.signup-card {
    width: min(100%, 700px);
    /* 2カラムが綺麗に並ぶよう、PC時は最大幅を少し広めに確保 */
    max-width: 700px;
    padding: 32px;
    background-color: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    box-sizing: border-box;
}

.signup-subtitle {
    font-size: 16px;
    font-weight: bold;
    color: #4a5568;
    text-align: center;
    margin-bottom: 24px;
}

.signup-form {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* 💻 PC・タブレット環境（初期状態：横並び） */
.form-grid {
    display: flex;
    gap: 24px;
    width: 100%;
}

.grid-column {
    flex: 1;
    /* 左右のカラムを50%ずつの等幅にする */
    display: flex;
    flex-direction: column;
    gap: 18px;
    /* 縦方向の入力欄同士の間隔 */
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.form-group label {
    font-size: 13px;
    font-weight: 600;
    color: #4a5568;
    min-height: 18px;
    /* ラベルのない項目があっても高さがズレないように保護 */
}

.form-control {
    padding: 12px;
    font-size: 15px;
    border: 1px solid #cbd5e1;
    border-radius: 6px;
    outline: none;
    box-sizing: border-box;
    width: 100%;
    transition: border-color 0.2s, background-color 0.2s;
}

.form-control:focus {
    border-color: #3182ce;
}

/* エラー時の入力欄スタイル */
.form-control.input-error {
    border-color: #e53e3e;
    background-color: #fff5f5;
}

/* エラーメッセージのテキスト */
.error-text {
    color: #e53e3e;
    font-size: 12px;
    font-weight: 500;
    margin-top: 2px;
}

.signup-btn {
    margin-top: 10px;
    width: inherit;
    padding: 14px;
    font-size: 16px;
    font-weight: bold;
    color: #ffffff;
    background-color: #3182ce;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.signup-btn:hover {
    background-color: #2b6cb0;
}

/* 📱 スマホ環境（画面幅が440px以下になったら縦一列に変化） */
@media screen and (max-width: 440px) {
    .signup-card {
        padding: 20px;
    }

    .form-grid {
        flex-direction: column;
        /* 左右並びを縦並びに切り替え */
        gap: 18px;
    }

    .grid-column {
        gap: 18px;
    }
}
</style>