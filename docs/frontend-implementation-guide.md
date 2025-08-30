# フロントエンド実装ガイド

このドキュメントでは、NippogenプロジェクトにおけるReact Native + Expoでのフロントエンド実装パターンとベストプラクティスを説明します。

## アーキテクチャ

### Feature-based 構造

プロジェクトはfeature-basedアーキテクチャを採用しており、機能別にディレクトリを分割しています。

```
src/features/
├── settings/           # API認証情報管理機能
│   ├── components/     # UIコンポーネント
│   ├── hooks/          # カスタムフック
│   ├── schemas/        # バリデーションスキーマ
│   └── types/          # 型定義
└── shared/             # 共通機能
    ├── utils/          # ユーティリティ
    └── types/          # 共通型定義
```

## 実装パターン（Issue #14 で確立）

### 1. TanStack Form + Zod バリデーション

#### フォーム管理
```typescript
import { useForm } from '@tanstack/react-form'
import { zodValidator } from '@tanstack/zod-form-adapter'

const form = useForm({
  defaultValues: {
    apiKey: '',
    // ... その他フィールド
  },
  
  onSubmit: async ({ value }) => {
    const result = validationSchema.safeParse(value)
    if (!result.success) {
      Alert.alert('バリデーションエラー', result.error.issues[0].message)
      return
    }
    await onSave(result.data)
  },
})
```

#### Zodスキーマ定義
```typescript
import { z } from 'zod'

export const credentialSchema = z.object({
  apiKey: z
    .string()
    .min(1, 'APIキーは必須です')
    .startsWith('prefix_', '正しいプレフィックスが必要です'),
  
  // その他のフィールド
})

export type CredentialFormData = z.infer<typeof credentialSchema>
```

#### フィールド実装
```typescript
<form.Field name="apiKey">
  {(field) => (
    <View>
      <TextInput
        value={field.state.value}
        onChangeText={field.handleChange}
        onBlur={field.handleBlur}
        // ... その他のプロパティ
      />
      {field.state.meta.errors && field.state.meta.errors.length > 0 && (
        <Text className="text-red-500 text-xs">
          {String(field.state.meta.errors[0])}
        </Text>
      )}
    </View>
  )}
</form.Field>
```

### 2. カスタムフックによる状態管理

```typescript
// hooks/useApiCredentials.ts
export function useApiCredentials() {
  const [credentials, setCredentials] = useState<Credential[]>([])
  const [isLoading, setIsLoading] = useState(false)

  const fetchCredentials = async () => {
    // API呼び出し実装
  }

  const saveCredential = async (data: CredentialFormData) => {
    try {
      setIsLoading(true)
      // API呼び出し
      await fetchCredentials() // リフレッシュ
    } catch (error) {
      // エラーハンドリング
    } finally {
      setIsLoading(false)
    }
  }

  return {
    credentials,
    isLoading,
    fetchCredentials,
    saveCredential,
    // ... その他のメソッド
  }
}
```

### 3. API通信パターン

#### Axios インスタンス設定
```typescript
// utils/axiosInstance.ts
import axios from 'axios'

export const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// リクエストインターセプター
axiosInstance.interceptors.request.use((config) => {
  config.headers['X-User-Id'] = 'mock-user-id'
  return config
})
```

#### API クライアント
```typescript
// utils/apiClient.ts
import type { components } from '@/types/api'

type Credential = components['schemas']['CredentialResponseDto']
type CredentialRequest = components['schemas']['CredentialRequestDto']

export const credentialApi = {
  getAll: (): Promise<Credential[]> =>
    axiosInstance.get('/credentials').then(res => res.data),

  create: (data: CredentialRequest): Promise<Credential> =>
    axiosInstance.post('/credentials', data).then(res => res.data),

  update: (id: number, data: CredentialRequest): Promise<Credential> =>
    axiosInstance.put(`/credentials/${id}`, data).then(res => res.data),

  delete: (id: number): Promise<void> =>
    axiosInstance.delete(`/credentials/${id}`).then(() => {}),
}
```

