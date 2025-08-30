# Nippogen 技術スタック

## 決定日

2025-08-24  
**更新**: 2025-08-30 (React Hook Form移行)

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

Presentation Layer
├── controllers/    # REST API
├── dto/           # レスポンス/リクエストDTO
└── mappers/       # DTOマッピング
```

## 詳細パッケージ構成

### Backend: オニオンアーキテクチャ実装

```
com.example.backend/
├── domain/                              # Domain Layer (Core)
│   ├── entities/
│   │   ├── PullRequest.java            # PRエンティティ
│   │   ├── GitHubCommit.java           # コミットエンティティ
│   │   └── User.java                   # ユーザーエンティティ
│   ├── repositories/
│   │   ├── GitHubRepository.java       # GitHub統合リポジトリIF
│   │   ├── ReportRepository.java       # 日報リポジトリIF
│   │   └── UserRepository.java         # ユーザーリポジトリIF
│   └── services/
│       ├── GitHubDomainService.java    # GitHub業務ロジック
│       └── ReportDomainService.java    # 日報業務ロジック
│
├── application/                         # Application Layer
│   ├── usecases/
│   │   ├── GitHubUseCase.java          # GitHub統合ユースケース
│   │   ├── ReportGenerationUseCase.java # 日報生成ユースケース
│   │   └── UserUseCase.java            # ユーザー管理ユースケース
│   └── dto/
│       ├── PullRequestDto.java         # PR情報DTO
│       ├── CommitDto.java              # コミット情報DTO
│       ├── ReportDto.java              # 日報DTO
│       └── UserDto.java                # ユーザーDTO
│
├── infrastructure/                      # Infrastructure Layer
│   ├── repositories/
│   │   ├── GitHubRestApiRepository.java # GitHub REST API実装
│   │   ├── JooqReportRepository.java   # 日報JOOQ実装
│   │   └── JooqUserRepository.java     # ユーザーJOOQ実装
│   ├── github/                         # GitHub API統合
│   │   ├── client/
│   │   │   └── GitHubRestApiClient.java # GitHub APIクライアント
│   │   ├── config/
│   │   │   └── GitHubApiConfiguration.java # WebClient設定
│   │   └── dto/
│   │       ├── GitHubRepositoryDto.java    # リポジトリDTO
│   │       ├── GitHubPullRequestDto.java   # PR DTO
│   │       ├── GitHubCommitDto.java        # コミットDTO
│   │       └── GitHubSearchResultDto.java  # 検索結果DTO
│   ├── external/
│   │   ├── OpenAiClient.java           # OpenAI API統合
│   │   └── TogglClient.java            # Toggl Track統合（予定）
│   ├── config/
│   │   ├── OpenAiConfig.java           # OpenAI設定
│   │   └── DatabaseConfig.java         # データベース設定
│   └── security/
│       └── EncryptionService.java      # APIキー暗号化
│
├── presentation/                        # Presentation Layer
│   ├── controllers/
│   │   ├── GitHubController.java       # GitHub API エンドポイント
│   │   ├── ReportController.java       # 日報API エンドポイント
│   │   ├── UserController.java         # ユーザーAPI エンドポイント
│   │   └── TestController.java         # テスト用エンドポイント
│   ├── dto/
│   │   ├── PullRequestResponse.java    # PR APIレスポンス
│   │   ├── CommitResponse.java         # コミット APIレスポンス
│   │   ├── ReportResponse.java         # 日報 APIレスポンス
│   │   └── ErrorResponse.java          # エラーレスポンス
│   └── mappers/
│       ├── GitHubResponseMapper.java   # GitHub DTO変換
│       ├── ReportResponseMapper.java   # 日報 DTO変換
│       └── UserResponseMapper.java     # ユーザー DTO変換
│
└── shared/                             # Shared Layer
    ├── exceptions/
    │   ├── GitHubMcpException.java     # GitHub API例外
    │   ├── ReportGenerationException.java # 日報生成例外
    │   ├── ValidationException.java    # バリデーション例外
    │   └── GlobalExceptionHandler.java # グローバル例外ハンドラー
    └── utils/
        ├── DateTimeUtils.java          # 日時ユーティリティ
        └── ValidationUtils.java        # バリデーションユーティリティ
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
│   └── settings/       # 設定機能（Issue #14で実装済み）
│       ├── components/ # API認証情報管理UI
│       │   ├── ApiServiceSelector.tsx         # サービス切り替えタブ
│       │   ├── CredentialCard.tsx            # 認証情報カード表示
│       │   ├── GitHubCredentialForm.tsx      # GitHub認証フォーム
│       │   ├── GitHubCredentialSection.tsx   # GitHub認証セクション
│       │   ├── NotionCredentialForm.tsx      # Notion認証フォーム
│       │   ├── NotionCredentialSection.tsx   # Notion認証セクション
│       │   ├── TogglCredentialForm.tsx       # Toggl認証フォーム
│       │   ├── TogglCredentialSection.tsx    # Toggl認証セクション
│       │   └── index.ts                      # エクスポート統合
│       ├── hooks/                            # サービス別認証情報管理
│       │   ├── useGitHubCredentials.ts       # GitHub認証情報CRUD
│       │   ├── useNotionCredentials.ts       # Notion認証情報CRUD
│       │   └── useTogglCredentials.ts        # Toggl認証情報CRUD
│       ├── schemas/                          # Zodバリデーション
│       │   ├── gitHubCredential.ts           # GitHub認証バリデーション
│       │   ├── notionCredential.ts           # Notion認証バリデーション
│       │   └── togglCredential.ts            # Toggl認証バリデーション
│       ├── stores/     # (将来実装予定)
│       └── types/      # (将来実装予定)
├── shared/
│   ├── components/     # 共通コンポーネント
│   ├── hooks/          # 共通hooks
│   ├── utils/          # ユーティリティ
│   │   ├── apiClient.ts                      # API通信クライアント
│   │   └── axiosInstance.ts                  # Axios設定
│   └── types/          # 共通型定義
└── app/               # Expo Router pages
    ├── settings/       # 設定画面
    │   ├── api-credentials.tsx               # API認証情報管理画面
    │   ├── index.tsx                         # 設定メイン画面
    │   └── _layout.tsx                       # 設定レイアウト
    └── _layout.tsx     # メインナビゲーション（設定タブ追加）
