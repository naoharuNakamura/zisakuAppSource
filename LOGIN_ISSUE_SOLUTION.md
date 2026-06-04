# ログイン問題 - 解決ガイド

## 🔴 問題の概要

- **ローカル環境**: ログインで404エラー  
- **デプロイ後**: ログインで401エラー

---

## ✅ 実施済みの修正

### 1. **vite.config.ts に開発用プロキシ設定を追加** ✓
```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

### 2. **application.yml を改善** ✓
```yaml
# 修正前: /home/database.db（Windowsでは存在しないパス）
# 修正後: ./database.db（プロジェクトディレクトリ内）

datasource:
  url: jdbc:sqlite:${SQLITE_DB_PATH:./database.db}

# JWT_SECRETを環境変数化
jwt:
  secret: ${JWT_SECRET:your-dev-key-here}
```

---

## 📝 ローカル環境でのテスト手順

### ステップ1: バックエンドビルド＆起動
```bash
cd zisaku-backend
./gradlew clean bootRun
```

**コンソール出力が以下のようになることを確認:**
```
Tomcat started on port 8080 (http) with context path '/'
Started ZisakuBackendApplication in X.XXX seconds
```

### ステップ2: テストデータを挿入
**別のターミナルで:**
```bash
cd zisaku-backend
python insert_data.py
```

**出力:**
```
✨ 成功: ジャンル最適化された写真・店名・住所を含む全7,000件のデータをインサートしました！
```

**生成されるテストユーザー:**
- Email: `random1@example.com` ～ `random19@example.com`
- Password: `Pass0001!` ～ `Pass0019!` (パターン: `Pass{i:04d}!`)

### ステップ3: フロントエンド起動
**別のターミナルで:**
```bash
cd zisaku-frontend
npm run dev
```

**出力:**
```
VITE vX.X.X ready in XXX ms
➜ Local: http://localhost:5173/
```

### ステップ4: ブラウザでテスト
1. http://localhost:5173 にアクセス
2. ログイン画面で以下の認証情報を入力:
   - **Email**: `random1@example.com`
   - **Password**: `Pass0001!`
3. ✅ ログイン成功 → 検索画面に遷移

---

## 🔧 デバッグ: ローカルでまだ404が出る場合

### ステップA: バックエンドが起動しているか確認
```powershell
# PowerShell でテスト
$headers = @{"Content-Type" = "application/json"}
$body = @{userEmail = "random1@example.com"; userPassword = "Pass0001!"} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:8080/api/m_user/login -Method POST -Headers $headers -Body $body
```

**期待される結果:**
- ✅ 200 OK → 401で「Invalid email or password」エラーの場合、テストデータ未挿入
- ❌ 404 Not Found → バックエンド停止中

### ステップB: ブラウザの開発者ツールで確認（F12キー）

**コンソールタブで実行:**
```javascript
// API テスト
fetch('/api/m_user/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userEmail: 'random1@example.com',
    userPassword: 'Pass0001!'
  })
})
.then(r => {
  console.log('Status:', r.status);
  return r.json();
})
.then(data => console.log('Response:', data))
.catch(err => console.error('Error:', err));
```

**Network タブで確認:**
- `POST /api/m_user/login` リクエストが見える
- レスポンスが JSON 形式で `{"token": "...", "user": {...}}` 

### ステップC: バックエンドのログでデバッグ

バックエンドコンソールに以下が表示されることを確認:
```
====== [デバッグ] ログイン試行中のメール: random1@example.com ======
====== [デバッグ] DBから取得したユーザー: 〇〇 〇〇 ======
====== [デバッグ] DBから取得したパスワードハッシュ: $2a$10$... ======
```

---

## 🌐 本番環境（Azure）での対応

### 対応1: JWT_SECRET を設定

**Azure Web Apps Application Settings に追加:**
```
JWT_SECRET = your-secure-256bit-key-here-minimum
```

**生成例:**
```bash
# Linux/Mac
openssl rand -base64 32

# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

### 対応2: SQLITE_DB_PATH を設定（データベース永続化）

デプロイ環境によって異なります：

**Azure Web Apps の場合:**
```
SQLITE_DB_PATH = /home/data/database.db
```

**Azure Static Web Apps + バックエンド API の場合:**
```
SQLITE_DB_PATH = /tmp/database.db
```

### 対応3: テストユーザーをDB に挿入

デプロイ環境で `insert_data.py` を実行するか、Azure SQL Database等を使用する場合は、スクリプトを適応させてください。

---

## 📊 認証フロー確認図

```
ローカル開発環境:
┌─────────────────────────────────────────────────────────────┐
│ Browser: localhost:5173                                     │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ Vue App (siteLogin.vue)                              │   │
│ │  ↓ POST /api/m_user/login                            │   │
│ │  (axios → baseURL="/")                               │   │
│ └──────────────────────────────────────────────────────┘   │
│     ↓ [vite.config.ts proxy]                               │
│     リクエストをリダイレクト                                  │
│     http://localhost:8080/api/m_user/login へ              │
│     ↓                                                      │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ Backend: localhost:8080 (Spring Boot)                │   │
│ │ ✓ UserController.login()                             │   │
│ │ ✓ CustomUserDetailsService (DB から user 取得)       │   │
│ │ ✓ Spring Security で パスワード比較                   │   │
│ │ ✓ JWT Token 生成                                    │   │
│ │  ← 200 OK { token, user }                            │   │
│ └──────────────────────────────────────────────────────┘   │
│     ↓                                                      │
│ ┌──────────────────────────────────────────────────────┐   │
│ │ auth.ts                                              │   │
│ │ ✓ token を localStorage に保存                        │   │
│ │ ✓ API インターセプター が次回のリクエストに自動付与  │   │
│ └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ チェックリスト

- [ ] vite.config.ts でプロキシ設定が有効か確認
- [ ] application.yml で JWT_SECRET が環境変数化されているか確認
- [ ] ローカルでテストデータ（insert_data.py）を実行
- [ ] ブラウザで http://localhost:5173 でログインテスト
- [ ] デプロイ環境で JWT_SECRET を Application Settings に設定
- [ ] デプロイ環境でテストユーザーが存在することを確認
- [ ] ブラウザの Network タブで API レスポンスが正しいことを確認

---

## 🆘 トラブルシューティング

| 症状 | 原因 | 解決方法 |
|---|---|---|
| ローカルで 404 | バックエンド停止 | `./gradlew bootRun` で起動 |
| ローカルで 404 | プロキシ設定未反映 | npm run dev を再実行 |
| ローカルで 401 | テストユーザー未作成 | `python insert_data.py` 実行 |
| 本番で 401 | JWT_SECRET 未設定 | Azure Settings に JWT_SECRET 追加 |
| 本番で 401 | DB にユーザーがない | デプロイ後にテストデータを挿入 |
| パスワードエラー | メール/パスワード間違い | Email は大文字小文字区別あり |

---

## 📞 最後に確認すること

**ローカルで動作確認後、本番にデプロイする前に:**

1. ✅ テストユーザーでログイン成功
2. ✅ お気に入り登録・削除が機能
3. ✅ レストラン検索が機能
4. ✅ ブラウザ開発者ツール F12 で Network タブにエラーがない
5. ✅ Azure の Application Insights でエラーログが出ていない

問題が解決したら、このファイルの最初の行を以下に更新してください：

```markdown
# ✅ ログイン問題 - 解決完了
```
