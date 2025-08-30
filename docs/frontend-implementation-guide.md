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

### 1. React Hook Form + Zod バリデーション

#### フォーム管理方針
- `useForm`フックで型安全なフォーム管理
- `zodResolver`を使用してZodスキーマとの統合
- `register`関数で簡潔なフィールド登録
- バリデーションエラーは自動的にフォーム状態に反映

#### バリデーション戦略
- Zodスキーマでサーバーサイドと同じバリデーションルール
- リアルタイムバリデーション（フィールドレベル）
- 型推論によるフォームデータの型安全性確保
- 複雑なフィールド（配列、Switch）は`useWatch`と`setValue`で制御

### 2. サービス別カスタムフックによる状態管理

#### 設計方針
- サービス（GitHub、Notion、Toggl）ごとに専用フック実装
- 統合フックではなく個別フックで関心の分離
- 各フックが独立してCRUD操作を管理
- エラーハンドリングとローディング状態を内包

#### フック責任範囲
- API通信の実行と結果の状態管理
- エラーハンドリングとユーザーフィードバック
- 楽観的更新によるUX向上
- 接続テスト機能の提供

### 3. API通信パターン

#### HTTP通信設計
- Axiosインスタンスで基本設定の一元化
- インターセプターでユーザーID等の共通ヘッダー設定
- タイムアウト設定とエラーハンドリング標準化

#### API クライアント構成
- サービス別にAPIクライアントを分離
- OpenAPI生成型を活用した型安全な通信
- CRUD操作と接続テスト機能を統一インターフェース化
- レスポンス型の明示による型安全性確保

### 4. コンポーネント設計原則

#### 自己完結型コンポーネント
- 各コンポーネントが独立した責任範囲を持つ
- composition patternを避け、シンプルな実装
- プロップスで外部からの制御ポイントを明確化
- 内部状態とロジックの完全カプセル化

#### コンポーネント構成
- フィーチャー別のディレクトリ構造
- エクスポート統合によるインポート簡略化
- サービス固有コンポーネントと共通コンポーネントの分離

### 5. 型安全性の確保

#### OpenAPI型統合
- バックエンドOpenAPI仕様から自動生成される型定義を活用
- サーバーサイドとクライアントサイドで同一の型安全性
- API変更時の型エラーによる早期発見

#### 型安全戦略
- Zodスキーマとの型整合性確保
- フォームデータ型の推論活用
- 必要に応じた型ガードの実装

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

### React Hook Form最適化
- `useWatch`で必要最小限の再レンダリング制御
- 複雑なフィールド（配列、Switch）の適切な制御
- フォーム送信時の型安全性確保

### パフォーマンス最適化
- React Hook Formによる高パフォーマンスフォーム
- axiosInstance で HTTP設定を一元化
- 適切なローディング状態の表示
- サービス別フックによる状態分離

### ユーザビリティ
- リアルタイムバリデーション
- 適切なフィードバック（ローディング、エラー、成功）
- 確認ダイアログでの操作安全性確保
- APIキーのマスク表示

## ベストプラクティス

1. **型安全性**: OpenAPI生成型を活用し、完全な型安全性を確保
2. **フォーム管理**: React Hook Form + Zodで高パフォーマンスバリデーション
3. **状態管理**: サービス別フックで関心の分離
4. **エラーハンドリング**: 全てのAPI呼び出しで適切なエラー処理
5. **コンポーネント設計**: 自己完結型で再利用可能なコンポーネント
6. **スタイル一貫性**: TailwindCSS classで統一されたデザインシステム

## 参考実装

Issue #14で実装されたAPI認証情報管理UIが、これらのパターンの完全な実装例となっています。新機能実装時はこの実装を参考にしてください。