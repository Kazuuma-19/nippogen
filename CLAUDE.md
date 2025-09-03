# CLAUDE.md

このファイルは、このリポジトリでコード作業を行う際のClaude Code (claude.ai/code) に対するガイダンスを提供します。

## プロジェクト構成

Nippogenは、フロントエンドとバックエンドを明確に分離したフルスタックアプリケーションです。

### 技術スタック

詳細なアーキテクチャ、技術選択の理由、開発方針については、[技術スタック](docs/tech-stack.md) を参照してください。

### 概要

#### バックエンド (Spring Boot + Java 21)

- **フレームワーク**: Spring Boot 3.5.4 with Java 21
- **データベース**: PostgreSQL with JOOQ for type-safe database operations
- **データベースマイグレーション**: Flyway for database schema management
- **API ドキュメント**: SpringDoc OpenAPI with Swagger UI
- **ビルドツール**: Gradle with wrapper
- **場所**: `/backend/` ディレクトリ

#### フロントエンド (React Native + Expo)

- **フレームワーク**: Expo with React Native 0.79.5 and React 19.0.0
- **スタイリング**: TailwindCSS with NativeWind for React Native
- **ルーティング**: Expo Router with file-based routing
- **HTTP クライアント**: Axios for API communication
- **TypeScript**: Full TypeScript support with strict mode
- **場所**: `/frontend/` ディレクトリ

#### 型安全性 & API 統合

- バックエンドJavaアノテーションからSpringDocによるOpenAPI仕様生成
- `openapi-typescript`経由でOpenAPI仕様からTypeScript型を自動生成
- 生成された型は`frontend/types/api.ts`に配置

## 開発コマンド

### プロジェクトセットアップ

```bash
# 全依存関係のインストール（フロントエンド・バックエンド）
make ready

# フロントエンドのみセットアップ
make front-ready

# バックエンドのみセットアップ
make back-ready
```

### 開発ワークフロー

```bash
# フロントエンド開発サーバー開始（モバイルテスト用トンネル付き）
cd frontend && npm run start

# または make コマンド使用
make front

# バックエンドをDockerでビルド・開始（Spring Boot + PostgreSQL）
make back

# バックエンドビルドのみ
make build
```

### データベース操作

```bash
# PostgreSQL コンテナ開始
make up

# PostgreSQL シェルアクセス
make psql

# コンテナ停止
make down

# コンテナ停止・データベースボリューム削除
make down-v
```

### コード品質 & 型

```bash
# フロントエンドlinting
cd frontend && npm run lint

# OpenAPI仕様からTypeScript型生成
cd frontend && npm run generate-types

# バックエンドテスト
cd backend && ./gradlew test

# バックエンドビルド（テスト含む）
cd backend && ./gradlew build
```

### データベース & JOOQ

```bash
# データベースマイグレーション実行
make migrate
# または: cd backend && ./gradlew flywayMigrate

# JOOQ クラス生成（Flywayマイグレーションを自動実行）
make jooq
# または: cd backend && ./gradlew generateJooq

# マイグレーション + JOOQ生成を一括実行
make db-setup
```

## プロジェクト構造

### バックエンド構造（オニオンアーキテクチャ）

- `src/main/java/com/example/backend/` - メインアプリケーションコード
  - `application/` - アプリケーション層（ユースケース、DTO）
  - `domain/` - ドメイン層（エンティティ、リポジトリインターface）
  - `infrastructure/` - インフラ層（データアクセス、外部API）
    - `github/` - GitHub REST API統合
      - `client/` - APIクライアント（GitHubRestApiClient）
      - `config/` - WebClient設定（GitHubApiConfiguration）
      - `dto/` - GitHub APIレスポンスDTO群
    - `repositories/` - リポジトリ実装
  - `presentation/` - プレゼンテーション層（コントローラー）
  - `shared/` - 共通機能（例外処理等）
- `src/main/resources/db/migration/` - Flyway マイグレーションファイル
- `src/main/resources/application.yml` - Spring 設定
- `build/generated-src/jooq/main/` - 生成された JOOQ クラス
- `openapi.json` - 生成された OpenAPI 仕様

### フロントエンド構造

- `app/` - Expo Router pages (file-based routing)
- `types/api.ts` - バックエンド OpenAPI 仕様から生成された TypeScript 型
- `global.css` - グローバル Tailwind CSS スタイル

## データベース設定

### 開発データベース

- **Host**: localhost:5433 (ローカル PostgreSQL との競合を避けるため)
- **Database**: nippogen
- **User**: postgres
- **Password**: postgres

### コンテナデータベース（バックエンド接続用）

- **Host**: db:5432
- **Database**: nippogen
- **User**: postgres
- **Password**: postgres

## コンテナ起動

### バックエンド + データベース一括起動

```bash
make back
```

このコマンドで以下が同時に起動されます：
- **Spring Boot アプリケーション** (ポート: 8080)
- **PostgreSQL データベース** (ポート: 5432)

起動後にアクセスできるURL：
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## API ドキュメント

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 主要開発パターン

### バックエンド開発

- **オニオンアーキテクチャ**: ドメイン中心設計で依存関係を内向きに
- **API ドキュメント**: SpringDoc アノテーション（`@Operation`、`@Tag`）を使用
- **データベース変更**: `src/main/resources/db/migration/` の Flyway マイグレーションで実行
- **JOOQ再生成**: スキーマ変更後は `./gradlew generateJooq` で JOOQ クラスを再生成
- **REST API規約**: `/api/` プレフィックスを使用
- **外部API統合**: WebClient を使用した型安全なHTTP通信
- **GitHub API**: REST API直接使用（Personal Access Token認証）

### フロントエンド開発

- ナビゲーションには Expo Router を使用（`app/` ディレクトリでファイルベースルーティング）
- NativeWind 経由で TailwindCSS クラスでスタイリング
- バックエンド API 変更後は `npm run generate-types` で型を再生成
- API 型は `types/api.ts` からインポート

### 型安全ワークフロー

1. SpringDoc アノテーション付きでバックエンドコントローラを更新
2. バックエンドを再起動して `openapi.json` を再生成
3. フロントエンドで `npm run generate-types` を実行
4. 更新された TypeScript 型を使用して型安全な API コールを実行

## 開発ガイドライン

詳細な開発プロセス、GitHubワークフロー、コーディング規約については、[開発ガイドライン](docs/development-guidelines.md) を参照してください。

## 環境要件

- **Java**: 21 (build.gradle で設定)
- **Node.js**: Expo SDK 53 対応
- **Docker**: PostgreSQL コンテナ用に必要
- **PostgreSQL**: 17 (Docker コンテナ経由)