# Nippogen 技術スタック

## 決定日

2025-08-24

## 基本アーキテクチャ

### Backend: オニオンアーキテクチャ

```
Domain Layer (Core)
├── entities/       # エンティティ
├── repositories/   # リポジトリインターfaces
└── services/       # ドメインサービス

Application Layer
├── usecases/       # ユースケース
├── dto/           # データ転送オブジェクト
└── services/       # アプリケーションサービス

Infrastructure Layer
├── repositories/   # リポジトリ実装
├── external/       # 外部API連携
├── config/         # 設定
└── security/       # セキュリティ

Interface Layer
├── controllers/    # REST API
├── dto/           # レスポンス/リクエストDTO
└── mappers/       # DTOマッピング
```

### Frontend: Feature-based アーキテクチャ

```
src/
├── features/
│   ├── auth/           # 認証機能
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── stores/     # Zustand stores
│   │   └── types/
│   ├── reports/        # 日報機能
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── stores/
│   │   └── types/
│   └── settings/       # 設定機能
│       ├── components/
│       ├── hooks/
│       ├── stores/
│       └── types/
├── shared/
│   ├── components/     # 共通コンポーネント
│   ├── hooks/          # 共通hooks
│   ├── utils/          # ユーティリティ
│   └── types/          # 共通型定義
└── app/               # Expo Router pages
```

## 決定技術スタック

### 1. AI サービス

- **GPT-5 mini** (OpenAI)
  - 理由: コスパ良好、日本語対応、十分な性能

### 2. フロントエンド状態管理

- **Zustand**
  - 理由: 軽量、シンプル、TypeScript 対応良好

### 3. 認証・セキュリティ

- **JWT + Spring Security**
  - パスワードハッシュ: **BCrypt** (Spring 標準)
  - API キー暗号化: **AES-256-GCM**
  - **注意**: ログイン認証は優先度低め（MVP 後）

### 4. 外部 API 連携

- **MCP (Model Context Protocol)** で外部サービス接続
  - GitHub、Toggl Track、Notion
  - レート制限対策: 基本的なリトライ + 待機のみ

### 5. HTTP クライアント

- **WebClient** (Spring WebFlux)
  - 理由: 非同期処理、モダン、パフォーマンス

### 6. テスト方針

- **最小限のテスト**
  - 主要サービスクラスの単体テスト
  - JUnit 5 + Mockito 使用

## 既存技術スタック（確認済み）

### Backend

- **Framework**: Spring Boot 3.5.4 + Java 21
- **Database**: PostgreSQL + JOOQ
- **Migration**: Flyway
- **API Docs**: SpringDoc OpenAPI
- **Build**: Gradle

### Frontend

- **Framework**: React Native 0.79.5 + React 19.0.0
- **Platform**: Expo SDK 53
- **Language**: TypeScript (strict mode)
- **Styling**: TailwindCSS (NativeWind)
- **Routing**: Expo Router (file-based)
- **HTTP**: Axios

## 開発方針

### MVP 実装優先順位

1. データベース設計・マイグレーション
2. 外部 API 連携（MCP）
3. AI 日報生成機能
4. 基本的な CRUD API
5. フロントエンド基本画面
6. 認証機能（最後）

### コーディング規約

- **Backend**: Java 標準 + Spring Boot best practices
- **Frontend**: ESLint + Prettier + TypeScript strict
- **Database**: Snake_case 命名規則
- **API**: REST + OpenAPI 仕様書必須

## セキュリティ考慮事項

- API キーは必ず暗号化保存
- 外部 API 通信は HTTPS 必須
- ユーザーデータの適切な分離
- SQL injection 対策（JOOQ 使用）

## パフォーマンス要件

- 日報生成: 3 分以内
- API 応答時間: 500ms 以内（通常時）
- 同時ユーザー: 10 人程度を想定