```

## 決定技術スタック

### 1. AI サービス

- **GPT-5 mini** (OpenAI)
  - 理由: コスパ良好、日本語対応、十分な性能

### 2. フロントエンドフォーム管理

- **React Hook Form** (TanStack Formから移行)
  - 理由: より軽量で高パフォーマンス、React Nativeでの安定性、広く採用されている
  - Zodとの統合: @hookform/resolversで型安全バリデーション

### 3. フロントエンド状態管理

- **Zustand**
  - 理由: 軽量、シンプル、TypeScript 対応良好

### 4. 認証・セキュリティ

- **JWT + Spring Security**
  - パスワードハッシュ: **BCrypt** (Spring 標準)
  - API キー暗号化: **AES-256-GCM**
  - **注意**: ログイン認証は優先度低め（MVP 後）

### 5. 外部 API 連携

- **GitHub REST API** 直接統合
  - WebClient を使用したHTTP通信
  - Personal Access Token認証
  - 機能: リポジトリアクセス、PR検索、コミット取得
  - レート制限対策: 基本的なリトライ + 待機のみ
- **将来予定**: Toggl Track、Notion
  - 実装方針: REST API直接統合（MCPからの移行完了）

### 6. HTTP クライアント

- **WebClient** (Spring WebFlux)
  - 理由: 非同期処理、モダン、パフォーマンス

### 7. テスト方針

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
- **HTTP**: Axios (axiosInstance設定済み)
- **Form Management**: React Hook Form + @hookform/resolvers (Issue #14で導入・移行)
- **Validation**: Zod (Issue #14で導入)
- **State Management**: React hooks + service-specific custom hooks (Zustand将来導入予定)

## 開発方針

### MVP 実装優先順位

1. データベース設計・マイグレーション ✅
2. 外部 API 連携（REST API直接実装） ✅
3. **API認証情報管理UI** ✅ (Issue #14完了)
4. AI 日報生成機能
5. 基本的な CRUD API
6. フロントエンド基本画面
7. 認証機能（最後）

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