### 4. コンポーネント設計原則

#### 自己完結型コンポーネント
```typescript
interface CredentialFormProps {
  initialData?: Credential | null
  onSave: (data: CredentialFormData) => Promise<void>
  onCancel: () => void
}

export function CredentialForm({ 
  initialData, 
  onSave, 
  onCancel 
}: CredentialFormProps) {
  // フォームロジック、状態管理を内部で完結
  // composition patternは使わずシンプルに実装
}
```

#### エクスポート統合
```typescript
// components/index.ts
export { ApiServiceSelector } from './ApiServiceSelector'
export { CredentialCard } from './CredentialCard'
export { GitHubCredentialForm } from './GitHubCredentialForm'
// ... その他
```

### 5. 型安全性の確保

#### OpenAPI生成型の活用
```typescript
// types/api.ts (自動生成)
import type { components } from '@/types/api'

type GitHubCredential = components['schemas']['GitHubCredentialResponseDto']
type TogglCredential = components['schemas']['TogglCredentialResponseDto']
type NotionCredential = components['schemas']['NotionCredentialResponseDto']
```

#### 型ガード
```typescript
function isGitHubCredential(credential: any): credential is GitHubCredential {
  return credential.type === 'GITHUB'
}
```

## スタイリング

### TailwindCSS (NativeWind)
```typescript
<View className="flex-1 bg-gray-50 p-6">
  <Text className="text-2xl font-bold text-gray-800 mb-6">
    タイトル
  </Text>
  <TouchableOpacity className="bg-primary py-3 px-6 rounded-lg">
    <Text className="text-white text-center font-medium">
      ボタン
    </Text>
  </TouchableOpacity>
</View>
```

### カラーテーマ
- `bg-primary`: プライマリカラー (#267D00)
- `text-gray-800`: メインテキスト
- `text-gray-600`: サブテキスト
- `border-gray-300`: ボーダー

## ナビゲーション

### Expo Router (File-based)
```
app/
├── _layout.tsx         # ルートレイアウト（タブナビゲーション）
├── settings/
│   ├── _layout.tsx     # 設定セクションレイアウト
│   ├── index.tsx       # 設定メイン画面
│   └── api-credentials.tsx # API認証情報画面
└── reports/
    ├── index.tsx       # 日報メイン画面
    └── generate.tsx    # 日報生成画面
```

## エラーハンドリング

### バリデーションエラー
```typescript
const result = schema.safeParse(value)
if (!result.success) {
  const firstError = result.error.issues[0]
  Alert.alert('バリデーションエラー', firstError.message)
  return
}
```

### API エラー
```typescript
try {
  await apiCall()
} catch (error) {
  console.error('API Error:', error)
  Alert.alert('エラー', '処理に失敗しました。もう一度お試しください。')
}
```

## 実装時の考慮点

### TanStack Form v2 互換性
- `form.Provider` は使用しない（React Native非対応）
- `form.Subscribe` のselectorはオブジェクト形式を使用
- エラー表示は `String()` でラップ

### パフォーマンス最適化
- 必要時のみコンポーネントを再レンダリング
- axiosInstance で HTTP設定を一元化
- 適切なローディング状態の表示

### ユーザビリティ
- リアルタイムバリデーション
- 適切なフィードバック（ローディング、エラー、成功）
- 確認ダイアログでの操作安全性確保
- APIキーのマスク表示

## ベストプラクティス

1. **型安全性**: OpenAPI生成型を活用し、完全な型安全性を確保
2. **バリデーション**: Zodスキーマで一元的なバリデーション実装
3. **エラーハンドリング**: 全てのAPI呼び出しで適切なエラー処理
4. **コンポーネント設計**: 自己完結型で再利用可能なコンポーネント
5. **状態管理**: カスタムフックで状態ロジックの分離
6. **スタイル一貫性**: TailwindCSS classで統一されたデザインシステム

## 参考実装

Issue #14で実装されたAPI認証情報管理UIが、これらのパターンの完全な実装例となっています。新機能実装時はこの実装を参考にしてください。